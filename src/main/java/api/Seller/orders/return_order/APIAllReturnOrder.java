package api.Seller.orders.return_order;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;

import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus.CANCELLED;
import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus.COMPLETED;
import static utilities.data.DataGenerator.getFirstString;

public class APIAllReturnOrder {
    Logger logger = LogManager.getLogger(T.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllReturnOrder(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public enum ReturnOrderStatus {
        IN_PROGRESS, COMPLETED, CANCELLED
    }

    enum RefundStatus {
        NOT_REFUND, PARTIAL_REFUNDED, REFUNDED
    }

    @Data
    public static class AllReturnOrderInformation {
        List<Integer> ids;
        List<String> returnOrderIds;
        List<Integer> bcOrderIds;
        List<Integer> customerIds;
        List<ReturnOrderStatus> statues;
        List<RefundStatus> refundStatues;
        List<Integer> returnBranchIds;
    }

    String getAllReturnOrdersPath = "/orderservices2/api/return-order/%s?page=%s&size=100&searchKeyword=%s&searchType=ORDER_ID&branchId=%s&restock=&status=&refundStatus=&staffName=";

    String getBranchQueryParams() {
        return loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\]]", "");
    }

    Response getAllReturnOrderResponse(int pageIndex, String... searchKeyword) {
        return api.get(getAllReturnOrdersPath.formatted(loginInfo.getStoreID(), pageIndex, getFirstString(searchKeyword), getBranchQueryParams()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public AllReturnOrderInformation getAllReturnOrdersInformation(String... searchKeyword) {
        // init suggestion model
        AllReturnOrderInformation info = new AllReturnOrderInformation();

        // init temp array
        List<String> ids = new ArrayList<>();
        List<String> returnOrderIds = new ArrayList<>();
        List<Integer> bcOrderIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<String> refundStatues = new ArrayList<>();
        List<Integer> returnBranchIds = new ArrayList<>();


        // get total return orders
        int totalOfReturnOrders = Integer.parseInt(getAllReturnOrderResponse(0, searchKeyword).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfReturnOrders / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllReturnOrderResponse(pageIndex,searchKeyword).jsonPath();
            ids.addAll(jsonPath.getList("id"));
            returnOrderIds.addAll(jsonPath.getList("returnOrderId"));
            bcOrderIds.addAll(jsonPath.getList("bcOrderId"));
            customerIds.addAll(jsonPath.getList("customerId"));
            statues.addAll(jsonPath.getList("status"));
            refundStatues.addAll(jsonPath.getList("refundStatus"));
            returnBranchIds.addAll(jsonPath.getList("returnBranchId"));
        }

        // get all return order info
        info.setIds(ids.stream().map(Integer::parseInt).toList());
        info.setReturnOrderIds(returnOrderIds);
        info.setBcOrderIds(bcOrderIds);
        info.setCustomerIds(customerIds);
        info.setStatues(statues.stream().map(ReturnOrderStatus::valueOf).toList());
        info.setRefundStatues(refundStatues.stream().map(APIAllReturnOrder.RefundStatus::valueOf).toList());
        info.setReturnBranchIds(returnBranchIds);

        // return model
        return info;
    }
}