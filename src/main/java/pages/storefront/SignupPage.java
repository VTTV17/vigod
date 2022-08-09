package pages.storefront;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonAction;
import utilities.database.InitConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static utilities.links.Links.*;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".navbar-brand.nav-link")
    WebElement USER_INFO_ICON;    

    @FindBy(css = "#btn-signup")
    WebElement SIGNUP_ICON;
    
    @FindBy(css = "#signup-username")
    WebElement USERNAME;
    
    @FindBy(css = "#signup-displayName")
    WebElement DISPLAY_USERNAME;
    
    @FindBy(css = "#signup-dob")
    WebElement BIRTHDAY;

    @FindBy (css = "#signup-password")
    WebElement PASSWORD;

    @FindBy (css = "#frm-signup .btn-submit")
    WebElement SIGNUP_BTN;

    @FindBy (css = "div.uik-select__valueWrapper>div>div:nth-child(2)")
    WebElement COUNTRY_CODE;

    @FindBy (css = "#signup-country-code")
    WebElement COUNTRY_DROPDOWN;

    @FindBy (css = "button.uik-select__option>span>div>div>div>div:nth-child(1)")
    List<WebElement> COUNTRY_LIST;

    @FindBy (css = "#activate-code")
    WebElement OTP;

    @FindBy (css = "#frm-activate .btn-submit")
    WebElement CONFIRM_OTP;

    @FindBy (css = ".resend-otp")
    WebElement RESEND_OTP;    
    
    public SignupPage navigate() {
        driver.get(DOMAIN1);
        return this;
    }
    
    public SignupPage clickUserInfoIcon() {
    	commonAction.clickElement(USER_INFO_ICON);
    	logger.info("Clicked on User Info icon.");
        return this;
    }    
    
    public SignupPage clickSignupIcon() {
    	commonAction.clickElement(SIGNUP_ICON);
    	logger.info("Clicked on Signup icon.");   
    	return this;
    }    
    
    public SignupPage selectCountry(String country) {
    	commonAction.clickElement(COUNTRY_DROPDOWN);
    	driver.findElement(By.xpath("//ul[@id='signup-country-code-menu']//span[text()='%s']".formatted(country))).click();
    	logger.info("Selected country: " + country);
    	return this;
    }

    public SignupPage inputMailOrPhoneNumber(String user) {
    	commonAction.inputText(USERNAME, user);
    	logger.info("Input '" + user + "' into Username field.");
        return this;
    }

    public SignupPage inputPassword(String password) {
    	commonAction.inputText(PASSWORD, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }
    
    public SignupPage inputDisplayName(String name) {
    	commonAction.inputText(DISPLAY_USERNAME, name);
    	logger.info("Input '" + name + "' into Display Name field.");
    	return this;
    }
    
    public SignupPage inputBirthday(String date) {
    	commonAction.inputText(BIRTHDAY, date);
    	logger.info("Input '" + date + "' into Birthday field.");
    	return this;
    }

    public SignupPage clickSignupBtn() {
    	commonAction.clickElement(SIGNUP_BTN);
    	logger.info("Clicked on Signup button.");  
        return this;
    }

    public SignupPage fillOutSignupForm(String country, String user, String password, String displayName, String birthday) {
		clickUserInfoIcon();
		clickSignupIcon();
    	selectCountry(country);
    	inputMailOrPhoneNumber(user);
    	inputPassword(password);
    	inputDisplayName(displayName);
    	inputBirthday(birthday);
    	clickSignupBtn();
        return this;
    }        
    
    public SignupPage inputVerificationCode(String verificationCode) throws SQLException {
    	commonAction.inputText(OTP, verificationCode);
    	logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }

    public void clickConfirmBtn() {
    	commonAction.clickElement(CONFIRM_OTP);
    	logger.info("Clicked on Confirm button."); 
    }
    
}
