package web.StoreFront.login;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.utils.PropertiesUtil;
import web.StoreFront.GeneralSF;

public class ForgotPasswordDialog {

    final static Logger logger = LogManager.getLogger(ForgotPasswordDialog.class);

    WebDriver driver;
    UICommonAction commonAction;

    public ForgotPasswordDialog(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    
    //Will move these locators to a separate file later
    By loc_lblForgotScreen = By.cssSelector("#forgot-pwd-modal .modal-content");
    By loc_ddlCountry = By.id("forgot-pwd-country-code");
	By loc_lstCountry = By.id("forgot-pwd-country-code-menu");
	String loc_ddvCountryByName = ".//span[text()='%s']";
    By loc_txtUsername = By.id("forgot-pwd-username");
    By loc_btnContinue = By.cssSelector("#frm-forgot-pwd .btn-submit"); 
    By loc_lnkBackToLogin = By.cssSelector("#forgot-pwd-modal [data-target='#login-modal']");
    By loc_txtPassword = By.id("verify-password");
    By loc_btnConfirm = By.cssSelector("#frm-verify .btn-submit"); 
    By loc_txtVerificationCode = By.id("verify-code"); 
    By loc_lblWrongVerificationCodeError = By.id("verify-fail");
    By loc_lblUsernameError = By.xpath("//div[@id='forgot-pwd-fail' and @style='display: inline-block;']");
    By loc_lblPasswordError = By.id("verify-password-error");

    public ForgotPasswordDialog selectCountry(String country) {
    	commonAction.click(loc_ddlCountry);
    	commonAction.click(new ByChained(loc_lstCountry, By.xpath(loc_ddvCountryByName.formatted(country))));
    	logger.info("Selected country: " + country); 
    	return this;
    }    
    public ForgotPasswordDialog inputUsername(String username) {
    	commonAction.sendKeys(loc_txtUsername, username);
    	logger.info("Input Username: " + username);
        return this;
    }
    public ForgotPasswordDialog clickContinueBtn() {
        commonAction.click(loc_btnContinue);
        logger.info("Clicked on Continue button.");
        return this;
    } 
    public ForgotPasswordDialog inputPassword(String password) {
    	commonAction.sendKeys(loc_txtPassword, password);
    	logger.info("Input Passowrd: " + password);
        return this;
    }      
    public ForgotPasswordDialog clickConfirmBtn() {
    	commonAction.click(loc_btnConfirm);
    	logger.info("Clicked on Confirm button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
    	return this;
    }      
    public LoginPage clickBackToLogin() {
    	commonAction.click(loc_lnkBackToLogin);
    	logger.info("Clicked on 'Back To Login' linktext.");
    	return new LoginPage(driver);
    }      
    public ForgotPasswordDialog inputVerificationCode(String verificationCode) {
    	commonAction.sendKeys(loc_txtVerificationCode, verificationCode);
    	logger.info("Input Verification Code: " + verificationCode);
        return this;
    } 
    public String getUsernameError() {
    	String text = commonAction.getText(loc_lblUsernameError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }          
    public String getPasswordError() {
    	String text = commonAction.getText(loc_lblPasswordError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }          
    public String getWrongVerificationCodeError() {
    	String text = commonAction.getText(loc_lblWrongVerificationCodeError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }     

    public void verifyTextAtForgotPasswordScreen(String signinLanguage) throws Exception {
    	String text = commonAction.getText(loc_lblForgotScreen);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.forgotPassword.text", signinLanguage));
    	logger.info("verifyTextAtForgotPasswordScreen completed");
    }       
    
}
