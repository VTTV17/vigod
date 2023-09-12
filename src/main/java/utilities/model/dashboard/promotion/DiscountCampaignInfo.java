package utilities.model.dashboard.promotion;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DiscountCampaignInfo {
    private String couponType;
    private Long couponValue;
    private Integer discountCampaignMinQuantity;
    private String status;
    private List<String> appliesBranch;
    private List<Long> discountCampaignPrice;
    private Map<String, List<String>> discountCampaignStatus;
}
