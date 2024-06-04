package api.Seller.affiliate.partner;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

public class APIPartnerManagement {
    final static Logger logger = LogManager.getLogger(APIPartnerManagement.class);
    String GET_DROPSHIP_LIST_PATH = "affiliateservice/api/partners/%s?keyword=&searchType=name&partnerType=DROP_SHIP&typeCommission=%s&sort=createdDate,desc&page=0&size=50";
    String GET_RESELLER_LIST_PATH = "affiliateservice/api/partners/%s?keyword=&partnerType=RESELLER&sort=createdDate,desc&page=0&size=50";
    String EXPORT_PARTNER_PATH = "affiliateservice/api/partners/%s/export?langKey=en";
    String GET_PACKAGE_PATH = "affiliateservice/api/partners/package/%s/%s";
    String REJECT_PARTNER_PATH = "affiliateservice/api/partners/%s/reject/%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPartnerManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response callAPIDropshipList(){
        return api.get(GET_DROPSHIP_LIST_PATH.formatted(loginInfo.getStoreID(),""),loginInfo.getAccessToken());
    }
    public Response callAPIResellerList(){
        return api.get(GET_RESELLER_LIST_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
    }
    public List<Integer> getDropshipList(){
        List<Integer> list = callAPIDropshipList().jsonPath().getList("id");
        logger.info("Dropship list: "+list);
        return list;
    }
    public List<Integer> getResellerList(){
        List<Integer> list = callAPIResellerList().jsonPath().getList("id");
        logger.info("Reseller list: "+list);
        return list;
    }
    public void callExportPartner(){
        Response response = api.get(EXPORT_PARTNER_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Export partner successed.");
    }
    public boolean isExceedPartner(boolean isDropship){
        Response response = api.get(GET_PACKAGE_PATH.formatted(isDropship?"DROP_SHIP":"RESELLER",loginInfo.getStoreID()),loginInfo.getAccessToken());
        return response.then().statusCode(200)
                .extract().jsonPath().getBoolean("exceedPartner");
    }
    public void rejectPartner(boolean isDropship, int...exceptId){
        List<Integer> ids;
        if(isDropship) ids = getDropshipList();
        else ids = getResellerList();
        //check exceptId belong to ids list, then remove from list
        for (int i=0; i<exceptId.length; i++){
            if(ids.contains(exceptId[i])) ids.remove(ids.indexOf(exceptId[i]));;
        }
        for (int id:ids) {
            Response response = api.put(REJECT_PARTNER_PATH.formatted(loginInfo.getStoreID(),id),loginInfo.getAccessToken());
            response.then().statusCode(200);
        }
    }
    public List<Integer> getPartnerHasCommissionByRevenue(){
        Response response = api.get(GET_DROPSHIP_LIST_PATH.formatted(loginInfo.getStoreID(),"REVENUE_COMMISSION"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }
    public List<Integer> getPartnerHasCommissionByProduct(){
        Response response = api.get(GET_DROPSHIP_LIST_PATH.formatted(loginInfo.getStoreID(),"SELLING_COMMISSION"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }
    public int getResellerIdByPhone(String phone){
        return callAPIResellerList().jsonPath().getInt("find {it.phoneNumber=='%s'}.id".formatted(phone));
    }
}
