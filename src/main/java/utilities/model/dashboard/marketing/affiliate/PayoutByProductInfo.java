package utilities.model.dashboard.marketing.affiliate;

import lombok.Data;

@Data
public class PayoutByProductInfo {
    long approvedAmount;
    int id;
    long paidAmount;
    String partnerCode;
    String partnerName;
    String partnerStatus;
    String partnerType;
    long payableAmount;
    long revenue;
    long totalCommission;
}
