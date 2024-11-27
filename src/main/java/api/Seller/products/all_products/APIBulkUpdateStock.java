package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIBulkUpdateStock {
    private final LoginDashboardInfo loginInfo;
    private final LoginInformation credential;

    public APIBulkUpdateStock(LoginInformation credential) {
        this.credential = credential;
        this.loginInfo = new Login().getInfo(credential);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockUpdateRequest {
        private List<StockAction> lstData;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class StockAction {
            private int itemId;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Integer modelId;
            private String action;
            private int stock;
            private int branchId;
        }
    }

    private List<StockUpdateRequest.StockAction> getListStockActions(int productId, int branchId, int newStock) {
        var productInfo = new APIGetProductDetail(credential).getProductInformation(productId);
        if (!productInfo.isHasModel()) {
            return List.of(new StockUpdateRequest.StockAction(productInfo.getId(), null, "CHANGE", newStock, branchId));
        }

        return productInfo.getModels().stream().map(model -> new StockUpdateRequest.StockAction(productInfo.getId(), model.getId(), "CHANGE", newStock, branchId))
                .toList();
    }

    private StockUpdateRequest generateChangeRequest(int productId, int branchId, int newStock) {
        return new StockUpdateRequest(getListStockActions(productId, branchId, newStock));
    }

    public void bulkUpdateStock(int productId, int branchId, int newStock) {
        new API().put("/itemservice/api/items/update-multiple-item-inventory/%d".formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), generateChangeRequest(productId, branchId, newStock))
                .then().statusCode(200);
    }
}
