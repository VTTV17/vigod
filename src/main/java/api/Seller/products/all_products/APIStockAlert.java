package api.Seller.products.all_products;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIStockAlert {
    Logger logger = LogManager.getLogger(APIStockAlert.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIStockAlert (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }
    String getProductAlertNumberPath = "/itemservice/api/stock-alert/storeId/%s/itemId/%s/with-models";

    public List<Integer> getProductAlertNumber(int productId) {
        JsonPath jsonPath = api.get(getProductAlertNumberPath.formatted(loginInfo.getStoreID(), productId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
        return jsonPath.getList("alertNumber");
    }
}
