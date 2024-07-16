package web.Dashboard.settings.plans;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.enums.PaymentMethod;
import utilities.model.dashboard.setting.packageinfo.PlanPaymentReview;
import utilities.model.dashboard.setting.packageinfo.PaymentCompleteInfo;
import utilities.thirdparty.ATM;
import utilities.thirdparty.PAYPAL;
import utilities.thirdparty.VISA;
import web.InternalTool.InternalTool;

public class PackagePayment {
	final static Logger logger = LogManager.getLogger(PackagePayment.class);
	
	WebDriver driver;
	UICommonAction commons;
	PackagePaymentElement elements;

	public PackagePayment(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		elements = new PackagePaymentElement();
	}
	
	public List<String> getPackagePeriodOptions() {
		commons.getElement(elements.loc_lblPackageBasePrice); //This implicitly means the options are filled with data, this element appearing means APIs needed are already executed. Reason #1
		List<String> durationOptions = new ArrayList<String>();
		for(int i=0; i<commons.getElements(elements.loc_rdoDurationOptions).size(); i++) {
			durationOptions.add(commons.getText(elements.loc_rdoDurationOptions, i));
		}
		logger.info("Retrieved Year - Price options available: {}", durationOptions);
		return durationOptions;
	}
	public PackagePayment selectDuration(int numberOfYears) {
		commons.click(By.xpath(elements.loc_rdoDurationByName.replaceAll("noys", String.valueOf(numberOfYears))));
		logger.info("Selected Year - Price options: {}", numberOfYears);
		return this;
	}
	
	public boolean isOnlinePaymentTabHidden() {
		boolean isHidden = commons.getAttribute(elements.loc_tabOnlinePayment, "hidden") != null;
		logger.info("Online Payment tab is hidden: {}", isHidden);
		return isHidden;
	}
	public List<String> getOnlinePaymentOptions() {
		commons.click(elements.loc_tabOnlinePayment);
		commons.getElement(elements.loc_rdoPAYPAL); //This implicitly means the options are present ready for further actions
		List<String> paymentOptions = new ArrayList<String>();
		if (!commons.getElements(elements.loc_rdoATM).isEmpty()) paymentOptions.add("ATM");
		if (!commons.getElements(elements.loc_rdoVISA).isEmpty()) paymentOptions.add("VISA");
		paymentOptions.add("PAYPAL"); //PAYPAL is always available
		logger.info("Retrieved payment options available: {}", paymentOptions);
		return paymentOptions;
	}	
	public PlanPaymentReview getFinalizePackageInfo() {
		PlanPaymentReview totalInfo = new PlanPaymentReview();
		totalInfo.setName(commons.getText(elements.loc_lblPackageName));
		totalInfo.setDuration(commons.getText(elements.loc_lblPackageDuration));
		totalInfo.setBasePrice(commons.getText(elements.loc_lblPackageBasePrice));
		if (!commons.getElements(elements.loc_lblPackageVAT).isEmpty()) totalInfo.setVatPrice(commons.getText(elements.loc_lblPackageVAT)); //Hidden for domain .biz stores
		if (!commons.getElements(elements.loc_lblRefund).isEmpty()) totalInfo.setRefundAmount(commons.getText(elements.loc_lblRefund)); //Hidden when purchase packages for the 1st time
		totalInfo.setFinalTotal(commons.getText(elements.loc_lblPackageFinalTotal));
		logger.info("Retrieved finalized package info: {}", totalInfo);
		return totalInfo;
	}	
	public PackagePayment clickPlaceOrderBtn() {
		commons.click(elements.loc_btnPlaceOrder);
		logger.info("Clicked 'Place Order' button");
		return this;
	}	
	public PackagePayment selectPaymentMethod(PaymentMethod method) {
		switch (method) {
		case BANKTRANSFER:
			commons.click(elements.loc_tabBankTransfer); break;
		case ATM:
			commons.click(elements.loc_tabOnlinePayment); commons.checkTheCheckBoxOrRadio(elements.loc_rdoATM); break;
		case VISA:
			commons.click(elements.loc_tabOnlinePayment); commons.checkTheCheckBoxOrRadio(elements.loc_rdoVISA); break;
		case PAYPAL:
			commons.click(elements.loc_tabOnlinePayment); commons.checkTheCheckBoxOrRadio(elements.loc_rdoPAYPAL); break;
		default:
			logger.info("Input method '{}' is invalid. Selecting Bank-Transfer by default",  method); commons.click(elements.loc_tabBankTransfer); break;
		}
		getFinalizePackageInfo();
		clickPlaceOrderBtn();
		commons.sleepInMiliSecond(1000);
		logger.info("Selected payment method: " + method);
		return this;
	}	
	
	//Will remove later
	/**
	 * @param method accepted values are "BANKTRANSFER", "ATM", "VISA" and "PAYPAL"
	 */
	public PackagePayment selectPaymentMethod(String method) {
		return selectPaymentMethod(PaymentMethod.valueOf(method));
	}
	
	public String completePayment(PaymentMethod method) {
		//Return orderId fast when the payment is Bank-Transfer
		if (method.equals(PaymentMethod.BANKTRANSFER)) {
			return getOrderId();
		}
		
		//Get current tab handle
		String currentWindowHandle = commons.getCurrentWindowHandle();
		
		int currentNumberOfWindows = 1;
		
		//Wait till a new tab is launch
		for(int i=0; i<5; i++) {
			currentNumberOfWindows = commons.getAllWindowHandles().size();
			if (currentNumberOfWindows >1) break;
			commons.sleepInMiliSecond(1000, "Wait till a new tab is launch");
		}
		
		//Switch to the newly launched tab
		commons.switchToWindow(1);
		
		switch (method) {
		case ATM:
			new ATM(driver).completePayment(); break;
		case VISA:
			new VISA(driver).completePayment(); break;
		case PAYPAL:
			new PAYPAL(driver).completePayment(); break;
		default:
			//No coding is needed
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
		return completePayment(PaymentMethod.valueOf(method));
	}	
	
	public PaymentCompleteInfo getPaymentCompleteInfo() {
		PaymentCompleteInfo paymentCompleteInfo = new PaymentCompleteInfo();
		paymentCompleteInfo.setOrderId(commons.getText(By.xpath(elements.loc_lblTopInfo.formatted(1))));
		paymentCompleteInfo.setSubscribedPackage(commons.getText(By.xpath(elements.loc_lblTopInfo.formatted(2))));
		paymentCompleteInfo.setDuration(commons.getText(By.xpath(elements.loc_lblTopInfo.formatted(3))));
		paymentCompleteInfo.setPaymentMethod(commons.getText(By.xpath(elements.loc_lblTopInfo.formatted(4))));
		paymentCompleteInfo.setTotal(commons.getText(elements.loc_lblContactInfo));
		logger.info("Retrieved payment complete info: {}", paymentCompleteInfo);
		return paymentCompleteInfo;
	}		
	
	public void clickBackToDashboardBtn() {
		commons.click(elements.loc_btnBackToDashboard);
		logger.info("Clicked 'Back To Dashboard' button");
	}
	
	public String getOrderId() {
		return getPaymentCompleteInfo().getOrderId();
	}
	
	public void approvePackageInInternalTool(PaymentMethod method, String orderID) {
		
		InternalTool internal = new InternalTool(driver);
		
		switch (method) {
		case BANKTRANSFER: {
			internal.openNewTabAndNavigateToInternalTool().login().navigateToPage("GoSell","Packages","New Packages");
			internal.approveOrder(orderID).closeTab(); break;
		}
		case PAYPAL: {
			internal.openNewTabAndNavigateToInternalTool().login().navigateToPage("GoSell","Packages","New Packages");
			for (int i=0; i<20; i++) {
				if (internal.getOrderApprovalStatus(orderID).contentEquals("Approved")) {
					break;
				}
				commons.sleepInMiliSecond(3000);
				commons.refreshPage();
			}
			internal.closeTab();
			break;
		}		
		default:
			//No coding is needed
		}
	}
	
	//Remove soon
	public void logoutAfterSuccessfulPurchase(PaymentMethod method, String orderID) {
		if (method.equals(PaymentMethod.BANKTRANSFER)) {
			new InternalTool(driver).openNewTabAndNavigateToInternalTool()
			.login().navigateToPage("GoSell","Packages","Orders list").approveOrder(orderID).closeTab();
			return;
		}
	}
	//Remove soon
	public void logoutAfterSuccessfulPurchase(String method, String orderID) {
		PaymentMethod payBy = PaymentMethod.valueOf(method);
		logoutAfterSuccessfulPurchase(payBy, orderID);
	}	
	
}
