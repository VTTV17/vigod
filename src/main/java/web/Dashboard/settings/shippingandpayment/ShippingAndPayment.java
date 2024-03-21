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
		commonAction.sleepInMiliSecond(500, "Wait in getGoogleAPIKey");
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
		logger.info("Is Giao Hang Tiet Kiem turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	boolean isGHNTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(new ByChained(elements.loc_blkGHN, elements.loc_btnToggle, elements.loc_tmpInput)).isSelected();
		logger.info("Is Giao Hang Nhanh turned on: " + isTurnedOn);
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
		return this;
	}
	
	public ShippingAndPayment clickCreditCardToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnCreditCardToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnCreditCardToggle));
			return this;
		}
		commonAction.click(elements.loc_btnCreditCardToggle);
		logger.info("Clicked on Credit Card toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCODToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnCODToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnCODToggle));
			return this;
		}
		commonAction.click(elements.loc_btnCODToggle);
		logger.info("Clicked on Cash On Delivery toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCashToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnCashToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnCashToggle));
			return this;
		}
		commonAction.click(elements.loc_btnCashToggle);
		logger.info("Clicked on Cash toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickDebtToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnDebtToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnDebtToggle));
			return this;
		}
		commonAction.click(elements.loc_btnDebtToggle);
		logger.info("Clicked on Debt toggle button.");
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
		return this;
	}
	
	public ShippingAndPayment clickBankTransferToggle() {
		if (commonAction.isElementVisiblyDisabled(elements.loc_btnBankTransferToggleAncestor)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnBankTransferToggle));
			return this;
		}
		commonAction.click(elements.loc_btnBankTransferToggle);
		logger.info("Clicked on Bank-Transfer toggle button.");
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

	void checkPermissionToEnableDisableShippingPayment(AllPermissions staffPermission) {
		boolean originalStatus;
		
		navigateToDetailScreenByURL();
		originalStatus = isGHTKTurnedOn();
		clickGHTKToggle();
		if (staffPermission.getSetting().getShippingAndPayment().isEnableDisable3rdShippingMethod()) {
			if (originalStatus) new ConfirmationDialog(driver).clickOKBtn();
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
			if (originalStatus) new ConfirmationDialog(driver).clickOKBtn();
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
			if (originalStatus) new ConfirmationDialog(driver).clickOKBtn();
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
			if (originalStatus) new ConfirmationDialog(driver).clickOKBtn();
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
		if (originalStatus) new ConfirmationDialog(driver).clickOKBtn();
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
	
	public void checkShippingPaymentSettingPermission(AllPermissions staffPermission) {
//		checkPermissionToEnableDisableShippingPayment(staffPermission);
		checkPermissionToAddRemoveGoogleAPIKey(staffPermission);
	}    
    
}
