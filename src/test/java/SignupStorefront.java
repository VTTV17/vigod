import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.storefront.HeaderSF;
import pages.storefront.LoginPage;
import pages.storefront.SignupPage;
import utilities.database.InitConnection;
import pages.Mailnesia;

import java.sql.SQLException;

public class SignupStorefront extends BaseTest{

	SignupPage signupPage;
	
    @BeforeMethod
    public void setup() throws InterruptedException {
    	super.setup();
    	signupPage = new SignupPage(driver);
    }		
	
    @Test
    public void SignupWithPhone() throws SQLException, InterruptedException {
    	String randomNumber = generate.generateNumber(3);
    	String country = "Austria";
    	String countryCode = "+43";
    	String phone = "9923456" + randomNumber;
    	String username = "automation0-shop" + randomNumber + "@mailnesia.com";
    	String password = "fortesting!1";
    	String nameDisplay = "Luke Thames";
    	String birthday = "02/02/1990";

    	signupPage.navigate()
    	.fillOutSignupForm(country, phone, password, nameDisplay, birthday)
    	.inputVerificationCode(new InitConnection().getActivationKey(countryCode + ":" + phone))
    	.clickConfirmBtn();
    	signupPage.inputEmail(username).clickCompleteBtn();
    	
		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon()
		.clickLogout();
    	
    	// Re-login with new password
    	new LoginPage(driver).navigate()
    	.performLogin(country, phone, password);
    	Thread.sleep(1000);
    	new HeaderSF(driver).clickUserInfoIcon()
        .clickLogout();    	
    }
    
    @Test
    public void SignupWithEmail() throws SQLException, InterruptedException {
    	String randomNumber = generate.generateNumber(3);
    	String country = "Brazil";
    	String username = "automation0-shop" + randomNumber + "@mailnesia.com";
    	String password = "fortesting!1";
    	String nameDisplay = "Luke Thames";
    	String birthday = "02/02/1990";    	
    	
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password, nameDisplay, birthday);
    	Thread.sleep(7000);

    	// Get verification code from Mailnesia
    	commonAction.openNewTab();
    	commonAction.switchToWindow(1);
    	String verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode();
    	commonAction.closeTab();
    	commonAction.switchToWindow(0);
    	
    	signupPage.inputVerificationCode(verificationCode)
    	.clickConfirmBtn();

		Thread.sleep(1000);
		new HeaderSF(driver).clickUserInfoIcon()
		.clickLogout();   	
    	
    	// Re-login with new password
    	new LoginPage(driver).navigate()
    	.performLogin(country, username, password);
    	Thread.sleep(1000);
    	new HeaderSF(driver).clickUserInfoIcon()
        .clickLogout();    	    	
    	
    }
    
}
