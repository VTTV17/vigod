package utilities.thirdparty;

import static utilities.account.AccountTest.PAYPAL_PASSWORD;
import static utilities.account.AccountTest.PAYPAL_USERNAME;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.commons.UICommonAction;

public class PAYPAL {

	final static Logger logger = LogManager.getLogger(PAYPAL.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public PAYPAL(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	//Will move these locators to a separate file
	By loc_btnCancel = By.id("cancelLink");
	By loc_txtUsername = By.id("email");
	By loc_btnNext = By.id("btnNext");
	By loc_txtPassword = By.id("password");
	By loc_btnLogin = By.id("btnLogin");
	By loc_btnCompletePurchase = By.id("payment-submit-btn");

	public PAYPAL clickCancelBtn() {
		commonAction.click(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button.");
		return this;
	}		
	
	public PAYPAL inputUsername(String username) {
		commonAction.inputText(loc_txtUsername, username);
		logger.info("Input Username: {}", username);
		return this;
	}
	public PAYPAL clickNext() {
		commonAction.click(loc_btnNext);
		logger.info("Clicked 'Next' button.");
		return this;
	}	
	public PAYPAL inputPassword(String password) {
		commonAction.inputText(loc_txtPassword, password);
		logger.info("Input Password: {}", password);
		return this;
	}
	public PAYPAL clickLogin() {
		commonAction.click(loc_btnLogin);
		logger.info("Clicked 'Login' button.");
		return this;
	}
	public PAYPAL clickCompletePurchase() {
		commonAction.click(loc_btnCompletePurchase);
		logger.info("Clicked on 'Complete Purchase' button.");
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
	public PAYPAL completePayment() {
		completePayment(PAYPAL_USERNAME, PAYPAL_PASSWORD);
		return this;
	}
	
	public PAYPAL abandonPayment() {
		clickCancelBtn();
		return this;
	}		
}
