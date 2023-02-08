package api.dashboard.promotion;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.customers.Customers.apiSegmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.setting.BranchManagement.*;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class CreatePromotion {
    String CREATE_FLASH_SALE_PATH = "/itemservice/api/campaigns/";
    String CREATE_PRODUCT_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
    String END_EARLY_FLASH_SALE_PATH = "/itemservice/api/campaigns/end-early/";
    String DELETE_FLASH_SALE_PATH = "/itemservice/api/campaigns/delete/%s?storeId=%s";
    String FLASH_SALE_LIST_PATH = "/itemservice/api/campaigns/search/";
    String WHOLESALE_CAMPAIGN_SCHEDULE_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=SCHEDULED";
    String WHOLESALE_CAMPAIGN_IN_PROGRESS_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=IN_PROGRESS";
    String DELETE_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/";
    API api = new API();
    Logger logger = LogManager.getLogger(CreatePromotion.class);

    // flash sale
    public static List<Integer> apiFlashSalePrice;
    public static List<Integer> apiFlashSalePurchaseLimit;
    public static List<Integer> apiFlashSaleStock;
    public static Map<String, List<String>> apiFlashSaleStatus = new HashMap<>();
    public static Instant apiFlashSaleStartTime;
    public static Instant apiFlashSaleEndTime;

    // product discount campaign
    public static int apiDiscountCampaignStock;
    public static List<Integer> apiDiscountCampaignPrice;
    public static Instant apiDiscountCampaignStartTime;
    public static Instant apiDiscountCampaignEndTime;
    public static int apiProductDiscountCouponValue;
    public static Map<String, List<String>> apiDiscountCampaignStatus;

    // discount code
    public static Instant apiDiscountCodeStartTime;
    public static Instant apiDiscountCodeEndTime;
    public static String apiCouponCode;
    public static Map<String, List<String>> apiDiscountCodeStatus;
    public static boolean apiIsLimitToOne;
    public static boolean apiIsLimitToUsage;
    public static int apiDiscountCodeType;
    public static boolean apiIsEnabledReward;
    public static int apiSegmentConditionType;
    public static int apiAppliesCondtionType;
    public static int apiMinimumRequiredType;
    public static int apiApplicableBranchCondition;
    public static String apiDiscountName;
    public static float apiCouponValue;
    /**
     * set branch condition
     * <p> DEFAULT value = - 1, no condition is provided, random condition should be generated</p>
     * <p> SET value = 0: ALL BRANCH</p>
     * <p> SET value = 1: SPECIFIC BRANCH</p>
     */
    public static int apiProductDiscountCampaignBranchConditionType = -1;

    public CreatePromotion endEarlyFlashSale() {
        // get schedule flash sale list
        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, apiStoreID), accessToken).jsonPath().getList("id");
        logger.debug("schedule flash sale list: %s".formatted(scheduleList));
        if (scheduleList != null)
            scheduleList.forEach(id -> new API().delete(DELETE_FLASH_SALE_PATH.formatted(id, apiStoreID), accessToken).then().statusCode(200));

        // get in progress flash sale
        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, apiStoreID), accessToken).jsonPath().getList("id");
        logger.debug("in-progress flash sale list: %s".formatted(inProgressList));
        if (inProgressList != null)
            inProgressList.forEach(id -> new API().post("%s%s?storeId=%s".formatted(END_EARLY_FLASH_SALE_PATH, id, apiStoreID), accessToken).then().statusCode(200));

        // update flash sale status
        apiBranchName.forEach(brName -> apiFlashSaleStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));


        return this;
    }

    public CreatePromotion createFlashSale(int... time) {
        endEarlyFlashSale();

        // flash sale name
        String flashSaleName = "Auto - Flash sale campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        apiFlashSaleStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        apiFlashSaleEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);
        StringBuilder body = new StringBuilder("""
                {
                    "name": "%s",
                    "startDate": "%s",
                    "endDate": "%s",
                    "items": [""".formatted(flashSaleName, apiFlashSaleStartTime, apiFlashSaleEndTime));
        int num = apiIsVariation ? nextInt(apiVariationList.size()) + 1 : 1;

        // init flash sale purchase limit
        apiFlashSalePurchaseLimit = new ArrayList<>();

        if (apiIsVariation) {
            for (int i = 0; i < num; i++) {
                // var value
                String varValue = apiVariationList.get(i);
                // check in-stock
                if (Collections.max(apiProductStockQuantity.get(varValue)) > 0) {
                    // sale stock
                    apiFlashSaleStock.set(i, nextInt(Collections.max(apiProductStockQuantity.get(varValue))) + 1);

                    // purchase limit
                    apiFlashSalePurchaseLimit.add(nextInt(apiFlashSaleStock.get(i)) + 1);

                    // variation model
                    int modelID = apiVariationModelID.get(i);

                    // variation price
                    apiFlashSalePrice.set(i, nextInt(apiProductSellingPrice.get(i)));

                    String flashSaleProduct = """
                            {
                                        "itemId": "%s",
                                        "limitPurchaseStock": "%s",
                                        "modelId": "%s",
                                        "price": "%s",
                                        "saleStock": "%s"
                                    }
                            """.formatted(apiProductID, apiFlashSalePurchaseLimit.get(i), modelID, apiFlashSalePrice.get(i), apiFlashSaleStock.get(i));
                    body.append(flashSaleProduct);
                    body.append(i == num - 1 ? "" : ",");
                }
            }
        } else {
            // sale stock
            apiFlashSaleStock.set(0, nextInt(Collections.max(apiProductStockQuantity.get(null))) + 1);

            // purchase limit
            apiFlashSalePurchaseLimit.add(nextInt(apiFlashSaleStock.get(0)) + 1);

            // sale price
            apiFlashSalePrice.set(0, nextInt(apiProductSellingPrice.get(0)));


            String flashSaleProduct = """
                    {
                                "itemId": "%s",
                                "limitPurchaseStock": "%s",
                                "price": "%s",
                                "saleStock": "%s"
                            }
                    """.formatted(apiProductID, apiFlashSalePurchaseLimit.get(0), apiFlashSalePrice.get(0), apiFlashSaleStock.get(0));
            body.append(flashSaleProduct);
        }
        body.append("]}");

        // post api create new flash sale campaign
        Response createFlashSale = api.post(CREATE_FLASH_SALE_PATH + apiStoreID, accessToken, String.valueOf(body));

        logger.debug("create flash sale %s".formatted(createFlashSale.asPrettyString()));

        createFlashSale.then().statusCode(200);

        // update flash sale status
        apiBranchName.forEach(brName -> apiFlashSaleStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> i < num ? "SCHEDULE" : "EXPIRED").toList()));

        return this;
    }

    public void endEarlyDiscountCampaign() {
        List<Integer> scheduleList = new API().get(WHOLESALE_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(apiStoreID), accessToken).jsonPath().getList("id");
        for (int campaignID : scheduleList) {
            new API().delete(DELETE_DISCOUNT_PATH + campaignID, accessToken).then().statusCode(200);
        }

        List<Integer> inProgressList = new API().get(WHOLESALE_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(apiStoreID), accessToken).jsonPath().getList("id");
        for (int campaignID : inProgressList) {
            new API().delete(DELETE_DISCOUNT_PATH + campaignID, accessToken).then().statusCode(200);
        }

        //update product discount campaign status
        apiBranchName.forEach(brName -> apiDiscountCampaignStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "EXPIRED").collect(Collectors.toList())));
    }

    public CreatePromotion createProductDiscountCampaign(int... time) {
        // end early discount campaign
        endEarlyDiscountCampaign();

        // campaign name
        String name = "Auto - [Product] Discount campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        apiDiscountCampaignStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        apiDiscountCampaignEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        // coupon type
        // 0: percentage
        // 1: fixed amount
        int productDiscountCouponType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);
        String couponTypeLabel = productDiscountCouponType == 0 ? "PERCENTAGE" : "FIXED_AMOUNT";

        // coupon value
        int minFixAmount = Collections.min(apiProductSellingPrice);
        apiProductDiscountCouponValue = productDiscountCouponType == 0 ? nextInt(MAX_PERCENT_DISCOUNT) + 1 : nextInt(minFixAmount) + 1;

        // set product campaign discount price
        IntStream.range(0, apiProductSellingPrice.size()).forEach(i -> apiDiscountCampaignPrice.set(i, (productDiscountCouponType == 0)
                ? Math.round(apiProductSellingPrice.get(i) * (1 - ((float) apiProductDiscountCouponValue / 100)))
                : (Math.max(0, apiProductSellingPrice.get(i) - apiProductDiscountCouponValue))));

        // init coupon type value
        StringBuilder body = new StringBuilder("""
                {
                    "name": "%s",
                    "storeId": "%s",
                    "discounts": [
                        {
                            "couponCode": "unused_code",
                            "activeDate": "%s",
                            "couponType": "%s",
                            "couponValue": "%s",
                            "expiredDate": "%s",
                            "type": "WHOLE_SALE",
                            "conditions": [""".formatted(name, apiStoreID, apiDiscountCampaignStartTime, couponTypeLabel, apiProductDiscountCouponValue, apiDiscountCampaignEndTime));

        // init segment condition
        // segment type:
        // 0: all customers
        // 1: specific segment
        int segmentConditionType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE);
        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                {
                    "conditionValue": %s
                }""".formatted(apiSegmentID);
        String segmentCondition = """
                {
                    "conditionOption": "%s",
                    "conditionType": "CUSTOMER_SEGMENT",
                    "values": [ %s ]
                },""".formatted(segmentConditionLabel, segmentConditionValue);

        // add segment condition into body
        body.append(segmentCondition);

        // init applies to condition
        // applies to type:
        // 0: all products
        // 1: specific collections
        // 2: specific products
        int appliesToType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLIES_TO_TYPE);
        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ALL_PRODUCTS"
                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
        String appliesToValue = appliesToType == 0 ? "" : """
                {
                    "conditionValue": %s
                }
                """.formatted(appliesToType == 1 ? apiCollectionID : apiProductID);
        String appliesToCondition = """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO",
                    "values": [ %s ]
                },
                """.formatted(appliesToLabel, appliesToValue);
        body.append(appliesToCondition);

        // init minimum requirement
        int min = 1;
        if (apiIsVariation) for (String key : apiProductStockQuantity.keySet())
            min = Math.min(min, Collections.min(apiProductStockQuantity.get(key)));
        else min = Collections.min(apiProductStockQuantity.get(null));
        apiDiscountCampaignStock = nextInt(Math.max(1, min)) + 1;

        String minimumRequirement = """
                {
                    "conditionOption": "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                },""".formatted(apiDiscountCampaignStock);
        body.append(minimumRequirement);

        // init applicable branch
        // if no branch condition is provided, generate random branch condition
        if (apiProductDiscountCampaignBranchConditionType == -1)
            nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
        String applicableCondition;
        String applicableConditionValue = "";
        List<String> productDiscountCampaignApplicableBranch;
        if (apiProductDiscountCampaignBranchConditionType == 0) {
            productDiscountCampaignApplicableBranch = apiBranchName;
            applicableCondition = "APPLIES_TO_BRANCH_ALL_BRANCHES";
        } else {
            List<Integer> activeBranchList = IntStream.range(0, apiBranchID.size()).filter(i -> apiAllBranchStatus.get(i).equals("ACTIVE")).mapToObj(i -> apiBranchID.get(i)).toList();
            int brID = activeBranchList.get(nextInt(activeBranchList.size()));

            productDiscountCampaignApplicableBranch = new ArrayList<>();
            productDiscountCampaignApplicableBranch.add(apiBranchName.get(apiBranchID.indexOf(brID)));

            applicableCondition = "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
            applicableConditionValue = """
                    {
                        "conditionValue": "%s"
                    }""".formatted(brID);
        }
        String applicableBranch = """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO_BRANCH",
                    "values": [ %s ]
                }""".formatted(applicableCondition, applicableConditionValue);
        body.append(applicableBranch);
        body.append("]}]}");

        Response createProductDiscountCampaign = api.post(CREATE_PRODUCT_DISCOUNT_PATH, accessToken, String.valueOf(body));
        createProductDiscountCampaign.then().statusCode(200);

        // debug log
        logger.debug("create product discount campaign");
        logger.debug(createProductDiscountCampaign.asPrettyString());

        // update product discount campaign status
        apiBranchName.forEach(brName -> apiDiscountCampaignStatus
                .put(brName, IntStream.range(0, apiIsVariation ? apiVariationList.size() : 1)
                        .mapToObj(i -> productDiscountCampaignApplicableBranch.contains(brName) ? "SCHEDULE" : "EXPIRED").toList()));
        return this;
    }

    public void createProductDiscountCode(int... time) {
        // coupon name
        String name = "Auto - [Product] Discount code - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        apiDiscountCodeStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        apiDiscountCodeEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        // coupon code
        apiCouponCode = "AUTO" + Instant.now().toEpochMilli();

        // usage limit
//        boolean couponLimitToOne = nextBoolean();
//        boolean couponLimitedUsage = nextBoolean();
        boolean couponLimitToOne = apiIsLimitToOne;
        boolean couponLimitedUsage = apiIsLimitToUsage;
        String couponTotal = couponLimitedUsage ? String.valueOf(nextInt(MAX_COUPON_USED_NUM) + 1) : "null";

        // coupon type
        // 0: percentage
        // 1: fixed amount
        // 2: free shipping
//        int couponType = nextInt(MAX_PRODUCT_CODE_DISCOUNT_TYPE);
        int couponType = apiDiscountCodeType;
        String couponTypeLabel = (couponType == 0) ? "PERCENTAGE" : ((couponType == 1) ? "FIXED_AMOUNT" : "FREE_SHIPPING");
        // coupon value
        int couponValue = (couponType == 0) ? (nextInt(MAX_PERCENT_DISCOUNT) + 1) : ((couponType == 1) ? (nextInt(MAX_FIXED_AMOUNT) + 1) : (nextInt(MAX_FREE_SHIPPING) + 1));
        // free shipping provided
        String freeShippingProviders = (couponType == 2) ? "giaohangnhanh,giaohangtietkiem,ahamove_bike,selfdelivery,ahamove_truck" : "";

        // apply discount code as a reward
//        boolean enabledRewards = nextBoolean();
        boolean enabledRewards = apiIsEnabledReward;

        String rewardsDescription = enabledRewards ? randomAlphabetic(MAX_REWARD_DESCRIPTION_LENGTH) + 1 : "";

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
                            "conditions": [""".formatted(name, apiStoreID, apiDiscountCodeStartTime, apiDiscountCodeEndTime, apiCouponCode, couponLimitToOne, couponLimitedUsage, couponTotal, couponTypeLabel, couponValue, freeShippingProviders, enabledRewards, rewardsDescription));

        // init segment condition
        // segment type:
        // 0: all customers
        // 1: specific segment
//        int segmentConditionType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_SEGMENT_TYPE);
        int segmentConditionType = apiSegmentConditionType;

        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                {
                    "conditionValue": %s
                }""".formatted(apiSegmentID);
        String segmentCondition = """
                {
                    "conditionOption": "%s",
                    "conditionType": "CUSTOMER_SEGMENT",
                    "values": [ %s ]
                },""".formatted(segmentConditionLabel, segmentConditionValue);

        // add segment condition into body
        body.append(segmentCondition);

        // init applies to condition
        // applies to type:
        // 0: all products
        // 1: specific collections
        // 2: specific products
//        int appliesToType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_APPLIES_TO_TYPE);
        int appliesToType = apiAppliesCondtionType;

        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ENTIRE_ORDER"
                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
        String appliesToValue = appliesToType == 0 ? "" : """
                {
                    "conditionValue": %s
                }
                """.formatted(appliesToType == 1 ? apiCollectionID : apiProductID);
        String appliesToCondition = """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO",
                    "values": [ %s ]
                },
                """.formatted(appliesToLabel, appliesToValue);
        body.append(appliesToCondition);

        // init minimum requirement
        // minimum requirement type
        // 0: None
        // 1: Minimum purchase amount (Only satisfied products)
        // 2: Minimum quantity of satisfied products
//        int minimumRequirementType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_MINIMUM_REQUIREMENT_TYPE);
        int minimumRequirementType = apiMinimumRequiredType;

        String minimumRequirementLabel = minimumRequirementType == 0 ? "MIN_REQUIREMENTS_NONE" : (minimumRequirementType == 1) ? "MIN_REQUIREMENTS_PURCHASE_AMOUNT" : "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS";
        int minStock = 1;
        if (apiIsVariation) {
            for (String key : apiProductStockQuantity.keySet()) {
                minStock = Math.min(minStock, Collections.min(apiProductStockQuantity.get(key)));
            }
        } else minStock = Collections.min(apiProductStockQuantity.get(null));
        int minPurchaseAmount = Collections.min(apiProductSellingPrice);
        String minimumRequirement = """
                {
                    "conditionOption": "%s",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                },""".formatted(minimumRequirementLabel, (minimumRequirementType == 1) ? (nextInt(minStock) + 1) : (nextInt(minStock * minPurchaseAmount) + 1));
        body.append(minimumRequirement);

        // init applicable branch
        // 0:APPLIES_TO_BRANCH_ALL_BRANCHES
        //1:APPLIES_TO_BRANCH_SPECIFIC_BRANCH
//        int applicableBranchCondition = nextInt(MAX_PRODUCT_DISCOUNT_CODE_APPLICABLE_BRANCH_TYPE);
        int applicableBranchCondition = apiApplicableBranchCondition;

        String applicableBranchLabel = applicableBranchCondition == 0 ? "APPLIES_TO_BRANCH_ALL_BRANCHES" : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
        String applicableConditionValue = applicableBranchCondition == 0 ? "" : """
                {
                    "conditionValue": "%s"
                }""".formatted(apiBranchID.get(nextInt(apiBranchID.size())));
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

        api.post(CREATE_PRODUCT_DISCOUNT_PATH, accessToken, String.valueOf(body)).then().statusCode(200);
        apiDiscountName = name;
        apiCouponValue = couponValue;
    }
}
