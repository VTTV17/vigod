package api.Seller.orders.order_management;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrderTags.OrderTags;
import api.Seller.products.all_products.APIProductConversionUnit;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.*;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;

import static api.Seller.orders.order_management.APIAllOrderCosts.OrderCosts;
import static api.Seller.orders.order_management.APIAllOrders.*;
import static api.Seller.orders.order_management.APIAllOrders.ShippingMethod.selfdelivery;
import static api.Seller.orders.order_management.APIAllOrders.ShippingMethod.valueOf;

@Slf4j
public class APIOrderDetail {
//    Logger logger = LogManager.getLogger(APIOrderDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIOrderDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class OrderInformation {
        long orderId;
        PaymentMethod paymentMethod;
        int itemsCount;
        int totalQuantity;
        ShippingMethod shippingMethod;
        OrderStatus status;
        OrderTags orderTags;
        OrderCosts orderCosts;
        List<Integer> itemIds;
        List<Integer> itemQuantity;
    }

    String getOrderDetailPath = "/orderservice3/api/gs/order-details/ids/%s?getLoyaltyEarningPoint=true&langKey=vi";
    String getPaymentHistoryPath = "/orderservices2/api/payment-histories/bc-order/%s";

    Response getDetailOfOrderResponse(long orderId) {
        return api.get(getOrderDetailPath.formatted(orderId), loginInfo.getAccessToken(), Map.of("langkey", "vi"));
    }

    public OrderStatus getOrderStatus(int orderId) {
        return OrderStatus.valueOf(getDetailOfOrderResponse(orderId).jsonPath().getString("orderInfo.status"));
    }

    public OrderInformation getOrderInformation(long orderId) {
        OrderInformation info = new OrderInformation();
        Response response = getDetailOfOrderResponse(orderId);

        if (response.statusCode() == 403) return info;
        JsonPath jsonPath = response.jsonPath();

        // get order information
        info.setOrderId(orderId);
        info.setPaymentMethod(PaymentMethod.valueOf(jsonPath.getString("orderInfo.paymentMethod")));
        info.setItemsCount(jsonPath.getInt("orderInfo.itemsCount"));
        info.setTotalQuantity(jsonPath.getInt("orderInfo.totalQuantity"));
        String shippingMethod = jsonPath.getString("orderInfo.deliveryName");
        info.setShippingMethod(Optional.ofNullable(shippingMethod).map(method -> valueOf(shippingMethod)).orElse(selfdelivery));
        info.setStatus(OrderStatus.valueOf(jsonPath.getString("orderInfo.status")));
        info.setOrderTags(new OrderTags(jsonPath.getList("orderTagInfos.tagId"), jsonPath.getList("orderTagInfos.name")));
        info.setOrderCosts((jsonPath.getList("orderInfo.orderCosts.id") != null) ? new OrderCosts(jsonPath.getList("orderInfo.orderCosts.id"), jsonPath.getList("orderInfo.orderCosts.id"), jsonPath.getList("orderInfo.orderCosts.id")) : new OrderCosts());
        info.setItemIds(jsonPath.getList("items.itemId"));
        info.setItemQuantity(jsonPath.getList("items.totalQuantity"));

        // return model
        return info;
    }

    public OrderDetailInfo getOrderDetail(long id) {
        return getDetailOfOrderResponse(id)
                .then()
                .statusCode(200)
                .extract()
                .as(OrderDetailInfo.class);
    }

    public List<PaymentHistoryInfo> getPaymentHistory(long orderId) {
        return Arrays.asList(api.get(getPaymentHistoryPath.formatted(orderId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(PaymentHistoryInfo[].class));
    }

    public void verifyOrderDetailAPI(OrderDetailInfo expectedInfo, long orderId) {
        OrderDetailInfo actualInfo = getOrderDetail(orderId);
        //Verify Customer info
        if (expectedInfo.getCustomerInfo().getName() != null) {
            Assert.assertEquals(actualInfo.getCustomerInfo().getName(), expectedInfo.getCustomerInfo().getName());
            Assert.assertEquals(actualInfo.getCustomerInfo().getPhone(), expectedInfo.getCustomerInfo().getPhone());
            Assert.assertEquals(actualInfo.getCustomerInfo().getDebtAmount(), expectedInfo.getCustomerInfo().getDebtAmount());
        }
        //Verify Shipping Addres
        if (expectedInfo.getShippingInfo().getContactName() != null) {
            Assert.assertEquals(actualInfo.getShippingInfo().getContactName(), expectedInfo.getShippingInfo().getContactName());
            Assert.assertEquals(actualInfo.getShippingInfo().getPhone(), expectedInfo.getShippingInfo().getPhone());
            Assert.assertEquals(actualInfo.getShippingInfo().getFullAddress(), expectedInfo.getShippingInfo().getFullAddress());
        }
        //Verify Billing Address
        if (expectedInfo.getBillingInfo().getContactName() != null) {
            Assert.assertEquals(actualInfo.getBillingInfo().getContactName(), expectedInfo.getBillingInfo().getContactName());
            Assert.assertEquals(actualInfo.getBillingInfo().getPhone(), expectedInfo.getBillingInfo().getPhone());
            Assert.assertEquals(actualInfo.getBillingInfo().getFullAddress(), expectedInfo.getBillingInfo().getFullAddress());
        }
        //Verify payment method
        Assert.assertEquals(actualInfo.getOrderInfo().getPaymentMethod(), expectedInfo.getOrderInfo().getPaymentMethod());
        //Verify earning point
        if (expectedInfo.getEarningPoint().getValue() > 0) {
            Assert.assertEquals(actualInfo.getEarningPoint().getValue(), expectedInfo.getEarningPoint().getValue());
        } else Assert.assertEquals(new EarningPoint(), expectedInfo.getEarningPoint());
        //Verify order summary
        Assert.assertEquals(actualInfo.getOrderInfo().getSubTotal(), expectedInfo.getOrderInfo().getSubTotal());
        Assert.assertEquals(actualInfo.getOrderInfo().getOriginalShippingFee(), expectedInfo.getOrderInfo().getOriginalShippingFee());
        Assert.assertEquals(actualInfo.getOrderInfo().getShippingFee(), expectedInfo.getOrderInfo().getShippingFee());
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalDiscount(), expectedInfo.getOrderInfo().getTotalDiscount());
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalAmount(), expectedInfo.getOrderInfo().getTotalAmount());
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalTaxAmount(), expectedInfo.getOrderInfo().getTotalTaxAmount());
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalPrice(), expectedInfo.getOrderInfo().getTotalPrice());
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalQuantity(), expectedInfo.getOrderInfo().getTotalQuantity());
        Assert.assertEquals(actualInfo.getOrderInfo().getPaymentMethod(), expectedInfo.getOrderInfo().getPaymentMethod());
        Assert.assertEquals(actualInfo.getOrderInfo().getPaid(), expectedInfo.getOrderInfo().getPaid());
        //Verify Discount Summary
        List<SummaryDiscount> actualSummaryDiscount = actualInfo.getSummaryDiscounts();
        List<SummaryDiscount> expectedSummaryDiscount = expectedInfo.getSummaryDiscounts();
        System.out.println("expectedSummaryDiscount: " + expectedSummaryDiscount);
        List<SummaryDiscount> actualSummaryDiscountUpdateModel = new ArrayList<>();
        actualSummaryDiscount.forEach(i -> {
            SummaryDiscount summaryDiscount = new SummaryDiscount();
            summaryDiscount.setLabel(i.getLabel().equals("Giảm giá phí vận chuyển") ? "Giảm phí vận chuyển" : i.getLabel());
            summaryDiscount.setValue(i.getValue());
            actualSummaryDiscountUpdateModel.add(summaryDiscount);
        });
        actualSummaryDiscountUpdateModel.sort(Comparator.comparing(SummaryDiscount::getLabel));
        expectedSummaryDiscount.sort(Comparator.comparing(SummaryDiscount::getLabel));
        Assert.assertEquals(actualSummaryDiscountUpdateModel, expectedSummaryDiscount);

        //Verify item order
        List<ItemOrderInfo> actualItemList = actualInfo.getItems();
        List<ItemOrderInfo> expectedItemList = expectedInfo.getItems();
        List<ItemOrderInfo> actualItemListUpdateModel = new ArrayList<>();
        actualItemList.forEach(i -> {
            ItemOrderInfo itemOrderInfo = new ItemOrderInfo();
            itemOrderInfo.setName(i.getName());
            itemOrderInfo.setVariationName(i.getVariationName());
            if (i.getGsOrderBXGYDTO() != null) {
                itemOrderInfo.setGsOrderBXGYDTO(i.getGsOrderBXGYDTO());
            }
            itemOrderInfo.setPrice(i.getPrice());
            itemOrderInfo.setPriceDiscount(i.getPriceDiscount());
            itemOrderInfo.setTotalAmount(i.getTotalAmount());
            List<ItemTotalDiscount> itemTotalDiscountList = new ArrayList<>();
            i.getItemTotalDiscounts().forEach(j -> {
                ItemTotalDiscount itemTotalDiscount = new ItemTotalDiscount();
                itemTotalDiscount.setLabel(j.getLabel());
                itemTotalDiscount.setValue(j.getValue());
                itemTotalDiscountList.add(itemTotalDiscount);
            });
            itemOrderInfo.setItemTotalDiscounts(itemTotalDiscountList);
            itemOrderInfo.setQuantity(i.getQuantity());
            if (i.getConversionUnitName() != null) {
                itemOrderInfo.setConversionUnitName(i.getConversionUnitName());
            }
            actualItemListUpdateModel.add(itemOrderInfo);
        });
        actualItemListUpdateModel.sort(Comparator.comparing(ItemOrderInfo::getName)
                .thenComparing(ItemOrderInfo::getVariationName)
                .thenComparing(ItemOrderInfo::getConversionUnitName));
        expectedItemList.sort(Comparator.comparing(ItemOrderInfo::getName)
                .thenComparing(ItemOrderInfo::getVariationName)
                .thenComparing(ItemOrderInfo::getConversionUnitName));
        Assert.assertEquals(actualItemListUpdateModel, expectedItemList);
        //Verify branch name
        Assert.assertEquals(actualInfo.getStoreBranch().getName(), expectedInfo.getStoreBranch().getName());
    }


    public Map<String, Long> getOrderItems(OrderDetailInfo info) {
        // Init orderItems items
        // Key: itemId - modelId (Variation), itemId (without variation)
        // Value: sold quantity
        Map<String, Long> orderItems = new HashMap<>();

        // Parse data to map
        info.getItems().forEach(item -> {
            // Get conversion exchange rate
            // If item is not conversion unit, exchangeRate = 1
            int exchangeRate = (item.getConversionUnitItemId() != null) ? new APIProductConversionUnit(loginInformation)
                    .getItemConversionUnit(Integer.parseInt(item.getParentId().split("\\D")[0]))
                    .stream()
                    .filter(unit -> unit.getItemCloneId() == item.getItemId())
                    .map(APIProductConversionUnit.ConversionUnitItem::getQuantity)
                    .findFirst()
                    .orElse(1) : 1;

            // At inventory, quantity only counts on main item, so we must merge it into main
            orderItems.merge(Optional.ofNullable(item.getParentId())
                            .orElse((item.getVariationId() != null)
                                    ? "%s_%s".formatted(item.getItemId(), item.getVariationId())
                                    : "%s".formatted(item.getItemId()))
                            .replaceAll("_", "-"),
                    (long) item.getQuantity() * exchangeRate,
                    Long::sum);
        });

       return orderItems;
    }
}
