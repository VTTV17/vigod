package api.Seller.orders.delivery;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIPartialDeliveryOrders {
    Logger logger = LogManager.getLogger(APIPartialDeliveryOrders.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public enum DeliveryMethod {
        selfdelivery, giaohangtietkiem, giaohangnhanh, ahamove, others
    }

    public APIPartialDeliveryOrders(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String partialDeliveryOrdersPath = "/orderservices2/api/partial-delivery-orders/order/%s";
    String partialDeliveryWithAvailableItemPath = "/orderservices2/api/shop/bc-orders/%s/partial-delivery-order/available-items/v2";

    Response getPartialDeliveryOrdersResponse(long orderId) {
        return api.get(partialDeliveryOrdersPath.formatted(orderId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public int getNumOfOrderDeliveryPackage(long orderId) {
        Response response = getPartialDeliveryOrdersResponse(orderId);
        return response.getBody().asString().isEmpty() ? 0 : response.jsonPath().getList("partialDeliveryInfos.id").size();
    }

    public Response getPartialDeliveryWithAvailableItemResponse(long orderId) {
        return api.get(partialDeliveryWithAvailableItemPath.formatted(orderId), loginInfo.getAccessToken());
    }

    public List<DeliveryMethod> getListDeliveryMethodWithOrder(long orderId) {
        List<String> deliveryMethods = getPartialDeliveryWithAvailableItemResponse(orderId)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("deliveryMethods");
        return deliveryMethods.stream().map(DeliveryMethod::valueOf).toList();
    }
}
