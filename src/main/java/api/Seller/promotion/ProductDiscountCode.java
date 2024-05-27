package api.Seller.promotion;

import api.Seller.customers.APIAllCustomers;
import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.api.promotion.productDiscountCode.ProductDiscountCodeConditions;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class ProductDiscountCode {
    String CREATE_PRODUCT_DISCOUNT_CODE_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
    String PRODUCT_DISCOUNT_CODE_DETAIL_PATH = "/orderservices2/api/gs-discount-campaigns/%s/full-condition";
    String END_EARLY_DISCOUNT_CODE_PATH = "/orderservices2/api/gs-discount?id=%s&storeId=%s";
    String LIST_DISCOUNT_CODE_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=COUPON&status=IN_PROGRESS&page=%s&size=20";
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

    @Data
    public class DiscountCodeInfo {
        private String couponCode;
        private String couponType;
        private Long couponValue;
        private Boolean couponLimitedUsage;
        private Integer couponTotal;
        private Integer couponUsed;
        private Boolean couponLimitToOne;
        private String freeShippingProviders;
        private Boolean enabledRewards;
        private String rewardsDescription;
        private boolean noneRequired;
        private Integer minQuantity;
        private Long minTotal;
        private List<String> platform;
        private Map<String, List<String>> discountCodeStatus;
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
        APIAllCustomers customers = new APIAllCustomers(loginInformation);
        List<Integer> listSegmentOfCustomer = customers.getListSegmentOfCustomer(conditions.getCustomerId());
        int segmentConditionType = (conditions.getSegmentConditionType() != null)
                ? conditions.getSegmentConditionType()
                : (((listSegmentOfCustomer == null) || listSegmentOfCustomer.isEmpty()) ? 0 : 1);

        String segmentConditionLabel = segmentConditionType == 0
                ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS"
                : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0
                ? ""
                : """
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
        int appliesToType = (conditions.getAppliesToType() != null)
                ? conditions.getAppliesToType()
                : (!productInfo.getCollectionIdList().isEmpty() ? 1 : List.of(0, 2).get(nextInt(2)));

        String appliesToLabel = appliesToType == 0
                ? "APPLIES_TO_ALL_PRODUCTS"
                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
        String appliesToValue = (appliesToType == 0)
                ? ""
                : """
                {
                    "conditionValue": %s
                }
                """.formatted((appliesToType == 1) ? productInfo.getCollectionIdList().get(0) : productInfo.getProductId());
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
        int minimumRequirementType = (conditions.getMinimumRequirementType() != null)
                ? conditions.getMinimumRequirementType()
                : nextInt(MAX_PRODUCT_DISCOUNT_CODE_MINIMUM_REQUIREMENT_TYPE);

        String minimumRequirementLabel = minimumRequirementType == 0
                ? "MIN_REQUIREMENTS_NONE"
                : (minimumRequirementType == 1)
                ? "MIN_REQUIREMENTS_PURCHASE_AMOUNT"
                : "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS";

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
                }""".formatted(minimumRequirementLabel,
                (minimumRequirementType == 1)
                        ? (nextLong(Math.max(minStock, 1) * minPurchaseAmount) + 1)
                        : (nextInt(Math.max(minStock, 1)) + 1));
    }

    String getPaymentMethodCondition() {
        // default all payment methods
        List<String> allPaymentMethod = List.of("VISA", "ATM", "DEBT", "COD", "MOMO", "PAYPAL", "BANK_TRANSFER");

        // get payment method condition
        List<String> paymentMethod = new ArrayList<>(conditions.getPaymentMethod());

        // when no payment method is provided, check all payment method
        if (paymentMethod.isEmpty()) paymentMethod.addAll(allPaymentMethod);

        // get list payment method condition
        List<String> paymentMethodCondition = paymentMethod.stream().map("{\"conditionValue\": \"%s\"}"::formatted).toList();

        return  """
                {
                    "conditionOption": "PAYMENT_METHOD",
                    "conditionType": "PAYMENT_METHOD",
                    "values": %s
                }""".formatted(paymentMethodCondition);
    }

    String getPlatformCondition() {
        // get platform
        boolean appliesToApp = conditions.getAppliesToApp() != null
                ? conditions.getAppliesToApp()
                : nextBoolean();
        boolean appliesToWeb = conditions.getAppliesToWeb() != null
                ? conditions.getAppliesToWeb()
                : nextBoolean();
        boolean appliesToPOS = conditions.getAppliesToPOS() != null
                ? conditions.getAppliesToPOS()
                : nextBoolean();

        // need to select at least one platform
        while (!appliesToApp & !appliesToWeb & !appliesToPOS) {
            appliesToApp = nextBoolean();
            appliesToWeb = nextBoolean();
            appliesToPOS = nextBoolean();
        }

        // init platform condition options
        String conditionOption = "PLATFORMS%s%s%s".formatted(appliesToApp ? "_APP" : "",
                appliesToWeb ? "_WEB" : "",
                appliesToPOS ? "_INSTORE" : "");

        // in case, add _ONLY when only one platform is selected
        conditionOption = "%s%s".formatted(conditionOption, (conditionOption.split("_").length == 2) ? "_ONLY" : "");

        return """
                {
                    "conditionOption": "%s",
                    "conditionType": "PLATFORMS",
                    "values": []
                }""".formatted(conditionOption);
    }

    String getBranchCondition() {
        int discountCodeBranchConditionType = conditions.getDiscountCodeBranchConditionType() != null
                ? conditions.getDiscountCodeBranchConditionType()
                : nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
        String applicableCondition = discountCodeBranchConditionType == 0
                ? "APPLIES_TO_BRANCH_ALL_BRANCHES"
                : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
        String applicableConditionValue = "";
        if (discountCodeBranchConditionType != 0) {
            List<Integer> activeBranchList = IntStream.range(0, brInfo.getBranchID().size())
                    .filter(i -> brInfo.getAllBranchStatus().get(i).equals("ACTIVE"))
                    .mapToObj(i -> brInfo.getBranchID().get(i))
                    .toList();
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
                [%s, %s, %s, %s, %s, %s]""".formatted(getSegmentCondition(),
                getAppliesToCondition(),
                getMinimumRequirement(),
                getPlatformCondition(),
                getPaymentMethodCondition(),
                getBranchCondition());
    }

    String getDiscountConfig(int startDatePlus) {
        // Get the current local time
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get the time zone of the local time
        ZoneId localZoneId = ZoneId.systemDefault();

        // Get the GMT+0 time zone
        ZoneId gmtZoneId = ZoneId.of("GMT+0");

        // start date
        Instant productDiscountCodeStartTime = localDateTime.truncatedTo(ChronoUnit.DAYS).atZone(localZoneId).withZoneSameInstant(gmtZoneId).plusDays(startDatePlus).toInstant();

        // end date
        Instant productDiscountCodeEndTime = localDateTime.truncatedTo(ChronoUnit.DAYS).atZone(localZoneId).withZoneSameInstant(gmtZoneId).plusDays(startDatePlus).plus(Duration.ofHours(23).plusMinutes(59)).toInstant();


        // coupon code
        String couponCode = "AUTO" + Instant.now().toEpochMilli();

        // usage limit
        boolean couponLimitToOne = conditions.getCouponLimitToOne() != null
                ? conditions.getCouponLimitToOne()
                : nextBoolean();
        boolean couponLimitedUsage = conditions.getCouponLimitedUsage() != null
                ? conditions.getCouponLimitedUsage()
                : nextBoolean();
        int apiLimitTimesUse = nextInt(MAX_COUPON_USED_NUM) + 1;
        String couponTotal = couponLimitedUsage
                ? String.valueOf(apiLimitTimesUse)
                : "null";

        // coupon type
        // 0: percentage
        // 1: fixed amount
        // 2: free shipping
        int couponType = conditions.getCouponType() != null
                ? conditions.getCouponType()
                : nextInt(MAX_PRODUCT_CODE_DISCOUNT_TYPE);
        String couponTypeLabel = (couponType == 0)
                ? "PERCENTAGE"
                : ((couponType == 1)
                ? "FIXED_AMOUNT"
                : "FREE_SHIPPING");
        // coupon value
        int couponValue = (couponType == 0)
                ? (nextInt(MAX_PERCENT_DISCOUNT) + 1)
                : ((couponType == 1)
                ? (nextInt(MAX_FIXED_AMOUNT) + 1)
                : (nextInt(MAX_FREE_SHIPPING) + 1));

        // free shipping provided
        String freeShippingProviders = "giaohangnhanh,giaohangtietkiem,ahamove_bike,selfdelivery,ahamove_truck";

        // apply discount code as a reward
        boolean enabledRewards = conditions.getEnableReward() != null
                ? conditions.getEnableReward()
                : nextBoolean();

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

    String getDiscountCodeBody(int startDatePlus) {
        // coupon name
        String name = "Auto - [Product] Discount code - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        return """
                {
                    "name": "%s",
                    "storeId": "%s",
                    "timeCopy": 0,
                    "description": "",
                    "discounts": %s
                }""".formatted(name, loginInfo.getStoreID(), getDiscountConfig(startDatePlus));
    }

    public void createProductDiscountCode(ProductDiscountCodeConditions conditions, ProductInfo productInfo, int startDatePlus) {
        // set product information
        this.productInfo = productInfo;

        // set conditions
        this.conditions = conditions;

        // init product discount code body
        String body = getDiscountCodeBody(startDatePlus);

        // POST API to create new discount code
        Response response = api.post(CREATE_PRODUCT_DISCOUNT_CODE_PATH, loginInfo.getAccessToken(), body);

        // check discount code is created
        response.then().statusCode(200);
    }

    boolean isMatchWithConditions(List<String> conditionOption, Map<String, List<String>> conditionValueMap, ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        // check product condition
        boolean appliesToProduct = conditionOption.contains("APPLIES_TO_SPECIFIC_PRODUCTS")
                ? conditionValueMap.get("APPLIES_TO").contains(String.valueOf(productInfo.getProductId()))
                : (!conditionOption.contains("APPLIES_TO_SPECIFIC_COLLECTIONS") || conditionValueMap.get("APPLIES_TO")
                .stream()
                .anyMatch(collectionId -> productInfo.getCollectionIdList().contains(Integer.valueOf(collectionId))));

        return appliesToProduct
                && (!conditionOption.contains("CUSTOMER_SEGMENT_SPECIFIC_SEGMENT") // check segment condition
                || ((listSegmentOfCustomer != null)
                && !listSegmentOfCustomer.isEmpty()
                && conditionValueMap.get("CUSTOMER_SEGMENT")
                .stream()
                .anyMatch(segmentId -> listSegmentOfCustomer.contains(Integer.valueOf(segmentId)))));
    }

    public DiscountCodeInfo getDiscountInformation(int discountId, ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        Response discountCodeDetail = api.get(PRODUCT_DISCOUNT_CODE_DETAIL_PATH.formatted(discountId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // get jsonPath
        JsonPath json = discountCodeDetail.jsonPath();

        /* Get all discount code conditions */
        // get condition type
        List<String> conditionType = Pattern.compile("conditionType.{4}(\\w+)")
                .matcher(discountCodeDetail.asPrettyString())
                .results()
                .map(matchResult -> String.valueOf(matchResult.group(1)))
                .toList();

        // get condition options
        List<String> conditionOption = Pattern.compile("conditionOption.{4}(\\w+)")
                .matcher(discountCodeDetail.asPrettyString())
                .results()
                .map(matchResult -> String.valueOf(matchResult.group(1)))
                .toList();

        // get condition value map <condition type, condition value list>
        Map<String, List<String>> conditionValueMap = new HashMap<>();
        for (int conditionTypeId = 0; conditionTypeId < conditionType.size(); conditionTypeId++) {
            List<String> conditionValueList = new ArrayList<>();
            if (conditionType.get(conditionTypeId).equals("PLATFORMS")) {
                String platformCondition = json.getString("discounts[0].conditions[%s].conditionOption".formatted(conditionTypeId)).replaceAll("PLATFORMS_|_ONLY", "");
                conditionValueList = Arrays.stream(platformCondition.split("_")).toList();
            } else
                for (int conditionValueId = 0; conditionValueId < json.getList("discounts[0].conditions[%s].values.id".formatted(conditionTypeId)).size(); conditionValueId++)
                    conditionValueList.add(String.valueOf(json.getInt("discounts[0].conditions[%s].values[%s].conditionValue".formatted(conditionTypeId, conditionValueId))));
            conditionValueMap.put(conditionType.get(conditionTypeId), conditionValueList);
        }

        // init discount code information model
        DiscountCodeInfo info = new DiscountCodeInfo();

        if (isMatchWithConditions(conditionOption, conditionValueMap, productInfo, listSegmentOfCustomer)) {
            /* Get discount code information */
            // get couponType
            String couponType = json.getString("discounts[0].couponType");
            info.setCouponType(couponType);

            // get couponCode
            String couponCode = json.getString("discounts[0].couponCode");
            info.setCouponCode(couponCode);

            // get couponValue
            long couponValue = Pattern.compile("couponValue.{4}(\\d+)")
                    .matcher(discountCodeDetail.asPrettyString())
                    .results()
                    .map(matchResult -> Long.valueOf(matchResult.group(1)))
                    .toList().get(0);
            info.setCouponValue(couponValue);

            // get couponLimitedUsage
            boolean couponLimitedUsage = json.getBoolean("discounts[0].couponLimitedUsage");
            info.setCouponLimitedUsage(couponLimitedUsage);
            if (couponLimitedUsage) {
                // get total coupon
                int couponTotal = json.getInt("discounts[0].couponTotal");
                info.setCouponTotal(couponTotal);

                // get total used coupon
                try {
                    int couponUsed = json.getInt("discounts[0].couponUsed");
                    info.setCouponUsed(couponUsed);
                } catch (NullPointerException ex) {
                    info.setCouponUsed(0);
                }
            }

            // get couponLimitToOne
            boolean couponLimitToOne = json.getBoolean("discounts[0].couponLimitToOne");
            info.setCouponLimitToOne(couponLimitToOne);

            // get freeShippingProviders
            String freeShippingProviders = json.getString("discounts[0].freeShippingProviders");
            info.setFreeShippingProviders(freeShippingProviders);

            // get enabledRewards
            boolean enabledRewards = json.getBoolean("discounts[0].enabledRewards");
            info.setEnabledRewards(enabledRewards);

            // get rewardsDescription
            String rewardsDescription = json.getString("discounts[0].rewardsDescription");
            info.setRewardsDescription(rewardsDescription.replaceAll("<.*?>", ""));

            if (conditionOption.contains("MIN_REQUIREMENTS_PURCHASE_AMOUNT"))
                // set min total amount
                info.setMinTotal(Long.valueOf(conditionValueMap.get("MINIMUM_REQUIREMENTS").get(0)));
            else if (conditionOption.contains("MIN_REQUIREMENTS_QUANTITY_OF_ITEMS"))
                // set min quantity
                info.setMinQuantity(Integer.valueOf(conditionValueMap.get("MINIMUM_REQUIREMENTS").get(0)));
            else
                // set none required
                info.setNoneRequired(true);

            // get platform
            info.setPlatform(conditionValueMap.get("PLATFORMS"));

            // get discount status
            String status = json.getString("discounts[0].status");

            // get list branches can apply discount code
            List<String> appliesToBranch = conditionOption.contains("APPLIES_TO_BRANCH_SPECIFIC_BRANCH")
                    ? conditionValueMap.get("APPLIES_TO_BRANCH")
                    .stream()
                    .map(brID -> brInfo.getBranchName().get(brInfo.getBranchID().indexOf(Integer.valueOf(brID))))
                    .toList()
                    : brInfo.getBranchName();

            Map<String, List<String>> statusMap = brInfo.getBranchName()
                    .stream()
                    .collect(Collectors.toMap(brName -> brName,
                            brName -> IntStream.range(0, productInfo.getVariationModelList().size()).mapToObj(varIndex -> appliesToBranch.contains(brName) ? status : "EXPIRED").toList(),
                            (a, b) -> b));
            info.setDiscountCodeStatus(statusMap);
        }

        // return discount code info
        return info;
    }

    List<Integer> getListInProgressDiscount(String discountType) {
        // get list in-progress discount code by API
        Response listDiscount = api.get(LIST_DISCOUNT_CODE_PATH.formatted(loginInfo.getStoreID(), 0), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // get in-progress discount code list
        List<Integer> discountList = new ArrayList<>(listDiscount.jsonPath().getList("findAll{ it.discounts[0].couponType == '%s' }.id".formatted(discountType)));

        // get total page
        int totalPage = Integer.parseInt(listDiscount.getHeader("X-Total-Count")) / 20;

        // get next page
        if (totalPage > 1)
            IntStream.range(1, totalPage).<List<Integer>>mapToObj(pageIndex ->
                            new ArrayList<>(api.get(LIST_DISCOUNT_CODE_PATH.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                                    .then()
                                    .statusCode(200)
                                    .extract()
                                    .response()
                                    .jsonPath()
                                    .getList("findAll{ it.discounts[0].couponType == '%s' }.id".formatted(discountType))))
                    .forEach(discountList::addAll);

        // return list discount code by discount type
        return discountList;
    }

    /**
     * Discount type: FREE_SHIPPING, PERCENTAGE, FIXED_AMOUNT
     */
    public DiscountCodeInfo getAvailableDiscountCode(String discountType, ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        // get list in-progress discount code
        List<Integer> listDiscountCode = getListInProgressDiscount(discountType);

        // init discount code information model
        DiscountCodeInfo info = new DiscountCodeInfo();

        // in case, have some discount code available, check it conditions
        if (!listDiscountCode.isEmpty()) for (int discountId : listDiscountCode) {
            // get discount code information
            info = getDiscountInformation(discountId, productInfo, listSegmentOfCustomer);

            // find first
            if (info.getCouponCode() != null) break;
        }

        // return discount code information
        return info;
    }
}
