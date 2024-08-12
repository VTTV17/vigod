package utilities.model.dashboard.customer;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Used for deserialization of JSON into POJOs 
 * Example base path: /beehiveservices/api/bc-orders/orders/storeId/127141?page=0&size=50&userId=43737902&customerId=3950154&userIdChannel=
 */

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerOrder {
    public String id;
    public String productNames;
    public String bcOrderGroupId;
    public String channel;
    public Integer storeId;
    public String status;
    public String buyerName;
    public BigDecimal total;
    public BigDecimal subTotal;
    public String currency;
    public String paymentMethod;
    public String note;
    public String createdDate;
    public String createdBy;
    public String updatedDate;
    public Integer itemsCount;
    public String phone;
    public String displayName;
    public String email;
    public String userId;
    public String appInstall;
    public String location;
    public String orderType;
    public List<Item> items;
    public Integer customerId;
    public String customerFullName;
    public String customerCountry;
    public String customerAddress;
    public String customerAddress2;
    public String customerWard;
    public String customerDistrict;
    public String customerCity;
    public String city;
    public String customerState;
    public String customerZipCode;
    public String customerPhone;
    public String customerPhoneBackup;
    public String inStore;
    public Integer branchId;
    public String branchName;
    public String wholesaleId;
    public String discountCode;
    public String discountType;
    public BigDecimal discountAmount;
    public BigDecimal shippingFee;
    public BigDecimal feeDeduction;
    public BigDecimal totalTaxAmount;
    public String shippingMethod;
    public String shippingService;
    public BigDecimal totalCostPrice;
    public BigDecimal debtAmount;
    public Boolean isPaid;
    public BigDecimal receivedAmount;
    public String payType;
    public Boolean hasDebt;
    public String rawPaymentMethod;
    public String fullShippingAddress;
    public String fullShippingAddressEn;
    public String partnerCode;
    public String phoneCode;
    public List<Object> orderTags;
    public String madeBy;
    public String statusUpdatedDate;
    public Integer totalItems;
    public Integer totalItemQty;
    public List<PaymentHistory> paymentHistories;
    public String userName;
}

class Item {
    public Integer id;
    public Integer itemId;
    public String itemModelId;
    public String bcOrderId;
    public String name;
    public BigDecimal price;
    public BigDecimal totalDiscount;
    public Integer quantity;
    public Integer weight;
    public String imageUrl;
    public BigDecimal costPrice;
    public String barcode;
    public BigDecimal orgPrice;
    public List<Object> orderItemIMEIDTO;
    public String sku;
    public String modelId;
    public String modelName;
}

class PaymentHistory {
    public Integer id;
    public String createDate;
    public String paymentMethod;
    public BigDecimal paymentAmount;
    public String paymentReceivedBy;
    public Integer bcOrderId;
    public String note;
}
