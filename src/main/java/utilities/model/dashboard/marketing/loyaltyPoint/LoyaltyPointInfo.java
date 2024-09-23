package utilities.model.dashboard.marketing.loyaltyPoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class
LoyaltyPointInfo {

    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private int id;
    private int storeId;
    private Boolean enabled;    //Use Boolean to get null value if no need update.
    private boolean showPoint;
    private boolean purchased;
    private Integer ratePoint;
    private Long rateAmount;
    private boolean refered;
    private boolean introduced;
    private boolean checkouted;
    private Integer exchangePoint;
    private Long exchangeAmount;
    private boolean enableExpiryDate;
    private Integer expirySince;
    private Integer referrerPoint;
    private Integer refereePoint;

}
