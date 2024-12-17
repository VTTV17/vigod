package api.Seller.products.transfer;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.SneakyThrows;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class APIEditTransfer {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    APITransferDetail.TransferDetailInfo transferDetail;
    private String UPDATE_TRANSFER_PATH = "/itemservice/api/transfers/update";
    public APIEditTransfer(LoginInformation loginInformation, int transferId) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        transferDetail = new APITransferDetail(loginInformation).getTransferDetail(transferId);
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class payLoadUpdate{
        public int id;
        public int originBranchId;
        public int destinationBranchId;
        public String status;
        public String note;
        public int storeId;
        public int createdByStaffId;
        public String staffName;
        public List<APITransferDetail.Item> itemTransfers;
        public String createdDate;
        public boolean changedPriceOrRate;
        public boolean hasLotLocation;
        public String shippingPlan;
        public String transferType;
        public String locale;
        public boolean ignoreErrorWarning;
    }
    @SneakyThrows
    public String removeAProductInTransfer(){
        List<APITransferDetail.Item> itemList = transferDetail.getItems();
        ObjectMapper objectMapper1 = new ObjectMapper();
        payLoadUpdate payLoad = objectMapper1.convertValue(transferDetail, payLoadUpdate.class);
        String itemModelId;
        if(itemList.size()>1){
            itemModelId = itemList.getFirst().getItemModelId();
            itemList.remove(0);
            payLoad.setItemTransfers(itemList);
            api.put(UPDATE_TRANSFER_PATH, loginInfo.getAccessToken(),payLoad).then().statusCode(200);
        }else throw new Exception("Can't remove product because there is one product in this transfer.");
        return itemModelId;
    }
}
