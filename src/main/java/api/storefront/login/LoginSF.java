package api.storefront.login;

import api.storefront.signup.SignUp;
import io.restassured.http.ContentType;

import static api.dashboard.login.Login.storeID;
import static api.dashboard.login.Login.storeName;
import static io.restassured.RestAssured.given;

public class LoginSF {
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
        sfToken = given().contentType(ContentType.JSON)
                .cookie("StoreId=%s".formatted(storeID))
                .when()
                .body(body)
                .post("https://%s.unisell.vn/api/login".formatted(storeName)).jsonPath().getString("id_token");

    }
}
