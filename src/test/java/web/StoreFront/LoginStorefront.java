package web.StoreFront;

import static org.testng.Assert.assertFalse;
import static utilities.account.AccountTest.ADMIN_COUNTRY_TIEN;
import static utilities.account.AccountTest.ADMIN_FACEBOOK_PASSWORD;
import static utilities.account.AccountTest.ADMIN_FACEBOOK_USERNAME;
import static utilities.account.AccountTest.ADMIN_PASSWORD_TIEN;
import static utilities.account.AccountTest.ADMIN_USERNAME_TIEN;
import static utilities.account.AccountTest.GOMUA_PASSWORD_EMAIL;
import static utilities.account.AccountTest.GOMUA_USERNAME_EMAIL;
import static utilities.account.AccountTest.GOMUA_USERNAME_PHONE;
import static utilities.account.AccountTest.SF_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.SF_USERNAME_PHONE_VI_1;
import static utilities.account.AccountTest.SF_USERNAME_VI_1;
import static utilities.links.Links.SF_ShopVi;
import static utilities.links.Links.SF_URL_TIEN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.kibana.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.thirdparty.Facebook;
import utilities.utils.PropertiesUtil;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.StoreFront.header.ChangePasswordDialog;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.ForgotPasswordDialog;
import web.StoreFront.login.LoginPage;
import web.StoreFront.signup.SignupPage;
import web.StoreFront.userprofile.MyAccount.MyAccount;

public class LoginStorefront extends BaseTest {

	LoginPage loginPage;
	HeaderSF headerPage;

	String STORE_USERNAME = ADMIN_USERNAME_TIEN;
	String STORE_PASSWORD = ADMIN_PASSWORD_TIEN;
	String STORE_COUNTRY = ADMIN_COUNTRY_TIEN;
	
	String BUYER_MAIL_USERNAME = SF_USERNAME_VI_1;
	String BUYER_MAIL_PASSWORD = SF_SHOP_VI_PASSWORD;
	String BUYER_MAIL_COUNTRY = ADMIN_COUNTRY_TIEN;
	String BUYER_PHONE_USERNAME = SF_USERNAME_PHONE_VI_1;
	String BUYER_PHONE_PASSWORD = SF_SHOP_VI_PASSWORD;
	String BUYER_PHONE_COUNTRY = ADMIN_COUNTRY_TIEN;
	String BUYER_FORGOT_MAIL_USERNAME = SF_USERNAME_VI_1;
	String BUYER_FORGOT_MAIL_PASSWORD = SF_SHOP_VI_PASSWORD;
	String BUYER_FORGOT_MAIL_COUNTRY = ADMIN_COUNTRY_TIEN;
	String BUYER_FORGOT_PHONE_USERNAME = SF_USERNAME_PHONE_VI_1;
	String BUYER_FORGOT_PHONE_PASSWORD = SF_SHOP_VI_PASSWORD;
	String BUYER_FORGOT_PHONE_COUNTRY = ADMIN_COUNTRY_TIEN;
	
	String GOMUA_MAIL_USERNAME = GOMUA_USERNAME_EMAIL;
	String GOMUA_MAIL_PASSWORD = GOMUA_PASSWORD_EMAIL;
	String GOMUA_MAIL_COUNTRY = ADMIN_COUNTRY_TIEN;
	String GOMUA_PHONE_USERNAME = GOMUA_USERNAME_PHONE;
	String GOMUA_PHONE_PASSWORD = ADMIN_PASSWORD_TIEN;
	String GOMUA_PHONE_COUNTRY = ADMIN_COUNTRY_TIEN;
	
	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey) {
		String translatedText = null;
		try {
			translatedText = PropertiesUtil.getPropertiesValueBySFLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedText;
	}		
	
	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		headerPage = new HeaderSF(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}

//	@Test
	public void LoginSF_01_CheckTranslation() throws Exception {
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language).clickUserInfoIcon().clickLoginIcon();
        loginPage.verifyTextAtLoginScreen(language);
        
        loginPage.navigate(SF_URL_TIEN);
        headerPage.clickUserInfoIcon().clickUserInfoIcon().clickLoginIcon();
        loginPage.clickForgotPassword().verifyTextAtForgotPasswordScreen(language);
	}		
	
	@Test
	public void LoginSF_03_LoginWithAllFieldsLeftBlank() {
		
		String emptyUsernameError = translateText("login.error.emptyUsername");
		String emptyPasswordError = translateText("login.error.emptyPassword");
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		//Empty username
		loginPage.performLogin("", generate.generateNumber(9));
		Assert.assertEquals(loginPage.getUsernameError(), emptyUsernameError);
		commonAction.refreshPage();
		
		//Empty password
		loginPage.performLogin(generate.generateNumber(9), "");
		Assert.assertEquals(loginPage.getPasswordError(), emptyPasswordError);
		commonAction.refreshPage();
		
		//Empty username and password
		loginPage.performLogin("", "");
		Assert.assertEquals(loginPage.getUsernameError(), emptyUsernameError);
		Assert.assertEquals(loginPage.getPasswordError(), emptyPasswordError);
	}

	@Test
	public void LoginSF_04_LoginWithInvalidPhoneFormat() {
		
		String invalidUsernameError = translateText("login.error.invalidUsername");
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		//7-digit phone number
		loginPage.performLogin(generate.generateNumber(7), generate.generateString(10));
		Assert.assertEquals(loginPage.getUsernameError(), invalidUsernameError);
		commonAction.refreshPage();
		
		//16-digit phone number
		loginPage.performLogin(generate.generateNumber(16), generate.generateString(10));
		Assert.assertEquals(loginPage.getUsernameError(), invalidUsernameError);
	}

	@Test
	public void LoginSF_05_LoginWithInvalidMailFormat() {
		
		String invalidUsernameError = translateText("login.error.invalidUsername");
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		loginPage.performLogin(generate.generateString(10), generate.generateString(10));
		Assert.assertEquals(loginPage.getUsernameError(), invalidUsernameError);
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

		String wrongCredentialsError = translateText("login.error.wrongCredentials");
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		loginPage.performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10));
		Assert.assertEquals(loginPage.getLoginFailError(), wrongCredentialsError);
		commonAction.refreshPage();
		
		loginPage.performLogin(generate.generateNumber(13), generate.generateString(10));
		Assert.assertEquals(loginPage.getLoginFailError(), wrongCredentialsError);
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
	public void BH_1282_LoginWithCorrectAccount() {

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
	public void BH_1595_LoginWithExistingGomuaAccount() {

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
	public void BH_1285_ForgotMailPassword() {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.selectCountry(country).inputUsername(username).clickContinueBtn().inputPassword(newPassword)
		.inputVerificationCode(new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? generate.getPhoneCode(country)+":"+username : username, "resetKey")).clickConfirmBtn();
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

//	@Test
	public void BH_1286_ForgotPhonePassword() {
		String country = BUYER_FORGOT_PHONE_COUNTRY;
		String username = BUYER_FORGOT_PHONE_USERNAME;
		String password = BUYER_FORGOT_PHONE_PASSWORD;
		String newPassword = password + "@" + generate.generateNumber(3);

		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickForgotPassword()
		.selectCountry(country).inputUsername(username).clickContinueBtn().inputPassword(newPassword)
		.inputVerificationCode(new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? generate.getPhoneCode(country)+":"+username : username, "resetKey")).clickConfirmBtn();
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
	public void LoginSF_10_ForgotPasswordForNonExistingAccount() {
		
		String mailNotExistError = translateText("forgotPassword.error.notExistingEmail");
		String phoneNotExistError = translateText("forgotPassword.error.notExistingPhone");
		
		String nonExistingMailAccount = generate.generateString(10) + "@nbobd.com";
		String nonExistingPhoneAccount = generate.generateNumber(13);
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		headerPage.clickUserInfoIcon().clickLoginIcon();
		Assert.assertEquals(loginPage.clickForgotPassword().inputUsername(nonExistingMailAccount).clickContinueBtn().getUsernameError(), mailNotExistError);
		
		commonAction.refreshPage();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		Assert.assertEquals(loginPage.clickForgotPassword().inputUsername(nonExistingPhoneAccount).clickContinueBtn().getUsernameError(), phoneNotExistError);
	}

	@Test
	public void BH_4593_ChangePasswordWithInvalidData() {
		
		String emptyPasswordError = translateText("login.error.emptyPassword");
		String wrongCurrentPasswordError = translateText("forgotPassword.error.wrongCurrentPassword");
		String invalidNewPasswordError = translateText("forgotPassword.error.invalidNewPassword");
		
		String newPassword = BUYER_MAIL_PASSWORD + "@" + generate.generateNumber(3);
		
		// Login
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		// Empty current password
		ChangePasswordDialog changePasswordDlg = headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword("").inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), wrongCurrentPasswordError);
		changePasswordDlg.clickCloseBtn();
		
		// Empty new password
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword("").clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), emptyPasswordError);
		changePasswordDlg.clickCloseBtn();
		
		// Incorrect current password
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD + "abc").inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), wrongCurrentPasswordError);
		changePasswordDlg.clickCloseBtn();

		// Inadequate number of characters
		newPassword = "asvn45$";
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), invalidNewPasswordError);
		changePasswordDlg.clickCloseBtn();
		
		// Absence of digits
		newPassword = "asvn$%^&";
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), invalidNewPasswordError);
		changePasswordDlg.clickCloseBtn();
		
		// Absence of special characters
		newPassword = "asvn4567";
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), invalidNewPasswordError);
		changePasswordDlg.clickCloseBtn();
	}
	

	//Already covered
	@Test
	public void BH_3813_ChangePassword() {
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
			
			headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(currentPassword).inputNewPassword(newPassword).clickDoneBtn();
		}
		
		// Logout then re-login with old password
		headerPage.clickUserInfoIcon().clickLogout();
		loginPage.performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	
	
	@Test
	public void BH_3814_ChangePasswordThatResemblesLast4Passwords() {
		
		String same4PasswordsError = translateText("forgotPassword.error.same4Passwords");
		
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
			ChangePasswordDialog changePasswordDlg = headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(newPassword).inputNewPassword(pw).clickDoneBtn();
			Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), same4PasswordsError);
			changePasswordDlg.clickCloseBtn();
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
