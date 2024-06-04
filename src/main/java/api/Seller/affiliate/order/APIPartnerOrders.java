package api.Seller.affiliate.order;

import api.Seller.login.Login;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.enums.ApproveStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import javax.annotation.Nullable;
import java.util.List;

public class APIPartnerOrders {
    String GET_ORDERS_HAS_COMMISSION_BY_PRODUCT_PATH = "/affiliateservice/api/orders/store/%s?size=50&partnerType.equals=%s&isLikeBcOrderId=true%s";
    String GET_ORDERS_HAS_COMMISSION_BY_REVENUE_PATH = "/intergrateaffiliateservice/api/orders/revenue/store/%s?size=50&typeCommission=REVENUE_COMMISSION&searchType=ORDER_ID%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPartnerOrders(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    /**
     *
     * @param isDropship
     * @param filter: param to filter if any (example: "&approveStatus.equals=1111")
     * @return
     */
    public List<Integer> getOrderHasProductCommisionList(boolean isDropship, String...filter){
        String partnerType = isDropship? "DROP_SHIP": "RESELLER";
        Response response;
        if (filter.length == 0) {
            response = api.get(GET_ORDERS_HAS_COMMISSION_BY_PRODUCT_PATH.formatted(loginInfo.getStoreID(),partnerType,""),loginInfo.getAccessToken());
        }else response = api.get(GET_ORDERS_HAS_COMMISSION_BY_PRODUCT_PATH.formatted(loginInfo.getStoreID(),partnerType,filter[0]),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }
    public List<Integer> getOrderHasRevenueCommission(String...filter){
        Response response;
        if(filter.length==0){
            response = api.get(GET_ORDERS_HAS_COMMISSION_BY_REVENUE_PATH.formatted(loginInfo.getStoreID(),""),loginInfo.getAccessToken());
        }else response = api.get(GET_ORDERS_HAS_COMMISSION_BY_REVENUE_PATH.formatted(loginInfo.getStoreID(),filter[0]),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }

    public List<Integer> getOrderProductCommissionByApproveStatus(ApproveStatus approveStatus){
        String filter = "&approveStatus.equals=%s".formatted(approveStatus);
        return getOrderHasProductCommisionList(true,filter);
    }
    public List<Integer> getOrderRevenueCommissionByApproveStatus(ApproveStatus approveStatus){
        if(approveStatus.equals(ApproveStatus.ALL)) return  getOrderHasRevenueCommission();
        String filter = "&approveStatus=%s".formatted(approveStatus);
        return getOrderHasRevenueCommission(filter);
    }
    public List<Integer> getResellerOrderByApproveStatus(ApproveStatus approveStatus){
        if(approveStatus.equals(ApproveStatus.ALL)) return  getOrderHasProductCommisionList(false);
        String filter = "&approveStatus.equals=%s".formatted(approveStatus);
        return getOrderHasProductCommisionList(false,filter);
    }

}
