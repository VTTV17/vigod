package web.StoreFront;

import static org.testng.Assert.assertFalse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import web.BaseTest;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.StoreFront.header.ChangePasswordDialog;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;
import web.StoreFront.signup.SignupPage;
import web.StoreFront.userprofile.MyAccount.MyAccount;
import utilities.thirdparty.Facebook;
import utilities.thirdparty.Mailnesia;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;

import static utilities.links.Links.*;
import static utilities.utils.PropertiesUtil.getPropertiesValueBySFLang;
import static utilities.account.AccountTest.*;

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
	String WRONG_CREDENTIALS_ERROR;
	String NON_EXISTING_EMAIL_ERROR;
	String NON_EXISTING_PHONE_ERROR;
	String WRONG_CURRENT_PASSWORD_ERROR;
	String INVALID_NEW_PASSWORD_ERROR;
	String SAME_4_NEW_PASSWORD_ERROR;

	public void loadValue(String language) throws Exception {
		
		STORE_USERNAME = ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = ADMIN_COUNTRY_TIEN;
		
		BUYER_MAIL_USERNAME = SF_USERNAME_VI_1;
		BUYER_MAIL_PASSWORD = SF_SHOP_VI_PASSWORD;
		BUYER_MAIL_COUNTRY = ADMIN_COUNTRY_TIEN;
		BUYER_PHONE_USERNAME = SF_USERNAME_PHONE_VI_1;
		BUYER_PHONE_PASSWORD = SF_SHOP_VI_PASSWORD;
		BUYER_PHONE_COUNTRY = ADMIN_COUNTRY_TIEN;
		BUYER_FORGOT_MAIL_USERNAME = SF_USERNAME_VI_1;
		BUYER_FORGOT_MAIL_PASSWORD = SF_SHOP_VI_PASSWORD;
		BUYER_FORGOT_MAIL_COUNTRY = ADMIN_COUNTRY_TIEN;
		BUYER_FORGOT_PHONE_USERNAME = SF_USERNAME_PHONE_VI_1;
		BUYER_FORGOT_PHONE_PASSWORD = SF_SHOP_VI_PASSWORD;
		BUYER_FORGOT_PHONE_COUNTRY = ADMIN_COUNTRY_TIEN;
		
		GOMUA_MAIL_USERNAME = GOMUA_USERNAME_EMAIL;
		GOMUA_MAIL_PASSWORD = GOMUA_PASSWORD_EMAIL;
		GOMUA_MAIL_COUNTRY = ADMIN_COUNTRY_TIEN;
		GOMUA_PHONE_USERNAME = GOMUA_USERNAME_PHONE;
		GOMUA_PHONE_PASSWORD = ADMIN_PASSWORD_TIEN;
		GOMUA_PHONE_COUNTRY = ADMIN_COUNTRY_TIEN;
		
		BLANK_USERNAME_ERROR = getPropertiesValueBySFLang("login.error.emptyUsername", language);
		BLANK_PASSWORD_ERROR = getPropertiesValueBySFLang("login.error.emptyPassword", language);
		INVALID_USERNAME_ERROR = getPropertiesValueBySFLang("login.error.invalidUsername", language);
		WRONG_CREDENTIALS_ERROR = getPropertiesValueBySFLang("login.error.wrongCredentials", language);
		NON_EXISTING_EMAIL_ERROR = getPropertiesValueBySFLang("forgotPassword.error.notExistingEmail", language);
		NON_EXISTING_PHONE_ERROR = getPropertiesValueBySFLang("forgotPassword.error.notExistingPhone", language);
		WRONG_CURRENT_PASSWORD_ERROR = getPropertiesValueBySFLang("forgotPassword.error.wrongCurrentPassword", language);
		INVALID_NEW_PASSWORD_ERROR = getPropertiesValueBySFLang("forgotPassword.error.invalidNewPassword", language);
		SAME_4_NEW_PASSWORD_ERROR = getPropertiesValueBySFLang("forgotPassword.error.same4Passwords", language);
	}	
	
	public String getVerificationCode(String phoneCode, String username) throws SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			verificationCode = new Mailnesia(driver).navigateToMailAndGetVerifyCode(username);
		} else {
			verificationCode = new InitConnection().getResetKey(phoneCode + ":" + username);
		}
		return verificationCode;
	}

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		headerPage = new HeaderSF(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}	
	
	@BeforeMethod
	public void setup() throws Exception {
		instantiatePageObjects();
		loadValue(language); // Temporary
	}

//	@Test
	public void LoginSF_01_CheckTranslation() throws Exception {
		
		loginPage.navigate(SF_URL_TIEN);
		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language).clickUserInfoIcon().clickLoginIcon();
        loginPage.verifyTextAtLoginScreen(language);
        
        loginPage.navigate(SF_URL_TIEN);
        new HeaderSF(driver).clickUserInfoIcon().clickUserInfoIcon().clickLoginIcon();
        loginPage.clickForgotPassword().verifyTextAtForgotPasswordScreen(language);
	}		
	
	@Test
	public void LoginSF_03_LoginWithAllFieldsLeftBlank() {
		
		loginPage.navigate(SF_URL_TIEN);
		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);
		
		//Empty username
		String error = loginPage.performLogin("", generate.generateNumber(9)).getUsernameError();
		Assert.assertEquals(error, BLANK_USERNAME_ERROR);
		commonAction.refreshPage();
		
		//Empty password
		error = loginPage.performLogin(generate.generateNumber(9), "").getPasswordError();
		Assert.assertEquals(error, BLANK_PASSWORD_ERROR);
		commonAction.refreshPage();
		
		//Empty username and password
		error = loginPage.performLogin("", "").getPasswordError();
		Assert.assertEquals(error, BLANK_PASSWORD_ERROR);
		error = loginPage.getUsernameError();
		Assert.assertEquals(error, BLANK_USERNAME_ERROR);
	}

	@Test
	public void LoginSF_04_LoginWithInvalidPhoneFormat() {
		
		loginPage.navigate(SF_URL_TIEN);
		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);
		
		//7-digit phone number
		String error = loginPage.performLogin(generate.generateNumber(7), generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, INVALID_USERNAME_ERROR);
		commonAction.refreshPage();
		
		//16-digit phone number
		error = loginPage.performLogin(generate.generateNumber(16), generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, INVALID_USERNAME_ERROR);
	}

	@Test
	public void LoginSF_05_LoginWithInvalidMailFormat() {
		
		loginPage.navigate(SF_URL_TIEN);
		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);
		
		String error = loginPage.performLogin(generate.generateString(10), generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, INVALID_USERNAME_ERROR);
	}

//	@Test
	public void BH_1335_UnableToChangePasswordForFacebookAccount() {
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickFacebookIcon();
		
		commonAction.switchToWindow(1);
		new Facebook(driver).performLogin(ADMIN_FACEBOOK_USERNAME, ADMIN_FACEBOOK_PASSWORD);
		commonAction.switchToWindow(0);
		
		commonAction.sleepInMiliSecond(5000);
		headerPage.clickUserInfoIcon();
		
		headerPage.clickUserInfoIcon();
		commonAction.sleepInMiliSecond(1000);
		
		assertFalse(headerPage.checkForPresenceOfChangePasswordLink());
	}
	
//	@Test
	public void LoginSF_13_NavigateBetweenLoginAndSignupForm() {
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language).clickUserInfoIcon().clickSignupIcon();
		
		new SignupPage(driver).inputBirthday("02/02/1990").clickLoginNow().clickForgotPassword().clickBackToLogin().clickCreateNewAccount().inputBirthday("01/02/1990");
	}
	
	@Test
	public void LoginSF_06_LoginWithNonExistingAccount() {

		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		String error = loginPage.performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10)).getLoginFailError();
		Assert.assertEquals(error, WRONG_CREDENTIALS_ERROR);
		commonAction.refreshPage();
		
		error = loginPage.performLogin(generate.generateNumber(13), generate.generateString(10)).getLoginFailError();
		Assert.assertEquals(error, WRONG_CREDENTIALS_ERROR);
	}
	
	@Test
	public void BH_1609_LogoutAccount() {
		loginPage.navigate(SF_URL_TIEN).performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		
		loginPage.navigate(SF_ShopVi).performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		commonAction.switchToWindow(0);
		headerPage.clickUserInfoIcon().clickLogout();
		
		commonAction.switchToWindow(1);
		commonAction.refreshPage();
		headerPage.clickUserInfoIcon().clickLogout();
	}
	
	@Test
	public void BH_1282_LoginWithCorrectAccount() throws InterruptedException {
		// Login
		loginPage.navigate(SF_URL_TIEN).performLogin(BUYER_PHONE_COUNTRY, BUYER_PHONE_USERNAME, BUYER_PHONE_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon().clickUserProfile().clickMyAccountSection();
		String displayName = new MyAccount(driver).getDisplayName();
		String phone = new MyAccount(driver).getPhoneNumber();
		headerPage.clickUserInfoIcon().clickLogout();

		// Login
		loginPage.navigate(SF_URL_TIEN).performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon().clickUserProfile().clickMyAccountSection();
		String displayName1 = new MyAccount(driver).getDisplayName();
		String mail = new MyAccount(driver).getEmail();
		headerPage.clickUserInfoIcon().clickLogout();

		// Verify user info in Dashboard
		web.Dashboard.login.LoginPage dashboard = new web.Dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		
		new AllCustomers(driver).navigate().inputSearchTerm(displayName).clickUser(displayName);
		Assert.assertEquals(new CustomerDetails(driver).getPhoneNumber(), phone);
		new CustomerDetails(driver).clickCancelBtn();
		
		new AllCustomers(driver).navigate().inputSearchTerm(displayName1).clickUser(displayName1);
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
	}

	@Test
	public void BH_1595_LoginWithExistingGomuaAccount() throws InterruptedException {
		// Login
		loginPage.navigate(SF_URL_TIEN).performLogin(GOMUA_PHONE_COUNTRY, GOMUA_PHONE_USERNAME, GOMUA_PHONE_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon().clickUserProfile().clickMyAccountSection();
		String displayName = new MyAccount(driver).getDisplayName();
		String phone = new MyAccount(driver).getPhoneNumber();
		headerPage.clickUserInfoIcon().clickLogout();

		// Login
		loginPage.navigate(SF_URL_TIEN).performLogin(GOMUA_MAIL_COUNTRY, GOMUA_MAIL_USERNAME, GOMUA_MAIL_PASSWORD);
		
		// Get user's display name in User profile
		headerPage.clickUserInfoIcon().clickUserProfile().clickMyAccountSection();
		String displayName1 = new MyAccount(driver).getDisplayName();
		String mail = new MyAccount(driver).getEmail();
		headerPage.clickUserInfoIcon().clickLogout();

		// Verify user info in Dashboard
		web.Dashboard.login.LoginPage dashboard = new web.Dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		
		new AllCustomers(driver).navigate().inputSearchTerm(displayName).clickUser(displayName);
		Assert.assertEquals(new CustomerDetails(driver).getPhoneNumber(), phone);
		new CustomerDetails(driver).clickCancelBtn();
	
		new AllCustomers(driver).navigate().inputSearchTerm(displayName1).clickUser(displayName1);
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
	}		
	
	@Test
	public void BH_1285_ForgotMailPassword() throws InterruptedException, SQLException {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.selectCountryForgot(country).inputUsernameForgot(username).clickContinueBtn().inputPasswordForgot(newPassword)
		.inputVerificationCode(getVerificationCode(generate.getPhoneCode(country), username)).clickConfirmBtn();
		headerPage.clickUserInfoIcon().clickLogout();

		// Re-login with new password
		loginPage.performLogin(country, username, newPassword);
		
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

		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.selectCountryForgot(country).inputUsernameForgot(username).clickContinueBtn().inputPasswordForgot(newPassword)
		.inputVerificationCode(getVerificationCode(generate.getPhoneCode(country), username)).clickConfirmBtn();
		headerPage.clickUserInfoIcon().clickLogout();

		// Re-login with new password
		loginPage.performLogin(country, username, newPassword);
		
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
	public void LoginSF_10_ForgotPasswordForNonExistingAccount() throws InterruptedException, SQLException {
		String nonExistingMailAccount = generate.generateString(10) + "@nbobd.com";
		String nonExistingPhoneAccount = generate.generateNumber(13);
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		headerPage.clickUserInfoIcon().clickLoginIcon();
		String error = loginPage.clickForgotPassword().inputUsernameForgot(nonExistingMailAccount).clickContinueBtn().getForgotPasswordError();
		Assert.assertEquals(error, NON_EXISTING_EMAIL_ERROR);
		
		commonAction.refreshPage();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		error = loginPage.clickForgotPassword().inputUsernameForgot(nonExistingPhoneAccount).clickContinueBtn().getForgotPasswordError();
		Assert.assertEquals(error, NON_EXISTING_PHONE_ERROR);
	}

	@Test
	public void BH_4593_ChangePasswordWithInvalidData() throws InterruptedException {
		String newPassword = BUYER_MAIL_PASSWORD + "@" + generate.generateNumber(3);
		String error;
		
		// Login
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
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
		loginPage.navigate(SF_URL_TIEN).performLogin(country, username, password);
		
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
		loginPage.performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	
	
	@Test
	public void BH_3814_ChangePasswordThatResemblesLast4Passwords() throws InterruptedException, SQLException {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		// Login
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		loginPage.performLogin(country, username, password);
		
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
		loginPage.performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	


    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	
	
}
