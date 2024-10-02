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
    public AnalyticsOrderSummaryInfo getOrderAnalyticsSummary(TimeFrame timeFrame){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Time-Zone", "Asia/Bangkok");

        if(timeFrame.equals(TimeFrame.CUSTOM_RANGE))
        {
            //Get startdate, enddate when timeFrame is custom
            String[] generateCustomTimeFrame = new APIGetOrderList(loginInformation).generateTimeFrame(timeFrame);
            String startDate = generateCustomTimeFrame[0].split("T")[0];
            String endDate = generateCustomTimeFrame[1].split("T")[0];
            GET_ORDER_ANALYTICS = GET_ORDER_ANALYTICS+"&toCustomTime="+startDate+"&fromCustomTime="+endDate;
        }
        Response response = api.get(GET_ORDER_ANALYTICS.formatted(loginInfo.getStoreID(),timeFrame.toString()),loginInfo.getAccessToken(),headerMap);
        response.then().statusCode(200);
        JSONObject responseJsonObject = new JSONObject(response.getBody().asString());
        System.out.println("responseJsonObject: "+responseJsonObject.get("analyticsOrderSummaryDTO"));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseJsonObject.get("analyticsOrderSummaryDTO").toString(), AnalyticsOrderSummaryInfo.class);
    }

    public void verifyOrderAnalyticAfterCreateOrder(AnalyticsOrderSummaryInfo analyticInfoBefore, OrderDetailInfo orderDetailInfo, TimeFrame timeFrame, Double productCostThisOrder){
        int totalOrderExpected = analyticInfoBefore.getTotalOrders()+1;
        Double totalAmountExpected = analyticInfoBefore.getTotalAmount() + orderDetailInfo.getOrderInfo().getTotalAmount();
        Double averageOrderExpected = totalAmountExpected/totalOrderExpected;
        Double receiveAmount = analyticInfoBefore.getReceivedAmount() + orderDetailInfo.getOrderInfo().getReceivedAmount();
        Double promotionCampaignExpected = analyticInfoBefore.getPromotionCampaign()+ APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.CAMPAIGN)+ APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.MEMBERSHIP)+ APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.WHOLESALE)+ APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.BUY_X_GET_Y);
        Double promotionCodeExpected = analyticInfoBefore.getPromotionCode() + APIOrderDetail.getPromotionValue(orderDetailInfo, PromotionType.COUPON);
        Double directDiscountExpected = analyticInfoBefore.getDirectDiscount() + (orderDetailInfo.getOrderInfo().getDirectDiscount()!=null ?orderDetailInfo.getOrderInfo().getDirectDiscount().getDiscountValue():0.0);
        Double redeemPointExpected = analyticInfoBefore.getRedeemPoints() + APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.POINT);
        Double shippingFeeAfterDiscount = orderDetailInfo.getOrderInfo().getOriginalShippingFee()!=null? orderDetailInfo.getOrderInfo().getOriginalShippingFee():0 - APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.FEE_SHIPPING);
        Double shippingFeeBefore = analyticInfoBefore.getShippingFee();
        Double shippingFeeAfterDiscountThisOrder = shippingFeeAfterDiscount>0?shippingFeeAfterDiscount:0;
        Double shippingFeeExpected = shippingFeeBefore + shippingFeeAfterDiscountThisOrder;
        Double shippingDiscountExpected = analyticInfoBefore.getShippingDiscount() + APIOrderDetail.getPromotionValue(orderDetailInfo,PromotionType.FEE_SHIPPING);
        Double uncollectedAmountExpected = analyticInfoBefore.getUncollectedAmount() + totalAmountExpected - receiveAmount;
        Double taxExpected = analyticInfoBefore.getTax() + orderDetailInfo.getOrderInfo().getTotalTaxAmount();
        Double revenueExpected = totalAmountExpected ;//= total amount - refund amount, but after order create, refund amount always  = 0.
        Double productCostExpected = analyticInfoBefore.getProductCost() + productCostThisOrder;
        Double profitExpected = revenueExpected - productCostExpected - shippingFeeExpected;
        Double profitAfterTaxExpected = profitExpected - taxExpected;
        //Get analytics order after order created
        AnalyticsOrderSummaryInfo analyticInfoAfter = getOrderAnalyticsSummary(timeFrame);
        //Verify
        Assert.assertEquals(analyticInfoAfter.getTotalOrders(),totalOrderExpected,"[Failed] Check order total.");
        Assert.assertEquals(analyticInfoAfter.getTotalAmount(),totalAmountExpected,"[Failed] Check total amount.");
        Assert.assertEquals(analyticInfoAfter.getAverageOrderValue(),averageOrderExpected,"[Failed] Check average order value.");
        Assert.assertEquals(analyticInfoAfter.getPromotionCampaign(),promotionCampaignExpected,"[Failed] Check promotion campaign.");
        Assert.assertEquals(analyticInfoAfter.getPromotionCode(),promotionCodeExpected,"[Failed] Check promotion code.");
        Assert.assertEquals(analyticInfoAfter.getDirectDiscount(),directDiscountExpected,"[Failed] Check direct discount.");
        Assert.assertEquals(analyticInfoAfter.getRedeemPoints(),redeemPointExpected,"[Failed] Check redeem point.");
        Assert.assertEquals(analyticInfoAfter.getShippingFee(),shippingFeeExpected,"[Failed] Check shipping fee.");
        Assert.assertEquals(analyticInfoAfter.getShippingDiscount(),shippingDiscountExpected,"[Failed] Check shipping discount.");
        Assert.assertEquals(analyticInfoAfter.getTax(),taxExpected,"[Failed] Check TAX.");
        Assert.assertEquals(analyticInfoAfter.getReceivedAmount(),receiveAmount,"[Failed] Check receive amount.");
        Assert.assertEquals(analyticInfoAfter.getUncollectedAmount(),uncollectedAmountExpected,"[Failed] Check uncollected amount.");
        Assert.assertEquals(analyticInfoAfter.getRevenue(), revenueExpected, "[Failed] Check revenue.");
        Assert.assertEquals(analyticInfoAfter.getProfit(), profitExpected,"[Failed] Check profit");
        Assert.assertEquals(analyticInfoAfter.getProfitAfterTax(), profitAfterTaxExpected, "[Failed] Check profit after TAX.");
        Assert.assertEquals(analyticInfoAfter.getProductCost(), productCostExpected, "[Failed] Check product cost.");
    }
}
