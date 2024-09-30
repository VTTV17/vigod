package api.Seller.marketing.membership;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.loyaltyProgram.LoyaltyProgramInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIEditLoyaltyProgram {
    String EDIT_MEMBERSHIP_PATH = "/beehiveservices/api/memberships";
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();
    public APIEditLoyaltyProgram(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public LoyaltyProgramInfo getPayloadAsDefault(int id){
        LoyaltyProgramInfo membershipDetail = new LoyaltyProgram(loginInformation).getMembershipDetail(id);
        return membershipDetail;
    }
    public void editLoyaltyProgram(LoyaltyProgramInfo loyaltyProgramInfo){
        Response response = api.put(EDIT_MEMBERSHIP_PATH,loginInfo.getAccessToken(),loyaltyProgramInfo);
        response.prettyPrint();
        response.then().statusCode(200);
    }
    public void turnOffMembershipBenefits(){
        LoyaltyProgramInfo membershipInfo = new LoyaltyProgram(loginInformation).getRandomMembershipDetail();
        membershipInfo.setEnabledBenefit(false);
        new APIEditLoyaltyProgram(loginInformation).editLoyaltyProgram(membershipInfo);
    }
    public void turnOnMembershipBenefits(){
        LoyaltyProgramInfo membershipInfo = new LoyaltyProgram(loginInformation).getRandomMembershipDetail();
        membershipInfo.setEnabledBenefit(true);
        membershipInfo.setDiscountMaxAmount(5000.0);
        membershipInfo.setDiscountPercent(20);
        new APIEditLoyaltyProgram(loginInformation).editLoyaltyProgram(membershipInfo);
    }
}
