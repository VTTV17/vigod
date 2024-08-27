package api.Seller.products.inventory;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIOrderDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import lombok.SneakyThrows;
import utilities.api.API;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class APIInventoryHistoryV2 {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIInventoryHistoryV2(
            LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getInventoryHistoryPath = "/itemservice/api/inventory-search/%s?search=%s&branchIds=%s&page=%s&size=100";

    @SneakyThrows
    public Response getInventoryResponse(int pageIndex, String keywords, String branchId) {
        return api.get(getInventoryHistoryPath.formatted(loginInfo.getStoreID(), keywords, branchId, pageIndex), loginInfo.getAccessToken(), Map.of("langkey", "vi"))
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public List<ProductInventoryHistory> getInventoryHistory(String keyword, String branchId) {
        return getInventoryResponse(0, keyword, branchId).jsonPath().getList(".", ProductInventoryHistory.class);
    }

    public List<ProductInventoryHistory> getAllInventoryHistory() {
        // Get all branches params
        String branchIds = loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", "");

        // get number of pages
        int numberOfPages = Integer.parseInt(getInventoryResponse(0, "", branchIds).getHeader("X-Total-Count")) / 100;

        // get other page data
        List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getInventoryResponse(pageIndex, "", branchIds).jsonPath())
                .toList();

        List<ProductInventoryHistory> productInventoryHistories = new ArrayList<>();
        jsonPaths.forEach(jsonPath -> productInventoryHistories.addAll(jsonPath.getList(".", ProductInventoryHistory.class)));
        return productInventoryHistories;
    }

    enum InventoryActionType {
        AUTO_SYNC_STOCK, FROM_CREATE_AT_ITEM_SCREEN, FROM_DELETE_LOT, FROM_EDIT_ORDER, FROM_EXCLUDE_STOCK_LOT_DATE, FROM_IMPORT, FROM_ITEM_LIST, FROM_LAZADA, FROM_LOCK, FROM_LOT_DATE_RESTOCK, FROM_PURCHASE_ORDER, FROM_RESET_STOCK, FROM_RETURN_ORDER, FROM_SHOPEE, FROM_SOLD, FROM_TIKI, FROM_TIKTOK, FROM_TRANSFER_AFFILIATE_IN, FROM_TRANSFER_AFFILIATE_OUT, FROM_TRANSFER_AFFILIATE_RESTOCK, FROM_TRANSFER_IN, FROM_TRANSFER_OUT, FROM_TRANSFER_RESTOCK, FROM_UNLOCK, FROM_UPDATE_AT_INSTORE_PURCHASE, FROM_UPDATE_AT_ITEM_SCREEN, FROM_UPDATE_AT_VARIATION_DETAIL, FROM_UPDATE_STOCK_IN_LOT
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInventoryHistory {
        private String productName;
        private int stockChange;
        private int remainingStock;
        private String inventoryType;
        private InventoryActionType actionType;
        private String orderId;
        private String operator;
        private String id;
        private boolean hasConversion;
    }

    public void checkInventoryAfterOrder(long orderId) {
        OrderDetailInfo info = new APIOrderDetail(loginInformation).getOrderDetail(orderId);
        Map<String, Long> map = new APIOrderDetail(loginInformation).getOrderItems(info);
        map.keySet().parallelStream().forEach(key -> {
            long expectedQuantity = map.get(key);
            long actualQuantity = getInventoryHistory(key, String.valueOf(info.getStoreBranch().getId())).get(0).getStockChange();
            new AssertCustomize().assertEquals(actualQuantity, -expectedQuantity, "Stock change must be %d, but found %d".formatted(expectedQuantity, actualQuantity));
        });
    }
}
