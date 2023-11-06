package api.dashboard.products;

import api.dashboard.login.Login;
import api.dashboard.setting.StoreInformation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

public class ProductCollection {
    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/";
    String DELETE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/delete/%s/%s";
    String getListOfProductCollectionsPath = "/itemservice/api/collections/list/%s?page=%s&size=100&itemType=BUSINESS_PRODUCT";

    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();
    public ProductCollection(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public int createCollection(ProductInfo... productInfo) {
        String productName = productInfo.length > 0 ? productInfo[0].getDefaultProductNameMap().get(new StoreInformation(loginInformation).getInfo().getDefaultLanguage()) : "auto";
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
        Response createCollection = api.post(CREATE_PRODUCT_COLLECTION_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);

        createCollection.then().statusCode(200);

        return createCollection.jsonPath().getInt("id");
    }
    public void deleteCollection(int collectionId){
        api.delete(DELETE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID(),collectionId),loginInfo.getAccessToken());
    }

    public List<String> getListOfManualProductCollectionsName() {
        JsonPath jsonPath = api.get(getListOfProductCollectionsPath.formatted(loginInfo.getStoreID(), 0),
                        loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();
        List<String> listOfCollectionsName = new ArrayList<>(jsonPath.getList("lstCollection.findAll{it.collectionType == 'MANUAL'}.name"));
        int totalPage = jsonPath.getInt("totalPage");
        if (totalPage > 1) {
            for (int pageIndex = 1; pageIndex < totalPage; pageIndex++) {
                JsonPath jPath = api.get(getListOfProductCollectionsPath.formatted(loginInfo.getStoreID(), pageIndex),
                                loginInfo.getAccessToken())
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .jsonPath();
                listOfCollectionsName.addAll(jPath.getList("lstCollection.findAll{it.collectionType == 'MANUAL'}.name"));
            }
        }
        return listOfCollectionsName;
    }
}
