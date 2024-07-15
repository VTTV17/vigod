package api.Seller.sale_channel.onlineshop;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIDomain {
    String GET_NEW_DOMAIN_PATH = "/storeservice/api/store-urls/stores/%s/urlTypes/STOREFRONT";
    String DELETE_NEW_DOMAIN_PATH = "/storeservice/api/store-urls/store-ids/%s/url-ids/%s";
    String CHECK_SUB_DOMAIN_EXIST = "/storeservice/api/store/check-url?isExist=false&url=%s";
    String UPDATE_SUB_DOMAIN = "/storeservice/api/stores/sub-domains";
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIDomain.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    public APIDomain(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public int getNewDomainId(){
        Response response = api.get(GET_NEW_DOMAIN_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        int id=0;
        if(response.statusCode()!= 404) {
            id = response.jsonPath().getInt("id");
        }
        return id;
    }
    public void deleteNewDomain(int id){
        Response response = api.delete(DELETE_NEW_DOMAIN_PATH.formatted(loginInfo.getStoreID(),id),loginInfo.getAccessToken());
        response.then().statusCode(200);
    }
    public boolean checkSubDomainExist(String subdomainName){
        Response response = api.get(CHECK_SUB_DOMAIN_EXIST.formatted(subdomainName),loginInfo.getAccessToken());
        String message = response.jsonPath().getString("message");
        if(message.equals("DOMAIN_NOT_EXISTED")) return false;
        return true;
    }
    public void updateSubDomain(String newSubDomain){
        String body = """
                {"subDomain":"%s"}
                """.formatted(newSubDomain);
        Response response = api.put(UPDATE_SUB_DOMAIN,loginInfo.getAccessToken(),body);
        response.then().statusCode(200);
    }
}
