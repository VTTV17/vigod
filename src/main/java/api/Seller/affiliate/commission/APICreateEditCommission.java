package api.Seller.affiliate.commission;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.affiliate.CommissionInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class APICreateEditCommission {
    String CREATE_COMMISSION_PATH = "affiliateservice/api/commissions/";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APICreateEditCommission(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public enum CommissionType{
        SELLING_COMMISSION,
        REVENUE_COMMISSION
    }
    public enum ApplicableProduct{
        ALL_PRODUCTS,
        SPECIFIC_PRODUCTS,
        SPECIFIC_COLLECTIONS
    }
    public enum ApplyTo{
        SELLING_UNIT,
        SELLING_TOTAL
    }
    public enum RateType{
        PERCENTAGE,
        AMOUNT
    }
    public enum ApplicableCondition{
        ALL_CONDITION,
        ANY_CONDITION
    }
    public enum Operator{
        GREATER_THAN_OR_EQUAL,
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        LESS_THAN_OR_EQUAL
    }
    public String getCommisionTypeInBody(int index){
        List<CommissionType> enumValues = new ArrayList<CommissionType>(EnumSet.allOf(CommissionType.class));
        return enumValues.get(index).toString();
    }
    public String getApplicableProductInBody(int index){
        List<ApplicableProduct> enumValues = new ArrayList<ApplicableProduct>(EnumSet.allOf(ApplicableProduct.class));
        return enumValues.get(index).toString();
    }
    public String getApplyToInBody(int index){
        List<ApplyTo> enumValues = new ArrayList<ApplyTo>(EnumSet.allOf(ApplyTo.class));
        return enumValues.get(index).toString();
    }
    public String getRateTypeInBody(int index){
        List<RateType> enumValues = new ArrayList<RateType>(EnumSet.allOf(RateType.class));
        return enumValues.get(index).toString();
    }
    public String getApplicableConditionInBody(int index){
        List<ApplicableCondition> enumValues = new ArrayList<ApplicableCondition>(EnumSet.allOf(ApplicableCondition.class));
        return enumValues.get(index).toString();
    }
    public String getOperatorInBody(int index){
        List<Operator> enumValues = new ArrayList<Operator>(EnumSet.allOf(Operator.class));
        return enumValues.get(index).toString();
    }
    public String getCommissionApplicableLevelInBody(Map<Integer,Integer> map){
        String applicableLevel ="";
        for(Map.Entry mapElement : map.entrySet()){
            int key = (int) mapElement.getKey();
            int value =(int) mapElement.getValue();
            applicableLevel += """
                    {
                                "condition": "%s",
                                "level": "%s",
                                "key": "a0baff94-7f72-49f9-be5b-3772bbbf6274"
                    },""".formatted(getOperatorInBody(key),value);
        }
        applicableLevel= applicableLevel.substring(0,applicableLevel.length()-1);
        return applicableLevel;
    }
    public CommissionInfo createProductCommisionForAll(CommissionInfo commissionInfo){
        String rateType = getRateTypeInBody(commissionInfo.getRateType());
        String rateInfo= """
                   "rate": "%s",
                    "%s": "%s",
                    "rateType": "%s",
                """.formatted(commissionInfo.getRate(),commissionInfo.getRateType()==1?"percent":"amount",commissionInfo.getRate(),rateType);
        String body = """
                {
                    "name": "%s",
                    "type": "ALL_PRODUCTS",
                    "typeCommission": "SELLING_COMMISSION",
                    "items": [],
                    "storeId": "%s",
                    %s
                    "combine": true,
                    "applyTo": "%s",
                    "applicableCondition": "%s",
                    "commissionApplicableLevels": [
                        %s
                    ]
                }
                """.formatted(commissionInfo.getName(),loginInfo.getStoreID(),rateInfo,getApplyToInBody(commissionInfo.getApplyTo()),
                getApplicableConditionInBody(commissionInfo.getApplicableCondition()),getCommissionApplicableLevelInBody(commissionInfo.getCommissionApplicableLevels()));
        Response response = api.post(CREATE_COMMISSION_PATH,loginInfo.getAccessToken(),body);
        response.then().statusCode(201);
        commissionInfo.setCommissionType(response.jsonPath().getInt("id"));
        return commissionInfo;
    }
}
