package utilities.model.dashboard.analytics;

import lombok.Data;

@Data
public class AnalyticsOrderSummaryInfo {
    private int id;
    private int totalOrders;
    private Double totalAmount;
    private int totalCancelledOrders;
    private Double totalCancelledAmount;
    private int totalReturnedOrders;
    private Double totalRefundAmount;
    private Double confirmedRefundedAmount;
    private Double averageOrderValue;
    private Double productCost;
    private Double promotionCampaign;
    private Double promotionCode;
    private Double directDiscount;
    private Double redeemPoints;
    private Double shippingFee;
    private Double shippingDiscount;
    private Double orderCost;
    private Double tax;
    private Double receivedAmount;
    private Double uncollectedAmount;
    private Double revenue;
    private Double profit;
    private Double profitAfterTax;
    private int totalBuyers;
}
