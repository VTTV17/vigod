package api.dashboard.login;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import utilities.api.API;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static utilities.links.Links.URI;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    String API_STORE_INFO_PATH = "/storeservice/api/stores/%s";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    public static String accessToken;
    public static int storeID;
    public static String storeName;
    public static String storeURL;
    API api = new API();

    Logger logger = LogManager.getLogger(Login.class);

    public void loginToDashboardByMail(String account, String password) {
        baseURI = URI;
        String body = """
                {
                    "username": "%s",
                    "password": "%s"
                }""".formatted(account, password);
        Response loginResponse = api.login(API_LOGIN_PATH, body);

        // if pre-condition can not complete -> skip test
        loginResponse.then().statusCode(200);

        // else get accessToken, storeID, storeName
        accessToken = loginResponse.jsonPath().getString("accessToken");
        storeID = loginResponse.jsonPath().getInt("store.id");
        storeName = loginResponse.jsonPath().getString("store.name");

        // get storeURL
        Response storeInfo = new API().get(API_STORE_INFO_PATH.formatted(storeID), accessToken);
        storeInfo.then().statusCode(200);
        storeURL = storeInfo.jsonPath().getString("url");
    }

    /**
     *
     * @param countryCode: example: "+84"
     * @param phoneNumber
     * @param password
     * @return Map with keys: accessToken, storeID
     */
    public Map<String,String> loginToDashboardWithPhone(String countryCode, String phoneNumber, String password) {
        RestAssured.baseURI = URI;
        String body = """
                {
                "mobile":
                    {
                        "countryCode":"%s",
                        "phoneNumber":"%s"
                    },
                "password":"%s",
                "rememberMe":true
                }""".formatted(countryCode, phoneNumber, password);
        Response loginResponse = api.login(DASHBOARD_LOGIN_PHONE_PATH, body);
        Assert.assertEquals(200,loginResponse.statusCode());
        accessToken = loginResponse.jsonPath().getString("accessToken");
        storeID = loginResponse.jsonPath().getInt("store.id");
        Map<String, String> map = new HashMap<>();
        map.put("accessToken",accessToken);
        map.put("storeID",String.valueOf(storeID));
        System.out.println(map);
        return map;
    }


}
