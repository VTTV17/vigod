package utilities.model.dashboard.cashbook;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class CashbookRecord {
    public String createdBy;
    public String createdDate;
    public String lastModifiedBy;
    public String lastModifiedDate;
    public Integer id;
    public Integer storeId;
    public String type;
    public String transactionCode;
    public String groupType;
    public Integer customerId;
    public String customerName;
    public String sourceType;
    public Integer branchId;
    public String branchName;
    public BigDecimal amount;
    public String paymentMethod;
    public String note;
    public Boolean forAccounting;
    public String createdByName;
    public Boolean isAuto;
    public Integer orderId;
    public Boolean isOrderDebt;
    public Integer paymentHistoryId;
    public BigDecimal amountChange;
    public BigDecimal debt;
    public String orderType;
    public String orderCreatedDate;
    public Integer returnOrderId;
    public Integer otherGroupId;
    public String otherGroupName;
    public Integer staffId;
    public String staffName;
    public Integer supplierId;
    public String supplierName;
}