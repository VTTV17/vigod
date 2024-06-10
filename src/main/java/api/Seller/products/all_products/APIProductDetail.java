package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.products.inventory.APIInventoryHistory;
import api.Seller.products.product_collections.APIProductCollection;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.Seller.products.all_products.APIProductDetail.ProductInformationEnum.*;

public class APIProductDetail {
    String GET_DASHBOARD_PRODUCT_LIST = "/itemservice/api/store/dashboard/storeID/items-v2?itemType=BUSINESS_PRODUCT&size=100&sort=lastModifiedDate%2Cdesc";
    String GET_PRODUCT_INFORMATION = "/itemservice/api/beehive-items/%s";
    String GET_COLLECTION_LANGUAGE = "/itemservice/api/collection-languages/collection/%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    BranchInfo branchInfo;

    public APIProductDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        branchInfo = new BranchManagement(loginInformation).getInfo();
    }

    @Data
    public static class WholesaleProductRawData {
        private String barcode;
        private List<Integer> stock;
        private List<Long> price;
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
        return (res.statusCode() == 200)
               && (res.jsonPath().getBoolean("isHideStock") == isHideStock)
               && (res.jsonPath().getBoolean("showOutOfStock") == isDisplayIfOutOfStock)
               && (((res.jsonPath().getInt("totalItem") - res.jsonPath().getInt("totalSoldItem")) > 0) == inStock)
               && (res.jsonPath().getBoolean("hasModel") == hasModel)
               && res.jsonPath().getString("inventoryManageType").equals(manageInventoryType);
    }

    public boolean checkProductInfo(int productID, String manageInventoryType, boolean hasLot) {
        // get product response
        Response res = api.get(GET_PRODUCT_INFORMATION.formatted(productID), loginInfo.getAccessToken());
        return (res.statusCode() == 200) && res.jsonPath().getString("inventoryManageType").equals(manageInventoryType) && Objects.equals(Optional.of(Boolean.parseBoolean(res.jsonPath().getString("lotAvailable"))).orElse(false), hasLot);
    }

    public enum ProductInformationEnum {
        name, description, attribute, SEO, price, variation, barcodes, inventory, stockQuantity, platform, status, tax, collection, onlineShopConfig, stockAlert;

        static List<ProductInformationEnum> getAllValues() {
            return new ArrayList<>(Arrays.asList(ProductInformationEnum.values()));
        }
    }

    public Map<String, String> getMainProductNameMap(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].name".formatted(i)), (a, b) -> b));
    }

    public Map<String, String> getMainProductDescription(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].description".formatted(i)), (a, b) -> b));
    }

    public List<String> getAttributeNames(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("itemAttributes.attributeName").size()).mapToObj(attributeIndex -> jsonPath.getString("itemAttributes[%s].attributeName".formatted(attributeIndex))).toList();
    }

    public List<String> getAttributeValues(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("itemAttributes.attributeValue").size()).mapToObj(attributeIndex -> jsonPath.getString("itemAttributes[%s].attributeValue".formatted(attributeIndex))).toList();
    }

    public List<Boolean> getIsDisplayAttributes(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("itemAttributes.isDisplay").size()).mapToObj(attributeIndex -> jsonPath.getBoolean("itemAttributes[%s].isDisplay".formatted(attributeIndex))).toList();
    }

    public Map<String, Map<String, String>> getSEOMap(JsonPath jsonPath) {
        // get SEO config from response
        Map<String, Map<String, String>> seoMap = new HashMap<>();
        seoMap.put("title", IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].seoTitle".formatted(i)) != null ? jsonPath.getString("languages[%s].seoTitle".formatted(i)) : "", (a, b) -> b)));
        seoMap.put("description", IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].seoDescription".formatted(i)) != null ? jsonPath.getString("languages[%s].seoDescription".formatted(i)) : "", (a, b) -> b)));
        seoMap.put("keywords", IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].seoKeywords".formatted(i)) != null ? jsonPath.getString("languages[%s].seoKeywords".formatted(i)) : "", (a, b) -> b)));
        seoMap.put("url", IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].seoUrl".formatted(i)) != null ? jsonPath.getString("languages[%s].seoUrl".formatted(i)) : "", (a, b) -> b)));
        // set SEO map
        return seoMap;
    }

    public boolean isShowOutOfStock(JsonPath jsonPath) {
        return jsonPath.getBoolean("showOutOfStock");
    }

    public List<Long> getListingPrices(Response response) {
        List<Long> listingPrice = Pattern.compile("orgPrice.{3}(\\d+)").matcher(response.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
        return (listingPrice.size() > 1)
                ? IntStream.range(1, listingPrice.size()).mapToObj(listingPrice::get).toList()
                : listingPrice;
    }

    public List<Long> getSellingPrices(Response response) {
        List<Long> sellingPrice = Pattern.compile("newPrice.{3}(\\d+)").matcher(response.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();

        return (sellingPrice.size() > 1)
                ? IntStream.range(1, sellingPrice.size()).mapToObj(sellingPrice::get).toList()
                : sellingPrice;
    }

    public List<Long> getCostPrices(Response response) {
        List<Long> costPrice = Pattern.compile("costPrice.{3}(\\d+)").matcher(response.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
        return (costPrice.size() > 1)
                ? IntStream.range(0, costPrice.size() - 1).mapToObj(costPrice::get).toList()
                : costPrice;
    }

    public List<String> getVariationModelList(JsonPath jsonPath, int productId) {
        List<String> modelList = jsonPath.getList("models.id").stream().map(modelId -> "%s-%s".formatted(productId, modelId)).toList();
        return modelList.isEmpty()
                ? List.of(String.valueOf(productId))
                : modelList;
    }

    public List<String> getListBarcodes(JsonPath jsonPath) {
        List<String> barcodes = jsonPath.getList("models.barcode");
        return barcodes.isEmpty()
                ? List.of(jsonPath.getString("barcode"))
                : barcodes;
    }

    Map<String, String> getVariationGroupNamesMap(JsonPath jsonPath) {
        List<Object> languageList = jsonPath.getList("models[0].languages.id");
        return languageList == null
                ? Map.of()
                : IntStream.range(0, jsonPath.getList("models[0].languages.language").size())
                .boxed()
                .collect(Collectors.toMap(languageIndex -> jsonPath.getString("models[0].languages[%s].language".formatted(languageIndex)),
                        langID -> jsonPath.getString("models[0].languages[%s].label".formatted(langID)),
                        (a, b) -> b));
    }

    public Map<String, List<String>> getVariationValuesMap(JsonPath jsonPath, List<String> languages) {
        // init variation list map
        Map<String, List<String>> variationValuesMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            for (int languageIndex = 0; languageIndex < jsonPath.getList("models[%s].languages.language".formatted(modelIndex)).size(); languageIndex++) {
                // get language
                String language = jsonPath.getString("models[%s].languages[%s].language".formatted(modelIndex, languageIndex));

                // add new variation value
                List<String> variationList = new ArrayList<>();
                if (variationValuesMap.get(language) != null)
                    variationList.addAll(variationValuesMap.get(language));
                variationList.add(jsonPath.getString("models[%s].languages[%s].name".formatted(modelIndex, languageIndex)));

                // add to map
                variationValuesMap.put(language, variationList);
            }
        }

        return variationValuesMap.isEmpty()
                ? languages.stream()
                .collect(Collectors.toMap(language -> language,
                        language -> List.of(""),
                        (a, b) -> b))
                : variationValuesMap;
    }

    public Map<String, List<Integer>> getProductQuantityMap(JsonPath jsonPath, List<String> modelList) {
        // init product stock map
        Map<String, List<Integer>> productStockQuantityMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            // get variation branch stock
            Map<Integer, Integer> varStock = new HashMap<>();
            for (int brIndex = 0; brIndex < jsonPath.getList("models[0].branches.branchId").size(); brIndex++) {
                varStock.put(jsonPath.getInt("models[%s].branches[%s].branchId".formatted(modelIndex, brIndex)), jsonPath.getInt("models[%s].branches[%s].totalItem".formatted(modelIndex, brIndex)));
            }

            // get variation stock
            productStockQuantityMap.put(modelList.get(modelIndex), branchInfo.getBranchID().stream().mapToInt(brIndex -> brIndex).mapToObj(varStock::get).toList());
        }
        return productStockQuantityMap.isEmpty()
                ? Map.of(modelList.get(0),
                branchInfo.getBranchID().stream().map(IntStream.range(0, jsonPath.getList("branches.branchId").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getInt("branches[%s].branchId".formatted(i)), i -> jsonPath.getInt("branches[%s].totalItem".formatted(i)), (a, b) -> b))::get).toList())
                : productStockQuantityMap;
    }

    public List<String> getVariationStatues(JsonPath jsonPath, String bhStatus) {
        // get variation status
        List<String> variationStatus = IntStream.range(0, jsonPath.getList("models.id").size()).mapToObj(modelIndex -> jsonPath.getString("models[%s].status".formatted(modelIndex))).toList();
        return variationStatus.isEmpty() ? List.of(bhStatus) : variationStatus;
    }

    public Map<String, Map<String, String>> getVersionNamesMap(JsonPath jsonPath, List<String> modelList, Map<String, String> mainName) {
        // init product name map
        Map<String, Map<String, String>> versionNames = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            // get variation list map
            Map<String, String> nameMap = new HashMap<>();
            for (int languageIndex = 0; languageIndex < jsonPath.getList("models[%s].languages.language".formatted(modelIndex)).size(); languageIndex++) {
                // get language
                String language = jsonPath.getString("models[%s].languages[%s].language".formatted(modelIndex, languageIndex));

                // get name map
                nameMap.put(language, jsonPath.getString("models[%s].languages[%s].versionName".formatted(modelIndex, languageIndex)));
            }

            // if variation product name
            nameMap.keySet().stream().filter(language -> nameMap.get(language) == null || nameMap.get(language).isEmpty()).forEachOrdered(language -> nameMap.put(language, mainName.get(language)));
            versionNames.put(modelList.get(modelIndex), nameMap);
        }
        return versionNames.isEmpty() ? Map.of(modelList.get(0), mainName) : versionNames;
    }

    public Map<String, Map<String, String>> getVersionDescriptionsMap(JsonPath jsonPath, List<String> modelList, Map<String, String> mainDescription) {
        // init product description map
        Map<String, Map<String, String>> versionDescriptions = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            // get variation list map
            Map<String, String> descriptionMap = new HashMap<>();
            for (int languageIndex = 0; languageIndex < jsonPath.getList("models[%s].languages.language".formatted(modelIndex)).size(); languageIndex++) {
                // get language
                String language = jsonPath.getString("models[%s].languages[%s].language".formatted(modelIndex, languageIndex));
                // get description map
                descriptionMap.put(language, jsonPath.getString("models[%s].languages[%s].description".formatted(modelIndex, languageIndex)));
            }

            // get variation product description
            descriptionMap.keySet().stream().filter(language -> descriptionMap.get(language) == null || descriptionMap.get(language).isEmpty()).forEach(language -> descriptionMap.put(language, mainDescription.get(language)));
            versionDescriptions.put(modelList.get(modelIndex), descriptionMap);
        }
        return versionDescriptions.isEmpty() ? Map.of(modelList.get(0), mainDescription) : versionDescriptions;
    }

    public Map<String, List<String>> getVariationAttributionNamesMap(JsonPath jsonPath, List<String> modelList, List<String> attributionGroups) {
        // init variation attribution groups map
        Map<String, List<String>> attributionGroupsMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            boolean reuseAttribute = Boolean.parseBoolean(Optional.ofNullable(jsonPath.getString("models[%s].reuseAttributes".formatted(modelIndex))).orElse("true"));

            // get variation attribute
            if (reuseAttribute) {
                attributionGroupsMap.put(modelList.get(modelIndex), attributionGroups);
            } else {
                List<String> attributeGroups = new ArrayList<>();
                for (int attributeIndex = 0; attributeIndex < jsonPath.getList("models[%s].modelAttributes.id".formatted(modelIndex)).size(); attributeIndex++) {
                    attributeGroups.add(jsonPath.getString("models[%s].modelAttributes[%s].attributeName".formatted(modelIndex, attributeIndex)));
                }

                attributionGroupsMap.put(modelList.get(modelIndex), attributeGroups);
            }
        }
        return attributionGroupsMap.isEmpty() ? Map.of(modelList.get(0), attributionGroups) : attributionGroupsMap;
    }

    public Map<String, List<String>> getVariationAttributionValuesMap(JsonPath jsonPath, List<String> modelList, List<String> attributionValues) {
        // init variation attribution values map
        Map<String, List<String>> attributionValuesMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            boolean reuseAttribute = Boolean.parseBoolean(Optional.ofNullable(jsonPath.getString("models[%s].reuseAttributes".formatted(modelIndex))).orElse("true"));

            // get variation attribute
            if (reuseAttribute) {
                attributionValuesMap.put(modelList.get(modelIndex), attributionValues);
            } else {
                List<String> attributeValues = new ArrayList<>();
                for (int attributeIndex = 0; attributeIndex < jsonPath.getList("models[%s].modelAttributes.id".formatted(modelIndex)).size(); attributeIndex++) {
                    attributeValues.add(jsonPath.getString("models[%s].modelAttributes[%s].attributeValue".formatted(modelIndex, attributeIndex)));
                }
                attributionValuesMap.put(modelList.get(modelIndex), attributeValues);
            }
        }
        return attributionValuesMap.isEmpty() ? Map.of(modelList.get(0), attributionValues) : attributionValuesMap;
    }

    public void checkSFAPI(int productId) {
        api.get("/itemservice/api/product/storefront-items/%s".formatted(productId), loginInfo.getAccessToken());
    }

    public Map<String, List<Boolean>> getIsDisplayVariationAttributeMap(JsonPath jsonPath, List<String> modelList, List<Boolean> isDisplayAttributes) {
        // init variation attribution values map
        Map<String, List<Boolean>> isDisplayAttributeMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            // get variation list map
            for (int languageIndex = 0; languageIndex < jsonPath.getList("models[%s].languages.language".formatted(modelIndex)).size(); languageIndex++) {
                boolean reuseAttribute = Boolean.parseBoolean(Optional.ofNullable(jsonPath.getString("models[%s].reuseAttributes".formatted(modelIndex))).orElse("true"));

                // get variation attribute
                if (reuseAttribute) {
                    isDisplayAttributeMap.put(modelList.get(modelIndex), isDisplayAttributes);
                } else {
                    List<Boolean> isDisplay = new ArrayList<>();
                    for (int attributeIndex = 0; attributeIndex < jsonPath.getList("models[%s].modelAttributes.id".formatted(modelIndex)).size(); attributeIndex++) {
                        isDisplay.add(jsonPath.getBoolean("models[%s].modelAttributes[%s].isDisplay".formatted(modelIndex, attributeIndex)));
                    }

                    isDisplayAttributeMap.put(modelList.get(modelIndex), isDisplay);
                }
            }
        }
        return isDisplayAttributeMap.isEmpty() ? Map.of(modelList.get(0), isDisplayAttributes) : isDisplayAttributeMap;
    }


    public ProductInfo getInfo(int productId, ProductInformationEnum... enums) {
        // get enum info
        List<ProductInformationEnum> infoEnum = Arrays.stream(enums).toList();
        if (infoEnum.isEmpty()) infoEnum = ProductInformationEnum.getAllValues();

        // get product response
        Response response = api.get(GET_PRODUCT_INFORMATION.formatted(productId), loginInfo.getAccessToken());

        // set JsonPath to get product info
        JsonPath jsonPath = response.jsonPath();

        // init product info model
        ProductInfo prdInfo = new ProductInfo();

        // set product ID
        prdInfo.setProductId(productId);

        // set deleted
        prdInfo.setDeleted((response.getStatusCode() == 404) && response.asPrettyString().contains("message"));

        // if product is not deleted, get product information
        if (response.getStatusCode() == 200) {

            // set model list
            prdInfo.setVariationModelList(getVariationModelList(jsonPath, productId));

            // set hasModel
            prdInfo.setHasModel(jsonPath.getBoolean("hasModel"));

            if (infoEnum.contains(name)) {
                // set product name
                prdInfo.setMainProductNameMap(getMainProductNameMap(jsonPath));

                // set variation product name map
                prdInfo.setVersionNameMap(getVersionNamesMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getMainProductNameMap()));
            }

            if (infoEnum.contains(description)) {
                // set product description
                prdInfo.setMainProductDescriptionMap(getMainProductDescription(jsonPath));

                // set variation product description map
                prdInfo.setVersionDescriptionMap(getVersionDescriptionsMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getMainProductDescriptionMap()));
            }

            if (infoEnum.contains(attribute)) {
                // set product attribution
                prdInfo.setAttributeNames(getAttributeNames(jsonPath));
                prdInfo.setAttributeValues(getAttributeValues(jsonPath));
                prdInfo.setIsDisplayAttributes(getIsDisplayAttributes(jsonPath));

                // set variation attribution
                prdInfo.setVariationAttributeNames(getVariationAttributionNamesMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getAttributeNames()));
                prdInfo.setVariationAttributeValues(getVariationAttributionValuesMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getAttributeValues()));
                prdInfo.setIsDisplayVariationAttributes(getIsDisplayVariationAttributeMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getIsDisplayAttributes()));
            }

            if (infoEnum.contains(SEO)) {
                // set SEO map
                prdInfo.setSeoMap(getSEOMap(jsonPath));
            }


            if (infoEnum.contains(onlineShopConfig)) {
                // set SF/Buyer app config
                prdInfo.setShowOutOfStock(isShowOutOfStock(jsonPath));
                prdInfo.setHideStock(jsonPath.getBoolean("isHideStock"));
                prdInfo.setEnabledListing(jsonPath.getBoolean("enabledListing"));
            }

            if (infoEnum.contains(inventory)) {
                // manage inventory
                try {
                    prdInfo.setManageInventoryByIMEI(jsonPath.getString("inventoryManageType").equals("IMEI_SERIAL_NUMBER"));
                } catch (NullPointerException ignored) {
                    prdInfo.setManageInventoryByIMEI(null);
                }

                // manage by lot date
                try {
                    prdInfo.setLotAvailable(jsonPath.getBoolean("lotAvailable"));
                    prdInfo.setExpiredQuality(jsonPath.getBoolean("expiredQuality"));
                } catch (NullPointerException ignored) {
                    prdInfo.setLotAvailable(false);
                    prdInfo.setExpiredQuality(false);
                }
            }

            if (infoEnum.contains(platform)) {
                // set product platform
                prdInfo.setOnApp(jsonPath.getBoolean("onApp"));
                prdInfo.setOnWeb(jsonPath.getBoolean("onWeb"));
                prdInfo.setInStore(jsonPath.getBoolean("inStore"));
                prdInfo.setInGoSocial(jsonPath.getBoolean("inGosocial"));
            }

            if (infoEnum.contains(status)) {
                // set product status
                prdInfo.setBhStatus(jsonPath.getString("bhStatus"));

                // set variation status list
                prdInfo.setVariationStatus(getVariationStatues(jsonPath, prdInfo.getBhStatus()));
            }

            if (infoEnum.contains(price)) {
                // set price
                prdInfo.setProductListingPrice(getListingPrices(response));
                prdInfo.setProductSellingPrice(getSellingPrices(response));
                prdInfo.setProductCostPrice(getCostPrices(response));
            }

            if (infoEnum.contains(variation)) {
                // set barcodes list
                prdInfo.setBarcodeList(getListBarcodes(jsonPath));

                // set variation group name map
                prdInfo.setVariationGroupNameMap(getVariationGroupNamesMap(jsonPath));

                // set variation list map
                prdInfo.setVariationValuesMap(getVariationValuesMap(jsonPath, jsonPath.getList("languages.language")));
            }

            if (infoEnum.contains(stockQuantity)) {
                // set product quantity map
                prdInfo.setProductStockQuantityMap(getProductQuantityMap(jsonPath, prdInfo.getVariationModelList()));
            }

            if (infoEnum.contains(tax)) {
                try {
                    // s.out
                    int taxId = jsonPath.getInt("taxId");
                    String taxRate = jsonPath.getString("taxSettings.find {it.id == %s}.rate".formatted(taxId));
                    prdInfo.setTaxRate(taxRate == null ? 0 : Double.parseDouble(taxRate));
                    prdInfo.setTaxName(jsonPath.getString("taxName"));
                    prdInfo.setTaxId(jsonPath.getInt("taxId"));
                } catch (NullPointerException ignored) {
                }
            }

            if (infoEnum.contains(collection)) {
                List<Integer> collectionIDList = new APIProductCollection(loginInformation).getProductListCollectionIds(productId);
                prdInfo.setCollectionIdList(collectionIDList);

                Map<Integer, Map<String, String>> collectionNameMap = new HashMap<>();
                if (!collectionIDList.isEmpty()) {
                    for (int colID : collectionIDList) {
                        Response collectionLanguage = api.get(GET_COLLECTION_LANGUAGE.formatted(colID), loginInfo.getAccessToken());
                        collectionLanguage.then().statusCode(200);
                        JsonPath collectionLanguageJson = collectionLanguage.jsonPath();
                        collectionNameMap.put(colID, IntStream.range(0, collectionLanguageJson.getList("id").size()).boxed().collect(Collectors.toMap(langID -> String.valueOf(collectionLanguageJson.getList("language").get(langID)), langID -> String.valueOf(collectionLanguageJson.getList("name").get(langID)), (a, b) -> b)));
                    }
                }

                // set collection name map
                prdInfo.setCollectionNameMap(collectionNameMap);
            }

            if (infoEnum.contains(stockAlert)) {
                prdInfo.setStockAlert(new APIStockAlert(loginInformation).getProductAlertNumber(productId));
            }
        }
        return prdInfo;
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

    String getListIMEIPath = "/itemservice/api/item-model-codes/store/%s/search?itemId=%s&modelId=%s&branchId=%s&status=AVAILABLE&page=0&size=100";

    public List<String> getListIMEI(int itemId, int modelId, int branchId) {
        return api.get(getListIMEIPath.formatted(loginInfo.getStoreID(), itemId, modelId, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("code");
    }

    public List<String> getListProductStatus(List<String> productIds) {
        return productIds.stream()
                .map(productId -> getInfo(Integer.parseInt(productId), status).getBhStatus())
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Integer> getListProductTaxId(List<String> productIds) {
        return productIds.stream()
                .map(productId -> getInfo(Integer.parseInt(productId), tax).getTaxId())
                .filter(taxId -> taxId != 0)
                .toList();
    }

    public List<Boolean> isDeleted(List<String> productIds) {
        return productIds.stream()
                .map(productId -> getInfo(Integer.parseInt(productId)).isDeleted())
                .toList();
    }

    public Map<String, List<Integer>> getCurrentProductStocksMap(List<String> productIds) {
        Map<String, List<Integer>> productQuantityMap = new HashMap<>();
        productIds.forEach(productId -> {
            Map<String, List<Integer>> stockMap = getInfo(Integer.parseInt(productId), stockQuantity).getProductStockQuantityMap();
            if (stockMap != null) {
                productQuantityMap.put(productId, stockMap.values().stream().flatMap(Collection::stream).toList());
            }
        });
        return productQuantityMap;
    }

    public List<Integer> getCurrentStockOfProducts(List<String> productIds) {
        Map<String, List<Integer>> productStocks = getCurrentProductStocksMap(productIds);
        return productStocks.values().stream().flatMap(Collection::stream).toList();
    }

    public List<Integer> getExpectedListProductStockQuantityAfterClearStock(List<String> productIds, Map<String, List<Integer>> beforeUpdateStocks) {
        Map<String, List<Integer>> productStocks = new HashMap<>(beforeUpdateStocks);
        productIds.forEach(productId -> {
            ProductInfo productInfo = getInfo(Integer.parseInt(productId), stockQuantity, inventory);
            if (productInfo.getProductStockQuantityMap() != null) {
                if (!productInfo.getLotAvailable()) {
                    productStocks.put(productId,
                            productInfo.getProductStockQuantityMap()
                                    .keySet()
                                    .stream()
                                    .flatMap(key -> branchInfo.getBranchID()
                                            .stream()
                                            .map(i -> 0)
                                            .toList()
                                            .stream())
                                    .toList());
                }
            }
        });
        return productStocks.values().stream().flatMap(Collection::stream).toList();
    }

    public List<Integer> getExpectedListProductStockQuantityAfterUpdateStock(List<String> productIds, int branchId, Map<String, List<Integer>> beforeUpdateStocks, int newStock) {
        Map<String, List<Integer>> productStocks = new HashMap<>(beforeUpdateStocks);
        for (String productId : productIds) {
            ProductInfo productInfo = getInfo(Integer.parseInt(productId), stockQuantity, inventory);
            if ((productInfo.getProductStockQuantityMap() != null)
                && !productInfo.getLotAvailable()
                && !productInfo.getManageInventoryByIMEI()) {
                List<Integer> allStocks = new ArrayList<>();
                productInfo.getProductStockQuantityMap().values().stream().map(ArrayList::new).forEach(varStocks -> {
                    varStocks.set(branchInfo.getBranchID().indexOf(branchId), newStock);
                    allStocks.addAll(varStocks);
                });
                productStocks.put(productId, allStocks);
            }
        }
        return productStocks.values().stream().flatMap(Collection::stream).toList();
    }

    public List<Boolean> getDisplayWhenOutOfStock(List<String> productIds) {
        return productIds.stream()
                .map(productId -> getInfo(Integer.parseInt(productId), onlineShopConfig).getShowOutOfStock())
                .filter(Objects::nonNull)
                .toList();
    }

    public Map<String, List<Boolean>> getMapOfListSellingPlatform(List<String> productIds) {
        // init temp arr
        List<Boolean> onApp = new ArrayList<>();
        List<Boolean> onWeb = new ArrayList<>();
        List<Boolean> inStore = new ArrayList<>();
        List<Boolean> inGoSocial = new ArrayList<>();
        productIds.stream()
                .map(productId -> getInfo(Integer.parseInt(productId), platform))
                .filter(productInfo -> productInfo.getOnWeb() != null)
                .forEach(productInfo -> {
                    onWeb.add(productInfo.getOnWeb());
                    onApp.add(productInfo.getOnApp());
                    inStore.add(productInfo.getInStore());
                    inGoSocial.add(productInfo.getInGoSocial());
                });
        return Map.of("onWeb", onWeb, "onApp", onApp, "inStore", inStore, "inGoSocial", inGoSocial);
    }

    public Map<String, List<Long>> getMapOfCurrentProductsPrice(List<String> productIds) {
        // init temp arr
        List<Long> listingPrice = new ArrayList<>();
        List<Long> sellingPrice = new ArrayList<>();
        List<Long> costPrice = new ArrayList<>();

        productIds.stream().map(productId -> getInfo(Integer.parseInt(productId), price))
                .filter(productInfo -> productInfo.getProductListingPrice() != null)
                .forEach(productInfo -> {
                    listingPrice.addAll(productInfo.getProductListingPrice());
                    sellingPrice.addAll(productInfo.getProductSellingPrice());
                    costPrice.addAll(productInfo.getProductCostPrice());
                });
        return Map.of("listingPrice", listingPrice, "sellingPrice", sellingPrice, "costPrice", costPrice);
    }

    public Map<String, List<Long>> getMapOfExpectedProductsPrice(List<String> productIds, long newListingPrice, long newSellingPrice, long newCostPrice) {
        // init temp arr
        List<Long> listingPrice = new ArrayList<>();
        List<Long> sellingPrice = new ArrayList<>();
        List<Long> costPrice = new ArrayList<>();

        productIds.stream().map(productId -> getInfo(Integer.parseInt(productId), price))
                .filter(productInfo -> productInfo.getProductListingPrice() != null)
                .forEach(productInfo -> {
                    listingPrice.addAll(IntStream.range(0, productInfo.getProductListingPrice().size()).mapToObj(index -> newListingPrice).toList());
                    sellingPrice.addAll(IntStream.range(0, productInfo.getProductSellingPrice().size()).mapToObj(index -> newSellingPrice).toList());
                    costPrice.addAll(IntStream.range(0, productInfo.getProductCostPrice().size()).mapToObj(index -> newCostPrice).toList());
                });
        return Map.of("listingPrice", listingPrice, "sellingPrice", sellingPrice, "costPrice", costPrice);
    }

    public Map<String, List<Boolean>> getMapOfCurrentManageByLotDate(List<String> productIds) {
        // init temp arr
        List<Boolean> lotAvailable = new ArrayList<>();
        List<Boolean> expiredQuality = new ArrayList<>();

        productIds.stream()
                .map(productId -> getInfo(Integer.parseInt(productId), inventory))
                .filter(productInfo -> productInfo.getManageInventoryByIMEI() != null).forEach(productInfo -> {
                    lotAvailable.add(productInfo.getLotAvailable());
                    expiredQuality.add(productInfo.getExpiredQuality());
                });
        return Map.of("lotAvailable", lotAvailable, "expiredQuality", expiredQuality);
    }

    public Map<String, List<Boolean>> getMapOfExpectedManageByLotDate(List<String> productIds,
                                                                      Map<String, List<Boolean>> beforeUpdateManageByLotDate,
                                                                      boolean isExpiredQuality) {
        // init temp arr
        List<Boolean> beforeLot = new ArrayList<>(beforeUpdateManageByLotDate.get("lotAvailable"));
        List<Boolean> beforeExpiry = new ArrayList<>(beforeUpdateManageByLotDate.get("expiredQuality"));
        List<Boolean> lotAvailable = new ArrayList<>();
        List<Boolean> expiredQuality = new ArrayList<>();
        List<Integer> listProductIdThatIsCanNotManageByLotDate = new APIInventoryHistory(loginInformation).listOfCanNotManagedByLotDateProductIds(productIds);

        productIds.forEach(productId -> {
            ProductInfo productInfo = getInfo(Integer.parseInt(productId), inventory);
            if (productInfo.getManageInventoryByIMEI() != null) {
                lotAvailable.add(beforeLot.get(lotAvailable.size()) || !productInfo.getManageInventoryByIMEI() && !listProductIdThatIsCanNotManageByLotDate.contains(Integer.parseInt(productId)));
                expiredQuality.add((!beforeLot.get(expiredQuality.size()) && lotAvailable.get(expiredQuality.size()) && beforeExpiry.get(expiredQuality.size())) || (!beforeLot.get(expiredQuality.size()) && lotAvailable.get(expiredQuality.size()) && isExpiredQuality) || (beforeLot.get(expiredQuality.size()) && beforeExpiry.get(expiredQuality.size())));
            }
        });
        return Map.of("lotAvailable", lotAvailable, "expiredQuality", expiredQuality);
    }

    public List<Integer> getListOfProductStockAlert(List<String> productIds) {
        // init temp arr
        APIStockAlert apiStockAlert = new APIStockAlert(loginInformation);
        return productIds.stream().map(productId -> apiStockAlert.getProductAlertNumber(Integer.parseInt(productId))).flatMap(Collection::stream).toList();
    }
}
