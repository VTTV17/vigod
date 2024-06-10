package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.products.inventory.APIInventoryHistory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.sort.SortData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.Seller.products.all_products.APIProductDetail.ProductInformationEnum.inventory;

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

    private record CacheQuery(String staffToken, int... branchIds) {
    }

    private final static Cache<CacheQuery, ProductManagementInfo> allProductCache = CacheBuilder.newBuilder().build();
    String allProductListPath = "/itemservice/api/store/dashboard/%s/items-v2?page=%s&size=100&bhStatus=&itemType=BUSINESS_PRODUCT&sort=lastModifiedDate,desc&branchIds=%s";

    @Data
    public static class ProductManagementInfo {
        private List<Integer> productIds;
        private List<Integer> variationNumber;
        private List<String> productNames;
        private List<Integer> remainingStocks;
    }

    Response getAllProductsResponse(int pageIndex, int... branchIds) {
        String branchId = branchIds.length == 0 ? "" : String.valueOf(branchIds[0]);
        return api.get(allProductListPath.formatted(loginInfo.getStoreID(), pageIndex, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public ProductManagementInfo getAllProductInformation(int... branchIds) {
        CacheQuery query = new CacheQuery(loginInfo.getStaffPermissionToken(), branchIds);
        ProductManagementInfo info = allProductCache.getIfPresent(query);
        if (Optional.ofNullable(info).isEmpty()) {
            if (!loginInfo.getStaffPermissionToken().isEmpty()) {
                ProductManagementInfo tempInfo = allProductCache.getIfPresent(new CacheQuery("", branchIds));
                allProductCache.invalidateAll();
                if (Optional.ofNullable(tempInfo).isPresent()) {
                    allProductCache.put(new CacheQuery("", branchIds), tempInfo);
                }
            }

            info = new ProductManagementInfo();
            // get page 0 data
            List<Integer> variationNumber = new ArrayList<>();
            List<Integer> allProductIds = new ArrayList<>();
            List<String> allProductNames = new ArrayList<>();
            List<Integer> remainingStocks = new ArrayList<>();

            // get total products
            int totalOfProducts = Integer.parseInt(getAllProductsResponse(0, branchIds).getHeader("X-Total-Count"));

            // get number of pages
            int numberOfPages = totalOfProducts / 100;

            // get other page data
            List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numberOfPages)
                    .parallel()
                    .mapToObj(pageIndex -> getAllProductsResponse(pageIndex, branchIds).jsonPath())
                    .toList();
            jsonPaths.forEach(jsonPath -> {
                variationNumber.addAll(jsonPath.getList("variationNumber"));
                allProductIds.addAll(jsonPath.getList("id"));
                allProductNames.addAll(jsonPath.getList("name"));
                remainingStocks.addAll(jsonPath.getList("remainingStock"));
            });
            info.setProductIds(allProductIds);
            info.setVariationNumber(variationNumber);
            info.setProductNames(allProductNames);
            info.setRemainingStocks(remainingStocks);

            // save cache
            allProductCache.put(query, info);
        }
        return info;
    }

    public int searchProductIdByName(String name) {
        ProductManagementInfo info = getAllProductInformation();
        if (!info.getProductIds().isEmpty()) {
            for (int index = 0; index < info.getProductNames().size(); index++) {
                if (info.getProductNames().get(index).equals(name)) {
                    return info.getProductIds().get(index);
                }
            }
            return searchProductIdByName(name);
        }
        return 0;
    }

    public List<Integer> getListProductId() {
        return getAllProductInformation().getProductIds();
    }

    List<Integer> getListProductId(boolean hasModel, int... branchIds) {
        ProductManagementInfo info = getAllProductInformation(branchIds);
        return IntStream.range(0, info.getProductIds().size())
                .filter(i -> (info.getVariationNumber().get(i) > 0) == hasModel)
                .mapToObj(info.getProductIds()::get)
                .toList();
    }

    public int getProductIdMatchWithConditions(boolean hasModel, boolean isManageByIMEI, boolean inStock, boolean isHideStock, boolean isDisplayIfOutOfStock, int... branchIds) {
        List<Integer> listProductId = getListProductId(hasModel, branchIds);
        APIProductDetail productInfo = new APIProductDetail(loginInformation);
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

    public int getProductIdForEdit(boolean hasModel, boolean isManageByIMEI, boolean hasLot) {
        APIInventoryHistory apiInventoryHistory = new APIInventoryHistory(loginInformation);
        APIProductDetail apiProductDetail = new APIProductDetail(loginInformation);
        String manageInventoryType = isManageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT";
        ProductManagementInfo info = getAllProductInformation();
        List<Integer> productIds = info.getProductIds();
        List<Integer> numOfVariations = info.getVariationNumber();

        return productIds.parallelStream()
                .filter(id -> Objects.equals(numOfVariations.get(productIds.indexOf(id)) > 0, hasModel)
                              && apiProductDetail.checkProductInfo(id, manageInventoryType, hasLot)
                              && apiInventoryHistory.canManageByLotDate(id.toString()))
                .findAny()
                .orElse(0);
    }

    public Map<String, Integer> getCurrentStocks(List<String> productIds) {
        // get all products info
        ProductManagementInfo info = getAllProductInformation();
        List<Integer> ids = info.getProductIds();
        List<Integer> remainingStock = info.getRemainingStocks();
        return productIds.stream().collect(Collectors.toMap(productId -> productId,
                productId -> remainingStock.get(ids.indexOf(Integer.parseInt(productId))),
                (a, b) -> b));
    }

    public Map<String, Integer> getCurrentStocks(List<String> productIds, int branchId) {
        // get all products info
        ProductManagementInfo info = getAllProductInformation(branchId);
        List<Integer> ids = info.getProductIds();
        List<Integer> remainingStock = info.getRemainingStocks();
        return productIds.stream().collect(Collectors.toMap(productId -> productId,
                productId -> remainingStock.get(ids.indexOf(Integer.parseInt(productId))),
                (a, b) -> b));
    }

    public List<Integer> getListProductStockQuantityAfterClearStock(List<String> productIds) {
        // get current product stock
        Map<String, Integer> productStocks = getCurrentStocks(productIds);
        return productStocks.values().stream().toList();
    }

    public List<Integer> getExpectedListProductStockQuantityAfterClearStock(List<String> productIds, Map<String, Integer> beforeUpdateStocks) {
        // get all products info
        Map<String, Integer> productStocks = new HashMap<>(beforeUpdateStocks);
        APIProductDetail productInformation = new APIProductDetail(loginInformation);
        productIds.forEach(productId -> {
            ProductInfo productInfo = productInformation.getInfo(Integer.parseInt(productId), inventory);
            if (productInfo.getLotAvailable() == null || !productInfo.getLotAvailable()) {
                productStocks.put(productId, 0);
            }
        });
        return productStocks.values().stream().toList();
    }

    List<Integer> getVariationNumber(List<String> productIds) {
        // get all products info
        ProductManagementInfo info = getAllProductInformation();
        List<Integer> ids = info.getProductIds();
        List<Integer> variationNumbers = info.getVariationNumber();
        return productIds.stream().map(productId -> variationNumbers.get(ids.indexOf(Integer.parseInt(productId)))).toList();
    }

    public List<Integer> getListProductStockQuantityAfterUpdateStock(List<String> productIds, int branchId) {
        // get current product stock
        Map<String, Integer> productStocks = getCurrentStocks(productIds, branchId);
        return productStocks.values().stream().toList();
    }

    public List<Integer> getExpectedListProductStockQuantityAfterUpdateStock(List<String> productIds, Map<String, Integer> beforeUpdateStocks, int newStock) {
        // get all products info
        Map<String, Integer> productStocks = new HashMap<>(beforeUpdateStocks);
        APIProductDetail productInformation = new APIProductDetail(loginInformation);
        List<Integer> variationNumbers = getVariationNumber(productIds);
        productIds.forEach(productId -> {
            ProductInfo productInfo = productInformation.getInfo(Integer.parseInt(productId), inventory);
            if ((productInfo.getLotAvailable() == null || !productInfo.getLotAvailable()) && !productInfo.getManageInventoryByIMEI()) {
                int variationNumber = variationNumbers.get(productIds.indexOf(productId));
                variationNumber = (variationNumber == 0) ? 1 : variationNumber;
                productStocks.put(productId, newStock * (variationNumber));
            }
        });
        return productStocks.values().stream().toList();
    }
}
