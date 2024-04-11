package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.products.product_collections.APIProductCollection;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProductInformation {
    String GET_DASHBOARD_PRODUCT_LIST = "/itemservice/api/store/dashboard/storeID/items-v2?itemType=BUSINESS_PRODUCT&size=100&sort=lastModifiedDate%2Cdesc";
    String GET_PRODUCT_INFORMATION = "/itemservice/api/beehive-items/%s";
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
        return res.statusCode() == 200
                && (res.jsonPath().getBoolean("isHideStock") == isHideStock)
                & (res.jsonPath().getBoolean("showOutOfStock") == isDisplayIfOutOfStock)
                & (((res.jsonPath().getInt("totalItem") - res.jsonPath().getInt("totalSoldItem")) > 0) == inStock)
                & (res.jsonPath().getBoolean("hasModel") == hasModel)
                & res.jsonPath().getString("inventoryManageType").equals(manageInventoryType);
    }

    public Map<String, String> getMainProductNameMap(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].name".formatted(i)), (a, b) -> b));
    }

    public Map<String, String> getMainProductDescription(JsonPath jsonPath) {
        return IntStream.range(0, jsonPath.getList("languages.language").size()).boxed().collect(Collectors.toMap(i -> jsonPath.getString("languages[%s].language".formatted(i)), i -> jsonPath.getString("languages[%s].description".formatted(i)), (a, b) -> b));
    }

    public List<String> getAttributeGroups(JsonPath jsonPath) {
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

    public Map<String, List<String>> getVariationAttributionGroupsMap(JsonPath jsonPath, List<String> modelList, List<String> attributionGroups) {
        // init variation attribution groups map
        Map<String, List<String>> attributionGroupsMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            boolean reuseAttribute;
            try {
                reuseAttribute = jsonPath.getBoolean("models[%s].reuseAttributes".formatted(modelIndex));
            } catch (NullPointerException ignored) {
                reuseAttribute = true;
            }

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
            boolean reuseAttribute;
            try {
                reuseAttribute = jsonPath.getBoolean("models[%s].reuseAttributes".formatted(modelIndex));
            } catch (NullPointerException ignored) {
                reuseAttribute = true;
            }

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

    public Map<String, List<Boolean>> getIsDisplayVariationAttributeMap(JsonPath jsonPath, List<String> modelList, List<Boolean> isDisplayAttributes) {
        // init variation attribution values map
        Map<String, List<Boolean>> isDisplayAttributeMap = new HashMap<>();

        // get variation info
        for (int modelIndex = 0; modelIndex < jsonPath.getList("models.id").size(); modelIndex++) {
            // get variation list map
            for (int languageIndex = 0; languageIndex < jsonPath.getList("models[%s].languages.language".formatted(modelIndex)).size(); languageIndex++) {
                boolean reuseAttribute = jsonPath.getBoolean("models[%s].reuseAttributes".formatted(modelIndex));
                System.out.println("reuseAttribute: " + reuseAttribute);
//                try {
//                    reuseAttribute = jsonPath.getBoolean("models[%s].reuseAttributes".formatted(modelIndex));
//                } catch (NullPointerException ignored) {
//                    reuseAttribute = true;
//                }
//                if (reuseAttribute == null)

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


    public ProductInfo getInfo(int productId) {
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
        if ((!prdInfo.isDeleted())) {
            // check response API 200
            response.then().statusCode(200);

            // set product name
            prdInfo.setMainProductNameMap(getMainProductNameMap(jsonPath));

            // set product description
            prdInfo.setMainProductDescriptionMap(getMainProductDescription(jsonPath));

            // set product attribution
            prdInfo.setAttributeGroups(getAttributeGroups(jsonPath));
            prdInfo.setAttributeValues(getAttributeValues(jsonPath));
            prdInfo.setIsDisplayAttributes(getIsDisplayAttributes(jsonPath));

            // set SEO map
            prdInfo.setSeoMap(getSEOMap(jsonPath));

            // set hasModel
            prdInfo.setHasModel(jsonPath.getBoolean("hasModel"));

            // set SF/Buyer app config
            prdInfo.setShowOutOfStock(isShowOutOfStock(jsonPath));
            prdInfo.setHideStock(jsonPath.getBoolean("isHideStock"));
            prdInfo.setEnabledListing(jsonPath.getBoolean("enabledListing"));

            // set product platform
            prdInfo.setOnApp(jsonPath.getBoolean("onApp"));
            prdInfo.setOnWeb(jsonPath.getBoolean("onWeb"));
            prdInfo.setInStore(jsonPath.getBoolean("inStore"));
            prdInfo.setInGoSocial(jsonPath.getBoolean("inGosocial"));

            // set product status
            prdInfo.setBhStatus(jsonPath.getString("bhStatus"));

            // manage inventory
            prdInfo.setManageInventoryByIMEI(jsonPath.getString("inventoryManageType").equals("IMEI_SERIAL_NUMBER"));

            // set price
            prdInfo.setProductListingPrice(getListingPrices(response));
            prdInfo.setProductSellingPrice(getSellingPrices(response));
            prdInfo.setProductCostPrice(getCostPrices(response));

            // set model list
            prdInfo.setVariationModelList(getVariationModelList(jsonPath, productId));

            // set barcodes list
            prdInfo.setBarcodeList(getListBarcodes(jsonPath));

            // set variation group name map
            prdInfo.setVariationGroupNameMap(getVariationGroupNamesMap(jsonPath));
            
            // set variation list map
            prdInfo.setVariationValuesMap(getVariationValuesMap(jsonPath,  new ArrayList<>(prdInfo.getMainProductNameMap().keySet())));

            // set product quantity map
            prdInfo.setProductStockQuantityMap(getProductQuantityMap(jsonPath, prdInfo.getVariationModelList()));

            // set variation status list
            prdInfo.setVariationStatus(getVariationStatues(jsonPath, prdInfo.getBhStatus()));

            // set variation product name map
            prdInfo.setVersionNameMap(getVersionNamesMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getMainProductNameMap()));

            // set variation product description map
            prdInfo.setVersionDescriptionMap(getVersionDescriptionsMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getMainProductDescriptionMap()));

            // set variation attribution
            prdInfo.setVariationAttributeGroups(getVariationAttributionGroupsMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getAttributeGroups()));
            prdInfo.setVariationAttributeValues(getVariationAttributionValuesMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getAttributeValues()));
            prdInfo.setIsDisplayVariationAttributes(getIsDisplayVariationAttributeMap(jsonPath, prdInfo.getVariationModelList(), prdInfo.getIsDisplayAttributes()));


            // s.out
            try {
                int taxId = jsonPath.getInt("taxId");
                String taxRate = jsonPath.getString("taxSettings.find {it.id == %s}.rate".formatted(taxId));
                prdInfo.setTaxRate(taxRate == null ? 0 : Double.parseDouble(taxRate));

                String taxName = jsonPath.getString("taxName");
                prdInfo.setTaxName(taxName);
            } catch (NullPointerException ignore) {
            }

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
        return prdInfo;
    }

    /**
     * return {barcode, list segment, list price, list stock}
     */
    public WholesaleProductInfo wholesaleProductInfo(ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        /* get wholesale product raw data from API */
        Response wholesaleProductInfo = api.get(GET_WHOLESALE_PRODUCT_DETAIL_PATH.formatted(productInfo.getProductId()), loginInfo.getAccessToken());
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

    String getListIMEIPath = "/itemservice/api/item-model-codes/store/%s/search?itemId=%s&modelId=%s&branchId=%s&status=AVAILABLE&page=0&size=100";

    public List<String> getListIMEI(String itemId, String modelId, int branchId) {
        return api.get(getListIMEIPath.formatted(loginInfo.getStoreID(), itemId, modelId, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("code");
    }

}
