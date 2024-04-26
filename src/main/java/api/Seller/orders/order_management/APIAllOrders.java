package api.Seller.orders.order_management;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static api.Seller.orders.order_management.APIAllOrders.OrderStatus.*;

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
        List<OrderStatus> statues;
        List<Integer> customerIds;
        List<Integer> branchIds;
        List<OrderTags> orderTags;
        List<String> shippingMethod;
    }

    @Data
    public static class OrderTags {
        List<Integer> tagId;
    }

    enum OrderStatus {
        CANCELLED, CANCEL_COMPLETED, CANCEL_PENDING, CANCEL_REJECTED, COMPLETED, DELIVERED, FAILED, IN_CANCEL, PARTIALLY_SHIPPING, PENDING, PICKED, REJECTED, RETURNED, SHIPPED, TO_CONFIRM, TO_SHIP, UNKNOWN, WAITING_FOR_PICKUP


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
        List<String> ids = new ArrayList<>();
        List<Integer> bcOrderGroupIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Object> orderTags = new ArrayList<>();
        List<String> shippingMethod = new ArrayList<>();

        // get total products
        int totalOfOrders = Integer.parseInt(getAllOrderResponse(0, branchQuery, channel).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = Math.min(totalOfOrders / 100, 99);

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
        info.setIds(ids.stream().map(Integer::parseInt).toList());
        info.setBcOrderGroupId(bcOrderGroupIds);
        info.setStatues(statues.stream().map(OrderStatus::valueOf).toList());
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

    public List<Integer> getListProductIdInNotCompletedOrder() {
        AllOrdersInformation info = getAllOrderInformation(Channel.GOSELL);
        List<Integer> orderIds = info.getIds();
        List<OrderStatus> statues = info.getStatues();

        // get list in-complete return order id
        List<Integer> inCompleteReturnOrderIds = orderIds.stream()
                .filter(orderId -> !(Objects.equals(statues.get(orderIds.indexOf(orderId)), CANCELLED)
                        || Objects.equals(statues.get(orderIds.indexOf(orderId)), DELIVERED)
                        || Objects.equals(statues.get(orderIds.indexOf(orderId)), FAILED)
                        || Objects.equals(statues.get(orderIds.indexOf(orderId)), REJECTED)))
                .toList();

        // init return order api
        APIOrderDetail orderDetail = new APIOrderDetail(loginInformation);

        // get list itemId in in-complete return order
        return inCompleteReturnOrderIds.stream()
                .map(orderDetail::getItemIds).filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }
}
