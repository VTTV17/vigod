package api.Seller.services;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class ServiceCollectionAPI {
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();
    String GET_SERVICE_COLLECTION_LIST_PATH = "itemservice/api/collections/list/%s?page=0&size=100&itemType=SERVICE&search=";
    public ServiceCollectionAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public int getNewestCollectionID(){
        Response listCollectionRes = api.get(GET_SERVICE_COLLECTION_LIST_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        listCollectionRes.then().statusCode(200);
        return  (int) listCollectionRes.jsonPath().getList("lstCollection.id").get(0);
    }
}
