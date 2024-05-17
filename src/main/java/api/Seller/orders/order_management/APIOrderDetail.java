package api.Seller.orders.order_management;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrderTags.OrderTags;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static api.Seller.orders.order_management.APIAllOrderCosts.*;
import static api.Seller.orders.order_management.APIAllOrders.*;
import static api.Seller.orders.order_management.APIAllOrders.ShippingMethod.*;

public class APIOrderDetail {
    Logger logger = LogManager.getLogger(APIOrderDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIOrderDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class OrderInformation {
        long orderId;
        PaymentMethod paymentMethod;
        int itemsCount;
        int totalQuantity;
        ShippingMethod shippingMethod;
        OrderStatus status;
        OrderTags orderTags;
        OrderCosts orderCosts;
        List<Integer> itemIds;
        List<Integer> itemQuantity;
    }

    String getOrderDetailPath = "/orderservice3/api/gs/order-details/ids/%s?getLoyaltyEarningPoint=true";

    Response getDetailOfOrderResponse(long orderId) {
        return api.get(getOrderDetailPath.formatted(orderId), loginInfo.getAccessToken(), Map.of("langkey", "en"));
    }

    public OrderStatus getOrderStatus(int orderId) {
        return OrderStatus.valueOf(getDetailOfOrderResponse(orderId).jsonPath().getString("orderInfo.status"));
    }

    public OrderInformation getOrderInformation(long orderId) {
        OrderInformation info = new OrderInformation();
        Response response = getDetailOfOrderResponse(orderId);

        if (response.statusCode() == 403) return info;
        JsonPath jsonPath = response.jsonPath();

        // get order information
        info.setOrderId(orderId);
        info.setPaymentMethod(PaymentMethod.valueOf(jsonPath.getString("orderInfo.paymentMethod")));
        info.setItemsCount(jsonPath.getInt("orderInfo.itemsCount"));
        info.setTotalQuantity(jsonPath.getInt("orderInfo.totalQuantity"));
        String shippingMethod = jsonPath.getString("orderInfo.deliveryName");
        info.setShippingMethod(Optional.ofNullable(shippingMethod).map(method -> valueOf(shippingMethod)).orElse(selfdelivery));
        info.setStatus(OrderStatus.valueOf(jsonPath.getString("orderInfo.status")));
        info.setOrderTags(new OrderTags(jsonPath.getList("orderTagInfos.tagId"), jsonPath.getList("orderTagInfos.name")));
        info.setOrderCosts((jsonPath.getList("orderInfo.orderCosts.id") != null) ? new OrderCosts(jsonPath.getList("orderInfo.orderCosts.id"), jsonPath.getList("orderInfo.orderCosts.id"), jsonPath.getList("orderInfo.orderCosts.id")) : new OrderCosts());
        info.setItemIds(jsonPath.getList("items.itemId"));
        info.setItemQuantity(jsonPath.getList("items.totalQuantity"));

        // return model
        return info;
    }
}
