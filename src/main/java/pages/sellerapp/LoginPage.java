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

    int defaultTimeout = 5;
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    By COUNTRYCODE = By.id("com.mediastep.GoSellForSeller.STG:id/edtCountry");
    By COUNTRY_SEARCHBOX = By.id("com.mediastep.GoSellForSeller.STG:id/edtCountriesSearch");
    By COUNTRY_SEARCHRESULT = By.id("com.mediastep.GoSellForSeller.STG:id/tvValue");
    
    By USERNAME = By.id("com.mediastep.GoSellForSeller.STG:id/edtUsername");
    By PASSWORD = By.id("com.mediastep.GoSellForSeller.STG:id/edtPassword");
    By TERM_CHECKBOX = By.id("com.mediastep.GoSellForSeller.STG:id/cbxTermAndPrivacy");
    By LOGIN_BTN = By.id("com.mediastep.GoSellForSeller.STG:id/btnLogin");
    
    By INVALID_USERNAME_ERROR = By.id("com.mediastep.GoSellForSeller.STG:id/tvErrorUsername");
    
    public LoginPage clickUsername() {
    	commonAction.getElement(USERNAME, defaultTimeout).click();
    	logger.info("Clicked on Username field.");
    	return this;
    }    
    
    public LoginPage inputUsername(String username) {
    	WebElement txtUsername = commonAction.getElement(USERNAME, defaultTimeout);
    	txtUsername.clear();
    	txtUsername.sendKeys(username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	WebElement txtPassword = commonAction.getElement(PASSWORD, defaultTimeout);
    	txtPassword.clear();
    	txtPassword.sendKeys(password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }
    
    public boolean isTermAgreementChecked() {
    	boolean isChecked = commonAction.getElement(TERM_CHECKBOX, defaultTimeout).getAttribute("checked").equals("true");
    	logger.info("Is Term Agreement checkbox checked: " + isChecked);
    	return isChecked;
    }
    
    public LoginPage clickAgreeTerm() {
    	if (isTermAgreementChecked()) {
    		return this;
    	}
    	commonAction.getElement(TERM_CHECKBOX, defaultTimeout).click();
    	logger.info("Clicked on Term Agreement checkbox.");
    	return this;
    }

    public LoginPage clickLoginBtn() {
    	commonAction.getElement(LOGIN_BTN, defaultTimeout).click();
    	logger.info("Clicked on Login button.");
        return this;
    }
    
    public boolean isLoginBtnEnabled() {
    	boolean isEnabled = commonAction.getElement(LOGIN_BTN, defaultTimeout).isEnabled();
    	logger.info("Is Login button enabled: " + isEnabled);
    	return isEnabled;
    }
    
    public String getUsernameError() {
    	String text = commonAction.getElement(INVALID_USERNAME_ERROR, defaultTimeout).getText();
    	logger.info("Retrieved error for username field: " + text);
    	return text;
    }

    public LoginPage clickCountryCodeField() {
    	commonAction.getElement(COUNTRYCODE, defaultTimeout).click();
    	logger.info("Clicked on Country code field.");
        return this;
    }    
    
    public LoginPage inputCountryCodeToSearchBox(String country) {
    	commonAction.getElement(COUNTRY_SEARCHBOX, defaultTimeout).sendKeys(country);
    	logger.info("Input Country code: " + country);
    	return this;
    }    
    
    public LoginPage selectCountryCodeFromSearchBox(String country) {
    	clickCountryCodeField();
    	inputCountryCodeToSearchBox(country);
    	
    	commonAction.getElement(COUNTRY_SEARCHRESULT, defaultTimeout).click();
    	logger.info("Selected country: " + country);
    	return this;
    }    
    
}
