package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import org.testng.collections.Lists;
import utilities.api.API;
import utilities.sort.SortData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class APIAllProducts {
    Login apiLogin = new Login();
    API api = new API();
    public static String DASHBOARD_PRODUCT_LIST_PATH = "itemservice/api/store/dashboard/%s/items-v2?langKey=vi&searchType=PRODUCT_NAME&searchSortItemEnum=null&searchItemName=&sort=&page=-1&size=100&inStock=false&saleChannel=&bhStatus=&branchIds=&shopeeId=&collectionId=%s&platform=&itemType=BUSINESS_PRODUCT";
    public List<String> getProductListInCollectionByLatest(String storeID, String token, int collectionID) throws ParseException {
        Response response = api.get(DASHBOARD_PRODUCT_LIST_PATH.formatted(storeID,collectionID),token);
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
        System.out.println(reverseView);
        return reverseView;
    }


}
