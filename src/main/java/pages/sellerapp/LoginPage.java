package pages.sellerapp;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class LoginPage {

	final static Logger logger = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    public LoginPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    By USERNAME = By.id("com.mediastep.GoSellForSeller.STG:id/edtUsername");
    By PASSWORD = By.id("com.mediastep.GoSellForSeller.STG:id/edtPassword");
    By TERM_CHECKBOX = By.id("com.mediastep.GoSellForSeller.STG:id/cbxTermAndPrivacy");
    By LOGIN_BTN = By.id("com.mediastep.GoSellForSeller.STG:id/btnLogin");
    
    By INVALID_PHONE_ERROR = By.id("com.mediastep.GoSellForSeller.STG:id/tvErrorUsername");
    
    public LoginPage clickUsername() {
    	commonAction.getElement(USERNAME, 5).click();
    	logger.info("Clicked on Username field.");
    	return this;
    }    
    
    public LoginPage inputUsername(String username) {
    	WebElement txtUsername = commonAction.getElement(USERNAME, 5);
    	txtUsername.clear();
    	txtUsername.sendKeys(username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	WebElement txtPassword = commonAction.getElement(PASSWORD, 5);
    	txtPassword.clear();
    	txtPassword.sendKeys(password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }
    
    public boolean isTermAgreementChecked() {
    	boolean isChecked = commonAction.getElement(TERM_CHECKBOX, 5).getAttribute("checked").equals("true");
    	logger.info("Is Term Agreement checkbox checked: " + isChecked);
    	return isChecked;
    }
    
    public LoginPage clickAgreeTerm() {
    	if (isTermAgreementChecked()) {
    		return this;
    	}
    	commonAction.getElement(TERM_CHECKBOX, 5).click();
    	logger.info("Clicked on Term Agreement checkbox.");
    	return this;
    }

    public LoginPage clickLoginBtn() {
    	commonAction.getElement(LOGIN_BTN, 5).click();
    	logger.info("Clicked on Login button.");
        return this;
    }
    
    public boolean isLoginBtnEnabled() {
    	boolean isEnabled = commonAction.getElement(LOGIN_BTN, 5).isEnabled();
    	logger.info("Is Login button enabled: " + isEnabled);
    	return isEnabled;
    }
    
    public String getUsernameError() {
    	String text = commonAction.getElement(INVALID_PHONE_ERROR, 5).getText();
    	logger.info("Retrieved error for username field: " + text);
    	return text;
    }

}
