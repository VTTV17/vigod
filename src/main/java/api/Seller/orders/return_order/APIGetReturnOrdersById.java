package api.Seller.orders.return_order;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.math.BigDecimal;
import java.util.List;

/**
 * APIGetReturnOrdersById class retrieves return orders by order ID.
 */
public class APIGetReturnOrdersById {
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor to initialize the APIGetReturnOrdersById with login credentials.
     *
     * @param credentials the login credentials
     */
    public APIGetReturnOrdersById(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReturnOrder {
        private long id;
        private String returnOrderId;
        private long bcOrderId;
        private long customerId;
        private String customerName;
        private long storeId;
        private String status;
        private boolean restock;
        private String refundStatus;
        private String returnBranchId;
        private String returnBranchName;
        private String note;
        private String createdBy;
        private String createdDate;
        private String lastModifiedDate;
        private String lastModifiedBy;
        private BigDecimal totalRefund;
        private BigDecimal refundAmount;
        private String currency;
        private List<ReturnOrderItem> returnOrderItemList;
        private List<Object> returnPaymentHistoryList;
        private BigDecimal taxAmount;
        private BigDecimal shippingFeeEstimate;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ReturnOrderItem {
            private long id;
            private int itemId;
            private String name;
            private int modelId;
            private String modelName;
            private int quantity;
            private BigDecimal price;
            private String currency;
            private long returnOrderId;
            private String inventoryManageType;
            private List<Object> returnOrderItemImeiList;
            private BigDecimal totalDiscount;
            private long orderItemId;
            private long parentItemId;
            private long parentModelId;
            private BigDecimal taxAmount;
        }
    }

    /**
     * Retrieves return orders by the given order ID.
     *
     * @param orderId the order ID to search for return orders
     * @return a list of ReturnOrder objects associated with the provided order ID
     */
    public List<ReturnOrder> getReturnOrdersById(long orderId) {
        String path = String.format("/orderservices2/api/return-orders/find-by-bc-order/%d", orderId);

        // Perform the API request and get the response as a String
        String responseString = new API()
                .get(path, loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .asPrettyString();

        // Parse the JSON response to a List of ReturnOrder objects
        try {
            return new ObjectMapper().readValue(responseString, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse return orders response", e);
        }
    }


    /**
     * Counts the number of completed return orders from the provided list.
     *
     * @param returnOrders the list of return orders to process
     * @return the count of completed return orders
     */
    public static int returnOrderCompletedCount(List<ReturnOrder> returnOrders) {
        if (returnOrders.isEmpty()) {
            return 0;
        }
        return (int) returnOrders.stream()
                .filter(returnOrder -> returnOrder.getStatus().equals("COMPLETED"))
                .count();
    }

    /**
     * Calculates the total refund amount for completed return orders.
     *
     * @param returnOrders the list of return orders to calculate the refund from.
     * @return the total refund amount as a BigDecimal, summing only the "COMPLETED" return orders.
     */
    public static BigDecimal getCompletedRefundAmount(List<ReturnOrder> returnOrders) {
        if (returnOrders.isEmpty()) return BigDecimal.ZERO;

        return returnOrders.parallelStream()
                .filter(returnOrder -> returnOrder.getStatus().equals("COMPLETED"))
                .map(ReturnOrder::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates the total refund amount for confirmed return orders, excluding those that haven't been refunded.
     *
     * @param returnOrders the list of return orders to calculate the refund from.
     * @return the total refund amount as a BigDecimal, summing only the orders where refund status is not "NOT_REFUND".
     */
    public static BigDecimal getConfirmedRefundAmount(List<ReturnOrder> returnOrders) {
        if (returnOrders.isEmpty()) return BigDecimal.ZERO;
        return returnOrders.parallelStream()
                .filter(returnOrder -> !returnOrder.getRefundStatus().equals("NOT_REFUND"))
                .map(ReturnOrder::getRefundAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}