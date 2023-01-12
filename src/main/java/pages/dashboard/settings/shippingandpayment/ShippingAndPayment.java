package pages.dashboard.settings.shippingandpayment;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
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
	WebElement GHTK_TOGGLE;
	@FindBy(id = "provider-giaohangnhanh")
	WebElement GHN_TOGGLE;
	@FindBy(id = "provider-ahamove_bike")
	WebElement AHAMOVE_TOGGLE;
	@FindBy(id = "provider-selfdelivery")
	WebElement SELF_DELIVERY_TOGGLE;
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

	public ShippingAndPayment navigate() {
		clickShippingAndPaymentTab();
		return this;
	}

	public ShippingAndPayment clickShippingAndPaymentTab() {
		commonAction.clickElement(SHIPPING_AND_PAYMENT_TAB);
		logger.info("Clicked on Shipping And Payment tab.");
		return this;
	}

	public ShippingAndPayment clickGHTKToggle() {
		if (commonAction.isElementVisiblyDisabled(GHTK_TOGGLE.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(GHTK_TOGGLE.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(GHTK_TOGGLE.findElement(TOGGLE_BTN));
		logger.info("Clicked on GHTK toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickGHNToggle() {
		if (commonAction.isElementVisiblyDisabled(GHN_TOGGLE.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(GHN_TOGGLE.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(GHN_TOGGLE.findElement(TOGGLE_BTN));
		logger.info("Clicked on GHN toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickAhamoveToggle() {
		if (commonAction.isElementVisiblyDisabled(AHAMOVE_TOGGLE.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(AHAMOVE_TOGGLE.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(AHAMOVE_TOGGLE.findElement(TOGGLE_BTN));
		logger.info("Clicked on Ahamove toggle button.");
		return this;
	}
	
	public ShippingAndPayment clickSelfDeliveryToggle() {
		if (commonAction.isElementVisiblyDisabled(SELF_DELIVERY_TOGGLE.findElement(By.xpath("./parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(SELF_DELIVERY_TOGGLE.findElement(TOGGLE_BTN)));
			return this;
		}
		commonAction.clickElement(SELF_DELIVERY_TOGGLE.findElement(TOGGLE_BTN));
		logger.info("Clicked on Self-Delivery toggle button.");
		return this;
	}

	public ShippingAndPayment enableSelfDelivery() {
		if (!SELF_DELIVERY_TOGGLE.findElement(TOGGLE_BTN).findElement(By.xpath("./input")).isSelected()) {
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
		if (!LOCAL_ATM_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
			clickLocalATMToggle();
			commonAction.sleepInMiliSecond(2000);
		}
		logger.info("Enabled Payment Method for %s.".formatted("Local ATM Card"));
		return this;
	}	

	public ShippingAndPayment enableCreditCard() {
		if (!CREDIT_CARD_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
			clickCreditCardToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Credit Card"));
		return this;
	}		

	public ShippingAndPayment enableCOD() {
		if (!COD_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
			clickCODToggle();
		}
		logger.info("Enabled Payment Method for %s.".formatted("Cash On Delivery"));
		return this;
	}		
	
	public ShippingAndPayment enableCash() {
		if (!CASH_TOGGLE.findElement(By.xpath("./input")).isSelected()) {
			clickCashToggle();
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
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		clickGHTKToggle();
    	} else if (permission.contentEquals("D")) {
    		clickGHTKToggle();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableGHN(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		clickGHNToggle();
    	} else if (permission.contentEquals("D")) {
    		clickGHNToggle();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableAhamove(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		clickAhamoveToggle();
    	} else if (permission.contentEquals("D")) {
    		clickAhamoveToggle();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableSelfDelivery(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		enableSelfDelivery();
    	} else if (permission.contentEquals("D")) {
    		enableSelfDelivery();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    
    public void verifyPermissionToEnableLocalATMCard(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		enableLocalATM();
    		clickLocalATMToggle();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		enableLocalATM();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableCreditCard(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		enableCreditCard();
    		clickCreditCardToggle();
    	} else if (permission.contentEquals("D")) {
    		enableCreditCard();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableCOD(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		enableCOD();
    		clickCODToggle();
    	} else if (permission.contentEquals("D")) {
    		enableCOD();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableCash(String permission) {
    	clickShippingAndPaymentTab();
    	if (permission.contentEquals("A")) {
    		enableCash();
    		clickCashToggle();
    	} else if (permission.contentEquals("D")) {
    		enableCash();
    		// Not completed
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableDebt(String permission) {
    	clickShippingAndPaymentTab();
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
    	clickShippingAndPaymentTab();
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
