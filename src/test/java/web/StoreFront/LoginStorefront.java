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

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import utilities.account.AccountTest;
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

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
