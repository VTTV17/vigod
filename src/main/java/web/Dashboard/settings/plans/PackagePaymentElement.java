package web.Dashboard.settings.plans;

import org.openqa.selenium.By;

public class PackagePaymentElement {
	By loc_rdoDurationOptions = By.cssSelector(".package-payment-selection__package .options .option");
	String loc_rdoDurationByName = "//div[@class='package-payment-selection__package']/*[@class='option']/p[@class='desktop-price' and (contains(.,'noys Year') or contains(.,'noys năm'))]";
	By loc_blkPaymentMethodSection = By.cssSelector(".package-payment-selection__payment");
	By loc_frmPayPal = By.xpath("//div[@class='package-payment-selection__payment']/div[not(@hidden)]/div/div[starts-with(@id,'zoid-paypal-buttons-uid')]/iframe[@title='PayPal']");
	By loc_btnPayPal = By.cssSelector(".paypal-button img.paypal-logo");
	By loc_btnPayPalCard = By.cssSelector(".paypal-button img.paypal-logo-card");
	By loc_tabOnlinePayment = By.cssSelector(".package-payment-selection__payment .tabs .tab:nth-of-type(1)");
	By loc_rdoATM = By.cssSelector("img[src*='atm']");
	By loc_rdoVISA = By.cssSelector("img[src*='visa']");
	By loc_rdoPAYPAL = By.cssSelector("img[src*='paypal']");
	By loc_tabBankTransfer = By.cssSelector(".package-payment-selection__payment .tabs .tab:nth-of-type(2)");
	By loc_lblPackageName = By.cssSelector(".package-payment-total-section .name");
	By loc_lblPackageDuration = By.cssSelector(".package-payment-total-section .period");
	By loc_lblPackageBasePrice = By.cssSelector(".package-payment-total-section .pre-total .price");
	By loc_lblPackageVAT = By.cssSelector(".package-payment-total-section .tax .price");
	By loc_lblRefund = By.cssSelector(".package-payment-total-section .refund .price");
	By loc_lblPackageFinalTotal = By.cssSelector(".package-payment-total-section .total .price");
	By loc_btnPlaceOrder = By.cssSelector(".package-payment-total-section button.place-order");
	
	String loc_lblTopInfo = "(//*[@class='completed-page']//div[@class='top']//div[@class='item']/p[2])[%s]";
	By loc_lblContactInfo = By.xpath("//*[@class='completed-page']//div[@class='total-and-contact']//div[@class='total']/p[2]");
	By loc_btnBackToDashboard = By.cssSelector(".completed-page button[type='submit']");	
}
