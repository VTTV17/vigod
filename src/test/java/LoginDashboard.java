import java.sql.SQLException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import pages.Mailnesia;
import pages.dashboard.LoginPage;
import pages.dashboard.SignupPage;
import pages.dashboard.home.HomePage;
import pages.dashboard.settings.account.AccountPage;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;

public class LoginDashboard extends BaseTest {
	
	LoginPage loginPage;
	
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
    	if (new LoginPage(driver).getSelectedLanguage().contentEquals("English")) {
    		message = INVALID_PASSWORD_FORMAT_ERROR_EN;
    	} else {
    		message = INVALID_PASSWORD_FORMAT_ERROR_VI;
    	}
    	new LoginPage(driver).verifyPasswordError(message).completeVerify();
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
		
		BLANK_ERROR = data.findValue("emptyError").asText();
		INVALID_MAIL_ERROR = data.findValue("invalidMailFormat").asText();
		INVALID_PHONE_ERROR = data.findValue("invalidPhoneFormat").asText();
		INVALID_CREDENTIALS_ERROR = data.findValue("invalidCredentials").asText();
		INVALID_PASSWORD_FORMAT_ERROR_VI = "Mật khẩu phải dài ít nhất 8 ký tự và có ít nhất 1 chữ, 1 số và 1 ký tự đặc biệt";
		INVALID_PASSWORD_FORMAT_ERROR_EN = "Your password must have at least 8 characters with at least 1 letter, 1 number and 1 special character";
    }	    

    @Test
    public void TC01_DB_LoginWithAllFieldsLeftBlank() {
    	// Username field is left empty.
        new LoginPage(driver).navigate()
                .performLogin("", generate.generateNumber(9))
                .verifyEmailOrPhoneNumberError(BLANK_ERROR)
                .completeVerify();
        // Password field is left empty.
        new LoginPage(driver).navigate()
		        .performLogin(generate.generateNumber(10), "")
		        .verifyPasswordError(BLANK_ERROR)
		        .completeVerify();
        // All fields are left empty.
        new LoginPage(driver).navigate()
		        .performLogin("", "")
		        .verifyEmailOrPhoneNumberError(BLANK_ERROR)
		        .verifyPasswordError(BLANK_ERROR)
		        .completeVerify();
    }

    @Test
    public void TC02_DB_LoginWithInvalidPhoneFormat() {
    	// Log in with a phone number consisting of 9 digits.
        new LoginPage(driver).navigate()
                .performLogin(generate.generateNumber(9), generate.generateString(10))
                .verifyEmailOrPhoneNumberError(INVALID_PHONE_ERROR)
                .completeVerify();
        // Log in with a phone number consisting of 14 digits.
        new LoginPage(driver)
        		.performLogin(generate.generateNumber(14), generate.generateString(10))
                .verifyEmailOrPhoneNumberError(INVALID_PHONE_ERROR)
                .completeVerify();
    }

    @Test
    public void TC03_DB_LoginWithInvalidMailFormat() {
    	// Mail does not have symbol @
        new LoginPage(driver).navigate()
        .performLogin(generate.generateString(10), generate.generateString(10))
        .verifyEmailOrPhoneNumberError(INVALID_MAIL_ERROR)
        .completeVerify();
        
        // Mail does not have suffix '.<>'. Eg. '.com'
        new LoginPage(driver).navigate()
        .performLogin(generate.generateString(10) + "@" , generate.generateString(10))
        .verifyEmailOrPhoneNumberError(INVALID_MAIL_ERROR)
        .completeVerify();
        
        new LoginPage(driver).navigate()
        .performLogin(generate.generateString(10) + "@" + generate.generateString(5) + ".", generate.generateString(10))
        .verifyEmailOrPhoneNumberError(INVALID_MAIL_ERROR)
        .completeVerify();
    }

    @Test
    public void TC04_DB_LoginWithWrongEmailAccount() {
        new LoginPage(driver).navigate()
                .performLogin(generate.generateString(10) + "@nbobd.com", generate.generateString(10))
                .verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR)
                .completeVerify();
    }

    @Test
    public void TC05_DB_LoginWithWrongPhoneAccount() {
        new LoginPage(driver).navigate()
                .performLogin(generate.generateNumber(13), generate.generateString(10))
                .verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR)
                .completeVerify();
    }

    @Test
    public void TC06_DB_LoginWithCorrectPhoneAccount() {
        new LoginPage(driver).navigate()
                .performLogin(PHONE_COUNTRY, PHONE, PHONE_PASSWORD);
        new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }

    @Test
    public void TC07_DB_LoginWithCorrectMailAccount() {
        new LoginPage(driver).navigate()
                .performLogin(MAIL, PASSWORD);
        new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }

    @Test
    public void TC08_DB_LoginWithFacebook() throws InterruptedException {
        new LoginPage(driver).navigate().performLoginWithFacebook(FACEBOOK, FACEBOOK_PASSWORD);   
        new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }

    @Test
    public void TC09_DB_StaffLogin() {
    	// Login with wrong credentials.
    	new LoginPage(driver).navigate()
    	.switchToStaffTab()
    	.performLogin(STAFF, generate.generateString(10))
    	.verifyEmailOrPasswordIncorrectError(INVALID_CREDENTIALS_ERROR)
    	.completeVerify();
    	// Login with correct credentials.
    	new LoginPage(driver).navigate()
    	.switchToStaffTab()
    	.performLogin(STAFF, STAFF_PASSWORD);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }    
    
    @Test
    public void BH_1813_StaffForgotPassword() throws InterruptedException, SQLException {
    	String newPassword = STAFF_PASSWORD + generate.generateNumber(4)+ "!";
    	
    	String staff = "emcehc@mailnesia.com";
    	
    	loginPage = new LoginPage(driver);
    	
    	language = loginPage.navigate().getSelectedLanguage();
    	
    	loginPage.switchToStaffTab()
		.clickForgotPassword()
        .inputEmailOrPhoneNumber(staff)
        .inputPassword("fortt!1")
        .clickContinueOrConfirmBtn();
        verifyChangePasswordError();
        
        loginPage.inputPassword("fortesting!")
        .clickContinueOrConfirmBtn();
        verifyChangePasswordError();
        
        loginPage.inputPassword("12345678!")
        .clickContinueOrConfirmBtn();
        verifyChangePasswordError();
        
        loginPage.inputPassword("fortesting1")
        .clickContinueOrConfirmBtn();   
        verifyChangePasswordError();
        
        loginPage.inputPassword(newPassword)
        .clickContinueOrConfirmBtn();
        
        String code = getVerificationCode(staff);
        
        loginPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) -1))
    	.clickContinueOrConfirmBtn();
    	verifyConfirmationCodeError();
    	
    	loginPage.inputVerificationCode(code)
    	.clickContinueOrConfirmBtn();
    	new HomePage(driver).clickLogout();

    	// Re-login with new password
    	new LoginPage(driver).navigate()
    	.switchToStaffTab()
    	.performLogin(staff, newPassword);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }
    
	@Test
	public void BH_4050_SellerChangePassword() throws InterruptedException {
		String username = "";
		String password = "";
		String country = "";
		
		String[][] testData = {
				{"Poland", "automation0-shop842@mailnesia.com", "fortesting!1"}, 
		};
		
		for (String[] row:testData) {
			country = row[0];
			username = row[1];
			password = row[2];
			
			String newPassword = password + generate.generateNumber(3)+ "!";
			
			// Login
			new LoginPage(driver)
			.navigate()
			.performLogin(country, username, password);
			
			// Change password
			new HomePage(driver).waitTillSpinnerDisappear()
			.navigateToPage("Settings");
			new AccountPage(driver).navigate()
			.changePassword(password, newPassword, newPassword);
			new HomePage(driver).getToastMessage();
			new HomePage(driver).clickLogout();
			
			// Re-login
			new LoginPage(driver)
			.navigate()
			.performLogin(country, username, newPassword);
			
			
			// Change password back to the first password
			String tempLogin = "";
			for (int i=0; i<5; i++) {
	    		tempLogin = newPassword;
	    		
				if (i!=4) {
					newPassword = password + generate.generateNumber(3)+ "!";
				} else {
					newPassword = password;
				}	
	    		
				new HomePage(driver)
				.navigateToPage("Settings");
	    		new AccountPage(driver).navigate()
	    		.changePassword(tempLogin, newPassword, newPassword);
	    		new HomePage(driver).getToastMessage();				
			}
			new HomePage(driver).clickLogout();
		}
	}
	
	@Test
	public void BH_4050_SellerForgotPassword() throws InterruptedException, SQLException {
		String username = "";
		String password = "";
		String country = "";
		
		String[][] testData = {
				{"Poland", "automation0-shop842@mailnesia.com", "fortesting!1"}, 
		};
		
		for (String[] row:testData) {
			country = row[0];
			username = row[1];
			password = row[2];
			
			String newPassword = "";
			String tempLogin;
			
			newPassword = password + generate.generateNumber(3)+ "!";
			
			loginPage = new LoginPage(driver);
			loginPage.navigate();
			loginPage.clickForgotPassword();
			loginPage.selectCountry(country);
			loginPage.inputEmailOrPhoneNumber(username)
	    	.inputPassword(newPassword)
	    	.clickContinueOrConfirmBtn();
			
			String code = null;
			for (int i=0;i<3;i++) {
				code = getVerificationCode(username);
				if (code ==null) {
					loginPage.clickResendOTP();
				} else {
					break;
				}
			}
			loginPage.inputVerificationCode(code);
			loginPage.clickContinueOrConfirmBtn();
	    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
	    	
	    	// Re-login with new password
	    	loginPage.navigate().performLogin(country, username, newPassword);
	    	
	    	// Change password back to the first password
	    	for (int i=0; i<5; i++) {
	    		tempLogin = newPassword;
	    		
				if (i!=4) {
					newPassword = password + generate.generateNumber(3)+ "!";
				} else {
					newPassword = password;
				}		
				
				if (i==0) new HomePage(driver).waitTillSpinnerDisappear();
				new HomePage(driver).navigateToPage("Settings");
	    		new AccountPage(driver).navigate()
	    		.changePassword(tempLogin, newPassword, newPassword);
	    		new HomePage(driver).getToastMessage();
	    	}
	    	new HomePage(driver).clickLogout();
		}
	}

}
