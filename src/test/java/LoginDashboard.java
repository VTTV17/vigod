import java.io.IOException;
import java.sql.SQLException;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.settings.account.AccountPage;
import pages.dashboard.signup.SignupPage;
import pages.thirdparty.Mailnesia;
import utilities.UICommonAction;
import utilities.jsonFileUtility;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;

public class LoginDashboard extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;

	JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String MAIL = data.findValue("seller").findValue("mail").findValue("username").asText();
	String PASSWORD = data.findValue("seller").findValue("mail").findValue("password").asText();
	String PHONE = "0707159324";
	String PHONE_PASSWORD = "Psso124@";
	String PHONE_COUNTRY = "Vietnam";
	String PHONE_COUNTRYCODE = data.findValue("seller").findValue("phone").findValue("countryCode").asText();
	String FACEBOOK = data.findValue("seller").findValue("facebook").findValue("username").asText();
	String FACEBOOK_PASSWORD = data.findValue("seller").findValue("facebook").findValue("password").asText();
	String STAFF = data.findValue("staff").findValue("mail").findValue("username").asText();
	String STAFF_PASSWORD = data.findValue("staff").findValue("mail").findValue("password").asText();
	String SELLER_FORGOT_MAIL_USERNAME = data.findValue("seller").findValue("forgotMail").findValue("username").asText();
	String SELLER_FORGOT_MAIL_PASSWORD = data.findValue("seller").findValue("forgotMail").findValue("password").asText();
	String SELLER_FORGOT_MAIL_COUNTRY = data.findValue("seller").findValue("forgotMail").findValue("country").asText();
	String SELLER_FORGOT_PHONE_USERNAME = data.findValue("seller").findValue("forgotPhone").findValue("username").asText();
	String SELLER_FORGOT_PHONE_PASSWORD = data.findValue("seller").findValue("forgotPhone").findValue("password").asText();
	String SELLER_FORGOT_PHONE_COUNTRY = data.findValue("seller").findValue("forgotPhone").findValue("country").asText();
	
	String BLANK_ERROR = data.findValue("emptyError").asText();
	String INVALID_MAIL_ERROR = data.findValue("invalidMailFormat").asText();
	String INVALID_PHONE_ERROR = data.findValue("invalidPhoneFormat").asText();
	String INVALID_CREDENTIALS_ERROR = data.findValue("invalidCredentials").asText();
	String INVALID_PASSWORD_FORMAT_ERROR_VI = "Mật khẩu phải dài ít nhất 8 ký tự và có ít nhất 1 chữ, 1 số và 1 ký tự đặc biệt";
	String INVALID_PASSWORD_FORMAT_ERROR_EN = "Your password must have at least 8 characters with at least 1 letter, 1 number and 1 special character";
	
	String INVALID_CODE_ERROR_VI = "Mã xác thực không đúng!";
	String INVALID_CODE_ERROR_EN = "Incorrect confirmation code!";

	public String getVerificationCode(String username) throws SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			verificationCode = new Mailnesia(driver).navigateToMailAndGetVerifyCode(username);
		} else {
			verificationCode = new InitConnection().getResetKey(loginPage.countryCode + ":" + username);
		}
		return verificationCode;
	}

	public void verifyChangePasswordError() throws InterruptedException {
		String message;
		if (loginPage.getSelectedLanguage().contentEquals("ENG")) {
			message = INVALID_PASSWORD_FORMAT_ERROR_EN;
		} else {
			message = INVALID_PASSWORD_FORMAT_ERROR_VI;
		}
		loginPage.verifyPasswordError(message).completeVerify();
	}

	public void verifyConfirmationCodeError() throws Exception {
		String message;
		if (language.contentEquals("ENG")) {
			message = INVALID_CODE_ERROR_EN;
		} else {
			message = INVALID_CODE_ERROR_VI;
		}
		new SignupPage(driver).verifyVerificationCodeError(message).completeVerify();
	}

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}	
	
	@BeforeMethod
	public void setup() {
		instantiatePageObjects();
	}
	
    @Test
	public void LoginDB_01_CheckTranslation() throws Exception {
		loginPage.navigate()
		.selectDisplayLanguage(language)
		.verifyTextAtLoginScreen();
		
		loginPage.clickForgotPassword()
		.verifyTextAtForgotPasswordScreen();
	}
    
    @Test
	public void LoginDB_02_LoginWithAllFieldsLeftBlank() throws Exception {
		// Username field is left empty.
		loginPage.navigate()
		.performLogin("", generate.generateNumber(9))
		.verifyEmailOrPhoneNumberError(BLANK_ERROR).completeVerify();
		
		// Password field is left empty.
		loginPage.navigate().performLogin(generate.generateNumber(10), "").verifyPasswordError(BLANK_ERROR)
				.completeVerify();
		// All fields are left empty.
		loginPage.navigate().performLogin("", "").verifyEmailOrPhoneNumberError(BLANK_ERROR)
				.verifyPasswordError(BLANK_ERROR).completeVerify();
	}

    @Test
	public void LoginDB_03_LoginWithInvalidPhoneFormat() {
		// Log in with a phone number consisting of 9 digits.
		loginPage.navigate().performLogin(generate.generateNumber(9), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_PHONE_ERROR).completeVerify();
		// Log in with a phone number consisting of 14 digits.
		loginPage.performLogin(generate.generateNumber(14), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_PHONE_ERROR).completeVerify();
	}

    @Test
	public void LoginDB_04_LoginWithInvalidMailFormat() {
		// Mail does not have symbol @
		loginPage.navigate().performLogin(generate.generateString(10), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_MAIL_ERROR).completeVerify();

		// Mail does not have suffix '.<>'. Eg. '.com'
		loginPage.navigate().performLogin(generate.generateString(10) + "@", generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_MAIL_ERROR).completeVerify();

		loginPage.navigate()
				.performLogin(generate.generateString(10) + "@" + generate.generateString(5) + ".",
						generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_MAIL_ERROR).completeVerify();
	}

    @Test
	public void LoginDB_05_LoginWithWrongEmailAccount() {
		loginPage.navigate()
				.performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
		
		loginPage.navigate().performLogin(generate.generateNumber(13), generate.generateString(10))
		.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
	}

    @Test
	public void LoginDB_06_LoginWithCorrectAccount() {
		loginPage.navigate().performLogin(PHONE_COUNTRY, PHONE, PHONE_PASSWORD);
		homePage.clickLogout();
		
		loginPage.navigate().performLogin(MAIL, PASSWORD);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

//    @Test
	public void LoginDB_07_LoginWithFacebook() {
		loginPage.navigate().performLoginWithFacebook(FACEBOOK, FACEBOOK_PASSWORD);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

    @Test
	public void LoginDB_08_StaffLogin() {
		// Login with wrong credentials.
		loginPage.navigate().switchToStaffTab().performLogin(STAFF, generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
		// Login with correct credentials.
		loginPage.navigate().switchToStaffTab().performLogin(STAFF, STAFF_PASSWORD);
		homePage.clickLogout();
	}

	@Test
	public void LoginDB_09_StaffForgotPassword() throws Exception {
		String newPassword = STAFF_PASSWORD + generate.generateNumber(4) + "!";

		String staff = "emcehc@mailnesia.com";

		if (loginPage.navigate().getSelectedLanguage().contentEquals("English")) {
			language = "ENG";
		} else {
			language = "VIE";
		}

		// Inadequate number of characters
		loginPage.switchToStaffTab()
		.clickForgotPassword()
		.inputEmailOrPhoneNumber(staff)
		.inputPassword("fortt!1")
		.clickContinueOrConfirmBtn();
		verifyChangePasswordError();
		
		// Absence of numbers
		loginPage.inputPassword("fortesting!")
		.clickContinueOrConfirmBtn();
		verifyChangePasswordError();
		
		// Absence of letters
		loginPage.inputPassword("12345678!")
		.clickContinueOrConfirmBtn();
		verifyChangePasswordError();

		// Absence of special characters
		loginPage.inputPassword("fortesting1")
		.clickContinueOrConfirmBtn();
		verifyChangePasswordError();

		// Input wrong verification code
		loginPage.inputPassword(newPassword)
		.clickContinueOrConfirmBtn();

		String code = getVerificationCode(staff);

		loginPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1))
		.clickContinueOrConfirmBtn();
		loginPage.verifyVerificationCodeError(language);

		// Input correct verification code
		loginPage.inputVerificationCode(code)
		.clickContinueOrConfirmBtn();
		homePage.waitTillSpinnerDisappear();
		homePage.clickLogout();

		// Re-login with new password
		loginPage.navigate()
		.switchToStaffTab()
		.performLogin(staff, newPassword);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

	@Test
	public void LoginDB_10_SellersChangePassword() throws InterruptedException {
		String username = "";
		String password = "";
		String country = "";

		String[][] testData = { 
				{ SELLER_FORGOT_MAIL_COUNTRY, SELLER_FORGOT_MAIL_USERNAME, SELLER_FORGOT_MAIL_PASSWORD },
				{ SELLER_FORGOT_PHONE_COUNTRY, SELLER_FORGOT_PHONE_USERNAME, SELLER_FORGOT_PHONE_PASSWORD }
		};

		for (String[] row : testData) {
			country = row[0];
			username = row[1];
			password = row[2];

			String newPassword = password + generate.generateNumber(3) + "!";

			// Login
			loginPage.navigate().performLogin(country, username, password);

			// Change password
			homePage.navigateToPage("Settings");
			new AccountPage(driver).navigate().changePassword(password, newPassword, newPassword);
			homePage.getToastMessage();
			homePage.clickLogout();

			// Re-login
			loginPage.navigate().performLogin(country, username, newPassword);

			// Change password back to the first password
			String currentPassword = "";
			for (int i = 0; i < 5; i++) {
				currentPassword = newPassword;

				newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;
				
				if (i == 0) homePage.navigateToPage("Settings");
				new AccountPage(driver).navigate().changePassword(currentPassword, newPassword, newPassword);
				homePage.getToastMessage();
			}
			homePage.clickLogout();
		}
	}

	@Test
	public void LoginDB_11_SellerForgotPassword() throws InterruptedException, SQLException {
		String username = "";
		String password = "";
		String country = "";

		String[][] testData = { 
				{ SELLER_FORGOT_MAIL_COUNTRY, SELLER_FORGOT_MAIL_USERNAME, SELLER_FORGOT_MAIL_PASSWORD },
				{ SELLER_FORGOT_PHONE_COUNTRY, SELLER_FORGOT_PHONE_USERNAME, SELLER_FORGOT_PHONE_PASSWORD }
		};

		for (String[] row : testData) {
			country = row[0];
			username = row[1];
			password = row[2];

			String newPassword =  password + generate.generateNumber(3) + "!";

			loginPage.navigate()
			.clickForgotPassword()
			.selectCountry(country)
			.inputEmailOrPhoneNumber(username)
			.inputPassword(newPassword)
			.clickContinueOrConfirmBtn();

			loginPage.inputVerificationCode(getVerificationCode(username))
			.clickContinueOrConfirmBtn();
			
			// Logout
			homePage.clickLogout();

			// Re-login with new password
			loginPage.navigate().performLogin(country, username, newPassword);

			// Change password back to the first password
			String currentPassword = "";
			for (int i = 0; i < 5; i++) {
				currentPassword = newPassword;

				newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;

				if (i == 0) homePage.navigateToPage("Settings");
				new AccountPage(driver).navigate().changePassword(currentPassword, newPassword, newPassword);
				homePage.getToastMessage();
			}
			homePage.clickLogout();
		}
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	
	
}
