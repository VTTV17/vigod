package api.Seller.sale_channel.onlineshop;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class Preferences {
    String LISTING_WEBSITE_CONFIG = "/storeservice/api/store-listing-webs/%s";

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public Preferences(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public boolean isEnabledListingProduct() {
        Response listingWebsiteConfig = api.get(LISTING_WEBSITE_CONFIG.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        listingWebsiteConfig.then().statusCode(200);
        JsonPath listingWebsiteConfigJson = listingWebsiteConfig.jsonPath();
        return listingWebsiteConfigJson.getBoolean("enabledProduct");
    }
}
