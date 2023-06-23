package api.storefront.signup;

import api.dashboard.setting.StoreInformation;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;

import java.sql.SQLException;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static java.lang.Thread.sleep;
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

    public void signUpByPhoneNumber(String... phone) throws SQLException {
        getGuestToken();
        customerName = "Auto - customer - " + new DataGenerator().generateDateTime("dd/MM hh:mm:ss");
        String apiPhoneNumber = (phone.length > 0) ? phone[0] : String.valueOf(Instant.now().toEpochMilli());
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
                }""".formatted(customerName, password, phoneCode, apiPhoneNumber);
        Response signUpResponse = new API().post(SIGN_UP_PHONE_PATH, guestToken, signupBody);

        signUpResponse.then().statusCode(200);

        String loginText = signUpResponse.jsonPath().getString("login");
        int userID = signUpResponse.jsonPath().getInt("id");

        String activeCode = new InitConnection().getActivationKey(loginText);

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().login("https://%s%s%s".formatted(new StoreInformation().getInfo().getStoreURL(), SF_DOMAIN, ACTIVE_PATH), activeBody).then().statusCode(200);
    }

    public void signUpByMail() throws InterruptedException {
        getGuestToken();
        customerName = "Auto - customer - " + new DataGenerator().generateDateTime("dd/MM hh:mm:ss");
        String apiMail = "%s@qa.team".formatted(Instant.now().toEpochMilli());
        password = "Abc@12345";
        System.out.println(apiMail);
        phoneCode = "+84";
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

        // access to qa.team and get active code
        WebDriver driver = new InitWebdriver().getDriver("chrome", "true");
        sleep(5000);
        driver.get("https://qa.team/inbox?code=%s".formatted(apiMail.split("@")[0]));
        sleep(5000);
        String activeCode;
        try {
            activeCode = driver.findElement(By.cssSelector("#messages > a:nth-child(1) > .subject")).getText().split(" ")[0];
        } catch (NoSuchElementException ex) {
            driver.get("https://qa.team/inbox?code=%s".formatted(apiMail.split("@")[0]));
            sleep(5000);
            activeCode = driver.findElement(By.cssSelector("#messages > a:nth-child(1) > .subject")).getText().split(" ")[0];
        }
        driver.quit();

        String activeBody = """
                {
                    "code": "%s",
                    "userId": %s
                }""".formatted(activeCode, userID);

        new API().login("https://%s%s%s".formatted(new StoreInformation().getInfo().getStoreURL(), SF_DOMAIN, ACTIVE_PATH), activeBody).then().statusCode(200);
    }
}
