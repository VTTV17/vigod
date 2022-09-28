import java.sql.SQLException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.Mailnesia;
import pages.storefront.HeaderSF;
import pages.storefront.LoginPage;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;

public class LoginStorefront extends BaseTest {

	LoginPage loginPage;
	HeaderSF headerPage;

	String MAIL;
	String PASSWORD;
	String COUNTRY;
	String PHONE;
	String PHONE_PASSWORD;
	String PHONE_COUNTRY;
	String PHONE_COUNTRYCODE;
	String BLANK_USERNAME_ERROR;
	String BLANK_PASSWORD_ERROR;
	String INVALID_USERNAME_ERROR;
	String INVALID_CREDENTIALS_ERROR;

	public String getVerificationCode(String username) throws InterruptedException, SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			Thread.sleep(10000);
			commonAction.openNewTab();
			commonAction.switchToWindow(1);
			verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode();
			commonAction.closeTab();
			commonAction.switchToWindow(0);
		} else {
			verificationCode = new InitConnection().getResetKey(loginPage.countryCode + ":" + username);
		}
		return verificationCode;
	}

	@BeforeClass
	public void readData() {
		JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");

		MAIL = data.findValue("buyer").findValue("mail").findValue("username").asText();
		PASSWORD = data.findValue("buyer").findValue("mail").findValue("password").asText();
		COUNTRY = data.findValue("buyer").findValue("mail").findValue("country").asText();
		PHONE = data.findValue("buyer").findValue("phone").findValue("username").asText();
		PHONE_PASSWORD = data.findValue("buyer").findValue("phone").findValue("password").asText();
		PHONE_COUNTRY = data.findValue("buyer").findValue("phone").findValue("country").asText();
		PHONE_COUNTRYCODE = data.findValue("buyer").findValue("phone").findValue("countryCode").asText();
		BLANK_USERNAME_ERROR = data.findValue("emptyUsernameError").asText();
		BLANK_PASSWORD_ERROR = data.findValue("emptyPasswordError").asText();
		INVALID_USERNAME_ERROR = data.findValue("invalidUsernameFormat").asText();
		INVALID_CREDENTIALS_ERROR = data.findValue("invalidCredentials").asText();
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		loginPage = new LoginPage(driver);
		headerPage = new HeaderSF(driver);
	}

	@Test
	public void TC01_SF_LoginWithAllFieldsLeftBlank() {
		loginPage.navigate().performLogin("", generate.generateNumber(9))
				.verifyEmailOrPhoneNumberError(BLANK_USERNAME_ERROR).completeVerify();
		// Password field is left empty.
		loginPage.navigate().performLogin(generate.generateNumber(9), "")
				.verifyPasswordError(BLANK_PASSWORD_ERROR).completeVerify();
		// All fields are left empty.
		loginPage.navigate().performLogin("", "").verifyEmailOrPhoneNumberError(BLANK_USERNAME_ERROR)
				.verifyPasswordError(BLANK_PASSWORD_ERROR).completeVerify();
	}

	@Test
	public void TC02_SF_LoginWithInvalidPhoneFormat() {
		// Log in with a phone number consisting of 7 digits.
		loginPage.navigate().performLogin(generate.generateNumber(7), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_USERNAME_ERROR).completeVerify();
		// Log in with a phone number consisting of 16 digits.
		loginPage.navigate().performLogin(generate.generateNumber(16), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_USERNAME_ERROR).completeVerify();
	}

	@Test
	public void TC03_SF_LoginWithInvalidMailFormat() {
		loginPage.navigate().performLogin(generate.generateString(10), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_USERNAME_ERROR).completeVerify();
	}

	@Test
	public void BH_1334_LoginWithNonExistingAccount() {
		loginPage.navigate()
				.performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
		loginPage.navigate().performLogin(generate.generateNumber(13), generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
	}

	@Test
	public void BH_1282_LoginWithCorrectAccount() throws InterruptedException {
		loginPage.navigate().performLogin(PHONE_COUNTRY, PHONE, PHONE_PASSWORD).waitTillLoaderDisappear();
		headerPage.clickUserInfoIcon().clickLogout();

		loginPage.navigate().performLogin(COUNTRY, MAIL, PASSWORD).waitTillLoaderDisappear();
		headerPage.clickUserInfoIcon().clickLogout();
	}

	@Test
	public void BH_1285_ForgotMailPassword() throws InterruptedException, SQLException {
		String newPassword = PASSWORD + "@" + generate.generateNumber(3);

		String mail = "buyertest12@mailnesia.com";

		loginPage.navigate();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword().inputUsernameForgot(mail).clickContinueBtn().inputPasswordForgot(newPassword);
		loginPage.inputVerificationCode(getVerificationCode(mail)).clickConfirmBtn().waitTillLoaderDisappear();

		headerPage.clickUserInfoIcon().clickLogout();

		// Re-login with new password
		loginPage.navigate().performLogin(mail, newPassword).waitTillLoaderDisappear();
		headerPage.clickUserInfoIcon().clickLogout();
	}

	@Test
	public void BH_1286_ForgotPhonePassword() throws InterruptedException, SQLException {
		String newPassword = PHONE_PASSWORD + "@" + generate.generateNumber(3);

		String phone = "9023456084";
		String phoneCountry = "+84";

		loginPage.navigate();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword().selectCountryForgot(phoneCountry).inputUsernameForgot(phone).clickContinueBtn()
				.inputPasswordForgot(newPassword);
		loginPage.inputVerificationCode(getVerificationCode(phone)).clickConfirmBtn().waitTillLoaderDisappear();

		headerPage.clickUserInfoIcon().clickLogout();

		// Re-login with new password
		loginPage.navigate().performLogin(phoneCountry, phone, newPassword).waitTillLoaderDisappear();
		headerPage.clickUserInfoIcon().clickLogout();
	}

}
