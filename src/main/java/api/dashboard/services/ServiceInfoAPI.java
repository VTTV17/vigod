package api.dashboard.services;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;

public class ServiceInfoAPI {
    API api = new API();
    LoginDashboardInfo loginInfo = new Login().getInfo();
    String SERVICE_DETAIL_PATH = "https://api.beecow.info/itemservice/api/beehive-items/%s?langKey=vi";

    public Response getServiceDetail(String serviceId){
        Response response = api.get(SERVICE_DETAIL_PATH.formatted(serviceId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
}
