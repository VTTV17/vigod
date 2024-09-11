package api.Buyer.productdetail;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.Map;

public class APIGetDiscountCampaignInformation {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIGetDiscountCampaignInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String checkDiscountCampaignPath(int storeId, int customerId) {
        return "/orderservices2/api/check-product-branch-wholesale/%d/%d".formatted(storeId, customerId);
    }

    @Data
    @AllArgsConstructor
    private static class CheckPayload {
        private List<Item> lstProduct;

        @Data
        @AllArgsConstructor
        public static class Item {
            private int itemId;
            private int branchId;
        }
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CampaignInformation {
        private int productId;
        private int branchId;
        private List<Wholesale> wholesales;
        private List<Integer> lstWholesaleIds;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Wholesale {
            private String type;
            private long wholesaleValue;
            private int minQuantity;
            private String minimumConditionType;
        }
    }

    CheckPayload getPayload(int itemId, int branchId) {
        return new CheckPayload(List.of(new CheckPayload.Item(itemId, branchId)));
    }

    public CampaignInformation getDiscountCampaignInformation(int itemId, int branchId, int customerId) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [GetDiscountCampaign] itemId: {}, branchId: {}, customerId: {} ", itemId, branchId, customerId);
        List<CampaignInformation> campaignInformationList = api.post(checkDiscountCampaignPath(loginInfo.getStoreID(), customerId),
                        loginInfo.getAccessToken(),
                        getPayload(itemId, branchId),
                        Map.of("platform", "ANDROID"))
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", CampaignInformation.class);

        return campaignInformationList.isEmpty() ? null : campaignInformationList.get(0);
    }
}
