package api.dashboard.login;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import utilities.api.API;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static utilities.links.Links.URI;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    public static String accessToken;
    public static int storeID;
    public static String storeName;
    API api = new API();

    public void loginToDashboardByMail(String account, String password) {
        baseURI = URI;
        String body = """
                {
                    "username": "%s",
                    "password": "%s"
                }""".formatted(account, password);
        Response loginResponse = api.login(API_LOGIN_PATH, body);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        storeID = loginResponse.jsonPath().getInt("store.id");
        storeName = loginResponse.jsonPath().getString("store.name");
    }
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
        return map;
    }


}
