package pages.buyerapp;

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

    By USERNAME = By.xpath("(//*[contains(@resource-id,'id/fragment_social_login_email_email_field')]//*[contains(@resource-id,'id/social_layout_limit_edittext')])[2]");
    By PASSWORD = By.xpath("(//*[contains(@resource-id,'id/fragment_social_login_email_password_field')]//*[contains(@resource-id,'id/social_layout_limit_edittext')])[2]");
    By LOGIN_BTN = By.xpath("//*[contains(@resource-id,'id/fragment_social_login_email_check_email')]");
    
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

    public LoginPage clickLoginBtn() {
    	commonAction.getElement(LOGIN_BTN, defaultTimeout).click();
    	logger.info("Clicked on Login button.");
        return this;
    }
    public LoginPage performLogin(String userName, String pass){
        inputUsername(userName);
        inputPassword(pass);
        clickLoginBtn();
        return this;
    }
}
