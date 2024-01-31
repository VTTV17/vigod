package api.Seller.products;

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
    String getInventoryPath = "/itemservice/api/inventory-summary/%s/items?langKey=vi&page=%s&size=50&filter=&sort=priority,asc&search=&branchIds=%s&isAlertStockProducts=false";
    LoginInformation loginInformation;
    LoginDashboardInfo info;
    API api = new API();

    public Inventory(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        info = new Login().getInfo(loginInformation);
    }

    @Data
    public static class InventoryInfo {
        private List<Integer> ids;
        private List<Integer> productIds;
    }

    Response getInventoryResponse(String branchCondition, int page) {
        return api.get(getInventoryPath.formatted(info.getStoreID(), page, branchCondition), info.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public InventoryInfo getInventoryInformation() {
        List<Integer> assignedBranches = (info.getAssignedBranches() != null) ? info.getAssignedBranches() : new BranchManagement(loginInformation).getInfo().getBranchID();
        String branchCondition = assignedBranches.stream().map(branchId -> (assignedBranches.indexOf(branchId) == 0 ? "%s" : ",%s").formatted(branchId)).collect(Collectors.joining());

        InventoryInfo info = new InventoryInfo();

        // get data page 0
        try {
            Response res = getInventoryResponse(branchCondition, 0);
            JsonPath jPath = res.jsonPath();
            List<Integer> ids = new ArrayList<>(jPath.getList("id"));
            List<Integer> productIds = new ArrayList<>(jPath.getList("productId"));

            // get total products
            int totalOfProducts = Integer.parseInt(res.getHeader("X-Total-Count"));

            // get number of pages
            int numberOfPages = totalOfProducts / 50;

            // get all inventory
            if (numberOfPages > 1) {
                for (int pageIndex = 1; pageIndex < numberOfPages; pageIndex++) {
                    jPath = getInventoryResponse(branchCondition, pageIndex).jsonPath();
                    ids.addAll(jPath.getList("id"));
                    productIds.addAll(jPath.getList("productId"));
                }
            }


            // set inventory id
            info.setIds(ids);

            // set inventory product id
            info.setProductIds(productIds);
        } catch (AssertionError ex) {
            info.setIds(List.of());
            info.setProductIds(List.of());
        }
        return info;
    }
}
