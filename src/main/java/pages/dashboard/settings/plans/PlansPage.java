package pages.dashboard.settings.plans;

import static utilities.links.Links.LOGIN_PAGE_TITLE;

import java.time.Duration;

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
import pages.internaltool.InternalTool;
import pages.thirdparty.ATM;
import pages.thirdparty.PAYPAL;
import pages.thirdparty.VISA;
import utilities.UICommonAction;
import utilities.enums.PaymentMethod;

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

	@FindBy(css = "img[src*='atm']")
	WebElement ATM_RADIO_BTN;

	@FindBy(css = "img[src*='visa']")
	WebElement VISA_RADIO_BTN;

	@FindBy(css = "img[src*='paypal']")
	WebElement PAYPAL_RADIO_BTN;

	@FindBy(css = ".setting-plans-step3__overlay")
	WebElement OVERLAY_ELEMENT;

	String PLAN_PRICE_12M = "//tr[contains(@class,'plan-price')]//td[count(//div[text()='%planName%']//ancestor::th/preceding-sibling::*)+1]//button[not(contains(@class,'price-btn--disable'))]";
	final static Logger logger = LogManager.getLogger(PlansPage.class);

	public PlansPage selectPlan(String planName) {
		commons.waitForElementInvisible(LOADING, 20);
		String newXpath = PLAN_PRICE_12M.replace("%planName%", planName);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		commons.clickElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(newXpath))));
		logger.info("Select plan: " + planName);
		commons.sleepInMiliSecond(1000);
		return this;
	}

	public PlansPage selectPayment() {
		commons.clickElement(BANK_TRANSFER_BTN);
		logger.info("Click bank transfer");
		commons.clickElement(PAY_BTN);
		logger.info("Click Pay button");
		return this;
	}

	/**
	 * Selects a payment method
	 * @param method
	 * @return the selected payment method. If an unsupported payment method is passed as input, it returns the same input value.
	 */
	public PaymentMethod selectPaymentMethod(PaymentMethod method) {
		switch (method) {
		case BANKTRANSFER:
			commons.clickElement(BANK_TRANSFER_BTN);
			break;
		case ATM:
			commons.clickElement(ONLINE_PAYMENT_BTN);
			commons.checkTheCheckBoxOrRadio(ATM_RADIO_BTN);
			break;
		case VISA:
			commons.clickElement(ONLINE_PAYMENT_BTN);
			commons.checkTheCheckBoxOrRadio(VISA_RADIO_BTN);
			break;
		case PAYPAL:
			commons.clickElement(ONLINE_PAYMENT_BTN);
			commons.checkTheCheckBoxOrRadio(PAYPAL_RADIO_BTN);
			break;
		default:
			logger.error("Unsupported payment method: " + method);
			return method;
		}
		
		commons.clickElement(PAY_BTN);
		commons.sleepInMiliSecond(1000);
		logger.info("Payment method selected: " + method);
		return method;
	}	
	
	/**
	 * Selects a payment method
	 * @param method Possible values are "BANKTRANSFER", "ATM", "VISA" and "PAYPAL"
	 * @return the selected payment method. If an unsupported payment method is passed as input, it returns the same input value.
	 */
	public String selectPaymentMethod(String method) {
		PaymentMethod payBy = PaymentMethod.valueOf(method);
		return selectPaymentMethod(payBy).name();
	}
	
	public String completePayment(PaymentMethod method) {
		if (method.equals(PaymentMethod.BANKTRANSFER)) {
			return getOrderId();
		}
		
		String currentWindowHandle = commons.getCurrentWindowHandle();
		int currentNumberOfWindows = commons.getAllWindowHandles().size();
		
		commons.switchToWindow(1);
		
		switch (method) {
		case ATM:
			new ATM(driver).completePayment();
			break;
		case VISA:
			new VISA(driver).completePayment();
			break;
		case PAYPAL:
			new PAYPAL(driver).completePayment();
			break;
		case BANKTRANSFER:
			//Will be removed later
			break;
		default:
			//Will be removed later
			break;
		}
		
		//Wait till the latest tab is closed
		for (int i=9; i>=0; i--) {
			if (commons.getAllWindowHandles().size() != currentNumberOfWindows) {
				break;
			}
			commons.sleepInMiliSecond(2000);
		}
		commons.switchToWindow(currentWindowHandle);
		return getOrderId();
	}

	/**
	 * @param method Input value: BANKTRANSFER/ATM/VISA/PAYPAL
	 */
	public String completePayment(String method) {
		PaymentMethod payBy = PaymentMethod.valueOf(method);
		return completePayment(payBy);
	}	
	
	public String getOrderId() {
		commons.sleepInMiliSecond(1000);
		logger.info("Getting orderID...");
		return commons.getText(ORDER_ID);
	}
	
	public PlansPage clickOnLogOut() {
		commons.sleepInMiliSecond(1000);
		commons.clickElement(LOGOUT_BTN);
		new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.titleIs(LOGIN_PAGE_TITLE));
		logger.info("Clicked on Logout link");
		return this;
	}
	
	/**
	 * Clicks on the element that obscures the screen
	 */
	public PlansPage clickOverlayElement() {
		commons.clickElement(OVERLAY_ELEMENT);
		logger.info("Clicked on overlay element");
		return this;
	}

	public void logoutAfterSuccessfulPurchase(PaymentMethod method, String orderID) {
		if (method.equals(PaymentMethod.BANKTRANSFER)) {
			new InternalTool(driver).openNewTabAndNavigateToInternalTool()
			.login().navigateToPage("GoSell","Packages","Orders list").approveOrder(orderID).closeTab();
			new HomePage(driver).clickLogout();
			return;
		}
		if (method.equals(PaymentMethod.PAYPAL)) {
			InternalTool internal = new InternalTool(driver);
			internal.openNewTabAndNavigateToInternalTool().login().navigateToPage("GoSell","Packages","Orders list");
			for (int i=0; i<20; i++) {
				if (internal.getOrderApprovalStatus(orderID).contentEquals("Approved")) {
					break;
				}
				commons.sleepInMiliSecond(3000);
				commons.refreshPage();
			}
			internal.closeTab();
		}
		clickOverlayElement();
		new ForceLogOutDialog(driver).clickLogOutBtn();
	}
	
	public void logoutAfterSuccessfulPurchase(String method, String orderID) {
		PaymentMethod payBy = PaymentMethod.valueOf(method);
		logoutAfterSuccessfulPurchase(payBy, orderID);
	}	
	
}
