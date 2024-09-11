package api.Buyer.productdetail;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.math.BigDecimal;
import java.util.List;

public class APIGetWholesaleProductInformation {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIGetWholesaleProductInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getWholesaleProductPath(int storeId, int itemId, int customerId, Integer modelId) {
        return "/itemservice/api/item/wholesale-pricing/get-list-store-front/%d/%d/GOSELL?userId=%d&modelId=%s".formatted(storeId, itemId, customerId, modelId != null ? modelId : "");
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WholesaleProductInformation {
        private long id;
        private String itemModelIds;
        private int minQuatity;
        private BigDecimal price;
        private int itemId;
        private String segmentIds;
    }

    public WholesaleProductInformation getWholesaleProductInformation(int itemId, int customerId, Integer modelId) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [GetWholesaleProduct] itemId: {}, customerId: {}, modelId: {}", itemId, customerId, modelId);

        // Get response string
        String responseString = api.get(getWholesaleProductPath(loginInfo.getStoreID(), itemId, customerId, modelId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .asPrettyString();

        try {
            List<WholesaleProductInformation> wholesaleProductInformationList = new ObjectMapper().readValue(responseString, new TypeReference<>() {
            });
            return wholesaleProductInformationList.isEmpty() ? null : wholesaleProductInformationList.get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
