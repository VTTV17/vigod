package api.dashboard.services;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class ServiceInfoAPI {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public ServiceInfoAPI (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String SERVICE_DETAIL_PATH = "https://api.beecow.info/itemservice/api/beehive-items/%s?langKey=vi";
    String DELETE_SERVICE_PATH = "/itemservice/api/items/%s";
    public Response getServiceDetail(int serviceId){
        Response response = api.get(SERVICE_DETAIL_PATH.formatted(serviceId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public void deleteService(int serviceId){
        String path = DELETE_SERVICE_PATH.formatted(serviceId);
        api.delete(path,loginInfo.getAccessToken()).then().statusCode(200);
    }
}
