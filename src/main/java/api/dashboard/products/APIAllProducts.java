package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import org.testng.collections.Lists;
import utilities.api.API;
import utilities.sort.SortData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIAllProducts {
    Login apiLogin = new Login();
    API api = new API();
    public static String DASHBOARD_PRODUCT_LIST_PATH = "itemservice/api/store/dashboard/%storeID%/items-v2?langKey=vi&searchType=PRODUCT_NAME&searchSortItemEnum=null&searchItemName=&sort=%sort%&page=0&size=1000&inStock=false&saleChannel=&bhStatus=&branchIds=&shopeeId=&collectionId=%collectionId%&platform=&itemType=BUSINESS_PRODUCT";
    public static String DASHBOAR_WHOLESALE_PRICE_ITEM_PATH = "itemservice/api/conversion-unit-items/item/%s";
    public static String DASHBOARD_PRODUCT_DETAIL_PATH = "itemservice/api/beehive-items/%s?langKey=vi";
    public List<String> getProductListInCollectionByLatest(String storeID, String token, String collectionID) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",storeID).replaceAll("%collectionId%",collectionID).replaceAll("%sort%",""),token);
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        SimpleDateFormat  formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<String> productNameList = response.jsonPath().getList("name");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        for(int i = 0; i<createdDateList.size();i++){
            Date date = formatter.parse(createdDateList.get(i).replaceAll("Z$", "+0000"));
            productCreatedDateMap.put(productNameList.get(i).toLowerCase(),date);
        }
        Map<String, Date> sortedMap = SortData.sortMapByValue(productCreatedDateMap);
        List<String> productSorted = sortedMap.keySet().stream().toList();
        List<String> reverseView = Lists.newReversedArrayList(productSorted);
        return reverseView;
    }
    public Map<String,Date> getProductCreatedDateMapByProductName(String storeID, String token, int collectionID,String productName) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",storeID).replaceAll("%collectionId%",String.valueOf(collectionID)).replaceAll("%sort%",""),token);
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
     *
     * @param token
     * @param storeID
     * @param operator: contains, is equal to, starts with, ends with.
     * @param value
     * @return Map with keys: productCreatedDateMap, CountItem, productCountItemMap
     * @throws ParseException
     */
    public Map getMapOfProductCreateDateMatchTitleCondition(String token, String storeID, String operator, String value) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",storeID).replaceAll("%collectionId%","").replaceAll("%sort%","lastModifiedDate,desc"),token);
        List<String> productNameList = response.jsonPath().getList("name");
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        List<Integer> productIDList = response.jsonPath().getList("id");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        List<Boolean> hasConversionList = response.jsonPath().getList("hasConversion");
        Map<String,Integer> productCountItemMap = new HashMap();
        int count =0;
        for (int i=0; i<productNameList.size();i++){
            switch (operator){
                case "contains":
                    if (productNameList.get(i).contains(value)){
                        String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                        Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);
                    }
                    break;
                case "is equal to":
                    if (productNameList.get(i).equals(value)){
                        String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                        Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);                    }
                    break;
                case "starts with":
                    if (productNameList.get(i).startsWith(value)){
                        String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                        Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);                    }
                    break;
                case "ends with":
                    if (productNameList.get(i).endsWith(value)){
                        String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                        Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        productCreatedDateMap.put(productName,date);
                        int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                        count = count + countConversionItem;
                        productCountItemMap.put(productName,countConversionItem);                    }
                    break;
            }
        }
        Map productCollectionInfo = new HashMap<>();
        productCollectionInfo.put("productCreatedDateMap",productCreatedDateMap);
        productCollectionInfo.put("CountItem",count);
        productCollectionInfo.put("productCountItemMap",productCountItemMap);
        System.out.println("productCollectionInfo: "+productCollectionInfo);
        return productCollectionInfo;
    }
//    public List<String> getListProductMatchCondition_SortNewest(Map<String,Date> productCreatedDateMap) {
//        Map<String, Date> sortedMap = SortData.sortMapByValue(productCreatedDateMap);
//        List<String> productSorted = sortedMap.keySet().stream().toList();
//        List<String> reverseView = Lists.newReversedArrayList(productSorted);
//        return reverseView;
//    }
    public String fortmatIfCreateDateMissMiliSecond(String time){
        Matcher m = Pattern.compile("\\d+").matcher(time);
        List<String> aa = new ArrayList<>();
        while (m.find()) {
            aa.add(m.group());
        }
        if (aa.size()<7) {
            time = time.replaceAll("Z", ".00Z");
        }
        return time;
    }
    public List<String> getProductListInCollectionByLatestModify(String storeID, String token, String collectionID) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",storeID).replaceAll("%collectionId%",collectionID).replaceAll("%sort%","lastModifiedDate,desc"),token);
        System.out.println(response.body().prettyPrint());
        List<String> productNameList = response.jsonPath().getList("name");
        return productNameList;
    }
    public Map getProductMatchPriceCondition(String token, String storeID, String operator, long value) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.replaceAll("%storeID%",storeID).replaceAll("%collectionId%","").replaceAll("%sort%","lastModifiedDate,desc"),token);
        List<String> productNameList = response.jsonPath().getList("name");
        List<Integer> productIDList = response.jsonPath().getList("id");
        List<String> createdDateList = response.jsonPath().getList("createdDate");
        List<Float> priceMainList = response.jsonPath().getList("newPrice");
        List<Boolean> hasConversionList = response.jsonPath().getList("hasConversion");
        List<Integer> variationNumberList = response.jsonPath().getList("variationNumber");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Map<String,Date> productCreatedDateMap = new HashMap<>();
        Map<String,Integer> productCountItemMap = new HashMap();
        int count =0;
        for (int i=0; i<productNameList.size();i++){
            List<Long> productPriceList = new ArrayList<>();
            if(variationNumberList.get(i)!= 0){
                Response productDetailResp = api.get(DASHBOARD_PRODUCT_DETAIL_PATH.formatted(productIDList.get(i)),token);
                List<Float>productVariationPriceList = productDetailResp.jsonPath().getList("models.newPrice");
                for (Float productVariationPrice: productVariationPriceList) {
                    productPriceList.add(productVariationPrice.longValue());
                }
            }else {
                Float price = priceMainList.get(i);
                Long productPrice = price.longValue();
                productPriceList.add(productPrice);
            }
            for (Long productPrice:productPriceList) {
                boolean isChecked= false;
                switch (operator){
                    case "is greater than":
                        if (productPrice > value){
                            isChecked = true;
                            String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                            Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                            String productName= productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                            productCreatedDateMap.put(productName,date);
                            int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                            count = count + countConversionItem;
                            productCountItemMap.put(productName,countConversionItem);
                        }
                        break;
                    case "is less than":
                        if (productPrice < value){
                            isChecked = true;
                            String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                            Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                            String productName= productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                            productCreatedDateMap.put(productName,date);
                            int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                            count = count + countConversionItem;
                            productCountItemMap.put(productName,countConversionItem);
                        }
                        break;
                    case "is equal to":
                        if ( productPrice == value){
                            isChecked = true;
                            System.out.println(i+"--"+productPrice);
                            String createDate = fortmatIfCreateDateMissMiliSecond(createdDateList.get(i));
                            Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
                            String productName= productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                            productCreatedDateMap.put(productName,date);
                            int countConversionItem = countProductItem(hasConversionList.get(i),productIDList.get(i).toString(),token);
                            count = count + countConversionItem;
                            productCountItemMap.put(productName,countConversionItem);
                        }
                        break;
                }
                if (isChecked == true){
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
     *
     * @param productCollectionInfo Map with keys: productCreatedDateMap... --- Get from function: getProductMatchPriceCondition, getMapOfProductCreateDateMatchTitleCondition
     * @return product list sorted
     */
    public List<String> getProductListCollection_SortNewest(Map productCollectionInfo){
//        Map<String, Date> productCreatedDateMap = (Map<String, Date>) productCollectionInfo.get("productCreatedDateMap");
        Map<String, Date> sortedMap = SortData.sortMapByValue(productCollectionInfo);
        List<String> productSorted = sortedMap.keySet().stream().toList();
        List<String> reverseView = Lists.newReversedArrayList(productSorted);
        return reverseView;
    }

    public int countProductItem(boolean hasConversionUnit, String productId, String token){
        int count;
        if(hasConversionUnit == true){
            Response conversionItemRes = api.get(DASHBOAR_WHOLESALE_PRICE_ITEM_PATH.formatted(productId),token);
            System.out.println(conversionItemRes.prettyPrint());
            List<Integer> wholesaleProductIDList = conversionItemRes.jsonPath().getList("conversionItemList.id");
            count =1+ wholesaleProductIDList.size();
        }else {
            count =1;
        }
        return count;
    }
    public Map productsBelongCollectionExpected_MultipleCondition(String token, String storeId,String conditionType, String... conditions) throws ParseException {
        APIAllProducts apiAllProducts = new APIAllProducts();
        int countItemExpected = 0;
        Map mergeProductMap = new HashMap<>();
        Map mergeProductMap_SortByNewest = new HashMap<>();

        Map mergeProductCountItemMap = new HashMap<>();
        Map compareProductMap = new HashMap<>();
        Map compareCountItemMap = new HashMap<>();
        for (String condition : conditions) {
            String conditionField = condition.split("-")[0];
            String operater = condition.split("-")[1];
            String value = condition.split("-")[2];
            Map productCreatedDateMap = new HashMap();
            Map productCountItemMap = new HashMap();
            if (conditionField.equalsIgnoreCase("Product title")) {
                Map productCollection = apiAllProducts.getMapOfProductCreateDateMatchTitleCondition(token, storeId, operater, value);
                productCreatedDateMap = (Map) productCollection.get("productCreatedDateMap");
                productCountItemMap = (Map) productCollection.get("productCountItemMap");
            } else if (conditionField.equalsIgnoreCase("Product price")) {
                Map productCollection = apiAllProducts.getProductMatchPriceCondition(token, storeId, operater, Long.parseLong(value));
                productCreatedDateMap = (Map) productCollection.get("productCreatedDateMap");
                productCountItemMap = (Map) productCollection.get("productCountItemMap");
            }
            if (conditionType.equalsIgnoreCase("Any condition")) {
                mergeProductMap.putAll(productCreatedDateMap);
                mergeProductCountItemMap.putAll(productCountItemMap);
            } else if (conditionType.equalsIgnoreCase("All conditions")) {
                if (compareProductMap.isEmpty()) {
                    compareProductMap.putAll(productCreatedDateMap);
                    compareCountItemMap.putAll(productCountItemMap);
                } else {
                    for (Object key : productCreatedDateMap.keySet()) {
                        if (compareProductMap.containsKey(key)) {
                            mergeProductMap.put(key, productCreatedDateMap.get(key));
                            mergeProductCountItemMap.put(key, productCountItemMap.get(key));
                        }
                    }
                }
            }
        }
        Collection<Integer> values = mergeProductCountItemMap.values();
        System.out.println("values: "+values);
        for (int v : values) {
            countItemExpected = countItemExpected + v;
        }
//        mergeProductMap_SortByNewest.put("productCreatedDateMap",mergeProductMap);
        System.out.println("mergeProductMap_SortByNewest: "+mergeProductMap_SortByNewest);
        List<String> productExpectedList =  apiAllProducts.getProductListCollection_SortNewest(mergeProductMap);
        System.out.println("productExpectedList1: "+productExpectedList);
        Map productCollectInfoMap = new HashMap<>();
        productCollectInfoMap.put("productExpectedList", productExpectedList);
        productCollectInfoMap.put("CountItem", countItemExpected);
        return productCollectInfoMap;
    }
    public static List<String> sortProductListByPriorityAndUpdatedDate(Map<String, Integer> productPriorityMap, String storeID, String token, int collectionID) throws ParseException {
        Map<String, Integer> sortedMap = SortData.sortMapByValue(productPriorityMap);
        List<String> sortedList = new ArrayList<>();
        List<Integer> values = sortedMap.values().stream().toList();
        Map<String, Date> productUpdatedMap = new HashMap<>();
        APIAllProducts apiAllProducts = new APIAllProducts();
        for (int i = 0; i < values.size(); i++) {
            String productKey1 = sortedMap.keySet().toArray()[i].toString();
            String productKey2 ;

            int value1 = values.get(i);
            int value2;
            if (i == values.size() - 1) {
                value2 = values.get(i - 1);
                productKey2 = sortedMap.keySet().toArray()[i-1].toString();
            } else {
                value2 = values.get(i + 1);
                productKey2 = sortedMap.keySet().toArray()[i+1].toString();
            }
            if (value1 == value2) {
                productUpdatedMap.putAll(apiAllProducts.getProductCreatedDateMapByProductName(storeID, token, collectionID, productKey1));
                productUpdatedMap.putAll(apiAllProducts.getProductCreatedDateMapByProductName(storeID, token, collectionID, productKey2));
                if(i == values.size()-1){
                    sortedList.addAll(apiAllProducts.getProductListCollection_SortNewest(productUpdatedMap));
                }
            }else if (productUpdatedMap.isEmpty()) {
                sortedList.add(productKey1);
            } else {
                sortedList.addAll(apiAllProducts.getProductListCollection_SortNewest(productUpdatedMap));
                productUpdatedMap = new HashMap<>();
            }

        }
        System.out.println("sortedList: " + sortedList);
        return sortedList;
    }
}
