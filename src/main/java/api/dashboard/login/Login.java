package api.dashboard.login;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static utilities.links.Links.URI;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    public static String accessToken;
    public static String refreshToken;
    public static int apiStoreID;
    public static String apiStoreName;
    public static int sellerID;
    API api = new API();

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

        // else get accessToken, apiStoreID, apiStoreName
        accessToken = loginResponse.jsonPath().getString("accessToken");
        refreshToken = loginResponse.jsonPath().getString("refreshToken");
        apiStoreID = loginResponse.jsonPath().getInt("store.id");
        apiStoreName = loginResponse.jsonPath().getString("store.name");
        sellerID = loginResponse.jsonPath().getInt("id");
    }

    /**
     *
     * @param countryCode: example: "+84"
     * @param phoneNumber
     * @param password
     * @return Map with keys: accessToken, apiStoreID
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
        loginResponse.then().statusCode(200);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        apiStoreID = loginResponse.jsonPath().getInt("store.id");
        Map<String, String> map = new HashMap<>();
        map.put("accessToken",accessToken);
        map.put("storeID",String.valueOf(apiStoreID));
        System.out.println(map);
        return map;
    }


}
