package api.Seller.orders.return_order;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIReturnOrderDetail {
    Logger logger = LogManager.getLogger(T.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIReturnOrderDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String purchaseOrderDetailPath = "/orderservices2/api/return-orders/store/%s/return-order/%s";

    Response getDetailOfReturnOrderResponse(int returnOrderId) {
        return api.get(purchaseOrderDetailPath.formatted(loginInfo.getStoreID(), returnOrderId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract().response();
    }

    public List<Integer> getItemIds(int returnOrderId) {
        return getDetailOfReturnOrderResponse(returnOrderId).jsonPath().getList("returnOrderItemList.itemId");
    }
}
