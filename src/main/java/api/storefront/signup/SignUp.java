package api.storefront.signup;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;

import java.sql.SQLException;

import static api.dashboard.setting.StoreInformation.storeURL;
import static io.restassured.RestAssured.given;
import static org.apache.commons.lang.RandomStringUtils.random;
import static utilities.links.Links.SF_DOMAIN;

public class SignUp {
    String SIGN_UP_PHONE_PATH = "/api/register2/mobile/phone";
    String GUEST_TOKEN_PATH = "/beecowgateway/api/guest";

    String ACTIVE_PATH = "/api/activate";

    public static String guestToken;
    public static String phoneNumber;
    public static String phoneCode;
    public static String password;
    public static String customerName;

    public void getGuestToken() {
        String body = """
                {
                    "langKey": "vi",
                    "locationCode": "vn"
                }""";
        guestToken = given().contentType(ContentType.JSON)
                .header("Authorization", "Basic aW50ZXJuYWw6TUtQZDVkUG1MZXg3b2hXcmxHeEpQR3htZ2ZTSFF0MXU=")
                .when()
                .body(body)
                .post(GUEST_TOKEN_PATH).jsonPath().getString("accessToken");
    }

    public void signUpByPhoneNumber(String... phone) throws SQLException {
        getGuestToken();
        customerName = "Auto - customer - " + new DataGenerator().generateDateTime("dd/MM hh:mm:ss");
        phoneNumber = (phone.length > 0) ? phone[0] : random(10, false, true).replace("00","0");
        phoneCode = (phone.length > 1) ? phone[1] : "+84";
        password = "Abc@12345";
        String signupBody = """
                {
                    "displayName": "%s",
                    "password": "%s",
                    "mobile": {
                        "countryCode": "%s",
                        "phoneNumber": "%s"
                    }
                }""".formatted(customerName, password, phoneCode, phoneNumber);
        Response signUpResponse = new API().post(SIGN_UP_PHONE_PATH, guestToken, signupBody);

        String loginText = signUpResponse.jsonPath().getString("login");
        int userID = signUpResponse.jsonPath().getInt("id");

        String activeCode = new InitConnection().getActivationKey(loginText);

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().login("https://%s%s%s".formatted(storeURL, SF_DOMAIN, ACTIVE_PATH), activeBody);
    }
}
