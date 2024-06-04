package api.Seller.sale_channel.shopee;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APICanLinkShopee {
    Logger logger = LogManager.getLogger(APICanLinkShopee.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APICanLinkShopee(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class CanLinkShopeeProductInformation {
        List<Integer> ids;
        List<String> names;
        List<Boolean> hasModels;
        List<String> inventoryManageTypes;
    }

    String getCanLinkShopeeProductPath = "/itemservice/api/items/can-link-shopee?storeId=%s&name=&variationNum=%s&page=0&size=10";

   public Response getCanLinkShopeeProductResponse(int variationNum) {
        return api.get(getCanLinkShopeeProductPath.formatted(loginInfo.getStoreID(), variationNum), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

//    public CanLinkShopeeProductInformation getCanLinkShopeeProductInformation() {
//
//    }
}
