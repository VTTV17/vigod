package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIProductCollection {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIProductCollection(LoginInformation loginInformation){
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    public static String DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH = "itemservice/api/collections/delete/%s/%s";
    public static String DASHBOARD_PRODUCT_COLLECTION_LIST_PATH = "itemservice/api/collections/list/%s?page=0&size=100&itemType=BUSINESS_PRODUCT&search=";
    String DASHBOARD_PRODUCT_LIST_IN_COLLECTIONS_PATH = "/itemservice/api/collections/detail/%s/%s";
    public int getNewestCollectionID(){
        Response listCollectionRes = api.get(DASHBOARD_PRODUCT_COLLECTION_LIST_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        listCollectionRes.then().statusCode(200);
        return  (int) listCollectionRes.jsonPath().getList("lstCollection.id").get(0);
    }
    public void deleteCollection(String collectionID){
        api.delete(DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID(),collectionID),loginInfo.getAccessToken());
    }

    public List<Integer> getListProductIDInCollections(int collectionID) {
        Response collectionDetail = api.get(DASHBOARD_PRODUCT_LIST_IN_COLLECTIONS_PATH.formatted(loginInfo.getStoreID(), collectionID),  loginInfo.getAccessToken());
        collectionDetail.then().statusCode(200);
        return collectionDetail.jsonPath().getList("lstProduct.id");
    }

}
