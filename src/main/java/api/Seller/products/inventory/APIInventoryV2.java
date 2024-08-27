package api.Seller.products.inventory;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIOrderDetail;
import api.Seller.products.all_products.APIProductConversionUnit;
import app.Buyer.account.myorders.orderdetail.OrderDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.testng.Assert;
import utilities.api.API;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.stream.IntStream;

import static api.Seller.products.all_products.APIProductConversionUnit.ConversionUnitItem;

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
        private int remainingItem;
        private int soldItem;
        private int transactionItem;
        private int priority;
        private int totalBranches;
        private String inventoryType;
        private boolean hasConversion;
    }

    public void checkInventoryAfterOrder(long orderId) {
        OrderDetailInfo info = new APIOrderDetail(loginInformation).getOrderDetail(orderId);
        Map<String, Long> map = new APIOrderDetail(loginInformation).getOrderItems(info);
        System.out.println(map);
        map.keySet().parallelStream().forEach(key -> {
            long expectedQuantity = map.get(key);
            long actualQuantity = getProductInventory(key, String.valueOf(info.getStoreBranch().getId())).get(0).getSoldItem();
            new AssertCustomize().assertEquals(actualQuantity, expectedQuantity, "Sold out must be %d, but found %d".formatted(expectedQuantity, actualQuantity));
        });
    }
}
