package api.Seller.login;

import api.Seller.setting.BranchManagement;
import com.google.common.collect.Iterables;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class Login {
    String API_LOGIN_PATH = "/api/authenticate/store/email/gosell";
    public String DASHBOARD_LOGIN_PHONE_PATH = "api/authenticate/store/phone/gosell";
    String switchStaffPath = "/api/authenticate/store/%s/switch-staff";
    String storeStaff = "/storeservice/api/store-staffs/user/%s";
    API api = new API();
    private static LoginInformation loginInfo = new LoginInformation();

    Response getLoginResponse(String account, String password) {
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

        // set userId
        info.setUserId(jPath.getInt("id"));

        // set ownerId
        info.setOwnerId(jPath.getInt("id"));

        try {
            // set storeID
            info.setStoreID(jPath.getInt("store.id"));

            // set storeName
            info.setStoreName(jPath.getString("store.name"));
        } catch (NullPointerException ignore) {
        }

        // if login by staff => login and get staff information
        if (!jPath.getList("authorities").contains("ROLE_STORE")) info = getStaffInfo(info);

        // set staffToken
        API.setStaffPermissionToken(info.getStaffPermissionToken() != null ? info.getStaffPermissionToken() : "");

        // get branch info
        BranchInfo branchInfo = new BranchManagement(loginInformation, info).getInfo();

        // get assigned branch ids
        info.setAssignedBranchesIds(branchInfo.getBranchID());

        // get assigned branch names
        info.setAssignedBranchesNames(branchInfo.getBranchName());

        // return login dashboard info
        return info;
    }

    private LoginDashboardInfo getStaffInfo(LoginDashboardInfo info) {
        // get staff store list
        List<Integer> getListStoreId = api.get(storeStaff.formatted(info.getUserId()), info.getAccessToken())
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
        info.setUserId(jPath.getInt("id"));

        // set storeID
        info.setStoreID(jPath.getInt("store.id"));

        // set storeName
        info.setStoreName(jPath.getString("store.name"));

        // set staff token
        info.setStaffPermissionToken(jPath.getString("staffPermissionsToken"));

        // set staff branches
        info.setAssignedBranchesIds(jPath.getList("branchIds"));

        // get ownerId
        int ownerId = api.get("/storeservice/api/stores/%s".formatted(Iterables.getLast(getListStoreId)), info.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getInt("ownerId");
        info.setOwnerId(ownerId);

        // return login dashboard info
        return info;
    }

    public Response getLoginWithPhoneResponse(String phoneCode, String phoneNumber, String password) {
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
