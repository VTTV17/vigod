package api.Seller.orders;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class APIAllOrders {
    Logger logger = LogManager.getLogger(APIAllOrders.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllOrders(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class AllOrdersInformation {
        List<Integer> ids;
        List<Integer> bcOrderGroupId;
        List<String> statues;
        List<Integer> customerIds;
        List<Integer> branchIds;
        List<OrderTags> orderTags;
        List<String> shippingMethod;
    }

    @Data
    public static class OrderTags {
        List<Integer> tagId;
    }

    public enum Channel {
        GOSELL, BEECOW, SHOPEE, LAZADA, TIKTOK
    }

    String getAllOrderPath = "/beehiveservices/api/orders/gosell-store/v2/%s?page=%s&size=100&%s&channel=%s&view=COMPACT";

    Response getAllOrderResponse(int pageIndex, String branchQuery, Channel channel) {
        return api.get(getAllOrderPath.formatted(loginInfo.getStoreID(), pageIndex, branchQuery, channel), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public AllOrdersInformation getAllOrderInformation(Channel channel) {
        // get branchQuery
        String branchQuery = "branchIds=%s".formatted(loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", ""));

        // init model
        AllOrdersInformation info = new AllOrdersInformation();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<Integer> bcOrderGroupIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Object> orderTags = new ArrayList<>();
        List<String> shippingMethod = new ArrayList<>();

        // get total products
        int totalOfOrders = Integer.parseInt(getAllOrderResponse(0, branchQuery, channel).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfOrders / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            Response response = getAllOrderResponse(pageIndex, branchQuery, channel);
            ids.addAll(response.jsonPath().getList("response.id"));
            bcOrderGroupIds.addAll(response.jsonPath().getList("response.bcOrderGroupId"));
            statues.addAll(response.jsonPath().getList("response.status"));
            customerIds.addAll(response.jsonPath().getList("response.customerId"));
            branchIds.addAll(response.jsonPath().getList("response.branchId"));
            orderTags.addAll(response.jsonPath().getList("response.orderTags"));
            shippingMethod.addAll(response.jsonPath().getList("response.shippingMethod"));
        }

        // set suggestion info
        info.setIds(ids);
        info.setBcOrderGroupId(bcOrderGroupIds);
        info.setStatues(statues);
        info.setCustomerIds(customerIds);
        info.setBranchIds(branchIds);
        info.setShippingMethod(shippingMethod);
        List<OrderTags> tagsList = new ArrayList<>();

        orderTags.forEach(tags -> {
            OrderTags tag = new OrderTags();
            tag.setTagId(Pattern.compile("tagId=(\\d+)").matcher(tags.toString())
                    .results()
                    .map(matchResult -> Integer.valueOf(matchResult.group(1)))
                    .toList());
            tagsList.add(tag);
        });
        info.setOrderTags(tagsList);

        // return model
        return info;
    }
}
