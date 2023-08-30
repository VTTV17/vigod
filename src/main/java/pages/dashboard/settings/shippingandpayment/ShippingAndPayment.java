package pages.dashboard.settings.shippingandpayment;

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
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ShippingAndPayment {

	final static Logger logger = LogManager.getLogger(ShippingAndPayment.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	public ShippingAndPayment(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "li:nth-child(3) > a.nav-link")
	WebElement SHIPPING_AND_PAYMENT_TAB;

	@FindBy(id = "provider-giaohangtietkiem")
	WebElement GHTK;
	@FindBy(id = "provider-giaohangnhanh")
	WebElement GHN;
	@FindBy(id = "provider-ahamove_bike")
	WebElement AHAMOVE;
	@FindBy(id = "provider-selfdelivery")
	WebElement SELF_DELIVERY;
	@FindBy(css = ".shipping__provider  button.gs-button__blue")
	WebElement SHIPPING_PROVIDER_SAVE_BTN;

	@FindBy(xpath = "(//div[contains(@class,'payment__method')]//label[contains(@class,'custom-check-box')])[1]")
	WebElement PRODUCT_CHECKBOX;
	@FindBy(xpath = "(//div[contains(@class,'payment__method')]//label[contains(@class,'custom-check-box')])[2]")
	WebElement SERVICE_CHECKBOX;
	
	@FindBy(xpath = "//img[@alt='Local ATM']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]")
	WebElement LOCAL_ATM_TOGGLE;	
	@FindBy(xpath = "//img[@alt='Credit card']/parent::*/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]")
	WebElement CREDIT_CARD_TOGGLE;	
	@FindBy(xpath = "//img[contains(@src,'COD')]/parent::*/following-sibling::*/label[contains(@class,'uik-checkbox__toggle')]")
	WebElement COD_TOGGLE;	
	@FindBy(xpath = "//img[contains(@src,'cash')]/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]")
	WebElement CASH_TOGGLE;	
	@FindBy(xpath = "//img[@alt='debt']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]")
	WebElement DEBT_TOGGLE;	
	@FindBy(xpath = "//img[@alt='paypal']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]")
	WebElement PAYPAL_TOGGLE;	
	@FindBy(xpath = "//img[@alt='bank transfer']/parent::*/following-sibling::*//label[contains(@class,'uik-checkbox__toggle')]")
	WebElement BANK_TRANSFER_TOGGLE;	
	
	@FindBy(css = "input#seoTitle")
	WebElement SEO_TITLE;

	By TOGGLE_BTN = By.xpath(".//label[contains(@class,'uik-checkbox__toggle')]");
	By ISBOXCHECKED = By.xpath(".//*[@class='uik-checkbox__checkboxIcon']");
	By PAYPAL_IMAGE = By.xpath("//img[@alt='paypal']");

	public ShippingAndPayment navigate() {
		clickShippingAndPaymentTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	
    	//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
    	for (int i=0; i<30; i++) {
    		if (commonAction.getElements(PAYPAL_IMAGE).size() >0) break;
    		commonAction.sleepInMiliSecond(500);
    	}
		return this;
	}

	public ShippingAndPayment clickShippingAndPaymentTab() {
		commonAction.clickElement(SHIPPING_AND_PAYMENT_TAB);
		logger.info("Clicked on Shipping And Payment tab.");
		return this;
	}

	public ShippingAndPayment clickGHTKToggle() {
		if (commonAction.isElementVisiblyDisabled(GHTK.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(GHTK.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(GHTK.findElement(TOGGLE_BTN));
		logger.info("Clicked on GHTK toggle button.");
		return this;
	}
	
	public boolean isGHTKTurnedOn() {
		boolean isTurnedOn = GHTK.findElement(TOGGLE_BTN).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Giao Hang Tiet Kiem turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	public boolean isGHNTurnedOn() {
		boolean isTurnedOn = GHN.findElement(TOGGLE_BTN).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Giao Hang Nhanh turned on: " + isTurnedOn);
		return isTurnedOn;
	}
	
	public boolean isAhamoveTurnedOn() {
		boolean isTurnedOn = AHAMOVE.findElement(TOGGLE_BTN).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Ahamove turned on: " + isTurnedOn);
		return isTurnedOn;
	}

	public boolean isSelfDeliveryTurnedOn() {
		boolean isTurnedOn = SELF_DELIVERY.findElement(TOGGLE_BTN).findElement(By.xpath("./input")).isSelected();
		logger.info("Is Self-Delivery turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isLocalATMTurnedOn() {
		boolean isTurnedOn = LOCAL_ATM_TOGGLE.findElement(By.xpath("./input")).isSelected();
		logger.info("Is Local ATM turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isCreditCardTurnedOn() {
		wait.until(ExpectedConditions.visibilityOf(CREDIT_CARD_TOGGLE)); // It takes some time for the element to appear.
		boolean isTurnedOn = CREDIT_CARD_TOGGLE.findElement(By.xpath("./input")).isSelected();
		logger.info("Is Credit Card turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isCODTurnedOn() {
		boolean isTurnedOn = COD_TOGGLE.findElement(By.xpath("./input")).isSelected();
		logger.info("Is Cash On Delivery turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public boolean isCashTurnedOn() {
		boolean isTurnedOn = CASH_TOGGLE.findElement(By.xpath("./input")).isSelected();
		logger.info("Is Cash turned on: " + isTurnedOn);
		return isTurnedOn;
	}	
	
	public ShippingAndPayment clickGHNToggle() {
		if (commonAction.isElementVisiblyDisabled(GHN.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(GHN.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(GHN.findElement(TOGGLE_BTN));
		logger.info("Clicked on GHN toggle button.");
		return this;
	}

	public ShippingAndPayment clickAhamoveToggle() {
		if (commonAction.isElementVisiblyDisabled(AHAMOVE.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(AHAMOVE.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(AHAMOVE.findElement(TOGGLE_BTN));
		logger.info("Clicked on Ahamove toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickSelfDeliveryToggle() {
		if (commonAction.isElementVisiblyDisabled(SELF_DELIVERY.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(SELF_DELIVERY.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(SELF_DELIVERY.findElement(TOGGLE_BTN));
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
		commonAction.clickElement(SHIPPING_PROVIDER_SAVE_BTN);
		logger.info("Clicked on Save button at Shipping Provider tab.");
		return this;
	}	
	
	/*--------------------------------------------------------------*/
	public ShippingAndPayment enablePaymentMethodFor(String productOrService) {
		if (productOrService.contentEquals("Product")) {
			if (!PRODUCT_CHECKBOX.findElement(ISBOXCHECKED).isDisplayed()) {
				commonAction.clickElement(PRODUCT_CHECKBOX);
			}
		} else {
			if (!SERVICE_CHECKBOX.findElement(ISBOXCHECKED).isDisplayed()) {
				commonAction.clickElement(SERVICE_CHECKBOX);
			}
		}
		logger.info("Enabled Payment Method for %s.".formatted(productOrService));
		return this;
	}

	public ShippingAndPayment clickLocalATMToggle() {
		if (commonAction.isElementVisiblyDisabled(LOCAL_ATM_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(LOCAL_ATM_TOGGLE));
			return this;
		}
		commonAction.clickElement(LOCAL_ATM_TOGGLE);
		logger.info("Clicked on Local ATM toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCreditCardToggle() {
		if (commonAction.isElementVisiblyDisabled(CREDIT_CARD_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(CREDIT_CARD_TOGGLE));
			return this;
		}
		commonAction.clickElement(CREDIT_CARD_TOGGLE);
		logger.info("Clicked on Credit Card toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCODToggle() {
		if (commonAction.isElementVisiblyDisabled(COD_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(COD_TOGGLE));
			return this;
		}
		commonAction.clickElement(COD_TOGGLE);
		logger.info("Clicked on Cash On Delivery toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickCashToggle() {
		if (commonAction.isElementVisiblyDisabled(CASH_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(CASH_TOGGLE));
			return this;
		}
		commonAction.clickElement(CASH_TOGGLE);
		logger.info("Clicked on Cash toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickDebtToggle() {
		if (commonAction.isElementVisiblyDisabled(DEBT_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(DEBT_TOGGLE));
			return this;
		}
		commonAction.clickElement(DEBT_TOGGLE);
		logger.info("Clicked on Debt toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickPaypalToggle() {
		if (commonAction.isElementVisiblyDisabled(PAYPAL_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(PAYPAL_TOGGLE));
			return this;
		}
		if (commonAction.isElementVisiblyDisabled(PAYPAL_TOGGLE.findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(PAYPAL_TOGGLE));
			return this;
		}
		commonAction.clickElement(PAYPAL_TOGGLE);
		logger.info("Clicked on Paypal toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickBankTransferToggle() {
		if (commonAction.isElementVisiblyDisabled(BANK_TRANSFER_TOGGLE.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(BANK_TRANSFER_TOGGLE));
			return this;
		}
		commonAction.clickElement(BANK_TRANSFER_TOGGLE);
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
		if (!DEBT_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
			clickDebtToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Debt"));
		return this;
	}		
	
	public ShippingAndPayment enablePaypal() {
		if (!PAYPAL_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
			clickPaypalToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Paypal"));
		return this;
	}		
	public ShippingAndPayment enableBankTransfer() {
		if (!BANK_TRANSFER_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
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
