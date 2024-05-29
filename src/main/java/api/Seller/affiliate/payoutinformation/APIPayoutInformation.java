package api.Seller.affiliate.payoutinformation;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.affiliate.PayoutByProductInfo;
import utilities.model.dashboard.marketing.affiliate.PayoutByRevenueInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Arrays;
import java.util.List;

public class APIPayoutInformation {
    String GET_PAYOUT_INFO_PRODUCT_PATH = "/affiliateservice/api/payouts-info/store/%s?page=0&size=50&searchKeywords=&partnerType=%s";
    String GET_PAYOUT_INFO_REVENUE_PATH = "/affiliateservice/api/revenue-commission/payout-info/%s?page=0&size=50&timeFrame=MONTHLY&fromDate=%s&searchKeywords=";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPayoutInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response getPayoutInfor_PartnerByProduct(boolean isDropship){
        String partnerType = isDropship?"DROP_SHIP":"RESELLER";
        Response response = api.get(GET_PAYOUT_INFO_PRODUCT_PATH.formatted(loginInfo.getStoreID(),partnerType),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public Response getPayoutInfor_PartnerByRevenue(){
        Response response = api.get(GET_PAYOUT_INFO_REVENUE_PATH.formatted(loginInfo.getStoreID(),new DataGenerator().generatePreviousTerm("yyyy-MM-dd")),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public PayoutByProductInfo getAPayoutHasPayableAmount_CommissionByProduct(boolean isDropship){
        Response response = getPayoutInfor_PartnerByProduct(isDropship);
        List<PayoutByProductInfo> payoutInfos = Arrays.asList(response.as(PayoutByProductInfo[].class));
        for (PayoutByProductInfo payout:payoutInfos) {
            if(payout.getPayableAmount()>0){
                return payout;
            }
        }
//        try {
//            throw new Exception("Not found partner has commission by product and payable amount");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }
    public PayoutByRevenueInfo getAPayoutHasPayableAmount_CommissionByRevenue(){
        Response response = getPayoutInfor_PartnerByRevenue();
        List<PayoutByRevenueInfo> payoutInfos = Arrays.asList(response.as(PayoutByRevenueInfo[].class));
        for (PayoutByRevenueInfo payout:payoutInfos){
            if(payout.getPayableAmount()>0){
                return payout;
            }
        }
//        try {
//            throw new Exception("Not found partner has commission by revenue and payable amount");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

}
