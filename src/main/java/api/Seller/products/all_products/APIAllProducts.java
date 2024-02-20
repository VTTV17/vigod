package api.Seller.products.all_products;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.sort.SortData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class APIAllProducts {
    API api = new API();
    LoginDashboardInfo loginInfo;
    public static String DASHBOARD_PRODUCT_LIST_PATH = "itemservice/api/store/dashboard/%storeID%/items-v2?langKey=vi&searchType=PRODUCT_NAME&searchSortItemEnum=null&searchItemName=&sort=%sort%&page=0&size=1000&inStock=false&saleChannel=&bhStatus=&branchIds=&shopeeId=&collectionId=%collectionId%&platform=&itemType=BUSINESS_PRODUCT";
    public static String DASHBOAR_CONVERSION_UNIT_ITEM_PATH = "itemservice/api/conversion-unit-items/item/%s";
    public static String DASHBOARD_PRODUCT_DETAIL_PATH = "itemservice/api/beehive-items/%s?langKey=vi";
    LoginInformation loginInformation;

    public APIAllProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    /**
     * @param collectionID
     * @return product list sorted by newest "createdDate" object
     * @throws ParseException
     */
    public List<String> getProductListInCollectionByLatest(String collectionID) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%", String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%", collectionID).replaceAll("%sort%", ""), loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<String> productNameList = response.jsonPath().getList("name");
        Map<String, Date> productCreatedDateMap = new HashMap<>();
        for (int i = 0; i < createdDateList.size(); i++) {
            Date date = formatter.parse(createdDateList.get(i).replaceAll("Z$", "+0000"));
            productCreatedDateMap.put(productNameList.get(i).toLowerCase(), date);
        }
        Map<String, Date> sortedMap = SortData.sortMapByValue(productCreatedDateMap);
        List<String> productSorted = new ArrayList<>(sortedMap.keySet().stream().toList());
        Collections.reverse(productSorted);
        return productSorted;
    }

    public Map<String, Date> getProductCreatedDateMapByProductName(int collectionID, String productName) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%", String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%", String.valueOf(collectionID)).replaceAll("%sort%", ""), loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<String> productNameList = response.jsonPath().getList("name");
        Map<String, Date> productCreatedDateMap = new HashMap<>();
        for (int i = 0; i < productNameList.size(); i++) {
            if (productNameList.get(i).equalsIgnoreCase(productName)) {
                Date date = formatter.parse(createdDateList.get(i).replaceAll("Z$", "+0000"));
                productCreatedDateMap.put(productNameList.get(i).toLowerCase(), date);
                break;
            }
        }
        return productCreatedDateMap;
    }

    /**
     * @param operator: contains, is equal to, starts with, ends with.
     * @param value
     * @return Map with keys: productCreatedDateMap, CountItem, productCountItemMap
     * @throws ParseException
     */
    public Map getMapOfProductCreateDateMatchTitleCondition(String operator, String value) throws Exception {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%", String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%", "").replaceAll("%sort%", "lastModifiedDate,desc"), loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> productNameList = response.jsonPath().getList("name");
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        List<Integer> productIDList = response.jsonPath().getList("id");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Map<String, Date> productCreatedDateMap = new HashMap<>();
        List<Boolean> hasConversionList = response.jsonPath().getList("hasConversion");
        Map<String, Integer> productCountItemMap = new HashMap();
        int count = 0;
        for (int i = 0; i < productNameList.size(); i++) {
            String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
            Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
            switch (operator) {
                case "contains", "bao gồm":
                    if (productNameList.get(i).contains(value)) {
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                        productCreatedDateMap.put(productName, date);
                        int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName, countConversionItem);
                    }
                    break;
                case "is equal to", "tương đương":
                    if (productNameList.get(i).equals(value)) {
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                        productCreatedDateMap.put(productName, date);
                        int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName, countConversionItem);
                    }
                    break;
                case "starts with", "bắt đầu bằng":
                    if (productNameList.get(i).startsWith(value)) {
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                        productCreatedDateMap.put(productName, date);
                        int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName, countConversionItem);
                    }
                    break;
                case "ends with", "kết thúc bằng":
                    if (productNameList.get(i).endsWith(value)) {
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                        productCreatedDateMap.put(productName, date);
                        int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName, countConversionItem);
                    }
                    break;
                default:
                    throw new Exception("Operator not match");
            }
        }
        Map productCollectionInfo = new HashMap<>();
        productCollectionInfo.put("productCreatedDateMap", productCreatedDateMap);
        productCollectionInfo.put("CountItem", count);
        productCollectionInfo.put("productCountItemMap", productCountItemMap);
        System.out.println("productCollectionInfo: " + productCollectionInfo);
        return productCollectionInfo;
    }

    public String fortmatIfCreateDateMissMiliSecond(String time) {
        //handle when missing milisecond
        Matcher m = Pattern.compile("\\d+").matcher(time);
        List<String> aa = new ArrayList<>();
        while (m.find()) {
            aa.add(m.group());
        }
        if (aa.size() < 7) {
            time = time.replaceAll("Z", ".0000Z");
        }
        //handle when milisecond has more than 4 characters
        String minisecond = time.split("\\.|Z")[1];
        if (minisecond.length() > 4) {
            minisecond = minisecond.substring(0, 4);
        }
        time = time.split("\\.|Z")[0] + "." + minisecond + "Z";
        return time;
    }

    public Map getProductMatchPriceCondition(String operator, long value) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%", String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%", "").replaceAll("%sort%", "lastModifiedDate,desc"), loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> productNameList = response.jsonPath().getList("name");
        List<Integer> productIDList = response.jsonPath().getList("id");
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        List<Float> priceMainList = response.jsonPath().getList("newPrice", Float.class);
        List<Boolean> hasConversionList = response.jsonPath().getList("hasConversion");
        List<Integer> variationNumberList = response.jsonPath().getList("variationNumber");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Map<String, Date> productCreatedDateMap = new HashMap<>();
        Map<String, Integer> productCountItemMap = new HashMap();
        int count = 0;
        for (int i = 0; i < productNameList.size(); i++) {
            List<Long> productPriceList = new ArrayList<>();
            if (variationNumberList.get(i) != 0) {
                Response productDetailResp = api.get(DASHBOARD_PRODUCT_DETAIL_PATH.formatted(productIDList.get(i)), loginInfo.getAccessToken());
                List<Float> productVariationPriceList = productDetailResp.jsonPath().getList("models.newPrice", Float.class);
                for (Float productVariationPrice : productVariationPriceList) {
                    productPriceList.add(productVariationPrice.longValue()); //get prices in case has variation
                }
            } else {
                Float price = priceMainList.get(i);
                Long productPrice = price.longValue();
                productPriceList.add(productPrice); //get price in case no variation.
            }
            for (Long productPrice : productPriceList) {
                boolean isChecked = false;
                String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                String dateString = createDate.replaceAll("Z$", "+0000");
                Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                switch (operator) {
                    case "is greater than", "lớn hơn":
                        if (productPrice > value) {
                            isChecked = true;
                            String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                            productCreatedDateMap.put(productName, date);
                            int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                            count = count + countConversionItem;
                            productCountItemMap.put(productName, countConversionItem);
                            System.out.println(productName + "---" + createDate + "---" + date.toString() + "---" + dateString);
                        }
                        break;
                    case "is less than", "nhỏ hơn":
                        if (productPrice < value) {
                            isChecked = true;
                            String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                            productCreatedDateMap.put(productName, date);
                            int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                            count = count + countConversionItem;
                            productCountItemMap.put(productName, countConversionItem);
                        }
                        break;
                    case "is equal to", "bằng với":
                        if (productPrice == value) {
                            isChecked = true;
                            System.out.println(i + "--" + productPrice);
                            String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                            productCreatedDateMap.put(productName, date);
                            int countConversionItem = countProductItem(hasConversionList.get(i), productIDList.get(i).toString());
                            count = count + countConversionItem;
                            productCountItemMap.put(productName, countConversionItem);
                        }
                        break;
                }
                if (isChecked) {
                    break;
                }
            }
        }
        Map productCollectionInfo = new HashMap<>();
        productCollectionInfo.put("productCreatedDateMap", productCreatedDateMap);
        productCollectionInfo.put("CountItem", count);
        productCollectionInfo.put("productCountItemMap", productCountItemMap);
        System.out.println("productCollectionInfo: before sort: " + productCollectionInfo);
        return productCollectionInfo;
    }

    /**
     * @param productCollectionInfo Map with keys: productCreatedDateMap... --- Get from function: getProductMatchPriceCondition, getMapOfProductCreateDateMatchTitleCondition
     * @return product list sorted
     */
    public List<String> getProductListCollection_SortNewest(Map productCollectionInfo) {
        Map<String, Date> sortedMap = SortData.sortMapByValue(productCollectionInfo);
        List<String> productSorted = new ArrayList<>(sortedMap.keySet().stream().toList());
        Collections.reverse(productSorted);
        return productSorted;
    }

    public int countProductItem(boolean hasConversionUnit, String productId) {
        int count;
        if (hasConversionUnit) {
            Response conversionItemRes = api.get(DASHBOAR_CONVERSION_UNIT_ITEM_PATH.formatted(productId), loginInfo.getAccessToken());
            conversionItemRes.then().statusCode(200);
            System.out.println(conversionItemRes.prettyPrint());
            List<Integer> wholesaleProductIDList = conversionItemRes.jsonPath().getList("conversionItemList.id");
            count = 1 + wholesaleProductIDList.size();
        } else {
            count = 1;
        }
        return count;
    }

    public JsonPath getProductConversionUnitJsonPath(int productID) {
        Response response = api.get(DASHBOAR_CONVERSION_UNIT_ITEM_PATH.formatted(productID), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath();
    }

    /**
     * Returns a list of conversion unit names for the given product ID.
     *
     * @param productID the ID of the product to retrieve conversion units for
     * @return
     */
    public List<String> getConversionUnitsOfProduct(int productID) {
        return getProductConversionUnitJsonPath(productID).getList("conversionItemList.unitName");
    }

    public JsonPath getAllProductJsonPath() {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%", String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%", "").replaceAll("%sort%", ""), loginInfo.getAccessToken());
        response.then().statusCode(200);
        response.jsonPath().prettyPeek();
        return response.jsonPath();
    }

    public List<String> getAllProductNames() {
        return getAllProductJsonPath().getList("name");
    }

    String allProductListPath = "/itemservice/api/store/dashboard/%s/items-v2?page=%s&size=100&bhStatus=&itemType=BUSINESS_PRODUCT&sort=lastModifiedDate,desc&branchIds=%s";

    @Data
    public static class ProductManagementInfo {
        private List<Integer> productIds;
        private List<Integer> variationNumber;
        private List<String> productNames;
    }

    Response getAllProductsResponse(int pageIndex, int... branchIds) {
        String branchId = branchIds.length == 0 ? "" : String.valueOf(branchIds[0]);
        return api.get(allProductListPath.formatted(loginInfo.getStoreID(), pageIndex, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public ProductManagementInfo getListProduct(int... branchIds) {

        ProductManagementInfo info = new ProductManagementInfo();
        // get page 0 data
        List<Integer> variationNumber = new ArrayList<>();
        List<Integer> allProductIds = new ArrayList<>();
        List<String> allProductNames = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getAllProductsResponse(0, branchIds).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            Response allProducts = getAllProductsResponse(pageIndex, branchIds);
            variationNumber.addAll(allProducts.jsonPath().getList("variationNumber"));
            allProductIds.addAll(allProducts.jsonPath().getList("id"));
            allProductNames.addAll(allProducts.jsonPath().getList("name"));
        }
        info.setProductIds(allProductIds);
        info.setVariationNumber(variationNumber);
        info.setProductNames(allProductNames);
        return info;
    }

    public int searchProductIdByName(String name) {
        ProductManagementInfo info = getListProduct();
        for (int index = 0; index < info.getProductNames().size(); index++) {
            if (info.getProductNames().get(index).equals(name)) {
                return info.getProductIds().get(index);
            }
        }
        return 0;
    }

    List<Integer> getListProductId(boolean hasModel, int... branchIds) {
        ProductManagementInfo info = getListProduct(branchIds);
        return IntStream.range(0, info.getProductIds().size())
                .filter(i -> (info.getVariationNumber().get(i) > 0) == hasModel)
                .mapToObj(info.getProductIds()::get)
                .toList();
    }

    public int getProductIdMatchWithConditions(boolean hasModel, boolean isManageByIMEI, boolean inStock, boolean isHideStock, boolean isDisplayIfOutOfStock, int... branchIds) {
        List<Integer> listProductId = getListProductId(hasModel, branchIds);
        ProductInformation productInfo = new ProductInformation(loginInformation);
        return listProductId.stream()
                .mapToInt(productId -> productId)
                .filter(productId -> productInfo.checkProductInfo(productId,
                        isManageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT",
                        hasModel,
                        inStock,
                        isHideStock,
                        isDisplayIfOutOfStock))
                .findFirst()
                .orElse(0);
    }

    public int getProductIDWithoutVariationAndOutOfStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock, int... branchIds) {
        return getProductIdMatchWithConditions(false, isManageByIMEI, false, isHideStock, isDisplayIfOutOfStock, branchIds);
    }

    public int getProductIDWithoutVariationAndInStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock, int... branchIds) {
        return getProductIdMatchWithConditions(false, isManageByIMEI, true, isHideStock, isDisplayIfOutOfStock, branchIds);
    }

    public int getProductIDWithVariationAndOutOfStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock, int... branchIds) {
        return getProductIdMatchWithConditions(true, isManageByIMEI, false, isHideStock, isDisplayIfOutOfStock, branchIds);
    }

    public int getProductIDWithVariationAndInStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock, int... branchIds) {
        return getProductIdMatchWithConditions(true, isManageByIMEI, true, isHideStock, isDisplayIfOutOfStock, branchIds);
    }

    String suggestProductPath = "/itemservice/api/store/%s/item-model/suggestion?page=%s&size=100&ignoreDeposit=true&branchId=%s&ignoreOutOfStock=true&includeConversion=true";
    @Data
    public static class SuggestionProductsInfo {
        private List<String> itemIds;
        private List<String> modelIds;
        private List<String> itemNames;
        private List<String> barcodes;
        private List<Long> remainingStocks;
        private List<String> inventoryManageTypes;
    }

    Response getSuggestionResponse(int pageIndex, int branchId) {
        return api.get(suggestProductPath.formatted(loginInfo.getStoreID(), pageIndex, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public SuggestionProductsInfo getListProduct(int branchId) {
        // init suggestion model
        SuggestionProductsInfo info = new SuggestionProductsInfo();

        // init temp array
        List<String> itemIds = new ArrayList<>();
        List<String> modelIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> barcodes = new ArrayList<>();
        List<String> remainingStocks = new ArrayList<>();
        List<String> inventoryManageTypes = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getSuggestionResponse(0, branchId).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            Response suggestProducts = getSuggestionResponse(pageIndex, branchId);
            itemIds.addAll(suggestProducts.jsonPath().getList("itemId"));
            modelIds.addAll(suggestProducts.jsonPath().getList("modelId"));
            itemNames.addAll(suggestProducts.jsonPath().getList("itemName"));
            barcodes.addAll(suggestProducts.jsonPath().getList("barcode"));
            remainingStocks.addAll(suggestProducts.jsonPath().getList("modelStock"));
            inventoryManageTypes.addAll(suggestProducts.jsonPath().getList("inventoryManageType"));
        }

        // set suggestion info
        info.setItemIds(itemIds);
        info.setModelIds(modelIds);
        info.setItemNames(itemNames);
        info.setBarcodes(barcodes);
        info.setRemainingStocks(remainingStocks.stream().map(Long::parseLong).toList());
        info.setInventoryManageTypes(inventoryManageTypes);

        // return suggestion model
        return info;
    }

    public SuggestionProductsInfo getSuggestProductIdMatchWithConditions(int branchId) {
        // get all suggestions information
        SuggestionProductsInfo suggestionInfo = getListProduct(branchId);

        // init suggestion model to get all products in-stock
        SuggestionProductsInfo info = new SuggestionProductsInfo();

        // init temp array
        List<String> itemIds = new ArrayList<>();
        List<String> modelIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> barcodes = new ArrayList<>();
        List<Long> remainingStocks = new ArrayList<>();
        List<String> inventoryManageTypes = new ArrayList<>();

        // filter by in-stock conditions
        IntStream.range(0, suggestionInfo.getItemIds().size())
                .filter(index -> (suggestionInfo.getRemainingStocks().get(index) > 0))
                .forEach(index -> {
                    itemIds.add(suggestionInfo.getItemIds().get(index));
                    itemNames.add(suggestionInfo.getItemNames().get(index));
                    modelIds.add(suggestionInfo.getModelIds().get(index));
                    barcodes.add(suggestionInfo.getBarcodes().get(index));
                    remainingStocks.add(suggestionInfo.getRemainingStocks().get(index));
                    inventoryManageTypes.add(suggestionInfo.getInventoryManageTypes().get(index));
                });

        // set in-stock all suggestions
        info.setItemIds(itemIds);
        info.setModelIds(modelIds);
        info.setItemNames(itemNames);
        info.setBarcodes(barcodes);
        info.setRemainingStocks(remainingStocks);
        info.setInventoryManageTypes(inventoryManageTypes);

        // return model
        return info;
    }

    public SuggestionProductsInfo getSuggestProductIdMatchWithConditions(boolean hasModel, boolean isManageByIMEI, int branchId) {
        SuggestionProductsInfo suggestionInfo = getListProduct(branchId);
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        List<String> itemIds = new ArrayList<>();
        List<String> modelIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> barcodes = new ArrayList<>();
        List<String> inventoryManageTypes = new ArrayList<>();
        IntStream.range(0, suggestionInfo.getItemIds().size())
                .filter(index -> (suggestionInfo.getModelIds().get(index).isEmpty() != hasModel)
                        && (suggestionInfo.getInventoryManageTypes().get(index).equals("IMEI_SERIAL_NUMBER") == isManageByIMEI)
                        && (suggestionInfo.getRemainingStocks().get(index) > 0))
                .forEach(index -> {
                    itemIds.add(suggestionInfo.getItemIds().get(index));
                    itemNames.add(suggestionInfo.getItemNames().get(index));
                    modelIds.add(suggestionInfo.getModelIds().get(index));
                    barcodes.add(suggestionInfo.getBarcodes().get(index));
                    inventoryManageTypes.add(suggestionInfo.getInventoryManageTypes().get(index));
                });
        info.setItemIds(itemIds);
        info.setModelIds(modelIds);
        info.setItemNames(itemNames);
        info.setBarcodes(barcodes);
        info.setInventoryManageTypes(inventoryManageTypes);

        return info;
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
