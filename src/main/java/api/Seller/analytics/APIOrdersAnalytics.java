package api.Seller.analytics;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIGetOrderList;
import api.Seller.orders.order_management.APIOrderDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import utilities.api.API;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.data.GetDataByRegex;
import utilities.enums.PromotionType;
import utilities.enums.analytics.TimeFrame;
import utilities.model.dashboard.analytics.AnalyticsOrderSummaryInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.dashboard.orders.orderdetail.SummaryDiscount;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.marketing.affiliate.payout.payoutinformation.PayoutInformationPage;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIOrdersAnalytics {
    String GET_ORDER_ANALYTICS = "analyticsservice/api/analytics-order/store/%s/gosell?timeFrame=%s";
    LoginDashboardInfo loginInfo;
    API api = new API();
    LoginInformation loginInformation;
    final static Logger logger = LogManager.getLogger(APIOrdersAnalytics.class);

    public APIOrdersAnalytics(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @SneakyThrows
    public AnalyticsOrderSummaryInfo getOrderAnalyticsSummary(TimeFrame timeFrame) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Time-Zone", "Asia/Bangkok");
        if (timeFrame.equals(TimeFrame.CUSTOM_RANGE)) {
            //Get startdate, enddate when timeFrame is custom
            String[] generateCustomTimeFrame = new APIGetOrderList(loginInformation).generateTimeFrame(timeFrame);
            String startDate = generateCustomTimeFrame[0].split("T")[0];
            String endDate = generateCustomTimeFrame[1].split("T")[0];
            GET_ORDER_ANALYTICS = GET_ORDER_ANALYTICS + "&toCustomTime=" + startDate + "&fromCustomTime=" + endDate;
        }
        Response response = api.get(GET_ORDER_ANALYTICS.formatted(loginInfo.getStoreID(), timeFrame.toString()), loginInfo.getAccessToken(), headerMap);
        response.then().statusCode(200);
        JSONObject responseJsonObject = new JSONObject(response.getBody().asString());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseJsonObject.get("analyticsOrderSummaryDTO").toString(), AnalyticsOrderSummaryInfo.class);
    }

    public void verifyOrderAnalyticAfterCreateOrder(AnalyticsOrderSummaryInfo analyticInfoBefore, OrderDetailInfo orderDetailInfo, TimeFrame timeFrame, Double productCostThisOrder) {
        //Get analytics order after order created
        AnalyticsOrderSummaryInfo analyticInfoAfter = getOrderAnalyticsSummary(timeFrame);
        List<TimeFrame> pastTimeFrame = List.of(TimeFrame.YESTERDAY, TimeFrame.YESTERDAY,
                TimeFrame.LAST_MONTH, TimeFrame.LAST_WEEK, TimeFrame.LAST_YEAR);

        if (pastTimeFrame.contains(timeFrame)) {
            Assert.assertEquals(analyticInfoBefore, analyticInfoAfter, "[Failed] Check analytic order info at yesterday.");
            return;
        }
        int totalOrderExpected = analyticInfoBefore.getTotalOrders() + 1;
        double totalAmountExpected = analyticInfoBefore.getTotalAmount() + orderDetailInfo.getOrderInfo().getTotalPrice();
        int cancelledOrdersExpected = analyticInfoBefore.getTotalCancelledOrders();
        double cancelledAmountExpected = analyticInfoBefore.getTotalCancelledAmount();
        int returnedOrdersExpected = analyticInfoBefore.getTotalReturnedOrders();
        double refundAmountExpected = analyticInfoBefore.getTotalRefundAmount();
        double confirmedRefundedAmount = analyticInfoBefore.getConfirmedRefundedAmount();
        double averageOrderExpected = totalAmountExpected / totalOrderExpected;
        double productCostExpected = analyticInfoBefore.getProductCost() + productCostThisOrder;
        double promotionCampaignExpected = analyticInfoBefore.getPromotionCampaign() + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.CAMPAIGN) + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.MEMBERSHIP) + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.WHOLESALE) + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.BUY_X_GET_Y);
        double promotionCodeExpected = analyticInfoBefore.getPromotionCode() + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.COUPON);
        double directDiscountExpected = analyticInfoBefore.getDirectDiscount() + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.DIRECT_DISCOUNT);
        double redeemPointExpected = analyticInfoBefore.getRedeemPoints() + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.POINT);
        double shippingFeeBefore = analyticInfoBefore.getShippingFee();
        double shippingFeeAfterDiscountThisOrder = orderDetailInfo.getOrderInfo().getShippingFee() == null ? 0 : orderDetailInfo.getOrderInfo().getShippingFee();
        double shippingFeeExpected = shippingFeeBefore + shippingFeeAfterDiscountThisOrder;
        double shippingDiscountExpected = analyticInfoBefore.getShippingDiscount() + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.FREE_SHIPPING);
        double orderCostExpected = analyticInfoBefore.getOrderCost();
        double receiveAmountExpected = analyticInfoBefore.getReceivedAmount() + orderDetailInfo.getOrderInfo().getReceivedAmount();
        double uncollectedAmountExpected = totalAmountExpected - receiveAmountExpected;
        double taxExpected = analyticInfoBefore.getTax() + orderDetailInfo.getOrderInfo().getTotalTaxAmount();
        double revenueExpected = totalAmountExpected - refundAmountExpected;
        double profitExpected = revenueExpected - productCostExpected - shippingFeeExpected;
        double profitAfterTaxExpected = profitExpected - taxExpected;

        //Verify
        Assert.assertEquals(analyticInfoAfter.getTotalOrders(), totalOrderExpected, "[Failed] Check order total.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getTotalAmount()), String.format("%.2f", totalAmountExpected), "[Failed] Check total amount.");
        Assert.assertEquals(analyticInfoAfter.getTotalCancelledOrders(), cancelledOrdersExpected, "[Failed] Check total cancel orders number.");
        Assert.assertEquals(analyticInfoAfter.getTotalCancelledAmount(), cancelledAmountExpected, "[Failed] Check total cancel amount.");
        Assert.assertEquals(analyticInfoAfter.getTotalReturnedOrders(), returnedOrdersExpected, "[Failed] Check return orders number.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getTotalRefundAmount()), String.format("%.2f", refundAmountExpected), "[Failed] Check total refund amount");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getConfirmedRefundedAmount()), String.format("%.2f", confirmedRefundedAmount), "[Failed] Check confirmed refunded amount.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getAverageOrderValue()), String.format("%.2f", averageOrderExpected), "[Failed] Check average order value.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getPromotionCampaign()), String.format("%.2f", promotionCampaignExpected), "[Failed] Check promotion campaign.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getPromotionCode()), String.format("%.2f", promotionCodeExpected), "[Failed] Check promotion code.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getDirectDiscount()), String.format("%.2f", directDiscountExpected), "[Failed] Check direct discount.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getRedeemPoints()), String.format("%.2f", redeemPointExpected), "[Failed] Check redeem point.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getShippingFee()), String.format("%.2f", shippingFeeExpected), "[Failed] Check shipping fee.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getShippingDiscount()), String.format("%.2f", shippingDiscountExpected), "[Failed] Check shipping discount.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getOrderCost()), String.format("%.2f", orderCostExpected), "[Failed] Check total order cost.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getTax()), String.format("%.2f", taxExpected), "[Failed] Check TAX.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getReceivedAmount()), String.format("%.2f", receiveAmountExpected), "[Failed] Check receive amount.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getUncollectedAmount()), String.format("%.2f", uncollectedAmountExpected), "[Failed] Check uncollected amount.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getRevenue()), String.format("%.2f", revenueExpected), "[Failed] Check revenue.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getProfit()), String.format("%.2f", profitExpected), "[Failed] Check profit");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getProfitAfterTax()), String.format("%.2f", profitAfterTaxExpected), "[Failed] Check profit after TAX.");
        Assert.assertEquals(String.format("%.2f", analyticInfoAfter.getProductCost()), String.format("%.2f", productCostExpected), "[Failed] Check product cost.");
    }

    @SneakyThrows
    public void waitOrderAnalyticsUpdateData(int totalOrderBefore, TimeFrame timeFrame) {
        for (int i = 0; i < 10; i++) {
            int totalOrderCurrent = getOrderAnalyticsSummary(timeFrame).getTotalOrders();
            if (totalOrderCurrent != totalOrderBefore) {
                logger.info("Total order updated.");
                return;
            }
            logger.info("Wait analytic order summary update.");
            Thread.sleep(500);
        }
    }
}
