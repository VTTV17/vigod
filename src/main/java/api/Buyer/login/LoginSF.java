package api.Buyer.login;

import static io.restassured.RestAssured.given;
import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.SF_DOMAIN_BIZ;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.mifmif.common.regex.Generex;

import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.api.payloadbuilder.CapchaPayloadBuilder;
import utilities.api.payloadbuilder.JsonObjectBuilder;
import utilities.model.dashboard.storefront.loginSF;
import utilities.model.sellerApp.login.LoginInformation;
import web.StoreFront.login.LoginPage;
public class LoginSF {
	
	final static Logger logger = LogManager.getLogger(LoginPage.class);
	
    private static String username;
    private static String password;
    private static String phoneCode;
    LoginInformation loginInformation;
    public LoginSF(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    }
    public Response LoginToSF(String username, String password, String phoneCode) {
    	
    	var storeInfo = new StoreInformation(loginInformation).getInfo();
    	
    	var sfDomain = storeInfo.getDashboardDomain().contentEquals("GOSELL_BIZ") ? SF_DOMAIN_BIZ : SF_DOMAIN;
    	
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
                .post("https://%s%s/api/login".formatted(storeInfo.getStoreURL(), sfDomain));
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
        
        //Bypass IP-based rate limits by randomly generated IPs each run
        String randomIP = new Generex("\\&testIp=[1-9][0-9]{0,2}\\.[1-9][0-9]{0,2}\\.[1-9][0-9]{0,2}\\.[1-9][0-9]{0,2}").random();
        
        Response resetResponse = new API().post("/api/account/reset_password/mobile/gosell?isResend=true%s".formatted(randomIP), "noTokenNeeded", body, headerMap);
        resetResponse.then().log().ifValidationFails().statusCode(200);

        return resetResponse;
    }    
  
    
    /**
     * Deletes buyer account using his id_token
     * @param idToken id_token of a buyer, obtained after the buyer is logged in
     * @return Response object
     */
    public Response deleteAccountByTokenId(String idToken) {
    	
    	Map<String, String> headerMap = new HashMap<>();
    	headerMap.put("authorization", "Bearer " + idToken);
    	
    	Response deleteResponse = new API().delete("/api/account/delete", idToken, headerMap);
    	deleteResponse.then().log().ifValidationFails().statusCode(200);
    	
    	return deleteResponse;
    } 
    public Response deleteAccount(String username, String password, String phoneCode) {
    	
    	LoginToSF(username, password, phoneCode);
    	
    	String token = getInfo().getAccessToken();
    	
    	return deleteAccountByTokenId(token);
    }    
    
    /**
     * That one API with the path /api/account
     * @param username
     * @param password
     * @param phoneCode
     * @return
     */
    public Response getAccountInfo(String username, String password, String phoneCode) {
    	
    	LoginToSF(username, password, phoneCode);
    	
    	String token = getInfo().getAccessToken();
    	
    	Response response = new API().get("/api/account", token);
    	response.then().log().ifValidationFails().statusCode(200);
    	
    	return response;
    } 
    
    /**
     * Changes password on Storefront
     * @param username
     * @param password
     * @param phoneCode
     * @param newPassword
     * @return
     */
    public Response changePassword(String username, String password, String phoneCode, String newPassword) {
    	
    	var storeInfo = new StoreInformation(loginInformation).getInfo();
    	
    	var sfDomain = storeInfo.getDashboardDomain().contentEquals("GOSELL_BIZ") ? SF_DOMAIN_BIZ : SF_DOMAIN;
    	
    	
    	//Get Storefront URL
    	var sfURL = storeInfo.getStoreURL() + sfDomain;
    	
    	//Prepare path
    	var changePasswordPath = "https://%s/api/change_password".formatted(sfURL);
    	
    	//Get token_id
    	LoginToSF(username, password, phoneCode);
    	String token = getInfo().getAccessToken();
    	
    	//Prepare headers
    	Map<String, String> headerMap = new HashMap<>();
    	headerMap.put("Authorization", "Bearer " + token);
    	
    	//Prepare payload
		JsonObject payload = new JsonObject();
		payload.addProperty("deviceToken", "");
		payload.addProperty("currentPassword", password);
		payload.addProperty("newPassword", newPassword);
    	
		//Go baby go
    	Response response = new API(sfURL).put(changePasswordPath, token, payload.toString(), headerMap);
    	response.then().log().ifValidationFails().statusCode(200);
    	logger.info("Password is successfully changed to: {}", newPassword);
    	
    	return response;
    }    
    
}
