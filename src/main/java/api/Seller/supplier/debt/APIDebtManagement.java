package api.Seller.supplier.debt;

import api.Seller.login.Login;
import api.Seller.supplier.debt.APICreateDebt.ReceiptType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class APIDebtManagement {
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    API api = new API();

    public APIDebtManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    @Data
    public static class AllSupplierDebtInformation {
        List<Integer> ids = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Boolean> published = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<String> receiptType = new ArrayList<>();
    }

    enum DebtStatus {
        RECEIVABLE, OPEN, PAYABLE, DEBT_FREE
    }

    String getAllSupplierDebtPath = "/itemservice/api/supplier-debts/store/%s?branchId=&page=%s&size=100";

    Response getAllSupplierDebtResponse(int pageIndex) {
        return api.get(getAllSupplierDebtPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public AllSupplierDebtInformation getAllDebtInformation() {
        // init model
        AllSupplierDebtInformation info = new AllSupplierDebtInformation();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<Integer> supplierIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Boolean> published = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<String> receiptType = new ArrayList<>();

        // get total products
        Response response = getAllSupplierDebtResponse(0);

        if (response.getStatusCode() == 403) return info;
        int totalOfDebts = Integer.parseInt(response
                .then()
                .statusCode(200)
                .extract()
                .response()
                .getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfDebts / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllSupplierDebtResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jsonPath.getList("supplierDebts.id"));
            supplierIds.addAll(jsonPath.getList("supplierDebts.supplier.id"));
            branchIds.addAll(jsonPath.getList("supplierDebts.branchId"));
            published.addAll(jsonPath.getList("supplierDebts.published"));
            receiptType.addAll(jsonPath.getList("supplierDebts.receiptType"));
            statues.addAll(jsonPath.getList("supplierDebts.status"));
        }

        // get all supplier debt
        info.setIds(ids);
        info.setSupplierIds(supplierIds);
        info.setBranchIds(branchIds);
        info.setPublished(published);
        info.setStatues(statues);
        info.setReceiptType(receiptType);

        // return model
        return info;
    }

    public List<Integer> getAllDebtInformation(List<Integer> assignedBranchIds) {
        // get all debt info
        AllSupplierDebtInformation supplierDebtInformation = getAllDebtInformation();

        // return list debt
        return IntStream.range(0, supplierDebtInformation.getIds().size())
                .filter(index -> assignedBranchIds.contains(supplierDebtInformation.getBranchIds().get(index)))
                .mapToObj(index -> supplierDebtInformation.getIds().get(index))
                .toList();
    }

    public int getIdOfOpenDebt(ReceiptType receiptType) {
        AllSupplierDebtInformation info = getAllDebtInformation();
        List<Integer> ids = info.getIds();
        List<String> statues = info.getStatues();
        List<String> receiptTypes = info.getReceiptType();
        return ids.stream()
                .mapToInt(id -> id)
                .filter(id -> statues.get(ids.indexOf(id)).equals(DebtStatus.OPEN.toString())
                        && receiptTypes.get(ids.indexOf(id)).equals(receiptType.toString()))
                .findFirst()
                .orElse(0);
    }

    public int getIdOfPayableDebt() {
        AllSupplierDebtInformation info = getAllDebtInformation();
        List<Integer> ids = info.getIds();
        List<String> statues = info.getStatues();
        return ids.stream()
                .mapToInt(id -> id)
                .filter(id -> statues.get(ids.indexOf(id)).equals(DebtStatus.PAYABLE.toString()))
                .findFirst()
                .orElse(0);
    }

    public int getIdOfReceivableDebt() {
        AllSupplierDebtInformation info = getAllDebtInformation();
        List<Integer> ids = info.getIds();
        List<String> statues = info.getStatues();
        return ids.stream()
                .mapToInt(id -> id)
                .filter(id -> statues.get(ids.indexOf(id)).equals(DebtStatus.RECEIVABLE.toString()))
                .findFirst()
                .orElse(0);
    }
}
