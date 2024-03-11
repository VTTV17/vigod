package web.Dashboard.promotion.discount.discountcode;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class ProductDiscountCodePage extends DiscountCodePage {

	final static Logger logger = LogManager.getLogger(ProductDiscountCodePage.class);

	public ProductDiscountCodePage(WebDriver driver) {
		super(driver);
	}

	public ProductDiscountCodePage navigateToDiscountCodeDetailScreenByURL(int productDiscountCodeId) {
		navigateByURL(DOMAIN + "/discounts/detail/COUPON/" + productDiscountCodeId);
		return this;
	}

	public ProductDiscountCodePage navigateToCreateDiscountCodeScreenByURL() {
		navigateByURL(DOMAIN + "/discounts/create/COUPON/");
		return this;
	}	

	public ProductDiscountCodePage navigateToEditDiscountCodeScreenByURL(int productDiscountCodeId) {
		navigateByURL(DOMAIN + "/discounts/edit/COUPON/" + productDiscountCodeId);
		return this;
	}		
	
}
