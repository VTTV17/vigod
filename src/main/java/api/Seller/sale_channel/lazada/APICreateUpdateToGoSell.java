package api.Seller.sale_channel.lazada;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APICreateUpdateToGoSell {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String CREATE_TO_GOSELL_PATH = "/lazadaservices/api/products/store/%s/sync-to-gosell";

    public APICreateUpdateToGoSell(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public void createLazadaToGoSell(String lazadaShop, List<Long> lazadaProductIdList) {
        String payload = """
                {
                  "syncItemData": [
                    {
                      "lazadaShopId": %s,
                      "lazadaItemIds": %s
                    }
                  ],
                  "createToGoSell": true,
                  "fields": [
                    "ALL"
                  ]
                }
                """.formatted(lazadaShop, lazadaProductIdList);
        Response response = api.post(CREATE_TO_GOSELL_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), payload);
        response.then().statusCode(200);
        new APILazadaAccount(loginInformation).waitFetchProductAPI(20, lazadaShop);
    }

    public void updateToGosell(String lazadaShop, List<Long> lazadaProductIdList) {
        String payLoad = """
                {
                  "syncItemData": [
                    {
                      "lazadaShopId": %s,
                      "lazadaItemIds": %s
                    }
                  ],
                  "createToGoSell": false,
                  "fields": [
                    "FIELD_NAME",
                    "FIELD_PRICE",
                    "FIELD_DESCRIPTION",
                    "FIELD_STOCK",
                    "FIELD_IMAGE"
                  ]
                }
                """.formatted(lazadaShop, lazadaProductIdList);
        Response response = api.post(CREATE_TO_GOSELL_PATH.formatted(loginInfo.getStoreID()), payLoad, loginInfo.getAccessToken());
        response.then().statusCode(200);
        new APILazadaAccount(loginInformation).waitFetchProductAPI(20, lazadaShop);
    }
}
