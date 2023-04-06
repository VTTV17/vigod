package api.dashboard.products;

import api.dashboard.customers.Customers;
import api.dashboard.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.collections.Lists;
import utilities.api.API;
import utilities.model.wholesaleProduct.WholesaleProductAnalyzedData;
import utilities.model.wholesaleProduct.WholesaleProductRawData;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.setting.BranchManagement.apiBranchID;
import static utilities.account.AccountTest.BUYER_ACCOUNT_THANG;

public class ProductInformation {
    String GET_DASHBOARD_PRODUCT_LIST = "/itemservice/api/store/dashboard/storeID/items-v2?itemType=BUSINESS_PRODUCT&size=100&sort=lastModifiedDate%2Cdesc";
    String GET_PRODUCT_INFORMATION = "/itemservice/api/beehive-items/%s";
    String GET_PRODUCT_COLLECTION = "/itemservice/api/collections/products/%s";
    String GET_COLLECTION_LANGUAGE = "/itemservice/api/collection-languages/collection/%s";
    String GET_WHOLESALE_PRODUCT_DETAIL_PATH = "/itemservice/api/item/wholesale-pricing/edit/%s?langKey=vi&page=0&size=100";
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

    Response getProductInformationResponse(int productID) {
        // get product information
        return api.get(GET_PRODUCT_INFORMATION.formatted(productID), accessToken);
    }

    public String getManageInventoryType(int productID) {
        return getProductInformationResponse(productID).jsonPath().getString("inventoryManageType");
    }

    public List<String> getProductBarcode(int productID) {
        Response res = getProductInformationResponse(productID);
        res.then().statusCode(200);
        return res.jsonPath().getBoolean("hasModel") ? res.jsonPath().getList("models.barcode") : List.of(res.jsonPath().getString("barcode"));
    }

    public List<Long> getProductSellingPrice(int productID) {
        Response res = getProductInformationResponse(productID);
        return productSellingPrice = Pattern.compile("newPrice.{3}(\\d+)").matcher(res.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
    }

    public void get(int productID) {
        // get branch id list
        if (apiBranchID == null) new BranchManagement().getBranchInformation();

        // get product response
        Response productInfo = getProductInformationResponse(productID);

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

//            initDiscountInformation();
        }
    }

    /**
     * return {barcode, list segment, list price, list stock}
     */
    public WholesaleProductAnalyzedData getWholesaleProductConfig(int productID) {
        /* get wholesale product raw data from API */
        Response wholesaleProductInfo = api.get(GET_WHOLESALE_PRODUCT_DETAIL_PATH.formatted(productID), accessToken);
        wholesaleProductInfo.then().statusCode(200);
        // get sale barcode group list
        List<String> barcodeList = wholesaleProductInfo.jsonPath().getList("lstResult.itemModelIds");
        // get sale price list
        List<Long> salePrice = Pattern.compile("price.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
        // get sale stock list
        List<Integer> saleStock = Pattern.compile("minQuatity.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList();
        // get sale segment group list
        List<String> segmentList = Pattern.compile("segmentIds.{4}((\\d+,*)+|\\w{3})").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> matchResult.group(1)).toList();
        // get number config per group barcode
        List<Integer> totalElements = Pattern.compile("totalElements.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList();

        /* raw data */
        List<WholesaleProductRawData> configs = new ArrayList<>();
        int index = 0;
        int customerID = new Customers().getCustomerID(BUYER_ACCOUNT_THANG);
        for (int i = 0; i < totalElements.size(); i++) {
            WholesaleProductRawData wholesaleRawData = new WholesaleProductRawData();
            wholesaleRawData.setBarcode(barcodeList.get(i));
            List<Long> priceList = new ArrayList<>();
            List<Integer> stockList = new ArrayList<>();
            for (int id = index; id < index + totalElements.get(i); id++)
                if (segmentList.get(id).equals("ALL") || Arrays.stream(segmentList.get(i).split(",")).toList().stream().map(segID -> new Customers().getListCustomerInSegment(Integer.valueOf(segID))).flatMap(Collection::stream).toList().contains(customerID)) {
                    priceList.add(salePrice.get(id));
                    stockList.add(saleStock.get(id));
                }
            wholesaleRawData.setPrice(priceList);
            wholesaleRawData.setStock(stockList);
            if (stockList.size() > 0) configs.add(wholesaleRawData);
            index += totalElements.get(i);
        }

        /* analyze data */
        // get product barcode list
        List<String> productBarcodeList = new ArrayList<>(new ProductInformation().getProductBarcode(productID));
        productBarcodeList.replaceAll(barcode -> barcode.replace("-", "_"));

        // get branch name
        List<String> branchNameList = new BranchManagement().getListBranchName();

        // init wholesale product status map
        Map<String, List<Boolean>> wholesaleProductStatus = new HashMap<>();

        List<String> saleBarcode = configs.stream().flatMap(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(","))).distinct().toList();

        // if variation has wholesale product => set status = true
        branchNameList.forEach(brName -> wholesaleProductStatus
                .put(brName, productBarcodeList.stream().map(saleBarcode::contains).toList()));

        // get wholesale product price
        List<Long> wholesaleProductPrice = new ArrayList<>(getProductSellingPrice(productID));
        configs.forEach(wpConfig -> wholesaleProductPrice.set(productBarcodeList.indexOf(wpConfig.getBarcode()), wpConfig.getPrice().get(0)));

        // get wholesale product stock
        List<Integer> wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, productBarcodeList.size()).forEachOrdered(i -> wholesaleProductStock.add(0));
        configs.forEach(wpConfig -> wholesaleProductStock.set(productBarcodeList.indexOf(wpConfig.getBarcode()), wpConfig.getStock().get(0)));

        WholesaleProductAnalyzedData analyzedData = new WholesaleProductAnalyzedData();
        analyzedData.setStatusMap(wholesaleProductStatus);
        analyzedData.setPriceList(wholesaleProductPrice);
        analyzedData.setStockList(wholesaleProductStock);
        return analyzedData;
    }

    /**
     * Retrieves a JSON path object containing all product data from the dashboard API.
     * @return the JsonPath object containing product data retrieved from the dashboard API.
     */
	public JsonPath getAllProductJsonPath() {
		Response response = api.get(GET_DASHBOARD_PRODUCT_LIST.replace("storeID", String.valueOf(apiStoreID)), accessToken);
		response.then().statusCode(200);
		return response.jsonPath();
	}
    
	/**
	 * Returns a list of product IDs and names for all products that have conversion units.
	 * @return a List of Lists containing Strings, where each inner List contains the ID (as a String) and name of a product with conversion units.
	 */
	public List<List<String>> getIdAndNameOfProductWithConversionUnits() {
		JsonPath productJsonPath = getAllProductJsonPath();
		
		List<Integer> id = productJsonPath.getList("findAll { it.hasConversion == true }.id");
		List<String> name = productJsonPath.getList("findAll { it.hasConversion == true }.name");
		
		List<List<String>> productData = new ArrayList<>();
		for (int i = 0; i < id.size(); i++) {
		    productData.add(Arrays.asList(String.valueOf(id.get(i)), name.get(i)));
		}
		
		return productData;
	}    
    
}
