package api.Seller.orders.delivery;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class APIDeliveryManagement {
    Logger logger = LogManager.getLogger(APIDeliveryManagement.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    @Getter
    private final static Cache<LoginDashboardInfo, AllDeliveryPackageInformation> deliveryCache = CacheBuilder.newBuilder().build();

    public APIDeliveryManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    enum DeliveryPackageStatus {
        PICKUP_PENDING, PICKED_UP, DEPARTURE, ARRIVED, SHIPPING, ON_HOLD, ON_RETURN, RETURNED, CANCELLED, DELIVERED
    }

    @Data
    public static class AllDeliveryPackageInformation {
        List<Integer> deliveryIds = new ArrayList<>();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<DeliveryPackageStatus> statues = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
    }

    String deliveryManagementPath = "/orderservices2/api/partial-delivery-orders/store/%s?searchType=CUSTOMER_NAME&searchKeyword=&page=%s&size=100";

    Response getAllDeliveryPackageResponse(int pageIndex) {
        return api.get(deliveryManagementPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public AllDeliveryPackageInformation getAllDeliveryInformation() {
        AllDeliveryPackageInformation info = deliveryCache.getIfPresent(loginInfo);
        if (info == null) {
            // init suggestion model
            info = new AllDeliveryPackageInformation();

            // init temp array
            List<Integer> deliveryIds = new ArrayList<>();
            List<Integer> orderIds = new ArrayList<>();
            List<Integer> customerIds = new ArrayList<>();
            List<String> statues = new ArrayList<>();
            List<Integer> branchIds = new ArrayList<>();

            // get total delivery packages
            Response response = getAllDeliveryPackageResponse(0);
            if (response.getStatusCode() == 403) return info;
            int totalOfDeliveryPackages = response.jsonPath().getInt("totalRecords");

            // get number of pages
            int numberOfPages = totalOfDeliveryPackages / 100;

            // get other page data
            List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(pageIndex -> getAllDeliveryPackageResponse(pageIndex).then().statusCode(200).extract().response())
                    .map(ResponseBodyExtractionOptions::jsonPath)
                    .toList();
            jsonPaths.forEach(jsonPath -> {
                deliveryIds.addAll(jsonPath.getList("partialDeliveryOrderList.deliveryId"));
                orderIds.addAll(jsonPath.getList("partialDeliveryOrderList.orderId"));
                customerIds.addAll(jsonPath.getList("partialDeliveryOrderList.customerId"));
                statues.addAll(jsonPath.getList("partialDeliveryOrderList.status"));
                branchIds.addAll(jsonPath.getList("partialDeliveryOrderList.branchId"));
            });

            // get all delivery package info
            info.setDeliveryIds(deliveryIds);
            info.setOrderIds(orderIds);
            info.setCustomerIds(customerIds);
            info.setStatues(statues.stream().map(DeliveryPackageStatus::valueOf).toList());
            info.setBranchIds(branchIds);

            // save cache
            deliveryCache.put(loginInfo, info);
        }

        // return model
        return info;
    }

    public List<Integer> getListDeliveryIdAfterFilterByBranches(List<Integer> assignedBranchIds) {
        AllDeliveryPackageInformation info = getAllDeliveryInformation();
        List<Integer> deliveryIds = info.getDeliveryIds();
        List<Integer> branchIds = info.getBranchIds();

        return deliveryIds.stream().filter(id -> assignedBranchIds.contains(branchIds.get(deliveryIds.indexOf(id)))).toList();
    }
}
