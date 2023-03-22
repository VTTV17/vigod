package api.dashboard.products;

import api.dashboard.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.collections.Lists;
import utilities.api.API;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.marketing.LoyaltyProgram.apiMembershipStatus;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.apiBranchID;
import static api.dashboard.setting.BranchManagement.apiBranchName;

public class ProductInformation {
    String GET_DASHBOARD_PRODUCT_LIST = "/itemservice/api/store/dashboard/storeID/items-v2?itemType=BUSINESS_PRODUCT&size=100&sort=lastModifiedDate%2Cdesc";
    String GET_PRODUCT_INFORMATION = "/itemservice/api/beehive-items/%s";
    String GET_PRODUCT_COLLECTION = "/itemservice/api/collections/products/%s";
    String GET_COLLECTION_LANGUAGE = "/itemservice/api/collection-languages/collection/%s";
    API api = new API();
    public static Map<String, String> variationNameMap;
    public static Map<String, List<String>> variationListMap;
    public static List<Long> productListingPrice;
    public static List<Long> productSellingPrice;
    public static boolean hasModel;
    public static boolean manageInventoryByIMEI;
    public static List<String> barcodeList;
    public static List<String> variationStatus;
    public static Map<String, String> defaultProductNameMap;
    public static Map<String, Map<String, String>> productNameMap;
    public static Map<String, String> defaultProductDescriptionMap;
    public static Map<String, Map<String, String>> productDescriptionMap;
    public static Map<String, Map<String, String>> seoMap;
    public static Map<String, List<Integer>> productStockQuantityMap;
    public static boolean showOutOfStock;
    public static boolean enabledListing;
    public static boolean isHideStock;
    public static String bhStatus;
    public static boolean deleted;
    public static boolean onApp;
    public static boolean onWeb;
    public static boolean inStore;
    public static boolean inGosocial;
    public static Map<Integer, Map<String, String>> collectionNameMap;

    /**
     * Return list product id has remaining stock > 0
     */
    public List<Integer> getProductList() {
        Response dashboardProductList = api.get(GET_DASHBOARD_PRODUCT_LIST.replace("storeID", String.valueOf(apiStoreID)), accessToken);
        dashboardProductList.then().statusCode(200);
        JsonPath productListJson = dashboardProductList.jsonPath();
        return Lists.newReversedArrayList(IntStream.range(0, productListJson.getList("id").size()).filter(i -> (int) (productListJson.getList("remainingStock").get(i)) > 0).mapToObj(i -> (int) (productListJson.getList("id").get(i))).toList());
    }

    public void get(Integer productID) {
        // get branch id list
        if (apiBranchID == null) new BranchManagement().getBranchInformation();

        // get product information
        Response productInfo = api.get(GET_PRODUCT_INFORMATION.formatted(productID), accessToken);

        productInfo.prettyPrint();

        // set JsonPath to get product info
        JsonPath productInfoJson = productInfo.jsonPath();

        deleted = productInfoJson.getString("message") != null;
        if (!deleted) {

            // get product name
            defaultProductNameMap = IntStream.range(0, productInfoJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getString("languages[%s].language".formatted(i)), i -> productInfoJson.getString("languages[%s].name".formatted(i)), (a, b) -> b));

            // get product description
            defaultProductDescriptionMap = IntStream.range(0, productInfoJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getString("languages[%s].language".formatted(i)), i -> productInfoJson.getString("languages[%s].description".formatted(i)), (a, b) -> b));

            // get SEO config
            seoMap = new HashMap<>();
            seoMap.put("title", IntStream.range(0, productInfoJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getString("languages[%s].language".formatted(i)), i -> productInfoJson.getString("languages[%s].seoTitle".formatted(i)) != null ? productInfoJson.getString("languages[%s].seoTitle".formatted(i)) : "", (a, b) -> b)));
            seoMap.put("description", IntStream.range(0, productInfoJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getString("languages[%s].language".formatted(i)), i -> productInfoJson.getString("languages[%s].seoDescription".formatted(i)) != null ? productInfoJson.getString("languages[%s].seoDescription".formatted(i)) : "", (a, b) -> b)));
            seoMap.put("keywords", IntStream.range(0, productInfoJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getString("languages[%s].language".formatted(i)), i -> productInfoJson.getString("languages[%s].seoKeywords".formatted(i)) != null ? productInfoJson.getString("languages[%s].seoKeywords".formatted(i)) : "", (a, b) -> b)));
            seoMap.put("url", IntStream.range(0, productInfoJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getString("languages[%s].language".formatted(i)), i -> productInfoJson.getString("languages[%s].seoUrl".formatted(i)) != null ? productInfoJson.getString("languages[%s].seoUrl".formatted(i)) : "", (a, b) -> b)));

            // check product has variation or not
            hasModel = productInfoJson.getBoolean("hasModel");

            // get SF/Buyer app config
            showOutOfStock = productInfoJson.getBoolean("showOutOfStock");
            isHideStock = productInfoJson.getBoolean("isHideStock");
            enabledListing = productInfoJson.getBoolean("enabledListing");

            // get product platform
            onApp = productInfoJson.getBoolean("onApp");
            onWeb = productInfoJson.getBoolean("onWeb");
            inStore = productInfoJson.getBoolean("inStore");
            inGosocial = productInfoJson.getBoolean("inGosocial");

            // get product status
            bhStatus = productInfoJson.getString("bhStatus");


            // manage inventory
            manageInventoryByIMEI = productInfoJson.getString("inventoryManageType").equals("IMEI_SERIAL_NUMBER");

            // get price
            productListingPrice = Pattern.compile("orgPrice.{3}(\\d+)").matcher(productInfo.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
            productSellingPrice = Pattern.compile("newPrice.{3}(\\d+)").matcher(productInfo.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
            if (hasModel) {
                productListingPrice = IntStream.range(1, productListingPrice.size()).mapToObj(i -> productListingPrice.get(i)).toList();
                productSellingPrice = IntStream.range(1, productSellingPrice.size()).mapToObj(i -> productSellingPrice.get(i)).toList();
            }
            if (!hasModel) {
                // get barcode list
                barcodeList = List.of(productInfoJson.getString("barcode"));

                // get variation name map
                variationNameMap = new HashMap<>();
                variationNameMap.put("en", null);
                variationNameMap.put("vi", null);

                // get variation list map
                variationListMap = new HashMap<>();
                List<String> noVar = new ArrayList<>();
                noVar.add(null);
                variationListMap.put("en", noVar);
                variationListMap.put("vi", noVar);

                // get product name
                productNameMap = new HashMap<>();
                productNameMap.put(barcodeList.get(0), defaultProductNameMap);

                // get product description
                productDescriptionMap = new HashMap<>();
                productDescriptionMap.put(barcodeList.get(0), defaultProductDescriptionMap);

                // get stock
                productStockQuantityMap = Map.of(barcodeList.get(0), apiBranchID.stream().map(IntStream.range(0, productInfoJson.getList("branches.branchId").size()).boxed().collect(Collectors.toMap(i -> productInfoJson.getInt("branches[%s].branchId".formatted(i)), i -> productInfoJson.getInt("branches[%s].totalItem".formatted(i)), (a, b) -> b))::get).toList());

                // get variation status
                variationStatus = List.of(productInfoJson.getString("bhStatus"));
            } else {
                // get barcode list
                barcodeList = productInfoJson.getList("models.barcode");

                // get variation name map
                variationNameMap = new HashMap<>();
                IntStream.range(0, productInfoJson.getList("models[0].languages.language").size()).forEach(langID -> variationNameMap.put(productInfoJson.getString("models[0].languages[%s].language".formatted(langID)), productInfoJson.getString("models[0].languages[%s].label".formatted(langID))));

                // init variation list map
                variationListMap = new HashMap<>();

                // init product stock map
                productStockQuantityMap = new HashMap<>();

                // init variation status
                variationStatus = new ArrayList<>();

                // init product name map
                productNameMap = new HashMap<>();

                // init product description map
                productDescriptionMap = new HashMap<>();

                // get variation info
                for (int modelsID = 0; modelsID < productInfoJson.getList("models.languages").size(); modelsID++) {

                    // get variation list map
                    Map<String, String> nameMap = new HashMap<>();
                    Map<String, String> descriptionMap = new HashMap<>();
                    for (int langID = 0; langID < productInfoJson.getList("models[%s].languages.language".formatted(modelsID)).size(); langID++) {
                        // get language
                        String language = productInfoJson.getString("models[%s].languages[%s].language".formatted(modelsID, langID));

                        // add new variation value
                        List<String> variationList = new ArrayList<>();
                        if (variationListMap.get(language) != null)
                            variationList.addAll(variationListMap.get(language));
                        variationList.add(productInfoJson.getString("models[%s].languages[%s].name".formatted(modelsID, langID)));

                        // add to map
                        variationListMap.put(language, variationList);

                        // get name map
                        nameMap.put(language, productInfoJson.getString("models[%s].languages[%s].versionName".formatted(modelsID, langID)));

                        // get description map
                        descriptionMap.put(language, productInfoJson.getString("models[%s].languages[%s].description".formatted(modelsID, langID)));
                    }

                    // if variation product name
                    nameMap.keySet().stream().filter(language -> nameMap.get(language) == null || nameMap.get(language).equals("")).forEachOrdered(language -> nameMap.put(language, defaultProductNameMap.get(language)));
                    productNameMap.put(barcodeList.get(modelsID), nameMap);

                    // get variation product description
                    descriptionMap.keySet().stream().filter(language -> descriptionMap.get(language) == null || descriptionMap.get(language).equals("")).forEach(language -> descriptionMap.put(language, defaultProductDescriptionMap.get(language)));
                    productDescriptionMap.put(barcodeList.get(modelsID), descriptionMap);

                    // get variation branch stock
                    Map<Integer, Integer> varStock = new HashMap<>();
                    for (int brID = 0; brID < productInfoJson.getList("models[0].branches.branchId").size(); brID++) {
                        varStock.put(productInfoJson.getInt("models[%s].branches[%s].branchId".formatted(modelsID, brID)), productInfoJson.getInt("models[%s].branches[%s].totalItem".formatted(modelsID, brID)));
                    }

                    // get variation stock
                    productStockQuantityMap.put(barcodeList.get(modelsID), apiBranchID.stream().mapToInt(brID -> brID).mapToObj(varStock::get).toList());

                    // get variation status
                    variationStatus.add(productInfoJson.getString("models[%s].status".formatted(modelsID)));
                }
            }

            Response collectionsList = api.get(GET_PRODUCT_COLLECTION.formatted(productID), accessToken);
            collectionsList.then().statusCode(200);
            List<Integer> collectionIDList = collectionsList.jsonPath().getList("id");

            collectionNameMap = new HashMap<>();
            for (int colID : collectionIDList) {
                Response collectionLanguage = api.get(GET_COLLECTION_LANGUAGE.formatted(colID), accessToken);
                collectionLanguage.then().statusCode(200);
                JsonPath collectionLanguageJson = collectionLanguage.jsonPath();
                collectionNameMap.put(colID, IntStream.range(0, collectionLanguageJson.getList("id").size()).boxed().collect(Collectors.toMap(langID -> String.valueOf(collectionLanguageJson.getList("language").get(langID)), langID -> String.valueOf(collectionLanguageJson.getList("name").get(langID)), (a, b) -> b)));
            }

            initDiscountInformation();
        }
    }

    void initDiscountInformation() {
        // init wholesale product status
        apiWholesaleProductStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiWholesaleProductStatus
                .put(brName, IntStream.range(0, barcodeList.size())
                        .mapToObj(i -> false).toList()));

        // init flash sale status
        apiFlashSaleStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiFlashSaleStatus
                .put(brName, IntStream.range(0, barcodeList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init discount campaign status
        apiDiscountCampaignStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiDiscountCampaignStatus
                .put(brName, IntStream.range(0, barcodeList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init flash sale price
        apiFlashSalePrice = new ArrayList<>();
        apiFlashSalePrice.addAll(productSellingPrice);

        // init flash sale stock
        apiFlashSaleStock = new ArrayList<>();
        barcodeList.forEach(barcode -> apiFlashSaleStock.add(Collections.max(productStockQuantityMap.get(barcode))));

        // init product discount campaign price
        apiDiscountCampaignPrice = new ArrayList<>();
        apiDiscountCampaignPrice.addAll(productSellingPrice);

        // init wholesale product price, rate and stock
        apiWholesaleProductPrice = new ArrayList<>();
        apiWholesaleProductPrice.addAll(productSellingPrice);

        apiWholesaleProductStock = new ArrayList<>();
        barcodeList.forEach(barcode -> apiWholesaleProductStock.add(Collections.max(productStockQuantityMap.get(barcode))));

        // discount code
        apiDiscountCodeStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiDiscountCodeStatus
                .put(brName, IntStream.range(0, barcodeList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // membership
        apiMembershipStatus = new HashMap<>();
        apiBranchName.forEach(brName -> apiMembershipStatus
                .put(brName, IntStream.range(0, barcodeList.size())
                        .mapToObj(i -> "EXPIRED").toList()));
    }
}
