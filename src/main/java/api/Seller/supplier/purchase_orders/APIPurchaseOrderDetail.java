package api.Seller.supplier.purchase_orders;

import api.Seller.login.Login;
import api.Seller.supplier.purchase_orders.APIAllPurchaseOrders.PurchaseOrderStatus;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.regex.Pattern;

public class APIPurchaseOrderDetail {
    Logger logger = LogManager.getLogger(APIPurchaseOrderDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPurchaseOrderDetail (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class PurchaseOrderInformation {
        int branchId;
        List<Integer> purchaseOderItems_itemId;
        List<Integer> purchaseOderItems_modelId;
        List<Integer> purchaseOrderItems_quantity;
        List<Long> purchaseOrderItems_importPrice;
    }

    String purchaseOrderDetailPath = "/itemservice/api/purchase-orders/%s";

    Response getDetailOfPurchaseOrderResponse(int purchaseId) {
        return api.get(purchaseOrderDetailPath.formatted(purchaseId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract().response();
    }

    public PurchaseOrderInformation getPurchaseOrderInformation(int purchaseId) {
        // init model
        PurchaseOrderInformation info = new PurchaseOrderInformation();

        // get jsonPath
        Response response = getDetailOfPurchaseOrderResponse(purchaseId);
        JsonPath jsonPath = response.jsonPath();

        // get purchase order info
        info.setBranchId(jsonPath.getInt("branchId"));
        info.setPurchaseOderItems_itemId(jsonPath.getList("purchaseOrderItems.itemId"));
        info.setPurchaseOderItems_modelId(jsonPath.getList("purchaseOrderItems.modelId"));
        info.setPurchaseOrderItems_quantity(jsonPath.getList("purchaseOrderItems.quantity"));
        info.setPurchaseOrderItems_importPrice(Pattern.compile("importPrice\":\\s*\"*(\\d+\\w+)").matcher(response.asPrettyString())
                .results()
                .map(matchResult -> Long.valueOf(matchResult.group(1)))
                .toList());

        // return model
        return info;
    }
    public List<Integer> getItemIds(int purchaseOrderId) {
        return getDetailOfPurchaseOrderResponse(purchaseOrderId).jsonPath().getList("purchaseOrderItems.itemId");
    }

    public PurchaseOrderStatus getPurchaseOrderStatus(int purchaseOrderId) {
        return PurchaseOrderStatus.valueOf(getDetailOfPurchaseOrderResponse(purchaseOrderId).jsonPath().getString("status"));
    }
}
