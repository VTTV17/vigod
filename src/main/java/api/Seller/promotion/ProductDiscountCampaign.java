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
import utilities.model.api.promotion.productDiscountCampaign.ProductDiscountCampaignConditions;
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
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class ProductDiscountCampaign {
    String CREATE_PRODUCT_DISCOUNT_CAMPAIGN_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
    String DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=SCHEDULED";
    String DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=IN_PROGRESS";
    String DISCOUNT_CAMPAIGN_DETAIL_PATH = "/orderservices2/api/gs-discount-campaigns/%s/full-condition";
    String DELETE_DISCOUNT_CAMPAIGN_PATH = "/orderservices2/api/gs-discount-campaigns/";
    API api = new API();
    Logger logger = LogManager.getLogger(ProductDiscountCampaign.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    BranchInfo brInfo;
    ProductDiscountCampaignConditions conditions;
    ProductInfo productInfo;

    @Data
    public static class DiscountCampaignInfo {
        private String couponType;
        private Long couponValue;
        private Integer discountCampaignMinQuantity;
        private String status;
        private List<String> appliesBranch;
        private List<Long> discountCampaignPrice;
        private Map<String, List<String>> discountCampaignStatus;
    }

    @Data
    public static class BranchDiscountCampaignInfo {
        private List<Integer> listOfMinimumRequirements;
        private List<String> listOfCouponTypes;
        private List<Long> listOfCouponValues;
    }

    public ProductDiscountCampaign(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        brInfo = new BranchManagement(loginInformation).getInfo();
    }

    public void endEarlyDiscountCampaign() {
        // get list schedule discount campaign
        List<Integer> scheduleList = api.get(DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(loginInfo.getStoreID()),
                loginInfo.getAccessToken()).jsonPath().getList("id");

        // end schedule discount campaign
        scheduleList.forEach(campaignID -> api.delete(DELETE_DISCOUNT_CAMPAIGN_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200));

        // get list in-progress discount campaign
        List<Integer> inProgressList = api.get(DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(loginInfo.getStoreID()),
                loginInfo.getAccessToken()).jsonPath().getList("id");

        // end in-progress discount campaign
        inProgressList.forEach(campaignID -> api.delete(DELETE_DISCOUNT_CAMPAIGN_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200));
    }

    String getSegmentCondition() {
        // get list segment of customer
        APIAllCustomers customers = new APIAllCustomers(loginInformation);
        List<Integer> listSegmentOfCustomer = customers.getListSegmentOfCustomer(conditions.getCustomerId());

        // segment type:
        // 0: all customers
        // 1: specific segment
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

        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ALL_PRODUCTS"
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
        List<String> variationModelList = productInfo.getVariationModelList();
        int min = Collections.min(productInfo.getProductStockQuantityMap().get(variationModelList.get(0)));
        if (productInfo.isHasModel()) for (int index = 1; index < variationModelList.size(); index++)
            min = Math.min(Collections.min(productInfo.getProductStockQuantityMap().get(variationModelList.get(index))), min);

        return """
                {
                    "conditionOption": "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                }""".formatted(nextInt(Math.max(min, 1)) + 1);
    }

    String getBranchCondition() {
        int discountCampaignBranchConditionType = (conditions.getDiscountCampaignBranchConditionType() != null)
                ? conditions.getDiscountCampaignBranchConditionType()
                : nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);

        String applicableCondition = discountCampaignBranchConditionType == 0
                ? "APPLIES_TO_BRANCH_ALL_BRANCHES"
                : "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";

        String applicableConditionValue = "";
        if (discountCampaignBranchConditionType != 0) {
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
                [%s, %s, %s, %s]""".formatted(getSegmentCondition(),
                getAppliesToCondition(),
                getMinimumRequirement(),
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
        Instant productDiscountCampaignStartTime = localDateTime.truncatedTo(ChronoUnit.DAYS).atZone(localZoneId).withZoneSameInstant(gmtZoneId).plusDays(startDatePlus).toInstant();

        // end date
        Instant productDiscountCampaignEndTime = localDateTime.truncatedTo(ChronoUnit.DAYS).atZone(localZoneId).withZoneSameInstant(gmtZoneId).plusDays(startDatePlus).plus(Duration.ofHours(23).plusMinutes(59)).toInstant();

        // coupon type
        // 0: percentage
        // 1: fixed amount
        int productDiscountCouponType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);
        String couponType = productDiscountCouponType == 0
                ? "PERCENTAGE"
                : "FIXED_AMOUNT";

        // coupon value
        long minFixAmount = Collections.min(productInfo.getProductSellingPrice());
        long couponValue = productDiscountCouponType == 0
                ? nextInt(MAX_PERCENT_DISCOUNT) + 1
                : nextLong(Math.max(minFixAmount, 1)) + 1;

        return """
                [
                        {
                            "couponCode": "unused_code",
                            "activeDate": "%s",
                            "couponType": "%s",
                            "couponValue": "%s",
                            "expiredDate": "%s",
                            "type": "WHOLE_SALE",
                            "conditions": %s
                        }
                ]""".formatted(productDiscountCampaignStartTime,
                couponType,
                couponValue,
                productDiscountCampaignEndTime,
                getAllCondition());
    }

    String getDiscountCampaignBody(int startDatePlus) {
        // campaign name
        String name = "Auto - [Product] Discount campaign - %s".formatted(new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));

        return """
                {
                    "name": "%s",
                    "storeId": "%s",
                    "timeCopy": 0,
                    "description": "",
                    "discounts": %s
                }""".formatted(name, loginInfo.getStoreID(), getDiscountConfig(startDatePlus));
    }

    public void createProductDiscountCampaign(ProductDiscountCampaignConditions conditions, ProductInfo productInfo, int startDatePlus) {
        // get product information
        this.productInfo = productInfo;

        // get product discount campaign condition
        this.conditions = conditions;

        // end early discount campaign
        endEarlyDiscountCampaign();

        // get discount campaign body
        String body = getDiscountCampaignBody(startDatePlus);

        // POST API to create new product discount campaign
        Response createProductDiscountCampaign = api.post(CREATE_PRODUCT_DISCOUNT_CAMPAIGN_PATH, loginInfo.getAccessToken(), body);

        // check product discount campaign is create
        createProductDiscountCampaign.then().statusCode(200);

        // debug log
        logger.debug("Product discount campaign id: %s".formatted(createProductDiscountCampaign.jsonPath().getInt("id")));
    }

    boolean isMatchWithConditions(List<String> conditionOption, Map<String, List<Integer>> conditionValueMap, ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        // check product condition
        boolean appliesToProduct = conditionOption.contains("APPLIES_TO_SPECIFIC_PRODUCTS")
                ? conditionValueMap.get("APPLIES_TO").contains(productInfo.getProductId())
                : (!conditionOption.contains("APPLIES_TO_SPECIFIC_COLLECTIONS") || conditionValueMap.get("APPLIES_TO")
                .stream()
                .anyMatch(collectionId -> productInfo.getCollectionIdList().contains(collectionId)));

        return appliesToProduct
                && (!conditionOption.contains("CUSTOMER_SEGMENT_SPECIFIC_SEGMENT") // check segment condition
                || ((listSegmentOfCustomer != null)
                && !listSegmentOfCustomer.isEmpty()
                && conditionValueMap.get("CUSTOMER_SEGMENT")
                .stream()
                .anyMatch(listSegmentOfCustomer::contains)));
    }

    DiscountCampaignInfo getInfo(int campaignID, ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        logger.info("CampaignId: %s.".formatted(campaignID));
        // GET discount campaign information by API
        Response discountCampaignDetail = api.get(DISCOUNT_CAMPAIGN_DETAIL_PATH.formatted(campaignID), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract().response();

        // get jsonPath
        JsonPath json = discountCampaignDetail.jsonPath();

        // get condition type
        List<String> conditionType = Pattern.compile("conditionType.{4}(\\w+)")
                .matcher(discountCampaignDetail.asPrettyString())
                .results()
                .map(matchResult -> String.valueOf(matchResult.group(1)))
                .toList();

        // get condition options
        List<String> conditionOption = Pattern.compile("conditionOption.{4}(\\w+)")
                .matcher(discountCampaignDetail.asPrettyString())
                .results()
                .map(matchResult -> String.valueOf(matchResult.group(1)))
                .toList();

        // get condition value map <condition type, condition value list>
        Map<String, List<Integer>> conditionValueMap = new HashMap<>();
        for (int conditionID = 0; conditionID < conditionType.size(); conditionID++) {
            List<Integer> conditionValueList = new ArrayList<>();
            for (int valueID = 0; valueID < json.getList("discounts[0].conditions[%s].values.id".formatted(conditionID)).size(); valueID++) {
                Integer conditionValue;
                // ignore payment method condition
                try {
                    conditionValue = json.getInt("discounts[0].conditions[%s].values[%s].conditionValue".formatted(conditionID, valueID));
                } catch (NumberFormatException ex) {
                    conditionValue = null;
                }
                if (conditionValue != null) conditionValueList.add(conditionValue);
            }

            conditionValueMap.put(conditionType.get(conditionID), conditionValueList);
        }

        // init discount campaign information
        DiscountCampaignInfo info = new DiscountCampaignInfo();

        if (isMatchWithConditions(conditionOption, conditionValueMap, productInfo, listSegmentOfCustomer)) {
            /* Get discount campaign information */
            // get couponType
            String couponType = json.getString("discounts[0].couponType");
            info.setCouponType(couponType);

            // get coupon value
            long couponValue = Pattern.compile("couponValue.{4}(\\d+)")
                    .matcher(discountCampaignDetail.asPrettyString())
                    .results()
                    .map(matchResult -> Long.valueOf(matchResult.group(1)))
                    .toList().get(0);
            info.setCouponValue(couponValue);

            /* Update discount campaign status, price, stock */
            // update min requirements quantity of items
            int discountCampaignMinQuantity = conditionValueMap.get("MINIMUM_REQUIREMENTS").get(0);
            info.setDiscountCampaignMinQuantity(discountCampaignMinQuantity);

            // update discount campaign price
            List<Long> discountCampaignPrice = new ArrayList<>(productInfo.getProductSellingPrice());
            discountCampaignPrice.replaceAll(variationPrice -> couponType.equals("FIXED_AMOUNT")
                    ? ((variationPrice > couponValue) ? (variationPrice - couponValue) : 0)
                    : ((variationPrice * (100 - couponValue)) / 100));
            info.setDiscountCampaignPrice(discountCampaignPrice);

            // update discount campaign status
            List<String> appliesToBranch = conditionOption.contains("APPLIES_TO_BRANCH_SPECIFIC_BRANCH")
                    ? conditionValueMap.get("APPLIES_TO_BRANCH")
                    .stream()
                    .map(brID -> brInfo.getBranchName().get(brInfo.getBranchID().indexOf(brID)))
                    .toList()
                    : brInfo.getBranchName();
            info.setAppliesBranch(appliesToBranch);

            // get discount status
            String status = json.getString("discounts[0].status");
            info.setStatus(status);

            Map<String, List<String>> statusMap = brInfo.getBranchName()
                    .stream()
                    .collect(Collectors.toMap(brName -> brName,
                            brName -> IntStream.range(0, productInfo.getVariationModelList().size()).mapToObj(varIndex -> appliesToBranch.contains(brName) ? status : "EXPIRED").toList(),
                            (a, b) -> b));
            info.setDiscountCampaignStatus(statusMap);
        }

        return info;
    }

    public Map<String, BranchDiscountCampaignInfo> getAllDiscountCampaignInfo(ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        // get list in-progress discount campaign
        List<Integer> discountCampaignList = api.get(DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("id");

        // init discount campaign information model
        List<DiscountCampaignInfo> infoList;

        // init discount campaign information
        Map<String, BranchDiscountCampaignInfo> discountCampaignInfo = new HashMap<>();
        if (!discountCampaignList.isEmpty()) {
            // get all in-progress discount campaign information
            infoList = discountCampaignList.stream()
                    .mapToInt(discountCampaignId -> discountCampaignId)
                    .mapToObj(discountCampaignId -> getInfo(discountCampaignId, productInfo, listSegmentOfCustomer))
                    .filter(info -> info.getDiscountCampaignMinQuantity() != null)
                    .toList();

            // map all information to map
            discountCampaignInfo = brInfo.getBranchName().stream().filter(brName -> !getCampaignInfo(brName, infoList).getListOfMinimumRequirements().isEmpty()).collect(Collectors.toMap(brName -> brName, brName -> getCampaignInfo(brName, infoList), (a, b) -> b));
        }

        // return all discount campaign information
        return discountCampaignInfo;
    }

    BranchDiscountCampaignInfo getCampaignInfo(String brName, List<DiscountCampaignInfo> infoList) {
        BranchDiscountCampaignInfo discountInfo = new BranchDiscountCampaignInfo();
        // all minimum of requirements
        List<Integer> listOfMinimumRequirements = new ArrayList<>();

        // all coupon types
        List<String> listOfCouponTypes = new ArrayList<>();

        // all coupon values
        List<Long> listOfCouponValues = new ArrayList<>();

        // get branch discount information
        for (DiscountCampaignInfo info : infoList) {
            if (info.getAppliesBranch().contains(brName)) {
                listOfMinimumRequirements.add(info.getDiscountCampaignMinQuantity());
                listOfCouponTypes.add(info.getCouponType());
                listOfCouponValues.add(info.getCouponValue());
            }
        }

        discountInfo.setListOfMinimumRequirements(listOfMinimumRequirements);
        discountInfo.setListOfCouponTypes(listOfCouponTypes);
        discountInfo.setListOfCouponValues(listOfCouponValues);
        return discountInfo;
    }
}
