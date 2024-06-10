package api.Seller.affiliate.partnerinventory;

import api.Seller.login.Login;
import api.Seller.products.all_products.APISuggestionProduct;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.affiliate.PartnerTransferInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIPartnerCreateTransfer {
    String CREATE_TRANSFER_PATH = "/itemservice/api/affiliate-transfers/create";
    API api = new API();
    LoginDashboardInfo sellerInfo, resellerInfo;
    LoginInformation sellerLoginInfo, resellerLoginInfo;
    public APIPartnerCreateTransfer(LoginInformation sellerLoginInfo, LoginInformation resellerLoginInfo) {
        this.sellerLoginInfo = sellerLoginInfo;
        this.resellerLoginInfo = resellerLoginInfo;
        sellerInfo = new Login().getInfo(sellerLoginInfo);
        resellerInfo = new Login().getInfo(resellerLoginInfo);
    }
    public String getItemTransferBody(int branchId){
        int productId  = new APISuggestionProduct(sellerLoginInfo).getListSuggestionProduct(branchId).getItemIds().get(0);
        String items = """
                "itemTransfers": [
                        {
                            "itemId": "%s",
                            "modelId": "",
                            "quantity": 1,
                            "codeList": [],
                            "inventoryManageType": "PRODUCT"
                        }
                    ]
                """.formatted(productId);
        return items;
    }
    public PartnerTransferInfo createPartnerTransfer(int originBranchId){
        String body = """
                {
                    "originBranchId": %s,
                    "destinationBranchId": %s,
                    "status": "READY_FOR_TRANSPORT",
                    "note": "",
                    "storeId": "%s",
                    "createdByStaffId": "",
                    %s,
                    "resellerStoreId": %s,
                    "refresh": false,
                    "ignoreErrorWarning": true,
                    "itemTransferLotDates": []
                }
                """.formatted(originBranchId,resellerInfo.getAssignedBranchesIds().get(0),
                sellerInfo.getStoreID(),getItemTransferBody(originBranchId),resellerInfo.getStoreID());
        Response response = api.post(CREATE_TRANSFER_PATH,sellerInfo.getAccessToken(),body);
        response.then().statusCode(200);
        return response.as(PartnerTransferInfo.class);
    }
}
