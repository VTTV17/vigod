package utilities.model.dashboard.marketing.loyaltyPoint;

import lombok.Data;

@Data
public class LoyaltyPointInfo {
//    int storeId;
//    boolean showPoint;
//    boolean refered;
//    int ratePoint;
//    long rateAmount;
//    boolean purchased;
//    String lastModifiedDate;
//    String lastModifiedBy;
//    boolean introduced;
//    int id;
//    int expirySince;
//    int exchangePoint;
//    long exchangeAmount;
//    boolean enabled;
//    boolean enableExpiryDate;
//    String createdDate;
//    String createdBy;
//    boolean checkout;

    //
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private int id;
    private int storeId;
    private boolean enabled;
    private boolean showPoint;
    private boolean purchased;
    private int ratePoint;
    private long rateAmount;
    private boolean refered;
    private boolean introduced;
    private boolean checkouted;
    private int exchangePoint;
    private long exchangeAmount;
    private boolean enableExpiryDate;
    private int expirySince;

}
