package api.Seller.orders.order_management;

import api.Seller.login.Login;
import api.Seller.orders.return_order.APIGetReturnOrdersById;
import api.Seller.orders.return_order.APIGetReturnOrdersById.ReturnOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.enums.PromotionType;
import utilities.enums.analytics.TimeFrame;
import utilities.helper.DateTimeRangeGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class provides functionality to retrieve and manage orders from GOSELL and BEECOW platforms.
 * It utilizes REST API calls to fetch order lists based on time frames and channels, and aggregates order information.
 */
public class APIGetOrderList {
    private static final Logger logger = LogManager.getLogger();
    private final LoginDashboardInfo loginInfo;
    private final LoginInformation credentials;

    /**
     * Constructor that initializes the class with login information.
     *
     * @param credentials The login credentials for the seller.
     */
    public APIGetOrderList(LoginInformation credentials) {
        this.credentials = credentials;
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Represents the order list, including individual orders and a summary view model.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderList {
        private List<Order> response;
        private OrderListSummaryVM orderListSummaryVM;

        /**
         * Represents individual orders with detailed information such as product details, customer info, and payment histories.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Order {
            private int id;
            private String productNames;
            private String bcOrderGroupId;
            private String channel;
            private int storeId;
            private String status;
            private String buyerName;
            private BigDecimal total;
            private BigDecimal subTotal;
            private String currency;
            private String paymentMethod;
            private String note;
            private String createdDate;
            private String createdBy;
            private String updatedDate;
            private int itemsCount;
            private String phone;
            private String displayName;
            private String email;
            private String userId;
            private String appInstall;
            private String location;
            private String orderType;
            private List<Item> items;
            private int customerId;
            private String customerFullName;
            private String customerCountry;
            private String customerAddress;
            private String customerAddress2;
            private String customerWard;
            private String customerDistrict;
            private String customerCity;
            private String city;
            private String customerState;
            private String customerZipCode;
            private String customerPhone;
            private String customerPhoneBackup;
            private String inStore;
            private int branchId;
            private String branchName;
            private String branchType;
            private String userName;
            private String wholesaleId;
            private String discountCode;
            private BigDecimal discountAmount;
            private BigDecimal shippingFee;
            private BigDecimal feeDeduction;
            private BigDecimal totalTaxAmount;
            private String shippingMethod;
            private String shippingService;
            private BigDecimal totalCostPrice;
            private BigDecimal debtAmount;
            private boolean isPaid;
            private BigDecimal receivedAmount;
            private String payType;
            private List<PaymentHistory> paymentHistories;
            private boolean hasDebt;
            private String rawPaymentMethod;
            private String fullShippingAddress;
            private String fullShippingAddressEn;
            private String phoneCode;
            private List<String> orderTags;
            private String madeBy;
            private String statusUpdatedDate;
            private String customerSaleChannel;
            private int totalItems;
            private int totalItemQty;
            private String receiverDisplayName;
            private String receiverEmail;
            private String receiverPhone;
            private String receiverPhoneCode;
            private String creatorDisplayName;
            private String creatorEmail;
            private String creatorPhone;
            private String creatorPhoneCode;
            private int earningPoint;
            private int redeemPoint;
            private BigDecimal pointAmount;
            private BigDecimal exchangeAmount;
            private String returnStatus;

            /**
             * Represents an item in an order, including its ID, price, quantity, and other relevant details.
             */
            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Item {
                private int id;
                private String name;
                private BigDecimal price;
                private int quantity;
                private String barcode;
                private BigDecimal costPrice;
                private int itemId;
                private int modelId;
            }

            /**
             * Represents the payment history of an order, including payment method, amount, and the date of the transaction.
             */
            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class PaymentHistory {
                private int id;
                private String createDate;
                private String paymentMethod;
                private BigDecimal paymentAmount;
            }
        }

        /**
         * Represents summarized information for an order list, including counts of orders by status and aggregated monetary values.
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class OrderListSummaryVM {
            private int toConfirmCount;
            private int shippedCount;
            private int deliveredCount;
            private int cancelledCount;
            private BigDecimal customerDebt;
            private BigDecimal sellerDebt;
            private BigDecimal receivedAmount;
        }
    }

    /**
     * Generates the appropriate date range based on the provided {@link TimeFrame}.
     *
     * @param timeFrame The time frame to filter the orders.
     * @return A String array with the start and end date of the specified time frame.
     */
    public String[] generateTimeFrame(TimeFrame timeFrame) {
        return switch (timeFrame) {
            case TODAY -> DateTimeRangeGenerator.getTodayRange();
            case YESTERDAY -> DateTimeRangeGenerator.getYesterdayRange();
            case THIS_WEEK -> DateTimeRangeGenerator.getThisWeekRange();
            case LAST_WEEK -> DateTimeRangeGenerator.getLastWeekRange();
            case THIS_MONTH -> DateTimeRangeGenerator.getThisMonthRange();
            case LAST_MONTH -> DateTimeRangeGenerator.getLastMonthRange();
            case THIS_YEAR -> DateTimeRangeGenerator.getThisYearRange();
            case LAST_YEAR -> DateTimeRangeGenerator.getLastYearRange();
            case LAST_7_DAYS -> DateTimeRangeGenerator.getLast7DaysRange();
            case LAST_30_DAYS -> DateTimeRangeGenerator.getLast30DaysRange();
            default -> DateTimeRangeGenerator.getCustomRange();
        };
    }

    /**
     * Retrieves the order list response from the API based on page index, sales channel, and time frame.
     *
     * @param pageIndex The index of the page to retrieve.
     * @param channel   The sales channel (e.g., "GOSELL" or "BEECOW").
     * @param timeFrame The time frame to filter the orders.
     * @return A REST-assured {@link Response} containing the order list.
     */
    private Response fetchOrderListResponse(int pageIndex, String channel, TimeFrame timeFrame) {
        String branchIds = loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", "");
        String path = "/beehiveservices/api/orders/gosell-store/v2/%d?page=%d&size=50&channel=%s&fromDate=%s&toDate=%s&branchIds=%s";
        String[] timeFrames = generateTimeFrame(timeFrame);
        return new API().get(path.formatted(loginInfo.getStoreID(), pageIndex, channel, timeFrames[0], timeFrames[1], branchIds), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().response();
    }

    /**
     * Retrieves and parses the order data from multiple pages based on the specified time frame and sales channel.
     *
     * @param timeFrame The time frame to filter the orders.
     * @param channel   The sales channel (e.g., "GOSELL" or "BEECOW").
     * @return A list of {@link OrderList} objects.
     */
    private List<OrderList> retrieveOrderData(TimeFrame timeFrame, String channel) {
        int totalOfOrders = Integer.parseInt(fetchOrderListResponse(0, channel, timeFrame).getHeader("X-Total-Count"));
        int numberOfPages = Math.min(totalOfOrders / 100, 99);

        return IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> fetchOrderListResponse(pageIndex, channel, timeFrame))
                .map(response -> response.as(OrderList.class))
                .toList();
    }

    /**
     * Retrieves, combines, and aggregates order lists based on the specified time frame and sales channel.
     * It fetches the order data from the provided channel, then combines the orders and aggregates the summary
     * information for a complete view of the order list.
     *
     * @param timeFrame The time frame to filter the orders (e.g., TODAY, LAST_30_DAYS, etc.).
     * @param channel   The sales channel to filter orders (e.g., "GOSELL" or "BEECOW").
     * @return A combined {@link OrderList} object that contains all the orders from the specified time frame and channel,
     * along with aggregated summary information.
     */
    public OrderList getAggregatedOrderList(TimeFrame timeFrame, String channel) {
        LogManager.getLogger().info("Waiting for the retrieval of the order list from the API for channel: {} and time frame: {}", channel, timeFrame);
        var orderLists = retrieveOrderData(timeFrame, channel);

        OrderList.OrderListSummaryVM summary = summarizeOrderList(orderLists.getFirst());
        List<OrderList.Order> combinedOrders = flattenOrderListResponses(orderLists);

        LogManager.getLogger().info("Completed retrieval of the order list from the API. Total orders retrieved: {}", combinedOrders.size());
        return new OrderList(combinedOrders, summary);
    }

    /**
     * Aggregates the summary of an order list.
     *
     * @param orderList The order list to summarize.
     * @return An aggregated {@link OrderList.OrderListSummaryVM}.
     */
    public OrderList.OrderListSummaryVM summarizeOrderList(OrderList orderList) {
        return orderList.getOrderListSummaryVM() != null
                ? orderList.getOrderListSummaryVM()
                : new OrderList.OrderListSummaryVM(0, 0, 0, 0,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Flattens a list of {@link OrderList} objects into a combined list of {@link OrderList.Order} objects.
     *
     * @param orderLists A list of {@link OrderList} objects containing multiple orders.
     * @return A combined list of {@link OrderList.Order} objects from all provided {@link OrderList}.
     */
    public List<OrderList.Order> flattenOrderListResponses(List<OrderList> orderLists) {
        return orderLists.stream()
                .map(OrderList::getResponse)
                .flatMap(Collection::stream)
                .toList();
    }


    /**
     * Retrieves the total count of orders from the provided OrderList.
     *
     * @param orderList The list of orders to calculate from.
     * @return The total count of orders as an int.
     */
    public static int getOrderCount(OrderList orderList) {
        logger.info("Starting getOrderCount...");
        if (orderList.getOrderListSummaryVM() == null) {
            logger.info("Completed getOrderCount with result: 0");
            return 0;
        }

        OrderList.OrderListSummaryVM summary = orderList.getOrderListSummaryVM();
        int result = summary.getCancelledCount() +
                     summary.getShippedCount() +
                     summary.getDeliveredCount() +
                     summary.getToConfirmCount();

        logger.info("Completed getOrderCount with result: {}", result);
        return result;
    }

    /**
     * Calculates the total product cost from the list of non-cancelled orders, considering the cost of returned products.
     *
     * @param orderList The list of orders to calculate from.
     * @param returnOrderProductCost The total product cost for the return orders.
     * @return The total product cost as a BigDecimal value, after deducting the return order product cost.
     */
    public static BigDecimal calculateTotalProductCost(OrderList orderList, BigDecimal returnOrderProductCost) {
        logger.info("Starting calculateTotalProductCost...");

        if (orderList.getResponse() == null) {
            BigDecimal result = BigDecimal.ZERO.subtract(returnOrderProductCost);
            logger.info("Completed calculateTotalProductCost with result: {}", result);
            return result;
        }

        // Collect non-cancelled order items
        List<OrderList.Order.Item> itemList = orderList.getResponse().parallelStream()
                .filter(order -> !order.getStatus().equals("CANCELLED"))
                .map(OrderList.Order::getItems)
                .flatMap(Collection::stream)
                .toList();

        // Calculate the total product cost
        BigDecimal totalOrderProductCost = itemList.stream()
                .map(item -> item.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal result = totalOrderProductCost.subtract(returnOrderProductCost);
        logger.info("Completed calculateTotalProductCost with result: {}", result);

        return result;
    }

    /**
     * Calculates the total amount for non-cancelled orders, subtracting the product cost.
     *
     * @param orderList The list of orders to calculate from.
     * @param orderCost The product cost to subtract.
     * @return The total amount as a BigDecimal.
     */
    public static BigDecimal getNonCancelledTotalAmount(OrderList orderList, BigDecimal orderCost) {
        logger.info("Starting getTotalAmount...");

        if (orderList.getResponse() == null) {
            logger.info("Completed getNonCancelledTotalAmount with result: {}", java.math.BigDecimal.ZERO.subtract(orderCost));
            return BigDecimal.ZERO.subtract(orderCost);
        }

        BigDecimal totalAmount = orderList.getResponse().parallelStream()
                .filter(order -> !order.getStatus().equals("CANCELLED"))
                .map(OrderList.Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal result = totalAmount.subtract(orderCost);
        logger.info("Completed getNonCancelledTotalAmount with result: {}", result);
        return result;
    }

    /**
     * Calculates the average order value based on the total non-cancelled order amount and the number of orders.
     * <p>
     * This method computes the average value of non-cancelled orders by dividing the total non-cancelled order
     * amount by the total number of non-cancelled orders.
     *
     * @param nonCancelledOrderAmount The total amount from all non-cancelled orders.
     * @param totalOrders             The total number of non-cancelled orders.
     * @return The average order value as a BigDecimal.
     */
    public static BigDecimal getAverageOrderValue(BigDecimal nonCancelledOrderAmount, int totalOrders) {
        logger.info("Starting getAverageOrderValue...");

        if (totalOrders == 0) {
            logger.info("Completed getAverageOrderValue with result: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal result = nonCancelledOrderAmount.divide(BigDecimal.valueOf(totalOrders), 0, RoundingMode.HALF_UP);
        logger.info("Completed getAverageOrderValue with result: {}", result);
        return result;
    }

    /**
     * Retrieves a list of orders that have a return status.
     *
     * @param orderList The list of orders to filter from.
     * @return A list of orders that have a non-null return status.
     */
    private static List<OrderList.Order> getOrdersWithReturnStatus(OrderList orderList) {
        if (orderList.getResponse() == null) {
            return List.of();
        }

        return orderList.getResponse().parallelStream()
                .filter(order -> order.getReturnStatus() != null)
                .toList();
    }

    /**
     * Retrieves a map of orders to their corresponding return orders.
     *
     * @param orderList the list of orders to retrieve return orders for
     * @return a map where the key is an Order and the value is a list of ReturnOrder
     */
    public Map<OrderList.Order, List<ReturnOrder>> getReturnOrdersMap(OrderList orderList) {
        List<OrderList.Order> ordersWithReturnStatus = getOrdersWithReturnStatus(orderList);

        logger.info("Orders with return status: {}", ordersWithReturnStatus.size());

        // Return an empty map if no orders have a return status
        if (ordersWithReturnStatus.isEmpty()) {
            return Collections.emptyMap();
        }

        // Create a map of orders to their corresponding return orders
        logger.info("Retrieving return orders...");
        Map<OrderList.Order, List<ReturnOrder>> returnOrdersMap = ordersWithReturnStatus.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        order -> order,
                        order -> new APIGetReturnOrdersById(credentials).getReturnOrdersById(order.getId())
                ));
        logger.info("Retrieved return orders map with size: {}", returnOrdersMap.size());

        return returnOrdersMap;
    }

    /**
     * Counts the number of completed return orders in the provided return orders map.
     *
     * @param returnOrdersMap the map of orders to return orders
     * @return the count of completed return orders
     */
    public static int getCompletedReturnOrderCount(Map<OrderList.Order, List<ReturnOrder>> returnOrdersMap) {
        if (returnOrdersMap.isEmpty()) {
            return 0;
        }

        logger.info("Counting completed return orders...");
        int completedCount = returnOrdersMap.keySet().parallelStream()
                .mapToInt(order -> APIGetReturnOrdersById.returnOrderCompletedCount(returnOrdersMap.get(order)))
                .sum();
        logger.info("Total completed return orders: {}", completedCount);
        return completedCount;
    }

    /**
     * Calculates the total product cost of return orders in the provided return orders map.
     *
     * @param returnOrdersMap the map of orders to return orders
     * @return the total product cost of return orders
     */
    public static BigDecimal getReturnOrdersProductCost(Map<OrderList.Order, List<ReturnOrder>> returnOrdersMap) {
        if (returnOrdersMap.isEmpty()) {
            return BigDecimal.ZERO;
        }

        logger.info("Calculating total product cost for return orders...");
        BigDecimal totalCost = returnOrdersMap.keySet().parallelStream()
                .flatMap(order -> returnOrdersMap.get(order).stream()
                        .filter(returnOrder -> returnOrder.getStatus().equals("COMPLETED"))
                        .map(returnOrder -> getReturnOrderProductCost(order, returnOrder)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Total product cost for return orders: {}", totalCost);
        return totalCost;
    }

    /**
     * Calculates the total completed refund amount from the return orders map.
     * It sums the refund amounts of all "COMPLETED" return orders.
     *
     * @param returnOrdersMap a map of orders and their associated return orders.
     * @return the total completed refund amount as a BigDecimal.
     */
    public static BigDecimal getCompletedReturnAmount(Map<OrderList.Order, List<ReturnOrder>> returnOrdersMap) {
        if (returnOrdersMap.isEmpty()) {
            return BigDecimal.ZERO;
        }

        logger.info("Calculating total completed refund amount...");
        BigDecimal totalRefundAmount = returnOrdersMap.keySet().parallelStream()
                .map(order -> APIGetReturnOrdersById.getCompletedRefundAmount(returnOrdersMap.get(order)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Total completed refund amount: {}", totalRefundAmount);
        return totalRefundAmount;
    }

    /**
     * Calculates the total confirmed refund amount from the return orders map.
     * It sums the refund amounts of return orders that have been refunded (excluding "NOT_REFUND" status).
     *
     * @param returnOrdersMap a map of orders and their associated return orders.
     * @return the total confirmed refund amount as a BigDecimal.
     */
    public static BigDecimal getConfirmedReturnAmount(Map<OrderList.Order, List<ReturnOrder>> returnOrdersMap) {
        if (returnOrdersMap.isEmpty()) {
            return BigDecimal.ZERO;
        }

        logger.info("Calculating total confirmed refund amount...");
        BigDecimal totalConfirmedRefund = returnOrdersMap.keySet().parallelStream()
                .map(order -> APIGetReturnOrdersById.getConfirmedRefundAmount(returnOrdersMap.get(order)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Total confirmed refund amount: {}", totalConfirmedRefund);
        return totalConfirmedRefund;
    }

    /**
     * Calculates the total product cost for a specific return order that has a "COMPLETED" status.
     *
     * @param order       the original order
     * @param returnOrder the return order, which should have a status of "COMPLETED"
     * @return the total product cost for the return order, or BigDecimal.ZERO if the return order is null or not completed
     */
    public static BigDecimal getReturnOrderProductCost(OrderList.Order order, ReturnOrder returnOrder) {
        if (returnOrder == null) return BigDecimal.ZERO;

        logger.info("Calculating product cost for return order...");
        BigDecimal totalProductCost = returnOrder.getReturnOrderItemList().parallelStream()
                .map(returnOrderItem -> getProductCostPrice(order, returnOrderItem.getItemId(), returnOrderItem.getModelId())
                        .multiply(BigDecimal.valueOf(returnOrderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Total product cost for return order: {}", totalProductCost);
        return totalProductCost;
    }

    /**
     * Retrieves the cost price of an item in the original order.
     * <p>
     * This method ensures that the cost price is based on the original order,
     * as the product cost can vary in different orders. This is crucial for
     * accurately calculating the total refund product cost price.
     *
     * @param order   the original order
     * @param itemId  the item ID
     * @param modelId the model ID
     * @return the cost price of the item in the original order, or BigDecimal.ZERO if not found
     */

    private static BigDecimal getProductCostPrice(OrderList.Order order, int itemId, Integer modelId) {
        return order.getItems().stream()
                .filter(item -> Objects.equals(item.getItemId(), itemId) && Objects.equals(item.getModelId(), modelId))
                .findFirst()
                .map(OrderList.Order.Item::getCostPrice)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Retrieves the count of cancelled orders from the provided OrderList.
     *
     * @param orderList The list of orders to calculate from.
     * @return The number of cancelled orders as an int.
     */
    public static int getCancelledOrderCount(OrderList orderList) {
        logger.info("Starting getCancelledOrderCount...");
        if (orderList.getOrderListSummaryVM() == null) {
            logger.info("Completed getCancelledOrderCount with result: 0");
            return 0;
        }

        int result = orderList.getOrderListSummaryVM().getCancelledCount();
        logger.info("Completed getCancelledOrderCount with result: {}", result);
        return result;
    }

    /**
     * Retrieves the total amount for cancelled orders.
     *
     * @param orderList The list of orders to calculate from.
     * @return The total amount of cancelled orders as a BigDecimal.
     */
    public static BigDecimal getCancelledAmount(OrderList orderList) {
        logger.info("Starting getCancelledAmount...");

        if (orderList.getResponse() == null) {
            logger.info("Completed getCancelledAmount with result: {}", BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }

        BigDecimal result = orderList.getResponse().parallelStream()
                .filter(order -> order.getStatus().equals("CANCELLED"))
                .map(OrderList.Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Completed getCancelledAmount with result: {}", result);
        return result;
    }

    /**
     * Retrieves the details of non-cancelled orders from the provided OrderList.
     *
     * @param orderList The list of orders to filter and retrieve details for.
     * @return A list of OrderDetailInfo for non-cancelled orders.
     */
    public List<OrderDetailInfo> getNonCancelledOrderDetails(OrderList orderList) {
        logger.info("Starting getNonCancelledOrderDetails...");

        if (orderList.getResponse() == null) {
            logger.info("Completed getNonCancelledOrderDetails with result: empty list");
            return List.of();
        }

        List<Integer> nonCancelledOrderIds = orderList.getResponse().parallelStream()
                .filter(order -> !order.getStatus().equals("CANCELLED"))
                .map(OrderList.Order::getId)
                .toList();

        List<OrderDetailInfo> result = nonCancelledOrderIds.parallelStream()
                .map(orderId -> new APIOrderDetail(credentials).getOrderDetail(orderId))
                .toList();

        logger.info("Completed getNonCancelledOrderDetails with result: {} orders", result.size());
        return result;
    }

    /**
     * Calculates the total redeem point amount for non-cancelled orders.
     *
     * @param nonCancelledOrderInfos The list of order details to process.
     * @return The total redeem point amount as a BigDecimal.
     */
    public static BigDecimal calculateTotalRedeemPoint(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Starting calculateTotalRedeemPoint...");

        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Completed calculateTotalRedeemPoint with result: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal result = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.POINT)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Completed calculateTotalRedeemPoint with result: {}", result);
        return result;
    }


    /**
     * Calculates the total direct discount for non-cancelled orders.
     *
     * @param nonCancelledOrderInfos The list of non-cancelled order details to process.
     * @return The total direct discount as a BigDecimal.
     */
    public static BigDecimal calculateDirectDiscount(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Calculating direct discount for non-cancelled orders...");
        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Direct discount calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.DIRECT_DISCOUNT)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Direct discount calculated: {}", totalDiscount);
        return totalDiscount;
    }

    /**
     * Calculates the total discount code amount for non-cancelled orders.
     *
     * @param nonCancelledOrderInfos The list of non-cancelled order details to process.
     * @return The total discount code amount as a BigDecimal.
     */
    public static BigDecimal calculatePromotionCode(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Calculating discount code amount for non-cancelled orders...");
        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Discount code amount calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscountCode = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.COUPON)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Discount code amount calculated: {}", totalDiscountCode);
        return totalDiscountCode;
    }

    /**
     * Calculates the total promotion campaign value for non-cancelled orders.
     * <p>
     * This method sums up the total discount amounts from various promotion sources,
     * including flash sales, discount campaigns, bXgY offers,
     * wholesale products, and membership discounts for the given list of
     * non-cancelled order details.
     *
     * @param nonCancelledOrderInfos The list of non-cancelled order details to process.
     * @return The total promotion campaign value as a BigDecimal.
     */

    public static BigDecimal calculatePromotionCampaign(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Calculating promotion campaign value for non-cancelled orders...");
        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Total promotion campaign value calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal flashSaleAmount = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.FLASH_SALE)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountCampaignAmount = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.CAMPAIGN)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bXgYAmount = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.BUY_X_GET_Y)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal membershipAmount = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.MEMBERSHIP)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal wholesaleProductAmount = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderInfo, PromotionType.WHOLESALE)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCampaignAmount = BigDecimal.ZERO;
        totalCampaignAmount = totalCampaignAmount.add(flashSaleAmount)
                .add(discountCampaignAmount)
                .add(bXgYAmount)
                .add(membershipAmount)
                .add(wholesaleProductAmount);

        logger.info("Total promotion campaign value calculated: {}", totalCampaignAmount);
        return totalCampaignAmount;
    }

    /**
     * Calculates the total shipping fee for non-cancelled orders in the provided order list.
     * <p>
     * This method computes the total shipping fee after applying any applicable discounts
     * for the given list of non-cancelled order details.
     *
     * @param nonCancelledOrderInfos The list of non-cancelled order details to process.
     * @return The total shipping fee after discounts as a BigDecimal.
     */
    public static BigDecimal calculatorShippingFee(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Calculating shipping fee for cancelled orders...");
        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Shipping fee calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalShippingFee = nonCancelledOrderInfos.parallelStream()
                .map(orderDetailInfo -> BigDecimal.valueOf(APIOrderDetail.getShippingFeeAfterDiscount(orderDetailInfo)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Shipping fee calculated: {}", totalShippingFee);
        return totalShippingFee;
    }

    /**
     * Calculates the total shipping discount for non-cancelled orders in the provided order list.
     *
     * @param nonCancelledOrderInfos The list of non-cancelled order details to process.
     * @return The total shipping discount as a BigDecimal.
     */
    public static BigDecimal calculatorShippingDiscount(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Calculating shipping discount...");
        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Shipping discount calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalShippingDiscount = nonCancelledOrderInfos.parallelStream()
                .map(orderDetailInfo -> BigDecimal.valueOf(APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.FREE_SHIPPING)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Shipping discount calculated: {}", totalShippingDiscount);
        return totalShippingDiscount;
    }


    /**
     * Calculates the total order cost for non-cancelled orders.
     *
     * @param nonCancelledOrderInfos The list of non-cancelled order details to process.
     * @return The total order cost as a BigDecimal.
     */
    public static BigDecimal calculatorOrderCost(List<OrderDetailInfo> nonCancelledOrderInfos) {
        logger.info("Calculating order cost for non-cancelled orders...");
        if (nonCancelledOrderInfos.isEmpty()) {
            logger.info("Total order cost calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalOrderCost = nonCancelledOrderInfos.parallelStream()
                .map(orderInfo -> BigDecimal.valueOf(APIOrderDetail.getOrderCost(orderInfo)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Total order cost calculated: {}", totalOrderCost);
        return totalOrderCost;
    }

    /**
     * Calculates the total tax for non-cancelled orders in the provided order list.
     * <p>
     * This method computes the total tax amount for all non-cancelled orders in the given order list.
     * The tax amount is derived from the order's total tax amount field and ensures that only
     * orders that are not marked as "CANCELLED" are included in the calculation.
     *
     * @param orderList The list of orders to process.
     * @return The total tax amount for non-cancelled orders as a BigDecimal.
     */
    public static BigDecimal calculatorTotalTax(OrderList orderList) {
        logger.info("Calculating total tax for cancelled orders...");
        if (orderList.getResponse() == null) {
            logger.info("Total tax calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalTax = orderList.getResponse().parallelStream()
                .filter(order -> !order.getStatus().equals("CANCELLED"))
                .map(OrderList.Order::getTotalTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Total tax calculated: {}", totalTax);
        return totalTax;
    }

    /**
     * Calculates the total received amount for the orders in the order list.
     *
     * @param orderList The list of orders to process.
     * @return The total received amount as a BigDecimal.
     */
    public static BigDecimal calculatorReceivedAmount(OrderList orderList) {
        logger.info("Calculating received amount for orders...");
        if (orderList.getResponse() == null) {
            logger.info("Total received amount calculated: {}", 0);
            return BigDecimal.ZERO;
        }

        BigDecimal totalReceivedAmount = orderList.getResponse().parallelStream()
                .map(OrderList.Order::getReceivedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("Total received amount calculated: {}", totalReceivedAmount);
        return totalReceivedAmount;
    }

    /**
     * Calculates the uncollected amount by adding the total non-cancelled amount and the cancelled amount,
     * then subtracting the received amount.
     *
     * @param totalNonCancelledAmount The total amount from non-cancelled orders.
     * @param cancelledAmount         The total amount from cancelled orders.
     * @param receivedAmount          The total received amount.
     * @return The uncollected amount as a BigDecimal.
     */
    public static BigDecimal calculateUncollectedAmount(BigDecimal totalNonCancelledAmount, BigDecimal cancelledAmount, BigDecimal receivedAmount) {
        logger.info("Calculating uncollected amount...");
        BigDecimal uncollectedAmount = totalNonCancelledAmount.add(cancelledAmount).subtract(receivedAmount);
        logger.info("Uncollected amount calculated: {}", uncollectedAmount);
        return uncollectedAmount;
    }


    /**
     * Calculates the total non-cancelled revenue by subtracting the refunded amount from the total non-cancelled amount.
     * <p>
     * This method calculates the revenue from all non-cancelled orders by subtracting the refunded amount
     * from the total amount of non-cancelled orders. The total amount includes all orders except for those
     * marked as "CANCELLED."
     *
     * @param totalNonCancelledAmount The total amount for non-cancelled orders.
     * @param refundedAmount          The total refunded amount.
     * @return The total non-cancelled revenue as a BigDecimal.
     */
    public static BigDecimal calculatorRevenue(BigDecimal totalNonCancelledAmount, BigDecimal refundedAmount) {
        logger.info("Calculating revenue...");
        BigDecimal revenue = totalNonCancelledAmount.subtract(refundedAmount);
        logger.info("Revenue calculated: {}", revenue);
        return revenue;
    }

    /**
     * Calculates the profit by subtracting the product cost and shipping fee from the revenue.
     *
     * @param revenue     The revenue.
     * @param productCost The total product cost.
     * @param shippingFee The shipping fee.
     * @return The profit as a BigDecimal.
     */
    public static BigDecimal calculatorProfit(BigDecimal revenue, BigDecimal productCost, BigDecimal shippingFee) {
        logger.info("Calculating profit...");
        BigDecimal profit = revenue.subtract(productCost).subtract(shippingFee);
        logger.info("Profit calculated: {}", profit);
        return profit;
    }

    /**
     * Calculates the profit after tax by subtracting the total tax from the profit.
     *
     * @param profit         The profit before tax.
     * @param totalTaxAmount The total tax amount.
     * @return The profit after tax as a BigDecimal.
     */
    public static BigDecimal calculatorProfitAfterTAX(BigDecimal profit, BigDecimal totalTaxAmount) {
        logger.info("Calculating profit after tax...");
        BigDecimal profitAfterTax = profit.subtract(totalTaxAmount);
        logger.info("Profit after tax calculated: {}", profitAfterTax);
        return profitAfterTax;
    }
}
