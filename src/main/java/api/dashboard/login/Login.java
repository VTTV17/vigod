package api.dashboard.login;

import io.restassured.response.Response;
import utilities.api.API;

import static io.restassured.RestAssured.baseURI;

public class Login {
    String URI = "https://api.beecow.info";
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";

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
}
