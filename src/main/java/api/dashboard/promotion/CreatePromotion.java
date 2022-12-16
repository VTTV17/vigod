package api.dashboard.promotion;

import api.dashboard.products.CreateProduct;
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

import static api.dashboard.customers.Customers.segmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static api.dashboard.products.CreateProduct.*;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class CreatePromotion {
    String CREATE_FLASH_SALE_PATH = "/itemservice/api/campaigns/";
    String CREATE_PRODUCT_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
    String END_EARLY_FLASH_SALE_PATH = "/itemservice/api/campaigns/end-early/";
    String DELETE_FLASH_SALE_PATH = "/itemservice/api/campaigns/delete/";
    String FLASH_SALE_LIST_PATH = "/itemservice/api/campaigns/search/";
    String WHOLESALE_CAMPAIGN_SCHEDULE_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=SCHEDULED";
    String WHOLESALE_CAMPAIGN_IN_PROGRESS_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=IN_PROGRESS";
    String DELETE_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/";
    API api = new API();
    Logger logger = LogManager.getLogger(CreatePromotion.class);

    // flash sale
    public static List<String> flashSaleVariationList;
    public static List<Integer> flashSaleVariationPrice;
    public static List<Integer> flashSaleVariationPurchaseLimit;
    public static List<Integer> flashSaleVariationStock;
    public static int flashSaleWithoutVariationPrice;
    public static int flashSaleWithoutVariationPurchaseLimit;
    public static int flashSaleWithoutVariationStock;
    public static List<String> flashSaleStatus = new ArrayList<>();
    public static Instant flashSaleStartTime;
    public static Instant flashSaleEndTime;

    // product discount campaign
    public static int productDiscountCampaignStock;
    public static Instant productDiscountCampaignStartTime;
    public static Instant productDiscountCampaignEndTime;
    public static Map<String, List<String>> productDiscountCampaignStatus = new HashMap<>();
    public static int productDiscountCouponType;
    public static int productDiscountCouponValue;

    public static List<String> productDiscountCampaignApplicableBranch;

    /**
     * set branch condition
     * <p> DEFAULT value = - 1, no condition is provided, random condition should be generated</p>
     * <p> SET value = 0: ALL BRANCH</p>
     * <p> SET value = 1: SPECIFIC BRANCH</p>
     */
    public static int productDiscountCampaignBranchConditionType = -1;

    public CreatePromotion() {
        // init flash sale/discount campaign status
        IntStream.range(0, isVariation ? variationList.size() : 1).forEachOrdered(i -> flashSaleStatus.add("EXPIRED"));
        activeBranchName.forEach(branch -> productDiscountCampaignStatus.put(branch, flashSaleStatus));
    }

    public CreatePromotion endEarlyFlashSale() {
        // get schedule flash sale list
        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, storeID), accessToken).jsonPath().getList("id");
        logger.debug("schedule flash sale list: %s".formatted(scheduleList));
        if (scheduleList != null)
            scheduleList.forEach(id -> new API().delete("%s%s?storeId=%s".formatted(DELETE_FLASH_SALE_PATH, id, storeID), accessToken).then().statusCode(200));

        // get in progress flash sale
        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, storeID), accessToken).jsonPath().getList("id");
        logger.debug("in-progress flash sale list: %s".formatted(inProgressList));
        if (inProgressList != null)
            inProgressList.forEach(id -> new API().post("%s%s?storeId=%s".formatted(END_EARLY_FLASH_SALE_PATH, id, storeID), accessToken).then().statusCode(200));

        // update flash sale status
        Collections.fill(flashSaleStatus, "EXPIRED");


        return this;
    }

    public CreatePromotion createFlashSale(int... time) {
        endEarlyFlashSale();

        // flash sale name
        String flashSaleName = "Auto - Flash sale campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        flashSaleStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        flashSaleEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);
        StringBuilder body = new StringBuilder("""
                {
                    "name": "%s",
                    "startDate": "%s",
                    "endDate": "%s",
                    "items": [""".formatted(flashSaleName, flashSaleStartTime, flashSaleEndTime));

        if (isVariation) {
            flashSaleVariationPrice = new ArrayList<>();
            flashSaleVariationList = new ArrayList<>();
            flashSaleVariationStock = new ArrayList<>();
            flashSaleVariationPurchaseLimit = new ArrayList<>();
            int numberOfSaleVariation = nextInt(variationList.size()) + 1;
            for (int i = 0; i < numberOfSaleVariation; i++) {
                // check in-stock
                if (Collections.max(variationStockQuantity.get(variationList.get(i))) > 0) {
                    // sale stock
                    int saleStock = nextInt(Collections.max(variationStockQuantity.get(variationList.get(i)))) + 1;
                    flashSaleVariationStock.add(saleStock);

                    // purchase limit
                    int limitPurchaseStock = nextInt(saleStock) + 1;
                    flashSaleVariationPurchaseLimit.add(limitPurchaseStock);

                    // variation model
                    int modelID = variationModelID.get(i);

                    flashSaleVariationList.add(variationList.get(i));

                    // variation price
                    int price = nextInt(variationSellingPrice.get(i));
                    flashSaleVariationPrice.add(price);

                    String flashSaleProduct = """
                            {
                                        "itemId": "%s",
                                        "limitPurchaseStock": "%s",
                                        "modelId": "%s",
                                        "price": "%s",
                                        "saleStock": "%s"
                                    }
                            """.formatted(productID, limitPurchaseStock, modelID, price, saleStock);
                    body.append(flashSaleProduct);
                    body.append(i == numberOfSaleVariation - 1? "" : ",");
                }
            }

            // variation not in flash sale campaign => set flash sale price = selling price.
            IntStream.range(numberOfSaleVariation, variationList.size()).forEachOrdered(i -> flashSaleVariationPrice.add(variationSellingPrice.get(i)));
        } else {
            // sale stock
            int saleStock = nextInt(Collections.max(withoutVariationStock)) + 1;
            flashSaleWithoutVariationStock = saleStock;

            // purchase limit
            int limitPurchaseStock = nextInt(saleStock) + 1;
            flashSaleWithoutVariationPurchaseLimit = limitPurchaseStock;

            // sale price
            flashSaleWithoutVariationPrice = nextInt(withoutVariationSellingPrice);


            String flashSaleProduct = """
                    {
                                "itemId": "%s",
                                "limitPurchaseStock": "%s",
                                "price": "%s",
                                "saleStock": "%s"
                            }
                    """.formatted(productID, limitPurchaseStock, flashSaleWithoutVariationPrice, saleStock);
            body.append(flashSaleProduct);
        }
        body.append("]}");

        // post api create new flash sale campaign
        Response createFlashSale = api.post(CREATE_FLASH_SALE_PATH + storeID, accessToken, String.valueOf(body));

        logger.debug("create flash sale");
        logger.debug(createFlashSale.asPrettyString());

        createFlashSale.then().statusCode(200);

        // update flash sale status
        if (isVariation)
            IntStream.range(0, flashSaleVariationList.size()).forEach(i -> flashSaleStatus.set(i, "SCHEDULE"));
        else Collections.fill(flashSaleStatus, "SCHEDULE");

        return this;
    }

    public void endEarlyDiscountCampaign() {
        List<Integer> scheduleList = new API().get(WHOLESALE_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(storeID), accessToken).jsonPath().getList("id");
        for (int campaignID : scheduleList) {
            new API().delete(DELETE_DISCOUNT_PATH + campaignID, accessToken).then().statusCode(200);
        }

        List<Integer> inProgressList = new API().get(WHOLESALE_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(storeID), accessToken).jsonPath().getList("id");
        for (int campaignID : inProgressList) {
            new API().delete(DELETE_DISCOUNT_PATH + campaignID, accessToken).then().statusCode(200);
        }

        //update product discount campaign status
        activeBranchName.forEach(branch -> productDiscountCampaignStatus.put(branch, IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> "EXPIRED").collect(Collectors.toList())));
    }

    public CreatePromotion createProductDiscountCampaign(int... time) {
        // end early discount campaign
        endEarlyDiscountCampaign();

        // campaign name
        String name = "Auto - [Product] Discount campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        productDiscountCampaignStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        productDiscountCampaignEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        // coupon type
        // 0: percentage
        // 1: fixed amount
        productDiscountCouponType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);
        String couponTypeLabel = productDiscountCouponType == 0 ? "PERCENTAGE" : "FIXED_AMOUNT";

        // coupon value
        int minFixAmount = isVariation ? Collections.min(variationSellingPrice) : withoutVariationSellingPrice;
        productDiscountCouponValue = productDiscountCouponType == 0 ? nextInt(MAX_PERCENT_DISCOUNT) + 1 : nextInt(minFixAmount) + 1;

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
                            "conditions": [""".formatted(name, storeID, productDiscountCampaignStartTime, couponTypeLabel, productDiscountCouponValue, productDiscountCampaignEndTime));

        // init segment condition
        // segment type:
        // 0: all customers
        // 1: specific segment
        int segmentConditionType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE);
        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                {
                    "conditionValue": %s
                }""".formatted(segmentID);
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
                """.formatted(appliesToType == 1 ? collectionID : productID);
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
        if (isVariation) {
            for (String key : variationStockQuantity.keySet()) {
                min = Math.min(min, Collections.min(variationStockQuantity.get(key)));
            }
        } else min = Collections.min(withoutVariationStock);
        productDiscountCampaignStock = nextInt(Math.max(1, min)) + 1;

        String minimumRequirement = """
                {
                    "conditionOption": "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                },""".formatted(productDiscountCampaignStock);
        body.append(minimumRequirement);

        // init applicable branch
        // if no branch condition is provided, generate random branch condition
        if (productDiscountCampaignBranchConditionType == -1)
            nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
        String applicableCondition;
        String applicableConditionValue = "";
        if (productDiscountCampaignBranchConditionType == 0) {
            productDiscountCampaignApplicableBranch = CreateProduct.activeBranchName;
            applicableCondition = "APPLIES_TO_BRANCH_ALL_BRANCHES";
        } else {
            productDiscountCampaignApplicableBranch = new ArrayList<>();
            int branchID = activeBranchIDList.get(nextInt(activeBranchIDList.size()));
            productDiscountCampaignApplicableBranch.add(CreateProduct.activeBranchName.get(activeBranchIDList.indexOf(branchID)));
            applicableCondition = "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
            applicableConditionValue = """
                    {
                        "conditionValue": "%s"
                    }""".formatted(branchID);
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
        activeBranchName.forEach(branch -> productDiscountCampaignStatus.put(branch, productDiscountCampaignApplicableBranch.contains(branch)
                ? IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> "SCHEDULE").collect(Collectors.toList())
                : IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> "EXPIRED").collect(Collectors.toList())));


        return this;
    }

    public void createProductDiscount() {
        // coupon name
        String name = randomAlphabetic(nextInt(MAX_PRODUCT_DISCOUNT_CODE_NAME_LENGTH));

        // start date
        String activeDate = Instant.now().plus(1, ChronoUnit.MINUTES).toString();

        // end date
        String expiredDate = Instant.now().plus(5, ChronoUnit.MINUTES).toString();

        // coupon code
        String couponCode = randomAlphabetic(nextInt(MAX_PRODUCT_DISCOUNT_CODE_LENGTH - MIN_PRODUCT_DISCOUNT_CODE_LENGTH + 1) + MIN_PRODUCT_DISCOUNT_CODE_LENGTH).toUpperCase();

        // usage limit
        boolean couponLimitToOne = nextBoolean();
        boolean couponLimitedUsage = nextBoolean();
        String couponTotal = couponLimitedUsage ? String.valueOf(nextInt(MAX_COUPON_USED_NUM) + 1) : "null";

        // coupon type
        // 0: percentage
        // 1: fixed amount
        // 2: free shipping
        int couponType = nextInt(MAX_PRODUCT_CODE_DISCOUNT_TYPE);
        String couponTypeLabel = (couponType == 0) ? "PERCENTAGE" : ((couponType == 1) ? "FIXED_AMOUNT" : "FREE_SHIPPING");
        // coupon value
        int couponValue = (couponType == 0) ? (nextInt(MAX_PERCENT_DISCOUNT) + 1) : ((couponType == 1) ? (nextInt(MAX_FIXED_AMOUNT) + 1) : (nextInt(MAX_FREE_SHIPPING) + 1));
        // free shipping provided
        String freeShippingProviders = (couponType == 2) ? "giaohangnhanh,giaohangtietkiem,ahamove_bike,selfdelivery,ahamove_truck" : "";

        // apply discount code as a reward
        boolean enabledRewards = nextBoolean();
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
                            "conditions": [""".formatted(name, storeID, activeDate, expiredDate, couponCode, couponLimitToOne, couponLimitedUsage, couponTotal, couponTypeLabel, couponValue, freeShippingProviders, enabledRewards, rewardsDescription));

        // init segment condition
        // segment type:
        // 0: all customers
        // 1: specific segment
        int segmentConditionType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_SEGMENT_TYPE);
        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                {
                    "conditionValue": %s
                }""".formatted(segmentID);
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
        int appliesToType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_APPLIES_TO_TYPE);
        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ENTIRE_ORDER"
                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
        String appliesToValue = appliesToType == 0 ? "" : """
                {
                    "conditionValue": %s
                }
                """.formatted(appliesToType == 1 ? collectionID : productID);
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
        int minimumRequirementType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_MINIMUM_REQUIREMENT_TYPE);
        String minimumRequirementLabel = minimumRequirementType == 0 ? "MIN_REQUIREMENTS_NONE" : (minimumRequirementType == 1) ? "MIN_REQUIREMENTS_PURCHASE_AMOUNT" : "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS";
        int minStock = 1;
        if (isVariation) {
            for (String key : variationStockQuantity.keySet()) {
                minStock = Math.min(minStock, Collections.min(variationStockQuantity.get(key)));
            }
        } else minStock = Collections.min(withoutVariationStock);
        int minPurchaseAmount = isVariation ? Collections.min(variationSellingPrice) : withoutVariationSellingPrice;
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
        int applicableBranchCondition = nextInt(MAX_PRODUCT_DISCOUNT_CODE_APPLICABLE_BRANCH_TYPE);
        String applicableBranchLabel = applicableBranchCondition == 0 ? "APPLIES_TO_BRANCH_ALL_BRANCHES" : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
        String applicableConditionValue = applicableBranchCondition == 0 ? "" : """
                {
                    "conditionValue": "%s"
                }""".formatted(activeBranchIDList.get(nextInt(activeBranchIDList.size())));
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
    }
}
