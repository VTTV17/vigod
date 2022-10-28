package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.*;

public class APIProductCollection {
    Login apiLogin = new Login();
    API api = new API();
    public static String DASHBOARD_PRODUCT_COLLECTION_LIST_PATH = "itemservice/api/collections/list/%s?page=0&size=100&itemType=BUSINESS_PRODUCT&search=";
    public int getNewestCollectionID(String storeId,String token){
        Response listCollectionRes = api.get(DASHBOARD_PRODUCT_COLLECTION_LIST_PATH.formatted(storeId),token);
        System.out.println("listCollectionRes"+listCollectionRes.jsonPath().getList("lstCollection"));
        return (int) listCollectionRes.jsonPath().getList("lstCollection.id").get(0);
    }


}
