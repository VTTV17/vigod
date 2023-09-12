package utilities.model.dashboard.promotion;

import lombok.Data;

import java.util.List;

@Data
public class BranchDiscountCampaignInfo {
    private List<Integer> listOfMinimumRequirements;

    private List<String> listOfCouponTypes;
    private List<Long> listOfCouponValues;
}
