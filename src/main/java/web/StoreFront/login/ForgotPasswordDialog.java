package web.StoreFront.login;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import lombok.SneakyThrows;
import utilities.commons.UICommonAction;
import utilities.enums.DisplayLanguage;
import utilities.utils.PropertiesUtil;
import web.StoreFront.GeneralSF;

public class ForgotPasswordDialog {

    final static Logger logger = LogManager.getLogger(ForgotPasswordDialog.class);

    WebDriver driver;
    UICommonAction commonAction;
    ForgotPasswordDialogElement locator;

    public ForgotPasswordDialog(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        locator = new ForgotPasswordDialogElement();
    }

    @SneakyThrows
    public static String localizedEmailNotExistError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("forgotPassword.error.notExistingEmail", language.name());
    }     
    @SneakyThrows
    public static String localizedPhoneNotExistError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("forgotPassword.error.notExistingPhone", language.name());
    }     
    @SneakyThrows
    public static String localizedWrongCurrentPasswordError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("forgotPassword.error.wrongCurrentPassword", language.name());
    }     
    @SneakyThrows
    public static String localizedInvalidNewPasswordError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("forgotPassword.error.invalidNewPassword", language.name());
    }     
    @SneakyThrows
    public static String localizedSame4PasswordsError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("forgotPassword.error.same4Passwords", language.name());
    }     
    
    public ForgotPasswordDialog selectCountry(String country) {
    	commonAction.click(locator.loc_ddlCountry);
    	commonAction.click(new ByChained(locator.loc_lstCountry, By.xpath(locator.loc_ddvCountryByName.formatted(country))));
    	logger.info("Selected country: " + country); 
    	return this;
    }    
    public ForgotPasswordDialog inputUsername(String username) {
    	commonAction.sendKeys(locator.loc_txtUsername, username);
    	logger.info("Input Username: " + username);
        return this;
    }
    public ForgotPasswordDialog clickContinueBtn() {
        commonAction.click(locator.loc_btnContinue);
        logger.info("Clicked on Continue button.");
        return this;
    } 
    public ForgotPasswordDialog inputPassword(String password) {
    	commonAction.sendKeys(locator.loc_txtPassword, password);
    	logger.info("Input Passowrd: " + password);
        return this;
    }      
    public ForgotPasswordDialog clickConfirmBtn() {
    	commonAction.click(locator.loc_btnConfirm);
    	logger.info("Clicked on Confirm button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
    	return this;
    }      
    public LoginPage clickBackToLogin() {
    	commonAction.click(locator.loc_lnkBackToLogin);
    	logger.info("Clicked on 'Back To Login' linktext.");
    	return new LoginPage(driver);
    }      
    public ForgotPasswordDialog inputVerificationCode(String verificationCode) {
    	commonAction.sendKeys(locator.loc_txtVerificationCode, verificationCode);
    	logger.info("Input Verification Code: " + verificationCode);
        return this;
    } 
    public String getUsernameError() {
    	String text = commonAction.getText(locator.loc_lblUsernameError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }          
    public String getPasswordError() {
    	String text = commonAction.getText(locator.loc_lblPasswordError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }          
    public String getWrongVerificationCodeError() {
    	String text = commonAction.getText(locator.loc_lblWrongVerificationCodeError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }     

    public void verifyTextAtForgotPasswordScreen(String signinLanguage) throws Exception {
    	String text = commonAction.getText(locator.loc_lblForgotScreen);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.forgotPassword.text", signinLanguage));
    	logger.info("verifyTextAtForgotPasswordScreen completed");
    }       
    
}
