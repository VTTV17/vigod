package api.Seller.orders.order_management;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrderTags.OrderTags;
import api.Seller.products.all_products.APIProductConversionUnit;
import api.Seller.setting.StoreInformation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.data.GetDataByRegex;
import utilities.enums.PromotionType;
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
    Logger logger = LogManager.getLogger(APIOrderDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String language = "vi";

    public APIOrderDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public APIOrderDetail(LoginInformation loginInformation, String language) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        this.language = language.substring(0, 2);
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

    String getOrderDetailPath = "/orderservice3/api/gs/order-details/ids/%s?getLoyaltyEarningPoint=true&langKey=%s";
    String getPaymentHistoryPath = "/orderservices2/api/payment-histories/bc-order/%s";

    Response getDetailOfOrderResponse(long orderId) {
        return api.get(getOrderDetailPath.formatted(orderId,language), loginInfo.getAccessToken(), Map.of("langkey", language));
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

    public APIOrderDetail verifyOrderDetailAPI(OrderDetailInfo expectedInfo, long orderId){
        OrderDetailInfo actualInfo = getOrderDetail(orderId);
        //Verify Customer info
        if(expectedInfo.getCustomerInfo().getName() != null){
            Assert.assertEquals(actualInfo.getCustomerInfo().getName().trim(),expectedInfo.getCustomerInfo().getName().trim(),"[Failed] Check customer name.");
            Assert.assertEquals(actualInfo.getCustomerInfo().getPhone(),expectedInfo.getCustomerInfo().getPhone(),"[Failed] Check customer phone.");
            Assert.assertEquals(actualInfo.getCustomerInfo().getDebtAmount(),expectedInfo.getCustomerInfo().getDebtAmount(),"[Failed] Check customer debt amount.");
        }
        //Verify Shipping Address
        if(expectedInfo.getShippingInfo().getContactName()!=null){
            Assert.assertEquals(actualInfo.getShippingInfo().getContactName(),expectedInfo.getShippingInfo().getContactName(),"[Failed] Shipping address - Check contact name.");
            Assert.assertEquals(actualInfo.getShippingInfo().getPhone(),expectedInfo.getShippingInfo().getPhone(),"[Failed] Shipping address - Check contact phone.");
            if(language.equalsIgnoreCase("vi"))
                Assert.assertEquals(actualInfo.getShippingInfo().getFullAddress(),expectedInfo.getShippingInfo().getFullAddress(),"[Failed] Shipping address - Check full address - langKey = vi.");
            else Assert.assertEquals(actualInfo.getShippingInfo().getFullAddressEn(),expectedInfo.getShippingInfo().getFullAddressEn(),"[Failed] Shipping address - Check full address - langKey = en.");
        }
        //Verify Billing Addresss   //4.6 change to billing info (thong tin xuat hoa don)
//        if(expectedInfo.getBillingInfo().getContactName()!=null){
//            Assert.assertEquals(actualInfo.getBillingInfo().getContactName(),expectedInfo.getBillingInfo().getContactName());
//            Assert.assertEquals(actualInfo.getBillingInfo().getPhone(),expectedInfo.getBillingInfo().getPhone());
//            Assert.assertEquals(actualInfo.getBillingInfo().getAddress1(),expectedInfo.getBillingInfo().getAddress1());
//            if(language.equalsIgnoreCase("vi"))
//                Assert.assertEquals(actualInfo.getBillingInfo().getFullAddress(),expectedInfo.getBillingInfo().getFullAddress());
//            else Assert.assertEquals(actualInfo.getBillingInfo().getFullAddressEn(),expectedInfo.getBillingInfo().getFullAddressEn());
//        }
        //Verify payment method
        Assert.assertEquals(actualInfo.getOrderInfo().getPaymentMethod(),expectedInfo.getOrderInfo().getPaymentMethod(), "[Failed] Check payment method.");
        //Verify earning point
        Assert.assertEquals(actualInfo.getEarningPoint().getValue(),expectedInfo.getEarningPoint().getValue(),"[Failed] Check earning point.");
        //Verify order summary
        Assert.assertEquals(actualInfo.getOrderInfo().getSubTotal(),expectedInfo.getOrderInfo().getSubTotal(),"[Failed] Check order info - subtotal.");
        Assert.assertEquals(actualInfo.getOrderInfo().getOriginalShippingFee(),expectedInfo.getOrderInfo().getOriginalShippingFee(),"[Failed] Check order info - original shipping fee.");
        Assert.assertEquals(actualInfo.getOrderInfo().getShippingFee(),expectedInfo.getOrderInfo().getShippingFee(),"[Failed] Check order info - shipping fee.");
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalAmount(),expectedInfo.getOrderInfo().getTotalAmount(),"[Failed] Check order info - total amount.");
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalTaxAmount(),expectedInfo.getOrderInfo().getTotalTaxAmount(),"[Failed] Check order info - total tax amount.");
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalPrice(),expectedInfo.getOrderInfo().getTotalPrice(),"[Failed] Check order info - total price.");
        Assert.assertEquals(actualInfo.getOrderInfo().getTotalQuantity(),expectedInfo.getOrderInfo().getTotalQuantity(),"[Failed] Check order info - total quantity.");
        Assert.assertEquals(actualInfo.getOrderInfo().getPaymentMethod(),expectedInfo.getOrderInfo().getPaymentMethod(), "[Failed] Check order info - payment method.");
        Assert.assertEquals(actualInfo.getOrderInfo().getPaid(),expectedInfo.getOrderInfo().getPaid(), "[Failed] Check order info - is Paid.");
        Assert.assertEquals(actualInfo.getOrderInfo().getPayType(),expectedInfo.getOrderInfo().getPayType(),"[Failed] Check order info - pay type.");
        Assert.assertEquals(actualInfo.getOrderInfo().getUsePoint(),expectedInfo.getOrderInfo().getUsePoint(), "[Failed] Check order info - use point.");
        Assert.assertEquals(actualInfo.getOrderInfo().getStatus(), expectedInfo.getOrderInfo().getStatus(),"[Failed] Check order info - order status.");
        Assert.assertEquals(actualInfo.getOrderInfo().getDebtAmount(), expectedInfo.getOrderInfo().getDebtAmount(), "[Failed] Check order info - debt amount of this order.");
        Assert.assertEquals(actualInfo.getOrderInfo().getReceivedAmount(), expectedInfo.getOrderInfo().getReceivedAmount(),"[Failed] Check order info - receive amount");
        Assert.assertEquals(DataGenerator.getDateByTimeZone(new StoreInformation(loginInformation).getInfo().getTimeZone(),actualInfo.getOrderInfo().getCreateDate())
                ,expectedInfo.getOrderInfo().getCreateDate(),"[Failed] Check order info - created date");
        Assert.assertEquals(actualInfo.getOrderInfo().getCreatedBy(),expectedInfo.getOrderInfo().getCreatedBy()
        ,"[Failed] Check order info - created by.");

        //Verify Discount Summary
        List<SummaryDiscount> actualSummaryDiscount = actualInfo.getSummaryDiscounts();
        List<SummaryDiscount> expectedSummaryDiscount = expectedInfo.getSummaryDiscounts();
        List<SummaryDiscount> actualSummaryDiscountUpdateModel = new ArrayList<>();
        actualSummaryDiscount.forEach(i->{
            SummaryDiscount summaryDiscount  = new SummaryDiscount();
            summaryDiscount.setLabel(i.getLabel().equals("Giảm giá phí vận chuyển")?"Giảm phí vận chuyển":i.getLabel());
            summaryDiscount.setValue(i.getValue());
            actualSummaryDiscountUpdateModel.add(summaryDiscount);
        });
        actualSummaryDiscountUpdateModel.sort(Comparator.comparing(SummaryDiscount::getLabel));
        expectedSummaryDiscount.sort(Comparator.comparing(SummaryDiscount::getLabel));
        Assert.assertEquals(actualSummaryDiscountUpdateModel,expectedSummaryDiscount, "[Failed] Check summary discount.");
        Assert.assertEquals(actualInfo.getTotalSummaryDiscounts(),-expectedInfo.getTotalSummaryDiscounts(),"[Failed] Check total summary discount.");

        //Verify item order
        List<ItemOrderInfo> actualItemList = actualInfo.getItems();
        List<ItemOrderInfo> expectedItemList = expectedInfo.getItems();
        List<ItemOrderInfo> actualItemListUpdateModel = new ArrayList<>();
        actualItemList.forEach(i -> {
            ItemOrderInfo itemOrderInfo = new ItemOrderInfo();
            itemOrderInfo.setName(i.getName());
            itemOrderInfo.setVariationName(i.getVariationName());
            if(i.getGsOrderBXGYDTO()!=null){
                itemOrderInfo.setGsOrderBXGYDTO(i.getGsOrderBXGYDTO());
            }
            itemOrderInfo.setPrice(i.getPrice());
            itemOrderInfo.setPriceDiscount(i.getPriceDiscount());
            itemOrderInfo.setTotalAmount(i.getTotalAmount());

            List<ItemTotalDiscount> itemTotalDiscountList = new ArrayList<>();
            i.getItemTotalDiscounts().forEach(j ->{
                ItemTotalDiscount itemTotalDiscount = new ItemTotalDiscount();
                itemTotalDiscount.setLabel(j.getLabel());
                itemTotalDiscount.setValue(j.getValue());
                itemTotalDiscountList.add(itemTotalDiscount);
            });
            if(!itemTotalDiscountList.isEmpty()) itemTotalDiscountList.sort(Comparator.comparing(ItemTotalDiscount::getLabel));
            itemOrderInfo.setItemTotalDiscounts(itemTotalDiscountList);

            itemOrderInfo.setQuantity(i.getQuantity());
            if(i.getConversionUnitName()!=null){
                itemOrderInfo.setConversionUnitName(i.getConversionUnitName());
            }
            actualItemListUpdateModel.add(itemOrderInfo);
        });
        expectedItemList.forEach(i->i.getItemTotalDiscounts().sort(Comparator.comparing(ItemTotalDiscount::getLabel)));
        actualItemListUpdateModel.sort(Comparator.comparing(ItemOrderInfo::getName)
                .thenComparing(ItemOrderInfo::getVariationName)
                .thenComparing(ItemOrderInfo::getConversionUnitName));
        expectedItemList.sort(Comparator.comparing(ItemOrderInfo::getName)
                .thenComparing(ItemOrderInfo::getVariationName)
                .thenComparing(ItemOrderInfo::getConversionUnitName));

        Assert.assertEquals(actualItemListUpdateModel,expectedItemList, "[Failed] Check order items list.");
        //Verify branch name
        Assert.assertEquals(actualInfo.getStoreBranch().getName(),expectedInfo.getStoreBranch().getName(), "[Failed] Check branch name.");
        logger.info("Verified order detail info after order created.");
        return this;
    }

    public APIOrderDetail verifyPaymentHistoryAfterCreateOrder(long orderId, double receiveAmount){
        List<PaymentHistoryInfo> paymentHistoryInfo = getPaymentHistory(orderId);
        Assert.assertEquals(paymentHistoryInfo.get(0).getPaymentAmount(),receiveAmount);
        return this;
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

    public static double getPromotionValue(OrderDetailInfo orderDetailInfo, PromotionType promotionType) {
        if (orderDetailInfo.getSummaryDiscounts() == null) return 0.0;
        return -orderDetailInfo.getSummaryDiscounts().stream()
                .filter(i -> i.getDiscountType().equals(promotionType.toString()))
                .mapToDouble(SummaryDiscount::getValue).sum();
    }

    /**
     * Retrieves the shipping fee after discount for the given order.
     * <p>
     * This method returns the shipping fee for the order after any discounts have been applied.
     * If the order information or shipping fee is null, it returns 0.0.
     *
     * @param orderDetailInfo The details of the order to retrieve the shipping fee from.
     * @return The shipping fee after discount as a double, or 0.0 if unavailable.
     */
    public static double getShippingFeeAfterDiscount(OrderDetailInfo orderDetailInfo) {
        if (orderDetailInfo.getOrderInfo() == null) return 0.0;
        if (orderDetailInfo.getOrderInfo().getShippingFee() == null) return 0.0;
        return orderDetailInfo.getOrderInfo().getShippingFee();
    }

    /**
     * Calculates the total order cost for the given order.
     * <p>
     * This method sums the amounts of all the order costs associated with the given order.
     * If the order information or the list of order costs is null, it returns 0.0.
     *
     * @param orderDetailInfo The details of the order to retrieve the costs from.
     * @return The total order cost as a double, or 0.0 if unavailable.
     */
    public static double getOrderCost(OrderDetailInfo orderDetailInfo) {
        if (orderDetailInfo.getOrderInfo() == null) return 0.0;
        if (orderDetailInfo.getOrderInfo().getOrderCosts() == null) return 0.0;
        return orderDetailInfo.getOrderInfo().getOrderCosts().stream()
                .mapToDouble(OrderCost::getAmount).sum();
    }
}
