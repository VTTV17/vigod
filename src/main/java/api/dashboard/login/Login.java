package api.dashboard.login;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.notNullValue;
import static utilities.links.Links.URI;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    API api = new API();
    private static LoginInformation loginInfo = new LoginInformation();

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

    public Login setLoginInformation(String account, String password) {
        if (loginInfo.getPassword() != null) loginInfo = new LoginInformation();

        // set local account
        loginInfo.setEmail(account);

        // set local password
        loginInfo.setPassword(password);

        return this;
    }

    /**
     * Sets the dashboard login information for a user with the given phone code, username and password.
     */
    public Login setLoginInformation(String phoneCode, String username, String password) {
        // re-init login information
        if (loginInfo.getPassword() != null) loginInfo = new LoginInformation();
        // set email/phone number
        if (username.matches("\\d+")) loginInfo.setPhoneNumber(username);
        else loginInfo.setEmail(username);

        // set password
        loginInfo.setPassword(password);

        // set phoneCode
        loginInfo.setPhoneCode(phoneCode);
        return this;
    }

    public LoginDashboardInfo getInfo(LoginInformation... loginInformation) {
        LoginInformation logInfo = loginInformation.length > 0 ? loginInformation[0] : loginInfo;

        // init login dashboard info model
        LoginDashboardInfo info = new LoginDashboardInfo();

        // get login response
        Response res;

        if (logInfo.getEmail() != null) {
            res = getLoginResponse(logInfo.getEmail(), logInfo.getPassword()); //if account is email
        } else res = getLoginWithPhoneResponse(logInfo.getPhoneCode(), logInfo.getPhoneNumber(), logInfo.getPassword());

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

    public Response getLoginWithPhoneResponse(String phoneCode, String phoneNumber, String password) {
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
                }""".formatted(phoneCode, phoneNumber, password);
        Response loginResponse = api.login(DASHBOARD_LOGIN_PHONE_PATH, body);
        loginResponse.then().statusCode(200);
        return loginResponse;
    }

    public LoginInformation getLoginInformation() {
        return loginInfo;
    }
}
