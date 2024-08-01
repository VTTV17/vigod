package api.Seller.products.lot_date;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.setting.BranchManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

import static api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import static api.Seller.products.lot_date.APILotDateDetail.LotInfo;

public class APIEditLotDate {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    BranchInfo branchInfo;
    String editLotDatePath = "/itemservice/api/lot-dates";

    public APIEditLotDate(
            LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        branchInfo = new BranchManagement(loginInformation).getInfo();
    }

    @Data
    @AllArgsConstructor
    public static class EditLotPayload {
        private int lotId;
        private String lotName;
        private String lotCode;
        private int id;
        private int storeId;
        private String manufactureDate;
        private String expiryDate;
        private String expiredInValues;
        private boolean notifyWhenExpired;
        private final int totalProduct = 1;
        private String lastModifiedDate;
        private String lastModifiedBy;
        private boolean isDeleted;
        private List<AddAndUpdateItem> addAndUpdateItems;
        private final List<Object> deleteItems = List.of();
    }

    @Data
    @AllArgsConstructor
    public static class AddAndUpdateItem {
        private int itemId;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer modelId;
        private String idCombine;
        private List<BranchStock> branches;
    }

    @Data
    @AllArgsConstructor
    public static class BranchStock {
        private int branchId;
        private int quantity;
        private final String action = "SET";
    }

    private EditLotPayload getPayload(int lotId, int productId, int newStock) {
        // Get product information
        ProductInfoV2 productInfoV2 = new APIProductDetailV2(loginInformation).getInfo(productId);

        // Get lot information
        LotInfo lotInfo = new APILotDateDetail(loginInformation).getInfo(lotId);

        // Get new item info
        List<AddAndUpdateItem> addAndUpdateItems = productInfoV2.isHasModel()
                ? productInfoV2.getModels()
                .stream()
                .map(model -> new AddAndUpdateItem(productId,
                        model.getId(),
                        "%d-%d".formatted(productId, model.getId()),
                        branchInfo.getBranchID()
                                .stream()
                                .map(branchId -> new BranchStock(branchId, newStock))
                                .toList()))
                .toList()
                : List.of(new AddAndUpdateItem(productId,
                null,
                String.valueOf(productId),
                branchInfo.getBranchID()
                        .stream()
                        .map(branchId -> new BranchStock(branchId, newStock))
                        .toList()));

        return new EditLotPayload(lotId,
                lotInfo.getLotName(),
                lotInfo.getLotCode(),
                lotId,
                loginInfo.getStoreID(),
                lotInfo.getManufactureDate(),
                lotInfo.getExpiryDate(),
                lotInfo.getExpiredInValues(),
                lotInfo.isNotifyWhenExpired(),
                lotInfo.getLastModifiedDate(),
                lotInfo.getLastModifiedBy(),
                lotInfo.isDeleted(),
                addAndUpdateItems);
    }

    public void addProductIntoLot(int lotId, int productId, int newStock) {
        api.put(editLotDatePath, loginInfo.getAccessToken(), getPayload(lotId, productId, newStock))
                .then().statusCode(200);
    }
}
