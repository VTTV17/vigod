package api.Seller.supplier.debt;

import api.Seller.login.Login;
import api.Seller.supplier.supplier.APISupplier;
import api.Seller.supplier.supplier.APISupplier.AllSupplierInformation;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;

public class APICreateDebt {
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    API api = new API();

    public APICreateDebt(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getSupplier() {
        APISupplier apiSupplier = new APISupplier(loginInformation);
        AllSupplierInformation supplierInformation = apiSupplier.getAllSupplierInformation();
        if (supplierInformation.getIds().isEmpty()) {
            apiSupplier.createSupplier();
            supplierInformation = apiSupplier.getAllSupplierInformation();
        }
        return """
                {
                	"name": "%s",
                	"code": "%s",
                	"id": %s
                }""".formatted(supplierInformation.getNames().get(0),
                supplierInformation.getCodes().get(0),
                supplierInformation.getIds().get(0));
    }

    String getCreateSupplierDebtBody(boolean isPublic, ReceiptType receiptType) {
        long epoch = Instant.now().toEpochMilli();
        String name = "Debt name %s".formatted(epoch);
        String description = "Debt description %s".formatted(epoch);
        return """
                {
                    "supplier": %s,
                    "storeId": "%s",
                    "branchId": %s,
                    "branchName": "%s",
                    "staffName": "",
                    "published": %s,
                    "name": "%s",
                    "description": "%s",
                    "subTotal": 10000,
                    "tax": 0,
                    "taxId": 0,
                    "discount": {
                        "type": "VALUE",
                        "value": 0
                    },
                    "supplierDebtCosts": [],
                    "originalAmount": 10000,
                    "receiptType": "%s",
                    "supplierDebtReceipts": []
                }""".formatted(getSupplier(),
                loginInfo.getStoreID(),
                loginInfo.getAssignedBranchesIds().get(0),
                loginInfo.getAssignedBranchesNames().get(0),
                isPublic,
                name,
                description,
                receiptType);
    }

    String createSupplierDebtPath = "/itemservice/api/supplier-debts";

    public enum ReceiptType {
        PAYMENT, RECEIPT
    }

    public Response createNewSupplierDebt(boolean isPublic, ReceiptType receiptType) {
        return api.post(createSupplierDebtPath, loginInfo.getAccessToken(), getCreateSupplierDebtBody(isPublic, receiptType))
                .then().statusCode(201)
                .extract()
                .response();
    }
}
