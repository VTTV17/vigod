package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.*;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class APIProductCollection {
    Login apiLogin = new Login();
    API api = new API();
    public static String DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH = "itemservice/api/collections/delete/%s/%s";
    public static String DASHBOARD_PRODUCT_COLLECTION_LIST_PATH = "itemservice/api/collections/list/%s?page=0&size=100&itemType=BUSINESS_PRODUCT&search=";
    String DASHBOARD_PRODUCT_LIST_IN_COLLECTIONS_PATH = "/itemservice/api/collections/detail/%s/%s";
    public int getNewestCollectionID(String storeId,String token){
        Response listCollectionRes = api.get(DASHBOARD_PRODUCT_COLLECTION_LIST_PATH.formatted(storeId),token);
        listCollectionRes.then().statusCode(200);
        return  (int) listCollectionRes.jsonPath().getList("lstCollection.id").get(0);
    }
    public void deleteCollection(String token, String storeID, String collectionID){
        api.delete(DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH.formatted(storeID,collectionID),token);
    }

    public List<Integer> getListProductIDInCollections(int collectionID) {
        Response collectionDetail = api.get(DASHBOARD_PRODUCT_LIST_IN_COLLECTIONS_PATH.formatted(apiStoreID, collectionID), accessToken);
        collectionDetail.then().statusCode(200);

        return collectionDetail.jsonPath().getList("lstProduct.id");
    }

}
