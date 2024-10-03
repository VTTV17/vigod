package web.Dashboard;

import api.Seller.analytics.APIOrdersAnalytics;
import api.Seller.orders.order_management.APIGetOrderList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utilities.enums.analytics.TimeFrame;
import utilities.model.sellerApp.login.LoginInformation;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static api.Seller.orders.order_management.APIGetOrderList.*;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

/**
 * Test class for validating order analytics through various time frames and channels.
 */
public class CheckOrderAnalyticsTest extends BaseTest {
    LoginInformation loginInformation;

    /**
     * Sets up the test class by initializing the LoginInformation instance.
     */
    @BeforeClass
    void setup() {
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
    }

    /**
     * Provides test data for order analytics testing.
     * Each data set consists of a TimeFrame and a channel.
     *
     * @return An array of test data.
     */
    @DataProvider(name = "analyticsTestData")
    public Object[][] productTestData() {
        return new Object[][]{
                {TimeFrame.TODAY, "GOSELL"},
                {TimeFrame.YESTERDAY, "GOSELL"},
                {TimeFrame.LAST_7_DAYS, "GOSELL"},
                {TimeFrame.LAST_30_DAYS, "GOSELL"},
                {TimeFrame.THIS_WEEK, "GOSELL"},
                {TimeFrame.LAST_WEEK, "GOSELL"},
                {TimeFrame.THIS_MONTH, "GOSELL"},
                {TimeFrame.LAST_MONTH, "GOSELL"},
                {TimeFrame.THIS_YEAR, "GOSELL"},
                {TimeFrame.LAST_YEAR, "GOSELL"},
                {TimeFrame.CUSTOM_RANGE, "GOSELL"},
        };
    }

    /**
     * Tests the order analytics for a given time frame and channel.
     *
     * @param timeFrame The time frame for the analytics.
     * @param channel   The channel for which analytics are performed.
     */
    @Test(dataProvider = "analyticsTestData")
    private void orderAnalyticsTest(TimeFrame timeFrame, String channel) {
        verifyOrderAnalyticsTest(timeFrame, channel);
    }

    /**
     * Validates order analytics for a given time frame and channel by asserting various metrics.
     *
     * @param timeFrame The time frame for the analytics.
     * @param channel   The channel for which analytics are performed.
     */
    private void verifyOrderAnalyticsTest(TimeFrame timeFrame, String channel) {
        SoftAssert softAssert = new SoftAssert();
        var orderAnalytics = new APIOrdersAnalytics(loginInformation).getOrderAnalyticsSummary(timeFrame);
        var orderList = new APIGetOrderList(loginInformation).getAggregatedOrderList(timeFrame, channel);
        var nonCancelledOrderInfos = new APIGetOrderList(loginInformation).getNonCancelledOrderDetails(orderList);
        var returnOrdersMap = new APIGetOrderList(loginInformation).getReturnOrdersMap(orderList);

        // Check total orders
        int totalOrders = getOrderCount(orderList);
        softAssert.assertEquals(orderAnalytics.getTotalOrders(), totalOrders,
                String.format("Total orders must be '%,d', but found '%,d'", orderAnalytics.getTotalOrders(), totalOrders));

        // Check order cost
        BigDecimal orderCost = calculatorOrderCost(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getOrderCost(), orderCost.doubleValue(),
                String.format("Order cost must be '%,.2f', but found '%,.2f'", orderAnalytics.getOrderCost(), orderCost.doubleValue()));

        // Check non-cancelled total amount
        BigDecimal nonCancelledTotalAmount = getNonCancelledTotalAmount(orderList, orderCost);
        softAssert.assertEquals(orderAnalytics.getTotalAmount(), nonCancelledTotalAmount.doubleValue(),
                String.format("Non-cancelled total amounts must be '%,.2f', but found '%,.2f'", orderAnalytics.getTotalAmount(), nonCancelledTotalAmount.doubleValue()));

        // Check product cost
        BigDecimal returnOrderProductCost = getReturnOrdersProductCost(returnOrdersMap);
        BigDecimal productCost = calculateTotalProductCost(orderList, returnOrderProductCost);
        softAssert.assertEquals(orderAnalytics.getProductCost(), productCost.doubleValue(),
                String.format("Product cost must be '%,.2f', but found '%,.2f'", orderAnalytics.getProductCost(), productCost.doubleValue()));

        // Check average order value
        BigDecimal avgOrderValue = getAverageOrderValue(nonCancelledTotalAmount, totalOrders);
        long expectedAvgOrderValue = avgOrderValue.longValue();
        long actualAvgOrderValue = BigDecimal.valueOf(orderAnalytics.getAverageOrderValue())
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        softAssert.assertEquals(actualAvgOrderValue, expectedAvgOrderValue,
                String.format("Average order value must be '%,d', but found '%,d'", expectedAvgOrderValue, actualAvgOrderValue));

        // Check return orders
        int totalReturnOrders = getCompletedReturnOrderCount(returnOrdersMap);
        softAssert.assertEquals(orderAnalytics.getTotalReturnedOrders(), totalReturnOrders,
                String.format("Total returned orders must be '%,d', but found '%,d'", orderAnalytics.getTotalReturnedOrders(), totalReturnOrders));

        // Check refunded amount
        BigDecimal refundAmount = getCompletedReturnAmount(returnOrdersMap);
        softAssert.assertEquals(orderAnalytics.getTotalRefundAmount(), refundAmount.doubleValue(),
                String.format("Refunded amount must be '%,.2f', but found '%,.2f'", orderAnalytics.getTotalRefundAmount(), refundAmount.doubleValue()));

        // Check confirmed refund amount
        BigDecimal confirmedAmount = getConfirmedReturnAmount(returnOrdersMap);
        softAssert.assertEquals(orderAnalytics.getConfirmedRefundedAmount(), confirmedAmount.doubleValue(),
                String.format("Confirmed refund amount must be '%,.2f', but found '%,.2f'", orderAnalytics.getConfirmedRefundedAmount(), confirmedAmount.doubleValue()));

        // Check cancelled order count
        int totalCanceledOrder = getCancelledOrderCount(orderList);
        softAssert.assertEquals(orderAnalytics.getTotalCancelledOrders(), totalCanceledOrder,
                String.format("Total cancelled orders must be '%,d', but found '%,d'", orderAnalytics.getTotalCancelledOrders(), totalCanceledOrder));

        // Check cancelled amount
        BigDecimal cancelledAmount = getCancelledAmount(orderList);
        softAssert.assertEquals(orderAnalytics.getTotalCancelledAmount(), cancelledAmount.doubleValue(),
                String.format("Cancelled amount must be '%,.2f', but found '%,.2f'", orderAnalytics.getTotalCancelledAmount(), cancelledAmount.doubleValue()));

        // Check redeem points
        BigDecimal redeemPoints = calculateTotalRedeemPoint(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getRedeemPoints(), redeemPoints.doubleValue(),
                String.format("Redeem points must be '%,.2f', but found '%,.2f'", orderAnalytics.getRedeemPoints(), redeemPoints.doubleValue()));

        // Check direct discount
        BigDecimal directDiscount = calculateDirectDiscount(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getDirectDiscount(), directDiscount.doubleValue(),
                String.format("Direct discount must be '%,.2f', but found '%,.2f'", orderAnalytics.getDirectDiscount(), directDiscount.doubleValue()));

        // Check promotion code
        BigDecimal promotionCode = calculatePromotionCode(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getPromotionCode(), promotionCode.doubleValue(),
                String.format("Promotion code must be '%,.2f', but found '%,.2f'", orderAnalytics.getPromotionCode(), promotionCode.doubleValue()));

        // Check promotion campaign
        BigDecimal promotionCampaign = calculatePromotionCampaign(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getPromotionCampaign(), promotionCampaign.doubleValue(),
                String.format("Promotion campaign must be '%,.2f', but found '%,.2f'", orderAnalytics.getPromotionCampaign(), promotionCampaign.doubleValue()));

        // Check shipping fee
        BigDecimal shippingFee = calculatorShippingFee(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getShippingFee(), shippingFee.doubleValue(),
                String.format("Shipping fee must be '%,.2f', but found '%,.2f'", orderAnalytics.getShippingFee(), shippingFee.doubleValue()));

        // Check shipping discount
        BigDecimal shippingDiscount = calculatorShippingDiscount(nonCancelledOrderInfos);
        softAssert.assertEquals(orderAnalytics.getShippingDiscount(), shippingDiscount.doubleValue(),
                String.format("Shipping discount must be '%,.2f', but found '%,.2f'", orderAnalytics.getShippingDiscount(), shippingDiscount.doubleValue()));

        // Check tax amount
        BigDecimal taxAmount = calculatorTotalTax(orderList);
        softAssert.assertEquals(orderAnalytics.getTax(), taxAmount.doubleValue(),
                String.format("Tax amount must be '%,.2f', but found '%,.2f'", orderAnalytics.getTax(), taxAmount.doubleValue()));

        // Check received amount
        BigDecimal receivedAmount = calculatorReceivedAmount(orderList);
        softAssert.assertEquals(orderAnalytics.getReceivedAmount(), receivedAmount.doubleValue(),
                String.format("Received amount must be '%,.2f', but found '%,.2f'", orderAnalytics.getReceivedAmount(), receivedAmount.doubleValue()));

        // Check uncollected amount
        BigDecimal uncollectedAmount = calculateUncollectedAmount(nonCancelledTotalAmount, cancelledAmount, receivedAmount);
        softAssert.assertEquals(orderAnalytics.getUncollectedAmount(), uncollectedAmount.doubleValue(),
                String.format("Uncollected amount must be '%,.2f', but found '%,.2f'", orderAnalytics.getUncollectedAmount(), uncollectedAmount.doubleValue()));

        // Check revenue
        BigDecimal revenue = calculatorRevenue(nonCancelledTotalAmount, refundAmount);
        softAssert.assertEquals(orderAnalytics.getRevenue(), revenue.doubleValue(),
                String.format("Revenue must be '%,.2f', but found '%,.2f'", orderAnalytics.getRevenue(), revenue.doubleValue()));

        // Check profit
        BigDecimal profit = calculatorProfit(revenue, productCost, shippingFee);
        softAssert.assertEquals(orderAnalytics.getProfit(), profit.doubleValue(),
                String.format("Profit must be '%,.2f', but found '%,.2f'", orderAnalytics.getProfit(), profit.doubleValue()));

        // Check profit after tax
        BigDecimal profitAfterTax = calculatorProfitAfterTAX(profit, taxAmount);
        softAssert.assertEquals(orderAnalytics.getProfitAfterTax(), profitAfterTax.doubleValue(),
                String.format("Profit after tax must be '%,.2f', but found '%,.2f'", orderAnalytics.getProfitAfterTax(), profitAfterTax.doubleValue()));

        // Assert all to collect all failed assertions
        softAssert.assertAll();
    }
}
