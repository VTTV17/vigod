import java.sql.SQLException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import pages.Mailnesia;
import pages.dashboard.LoginPage;
import pages.dashboard.home.HomePage;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;

public class LoginDashboard extends BaseTest {
	
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
        new LoginPage(driver).navigate()
                .performLogin(generate.generateString(10), generate.generateString(10))
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
                .performLogin(PHONE_COUNTRYCODE, PHONE, PHONE_PASSWORD);
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
    
    //Don't run this test case. It should only be run in regression test.
//    @Test
    public void TC10_DB_SellerForgotEmailPassword() throws InterruptedException {
    	String newPassword = PASSWORD + generate.generateNumber(4)+ "!";
    	
    	new LoginPage(driver).navigate()
    	.clickForgotPassword()
    	.inputEmailOrPhoneNumber(MAIL)
    	.inputPassword(newPassword)
    	.clickContinueOrConfirmBtn();
    	
    	// Get verification code from Mailnesia
    	Thread.sleep(7000);
    	commonAction.openNewTab(); // Open a new tab
    	commonAction.switchToWindow(1); // Switch to the newly opened tab
    	String verificationCode = new Mailnesia(driver).navigate(MAIL).getVerificationCode(); // Get verification code
    	commonAction.closeTab(); // Close the newly opened tab
    	commonAction.switchToWindow(0); // Switch back to the original tab
    	
    	new LoginPage(driver).inputVerificationCode(verificationCode)
    	.clickContinueOrConfirmBtn();
    	new HomePage(driver).clickLogout();
    	
    	// Re-login with new password
    	new LoginPage(driver).navigate().performLogin(MAIL, newPassword);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }
    
    //Don't run this test case. It should only be run in regression test.
//    @Test
    public void TC11_DB_StaffForgotPassword() throws InterruptedException {
    	String newPassword = STAFF_PASSWORD + generate.generateNumber(4)+ "!";
    	
        new LoginPage(driver).navigate()
		.switchToStaffTab()
		.clickForgotPassword()
        .inputEmailOrPhoneNumber(STAFF)
        .inputPassword(newPassword)
        .clickContinueOrConfirmBtn();
        
    	// Get verification code from Mailnesia
        Thread.sleep(7000);
    	commonAction.openNewTab();
    	commonAction.switchToWindow(1);
    	String verificationCode = new Mailnesia(driver).navigate(STAFF).getVerificationCode();
    	commonAction.closeTab(); 
    	commonAction.switchToWindow(0);
    	
    	new LoginPage(driver).inputVerificationCode(verificationCode)
    	.clickContinueOrConfirmBtn();
    	new HomePage(driver).clickLogout();

    	// Re-login with new password
    	new LoginPage(driver).navigate()
    	.switchToStaffTab()
    	.performLogin(STAFF, newPassword);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }
    
    //Don't run this test case. It should only be run in regression test.
//  @Test
  public void TC12_DB_SellerForgotPhonePassword() throws InterruptedException, SQLException {
  	String newPassword = PHONE_PASSWORD + generate.generateNumber(4)+ "!";
  	
  	new LoginPage(driver).navigate()
  	.clickForgotPassword()
  	.selectCountry(PHONE_COUNTRY)
  	.inputEmailOrPhoneNumber(PHONE)
  	.inputPassword(newPassword)
  	.clickContinueOrConfirmBtn()
  	.inputVerificationCode(new InitConnection().getResetKey(PHONE_COUNTRYCODE + ":" + PHONE))
  	.clickContinueOrConfirmBtn();
	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();  
  	
  	// Re-login with new password
  	new LoginPage(driver).navigate()
  	.performLogin(PHONE_COUNTRY, PHONE, newPassword);
  	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
  }   
}
