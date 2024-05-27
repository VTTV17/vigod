package api.Seller.promotion;

import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.products.product_collections.APIProductCollection;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.account.AccountTest.BUYER_ACCOUNT_THANG;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;
import static utilities.character_limit.CharacterLimit.*;

public class CreatePromotion {
    String CREATE_FLASH_SALE_PATH = "/itemservice/api/campaigns/";
    String CREATE_PRODUCT_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/coupons";
    String END_EARLY_FLASH_SALE_PATH = "/itemservice/api/campaigns/end-early/";
    String DELETE_FLASH_SALE_PATH = "/itemservice/api/campaigns/delete/%s?storeId=%s";
    String FLASH_SALE_LIST_PATH = "/itemservice/api/campaigns/search/";
    String FLASH_SALE_DETAIL = "/itemservice/api/campaigns/%s?storeId=%s";
    String DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=SCHEDULED";
    String DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH = "/orderservices2/api/gs-discount-campaigns?storeId=%s&type=WHOLE_SALE&status=IN_PROGRESS";
    String DISCOUNT_CAMPAIGN_DETAIL = "/orderservices2/api/gs-discount-campaigns/%s/full-condition";
    String DELETE_DISCOUNT_PATH = "/orderservices2/api/gs-discount-campaigns/";
    String END_EARLY_DISCOUNT_CODE_PATH = "/orderservices2/api/gs-discount?id=%s&storeId=%s";
    API api = new API();
    Logger logger = LogManager.getLogger(CreatePromotion.class);

    // flash sale
    private List<Long> flashSalePrice;

    private List<Integer> flashSaleStock;
    private Map<String, List<String>> flashSaleStatus = new HashMap<>();

    // product discount campaign
    private int discountCampaignMinQuantity;
    private List<Long> discountCampaignPrice;
    private Map<String, List<String>> discountCampaignStatus;
    private Integer discountCampaignBranchConditionType;

    // discount code
    public static Instant apiDiscountCodeStartTime;
    public static Instant apiDiscountCodeEndTime;
    public static String apiCouponCode;
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
    public static int apiDiscountId;
    public static int apiLimitTimesUse = -1;
    /**
     * set branch condition
     * <p> DEFAULT value = - 1, no condition is provided, random condition should be generated</p>
     * <p> SET value = 0: ALL BRANCH</p>
     * <p> SET value = 1: SPECIFIC BRANCH</p>
     */

    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public CreatePromotion (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        brInfo = new BranchManagement(loginInformation).getInfo();
    }

    BranchInfo brInfo;
    Instant flashSaleStartTime;
    Instant productDiscountCampaignStartTime;

    public void waitPromotionStart() throws InterruptedException {
        long wait = flashSaleStartTime == null && productDiscountCampaignStartTime == null ? Instant.now().toEpochMilli() : flashSaleStartTime == null ? productDiscountCampaignStartTime.toEpochMilli() : productDiscountCampaignStartTime == null ? flashSaleStartTime.toEpochMilli() : Math.min(flashSaleStartTime.toEpochMilli(), productDiscountCampaignStartTime.toEpochMilli());
        wait = wait - Instant.now().toEpochMilli();
        sleep(wait);
    }

//    public CreatePromotion endEarlyFlashSale() {
//        // get schedule flash sale list
//        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        logger.debug("schedule flash sale list: %s".formatted(scheduleList));
//        if (scheduleList != null)
//            scheduleList.forEach(id -> new API().delete(DELETE_FLASH_SALE_PATH.formatted(id, loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200));
//
//        // get in progress flash sale
//        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        logger.debug("in-progress flash sale list: %s".formatted(inProgressList));
//        if (inProgressList != null)
//            inProgressList.forEach(id -> new API().post("%s%s?storeId=%s".formatted(END_EARLY_FLASH_SALE_PATH, id, loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200));
//        return this;
//    }
//
//    public CreatePromotion createFlashSale(ProductInfo productInfo, int... time) {
//        endEarlyFlashSale();
//        StringBuilder tempBody = new StringBuilder();
//        int num = nextInt(productInfo.getBarcodeList().size()) + 1;
//
//        if (productInfo.isHasModel()) {
//            for (int i = 0; i < num; i++) {
//                // get barcode
//                String barcode = productInfo.getBarcodeList().get(i);
//                // check in-stock
//                if (Collections.max(productInfo.getProductStockQuantityMap().get(barcode)) > 0) {
//                    if (!tempBody.toString().isEmpty()) {
//                        tempBody.append(",");
//                    }
//                    // sale stock
//                    int stock = nextInt(Math.max(Collections.max(productInfo.getProductStockQuantityMap().get(barcode)), 1)) + 1;
//
//                    // purchase limit
//                    int purchaseLimit = nextInt(stock + 1);
//
//                    // variation model
//                    String modelID = barcode.split("-")[1];
//
//                    // variation price
//                    long price = nextLong(productInfo.getProductSellingPrice().get(i));
//
//                    String flashSaleProduct = """
//                            {
//                                        "itemId": "%s",
//                                        "limitPurchaseStock": "%s",
//                                        "modelId": "%s",
//                                        "price": "%s",
//                                        "saleStock": "%s"
//                                    }
//                            """.formatted(productInfo.getProductId(), purchaseLimit, modelID, price, stock);
//                    tempBody.append(flashSaleProduct);
//                }
//            }
//        } else {
//            // sale stock
//            int stock = nextInt(Math.max(Collections.max(productInfo.getProductStockQuantityMap().get(String.valueOf(productInfo.getProductId()))), 1)) + 1;
//
//            // purchase limit
//            int purchaseLimit = nextInt(stock) + 1;
//
//            // sale price
//            long price = nextLong(productInfo.getProductSellingPrice().get(0));
//
//            String flashSaleProduct = """
//                    {
//                                "itemId": "%s",
//                                "limitPurchaseStock": "%s",
//                                "price": "%s",
//                                "saleStock": "%s"
//                            }
//                    """.formatted(productInfo.getProductId(), purchaseLimit, price, stock);
//            tempBody.append(flashSaleProduct);
//        }
//
//        // flash sale name
//        String flashSaleName = "Auto - Flash sale campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
//        // start date
//        int startMin = time.length > 0 ? time[0] : nextInt(60);
//        this.flashSaleStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);
//
//        // end date
//        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
//        Instant flashSaleEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);
//
//        String body = """
//                {
//                    "name": "%s",
//                    "startDate": "%s",
//                    "endDate": "%s",
//                    "items": [%s]}""".formatted(flashSaleName, this.flashSaleStartTime, flashSaleEndTime, tempBody);
//
//
//        // post api create new flash sale campaign
//        Response createFlashSale = api.post(CREATE_FLASH_SALE_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
//
//        logger.debug("create flash sale %s".formatted(createFlashSale.asPrettyString()));
//        if (createFlashSale.statusCode() != 200) System.out.println(body);
//
//        createFlashSale.then().statusCode(200);
//
//        return this;
//    }
//
//    void getFlashSaleInformation(int flashSaleID, List<String> listVariationModelId) {
//        Response flashSaleDetail = api.get(FLASH_SALE_DETAIL.formatted(flashSaleID, loginInfo.getStoreID()), loginInfo.getAccessToken());
//        flashSaleDetail.then().statusCode(200);
//        JsonPath flashSaleDetailJson = flashSaleDetail.jsonPath();
//
//        // init flash sale stock list
//        if (flashSaleStock == null) {
//            flashSaleStock = new ArrayList<>();
//            listVariationModelId.forEach(barcode -> flashSaleStock.add(0));
//        }
//
//        // update flash sale status map
//        String status = flashSaleDetailJson.getString("status");
//        List<String> hasFlashSaleBarcodeList = flashSaleDetailJson.getList("items.itemModelId");
//        brInfo.getBranchName().forEach(brName -> {
//            List<String> statusList = new ArrayList<>(flashSaleStatus.get(brName));
//            hasFlashSaleBarcodeList.stream().filter(listVariationModelId::contains).forEachOrdered(promotionBarcode -> statusList.set(listVariationModelId.indexOf(promotionBarcode), status));
//            flashSaleStatus.put(brName, statusList);
//        });
//
//        // update flash sale price
//        List<Long> flashSalePrice = Pattern.compile("newPrice.{3}(\\d+)").matcher(flashSaleDetail.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
//        hasFlashSaleBarcodeList.stream().filter(listVariationModelId::contains).forEachOrdered(s -> this.flashSalePrice.set(listVariationModelId.indexOf(s), flashSalePrice.get(hasFlashSaleBarcodeList.indexOf(s))));
//
//        // update flash sale stock
//        hasFlashSaleBarcodeList.stream().filter(listVariationModelId::contains).forEach(promotionBarcode -> flashSaleStock.set(listVariationModelId.indexOf(promotionBarcode), IntStream.range(0, flashSaleDetailJson.getList("items.saleStock").size()).mapToObj(itemID -> (int) flashSaleDetailJson.getFloat("items[%s].saleStock".formatted(itemID))).toList().get(hasFlashSaleBarcodeList.indexOf(promotionBarcode))));
//    }
//
//    public FlashSaleInfo getFlashSaleInfo(List<String> listVariationModelId, List<Long> sellingPrice) {
//        // init flash sale information model
//        FlashSaleInfo info = new FlashSaleInfo();
//
//        // init flash sale list
//        List<Integer> flashSaleList = new ArrayList<>();
//
//        // get in-progress list
//        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        if (inProgressList != null) flashSaleList.addAll(inProgressList);
//
//        // get schedule list
//        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        if (scheduleList != null) flashSaleList.addAll(scheduleList);
//
//        // init flash sale status map
//        if (flashSaleStatus == null || flashSaleStatus.keySet().isEmpty() || flashSaleList.isEmpty()) {
//            flashSaleStatus = new HashMap<>();
//            brInfo.getBranchName().forEach(brName -> flashSaleStatus.put(brName, listVariationModelId.stream().map(barcode -> "EXPIRED").toList()));
//        }
//
//        // init flash sale price list
//        if (flashSalePrice == null || flashSalePrice.isEmpty()) {
//            flashSalePrice = new ArrayList<>();
//            listVariationModelId.forEach(barcode -> flashSalePrice.add(sellingPrice.get(listVariationModelId.indexOf(barcode))));
//        }
//
//        // get last flash sale info
//        flashSaleList.forEach(flsID -> getFlashSaleInformation(flsID, listVariationModelId));
//
//        // set last flash sale price
//        info.setFlashSalePrice(flashSalePrice);
//
//        // set last flash sale status
//        info.setFlashSaleStatus(flashSaleStatus);
//
//        // set last flash sale stock
//        info.setFlashSaleStock(flashSaleStock);
//
//        return info;
//    }
//
//    public CreatePromotion endEarlyDiscountCampaign() {
//        List<Integer> scheduleList = new API().get(DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        for (int campaignID : scheduleList) {
//            new API().delete(DELETE_DISCOUNT_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200);
//        }
//
//        List<Integer> inProgressList = new API().get(DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        for (int campaignID : inProgressList) {
//            new API().delete(DELETE_DISCOUNT_PATH + campaignID, loginInfo.getAccessToken()).then().statusCode(200);
//        }
//        return this;
//    }
//
//    public CreatePromotion createProductDiscountCampaign(ProductInfo productInfo, int... time) throws InterruptedException {
//        // end early discount campaign
//        endEarlyDiscountCampaign();
//
//        // campaign name
//        String name = "Auto - [Product] Discount campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
//
//        // start date
//        int startMin = time.length > 0 ? time[0] : nextInt(60);
//        this.productDiscountCampaignStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);
//
//        // end date
//        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
//        Instant productDiscountCampaignEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);
//
//        // coupon type
//        // 0: percentage
//        // 1: fixed amount
//        int productDiscountCouponType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);
//        String couponTypeLabel = productDiscountCouponType == 0 ? "PERCENTAGE" : "FIXED_AMOUNT";
//
//        // coupon value
//        long minFixAmount = Collections.min(productInfo.getProductSellingPrice());
//        long productDiscountCouponValue = productDiscountCouponType == 0 ? nextInt(MAX_PERCENT_DISCOUNT) + 1 : nextLong(minFixAmount) + 1;
//
//        // init coupon type value
//        StringBuilder body = new StringBuilder("""
//                {
//                    "name": "%s",
//                    "storeId": "%s",
//                    "discounts": [
//                        {
//                            "couponCode": "unused_code",
//                            "activeDate": "%s",
//                            "couponType": "%s",
//                            "couponValue": "%s",
//                            "expiredDate": "%s",
//                            "type": "WHOLE_SALE",
//                            "conditions": [""".formatted(name, loginInfo.getStoreID(), productDiscountCampaignStartTime, couponTypeLabel, productDiscountCouponValue, productDiscountCampaignEndTime));
//
//        // init segment condition
//        // segment type:
//        // 0: all customers
//        // 1: specific segment
//        if (new Customers(loginInformation).getSegmentID() == 0)
//            new Customers(loginInformation).createSegmentByAPI(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84");
//        int segmentConditionType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE);
//        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
//        String segmentConditionValue = segmentConditionType == 0 ? "" : """
//                {
//                    "conditionValue": %s
//                }""".formatted(new Customers(loginInformation).getSegmentID());
//        String segmentCondition = """
//                {
//                    "conditionOption": "%s",
//                    "conditionType": "CUSTOMER_SEGMENT",
//                    "values": [ %s ]
//                },""".formatted(segmentConditionLabel, segmentConditionValue);
//
//        // add segment condition into body
//        body.append(segmentCondition);
//
//        // init applies to condition
//        // applies to type:
//        // 0: all products
//        // 1: specific collections
//        // 2: specific products
//        int appliesToType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLIES_TO_TYPE);
//        String appliesToLabel = appliesToType == 0 ? "APPLIES_TO_ALL_PRODUCTS"
//                : (appliesToType == 1) ? "APPLIES_TO_SPECIFIC_COLLECTIONS" : "APPLIES_TO_SPECIFIC_PRODUCTS";
//        String appliesToValue = appliesToType == 0 ? "" : """
//                {
//                    "conditionValue": %s
//                }
//                """.formatted(appliesToType == 1 ? new ProductCollection(loginInformation).createCollection(productInfo) : productInfo.getProductId());
//        String appliesToCondition = """
//                {
//                    "conditionOption": "%s",
//                    "conditionType": "APPLIES_TO",
//                    "values": [ %s ]
//                },
//                """.formatted(appliesToLabel, appliesToValue);
//        body.append(appliesToCondition);
//
//        // init minimum requirement
//        int min = 1;
//        if (productInfo.isHasModel()) for (String key : productInfo.getProductStockQuantityMap().keySet())
//            min = Math.min(min, Collections.min(productInfo.getProductStockQuantityMap().get(key)));
//        else min = Collections.min(productInfo.getProductStockQuantityMap().get(String.valueOf(productInfo.getProductId())));
//        discountCampaignMinQuantity = nextInt(Math.max(1, min)) + 1;
//
//        String minimumRequirement = """
//                {
//                    "conditionOption": "MIN_REQUIREMENTS_QUANTITY_OF_ITEMS",
//                    "conditionType": "MINIMUM_REQUIREMENTS",
//                    "values": [
//                        {
//                            "conditionValue": "%s"
//                        }
//                    ]
//                },""".formatted(discountCampaignMinQuantity);
//        body.append(minimumRequirement);
//
//        // init applicable branch
//        // if no branch condition is provided, generate random branch condition
//
//        if (discountCampaignBranchConditionType == null) {
//            discountCampaignBranchConditionType = nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_APPLICABLE_BRANCH_TYPE);
//        }
//
//        String applicableCondition;
//        String applicableConditionValue = "";
//        if (discountCampaignBranchConditionType == 0) {
//            applicableCondition = "APPLIES_TO_BRANCH_ALL_BRANCHES";
//        } else {
//            List<Integer> activeBranchList = IntStream.range(0, brInfo.getBranchID().size()).filter(i -> brInfo.getAllBranchStatus().get(i).equals("ACTIVE")).mapToObj(i -> brInfo.getBranchID().get(i)).toList();
//            int brID = activeBranchList.get(nextInt(activeBranchList.size()));
//
//            applicableCondition = "APPLIES_TO_BRANCH_SPECIFIC_BRANCH";
//            applicableConditionValue = """
//                    {
//                        "conditionValue": "%s"
//                    }""".formatted(brID);
//        }
//        String applicableBranch = """
//                {
//                    "conditionOption": "%s",
//                    "conditionType": "APPLIES_TO_BRANCH",
//                    "values": [ %s ]
//                }""".formatted(applicableCondition, applicableConditionValue);
//        body.append(applicableBranch);
//        body.append("]}]}");
//
//        Response createProductDiscountCampaign = api.post(CREATE_PRODUCT_DISCOUNT_PATH, loginInfo.getAccessToken(), String.valueOf(body));
//        createProductDiscountCampaign.then().statusCode(200);
//
//        // debug log
//        logger.debug("create product discount campaign");
//        logger.debug(createProductDiscountCampaign.asPrettyString());
//
//        return this;
//    }
//
//    private String getSegmentCondition(int segmentConditionType) {
//        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
//        String segmentConditionValue = segmentConditionType == 0 ? "" : """
//                {
//                    "conditionValue": %s
//                }""".formatted(new Customers(loginInformation).getSegmentID());
//        return """
//                {
//                    "conditionOption": "%s",
//                    "conditionType": "CUSTOMER_SEGMENT",
//                    "values": [ %s ]
//                },""".formatted(segmentConditionLabel, segmentConditionValue);
//    }
//
//    void getDiscountCampaignInformation(int campaignID, List<String> listVariationModel, List<Integer> listSegmentOfCustomer) {
//        Response discountCampaignDetail = api.get(DISCOUNT_CAMPAIGN_DETAIL.formatted(campaignID), loginInfo.getAccessToken());
//        discountCampaignDetail.then().statusCode(200);
//
//        JsonPath discountCampaignDetailJson = discountCampaignDetail.jsonPath();
//
//        /* Get discount campaign information */
//        // get couponType
//        String couponType = discountCampaignDetailJson.getString("discounts[0].couponType");
//
//        // get coupon value
//        long couponValue = Pattern.compile("couponValue.{4}(\\d+)").matcher(discountCampaignDetail.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList().get(0);
//
//        // get discount status
//        String status = discountCampaignDetailJson.getString("discounts[0].status");
//
//        // get condition type
//        List<String> conditionType = Pattern.compile("conditionType.{4}(\\w+)").matcher(discountCampaignDetail.asPrettyString()).results().map(matchResult -> String.valueOf(matchResult.group(1))).toList();
//
//        // get condition options
//        List<String> conditionOption = Pattern.compile("conditionOption.{4}(\\w+)").matcher(discountCampaignDetail.asPrettyString()).results().map(matchResult -> String.valueOf(matchResult.group(1))).toList();
//
//        // get condition value map <condition type, condition value list>
//        Map<String, List<Integer>> conditionValueMap = new HashMap<>();
//        for (int conditionID = 0; conditionID < conditionType.size(); conditionID++) {
//            List<Integer> conditionValueList = new ArrayList<>();
//            for (int valueID = 0; valueID < discountCampaignDetailJson.getList("discounts[0].conditions[%s].values.id".formatted(conditionID)).size(); valueID++) {
//                conditionValueList.add(discountCampaignDetailJson.getInt("discounts[0].conditions[%s].values[%s].conditionValue".formatted(conditionID, valueID)));
//            }
//            conditionValueMap.put(conditionType.get(conditionID), conditionValueList);
//        }
//
//        /* Update discount campaign status, price, stock */
//        // update min requirements quantity of items
//        discountCampaignMinQuantity = conditionValueMap.get("MINIMUM_REQUIREMENTS").get(0);
//
//        // update discount campaign price
//        discountCampaignPrice.replaceAll(price -> couponType.equals("FIXED_AMOUNT") ? (price > couponValue) ? (price - couponValue) : 0 : (price * (100 - couponValue)) / 100);
//
//        // update discount campaign status
//        List<String> appliesToBranch = conditionOption.contains("APPLIES_TO_BRANCH_SPECIFIC_BRANCH") ? conditionValueMap.get("APPLIES_TO_BRANCH").stream().map(brID -> brInfo.getBranchName().get(brInfo.getBranchID().indexOf(brID))).toList() : brInfo.getBranchName();
//        boolean appliesToProduct = (conditionOption.contains("APPLIES_TO_SPECIFIC_COLLECTIONS") && conditionValueMap.get("APPLIES_TO").stream().map(integer -> new APIProductCollection(loginInformation).getListProductIDInCollections(integer)).flatMap(Collection::stream).toList().contains(Integer.parseInt(listVariationModel.get(0).split("-")[0]))) || (!conditionOption.contains("APPLIES_TO_SPECIFIC_COLLECTIONS") && (!conditionOption.contains("APPLIES_TO_SPECIFIC_PRODUCTS") || conditionValueMap.get("APPLIES_TO").contains(Integer.parseInt(listVariationModel.get(0).split("-")[0]))));
//
//        boolean appliesToCustomer = !conditionOption.contains("CUSTOMER_SEGMENT_SPECIFIC_SEGMENT") || (listSegmentOfCustomer != null && !listSegmentOfCustomer.isEmpty() && conditionValueMap.get("CUSTOMER_SEGMENT").stream().anyMatch(listSegmentOfCustomer::contains));
//
//        if (appliesToProduct && appliesToCustomer) {
//            brInfo.getBranchName().forEach(brName -> {
//                List<String> branchStatus = new ArrayList<>(discountCampaignStatus.get(brName));
//                IntStream.range(0, listVariationModel.size()).filter(i -> appliesToBranch.contains(brName)).forEachOrdered(i -> branchStatus.set(i, status));
//                discountCampaignStatus.put(brName, branchStatus);
//            });
//        }
//    }
//
//    public DiscountCampaignInfo getDiscountCampaignInfo(List<String> listVariationModel, List<Long> sellingPrice, List<Integer> listSegmentOfCustomer) {
//        // init discount campaign information model
//        DiscountCampaignInfo info = new DiscountCampaignInfo();
//
//        List<Integer> discountCampaignList = new ArrayList<>();
//        List<Integer> inProgressList = new API().get(DISCOUNT_CAMPAIGN_SCHEDULE_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        if (inProgressList != null) discountCampaignList.addAll(inProgressList);
//
//        List<Integer> scheduleList = new API().get(DISCOUNT_CAMPAIGN_IN_PROGRESS_LIST_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
//        if (scheduleList != null) discountCampaignList.addAll(scheduleList);
//
//        // init discount campaign price
//        if (discountCampaignPrice == null || discountCampaignPrice.isEmpty())
//            discountCampaignPrice = new ArrayList<>(sellingPrice);
//
//        // init discount campaign status
//        if (discountCampaignStatus == null || discountCampaignStatus.keySet().isEmpty() || discountCampaignList.isEmpty()) {
//            discountCampaignStatus = new HashMap<>();
//            brInfo.getBranchName().forEach(brName -> discountCampaignStatus.put(brName, listVariationModel.stream().map(barcode -> "EXPIRED").toList()));
//        }
//
//        // get last discount campaign information
//        discountCampaignList.forEach(campaignID -> getDiscountCampaignInformation(campaignID, listVariationModel, listSegmentOfCustomer));
//
//        // get last discount campaign status
//        info.setDiscountCampaignStatus(discountCampaignStatus);
//
//        // get last discount campaign price
//        info.setDiscountCampaignPrice(discountCampaignPrice);
//
//        // get last discount campaign minimum quantity
//        info.setDiscountCampaignMinQuantity(discountCampaignMinQuantity);
//
//        return info;
//    }


    public void createProductDiscountCode(int... time) throws InterruptedException {
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
        boolean couponLimitToOne = apiIsLimitToOne;
        boolean couponLimitedUsage = apiIsLimitToUsage;
        if (apiLimitTimesUse == -1) {
            apiLimitTimesUse = nextInt(MAX_COUPON_USED_NUM) + 1;
        }
        String couponTotal = couponLimitedUsage ? String.valueOf(apiLimitTimesUse) : "null";

        // coupon type
        // 0: percentage
        // 1: fixed amount
        // 2: free shipping
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
                            "conditions": [""".formatted(name, loginInfo.getStoreID(), apiDiscountCodeStartTime, apiDiscountCodeEndTime, apiCouponCode, couponLimitToOne, couponLimitedUsage, couponTotal, couponTypeLabel, couponValue, freeShippingProviders, enabledRewards, rewardsDescription));

        // init segment condition
        // segment type:
        // 0: all customers
        // 1: specific segment
//        int segmentConditionType = nextInt(MAX_PRODUCT_DISCOUNT_CODE_SEGMENT_TYPE);
        int segmentConditionType = apiSegmentConditionType;
        if (new APISegment(loginInformation).getSegmentID() == 0)
            new APISegment(loginInformation).createSegmentByAPI(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84");
        String segmentConditionLabel = segmentConditionType == 0 ? "CUSTOMER_SEGMENT_ALL_CUSTOMERS" : "CUSTOMER_SEGMENT_SPECIFIC_SEGMENT";
        String segmentConditionValue = segmentConditionType == 0 ? "" : """
                {
                    "conditionValue": %s
                }""".formatted(new APISegment(loginInformation).getSegmentID());
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
                """.formatted(appliesToType == 1 ? new APIProductCollection(loginInformation).createCollection() : new CreateProduct(loginInformation).getProductID());
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
        if (new CreateProduct(loginInformation).isHasModel()) {
            for (String key : new CreateProduct(loginInformation).getProductStockQuantity().keySet()) {
                minStock = Math.min(minStock, Collections.min(new CreateProduct(loginInformation).getProductStockQuantity().get(key)));
            }
        } else minStock = Collections.min(new CreateProduct(loginInformation).getProductStockQuantity().get(null));
        long minPurchaseAmount = Collections.min(new CreateProduct(loginInformation).getProductSellingPrice());
        String minimumRequirement = """
                {
                    "conditionOption": "%s",
                    "conditionType": "MINIMUM_REQUIREMENTS",
                    "values": [
                        {
                            "conditionValue": "%s"
                        }
                    ]
                },""".formatted(minimumRequirementLabel, (minimumRequirementType == 1) ? (nextInt(minStock) + 1) : (nextLong(minPurchaseAmount) + 1));
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
                }""".formatted(brInfo.getBranchID().get(nextInt(brInfo.getBranchID().size())));
        String applicableBranch = """
                {
                    "conditionOption": "%s",
                    "conditionType": "APPLIES_TO_BRANCH",
                    "values": [ %s ]
                },""".formatted(applicableBranchLabel, applicableConditionValue);
        body.append(applicableBranch);
        //Payment method condition, hard ALL
        String paymentMethodAll = """
                {
                                                     "conditionOption": "PAYMENT_METHOD",
                                                     "conditionType": "PAYMENT_METHOD",
                                                     "values": [
                                                         {
                                                             "conditionValue": "VISA"
                                                         },
                                                         {
                                                             "conditionValue": "COD"
                                                         },
                                                         {
                                                             "conditionValue": "ATM"
                                                         },
                                                         {
                                                             "conditionValue": "DEBT"
                                                         },
                                                         {
                                                             "conditionValue": "MOMO"
                                                         },
                                                         {
                                                             "conditionValue": "PAYPAL"
                                                         },
                                                         {
                                                             "conditionValue": "BANK_TRANSFER"
                                                         }
                                                     ]
                                                 },
                """;
        body.append(paymentMethodAll);
        // init platform
        String platform = """
                {
                    "conditionOption": "PLATFORMS_APP_WEB_INSTORE",
                    "conditionType": "PLATFORMS",
                    "values": []
                }""";
        body.append(platform);
        body.append("]}]}");

        Response response = api.post(CREATE_PRODUCT_DISCOUNT_PATH, loginInfo.getAccessToken(), String.valueOf(body));
        response.then().statusCode(200);
        apiDiscountName = name;
        apiCouponValue = couponValue;
        apiDiscountId = response.jsonPath().getInt("discounts[0].id");
    }

    public void endEarlyDiscount(int discountId) {
        String path = END_EARLY_DISCOUNT_CODE_PATH.formatted(discountId, loginInfo.getStoreID());
        Response response = api.put(path, loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Call api to end early discount.");
    }
    
    //Temporary function. Will think of a better way to do this
    public JsonPath createProductDiscountCodeJsonPath() {
    	String discountName = "Auto - [Product] Discount code - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
    	String couponCode = "AUTO" + Instant.now().toEpochMilli();
    	
    	String body = """
    			{
    			"description": "",
    			"discounts": [{
    			"activeDate": "2024-03-06T17:00:00.000Z",
    			"conditions": [{
    			"conditionOption": "CUSTOMER_SEGMENT_ALL_CUSTOMERS",
    			"conditionType": "CUSTOMER_SEGMENT",
    			"values": []
    			}, {
    			"conditionOption": "APPLIES_TO_ENTIRE_ORDER",
    			"conditionType": "APPLIES_TO",
    			"values": []
    			}, {
    			"conditionOption": "MIN_REQUIREMENTS_NONE",
    			"conditionType": "MINIMUM_REQUIREMENTS",
    			"values": [{
    			"conditionValue": 0
    			}]
    			}, {
    			"conditionOption": "PLATFORMS_WEB_ONLY",
    			"conditionType": "PLATFORMS",
    			"values": []
    			}, {
    			"conditionOption": "APPLIES_TO_BRANCH_ALL_BRANCHES",
    			"conditionType": "APPLIES_TO_BRANCH",
    			"values": []
    			}, {
    			"conditionOption": "PAYMENT_METHOD",
    			"conditionType": "PAYMENT_METHOD",
    			"values": [{
    			"conditionValue": "MOMO"
    			}]
    			}],
    			"couponCode": "%s",
    			"couponLimitToOne": false,
    			"couponLimitedUsage": false,
    			"couponTotal": null,
    			"couponType": "PERCENTAGE",
    			"couponValue": "1",
    			"expiredDate": "3024-03-08T16:59:59.999Z",
    			"storeId": "%s",
    			"type": "COUPON",
    			"freeShippingProviders": "",
    			"feeShippingType": "FIXED_AMOUNT",
    			"enabledRewards": false,
    			"rewardsDescription": "",
    			"hideInStore": false
    			}],
    			"name": "%s",
    			"storeId": "%s",
    			"timeCopy": 0
    			}
    			""".formatted(couponCode, loginInfo.getStoreID(), discountName, loginInfo.getStoreID());
    	Response createRecord = api.post(CREATE_PRODUCT_DISCOUNT_PATH, loginInfo.getAccessToken(), body);
    	createRecord.then().statusCode(200);
    	return createRecord.jsonPath();
    }  
    public int createProductDiscountCode() {
    	return createProductDiscountCodeJsonPath().getInt("id");
    }    
    
    //Temporary function. Will think of a better way to do this
    public JsonPath createServiceDiscountCodeJsonPath() {
    	String discountName = "Auto - [Service] Discount code - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
    	String couponCode = "AUTO" + Instant.now().toEpochMilli();
    	
    	String body = """
    			{
    			"description": "",
    			"discounts": [{
    			"activeDate": "2024-03-06T17:00:00.000Z",
    			"conditions": [{
    			"conditionOption": "CUSTOMER_SEGMENT_ALL_CUSTOMERS",
    			"conditionType": "CUSTOMER_SEGMENT",
    			"values": []
    			}, {
    			"conditionOption": "APPLIES_TO_ENTIRE_ORDER",
    			"conditionType": "APPLIES_TO",
    			"values": []
    			}, {
    			"conditionOption": "MIN_REQUIREMENTS_NONE",
    			"conditionType": "MINIMUM_REQUIREMENTS",
    			"values": [{
    			"conditionValue": 0
    			}]
    			}, {
    			"conditionOption": "PLATFORMS_WEB_ONLY",
    			"conditionType": "PLATFORMS",
    			"values": []
    			}, {
    			"conditionOption": "APPLIES_TO_BRANCH_ALL_BRANCHES",
    			"conditionType": "APPLIES_TO_BRANCH",
    			"values": []
    			}, {
    			"conditionOption": "PAYMENT_METHOD",
    			"conditionType": "PAYMENT_METHOD",
    			"values": []
    			}],
    			"couponCode": "%s",
    			"couponLimitToOne": false,
    			"couponLimitedUsage": false,
    			"couponTotal": null,
    			"couponType": "PERCENTAGE",
    			"couponValue": "1",
    			"expiredDate": "3024-03-08T16:59:59.999Z",
    			"storeId": "%s",
    			"type": "COUPON_SERVICE",
    			"freeShippingProviders": "",
    			"feeShippingType": "FIXED_AMOUNT",
    			"enabledRewards": false,
    			"rewardsDescription": "",
    			"hideInStore": false
    			}],
    			"name": "%s",
    			"storeId": "%s",
    			"timeCopy": 0
    			}
    			""".formatted(couponCode, loginInfo.getStoreID(), discountName, loginInfo.getStoreID());
    	Response createRecord = api.post(CREATE_PRODUCT_DISCOUNT_PATH, loginInfo.getAccessToken(), body);
    	createRecord.then().statusCode(200);
    	return createRecord.jsonPath();
    }  
    public int createServiceDiscountCode() {
    	return createServiceDiscountCodeJsonPath().getInt("id");
    }       
    
    public void deleteDiscount(int discountId) {
        String path = DELETE_DISCOUNT_PATH + discountId;
        Response response = api.delete(path, loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Deleted Discount with id: " + discountId);
    }    
    
}
