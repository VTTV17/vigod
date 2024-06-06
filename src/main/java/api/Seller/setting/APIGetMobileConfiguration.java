package api.Seller.setting;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIGetMobileConfiguration {
    Logger logger = LogManager.getLogger(APIGetMobileConfiguration.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIGetMobileConfiguration (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }

    String path = "/beehiveservices/api/mobile-configs/shop/validation?shopId=%s";

    Response getMobileConfigResponse() {
        return api.get(path.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public String getAndroidAppPackage() {
        return getMobileConfigResponse().jsonPath().getString("bundleAndroid");
    }
}
