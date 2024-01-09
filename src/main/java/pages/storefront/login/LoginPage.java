package pages.storefront.login;

import api.dashboard.setting.StoreInformation;
import api.storefront.login.LoginSF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.storefront.GeneralSF;
import pages.storefront.header.HeaderSF;
import pages.storefront.signup.SignupPage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;

import static utilities.links.Links.*;

public class LoginPage {
	
	final static Logger logger = LogManager.getLogger(LoginPage.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    By loc_txtSignInScreen = By.cssSelector("#login-modal .modal-content");
    By loc_txtForgotScreen = By.cssSelector("#forgot-pwd-modal .modal-content");
    
    By loc_ddlCountry = By.id("login-country-code");
	By loc_lstCountry = By.id("login-country-code-menu");
	
    By loc_txtUsername = By.id("login-username");
    By loc_txtPassword = By.id("login-password"); 
    By loc_btnLogin = By.cssSelector("#frm-login .btn-submit");
    
    By loc_lnkForgotPassword = By.id("open-forgot-pwd");
    By loc_ddlCountryForgot = By.id("forgot-pwd-country-code");
	By loc_lstCountryForgot = By.id("forgot-pwd-country-code-menu");
    By loc_txtUsernameForgot = By.id("forgot-pwd-username");
    By loc_txtPasswordForgot = By.id("verify-password");
    By loc_lnkBackToLogin = By.cssSelector("#forgot-pwd-modal [data-target='#login-modal']");
    
    By loc_btnFacebookLogin = By.cssSelector("#login-modal .facebook-login-button"); 

    By loc_lnkCreateAccount = By.cssSelector("#login-modal [data-target='#signup-modal']");
     
    By loc_btnContinueForgot = By.cssSelector("#frm-forgot-pwd .btn-submit"); 
      
    By loc_btnConfirmForgot = By.cssSelector("#frm-verify .btn-submit"); 

    By loc_txtVerificationCode = By.id("verify-code"); 
    
    By loc_lblUsernameError = By.id("login-username-error");
    By loc_lblLoginFailError = By.id("login-fail");
    By loc_lblPasswordError = By.id("login-password-error");
    
	By loc_lblForgotPasswordError = By.id("forgot-pwd-fail");
    
    public LoginPage navigate(String domain) {
        driver.get(domain);
        logger.debug("Page title is: "+driver.getTitle());
        return this;
    }

    public LoginPage selectCountry(String country) {
    	commonAction.click(loc_ddlCountry);
    	commonAction.click(new ByChained(loc_lstCountry, By.xpath(".//span[text()='%s']".formatted(country))));
    	logger.info("Selected country: " + country);
    	return this;
    }        
    
    public LoginPage selectCountryForgot(String country) {
    	commonAction.click(loc_ddlCountryForgot);
    	commonAction.click(new ByChained(loc_lstCountryForgot, By.xpath(".//span[text()='%s']".formatted(country))));
    	String[] selectedOption = commonAction.getText(loc_ddlCountryForgot).split("\n");
    	logger.info("Selected country '%s'. Its according code is '%s'.".formatted(selectedOption[0],selectedOption[1]));   	
    	return this;
    }        
    
    public LoginPage inputEmailOrPhoneNumber(String username) {
    	commonAction.sendKeys(loc_txtUsername, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.sendKeys(loc_txtPassword, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }    

    public LoginPage inputUsernameForgot(String username) {
    	commonAction.sendKeys(loc_txtUsernameForgot, username);
    	logger.info("Input '" + username + "' into Username field to get a new password.");
        return this;
    }

    public LoginPage inputPasswordForgot(String password) {
    	commonAction.sendKeys(loc_txtPasswordForgot, password);
    	logger.info("Input '" + password + "' into Password field to get a new password.");
        return this;
    }    
    
    public LoginPage clickLoginBtn() {
    	commonAction.click(loc_btnLogin);
    	logger.info("Clicked on Login button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }

    public LoginPage performLogin(String username, String password) {
        new GeneralSF(driver).waitTillLoaderDisappear();
    	new HeaderSF(driver).clickUserInfoIcon().clickLoginIcon();
    	inputEmailOrPhoneNumber(username);
    	inputPassword(password);
    	clickLoginBtn();
        return this;
    }

    public LoginPage performLogin(String country, String username, String password) {
    	new HeaderSF(driver).clickUserInfoIcon().clickLoginIcon();
    	selectCountry(country);
    	inputEmailOrPhoneNumber(username);
    	inputPassword(password);
    	clickLoginBtn();
    	return this;
    }    
    
    public LoginPage clickForgotPassword() {
    	commonAction.click(loc_lnkForgotPassword);
    	logger.info("Clicked on Forgot Password linktext.");
    	return this;
    }   
    
    public LoginPage clickFacebookIcon() {
    	commonAction.click(loc_btnFacebookLogin);
    	logger.info("Clicked on Facebook login icon.");
    	commonAction.sleepInMiliSecond(1000);
    	return this;
    }  
    
    public LoginPage clickBackToLogin() {
    	commonAction.click(loc_lnkBackToLogin);
    	logger.info("Clicked on 'Back To Login' linktext.");
    	return this;
    }  
    
    public SignupPage clickCreateNewAccount() {
    	commonAction.click(loc_lnkCreateAccount);
    	logger.info("Clicked on 'Create New Account' linktext.");
    	return new SignupPage(driver);
    }    

    public LoginPage clickContinueBtn() {
    	commonAction.click(loc_btnContinueForgot);
    	logger.info("Clicked on Continue button.");
    	return this;
    }    
    
    public LoginPage clickConfirmBtn() {
    	commonAction.click(loc_btnConfirmForgot);
    	logger.info("Clicked on Confirm button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
    	return this;
    }    

    public LoginPage inputVerificationCode(String verificationCode) {
    	commonAction.sendKeys(loc_txtVerificationCode, verificationCode);
    	logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }   
    
    public String getLoginFailError() {
    	String text = commonAction.getText(loc_lblLoginFailError);
    	logger.info("Error retrieved: " + text);
    	return text;
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
    
    public String getForgotPasswordError() {
    	String text = commonAction.getText(loc_lblForgotPasswordError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }      

    public void completeVerify() {
        soft.assertAll();
    }

    public void clickLoginBtnJS() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commonAction.getElement(loc_btnLogin));
        logger.info("Clicked on Login button.");
    }

    public void performLoginJS(String username, String password, String phoneCode, LoginInformation loginInformation) {
        // get buyer accessToken
        LoginSF login = new LoginSF(loginInformation);
        login.LoginToSF(username, password, phoneCode);
        String accessToken = login.getInfo().getAccessToken();

        // navigate to storefront
        navigate("https://%s%s/".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN));

        // update accessToken cookie
        ((JavascriptExecutor) driver).executeScript("document.cookie = 'Authorization=\"Bearer %s\"'".formatted(accessToken));

        // refresh page to get the newest configuration
        driver.navigate().refresh();
    }

    public void verifyTextAtLoginScreen(String signinLanguage) throws Exception {
        String text = commonAction.getText(loc_txtSignInScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.screen.text", signinLanguage));
        logger.info("verifyTextAtLoginScreen completed");
    }
    
    public void verifyTextAtForgotPasswordScreen(String signinLanguage) throws Exception {
    	String text = commonAction.getText(loc_txtForgotScreen);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.forgotPassword.text", signinLanguage));
    	logger.info("verifyTextAtForgotPasswordScreen completed");
    }    
    
}
