package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class OrderDetailInfo {
    private OrderInfo orderInfo;
    private CustomerOrderInfo customerInfo;
    private BillingInfo billingInfo;
    private ShippingInfo shippingInfo;
    private StoreBranch storeBranch;
    private List<Item> items;
    private List<Object> orderTagInfos;
    private List<SummaryDiscount> summaryDiscounts;
    private List<Object> couponDiscounts;
    private List<Object> allCouponDiscounts;
    private int totalSummaryDiscounts;
    private int totalSummaryDiscountWithoutFreeshippingAndPoint;
    private Boolean isNewOrder;
    private EarningPoint earningPoint;
    private BcOrderGroup bcOrderGroup;
    private Boolean isMPOSRefund;
    private Boolean newOrder;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class SummaryDiscount {
    private double value;
    private String discountType;
    private String label;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class OrderInfo {
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
    private int totalTaxAmount;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class DeliveryOrder {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private int weight;
    private double originalFee;
    private double fee;
    private double feeDeduction;
    private String currency;
    private boolean freeShipping;
    private String status;
    private String statusUpdatedDate;
    private DeliveryInfo deliveryInfo;
    private PickUpAddress pickUpAddress;
    private int deliveryServiceId;
    private String providerName;
    private String serviceName;
    private int bcOrderId;
    private String shippingServiceNameVi;
    private String shippingServiceNameEn;
    private int length;
    private int width;
    private int height;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class DeliveryInfo {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private String contactName;
    private String phoneNumber;
    private String email;
    private String countryCode;
    private String locationCode;
    private String districtCode;
    private String address;
    private String address2;
    private String wardCode;
    private String city;
    private String zipCode;
    private String phoneCode;
    private Object geoLocation;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class PickUpAddress {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private String contactName;
    private String phoneNumber;
    private String email;
    private String countryCode;
    private String locationCode;
    private String districtCode;
    private String address;
    private String address2;
    private String wardCode;
    private String city;
    private String zipCode;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class CouponCode {
    private double discountValue;
    private String discountCode;
    private String couponType;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class DirectDiscount{
    private String discountType;
    private Double discountValue;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class CustomerOrderInfo {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private int userId;
    private double debtAmount;
    private Boolean guest;
    private Avatar avatar;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Avatar {
    private int imageId;
    private String imageUUID;
    private String urlPrefix;
    private String extension;
    private String fullUrl;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class BillingInfo {
    private String contactName;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String phone;
    private String phone2;
    private String country;
    private String countryCode;
    private String district;
    private String ward;
    private String outSideCity;
    private String zipCode;
    private String phoneCode;
    private String email;
    private String insideCityCode;
    private String stateCode;
    private String fullAddress;
    private String fullAddressEn;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class ShippingInfo {
    private String contactName;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String phone;
    private String phone2;
    private String country;
    private String countryCode;
    private String district;
    private String ward;
    private String outSideCity;
    private String zipCode;
    private String phoneCode;
    private String email;
    private String insideCityCode;
    private String stateCode;
    private String fullAddress;
    private String fullAddressEn;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class StoreBranch {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private String name;
    private int storeId;
    private String code;
    private String address;
    private String ward;
    private String district;
    private String city;
    private String phoneNumberFirst;
    @JsonSetter("default")
    private Boolean default1;
    private Boolean isDefault;
    private String branchStatus;
    private String branchType;
    private String address2;
    private String countryCode;
    private String cityName;
    private String zipCode;
    private Boolean defaultBranch;
    private Boolean status;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Item {
    private int id;
    private int itemId;
    private String name;
    private double price;
    private double totalAmount;
    private String currency;
    private String imageUrl;
    private int quantity;
    private double weight;
    private double height;
    private double length;
    private double width;
    private String createdDate;
    private Boolean isDeposit;
    private Boolean flashSale;
    private String inventoryManageType;
    private List<Object> orderItemIMEIs;
    private Boolean isHasLot;
    private Boolean isHasLocation;
    private Boolean isOrderCreatedBeforeItemEnabledLotDate;
    private List<Object> lstReturnedImei;
    private double totalDiscount;
    private double priceDiscount;
    private int totalQuantity;
    private List<ItemTotalDiscount> itemTotalDiscounts;
    private Boolean deposit;
    private Boolean hasLot;
    private Boolean hasLocation;
    private String conversionUnitName;
    private int conversionUnitItemId;
    private String parentId;
    private int variationId;
    private String variationName;
    private LotLocation lotLocation;
}
@Data
class ItemTotalDiscount {
    private Double value;
    private String discountType;
    private String label;
    private int referenceId;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class LotLocation {
    private String itemName;
    private int lotDateId;
    private int quantity;
    private int itemId;
    private String itemImage;
    private boolean hasLot;
    private List<Lot> lots;
    private List<Location> locations;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Lot {
    private int id;
    private int storeId;
    private String lotName;
    private String lotCode;
    private String manufactureDate;
    private String expiryDate;
    private String expiredInValues;
    private int remainingStock;
    private int remainingExpiryDays;
    private int selectedQuantity;
    private boolean isLotDeleted;
    private int remainingReturnStock;
    private List<Location> locations;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Location {
    private int id;
    private String locationName;
    private String locationCode;
    private String locationPath;
    private String locationPathName;
    private int quantity;
    private int selectedQuantity;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class EarningPoint {
    private int id;
    private double value;
    private String event;
    private int storeId;
    private int buyerId;
    private int sourceId;
    private String sourceType;
    private String earnDay;
    private double remainingValue;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class BcOrderGroup {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private Boolean paid;
    private List<Object> orders;
    private List<Integer> orderIds;
    private int orderCount;
    private int itemCount;
    private int totalQuantity;
    private double totalPrice;
    private Boolean withSameProviderShouldReturnTheCheapest;
}