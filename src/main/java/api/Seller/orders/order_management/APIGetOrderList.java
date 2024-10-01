package api.Seller.orders.order_management;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class provides functionality to retrieve and manage orders from the GOSELL and BEECOW platforms.
 * It utilizes REST API calls to fetch order lists based on time frames and channels and aggregates order information.
 */
public class APIGetOrderList {
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the class with login information.
     * @param credentials The login credentials for the seller.
     */
    public APIGetOrderList(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Represents the order list, including the list of orders and a summary view model.
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
            private String createdBy;
            private String updatedDate;
            private int itemsCount;
            private String displayName;
            private String userId;
            private String appInstall;
            private String location;
            private String orderType;
            private List<Item> items;
            private Long customerId;
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
            private String inStore;
            private Long branchId;
            private String branchName;
            private String branchType;
            private String userName;
            private String wholesaleId;
            private String discountCode;
            private BigDecimal discountAmount;
            private BigDecimal shippingFee;
            private BigDecimal feeDeduction;
            private BigDecimal totalTaxAmount;
            private BigDecimal totalCostPrice;
            private BigDecimal debtAmount;
            private Boolean isPaid;
            private BigDecimal receivedAmount;
            private String payType;
            private List<PaymentHistory> paymentHistories;
            private boolean hasDebt;
            private String rawPaymentMethod;
            private String fullShippingAddress;
            private String fullShippingAddressEn;
            private String madeBy;
            private String statusUpdatedDate;
            private String customerSaleChannel;
            private int totalItems;
            private int totalItemQty;
            private String receiverDisplayName;
            private String creatorDisplayName;
            private BigDecimal earningPoint;
            private BigDecimal redeemPoint;
            private BigDecimal pointAmount;
            private BigDecimal exchangeAmount;

            /**
             * Represents an item in an order, including its ID, price, quantity, and other relevant details.
             */
            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Item {
                private Long id;
                private Long itemId;
                private String itemModelId;
                private String modelId;
                private String name;
                private BigDecimal price;
                private BigDecimal totalDiscount;
                private int quantity;
                private int weight;
                private String imageUrl;
                private String modelName;
                private BigDecimal costPrice;
                private String barcode;
                private BigDecimal orgPrice;
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
                private String note;
                private String paymentReceivedBy;
                private Long bcOrderId;
            }
        }

        /**
         * Represents the summarized information for an order list, including counts of orders by status and aggregated monetary values.
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
     * Generates the appropriate time frame for the order list based on the provided {@link TimeFrame} enum.
     * @param timeFrame The time frame to filter the orders.
     * @return A String array with the start and end date of the specified time frame.
     */
    String[] getTimeFrame(TimeFrame timeFrame) {
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
     * Retrieves the API response for an order list based on page index, channel, and time frame.
     * @param pageIndex The index of the page to retrieve.
     * @param channel The sales channel (e.g., "GOSELL" or "BEECOW").
     * @param timeFrame The time frame to filter the orders.
     * @return A REST-assured {@link Response} containing the order list.
     */
    private Response getOrderListResponse(int pageIndex, String channel, TimeFrame timeFrame) {
        String branchIds = loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", "");
        String path = "/beehiveservices/api/orders/gosell-store/v2/%d?page=%d&size=50&channel=%s&noOfProductFilterType=ALL&qtyProductFilterType=ALL&fromDate=%s&toDate=%s&branchIds=%s";
        String[] timeFrames = getTimeFrame(timeFrame);
        return new API().get(path.formatted(loginInfo.getStoreID(), pageIndex, channel, timeFrames[0], timeFrames[1], branchIds), loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().response();
    }

    /**
     * Parses the API response data into a list of {@link OrderList} objects, retrieving data from multiple pages if necessary.
     * @param timeFrame The time frame to filter the orders.
     * @param channel The sales channel (e.g., "GOSELL" or "BEECOW").
     * @return A list of parsed {@link OrderList} objects.
     */
    private List<OrderList> parseData(TimeFrame timeFrame, String channel) {
        int totalOfOrders = Integer.parseInt(getOrderListResponse(0, channel, timeFrame).getHeader("X-Total-Count"));
        int numberOfPages = Math.min(totalOfOrders / 100, 99);

        return IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj((int pageIndex) -> getOrderListResponse(pageIndex, channel, timeFrame))
                .map(response -> response.as(OrderList.class))
                .toList();
    }

    /**
     * Generates a combined order list with aggregated summary information based on a given time frame.
     * @param timeFrame The time frame to filter the orders.
     * @return A combined {@link OrderList} that includes orders from multiple sources and their summarized information.
     */
    public OrderList getOrderList(TimeFrame timeFrame) {
        var goSELLOrders = parseData(timeFrame, "GOSELL");
        var goMUAOrders = parseData(timeFrame, "BEECOW");

        OrderList.OrderListSummaryVM summary = aggregateSummary(goSELLOrders.get(0), goMUAOrders.get(0));

        List<OrderList.Order> combinedOrders = combineOrders(goSELLOrders, goMUAOrders);

        return new OrderList(combinedOrders, summary);
    }

    /**
     * Aggregates the order list summaries from GOSELL and BEECOW, with null checks to handle missing summaries.
     * @param goSELLOrders The first order summary from GOSELL.
     * @param goMUAOrders The first order summary from BEECOW.
     * @return An aggregated {@link OrderList.OrderListSummaryVM}.
     */
    private OrderList.OrderListSummaryVM aggregateSummary(OrderList goSELLOrders, OrderList goMUAOrders) {
        OrderList.OrderListSummaryVM goSELLSummary = goSELLOrders.getOrderListSummaryVM();
        OrderList.OrderListSummaryVM goMUASummary = goMUAOrders.getOrderListSummaryVM();

        int toConfirmCount = (goSELLSummary != null ? goSELLSummary.getToConfirmCount() : 0)
                             + (goMUASummary != null ? goMUASummary.getToConfirmCount() : 0);

        int shippedCount = (goSELLSummary != null ? goSELLSummary.getShippedCount() : 0)
                           + (goMUASummary != null ? goMUASummary.getShippedCount() : 0);

        int deliveredCount = (goSELLSummary != null ? goSELLSummary.getDeliveredCount() : 0)
                             + (goMUASummary != null ? goMUASummary.getDeliveredCount() : 0);

        int cancelledCount = (goSELLSummary != null ? goSELLSummary.getCancelledCount() : 0)
                             + (goMUASummary != null ? goMUASummary.getCancelledCount() : 0);

        BigDecimal customerDebt = aggregateBigDecimal(
                goSELLSummary != null ? goSELLSummary.getCustomerDebt() : BigDecimal.ZERO,
                goMUASummary != null ? goMUASummary.getCustomerDebt() : BigDecimal.ZERO
        );

        BigDecimal sellerDebt = aggregateBigDecimal(
                goSELLSummary != null ? goSELLSummary.getSellerDebt() : BigDecimal.ZERO,
                goMUASummary != null ? goMUASummary.getSellerDebt() : BigDecimal.ZERO
        );

        BigDecimal receivedAmount = aggregateBigDecimal(
                goSELLSummary != null ? goSELLSummary.getReceivedAmount() : BigDecimal.ZERO,
                goMUASummary != null ? goMUASummary.getReceivedAmount() : BigDecimal.ZERO
        );

        return new OrderList.OrderListSummaryVM(toConfirmCount, shippedCount, deliveredCount, cancelledCount, customerDebt, sellerDebt, receivedAmount);
    }

    /**
     * Combines all orders from GOSELL and BEECOW into a single list.
     * @param goSELLOrders The list of orders from GOSELL.
     * @param goMUAOrders The list of orders from BEECOW.
     * @return A combined list of orders from both sources.
     */
    private List<OrderList.Order> combineOrders(List<OrderList> goSELLOrders, List<OrderList> goMUAOrders) {
        return Stream.concat(
                goSELLOrders.stream().map(OrderList::getResponse).flatMap(Collection::stream),
                goMUAOrders.stream().map(OrderList::getResponse).flatMap(Collection::stream)
        ).toList();
    }

    /**
     * Aggregates two BigDecimal values by adding them together.
     * @param value1 The first BigDecimal value.
     * @param value2 The second BigDecimal value.
     * @return The sum of the two BigDecimal values.
     */
    private BigDecimal aggregateBigDecimal(BigDecimal value1, BigDecimal value2) {
        return value1.add(value2);
    }

    public static long getTotalProductCost(OrderList orderList) {
        var itemList = orderList.getResponse().parallelStream().map(OrderList.Order::getItems).flatMap(Collection::stream).toList();

        return itemList.stream().mapToLong(item -> item.getCostPrice().longValue() * item.getQuantity()).sum();
    }
}
