package api.dashboard.products;

import api.dashboard.customers.Customers;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.dashboard.customers.Customers.segmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;
import static utilities.links.Links.STORE_CURRENCY;

public class CreateProduct {

    // api get path
    String API_TAX_LIST_PATH = "/storeservice/api/tax-settings/store/";
    String API_BRANCH_LIST_PATH = "/storeservice/api/store-branch/active/";
    String API_POST_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";

    //    String PRODUCT_LIST_PATH = "/itemservice/api/store/dashboard/";
    String PRODUCT_DETAIL_PATH = "/itemservice/api/beehive-items/";
    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/";
    String CREATE_WHOLESALE_PRICE_PATH = "/itemservice/api/item/wholesale-pricing";

    // pre-info
    private List<Integer> taxList;
    private List<String> taxName;
    private List<Float> taxRate;
    public static List<Integer> branchIDList;
    public static List<String> branchName;
    public static List<String> branchAddress;

    // product info
    public static int withoutVariationListingPrice;
    public static int withoutVariationSellingPrice;
    public static int withoutVariationCostPrice;
    public static int withoutVariationStock;
    public static Map<String, List<String>> variationMap;
    public static List<String> variationList;
    public static List<Integer> variationModelID;
    public static List<Integer> variationStockQuantity;
    public static List<Integer> variationListingPrice;
    public static List<Integer> variationSellingPrice;
    public static List<Integer> variationCostPrice;

    // wholesale product price
    public static List<Integer> wholesaleProductVariationSalePrice;
    public static List<Integer> wholesaleProductVariationSaleStock;
    public static int wholesaleProductWithoutVariationSalePrice;
    public static int wholesaleProductWithoutVariationSaleStock;

    public static boolean isVariation;
    public static boolean isIMEIProduct;
    public static int productID;
    public static String productName;

    public static int collectionID;

    API api = new API();

    Logger logger = LogManager.getLogger(CreateProduct.class);

    public CreateProduct getTaxList() {
        Response taxResponse = api.get(API_TAX_LIST_PATH + storeID, accessToken);
        taxList = taxResponse.jsonPath().getList("id");
        taxName = taxResponse.jsonPath().getList("name");
        taxRate = taxResponse.jsonPath().getList("rate");
        return this;
    }

    public void getBranchList() {

        Response branchResponse = api.get(API_BRANCH_LIST_PATH + storeID, accessToken);
        branchIDList = branchResponse.jsonPath().getList("id");
        branchName = branchResponse.jsonPath().getList("name");
        branchAddress = branchResponse.jsonPath().getList("address");
    }


    public CreateProduct createWithoutVariationProduct(boolean isIMEIProduct, Integer... stockQuantity) {
        CreateProduct.isIMEIProduct = isIMEIProduct;

        // is not variation product
        isVariation = false;

        // random some product information
        // product name
        productName = randomAlphabetic(nextInt(MAX_PRODUCT_NAME) + 1);

        //product description
        String description = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // listing price
        withoutVariationListingPrice = (int) (Math.random() * MAX_PRICE);

        // selling price
        withoutVariationSellingPrice = (int) (Math.random() * withoutVariationListingPrice);

        // cost price
        withoutVariationCostPrice = (int) (Math.random() * withoutVariationSellingPrice);

        // product dimension
        int weight = nextInt(MAX_WEIGHT);
        int height = nextInt(MAX_HEIGHT);
        int length = nextInt(MAX_LENGTH);
        int width = nextInt(MAX_WIDTH);

        // product tax
        int taxID = taxList.get(nextInt(taxList.size()));

        // setting display if out of stock
        boolean showOutOfStock = nextBoolean();

        // setting hide remaining stock
        boolean isHideStock = nextBoolean();

        // create body
        StringBuilder body = new StringBuilder("""
                {
                    "categories": [
                        {
                            "id": null,
                            "level": 1,
                            "cateId": 1014
                        },
                        {
                            "id": null,
                            "level": 2,
                            "cateId": 1680
                        }
                    ],
                    "name": "%s",
                    "cateId": 1680,
                    "currency": "%s",
                    "description": "%s",
                    "itemType": "BUSINESS_PRODUCT",
                    "discount": 0,
                    "costPrice": %s,
                    "orgPrice": %s,
                    "newPrice": %s,
                    "totalComment": 0,
                    "totalLike": 0,
                    "images": [
                        {
                            "imageUUID": "52385f81-bc92-4169-b8e5-9b3b7d4d5842",
                            "urlPrefix": "https://d3a0f2zusjbf7r.cloudfront.net",
                            "extension": "jpg"
                        }
                    ],
                    "totalItem": 0,
                    "shippingInfo": {
                        "weight": %s,
                        "height": %s,
                        "length": %s,
                        "width": %s
                    },
                    "parentSku": "",
                    "models": [],
                    "priority": "",
                    "taxId": %s,
                    "quantityChanged": true,
                    "bcoin": 0,
                    "isSelfDelivery": false,
                    "showOutOfStock": %s,
                    "barcode": null,
                    "isHideStock": %s,
                    "conversionUnitId": null,
                    "onApp": true,
                    "onWeb": true,
                    "inStore": true,
                    "inGosocial": true,
                    "enabledListing": false,
                    "lstInventory": [""".formatted(productName, STORE_CURRENCY, description, withoutVariationCostPrice, withoutVariationListingPrice, withoutVariationSellingPrice, weight, height, length, width, taxID, showOutOfStock, isHideStock));

        // init stock quantity get
        withoutVariationStock = stockQuantity.length == 0 ? nextInt(MAX_STOCK_QUANTITY) : stockQuantity[0];

        // set stock quantity for each branch
        for (int i = 0; i < branchIDList.size(); i++) {

            // generate stock object
            String branchStock = """
                    {
                                "branchId": %s,
                                "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                "inventoryCurrent": 0,
                                "inventoryStock": %s,
                                "inventoryType": "SET"
                            }""".formatted(branchIDList.get(i), withoutVariationStock);

            // add stock object to body
            // if is not last branch => add "," and add create new branch stock
            // else add "]," to close "lstInventory" array
            body.append(branchStock).append(i < branchIDList.size() - 1 ? "," : "],");
        }

        // add serial number for imei product
        if (!isIMEIProduct) {
            // set inventory manage type: PRODUCT
            body.append("\"inventoryManageType\": \"PRODUCT\"}");
        } else {
            // set inventory manage type: IMEI
            body.append("\"inventoryManageType\": \"IMEI_SERIAL_NUMBER\",\"itemModelCodeDTOS\": [");

            // add IMEI for each branch
            for (int branchIndex = 0; branchIndex < branchIDList.size(); branchIndex++) {
                // get number of IMEI per branch
                for (int i = 0; i < withoutVariationStock; i++) {
                    // generate IMEI object
                    // IMEI format: branchName_IMEI_Index
                    body.append("""
                            {
                                        "branchId": %s,
                                        "code": "%s",
                                        "status": "AVAILABLE"
                                    }
                            """.formatted(branchIDList.get(branchIndex), "%s_IMEI_%s".formatted(branchName.get(branchIndex), i)));

                    // add IMEI object to body
                    // add "," if is not last stock
                    body.append((branchIndex == (branchIDList.size() - 1)) ? ((i == (withoutVariationStock - 1)) ? "" : ",") : ",");
                }
            }

            // after add IMEI/Serial Number
            // add "]" to close itemModelCodeDTOS array
            // add "}" to close body object
            body.append("]}");
        }

        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, String.valueOf(body));

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        return this;
    }

    /**
     * generate Variation value
     */
    private List<String> generateListString(int size) {
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randomList.add(RandomStringUtils.randomAlphanumeric(MAX_VARIATION_VALUE));
        }
        return randomList;
    }

    /**
     * generate variation maps (variation name : get variation value)
     */
    public Map<String, List<String>> randomVariationMap() {
        // init variation map
        // key: variation name
        // values: get of variation name
        Map<String, List<String>> map = new HashMap<>();

        // generate number of variation
        int variationNum = RandomUtils.nextInt(MAX_VARIATION_QUANTITY) + 1;

        // init get number of each variation
        List<Integer> numberOfVariationValue = new ArrayList<>();

        // generate number variation value of first variation
        numberOfVariationValue.add(RandomUtils.nextInt(MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION) + 1);

        // get number variation value of other variation
        for (int i = 1; i < variationNum; i++) {
            int prevMulti = 1;
            for (int id = 0; id < i; id++) {
                prevMulti = prevMulti * numberOfVariationValue.get(id);
            }
            numberOfVariationValue.add(RandomUtils.nextInt(Math.min((MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS / prevMulti), MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION)) + 1);
        }

        // generate random data for variation map
        for (Integer num : numberOfVariationValue) {
            map.put(RandomStringUtils.randomAlphanumeric(MAX_VARIATION_NAME), generateListString(num));
        }

        // return variation map
        return map;
    }


    /**
     * <p> get get variation value after mixed variation</p>
     * <p> example: var1 = {a, b, c} and var2 = {d}</p>
     * <p> with above variations, we have 3 variation value {a|d, b|d, c|d}</p>
     */
    public List<String> mixVariationValue(List<String> variationValueList1, List<String> variationValueList2) {
        List<String> mixedVariationValueList = new ArrayList<>();
        for (String var1 : variationValueList1) {
            for (String var2 : variationValueList2) {
                mixedVariationValueList.add(var1 + "|" + var2);
            }
        }
        return mixedVariationValueList;
    }


    public CreateProduct createVariationProduct(boolean isIMEIProduct, int... stock) {
        CreateProduct.isIMEIProduct = isIMEIProduct;

        // is variation product
        isVariation = true;

        // random some product information
        // product name
        productName = randomAlphabetic(nextInt(MAX_PRODUCT_NAME) + 1);

        //product description
        String description = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // product dimension
        int weight = nextInt(MAX_WEIGHT);
        int height = nextInt(MAX_HEIGHT);
        int length = nextInt(MAX_LENGTH);
        int width = nextInt(MAX_WIDTH);

        // product tax
        int taxID = taxList.get(nextInt(taxList.size()));

        // setting display if out of stock
        boolean showOutOfStock = nextBoolean();

        // setting hide remaining stock
        boolean isHideStock = nextBoolean();

        // create body
        StringBuilder body = new StringBuilder("""
                {
                    "categories": [
                        {
                            "id": null,
                            "level": 1,
                            "cateId": 1014
                        },
                        {
                            "id": null,
                            "level": 2,
                            "cateId": 1680
                        }
                    ],
                    "name": "%s",
                    "cateId": 1680,
                    "currency": "%s",
                    "description": "%s",
                    "itemType": "BUSINESS_PRODUCT",
                    "discount": 0,
                    "totalComment": 0,
                    "totalLike": 0,
                    "images": [
                        {
                            "imageUUID": "52385f81-bc92-4169-b8e5-9b3b7d4d5842",
                            "urlPrefix": "https://d3a0f2zusjbf7r.cloudfront.net",
                            "extension": "jpg"
                        }
                    ],
                    "totalItem": 0,
                    "shippingInfo": {
                        "weight": %s,
                        "height": %s,
                        "length": %s,
                        "width": %s
                    },
                    "parentSku": "",
                    "priority": "",
                    "taxId": %s,
                    "quantityChanged": true,
                    "bcoin": 0,
                    "isSelfDelivery": false,
                    "showOutOfStock": %s,
                    "barcode": null,
                    "isHideStock": %s,
                    "conversionUnitId": null,
                    "onApp": true,
                    "onWeb": true,
                    "inStore": true,
                    "inGosocial": true,
                    "enabledListing": false,
                    "models": [""".formatted(productName, STORE_CURRENCY, description, weight, height, length, width, taxID, showOutOfStock, isHideStock));

        // generate variation map
        variationMap = randomVariationMap();

        // get variation name with format varName1|varName2|...
        List<String> varName = new ArrayList<>(variationMap.keySet());
        StringBuilder variationName = new StringBuilder();
        for (int i = 0; i < varName.size(); i++)
            variationName.append(varName.get(i)).append((i < varName.size() - 1) ? "|" : "");

        // get variation value get
        // variationValue format: varValue1|varValue2|...
        List<List<String>> varValueList = new ArrayList<>(variationMap.values());
        variationList = varValueList.get(0);
        if (varValueList.size() > 1) {
            for (int i = 1; i < varValueList.size(); i++) {
                variationList = mixVariationValue(variationList, varValueList.get(i));
            }
        }

        // variation price
        // generate listing price
        // condition: listing price <= MAX_PRICE
        variationListingPrice = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationListingPrice.add((int) (Math.random() * MAX_PRICE));
        }

        // generate selling price
        // condition: selling price <= listing price
        variationSellingPrice = new ArrayList<Integer>();
        for (int i = 0; i < variationList.size(); i++) {
            variationSellingPrice.add((int) (Math.random() * variationListingPrice.get(i)));
        }

        // generate cost price
        // condition: cost price <= selling price
        variationCostPrice = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationCostPrice.add((int) (Math.random() * variationSellingPrice.get(i)));
        }

        // generate variation stock for each variation
        variationStockQuantity = new ArrayList<>();
        int startQuantity = stock.length > 0 ? stock[0] : nextInt(MAX_STOCK_QUANTITY);
        int increaseQuantity = stock.length > 1 ? stock[1] : nextInt((MAX_STOCK_QUANTITY - startQuantity) / (1000 * varValueList.size()));
        variationStockQuantity.add(startQuantity);
        for (int i = 1; i < variationList.size(); i++) {
            variationStockQuantity.add(variationStockQuantity.get(i - 1) + increaseQuantity);
        }

        // set variation stock for all variation
        // stock format:
        // IMEI product "models": [{"lstInventory": [ {variation1_branch1_Stock}, {variation1_branch2_Stock}, ...], "itemModelCodeDTOS": [ ...]}, {"lstInventory": [...], "itemModelCodeDTOS": [...]}],
        // Normal product "models" : [{"lstInventory": [ {variation1_branch1_Stock}, {variation1_branch2_Stock}, ...]}, {"lstInventory": [... ]}],
        for (int i = 0; i < variationList.size(); i++) {
            String variationStock = """
                    {
                                "name": "%s",
                                "label": "%s",
                                "orgPrice": %s,
                                "discount": 0,
                                "newPrice": %s,
                                "totalItem": 0,
                                "sku": "",
                                "newStock": 0,
                                "costPrice": %s,
                                "lstInventory": [""".formatted(variationList.get(i), variationName, variationListingPrice.get(i), variationSellingPrice.get(i), variationCostPrice.get(i));
            body.append(variationStock);

            // set stock quantity for each branch
            for (int branchId = 0; branchId < branchIDList.size(); branchId++) {

                // generate stock object
                String branchStock = """
                        {
                                    "branchId": %s,
                                    "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                    "inventoryCurrent": 0,
                                    "inventoryStock": %s,
                                    "inventoryType": "SET"
                                }""".formatted(branchIDList.get(branchId), variationStockQuantity.get(i));

                // add stock object to body
                body.append(branchStock);

                // if is not last branch => add ","
                // check is last branch:
                // IMEI product => add "],"
                // Normal product => check is not last variation => add "]}," else add "]}],"
                body.append((branchId < (branchIDList.size() - 1)) ? "," : (isIMEIProduct ? "]," : ((i < (variationList.size() - 1)) ? "]}," : "]}],")));
            }

            // add serial number for imei product
            if (isIMEIProduct) {
                // set inventory manage type: IMEI
                body.append("\"itemModelCodeDTOS\": [");
                // add IMEI for each branch
                for (int branchIndex = 0; branchIndex < branchIDList.size(); branchIndex++) {
                    // get number of IMEI per branch
                    for (int id = 0; id < variationStockQuantity.get(i); id++) {
                        // generate IMEI object
                        // IMEI format: branchName_IMEI_Index
                        body.append("""
                                {
                                            "branchId": %s,
                                            "code": "%s",
                                            "status": "AVAILABLE"
                                        }
                                """.formatted(branchIDList.get(branchIndex), "%s%s_IMEI_%s".formatted(variationList.get(i), branchName.get(branchIndex), id)));

                        // add IMEI object to body
                        body.append(((branchIndex == (branchIDList.size() - 1)) && (id == (variationStockQuantity.get(i) - 1))) ? "" : ",");
                    }
                }

                // check is not last variation: add "]}," else add "]}],"
                body.append(i < variationList.size() - 1 ? "]}," : "]}],");
            }
        }

        // add inventory by
        body.append(isIMEIProduct ? "\"inventoryManageType\": \"IMEI_SERIAL_NUMBER\"}" : "\"inventoryManageType\": \"PRODUCT\"}");

        // get remaining stock
        variationStockQuantity.replaceAll(stockQuantity -> stockQuantity * branchIDList.size());

        // post without variation product
        Response createProductResponse = api.post(API_POST_PRODUCT_PATH, accessToken, String.valueOf(body));

        // log
        logger.debug("Create product response: %s".formatted(createProductResponse.asPrettyString()));

        // if pre-condition can not complete -> skip test
        createProductResponse.then().statusCode(200);

        // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");

        // Variation product
        // get variation modelID
        variationModelID = createProductResponse.jsonPath().getList("models.id");

        // get variation get
        variationList = createProductResponse.jsonPath().getList("models.orgName");

        // get variation price
        variationListingPrice = new ArrayList<>();
        variationSellingPrice = new ArrayList<>();
        variationCostPrice = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationListingPrice.add((int) createProductResponse.jsonPath().getFloat("models.orgPrice[%s]".formatted(i)));
            variationSellingPrice.add((int) createProductResponse.jsonPath().getFloat("models.newPrice[%s]".formatted(i)));
            variationCostPrice.add((int) createProductResponse.jsonPath().getFloat("models.costPrice[%s]".formatted(i)));
        }
        return this;
    }

    public void getProductInformation(int... productID) {
        int prodID = productID.length == 0 ? CreateProduct.productID : productID[0];

        Response productDetailResponse = api.get(PRODUCT_DETAIL_PATH + prodID, accessToken);

        isVariation = productDetailResponse.jsonPath().getList("models").size() > 0;

        if (isVariation) {
            // Variation product
            // get variation modelID
            variationModelID = productDetailResponse.jsonPath().getList("models.id");

            // get variation get
            variationList = productDetailResponse.jsonPath().getList("models.orgName");

            // get variation price
            variationListingPrice = new ArrayList<>();
            variationSellingPrice = new ArrayList<>();
            variationCostPrice = new ArrayList<>();
            for (int i = 0; i < variationList.size(); i++) {
                variationListingPrice.add((int) productDetailResponse.jsonPath().getFloat("models.orgPrice[%s]".formatted(i)));
                variationSellingPrice.add((int) productDetailResponse.jsonPath().getFloat("models.newPrice[%s]".formatted(i)));
                variationCostPrice.add((int) productDetailResponse.jsonPath().getFloat("models.costPrice[%s]".formatted(i)));
            }

            // get variation stock
            variationStockQuantity = productDetailResponse.jsonPath().getList("models.remainingItem");
        } else {
            // Without variation product
            // get price
            withoutVariationListingPrice = (int) productDetailResponse.jsonPath().getFloat("orgPrice");
            withoutVariationSellingPrice = (int) productDetailResponse.jsonPath().getFloat("newPrice");
            withoutVariationCostPrice = (int) productDetailResponse.jsonPath().getFloat("costPrice");

            // get stock quantity
            withoutVariationStock = productDetailResponse.jsonPath().getInt("branches[0].totalItem");
        }
    }

    public CreateProduct addWholeSalePriceProduct() {
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productID));
        if (isVariation) {
            wholesaleProductVariationSalePrice = new ArrayList<>();
            wholesaleProductVariationSaleStock = new ArrayList<>();
            for (int i = 0; i < variationList.size(); i++) {
                wholesaleProductVariationSalePrice.add(nextInt(variationSellingPrice.get(i)) + 1);
                wholesaleProductVariationSaleStock.add(nextInt(variationStockQuantity.get(i) / branchIDList.size()) + 1);
                String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
                String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(segmentID);
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
                        }""".formatted(title, wholesaleProductVariationSaleStock.get(i), "%s_%s".formatted(productID, variationModelID.get(i)), STORE_CURRENCY, wholesaleProductVariationSalePrice.get(i), segmentIDs, productID);
                body.append(variationWholesaleConfig);
                body.append((i == (variationList.size() - 1)) ? "" : ",");
            }
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            wholesaleProductWithoutVariationSalePrice = nextInt(withoutVariationSellingPrice) + 1;
            wholesaleProductWithoutVariationSaleStock = nextInt(withoutVariationStock / branchIDList.size()) + 1;
            String segmentIDs = nextBoolean() ? "ALL" : String.valueOf(Customers.segmentID);
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
                    }""".formatted(title, wholesaleProductWithoutVariationSaleStock, productID, STORE_CURRENCY, wholesaleProductWithoutVariationSalePrice, segmentIDs, productID);
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholeSale = api.post(CREATE_WHOLESALE_PRICE_PATH, accessToken, String.valueOf(body));
        logger.debug("add wholesale price for product: %s".formatted(addWholeSale.asPrettyString()));

        return this;
    }

    public void createCollection() {
        String collectionName = randomAlphabetic(nextInt(MAX_PRODUCT_COLLECTION_NAME_LENGTH - MIN_PRODUCT_COLLECTION_NAME_LENGTH) + MIN_PRODUCT_COLLECTION_NAME_LENGTH);
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

        collectionID = createCollection.jsonPath().getInt("id");
    }


}
