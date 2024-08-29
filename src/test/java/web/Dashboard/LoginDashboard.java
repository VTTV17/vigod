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
import static utilities.account.AccountTest.STAFF_VN_PASSWORD;
import static utilities.account.AccountTest.STAFF_VN_USERNAME;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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
		try {
			return PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}	
	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey, DisplayLanguage lang) {
		try {
			return PropertiesUtil.getPropertiesValueByDBLang(propertyKey, lang.name());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}	
	
	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}

	void navigateToPage(Domain domain) {
		switch (domain) {
			case VN -> loginPage.navigate().selectDisplayLanguage(language);
			case BIZ -> loginPage.navigateBiz();
			default -> throw new IllegalArgumentException("Unexpected value: " + domain);
		}
	}	
	
//    @Test
	public void LoginDB_01_CheckTranslation() throws Exception {
		loginPage.navigate().selectDisplayLanguage(language).verifyTextAtLoginScreen();

//		loginPage.clickForgotPassword().verifyTextAtForgotPasswordScreen();
	}

	@Test
	public void LoginDB_02_LoginWithAllFieldsLeftBlank() {

		String emptyFieldError = translateText("services.create.inputFieldEmptyError", DisplayLanguage.valueOf(language));
		
		navigateToPage(Domain.valueOf(domain));
		
		// Empty username
		String error = loginPage.performLogin("", new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getUsernameError();
		Assert.assertEquals(error, emptyFieldError);
		commonAction.refreshPage();

		// Empty password
		error = loginPage.performLogin(new Generex("\\d{9}").random(), "").getPasswordError();
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

		String invalidPhoneError = translateText("login.screen.error.invalidPhone", DisplayLanguage.valueOf(language));
		
		navigateToPage(Domain.valueOf(domain));
		
		// 7-digit phone number
		//https://mediastep.atlassian.net/browse/BH-29615
		String error = loginPage.performLogin(new Generex("\\d{7}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getUsernameError();
		Assert.assertEquals(error, invalidPhoneError);
		commonAction.refreshPage();

		// 16-digit phone number
		error = loginPage.performLogin(new Generex("\\d{16}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getUsernameError();
		Assert.assertEquals(error, invalidPhoneError);
	}

	@Test
	public void LoginDB_04_LoginWithInvalidMailFormat() {

		String invalidEmailError = translateText("login.screen.error.invalidMail", DisplayLanguage.valueOf(language));
		
		navigateToPage(Domain.valueOf(domain));
		
		// Mail does not have symbol @
		String error = loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);
		commonAction.refreshPage();

		// Mail does not have suffix '.<>'. Eg. '.com'
		error = loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);
		commonAction.refreshPage();

		error = loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(),
				new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getUsernameError();
		Assert.assertEquals(error, invalidEmailError);

	}

	@Test
	public void LoginDB_05_LoginWithNonExistingAccount() {

		String wrongCredentialsError = translateText("login.screen.error.wrongCredentials", DisplayLanguage.valueOf(language));
		
		navigateToPage(Domain.valueOf(domain));
		
		// Email account
		String error = loginPage.performLogin(new Generex("[a-z]{5}\\d{5}\\@[a-z]mail\\.[a-z]{2,3}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random())
				.getLoginFailError();
		Assert.assertEquals(error, wrongCredentialsError);

		// Phone account
		error = loginPage.performLogin(new Generex("\\d{8,15}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getLoginFailError();
		Assert.assertEquals(error, wrongCredentialsError);
	}

	@Test
	public void LoginDB_06_LoginWithCorrectAccount() {

		navigateToPage(Domain.valueOf(domain));

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
		homePage.clickLogout();

		// Phone account
		loginPage.performLogin(phoneCountry, phoneUsername, phonePassword);
		homePage.clickLogout();
	}

//	@Test
//	Not updated yet
	public void LoginDB_07_LoginWithFacebook() {
		loginPage.navigate().performLoginWithFacebook(ADMIN_FACEBOOK_USERNAME, ADMIN_FACEBOOK_PASSWORD);
		homePage.waitTillSpinnerDisappear1().clickLogout();
	}

	@Test
	public void LoginDB_08_StaffLogin() {

		navigateToPage(Domain.valueOf(domain));

		String mailUsername, mailPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailUsername = STAFF_VN_USERNAME;
			mailPassword = STAFF_VN_PASSWORD;
		} else {
			mailUsername = AccountTest.STAFF_BIZ_USERNAME;
			mailPassword = AccountTest.STAFF_BIZ_PASSWORD;
		}
		
		//Wrong credentials
		String error = loginPage.switchToStaffTab().performLogin(mailUsername, new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random()).getLoginFailError();
		Assert.assertEquals(error, translateText("login.screen.error.wrongCredentials", DisplayLanguage.valueOf(language)));

		//Correct credentials
		loginPage.switchToStaffTab().performLogin(mailUsername, mailPassword);
		homePage.clickLogout();
	}

	@Test
	public void LoginDB_09_StaffForgotPassword() {
		
		String invalidPasswordError = translateText("login.forgotPassword.error.invalidPassword", DisplayLanguage.valueOf(language));
		
		String mailUsername, mailPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailUsername = STAFF_VN_USERNAME;
			mailPassword = STAFF_VN_PASSWORD;
		} else {
			mailUsername = AccountTest.STAFF_BIZ_USERNAME;
			mailPassword = AccountTest.STAFF_BIZ_PASSWORD;
		}
		
		navigateToPage(Domain.valueOf(domain));

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
		Assert.assertEquals(forgotPasswordPage.getVerificationCodeError(), translateText("login.screen.error.wrongVerificationCode", DisplayLanguage.valueOf(language)));

		// Correct verification code
		forgotPasswordPage.inputVerificationCode(code).clickConfirmBtn();
		homePage.waitTillSpinnerDisappear1().clickLogout();

		// Re-login with new password
		navigateToPage(Domain.valueOf(domain));
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
		
		String newPassword = new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random();

		// Login
		navigateToPage(Domain.valueOf(domain));
		loginPage.performLogin(country, username, password);
		homePage.verifyPageLoaded();

		// Change password
		new AccountPage(driver, Domain.valueOf(domain)).navigateByURL().changePassword(password, newPassword, newPassword);
		homePage.getToastMessage();
		new ConfirmationDialog(driver).clickOKBtn();

		// Re-login
		navigateToPage(Domain.valueOf(domain));
		loginPage.performLogin(country, username, newPassword);
		homePage.verifyPageLoaded();

		// Change password back to the first password
		String currentPassword = "";
		for (int i = 0; i < 5; i++) {
			currentPassword = newPassword;

			LoginInformation ownerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, currentPassword).getLoginInformation();
			
			newPassword = (i != 4) ? new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random() : password;

			new APIAccount(ownerCredentials).changePassword(currentPassword, newPassword);
		}
	}
	@Test
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
		
		String newPassword = new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random();

		navigateToPage(Domain.valueOf(domain));
		ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPassword();
		
		forgotPasswordPage.selectCountry(country).inputUsername(username).inputPassword(newPassword).clickContinueBtn();

		String code = new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? DataGenerator.getPhoneCode(country)+":"+username : username, "resetKey");
		
		forgotPasswordPage.inputVerificationCode(code).clickConfirmBtn();

		// Logout
		homePage.waitTillSpinnerDisappear1().waitTillLoadingDotsDisappear().clickLogout();

		// Re-login with new password
		navigateToPage(Domain.valueOf(domain));
		loginPage.performLogin(country, username, newPassword);

		// Change password back to the first password
		String currentPassword = "";
		for (int i = 0; i < 5; i++) {
			currentPassword = newPassword;
			
			LoginInformation ownerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, currentPassword).getLoginInformation();

			newPassword = (i != 4) ? new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random() : password;

			new APIAccount(ownerCredentials).changePassword(currentPassword, newPassword);
		}
	}

	@AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}
}
