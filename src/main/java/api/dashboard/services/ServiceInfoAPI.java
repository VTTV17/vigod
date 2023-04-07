package api.dashboard.services;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;

public class ServiceInfoAPI {
    API api = new API();
    String SERVICE_DETAIL_PATH = "https://api.beecow.info/itemservice/api/beehive-items/%s?langKey=vi";

    public Response getServiceDetail(String serviceId){
        Response response = api.get(SERVICE_DETAIL_PATH.formatted(serviceId), Login.accessToken);
        response.then().statusCode(200);
        return response;
    }
}
