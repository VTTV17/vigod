package api.dashboard.products;

import api.dashboard.login.Login;
import api.dashboard.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductRawData;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProductInformation {
    String GET_DASHBOARD_PRODUCT_LIST = "/itemservice/api/store/dashboard/storeID/items-v2?itemType=BUSINESS_PRODUCT&size=100&sort=lastModifiedDate%2Cdesc";
    String GET_PRODUCT_INFORMATION = "/itemservice/api/beehive-items/%s";
    String GET_PRODUCT_COLLECTION = "/itemservice/api/collections/products/%s";
    String GET_COLLECTION_LANGUAGE = "/itemservice/api/collection-languages/collection/%s";
    String GET_WHOLESALE_PRODUCT_DETAIL_PATH = "/itemservice/api/item/wholesale-pricing/edit/%s?langKey=vi&page=0&size=100";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    BranchInfo branchInfo;
    public ProductInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        branchInfo = new BranchManagement(loginInformation).getInfo();
    }

    /**
     * Return list product id has remaining stock > 0
     */
    public List<Integer> getProductList() {
        Response dashboardProductList = api.get(GET_DASHBOARD_PRODUCT_LIST.replace("storeID", String.valueOf(loginInfo.getStoreID())), loginInfo.getAccessToken());
        dashboardProductList.then().statusCode(200);
        JsonPath productListJson = dashboardProductList.jsonPath();
        List<Integer> productList = new ArrayList<>(IntStream.range(0, productListJson.getList("id").size()).filter(i -> (int) (productListJson.getList("remainingStock").get(i)) > 0).mapToObj(i -> (int) (productListJson.getList("id").get(i))).toList());
        Collections.reverse(productList);
        return productList;
    }

    public boolean checkProductInfo(int productID, String manageInventoryType, boolean hasModel, boolean inStock, boolean isHideStock, boolean isDisplayIfOutOfStock) {
        // get product response
        Response res = api.get(GET_PRODUCT_INFORMATION.formatted(productID), loginInfo.getAccessToken());
        return res.statusCode() == 200
                && (res.jsonPath().getBoolean("isHideStock") == isHideStock)
                & (res.jsonPath().getBoolean("showOutOfStock") == isDisplayIfOutOfStock)
                & (((res.jsonPath().getInt("totalItem") - res.jsonPath().getInt("totalSoldItem")) > 0) == inStock)
                & (res.jsonPath().getBoolean("hasModel") == hasModel)
                & res.jsonPath().getString("inventoryManageType").equals(manageInventoryType);
    }

    public ProductInfo getInfo(int productID) {
        // get product response
        Response res = api.get(GET_PRODUCT_INFORMATION.formatted(productID), loginInfo.getAccessToken());

        // set JsonPath to get product info
        JsonPath resJson = res.jsonPath();

        // init product info model
        ProductInfo prdInfo = new ProductInfo();

        // set product ID
        prdInfo.setProductID(productID);

        // set deleted
        prdInfo.setDeleted((res.getStatusCode() == 404) && res.asPrettyString().contains("message"));

        // if product is not deleted, get product information
        if ((!prdInfo.isDeleted())) {
            // check response API 200
            res.then().statusCode(200);

            // set product name
            prdInfo.setDefaultProductNameMap(IntStream.range(0, resJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> resJson.getString("languages[%s].language".formatted(i)), i -> resJson.getString("languages[%s].name".formatted(i)), (a, b) -> b)));

            // set product description
            prdInfo.setDefaultProductDescriptionMap(IntStream.range(0, resJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> resJson.getString("languages[%s].language".formatted(i)), i -> resJson.getString("languages[%s].description".formatted(i)), (a, b) -> b)));

            // get SEO config from response
            Map<String, Map<String, String>> seoMap = new HashMap<>();
            seoMap.put("title", IntStream.range(0, resJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> resJson.getString("languages[%s].language".formatted(i)), i -> resJson.getString("languages[%s].seoTitle".formatted(i)) != null ? resJson.getString("languages[%s].seoTitle".formatted(i)) : "", (a, b) -> b)));
            seoMap.put("description", IntStream.range(0, resJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> resJson.getString("languages[%s].language".formatted(i)), i -> resJson.getString("languages[%s].seoDescription".formatted(i)) != null ? resJson.getString("languages[%s].seoDescription".formatted(i)) : "", (a, b) -> b)));
            seoMap.put("keywords", IntStream.range(0, resJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> resJson.getString("languages[%s].language".formatted(i)), i -> resJson.getString("languages[%s].seoKeywords".formatted(i)) != null ? resJson.getString("languages[%s].seoKeywords".formatted(i)) : "", (a, b) -> b)));
            seoMap.put("url", IntStream.range(0, resJson.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> resJson.getString("languages[%s].language".formatted(i)), i -> resJson.getString("languages[%s].seoUrl".formatted(i)) != null ? resJson.getString("languages[%s].seoUrl".formatted(i)) : "", (a, b) -> b)));
            // set SEO map
            prdInfo.setSeoMap(seoMap);

            // set hasModel
            prdInfo.setHasModel(resJson.getBoolean("hasModel"));

            // set SF/Buyer app config
            prdInfo.setShowOutOfStock(resJson.getBoolean("showOutOfStock"));
            prdInfo.setHideStock(resJson.getBoolean("isHideStock"));
            prdInfo.setEnabledListing(resJson.getBoolean("enabledListing"));

            // set product platform
            prdInfo.setOnApp(resJson.getBoolean("onApp"));
            prdInfo.setOnWeb(resJson.getBoolean("onWeb"));
            prdInfo.setInStore(resJson.getBoolean("inStore"));
            prdInfo.setInGosocial(resJson.getBoolean("inGosocial"));

            // set product status
            prdInfo.setBhStatus(resJson.getString("bhStatus"));

            // manage inventory
            prdInfo.setManageInventoryByIMEI(resJson.getString("inventoryManageType").equals("IMEI_SERIAL_NUMBER"));

            // get price from response
            List<Long> listingPrice = Pattern.compile("orgPrice.{3}(\\d+)").matcher(res.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
            List<Long> sellingPrice = Pattern.compile("newPrice.{3}(\\d+)").matcher(res.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
            // set price
            prdInfo.setProductListingPrice(prdInfo.isHasModel() ? IntStream.range(1, listingPrice.size()).mapToObj(listingPrice::get).toList() : listingPrice);
            prdInfo.setProductSellingPrice(prdInfo.isHasModel() ? IntStream.range(1, sellingPrice.size()).mapToObj(sellingPrice::get).toList() : sellingPrice);

            if (prdInfo.isHasModel()) {
                // set model list
                List<String> modelList = resJson.getList("models.id").stream().map(modelId -> "%s-%s".formatted(productID, modelId)).toList();
                prdInfo.setVariationModelList(modelList);

                // set barcode list
                prdInfo.setBarcodeList(resJson.getList("models.barcode"));

                // get variation name map
                Map<String, String> variationNameMap = new HashMap<>();
                IntStream.range(0, resJson.getList("models[0].languages.language").size()).forEach(langID -> variationNameMap.put(resJson.getString("models[0].languages[%s].language".formatted(langID)), resJson.getString("models[0].languages[%s].label".formatted(langID))));
                // set variation map
                prdInfo.setVariationNameMap(variationNameMap);

                // init variation list map
                Map<String, List<String>> variationListMap = new HashMap<>();

                // init product stock map
                Map<String, List<Integer>> productStockQuantityMap = new HashMap<>();

                // init variation status
                List<String> variationStatus = new ArrayList<>();

                // init product name map
                Map<String, Map<String, String>> productNameMap = new HashMap<>();

                // init product description map
                Map<String, Map<String, String>> productDescriptionMap = new HashMap<>();

                // get variation info
                for (int modelsID = 0; modelsID < resJson.getList("models.languages").size(); modelsID++) {

                    // get variation list map
                    Map<String, String> nameMap = new HashMap<>();
                    Map<String, String> descriptionMap = new HashMap<>();
                    for (int langID = 0; langID < resJson.getList("models[%s].languages.language".formatted(modelsID)).size(); langID++) {
                        // get language
                        String language = resJson.getString("models[%s].languages[%s].language".formatted(modelsID, langID));

                        // add new variation value
                        List<String> variationList = new ArrayList<>();
                        if (variationListMap.get(language) != null)
                            variationList.addAll(variationListMap.get(language));
                        variationList.add(resJson.getString("models[%s].languages[%s].name".formatted(modelsID, langID)));

                        // add to map
                        variationListMap.put(language, variationList);

                        // get name map
                        nameMap.put(language, resJson.getString("models[%s].languages[%s].versionName".formatted(modelsID, langID)));

                        // get description map
                        descriptionMap.put(language, resJson.getString("models[%s].languages[%s].description".formatted(modelsID, langID)));
                    }

                    // if variation product name
                    nameMap.keySet().stream().filter(language -> nameMap.get(language) == null || nameMap.get(language).isEmpty()).forEachOrdered(language -> nameMap.put(language, prdInfo.getDefaultProductNameMap().get(language)));
                    productNameMap.put(prdInfo.getVariationModelList().get(modelsID), nameMap);

                    // get variation product description
                    descriptionMap.keySet().stream().filter(language -> descriptionMap.get(language) == null || descriptionMap.get(language).isEmpty()).forEach(language -> descriptionMap.put(language, prdInfo.getDefaultProductDescriptionMap().get(language)));
                    productDescriptionMap.put(prdInfo.getVariationModelList().get(modelsID), descriptionMap);

                    // get variation branch stock
                    Map<Integer, Integer> varStock = new HashMap<>();
                    for (int brID = 0; brID < resJson.getList("models[0].branches.branchId").size(); brID++) {
                        varStock.put(resJson.getInt("models[%s].branches[%s].branchId".formatted(modelsID, brID)), resJson.getInt("models[%s].branches[%s].totalItem".formatted(modelsID, brID)));
                    }

                    // get variation stock
                    productStockQuantityMap.put(prdInfo.getVariationModelList().get(modelsID), branchInfo.getBranchID().stream().mapToInt(brID -> brID).mapToObj(varStock::get).toList());

                    // get variation status
                    variationStatus.add(resJson.getString("models[%s].status".formatted(modelsID)));
                }
                // set variation list map
                prdInfo.setVariationListMap(variationListMap);

                // set product quantity map
                prdInfo.setProductStockQuantityMap(productStockQuantityMap);

                // set variation status list
                prdInfo.setVariationStatus(variationStatus);

                // set variation product name map
                prdInfo.setProductNameMap(productNameMap);

                // set variation product description map
                prdInfo.setProductDescriptionMap(productDescriptionMap);

            } else {
                // set model list
                prdInfo.setVariationModelList(List.of(String.valueOf(productID)));

                // set barcode list
                prdInfo.setBarcodeList(List.of(resJson.getString("barcode")));

                // get variation name map
                Map<String, String> variationNameMap = new HashMap<>();
                variationNameMap.put("en", null);
                variationNameMap.put("vi", null);
                // set variation name map
                prdInfo.setVariationNameMap(variationNameMap);

                // get variation list map
                Map<String, List<String>> variationListMap = new HashMap<>();
                List<String> noVar = new ArrayList<>();
                noVar.add(null);
                variationListMap.put("en", noVar);
                variationListMap.put("vi", noVar);
                // set variation list map
                prdInfo.setVariationListMap(variationListMap);

                // get variation product name
                Map<String, Map<String, String>> productNameMap = new HashMap<>();
                productNameMap.put(prdInfo.getVariationModelList().get(0), prdInfo.getDefaultProductNameMap());
                //set variation product name
                prdInfo.setProductNameMap(productNameMap);

                // get product description
                Map<String, Map<String, String>> productDescriptionMap = new HashMap<>();
                productDescriptionMap.put(prdInfo.getVariationModelList().get(0), prdInfo.getDefaultProductDescriptionMap());
                // set variation product description
                prdInfo.setProductDescriptionMap(productDescriptionMap);

                // set stock
                prdInfo.setProductStockQuantityMap(Map.of(prdInfo.getVariationModelList().get(0), branchInfo.getBranchID().stream().map(IntStream.range(0, resJson.getList("branches.branchId").size()).boxed().collect(Collectors.toMap(i -> resJson.getInt("branches[%s].branchId".formatted(i)), i -> resJson.getInt("branches[%s].totalItem".formatted(i)), (a, b) -> b))::get).toList()));


                // set variation status
                prdInfo.setVariationStatus(List.of(resJson.getString("bhStatus")));
            }
            // s.out
            try {
                int taxId = resJson.getInt("taxId");
                String taxRate = resJson.getString("taxSettings.find {it.id == %s}.rate".formatted(taxId));
                prdInfo.setTaxRate(taxRate == null ? 0 : Double.parseDouble(taxRate));
            } catch (NullPointerException ignore) {}

            Response collectionsList = api.get(GET_PRODUCT_COLLECTION.formatted(productID), loginInfo.getAccessToken());
            collectionsList.then().statusCode(200);
            List<Integer> collectionIDList = collectionsList.jsonPath().getList("id");
            prdInfo.setCollectionIdList(collectionIDList);

            Map<Integer, Map<String, String>> collectionNameMap = new HashMap<>();
            for (int colID : collectionIDList) {
                Response collectionLanguage = api.get(GET_COLLECTION_LANGUAGE.formatted(colID), loginInfo.getAccessToken());
                collectionLanguage.then().statusCode(200);
                JsonPath collectionLanguageJson = collectionLanguage.jsonPath();
                collectionNameMap.put(colID, IntStream.range(0, collectionLanguageJson.getList("id").size()).boxed().collect(Collectors.toMap(langID -> String.valueOf(collectionLanguageJson.getList("language").get(langID)), langID -> String.valueOf(collectionLanguageJson.getList("name").get(langID)), (a, b) -> b)));
            }

            // set collection name map
            prdInfo.setCollectionNameMap(collectionNameMap);
        }
        return prdInfo;
    }

    /**
     * return {barcode, list segment, list price, list stock}
     */
    public WholesaleProductInfo wholesaleProductInfo(ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        /* get wholesale product raw data from API */
        Response wholesaleProductInfo = api.get(GET_WHOLESALE_PRODUCT_DETAIL_PATH.formatted(productInfo.getProductID()), loginInfo.getAccessToken());
        wholesaleProductInfo.then().statusCode(200);
        // get sale barcode group list
        List<String> barcodeList = wholesaleProductInfo.jsonPath().getList("lstResult.itemModelIds");
        // get sale price list
        List<Long> salePrice = Pattern.compile("price.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
        // get sale stock list
        List<Integer> saleStock = Pattern.compile("minQuatity.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList();
        // get sale segment group list
        JsonPath wholesaleJsonPath = wholesaleProductInfo.jsonPath();
        List<Object> segmentList = new ArrayList<>();
        for (int i = 0; i < wholesaleJsonPath.getList("lstResult").size(); i++) {
            for (int configId = 0; configId < wholesaleJsonPath.getList("lstResult[%s].paging.content".formatted(i)).size(); configId++) {
                segmentList.add(wholesaleJsonPath.getString("lstResult[%s].paging.content[%s].segmentIds".formatted(i, configId)));
            }
        }

        // get number config per group barcode
        List<Integer> totalElements = Pattern.compile("totalElements.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList();

        /* raw data */
        List<WholesaleProductRawData> configs = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < totalElements.size(); i++) {
            WholesaleProductRawData wholesaleRawData = new WholesaleProductRawData();
            wholesaleRawData.setBarcode(barcodeList.get(i));
            List<Long> priceList = new ArrayList<>();
            List<Integer> stockList = new ArrayList<>();
            for (int id = index; id < index + totalElements.get(i); id++) {
                if (segmentList.get(id) != null) {
                    if (listSegmentOfCustomer != null) {
                        if (segmentList.get(id).equals("ALL") || (!listSegmentOfCustomer.isEmpty() && Arrays.stream(segmentList.get(id).toString().split(",")).toList().stream().anyMatch(segId -> listSegmentOfCustomer.contains(Integer.valueOf(segId)))))
                            if (stockList.contains(saleStock.get(id)))
                                priceList.set(stockList.indexOf(saleStock.get(id)), Math.min(salePrice.get(id), priceList.get(stockList.indexOf(saleStock.get(id)))));
                            else {
                                priceList.add(salePrice.get(id));
                                stockList.add(saleStock.get(id));
                            }
                    }
                }
            }
            wholesaleRawData.setPrice(priceList);
            wholesaleRawData.setStock(stockList);
            if (!stockList.isEmpty()) configs.add(wholesaleRawData);
            index += totalElements.get(i);
        }

        /* analyze data */
        // get product barcode list
        List<String> listVariationModelId = new ArrayList<>(productInfo.getVariationModelList());
        listVariationModelId.replaceAll(barcode -> barcode.replace("-", "_"));

        // get branch name
        List<String> branchNameList = branchInfo.getBranchName();

        // init wholesale product status map
        Map<String, List<Boolean>> wholesaleProductStatus = new HashMap<>();

        List<String> saleBarcode = configs.stream().flatMap(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(","))).distinct().toList();

        // if variation has wholesale product => set status = true
        branchNameList.forEach(brName -> wholesaleProductStatus
                .put(brName, listVariationModelId.stream().map(saleBarcode::contains).toList()));

        // get wholesale product price
        List<Long> wholesaleProductPrice = new ArrayList<>(productInfo.getProductSellingPrice());
        configs.forEach(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(",")).toList().forEach(code -> wholesaleProductPrice.set(listVariationModelId.indexOf(code), wpConfig.getPrice().get(0))));

        // get wholesale product stock
        List<Integer> wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, listVariationModelId.size()).forEachOrdered(i -> wholesaleProductStock.add(0));
        configs.forEach(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(",")).toList().forEach(code -> wholesaleProductStock.set(listVariationModelId.indexOf(code), wpConfig.getStock().get(0))));

        WholesaleProductInfo analyzedData = new WholesaleProductInfo();
        analyzedData.setStatusMap(wholesaleProductStatus);
        analyzedData.setPriceList(wholesaleProductPrice);
        analyzedData.setStockList(wholesaleProductStock);
        return analyzedData;
    }

    /**
     * Retrieves a JSON path object containing all product data from the dashboard API.
     *
     * @return the JsonPath object containing product data retrieved from the dashboard API.
     */
    public JsonPath getAllProductJsonPath() {
        Response response = api.get(GET_DASHBOARD_PRODUCT_LIST.replace("storeID", String.valueOf(loginInfo.getStoreID())), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath();
    }

    /**
     * Returns a list of product IDs and names for all products that have conversion units.
     *
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
