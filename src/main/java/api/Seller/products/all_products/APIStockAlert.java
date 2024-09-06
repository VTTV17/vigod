package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.path.json.JsonPath;
import lombok.Data;
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

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class StockAlert {
        private int itemId;
        private Integer modelId;
        private int alertNumber;
    }
    public List<Integer> getProductAlertNumber(int productId) {
        List<StockAlert> stockAlerts = api.get(getProductAlertNumberPath.formatted(loginInfo.getStoreID(), productId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", StockAlert.class);

        return (stockAlerts.size() > 1)
                ? stockAlerts.stream().filter(alert -> alert.getModelId() != null).map(StockAlert::getAlertNumber).toList()
                : stockAlerts.stream().map(StockAlert::getAlertNumber).toList();
    }
}
