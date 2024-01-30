package api.Seller.products;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.stream.Collectors;

public class Inventory {
    String getInventoryPath = "/itemservice/api/inventory-summary/%s/items?langKey=vi&page=0&size=100&filter=&sort=priority,asc&search=&branchIds=%s&isAlertStockProducts=false";
    LoginInformation loginInformation;
    LoginDashboardInfo info;
    API api = new API();

    public Inventory(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        info = new Login().getInfo(loginInformation);
    }

    @Data
    public static class InventoryInfo {
        private List<Integer> id;
        private List<Integer> productIds;
    }

    public InventoryInfo getInventoryInformation() {
        List<Integer> assignedBranches = (info.getAssignedBranches() != null) ? info.getAssignedBranches() : new BranchManagement(loginInformation).getInfo().getBranchID();
        String branchCondition = assignedBranches.stream().map(branchId -> (assignedBranches.indexOf(branchId) == 0 ? "%s" : ",%s").formatted(branchId)).collect(Collectors.joining());
        JsonPath jPath = api.get(getInventoryPath.formatted(info.getStoreID(), branchCondition), info.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();


        InventoryInfo info = new InventoryInfo();
        // set inventory id
        info.setId(jPath.getList("id"));

        // set inventory product id
        info.setProductIds(jPath.getList("productId"));

        return info;
    }
}
