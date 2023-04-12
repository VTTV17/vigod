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
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);

    LoginDashboardInfo loginInfo;
    TaxInfo taxInfo;
    BranchInfo branchInfo;
    StoreInfo storeInfo;
    private static String productName;
    private static String productDescription;
    private static boolean hasModel;
    private static int productID;
    private static List<Integer> variationModelID;
    private static int collectionID;
    private boolean manageByIMEI;

    {
        loginInfo = new Login().getInfo();
        taxInfo = new VAT().getInfo();
        branchInfo = new BranchManagement().getInfo();
        storeInfo = new StoreInformation().getInfo();
    }

    boolean showOutOfStock = true;
    boolean hideStock = false;
    boolean enableListing = false;
    boolean showOnApp = true;
    boolean showOnWeb = true;
    boolean showInStore = true;
    boolean showInGoSocial = true;

    public CreateProduct setShowOutOfStock(boolean showOutOfStock) {
        this.showOutOfStock = showOutOfStock;
        return this;
    }

    public CreateProduct setHideStock(boolean hideStock) {
        this.hideStock = hideStock;
        return this;
    }

    JsonPath createWithoutVariationProductJsonPath(boolean isIMEIProduct, int... branchStock) {
        // is not variation product
        hasModel = false;

        // manage by IMEI
        manageByIMEI = isIMEIProduct;

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
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, CreateProduct.productName, STORE_CURRENCY, productDescription, taxID, showOutOfStock, hideStock, enableListing, showOnApp, showOnWeb, showInStore, showInGoSocial, seoTitle, seoDescription, seoKeywords, seoURL),
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

        // manage by IMEI
        manageByIMEI = isIMEIProduct;

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
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, CreateProduct.productName, STORE_CURRENCY, productDescription, taxID, showOutOfStock, hideStock, enableListing, showOnApp, showOnWeb, showInStore, showInGoSocial, seoTitle, seoDescription, seoKeywords, seoURL),
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

        // manage by IMEI
        manageByIMEI = isIMEIProduct;

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
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, CreateProduct.productName, STORE_CURRENCY, productDescription, taxID, showOutOfStock, hideStock, enableListing, showOnApp, showOnWeb, showInStore, showInGoSocial, seoTitle, seoDescription, seoKeywords, seoURL),
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
        variationModelID = createProductResponse.jsonPath().getList("models.id");

        return this;
    }

    public int getProductID() {
        return CreateProduct.productID;
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

    public List<Integer> getVariationModelID() {
        return CreateProduct.variationModelID;
    }

    public List<Long> getProductSellingPrice() {
        return new CreateProductBody().getProductSellingPrice();
    }

    public Map<String, List<Integer>> getProductStockQuantity() {
        return new CreateProductBody().getProductStockQuantity();
    }

    public List<String> getVariationList() {
        return new CreateProductBody().getVariationList();
    }

    public boolean isManageByIMEI() {
        return manageByIMEI;
    }

    public CreateProduct addWholesalePriceProduct() {
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productID));
        String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(new Customers().getSegmentID());
        int num = hasModel ? nextInt(variationModelID.size()) + 1 : 1;
        if (hasModel) {
            for (int i = 0; i < num; i++) {
                long price = nextLong(getProductSellingPrice().get(i)) + 1;
                int stock = nextInt(Collections.max(getProductStockQuantity().get(getVariationList().get(i)))) + 1;
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
                        }""".formatted(title, stock, "%s_%s".formatted(productID, variationModelID.get(i)), STORE_CURRENCY, price, segmentIDs, productID);
                body.append(variationWholesaleConfig);
                body.append((i == (num - 1)) ? "" : ",");
            }
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            long price = nextLong(getProductSellingPrice().get(0)) + 1;
            int stock = nextInt(Collections.max(getProductStockQuantity().get(null))) + 1;
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

        collectionID = createCollection.jsonPath().getInt("id");
    }

    public int getCollectionID() {
        return CreateProduct.collectionID;
    }
}
