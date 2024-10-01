package api.Seller.orders.order_management;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utilities.api.API;
import utilities.enums.analytics.TimeFrame;
import utilities.helper.DateTimeRangeGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class provides functionality to retrieve and manage orders from GOSELL and BEECOW platforms.
 * It utilizes REST API calls to fetch order lists based on time frames and channels, and aggregates order information.
 */
public class APIGetOrderList {
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the class with login information.
     *
     * @param credentials The login credentials for the seller.
     */
    public APIGetOrderList(LoginInformation credentials) {
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
        private OrderListSummaryVM summary;

        /**
         * Represents individual orders with detailed information such as product details, customer info, and payment histories.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Order {
            private String id;
            private String productNames;
            private String bcOrderGroupId;
            private String channel;
            private Long storeId;
            private String status;
            private String buyerName;
            private BigDecimal total;
            private BigDecimal subTotal;
            private String currency;
            private String paymentMethod;
            private String note;
            private String createdDate;
            private String updatedDate;
            private int itemsCount;
            private String displayName;
            private String customerName;
            private Long customerId;
            private List<Item> items;
            private List<PaymentHistory> paymentHistories;
            private boolean hasDebt;
            private String fullShippingAddress;
            private String receiverDisplayName;
            private String creatorDisplayName;

            /**
             * Represents an item in an order, including its ID, price, quantity, and other relevant details.
             */
            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Item {
                private Long id;
                private String name;
                private BigDecimal price;
                private int quantity;
                private String barcode;
                private BigDecimal costPrice;
            }

            /**
             * Represents the payment history of an order, including payment method, amount, and the date of the transaction.
             */
            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class PaymentHistory {
                private Long id;
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
    private String[] generateTimeFrame(TimeFrame timeFrame) {
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
     *         along with aggregated summary information.
     */
    public OrderList getAggregatedOrderList(TimeFrame timeFrame, String channel) {
        var orderLists = retrieveOrderData(timeFrame, channel);

        OrderList.OrderListSummaryVM summary = summarizeOrderList(orderLists.get(0));
        List<OrderList.Order> combinedOrders = flattenOrderListResponses(orderLists);

        return new OrderList(combinedOrders, summary);
    }

    /**
     * Aggregates the summary of an order list.
     *
     * @param orderList The order list to summarize.
     * @return An aggregated {@link OrderList.OrderListSummaryVM}.
     */
    public OrderList.OrderListSummaryVM summarizeOrderList(OrderList orderList) {
        OrderList.OrderListSummaryVM summary = orderList.getSummary();

        int toConfirmCount = summary != null ? summary.getToConfirmCount() : 0;
        int shippedCount = summary != null ? summary.getShippedCount() : 0;
        int deliveredCount = summary != null ? summary.getDeliveredCount() : 0;
        int cancelledCount = summary != null ? summary.getCancelledCount() : 0;

        BigDecimal customerDebt = summary != null ? summary.getCustomerDebt() : BigDecimal.ZERO;
        BigDecimal sellerDebt = summary != null ? summary.getSellerDebt() : BigDecimal.ZERO;
        BigDecimal receivedAmount = summary != null ? summary.getReceivedAmount() : BigDecimal.ZERO;

        return new OrderList.OrderListSummaryVM(toConfirmCount, shippedCount, deliveredCount, cancelledCount, customerDebt, sellerDebt, receivedAmount);
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
     * Calculates the total product cost from the list of orders.
     *
     * @param orderList The list of orders to calculate from.
     * @return The total product cost as a long value.
     */
    public static long calculateTotalProductCost(OrderList orderList) {
        var itemList = orderList.getResponse().parallelStream()
                .map(OrderList.Order::getItems)
                .flatMap(Collection::stream)
                .toList();

        return itemList.stream()
                .mapToLong(item -> item.getCostPrice().longValue() * item.getQuantity())
                .sum();
    }
}
