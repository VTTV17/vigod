package web.Dashboard.settings.shippingandpayment;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

public class ShippingAndPaymentElement {

	By loc_btnGoogleAPIKey = By.cssSelector("[data-testid='btn-google-api']");
	By loc_txtGoogleAPIKey = By.id("googleApiKey");
	By loc_btnSaveGoogleAPIKey = By.cssSelector("[data-testid='confirmBtn']");
	
	By loc_tabShippingPayment = By.cssSelector("li:nth-child(3) > a.nav-link");
	
	By loc_btnToggle = By.xpath(".//label[contains(@class,'uik-checkbox__toggle')]");
	
	By loc_blkGHTK = By.id("provider-giaohangtietkiem");
	By loc_blkGHTKAncestor = new ByChained(loc_blkGHTK, By.xpath("./parent::*"));
	
	By loc_blkGHN = By.id("provider-giaohangnhanh");
	By loc_blkGHNAncestor = new ByChained(loc_blkGHN, By.xpath("./parent::*"));
	
	By loc_blkAhamove = By.id("provider-ahamove_bike");
	By loc_blkAhamoveAncestor = new ByChained(loc_blkAhamove, By.xpath("./parent::*"));
	
	By loc_blkVnPost = By.id("provider-vnpost");
	By loc_blkVnPostAncestor = new ByChained(loc_blkVnPost, By.xpath("./parent::*"));
	
	By loc_blkSelfDelivery = By.id("provider-selfdelivery");
	By loc_blkSelfDeliveryAncestor = new ByChained(loc_blkSelfDelivery, By.xpath("./parent::*"));
	
	By loc_btnSaveShippingProvider = By.cssSelector(".shipping__provider button.gs-button__blue");
	
	By loc_chkProduct = By.xpath("(//div[contains(@class,'payment__method')]//label[contains(@class,'custom-check-box')])[1]");
	By loc_chkService = By.xpath("(//div[contains(@class,'payment__method')]//label[contains(@class,'custom-check-box')])[2]");
	
	By loc_blkATM = By.xpath("//img[@alt='Local ATM']/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnATMToggle = new ByChained(loc_blkATM, loc_btnToggle);
	By loc_btnATMToggleAncestor = new ByChained(loc_btnATMToggle, By.xpath("./parent::*/parent::*"));
	
	By loc_blkCreditCard = By.xpath("//img[@alt='Credit card']/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnCreditCardToggle = new ByChained(loc_blkCreditCard, loc_btnToggle);
	By loc_btnCreditCardToggleAncestor = new ByChained(loc_btnCreditCardToggle, By.xpath("./parent::*/parent::*"));
	
	By loc_blkCOD = By.xpath("//img[contains(@src,'COD')]/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnCODToggle = new ByChained(loc_blkCOD, loc_btnToggle);
	By loc_btnCODToggleAncestor = new ByChained(loc_btnCODToggle, By.xpath("./parent::*/parent::*"));
	
	By loc_blkCash = By.xpath("//img[contains(@src,'cash')]/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnCashToggle = new ByChained(loc_blkCash, loc_btnToggle);
	By loc_btnCashToggleAncestor = new ByChained(loc_btnCashToggle, By.xpath("./parent::*/parent::*"));
	
	By loc_blkDebt = By.xpath("//img[@alt='debt']/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnDebtToggle = new ByChained(loc_blkDebt, loc_btnToggle);
	By loc_btnDebtToggleAncestor = new ByChained(loc_btnDebtToggle, By.xpath("./parent::*/parent::*"));
	
	By loc_blkPaypal = By.xpath("//img[@alt='paypal']/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnPaypalToggle = new ByChained(loc_blkPaypal, loc_btnToggle);
	By loc_btnPaypalToggleAncestor = new ByChained(loc_btnPaypalToggle, By.xpath("./parent::*/parent::*"));
	By loc_btnPaypalToggleAncestor1 = new ByChained(loc_btnPaypalToggle, By.xpath("./parent::*/parent::*/parent::*"));
	
	By loc_blkBankTransfer = By.xpath("//img[@alt='bank transfer']/ancestor::*[@class='setting__payment-wrapper']");
	By loc_btnBankTransferToggle = new ByChained(loc_blkBankTransfer, loc_btnToggle);
	By loc_btnBankTransferToggleAncestor = new ByChained(loc_btnBankTransferToggle, By.xpath("./parent::*/parent::*"));
	
	By loc_tmpInput = By.xpath("./input");
	
	By loc_tmpChecked = By.xpath(".//*[@class='uik-checkbox__checkboxIcon']");
	
	By loc_imgPaypal = By.xpath("//img[@alt='paypal']");
	
	By loc_txtMomoPartnerCode = By.id("partnerCode");
	By loc_txtMomoAccessKey = By.id("accessKey");
	By loc_txtMomoSecretKey = By.id("secretKey");
	By loc_btnMomoSave = By.cssSelector(".setting_btn_save");
	
	By loc_ddlDebtSegment = By.cssSelector(".select-customer-options");
	By loc_btnDebtSegmentDone = new ByChained(loc_ddlDebtSegment, By.cssSelector(".btn-save"));
	
	By loc_txtPayPalExchangeRate = By.id("paypalExchangeRate");
	By loc_tltPayPalExchangeRate = By.xpath("//*[contains(@class,'bank-condition paypal')]//div[@data-tooltipped]");
	
	By loc_btnVNPaySetting = By.cssSelector(".payment__method .uik-widget-title__wrapper button");
	By loc_btnSaveVNPaySetting = By.cssSelector(".VNPay-modal .gs-button__green");
	
}
