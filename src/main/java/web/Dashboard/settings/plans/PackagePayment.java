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
	
	/**
	 * This work-around is devised after closely watching the page's behavior.
	 * What really happens behind the scene is the page actively waits until an API called calculate-amount is executed,
	 * that's when this element appears on the screen
	 */
	void waitTillDataFullyLoaded() {
		commons.getElement(elements.loc_lblPackageBasePrice); //This implicitly means the options are filled with data, this element appearing means APIs needed are already executed. Reason #1
	}
	
	public List<String> getDurationOptions() {
		waitTillDataFullyLoaded();
		
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
	
	/**
	 * When refundAmount >= total amount of a package, payment method is hidden. This function helps check that
	 * @return true if the section is displayed, false otherwise
	 */
	public boolean isPaymentMethodSectionDisplayed() {
		waitTillDataFullyLoaded();
		
		boolean isDisplayed = !commons.getElements(elements.loc_blkPaymentMethodSection).isEmpty();
		logger.info("Payment method section is displayed: {}", isDisplayed);
		return isDisplayed;
	}		
	
	/**
	 * When a shop's country is not Vietnam, PayPal is prioritized. This function helps check that
	 */
	public boolean isPayPalPrioritized() {
	    waitTillDataFullyLoaded();
	    
	    //These two variables can't be of the same value. When they are, it implicitly means the page is not ready to test
	    boolean isPrioritized = false;
	    boolean isTabPresent = false;
	    
	    int maxTries = 5;
	    int attempts = 0;
	    int sleepDuration = 1000;
	    
	    while (attempts < maxTries) {
	        isPrioritized = !commons.getElements(elements.loc_frmPayPal).isEmpty();
	        isTabPresent = isOnlinePaymentTabDisplayed();
	        
	        if (isPrioritized != isTabPresent) break;
	        logger.debug("Payment method section is not ready to test. Retrying...");
	        commons.sleepInMiliSecond(sleepDuration);
	        attempts++;
	    }
	    
	    logger.info("PayPal is prioritized: {}", isPrioritized);
	    return isPrioritized;
	}

	public boolean isBankTransferTabDisplayed() {
		boolean isDisplayed = !commons.getElements(elements.loc_tabBankTransfer).isEmpty();
		logger.info("Bank-Transfer tab is displayed: {}", isDisplayed);
		return isDisplayed;
	}	
	public boolean isOnlinePaymentTabDisplayed() {
		boolean isDisplayed = !commons.getElements(elements.loc_tabOnlinePayment).isEmpty();
		logger.info("Online Payment tab is displayed: {}", isDisplayed);
		return isDisplayed;
	}	
	/**
	 * Checks if online payment method is hidden when the total amount a customer has to pay for a package is 0
	 */
	public boolean isOnlinePaymentTabHidden() {
		boolean isHidden = commons.getAttribute(elements.loc_tabOnlinePayment, "hidden") != null;
		logger.info("Online Payment tab is hidden: {}", isHidden);
		return isHidden;
	}
	public List<PaymentMethod> getOnlinePaymentOptions() {
		commons.click(elements.loc_tabOnlinePayment);
		commons.getElement(elements.loc_rdoPAYPAL); //This implicitly means the options are present ready for further actions
		List<PaymentMethod> paymentOptions = new ArrayList<>();
		if (!commons.getElements(elements.loc_rdoATM).isEmpty()) paymentOptions.add(PaymentMethod.ATM);
		if (!commons.getElements(elements.loc_rdoVISA).isEmpty()) paymentOptions.add(PaymentMethod.VISA);
		paymentOptions.add(PaymentMethod.PAYPAL); //PAYPAL is always available
		return paymentOptions;
	}

	public List<PaymentMethod> getAvailablePaymentOptions() {
		List<PaymentMethod> paymentOptions = new ArrayList<>();
		
		if (isPayPalPrioritized()) {
			paymentOptions.add(PaymentMethod.PAYPAL);
		} else {
			paymentOptions.addAll(getOnlinePaymentOptions());
		}
		
		if (isBankTransferTabDisplayed()) paymentOptions.add(PaymentMethod.BANKTRANSFER);
		
		logger.info("Retrieved payment options available: {}", paymentOptions);
		
		return paymentOptions;
	}	
	public PlanPaymentReview getFinalizePackageInfo() {
		PlanPaymentReview totalInfo = new PlanPaymentReview();
		totalInfo.setName(commons.getText(elements.loc_lblPackageName));
		totalInfo.setDuration(commons.getText(elements.loc_lblPackageDuration));
		totalInfo.setBasePrice(commons.getText(elements.loc_lblPackageBasePrice));
		if (!commons.getElements(elements.loc_lblPackageVAT).isEmpty()) totalInfo.setVatPrice(commons.getText(elements.loc_lblPackageVAT)); //Hidden for domain .biz stores
		if (!commons.getElements(elements.loc_lblRefund).isEmpty()) totalInfo.setRefundAmount(commons.getText(elements.loc_lblRefund)); //Hidden when purchasing packages for the 1st time
		totalInfo.setFinalTotal(commons.getText(elements.loc_lblPackageFinalTotal));
		logger.info("Retrieved finalized package info: {}", totalInfo);
		return totalInfo;
	}	
	public PackagePayment clickPlaceOrderBtn() {
		commons.click(elements.loc_btnPlaceOrder);
		logger.info("Clicked 'Place Order' button");
		return this;
	}	

	/**
	 * Waits until a new window is launch then switch to it
	 * @param initialWindowCount the current number of windows, usually 1
	 */
	void switchToNewWindow(String originalWindow) {
	    int maxTries = 5;
	    int sleepDuration = 1000;
	    int attempts = 0;

	    while (attempts < maxTries) {
	        List<String> windowHandles = commons.getAllWindowHandles();
	        if (windowHandles.size() > 1) {
	            for (String windowHandle : windowHandles) {
	                if (!windowHandle.equals(originalWindow)) {
	                    commons.switchToWindow(windowHandle);
	                    return;
	                }
	            }
	        }
	        commons.sleepInMiliSecond(sleepDuration, "Wait till a new window is launched");
	        attempts++;
	    }
	}
	void switchToOriginalWindow(String originalWindow) {
	    int maxTries = 5;
	    int sleepDuration = 2000;
	    int attempts = 0;

	    while (attempts < maxTries) {
	        if (commons.getAllWindowHandles().size() == 1) {
	            commons.switchToWindow(originalWindow);
	            return;
	        }
	        commons.sleepInMiliSecond(sleepDuration, "Wait till the new window is closed");
	        attempts++;
	    }
	}
	
	PackagePayment selectBankTransfer() {
		commons.click(elements.loc_tabBankTransfer);
		clickPlaceOrderBtn();
		return this;
	}
	String completeBankTransfer() {
		return getOrderId();
	}
	PackagePayment selectATM() {
		commons.click(elements.loc_tabOnlinePayment);
		commons.click(elements.loc_rdoATM);
		clickPlaceOrderBtn();
		commons.sleepInMiliSecond(1000);
		return this;
	}
	String completeATM() {
		String currentWindowHandle = commons.getCurrentWindowHandle();
		switchToNewWindow(currentWindowHandle);
		new ATM(driver).completePayment();
		switchToOriginalWindow(currentWindowHandle);
		return getOrderId();
	}
	void abandonATM() {
		String currentWindowHandle = commons.getCurrentWindowHandle();
		switchToNewWindow(currentWindowHandle);
		new ATM(driver).abandonPayment();
		switchToOriginalWindow(currentWindowHandle);
	}
	PackagePayment selectVISA() {
		commons.click(elements.loc_tabOnlinePayment);
		commons.click(elements.loc_rdoVISA);
		clickPlaceOrderBtn();
		commons.sleepInMiliSecond(1000);
		return this;
	}
	String completeVISA() {
		String currentWindowHandle = commons.getCurrentWindowHandle();
		switchToNewWindow(currentWindowHandle);
		new VISA(driver).completePayment();
		switchToOriginalWindow(currentWindowHandle);
		return getOrderId();
	}
	void abandonVISA() {
		String currentWindowHandle = commons.getCurrentWindowHandle();
		switchToNewWindow(currentWindowHandle);
		new VISA(driver).abandonPayment();
		switchToOriginalWindow(currentWindowHandle);
	}	
	PackagePayment selectPayPal() {
		
		//Especial logic handling for PayPal
		if (!isPayPalPrioritized()) {
			commons.click(elements.loc_tabOnlinePayment);
			commons.click(elements.loc_rdoPAYPAL);
		}

		selectPrioritizedPayPal();
		return this;
	}
	PackagePayment selectPrioritizedPayPal() {
		commons.switchToFrameByElement(commons.getElement(elements.loc_frmPayPal));
		commons.click(elements.loc_btnPayPal);
		commons.sleepInMiliSecond(1000);
		return this;
	}
	String completePayPal() {
		String currentWindowHandle = commons.getCurrentWindowHandle();
		switchToNewWindow(currentWindowHandle);
		new PAYPAL(driver).completePayment();
		switchToOriginalWindow(currentWindowHandle);
		return getOrderId();
	}
	void abandonPayPal() {
		String currentWindowHandle = commons.getCurrentWindowHandle();
		switchToNewWindow(currentWindowHandle);
		new PAYPAL(driver).abandonPayment();
		switchToOriginalWindow(currentWindowHandle);
	}	
	
	//Will remove later
	/**
	 * @param method accepted values are "BANKTRANSFER", "ATM", "VISA" and "PAYPAL"
	 */
	public PackagePayment selectPaymentMethod(String method) {
		return selectPaymentMethod(PaymentMethod.valueOf(method));
	}
	
	public PackagePayment selectPaymentMethod(PaymentMethod method) {
		switch (method) {
		case BANKTRANSFER -> selectBankTransfer();
		case ATM -> selectATM();
		case VISA -> selectVISA();
		case PAYPAL -> selectPayPal();
		default -> throw new IllegalArgumentException("Unexpected value: " + method);
		}
		logger.info("Selected payment method: " + method);
		return this;
	}

	public String completePayment(PaymentMethod method) {
		return switch (method) {
		case BANKTRANSFER: {
			yield completeBankTransfer();
		}
		case ATM: {
			yield completeATM();
		}
		case VISA: {
			yield completeVISA();
		}
		case PAYPAL: {
			yield completePayPal();
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + method);
		};
	}	
	
	public String payThenComplete(PaymentMethod method) {
		return switch (method) {
		case BANKTRANSFER: {
			yield selectPaymentMethod(PaymentMethod.BANKTRANSFER).completePayment(PaymentMethod.BANKTRANSFER);
		}
		case ATM: {
			yield selectPaymentMethod(PaymentMethod.ATM).completePayment(PaymentMethod.ATM);
		}
		case VISA: {
			yield selectPaymentMethod(PaymentMethod.VISA).completePayment(PaymentMethod.VISA);
		}
		case PAYPAL: {
			yield selectPaymentMethod(PaymentMethod.PAYPAL).completePayment(PaymentMethod.PAYPAL);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + method);
		};
	}
	
	public void abandonPayment(PaymentMethod method) {
		switch (method) {
		case BANKTRANSFER -> {}
		case ATM -> abandonATM();
		case VISA -> abandonVISA();
		case PAYPAL -> abandonPayPal();
		default -> throw new IllegalArgumentException("Unexpected value: " + method);
		};
	}	
	
	//Will be removed
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
//		case PAYPAL: {
//			internal.openNewTabAndNavigateToInternalTool().login().navigateToPage("GoSell","Packages","New Packages");
//			for (int i=0; i<15; i++) {
//				if (internal.getOrderApprovalStatus(orderID).contentEquals("Approved")) {
//					break;
//				}
//				commons.sleepInMiliSecond(3000);
//				commons.refreshPage();
//			}
//			internal.closeTab();
//			break;
//		}		
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
