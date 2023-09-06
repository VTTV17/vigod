package api.dashboard.promotion;

import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.setting.BranchManagement;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.api.promotion.productDiscountCode.ProductDiscountCodeConditions;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class ProductDiscountCode {
    String CREATE_PRODUCT_DISCOUNT_CODE_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
    String END_EARLY_DISCOUNT_CODE_PATH = "/orderservices2/api/gs-discount?id=%s&storeId=%s";
    API api = new API();
    Logger logger = LogManager.getLogger(ProductDiscountCode.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    BranchInfo brInfo;
    ProductInfo productInfo;
    ProductDiscountCodeConditions conditions;


    public ProductDiscountCode(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        brInfo = new BranchManagement(loginInformation).getInfo();
    }

    public void endEarlyDiscount(int discountId) {
        String path = END_EARLY_DISCOUNT_CODE_PATH.formatted(discountId, loginInfo.getStoreID());
        Response response = api.put(path, loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Call api to end early discount.");
    }

    String getSegmentCondition() {
        // segment type:
        // 0: all customers
        // 1: specific segment
        Customers customers = new Customers(loginInformation);
        List<Integer> listSegmentOfCustomer = customers.getListSegmentOfCustomer(conditions.getCustomerId());
        int segmentConditionType = (conditions.getSegmentConditionType() != null) ? conditions.getSegmentConditionType() : (((listSegmentOfCustomer == null) || listSegmentOfCustomer.isEmpty()) ? 0 : 1);

        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                {
                    "conditionValue": %s
                }""".formatted(listSegmentOfCustomer.get(0));
        return """
                {
                    "conditionOption": "%s",
                    "conditionType": "CUSTOMER_SEGMENT",
                    "values": [ %s ]
                }""".formatted(segmentConditionLabel, segmentConditionValue);
    }

    String getAppliesToCondition() {
        // applies to type:
        // 0: all products
        // 1: specific collections
        // 2: specific products
        int appliesToType = (conditions.getAppliesToType() != null) ? conditions.getAppliesToType() : (!productInfo.getCollectionIdList().isEmpty() ? 1 : List.of(0, 2).get(nextInt(2)));
        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ALL_PRODUCTS"
                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
        String appliesToValue = (appliesToType == 0) ? "" : """
                {
                    "conditionValue": %s
                }
                """.formatted((appliesToType == 1) ? productInfo.getCollectionIdList().get(0) : productInfo.getProductID());
        return """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO",
                    "values": [ %s ]
                }
                """.formatted(appliesToLabel, appliesToValue);
    }

    String getMinimumRequirement() {
        // init minimum requirement
        // minimum requirement type
        // 0: None
        // 1: Minimum purchase amount (Only satisfied products)
        // 2: Minimum quantity of satisfied products
        int minimumRequirementType = (conditions.getMinimumRequirementType() != null) ? conditions.getMinimumRequirementType() : nextInt(MAX_PRODUCT_DISCOUNT_CODE_MINIMUM_REQUIREMENT_TYPE);
        String minimumRequirementLabel = minimumRequirementType == 0 ? "MIN_REQUIREMENTS_NONE" : (minimumRequirementType == 1) ? "MIN_REQUIREMENTS_PURCHASE_AMOUNT" : "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS";

        // get list models
        List<String> variationModelList = productInfo.getVariationModelList();

        // init minimum quantity to applies promotion
        int minStock = Collections.min(productInfo.getProductStockQuantityMap().get(variationModelList.get(0)));
        if (productInfo.isHasModel()) for (int index = 1; index < variationModelList.size(); index++)
            minStock = Math.min(Collections.min(productInfo.getProductStockQuantityMap().get(variationModelList.get(index))), minStock);

        // get minimum selling price
        long minPurchaseAmount = Collections.min(productInfo.getProductSellingPrice());

        return """
                {
                    "conditionOption": "%s",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                }""".formatted(minimumRequirementLabel, (minimumRequirementType == 1) ? (nextLong(Math.max(minStock, 1) * minPurchaseAmount) + 1) : (nextInt(Math.max(minStock, 1)) + 1));
    }

    String getPlatformCondition() {
        // get platform
        boolean appliesToApp = conditions.getAppliesToApp() != null ? conditions.getAppliesToApp() : nextBoolean();
        boolean appliesToWeb = conditions.getAppliesToWeb() != null ? conditions.getAppliesToWeb() : nextBoolean();
        boolean appliesToPOS = conditions.getAppliesToPOS() != null ? conditions.getAppliesToPOS() : nextBoolean();

        // need to select at least one platform
        while (!appliesToApp & !appliesToWeb & !appliesToPOS) {
            appliesToApp = nextBoolean();
            appliesToWeb = nextBoolean();
            appliesToPOS = nextBoolean();
        }

        // init platform condition options
        String conditionOption = "PLATFORMS%s%s%s".formatted(appliesToApp ? "_APP" : "", appliesToWeb ? "_WEB" : "", appliesToPOS ? "_INSTORE" : "");

        // in case, add _ONLY when only one platform is selected
        conditionOption = "%s%s".formatted(conditionOption, conditionOption.split("_").length == 2 ? "_ONLY" : "");

        return """
                {
                    "conditionOption": "%s",
                    "conditionType": "PLATFORMS",
                    "values": []
                }""".formatted(conditionOption);
    }

    String getBranchCondition() {
        int discountCampaignBranchConditionType = conditions.getDiscountCampaignBranchConditionType() != null ? conditions.getDiscountCampaignBranchConditionType() : nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
        String applicableCondition = discountCampaignBranchConditionType == 0 ? "APPLIES_TO_BRANCH_ALL_BRANCHES" : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
        String applicableConditionValue = "";
        if (discountCampaignBranchConditionType != 0) {
            List<Integer> activeBranchList = IntStream.range(0, brInfo.getBranchID().size()).filter(i -> brInfo.getAllBranchStatus().get(i).equals("ACTIVE")).mapToObj(i -> brInfo.getBranchID().get(i)).toList();
            int brID = activeBranchList.get(nextInt(activeBranchList.size()));
            applicableConditionValue = """
                    {
                        "conditionValue": "%s"
                    }""".formatted(brID);
        }
        return """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO_BRANCH",
                    "values": [ %s ]
                }""".formatted(applicableCondition, applicableConditionValue);
    }

    String getAllCondition() {
        return """
                [%s, %s, %s, %s, %s]""".formatted(getSegmentCondition(),
                getAppliesToCondition(),
                getMinimumRequirement(),
                getPlatformCondition(),
                getBranchCondition());
    }

    String getDiscountConfig(int... time) {
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        Instant productDiscountCodeStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        Instant productDiscountCodeEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        // coupon code
        String couponCode = "AUTO" + Instant.now().toEpochMilli();

        // usage limit
        boolean couponLimitToOne = conditions.getCouponLimitToOne() != null ? conditions.getCouponLimitToOne() : nextBoolean();
        boolean couponLimitedUsage = conditions.getCouponLimitedUsage() != null ? conditions.getCouponLimitedUsage() : nextBoolean();
        int apiLimitTimesUse = nextInt(MAX_COUPON_USED_NUM) + 1;
        String couponTotal = couponLimitedUsage ? String.valueOf(apiLimitTimesUse) : "null";

        // coupon type
        // 0: percentage
        // 1: fixed amount
        // 2: free shipping
        int couponType = conditions.getCouponType() != null ? conditions.getCouponType() : nextInt(MAX_PRODUCT_CODE_DISCOUNT_TYPE);
        String couponTypeLabel = (couponType == 0) ? "PERCENTAGE" : ((couponType == 1) ? "FIXED_AMOUNT" : "FREE_SHIPPING");
        // coupon value
        int couponValue = (couponType == 0) ? (nextInt(MAX_PERCENT_DISCOUNT) + 1) : ((couponType == 1) ? (nextInt(MAX_FIXED_AMOUNT) + 1) : (nextInt(MAX_FREE_SHIPPING) + 1));

        // free shipping provided
        String freeShippingProviders = "giaohangnhanh,giaohangtietkiem,ahamove_bike,selfdelivery,ahamove_truck";

        // apply discount code as a reward
        boolean enabledRewards = conditions.getEnableReward() != null ? conditions.getEnableReward() : nextBoolean();

        String rewardsDescription = enabledRewards ? "Reward description" : "";
        return """
                [
                     {
                         "activeDate": "%s",
                         "conditions": %s,
                         "couponCode": "%s",
                         "couponLimitToOne": %s,
                         "couponLimitedUsage": %s,
                         "couponTotal": %s,
                         "couponType": "%s",
                         "couponValue": "%s",
                         "expiredDate": "%s",
                         "storeId": "%s",
                         "type": "COUPON",
                         "freeShippingProviders": "%s",
                         "feeShippingType": "FIXED_AMOUNT",
                         "enabledRewards": %s,
                         "rewardsDescription": "%s",
                         "hideInStore": false
                     }
                ]""".formatted(productDiscountCodeStartTime,
                getAllCondition(),
                couponCode,
                couponLimitToOne,
                couponLimitedUsage,
                couponTotal,
                couponTypeLabel,
                couponValue,
                productDiscountCodeEndTime,
                loginInfo.getStoreID(),
                couponType == 2 ? freeShippingProviders : "",
                enabledRewards,
                rewardsDescription);
    }

    String getDiscountCodeBody(int... time) {
        // coupon name
        String name = "Auto - [Product] Discount code - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        return """
                {
                    "name": "%s",
                    "storeId": "%s",
                    "timeCopy": 0,
                    "description": "",
                    "discounts": %s
                }""".formatted(name, loginInfo.getStoreID(), getDiscountConfig(time));
    }

    public void createProductDiscountCode(ProductDiscountCodeConditions conditions, ProductInfo productInfo, int... time) {
        // set product information
        this.productInfo = productInfo;

        // set conditions
        this.conditions = conditions;

        // init product discount code body
        String body = getDiscountCodeBody(time);

        // POST API to create new discount code
        Response response = api.post(CREATE_PRODUCT_DISCOUNT_CODE_PATH, loginInfo.getAccessToken(), body);

        // check discount code is created
        response.then().statusCode(200);
    }
}
