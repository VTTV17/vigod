package api.storefront.productdetail;

import api.dashboard.login.Login;
import api.storefront.login.LoginSF;
import io.restassured.response.Response;
import pages.buyerapp.buyergeneral.BuyerGeneral;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.storefront.loginSF;

public class APIProductDetail {
    final static Logger logger = LogManager.getLogger(APIProductDetail.class);

    API api = new API();
    public static String ADD_TO_CART_PATH = "orderservices2/api/shop-carts/add-to-cart/domain/gosell";
    loginSF loginInfo = new LoginSF().getInfo();

    public void callAddToCart(int itemId, int branchId, int quantity){
        String body = """
                {
                    "itemId": %s,
                    "quantity": %s,
                    "branchId": %s,
                    "langKey": "en"
                }
                """.formatted(itemId,quantity,branchId);
        Response response =  api.post(ADD_TO_CART_PATH,loginInfo.getAccessToken(),body);
        response.then().statusCode(200);
        logger.info("Call api add to cart.");
    }
}
