package api.storefront.login;

import api.dashboard.login.Login;
import api.dashboard.setting.StoreInformation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.model.dashboard.storefront.loginSF;
import utilities.model.sellerApp.login.LoginInformation;

import static io.restassured.RestAssured.given;
import static utilities.links.Links.SF_DOMAIN;
public class LoginSF {
    private static String username;
    private static String password;
    private static String phoneCode;
    LoginInformation loginInformation;
    public LoginSF(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    }
    public Response LoginToSF(String username, String password, String phoneCode) {
        String body = """
                {
                    "username": "%s",
                    "password": "%s",
                    "phoneCode": "%s"
                }""".formatted(username, password, phoneCode);
        Response loginSF = given().contentType(ContentType.JSON)
                .cookie("StoreId=%s".formatted(new Login().getInfo(loginInformation).getStoreID()))
                .when()
                .body(body)
                .post("https://%s%s/api/login".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN));
        loginSF.then().statusCode(200);
        LoginSF.username = username;
        LoginSF.password = password;
        LoginSF.phoneCode = phoneCode;
        return loginSF;
    }
    public loginSF getInfo(){
        Response responseLogin = LoginToSF(username,password,phoneCode);
        loginSF loginSFInfo = new loginSF();
        loginSFInfo.setAccessToken(responseLogin.jsonPath().getString("id_token"));
        return loginSFInfo;
    }

}
