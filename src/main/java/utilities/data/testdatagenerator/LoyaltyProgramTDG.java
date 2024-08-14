package utilities.data.testdatagenerator;

import utilities.data.DataGenerator;
import utilities.model.dashboard.marketing.loyaltyProgram.LoyaltyProgramInfo;

public class LoyaltyProgramTDG {
    public static LoyaltyProgramInfo generateLoyaltyProgram(){
        LoyaltyProgramInfo membershipInfo = new LoyaltyProgramInfo();
        String random = "Membership " + new DataGenerator().generateString(5);
        membershipInfo.setName(random);
        membershipInfo.setDescription("Membership Description" + random);
        membershipInfo.setPriority(1);
        membershipInfo.setEnabledBenefit(true);
        membershipInfo.setDiscountPercent(20);
        membershipInfo.setDiscountMaxAmount(Double.valueOf(50000));
        return membershipInfo;
    }

}
