package web.Dashboard.promotion.discount.discountcode;

import org.openqa.selenium.By;

public class DiscountCodeElement {

	By loc_txtDiscountCodeName = By.cssSelector("input#name");

	//0: All customers
	//1: Specific segment
	By loc_rdoSegmentOptions = By.cssSelector("input[name ='conditionCustomerSegment']");
	By loc_lnkAddSegment = By.cssSelector("fieldset[name ='conditionCustomerSegment'] .gs-fake-link");

	//0: Entire order
	//1: Specific product collections
	//2: Specific products
	By loc_rdoApplyToOptions = By.cssSelector("fieldset[name ='conditionAppliesTo'] label");
	By loc_lnkAddCollectionOrSpecificProduct = By.cssSelector("fieldset[name ='conditionAppliesTo'] .gs-fake-link");

	By loc_btnSave = By.cssSelector(".gs-button__green");

	By loc_lblPageTitle = By.cssSelector(".gs-page-title");
	By loc_dlgSelectSegment = By.cssSelector(".select-segment-modal");
	By loc_dlgSelectCollection = By.cssSelector(".select-collection-modal");
	By loc_dlgSelectProduct = By.cssSelector(".product-no-variation-modal");
	By loc_txtSearchInDialog = By.cssSelector(".search-input");

	By loc_tblProductNames = By.cssSelector(".product-name");

}
