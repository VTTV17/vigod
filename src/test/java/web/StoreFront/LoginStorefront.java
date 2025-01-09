package web.StoreFront;

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
import static utilities.links.Links.SF_URL_TIEN;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.thirdparty.Facebook;
import web.StoreFront.header.ChangePasswordDialog;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.ForgotPasswordDialog;
import web.StoreFront.login.LoginPage;

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
	
	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		headerPage = new HeaderSF(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}

//	@Test
	@Deprecated
	public void LoginSF_01_CheckTranslation() throws Exception {
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language).clickUserInfoIcon().clickLoginIcon();
        loginPage.verifyTextAtLoginScreen(language);
        
        loginPage.navigate(SF_URL_TIEN);
        headerPage.clickUserInfoIcon().clickUserInfoIcon().clickLoginIcon();
        loginPage.clickForgotPassword().verifyTextAtForgotPasswordScreen(language);
	}		
	
//	@Test
	@Deprecated
	public void BH_1335_UnableToChangePasswordForFacebookAccount() {
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().clickLoginIcon();
		loginPage.clickFacebookIcon();
		
		commonAction.switchToWindow(1);
		new Facebook(driver).performLogin(ADMIN_FACEBOOK_USERNAME, ADMIN_FACEBOOK_PASSWORD);
		commonAction.switchToWindow(0);
		
		UICommonAction.sleepInMiliSecond(5000);
		headerPage.clickUserInfoIcon();
		
		headerPage.clickUserInfoIcon();
		UICommonAction.sleepInMiliSecond(1000);
		
		Assert.assertFalse(headerPage.checkForPresenceOfChangePasswordLink());
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
		.inputVerificationCode(new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? DataGenerator.getPhoneCode(country)+":"+username : username, "resetKey")).clickConfirmBtn();
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
		.inputVerificationCode(new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? DataGenerator.getPhoneCode(country)+":"+username : username, "resetKey")).clickConfirmBtn();
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
		
		String nonExistingMailAccount = generate.generateString(10) + "@nbobd.com";
		String nonExistingPhoneAccount = generate.generateNumber(13);
		
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		headerPage.clickUserInfoIcon().clickLoginIcon();
		Assert.assertEquals(loginPage.clickForgotPassword().inputUsername(nonExistingMailAccount).clickContinueBtn().getUsernameError(), ForgotPasswordDialog.localizedEmailNotExistError(DisplayLanguage.valueOf(language)));
		
		commonAction.refreshPage();
		headerPage.clickUserInfoIcon().clickLoginIcon();
		Assert.assertEquals(loginPage.clickForgotPassword().inputUsername(nonExistingPhoneAccount).clickContinueBtn().getUsernameError(), ForgotPasswordDialog.localizedPhoneNotExistError(DisplayLanguage.valueOf(language)));
	}

	@Test
	public void BH_4593_ChangePasswordWithInvalidData() {
		
		String newPassword = BUYER_MAIL_PASSWORD + "@" + generate.generateNumber(3);
		
		// Login
		loginPage.navigate(SF_URL_TIEN);
		headerPage.clickUserInfoIcon().changeLanguage(language);
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD);
		
		// Empty current password
		ChangePasswordDialog changePasswordDlg = headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword("").inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), ForgotPasswordDialog.localizedWrongCurrentPasswordError(DisplayLanguage.valueOf(language)));
		changePasswordDlg.clickCloseBtn();
		
		// Empty new password
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword("").clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), LoginPage.localizedEmptyPasswordError(DisplayLanguage.valueOf(language)));
		changePasswordDlg.clickCloseBtn();
		
		// Incorrect current password
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD + "abc").inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), ForgotPasswordDialog.localizedWrongCurrentPasswordError(DisplayLanguage.valueOf(language)));
		changePasswordDlg.clickCloseBtn();

		// Inadequate number of characters
		newPassword = "asvn45$";
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), ForgotPasswordDialog.localizedInvalidNewPasswordError(DisplayLanguage.valueOf(language)));
		changePasswordDlg.clickCloseBtn();
		
		// Absence of digits
		newPassword = "asvn$%^&";
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), ForgotPasswordDialog.localizedInvalidNewPasswordError(DisplayLanguage.valueOf(language)));
		changePasswordDlg.clickCloseBtn();
		
		// Absence of special characters
		newPassword = "asvn4567";
		headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(BUYER_MAIL_PASSWORD).inputNewPassword(newPassword).clickDoneBtn();
		Assert.assertEquals(changePasswordDlg.getNewPasswordError(), ForgotPasswordDialog.localizedInvalidNewPasswordError(DisplayLanguage.valueOf(language)));
		changePasswordDlg.clickCloseBtn();
	}
	

	//Already covered
	@Test
	public void BH_3813_ChangePassword() {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		String newPassword = new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random();
		
		// Login
		loginPage.navigate(SF_URL_TIEN).performLogin(country, username, password);
		
		// Change password back to the first password
		String currentPassword = "";
		for (int i=0; i<5; i++) {
			
			currentPassword = (i==0) ? password : newPassword;
			
			newPassword = (i!=4) ? new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random() : password;
			
			headerPage.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(currentPassword).inputNewPassword(newPassword).clickDoneBtn();
		}
		
		// Logout then re-login with old password
		headerPage.clickUserInfoIcon().clickLogout();
		loginPage.performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	
	
	@Test
	public void BH_3814_ChangePasswordThatResemblesLast4Passwords() {
		
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
			Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), ForgotPasswordDialog.localizedSame4PasswordsError(DisplayLanguage.valueOf(language)));
			changePasswordDlg.clickCloseBtn();
		}
		
		// Logout then re-login with old password
		headerPage.clickUserInfoIcon().clickLogout();
		loginPage.performLogin(country, username, newPassword);
		headerPage.clickUserInfoIcon().clickLogout();
	}	


    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
