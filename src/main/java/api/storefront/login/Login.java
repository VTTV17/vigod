package api.storefront.login;

import api.storefront.signup.SignUp;
import utilities.api.API;

import static api.dashboard.login.Login.storeName;

public class Login {
    public static String sfToken;

    public void LoginByPhoneNumber(String... loginInfo) {
        String username = loginInfo.length > 0 ? loginInfo[0] : SignUp.phoneNumber;
        String password = loginInfo.length > 1 ? loginInfo[1] : SignUp.password;
        String phoneCode = loginInfo.length > 2 ? loginInfo[2] : SignUp.phoneCode;
        String body = """
                {
                    "username": "%s",
                    "password": "%s",
                    "phoneCode": "%s"
                }""".formatted(username, password, phoneCode);
        sfToken = new API().login("https://%s.unisell.vn/api/login".formatted(storeName), body).jsonPath().getString("id_token");
    }
}
