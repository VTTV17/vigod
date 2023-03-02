package api.storefront.signup;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;

import java.sql.SQLException;
import java.time.Instant;

import static api.dashboard.setting.StoreInformation.apiStoreURL;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.apache.commons.lang.RandomStringUtils.random;
import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.URI;

public class SignUp {
    String SIGN_UP_PHONE_PATH = "/api/register2/mobile/phone";
    String GUEST_TOKEN_PATH = "/beecowgateway/api/guest";

    String ACTIVE_PATH = "/api/activate";

    public static String apiGuestToken;
    public static String apiPhoneNumber;
    public static String apiPhoneCode;
    public static String apiPassword;
    public static String apiCustomerName;

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
        apiGuestToken = guestResponse.jsonPath().getString("accessToken");
    }

    public void signUpByPhoneNumber(String... phone) throws SQLException {
        getGuestToken();
        apiCustomerName = "Auto - customer - " + new DataGenerator().generateDateTime("dd/MM hh:mm:ss");
        apiPhoneNumber = (phone.length > 0) ? phone[0] : String.valueOf(Instant.now().toEpochMilli());
        apiPhoneCode = (phone.length > 1) ? phone[1] : "+84";
        apiPassword = "Abc@12345";
        String signupBody = """
                {
                    "displayName": "%s",
                    "password": "%s",
                    "mobile": {
                        "countryCode": "%s",
                        "phoneNumber": "%s"
                    }
                }""".formatted(apiCustomerName, apiPassword, apiPhoneCode, apiPhoneNumber);
        Response signUpResponse = new API().post(SIGN_UP_PHONE_PATH, apiGuestToken, signupBody);

        signUpResponse.then().statusCode(200);

        String loginText = signUpResponse.jsonPath().getString("login");
        int userID = signUpResponse.jsonPath().getInt("id");

        String activeCode = new InitConnection().getActivationKey(loginText);

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().login("https://%s%s%s".formatted(apiStoreURL, SF_DOMAIN, ACTIVE_PATH), activeBody).then().statusCode(200);
    }
}
