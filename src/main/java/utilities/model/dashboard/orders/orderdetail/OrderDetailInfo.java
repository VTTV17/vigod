package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private OrderBankInfo orderBankInfo;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class OrderBankInfo{
    private long id;
    private String nameHolder;
    private String accountNumber;
    private String bankId;
    private String bankName;
    private String countryCode;
    private String swiftCode;
    private String routingNumber;
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private long bcOrderId;
}