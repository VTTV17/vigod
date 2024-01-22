package api.Seller.login;

import com.google.common.collect.Iterables;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static utilities.links.Links.URI;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    String switchStaffPath = "/api/authenticate/store/%s/switch-staff";
    String storeStaff = "/storeservice/api/store-staffs/user/%s";
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

    public LoginDashboardInfo getInfo(LoginInformation loginInformation) {
        // init login dashboard info model
        LoginDashboardInfo info = new LoginDashboardInfo();

        // get login response
        Response res;

        if (loginInformation.getEmail() != null)
            res = getLoginResponse(loginInformation.getEmail(), loginInformation.getPassword()); //if account is email
        else
            res = getLoginWithPhoneResponse(loginInformation.getPhoneCode(), loginInformation.getPhoneNumber(), loginInformation.getPassword());

        // get jsonPath
        JsonPath jPath = res.jsonPath();

        // set accessToken
        info.setAccessToken(jPath.getString("accessToken"));

        // set refreshToken
        info.setRefreshToken(jPath.getString("refreshToken"));

        // set sellerID
        info.setSellerID(jPath.getInt("id"));

        try {
            // set storeID
            info.setStoreID(jPath.getInt("store.id"));

            // set storeName
            info.setStoreName(jPath.getString("store.name"));
        } catch (NullPointerException ignore) {
        }

        // if login by staff => login and get staff information
        if (!jPath.getList("authorities").contains("ROLE_STORE")) info = new Login().getStaffInfo(info);

        // return login dashboard info
        return info;
    }

    private LoginDashboardInfo getStaffInfo(LoginDashboardInfo info) {
        // get staff store list
        List<Integer> getListStoreId = api.get(storeStaff.formatted(info.getSellerID()), info.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getList("id");

        // get jPath
        JsonPath jPath = api.post(switchStaffPath.formatted(Iterables.getLast(getListStoreId)), info.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();

        // init modal
        info = new LoginDashboardInfo();

        // set accessToken
        info.setAccessToken(jPath.getString("accessToken"));

        // set refreshToken
        info.setRefreshToken(jPath.getString("refreshToken"));

        // set sellerID
        info.setSellerID(jPath.getInt("id"));

        // set storeID
        info.setStoreID(jPath.getInt("store.id"));

        // set storeName
        info.setStoreName(jPath.getString("store.name"));

        // set staff token
        info.setStaffToken(jPath.getString("staffPermissionsToken"));

        // set staff branches
        info.setAssignedBranches(jPath.getList("branchIds"));

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
