package api.Buyer.productdetail;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIGetFlashSaleInformation {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIGetFlashSaleInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getFlashSalePath(int itemId, Integer modelId) {
        return "/itemservice/api/campaigns/product/%d?modelId=%s".formatted(itemId, modelId != null ? modelId : "");
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlashSaleInformation {
        private String status;
        private List<Item> items;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String itemModelId;
            private long newPrice;
            private int saleStock;
            private int purchaseLimitStock;
            private int soldStock;
            private int transactionStock;
            private int litemId;
        }
    }

    public FlashSaleInformation getFlashSaleInformation(int itemId, Integer modelId) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [GetFlashSale] itemId: {}, modelId: {} ", itemId, modelId);
        Response response = api.get(getFlashSalePath(itemId, modelId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.asPrettyString().isEmpty() ? null : response.as(FlashSaleInformation.class);
    }
}
