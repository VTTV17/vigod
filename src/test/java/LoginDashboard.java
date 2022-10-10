import java.sql.SQLException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import pages.Mailnesia;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.settings.account.AccountPage;
import pages.dashboard.signup.SignupPage;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;

public class LoginDashboard extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;

	String MAIL;
	String PASSWORD;
	String PHONE;
	String PHONE_PASSWORD;
	String PHONE_COUNTRYCODE;
	String PHONE_COUNTRY;
	String FACEBOOK;
	String FACEBOOK_PASSWORD;
	String STAFF;
	String STAFF_PASSWORD;
	
	String SELLER_FORGOT_MAIL_USERNAME;
	String SELLER_FORGOT_MAIL_PASSWORD;
	String SELLER_FORGOT_MAIL_COUNTRY;
	String SELLER_FORGOT_PHONE_USERNAME;
	String SELLER_FORGOT_PHONE_PASSWORD;
	String SELLER_FORGOT_PHONE_COUNTRY;		
	
	String BLANK_ERROR;
	String INVALID_MAIL_ERROR;
	String INVALID_PHONE_ERROR;
	String INVALID_CREDENTIALS_ERROR;
	String INVALID_PASSWORD_FORMAT_ERROR_VI;
	String INVALID_PASSWORD_FORMAT_ERROR_EN;
	String INVALID_CODE_ERROR_VI = "Mã xác thực không đúng!";
	String INVALID_CODE_ERROR_EN = "Incorrect confirmation code!";

	String language;

	public String getVerificationCode(String username) throws InterruptedException, SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			Thread.sleep(8000);
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

	public void verifyChangePasswordError() throws InterruptedException {
		String message;
		if (loginPage.getSelectedLanguage().contentEquals("English")) {
			message = INVALID_PASSWORD_FORMAT_ERROR_EN;
		} else {
			message = INVALID_PASSWORD_FORMAT_ERROR_VI;
		}
		loginPage.verifyPasswordError(message).completeVerify();
	}

	public void verifyConfirmationCodeError() throws InterruptedException {
		String message;
		if (language.contentEquals("English")) {
			message = INVALID_CODE_ERROR_EN;
		} else {
			message = INVALID_CODE_ERROR_VI;
		}
		new SignupPage(driver).verifyVerificationCodeError(message).completeVerify();
	}

	@BeforeClass
	public void readData() {
		JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");

		MAIL = data.findValue("seller").findValue("mail").findValue("username").asText();
		PASSWORD = data.findValue("seller").findValue("mail").findValue("password").asText();
		PHONE = data.findValue("seller").findValue("phone").findValue("username").asText();
		PHONE_PASSWORD = data.findValue("seller").findValue("phone").findValue("password").asText();
		PHONE_COUNTRY = data.findValue("seller").findValue("phone").findValue("country").asText();
		PHONE_COUNTRYCODE = data.findValue("seller").findValue("phone").findValue("countryCode").asText();
		FACEBOOK = data.findValue("seller").findValue("facebook").findValue("username").asText();
		FACEBOOK_PASSWORD = data.findValue("seller").findValue("facebook").findValue("password").asText();
		STAFF = data.findValue("staff").findValue("mail").findValue("username").asText();
		STAFF_PASSWORD = data.findValue("staff").findValue("mail").findValue("password").asText();
		SELLER_FORGOT_MAIL_USERNAME = data.findValue("seller").findValue("forgotMail").findValue("username").asText();
		SELLER_FORGOT_MAIL_PASSWORD = data.findValue("seller").findValue("forgotMail").findValue("password").asText();
		SELLER_FORGOT_MAIL_COUNTRY = data.findValue("seller").findValue("forgotMail").findValue("country").asText();
		SELLER_FORGOT_PHONE_USERNAME = data.findValue("seller").findValue("forgotPhone").findValue("username").asText();
		SELLER_FORGOT_PHONE_PASSWORD = data.findValue("seller").findValue("forgotPhone").findValue("password").asText();
		SELLER_FORGOT_PHONE_COUNTRY = data.findValue("seller").findValue("forgotPhone").findValue("country").asText();
		
		BLANK_ERROR = data.findValue("emptyError").asText();
		INVALID_MAIL_ERROR = data.findValue("invalidMailFormat").asText();
		INVALID_PHONE_ERROR = data.findValue("invalidPhoneFormat").asText();
		INVALID_CREDENTIALS_ERROR = data.findValue("invalidCredentials").asText();
		INVALID_PASSWORD_FORMAT_ERROR_VI = "Mật khẩu phải dài ít nhất 8 ký tự và có ít nhất 1 chữ, 1 số và 1 ký tự đặc biệt";
		INVALID_PASSWORD_FORMAT_ERROR_EN = "Your password must have at least 8 characters with at least 1 letter, 1 number and 1 special character";
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
	}
	
//    @Test
	public void TC01_DB_LoginWithAllFieldsLeftBlank() {
		// Username field is left empty.
		loginPage.navigate().performLogin("", generate.generateNumber(9))
				.verifyEmailOrPhoneNumberError(BLANK_ERROR).completeVerify();
		// Password field is left empty.
		loginPage.navigate().performLogin(generate.generateNumber(10), "").verifyPasswordError(BLANK_ERROR)
				.completeVerify();
		// All fields are left empty.
		loginPage.navigate().performLogin("", "").verifyEmailOrPhoneNumberError(BLANK_ERROR)
				.verifyPasswordError(BLANK_ERROR).completeVerify();
	}

//    @Test
	public void TC02_DB_LoginWithInvalidPhoneFormat() {
		// Log in with a phone number consisting of 9 digits.
		loginPage.navigate().performLogin(generate.generateNumber(9), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_PHONE_ERROR).completeVerify();
		// Log in with a phone number consisting of 14 digits.
		loginPage.performLogin(generate.generateNumber(14), generate.generateString(10))
				.verifyEmailOrPhoneNumberError(INVALID_PHONE_ERROR).completeVerify();
	}

//    @Test
	public void TC03_DB_LoginWithInvalidMailFormat() {
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

//    @Test
	public void TC04_DB_LoginWithWrongEmailAccount() {
		loginPage.navigate()
				.performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
	}

//    @Test
	public void TC05_DB_LoginWithWrongPhoneAccount() {
		loginPage.navigate().performLogin(generate.generateNumber(13), generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
	}

//    @Test
	public void TC06_DB_LoginWithCorrectPhoneAccount() {
		loginPage.navigate().performLogin(PHONE_COUNTRY, PHONE, PHONE_PASSWORD);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

//    @Test
	public void TC07_DB_LoginWithCorrectMailAccount() {
		loginPage.navigate().performLogin(MAIL, PASSWORD);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

//    @Test
	public void TC08_DB_LoginWithFacebook() throws InterruptedException {
		loginPage.navigate().performLoginWithFacebook(FACEBOOK, FACEBOOK_PASSWORD);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

//    @Test
	public void TC09_DB_StaffLogin() {
		// Login with wrong credentials.
		loginPage.navigate().switchToStaffTab().performLogin(STAFF, generate.generateString(10))
				.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR).completeVerify();
		// Login with correct credentials.
		loginPage.navigate().switchToStaffTab().performLogin(STAFF, STAFF_PASSWORD);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

//	@Test
	public void BH_1813_StaffForgotPassword() throws InterruptedException, SQLException {
		String newPassword = STAFF_PASSWORD + generate.generateNumber(4) + "!";

		String staff = "emcehc@mailnesia.com";

		language = loginPage.navigate().getSelectedLanguage();

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
		verifyConfirmationCodeError();

		// Input correct verification code
		loginPage.inputVerificationCode(code)
		.clickContinueOrConfirmBtn();
		homePage.clickLogout();

		// Re-login with new password
		loginPage.navigate()
		.switchToStaffTab()
		.performLogin(staff, newPassword);
		homePage.waitTillSpinnerDisappear().clickLogout();
	}

	@Test
	public void BH_4050_SellersChangePassword() throws InterruptedException {
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
			homePage.waitTillSpinnerDisappear().navigateToPage("Settings");
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
	public void BH_4050_SellerForgotPassword() throws InterruptedException, SQLException {
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

			// Get verification code
			String code = null;
			for (int i = 0; i < 3; i++) {
				code = getVerificationCode(username);
				if (code == null) {
					loginPage.clickResendOTP();
				} else {
					break;
				}
			}
			
			loginPage.inputVerificationCode(code)
			.clickContinueOrConfirmBtn();
			
			// Logout
			homePage.waitTillSpinnerDisappear().clickLogout();

			// Re-login with new password
			loginPage.navigate().performLogin(country, username, newPassword);

			// Change password back to the first password
			String currentPassword = "";
			for (int i = 0; i < 5; i++) {
				currentPassword = newPassword;

				newPassword = (i!=4) ? password + generate.generateNumber(3)+ "!" : password;

				if (i == 0) homePage.waitTillSpinnerDisappear().navigateToPage("Settings");
				new AccountPage(driver).navigate().changePassword(currentPassword, newPassword, newPassword);
				homePage.getToastMessage();
			}
			homePage.clickLogout();
		}
	}

}
