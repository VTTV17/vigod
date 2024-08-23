package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class OrderInfo {
    private String orderId;
    private String orderNumber;
    private String createDate;
    private String lastModifiedDate;
    private String paymentMethod;
    private int itemsCount;
    private int totalQuantity;
    private Boolean paid;
    private Boolean isAllowEarningPoint;
    private String status;
    private double totalPrice;
    private double totalAmount;
    private double subTotal;
    private double subTotalAfterDiscount;
    private String note;
    private String currency;
    private double totalDiscount;
    private List<Object> discounts;
    private String transactionNumber;
    private double totalTaxAmount;
    private double loyaltyPoint;
    private int pointAmount;
    private double receivedAmount;
    private double debtAmount;
    private String payType;
    private String channel;
    private int refundedAmount;
    private String inStoreCreatedBy;
    private String createdBy;
    private String appInstall;
    private String inStore;
    private Boolean isInStore;
    private DirectDiscount directDiscount;
    private List<CouponCode> couponCodes;
    private String deliveryName;
    private Double shippingFee;
    private Double originalShippingFee;
    private String shippingServiceNameVi;
    private String shippingServiceNameEn;
    private DeliveryOrder deliveryOrder;
    private Double orgTotalPrice;
    private Long usePoint;
}
