package web.Dashboard.sales_channels.shopee.link_products;

import org.openqa.selenium.By;

public class LinkProductsElement {
	By loc_txtSearchBox = By.xpath("//div[@class='ml-auto']/preceding-sibling::*//input");

	By loc_txtSearchGosellProductNameByShopeeItemId(String shopeeItemId) {
		return By.xpath("//div[contains(@class,'shopee-link-product')]//table/tbody/tr/td[.='%s']//following-sibling::*/div[@class='link-product-search-box']//input".formatted(shopeeItemId));
	}
	
	By loc_ddvSearchResult(String gosellProductName) {
		return By.xpath("//div[@class='search-box-dropdown']/div[.=\"%s\"]".formatted(gosellProductName));
	}

	// TODO make this description clearer
	/**
	 * Gets locator of a variation row by index. When the index >=0, this function
	 * returns the locator with a specific index
	 * @param index is 1-based
	 */
	By loc_tblLinkVariationRow(int index) {
		if (index < 0)
			return By.xpath("//*[starts-with(@class,'modal-dialog')]//table//tbody/tr");
		return By.xpath("(//*[starts-with(@class,'modal-dialog')]//table//tbody/tr)[%s]".formatted(index));
	}
	
	By loc_tblShopeeVariationRow(int index) {
		return By.xpath("(//*[starts-with(@class,'modal-dialog')]//table//tbody/tr)[%s]/td[1]".formatted(index));
	}

	By loc_ddlGosellVariation(int index) {
		return By.xpath("(//*[starts-with(@class,'modal-dialog')]//table//tbody/tr//div[contains(@class,'control')])[%s]".formatted(index));
	}

	By loc_ddvGosellVariation(int index) {
		return By.xpath("(//*[starts-with(@class,'modal-dialog')]//table//tbody/tr)[%s]//div[contains(@id,'listbox')]//div[contains(@id,'option')]".formatted(index));
	}
	
    By loc_chkShopeeProductId(String shopeeProductId) {
    	return By.xpath("//td[.='%s']//preceding-sibling::*//div[contains(@class,'uik-checkbox__label')]".formatted(shopeeProductId));
    }
    
    By loc_lnkSelectAction = By.cssSelector(".gs-dropdown-action .gs-fake-link");	
    
    By loc_ddvUnlink = By.xpath("//div[@class=' actions expand']/div[starts-with(.,'Hủy')]");
}
