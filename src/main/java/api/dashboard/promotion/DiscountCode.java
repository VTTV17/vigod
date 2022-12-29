package api.dashboard.promotion;

import api.dashboard.login.Login;
import api.dashboard.setting.BranchManagement;
import io.restassured.response.Response;
import utilities.api.API;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static api.dashboard.setting.BranchManagement.branchID;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class DiscountCode {
    public void createDiscountCode(String account, String password) {
        int index = 1;
        new Login().loginToDashboardByMail(account, password);
        new BranchManagement().getBranchInformation();
        API api = new API();
        Response segmentList = api.get("/beehiveservices/api/segments/store/%s?page=0&size=100".formatted(storeID), accessToken);
        segmentList.then().statusCode(200);

        Response productList = api.get("/itemservice/api/store/dashboard/%s/items-v2?langKey=vi&page=0&size=100".formatted(storeID), accessToken);
        productList.then().statusCode(200);

        Response collectionList = api.get("/itemservice/api/collections/list/%s?page=0&size=100&itemType=BUSINESS_PRODUCT".formatted(storeID), accessToken);
        collectionList.then().statusCode(200);

        List<Integer> segmentIDList = segmentList.jsonPath().getList("id");

        List<Integer> productIDList = productList.jsonPath().getList("id");

        List<Integer> collectionIDList = collectionList.jsonPath().getList("lstCollection.id");


        String CREATE_PRODUCT_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
        // reward or not
        // 0: not check on set as reward
        // 1: check on set as reward
        for (int rewardId = 0; rewardId < 2; rewardId++) {
            // coupon name
            String name1 = rewardId == 0 ? "_notReward_" : "_isReward_";
            // apply discount code as a reward
            boolean enabledRewards = rewardId != 0;
            String rewardsDescription = enabledRewards ? "REWARD DES" : "";
            // start date
            String activeDate = Instant.now().plus(120, ChronoUnit.MINUTES).toString();
            // end date
            String expiredDate = Instant.now().plus(5, ChronoUnit.DAYS).toString();

            // limitToOne:
            // 0: false
            // 1: true
            for (int limitToOne = 0; limitToOne < 2; limitToOne++) {
                boolean couponLimitToOne = limitToOne != 0;
                String name2 = name1;
                name2 += couponLimitToOne ? "notLimitToOne_" : "isLimitToOne_";
                // limitUsage:
                // 0: false
                // 1: true
                for (int limitUsage = 0; limitUsage < 2; limitUsage++) {
                    String name3 = name2;
                    boolean couponLimitedUsage = limitUsage != 0;
                    name3 += couponLimitedUsage ? "notLimitUsage_" : "isLimitUsage_";
                    // coupon type
                    // 0: percentage
                    // 1: fixed amount
                    // 2: free shipping
                    for (int couponType = 0; couponType < 3; couponType++) {
                        String name4 = name3;
                        name4 += couponType == 0 ? "Percentage_": couponType == 1 ? "Fix Amount_" : "FreeShipping_";

                        // segment type
                        // 0: all customers
                        // 1: specific segment
                        for (int segmentConditionType = 0; segmentConditionType < 2; segmentConditionType++) {
                            String name5 = name4;
                            name5 += segmentConditionType == 0 ? "All customers_" : "Specific segment_";

                            // init applies to condition
                            // applies to type:
                            // 0: all products
                            // 1: specific collections
                            // 2: specific products
                            for (int appliesToType = 0; appliesToType < 3; appliesToType++) {
                                String name6 = name5;
                                name6 += appliesToType == 0 ? "Entire order_" : appliesToType == 1 ? "Specific product collections_" : "Specific products";

                                // init minimum requirement
                                // minimum requirement type
                                // 0: None
                                // 1: Minimum purchase amount (Only satisfied products)
                                // 2: Minimum quantity of satisfied products
                                for (int minimumRequirementType = 0; minimumRequirementType < 3; minimumRequirementType++) {
                                    String name7 = name6;
                                    name7 += minimumRequirementType == 0 ? "No requirement minimum_" : (minimumRequirementType == 1) ? "Minimum purchase amount_" : "Minimum quantity of items_";

                                    // init applicable branch
                                    for (int applicableBranchCondition = 0; applicableBranchCondition < 2; applicableBranchCondition++) {
                                        String name8 = name7;
                                        name8 += applicableBranchCondition == 0 ? "All branches" : "Specific branch";

                                        String couponTotal = couponLimitedUsage ? String.valueOf(nextInt(MAX_COUPON_USED_NUM) + 1) : "null";

                                        // coupon code
                                        String couponCode = "AUTO" + Instant.now().toEpochMilli();

                                        String couponTypeLabel = (couponType == 0) ? "PERCENTAGE" : ((couponType == 1) ? "FIXED_AMOUNT" : "FREE_SHIPPING");

                                        // free shipping provided
                                        String freeShippingProviders = (couponType == 2) ? "giaohangnhanh,giaohangtietkiem,ahamove_bike,selfdelivery,ahamove_truck" : "";

                                        // coupon value
                                        int couponValue = (couponType == 0) ? (nextInt(MAX_PERCENT_DISCOUNT) + 1) : ((couponType == 1) ? (nextInt(MAX_FIXED_AMOUNT) + 1) : (nextInt(MAX_FREE_SHIPPING) + 1));

                                        StringBuilder body = new StringBuilder("""
                                                {
                                                    "name": "%s",
                                                    "storeId": "%s",
                                                    "discounts": [
                                                        {
                                                            "activeDate": "%s",
                                                            "expiredDate": "%s",
                                                            "couponCode": "%s",
                                                            "couponLimitToOne": %s,
                                                            "couponLimitedUsage": %s,
                                                            "couponTotal": %s,
                                                            "couponType": "%s",
                                                            "couponValue": "%s",
                                                            "type": "COUPON",
                                                            "freeShippingProviders": "%s",
                                                            "feeShippingType": "FIXED_AMOUNT",
                                                            "enabledRewards": %s,
                                                            "rewardsDescription": "%s",
                                                            "conditions": [""".formatted(name8, storeID, activeDate, expiredDate, couponCode, couponLimitToOne, couponLimitedUsage, couponTotal, couponTypeLabel, couponValue, freeShippingProviders, enabledRewards, rewardsDescription));
                                        // init segment condition
                                        // segment type:
                                        // 0: all customers
                                        // 1: specific segment
                                        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
                                        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                                                {
                                                    "conditionValue": %s
                                                },
                                                {
                                                    "conditionValue": %s
                                                }""".formatted(segmentIDList.get(0), segmentIDList.get(1));

                                        String segmentCondition = """
                                                {
                                                    "conditionOption": "%s",
                                                    "conditionType": "CUSTOMER_SEGMENT",
                                                    "values": [ %s ]
                                                },""".formatted(segmentConditionLabel, segmentConditionValue);
                                        // add segment condition into body
                                        body.append(segmentCondition);


                                        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ENTIRE_ORDER"
                                                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
                                        String appliesToValue = appliesToType == 0 ? "" : """
                                                {
                                                    "conditionValue": %s
                                                }, {
                                                    "conditionValue": %s
                                                }
                                                """.formatted(appliesToType == 1 ? collectionIDList.get(0) : productIDList.get(0),
                                                appliesToType == 1 ? collectionIDList.get(1) : productIDList.get(1));
                                        String appliesToCondition = """
                                                {
                                                    "conditionOption": "%s",
                                                    "conditionType": "APPLIES_TO",
                                                    "values": [ %s ]
                                                },
                                                """.formatted(appliesToLabel, appliesToValue);
                                        body.append(appliesToCondition);

                                        String minimumRequirementLabel = minimumRequirementType == 0 ? "MIN_REQUIREMENTS_NONE" : (minimumRequirementType == 1) ? "MIN_REQUIREMENTS_PURCHASE_AMOUNT" : "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS";
                                        String minimumRequirement = """
                                                {
                                                    "conditionOption": "%s",
                                                    "conditionType": "MINIMUM_REQUIREMENTS",
                                                    "values": [
                                                        {
                                                            "conditionValue": 1
                                                        }
                                                    ]
                                                },""".formatted(minimumRequirementLabel);
                                        body.append(minimumRequirement);

                                        String applicableBranchLabel = applicableBranchCondition == 0 ? "APPLIES_TO_BRANCH_ALL_BRANCHES" : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
                                        String applicableConditionValue = applicableBranchCondition == 0 ? "" : """
                                                {
                                                    "conditionValue": "%s"
                                                }""".formatted(branchID.get(nextInt(branchID.size())));
                                        String applicableBranch = """
                                                {
                                                    "conditionOption": "%s",
                                                    "conditionType": "APPLIES_TO_BRANCH",
                                                    "values": [ %s ]
                                                },""".formatted(applicableBranchLabel, applicableConditionValue);
                                        body.append(applicableBranch);
                                        // init platform
                                        String platform = """
                                                {
                                                    "conditionOption": "PLATFORMS_APP_WEB_INSTORE",
                                                    "conditionType": "PLATFORMS",
                                                    "values": []
                                                }""";
                                        body.append(platform);
                                        body.append("]}]}");
                                        Response res = new API().post(CREATE_PRODUCT_DISCOUNT_PATH, accessToken, String.valueOf(body));

                                        res.then().statusCode(200);
                                        System.out.println(index);
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
