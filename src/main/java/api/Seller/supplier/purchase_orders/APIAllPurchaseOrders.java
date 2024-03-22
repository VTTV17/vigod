package api.Seller.supplier.purchase_orders;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class APIAllPurchaseOrders {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllPurchaseOrders(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    @Data
    public static class AllPurchaseOrdersInformation {
        List<Integer> ids;
        List<String> purchaseIds;
        List<String> supplierNames;
        List<String> branchNames;
        List<String> statues;
        List<Long> amounts;
        List<String> staffCreated;
    }

    String getListPurchaseOrderPath = "/itemservice/api/purchase-orders/store-id/%s?keyword=&page=%s&size=100%s";

    String getBranchQueryParams() {
        return loginInfo.getAssignedBranchesIds().stream().mapToInt(branchId -> branchId)
                .mapToObj("&destinationBranchId=%s"::formatted)
                .collect(Collectors.joining());
    }

    Response getAllPurchaseOrderResponse(int pageIndex) {
        return api.get(getListPurchaseOrderPath.formatted(loginInfo.getStoreID(), pageIndex, getBranchQueryParams()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public AllPurchaseOrdersInformation getAllPurchaseOrdersInformation() {
        // init suggestion model
        AllPurchaseOrdersInformation info = new AllPurchaseOrdersInformation();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<String> purchaseIds = new ArrayList<>();
        List<String> supplierNames = new ArrayList<>();
        List<String> branchNames = new ArrayList<>();
        List<Long> amounts = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<String> staffCreated = new ArrayList<>();


        // get total products
        int totalOfProducts = Integer.parseInt(getAllPurchaseOrderResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllPurchaseOrderResponse(pageIndex).jsonPath();
            ids.addAll(jsonPath.getList("id"));
            purchaseIds.addAll(jsonPath.getList("purchaseId"));
            supplierNames.addAll(jsonPath.getList("supplierName"));
            branchNames.addAll(jsonPath.getList("branchName"));
            amounts.addAll(jsonPath.getList("amount"));
            statues.addAll(jsonPath.getList("status"));
            staffCreated.addAll(jsonPath.getList("staffCreated"));
        }

        // get all purchase order info
        info.setIds(ids);
        info.setPurchaseIds(purchaseIds);
        info.setSupplierNames(supplierNames);
        info.setBranchNames(branchNames);
        info.setAmounts(amounts);
        info.setStatues(statues);
        info.setStaffCreated(staffCreated);

        // return model
        return info;
    }

    public List<Integer> getListPurchaseOrderMatchWithCondition(List<String> assignedBranchNames) {
        AllPurchaseOrdersInformation info = getAllPurchaseOrdersInformation();
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        return ids.stream()
                .filter(id -> assignedBranchNames.contains(branchNames.get(ids.indexOf(id))))
                .toList();
    }

    public List<Integer> getListPurchaseOrderMatchWithCondition(List<String> assignedBranchNames, String staffName) {
        AllPurchaseOrdersInformation info = getAllPurchaseOrdersInformation();
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> staffCreated = info.getStaffCreated();
        return ids.stream()
                .filter(id -> assignedBranchNames.contains(branchNames.get(ids.indexOf(id)))
                        && staffName.equals(staffCreated.get(ids.indexOf(id))))
                .toList();
    }

    public int getOrderPurchaseId(List<String> assignedBranchNames) {
        AllPurchaseOrdersInformation info = getAllPurchaseOrdersInformation();
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> statues = info.getStatues();
        return ids.stream()
                .filter(id -> statues.get(ids.indexOf(id)).equals("ORDER")
                        && assignedBranchNames.contains(branchNames.get(ids.indexOf(id))))
                .findFirst()
                .orElse(0);
    }

    public int getInProgressPurchaseId(List<String> assignedBranchNames) {
        AllPurchaseOrdersInformation info = getAllPurchaseOrdersInformation();
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> statues = info.getStatues();
        return ids.stream()
                .filter(id -> statues.get(ids.indexOf(id)).equals("IN_PROGRESS")
                        && assignedBranchNames.contains(branchNames.get(ids.indexOf(id))))
                .findFirst()
                .orElse(0);
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
}
