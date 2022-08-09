package pages.dashboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.UICommonAction;

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
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = "div.uik-select__valueRenderedWrapper")
    WebElement COUNTRY_DROPDOWN;    
    
    @FindBy(css = "input[name='username']")
    WebElement USERNAME;

    @FindBy (css = "input[name='password']")
    WebElement PASSWORD;

    @FindBy (css = "button.gs-button")
    WebElement LOGIN_BTN;

    @FindBy (css = "#username + .invalid-feedback")
    WebElement USER_ERROR;

    @FindBy (css = "#password + .invalid-feedback")
    WebElement PASSWORD_ERROR;    
    
    @FindBy (css = "div.alert__wrapper")
    WebElement INVALID_USER_ERROR;	
    
    @FindBy (css = ".login-widget__btnSubmitFaceBook")
    WebElement FACEBOOK_BTN;	
    
    @FindBy(css = "#email")
    WebElement FACEBOOK_USERNAME;	

    @FindBy (css = "#pass")
    WebElement FACEBOOK_PASSWORD;

    @FindBy (css = "input[name='login']")
    WebElement FACEBOOK_LOGIN_BTN;        
    
    @FindBy (css = "span.login-widget__tab:nth-child(2)")
    WebElement STAFF_TAB;

    @FindBy (css = "div.modal-content")
    WebElement WARNING_POPUP;
    
    @FindBy (css = ".login-widget__forgotPassword")
    WebElement FORGOT_PASSWORD;
    
    @FindBy (css = ".login-widget__btnSubmit")
    WebElement CONTINUE_BTN;

    @FindBy (css = "input[name='key']")
    WebElement VERIFICATION_CODE;    
    
    public LoginPage navigate() {
        driver.get(DOMAIN + LOGIN_PATH);
        wait.until(ExpectedConditions.titleIs(LOGIN_PAGE_TITLE));
        return this;
    }

    public LoginPage selectCountry(String country) {
    	commonAction.clickElement(COUNTRY_DROPDOWN);
    	driver.findElement(By.xpath("//*[@class='uik-select__optionList']//div[@class='phone-option']/div[text()='%s']".formatted(country))).click();
    	logger.info("Selected country: " + country);
    	return this;
    }    
    
    public LoginPage switchToStaffTab() {
    	commonAction.clickElement(STAFF_TAB);
    	logger.info("Switched to Staff Tab.");
        return this;
    }

    public LoginPage clickFacebookBtn() {
    	commonAction.clickElement(FACEBOOK_BTN);
    	logger.info("Clicked on Facebook linktext.");
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

    public LoginPage inputFacebookUsername(String username) {
    	commonAction.inputText(FACEBOOK_USERNAME, username);
    	logger.info("Input '" + username + "' into Facebook Username field.");
        return this;
    }

    public LoginPage inputFacebookPassword(String password) {
    	commonAction.inputText(FACEBOOK_PASSWORD, password);
    	logger.info("Input '" + password + "' into Facebook Password field.");
        return this;
    }    
    
    public LoginPage clickLoginBtn() {
    	commonAction.clickElement(LOGIN_BTN);
    	logger.info("Clicked on Login button.");
        return this;
    }

    public LoginPage clickFacebookLoginBtn() {
    	commonAction.clickElement(FACEBOOK_LOGIN_BTN);
    	logger.info("Clicked on Facebook Login button.");
        return this;
    }
    
    public LoginPage clickForgotPassword() {
    	commonAction.clickElement(FORGOT_PASSWORD);
    	logger.info("Clicked on Forgot Password linktext.");
    	return this;
    }
    
    public LoginPage clickContinueOrConfirmBtn() {
    	commonAction.clickElement(CONTINUE_BTN);
    	logger.info("Clicked on Continue/Confirm button.");
    	return this;
    }

    public LoginPage inputVerificationCode(String verificationCode) {
    	commonAction.inputText(VERIFICATION_CODE, verificationCode);
    	logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }
    
    public LoginPage performLogin(String username, String password) {
    	inputEmailOrPhoneNumber(username);
    	inputPassword(password);
    	clickLoginBtn();
        return this;
    }
    
    public LoginPage performLogin(String country, String username, String password) {
    	selectCountry(country);
    	inputEmailOrPhoneNumber(username);
    	inputPassword(password);
    	clickLoginBtn();
    	return this;
    }

    public LoginPage performLoginWithFacebook(String username, String password) {
    	String originalWindow = commonAction.getCurrentWindowHandle();
    	
    	clickFacebookBtn();
    	
    	for (String windowHandle : commonAction.getAllWindowHandles()) {
    	    if(!originalWindow.contentEquals(windowHandle)) {
    	        commonAction.switchToWindow(windowHandle);
    	        break;
    	    }
    	}
    	
    	inputFacebookUsername(username);
    	inputFacebookPassword(password);
    	clickFacebookLoginBtn();
    	commonAction.switchToWindow(originalWindow);
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

    public void verifyLoginWithDeletedStaffAccount(String content) {
        wait.until(ExpectedConditions.visibilityOf(WARNING_POPUP));
        Assert.assertTrue(WARNING_POPUP.getText().contains(content),
                "[Login][Deleted Staff Account] No warning popup has been shown");
    }

    public void completeVerify() {
        soft.assertAll();
    }    
    
}
