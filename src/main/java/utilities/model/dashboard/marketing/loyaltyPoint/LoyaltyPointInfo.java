package utilities.model.dashboard.marketing.loyaltyPoint;

import lombok.Data;

@Data
public class LoyaltyPointInfo {
    int storeId;
    boolean showPoint;
    boolean refered;
    int ratePoint;
    long rateAmount;
    boolean purchased;
    String lastModifiedDate;
    String lastModifiedBy;
    boolean introduced;
    int id;
    int expirySince;
    int exchangePoint;
    long exchangeAmount;
    boolean enabled;
    boolean enableExpiryDate;
    String createdDate;
    String createdBy;
    boolean checkout;
}
