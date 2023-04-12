package api.dashboard.setting;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;

import java.util.List;
import java.util.stream.IntStream;

public class StoreInformation {
    String API_STORE_INFO_PATH = "/storeservice/api/stores/%s";
    String API_STORE_LANGUAGE_PATH = "/storeservice/api/store-language/store/%s/all";
    public StoreInfo getInfo() {
        // get login dashboard information
        LoginDashboardInfo loginInfo = new Login().getInfo();

        // init store info model
        StoreInfo storeInfo = new StoreInfo();
        // get storeURL
        Response storeRes = new API().get(API_STORE_INFO_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        storeRes.then().statusCode(200);

        // set store url
        storeInfo.setStoreURL(storeRes.jsonPath().getString("url"));

        // set store logo
        storeInfo.setStoreLogo(storeRes.jsonPath().getString("storeImage.fullUrl"));

        // set store default language
        storeInfo.setDefaultLanguage(storeRes.jsonPath().getString("countryCode").equals("VN") ? "vi" : "en");

        // get store language list
        Response languageRes = new API().get(API_STORE_LANGUAGE_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        languageRes.then().statusCode(200);
        List<Boolean> publishLangList = languageRes.jsonPath().getList("published");

        // set all store languages
        storeInfo.setStoreLanguageList(languageRes.jsonPath().getList("langCode"));

        // set published language
        storeInfo.setSFLangList(IntStream.range(0, publishLangList.size()).filter(publishLangList::get).mapToObj(storeInfo.getStoreLanguageList()::get).toList());

        // return store information
        return storeInfo;
    }
}
