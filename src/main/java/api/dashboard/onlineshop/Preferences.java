package api.dashboard.onlineshop;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class Preferences {
    String LISTING_WEBSITE_CONFIG = "/storeservice/api/store-listing-webs/%s";
    public static boolean enabledProduct;
    API api = new API();
    public void getListingWebsiteConfig() {
        Response listingWebsiteConfig = api.get(LISTING_WEBSITE_CONFIG.formatted(apiStoreID), accessToken);
        listingWebsiteConfig.then().statusCode(200);
        JsonPath listingWebsiteConfigJson = listingWebsiteConfig.jsonPath();
        enabledProduct = listingWebsiteConfigJson.getBoolean("enabledProduct");
    }
}
