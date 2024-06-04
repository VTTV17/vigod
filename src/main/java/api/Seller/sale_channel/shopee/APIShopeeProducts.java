package api.Seller.sale_channel.shopee;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class APIShopeeProducts {
    Logger logger = LogManager.getLogger(APIShopeeProducts.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIShopeeProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    enum GoSELLStatus {
        LINK, UNLINK
    }

    @Data
    public static class ShopeeProductsInformation {
        List<Integer> ids = new ArrayList<>();
        List<Long> shopeeItemIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Integer> shopeeIds = new ArrayList<>();
        List<String> shopeeShopNames = new ArrayList<>();
        List<GoSELLStatus> gosellStatues = new ArrayList<>();
        List<Boolean> hasVariations = new ArrayList<>();
        List<Integer> variationNum = new ArrayList<>();
    }

    String allShopeeProductPath = "/shopeeservices/api/items/bc-store/%s?page=%s&size=100&getBcItemName=true&sort=update_time,DESC";

    Response getShopeeProductResponse(int pageIndex) {
        return api.get(allShopeeProductPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public ShopeeProductsInformation getShopeeProductInformation() {
        // init temp
        ShopeeProductsInformation info = new ShopeeProductsInformation();
        List<Integer> ids = new ArrayList<>();
        List<Long> shopeeItemIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Integer> shopeeIds = new ArrayList<>();
        List<String> shopeeShopNames = new ArrayList<>();
        List<String> gosellStatues = new ArrayList<>();
        List<Boolean> hasVariations = new ArrayList<>();
        List<Integer> variationNum = new ArrayList<>();

        Response response = getShopeeProductResponse(0);
        if (response.getStatusCode() != 403) {
            int numOfPages = Integer.parseInt(response.getHeader("X-Total-Count")) / 100;

            List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numOfPages).parallel()
                    .mapToObj(pageIndex -> getShopeeProductResponse(pageIndex).then().statusCode(200).extract().jsonPath())
                    .toList();

            jsonPaths.forEach(jsonPath -> {
                ids.addAll(jsonPath.getList("id"));
                shopeeItemIds.addAll(jsonPath.getList("shopeeItemId"));
                branchIds.addAll(jsonPath.getList("branchId"));
                shopeeIds.addAll(jsonPath.getList("shopeeShopId"));
                shopeeShopNames.addAll(jsonPath.getList("shopeeShopName"));
                gosellStatues.addAll(jsonPath.getList("gosellStatus"));
                hasVariations.addAll(jsonPath.getList("hasVariation"));

                // get variation number
                List<ArrayList<Integer>> variationIds = jsonPath.getList("variations.id");
                AtomicInteger productIndex = new AtomicInteger(0);
                List<Integer> varNum = hasVariations.stream().map(hasVariation -> hasVariation ? variationIds.get(productIndex.getAndIncrement()).size() : 0).toList();
                variationNum.addAll(varNum);
            });
        }

        // return model
        info.setIds(ids);
        info.setShopeeItemIds(shopeeItemIds);
        info.setBranchIds(branchIds);
        info.setShopeeIds(shopeeIds);
        info.setShopeeShopNames(shopeeShopNames);
        info.setGosellStatues(gosellStatues.stream().map(GoSELLStatus::valueOf).toList());
        info.setHasVariations(hasVariations);
        info.setVariationNum(variationNum);

        return info;
    }

}
