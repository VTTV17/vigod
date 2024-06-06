package api.Seller.orders.delivery;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import static api.Seller.orders.order_management.APIAllOrders.*;

public class APIDeliveryDetail {
    Logger logger = LogManager.getLogger(T.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIDeliveryDetail (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }

    String getDeliveryPackageDetailPath = "/orderservices2/api/partial-delivery-orders/store/%s/partialDeliveryOrder/%s";

    Response getDeliveryDetailResponse(int deliveryId) {
        return api.get(getDeliveryPackageDetailPath.formatted(loginInfo.getStoreID(), deliveryId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public OrderStatus getOrderStatus(int deliveryId) {
        return OrderStatus.valueOf(getDeliveryDetailResponse(deliveryId).jsonPath().getString("orderStatus"));
    }
}
