package utilities.thirdparty;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.commons.UICommonAction;

public class Facebook {

	final static Logger logger = LogManager.getLogger(Facebook.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public Facebook(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "#email")
	WebElement FACEBOOK_USERNAME;

	@FindBy(css = "#pass")
	WebElement FACEBOOK_PASSWORD;

	@FindBy(css = "input[name='login']")
	WebElement FACEBOOK_LOGIN_BTN;
	
	@FindBy(xpath = "(//div[@role='button']//span[@dir='auto']/span)[1]")
	WebElement CONTINUE_AS_BTN;

	public Facebook inputUsername(String username) {
		commonAction.inputText(FACEBOOK_USERNAME, username);
		logger.info("Input '" + username + "' into Facebook Username field.");
		return this;
	}

	public Facebook inputPassword(String password) {
		commonAction.inputText(FACEBOOK_PASSWORD, password);
		logger.info("Input '" + password + "' into Facebook Password field.");
		return this;
	}

	public Facebook clickLoginBtn() {
		commonAction.clickElement(FACEBOOK_LOGIN_BTN);
		logger.info("Clicked on Facebook Login button.");
		return this;
	}
	
	public Facebook clickContinueAsBtn() {
		commonAction.clickElement(CONTINUE_AS_BTN);
		logger.info("Clicked on 'Continue As' button.");
		return this;
	}

	public Facebook performLogin(String username, String password) {
		inputUsername(username);
		inputPassword(password);
		clickLoginBtn();
		return this;
	}

}
