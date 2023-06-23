package api.dashboard.products;

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
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.STORE_CURRENCY;

public class CreateProduct {

    // api get path
    String API_POST_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";
    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);

    LoginDashboardInfo loginInfo = new Login().getInfo();
    TaxInfo taxInfo = new VAT().getInfo();
    BranchInfo branchInfo = new BranchManagement().getInfo();
    StoreInfo storeInfo = new StoreInformation().getInfo();
    private static String productName;
    private static String productDescription;
    private static boolean hasModel;
    private static int productID;
    private static List<Integer> variationModelID;
    private boolean manageByIMEI;
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
}
