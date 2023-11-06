package utilities.model.api.promotion.productDiscountCode;

import lombok.Data;

import java.util.List;

@Data
public class ProductDiscountCodeConditions {
    private int customerId;
    private Boolean enableReward;
    /** 0: percentage, 1: fixed amount, 2: free shipping */
    private Integer couponType;
    private Boolean couponLimitToOne;
    private Boolean couponLimitedUsage;
    /** 0: all customers, 1: specific segment */
    private Integer segmentConditionType;
    /** 0: all products, 1: specific collections, 2: specific products */
    private Integer appliesToType;
    /** 0: None, 1: Minimum purchase amount (Only satisfied products), 2: Minimum quantity of satisfied products */
    private Integer minimumRequirementType;
    private Boolean appliesToApp;
    private Boolean appliesToWeb;
    private Boolean appliesToPOS;
    /** 0: All branches, 1: Specific branch */
    private Integer discountCodeBranchConditionType;
    /** List.of("VISA", "ATM", "DEBT", "COD", "MOMO", "PAYPAL", "BANK_TRANSFER") */
    private List<String> paymentMethod;
}
