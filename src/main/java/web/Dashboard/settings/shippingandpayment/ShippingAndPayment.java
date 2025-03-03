package web.Dashboard.settings.shippingandpayment;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

public class ShippingAndPayment {

	final static Logger logger = LogManager.getLogger(ShippingAndPayment.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	ShippingAndPaymentElement elements;

	public ShippingAndPayment(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new ShippingAndPaymentElement();
	}

	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey) {
		String translatedText = null;
		try {
			translatedText = PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedText;
	}	
	
	public ShippingAndPayment navigate() {
		clickShippingAndPaymentTab();
    	homePage.waitTillSpinnerDisappear1();
    	
    	//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
    	for (int i=0; i<30; i++) {
    		if (!commonAction.getElements(elements.loc_imgPaypal).isEmpty()) break;
    		commonAction.sleepInMiliSecond(500);
    	}
		return this;
	}

	ShippingAndPayment navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public ShippingAndPayment navigateToDetailScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=2");
    	for (int i=0; i<30; i++) {
    		if (!commonAction.getElements(elements.loc_imgPaypal).isEmpty()) break;
    		commonAction.sleepInMiliSecond(500);
    	}
    	commonAction.sleepInMiliSecond(1000, "Wait a little after navigation");
		return this;
	}		
	
	public ShippingAndPayment clickShippingAndPaymentTab() {
		commonAction.click(elements.loc_tabShippingPayment);
		logger.info("Clicked on Shipping And Payment tab.");
		return this;
	}

	public ShippingAndPayment clickGoogleAPIKeyBtn() {
		commonAction.click(elements.loc_btnGoogleAPIKey);
		logger.info("Clicked on Google API Key button.");
		return this;
	}	
	
	public ShippingAndPayment inputGoogleAPIKey(String key) {
		commonAction.inputText(elements.loc_txtGoogleAPIKey, key);
		logger.info("Input Google API Key: " + key);
		return this;
	}	

	public String getGoogleAPIKey() {
		String value = commonAction.getAttribute(elements.loc_txtGoogleAPIKey, "value");
		logger.info("Retrieved Google API Key: " + value);
		return value;
	}	
	
	public ShippingAndPayment clickSaveGoogleAPIKeyBtn() {
		commonAction.click(elements.loc_btnSaveGoogleAPIKey);
		logger.info("Clicked on Save Google API Key button.");
		return this;
	}
	
	boolean isGHTKTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_blkGHTK, elements.loc_btnToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is GHTK turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	boolean isGHNTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_blkGHN, elements.loc_btnToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is GHN turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	boolean isAhamoveTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_blkAhamove, elements.loc_btnToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Ahamove turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	boolean isVNPostTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_blkVnPost, elements.loc_btnToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is VNPost turned on: " + isTurnedOn);
		return isTurnedOn;
	}

	boolean isSelfDeliveryTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_blkSelfDelivery, elements.loc_btnToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Self-Delivery turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	boolean isLocalATMTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_btnATMToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Local ATM turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	boolean isCreditCardTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_btnCreditCardToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Credit Card turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	boolean isCODTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_btnCODToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Cash On Delivery turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	boolean isCashTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_btnCashToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Cash turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	boolean isDebtTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_btnDebtToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Debt turned on: " + isTurnedOn);
		return isTurnedOn;
	}	

	public ShippingAndPayment clickGHTKToggle() {
		By chainedBy = new ByChained(elements.loc_blkGHTK, elements.loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(elements.loc_blkGHTKAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(chainedBy));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on GHTK toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickGHNToggle() {
		By chainedBy = new ByChained(elements.loc_blkGHN, elements.loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(elements.loc_blkGHNAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(chainedBy));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on GHN toggle button.");
		return this;
	}

	public ShippingAndPayment clickAhamoveToggle() {
		By chainedBy = new ByChained(elements.loc_blkAhamove, elements.loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(elements.loc_blkAhamoveAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(chainedBy));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on Ahamove toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickVNPostToggle() {
		By chainedBy = new ByChained(elements.loc_blkVnPost, elements.loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(elements.loc_blkVnPostAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(chainedBy));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on VNPost toggle button.");
		return this;
	}	
	
	public ShippingAndPayment clickSelfDeliveryToggle() {
		By chainedBy = new ByChained(elements.loc_blkSelfDelivery, elements.loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(elements.loc_blkSelfDeliveryAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(chainedBy));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on Self-Delivery toggle button.");
		return this;
	}

	public ShippingAndPayment enableSelfDelivery() {
		if (!isSelfDeliveryTurnedOn()) {
			clickSelfDeliveryToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Self Delivery"));
		return this;
	}	
	
	public ShippingAndPayment clickShippingProviderSaveBtn() {
		commonAction.click(elements.loc_btnSaveShippingProvider);
		logger.info("Clicked on Save button at Shipping Provider tab.");
		return this;
	}	
	
	/*--------------------------------------------------------------*/
	public ShippingAndPayment enablePaymentMethodFor(String productOrService) {
		if (productOrService.contentEquals("Product")) {
			if (!commonAction.isElementDisplay(new ByChained(elements.loc_chkProduct, elements.loc_tmpChecked))) {
				commonAction.click(elements.loc_chkProduct);
			}
		} else {
			if (!commonAction.isElementDisplay(new ByChained(elements.loc_chkService, elements.loc_tmpChecked))) {
				commonAction.click(elements.loc_chkService);
			}
		}
		logger.info("Enabled Payment Method for %s.".formatted(productOrService));
		return this;
	}

	public ShippingAndPayment clickLocalATMToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnATMToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnATMToggle));
			return this;
		}
		commonAction.click(elements.loc_btnATMToggle);
		logger.info("Clicked on Local ATM toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}
	
	public ShippingAndPayment clickCreditCardToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnCreditCardToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnCreditCardToggle));
			return this;
		}
		commonAction.click(elements.loc_btnCreditCardToggle);
		logger.info("Clicked on Credit Card toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}
	
	public ShippingAndPayment clickCODToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnCODToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnCODToggle));
			return this;
		}
		commonAction.click(elements.loc_btnCODToggle);
		logger.info("Clicked on Cash On Delivery toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}
	
	public ShippingAndPayment clickCashToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnCashToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnCashToggle));
			return this;
		}
		commonAction.click(elements.loc_btnCashToggle);
		logger.info("Clicked on Cash toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}
	
	public ShippingAndPayment clickDebtToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnDebtToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnDebtToggle));
			return this;
		}
		commonAction.click(elements.loc_btnDebtToggle);
		logger.info("Clicked on Debt toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}
	
	public ShippingAndPayment clickPaypalToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnPaypalToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnPaypalToggle));
			return this;
		}
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnPaypalToggleAncestor1)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnPaypalToggle));
			return this;
		}
		commonAction.click(elements.loc_btnPaypalToggle);
		logger.info("Clicked on Paypal toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}
	
	public ShippingAndPayment clickBankTransferToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnBankTransferToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnBankTransferToggle));
			return this;
		}
		commonAction.click(elements.loc_btnBankTransferToggle);
		logger.info("Clicked on Bank-Transfer toggle button.");
		commonAction.sleepInMiliSecond(500, "Wait a little after click on toggles");
		return this;
	}


	public ShippingAndPayment enableLocalATM() {
		if (!isLocalATMTurnedOn()) {
			clickLocalATMToggle();
			commonAction.sleepInMiliSecond(2000);
		}
		logger.info("Enabled Payment Method for %s.".formatted("Local ATM Card"));
		return this;
	}	

	public ShippingAndPayment enableCreditCard() {
		if (!isCreditCardTurnedOn()) {
			clickCreditCardToggle();
			commonAction.sleepInMiliSecond(2000);
		}
		logger.info("Enabled Payment Method for %s.".formatted("Credit Card"));
		return this;
	}		

	public ShippingAndPayment enableCOD() {
		if (!isCODTurnedOn()) {
			clickCODToggle();
			commonAction.sleepInMiliSecond(2000);
		}
		logger.info("Enabled Payment Method for %s.".formatted("Cash On Delivery"));
		return this;
	}		
	
	public ShippingAndPayment enableCash() {
		if (!isCashTurnedOn()) {
			clickCashToggle();
			commonAction.sleepInMiliSecond(2000);
		}
		logger.info("Enabled Payment Method for %s.".formatted("Cash"));
		return this;
	}		
	
	public ShippingAndPayment enableDebt() {
		if (!commonAction.getElement(new ByChained(elements.loc_btnDebtToggle, elements.loc_tmpInput)).isSelected()) {
			clickDebtToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Debt"));
		return this;
	}		
	
	public ShippingAndPayment enablePaypal() {
		if (!commonAction.getElement(new ByChained(elements.loc_btnPaypalToggle, elements.loc_tmpInput)).isSelected()) {
			clickPaypalToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Paypal"));
		return this;
	}		
	public ShippingAndPayment enableBankTransfer() {
		if (!commonAction.getElement(new ByChained(elements.loc_btnBankTransferToggle, elements.loc_tmpInput)).isSelected()) {
			clickBankTransferToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Bank Transfer"));
		return this;
	}		

	public ShippingAndPayment inputMomoPartnerCode(String partnerCode) {
		commonAction.inputText(elements.loc_txtMomoPartnerCode, partnerCode);
		logger.info("Input Momo Partner Code: " + partnerCode);
		return this;
	}		
	public ShippingAndPayment inputMomoAccessKey(String accessKey) {
		commonAction.inputText(elements.loc_txtMomoAccessKey, accessKey);
		logger.info("Input Momo Access Key: " + accessKey);
		return this;
	}		
	public ShippingAndPayment inputMomoSecretKey(String secretKey) {
		commonAction.inputText(elements.loc_txtMomoSecretKey, secretKey);
		logger.info("Input Momo Secret Key: " + secretKey);
		return this;
	}		
	public ShippingAndPayment clickMomoSaveBtn() {
		commonAction.click(elements.loc_btnMomoSave);
		logger.info("Clicked on Momo Save button");
		return this;
	}		
	public String getMomoPartnerCode() {
		String value = commonAction.getAttribute(elements.loc_txtMomoPartnerCode, "value");
		logger.info("Retrieved Momo Partner Code: " + value);
		return value;
	}		
	public String getMomoAccessKey() {
		String value = commonAction.getAttribute(elements.loc_txtMomoAccessKey, "value");
		logger.info("Retrieved Momo Access Key: " + value);
		return value;
	}		
	public String getMomoSecretKey() {
		String value = commonAction.getAttribute(elements.loc_txtMomoSecretKey, "value");
		logger.info("Retrieved Momo Secret Key: " + value);
		return value;
	}		

	public ShippingAndPayment clickDebtSegmentDropdown() {
		commonAction.click(elements.loc_ddlDebtSegment);
		logger.info("Clicked on Debt Segment dropdown");
		return this;
	}
	
	public ShippingAndPayment clickDebtSegmentDoneBtn() {
		commonAction.click(elements.loc_btnDebtSegmentDone);
		logger.info("Clicked on Debt Segment Done button");
		return this;
	}
	
	public ShippingAndPayment clickPayPalExchangeRateField() {
		commonAction.click(elements.loc_txtPayPalExchangeRate);
		logger.info("Clicked on PayPal Exchange Rate field");
		return this;
	}
	
	public ShippingAndPayment clickPayPalTooltip() {
		commonAction.click(elements.loc_tltPayPalExchangeRate);
		logger.info("Clicked on PayPal Exchange Rate tooltip");
		return this;
	}
	
	public ShippingAndPayment clickVNPaySettingBtn() {
		commonAction.click(elements.loc_btnVNPaySetting);
		logger.info("Clicked on VNPay setting button");
		return this;
	}
	
	public ShippingAndPayment clickSaveVNPaySettingBtn() {
		commonAction.click(elements.loc_btnSaveVNPaySetting);
		logger.info("Clicked on Save VNPay setting button");
		return this;
	}
	
    public void verifyPermissionToEnableGHTK(String permission) {
    	navigate();
    	clickGHTKToggle();
    	boolean isTurnedOn = isGHTKTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "GHTK");
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "GHTK");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableGHN(String permission) {
    	navigate();
    	clickGHNToggle();
    	boolean isTurnedOn = isGHNTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "GHN");
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "GHN");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableAhamove(String permission) {
    	navigate();
    	clickAhamoveToggle();
    	boolean isTurnedOn = isAhamoveTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "Ahamove");
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "Ahamove");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableSelfDelivery(String permission) {
    	navigate();
    	enableSelfDelivery();
    	boolean isTurnedOn = isSelfDeliveryTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "Self-Delivery");
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "Self-Delivery");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    
    public void verifyPermissionToEnableLocalATMCard(String permission) {
    	navigate();
    	enableLocalATM();
    	boolean isTurnedOn = isLocalATMTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "Local ATM");
    		clickLocalATMToggle();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "Local ATM");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableCreditCard(String permission) {
    	navigate();
    	enableCreditCard();
    	boolean isTurnedOn = isCreditCardTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "Credit Card");
    		clickCreditCardToggle();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "Credit Card");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableCOD(String permission) {
    	navigate();
    	enableCOD();
    	boolean isTurnedOn = isCODTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "Cash On Delivery");
    		clickCODToggle();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "Cash On Delivery");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableCash(String permission) {
    	navigate();
    	enableCash();
    	boolean isTurnedOn = isCashTurnedOn();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(isTurnedOn, "Cash");
    		clickCashToggle();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(isTurnedOn, "Cash");
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableDebt(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		enableDebt();
    		clickDebtToggle();
    	} else if (permission.contentEquals("D")) {
    		enableDebt();
    		// Not completed
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnablePaypal(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		enablePaypal();
    		clickPaypalToggle();
    	} else if (permission.contentEquals("D")) {
    		enablePaypal();
    		// Not completed
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }

    void confirmToSwitchOffToggles() {
    	new ConfirmationDialog(driver).clickOKBtn();
    	commonAction.sleepInMiliSecond(500, "Wait a little after clicking on OK button in confirmation dialog");
    }
    
	void checkPermissionToEnableDisableShippingPayment(AllPermissions staffPermission) {
		boolean originalStatus;
		
		navigateToDetailScreenByURL();
		originalStatus = isGHTKTurnedOn();
		clickGHTKToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHNTurnedOn()|isAhamoveTurnedOn()|isVNPostTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isGHTKTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isGHTKTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isGHNTurnedOn();
		clickGHNToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isAhamoveTurnedOn()|isVNPostTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isGHNTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isGHNTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}		
		
		navigateToDetailScreenByURL();
		originalStatus = isAhamoveTurnedOn();
		clickAhamoveToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isGHNTurnedOn()|isVNPostTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isAhamoveTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isAhamoveTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isVNPostTurnedOn();
		clickVNPostToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isGHNTurnedOn()|isAhamoveTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isVNPostTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isVNPostTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}

		navigateToDetailScreenByURL();
		originalStatus = isSelfDeliveryTurnedOn();
		clickSelfDeliveryToggle();
		if (originalStatus) confirmToSwitchOffToggles();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisableSelfDeliveryMethod()) {
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isGHNTurnedOn()|isAhamoveTurnedOn()|isVNPostTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isSelfDeliveryTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isSelfDeliveryTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}		
		
		//Re-enable SelfDelivery so that it facilitate testing of other permissions
		enableSelfDelivery();
		clickShippingProviderSaveBtn();
		commonAction.sleepInMiliSecond(2000, "Wait a little after clicking on Save button to save delivery configuration");
		
		logger.info("Finished checkPermissionToEnableDisableShippingPayment");
	}
	
	void checkPermissionToEnableDisableShippingPayment1(AllPermissions staffPermission) {
		boolean originalStatus;
		
		navigateToDetailScreenByURL();
		originalStatus = isGHTKTurnedOn();
		clickGHTKToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHNTurnedOn()|isAhamoveTurnedOn()|isVNPostTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isGHTKTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isGHTKTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isGHNTurnedOn();
		clickGHNToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isAhamoveTurnedOn()|isVNPostTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isGHNTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isGHNTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}		
		
		navigateToDetailScreenByURL();
		originalStatus = isAhamoveTurnedOn();
		clickAhamoveToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isGHNTurnedOn()|isVNPostTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isAhamoveTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isAhamoveTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isVNPostTurnedOn();
		clickVNPostToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isGHNTurnedOn()|isAhamoveTurnedOn()|isSelfDeliveryTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isVNPostTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isVNPostTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isSelfDeliveryTurnedOn();
		clickSelfDeliveryToggle();
		if (originalStatus) confirmToSwitchOffToggles();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisableSelfDeliveryMethod()) {
			clickShippingProviderSaveBtn();
			if (originalStatus) {
				if (isGHTKTurnedOn()|isGHNTurnedOn()|isAhamoveTurnedOn()|isVNPostTurnedOn()) {
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
					Assert.assertNotEquals(isSelfDeliveryTurnedOn(), originalStatus);
				} else {
					new ConfirmationDialog(driver).clickYellowBtn();
				}
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				Assert.assertNotEquals(isSelfDeliveryTurnedOn(), originalStatus);
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}		
		
		//Re-enable SelfDelivery so that it facilitate testing of other permissions
		enableSelfDelivery();
		clickShippingProviderSaveBtn();
		commonAction.sleepInMiliSecond(2000, "Wait a little after clicking on Save button to save delivery configuration");
		
		logger.info("Finished checkPermissionToEnableDisableShippingPayment");
	}
	
	void checkPermissionToAddRemoveGoogleAPIKey(AllPermissions staffPermission) {
		navigateToDetailScreenByURL();
		
		clickGoogleAPIKeyBtn();
		
		if (staffPermission.getSetting().getShippingAndPayment().isAddRemoveGoogleAPIKey()) {
			String originalKey = getGoogleAPIKey();
			if (originalKey.isEmpty()) {
				inputGoogleAPIKey("TestPermission");
				clickSaveGoogleAPIKeyBtn();
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				clickGoogleAPIKeyBtn();
				inputGoogleAPIKey("");
				clickSaveGoogleAPIKeyBtn();
			} else {
				inputGoogleAPIKey("");
				clickSaveGoogleAPIKeyBtn();
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				
				clickGoogleAPIKeyBtn();
				inputGoogleAPIKey(originalKey);
				clickSaveGoogleAPIKeyBtn();
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToAddRemoveGoogleAPIKey");
	}
	
	void checkPermissionToEnableDisablePaymentMethod(AllPermissions staffPermission) {
		boolean originalStatus;
		
		navigateToDetailScreenByURL();
		originalStatus = isLocalATMTurnedOn();
		clickLocalATMToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisablePaymentMethod()) {
			if (originalStatus) confirmToSwitchOffToggles();
			Assert.assertNotEquals(isLocalATMTurnedOn(), originalStatus);
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		enableLocalATM();
		
		navigateToDetailScreenByURL();
		originalStatus = isCreditCardTurnedOn();
		clickCreditCardToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisablePaymentMethod()) {
			Assert.assertNotEquals(isCreditCardTurnedOn(), originalStatus);
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isCODTurnedOn();
		clickCODToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisablePaymentMethod()) {
			Assert.assertNotEquals(isCODTurnedOn(), originalStatus);
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		navigateToDetailScreenByURL();
		originalStatus = isCashTurnedOn();
		clickCashToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisablePaymentMethod()) {
			Assert.assertNotEquals(isCashTurnedOn(), originalStatus);
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		//Code for Momo payment goes here
		
		navigateToDetailScreenByURL();
		originalStatus = isDebtTurnedOn();
		clickDebtToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisablePaymentMethod()) {
			if (isSelfDeliveryTurnedOn()) {
				Assert.assertNotEquals(isDebtTurnedOn(), originalStatus);
			} else {
				Assert.assertEquals(isDebtTurnedOn(), originalStatus); // If Self-Delivery is not turned on, debt payment cannot be turned on
			}
			
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}

		//Code for Paypal and Banktransfer payment goes here
		
		logger.info("Finished checkPermissionToEnableDisablePaymentMethod");
	}	
	
	void checkPermissionToUpdatePaymentMethodInfo(AllPermissions staffPermission) {
		
		navigateToDetailScreenByURL();
		
		if (isDebtTurnedOn()) {
			clickDebtSegmentDropdown();
			if (staffPermission.getSetting().getShippingAndPayment().isUpdatePaymentMethodInformation()) {
				clickDebtSegmentDoneBtn();
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else if (staffPermission.getSetting().getShippingAndPayment().isEnableDisablePaymentMethod()) {
			if (isSelfDeliveryTurnedOn()) {
				enableDebt();
				clickDebtSegmentDropdown();
				if (staffPermission.getSetting().getShippingAndPayment().isUpdatePaymentMethodInformation()) {
					clickDebtSegmentDoneBtn();
					Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
				} else {
					Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
				}
			} else {
				logger.info("Self-Delivery is off. Skipping this case");
			}
		} else {
			logger.info("Debt payment is off and permission to enable payment method is not granted. Skipping checking permission to update Debt payment info");
		}
		
		navigateToDetailScreenByURL();
		
		//Re-enable SelfDelivery so that it facilitate testing of other permissions
		if (!isSelfDeliveryTurnedOn()) {
			enableSelfDelivery();
			clickShippingProviderSaveBtn();
			isSelfDeliveryTurnedOn();
			navigateToDetailScreenByURL();
		}
		
		if (getMomoPartnerCode().isEmpty()) inputMomoPartnerCode("partnercode");
		if (getMomoAccessKey().isEmpty()) inputMomoAccessKey("accesskey");
		if (getMomoSecretKey().isEmpty()) inputMomoSecretKey("secretkey");
		clickMomoSaveBtn();
		if (staffPermission.getSetting().getShippingAndPayment().isUpdatePaymentMethodInformation()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.create.successMessage"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}

		clickPayPalExchangeRateField();
		clickPayPalTooltip();
		if (staffPermission.getSetting().getShippingAndPayment().isUpdatePaymentMethodInformation()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
				
		clickVNPaySettingBtn();
		if (staffPermission.getSetting().getShippingAndPayment().isUpdatePaymentMethodInformation()) {
			clickSaveVNPaySettingBtn();
//			Assert.assertTrue(homePage.getToastMessage().contains("VNPay") || homePage.getToastMessage().contentEquals(translateText("promotion.flashSale.edit.successMessage")));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}	
		
		logger.info("Finished checkPermissionToEnableDisablePaymentMethod");
	}	
	
	public void checkShippingPaymentSettingPermission(AllPermissions staffPermission) {
		checkPermissionToEnableDisableShippingPayment(staffPermission);
		checkPermissionToAddRemoveGoogleAPIKey(staffPermission);
		checkPermissionToEnableDisablePaymentMethod(staffPermission);
		checkPermissionToUpdatePaymentMethodInfo(staffPermission);
	}    
    
}
