package api.Seller.sale_channel.onlineshop;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.onlineshop.ListingStoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIPreferences {
    public static String ENABLE_GUEST_CHECKOUT_PATH = "storeservice/api/stores/%s/checkout?enableGuestCheckout=%s";
    String GET_GUEST_CHECKOUT_STATUS_PATH = "/storeservice/api/stores/%s/checkout";
    String STORE_LISTING_PATH = "/storeservice/api/store-listing-webs/%s";
    final static Logger logger = LogManager.getLogger(APIPreferences.class);
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPreferences (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    /**
     * To set up turn on or turn off checkout with guest
     * @param isEnable true or false
     */
    public void setUpGuestCheckout (boolean isEnable){
        String path = ENABLE_GUEST_CHECKOUT_PATH.formatted(loginInfo.getStoreID(),isEnable);
        Response response = api.put(path,loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Set up guest checkout = "+isEnable);
    }
    public boolean getGuestCheckoutStatus(){
        Response response = api.get(GET_GUEST_CHECKOUT_STATUS_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().get();
    }
    public void enableStoreListingWeb(ListingStoreInfo listingStoreInfo){
        Response response = api.put(STORE_LISTING_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),listingStoreInfo);
        response.then().statusCode(201);
    }
}
