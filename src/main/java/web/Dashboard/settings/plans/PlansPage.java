package web.Dashboard.settings.plans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import web.Dashboard.home.HomePage;
import web.InternalTool.InternalTool;
import utilities.thirdparty.ATM;
import utilities.thirdparty.PAYPAL;
import utilities.thirdparty.VISA;
import utilities.commons.UICommonAction;
import utilities.enums.PaymentMethod;

public class PlansPage extends HomePage {
	WebDriver driver;
	UICommonAction commons;

	public PlansPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_btnPay = By.xpath("//button[contains(@class,'btn-pay')]");
	By loc_icnLoading = By.xpath("//div[contains(@class,'loading-wrapper')]");
	By loc_lblOrderId = By.xpath("//table[@class='d-tablet-none d-desktop-exclude-tablet-block']//tr[1]//td[@class='value']");
	By loc_btnOnlinePayment = By.cssSelector(".btn-group button:nth-of-type(1)");
	By loc_btnBankTransfer = By.cssSelector(".btn-group button:nth-of-type(2)");
	By loc_btnLogout = By.cssSelector(".wizard-layout__content a[href='/logout']");
	By loc_rdoATM = By.cssSelector("img[src*='atm']");
	By loc_rdoVISA = By.cssSelector("img[src*='visa']");
	By loc_rdoPAYPAL = By.cssSelector("img[src*='paypal']");
	By loc_tmpOverlayElement = By.cssSelector(".setting-plans-step3__overlay");
	
	String PLAN_PRICE_12M = "//tr[contains(@class,'plan-price')]//td[count(//div[text()='%planName%']//ancestor::th/preceding-sibling::*)+1]//button[not(contains(@class,'price-btn--disable'))]";
	final static Logger logger = LogManager.getLogger(PlansPage.class);

	public PlansPage selectPlan(String planName) {
		commons.waitInvisibilityOfElementLocated(loc_icnLoading);
		String newXpath = PLAN_PRICE_12M.replace("%planName%", planName);
		commons.click(By.xpath(newXpath));
		logger.info("Select plan: " + planName);
		commons.sleepInMiliSecond(1000);
		return this;
	}

	public PlansPage selectPayment() {
		commons.click(loc_btnBankTransfer);
		logger.info("Click bank transfer");
		commons.click(loc_btnPay);
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
			commons.click(loc_btnBankTransfer);
			break;
		case ATM:
			commons.click(loc_btnOnlinePayment);
			commons.checkTheCheckBoxOrRadio(loc_rdoATM);
			break;
		case VISA:
			commons.click(loc_btnOnlinePayment);
			commons.checkTheCheckBoxOrRadio(loc_rdoVISA);
			break;
		case PAYPAL:
			commons.click(loc_btnOnlinePayment);
			commons.checkTheCheckBoxOrRadio(loc_rdoPAYPAL);
			break;
		default:
			logger.error("Unsupported payment method: " + method);
			return method;
		}
		
		commons.click(loc_btnPay);
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
		return commons.getText(loc_lblOrderId);
	}
	
	public PlansPage clickOnLogOut() {
		commons.sleepInMiliSecond(1000);
		commons.click(loc_btnLogout);
//		new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.titleIs(LOGIN_PAGE_TITLE));
		logger.info("Clicked on Logout link");
		return this;
	}
	
	/**
	 * Clicks on the element that obscures the screen
	 */
	public PlansPage clickOverlayElement() {
		commons.click(loc_tmpOverlayElement);
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
