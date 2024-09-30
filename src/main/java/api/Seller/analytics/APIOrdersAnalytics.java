package api.Seller.analytics;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.testng.Assert;
import utilities.api.API;
import utilities.commons.UICommonAction;
import utilities.enums.PromotionType;
import utilities.enums.analytics.TimeFrame;
import utilities.model.dashboard.analytics.AnalyticsOrderSummaryInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.dashboard.orders.orderdetail.SummaryDiscount;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.HashMap;
import java.util.Map;

public class APIOrdersAnalytics {
    String GET_ORDER_ANALYTICS = "analyticsservice/api/analytics-order/store/%s/gosell?timeFrame=%s";
    LoginDashboardInfo loginInfo;
    API api = new API();
    LoginInformation loginInformation;

    public APIOrdersAnalytics(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public AnalyticsOrderSummaryInfo getOrderAnalyticsSummary(TimeFrame timeFrame){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Time-Zone", "Asia/Bangkok");
        //Get startdate, enddate when timeFrame is custom
        String startDate = "";
        String endDate = "";
        if(timeFrame.equals(TimeFrame.CUSTOM_RANGE)) GET_ORDER_ANALYTICS = GET_ORDER_ANALYTICS+"toCustomTime="+startDate+"fromCustomTime="+endDate;
        Response response = api.get(GET_ORDER_ANALYTICS.formatted(loginInfo.getStoreID(),timeFrame.toString()),loginInfo.getAccessToken(),headerMap);
        response.then().statusCode(200);
        return response.jsonPath().getObject("analyticsOrderSummaryDTO",AnalyticsOrderSummaryInfo.class);
    }
    private Double getPromotionValue(OrderDetailInfo orderDetailInfo, PromotionType promotionType){
        return orderDetailInfo.getSummaryDiscounts().stream()
                .filter(i -> i.getDiscountType().equals(promotionType.toString()))
                .mapToDouble(SummaryDiscount::getValue).sum();
    }
    public void verifyOrderAnalyticAfterCreateOrder(AnalyticsOrderSummaryInfo analyticInfoBefore, OrderDetailInfo orderDetailInfo, TimeFrame timeFrame, Double productCost){
        int totalOrderExpected = analyticInfoBefore.getTotalOrders()+1;
        Double totalAmountExpected = analyticInfoBefore.getTotalAmount() + orderDetailInfo.getOrderInfo().getTotalAmount();
        Double averageOrderExpected = totalAmountExpected/totalOrderExpected;
        Double receiveAmount = analyticInfoBefore.getReceivedAmount() + orderDetailInfo.getOrderInfo().getReceivedAmount();
        // Product cost
        Double promotionCampaignExpected = analyticInfoBefore.getPromotionCampaign()+ getPromotionValue(orderDetailInfo,PromotionType.CAMPAIGN);
        Double promotionCodeExpected = analyticInfoBefore.getPromotionCode() + getPromotionValue(orderDetailInfo, PromotionType.COUPON);
        Double directDiscountExpected = analyticInfoBefore.getDirectDiscount() + orderDetailInfo.getOrderInfo().getDirectDiscount().getDiscountValue();
        Double redeemPointExpected = analyticInfoBefore.getRedeemPoints() + getPromotionValue(orderDetailInfo,PromotionType.POINT);
        Double shippingFeeAfterDiscount = orderDetailInfo.getOrderInfo().getOriginalShippingFee() - getPromotionValue(orderDetailInfo,PromotionType.FEE_SHIPPING);
        Double shippingFeeExpected = analyticInfoBefore.getShippingFee() + shippingFeeAfterDiscount>0?shippingFeeAfterDiscount:0;
        Double shippingDiscountExpected = getPromotionValue(orderDetailInfo,PromotionType.FEE_SHIPPING);
        Double uncollectedAmount = totalAmountExpected - receiveAmount;
        Double taxExpected = analyticInfoBefore.getTax() + orderDetailInfo.getOrderInfo().getTotalTaxAmount();
        Double revenueExpected = totalAmountExpected ;//= total amount - refund amount, but after order create, refund amount always  = 0.
        Double profitExpected = revenueExpected - productCost - shippingFeeExpected;
        Double profitAfter = profitExpected - taxExpected;
        //Get analytics order after order created
        AnalyticsOrderSummaryInfo analyticInfoAfter = getOrderAnalyticsSummary(timeFrame);
        //Verify
        Assert.assertEquals(analyticInfoAfter.getTotalOrders(),totalOrderExpected,"[Failed] Check order total.");
        Assert.assertEquals(analyticInfoAfter.getTotalAmount(),totalAmountExpected,"[Failed] Check total amount.");
        Assert.assertEquals(analyticInfoAfter.getAverageOrderValue(),averageOrderExpected,"[Failed] Check average order value.");
        Assert.assertEquals(analyticInfoAfter.getPromotionCampaign(),promotionCampaignExpected,"[Failed] Check promotion campaign.");
        Assert.assertEquals(analyticInfoAfter.getProductCost(),promotionCodeExpected,"[Failed] Check promotion code.");
        Assert.assertEquals(analyticInfoAfter.getDirectDiscount(),directDiscountExpected,"[Failed] Check direct discount.");
        Assert.assertEquals(analyticInfoAfter.getRedeemPoints(),redeemPointExpected,"[Failed] Check redeem point.");
        Assert.assertEquals(analyticInfoAfter.getShippingFee(),shippingFeeExpected,"[Failed] Check shipping fee.");
        Assert.assertEquals(analyticInfoAfter.getShippingDiscount(),shippingDiscountExpected,"[Failed] Check shipping discount.");
        Assert.assertEquals(analyticInfoAfter.getTax(),taxExpected,"[Failed] Check TAX.");
        Assert.assertEquals(analyticInfoAfter.getReceivedAmount(),revenueExpected,"[Failed] Check receive amount.");
        Assert.assertEquals(analyticInfoAfter.getUncollectedAmount(),uncollectedAmount,"[Failed] Check uncollected amount.");
        Assert.assertEquals(analyticInfoAfter.getRevenue(), revenueExpected, "[Failed] Check revenue.");
    }
}
