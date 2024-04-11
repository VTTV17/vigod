package api.Seller.products.product_collections;

import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class APIProductCollection {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIProductCollection(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public String DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH = "itemservice/api/collections/delete/%s/%s";
    public String DASHBOARD_PRODUCT_COLLECTION_LIST_PATH = "itemservice/api/collections/list/%s?page=%s&size=100&itemType=BUSINESS_PRODUCT&search=";

    @Data
    static
    public class CollectionInfo {
        List<Integer> collectionIds = new ArrayList<>();
        List<String> collectionNames = new ArrayList<>();
        List<String> collectionTypes = new ArrayList<>();
    }

    public Response getCollectionListResponse(int pageIndex) {
        return api.get(DASHBOARD_PRODUCT_COLLECTION_LIST_PATH.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public CollectionInfo getCollectionInfo() {
        CollectionInfo info = new CollectionInfo();

        Response collectionListResponse = getCollectionListResponse(0);

        // if staff do not have permission, end.
        if (collectionListResponse.getStatusCode() == 403) return info;

        // get number of pages
        int numberOfPages = collectionListResponse.jsonPath().getInt("totalPage");

        List<Integer> collectionIds = new ArrayList<>();
        List<String> collectionNames = new ArrayList<>();
        List<String> collectionTypes = new ArrayList<>();

        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
            collectionListResponse = getCollectionListResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            collectionIds.addAll(collectionListResponse.jsonPath().getList("lstCollection.id"));
            collectionNames.addAll(collectionListResponse.jsonPath().getList("lstCollection.name"));
            collectionTypes.addAll(collectionListResponse.jsonPath().getList("lstCollection.collectionType"));
        }

        info.setCollectionIds(collectionIds);
        info.setCollectionNames(collectionNames);
        info.setCollectionTypes(collectionTypes);

        return info;
    }

    public int getNewestCollectionID() {
        return getCollectionInfo().getCollectionIds().get(0);
    }

    public void deleteCollection(String collectionID) {
        api.delete(DASHBOARD_DELETE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID(), collectionID), loginInfo.getAccessToken());
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

    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/%s";

    public int createCollection(ProductInfo... productInfo) {
        String productName = productInfo.length > 0 ? productInfo[0].getMainProductNameMap().get(new StoreInformation(loginInformation).getInfo().getDefaultLanguage()) : "auto";
        String collectionName = "Auto - Collections - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "collectionType": "AUTOMATED",
                    "lstImage": [],
                    "lstCondition": [
                        {
                            "conditionField": "PRODUCT_NAME",
                            "operand": "CONTAINS",
                            "values": [
                                {
                                    "value": "%s"
                                }
                            ]
                        }
                    ],
                    "conditionType": "ALL",
                    "lstProduct": [],
                    "itemType": "BUSINESS_PRODUCT",
                    "bcStoreId": "%s"
                }""".formatted(collectionName, productName, loginInfo.getStoreID());
        return api.post(CREATE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");
    }

    String DELETE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/delete/%s/%s";

    public void deleteCollection(int collectionId) {
        api.delete(DELETE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID(), collectionId), loginInfo.getAccessToken());
    }

    String GET_PRODUCT_COLLECTION = "/itemservice/api/collections/products/%s";

    public List<Integer> getProductListCollectionIds(int productID) {
        return (productID != 0)
                ? api.get(GET_PRODUCT_COLLECTION.formatted(productID), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("id")
                : new ArrayList<>();
    }
}
