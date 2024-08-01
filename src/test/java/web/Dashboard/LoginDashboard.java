package web.Dashboard;

import static utilities.account.AccountTest.ADMIN_COUNTRY_TIEN;
import static utilities.account.AccountTest.ADMIN_FACEBOOK_PASSWORD;
import static utilities.account.AccountTest.ADMIN_FACEBOOK_USERNAME;
import static utilities.account.AccountTest.ADMIN_FORGOTPASSWORD_COUNTRY_MAIL;
import static utilities.account.AccountTest.ADMIN_FORGOTPASSWORD_COUNTRY_PHONE;
import static utilities.account.AccountTest.ADMIN_FORGOTPASSWORD_PASSWORD_MAIL;
import static utilities.account.AccountTest.ADMIN_FORGOTPASSWORD_PASSWORD_PHONE;
import static utilities.account.AccountTest.ADMIN_FORGOTPASSWORD_USERNAME_MAIL;
import static utilities.account.AccountTest.ADMIN_FORGOTPASSWORD_USERNAME_PHONE;
import static utilities.account.AccountTest.ADMIN_PASSWORD_TIEN;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;
import static utilities.account.AccountTest.ADMIN_USERNAME_TIEN;
import static utilities.account.AccountTest.STAFF_PASSWORD_TIEN;
import static utilities.account.AccountTest.STAFF_USERNAME_TIEN;

import java.io.IOException;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.APIAccount;
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.ForgotPasswordPage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;

public class LoginDashboard extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;

	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey) {
		String translatedText = null;
		try {
			translatedText = PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedText;
	}	
	
	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}

//    @Test
	public void LoginDB_01_CheckTranslation() throws Exception {
		loginPage.navigate().selectDisplayLanguage(language).verifyTextAtLoginScreen();

//		loginPage.clickForgotPassword().verifyTextAtForgotPasswordScreen();
	}

	@Test
	public void LoginDB_02_LoginWithAllFieldsLeftBlank() {

		String emptyFieldError = translateText("services.create.inputFieldEmptyError");
		
		loginPage.navigate().selectDisplayLanguage(language);
		
		// Empty username
		String error = loginPage.performLogin("", generate.generateNumber(9)).getUsernameError();
		Assert.assertEquals(error, emptyFieldError);
		commonAction.refreshPage();

		// Empty password
		error = loginPage.performLogin(generate.generateNumber(9), "").getPasswordError();
		Assert.assertEquals(error, emptyFieldError);
		commonAction.refreshPage();

		// Empty username and password
		error = loginPage.performLogin("", "").getPasswordError();
		Assert.assertEquals(error, emptyFieldError);
		error = loginPage.getUsernameError();
		Assert.assertEquals(error, emptyFieldError);
	}

	@Test
	public void LoginDB_03_LoginWithInvalidPhoneFormat() {

		String invalidPhoneError = translateText("login.screen.error.invalidPhone");
		
		loginPage.navigate().selectDisplayLanguage(language);

		// 7-digit phone number
		//https://mediastep.atlassian.net/browse/BH-29615
		String error = loginPage.performLogin(generate.generateNumber(7), generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, invalidPhoneError);
		commonAction.refreshPage();

		// 16-digit phone number
		error = loginPage.performLogin(generate.generateNumber(16), generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, invalidPhoneError);
	}

	@Test
	public void LoginDB_04_LoginWithInvalidMailFormat() {

		String invalidEmailError = translateText("login.screen.error.invalidMail");
		
		loginPage.navigate().selectDisplayLanguage(language);

		// Mail does not have symbol @
		String error = loginPage.performLogin(generate.generateString(10), generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);
		commonAction.refreshPage();

		// Mail does not have suffix '.<>'. Eg. '.com'
		error = loginPage.performLogin(generate.generateString(10) + "@", generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);
		commonAction.refreshPage();

		error = loginPage.performLogin(generate.generateString(10) + "@" + generate.generateString(5) + ".",
				generate.generateString(10)).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);

	}

	@Test
	public void LoginDB_05_LoginWithNonExistingAccount() {

		String wrongCredentialsError = translateText("login.screen.error.wrongCredentials");
		
		loginPage.navigate().selectDisplayLanguage(language);

		// Email account
		String error = loginPage.performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10))
				.getLoginFailError();
		Assert.assertEquals(error, wrongCredentialsError);

		// Phone account
		error = loginPage.performLogin(generate.generateNumber(13), generate.generateString(10)).getLoginFailError();
		Assert.assertEquals(error, wrongCredentialsError);
	}

	@Test
	public void LoginDB_06_LoginWithCorrectAccount() {

		loginPage.navigate().selectDisplayLanguage(language);

		// Email account
		loginPage.performLogin(ADMIN_COUNTRY_TIEN, ADMIN_USERNAME_TIEN, ADMIN_PASSWORD_TIEN);
		homePage.clickLogout();

		// Phone account
		loginPage.performLogin(ADMIN_COUNTRY_TIEN, ADMIN_SHOP_VI_USERNAME, ADMIN_SHOP_VI_PASSWORD);
		homePage.clickLogout();
	}

    @Test
	public void LoginDB_07_LoginWithFacebook() {
		loginPage.navigate().performLoginWithFacebook(ADMIN_FACEBOOK_USERNAME, ADMIN_FACEBOOK_PASSWORD);
		homePage.waitTillSpinnerDisappear1().clickLogout();
	}

	@Test
	public void LoginDB_08_StaffLogin() {

		loginPage.navigate().selectDisplayLanguage(language);

		// Wrong credentials.
		String error = loginPage.switchToStaffTab().performLogin(STAFF_USERNAME_TIEN, generate.generateString(10)).getLoginFailError();
		Assert.assertEquals(error, translateText("login.screen.error.wrongCredentials"));

		// Correct credentials.
		loginPage.switchToStaffTab().performLogin(STAFF_USERNAME_TIEN, STAFF_PASSWORD_TIEN);
		homePage.clickLogout();
	}

	@Test
	public void LoginDB_09_StaffForgotPassword() {
		
		String invalidPasswordError = translateText("login.forgotPassword.error.invalidPassword");
		String staff = "emcehc@mailnesia.com";
		String newPassword = STAFF_PASSWORD_TIEN + generate.generateNumber(4) + "!";
		
		loginPage.navigate().selectDisplayLanguage(language);

		ForgotPasswordPage forgotPasswordPage = loginPage.switchToStaffTab().clickForgotPassword();
		
		// Inadequate number of characters
		forgotPasswordPage.inputUsername(staff).inputPassword("fortt!1").clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		// Absence of numbers
		forgotPasswordPage.inputPassword("fortesting!").clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		// Absence of letters
		forgotPasswordPage.inputPassword("12345678!").clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		// Absence of special characters
		forgotPasswordPage.inputPassword("fortesting1").clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		forgotPasswordPage.inputPassword(newPassword).clickContinueBtn();
		String code = new KibanaAPI().getKeyFromKibana(staff, "resetKey");
		
		// Incorrect verification code
		forgotPasswordPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1)).clickConfirmBtn().getVerificationCodeError();
		Assert.assertEquals(forgotPasswordPage.getVerificationCodeError(), translateText("login.screen.error.wrongVerificationCode"));

		// Correct verification code
		forgotPasswordPage.inputVerificationCode(code).clickConfirmBtn();
		homePage.waitTillSpinnerDisappear1().clickLogout();

		// Re-login with new password
		loginPage.navigate().switchToStaffTab().performLogin(staff, newPassword);
		homePage.waitTillSpinnerDisappear1().clickLogout();
	}

	@DataProvider
	public Object[][] adminForgetPassword() {
		return new Object[][] { 
			{ ADMIN_FORGOTPASSWORD_COUNTRY_MAIL, ADMIN_FORGOTPASSWORD_USERNAME_MAIL, ADMIN_FORGOTPASSWORD_PASSWORD_MAIL },
			{ ADMIN_FORGOTPASSWORD_COUNTRY_PHONE, ADMIN_FORGOTPASSWORD_USERNAME_PHONE, ADMIN_FORGOTPASSWORD_PASSWORD_PHONE }
		};
	}	
	@Test(dataProvider = "adminForgetPassword")
	public void LoginDB_10_SellersChangePassword(String country, String username, String password) {

		String newPassword = password + generate.generateNumber(3) + "!";

		// Login
		loginPage.navigate().performLogin(country, username, password);

		// Change password
		homePage.navigateToPage("Settings");
		new AccountPage(driver).navigate().changePassword(password, newPassword, newPassword);
		homePage.getToastMessage();
		new ConfirmationDialog(driver).clickOKBtn();

		// Re-login
		loginPage.navigate().performLogin(country, username, newPassword);

		// Change password back to the first password
		String currentPassword = "";
		for (int i = 0; i < 5; i++) {
			currentPassword = newPassword;

			LoginInformation ownerCredentials = new Login().setLoginInformation(generate.getPhoneCode(country), username, currentPassword).getLoginInformation();
			
			newPassword = (i != 4) ? password + generate.generateNumber(3) + "!" : password;

			new APIAccount(ownerCredentials).changePassword(currentPassword, newPassword);
		}
	}
	@Test(dataProvider = "adminCredentials")
	public void LoginDB_11_SellerForgotPassword(String country, String username, String password)  {

		String newPassword = password + generate.generateNumber(3) + "!";

		ForgotPasswordPage forgotPasswordPage = loginPage.navigate().clickForgotPassword();
		
		forgotPasswordPage.selectCountry(country).inputUsername(username).inputPassword(newPassword).clickContinueBtn();

		String code = new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? generate.getPhoneCode(country)+":"+username : username, "resetKey");
		
		forgotPasswordPage.inputVerificationCode(code).clickConfirmBtn();

		// Logout
		homePage.clickLogout();

		// Re-login with new password
		loginPage.navigate().performLogin(country, username, newPassword);

		// Change password back to the first password
		String currentPassword = "";
		for (int i = 0; i < 5; i++) {
			currentPassword = newPassword;
			
			LoginInformation ownerCredentials = new Login().setLoginInformation(generate.getPhoneCode(country), username, currentPassword).getLoginInformation();

			newPassword = (i != 4) ? password + generate.generateNumber(3) + "!" : password;

			new APIAccount(ownerCredentials).changePassword(currentPassword, newPassword);
		}
	}

	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		driver.quit();
	}

}
