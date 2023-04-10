package pages.storefront.login;

import api.dashboard.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.storefront.GeneralSF;
import pages.storefront.header.HeaderSF;
import pages.storefront.signup.SignupPage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

import static api.dashboard.setting.StoreInformation.apiStoreURL;
import static utilities.links.Links.*;

import java.time.Duration;

public class LoginPage {
	
	final static Logger logger = LogManager.getLogger(LoginPage.class);
	
	public String country;
	public String countryCode;
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = "#login-modal .modal-content")
    WebElement SIGNIN_SCREEN_TXT;       
    
    @FindBy (css = "#forgot-pwd-modal .modal-content")
    WebElement SIGNIN_FORGOTPASSWORD_TXT;       
    
    @FindBy (id = "login-country-code")
    WebElement COUNTRY_DROPDOWN;       
    
    @FindBy (id = "forgot-pwd-country-code")
    WebElement COUNTRY_FORGOT_DROPDOWN;       
    
    @FindBy(id = "login-username")
    WebElement USERNAME;
    
    @FindBy (id = "login-password")
    WebElement PASSWORD;    

    @FindBy (id = "open-forgot-pwd")
    WebElement FORGOT_PASSWORD;       
    
    @FindBy(id = "forgot-pwd-username")
    WebElement USERNAME_FORGOT_TXTBOX; 
    
    @FindBy(id = "verify-password")
    WebElement PASSWORD_FORGOT_TXTBOX;

    @FindBy (css = "#forgot-pwd-modal [data-target='#login-modal']")
    WebElement BACK_TO_LOGIN_LINKTEXT;  
    
    @FindBy (css = "#login-modal .facebook-login-button")
    WebElement FACEBOOK_LOGIN_ICON;  
    
    @FindBy (css = "#login-modal [data-target='#signup-modal']")
    WebElement CREATE_NEW_ACCOUNT_LINKTEXT;  
    
    @FindBy (css = "#frm-forgot-pwd .btn-submit")
    WebElement CONTINUE_BTN;    
    
    @FindBy (css = "#frm-verify .btn-submit")
    WebElement CONFIRM_BTN;    

    @FindBy (id = "verify-code")
    WebElement VERIFICATION_CODE;       
    
    @FindBy (xpath = "(//button[@class='btn btn-primary btn-block btn-submit'])[1]")
    WebElement LOGIN_BTN;

    @FindBy(css = ".loader")
    WebElement SPINNER;
    
    @FindBy (id = "login-username-error")
    WebElement USER_ERROR;

    @FindBy (id = "login-password-error")
    WebElement PASSWORD_ERROR;    

    @FindBy (id = "login-fail")
    WebElement INVALID_USER_ERROR;	

	@FindBy (id = "forgot-pwd-fail")
	WebElement FORGOT_PASSWORD_NONEXISTING_ACCOUNT_ERROR;	
    
    public LoginPage navigate() {
        driver.get(DOMAIN1);
        wait.until(ExpectedConditions.titleIs(LOGIN_PAGE_TITLE1));
        return this;
    }
    public LoginPage navigate(String domain) {
        driver.get(domain);
        logger.debug("Page title is: "+driver.getTitle());
        return this;
    }

    public LoginPage selectCountry(String country) {
    	commonAction.clickElement(COUNTRY_DROPDOWN);
    	driver.findElement(By.xpath("//ul[@id='login-country-code-menu']//a[@class='dropdown-item']/span[text()='%s']".formatted(country))).click();
    	logger.info("Selected country: " + country);
    	return this;
    }        
    
    public LoginPage selectCountryForgot(String country) {
    	commonAction.clickElement(COUNTRY_FORGOT_DROPDOWN);
    	driver.findElement(By.xpath("//ul[@id='forgot-pwd-country-code-menu']//a[@class='dropdown-item']/span[text()='%s']".formatted(country))).click();
    	String[] selectedOption = COUNTRY_FORGOT_DROPDOWN.getText().split("\n");
    	logger.info("Selected country '%s'. Its according code is '%s'.".formatted(selectedOption[0],selectedOption[1]));
    	this.country = selectedOption[0];
    	this.countryCode = selectedOption[1];    	
    	return this;
    }        
    
    public LoginPage inputEmailOrPhoneNumber(String username) {
    	commonAction.inputText(USERNAME, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.inputText(PASSWORD, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }    

    public LoginPage inputUsernameForgot(String username) {
    	commonAction.inputText(USERNAME_FORGOT_TXTBOX, username);
    	logger.info("Input '" + username + "' into Username field to get a new password.");
        return this;
    }

    public LoginPage inputPasswordForgot(String password) {
    	commonAction.inputText(PASSWORD_FORGOT_TXTBOX, password);
    	logger.info("Input '" + password + "' into Password field to get a new password.");
        return this;
    }    
    
    public LoginPage clickLoginBtn() {
    	commonAction.clickElement(LOGIN_BTN);
    	logger.info("Clicked on Login button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }

    public LoginPage performLogin(String username, String password) {
        new GeneralSF(driver).waitTillLoaderDisappear();
    	new HeaderSF(driver).clickUserInfoIcon()
    	.clickLoginIcon();
    	inputEmailOrPhoneNumber(username);
    	inputPassword(password);
    	clickLoginBtn();
        return this;
    }

    public LoginPage performLogin(String country, String username, String password) {
    	new HeaderSF(driver).clickUserInfoIcon()
    	.clickLoginIcon();
    	selectCountry(country);
    	inputEmailOrPhoneNumber(username);
    	inputPassword(password);
    	clickLoginBtn();
    	return this;
    }    
    
    public LoginPage clickForgotPassword() {
    	commonAction.clickElement(FORGOT_PASSWORD);
    	logger.info("Clicked on Forgot Password linktext.");
    	return this;
    }   
    
    public LoginPage clickFacebookIcon() {
    	commonAction.clickElement(FACEBOOK_LOGIN_ICON);
    	logger.info("Clicked on Facebook login icon.");
    	commonAction.sleepInMiliSecond(1000);
    	return this;
    }  
    
    public LoginPage clickBackToLogin() {
    	commonAction.clickElement(BACK_TO_LOGIN_LINKTEXT);
    	logger.info("Clicked on 'Back To Login' linktext.");
    	return this;
    }  
    
    public SignupPage clickCreateNewAccount() {
    	commonAction.clickElement(CREATE_NEW_ACCOUNT_LINKTEXT);
    	logger.info("Clicked on 'Create New Account' linktext.");
    	return new SignupPage(driver);
    }    

    public LoginPage clickContinueBtn() {
    	commonAction.clickElement(CONTINUE_BTN);
    	logger.info("Clicked on Continue button.");
    	return this;
    }    
    
    public LoginPage clickConfirmBtn() {
    	commonAction.clickElement(CONFIRM_BTN);
    	logger.info("Clicked on Confirm button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
    	return this;
    }    

    public LoginPage inputVerificationCode(String verificationCode) {
    	commonAction.inputText(VERIFICATION_CODE, verificationCode);
    	logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }   
    
    public LoginPage verifyEmailOrPhoneNumberError(String errMessage) {
        String text = commonAction.getText(USER_ERROR);
        soft.assertEquals(text, errMessage, "[Login][Email or Phone Number] Message does not match.");
        logger.info("verifyEmailOrPhoneNumberError completed");
        return this;
    }

    public LoginPage verifyPasswordError(String errMessage) {
        String text = commonAction.getText(PASSWORD_ERROR);
        soft.assertEquals(text,errMessage, "[Login][Password] Message does not match.");
        logger.info("verifyPasswordError completed");
        return this;
    }

    public LoginPage verifyEmailOrPasswordIncorrectError(String errMessage) {
        String text = commonAction.getText(INVALID_USER_ERROR);
        soft.assertEquals(text,errMessage, "[Login][Invalid Email/Password] Message does not match.");
        logger.info("verifyEmailOrPasswordIncorrectError completed");
        return this;
    }    
    
    public LoginPage verifyForgetPasswordForNonExistingAccountError(String errMessage) {
    	String text = commonAction.getText(FORGOT_PASSWORD_NONEXISTING_ACCOUNT_ERROR);
    	soft.assertEquals(text,errMessage, "[Login][Forgot password] Message does not match.");
    	logger.info("verifyForgetPasswordForNonExistingAccountError completed");
    	return this;
    }      
  
    public void completeVerify() {
        soft.assertAll();
    }

    void inputEmailOrPhoneNumberJS(String username) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '%s'".formatted(username), USERNAME);
        logger.info("Input '" + username + "' into Username field.");
    }

    void inputPasswordJS(String password) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '%s'".formatted(password), PASSWORD);
        logger.info("Input '" + password + "' into Password field.");
    }

    public void clickLoginBtnJS() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LOGIN_BTN);
        logger.info("Clicked on Login button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
    }

    public void performLoginJS(String username, String password) {
        if (apiStoreURL == null) new StoreInformation().getStoreInformation();
        navigate("https://%s%s/".formatted(apiStoreURL, SF_DOMAIN));
        new GeneralSF(driver).waitTillLoaderDisappear();
        new HeaderSF(driver).clickUserInfoIconJS()
                .clickLoginIconJS();
        inputEmailOrPhoneNumberJS(username);
        inputPasswordJS(password);
        clickLoginBtnJS();
    }

    public void verifyTextAtLoginScreen(String signinLanguage) throws Exception {
        String text = commonAction.getText(SIGNIN_SCREEN_TXT);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.screen.text", signinLanguage));
        logger.info("verifyTextAtLoginScreen completed");
    }
    
    public void verifyTextAtForgotPasswordScreen(String signinLanguage) throws Exception {
    	String text = commonAction.getText(SIGNIN_FORGOTPASSWORD_TXT);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.forgotPassword.text", signinLanguage));
    	logger.info("verifyTextAtForgotPasswordScreen completed");
    }    
    
}
