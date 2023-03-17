package api.dashboard.products;

import api.dashboard.customers.Customers;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import api.dashboard.setting.VAT;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.api_body.product.CreateProductBody;
import utilities.data.DataGenerator;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.marketing.LoyaltyProgram.apiMembershipStatus;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.apiBranchID;
import static api.dashboard.setting.BranchManagement.apiBranchName;
import static api.dashboard.setting.StoreInformation.apiDefaultLanguage;
import static api.dashboard.setting.StoreInformation.apiStoreLanguageList;
import static api.dashboard.setting.VAT.apiTaxList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRODUCT_DESCRIPTION;
import static utilities.character_limit.CharacterLimit.MAX_WHOLESALE_PRICE_TITLE;
import static utilities.links.Links.STORE_CURRENCY;

public class CreateProduct {

    // api get path
    String API_POST_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";
    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/";
    String CREATE_WHOLESALE_PRICE_PATH = "/itemservice/api/item/wholesale-pricing";

    // product info
    public static Map<String, List<String>> apiVariationMap;
    public static List<String> apiVariationList;
    public static List<Integer> apiVariationModelID;
    public static Map<String, List<Integer>> apiProductStockQuantity;
    public static List<Long> apiProductListingPrice;
    public static List<Long> apiProductSellingPrice;

    // wholesale product price
    public static List<Long> apiWholesaleProductPrice;
    public static List<Float> apiWholesaleProductRate;
    public static List<Integer> apiWholesaleProductStock;
    public static Map<String, List<Boolean>> apiWholesaleProductStatus;

    public static boolean apiIsVariation;
    public static int apiProductID = 0;
    public static String apiProductName;
    public static String apiProductDescription;
    public static int apiTaxID;

    public static int apiCollectionID;
    public static String apiCollectionName;
    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);

    public CreateProduct() {
        if (apiBranchID == null) new BranchManagement().getBranchInformation();
        if (apiTaxList == null) new VAT().getTaxList();
        if (apiStoreLanguageList == null) new StoreInformation().getStoreInformation();
    }


    public CreateProduct createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) {
        // is not variation product
        apiIsVariation = false;

        // random some product information
        // product name
        apiProductName = "[%s] %s".formatted(apiDefaultLanguage, isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
        apiProductName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        //product description
        apiProductDescription = "[%s] product description".formatted(apiDefaultLanguage);

        // product SEO
        long epoch = Instant.now().toEpochMilli();
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(apiDefaultLanguage, epoch);
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(apiDefaultLanguage, epoch);
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(apiDefaultLanguage, epoch);
        String seoURL = "%s%s".formatted(apiDefaultLanguage, epoch);

        // product tax
        apiTaxID = apiTaxList.get(nextInt(apiTaxList.size()));

        // generate product info
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, apiProductName, STORE_CURRENCY, apiProductDescription, apiTaxID, seoTitle, seoDescription, seoKeywords, seoURL),
                productBody.withoutVariationInfo(isIMEIProduct, apiBranchID, apiBranchName, branchStock),
                productBody.withoutVariationBranchConfig(apiBranchID));

        // get product stock and price
        apiVariationList = new ArrayList<>();
        apiVariationList.addAll(productBody.variationList);

        apiProductSellingPrice = new ArrayList<>();
        apiProductSellingPrice.addAll(productBody.productSellingPrice);

        apiProductListingPrice = new ArrayList<>();
        apiProductListingPrice.addAll(productBody.productListingPrice);

        apiProductStockQuantity = new HashMap<>();
        productBody.productStockQuantity.keySet().forEach(key -> apiProductStockQuantity.put(key, productBody.productStockQuantity.get(key)));

        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, body);
        if (createProductResponse.getStatusCode() != 201) {
            logger.error(body);
            logger.error("An occurred when create product. Debug log: \n%s".formatted(createProductResponse.asPrettyString()));
        }
        createProductResponse.then().statusCode(201);

        // get apiProductID for another test
        apiProductID = createProductResponse.jsonPath().getInt("id");

        // init discount information
        initDiscountInformation();


        return this;
    }

    public CreateProduct createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) {
        // is variation product
        apiIsVariation = true;

        // random some product information
        // product name
        apiProductName = "[%s] %s".formatted(apiDefaultLanguage, isIMEIProduct ? ("Auto - IMEI - variation - ") : ("Auto - Normal - variation - "));
        apiProductName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        //product description
        apiProductDescription = "[%s] product description".formatted(apiDefaultLanguage);

        // product SEO
        long epoch = Instant.now().toEpochMilli();
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(apiDefaultLanguage, epoch);
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(apiDefaultLanguage, epoch);
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(apiDefaultLanguage, epoch);
        String seoURL = "%s%s".formatted(apiDefaultLanguage, epoch);

        // product tax
        apiTaxID = apiTaxList.get(nextInt(apiTaxList.size()));

        // create body
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, apiProductName, STORE_CURRENCY, apiProductDescription, apiTaxID, seoTitle, seoDescription, seoKeywords, seoURL),
                productBody.variationInfo(isIMEIProduct, apiBranchID, apiBranchName, increaseNum, branchStock),
                productBody.variationBranchConfig(apiBranchID));

        // get product stock and price
        apiVariationMap = new HashMap<>();
        productBody.variationMap.keySet().forEach(key -> apiVariationMap.put(key, productBody.variationMap.get(key)));

        apiVariationList = new ArrayList<>();
        apiVariationList.addAll(productBody.variationList);

        apiProductSellingPrice = new ArrayList<>();
        apiProductSellingPrice.addAll(productBody.productSellingPrice);

        apiProductListingPrice = new ArrayList<>();
        apiProductListingPrice.addAll(productBody.productListingPrice);

        apiProductStockQuantity = new HashMap<>();
        productBody.productStockQuantity.keySet().forEach(key -> apiProductStockQuantity.put(key, productBody.productStockQuantity.get(key)));


        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, body);
        if (createProductResponse.getStatusCode() != 201) {
            logger.error(body);
            logger.error("An occurred when create product. Debug log: \n%s".formatted(createProductResponse.asPrettyString()));
        }

        // if pre-condition can not complete -> skip test
        createProductResponse.then().statusCode(201);

        // get apiProductID for another test
        apiProductID = createProductResponse.jsonPath().getInt("id");

        // get variation modelID
        apiVariationModelID = createProductResponse.jsonPath().getList("models.id");

        // init discount information
        initDiscountInformation();

        return this;
    }

    void initDiscountInformation() {
        // init wholesale product status
        apiWholesaleProductStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiWholesaleProductStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> false).toList()));

        // init flash sale status
        apiFlashSaleStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiFlashSaleStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init discount campaign status
        apiDiscountCampaignStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiDiscountCampaignStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init flash sale price
        apiFlashSalePrice = new ArrayList<>();
        apiFlashSalePrice.addAll(apiProductSellingPrice);

        // init flash sale stock
        apiFlashSaleStock = new ArrayList<>();
        apiVariationList.forEach(varName -> apiFlashSaleStock.add(Collections.max(apiProductStockQuantity.get(varName))));

        // init product discount campaign price
        apiDiscountCampaignPrice = new ArrayList<>();
        apiDiscountCampaignPrice.addAll(apiProductSellingPrice);

        // init wholesale product price, rate and stock
        apiWholesaleProductPrice = new ArrayList<>();
        apiWholesaleProductPrice.addAll(apiProductSellingPrice);

        apiWholesaleProductRate = new ArrayList<>();
        IntStream.range(0, apiWholesaleProductPrice.size()).forEach(i -> apiWholesaleProductRate.add(Float.valueOf(new DecimalFormat("#.##").format((1 - (float) apiWholesaleProductPrice.get(i) / apiProductSellingPrice.get(i)) * 100))));

        apiWholesaleProductStock = new ArrayList<>();
        apiVariationList.forEach(varName -> apiWholesaleProductStock.add(Collections.max(apiProductStockQuantity.get(varName))));

        // discount code
        apiDiscountCodeStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiDiscountCodeStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // membership
        apiMembershipStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiMembershipStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));
    }

    public CreateProduct addWholesalePriceProduct() {
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(apiProductID));
        String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(Customers.apiSegmentID);
        int num = apiIsVariation ? nextInt(apiVariationList.size()) + 1 : 1;
        if (apiIsVariation) {
            for (int i = 0; i < num; i++) {
                apiWholesaleProductPrice.set(i, nextLong(apiProductSellingPrice.get(i)) + 1);
                apiWholesaleProductStock.set(i, nextInt(Collections.max(apiProductStockQuantity.get(apiVariationList.get(i)))) + 1);
                String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
                String variationWholesaleConfig = """
                        {
                            "id": null,
                            "title": "%s",
                            "minQuatity": %s,
                            "itemModelIds": "%s",
                            "currency": "%s",
                            "price": %s,
                            "segmentIds": "%s",
                            "itemId": "%s",
                            "action": null
                        }""".formatted(title, apiWholesaleProductStock.get(i), "%s_%s".formatted(apiProductID, apiVariationModelID.get(i)), STORE_CURRENCY, apiWholesaleProductPrice.get(i), segmentIDs, apiProductID);
                body.append(variationWholesaleConfig);
                body.append((i == (num - 1)) ? "" : ",");
            }
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            apiWholesaleProductPrice.set(0, nextLong(apiProductSellingPrice.get(0)) + 1);
            apiWholesaleProductStock.set(0, nextInt(Collections.max(apiProductStockQuantity.get(null))) + 1);
            String variationWholesaleConfig = """
                    {
                        "id": null,
                        "title": "%s",
                        "minQuatity": %s,
                        "itemModelIds": "%s",
                        "currency": "%s",
                        "price": %s,
                        "segmentIds": "%s",
                        "itemId": "%s",
                        "action": null
                    }""".formatted(title, apiWholesaleProductStock.get(0), apiProductID, STORE_CURRENCY, apiWholesaleProductPrice.get(0), segmentIDs, apiProductID);
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholesale = api.post(CREATE_WHOLESALE_PRICE_PATH, accessToken, String.valueOf(body));
        addWholesale.then().statusCode(200);

        // update wholesale product rate
        apiWholesaleProductRate = new ArrayList<>();
        IntStream.range(0, apiWholesaleProductPrice.size()).forEach(i -> apiWholesaleProductRate.add(Float.valueOf(new DecimalFormat("#.##").format((1 - (float) apiWholesaleProductPrice.get(i) / apiProductSellingPrice.get(i)) * 100))));

        // update wholesale product status
        apiBranchName.forEach(brName -> apiWholesaleProductStatus.put(brName, IntStream.range(0, apiIsVariation ? apiVariationList.size() : 1).mapToObj(i -> i < num).toList()));
        return this;
    }

    public void createCollection() {
        apiCollectionName = "Auto - Collections - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "collectionType": "AUTOMATED",
                    "lstImage": [],
                    "lstCondition": [
                        {
                            "conditionField": "PRODUCT_NAME",
                            "operand": "CONTAINS",
                            "values": [
                                {
                                    "value": "Auto"
                                }
                            ]
                        }
                    ],
                    "conditionType": "ALL",
                    "lstProduct": [],
                    "itemType": "BUSINESS_PRODUCT",
                    "bcStoreId": "%s"
                }""".formatted(apiCollectionName, apiStoreID);
        Response createCollection = api.post(CREATE_PRODUCT_COLLECTION_PATH + apiStoreID, accessToken, body);

        createCollection.then().statusCode(200);

        apiCollectionID = createCollection.jsonPath().getInt("id");
    }
}
