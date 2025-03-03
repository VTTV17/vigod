package web.Dashboard;

import static utilities.account.AccountTest.ADMIN_COUNTRY_TIEN;
import static utilities.account.AccountTest.ADMIN_FACEBOOK_PASSWORD;
import static utilities.account.AccountTest.ADMIN_FACEBOOK_USERNAME;
import static utilities.account.AccountTest.ADMIN_PASSWORD_TIEN;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;
import static utilities.account.AccountTest.ADMIN_USERNAME_TIEN;
import static utilities.account.AccountTest.STAFF_VN_PASSWORD;
import static utilities.account.AccountTest.STAFF_VN_USERNAME;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import api.Seller.login.Login;
import api.Seller.setting.APIAccount;
import utilities.account.AccountTest;
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.ForgotPasswordPage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;

public class LoginDashboard extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;

	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver, Domain.valueOf(domain));
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
	}

	@Test
	public void LoginDB_02_LoginWithAllFieldsLeftBlank() {

		String emptyFieldError = LoginPage.localizedEmptyFieldError(DisplayLanguage.valueOf(language));
		
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));
		
		// Empty username
		String error = loginPage.performLogin("", DataGenerator.randomValidPassword()).getUsernameError();
		Assert.assertEquals(error, emptyFieldError);
		commonAction.refreshPage();

		// Empty password
		error = loginPage.performLogin(DataGenerator.randomPhone(), "").getPasswordError();
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

		String invalidPhoneError = LoginPage.localizedInvalidPhoneError(DisplayLanguage.valueOf(language));
		
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));
		
		// 7-digit phone number
		//https://mediastep.atlassian.net/browse/BH-29615
		String error = loginPage.performLogin(DataGenerator.generatePhoneFromRegex("\\d{7}"), DataGenerator.randomValidPassword()).getUsernameError();
		Assert.assertEquals(error, invalidPhoneError);
		commonAction.refreshPage();

		// 16-digit phone number
		error = loginPage.performLogin(DataGenerator.generatePhoneFromRegex("\\d{16}"), DataGenerator.randomValidPassword()).getUsernameError();
		Assert.assertEquals(error, invalidPhoneError);
	}

	@Test
	public void LoginDB_04_LoginWithInvalidMailFormat() {

		String invalidEmailError = LoginPage.localizedInvalidEmailError(DisplayLanguage.valueOf(language));
		
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));
		
		// Mail does not have symbol @
		String error = loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), DataGenerator.randomValidPassword()).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);
		commonAction.refreshPage();

		// Mail does not have suffix '.<>'. Eg. '.com'
		error = loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), DataGenerator.randomValidPassword()).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);
		commonAction.refreshPage();

		error = loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(),
				DataGenerator.randomValidPassword()).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);

	}

	@Test
	public void LoginDB_05_LoginWithNonExistingAccount() {

		String wrongCredentialsError = LoginPage.localizedWrongCredentialsError(DisplayLanguage.valueOf(language));
		
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));
		
		// Email account
		String error = loginPage.performLogin(DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword())
				.getLoginFailError();
		Assert.assertEquals(error, wrongCredentialsError);

		// Phone account
		error = loginPage.performLogin(DataGenerator.randomPhone(), DataGenerator.randomValidPassword()).getLoginFailError();
		Assert.assertEquals(error, wrongCredentialsError);
	}

	@Test
	public void LoginDB_06_LoginWithCorrectAccount() {

		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));

		String mailCountry, mailUsername, mailPassword;
		String phoneCountry, phoneUsername, phonePassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailCountry = ADMIN_COUNTRY_TIEN;
			mailUsername = ADMIN_USERNAME_TIEN;
			mailPassword = ADMIN_PASSWORD_TIEN;
			phoneCountry = ADMIN_COUNTRY_TIEN;
			phoneUsername = ADMIN_SHOP_VI_USERNAME;
			phonePassword = ADMIN_SHOP_VI_PASSWORD;
		} else {
			mailCountry = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			mailUsername = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			mailPassword = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
			phoneCountry = AccountTest.ADMIN_PHONE_BIZ_COUNTRY;
			phoneUsername = AccountTest.ADMIN_PHONE_BIZ_USERNAME;
			phonePassword = AccountTest.ADMIN_PHONE_BIZ_PASSWORD;			
		}
		
		// Email account
		loginPage.performLogin(mailCountry, mailUsername, mailPassword);
		homePage.verifyPageLoaded().clickLogout();

		// Phone account
		//TODO create a phone account for domain .biz
		if (Domain.valueOf(domain).equals(Domain.VN) ) {
			loginPage.performLogin(phoneCountry, phoneUsername, phonePassword);
			homePage.verifyPageLoaded().clickLogout();
		}

	}
	
	//Captcha verification is now needed for FB login attempts done on automated browsers
//	@Test
	public void LoginDB_07_LoginWithFacebook() {
		
		String mailUsername, mailPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailUsername = ADMIN_FACEBOOK_USERNAME;
			mailPassword = ADMIN_FACEBOOK_PASSWORD;
		} else {
			mailUsername = AccountTest.ADMIN_FACEBOOK_BIZ_USERNAME;
			mailPassword = AccountTest.ADMIN_FACEBOOK_BIZ_PASSWORD;
		}
		
		loginPage.navigate().performLoginWithFacebook(mailUsername, mailPassword);
		homePage.verifyPageLoaded().clickLogout();
	}

	@Test
	public void LoginDB_08_StaffLogin() {

		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));

		String mailUsername, mailPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailUsername = STAFF_VN_USERNAME;
			mailPassword = STAFF_VN_PASSWORD;
		} else {
			mailUsername = AccountTest.STAFF_BIZ_USERNAME;
			mailPassword = AccountTest.STAFF_BIZ_PASSWORD;
		}
		
		//Wrong credentials
		String error = loginPage.switchToStaffTab().performLogin(mailUsername, DataGenerator.randomValidPassword()).getLoginFailError();
		Assert.assertEquals(error, LoginPage.localizedWrongCredentialsError(DisplayLanguage.valueOf(language)));

		//Correct credentials
		loginPage.switchToStaffTab().performLogin(mailUsername, mailPassword);
		homePage.clickLogout();
	}

	//Temporarily commented out because captcha needs to be solved
//	@Test
	public void LoginDB_09_StaffForgotPassword() {
		
		String invalidPasswordError = ForgotPasswordPage.localizedInvalidPasswordError(DisplayLanguage.valueOf(language));
		
		String mailUsername, mailPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailUsername = STAFF_VN_USERNAME;
			mailPassword = STAFF_VN_PASSWORD;
		} else {
			mailUsername = AccountTest.STAFF_BIZ_USERNAME;
			mailPassword = AccountTest.STAFF_BIZ_PASSWORD;
		}
		
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));

		ForgotPasswordPage forgotPasswordPage = loginPage.switchToStaffTab().clickForgotPassword();
		
		// Inadequate number of characters
		forgotPasswordPage.inputUsername(mailUsername).inputPassword(new Generex("[a-z]{3}\\d{3}[!#@]").random()).clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		// Absence of numbers
		forgotPasswordPage.inputPassword(new Generex("[a-z]{8}[!#@]").random()).clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		// Absence of letters
		forgotPasswordPage.inputPassword(new Generex("\\d{8}[!#@]").random()).clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		// Absence of special characters
		forgotPasswordPage.inputPassword(new Generex("[a-z]{5,8}\\d{5,8}").random()).clickContinueBtn();
		Assert.assertEquals(forgotPasswordPage.getPasswordError(), invalidPasswordError);

		forgotPasswordPage.inputPassword(mailPassword).clickContinueBtn();
		String code = new KibanaAPI().getKeyFromKibana(mailUsername, "resetKey");
		
		// Incorrect verification code
		forgotPasswordPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1)).clickConfirmBtn();
		Assert.assertEquals(forgotPasswordPage.getVerificationCodeError(), ForgotPasswordPage.localizedWrongVerificationCodeError(DisplayLanguage.valueOf(language)));

		// Correct verification code
		forgotPasswordPage.inputVerificationCode(code).clickConfirmBtn();
		homePage.waitTillSpinnerDisappear1().clickLogout();

		// Re-login with new password
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));
		loginPage.switchToStaffTab().performLogin(mailUsername, mailPassword);
		homePage.waitTillSpinnerDisappear1().clickLogout();
	}

	@Test
	public void LoginDB_10_SellersChangePassword() {

		String country, username, password;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			country = ADMIN_COUNTRY_TIEN;
			username = ADMIN_USERNAME_TIEN;
			password = ADMIN_PASSWORD_TIEN;
		} else {
			country = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			username = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			password = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
		}
		
		String newPassword = DataGenerator.randomValidPassword();

		// Login
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performLogin(country, username, password);
		homePage.verifyPageLoaded();

		// Change password
		new AccountPage(driver, Domain.valueOf(domain)).navigateByURL().changePassword(password, newPassword, newPassword);
		homePage.getToastMessage();
		new ConfirmationDialog(driver).clickOKBtn();

		// Re-login
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performLogin(country, username, newPassword);
		homePage.verifyPageLoaded();

		// Change password back to the first password
		String currentPassword = "";
		for (int i = 0; i < 5; i++) {
			currentPassword = newPassword;

			LoginInformation ownerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, currentPassword).getLoginInformation();
			
			newPassword = (i == 4) ? password : DataGenerator.randomValidPassword();

			new APIAccount(ownerCredentials).changePassword(currentPassword, newPassword);
		}
	}
	
	//Temporarily commented out because captcha needs to be solved
//	@Test
	public void LoginDB_11_SellerForgotPassword()  {

		String country, username, password;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			country = ADMIN_COUNTRY_TIEN;
			username = ADMIN_USERNAME_TIEN;
			password = ADMIN_PASSWORD_TIEN;
		} else {
			country = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			username = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			password = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
		}
		
		String newPassword = DataGenerator.randomValidPassword();

		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language));
		ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPassword();
		
		forgotPasswordPage.selectCountry(country).inputUsername(username).inputPassword(newPassword).clickContinueBtn();

		String code = new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? DataGenerator.getPhoneCode(country)+":"+username : username, "resetKey");
		
		forgotPasswordPage.inputVerificationCode(code).clickConfirmBtn();

		// Logout
		homePage.waitTillSpinnerDisappear1().waitTillLoadingDotsDisappear().clickLogout();

		// Re-login with new password
		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performLogin(country, username, newPassword);

		// Change password back to the first password
		String currentPassword = "";
		for (int i = 0; i < 5; i++) {
			currentPassword = newPassword;
			
			LoginInformation ownerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, currentPassword).getLoginInformation();

			newPassword = (i == 4) ? password : DataGenerator.randomValidPassword();

			new APIAccount(ownerCredentials).changePassword(currentPassword, newPassword);
		}
	}

	@AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}
}
