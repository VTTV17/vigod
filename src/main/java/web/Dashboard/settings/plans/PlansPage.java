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

public class PlansPage {
	final static Logger logger = LogManager.getLogger(PlansPage.class);
	
	WebDriver driver;
	UICommonAction commons;
	PlansPageElement elements;
	HomePage homePage;

	public PlansPage(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		elements = new PlansPageElement();
		homePage = new HomePage(driver);
	}

	public PlansPage selectPlan(String planName) {
		commons.waitInvisibilityOfElementLocated(elements.loc_icnLoading);
		String newXpath = elements.PLAN_PRICE_12M.replace("%planName%", planName);
		commons.click(By.xpath(newXpath));
		logger.info("Select plan: " + planName);
		commons.sleepInMiliSecond(1000);
		return this;
	}

	public PlansPage selectPayment() {
		commons.click(elements.loc_btnBankTransfer);
		logger.info("Click bank transfer");
		commons.click(elements.loc_btnPay);
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
			commons.click(elements.loc_btnBankTransfer);
			break;
		case ATM:
			commons.click(elements.loc_btnOnlinePayment);
			commons.checkTheCheckBoxOrRadio(elements.loc_rdoATM);
			break;
		case VISA:
			commons.click(elements.loc_btnOnlinePayment);
			commons.checkTheCheckBoxOrRadio(elements.loc_rdoVISA);
			break;
		case PAYPAL:
			commons.click(elements.loc_btnOnlinePayment);
			commons.checkTheCheckBoxOrRadio(elements.loc_rdoPAYPAL);
			break;
		default:
			logger.error("Unsupported payment method: " + method);
			return method;
		}
		
		clickPayBtn();
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
		logger.info("Getting orderID...");
		return commons.getText(elements.loc_lblOrderId);
	}
	
	public PlansPage clickPayBtn() {
		commons.click(elements.loc_btnPay);
		logger.info("Clicked 'Pay' button");
		return this;
	}
	
	public PlansPage clickOnLogOut() {
		commons.sleepInMiliSecond(1000);
		commons.click(elements.loc_btnLogout);
//		new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.titleIs(LOGIN_PAGE_TITLE));
		logger.info("Clicked on Logout link");
		return this;
	}
	
	/**
	 * Clicks on the element that obscures the screen
	 */
	public PlansPage clickOverlayElement() {
		commons.click(elements.loc_tmpOverlayElement);
		logger.info("Clicked on overlay element");
		return this;
	}

	public void logoutAfterSuccessfulPurchase(PaymentMethod method, String orderID) {
		if (method.equals(PaymentMethod.BANKTRANSFER)) {
			new InternalTool(driver).openNewTabAndNavigateToInternalTool()
			.login().navigateToPage("GoSell","Packages","Orders list").approveOrder(orderID).closeTab();
			homePage.clickLogout();
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
