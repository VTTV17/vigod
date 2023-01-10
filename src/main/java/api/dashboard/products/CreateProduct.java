package api.dashboard.products;

import api.dashboard.customers.Customers;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.api_body.product.CreateProductBody;
import utilities.data.DataGenerator;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static api.dashboard.marketing.LoyaltyProgram.membershipStatus;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.branchID;
import static api.dashboard.setting.BranchManagement.branchName;
import static api.dashboard.setting.VAT.taxList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
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
    public static Map<String, List<String>> variationMap;
    public static List<String> variationList;
    public static List<Integer> variationModelID;
    public static Map<String, List<Integer>> productStockQuantity;
    public static List<Integer> productListingPrice;
    public static List<Integer> productSellingPrice;

    // wholesale product price
    public static List<Integer> wholesaleProductPrice;
    public static List<Float> wholesaleProductRate;
    public static List<Integer> wholesaleProductStock;
    public static Map<String, List<Boolean>> wholesaleProductStatus;

    public static boolean isVariation;
    public static int productID;
    public static String productName;
    public static String productDescription;
    public static int taxID;

    public static int collectionID;
    public static boolean hasCollections;
    public static String collectionName;
    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);


    public CreateProduct createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) {
        // is not variation product
        isVariation = false;

        // random some product information
        // product name
        productName = isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - ");
        productName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        //product description
        productDescription = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // product tax
        taxID = taxList.get(nextInt(taxList.size()));

        // generate product info
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, productName, STORE_CURRENCY, productDescription, taxID),
                productBody.withoutVariationInfo(isIMEIProduct, branchID, branchName, branchStock),
                productBody.withoutVariationBranchConfig(branchID));

        // get product stock and price
        variationList = new ArrayList<>();
        variationList.addAll(productBody.variationList);

        productSellingPrice = new ArrayList<>();
        productSellingPrice.addAll(productBody.productSellingPrice);

        productListingPrice = new ArrayList<>();
        productListingPrice.addAll(productBody.productListingPrice);

        productStockQuantity = new HashMap<>();
        productBody.productStockQuantity.keySet().forEach(key -> productStockQuantity.put(key, productBody.productStockQuantity.get(key)));

        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, body);
        if (createProductResponse.getStatusCode() != 201) {
            logger.error(body);
            logger.error("An occurred when create product. Debug log: \n%s".formatted(createProductResponse.asPrettyString()));
        }
        createProductResponse.then().statusCode(201);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        // init discount information
        initDiscountInformation();


        return this;
    }

    public CreateProduct createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) {
        // is variation product
        isVariation = true;

        // random some product information
        // product name
        productName = isIMEIProduct ? ("Auto - IMEI - variation - ") : ("Auto - Normal - variation - ");
        productName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        //product description
        productDescription = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // product tax
        taxID = taxList.get(nextInt(taxList.size()));

        // create body
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, productName, STORE_CURRENCY, productDescription, taxID),
                productBody.variationInfo(isIMEIProduct, branchID, branchName, increaseNum, branchStock),
                productBody.variationBranchConfig(branchID));

        // get product stock and price
        variationMap = new HashMap<>();
        productBody.variationMap.keySet().forEach(key -> variationMap.put(key, productBody.variationMap.get(key)));

        variationList = new ArrayList<>();
        variationList.addAll(productBody.variationList);

        productSellingPrice = new ArrayList<>();
        productSellingPrice.addAll(productBody.productSellingPrice);

        productListingPrice = new ArrayList<>();
        productListingPrice.addAll(productBody.productListingPrice);

        productStockQuantity = new HashMap<>();
        productBody.productStockQuantity.keySet().forEach(key -> productStockQuantity.put(key, productBody.productStockQuantity.get(key)));


        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, body);
        if (createProductResponse.getStatusCode() != 201) {
            logger.error(body);
            logger.error("An occurred when create product. Debug log: \n%s".formatted(createProductResponse.asPrettyString()));
        }

        // if pre-condition can not complete -> skip test
        createProductResponse.then().statusCode(201);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        // get variation modelID
        variationModelID = createProductResponse.jsonPath().getList("models.id");

        // init discount information
        initDiscountInformation();

        return this;
    }

    void initDiscountInformation() {
        // init wholesale product status
        wholesaleProductStatus = new HashMap<>();
        branchName.forEach(brName -> wholesaleProductStatus
                .put(brName, IntStream.range(0, variationList.size())
                        .mapToObj(i -> false).toList()));

        // init flash sale status
        flashSaleStatus = new HashMap<>();
        branchName.forEach(brName -> flashSaleStatus
                .put(brName, IntStream.range(0, variationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init discount campaign status
        discountCampaignStatus = new HashMap<>();
        branchName.forEach(brName -> discountCampaignStatus
                .put(brName, IntStream.range(0, variationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init flash sale price
        flashSalePrice = new ArrayList<>();
        flashSalePrice.addAll(productSellingPrice);

        // init flash sale stock
        flashSaleStock = new ArrayList<>();
        variationList.forEach(varName -> flashSaleStock.add(Collections.max(productStockQuantity.get(varName))));

        // init product discount campaign price
        discountCampaignPrice = new ArrayList<>();
        discountCampaignPrice.addAll(productSellingPrice);

        // init wholesale product price, rate and stock
        wholesaleProductPrice = new ArrayList<>();
        wholesaleProductPrice.addAll(productSellingPrice);

        wholesaleProductRate = new ArrayList<>();
        IntStream.range(0, wholesaleProductPrice.size()).forEach(i -> wholesaleProductRate.add(Float.valueOf(new DecimalFormat("#.##").format((1 - (float) wholesaleProductPrice.get(i) / productSellingPrice.get(i)) * 100))));

        wholesaleProductStock = new ArrayList<>();
        variationList.forEach(varName -> wholesaleProductStock.add(Collections.max(productStockQuantity.get(varName))));

        // discount code
        discountCodeStatus = new HashMap<>();
        branchName.forEach(brName -> discountCodeStatus
                .put(brName, IntStream.range(0, variationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // membership
        membershipStatus = new HashMap<>();
        branchName.forEach(brName -> membershipStatus
                .put(brName, IntStream.range(0, variationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));
    }

    public CreateProduct addWholesalePriceProduct() {
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productID));
        String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(Customers.segmentID);
        int num = isVariation ? nextInt(variationList.size()) + 1 : 1;
        if (isVariation) {
            for (int i = 0; i < num; i++) {
                wholesaleProductPrice.set(i, nextInt(productSellingPrice.get(i)) + 1);
                wholesaleProductStock.set(i, nextInt(Collections.max(productStockQuantity.get(variationList.get(i)))) + 1);
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
                        }""".formatted(title, wholesaleProductStock.get(i), "%s_%s".formatted(productID, variationModelID.get(i)), STORE_CURRENCY, wholesaleProductPrice.get(i), segmentIDs, productID);
                body.append(variationWholesaleConfig);
                body.append((i == (num - 1)) ? "" : ",");
            }
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            wholesaleProductPrice.set(0, nextInt(productSellingPrice.get(0)) + 1);
            wholesaleProductStock.set(0, nextInt(Collections.max(productStockQuantity.get(null))) + 1);
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
                    }""".formatted(title, wholesaleProductStock.get(0), productID, STORE_CURRENCY, wholesaleProductPrice.get(0), segmentIDs, productID);
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholesale = api.post(CREATE_WHOLESALE_PRICE_PATH, accessToken, String.valueOf(body));
        addWholesale.then().statusCode(200);

        // update wholesale product rate
        wholesaleProductRate = new ArrayList<>();
        IntStream.range(0, wholesaleProductPrice.size()).forEach(i -> wholesaleProductRate.add(Float.valueOf(new DecimalFormat("#.##").format((1 - (float) wholesaleProductPrice.get(i) / productSellingPrice.get(i)) * 100))));

        // update wholesale product status
        branchName.forEach(brName -> wholesaleProductStatus.put(brName, IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> i < num).toList()));
        return this;
    }

    public void createCollection() {
        hasCollections = true;
        collectionName = "Auto - Collections - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
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
                }""".formatted(collectionName, storeID);
        Response createCollection = api.post(CREATE_PRODUCT_COLLECTION_PATH + storeID, accessToken, body);

        createCollection.then().statusCode(200);

        collectionID = createCollection.jsonPath().getInt("id");
    }
}
