package api.dashboard.setting;

import io.restassured.response.Response;
import utilities.api.API;

import java.util.List;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class StoreInformation {
    String API_STORE_INFO_PATH = "/storeservice/api/stores/%s";
    String API_STORE_LANGUAGE_PATH = "/storeservice/api/store-language/store/%s/all";
    public static String apiStoreURL;
    public static String apiStoreLogo;
    public static String apiDefaultLanguage;
    public static List<String> apiStoreLanguageList;
    public static List<String> apiSFLangList;
    public void getStoreInformation() {
        // get storeURL
        Response storeInfo = new API().get(API_STORE_INFO_PATH.formatted(apiStoreID), accessToken);
        storeInfo.then().statusCode(200);
        apiStoreURL = storeInfo.jsonPath().getString("url");
        apiStoreLogo = storeInfo.jsonPath().getString("storeImage.fullUrl");
        apiDefaultLanguage = storeInfo.jsonPath().getString("countryCode").equals("VN") ? "vi" : "en";

        // get store language list
        Response storeLanguage = new API().get(API_STORE_LANGUAGE_PATH.formatted(apiStoreID), accessToken);
        storeLanguage.then().statusCode(200);
        List<Boolean> publishLangList = storeLanguage.jsonPath().getList("published");
        apiStoreLanguageList = storeLanguage.jsonPath().getList("langCode");
        apiSFLangList = IntStream.range(0, publishLangList.size()).filter(publishLangList::get).mapToObj(apiStoreLanguageList::get).toList();
    }
}
