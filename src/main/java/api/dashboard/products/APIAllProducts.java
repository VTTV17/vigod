package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Getter;
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
    public  APIAllProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    /**
     *
     * @param collectionID
     * @return product list sorted by newest "createdDate" object
     * @throws ParseException
     */
    public List<String> getProductListInCollectionByLatest(String collectionID) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",collectionID).replaceAll("%sort%",""),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        SimpleDateFormat  formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<String> productNameList = response.jsonPath().getList("name");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        for(int i = 0; i<createdDateList.size();i++){
            Date date = formatter.parse(createdDateList.get(i).replaceAll("Z$", "+0000"));
            productCreatedDateMap.put(productNameList.get(i).toLowerCase(),date);
        }
        Map<String, Date> sortedMap = SortData.sortMapByValue(productCreatedDateMap);
        List<String> productSorted = new ArrayList<>(sortedMap.keySet().stream().toList());
        Collections.reverse(productSorted);
        return productSorted;
    }

    public Map<String,Date> getProductCreatedDateMapByProductName(int collectionID,String productName) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",String.valueOf(collectionID)).replaceAll("%sort%",""),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        SimpleDateFormat  formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<String> productNameList = response.jsonPath().getList("name");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        for(int i = 0; i<productNameList.size();i++){
            if(productNameList.get(i).equalsIgnoreCase(productName)){
                Date date = formatter.parse(createdDateList.get(i).replaceAll("Z$", "+0000"));
                productCreatedDateMap.put(productNameList.get(i).toLowerCase(),date);
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
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%","").replaceAll("%sort%","lastModifiedDate,desc"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> productNameList = response.jsonPath().getList("name");
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        List<Integer> productIDList = response.jsonPath().getList("id");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        List<Boolean> hasConversionList = response.jsonPath().getList("hasConversion");
        Map<String,Integer> productCountItemMap = new HashMap();
        int count = 0;
        for (int i=0; i<productNameList.size();i++){
            String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
            Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
            switch (operator){
                case "contains","bao gồm":
                    if (productNameList.get(i).contains(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);
                    }
                    break;
                case "is equal to","tương đương":
                    if (productNameList.get(i).equals(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);                    }
                    break;
                case "starts with","bắt đầu bằng":
                    if (productNameList.get(i).startsWith(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);                    }
                    break;
                case "ends with","kết thúc bằng":
                    if (productNameList.get(i).endsWith(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);                    }
                    break;
                default: throw new Exception("Operator not match");
            }
        }
        Map productCollectionInfo = new HashMap<>();
        productCollectionInfo.put("productCreatedDateMap",productCreatedDateMap);
        productCollectionInfo.put("CountItem",count);
        productCollectionInfo.put("productCountItemMap",productCountItemMap);
        System.out.println("productCollectionInfo: "+productCollectionInfo);
        return productCollectionInfo;
    }

    public String fortmatIfCreateDateMissMiliSecond(String time) {
        Matcher m = Pattern.compile("\\d+").matcher(time);
        List<String> aa = new ArrayList<>();
        while (m.find()) {
            aa.add(m.group());
        }
        if (aa.size() < 7) {
            time = time.replaceAll("Z", ".00Z");
        }
        return time;
    }

    public Map getProductMatchPriceCondition(String operator, long value) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%","").replaceAll("%sort%","lastModifiedDate,desc"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> productNameList = response.jsonPath().getList("name");
        List<Integer> productIDList = response.jsonPath().getList("id");
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        List<Float> priceMainList = response.jsonPath().getList("newPrice",Float.class);
        List<Boolean> hasConversionList = response.jsonPath().getList("hasConversion");
        List<Integer> variationNumberList = response.jsonPath().getList("variationNumber");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        Map<String,Integer> productCountItemMap = new HashMap();
        int count =0;
        for (int i=0; i<productNameList.size();i++){
            List<Long> productPriceList = new ArrayList<>();
            if(variationNumberList.get(i)!= 0){
                Response productDetailResp = api.get(DASHBOARD_PRODUCT_DETAIL_PATH.formatted(productIDList.get(i)),loginInfo.getAccessToken());
                List<Float>productVariationPriceList = productDetailResp.jsonPath().getList("models.newPrice",Float.class);
                for (Float productVariationPrice: productVariationPriceList) {
                    productPriceList.add(productVariationPrice.longValue()); //get prices in case has variation
                }
            }else {
                Float price = priceMainList.get(i);
                Long productPrice = price.longValue();
                productPriceList.add(productPrice); //get price in case no variation.
            }
            for (Long productPrice:productPriceList) {
                boolean isChecked= false;
                String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                switch (operator){
                    case "is greater than","lớn hơn":
                        if (productPrice > value){
                            isChecked = true;
                            String productName= productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                            productCreatedDateMap.put(productName,date);
                            int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                            count = count + countConversionItem;
                            productCountItemMap.put(productName,countConversionItem);
                        }
                        break;
                    case "is less than","nhỏ hơn":
                        if (productPrice < value){
                            isChecked = true;
                            String productName= productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                            productCreatedDateMap.put(productName,date);
                            int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                            count = count + countConversionItem;
                            productCountItemMap.put(productName,countConversionItem);
                        }
                        break;
                    case "is equal to","bằng với":
                        if ( productPrice == value){
                            isChecked = true;
                            System.out.println(i+"--"+productPrice);
                            String productName= productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                            productCreatedDateMap.put(productName,date);
                            int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString());
                            count = count + countConversionItem;
                            productCountItemMap.put(productName,countConversionItem);
                        }
                        break;
                }
                if (isChecked){
                    break;
                }
            }
        }
        Map productCollectionInfo = new HashMap<>();
        productCollectionInfo.put("productCreatedDateMap",productCreatedDateMap);
        productCollectionInfo.put("CountItem",count);
        productCollectionInfo.put("productCountItemMap",productCountItemMap);
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

    public int countProductItem(boolean hasConversionUnit, String productId){
        int count;
        if(hasConversionUnit == true){
            Response conversionItemRes = api.get(DASHBOAR_CONVERSION_UNIT_ITEM_PATH.formatted(productId),loginInfo.getAccessToken());
            conversionItemRes.then().statusCode(200);
            System.out.println(conversionItemRes.prettyPrint());
            List<Integer> wholesaleProductIDList = conversionItemRes.jsonPath().getList("conversionItemList.id");
            count =1+ wholesaleProductIDList.size();
        }else {
            count =1;
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

    String allProductListPath = "/itemservice/api/store/dashboard/%s/items-v2?page=0&size=1000&bhStatus=ACTIVE&itemType=BUSINESS_PRODUCT";

    List<Integer> getListProductId(boolean hasModel) {
        Response allProducts = api.get(allProductListPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        allProducts.then().statusCode(200);
        List<Integer> variationNumber = allProducts.jsonPath().getList("variationNumber");
        List<Integer> allProductsId = allProducts.jsonPath().getList("id");
        return IntStream.range(0, allProductsId.size()).filter(i -> (variationNumber.get(i) > 0) == hasModel).mapToObj(allProductsId::get).toList();
    }

    @Getter
    private int productID;

    public int getProductIDWithoutVariationAndOutOfStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock) {
        List<Integer> listProductId = getListProductId(false);
        ProductInformation productInfo = new ProductInformation(loginInformation);
        return productID = listProductId.stream().mapToInt(productId -> productId).filter(productId -> productInfo.checkProductInfo(productId, isManageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT", false, false, isHideStock, isDisplayIfOutOfStock)).findFirst().orElse(0);
    }

    public int getProductIDWithoutVariationAndInStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock) {
        List<Integer> listProductId = getListProductId(false);
        ProductInformation productInfo = new ProductInformation(loginInformation);
        return listProductId.stream().mapToInt(productId -> productId).filter(productId -> productInfo.checkProductInfo(productId, isManageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT", false, true, isHideStock, isDisplayIfOutOfStock)).findFirst().orElse(0);
    }

    public int getProductIDWithVariationAndOutOfStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock) {
        List<Integer> listProductId = getListProductId(true);
        ProductInformation productInfo = new ProductInformation(loginInformation);
        return productID = listProductId.stream().mapToInt(productId -> productId).filter(productId -> productInfo.checkProductInfo(productId, isManageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT", true, false, isHideStock, isDisplayIfOutOfStock)).findFirst().orElse(0);
    }

    public int getProductIDWithVariationAndInStock(boolean isManageByIMEI, boolean isHideStock, boolean isDisplayIfOutOfStock) {
        List<Integer> listProductId = getListProductId(true);
        ProductInformation productInfo = new ProductInformation(loginInformation);
        return productID = listProductId.stream().mapToInt(productId -> productId).filter(productId -> productInfo.checkProductInfo(productId, isManageByIMEI ? "IMEI_SERIAL_NUMBER" : "PRODUCT", true, true, isHideStock, isDisplayIfOutOfStock)).findFirst().orElse(0);
    }

}
