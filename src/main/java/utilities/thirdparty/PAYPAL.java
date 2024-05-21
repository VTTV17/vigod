package utilities.thirdparty;

import static utilities.account.AccountTest.PAYPAL_PASSWORD;
import static utilities.account.AccountTest.PAYPAL_USERNAME;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.commons.UICommonAction;

public class PAYPAL {

	final static Logger logger = LogManager.getLogger(PAYPAL.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public PAYPAL(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "email")
	WebElement USERNAME;
	
	@FindBy(id = "btnNext")
	WebElement NEXT_BTN;

	@FindBy(id = "password")
	WebElement PASSWORD;

	@FindBy(id = "btnLogin")
	WebElement LOGIN_BTN;

	@FindBy(id = "payment-submit-btn")
	WebElement COMPLETE_PURCHASE_BTN;

	public PAYPAL inputUsername(String username) {
		commonAction.inputText(USERNAME, username);
		logger.info("Input '" + username + "' into Username field.");
		return this;
	}

	public PAYPAL clickNext() {
		commonAction.clickElement(NEXT_BTN);
		logger.info("Clicked on 'Next' button.");
		return this;
	}	
	
	public PAYPAL inputPassword(String password) {
		commonAction.inputText(PASSWORD, password);
		logger.info("Input '" + password + "' into Password field.");
		return this;
	}

	public PAYPAL clickLogin() {
		commonAction.clickElement(LOGIN_BTN);
		logger.info("Clicked on 'Login' button.");
		return this;
	}

	public PAYPAL clickCompletePurchase() {
		// Without the 2 consecutive lines below, the new tab won't disappear on its own
//		wait.until(ExpectedConditions.visibilityOf(PAYNOW_BTN));
//		commonAction.sleepInMiliSecond(10000); 
		commonAction.clickElement(COMPLETE_PURCHASE_BTN);
		logger.info("Clicked on 'Complete Purchase' button.");
		return this;
	}

	public PAYPAL completePayment() {
		completePayment(PAYPAL_USERNAME, PAYPAL_PASSWORD);
		return this;
	}
	
	public PAYPAL completePayment(String username, String password) {
		inputUsername(username);
		clickNext();
		inputPassword(password);
		clickLogin();
		clickCompletePurchase();
		return this;
	}

}
