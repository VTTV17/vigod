package api.Seller.products.inventory;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    String getInventoryPath = "/itemservice/api/inventory-summary/%s/items?langKey=vi&page=%s&size=100&filter=&sort=priority,asc&search=&branchIds=%s&isAlertStockProducts=false";
    LoginInformation loginInformation;
    LoginDashboardInfo info;
    API api = new API();

    public Inventory(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        info = new Login().getInfo(loginInformation);
    }

    @Data
    public static class InventoryInfo {
        private List<Integer> ids = new ArrayList<>();
        private List<Integer> productIds = new ArrayList<>();
    }

    Response getInventoryResponse(String branchCondition, int page) {
        return api.get(getInventoryPath.formatted(info.getStoreID(), page, branchCondition), info.getAccessToken());
    }

    public InventoryInfo getInventoryInformation() {
        List<Integer> assignedBranches = (info.getAssignedBranchesIds() != null) ? info.getAssignedBranchesIds() : new BranchManagement(loginInformation).getInfo().getBranchID();
        String branchCondition = assignedBranches.stream().map(branchId -> (assignedBranches.indexOf(branchId) == 0 ? "%s" : ",%s").formatted(branchId)).collect(Collectors.joining());

        InventoryInfo info = new InventoryInfo();

        // get data page 0
        Response res = getInventoryResponse(branchCondition, 0);

        // if staff does not have permission, end.
        if (res.getStatusCode() == 403) return info;

        // else get all inventory information
        List<Integer> ids = new ArrayList<>();
        List<Integer> productIds = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(res.getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jPath = getInventoryResponse(branchCondition, pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jPath.getList("id"));
            productIds.addAll(jPath.getList("productId"));
        }

        // set inventory id
        info.setIds(ids);

        // set inventory product id
        info.setProductIds(productIds);

        return info;
    }
}
