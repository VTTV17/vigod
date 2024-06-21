package api.Seller.promotion;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.enums.DiscountStatus;
import utilities.enums.DiscountType;
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
    public Response getPromotionListRes(DiscountType discountType, DiscountStatus status){
        String typeParam;
        switch (discountType){
            case PRODUCT_DISCOUNT_CODE -> typeParam = "COUPON";
            case SERVICE_DISCOUNT_CODE -> typeParam = "COUPON_SERVICE";
            case PRODUCT_DISCOUNT_CAMPAIGN -> typeParam = "WHOLE_SALE";
            case SERVICE_DISCOUNT_CAMPAIGN -> typeParam = "WHOLE_SALE_SERVICE";
            default -> typeParam = "";
        }
        Response response = api.get(DISCOUNT_CAMPAIGN_LIST_PATH.formatted(loginInfo.getStoreID(),typeParam,status),loginInfo.getAccessToken());
        return response;
    }
    public int getDiscountId(DiscountType discountType, DiscountStatus status){
        List<Integer> ids= getDiscountIdList(discountType, status);
        return ids.isEmpty() ? -1 : ids.get(0);
    }
    public List<Integer> getDiscountIdList(DiscountType discountType, DiscountStatus status){
    	Response response = getPromotionListRes(discountType,status);
    	return response.then().statusCode(200).extract().jsonPath().getList("id");
    }
    public void endEarlyInprogressDiscountCampaign() {
        // get list in-progress discount campaign
        List<Integer> inProgressList = getPromotionListRes(DiscountType.ALL,DiscountStatus.IN_PROGRESS).jsonPath().getList("id");

        // end in-progress service campaign
        inProgressList.forEach(campaignID -> api.delete(DELETE_DISCOUNT_CAMPAIGN_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200));
    }
}
