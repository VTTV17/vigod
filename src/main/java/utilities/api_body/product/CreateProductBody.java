package utilities.api_body.product;

import api.dashboard.setting.StoreInformation;
import org.apache.commons.lang.math.JVMRandom;
import utilities.data.DataGenerator;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;


public class CreateProductBody {
    private static List<String> variationList;
    List<Long> productListingPrice;
    private static List<Long> productSellingPrice;
    // String: variation name, List: stock quantity per each branch
    private static Map<String, List<Integer>> productStockQuantity = new HashMap<>();

    public List<String> getVariationList() {
        return CreateProductBody.variationList;
    }

    public List<Long> getProductSellingPrice() {
        return CreateProductBody.productSellingPrice;
    }

    public Map<String, List<Integer>> getProductStockQuantity() {
        return CreateProductBody.productStockQuantity;
    }


    public String productInfo(boolean isIMEIProduct, String name, String currency, String description, int taxID, boolean showOutOfStock, boolean hideStock, boolean enableListing, boolean showOnApp, boolean showOnWeb, boolean showInStore, boolean showInGoSocial, String seoTitle, String seoDescription, String seoKeywords, String seoURL) {
        return """
                {
                    "name": "%s",
                    "cateId": 1680,
                    "itemType": "BUSINESS_PRODUCT",
                    "currency": "%s",
                    "description": "%s",
                    "totalComment": 0,
                    "totalLike": 0,
                    "priority": "",
                    "taxId": %s,
                    "quantityChanged": true,
                    "bcoin": 0,
                    "isSelfDelivery": false,
                    "showOutOfStock": %s,
                    "barcode": null,
                    "isHideStock": %s,
                    "enabledListing": %s,
                    "inventoryManageType": "%s",
                    "conversionUnitId": null,
                    "onApp": %s,
                    "onWeb": %s,
                    "inStore": %s,
                    "inGosocial": %s,
                    "seoTitle": "%s",
                    "seoDescription": "%s",
                    "seoKeywords": "%s",
                    "seoUrl": "%s",
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
                    "images": [
                        {
                            "imageUUID": "846d2ca4-40f5-4e8d-acc0-eea6280a5dda",
                            "urlPrefix": "https://d3a0f2zusjbf7r.cloudfront.net",
                            "extension": "jpg"
                        }
                    ],
                    "shippingInfo": {
                        "weight": 100,
                        "height": 10,
                        "length": 10,
                        "width": 10
                    },
                """.formatted(name, currency, description, taxID, showOutOfStock, hideStock, enableListing, isIMEIProduct ? "IMEI_SERIAL_NUMBER" : "PRODUCT", showOnApp, showOnWeb, showInStore, showInGoSocial, seoTitle, seoDescription, seoKeywords, seoURL);
    }

    public String variationInfo(LoginInformation loginInformation, boolean isIMEIProduct, List<Integer> branchIDList, List<String> branchNameList, int increaseNum, int... branchStockQuantity) {
        // get store information
        StoreInfo storeInfo = new StoreInformation(loginInformation).getInfo();

        // generate variation map
        Map<String, List<String>> variationMap = new DataGenerator().randomVariationMap();

        // get variation name
        List<String> varName = new ArrayList<>(variationMap.keySet());
        String variationName = IntStream.range(1, varName.size()).mapToObj(i -> "|%s_%s".formatted(storeInfo.getDefaultLanguage(), varName.get(i))).collect(Collectors.joining("", "%s_%s".formatted(storeInfo.getDefaultLanguage(), varName.get(0)), ""));

        // get variation value
        List<List<String>> varValue = new ArrayList<>(variationMap.values());
        variationList = new ArrayList<>();
        varValue.get(0).forEach(var -> variationList.add("%s_%s".formatted(storeInfo.getDefaultLanguage(), var)));
        if (varValue.size() > 1)
            IntStream.range(1, varValue.size())
                    .forEachOrdered(i -> variationList = new DataGenerator()
                            .mixVariationValue(variationList, varValue.get(i), storeInfo.getDefaultLanguage()));

        // random variation listing price
        productListingPrice = new ArrayList<>();
        IntStream.range(0, variationList.size())
                .mapToLong(i -> nextLong(MAX_PRICE))
                .forEachOrdered(orgPrice -> productListingPrice.add(orgPrice));

        // random variation selling price
        productSellingPrice = new ArrayList<>();
        productListingPrice.stream()
                .mapToLong(JVMRandom::nextLong)
                .forEachOrdered(newPrice -> productSellingPrice.add(newPrice));


        // random variation stock per branch
        productStockQuantity = new HashMap<>();
        for (int i = 0; i < variationList.size(); i++) {
            List<Integer> variationStock = new ArrayList<>();
            // set branch stock
            for (int branchIndex = 0; branchIndex < branchIDList.size(); branchIndex++) {
                variationStock.add((branchStockQuantity.length > branchIndex) ? (branchStockQuantity[branchIndex] + (i * increaseNum)) : 0);
            }
            productStockQuantity.put(variationList.get(i), variationStock);
        }


        StringBuilder models = new StringBuilder("""
                "models": [""");
        for (int i = 0; i < variationList.size(); i++) {
            models.append("""
                    {
                                "name": "%s",
                                "orgPrice": %s,
                                "discount": 0,
                                "newPrice": %s,
                                "totalItem": 0,
                                "label": "%s",
                                "sku": "",
                                "newStock": 0,
                                "costPrice": 0,
                                "lstInventory": [
                        """.formatted(variationList.get(i), productListingPrice.get(i), productSellingPrice.get(i), variationName));

            for (int index = 0; index < branchIDList.size(); index++) {
                // generate stock object
                String setStock = """
                        {
                                    "branchId": %s,
                                    "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                    "inventoryCurrent": 0,
                                    "inventoryStock": %s,
                                    "inventoryType": "SET"
                                }""".formatted(branchIDList.get(index), productStockQuantity.get(variationList.get(i)).get(index));

                // add stock object to body
                models.append(setStock);

                // if is not last branch => add ","
                // check is last branch:
                // IMEI product => add "],"
                // Normal product => check is not last variation => add "]}," else add "]}],"
                models.append((index < (branchIDList.size() - 1)) ? "," : (isIMEIProduct ? "]," : ((i < (variationList.size() - 1)) ? "]}," : "]}],")));
            }

            // add serial number for imei product
            if (isIMEIProduct) {
                // set inventory manage type: IMEI
                models.append("\"itemModelCodeDTOS\": [");
                // add IMEI for each branch
                for (int branchIndex = 0; branchIndex < branchIDList.size(); branchIndex++) {
                    // get number of IMEI per branch
                    List<Integer> branchStock = productStockQuantity.get(variationList.get(i));
                    // get number of IMEI per branch
                    for (int id = 0; id < branchStock.get(branchIndex); id++) {
                        // generate IMEI object
                        // IMEI format: branchName_IMEI_Index
                        models.append("""
                                {
                                            "branchId": %s,
                                            "code": "%s",
                                            "status": "AVAILABLE"
                                        }
                                """.formatted(branchIDList.get(branchIndex), "%s%s_IMEI_%s".formatted(variationList.get(i), branchNameList.get(branchIndex), id)));

                        // add IMEI object to body
                        // add "," if is not last stock
                        // check if next branched in-stock => add new IMEI object
                        models.append(branchIndex < branchIDList.size() - 1 ? (branchStock.get(branchIndex + 1) == 0) && (id == (branchStock.get(branchIndex) - 1)) ? "" : "," : id < (branchStock.get(branchIndex) - 1) ? "," : "");
                    }
                }

                // check is not last variation: add "]}," else add "]}],"
                models.append(i < variationList.size() - 1 ? "]}," : "]}],");
            }
        }
        return models.toString();
    }

    public String variationBranchConfig(List<Integer> branchIDList) {
        StringBuilder lstInventory = new StringBuilder("""
                "lstInventory": [""");
        for (int i = 0; i < branchIDList.size(); i++) {
            int branchIndex = i;
            lstInventory.append("""
                    {
                                "branchId": %s,
                                "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                "inventoryCurrent": 0,
                                "inventoryStock": %s,
                                "inventoryType": "SET"
                            }""".formatted(branchIDList.get(i), productStockQuantity.keySet().stream().map(key -> productStockQuantity.get(key).get(branchIndex)).toList().stream().reduce(0, Integer::sum)));
            lstInventory.append(i < branchIDList.size() - 1 ? "," : "");
        }
        lstInventory.append("]}");
        return lstInventory.toString();
    }

    public String withoutVariationInfo(boolean isIMEIProduct, List<Integer> branchIDList, List<String> branchNameList, int... branchStockQuantity) {
        // set variation list
        variationList = new ArrayList<>();
        variationList.add(null);

        // random listing price
        productListingPrice = new ArrayList<>();
        productListingPrice.add(nextLong(MAX_PRICE));

        // random selling price
        productSellingPrice = new ArrayList<>();
        productSellingPrice.add(nextLong(productListingPrice.get(0)));

        // set branch stock
        productStockQuantity = new HashMap<>();
        productStockQuantity.put(null, IntStream.range(0, branchIDList.size()).mapToObj(i -> branchStockQuantity.length > i ? branchStockQuantity[i] : 0).toList());

        StringBuilder itemModelCodeDTOS = new StringBuilder("""
                "itemModelCodeDTOS": [""");
        if (isIMEIProduct) {
            // add IMEI for each branch
            for (int index = 0; index < branchIDList.size(); index++) {
                // get number of IMEI per branch
                for (int i = 0; i < productStockQuantity.get(null).get(index); i++) {
                    // generate IMEI object
                    // IMEI format: branchName_IMEI_Index
                    itemModelCodeDTOS.append("""
                            {
                                        "branchId": %s,
                                        "code": "%s",
                                        "status": "AVAILABLE"
                                    }
                            """.formatted(branchIDList.get(index), "%s_IMEI_%s".formatted(branchNameList.get(index), i)));

                    // add IMEI object to body
                    // add "," if is not last stock
                    // check if next branched in-stock => add new IMEI object
                    itemModelCodeDTOS.append(index < branchIDList.size() - 1 ? (productStockQuantity.get(null).get(index + 1) == 0) && (i == (productStockQuantity.get(null).get(index) - 1)) ? "" : "," : i < (productStockQuantity.get(null).get(index) - 1) ? "," : "");
                }
            }
        }
        itemModelCodeDTOS.append("],");
        itemModelCodeDTOS.append("""
                "costPrice": 0,
                "orgPrice": %s,
                "newPrice": %s,
                "totalItem": 0,""".formatted(productListingPrice.get(0), productSellingPrice.get(0)));
        return itemModelCodeDTOS.toString();
    }

    public String withoutVariationBranchConfig(List<Integer> branchIDList) {
        StringBuilder lstInventory = new StringBuilder("""
                "lstInventory": [""");
        for (int i = 0; i < branchIDList.size(); i++) {
            lstInventory.append("""
                    {
                                "branchId": %s,
                                "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                                "inventoryCurrent": 0,
                                "inventoryStock": %s,
                                "inventoryType": "SET"
                            }""".formatted(branchIDList.get(i), productStockQuantity.get(null).get(i)));
            lstInventory.append(i < branchIDList.size() - 1 ? "," : "");
        }
        lstInventory.append("]}");
        return lstInventory.toString();
    }
}
