package api.Seller.orders.order_management;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.Map;

import static api.Seller.orders.order_management.APIAllOrders.*;

public class APIOrderDetail {
    Logger logger = LogManager.getLogger(APIOrderDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIOrderDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getOrderDetailPath = "/orderservice3/api/gs/order-details/ids/%s?getLoyaltyEarningPoint=true";

    Response getDetailOfOrderResponse(long orderId) {
        return api.get(getOrderDetailPath.formatted(orderId), loginInfo.getAccessToken(), Map.of("langkey", "en"));
    }

    public List<Integer> getItemIds(long orderId) {
            Response response = getDetailOfOrderResponse(orderId);
            int count = 0;
            while (response.statusCode() == 500) {
                logger.info(response.getBody().asString());
                response = getDetailOfOrderResponse(orderId);
                count ++;
                if (count == 2) break;
            }
        return response.jsonPath().getList("items.itemId");
    }

    public OrderStatus getOrderStatus(int orderId) {
        return OrderStatus.valueOf(getDetailOfOrderResponse(orderId).jsonPath().getString("orderInfo.status"));
    }
}
