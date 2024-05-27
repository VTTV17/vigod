package api.Seller.products.all_products;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICheckItemModel {
    Logger logger = LogManager.getLogger(APICheckItemModel.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APICheckItemModel (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }

    String checkItemModelPath = "/itemservice/api/item-model-codes/item-model?itemModelIds=%s&status=AVAILABLE";
    public boolean itemModelAvailable(String modelCode) {
        return !api.get(checkItemModelPath.formatted(modelCode), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("status").isEmpty();
    }
}
