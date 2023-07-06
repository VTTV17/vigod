package pages.buyerapp;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import pages.storefront.GeneralSF;
import utilities.UICommonMobile;

public class LoginPage {

	final static Logger logger = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    By USERNAME = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]//*[ends-with(@resource-id,'edittext')]");
    By PASSWORD = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]//*[ends-with(@resource-id,'edittext')]");
    By LOGIN_BTN = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");
    By SIGNUP_LINKTEXT = By.xpath("//*[ends-with(@resource-id,'txt_sign_up')]");
    By PHONE_TAB = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");
    By FORGOTPASSWORD = By.xpath("//*[ends-with(@resource-id,'forgot_pass')]");
    By USERNAME_FORGOTPASSWORD = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[1]");
    By PASSWORD_FORGOTPASSWORD = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[2]");

    public LoginPage clickUsername() {
    	commonAction.clickElement(USERNAME, defaultTimeout);
    	logger.info("Clicked on Username field.");
    	return this;
    }    
    
    public LoginPage inputUsername(String username) {
    	commonAction.inputText(USERNAME, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.inputText(PASSWORD, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public boolean isLoginBtnEnabled() {
    	boolean isEnabled = commonAction.isElementEnabled(LOGIN_BTN);
    	logger.info("Is 'Login' button enabled: " + isEnabled);
    	return isEnabled;
    }    
    
    public LoginPage clickLoginBtn() {
    	commonAction.clickElement(LOGIN_BTN, defaultTimeout);
    	logger.info("Clicked on Login button.");
        return this;
    }

    public LoginPage clickSignupLinkText() {
    	commonAction.clickElement(SIGNUP_LINKTEXT, defaultTimeout);
    	logger.info("Clicked on 'Signup' link text.");
        return this;
    }
    
    public LoginPage performLogin(String userName, String pass){
        if (!userName.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
            clickPhoneTab();
        }
        inputUsername(userName);
        inputPassword(pass);
        commonAction.sleepInMiliSecond(1000);
        clickLoginBtn();
        return this;
    }
    
    public LoginPage performLogin(String country, String username, String password){
    	if (username.matches("\\d+")) {
    		clickPhoneTab();
    		new SignupPage(driver).selectCountryCodeFromSearchBox(country);
    	}
    	inputUsername(username);
    	inputPassword(password);
    	clickLoginBtn();
    	return this;
    }
    public LoginPage clickPhoneTab() {
        commonAction.clickElement(PHONE_TAB);
        logger.info("Clicked on Phone tab.");
        return this;
    }
    public LoginPage clickForgotPasswordLink() {
    	commonAction.clickElement(FORGOTPASSWORD, defaultTimeout);
    	logger.info("Clicked on 'Forgot Password' link text.");
        return this;
    }    
    
    public LoginPage inputUsernameForgotPassword(String username) {
    	commonAction.inputText(USERNAME_FORGOTPASSWORD, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputNewPassword(String password) {
        commonAction.inputText(PASSWORD_FORGOTPASSWORD, password);
        logger.info("Input '" + password + "' into New Password field.");
        return this;
    }
    public LoginPage verifyToastMessage(String expected){
        new BuyerGeneral(driver).verifyToastMessage(expected);
        return this;
    }
}
