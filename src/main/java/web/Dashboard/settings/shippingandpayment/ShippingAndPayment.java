package web.Dashboard.settings.shippingandpayment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class ShippingAndPayment {

	final static Logger logger = LogManager.getLogger(ShippingAndPayment.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ShippingAndPayment(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_tabShippingPayment = By.cssSelector("li:nth-child(3) > a.nav-link");
	By loc_blkGHTK = By.id("provider-giaohangtietkiem");
	By loc_blkGHN = By.id("provider-giaohangnhanh");
	By loc_blkAhamove = By.id("provider-ahamove_bike");
	By loc_blkSelfDelivery = By.id("provider-selfdelivery");
	By loc_btnSaveShippingProvider = By.cssSelector(".shipping__provider button.gs-button__blue");
	By loc_chkProduct = By.xpath("(//div[contains(@class,'payment__method')]//label[contains(@class,'custom-check-box')])[1]");
	By loc_chkService = By.xpath("(//div[contains(@class,'payment__method')]//label[contains(@class,'custom-check-box')])[2]");
	By loc_btnATMToggle = By.xpath("//img[@alt='Local ATM']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]");
	By loc_btnCreditCardToggle = By.xpath("//img[@alt='Credit card']/parent::*/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]");
	By loc_btnCODToggle = By.xpath("//img[contains(@src,'COD')]/parent::*/following-sibling::*/label[contains(@class,'uik-checkbox__toggle')]");
	By loc_btnCashToggle = By.xpath("//img[@alt='cash']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]");
	By loc_btnDebtToggle = By.xpath("//img[@alt='debt']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]");
	By loc_btnPaypalToggle = By.xpath("//img[@alt='paypal']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]");
	By loc_btnBankTransferToggle = By.xpath("//img[@alt='bank transfer']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]");

	By loc_btnToggle = By.xpath(".//label[contains(@class,'uik-checkbox__toggle')]");
	By loc_tmpChecked = By.xpath(".//*[@class='uik-checkbox__checkboxIcon']");
	By loc_imgPaypal = By.xpath("//img[@alt='paypal']");

	public ShippingAndPayment navigate() {
		clickShippingAndPaymentTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	
    	//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
    	for (int i=0; i<30; i++) {
    		if (commonAction.getElements(loc_imgPaypal).size() >0) break;
    		commonAction.sleepInMiliSecond(500);
    	}
		return this;
	}

	public ShippingAndPayment clickShippingAndPaymentTab() {
		commonAction.click(loc_tabShippingPayment);
		logger.info("Clicked on Shipping And Payment tab.");
		return this;
	}

	public ShippingAndPayment clickGHTKToggle() {
		By chainedBy = new ByChained(loc_blkGHTK, loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(new ByChained(loc_blkGHTK, By.xpath("./parent::*"))))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(chainedBy)));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on GHTK toggle button.");
		return this;
	}
	
	public boolean isGHTKTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_blkGHTK).findElement(loc_btnToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Giao Hang Tiet Kiem turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	public boolean isGHNTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_blkGHN).findElement(loc_btnToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Giao Hang Nhanh turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	public boolean isAhamoveTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_blkAhamove).findElement(loc_btnToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Ahamove turned on: " + isTurnedOn);
		return isTurnedOn;
	}

	public boolean isSelfDeliveryTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_blkSelfDelivery).findElement(loc_btnToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Self-Delivery turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isLocalATMTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_btnATMToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Local ATM turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isCreditCardTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_btnCreditCardToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Credit Card turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isCODTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_btnCODToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Cash On Delivery turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isCashTurnedOn() {
		boolean isTurnedOn = commonAction.getElement(loc_btnCashToggle).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Cash turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public ShippingAndPayment clickGHNToggle() {
		By chainedBy = new ByChained(loc_blkGHN, loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_blkGHN).findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(chainedBy)));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on GHN toggle button.");
		return this;
	}

	public ShippingAndPayment clickAhamoveToggle() {
		By chainedBy = new ByChained(loc_blkAhamove, loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_blkAhamove).findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(chainedBy)));
			return this;
		}
		commonAction.click(chainedBy);
		logger.info("Clicked on Ahamove toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickSelfDeliveryToggle() {
		By chainedBy = new ByChained(loc_blkSelfDelivery, loc_btnToggle);
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_blkSelfDelivery).findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(chainedBy)));
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
		commonAction.click(loc_btnSaveShippingProvider);
		logger.info("Clicked on Save button at Shipping Provider tab.");
		return this;
	}	
	
	/*--------------------------------------------------------------*/
	public ShippingAndPayment enablePaymentMethodFor(String productOrService) {
		if (productOrService.contentEquals("Product")) {
			if (!commonAction.getElement(loc_chkProduct).findElement(loc_tmpChecked).isDisplayed()) {
				commonAction.click(loc_chkProduct);
			}
		} else {
			if (!commonAction.getElement(loc_chkService).findElement(loc_tmpChecked).isDisplayed()) {
				commonAction.click(loc_chkService);
			}
		}
		logger.info("Enabled Payment Method for %s.".formatted(productOrService));
		return this;
	}

	public ShippingAndPayment clickLocalATMToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnATMToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnATMToggle)));
			return this;
		}
		commonAction.click(loc_btnATMToggle);
		logger.info("Clicked on Local ATM toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCreditCardToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnCreditCardToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnCreditCardToggle)));
			return this;
		}
		commonAction.click(loc_btnCreditCardToggle);
		logger.info("Clicked on Credit Card toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCODToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnCODToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnCODToggle)));
			return this;
		}
		commonAction.click(loc_btnCODToggle);
		logger.info("Clicked on Cash On Delivery toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCashToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnCashToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnCashToggle)));
			return this;
		}
		commonAction.click(loc_btnCashToggle);
		logger.info("Clicked on Cash toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickDebtToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnDebtToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnDebtToggle)));
			return this;
		}
		commonAction.click(loc_btnDebtToggle);
		logger.info("Clicked on Debt toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickPaypalToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnPaypalToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnPaypalToggle)));
			return this;
		}
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnPaypalToggle).findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnPaypalToggle)));
			return this;
		}
		commonAction.click(loc_btnPaypalToggle);
		logger.info("Clicked on Paypal toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickBankTransferToggle() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnBankTransferToggle).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnBankTransferToggle)));
			return this;
		}
		commonAction.click(loc_btnBankTransferToggle);
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
		if (!commonAction.getElement(loc_btnDebtToggle).findElement(By.xpath("./input")).isSelected()) {
			clickDebtToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Debt"));
		return this;
	}		
	
	public ShippingAndPayment enablePaypal() {
		if (!commonAction.getElement(loc_btnPaypalToggle).findElement(By.xpath("./input")).isSelected()) {
			clickPaypalToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Paypal"));
		return this;
	}		
	public ShippingAndPayment enableBankTransfer() {
		if (!commonAction.getElement(loc_btnBankTransferToggle).findElement(By.xpath("./input")).isSelected()) {
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
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
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }

}
