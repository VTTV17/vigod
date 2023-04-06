package api.storefront.login;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.setting.StoreInformation.apiStoreURL;
import static io.restassured.RestAssured.given;
import static utilities.links.Links.SF_DOMAIN;

public class LoginSF {
    public static String sfToken;

    public void LoginToSF(String username, String password, String phoneCode) {
        String body = """
                {
                    "username": "%s",
                    "password": "%s",
                    "phoneCode": "%s"
                }""".formatted(username, password, phoneCode);
        Response loginSF = given().contentType(ContentType.JSON)
                .cookie("StoreId=%s".formatted(apiStoreID))
                .when()
                .body(body)
                .post("https://%s%s/api/login".formatted(apiStoreURL, SF_DOMAIN));
        loginSF.then().statusCode(200);
        sfToken = loginSF.jsonPath().getString("id_token");
    }
}
