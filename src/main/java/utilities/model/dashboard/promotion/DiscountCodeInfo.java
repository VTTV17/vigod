package utilities.model.dashboard.promotion;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DiscountCodeInfo {
    private String couponCode;
    private String couponType;
    private Long couponValue;
    private Boolean couponLimitedUsage;
    private Integer couponTotal;
    private Integer couponUsed;
    private Boolean couponLimitToOne;
    private String freeShippingProviders;
    private Boolean enabledRewards;
    private String rewardsDescription;
    private boolean noneRequired;
    private Integer minQuantity;
    private Long minTotal;
    private List<String> platform;
    private Map<String, List<String>> discountCodeStatus;
}
