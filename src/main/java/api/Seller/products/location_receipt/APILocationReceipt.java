package api.Seller.products.location_receipt;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class APILocationReceipt {
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    API api = new API();

    public APILocationReceipt(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class AllLocationReceiptInfo {
        List<Integer> ids = new ArrayList<>();
        List<String> locationReceiptIds = new ArrayList<>();
        List<String> branchNames = new ArrayList<>();
        List<Integer> totalProducts = new ArrayList<>();
        List<Integer> totalQuantity = new ArrayList<>();
        List<Integer> totalLocations = new ArrayList<>();
        List<String> statues = new ArrayList<>();
    }

    String getAllLocationReceiptPath = "/itemservice/api/location-receipt/search/store/%s?page=%s&size=100";

    Response getAllLocationReceiptResponse(int pageIndex) {
        return api.get(getAllLocationReceiptPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public AllLocationReceiptInfo getAllLocationReceiptInfo() {
        // init model
        AllLocationReceiptInfo info = new AllLocationReceiptInfo();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<String> locationReceiptIds = new ArrayList<>();
        List<String> branchNames = new ArrayList<>();
        List<Integer> totalProducts = new ArrayList<>();
        List<Integer> totalQuantity = new ArrayList<>();
        List<Integer> totalLocations = new ArrayList<>();
        List<String> statues = new ArrayList<>();

        // check permission
        if (getAllLocationReceiptResponse(0).statusCode() == 403) return info;

        // get total of pages
        int numOfPages = getAllLocationReceiptResponse(0).jsonPath().getInt("number");
        numOfPages = numOfPages > 0 ? numOfPages : 1;

        // get all location receipt
        IntStream.range(0, numOfPages).mapToObj(pageIndex -> getAllLocationReceiptResponse(pageIndex)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()).forEach(jsonPath -> {
            ids.addAll(jsonPath.getList("content.id"));
            locationReceiptIds.addAll(jsonPath.getList("content.locationReceiptId"));
            branchNames.addAll(jsonPath.getList("content.branchName"));
            totalProducts.addAll(jsonPath.getList("content.totalProduct"));
            totalQuantity.addAll(jsonPath.getList("content.totalQuantity"));
            totalLocations.addAll(jsonPath.getList("content.totalLocation"));
            statues.addAll(jsonPath.getList("content.status"));
        });

        // get all location receipt info
        info.setIds(ids);
        info.setLocationReceiptIds(locationReceiptIds);
        info.setBranchNames(branchNames);
        info.setTotalProducts(totalProducts);
        info.setTotalLocations(totalLocations);
        info.setTotalQuantity(totalQuantity);
        info.setStatues(statues);

        return info;
    }

    public boolean hasLocationReceiptInUnassignedBranches() {
        // get assigned branches
        List<String> assignedBranchNames = loginInfo.getAssignedBranchesNames();

        // get all location receipt
        AllLocationReceiptInfo info = getAllLocationReceiptInfo();

        // get id and name of location receipts
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();

        // get list location receipts that have branch is not in assigned branches.
        return !ids.stream()
                .filter(id -> !assignedBranchNames.contains(branchNames.get(ids.indexOf(id))))
                .toList()
                .isEmpty();
    }

    public List<Integer> getListAddProductToLocation(List<String> assignedBranchNames, AllLocationReceiptInfo... receiptInfo) {
        AllLocationReceiptInfo info = (receiptInfo.length == 0)
                ? getAllLocationReceiptInfo()
                : receiptInfo[0];

        // get receipt info
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> locationReceiptIds = info.getLocationReceiptIds();

        return ids.stream().filter(id -> assignedBranchNames.contains(branchNames.get(ids.indexOf(id)))
                && locationReceiptIds.get(ids.indexOf(id)).contains("ADD")).toList();
    }

    public List<Integer> getListGetProductFromLocation(List<String> assignedBranchNames, AllLocationReceiptInfo... receiptInfo) {
        AllLocationReceiptInfo info = (receiptInfo.length == 0)
                ? getAllLocationReceiptInfo()
                : receiptInfo[0];

        // get receipt info
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> locationReceiptIds = info.getLocationReceiptIds();

        return ids.stream().filter(id -> assignedBranchNames.contains(branchNames.get(ids.indexOf(id)))
                && locationReceiptIds.get(ids.indexOf(id)).contains("GET")).toList();
    }

    public Integer getDraftAddLocationReceiptId(List<String> assignedBranchNames, AllLocationReceiptInfo... receiptInfo) {
        AllLocationReceiptInfo info = (receiptInfo.length == 0)
                ? getAllLocationReceiptInfo()
                : receiptInfo[0];

        // get receipt info
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> locationReceiptIds = info.getLocationReceiptIds();
        List<String> locationStatues = info.getStatues();

        return ids.stream()
                .filter(id -> assignedBranchNames.contains(branchNames.get(ids.indexOf(id)))
                        && locationReceiptIds.get(ids.indexOf(id)).contains("ADD")
                        && locationStatues.get(ids.indexOf(id)).equals("DRAFT"))
                .findFirst()
                .orElse(0);
    }

    public Integer getDraftGetLocationReceiptId(List<String> assignedBranchNames, AllLocationReceiptInfo... receiptInfo) {
        AllLocationReceiptInfo info = (receiptInfo.length == 0)
                ? getAllLocationReceiptInfo()
                : receiptInfo[0];

        // get receipt info
        List<Integer> ids = info.getIds();
        List<String> branchNames = info.getBranchNames();
        List<String> locationReceiptIds = info.getLocationReceiptIds();
        List<String> locationStatues = info.getStatues();

        return ids.stream()
                .filter(id -> assignedBranchNames.contains(branchNames.get(ids.indexOf(id)))
                        && locationReceiptIds.get(ids.indexOf(id)).contains("GET")
                        && locationStatues.get(ids.indexOf(id)).equals("DRAFT"))
                .findFirst()
                .orElse(0);
    }

    String importLotOrLocationPath = "/itemservice/api/items/import-lot-location/%s";

    public void importLotOrLocation() {
        api.importFile(importLotOrLocationPath.formatted(loginInfo.getStoreID()),
                        loginInfo.getAccessToken(),
                        "import_location_receipt_template.xlsx",
                        "branchIds",
                        String.valueOf(loginInfo.getAssignedBranchesIds().get(0)))
                .prettyPrint();
    }
}
