package api.Seller.orders.order_management;

import api.Seller.login.Login;
import api.Seller.orders.delivery.APIPartialDeliveryOrders;
import api.Seller.orders.return_order.APIGetListReturnOrderByOrderId;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

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
        CANCELLED, CANCEL_COMPLETED, CANCEL_PENDING, CANCEL_REJECTED, COMPLETED, DELIVERED, FAILED, IN_CANCEL, PARTIALLY_SHIPPING, PENDING, PICKED, REJECTED, RETURNED, SHIPPED, TO_SHIP, UNKNOWN, WAITING_FOR_PICKUP, IN_DELIVERY
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
        giaohangnhanh, giaohangtietkiem, ahamove_bike, selfdelivery, ahamove_truck, ahamove
    }

    enum PayType {
        PAID, UNPAID, PARTIAL
    }

    enum InStore {
        GO_SOCIAL, FALSE, INSTORE_PURCHASE, PARTNER_DROP_SHIP, LANDING_PAGE

    }

    String getAllOrderPath = "/beehiveservices/api/orders/gosell-store/v2/%s?page=%s&size=100&%s&channel=%s&status=%s&view=COMPACT";
    OrderStatus orderStatus;

    Response getAllOrderResponse(int pageIndex, String branchQuery, Channel channel) {
        String status = Optional.ofNullable(orderStatus).map(Enum::toString).orElse("");
        return api.get(getAllOrderPath.formatted(loginInfo.getStoreID(), pageIndex, branchQuery, channel, status), loginInfo.getAccessToken());
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

        List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numberOfPages)
                .parallel()
                .mapToObj((int pageIndex) -> getAllOrderResponse(pageIndex, branchQuery, channel))
                .map(ResponseBodyExtractionOptions::jsonPath)
                .toList();

        // get other page data
        for (JsonPath jsonPath : jsonPaths) {
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
                              && Optional.ofNullable(madeBy.get(ids.indexOf(id))).isPresent()
                              && madeBy.get(ids.indexOf(id)).equals(staffName))
                .boxed()
                .toList();
    }

    public long getOrderIdForViewDetail(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        return ids.stream()
                .mapToLong(id -> id).filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id))))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForConfirmOrder(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        return ids.stream()
                .mapToLong(id -> id).filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                                                  && Objects.equals(orderStatuses.get(ids.indexOf(id)), TO_SHIP))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForEditOrder(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<PaymentMethod> paymentMethods = new ArrayList<>(info.getPaymentMethods());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        return ids.stream()
                .mapToLong(id -> id).filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                                                  && Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                                                  && Objects.equals(orderStatuses.get(ids.indexOf(id)), TO_SHIP)
                                                  && !Objects.equals(paymentMethods.get(ids.indexOf(id)), ONLINE_BANKING)
                                                  && !Objects.equals(paymentMethods.get(ids.indexOf(id)), CREDIT_DEBIT_CARD)
                                                  && !Objects.equals(paymentMethods.get(ids.indexOf(id)), MPOS))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForCancelOrder(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                              && (Objects.equals(orderStatuses.get(ids.indexOf(id)), TO_SHIP)
                                  || (Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                                      && (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW))
                                      && !Objects.equals(orderStatuses.get(ids.indexOf(id)), CANCELLED)
                                      && !Objects.equals(orderStatuses.get(ids.indexOf(id)), DELIVERED)
                                      && !Objects.equals(orderStatuses.get(ids.indexOf(id)), FAILED)
                                      && !Objects.equals(orderStatuses.get(ids.indexOf(id)), COMPLETED)
                                      && !Objects.equals(orderStatuses.get(ids.indexOf(id)), RETURNED)
                                      && !Objects.equals(orderStatuses.get(ids.indexOf(id)), PENDING))))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForDeliveredOrder(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> (assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                               && Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                               && Objects.equals(orderStatuses.get(ids.indexOf(id)), SHIPPED)))
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForAddShipmentPackage(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());
        APIPartialDeliveryOrders apiPartialDeliveryOrders = new APIPartialDeliveryOrders(loginInformation);
        return ids.parallelStream()
                .mapToLong(id -> id)
                .filter(id -> (assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                               && Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                               && Objects.equals(orderStatuses.get(ids.indexOf(id)), SHIPPED))
                              && apiPartialDeliveryOrders.getPartialDeliveryWithAvailableItemResponse(id).getStatusCode() == 200)
                .findAny()
                .orElse(0L);
    }

    public long getOrderIdForRemoveTagsFromOrder(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderTags> orderTags = new ArrayList<>(info.getOrderTags());

        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                              && !orderTags.get(ids.indexOf(id)).getTagIds().isEmpty())
                .findFirst()
                .orElse(0L);
    }

    public long getOrderIdForConfirmPayment(Channel channel, List<Integer> assignedBranchIds) {
        AllOrdersInformation info = getAllOrderInformation(channel);
        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<OrderStatus> orderStatuses = new ArrayList<>(info.getStatues());
        List<PaymentMethod> paymentMethods = new ArrayList<>(info.getPaymentMethods());
        List<InStore> inStores = new ArrayList<>(info.getInStores());
        List<PayType> payTypes = new ArrayList<>(info.getPayTypes());

        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> (assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                               && isShowConfirmPayment(orderStatuses.get(ids.indexOf(id)),
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
        return !(Objects.equals(inStore, InStore.GO_SOCIAL) || // ignore GoSocial order
                 paymentMethodList.contains(paymentMethod) ||
                 (Objects.equals(status, DELIVERED) || Objects.equals(status, RETURNED)) && Objects.equals(payType, PAID) || // order with payType PAID and status in (DELIVERED, RETURNED) or
                 Objects.equals(payType, UNPAID) && (Objects.equals(status, CANCELLED)) || // or order with payType UNPAID and paymentMethod in (COD, BANK_TRANSFER, MOMO, PAYPAL, ONLINE_BANKING, CREDIT_DEBIT_CARD) or status CANCELLED
                 Objects.equals(paymentMethod, DEBT) && (Objects.equals(status, DELIVERED) || Objects.equals(status, CANCELLED))); // or order with paymentMethod DEBT and status in (DELIVERED, CANCELLED) are not count debt
    }

    public boolean isPrintOrder() {
        String getPrintSettingPath = "/storeservice/api/store-settings/store/%s";
        return api.get(getPrintSettingPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getBoolean("isPrintOrders");
    }

    public long getOrderIdForReturnOrder(List<Integer> assignedBranchIds) {
        orderStatus = DELIVERED;
        AllOrdersInformation info = getAllOrderInformation(GOSELL);

        // init temp arr
        List<Long> ids = new ArrayList<>(info.getIds());
        List<Integer> branchIds = new ArrayList<>(info.getBranchIds());
        List<ShippingMethod> shippingMethods = new ArrayList<>(info.getShippingMethods());

        return ids.stream()
                .mapToLong(id -> id)
                .filter(id -> assignedBranchIds.contains(branchIds.get(ids.indexOf(id)))
                              && Objects.equals(shippingMethods.get(ids.indexOf(id)), selfdelivery)
                              && canReturnOrder(id))
                .findFirst()
                .orElse(0L);
    }

    boolean canReturnOrder(long orderId) {
        int totalQuantityInOrder = new ArrayList<>(Optional.ofNullable(new APIOrderDetail(loginInformation).getOrderInformation(orderId).getItemQuantity()).orElse(List.of())).stream().mapToInt(q -> q).sum();
        int totalQuantityInReturn = new APIGetListReturnOrderByOrderId(loginInformation).getItemReturnInformation(orderId).getQuantity().stream().mapToInt(q -> q).sum();

        return totalQuantityInOrder > totalQuantityInReturn;
    }
}
