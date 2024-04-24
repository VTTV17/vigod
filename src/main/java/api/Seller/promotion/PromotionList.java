package api.Seller.promotion;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class PromotionList {
    String DISCOUNT_CAMPAIGN_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=%s&status=%s&sort=lastModifiedDate,desc";
    String DELETE_DISCOUNT_CAMPAIGN_PATH = "/orderservices2/api/gs-discount-campaigns/";

    API api = new API();
    Logger logger = LogManager.getLogger(PromotionList.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public PromotionList (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response getPromotionListRes(String discountType, String status){
        String typeParam;
        switch (discountType){
            case "Product Discount Code" -> typeParam = "COUPON";
            case "Service Discount Code" -> typeParam = "COUPON_SERVICE";
            case "Product Discount Campaign" -> typeParam = "WHOLE_SALE";
            case "Service Discount Campaign" -> typeParam = "WHOLE_SALE_SERVICE";
            default -> typeParam = "";
        }
        String statusParam;
        switch (status){
            case "Scheduled" -> statusParam = "SCHEDULED";
            case "Expired" -> statusParam = "EXPIRED";
            case "In Progress" -> statusParam = "IN_PROGRESS";
            default -> statusParam = "";
        }
        Response response = api.get(DISCOUNT_CAMPAIGN_LIST_PATH.formatted(loginInfo.getStoreID(),typeParam,statusParam),loginInfo.getAccessToken());
        return response;
    }
    public int getDiscountId(String discountType, String status){
        List<Integer> ids= getDiscountIdList(discountType, status);
        return ids.isEmpty() ? -1 : ids.get(0);
    }
    public List<Integer> getDiscountIdList(String discountType, String status){
    	Response response = getPromotionListRes(discountType,status);
    	return response.then().statusCode(200).extract().jsonPath().getList("id");
    }
    public void endEarlyInprogressDiscountCampaign() {
        // get list in-progress discount campaign
        List<Integer> inProgressList = getPromotionListRes("","In Progress").jsonPath().getList("id");

        // end in-progress service campaign
        inProgressList.forEach(campaignID -> api.delete(DELETE_DISCOUNT_CAMPAIGN_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200));
    }
}
