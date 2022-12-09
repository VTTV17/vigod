import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.Mailnesia;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.customers.allcustomers.CustomerDetails;
import pages.dashboard.home.HomePage;
import pages.gomua.headergomua.HeaderGoMua;
import pages.gomua.logingomua.LoginGoMua;
import pages.gomua.myprofile.MyProfileGoMua;
import pages.storefront.GeneralSF;
import pages.storefront.header.HeaderSF;
import pages.storefront.login.LoginPage;
import pages.storefront.signup.SignupPage;
import pages.storefront.userprofile.MyAddress;
import pages.storefront.userprofile.MyAccount.MyAccount;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;

public class SignupStorefront extends BaseTest {
	
	final static Logger logger = LogManager.getLogger(SignupStorefront.class);

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
	String EMPTY_DISPLAYNAME_ERROR_VI = "Vui lòng nhập tên của bạn";
	String INVALID_FORMAT_ERROR_VI = "Số điện thoại hoặc email không đúng";
	String INVALID_CODE_ERROR_VI = "Mã xác thực không đúng";
	String INVALID_CODE_ERROR_EN = "Incorrect confirmation code!";

	// This function generate test data for each test case
	public void generateTestData() {
		phone = generate.randomNumberGeneratedFromEpochTime(10); //Random number of 10 digits;
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		password = "fortesting!1";
		country = "rd";
		displayName = "Automation Buyer " + phone;
		birthday = "21/02/1990";
		language = "rd";
	}
	
	// This function returns a verification code needed for sign-up procedure. It works for both phone and email account
	public String getVerificationCode(String username) throws SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			verificationCode = new Mailnesia(driver).navigateToMailAndGetVerifyCode(username);
		} else {
			verificationCode = new InitConnection().getActivationKey(signupPage.countryCode + ":" + username);
		}
		return verificationCode;
	}
	
	// This function checks if an email is sent to the user saying the user has signed up for an account successfully
	public void verifyEmailUponSuccessfulSignup(String username) {
		String language = new GeneralSF(driver).getDisplayLanguage();
		String title = commonAction.getPageTitle();
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		String expectedVerificationCodeMessage;
		String expectedSuccessfulSignupMessage;
		if (language.contentEquals("vi")) {
			expectedSuccessfulSignupMessage = signupPage.SUCCESSFUL_SIGNUP_MESSAGE_VI.formatted(title);
			expectedVerificationCodeMessage = signupPage.VERIFICATION_CODE_MESSAGE_VI.formatted(title);
		} else {
			expectedSuccessfulSignupMessage = signupPage.SUCCESSFUL_SIGNUP_MESSAGE_EN.formatted(title);
			expectedVerificationCodeMessage = signupPage.VERIFICATION_CODE_MESSAGE_VI.formatted(title);
		}
		String [][] mailContent = new Mailnesia(driver).navigate(username).getListOfEmailHeaders();
		Assert.assertEquals(mailContent[0][3], expectedSuccessfulSignupMessage);
		Assert.assertTrue(mailContent[1][3].contains(expectedVerificationCodeMessage));
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
	public void BH_4588_SignUpWithInvalidCredential() {

		signupPage.navigate().fillOutSignupForm(country, "", password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(EMPTY_USERNAME_ERROR_VI).completeVerify();

		signupPage.navigate().fillOutSignupForm(country, phone, "", displayName, birthday);
		signupPage.verifyPasswordError(EMPTY_PASSWORD_ERROR_VI).completeVerify();

		signupPage.navigate().fillOutSignupForm(country, phone, password, "", birthday);
		signupPage.verifyDisplayNameError(EMPTY_DISPLAYNAME_ERROR_VI).completeVerify();		
		
		mail = "automation_mail.com";
		signupPage.navigate().fillOutSignupForm(country, mail, password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(INVALID_FORMAT_ERROR_VI).completeVerify();

		phone = "3454567";
		signupPage.navigate().fillOutSignupForm(country, phone, password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(INVALID_FORMAT_ERROR_VI).completeVerify();

	}

	@Test
	public void BH_4589_ResendVerificationCodeToEmail() throws SQLException {
		
		username = mail;
		
		// Signup
		signupPage.navigate().fillOutSignupForm(country, username, password, displayName, birthday);
		country = signupPage.country;

		// Verify if new code has been sent to users when they click on Resend button
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode).clickResendOTP();
		
		if (!username.matches("\\d+")) { //If that's a mail account, wait for 8s
			commonAction.sleepInMiliSecond(8000); 
		}
		
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode).clickConfirmBtn();
		
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn(); //If that's a phone account, input email info
		}
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, username, password);
	}

	@Test
	public void BH_1625_ResendVerificationCodeToPhone() throws SQLException {

		username = phone;

		// Signup
		signupPage.navigate().fillOutSignupForm(country, username, password, displayName, birthday);
		country = signupPage.country;

		// Verify if new code has been sent to users when they click on Resend button
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode).clickResendOTP();
		
		if (!username.matches("\\d+")) { //If that's a mail account, wait for 8s
			commonAction.sleepInMiliSecond(8000); 
		}
		
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode).clickConfirmBtn();
		
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn(); //If that's a phone account, input email info
		}
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, username, password);
	}

	@Test
	public void BH_1593_SignUpWithUsedEmailAccount() {
		// Signup
		signupPage.navigate()
		.fillOutSignupForm(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD, displayName, birthday)
		.verifyUsernameExistError(MAIL_EXIST_ERROR_VI)
		.completeVerify();
	}

	@Test
	public void BH_1279_SignUpWithUsedPhoneAccount() {
		// Signup
		signupPage.navigate()
		.fillOutSignupForm(BUYER_PHONE_COUNTRY, BUYER_PHONE_USERNAME, BUYER_PHONE_PASSWORD, displayName, birthday)
		.verifyUsernameExistError(PHONE_EXIST_ERROR_VI)
		.completeVerify();
	}

	@Test
	public void BH_1278_SignupWithPhone() throws SQLException {
		username = phone;
		country = "Vietnam";
		
//		signupPage.navigate();
//		new HeaderSF(driver).clickUserInfoIcon().changeLanguage("English");
		
		// Signup
		signupPage.navigate()
		.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		countryCode = signupPage.countryCode;
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, username, password);

		// Verify user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		Assert.assertEquals(new MyAccount(driver).getDisplayName(), displayName);
		Assert.assertEquals(new MyAccount(driver).getEmail(), mail);
		Assert.assertEquals(new MyAccount(driver).getBirthday(), birthday);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new MyAccount(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		new UserProfileInfo(driver).clickMyAddressSection();
		Assert.assertEquals(new MyAddress(driver).getCountry(), country);
		
		// Log into Dashboard
		pages.dashboard.login.LoginPage dashboard = new pages.dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear();
		
		// Verify user info in Dashboard
		new AllCustomers(driver).navigate().selectBranch("None Branch").clickUser(displayName);
		
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new CustomerDetails(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			signupPage.navigate();
			verifyEmailUponSuccessfulSignup(username);			
		}
	}

	@Test
	public void BH_1594_SignupWithEmail() throws SQLException {
		username = mail;
		country = "Philippines";
		
//		signupPage.navigate();
//		new HeaderSF(driver).clickUserInfoIcon().changeLanguage("English");
		
		// Signup
		signupPage.navigate()
		.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		countryCode = signupPage.countryCode;
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, username, password);

		// Verify user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		Assert.assertEquals(new MyAccount(driver).getDisplayName(), displayName);
		Assert.assertEquals(new MyAccount(driver).getEmail(), mail);
		Assert.assertEquals(new MyAccount(driver).getBirthday(), birthday);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new MyAccount(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		new UserProfileInfo(driver).clickMyAddressSection();
		Assert.assertEquals(new MyAddress(driver).getCountry(), country);
		
		// Log into Dashboard
		pages.dashboard.login.LoginPage dashboard = new pages.dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear();
		
		// Verify user info in Dashboard
		new AllCustomers(driver).navigate().selectBranch("None Branch").clickUser(displayName);
		
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new CustomerDetails(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			signupPage.navigate();
			verifyEmailUponSuccessfulSignup(username);			
		}
	}

	@Test
	public void BH_4590_SignupForEmailWithWrongVerificationCode() throws SQLException {

		username = mail;
		country = "Philippines";

		// Signup
		signupPage.navigate().fillOutSignupForm(country, username, password, displayName, birthday);
		country = signupPage.country;
		
		// Get verification code
		String code = getVerificationCode(username);
		
		// Input wrong verification code
		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1)).clickConfirmBtn();
		signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR_VI).completeVerify();
		
		// Input correct verification code
		signupPage.inputVerificationCode(code).clickConfirmBtn();
		
		// Input mail info if this is a phone account
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, username, password);
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();
		
	}

	@Test
	public void BH_1288_LogIntoGomuaWithAccountCreatedOnStorefront() throws SQLException {
		
		boolean mailProvided = false;
		boolean birthdayProvided = false;
		username = phone;
		country = "Vietnam";
		birthday = (birthdayProvided) ? birthday : "";
		
		// Signup
		signupPage.navigate()
		.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		countryCode = signupPage.countryCode;
		if (username.matches("\\d+")) { // Check if this is a phone account
			if (mailProvided) { // Decide whether to provide an email account or not
				signupPage.inputEmail(mail).clickCompleteBtn();
			} else {
				signupPage.clickLater();
			}
		}
			
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate().performLogin(country, username, password);

		// Get user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		String SF_DisplayName = new MyAccount(driver).getDisplayName();
		String SF_Gender = new MyAccount(driver).getGender();
		String SF_Email = new MyAccount(driver).getEmail();
		String SF_Birthday = new MyAccount(driver).getBirthday();
		String SF_Phone = (username.matches("\\d+")) ? new MyAccount(driver).getPhoneNumber() : "";
		
		// Log into Gomua
		new HeaderGoMua(driver).navigateToGoMua().clickOnLogInBTN();
		new LoginGoMua(driver).loginWithUserName(username, password);
		
		// Get user info in Gomua
		new HeaderGoMua(driver).goToMyProfile();
		new MyProfileGoMua(driver).clickOnEditProfile();
		
		String Gomua_DisplayName = new MyProfileGoMua(driver).getDisplayName();
		String Gomua_Email = new MyProfileGoMua(driver).getEmail();
		String Gomua_Phone = new MyProfileGoMua(driver).getPhoneNumber();
		String Gomua_Gender = new MyProfileGoMua(driver).getGender();
		String Gomua_Birthday = new MyProfileGoMua(driver).getBirthday();
		
		// Format birthday as dd/mm/yyyy
		String formattedBirthday = birthday;
		if (birthdayProvided) {
			Matcher m = Pattern.compile("\\d+").matcher(Gomua_Birthday);
			List<String> code=new ArrayList<String>(); 
			while (m.find()) {
				code.add(m.group());
			}
			formattedBirthday = String.join("/", code.get(2), code.get(1), code.get(0));
		}
		
		// Verify user info in SF and Gomua match
		Assert.assertEquals(Gomua_DisplayName, SF_DisplayName);
		Assert.assertEquals(Gomua_Gender, SF_Gender);
		Assert.assertEquals(Gomua_Email, SF_Email);
		if (username.matches("\\d+")) {
			Assert.assertEquals(Gomua_Phone.replace(" ", ":"), SF_Phone);
		}
		Assert.assertEquals(formattedBirthday, SF_Birthday);
	}

}
