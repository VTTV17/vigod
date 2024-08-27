package api.Seller.products.inventory;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIOrderDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class APIInventoryV2 {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIInventoryV2(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getInventoryPath(int storeId, int pageIndex, String keyword, String branchId) {
        return "/itemservice/api/inventory-summary/%s/items?langKey=vi&page=%s&size=100&filter=&sort=priority,asc&search=%s&branchIds=%s&isAlertStockProducts=false".formatted(storeId, pageIndex, keyword, branchId);
    }

    private Response getInventoryResponse(int pageIndex, String keyword, String branchId) {
        return api.get(getInventoryPath(loginInfo.getStoreID(), pageIndex, keyword, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public List<ProductInventory> getProductInventory(String keyword, String branchId) {
        return getInventoryResponse(0, keyword, branchId).jsonPath().getList(".", ProductInventory.class);
    }

    public List<ProductInventory> getAllProductInventory() {
        // Get all branches params
        String branchIds = loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", "");

        // get number of pages
        int numberOfPages = Integer.parseInt(getInventoryResponse(0, "", branchIds).getHeader("X-Total-Count")) / 100;

        // get other page data
        List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getInventoryResponse(pageIndex, "", branchIds).jsonPath())
                .toList();

        List<ProductInventory> productInventoryHistories = new ArrayList<>();
        jsonPaths.forEach(jsonPath -> productInventoryHistories.addAll(jsonPath.getList(".", ProductInventory.class)));
        return productInventoryHistories;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInventory {
        private String id;
        private int productId;
        private boolean isVariation;
        private String modelName;
        private String itemName;
        private String barcode;
        private String status;
        private long newPrice;
        private long orgPrice;
        private long remainingItem;
        private long soldItem;
        private int transactionItem;
        private int priority;
        private int totalBranches;
        private String inventoryType;
        private boolean hasConversion;
    }

    public void checkInventoryAfterOrder(long orgStock, long orderId) {
        OrderDetailInfo info = new APIOrderDetail(loginInformation).getOrderDetail(orderId);
        Map<String, Long> map = new APIOrderDetail(loginInformation).getOrderItems(info);
        map.keySet().forEach(barcode -> {
            // Get product/variation inventory
            ProductInventory inventory = getProductInventory(barcode, String.valueOf(info.getStoreBranch().getId())).parallelStream()
                    .filter(inv -> barcode.equals(inv.getId())).findFirst().orElse(null);

            // Check inventory available
            if (inventory != null) {
                // Check sold quantity
                long expectedSoldQuantity = map.get(barcode);
                long actualSoldQuantity = inventory.getSoldItem();
                new AssertCustomize().assertEquals(actualSoldQuantity, expectedSoldQuantity, "[Item barcode: %s] Sold out must be %d, but found %d".formatted(barcode, expectedSoldQuantity, actualSoldQuantity));

                // Check remaining stock
                long expectedRemainingStock = orgStock - expectedSoldQuantity;
                long actualRemainingStock = inventory.getRemainingItem();
                new AssertCustomize().assertEquals(actualRemainingStock, expectedRemainingStock, "[Item barcode: %s] Remaining stock must be %d, but found %d".formatted(barcode, expectedRemainingStock, actualRemainingStock));
            } else throw new RuntimeException("No inventory with barcode: %s".formatted(barcode));
        });
    }
}
