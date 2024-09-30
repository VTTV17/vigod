package api.Buyer.productdetail;

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import io.restassured.response.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.api.API;
import utilities.model.dashboard.storefront.loginSF;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.HashMap;
import java.util.Map;

public class APIProductDetail {
    final static Logger logger = LogManager.getLogger(APIProductDetail.class);

    API api = new API();
    public static String ADD_TO_CART_PATH = "orderservices2/api/shop-carts/add-to-cart/domain/gosell";
    loginSF loginInfo;
    LoginInformation loginInformation;
    public APIProductDetail (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new LoginSF(loginInformation).getInfo();
    }


    public void callAddToCart(int itemId, int branchId, int quantity){
        String body = """
                {
                    "itemId": %s,
                    "quantity": %s,
                    "branchId": %s,
                    "langKey": "en"
                }
                """.formatted(itemId,quantity,branchId);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("StoreId", String.valueOf(new Login().getInfo(loginInformation).getStoreID()));
        Response response =  api.post(ADD_TO_CART_PATH,loginInfo.getAccessToken(),body,headerMap);
        response.then().statusCode(200);
        logger.info("Call api add to cart.");
    }
}
