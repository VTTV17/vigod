import io.restassured.RestAssured;
import org.testng.annotations.Test;
import utilities.api.API;

import java.util.List;

public class APITest {
    public String accessToken;
    public String token;
    public List<Integer> listID;
    public static String URI_DASHBOARD = "https://api.mediastep.ca";
    public static String URI_SF = "https://bigdataca.mediastep.ca/";
    public static String SELLER_ACCOUNT = "bigdata_ca@yopmail.com";
    public static String SELLER_PASSWORD = "H123456@";
    public static String BUYER_ACCOUNT = "longtest006@yopmail.com";
    public static String BUYER_PASSWORD = "123456Aa@";

    public static int BRANCH_ID = 86499;


    @Test()
    public void Login() {
        RestAssured.baseURI = URI_DASHBOARD;
        String body = """
                {
                    "username": "%s",
                    "password": "%s",
                    "rememberMe": true
                }""".formatted(SELLER_ACCOUNT, SELLER_PASSWORD);
        accessToken = new API().login("/api/authenticate/store/email/gosell", body).jsonPath().getString("accessToken");
        System.out.println(accessToken);

    }

    @Test(priority = 1)
    public void getListProducts() {
        RestAssured.baseURI = URI_DASHBOARD;
        listID = new API().list("/itemservice/api/store/dashboard/86488/items-v2?langKey=vi&searchType=SKU&searchSortItemEnum=null&searchItemName=&sort=priority,desc&sort=lastModifiedDate,desc&page=0&size=300&inStock=false&saleChannel=&bhStatus=ACTIVE&branchIds=86499&shopeeId=&collectionId=&platform=&itemType=BUSINESS_PRODUCT", accessToken).jsonPath().get("id");
        System.out.println(listID);
    }

//    @Test
//    public void getUnit() {
//        RestAssured.baseURI = "https://api.beecow.info";
//        String body = """
//                {
//                    "lstItemId": [],
//                    "key": null
//                }
//                """;
//        listID = new API().search("/itemservice/api/item/conversion-units/search", accessToken, body).jsonPath().get("content.id");
//        System.out.println(listID);
//    }

    @Test(priority = 2)
    public void loginSF() {
        RestAssured.baseURI = URI_SF;
        String body = """
                {
                     "username": "%s",
                     "password": "%s",
                     "phoneCode": "+84"
                 }""".formatted(BUYER_ACCOUNT,BUYER_PASSWORD);
        token = new API().login("/api/login", body).jsonPath().getString("id_token");
        System.out.println(token);
    }

    @Test(priority = 3)
    public void addToCart() {
        String body;
        RestAssured.baseURI = URI_DASHBOARD;
        for (int id : listID) {
            body = """
                    {
                        "itemId": %d,
                        "quantity": %d,
                        "branchId": %d,
                        "langKey": "en"
                    }
                    """.formatted(id, 1, BRANCH_ID);
            new API().create("/orderservices2/api/shop-carts/add-to-cart/domain/gosell",token, body);
        }
    }
}
