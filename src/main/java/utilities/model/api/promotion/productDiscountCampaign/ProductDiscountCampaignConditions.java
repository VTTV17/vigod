package utilities.model.api.promotion.productDiscountCampaign;

import lombok.Data;

@Data
public class ProductDiscountCampaignConditions {
    private int customerId;
    private Integer segmentConditionType;
    private Integer appliesToType;
    private Integer discountCampaignBranchConditionType;
}
