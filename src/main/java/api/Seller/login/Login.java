package api.Seller.login;

import api.Buyer.login.ResetPasswordPayloadBuilder;
import api.Seller.setting.BranchManagement;
import com.google.common.collect.Iterables;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.api.API;
import utilities.api.payloadbuilder.CapchaPayloadBuilder;
import utilities.api.payloadbuilder.JsonObjectBuilder;
import utilities.data.DataGenerator;
import utilities.model.LoginCredentials;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Login {
    private static final Logger log = LoggerFactory.getLogger(Login.class);
    
    String loginWithEmailPath = "/api/authenticate/store/email/gosell";
    String loginWithPhonePath = "api/authenticate/store/phone/gosell";
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

        try {
            return api.login(loginWithEmailPath, body).then().statusCode(200).extract().response();
        } catch (AssertionError ex) {
            LogManager.getLogger().debug(ex);
            return api.login(loginWithEmailPath, body).then().statusCode(200).extract().response();
        }
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
        // set username, password and phoneCode if any
        if (username.matches("\\d+")) {
            loginInfo = new LoginInformation(phoneCode, username, password);
        } else {
            loginInfo = new LoginInformation(username, password);
        }
        return this;
    }

    public Login setLoginInformation(LoginCredentials account) {
        return setLoginInformation(DataGenerator.getPhoneCode(account.getCountry()), account.getUsername(), account.getPassword());
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
        if (Optional.ofNullable(info.getStaffPermissionToken()).isEmpty()) info.setStaffPermissionToken("");
        API.setStaffPermissionToken(info.getStaffPermissionToken());

        // get branch info
        BranchInfo branchInfo = new BranchManagement(loginInformation, info).getInfo();

        // get assigned branch ids
        info.setAssignedBranchesIds(branchInfo.getBranchID());

        // get assigned branch names
        info.setAssignedBranchesNames(branchInfo.getBranchName());

        //get role
        info.setUserRole(jPath.getList("authorities"));

        //get currency symbol
        info.setSymbol(res.jsonPath().getString("store.symbol"));

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

        // get userName
        info.setUserName(jPath.getString("displayName"));

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
        Response loginResponse = api.login(loginWithPhonePath, body);
        loginResponse.then().log().ifValidationFails().statusCode(200);
        return loginResponse;
    }

    public LoginInformation getLoginInformation() {
        return loginInfo;
    }

    public Response resetPhonePassword(String phoneNumber, String phoneCode) {
        JSONObject capchaPayload = new CapchaPayloadBuilder().givenCaptchaResponse("").givenGReCaptchaResponse("").givenImageBase64("").build();
        JSONObject resetPayload = new ResetPasswordPayloadBuilder().givenCountryCode(phoneCode).givenPhoneNumber(phoneNumber).build();
        String body = JsonObjectBuilder.mergeJSONObjects(capchaPayload, resetPayload).toString();

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "api.beecow.info");
        headerMap.put("x-request-origin", "DASHBOARD");
        headerMap.put("isinternational", "false");
        headerMap.put("platform", "WEB");

        Response resetResponse = new API().post("/api/account/reset_password/mobile/gosell", "noTokenNeeded", body, headerMap);
        resetResponse.then().log().ifValidationFails().statusCode(200);

        return resetResponse;
    }

}
