package api.Buyer.login;

import api.CapchaPayloadBuilder;
import api.JsonObjectBuilder;
import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.storefront.loginSF;
import utilities.model.sellerApp.login.LoginInformation;

import static io.restassured.RestAssured.given;
import static utilities.links.Links.SF_DOMAIN;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
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
        Response loginSF = given().relaxedHTTPSValidation().contentType(ContentType.JSON)
                .cookie("StoreId=%s".formatted(new Login().getInfo(loginInformation).getStoreID()))
                .when()
                .body(body)
                .post("https://%s%s/api/login".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN));
        loginSF.then().log().ifValidationFails().statusCode(200);
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

    public Response resetPhonePassword(String phoneNumber, String phoneCode) {
    	
    	JSONObject capchaPayload = new CapchaPayloadBuilder().givenCaptchaResponse("").givenGReCaptchaResponse("").givenImageBase64("").build();
    	JSONObject resetPayload = new ResetPasswordPayloadBuilder().givenCountryCode(phoneCode).givenPhoneNumber(phoneNumber).build();
    	String body = JsonObjectBuilder.mergeJSONObjects(capchaPayload, resetPayload).toString();
    	
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("x-request-origin", "STOREFRONT");
        headerMap.put("platform", "WEB");
        headerMap.put("storeid", String.valueOf(new Login().getInfo(loginInformation).getStoreID()));
        
        Response resetResponse = new API().post("/api/account/reset_password/mobile/gosell", "noTokenNeeded", body, headerMap);
        resetResponse.then().log().ifValidationFails().statusCode(200);

        return resetResponse;
    }    
    
    public Response deletePhoneAccount(String username, String password, String phoneCode) {
    	
    	LoginToSF(username, password, phoneCode);
    	
    	String token = getInfo().getAccessToken();
    	
    	Map<String, String> headerMap = new HashMap<>();
    	headerMap.put("authorization", "Bearer " + token);
    	
    	Response resetResponse = new API().delete("/api/account/delete", token, headerMap);
    	resetResponse.then().log().ifValidationFails().statusCode(200);
    	
    	return resetResponse;
    }    
    
}
