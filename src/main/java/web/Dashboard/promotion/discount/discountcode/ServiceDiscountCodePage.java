package web.Dashboard.promotion.discount.discountcode;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class ServiceDiscountCodePage extends DiscountCodePage {
	
	final static Logger logger = LogManager.getLogger(ServiceDiscountCodePage.class);
	
	public ServiceDiscountCodePage(WebDriver driver) {
		super(driver);
	}
	
	public ServiceDiscountCodePage navigateToDiscountCodeDetailScreenByURL(int serviceDiscountCodeId) {
		navigateByURL(DOMAIN + "/discounts/detail/COUPON_SERVICE/" + serviceDiscountCodeId);
		return this;
	}

	public ServiceDiscountCodePage navigateToCreateDiscountCodeScreenByURL() {
		navigateByURL(DOMAIN + "/discounts/create/COUPON_SERVICE/");
		return this;
	}	
	public ServiceDiscountCodePage navigateToEditDiscountCodeScreenByURL(int serviceDiscountCodeId) {
		navigateByURL(DOMAIN + "/discounts/edit/COUPON_SERVICE/" + serviceDiscountCodeId);
		return this;
	}	  	
	
}
