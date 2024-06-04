package api.Seller.setting;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.stream.IntStream;

public class StoreInformation {
    String API_STORE_INFO_PATH = "/storeservice/api/stores/%s";
    String API_STORE_LANGUAGE_PATH = "/storeservice/api/store-language/store/%s/all";
    String getLangNamePath = "/catalogservices/api/languages";
    // get login dashboard information
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    private final static Cache<String, StoreInfo> storeInfoCache = CacheBuilder.newBuilder().build();

    public StoreInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public StoreInfo getInfo() {
        StoreInfo storeInfo = storeInfoCache.getIfPresent(loginInfo.getStaffPermissionToken());

        if (storeInfo == null) {
            // init store info model
            storeInfo = new StoreInfo();
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
            JsonPath jsonPath = new API().get(API_STORE_LANGUAGE_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                    .then()
                    .statusCode(200)
                    .extract().jsonPath();
            List<Boolean> publishLangList = jsonPath.getList("published");

            // set all store languages code.
            List<String> langCodeList = jsonPath.getList("langCode");
            storeInfo.setStoreLanguageList(langCodeList);

            List<String> langNameList = jsonPath.getList("langName");
            storeInfo.setStoreLanguageName(langNameList);

            // set published language
            storeInfo.setSFLangList(IntStream.range(0, publishLangList.size()).filter(publishLangList::get).mapToObj(storeInfo.getStoreLanguageList()::get).toList());

            // save cache
            storeInfoCache.put(loginInfo.getAccessToken(), storeInfo);
        }

        // return store information
        return storeInfo;
    }
}
