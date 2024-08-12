package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private List<ItemOrderInfo> items;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class GsOrderBXGYDTO {
    private int itemId;
    private int modelId;
    private int bxgyId;
    private int bcOrderId;
    private double promoAmount;
    private int orderItemId;
    private String createdDate;
    private String lastModifiedDate;
    private String sku;
}