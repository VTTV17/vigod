package api.dashboard.login;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.notNullValue;
import static utilities.links.Links.URI;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    private static String account;
    private static String password;
    public static String accessToken;
    public static String refreshToken;
    public static int apiStoreID;
    public static String apiStoreName;
    public static int sellerID;
    API api = new API();

    Response getLoginResponse(String account, String password) {
        baseURI = URI;
        String body = """
                {
                    "username": "%s",
                    "password": "%s"
                }""".formatted(account, password);
        Response loginResponse = api.login(API_LOGIN_PATH, body);

        loginResponse.then().statusCode(200);
        loginResponse.then().body("store.id", notNullValue());

        return loginResponse;
    }

    public void loginToDashboardByMail(String account, String password) {
        // set local account
        Login.account = account;

        // set local password
        Login.password = password;

        // get login response
        Response loginResponse = getLoginResponse(account, password);

        // else get accessToken, apiStoreID, apiStoreName
        accessToken = loginResponse.jsonPath().getString("accessToken");
        refreshToken = loginResponse.jsonPath().getString("refreshToken");
        apiStoreID = loginResponse.jsonPath().getInt("store.id");
        apiStoreName = loginResponse.jsonPath().getString("store.name");
        sellerID = loginResponse.jsonPath().getInt("id");
    }

    public LoginDashboardInfo getInfo() {
        // init login dashboard info model
        LoginDashboardInfo info = new LoginDashboardInfo();

        // get login response
        Response res = getLoginResponse(Login.account, Login.password);

        // set accessToken
        info.setAccessToken(res.jsonPath().getString("accessToken"));

        // set refreshToken
        info.setRefreshToken(res.jsonPath().getString("refreshToken"));

        // set sellerID
        info.setSellerID(res.jsonPath().getInt("id"));

        // set storeID
        info.setStoreID(res.jsonPath().getInt("store.id"));

        // set storeName
        info.setStoreName(res.jsonPath().getString("store.name"));

        // return login dashboard info
        return info;
    }

    /**
     * @param countryCode: example: "+84"
     * @param phoneNumber
     * @param password
     * @return Map with keys: accessToken, apiStoreID
     */
    public Map<String, String> loginToDashboardWithPhone(String countryCode, String phoneNumber, String password) {
        String token = new Login().getInfo().getAccessToken();
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
        map.put("accessToken", accessToken);
        map.put("storeID", String.valueOf(apiStoreID));
        System.out.println(map);
        return map;
    }


}
