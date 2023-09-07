package utilities.model.api.promotion.productDiscountCode;

import lombok.Data;

@Data
public class ProductDiscountCodeConditions {
    private int customerId;
    private Boolean enableReward;
    private Integer couponType;
    private Boolean couponLimitToOne;
    private Boolean couponLimitedUsage;
    private Integer segmentConditionType;
    private Integer appliesToType;
    private Integer minimumRequirementType;
    private Boolean appliesToApp;
    private Boolean appliesToWeb;
    private Boolean appliesToPOS;
    private Integer discountCodeBranchConditionType;
}
