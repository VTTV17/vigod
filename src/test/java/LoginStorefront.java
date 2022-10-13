import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.Mailnesia;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.customers.allcustomers.CustomerDetails;
import pages.dashboard.home.HomePage;
import pages.storefront.header.ChangePasswordDialog;
import pages.storefront.header.HeaderSF;
import pages.storefront.login.LoginPage;
import pages.storefront.userprofile.MyAccount.MyAccount;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;

public class LoginStorefront extends BaseTest {

	LoginPage loginPage;
	HeaderSF headerPage;

	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;
	
	String BUYER_MAIL_USERNAME;
	String BUYER_MAIL_PASSWORD;
	String BUYER_MAIL_COUNTRY;
	String BUYER_PHONE_USERNAME;
	String BUYER_PHONE_PASSWORD;
	String BUYER_PHONE_COUNTRY;

	String BUYER_FORGOT_MAIL_USERNAME;
	String BUYER_FORGOT_MAIL_PASSWORD;
	String BUYER_FORGOT_MAIL_COUNTRY;
	String BUYER_FORGOT_PHONE_USERNAME;
	String BUYER_FORGOT_PHONE_PASSWORD;
	String BUYER_FORGOT_PHONE_COUNTRY;	
	
	String GOMUA_MAIL_USERNAME;
	String GOMUA_MAIL_PASSWORD;
	String GOMUA_MAIL_COUNTRY;
	String GOMUA_PHONE_USERNAME;
	String GOMUA_PHONE_PASSWORD;
	String GOMUA_PHONE_COUNTRY;
	
	String BLANK_USERNAME_ERROR;
	String BLANK_PASSWORD_ERROR;
	String INVALID_USERNAME_ERROR;
	String INVALID_CREDENTIALS_ERROR;
	String NON_EXISTING_EMAIL_ERROR;
	String NON_EXISTING_PHONE_ERROR;
	String WRONG_CURRENT_PASSWORD_ERROR;
	String INVALID_NEW_PASSWORD_ERROR;
	String SAME_NEW_PASSWORD_ERROR;
	String SAME_4_NEW_PASSWORD_ERROR;


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
		JsonNode db = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
		JsonNode sf = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
		JsonNode gm = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("gomua");
		STORE_USERNAME = db.findValue("seller").findValue("mail").findValue("username").asText();
		STORE_PASSWORD = db.findValue("seller").findValue("mail").findValue("password").asText();
		STORE_COUNTRY = db.findValue("seller").findValue("mail").findValue("country").asText();
		BUYER_MAIL_USERNAME = sf.findValue("buyer").findValue("mail").findValue("username").asText();
		BUYER_MAIL_PASSWORD = sf.findValue("buyer").findValue("mail").findValue("password").asText();
		BUYER_MAIL_COUNTRY = sf.findValue("buyer").findValue("mail").findValue("country").asText();
		BUYER_PHONE_USERNAME = sf.findValue("buyer").findValue("phone").findValue("username").asText();
		BUYER_PHONE_PASSWORD = sf.findValue("buyer").findValue("phone").findValue("password").asText();
		BUYER_PHONE_COUNTRY = sf.findValue("buyer").findValue("phone").findValue("country").asText();
		BUYER_FORGOT_MAIL_USERNAME = sf.findValue("buyer").findValue("forgotMail").findValue("username").asText();
		BUYER_FORGOT_MAIL_PASSWORD = sf.findValue("buyer").findValue("forgotMail").findValue("password").asText();
		BUYER_FORGOT_MAIL_COUNTRY = sf.findValue("buyer").findValue("forgotMail").findValue("country").asText();
		BUYER_FORGOT_PHONE_USERNAME = sf.findValue("buyer").findValue("forgotPhone").findValue("username").asText();
		BUYER_FORGOT_PHONE_PASSWORD = sf.findValue("buyer").findValue("forgotPhone").findValue("password").asText();
		BUYER_FORGOT_PHONE_COUNTRY = sf.findValue("buyer").findValue("forgotPhone").findValue("country").asText();
		GOMUA_MAIL_USERNAME = gm.findValue("buyer").findValue("mail").findValue("username").asText();
		GOMUA_MAIL_PASSWORD = gm.findValue("buyer").findValue("mail").findValue("password").asText();
		GOMUA_MAIL_COUNTRY = gm.findValue("buyer").findValue("mail").findValue("country").asText();
		GOMUA_PHONE_USERNAME = gm.findValue("buyer").findValue("phone").findValue("username").asText();
		GOMUA_PHONE_PASSWORD = gm.findValue("buyer").findValue("phone").findValue("password").asText();
		GOMUA_PHONE_COUNTRY = gm.findValue("buyer").findValue("phone").findValue("country").asText();
		
		BLANK_USERNAME_ERROR = sf.findValue("emptyUsernameError").asText();
		BLANK_PASSWORD_ERROR = sf.findValue("emptyPasswordError").asText();
		INVALID_USERNAME_ERROR = sf.findValue("invalidUsernameFormat").asText();
		INVALID_CREDENTIALS_ERROR = sf.findValue("invalidCredentials").asText();
		NON_EXISTING_EMAIL_ERROR = sf.findValue("notExistingEmailAccount").asText();
		NON_EXISTING_PHONE_ERROR = sf.findValue("notExistingPhoneAccount").asText();
		WRONG_CURRENT_PASSWORD_ERROR = sf.findValue("wrongCurrentPassword").asText();
		INVALID_NEW_PASSWORD_ERROR = sf.findValue("invalidNewPassword").asText();
		SAME_4_NEW_PASSWORD_ERROR = sf.findValue("same4Passwords").asText();
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		loginPage = new LoginPage(driver);
		headerPage = new HeaderSF(driver);
	}

//	@Test
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

//	@Test
	public void TC02_SF_LoginWithInvalidPhoneFormat() {
		// Log in with a phone number consisting of 7 digits.
		loginPage.navigate().performLogin(generate.generateNumber(7), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_USERNAME_ERROR).completeVerify();
		// Log in with a phone number consisting of 16 digits.
		loginPage.navigate().performLogin(generate.generateNumber(16), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_USERNAME_ERROR).completeVerify();
	}

//	@Test
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
	
	// Run again once new code is deployed onto STAG
	@Test
	public void BH_1282_LoginWithCorrectAccount() throws InterruptedException {
		// Login
		loginPage.navigate().performLogin(BUYER_PHONE_COUNTRY, BUYER_PHONE_USERNAME, BUYER_PHONE_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		String displayName = new MyAccount(driver).getDisplayName();
		String phone = new MyAccount(driver).getPhoneNumber();

		headerPage.clickUserInfoIcon().clickLogout();

		// Login
		loginPage.navigate().performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		String displayName1 = new MyAccount(driver).getDisplayName();
		String mail = new MyAccount(driver).getEmail();

		headerPage.clickUserInfoIcon().clickLogout();

		// Verify user info in Dashboard
		pages.dashboard.login.LoginPage dashboard = new pages.dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear();
		
		new AllCustomers(driver).navigate().inputSearchTerm(displayName).clickUser(displayName);
		Assert.assertTrue((new CustomerDetails(driver).getPhoneNumber()).contains(phone.split(":")[1]));
		
		commonAction.navigateBack();
		new AllCustomers(driver).navigate().inputSearchTerm(displayName1).clickUser(displayName1);
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
	}

	// Run again once new code is deployed onto STAG
	@Test
	public void BH_1595_LoginWithExistingGomuaAccount() throws InterruptedException {
		// Login
		loginPage.navigate().performLogin(GOMUA_PHONE_COUNTRY, GOMUA_PHONE_USERNAME, GOMUA_PHONE_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		String displayName = new MyAccount(driver).getDisplayName();
		String phone = new MyAccount(driver).getPhoneNumber();

		headerPage.clickUserInfoIcon().clickLogout();

		// Login
		loginPage.navigate().performLogin(GOMUA_MAIL_COUNTRY, GOMUA_MAIL_USERNAME, GOMUA_MAIL_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		String displayName1 = new MyAccount(driver).getDisplayName();
		String mail = new MyAccount(driver).getEmail();

		headerPage.clickUserInfoIcon().clickLogout();

		// Verify user info in Dashboard
		pages.dashboard.login.LoginPage dashboard = new pages.dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear();
		
		new AllCustomers(driver).navigate().inputSearchTerm(displayName).clickUser(displayName);
		Assert.assertTrue((new CustomerDetails(driver).getPhoneNumber()).contains(phone.split(":")[1]));
		
		commonAction.navigateBack();
		new AllCustomers(driver).navigate().inputSearchTerm(displayName1).clickUser(displayName1);
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
	}		
	
	@Test
	public void BH_1285_ForgotMailPassword() throws InterruptedException, SQLException {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		loginPage.navigate();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.selectCountryForgot(country)
		.inputUsernameForgot(username)
		.clickContinueBtn()
		.inputPasswordForgot(newPassword);
		loginPage.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();

		headerPage.clickUserInfoIcon().clickLogout();

		// Re-login with new password
		loginPage.navigate().performLogin(country, username, newPassword);
		
		// Change password back to the first password
		String currentPassword = "";
		for (int i=0; i<5; i++) {
			currentPassword = newPassword;
    		
			newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;
			
    		headerPage.clickUserInfoIcon()
    		.clickChangePassword()
    		.inputCurrentPassword(currentPassword)
    		.inputNewPassword(newPassword)
    		.clickDoneBtn();
		}			
	}

	@Test
	public void BH_1286_ForgotPhonePassword() throws InterruptedException, SQLException {
		String country = BUYER_FORGOT_PHONE_COUNTRY;
		String username = BUYER_FORGOT_PHONE_USERNAME;
		String password = BUYER_FORGOT_PHONE_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		loginPage.navigate();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.selectCountryForgot(country)
		.inputUsernameForgot(username)
		.clickContinueBtn()
		.inputPasswordForgot(newPassword);
		loginPage.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();

		headerPage.clickUserInfoIcon().clickLogout();

		// Re-login with new password
		loginPage.navigate().performLogin(country, username, newPassword);
		
		// Change password back to the first password
		String currentPassword = "";
		for (int i=0; i<5; i++) {
			currentPassword = newPassword;
    		
			newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;
			
    		headerPage.clickUserInfoIcon()
    		.clickChangePassword()
    		.inputCurrentPassword(currentPassword)
    		.inputNewPassword(newPassword)
    		.clickDoneBtn();
		}		
	}
	
	@Test
	public void BH_4591_ForgotPasswordForNonExistingAccount() throws InterruptedException, SQLException {
		String nonExistingMailAccount = generate.generateString(10) + "@nbobd.com";
		String nonExistingPhoneAccount = generate.generateNumber(13);
		
		loginPage.navigate();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.inputUsernameForgot(nonExistingMailAccount)
		.clickContinueBtn()
		.verifyForgetPasswordForNonExistingAccountError(NON_EXISTING_EMAIL_ERROR)
		.completeVerify();
		
		loginPage.navigate();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.inputUsernameForgot(nonExistingPhoneAccount)
		.clickContinueBtn()
		.verifyForgetPasswordForNonExistingAccountError(NON_EXISTING_PHONE_ERROR)
		.completeVerify();
	}

	@Test
	public void BH_4593_ChangePasswordWithInvalidData() throws InterruptedException {
		String newPassword = BUYER_MAIL_PASSWORD + "@" + generate.generateNumber(3);
		String error;
		
		// Login
		loginPage.navigate().performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		// Empty current password
		error = headerPage.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword("")
				.inputNewPassword(newPassword)
				.clickDoneBtn()
				.getErrorForCurrentPasswordField();
		new ChangePasswordDialog(driver).clickCloseBtn();
		Assert.assertEquals(error, WRONG_CURRENT_PASSWORD_ERROR);
		
		// Empty new password
		error = headerPage.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(BUYER_MAIL_PASSWORD)
				.inputNewPassword("")
				.clickDoneBtn()
				.getErrorForNewPasswordField();
		new ChangePasswordDialog(driver).clickCloseBtn();
		Assert.assertEquals(error, BLANK_PASSWORD_ERROR);
		
		// Incorrect current password
		error = headerPage.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(BUYER_MAIL_PASSWORD + "abc")
				.inputNewPassword(newPassword)
				.clickDoneBtn()
				.getErrorForCurrentPasswordField();
		new ChangePasswordDialog(driver).clickCloseBtn();
		Assert.assertEquals(error, WRONG_CURRENT_PASSWORD_ERROR);

		// Inadequate number of characters.
		newPassword = "asvn45$";
		error = headerPage.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(BUYER_MAIL_PASSWORD)
				.inputNewPassword(newPassword)
				.clickDoneBtn()
				.getErrorForNewPasswordField();
		new ChangePasswordDialog(driver).clickCloseBtn();
		Assert.assertEquals(error, INVALID_NEW_PASSWORD_ERROR);
		
		// Absence of digits.
		newPassword = "asvn$%^&";
		error = headerPage.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(BUYER_MAIL_PASSWORD)
				.inputNewPassword(newPassword)
				.clickDoneBtn()
				.getErrorForNewPasswordField();
		new ChangePasswordDialog(driver).clickCloseBtn();
		Assert.assertEquals(error, INVALID_NEW_PASSWORD_ERROR);
		
		// Absence of special characters.
		newPassword = "asvn4567";
		error = headerPage.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(BUYER_MAIL_PASSWORD)
				.inputNewPassword(newPassword)
				.clickDoneBtn()
				.getErrorForNewPasswordField();
		new ChangePasswordDialog(driver).clickCloseBtn();
		Assert.assertEquals(error, INVALID_NEW_PASSWORD_ERROR);
	}
	

	@Test
	public void BH_3813_ChangePassword() throws InterruptedException, SQLException {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);
		
		// Login
		loginPage.navigate().performLogin(country, username, password);
		
		// Change password back to the first password
		String currentPassword = "";
		for (int i=0; i<5; i++) {
			
			currentPassword = (i==0) ? password : newPassword;
			
			newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;
			
			headerPage.clickUserInfoIcon()
			.clickChangePassword()
			.inputCurrentPassword(currentPassword)
			.inputNewPassword(newPassword)
			.clickDoneBtn();
		}
		
		// Logout then re-login with old password
		headerPage.clickUserInfoIcon().clickLogout();
		loginPage.navigate().performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	
	
	@Test
	public void BH_3814_ChangePasswordThatResemblesLast4Passwords() throws InterruptedException, SQLException {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		// Login
		loginPage.navigate().performLogin(country, username, password);
		
		// Change password back to the first password
		String currentPassword = "";
		List<String> oldPasswords = new ArrayList<String>(); 
		for (int i=0; i<5; i++) {
			
			currentPassword = (i==0) ? password : newPassword;
    		
			newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;
			
    		headerPage.clickUserInfoIcon()
    		.clickChangePassword()
    		.inputCurrentPassword(currentPassword)
    		.inputNewPassword(newPassword)
    		.clickDoneBtn();
    		
    		if (i==0||i==4) continue; // First and last changed passwords will not be added to the list
    		oldPasswords.add(newPassword);
		}
		
		// Verify new password should not be the same as the last 4 passwords.
		for (String pw : oldPasswords) {
    		String error = headerPage.clickUserInfoIcon()
	    		.clickChangePassword()
	    		.inputCurrentPassword(newPassword)
	    		.inputNewPassword(pw)
	    		.clickDoneBtn()
				.getErrorForCurrentPasswordField();
			new ChangePasswordDialog(driver).clickCloseBtn();
			Assert.assertEquals(error, SAME_4_NEW_PASSWORD_ERROR);
		}
		
		
		// Logout then re-login with old password
		headerPage.clickUserInfoIcon().clickLogout();
		loginPage.navigate().performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	
	
}
