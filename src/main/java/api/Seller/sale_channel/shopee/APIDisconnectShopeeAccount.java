package api.Seller.sale_channel.shopee;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIDisconnectShopeeAccount {
    Logger logger = LogManager.getLogger(APIDisconnectShopeeAccount.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIDisconnectShopeeAccount(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String disconnectShopeeAccountPath = "/shopeeservices/api/shops/disconnect";

    public void disconnectShopeeAccount() {
        APIShopeeManagement.ShopeeManagementInformation info = new APIShopeeManagement(loginInformation).getShopeeManagementInfo();

        info.getIds().parallelStream().forEach(id -> {
            String body = """
                    {
                        "id": %s,
                        "bcStoreId": "%s",
                        "shopeeId": %s,
                        "branchId": %s,
                        "authCode": null,
                        "reAuth": false
                    }""".formatted(id, loginInfo.getStoreID(), info.getShopIds().get(info.getIds().indexOf(id)), info.getBranchIds().get(info.getIds().indexOf(id)));

            api.put(disconnectShopeeAccountPath, loginInfo.getAccessToken(), body)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
        });

        // clear cache
        APIShopeeManagement.getShopeeAccountCache().invalidateAll();
    }
}
