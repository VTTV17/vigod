package api.dashboard.products;

import api.dashboard.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

import static api.dashboard.login.Login.*;

public class PurchaseOrders {
    API api = new API();
    String CREATE_PURCHASE_ORDER_PATH = "/itemservice/api/purchase-orders";
    JsonPath createPurchaseOrderJsonPath() {
        SupplierAPI sup = new SupplierAPI();
        int supplierId = sup.getListSupplierID("").size() == 0 ? sup.createSupplierAndGetSupplierID() : sup.getListSupplierID("").get(0);
        int branchId = new BranchManagement().getListBranchID().get(0);
        int itemId = new CreateProduct().createWithoutVariationProductAndGetProductID(false);
        String inventoryManageType = new ProductInformation().getManageInventoryType(itemId);
        String body = """
                {
                    "note": "",
                    "status": "ORDER",
                    "purchaseId": "",
                    "supplier": {
                        "id": %s
                    },
                    "storeId": "%s",
                    "createdByStaffId": "%s",
                    "branchId": %s,
                    "discount": {
                        "value": 0,
                        "type": "VALUE"
                    },
                    "purchaseCosts": [],
                    "amount": 0,
                    "purchaseOrderItems": [
                        {
                            "itemId": "%s",
                            "modelId": "",
                            "quantity": 1,
                            "importPrice": 0,
                            "codeList": [],
                            "inventoryManageType": "%s"
                        }
                    ]
                }""".formatted(supplierId, apiStoreID, sellerID, branchId, itemId, inventoryManageType);

        Response createPurchaseOrder = api.post(CREATE_PURCHASE_ORDER_PATH, accessToken, body);
        createPurchaseOrder.then().statusCode(201);

        return createPurchaseOrder.jsonPath();
    }

    public String createPurchaseOrderAndGetOrderId() {
        return createPurchaseOrderJsonPath().getString("purchaseId");
    }
}
