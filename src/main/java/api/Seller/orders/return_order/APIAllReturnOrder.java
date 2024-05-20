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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus.COMPLETED;
import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus.IN_PROGRESS;
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
        List<Integer> ids = new ArrayList<>();
        List<String> returnOrderIds = new ArrayList<>();
        List<Integer> bcOrderIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<ReturnOrderStatus> statues = new ArrayList<>();
        List<RefundStatus> refundStatues = new ArrayList<>();
        List<Integer> returnBranchIds = new ArrayList<>();
        List<Boolean> restocks = new ArrayList<>();
        List<Long> refundAmounts = new ArrayList<>();
        List<Long> totalRefunds = new ArrayList<>();
    }

    String getAllReturnOrdersPath = "/orderservices2/api/return-order/%s?page=%s&size=100&searchKeyword=%s&searchType=ORDER_ID&branchId=%s&restock=&status=&refundStatus=&staffName=";

    String getBranchQueryParams() {
        return loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\]]", "");
    }

    Response getAllReturnOrderResponse(int pageIndex, String... searchKeyword) {
        return api.get(getAllReturnOrdersPath.formatted(loginInfo.getStoreID(), pageIndex, getFirstString(searchKeyword), getBranchQueryParams()), loginInfo.getAccessToken());
    }

    public AllReturnOrderInformation getAllReturnOrdersInformation(String... searchKeyword) {
        // init suggestion model
        AllReturnOrderInformation info = new AllReturnOrderInformation();

        // get page 0 response
        Response response = getAllReturnOrderResponse(0, searchKeyword);

        if (response.getStatusCode() == 403) return info;

        // init temp array
        List<String> ids = new ArrayList<>();
        List<String> returnOrderIds = new ArrayList<>();
        List<Integer> bcOrderIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<String> refundStatues = new ArrayList<>();
        List<String> returnBranchIds = new ArrayList<>();
        List<Boolean> restocks = new ArrayList<>();
        List<Long> refundAmounts = new ArrayList<>();
        List<Long> totalRefunds = new ArrayList<>();

        // get total return orders
        int totalOfReturnOrders = Integer.parseInt(response.getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfReturnOrders / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllReturnOrderResponse(pageIndex, searchKeyword)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jsonPath.getList("id"));
            returnOrderIds.addAll(jsonPath.getList("returnOrderId"));
            bcOrderIds.addAll(jsonPath.getList("bcOrderId"));
            customerIds.addAll(jsonPath.getList("customerId"));
            statues.addAll(jsonPath.getList("status"));
            refundStatues.addAll(jsonPath.getList("refundStatus"));
            returnBranchIds.addAll(jsonPath.getList("returnBranchId"));
            restocks.addAll(jsonPath.getList("restock"));
            refundAmounts.addAll(jsonPath.getList("refundAmount"));
            totalRefunds.addAll(jsonPath.getList("totalRefund"));
        }

        // get all return order info
        info.setIds(ids.stream().map(Integer::parseInt).toList());
        info.setReturnOrderIds(returnOrderIds);
        info.setBcOrderIds(bcOrderIds);
        info.setCustomerIds(customerIds);
        info.setStatues(statues.stream().map(ReturnOrderStatus::valueOf).toList());
        info.setRefundStatues(refundStatues.stream().map(APIAllReturnOrder.RefundStatus::valueOf).toList());
        info.setReturnBranchIds(returnBranchIds.stream().mapToInt(Integer::parseInt).boxed().toList());
        info.setRestocks(restocks);
        info.setRefundAmounts(refundAmounts);
        info.setTotalRefunds(totalRefunds);

        // return model
        return info;
    }

    public List<Integer> getListReturnOrderIdAfterFilterByAssignedBranch(List<Integer> assignedBranchIds, String... searchKeyword) {
        AllReturnOrderInformation info = getAllReturnOrdersInformation(searchKeyword);
        List<Integer> ids = new ArrayList<>(info.getIds());
        List<Integer> returnBranchIds = new ArrayList<>(info.getReturnBranchIds());

        return ids.stream()
                .filter(id -> assignedBranchIds.contains(returnBranchIds.get(ids.indexOf(id))))
                .toList();
    }

    public int getReturnOrderIdForViewDetail(List<Integer> assignedBranchIds, String... searchKeyword) {
        AllReturnOrderInformation info = getAllReturnOrdersInformation(searchKeyword);
        List<Integer> ids = new ArrayList<>(info.getIds());
        List<Integer> returnBranchIds = new ArrayList<>(info.getReturnBranchIds());

        return ids.stream()
                .filter(id -> assignedBranchIds.contains(returnBranchIds.get(ids.indexOf(id))))
                .findFirst()
                .orElse(0);
    }

    public int getReturnOrderIdForEdit(List<Integer> assignedBranchIds, String... searchKeyword) {
        AllReturnOrderInformation info = getAllReturnOrdersInformation(searchKeyword);
        List<Integer> ids = new ArrayList<>(info.getIds());
        List<Integer> returnBranchIds = new ArrayList<>(info.getReturnBranchIds());
        List<ReturnOrderStatus> statuses = new ArrayList<>(info.getStatues());
        List<Boolean> restocks = new ArrayList<>(info.getRestocks());

        return ids.stream()
                .filter(id -> assignedBranchIds.contains(returnBranchIds.get(ids.indexOf(id)))
                        && Objects.equals(statuses.get(ids.indexOf(id)), IN_PROGRESS)
                        && !restocks.get(ids.indexOf(id)))
                .findFirst()
                .orElse(0);
    }

    public int getReturnOrderIdForCompleted(List<Integer> assignedBranchIds, String... searchKeyword) {
        AllReturnOrderInformation info = getAllReturnOrdersInformation(searchKeyword);
        List<Integer> ids = new ArrayList<>(info.getIds());
        List<Integer> returnBranchIds = new ArrayList<>(info.getReturnBranchIds());
        List<ReturnOrderStatus> statuses = new ArrayList<>(info.getStatues());

        return ids.stream()
                .filter(id -> assignedBranchIds.contains(returnBranchIds.get(ids.indexOf(id)))
                        && Objects.equals(statuses.get(ids.indexOf(id)), IN_PROGRESS))
                .findFirst()
                .orElse(0);
    }

    public int getReturnOrderIdForCancel(List<Integer> assignedBranchIds, String... searchKeyword) {
        AllReturnOrderInformation info = getAllReturnOrdersInformation(searchKeyword);
        List<Integer> ids = new ArrayList<>(info.getIds());
        List<Integer> returnBranchIds = new ArrayList<>(info.getReturnBranchIds());
        List<ReturnOrderStatus> statuses = new ArrayList<>(info.getStatues());

        return ids.stream()
                .filter(id -> assignedBranchIds.contains(returnBranchIds.get(ids.indexOf(id)))
                        && Objects.equals(statuses.get(ids.indexOf(id)), IN_PROGRESS))
                .findFirst()
                .orElse(0);
    }

    public int getReturnOrderIdForConfirmPayment(List<Integer> assignedBranchIds, String... searchKeyword) {
        AllReturnOrderInformation info = getAllReturnOrdersInformation(searchKeyword);
        List<Integer> ids = new ArrayList<>(info.getIds());
        List<Integer> returnBranchIds = new ArrayList<>(info.getReturnBranchIds());
        List<ReturnOrderStatus> statuses = new ArrayList<>(info.getStatues());
        List<Boolean> restocks = new ArrayList<>(info.getRestocks());
        List<Long> refundAmounts = new ArrayList<>(info.getRefundAmounts());
        List<Long> totalRefunds = new ArrayList<>(info.getTotalRefunds());

        return ids.stream()
                .filter(id -> assignedBranchIds.contains(returnBranchIds.get(ids.indexOf(id)))
                        && (Objects.equals(statuses.get(ids.indexOf(id)), COMPLETED) || restocks.get(ids.indexOf(id)))
                        && (!Objects.equals(totalRefunds.get(ids.indexOf(id)), refundAmounts.get(ids.indexOf(id)))))
                .findFirst()
                .orElse(0);
    }
}