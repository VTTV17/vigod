package api.dashboard;

import io.restassured.RestAssured;
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

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;
import static utilities.links.Links.STORE_CURRENCY;

public class APICommon {
    public static String URI_DASHBOARD = "https://api.beecow.info";
    public static String DASHBOARD_API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public static String DASHBOARD_API_TAX_LIST = "/storeservice/api/tax-settings/store/";
    public static String DASHBOARD_API_BRANCH_LIST = "/storeservice/api/store-branch/active/";
    public static String DASHBOARD_API_POST_PRODUCT = "/itemservice/api/items?fromSource=DASHBOARD";
    public static String accessToken;
    public static int storeID;
    public static List<Integer> taxList;
    public static List<String> taxName;
    public static List<Double> taxRate;
    public static List<Integer> branchList;
    public static List<String> branchName;
    public static List<String> branchAddress;
    public static Integer withoutVariationStock;
    public static Map<String, List<String>> variationMap;
    public static List<String> variationList;
    public static List<Integer> variationStockQuantity;
    public static List<Integer> variationListingPrice;
    public static List<Integer> variationSellingPrice;
    public static List<Integer> variationCostPrice;

    Logger logger = LogManager.getLogger(APICommon.class);


    public static int productID;
    API api = new API();

    public void loginToDashboard(String account, String password) {
        RestAssured.baseURI = URI_DASHBOARD;
        String body = """
                {
                    "username": "%s",
                    "password": "%s"
                }""".formatted(account, password);
        Response loginResponse = api.login(DASHBOARD_API_LOGIN_PATH, body);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        storeID = loginResponse.jsonPath().getInt("store.id");
    }

    public void getTaxList() {
        RestAssured.baseURI = URI_DASHBOARD;
        Response taxResponse = api.list(DASHBOARD_API_TAX_LIST + storeID, accessToken);
        taxList = taxResponse.jsonPath().getList("id");
        taxName = taxResponse.jsonPath().getList("name");
        taxRate = taxResponse.jsonPath().getList("rate");
    }

    public void getBranchList() {
        RestAssured.baseURI = URI_DASHBOARD;
        Response branchResponse = api.list(DASHBOARD_API_BRANCH_LIST + storeID, accessToken);
        branchList = branchResponse.jsonPath().getList("id");
        branchName = branchResponse.jsonPath().getList("name");
        branchAddress = branchResponse.jsonPath().getList("address");

    }

    String account = "stgauto@nbobd.com";
    String password = "Abc@12345";


    public void createWithoutVariationProduct(boolean isIMEIProduct, Integer... stockQuantity) {
        // login and get storeID + accessToken
        loginToDashboard(account, password);

        // get taxName and taxList
        getTaxList();

        // get branchName and branchList
        getBranchList();

        // random some product information
        // product name
        String name = randomAlphabetic(nextInt(MAX_PRODUCT_NAME) + 1);

        //product description
        String description = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);

        // listing price
        int orgPrice = (int) (Math.random() * MAX_PRICE);

        // selling price
        int newPrice = (int) (Math.random() * orgPrice);

        // cost price
        int costPrice = (int) (Math.random() * newPrice);

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
                    "lstInventory": [""".formatted(name, STORE_CURRENCY, description, costPrice, orgPrice, newPrice, weight, height, length, width, taxID, showOutOfStock, isHideStock));

        // init stock quantity list
        withoutVariationStock = stockQuantity.length == 0 ? nextInt(MAX_STOCK_QUANTITY) : stockQuantity[0];

        // set stock quantity for each branch
        for (int i = 0; i < branchList.size(); i++) {

            // generate stock object
            String branchStock = """
                    {
                                "branchId": %s,
                                "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                "inventoryCurrent": 0,
                                "inventoryStock": %s,
                                "inventoryType": "SET"
                            }""".formatted(branchList.get(i), withoutVariationStock);

            // add stock object to body
            body.append(branchStock).append(i < branchList.size() - 1 ? "," : "],");
        }

        // add serial number for imei product
        if (!isIMEIProduct) {
            // set inventory manage type: PRODUCT
            body.append("\"inventoryManageType\": \"PRODUCT\"}");
        } else {
            // set inventory manage type: IMEI
            body.append("\"inventoryManageType\": \"IMEI_SERIAL_NUMBER\",\"itemModelCodeDTOS\": [");

            // add IMEI for each branch
            for (int branchIndex = 0; branchIndex < branchList.size(); branchIndex++) {
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
                            """.formatted(branchList.get(branchIndex), "%s_IMEI_%s".formatted(branchName.get(branchIndex), i)));

                    // add IMEI object to body
                    body.append((branchIndex == (branchList.size() - 1)) ? ((i == (withoutVariationStock - 1)) ? "" : ",") : ",");
                }
                body.append("]}");
            }

            // set API URI
            RestAssured.baseURI = URI_DASHBOARD;

            // post without variation product
            Response createProductResponse = api.create(DASHBOARD_API_POST_PRODUCT, accessToken, String.valueOf(body));

            // get productID for another test
            productID = createProductResponse.jsonPath().getInt("id");

            createProductResponse.prettyPrint();
        }
    }

    /**
     * generate Variation value
     */
    private List<String> generateListString(int size, int length) {
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randomList.add(RandomStringUtils.randomAlphanumeric(length));
        }
        return randomList;
    }

    /**
     * generate variation maps <variation name : list variation value>
     */
    public Map<String, List<String>> randomVariationMap() {
        Map<String, List<String>> map = new HashMap<>();
        int variationNum = RandomUtils.nextInt(MAX_VARIATION_QUANTITY) + 1;
        List<Integer> numberOfVariationValue = new ArrayList<>();
        numberOfVariationValue.add(RandomUtils.nextInt(MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION) + 1);
        for (int i = 1; i < variationNum; i++) {
            int prevMulti = 1;
            for (int id = 0; id < i; id++) {
                prevMulti = prevMulti * numberOfVariationValue.get(id);
            }
            numberOfVariationValue.add(RandomUtils.nextInt(Math.min((MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS / prevMulti), MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION)) + 1);
        }
        for (Integer num : numberOfVariationValue) {
            map.put(RandomStringUtils.randomAlphanumeric(MAX_VARIATION_NAME), generateListString(num, MAX_VARIATION_VALUE));
        }
        return map;
    }

    public List<String> mixVariationValue(List<String> variationValueList1, List<String> variationValueList2) {
        List<String> mixedVariationValueList = new ArrayList<>();
        for (String var1 : variationValueList1) {
            for (String var2 : variationValueList2) {
                mixedVariationValueList.add(var1 + " " + var2);
            }
        }
        return mixedVariationValueList;
    }


    public void createVariationProduct(boolean isIMEIProduct) {
        // login and get storeID + accessToken
        loginToDashboard(account, password);

        // get taxName and taxList
        getTaxList();

        // get branchName and branchList
        getBranchList();

        // random some product information
        // product name
        String name = randomAlphabetic(nextInt(MAX_PRODUCT_NAME) + 1);

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
                    "models": [""".formatted(name, STORE_CURRENCY, description, weight, height, length, width, taxID, showOutOfStock, isHideStock));

        variationMap = randomVariationMap();
        // get variation name
        List<String> varName = new ArrayList<>(variationMap.keySet());
        StringBuilder variationName = new StringBuilder();
        for (int i = 0; i < varName.size(); i++)
            variationName.append(varName.get(i)).append((i < varName.size() - 1) ? "|" : "");

        // get variation value list
        List<List<String>> varValueList = new ArrayList<>(variationMap.values());
        variationList = varValueList.get(0);
        if (varValueList.size() > 1) {
            for (int i = 1; i < varValueList.size(); i++) {
                variationList = mixVariationValue(variationList, varValueList.get(i));
            }
        }

        // variation price

        variationListingPrice = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationListingPrice.add((int) (Math.random() * MAX_PRICE));
        }


        variationSellingPrice = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationSellingPrice.add((int) (Math.random() * variationListingPrice.get(i)));
        }


        variationCostPrice = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationCostPrice.add((int) (Math.random() * variationSellingPrice.get(i)));
        }


        variationStockQuantity = new ArrayList<>();
        for (int i = 0; i < variationList.size(); i++) {
            variationStockQuantity.add(nextInt(MAX_STOCK_QUANTITY));
        }


        // set variation stock
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
                                "lstInventory": [""".formatted(variationList.get(i).replace(" ", "|"), variationName, variationListingPrice.get(i), variationSellingPrice.get(i), variationCostPrice.get(i));
            body.append(variationStock);

            // set stock quantity for each branch
            for (int branchId = 0; branchId < branchList.size(); branchId++) {

                // generate stock object
                String branchStock = """
                        {
                                    "branchId": %s,
                                    "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                    "inventoryCurrent": 0,
                                    "inventoryStock": %s,
                                    "inventoryType": "SET"
                                }""".formatted(branchList.get(branchId), variationStockQuantity.get(i));

                // add stock object to body
                body.append(branchStock).append(branchId < branchList.size() - 1 ? "," : "],");
            }

            // add serial number for imei product
            if (isIMEIProduct) {
                // set inventory manage type: IMEI
                body.append("\"itemModelCodeDTOS\": [");

                // add IMEI for each branch
                for (int branchIndex = 0; branchIndex < branchList.size(); branchIndex++) {
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
                                """.formatted(branchList.get(branchIndex), "%s%s_IMEI_%s".formatted(variationList.get(i), branchName.get(branchIndex), id)));

                        // add IMEI object to body
                        body.append(((branchIndex == (branchList.size() - 1)) && (id == (variationStockQuantity.get(i) - 1))) ? "" : ",");
                    }
                }
            }
            body.append(i < variationList.size() - 1 ? "]}," : "]}],");
        }

        body.append(isIMEIProduct ? "\"inventoryManageType\": \"IMEI_SERIAL_NUMBER\"}" : "\"inventoryManageType\": \"PRODUCT\"}");

        // set API URI
        RestAssured.baseURI = URI_DASHBOARD;

        // post without variation product
        Response createProductResponse = api.create(DASHBOARD_API_POST_PRODUCT, accessToken, String.valueOf(body));

         // get productID for another test
        productID = createProductResponse.jsonPath().getInt("id");
    }

}
