package api.dashboard.setting;

import io.restassured.response.Response;
import utilities.api.API;

import java.security.SecureRandom;
import java.util.List;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class StoreInformation {
    String API_STORE_INFO_PATH = "/storeservice/api/stores/%s";
    String API_STORE_LANGUAGE_PATH = "/storeservice/api/store-language/store/%s/all";
    public static String apiStoreURL;
    public static String apiStoreLogo;
    public static String apiDefaultLanguage;
    public static List<String> apiStoreLanguageList;
    public void getStoreInformation() {
        // get storeURL
        Response storeInfo = new API().get(API_STORE_INFO_PATH.formatted(apiStoreID), accessToken);
        storeInfo.then().statusCode(200);
        apiStoreURL = storeInfo.jsonPath().getString("url");
        apiStoreLogo = storeInfo.jsonPath().getString("storeImage.fullUrl");
        apiDefaultLanguage = storeInfo.jsonPath().getString("countryCode").equals("VN") ? "VIE" : "ENG";

        // get store language list
        Response storeLanguage = new API().get(API_STORE_LANGUAGE_PATH.formatted(apiStoreID), accessToken);
        storeLanguage.then().statusCode(200);
        apiStoreLanguageList = storeLanguage.jsonPath().getList("langCode");
    }
}
