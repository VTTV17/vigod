package api.Seller.promotion;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class PromotionList {
    String DISCOUNT_CAMPAIGN_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=%s&status=%s";

    API api = new API();
    Logger logger = LogManager.getLogger(ProductDiscountCampaign.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public PromotionList (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response getPromotionListRes(String discountType, String status){
        String typeParam;
        switch (discountType){
            case "All Types" -> typeParam = "";
            case "Product Discount Code" -> typeParam = "COUPON";
            case "Service Discount Code" -> typeParam = "COUPON_SERVICE";
            case "Product Discount Campaign" -> typeParam = "WHOLE_SALE";
            case "Service Discount Campaign" -> typeParam = "WHOLE_SALE_SERVICE";
            default -> typeParam = "";
        }
        String statusParam;
        switch (status){
            case "All Status" -> statusParam = "";
            case "Scheduled" -> statusParam = "SCHEDULED";
            case "Expired" -> statusParam = "EXPIRED";
            case "In Progress" -> statusParam = "IN_PROGRESS";
            default -> statusParam = "";
        }
        Response response = api.get(DISCOUNT_CAMPAIGN_LIST_PATH.formatted(loginInfo.getStoreID(),typeParam,statusParam),loginInfo.getAccessToken());
        return response;
    }
    public int getDiscountId(String discountType, String status){
        Response response = getPromotionListRes(discountType,status);
        List<Integer> ids= response.then().statusCode(200).extract().jsonPath().getList("id");
        if(ids.isEmpty()){
            try {
                throw new Exception(discountType + " has status: '%s' not found".formatted(status));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ids.get(0);
    }
}
