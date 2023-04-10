package api.dashboard.products;

import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import api.dashboard.setting.VAT;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.api_body.product.CreateProductBody;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;

import java.time.Instant;
import java.util.*;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_WHOLESALE_PRICE_TITLE;
import static utilities.links.Links.STORE_CURRENCY;

public class CreateProduct {

    // api get path
    String API_POST_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";
    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/";
    String CREATE_WHOLESALE_PRICE_PATH = "/itemservice/api/item/wholesale-pricing";

    // product info
    public static List<String> apiVariationList;
    public static List<Integer> apiVariationModelID;
    public static Map<String, List<Integer>> apiProductStockQuantity;
    public static List<Long> apiProductSellingPrice;

    // wholesale product price
    public static List<Float> apiWholesaleProductRate;

    public static int apiCollectionID;
    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);

    LoginDashboardInfo loginInfo;
    TaxInfo taxInfo;
    BranchInfo branchInfo;
    StoreInfo storeInfo;
    static String productName;
    static String productDescription;
    static boolean hasModel;
    static int productID;

    {
        loginInfo = new Login().getInfo();
        taxInfo = new VAT().getInfo();
        branchInfo = new BranchManagement().getInfo();
        storeInfo = new StoreInformation().getInfo();
    }

    JsonPath createWithoutVariationProductJsonPath(boolean isIMEIProduct, int... branchStock) {
        // is not variation product
        hasModel = false;

        // random some product information
        // product name
        CreateProduct.productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));

        //product description
        CreateProduct.productDescription = "[%s] product description".formatted(storeInfo.getDefaultLanguage());

        // product SEO
        long epoch = Instant.now().toEpochMilli();
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoURL = "%s%s".formatted(storeInfo.getDefaultLanguage(), epoch);

        // product tax
        int taxID = taxInfo.getTaxID().get(nextInt(taxInfo.getTaxID().size()));

        // generate product info
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, CreateProduct.productName, STORE_CURRENCY, productDescription, taxID, seoTitle, seoDescription, seoKeywords, seoURL),
                productBody.withoutVariationInfo(isIMEIProduct, branchInfo.getBranchID(), branchInfo.getBranchName(), branchStock),
                productBody.withoutVariationBranchConfig(branchInfo.getBranchID()));

        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, loginInfo.getAccessToken(), body);
        createProductResponse.then().statusCode(201);

        return createProductResponse.jsonPath();
    }

    public int createWithoutVariationProductAndGetProductID(boolean isIMEIProduct, int... branchStock) {
        return createWithoutVariationProductJsonPath(isIMEIProduct, branchStock).getInt("id");
    }

    public CreateProduct createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) {
        // is not variation product
        hasModel = false;

        // random some product information
        // product name
        CreateProduct.productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));

        //product description
        CreateProduct.productDescription = "[%s] product description".formatted(storeInfo.getDefaultLanguage());

        // product SEO
        long epoch = Instant.now().toEpochMilli();
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoURL = "%s%s".formatted(storeInfo.getDefaultLanguage(), epoch);

        // product tax
        int taxID = taxInfo.getTaxID().get(nextInt(taxInfo.getTaxID().size()));

        // generate product info
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, CreateProduct.productName, STORE_CURRENCY, productDescription, taxID, seoTitle, seoDescription, seoKeywords, seoURL),
                productBody.withoutVariationInfo(isIMEIProduct, branchInfo.getBranchID(), branchInfo.getBranchName(), branchStock),
                productBody.withoutVariationBranchConfig(branchInfo.getBranchID()));
        
        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, loginInfo.getAccessToken(), body);
        if (createProductResponse.getStatusCode() != 201) {
            logger.error(body);
            logger.error("An occurred when create product. Debug log: \n%s".formatted(createProductResponse.asPrettyString()));
        }
        createProductResponse.then().statusCode(201);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");


        return this;
    }

    public CreateProduct createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) {
        // is variation product
        hasModel = true;

        // random some product information
        // product name
        CreateProduct.productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - variation - ") : ("Auto - Normal - variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));

        //product description
        CreateProduct.productDescription = "[%s] product description".formatted(storeInfo.getDefaultLanguage());

        // product SEO
        long epoch = Instant.now().toEpochMilli();
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        String seoURL = "%s%s".formatted(storeInfo.getDefaultLanguage(), epoch);

        // product tax
        int taxID = taxInfo.getTaxID().get(nextInt(taxInfo.getTaxID().size()));

        // create body
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, CreateProduct.productName, STORE_CURRENCY, productDescription, taxID, seoTitle, seoDescription, seoKeywords, seoURL),
                productBody.variationInfo(isIMEIProduct, branchInfo.getBranchID(), branchInfo.getBranchName(), increaseNum, branchStock),
                productBody.variationBranchConfig(branchInfo.getBranchID()));


        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, loginInfo.getAccessToken(), body);
        if (createProductResponse.getStatusCode() != 201) {
            logger.error(body);
            logger.error("An occurred when create product. Debug log: \n%s".formatted(createProductResponse.asPrettyString()));
        }

        // if pre-condition can not complete -> skip test
        createProductResponse.then().statusCode(201);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        // get variation modelID
        apiVariationModelID = createProductResponse.jsonPath().getList("models.id");

        return this;
    }
    
    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return CreateProduct.productName;
    }

    public String getProductDescription() {
        return CreateProduct.productDescription;
    }
    
    public boolean isHasModel() {
        return CreateProduct.hasModel;
    }

    public CreateProduct addWholesalePriceProduct() {
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productID));
        String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(new Customers().getSegmentID());
        int num = hasModel ? nextInt(apiVariationList.size()) + 1 : 1;
        if (hasModel) {
            for (int i = 0; i < num; i++) {
                long price = nextLong(apiProductSellingPrice.get(i)) + 1;
                int stock = nextInt(Collections.max(apiProductStockQuantity.get(apiVariationList.get(i)))) + 1;
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
                        }""".formatted(title, stock, "%s_%s".formatted(productID, apiVariationModelID.get(i)), STORE_CURRENCY, price, segmentIDs, productID);
                body.append(variationWholesaleConfig);
                body.append((i == (num - 1)) ? "" : ",");
            }
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            long price = nextLong(apiProductSellingPrice.get(0)) + 1;
            int stock = nextInt(Collections.max(apiProductStockQuantity.get(null))) + 1;
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
                    }""".formatted(title, stock, productID, STORE_CURRENCY, price, segmentIDs, productID);
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholesale = api.post(CREATE_WHOLESALE_PRICE_PATH, loginInfo.getAccessToken(), String.valueOf(body));
        addWholesale.then().statusCode(200);

        return this;
    }

    public void createCollection() {
        String collectionName = "Auto - Collections - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
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
                }""".formatted(collectionName, loginInfo.getStoreID());
        Response createCollection = api.post(CREATE_PRODUCT_COLLECTION_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);

        createCollection.then().statusCode(200);

        apiCollectionID = createCollection.jsonPath().getInt("id");
    }
}
