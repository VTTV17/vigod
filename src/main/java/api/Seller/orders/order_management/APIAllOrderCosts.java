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
import java.util.Optional;

public class APIAllOrderCosts {
    Logger logger = LogManager.getLogger(APIAllOrderCosts.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllOrderCosts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class OrderCosts {
        List<Integer> ids;
        List<String> names;
        List<Long> amounts;

        public OrderCosts() {
            this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
        public OrderCosts(List<Integer> ids, List<String> names, List<Long> amounts) {
            this.ids = Optional.ofNullable(ids).orElse(List.of());
            this.names = Optional.ofNullable(names).orElse(List.of());
            this.amounts = Optional.ofNullable(amounts).orElse(List.of());
        }
    }

    String getAllOrderCostsInStorePath = "/orderservices2/api/cost/storeId/%s";

    Response getAllOrderCostsResponse() {
        return api.get(getAllOrderCostsInStorePath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public OrderCosts getAllOrderCostsInformation() {
        OrderCosts info = new OrderCosts();

        JsonPath jsonPath = getAllOrderCostsResponse().jsonPath();

        // get all order costs information
        info.setIds(jsonPath.getList("id"));
        info.setNames(jsonPath.getList("name"));
        info.setAmounts(jsonPath.getList("amount"));

        // return model
        return info;
    }
}
