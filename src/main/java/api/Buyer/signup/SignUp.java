package api.Buyer.signup;

import api.CapchaPayloadBuilder;
import api.JsonObjectBuilder;
import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import api.kibana.KibanaAPI;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import static io.restassured.RestAssured.given;
import static utilities.links.Links.SF_DOMAIN;

public class SignUp {
    String SIGN_UP_PHONE_PATH = "/api/register2/mobile/phone";
    String SIGN_UP_MAIL_PATH = "/api/register2/mobile";
    String GUEST_TOKEN_PATH = "/beecowgateway/api/guest";

    String ACTIVE_PATH = "/api/activate";

    private String guestToken;
    private String phoneCode;
    private String password;
    private String customerName;
    LoginInformation loginInformation;
    public SignUp (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    }

    public void getGuestToken() {
        String body = """
                {
                    "langKey": "vi",
                    "locationCode": "vn"
                }""";
        Response guestResponse = given().contentType(ContentType.JSON)
                .header("Authorization", "Basic aW50ZXJuYWw6TUtQZDVkUG1MZXg3b2hXcmxHeEpQR3htZ2ZTSFF0MXU=")
                .when()
                .body(body)
                .post(GUEST_TOKEN_PATH);
        guestResponse.then().statusCode(201);
        guestToken = guestResponse.jsonPath().getString("accessToken");
    }

    public void signUpByPhoneNumber(String password, String... phone) {
        getGuestToken();
        customerName = "Auto - customer - " + new DataGenerator().generateDateTime("dd/MM hh:mm:ss");
        String apiPhoneNumber = (phone.length > 0) ? phone[0] : String.valueOf(Instant.now().toEpochMilli());
        phoneCode = (phone.length > 1) ? phone[1] : "+84";
        String signupBody = """
                {
                    "displayName": "%s",
                    "password": "%s",
                    "mobile": {
                        "countryCode": "%s",
                        "phoneNumber": "%s"
                    }
                }""".formatted(customerName, password, phoneCode, apiPhoneNumber);
        Response signUpResponse = new API().post(SIGN_UP_PHONE_PATH, guestToken, signupBody);

        signUpResponse.then().statusCode(200);

        String loginText = signUpResponse.jsonPath().getString("login");
        int userID = signUpResponse.jsonPath().getInt("id");

        String activeCode = null;
        try {
            activeCode = new InitConnection().getActivationKey(loginText);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().login("https://%s%s%s".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN, ACTIVE_PATH), activeBody).then().statusCode(200);
    }

    public void signUpByMail(String apiMail, String password) {
        getGuestToken();
        customerName = "Auto - customer - " + new DataGenerator().generateDateTime("dd/MM hh:mm:ss");
        String signupBody = """
                {
                     "displayName": "%s",
                     "password": "%s",
                     "locationCode": "VN",
                     "email": "%s"
                 }""".formatted(customerName, password, apiMail);
        Response signUpResponse = new API().post(SIGN_UP_MAIL_PATH, guestToken, signupBody);

        signUpResponse.then().statusCode(200);

        int userID = signUpResponse.jsonPath().getInt("id");

        String activeCode = "";
        try {
            activeCode = new InitConnection().getActivationKey(apiMail);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().login("https://%s%s%s".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN, ACTIVE_PATH), activeBody).then().statusCode(200);
    }

    public void signUpByPhoneNumber(String phoneCode, String phone, String locationCode, String langKey, String displayName, String password) {
        getGuestToken();
        
        LoginDashboardInfo dashboardModel = new Login().getInfo(loginInformation);
        String storeName = dashboardModel.getStoreName();
        String storeId = String.valueOf(dashboardModel.getStoreID());
        String storeURL = new StoreInformation(loginInformation).getInfo().getStoreURL();
        
    	JSONObject capchaPayload = new CapchaPayloadBuilder().givenCaptchaResponse("").givenGReCaptchaResponse("").givenImageBase64("").build();
    	JSONObject registerPayload = new RegisterPayloadBuilder().givenDisplayName(displayName).givenPassword(password).givenLocationCode(locationCode)
    			.givenLangKey(langKey).givenDomain("gosell").givenGoSellShopUrl("https://%s%s".formatted(storeURL, SF_DOMAIN)).givenGoSellShopName(storeName)
    			.givenMobile(phoneCode, phone).build();
    	String body = JsonObjectBuilder.mergeJSONObjects(capchaPayload, registerPayload).toString();
        
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "api.beecow.info");
        headerMap.put("x-request-origin", "STOREFRONT");
        headerMap.put("platform", "WEB");
        headerMap.put("storeid", storeId);
        
        Response signUpResponse = new API().post(SIGN_UP_PHONE_PATH, guestToken, body, headerMap);
        signUpResponse.then().log().ifValidationFails().statusCode(200);

        String loginText = signUpResponse.jsonPath().getString("login");
        int userID = signUpResponse.jsonPath().getInt("id");

        String activeCode = new KibanaAPI().getKeyFromKibana(loginText, "activationKey");

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().post("https://%s%s%s".formatted(storeURL, SF_DOMAIN, ACTIVE_PATH), "noTokenNeeded", activeBody).then().log().ifValidationFails().statusCode(200);
    }    

    
}
