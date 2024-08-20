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
    private Double totalSummaryDiscounts;
    private Double totalSummaryDiscountWithoutFreeshippingAndPoint;
    private Boolean isNewOrder;
    private EarningPoint earningPoint;
    private BcOrderGroup bcOrderGroup;
    private Boolean isMPOSRefund;
    private Boolean newOrder;
}

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class BcOrderGroup {
    private String createdDate;
    private String lastModifiedDate;
    private Long id;
    private Boolean paid;
    private List<Object> orders;
    private List<Integer> orderIds;
    private Long orderCount;
    private Long itemCount;
    private Long totalQuantity;
    private double totalPrice;
    private Boolean withSameProviderShouldReturnTheCheapest;
}
