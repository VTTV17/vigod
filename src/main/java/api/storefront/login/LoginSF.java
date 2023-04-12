package api.storefront.login;

import api.dashboard.login.Login;
import api.dashboard.setting.StoreInformation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static utilities.links.Links.SF_DOMAIN;

public class LoginSF {

    public void LoginToSF(String username, String password, String phoneCode) {
        String body = """
                {
                    "username": "%s",
                    "password": "%s",
                    "phoneCode": "%s"
                }""".formatted(username, password, phoneCode);
        Response loginSF = given().contentType(ContentType.JSON)
                .cookie("StoreId=%s".formatted(new Login().getInfo().getStoreID()))
                .when()
                .body(body)
                .post("https://%s%s/api/login".formatted(new StoreInformation().getInfo().getStoreURL(), SF_DOMAIN));
        loginSF.then().statusCode(200);
    }
}
