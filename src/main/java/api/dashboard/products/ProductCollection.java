package api.dashboard.products;

import api.dashboard.login.Login;
import api.dashboard.setting.StoreInformation;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class ProductCollection {
    String CREATE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/create/";
    String DELETE_PRODUCT_COLLECTION_PATH = "/itemservice/api/collections/delete/%s/%s";

    private int collectionID;
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();
    public ProductCollection(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public ProductCollection createCollection(ProductInfo... productInfo) {
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
        Response createCollection = new API().post(CREATE_PRODUCT_COLLECTION_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);

        createCollection.then().statusCode(200);

        collectionID = createCollection.jsonPath().getInt("id");
        return this;
    }
    public void deleteCollection(int collectionId){
        api.delete(DELETE_PRODUCT_COLLECTION_PATH.formatted(loginInfo.getStoreID(),collectionId),loginInfo.getAccessToken());
    }
    public int getCollectionID() {
        return collectionID;
    }
}
