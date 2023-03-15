package api.dashboard.onlineshop;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

public class APIPreferences {
    public static String ENABLE_GUEST_CHECKOUT_PATH = "storeservice/api/stores/%s/checkout?enableGuestCheckout=%s";
    final static Logger logger = LogManager.getLogger(APIPreferences.class);
    API api = new API();
    public void setUpGuestCheckout (String token, int storeId,boolean isEnable){
        String path = ENABLE_GUEST_CHECKOUT_PATH.formatted(storeId,isEnable);
        Response response = api.put(path,token);
        response.then().statusCode(200);
        logger.info("Set up guest checkout = "+isEnable);
    }
}
