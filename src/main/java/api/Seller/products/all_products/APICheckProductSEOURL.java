package api.Seller.products.all_products;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICheckProductSEOURL {
    Logger logger = LogManager.getLogger(APICheckProductSEOURL.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APICheckProductSEOURL(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String checkSEOURLPath = "/ssrstorefront/api/seo-links/validate/store/%s?url=%s&langKey=%s&type=BUSINESS_PRODUCT&data=%s";

    public boolean isAvailableSEOURL(String url, String langKey, int productId) {
        return api.get(checkSEOURLPath.formatted(loginInfo.getStoreID(), url, langKey, productId), loginInfo.getAccessToken())
                       .getStatusCode() == 400;
    }
}
