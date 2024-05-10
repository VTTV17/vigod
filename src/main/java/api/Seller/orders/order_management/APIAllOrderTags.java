package api.Seller.orders.order_management;

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

public class APIAllOrderTags {
    Logger logger = LogManager.getLogger(APIAllOrderTags.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllOrderTags(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class OrderTags {
        List<Integer> tagIds;
        List<String> tagNames;

        public OrderTags() {}
        public OrderTags(List<Integer> tagIds, List<String> tagNames) {
            this.tagIds = tagIds;
            this.tagNames = tagNames;
        }


    }

    String getAllOrderTagsPath = "/orderservices2/api/search/tags/%s?page=%s&size=100";

    Response getAllOrderTagsResponse(int pageIndex) {
        return api.get(getAllOrderTagsPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public OrderTags getAllOrderTagsInformation() {
        // init model
        OrderTags info = new OrderTags();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();

        // get total order tags
        int totalOfOrderTags = Integer.parseInt(getAllOrderTagsResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfOrderTags / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllOrderTagsResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jsonPath.getList("id"));
            names.addAll(jsonPath.getList("name"));

        }

        // set order tags info
        info.setTagIds(ids);
        info.setTagNames(names);

        // return model
        return info;

    }
}
