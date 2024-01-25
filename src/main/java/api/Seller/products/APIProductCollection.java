package api.Seller.products;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class APIProductCollection {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIProductCollection(LoginInformation loginInformation){
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public static String DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH = "itemservice/api/collections/delete/%s/%s";
    public static String DASHBOARD_PRODUCT_COLLECTION_LIST_PATH = "itemservice/api/collections/list/%s?page=%s&size=100&itemType=BUSINESS_PRODUCT&search=";
    String DASHBOARD_PRODUCT_LIST_IN_COLLECTIONS_PATH = "/itemservice/api/collections/detail/%s/%s";
    @Data
    static
    public class CollectionInfo {
        List<Integer> collectionIds;
        List<String> collectionNames;
        List<String> collectionTypes;
    }
    CollectionInfo getCollectionInfo() {
        CollectionInfo info = new CollectionInfo();

        Response res = api.get(DASHBOARD_PRODUCT_COLLECTION_LIST_PATH.formatted(loginInfo.getStoreID(), 0),loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();

        // get number of pages
        int numberOfPages = res.jsonPath().getInt("totalPage");

        List<Integer> collectionIds = new ArrayList<>(res.jsonPath().getList("lstCollection.id"));
        List<String> collectionNames = new ArrayList<>(res.jsonPath().getList("lstCollection.name"));
        List<String> collectionTypes = new ArrayList<>(res.jsonPath().getList("lstCollection.collectionType"));

        for (int pageIndex = 1; pageIndex < numberOfPages; pageIndex ++) {
            res = api.get(DASHBOARD_PRODUCT_COLLECTION_LIST_PATH.formatted(loginInfo.getStoreID(), pageIndex),loginInfo.getAccessToken())
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            collectionIds.addAll(res.jsonPath().getList("lstCollection.id"));
            collectionNames.addAll(res.jsonPath().getList("lstCollection.name"));
            collectionTypes.addAll(res.jsonPath().getList("lstCollection.collectionType"));
        }

        info.setCollectionIds(collectionIds);
        info.setCollectionNames(collectionNames);
        info.setCollectionTypes(collectionTypes);

        return info;



    }
    public int getNewestCollectionID(){
       return getCollectionInfo().getCollectionIds().get(0);
    }
    public void deleteCollection(String collectionID){
        api.delete(DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID(),collectionID),loginInfo.getAccessToken());
    }

    public List<Integer> getListProductIDInCollections(int collectionID) {
        Response collectionDetail = api.get(DASHBOARD_PRODUCT_LIST_IN_COLLECTIONS_PATH.formatted(loginInfo.getStoreID(), collectionID),  loginInfo.getAccessToken());
        collectionDetail.then().statusCode(200);
        return collectionDetail.jsonPath().getList("lstProduct.id");
    }

    public CollectionInfo getManualCollection() {
        CollectionInfo info = getCollectionInfo();
        List<Integer> collectionIds = new ArrayList<>();
        List<String> collectionNames = new ArrayList<>();
        IntStream.range(0, info.getCollectionIds().size()).filter(index -> info.getCollectionTypes().get(index).equals("MANUAL")).forEach(index -> {
            collectionIds.add(info.getCollectionIds().get(index));
            collectionNames.add(info.getCollectionNames().get(index));
        });

        CollectionInfo newInfo = new CollectionInfo();
        newInfo.setCollectionIds(collectionIds);
        newInfo.setCollectionNames(collectionNames);

        return newInfo;
    }

}
