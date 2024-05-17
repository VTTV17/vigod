package web.Dashboard.settings.plans;

import org.openqa.selenium.By;

public class PlansPageElement {

	By loc_btnPay = By.xpath("//button[contains(@class,'btn-pay')]");
	By loc_icnLoading = By.xpath("//div[contains(@class,'loading-wrapper')]");
	By loc_lblOrderId = By.xpath("//table//tr[1]//td[@class='value']");
	By loc_btnOnlinePayment = By.cssSelector(".btn-group button:nth-of-type(1)");
	By loc_btnBankTransfer = By.cssSelector(".btn-group button:nth-of-type(2)");
	By loc_btnLogout = By.cssSelector(".wizard-layout__content a[href='/logout']");
	By loc_rdoATM = By.cssSelector("img[src*='atm']");
	By loc_rdoVISA = By.cssSelector("img[src*='visa']");
	By loc_rdoPAYPAL = By.cssSelector("img[src*='paypal']");
	By loc_tmpOverlayElement = By.cssSelector(".setting-plans-step3__overlay");
	
	String PLAN_PRICE_12M = "//tr[contains(@class,'plan-price')]//td[count(//div[text()='%planName%']//ancestor::th/preceding-sibling::*)+1]//button[not(contains(@class,'price-btn--disable'))]";
	
}
