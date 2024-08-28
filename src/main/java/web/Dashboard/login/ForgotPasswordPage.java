package web.Dashboard.login;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class ForgotPasswordPage {

    final static Logger logger = LogManager.getLogger(ForgotPasswordPage.class);

    WebDriver driver;
    UICommonAction commonAction;

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    
    //Will move these locators to a separate file later
    By loc_txtUsername = By.id("phoneOrEmail"); 
    By loc_txtPassword = By.id("password");
    By loc_btnContinue = By.cssSelector(".forgot-password-container [type='submit']"); 
    By loc_txtVerificationCode = By.cssSelector("input#code"); 
    By loc_btnConfirm = By.cssSelector(".reset-password-container [type='submit']"); 
    By loc_lnkResendOTP = By.cssSelector(".btn-resend");
    By loc_lblVerificationCodeError = By.cssSelector(".reset-password-container .invalid-feedback");

    //Identical to LoginPage.selectCountry(country). Will do something about it
    public ForgotPasswordPage selectCountry(String country) {
    	new LoginPage(driver).selectCountry(country);
        return this;
    }    
    public ForgotPasswordPage inputUsername(String username) {
        commonAction.inputText(loc_txtUsername, username);
        logger.info("Input '" + username + "' into Username field.");
        return this;
    }
    public ForgotPasswordPage inputPassword(String password) {
        commonAction.inputText(loc_txtPassword, password);
        logger.info("Input '" + password + "' into Password field.");
        return this;
    }
    public ForgotPasswordPage clickResendOTP() {
        commonAction.click(loc_lnkResendOTP);
        logger.info("Clicked on Resend linktext.");
        return this;
    }
    public ForgotPasswordPage clickContinueBtn() {
        commonAction.click(loc_btnContinue);
        logger.info("Clicked on Continue button.");
        return this;
    }
    public ForgotPasswordPage inputVerificationCode(String verificationCode) {
        commonAction.sendKeys(loc_txtVerificationCode, verificationCode);
        logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }
    public ForgotPasswordPage clickConfirmBtn() {
        commonAction.click(loc_btnConfirm);
        logger.info("Clicked on Confirm button.");
        return this;
    }
    
    //Identical to LoginPage.getPasswordError(). Will do something about it
    public String getPasswordError() {
    	return new LoginPage(driver).getPasswordError();
    }     
    
    public String getVerificationCodeError() {
    	String text = commonAction.getText(loc_lblVerificationCodeError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }     

}
