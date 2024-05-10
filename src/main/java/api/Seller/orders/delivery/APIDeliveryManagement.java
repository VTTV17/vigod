package api.Seller.orders.delivery;

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

public class APIDeliveryManagement {
    Logger logger = LogManager.getLogger(APIDeliveryManagement.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIDeliveryManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    enum DeliveryPackageStatus {
        PICKUP_PENDING, PICKED_UP, DEPARTURE, ARRIVED, SHIPPING, ON_HOLD, ON_RETURN, RETURNED, CANCELLED, DELIVERED
    }

    @Data
    public static class AllDeliveryPackageInformation {
        List<Integer> deliveryIds;
        List<Integer> orderIds;
        List<Integer> customerIds;
        List<DeliveryPackageStatus> statues;
        List<Integer> branchIds;
    }

    String deliveryManagementPath = "/orderservices2/api/partial-delivery-orders/store/%s?searchType=CUSTOMER_NAME&searchKeyword=&page=%s&size=100";

    Response getAllDeliveryPackageResponse(int pageIndex) {
        return api.get(deliveryManagementPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public AllDeliveryPackageInformation getAllReturnOrdersInformation() {
        // init suggestion model
        AllDeliveryPackageInformation info = new AllDeliveryPackageInformation();

        // init temp array
        List<Integer> deliveryIds = new ArrayList<>();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();


        // get total delivery packages
        int totalOfDeliveryPackages =getAllDeliveryPackageResponse(0).jsonPath().getInt("totalRecords");

        // get number of pages
        int numberOfPages = totalOfDeliveryPackages / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllDeliveryPackageResponse(pageIndex).jsonPath();
            deliveryIds.addAll(jsonPath.getList("id"));
            orderIds.addAll(jsonPath.getList("returnOrderId"));
            customerIds.addAll(jsonPath.getList("customerId"));
            statues.addAll(jsonPath.getList("status"));
            branchIds.addAll(jsonPath.getList("returnBranchId"));
        }

        // get all delivery package info
        info.setDeliveryIds(deliveryIds);
        info.setOrderIds(orderIds);
        info.setCustomerIds(customerIds);
        info.setStatues(statues.stream().map(DeliveryPackageStatus::valueOf).toList());
        info.setBranchIds(branchIds);

        // return model
        return info;
    }
}
