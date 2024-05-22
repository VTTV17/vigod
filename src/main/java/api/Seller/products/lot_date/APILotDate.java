package api.Seller.products.lot_date;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

public class APILotDate {
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    API api = new API();

    public APILotDate(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class LotDateManagementInfo {
        List<Integer> lotDateIds = new ArrayList<>();
        List<String> lotNames = new ArrayList<>();
        List<String> lotCodes = new ArrayList<>();
        List<Integer> totalLocations = new ArrayList<>();
        List<Integer> remainingStocks = new ArrayList<>();
        List<Integer> incomingStocks = new ArrayList<>();
        List<Integer> totalProducts = new ArrayList<>();
        List<Integer> totalBranches = new ArrayList<>();
    }

    String getAllLotDatePath = "/itemservice/api/lot-dates/store/%s/search?page=%s&size=100&sort=DESC&sortType=LAST_MODIFIED_DATE";

    Response getAllLotDateResponse(int pageIndex) {
        return api.get(getAllLotDatePath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public LotDateManagementInfo getAllLotDateInformation() {
        LotDateManagementInfo info = new LotDateManagementInfo();

        // init temp array
        List<Integer> lotDateIds = new ArrayList<>();
        List<String> lotNames = new ArrayList<>();
        List<String> lotCodes = new ArrayList<>();
        List<Integer> totalLocations = new ArrayList<>();
        List<Integer> remainingStocks = new ArrayList<>();
        List<Integer> incomingStocks = new ArrayList<>();
        List<Integer> totalProducts = new ArrayList<>();
        List<Integer> totalBranches = new ArrayList<>();

        // get total products
        int totalOfLotDates = Integer.parseInt(getAllLotDateResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfLotDates / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jPath = getAllLotDateResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            lotDateIds.addAll(jPath.getList("id"));
            lotNames.addAll(jPath.getList("lotName"));
            lotCodes.addAll(jPath.getList("lotCode"));
            totalLocations.addAll(jPath.getList("totalLocation"));
            remainingStocks.addAll(jPath.getList("remainingStock"));
            incomingStocks.addAll(jPath.getList("incomingStock"));
            totalProducts.addAll(jPath.getList("totalProduct"));
            totalBranches.addAll(jPath.getList("totalBranch"));
        }

        // get all lot date information
        info.setLotDateIds(lotDateIds);
        info.setLotNames(lotNames);
        info.setLotCodes(lotCodes);
        info.setTotalLocations(totalLocations);
        info.setRemainingStocks(remainingStocks);
        info.setIncomingStocks(incomingStocks);
        info.setTotalProducts(totalProducts);
        info.setTotalBranches(totalBranches);

        return info;
    }

    @Data
    public static class AllProductLotInfo {
        private List<Integer> ids;
        private List<String> lotNames;
        private List<String> lotCodes;
        private List<Integer> remainingStocks;
    }

    @Data
    public static class ProductLotInfo {
        private Integer id;
        private String lotName;
        private String lotCode;
        private Integer remainingStock;
    }

    String productLotPath = "/itemservice/api/lot-dates/store/%s/search-receipt?page=%s&size=100";

    Response getProductLotResponse(int pageIndex, int itemId, int modelId, int branchId, String locationReceiptType) {
        String body = """
                {
                    "itemId": %s,
                    "modelId": %s,
                    "branchId": "%s",
                    "searchKeyword": "",
                    "hideExpire": false,
                    "locationReceiptType": "%s"
                }""".formatted(itemId, (modelId == 0) ? "" : modelId, branchId, locationReceiptType);
        return api.post(productLotPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken(), body);
    }

    public AllProductLotInfo getAllProductLotInfo(int itemId, int modelId, int branchId, String locationReceiptType) {
        // init model
        AllProductLotInfo info = new AllProductLotInfo();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<String> lotNames = new ArrayList<>();
        List<String> lotCodes = new ArrayList<>();
        List<Integer> remainingStocks = new ArrayList<>();

        // get total products
        int totalOfLotDate = Integer.parseInt(getProductLotResponse(0, itemId, modelId, branchId, locationReceiptType).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfLotDate / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jPath = getProductLotResponse(pageIndex, itemId, modelId, branchId, locationReceiptType)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jPath.getList("id"));
            lotNames.addAll(jPath.getList("lotName"));
            lotCodes.addAll(jPath.getList("lotCode"));
            remainingStocks.addAll(jPath.getList("remainingStock"));
        }

        // set result
        info.setIds(ids);
        info.setLotNames(lotNames);
        info.setLotCodes(lotCodes);
        info.setRemainingStocks(remainingStocks);

        return info;
    }


    public ProductLotInfo getLotInStock(int itemId, int modelId, int branchId, String locationReceiptType) {
        AllProductLotInfo searchInfo = getAllProductLotInfo(itemId, modelId, branchId, locationReceiptType);
        ProductLotInfo info = new ProductLotInfo();

        for (int index = 0; index < searchInfo.getIds().size(); index++) {
            if (searchInfo.getRemainingStocks().get(index) > 0) {
                info.setId(searchInfo.getIds().get(index));
                info.setLotCode(searchInfo.getLotCodes().get(index));
                info.setLotName(searchInfo.getLotNames().get(index));
                info.setRemainingStock(searchInfo.getRemainingStocks().get(index));

                break;
            }
        }

        return info;
    }
}
