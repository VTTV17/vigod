package api.dashboard.products;

import api.dashboard.customers.Customers;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.api_body.product.CreateProductBody;
import utilities.data.DataGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static api.dashboard.customers.Customers.segmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRODUCT_DESCRIPTION;
import static utilities.character_limit.CharacterLimit.MAX_WHOLESALE_PRICE_TITLE;
import static utilities.links.Links.STORE_CURRENCY;

public class CreateProduct {

    // api get path
    String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/";
    String API_BRANCH_LIST_PATH = "/storeservice/api/store-branch/active/";
    String API_POST_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";
    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/";
    String CREATE_WHOLESALE_PRICE_PATH = "/itemservice/api/item/wholesale-pricing";

    // pre-info
    private List<Integer> taxList;
    public static List<Integer> branchIDList;
    public static List<String> branchName;
    public static List<String> branchAddress;

    // product info
    public static int withoutVariationListingPrice;
    public static int withoutVariationSellingPrice;
    public static List<Integer> withoutVariationStock;
    public static Map<String, List<String>> variationMap;
    public static List<String> variationList;
    public static List<Integer> variationModelID;
    public static Map<String, List<Integer>> variationStockQuantity;
    public static List<Integer> variationListingPrice;
    public static List<Integer> variationSellingPrice;

    // wholesale product price
    public static List<Integer> variationWholesaleProductPrice;
    public static List<Integer> variationWholesaleProductStock;
    public static int withoutVariationWholesaleProductPrice;
    public static int withoutVariationWholesaleProductStock;

    public static boolean isVariation;
    public static boolean isIMEIProduct;
    public static int productID;
    public static String productName;

    public static int collectionID;

    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);

    public CreateProduct getTaxList() {
        Response taxResponse = api.get(API_TAX_LIST_PATH + storeID, accessToken);
        taxResponse.then().statusCode(200);
        taxList = taxResponse.jsonPath().getList("id");
        return this;
    }

    public void getBranchList() {
        Response branchResponse = api.get(API_BRANCH_LIST_PATH + storeID, accessToken);
        branchResponse.then().statusCode(200);
        branchIDList = branchResponse.jsonPath().getList("id");
        branchName = branchResponse.jsonPath().getList("name");
        branchAddress = branchResponse.jsonPath().getList("address");
    }


    public CreateProduct createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) {
        CreateProduct.isIMEIProduct = isIMEIProduct;

        // is not variation product
        isVariation = false;

        // random some product information
        // product name
        productName = isIMEIProduct ? ("IMEI - without variation - ") : ("Normal - without variation - ");
        productName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        //product description
        String description = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // product tax
        int taxID = taxList.get(nextInt(taxList.size()));

        // generate product info
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, productName, STORE_CURRENCY, description, taxID),
                productBody.withoutVariationInfo(isIMEIProduct, branchIDList, branchName, branchStock),
                productBody.withoutVariationBranchConfig(branchIDList));

        // get product stock and price
        withoutVariationSellingPrice = productBody.withoutVariationSellingPrice;
        withoutVariationListingPrice = productBody.withoutVariationListingPrice;
        withoutVariationStock = productBody.withoutVariationStock;

        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, String.valueOf(body));

        createProductResponse.then().statusCode(201);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        return this;
    }

    public CreateProduct createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) {
        CreateProduct.isIMEIProduct = isIMEIProduct;

        // is variation product
        isVariation = true;

        // random some product information
        // product name
        productName = isIMEIProduct ? ("IMEI - variation - ") : ("Normal - variation - ");
        productName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        //product description
        String description = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // product tax
        int taxID = taxList.get(nextInt(taxList.size()));

        // create body
        CreateProductBody productBody = new CreateProductBody();
        String body = "%s%s%s".formatted(productBody.productInfo(isIMEIProduct, productName, STORE_CURRENCY, description, taxID),
                productBody.variationInfo(isIMEIProduct, branchIDList, branchName, increaseNum, branchStock),
                productBody.variationBranchConfig(branchIDList));

        // get product stock and price
        variationMap = productBody.variationMap;
        variationList = productBody.variationList;
        variationSellingPrice = productBody.variationSellingPrice;
        variationListingPrice = productBody.variationListingPrice;
        variationStockQuantity = productBody.variationStockQuantity;


        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, body);
        createProductResponse.then().statusCode(201);

        // log
        logger.debug("Create product response: %s".formatted(createProductResponse.asPrettyString()));

        // if pre-condition can not complete -> skip test
        createProductResponse.then().statusCode(201);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        // get variation modelID
        variationModelID = createProductResponse.jsonPath().getList("models.id");

        return this;
    }

    public CreateProduct addWholesalePriceProduct() {
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productID));
        String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(Customers.segmentID);
        if (isVariation) {
            variationWholesaleProductPrice = new ArrayList<>();
            variationWholesaleProductStock = new ArrayList<>();
            int numberOfSaleVariation = nextInt(variationList.size()) + 1;
            for (int i = 0; i < numberOfSaleVariation; i++) {
                variationWholesaleProductPrice.add(nextInt(variationSellingPrice.get(i)) + 1);
                variationWholesaleProductStock.add(nextInt(Collections.max(variationStockQuantity.get(variationList.get(i))))  + 1);
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
                        }""".formatted(title, variationWholesaleProductStock.get(i), "%s_%s".formatted(productID, variationModelID.get(i)), STORE_CURRENCY, variationWholesaleProductPrice.get(i), segmentIDs, productID);
                body.append(variationWholesaleConfig);
                body.append((i == (numberOfSaleVariation - 1)) ? "" : ",");
            }

            // set wholesale product stock = 0 if variations have not wholesale product price
            IntStream.range(numberOfSaleVariation, variationList.size()).forEach(i -> variationWholesaleProductStock.add(0));

            // set wholesale product price = selling price if variations have not wholesale product price
            IntStream.range(numberOfSaleVariation, variationList.size()).forEach(i -> variationWholesaleProductPrice.add(variationSellingPrice.get(i)));
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            withoutVariationWholesaleProductPrice = nextInt(withoutVariationSellingPrice) + 1;
            withoutVariationWholesaleProductStock = nextInt(Collections.max(withoutVariationStock)) + 1;
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
                    }""".formatted(title, withoutVariationWholesaleProductStock, productID, STORE_CURRENCY, withoutVariationWholesaleProductPrice, segmentIDs, productID);
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholesale = api.post(CREATE_WHOLESALE_PRICE_PATH, accessToken, String.valueOf(body));
        addWholesale.then().statusCode(200);
        logger.debug("add wholesale price for product: %s".formatted(addWholesale.asPrettyString()));

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
                                    "value": "%s"
                                }
                            ]
                        }
                    ],
                    "conditionType": "ALL",
                    "lstProduct": [],
                    "itemType": "BUSINESS_PRODUCT",
                    "bcStoreId": "%s"
                }""".formatted(collectionName, productName, storeID);
        Response createCollection = api.post(CREATE_PRODUCT_COLLECTION_PATH + storeID, accessToken, body);

        createCollection.then().statusCode(200);

        collectionID = createCollection.jsonPath().getInt("id");
    }
}
