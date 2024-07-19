package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts.ProductManagementInfo;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class APIAllProductsForCheckSortAndFilter {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllProductsForCheckSortAndFilter(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String allProductListPath = "/itemservice/api/store/dashboard/%s/items-v2?page=%s&size=100&itemType=BUSINESS_PRODUCT";

    Response getAllProductsResponse(String getAllProductPath, int pageIndex) {

        return api.get(getAllProductPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public ProductManagementInfo getAllProductInformation(String getAllProductPath) {
        // Init product management info
        ProductManagementInfo info = new ProductManagementInfo();
        // get page 0 data
        List<Integer> variationNumber = new ArrayList<>();
        List<Integer> allProductIds = new ArrayList<>();
        List<String> allProductNames = new ArrayList<>();
        List<Integer> remainingStocks = new ArrayList<>();
        List<Integer> priority = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getAllProductsResponse(getAllProductPath, 0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 50;

        // get other page data
        List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj(pageIndex -> getAllProductsResponse(getAllProductPath, pageIndex).jsonPath())
                .toList();
        jsonPaths.forEach(jsonPath -> {
            variationNumber.addAll(jsonPath.getList("variationNumber"));
            allProductIds.addAll(jsonPath.getList("id"));
            allProductNames.addAll(jsonPath.getList("name"));
            remainingStocks.addAll(jsonPath.getList("remainingStock"));
            priority.addAll(jsonPath.getList("priority"));
        });
        info.setProductIds(allProductIds);
        info.setVariationNumber(variationNumber);
        info.setProductNames(allProductNames);
        info.setRemainingStocks(remainingStocks);
        info.setPriority(priority);

        return info;
    }

    public List<String> getListProductNameAfterSortByRecentUpdated() {
        String getAllProductPath = allProductListPath + "&sort=lastModifiedDate,desc";
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductNameAfterSortByStockHighToLow() {
        String getAllProductPath = allProductListPath + "&sort=stock,desc";
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductNameAfterSortByStockLowToHigh() {
        String getAllProductPath = allProductListPath + "&sort=stock,asc";
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductNameAfterSortByPriorityHighToLow() {
        String getAllProductPath = allProductListPath + "&sort=priority,desc&sort=lastModifiedDate,desc";
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductNameAfterSortByPriorityLowToHigh() {
        String getAllProductPath = allProductListPath + "&sort=priority,asc&sort=lastModifiedDate,desc";
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductAfterFilterByStatus(String status) {
        String getAllProductPath = allProductListPath + "&sort=lastModifiedDate,desc&bhStatus=%s".formatted(status);
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductAfterFilterByChannel(String channel) {
        String getAllProductPath = allProductListPath + "&sort=lastModifiedDate,desc&saleChannel=%s".formatted(channel);
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductAfterFilterByPlatform(String platform) {
        String getAllProductPath = allProductListPath + "&sort=lastModifiedDate,desc&platform=%s".formatted(platform);
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductAfterFilterByBranch(int branchId) {
        String getAllProductPath = allProductListPath + "&sort=lastModifiedDate,desc&branchIds=%s".formatted(branchId);
        return getAllProductInformation(getAllProductPath).getProductNames();
    }

    public List<String> getListProductAfterFilterByCollection(String collectionId) {
        String getAllProductPath = allProductListPath + "&sort=lastModifiedDate,desc&collectionId=%s".formatted(collectionId);
        return getAllProductInformation(getAllProductPath).getProductNames();
    }
}
