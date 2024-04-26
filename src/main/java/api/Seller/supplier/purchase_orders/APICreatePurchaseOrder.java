package api.Seller.supplier.purchase_orders;

import api.Seller.login.Login;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.setting.BranchManagement;
import api.Seller.supplier.supplier.APISupplier;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICreatePurchaseOrder {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APICreatePurchaseOrder(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String CREATE_PURCHASE_ORDER_PATH = "/itemservice/api/purchase-orders";

    JsonPath createPurchaseOrderJsonPath() {
        APISupplier sup = new APISupplier(loginInformation);
        int supplierId = sup.getListSupplierID("").isEmpty() ? sup.createSupplierAndGetSupplierID() : sup.getListSupplierID("").get(0);
        int branchId = new BranchManagement(loginInformation).getInfo() // get branch info
                .getBranchID() // get list branch ID
                .get(0); // get first branch in list
        int itemId = new CreateProduct(loginInformation).createWithoutVariationProduct(false, 1).getProductID();
        String inventoryManageType = new APIProductDetail(loginInformation).getInfo(itemId).getManageInventoryByIMEI() ? "IMEI_SERIAL_NUMBER" : "PRODUCT";
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
                }""".formatted(supplierId, loginInfo.getStoreID(), new Login().getInfo(loginInformation).getUserId(), branchId, itemId, inventoryManageType);

        Response createPurchaseOrder = api.post(CREATE_PURCHASE_ORDER_PATH, loginInfo.getAccessToken(), body);
        createPurchaseOrder.then().statusCode(201);

        return createPurchaseOrder.jsonPath();
    }

    public String createPurchaseOrderAndGetOrderId() {
        return createPurchaseOrderJsonPath().getString("purchaseId");
    }
}
