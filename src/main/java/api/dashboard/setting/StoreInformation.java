package api.dashboard.setting;

import io.restassured.response.Response;
import utilities.api.API;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;

public class StoreInformation {
    String API_STORE_INFO_PATH = "/storeservice/api/stores/%s";
    public static String storeURL;
    public void getStoreInformation() {
        // get storeURL
        Response storeInfo = new API().get(API_STORE_INFO_PATH.formatted(storeID), accessToken);
        storeInfo.then().statusCode(200);
        storeURL = storeInfo.jsonPath().getString("url");
    }
}
