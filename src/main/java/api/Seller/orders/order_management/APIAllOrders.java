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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static api.Seller.orders.order_management.APIAllOrderTags.OrderTags;
import static api.Seller.orders.order_management.APIAllOrders.Channel.BEECOW;
import static api.Seller.orders.order_management.APIAllOrders.Channel.GOSELL;
import static api.Seller.orders.order_management.APIAllOrders.InStore.FALSE;
import static api.Seller.orders.order_management.APIAllOrders.OrderStatus.*;
import static api.Seller.orders.order_management.APIAllOrders.PayType.PAID;
import static api.Seller.orders.order_management.APIAllOrders.PayType.UNPAID;
import static api.Seller.orders.order_management.APIAllOrders.PaymentMethod.*;
import static api.Seller.orders.order_management.APIAllOrders.ShippingMethod.selfdelivery;

public class APIAllOrders {
    Logger logger = LogManager.getLogger(APIAllOrders.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAllOrders(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class AllOrdersInformation {
        List<Long> ids = new ArrayList<>();
        List<Integer> bcOrderGroupId = new ArrayList<>();
        List<OrderStatus> statues = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<OrderTags> orderTags = new ArrayList<>();
        List<String> madeBy = new ArrayList<>();
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        List<ShippingMethod> shippingMethods = new ArrayList<>();
        List<InStore> inStores = new ArrayList<>();
        List<PayType> payTypes = new ArrayList<>();
    }

    public enum OrderStatus {
        CANCELLED, CANCEL_COMPLETED, CANCEL_PENDING, CANCEL_REJECTED, COMPLETED, DELIVERED, FAILED, IN_CANCEL, PARTIALLY_SHIPPING, PENDING, PICKED, REJECTED, RETURNED, SHIPPED, TO_CONFIRM, TO_SHIP, UNKNOWN, WAITING_FOR_PICKUP
    }

    public enum Channel {
        GOSELL, BEECOW, SHOPEE, LAZADA, TIKTOK;

        public static List<Channel> getAllOrderChannels() {
            return new ArrayList<>(Arrays.asList(values()));
        }
    }

    enum PaymentMethod {
        COD, CASH, MOMO, DEBT, PAYPAL, BANK_TRANSFER, MPOS, POS, CREDIT_DEBIT_CARD, ONLINE_BANKING, ZALO, OTHER
    }

    enum ShippingMethod {
        giaohangnhanh, giaohangtietkiem, ahamove_bike, selfdelivery, ahamove_truck
    }

    enum PayType {
        PAID, UNPAID, PARTIAL
    }

    enum InStore {
        GO_SOCIAL, FALSE, INSTORE_PURCHASE, PARTNER_DROP_SHIP, LANDING_PAGE

    }

    String getAllOrderPath = "/beehiveservices/api/orders/gosell-store/v2/%s?page=%s&size=100&%s&channel=%s&view=COMPACT";

    Response getAllOrderResponse(int pageIndex, String branchQuery, Channel channel) {
        return api.get(getAllOrderPath.formatted(loginInfo.getStoreID(), pageIndex, branchQuery, channel), loginInfo.getAccessToken());
    }

    public AllOrdersInformation getAllOrderInformation(Channel channel) {
        // get branchQuery
        String branchQuery = "branchIds=%s".formatted(loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", ""));

        // init model
        AllOrdersInformation info = new AllOrdersInformation();

        // init temp array
        List<String> ids = new ArrayList<>();
        List<Integer> bcOrderGroupIds = new ArrayList<>();
        List<String> statues = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Object> orderTags = new ArrayList<>();
        List<String> madeBy = new ArrayList<>();
        List<String> paymentMethods = new ArrayList<>();
        List<String> shippingMethods = new ArrayList<>();
        List<String> inStores = new ArrayList<>();
        List<String> payTypes = new ArrayList<>();

        // get page 0 response
        Response response = getAllOrderResponse(0, branchQuery, channel);

        // check permission
        if (response.getStatusCode() == 403) return info;

        // get total products
        int totalOfOrders = Integer.parseInt(response.getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = Math.min(totalOfOrders / 100, 99);

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllOrderResponse(pageIndex, branchQuery, channel)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jsonPath.getList("response.id"));
            bcOrderGroupIds.addAll(jsonPath.getList("response.bcOrderGroupId"));
            statues.addAll(jsonPath.getList("response.status"));
            customerIds.addAll(jsonPath.getList("response.customerId"));
            branchIds.addAll(jsonPath.getList("response.branchId"));
            orderTags.addAll(jsonPath.getList("response.orderTags"));
            madeBy.addAll(jsonPath.getList("response.madeBy"));
            paymentMethods.addAll(jsonPath.getList("response.paymentMethod"));
            shippingMethods.addAll(jsonPath.getList("response.shippingMethod"));
            inStores.addAll(jsonPath.getList("response.inStore"));
            payTypes.addAll(jsonPath.getList("response.payType"));
        }

        // set suggestion info
        info.setIds(ids.stream().map(Long::parseLong).toList());
        info.setBcOrderGroupId(bcOrderGroupIds);
        info.setStatues(statues.stream().map(OrderStatus::valueOf).toList());
        info.setCustomerIds(customerIds);
        info.setBranchIds(branchIds);
        info.setOrderTags(orderTags.stream().map(tags -> new OrderTags(Pattern.compile("tagId=(\\d+)").matcher(tags.toString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList(),
                Pattern.compile("name=(\\w+)").matcher(tags.toString()).results().map(matchResult -> String.valueOf(matchResult.group(1))).toList())).toList());
        info.setMadeBy(madeBy);
        info.setPaymentMethods(paymentMethods.stream().map(PaymentMethod::valueOf).toList());
        info.setShippingMethods(shippingMethods.stream().map(shippingMethod -> ((shippingMethod == null) || shippingMethod.isEmpty()) ? selfdelivery : ShippingMethod.valueOf(shippingMethod)).toList());
        info.setInStores(inStores.stream().map(inStore -> (inStore == null) ? FALSE : InStore.valueOf(inStore)).toList());
        info.setPayTypes(payTypes.stream().map(payType -> (payType == null) ? PAID : PayType.valueOf(payType)).toList());
        // return model
        return info;
    }

    public List<Long> getOrderIdsAfterFilterByAssignedBranchIds(Channel channel, List<Integer> assignedBranchIds) {
        // get all orders info
        AllOrdersInformation info = getAllOrderInformation(channel);

        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id))))
                .boxed()
                .toList();
    }

    public List<Long> getOrderIdsAfterFilterByAssignedBranchIdsAndCreatedBy(Channel channel, List<Integer> assignedBranchIds, String staffName) {
        // get all orders info
        AllOrdersInformation info = getAllOrderInformation(channel);

        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<String> madeBy = new ArrayList<>(info.getMadeBy());
        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                        && madeBy.get(branchIds.get(ids.indexOf(id))).equals(staffName))
                .boxed()
                .toList();
    }

    public long getOrderIdForViewDetail(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        return info.getIds().isEmpty() ? 0 : info.getIds().get(0);
    }

    public long getOrderIdForConfirmOrder(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        return ids.stream()
                .mapToLong(id -> id).filter(id -> Objects.equals(orderStatuses.get(ids.indexOf(id)), TO_CONFIRM))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForEditOrder(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<PaymentMethod> paymentMethods = new ArrayList<>(info.getPaymentMethods());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        return ids.stream()
                .mapToLong(id -> id).filter(id -> Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                        && Objects.equals(orderStatuses.get(ids.indexOf(id)), TO_CONFIRM)
                        && !Objects.equals(paymentMethods.get(ids.indexOf(id)), ONLINE_BANKING)
                        && !Objects.equals(paymentMethods.get(ids.indexOf(id)), CREDIT_DEBIT_CARD)
                        && !Objects.equals(paymentMethods.get(ids.indexOf(id)), MPOS))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForCancelOrder(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> Objects.equals(orderStatuses.get(ids.indexOf(id)), TO_CONFIRM)
                        || (Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                        && (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW))
                        && !Objects.equals(orderStatuses.get(ids.indexOf(id)), CANCELLED)
                        && !Objects.equals(orderStatuses.get(ids.indexOf(id)), DELIVERED)
                        && !Objects.equals(orderStatuses.get(ids.indexOf(id)), FAILED)
                        && !Objects.equals(orderStatuses.get(ids.indexOf(id)), COMPLETED)
                        && !Objects.equals(orderStatuses.get(ids.indexOf(id)), RETURNED)
                        && !Objects.equals(orderStatuses.get(ids.indexOf(id)), PENDING)))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForDeliveredOrder(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> (Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                        && Objects.equals(orderStatuses.get(ids.indexOf(id)), SHIPPED)))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForAddTagsToOrder(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderTags> orderTags = new ArrayList<>(info.getOrderTags());
        int numberOfTagsInOrder = new APIAllOrderTags(loginInformation).getAllOrderTagsInformation().getTagIds().size();

        return numberOfTagsInOrder == 0 ? 0 : ids.stream()
                .mapToLong(id -> id)
                .filter(id -> numberOfTagsInOrder > orderTags.get(ids.indexOf(id)).getTagIds().size())
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForRemoveTagsFromOrder(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderTags> orderTags = new ArrayList<>(info.getOrderTags());

        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> !orderTags.get(ids.indexOf(id)).getTagIds().isEmpty())
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForConfirmPayment(Channel channel) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<PaymentMethod> paymentMethods = new ArrayList<>(info.getPaymentMethods());
        List<InStore> inStores = new ArrayList<>(info.getInStores());
        List<PayType> payTypes = new ArrayList<>(info.getPayTypes());

        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> (isShowConfirmPayment(orderStatuses.get(ids.indexOf(id)),
                        paymentMethods.get(ids.indexOf(id)),
                        payTypes.get(ids.indexOf(id)),
                        inStores.get(ids.indexOf(id))))
                ).findFirst()
                .orElse(0);
    }


    boolean isShowConfirmPayment(OrderStatus status,
                                 PaymentMethod paymentMethod,
                                 PayType payType,
                                 InStore inStore) {
        List<PaymentMethod> paymentMethodList = List.of(COD, BANK_TRANSFER, MOMO, PAYPAL, ONLINE_BANKING, CREDIT_DEBIT_CARD);
        return (!Objects.equals(inStore, InStore.GO_SOCIAL) && ((Objects.equals(status, DELIVERED)) && !Objects.equals(payType, PAID)))
                || (!Objects.equals(status, DELIVERED) && (Objects.equals(paymentMethod, DEBT) || (!Objects.equals(payType, UNPAID) && !paymentMethodList.contains(paymentMethod))));
    }
}
