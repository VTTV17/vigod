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

    public LotDateManagementInfo getLotDateInformation() {
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
        int totalOfProducts = Integer.parseInt(getAllLotDateResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = ((totalOfProducts / 100) > 0) ? (totalOfProducts / 100) : 1;

        // get all inventory
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
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
}
