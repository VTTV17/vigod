package api.dashboard.onlineshop;

import api.dashboard.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;

public class Preferences {
    String LISTING_WEBSITE_CONFIG = "/storeservice/api/store-listing-webs/%s";

    API api = new API();
    LoginDashboardInfo loginInfo = new Login().getInfo();
    public boolean isEnabledListingProduct() {
        Response listingWebsiteConfig = api.get(LISTING_WEBSITE_CONFIG.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        listingWebsiteConfig.then().statusCode(200);
        JsonPath listingWebsiteConfigJson = listingWebsiteConfig.jsonPath();
        return listingWebsiteConfigJson.getBoolean("enabledProduct");
    }
}
