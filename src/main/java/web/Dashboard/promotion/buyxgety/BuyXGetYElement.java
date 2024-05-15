package web.Dashboard.promotion.buyxgety;

import org.openqa.selenium.By;

public class BuyXGetYElement {

	String loc_lstPromotionRoot = "//div[contains(@class, 'buy-x-get-y-table')]//tbody/tr";
    By loc_lstPromotionName = By.xpath(loc_lstPromotionRoot + "/td[1]");
    By loc_lstGiveawayType = By.xpath(loc_lstPromotionRoot + "/td[2]");
    By loc_lstPromotionActiveDate = By.xpath(loc_lstPromotionRoot + "/td[3]");
    By loc_lstPromotionStatus = By.xpath(loc_lstPromotionRoot + "/td[4]");
    By loc_lstTotalOrder = By.xpath(loc_lstPromotionRoot + "/td[5]");
    
    String loc_tmpDetailFieldRoot = "(//div[contains(@class, 'bxgy-detail-item')])[%s]/div[2]";
    By loc_lblNameField = By.xpath(loc_tmpDetailFieldRoot.formatted(1));
    By loc_lblActiveDateField = By.xpath(loc_tmpDetailFieldRoot.formatted(2));
    
    By loc_btnCreatePromotion = By.cssSelector(".gss-content-header .gs-button__green");
    
	By loc_txtPromotionName = By.cssSelector("input#name");

	//0: All customers
	//1: Specific segment
	By loc_rdoSegmentOptions = By.xpath("(//div[contains(@class,'condition-group')])[1]//input");
	By loc_lnkAddSegment = By.xpath("(//div[contains(@class,'condition-group')])[1]//span[starts-with(@class,'gs-fake-link')]");

	//0: Combo
	//1: Any items on a list
	By loc_rdoApplyToOptions = By.xpath("(//div[contains(@class,'condition-group')])[2]//label//input");
	By loc_btnAddProductForCombo = By.className("btn-add-product");
	
	By loc_rdoApplyToAnyItemOptions = By.xpath("((//div[contains(@class,'condition-group')])[2]//div[contains(@class,'radio-field-form-control')]/preceding-sibling::b)[2]/following-sibling::*//input");
	
	By loc_lnkAddCollectionOrSpecificProduct = By.xpath("(//div[contains(@class,'condition-group')])[2]//span[starts-with(@class,'gs-fake-link')]");

	//0: Collections
	//1: Products
	By loc_rdoGiftOptions = By.xpath("(//div[contains(@class,'condition-group')])[3]//label//input");
	By loc_lnkAddCollectionOrSpecificProductAsGift = By.xpath("(//div[contains(@class,'condition-group')])[3]//span[starts-with(@class,'gs-fake-link')]");
	
	By loc_btnSave = By.cssSelector(".gs-button__green--outline");
	By loc_btnMarkExpired = By.cssSelector(".gs-button__green");
	By loc_btnStop = By.id("stop");
	
	By loc_lblPageTitle = By.cssSelector(".gs-page-title");
	By loc_dlgSelectSegment = By.cssSelector(".select-segment-modal");
	By loc_tblSegmentNames = By.cssSelector(".segment-name");
	By loc_dlgSelectCollection = By.cssSelector(".select-collection-modal");
	By loc_tblCollectionNames = By.cssSelector(".product-name");
	By loc_dlgSelectProduct = By.cssSelector(".product-select-variation-modal");
	By loc_tblProductNames = By.cssSelector(".product-select-variation-modal .item-info-name");
	By loc_txtSearchInDialog = By.cssSelector(".modal-body input[type='text']");
	

	

}
