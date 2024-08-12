package api.Seller.orders.pos;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIProductDetailV2;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static api.Seller.orders.pos.APIPOSApplyDiscount.POSApplyDiscountPayload.*;

public class APIPOSApplyDiscount {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIPOSApplyDiscount(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    @AllArgsConstructor
    protected static class POSApplyDiscountPayload {
        private List<Branch> branches;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String userId;
        private final int selfDeliveryFee = 0;
        private final String paymentMethod = "CASH";
        private final String langKey = "vi";

        @Data
        @AllArgsConstructor
        protected static class Branch {
            private int branchId;
            private List<Item> items;
        }

        @Data
        @AllArgsConstructor
        protected static class Item {
            private int itemId;
            private String modelId;
            private int quantity;
        }
    }

    protected final String getPOSApplyDiscountPath = "/orderservice3/api/discount/pos/apply";

    protected POSApplyDiscountPayload getPOSApplyDiscountPayload(List<Integer> productIds, int quantity, int branchId, String customerId) {
        // Get list items in cart
        List<Item> items = new ArrayList<>();
        productIds.stream().map(productId -> new APIProductDetailV2(loginInformation).getInfo(productId))
                .forEach(infoV2 -> infoV2.getVariationModelList()
                        .stream()
                        .map(modelId -> new Item(infoV2.getId(), (modelId != null) ? modelId.toString() : "", quantity))
                        .forEach(items::add));

        // Get POS apply discount payload
        return new POSApplyDiscountPayload(List.of(new Branch(branchId, items)), customerId);
    }

    public void getPOSApplyDiscountInfo(List<Integer> productIds, int quantity, int branchId, String customerId) {
        api.post(getPOSApplyDiscountPath, loginInfo.getAccessToken(), getPOSApplyDiscountPayload(productIds, quantity, branchId, customerId), Map.of("storeId", loginInfo.getStoreID())).prettyPrint();
    }
}
