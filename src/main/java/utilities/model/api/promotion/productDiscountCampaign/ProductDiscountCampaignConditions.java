package utilities.model.api.promotion.productDiscountCampaign;

import lombok.Data;

@Data
public class ProductDiscountCampaignConditions {
    private int customerId;
    /**
     * 0: all customers
     * 1: specific segment
     */
    private Integer segmentConditionType;
    /**
     * 0: all products
     * 1: specific collections
     * 2: specific products
     */
    private Integer appliesToType;
    /**
     * 0: all branches
     * 1: random branch
     */
    private Integer discountCampaignBranchConditionType;
}
