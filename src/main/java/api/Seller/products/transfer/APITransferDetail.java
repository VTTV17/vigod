package api.Seller.products.transfer;

import api.Seller.login.Login;
import api.Seller.products.transfer.TransferManagement.TransferStatus;
import api.Seller.sale_channel.lazada.APILazadaProducts;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.SQLGetInventoryEvent;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

public class APITransferDetail {
    String getTransferDetailPath = "/itemservice/api/transfers/detail/%s/%s";
    static Logger logger = LogManager.getLogger(APITransferDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APITransferDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class Image{
        public String imageUUID;
        public String urlPrefix;
        public String extension;
        public String fullUrl;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Item{
        public int id;
        public int itemId;
        public int modelId;
        public int quantity;
        public int recordedInventory;
        public String inventoryManageType;
        public ArrayList<Object> codeList;
        public boolean hasLot;
        public boolean hasLocation;
        public int weight;
        public int width;
        public int height;
        public int length;
        public String itemName;
        public String modelLabel;
        public String modelValue;
        public int remaining;
        public String sku;
        public Image image;
        public String itemModelId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TransferDetailInfo {
        public int id;
        public int originBranchId;
        public int destinationBranchId;
        public String status;
        public String note;
        public int storeId;
        public int createdByStaffId;
        public String staffName;
        public List<Item> items;
        public String createdDate;
        public boolean changedPriceOrRate;
        public boolean hasLotLocation;
        public String shippingPlan;
        public String transferType;
    }

    Response getTransferDetailResponse(int transferId) {
        return api.get(getTransferDetailPath.formatted(loginInfo.getStoreID(), transferId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public List<Integer> getItemIds(int transferId) {
        return getTransferDetailResponse(transferId).jsonPath().getList("items.itemId");
    }

    public TransferStatus getTransferStatus(int transferId) {
        return TransferStatus.valueOf(getTransferDetailResponse(transferId).jsonPath().getString("status"));
    }
    public TransferDetailInfo getTransferDetail(int transferId){
        return getTransferDetailResponse(transferId).as(TransferDetailInfo.class);
    }
    public List<SQLGetInventoryEvent.InventoryEvent> getEventFromTransferDetail(int transferId){
        TransferDetailInfo  transferDetailInfo = getTransferDetail(transferId);
        List<Item> transferItem = transferDetailInfo.getItems();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventList = new ArrayList<>();
        List<Long> productHasEvent = new APILazadaProducts(loginInformation).getProductListHasLinkedSyncedLazada();
        System.out.println("productHasEvent: "+productHasEvent);
        for (Item item : transferItem){
            if(!productHasEvent.contains(Long.parseLong(String.valueOf(item.getItemId())))) continue;
            SQLGetInventoryEvent.InventoryEvent event = new SQLGetInventoryEvent.InventoryEvent();
            event.setBranch_id(String.valueOf(transferDetailInfo.getOriginBranchId()));
            event.setItem_id(String.valueOf(item.getItemId()));
            if(item.getModelId()!=0) event.setModel_id(item.getItemModelId());
            event.setAction("GS_CHANGE_PRODUCT_STOCK");
            inventoryEventList.add(event);
        };
        return inventoryEventList;
    }
}
