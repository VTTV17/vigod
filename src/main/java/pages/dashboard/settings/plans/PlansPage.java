package pages.dashboard.settings.plans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.thirdparty.ATM;
import pages.thirdparty.PAYPAL;
import pages.thirdparty.VISA;
import utilities.UICommonAction;
import static utilities.account.AccountTest.*;

import java.time.Duration;

public class PlansPage extends HomePage {
	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commons;

	public PlansPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//button[contains(@class,'btn-pay')]")
	WebElement PAY_BTN;
	@FindBy(xpath = "//div[contains(@class,'loading-wrapper')]")
	WebElement LOADING;
	@FindBy(xpath = "//table[@class='d-tablet-none d-desktop-exclude-tablet-block']//tr[1]//td[@class='value']")
	WebElement ORDER_ID;

	@FindBy(css = ".btn-group button:nth-of-type(1)")
	WebElement ONLINE_PAYMENT_BTN;
	@FindBy(css = ".btn-group button:nth-of-type(2)")
	WebElement BANK_TRANSFER_BTN;

	@FindBy(css = ".wizard-layout__content a[href='/logout']")
	WebElement LOGOUT_BTN;

	@FindBy(css = ".online-payment div div label:nth-of-type(1)")
	WebElement ATM_RADIO_BTN;

	@FindBy(css = ".online-payment div div label:nth-of-type(2)")
	WebElement VISA_RADIO_BTN;

	@FindBy(css = ".online-payment div div label:nth-of-type(3)")
	WebElement PAYPAL_RADIO_BTN;

	String PLAN_PRICE_12M = "//tr[contains(@class,'plan-price')]//td[count(//div[text()='%planName%']//ancestor::th/preceding-sibling::*)+1]//button[not(contains(@class,'price-btn--disable'))]";
	final static Logger logger = LogManager.getLogger(PlansPage.class);

	public PlansPage selectPlan(String planName) {
		commons.waitForElementInvisible(LOADING, 20);
		String newXpath = PLAN_PRICE_12M.replace("%planName%", planName);
		commons.clickElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newXpath))));
		logger.info("Select plan: " + planName);
		return this;
	}

	public PlansPage selectPayment() {
		commons.clickElement(BANK_TRANSFER_BTN);
		logger.info("Click bank transfer");
		commons.clickElement(PAY_BTN);
		logger.info("Click Pay button");
		return this;
	}

	public String selectPaymentMethod(String method) {
		if (method.contentEquals("ATM")) {
			commons.clickElement(ONLINE_PAYMENT_BTN);
			commons.checkTheCheckBoxOrRadio(ATM_RADIO_BTN);
		} else if (method.contentEquals("VISA")) {
			commons.clickElement(ONLINE_PAYMENT_BTN);
			commons.checkTheCheckBoxOrRadio(VISA_RADIO_BTN);
		} else if (method.contentEquals("PAYPAL")) {
			commons.clickElement(ONLINE_PAYMENT_BTN);
			commons.checkTheCheckBoxOrRadio(PAYPAL_RADIO_BTN);
		} else if (method.contentEquals("BANKTRANSFER")) {
			commons.clickElement(BANK_TRANSFER_BTN);
		} else {
			logger.error("Unsupported payment method: " + method);
			return method;
		}
		commons.clickElement(PAY_BTN);
		commons.sleepInMiliSecond(1000);
		logger.info("Payment method selected: " + method);
		return method;
	}

	public PlansPage purchasePlan(String plan, String paymentMethod) {
		selectPlan(plan);
		selectPaymentMethod(paymentMethod);
		if (paymentMethod.contentEquals("ATM")) {
			commons.switchToWindow(1);
			ATM atm = new ATM(driver);
			atm.completePayment(ATM_BANK, ATM_CARDNUMBER, ATM_CARDHOLDER, ATM_ISSUINGDATE, ATM_OTP);
			commons.sleepInMiliSecond(7000);
			commons.switchToWindow(0);
		} else if (paymentMethod.contentEquals("VISA")) {
			commons.switchToWindow(1);
			VISA visa = new VISA(driver);
			visa.completePayment(VISA_CARDNUMBER, VISA_EXPIRYDATE, VISA_CCV, VISA_CARDHOLDER, VISA_EMAIL, VISA_COUNTRY,
					VISA_CITY, VISA_ADDRESS, VISA_OTP);
			commons.sleepInMiliSecond(7000);
			commons.switchToWindow(0);
		} else if (paymentMethod.contentEquals("PAYPAL")) {
			commons.switchToWindow(1);
			PAYPAL paypal = new PAYPAL(driver);
			paypal.completePayment(PAYPAL_USERNAME, PAYPAL_PASSWORD);
			commons.sleepInMiliSecond(7000);
			commons.switchToWindow(0);
		}
		logger.info("Purchased plan '%s' and paid for it via '%s' successfully".formatted(plan, paymentMethod));
		return this;
	}

	public String getOrderId() {
		logger.info("Get orderID: " + commons.getText(ORDER_ID));
		return commons.getText(ORDER_ID);
	}

	public PlansPage clickOnLogOut() {
		commons.clickElement(LOGOUT_BTN);
		logger.info("Clicked on Logout link");
		return this;
	}
}
