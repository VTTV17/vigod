package api.dashboard.promotion;

import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.APIProductCollection;
import api.dashboard.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.promotion.DiscountCampaignInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
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
    private int discountCampaignMinQuantity;
    private List<Long> discountCampaignPrice;
    private Map<String, List<String>> discountCampaignStatus;
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    BranchInfo brInfo;
    private int customerId;

    public ProductDiscountCampaign(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        brInfo = new BranchManagement(loginInformation).getInfo();
    }

    public ProductDiscountCampaign setCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    public ProductDiscountCampaign endEarlyDiscountCampaign() {
        List<Integer> scheduleList = new API().get(DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        for (int campaignID : scheduleList) {
            new API().delete(DELETE_DISCOUNT_CAMPAIGN_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200);
        }

        List<Integer> inProgressList = new API().get(DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        for (int campaignID : inProgressList) {
            new API().delete(DELETE_DISCOUNT_CAMPAIGN_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200);
        }
        return this;
    }

    String getSegmentCondition(int customerId) {
        // segment type:
        // 0: all customers
        // 1: specific segment
        Customers customers = new Customers(loginInformation);
        List<Integer> listSegmentOfCustomer = customers.getListSegmentOfCustomer(customerId);
        int segmentConditionType = ((listSegmentOfCustomer == null) || listSegmentOfCustomer.isEmpty()) ? 0 : 1;

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

    String getAppliesToCondition(ProductInfo productInfo) {
        // applies to type:
        // 0: all products
        // 1: specific collections
        // 2: specific products
        int appliesToType = !productInfo.getCollectionIdList().isEmpty() ? 1 : List.of(0, 2).get(nextInt(2));
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

    String getMinimumRequirement(ProductInfo productInfo) {
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
        int discountCampaignBranchConditionType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
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

    String getAllCondition(int customerId, ProductInfo productInfo) {
        return """
                [%s, %s, %s, %s]""".formatted(getSegmentCondition(customerId),
                getAppliesToCondition(productInfo),
                getMinimumRequirement(productInfo),
                getBranchCondition());
    }

    String getDiscountConfig(int customerId, ProductInfo productInfo, int... time) {
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        Instant productDiscountCampaignStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        Instant productDiscountCampaignEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        // coupon type
        // 0: percentage
        // 1: fixed amount
        int productDiscountCouponType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);
        String couponType = productDiscountCouponType == 0 ? "PERCENTAGE" : "FIXED_AMOUNT";

        // coupon value
        long minFixAmount = Collections.min(productInfo.getProductSellingPrice());
        long couponValue = productDiscountCouponType == 0 ? nextInt(MAX_PERCENT_DISCOUNT) + 1 : nextLong(minFixAmount) + 1;
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
                ]""".formatted(productDiscountCampaignStartTime, couponType, couponValue, productDiscountCampaignEndTime, getAllCondition(customerId, productInfo));
    }

    String getDiscountCampaignBody(int customerId, ProductInfo productInfo, int... time) {
        // campaign name
        String name = "Auto - [Product] Discount campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        return """
                {
                    "name": "%s",
                    "storeId": "%s",
                    "timeCopy": 0,
                    "description": "",
                    "discounts": %s
                }""".formatted(name, loginInfo.getStoreID(), getDiscountConfig(customerId, productInfo, time));
    }

    public void createProductDiscountCampaign(ProductInfo productInfo, int... time) {
        // end early discount campaign
        endEarlyDiscountCampaign();
        String body = getDiscountCampaignBody(customerId, productInfo, time);

        Response createProductDiscountCampaign = api.post(CREATE_PRODUCT_DISCOUNT_CAMPAIGN_PATH, loginInfo.getAccessToken(), String.valueOf(body));
        createProductDiscountCampaign.then().statusCode(200);

        // debug log
        logger.debug("create product discount campaign");
        logger.debug(createProductDiscountCampaign.asPrettyString());

    }

    void getDiscountCampaignInformation(int campaignID, List<String> listVariationModel, List<Integer> listSegmentOfCustomer) {
        Response discountCampaignDetail = api.get(DISCOUNT_CAMPAIGN_DETAIL_PATH.formatted(campaignID), loginInfo.getAccessToken());
        discountCampaignDetail.then().statusCode(200);

        JsonPath discountCampaignDetailJson = discountCampaignDetail.jsonPath();

        /* Get discount campaign information */
        // get couponType
        String couponType = discountCampaignDetailJson.getString("discounts[0].couponType");

        // get coupon value
        long couponValue = Pattern.compile("couponValue.{4}(\\d+)").matcher(discountCampaignDetail.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList().get(0);

        // get discount status
        String status = discountCampaignDetailJson.getString("discounts[0].status");

        // get condition type
        List<String> conditionType = Pattern.compile("conditionType.{4}(\\w+)").matcher(discountCampaignDetail.asPrettyString()).results().map(matchResult -> String.valueOf(matchResult.group(1))).toList();

        // get condition options
        List<String> conditionOption = Pattern.compile("conditionOption.{4}(\\w+)").matcher(discountCampaignDetail.asPrettyString()).results().map(matchResult -> String.valueOf(matchResult.group(1))).toList();

        // get condition value map <condition type, condition value list>
        Map<String, List<Integer>> conditionValueMap = new HashMap<>();
        for (int conditionID = 0; conditionID < conditionType.size(); conditionID++) {
            List<Integer> conditionValueList = new ArrayList<>();
            for (int valueID = 0; valueID < discountCampaignDetailJson.getList("discounts[0].conditions[%s].values.id".formatted(conditionID)).size(); valueID++) {
                conditionValueList.add(discountCampaignDetailJson.getInt("discounts[0].conditions[%s].values[%s].conditionValue".formatted(conditionID, valueID)));
            }
            conditionValueMap.put(conditionType.get(conditionID), conditionValueList);
        }

        /* Update discount campaign status, price, stock */
        // update min requirements quantity of items
        discountCampaignMinQuantity = conditionValueMap.get("MINIMUM_REQUIREMENTS").get(0);

        // update discount campaign price
        discountCampaignPrice.replaceAll(price -> couponType.equals("FIXED_AMOUNT") ? (price > couponValue) ? (price - couponValue) : 0 : (price * (100 - couponValue)) / 100);

        // update discount campaign status
        List<String> appliesToBranch = conditionOption.contains("APPLIES_TO_BRANCH_SPECIFIC_BRANCH") ? conditionValueMap.get("APPLIES_TO_BRANCH").stream().map(brID -> brInfo.getBranchName().get(brInfo.getBranchID().indexOf(brID))).toList() : brInfo.getBranchName();
        boolean appliesToProduct = (conditionOption.contains("APPLIES_TO_SPECIFIC_COLLECTIONS") && conditionValueMap.get("APPLIES_TO").stream().map(integer -> new APIProductCollection(loginInformation).getListProductIDInCollections(integer)).flatMap(Collection::stream).toList().contains(Integer.parseInt(listVariationModel.get(0).split("-")[0]))) || (!conditionOption.contains("APPLIES_TO_SPECIFIC_COLLECTIONS") && (!conditionOption.contains("APPLIES_TO_SPECIFIC_PRODUCTS") || conditionValueMap.get("APPLIES_TO").contains(Integer.parseInt(listVariationModel.get(0).split("-")[0]))));

        boolean appliesToCustomer = !conditionOption.contains("CUSTOMER_SEGMENT_SPECIFIC_SEGMENT") || (listSegmentOfCustomer != null && !listSegmentOfCustomer.isEmpty() && conditionValueMap.get("CUSTOMER_SEGMENT").stream().anyMatch(listSegmentOfCustomer::contains));

        if (appliesToProduct && appliesToCustomer) {
            brInfo.getBranchName().forEach(brName -> {
                List<String> branchStatus = new ArrayList<>(discountCampaignStatus.get(brName));
                IntStream.range(0, listVariationModel.size()).filter(i -> appliesToBranch.contains(brName)).forEachOrdered(i -> branchStatus.set(i, status));
                discountCampaignStatus.put(brName, branchStatus);
            });
        }
    }

    public DiscountCampaignInfo getDiscountCampaignInfo(List<String> listVariationModel, List<Long> sellingPrice, List<Integer> listSegmentOfCustomer) {
        // init discount campaign information model
        DiscountCampaignInfo info = new DiscountCampaignInfo();

        List<Integer> discountCampaignList = new ArrayList<>();
        List<Integer> inProgressList = new API().get(DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        if (inProgressList != null) discountCampaignList.addAll(inProgressList);

        List<Integer> scheduleList = new API().get(DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        if (scheduleList != null) discountCampaignList.addAll(scheduleList);

        // init discount campaign price
        if (discountCampaignPrice == null || discountCampaignPrice.isEmpty())
            discountCampaignPrice = new ArrayList<>(sellingPrice);

        // init discount campaign status
        if (discountCampaignStatus == null || discountCampaignStatus.keySet().isEmpty() || discountCampaignList.isEmpty()) {
            discountCampaignStatus = new HashMap<>();
            brInfo.getBranchName().forEach(brName -> discountCampaignStatus.put(brName, listVariationModel.stream().map(barcode -> "EXPIRED").toList()));
        }

        // get last discount campaign information
        discountCampaignList.forEach(campaignID -> getDiscountCampaignInformation(campaignID, listVariationModel, listSegmentOfCustomer));

        // get last discount campaign status
        info.setDiscountCampaignStatus(discountCampaignStatus);

        // get last discount campaign price
        info.setDiscountCampaignPrice(discountCampaignPrice);

        // get last discount campaign minimum quantity
        info.setDiscountCampaignMinQuantity(discountCampaignMinQuantity);

        return info;
    }
}
