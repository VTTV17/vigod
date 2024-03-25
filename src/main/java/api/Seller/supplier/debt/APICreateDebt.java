package api.Seller.supplier.debt;

import api.Seller.login.Login;
import api.Seller.supplier.supplier.APISupplier;
import api.Seller.supplier.supplier.APISupplier.AllSupplierInformation;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
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

    String getCreateSupplierDebtBody(boolean isPublic, ReceiptType receiptType, BranchInfo... branchInfo) {
        long epoch = Instant.now().toEpochMilli();
        String name = "Debt name %s".formatted(epoch);
        String description = "Debt description %s".formatted(epoch);
        String branchName;
        int branchId;
        if (branchInfo.length != 0) {
            branchId = branchInfo[0].getBranchID().get(0);
            branchName = branchInfo[0].getBranchName().get(0);
        } else {
            branchId = loginInfo.getAssignedBranchesIds().get(0);
            branchName = loginInfo.getAssignedBranchesNames().get(0);
        }
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
                }""".formatted(getSupplier(), loginInfo.getStoreID(), branchId, branchName, isPublic, name, description, receiptType);
    }

    String createSupplierDebtPath = "/itemservice/api/supplier-debts";

    public enum ReceiptType {
        PAYMENT, RECEIPT
    }

    public Response createNewSupplierDebt(boolean isPublic, ReceiptType receiptType, BranchInfo... branchInfo) {
        return api.post(createSupplierDebtPath, loginInfo.getAccessToken(), getCreateSupplierDebtBody(isPublic, receiptType, branchInfo))
                .then().statusCode(201)
                .extract()
                .response();
    }

    public int createAndGetSupplierDebtId(boolean isPublic, ReceiptType receiptType, BranchInfo... branchInfo) {
        return createNewSupplierDebt(isPublic, receiptType, branchInfo).jsonPath().getInt("id");
    }
}
