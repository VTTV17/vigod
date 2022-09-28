import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.storefront.HeaderSF;
import pages.storefront.LoginPage;
import pages.storefront.SignupPage;
import pages.storefront.userprofile.MyAccount;
import pages.storefront.userprofile.MyAddress;
import pages.storefront.userprofile.UserProfileInfo;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;
import pages.Mailnesia;
import pages.dashboard.customers.AllCustomers;
import pages.dashboard.customers.CustomerDetails;
import pages.dashboard.home.HomePage;

import java.sql.SQLException;

public class SignupStorefront extends BaseTest {

	SignupPage signupPage;

	String randomNumber;
	String username;
	String mail;
	String phone;
	String password;
	String country;
	String countryCode;
	String displayName;
	String birthday;
	String language;

	String BUYER_MAIL_USERNAME;
	String BUYER_MAIL_PASSWORD;
	String BUYER_MAIL_COUNTRY;

	String BUYER_PHONE_USERNAME;
	String BUYER_PHONE_PASSWORD;
	String BUYER_PHONE_COUNTRY;

	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;

	String MAIL_EXIST_ERROR_VI = "Email đã tồn tại";;
	String PHONE_EXIST_ERROR_VI = "Số điện thoại đã tồn tại";
	String EMPTY_USERNAME_ERROR_VI = "Hãy nhập số điện thoại hoặc email";
	String EMPTY_PASSWORD_ERROR_VI = "Hãy nhập mật khẩu";
	String INVALID_FORMAT_ERROR_VI = "Số điện thoại hoặc email không đúng";
//	String PHONE_EXIST_ERROR_VI = "Số điện thoại đã tồn tại";
	String INVALID_CODE_ERROR_VI = "Mã xác thực không đúng";
	String INVALID_CODE_ERROR_EN = "Incorrect confirmation code!";

	public void generateTestData() throws InterruptedException {
		randomNumber = generate.generateNumber(3);
		mail = "automation0-buyer" + randomNumber + "@mailnesia.com";
		phone = "9123456" + randomNumber;
		password = "fortesting!1";
		country = "rd";
		displayName = "Automation Buyer " + randomNumber;
		birthday = "02/02/1990";
		language = "rd";
	}

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
			verificationCode = new InitConnection().getActivationKey(signupPage.countryCode + ":" + username);
		}
		return verificationCode;
	}

	@BeforeClass
	public void readData() {
		JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
		JsonNode data1 = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
		STORE_USERNAME = data.findValue("seller").findValue("mail").findValue("username").asText();
		STORE_PASSWORD = data.findValue("seller").findValue("mail").findValue("password").asText();
		STORE_COUNTRY = data.findValue("seller").findValue("mail").findValue("country").asText();
		BUYER_MAIL_USERNAME = data1.findValue("buyer").findValue("mail").findValue("username").asText();
		BUYER_MAIL_PASSWORD = data1.findValue("buyer").findValue("mail").findValue("password").asText();
		BUYER_MAIL_COUNTRY = data1.findValue("buyer").findValue("mail").findValue("country").asText();
		BUYER_PHONE_USERNAME = data1.findValue("buyer").findValue("phone").findValue("username").asText();
		BUYER_PHONE_PASSWORD = data1.findValue("buyer").findValue("phone").findValue("password").asText();
		BUYER_PHONE_COUNTRY = data1.findValue("buyer").findValue("phone").findValue("country").asText();
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		signupPage = new SignupPage(driver);
		generateTestData();
	}

	@Test
	public void BH_4588_SignUpWithInvalidCredential() throws SQLException, InterruptedException {

		String country = "Philippines";

		signupPage.navigate().fillOutSignupForm(country, "", password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(EMPTY_USERNAME_ERROR_VI).completeVerify();

		signupPage.navigate().fillOutSignupForm(country, phone, "", displayName, birthday);
		signupPage.verifyPasswordError(EMPTY_PASSWORD_ERROR_VI).completeVerify();

		mail = "automation_mail.com";
		signupPage.navigate().fillOutSignupForm(country, mail, password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(INVALID_FORMAT_ERROR_VI).completeVerify();

		phone = "3454567";
		signupPage.navigate().fillOutSignupForm(country, phone, password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(INVALID_FORMAT_ERROR_VI).completeVerify();

	}

	@Test
	public void BH_4589_ResendVerificationCodeToEmail() throws SQLException, InterruptedException {

		String country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, mail, password, displayName, birthday);

		String firstCode = getVerificationCode(mail);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();

		String resentCode = getVerificationCode(mail);
		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		signupPage.clickConfirmBtn();

		country = signupPage.country;

		// Logout
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, mail, password);
	}

	@Test
	public void BH_1625_ResendVerificationCodeToPhone() throws SQLException, InterruptedException {

		String country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, phone, password, displayName, birthday);

		String firstCode = getVerificationCode(phone);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();

		String resentCode = getVerificationCode(phone);
		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		signupPage.clickConfirmBtn();
		signupPage.inputEmail(mail).clickCompleteBtn();

		country = signupPage.country;

		// Logout
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, phone, password);
	}

	@Test
	public void BH_1593_SignUpWithUsedEmailAccount() throws SQLException, InterruptedException {
		// Signup
		signupPage.navigate().fillOutSignupForm(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD,
				displayName, birthday);
		signupPage.verifyUsernameExistError(MAIL_EXIST_ERROR_VI).completeVerify();
	}

	@Test
	public void BH_1279_SignUpWithUsedPhoneAccount() throws SQLException, InterruptedException {
		// Signup
		signupPage.navigate().fillOutSignupForm(BUYER_PHONE_COUNTRY, BUYER_PHONE_USERNAME, BUYER_PHONE_PASSWORD,
				displayName, birthday);
		signupPage.verifyUsernameExistError(PHONE_EXIST_ERROR_VI).completeVerify();
	}

	@Test
	public void BH_1278_SignupWithPhone() throws SQLException, InterruptedException {
		String country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, phone, password, displayName, birthday)
				.inputVerificationCode(getVerificationCode(phone)).clickConfirmBtn();
		signupPage.inputEmail(mail).clickCompleteBtn();

		countryCode = signupPage.countryCode;

		// Logout
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, phone, password);

		// Check user profile
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickUserProfile();
		new UserProfileInfo(driver).clickMyAccountSection();

		// Verify user info in SF
		Assert.assertEquals(new MyAccount(driver).getDisplayName(), displayName);
		Assert.assertEquals(new MyAccount(driver).getEmail(), mail);
//    	Assert.assertEquals(new MyAccount(driver).getPhoneNumber(), countryCode + ":" + phone);
		Assert.assertEquals(new MyAccount(driver).getBirthday(), birthday);

		new UserProfileInfo(driver).clickMyAddressSection();
		Assert.assertEquals(new MyAddress(driver).getCountry(), country);

		// Verify user info in Dashboard
		pages.dashboard.LoginPage dashboard = new pages.dashboard.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear().navigateToPage("Customers");
		new HomePage(driver).waitTillSpinnerDisappear();
		new AllCustomers(driver).selectBranch("None Branch").getPhoneNumber(displayName);
		new AllCustomers(driver).clickUser(displayName);
		new CustomerDetails(driver).getPhoneNumber();

		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
	}

	@Test
	public void BH_1594_SignupWithEmail() throws SQLException, InterruptedException {
		String country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, mail, password, displayName, birthday)
				.inputVerificationCode(getVerificationCode(mail)).clickConfirmBtn();

		// Logout
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, mail, password);

		// Check user profile
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickUserProfile();
		new UserProfileInfo(driver).clickMyAccountSection();

		// Verify user info in SF
		Assert.assertEquals(new MyAccount(driver).getDisplayName(), displayName);
		Assert.assertEquals(new MyAccount(driver).getEmail(), mail);
		Assert.assertEquals(new MyAccount(driver).getBirthday(), birthday);

		new UserProfileInfo(driver).clickMyAddressSection();
		Assert.assertEquals(new MyAddress(driver).getCountry(), country);

		// Verify user info in Dashboard
		pages.dashboard.LoginPage dashboard = new pages.dashboard.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear().navigateToPage("Customers");
		new AllCustomers(driver).selectBranch("None Branch").clickUser(displayName);
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
	}

	@Test
	public void BH_4590_SignupForEmailWithWrongVerificationCode() throws SQLException, InterruptedException {

		String country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, mail, password, displayName, birthday);

		String code = getVerificationCode(mail);

		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1));
		signupPage.clickConfirmBtn();

		signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR_VI).completeVerify();

		signupPage.inputVerificationCode(code);
		signupPage.clickConfirmBtn();

		country = signupPage.country;

		// Logout
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, mail, password);
	}

	@Test
	public void BH_4590_SignupForPhoneWithWrongVerificationCode() throws SQLException, InterruptedException {

		String country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, phone, password, displayName, birthday);

		String code = getVerificationCode(phone);

		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1)).clickConfirmBtn();
		signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR_VI).completeVerify();
		signupPage.inputVerificationCode(code).clickConfirmBtn();
		signupPage.inputEmail(mail).clickCompleteBtn();

		country = signupPage.country;

		// Logout
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, phone, password);
	}

}
