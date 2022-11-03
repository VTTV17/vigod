package api.dashboard.promotion;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    String END_EARLY_DISCOUNT_PATH = "/orderservices2/api/gs-discount?id=%s&storeId=%s";
    API api = new API();
    Logger logger = LogManager.getLogger(CreatePromotion.class);

    public static List<String> variationSaleList;
    public static List<Integer> variationSalePrice;
    public static List<Integer> variationPurchaseLimit;
    public static List<Integer> variationSaleStock;
    public static int withoutVariationSalePrice;
    public static int withoutVariationPurchaseLimit;
    public static int withoutVariationSaleStock;
    public static Instant startFlashSaleTime;
    public static Instant startWholesaleCampaignTime;
    public static int productWholesaleCouponType;
    public static int productWholesaleCouponValue;

    public static int productWholesaleCampaignBranchType;
    public static int productWholesaleCampaignBranchID;

    public CreatePromotion endEarlyFlashSale() {
        // get schedule flash sale list
        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, storeID), accessToken).jsonPath().getList("id");
        logger.debug("schedule flash sale list: %s".formatted(scheduleList));
        if (scheduleList.size() > 0) {
            for (Integer id : scheduleList) {
                Response delete = new API().delete("%s%s?storeId=%s".formatted(DELETE_FLASH_SALE_PATH, id, storeID), accessToken);
                logger.debug("delete flash sale id: %s".formatted(id));
                logger.debug(delete.asPrettyString());
            }
        }

        // get in progress flash sale
        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, storeID), accessToken).jsonPath().getList("id");
        logger.debug("in-progress flash sale list: %s".formatted(inProgressList));
        if (inProgressList.size() > 0) {
            for (Integer id : inProgressList) {
                Response endEarly = new API().post("%s%s?storeId=%s".formatted(END_EARLY_FLASH_SALE_PATH, id, storeID), accessToken);
                logger.debug("end early flash sale id: %s".formatted(id));
                logger.debug(endEarly.asPrettyString());
            }
        }

        return this;
    }

    public CreatePromotion createFlashSale(int... time) {
        endEarlyFlashSale();

        // flash sale name
        String flashSaleName = randomAlphabetic(nextInt(MAX_FLASH_SALE_CAMPAIGN_NAME - MIN_FLASH_SALE_CAMPAIGN_NAME + 1) + MIN_FLASH_SALE_CAMPAIGN_NAME);
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        startFlashSaleTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 2 ? time[1] : startMin + nextInt(60);
        Instant endDate = Instant.now().plus(endMin, ChronoUnit.MINUTES);
        StringBuilder body = new StringBuilder("""
                {
                    "name": "%s",
                    "startDate": "%s",
                    "endDate": "%s",
                    "items": [""".formatted(flashSaleName, startFlashSaleTime, endDate));

        if (isVariation) {
            variationSalePrice = new ArrayList<>();
            variationSaleList = new ArrayList<>();
            variationSaleStock = new ArrayList<>();
            variationPurchaseLimit = new ArrayList<>();
            for (int i = 0; i < variationList.size(); i++) {
                // check in-stock
                if (variationStockQuantity.get(i) > 0) {
                    // sale stock
                    int saleStock = nextInt(variationStockQuantity.get(i)) + 1;
                    variationSaleStock.add(saleStock);

                    // purchase limit
                    int limitPurchaseStock = nextInt(saleStock) + 1;
                    variationPurchaseLimit.add(limitPurchaseStock);

                    // variation model
                    int modelID = variationModelID.get(i);

                    variationSaleList.add(variationList.get(i));

                    // variation price
                    int price = nextInt(variationSellingPrice.get(i));
                    variationSalePrice.add(price);

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
                    body.append(i < variationList.size() - 1 ? "," : "");
                }
            }
        } else {
            // sale stock
            int saleStock = nextInt(withoutVariationStock) + 1;
            withoutVariationSaleStock = saleStock;

            // purchase limit
            int limitPurchaseStock = nextInt(saleStock) + 1;
            withoutVariationPurchaseLimit = limitPurchaseStock;

            // sale price
            withoutVariationSalePrice = nextInt(withoutVariationSellingPrice);


            String flashSaleProduct = """
                    {
                                "itemId": "%s",
                                "limitPurchaseStock": "%s",
                                "price": "%s",
                                "saleStock": "%s"
                            }
                    """.formatted(productID, limitPurchaseStock, withoutVariationSalePrice, saleStock);
            body.append(flashSaleProduct);
        }
        body.append("]}");

        // post api create new flash sale campaign
        Response createFlashSale = api.post(CREATE_FLASH_SALE_PATH + storeID, accessToken, String.valueOf(body));

        logger.debug("create flash sale");
        logger.debug(createFlashSale.asPrettyString());

        return this;
    }

    public CreatePromotion endEarlyWholesaleCampaign() {
        List<Integer> scheduleList = new API().get(WHOLESALE_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(storeID), accessToken).jsonPath().getList("id");
        for (int campaignID : scheduleList) {
            new API().delete(DELETE_DISCOUNT_PATH + campaignID, accessToken);
        }

        List<Integer> inProgressList = new API().get(WHOLESALE_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(storeID), accessToken).jsonPath().getList("id");
        for (int campaignID : inProgressList) {
            new API().delete(DELETE_DISCOUNT_PATH + campaignID, accessToken);
        }
        return this;
    }

    public CreatePromotion createProductWholeSaleCampaign(int... time) {
        // campaign name
        String name = randomAlphabetic(nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_NAME) + 1);

        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        startWholesaleCampaignTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 2 ? time[1] : startMin + nextInt(60);
        Instant expiredDate = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        // coupon type
        // 0: percentage
        // 1: fixed amount
        productWholesaleCouponType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);
        String couponTypeLabel = productWholesaleCouponType == 0 ? "PERCENTAGE" : "FIXED_AMOUNT";
        // coupon value
        productWholesaleCouponValue = productWholesaleCouponType == 0 ? nextInt(MAX_PERCENT_DISCOUNT) + 1 : nextInt(MAX_FIXED_AMOUNT) + 1;

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
                            "conditions": [""".formatted(name, storeID, startWholesaleCampaignTime, couponTypeLabel, productWholesaleCouponValue, expiredDate));

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
        int min = isVariation ? Collections.min(variationStockQuantity) : withoutVariationStock;
        int minQuantity = nextInt(min) + 1;

        if (isVariation) {
            variationSaleStock = new ArrayList<>();
            IntStream.range(0, variationList.size()).forEachOrdered(i -> variationSaleStock.add(minQuantity));
        }
        else withoutVariationStock = minQuantity;

        String minimumRequirement = """
                {
                    "conditionOption": "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                },""".formatted(minQuantity);
        body.append(minimumRequirement);

        // init applicable branch
        productWholesaleCampaignBranchType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
        productWholesaleCampaignBranchID = branchIDList.get(nextInt(branchIDList.size()));
        String applicableBranchLabel = productWholesaleCampaignBranchType == 0 ? "APPLIES_TO_BRANCH_ALL_BRANCHES" : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
        String applicableConditionValue = productWholesaleCampaignBranchType == 0 ? "" : """
                {
                    "conditionValue": "%s"
                }""".formatted(productWholesaleCampaignBranchID);
        String applicableBranch = """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO_BRANCH",
                    "values": [ %s ]
                }""".formatted(applicableBranchLabel, applicableConditionValue);
        body.append(applicableBranch);
        body.append("]}]}");

        Response createProductWholesaleCampaign = api.post(CREATE_PRODUCT_DISCOUNT_PATH, accessToken, String.valueOf(body));

        // debug log
        logger.debug("create product wholesale campaign");
        logger.debug(createProductWholesaleCampaign.asPrettyString());
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
        int minStock = isVariation ? Collections.min(variationStockQuantity) : withoutVariationStock;
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
                }""".formatted(branchIDList.get(nextInt(branchIDList.size())));
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

        api.post(CREATE_PRODUCT_DISCOUNT_PATH, accessToken, String.valueOf(body));
    }
}
