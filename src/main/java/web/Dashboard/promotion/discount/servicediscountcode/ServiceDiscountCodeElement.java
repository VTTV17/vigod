package web.Dashboard.promotion.discount.servicediscountcode;

import org.openqa.selenium.By;

public class ServiceDiscountCodeElement {

    //0: Entire order
    //1: Specific product collections
    //2: Specific products
    By loc_rdoApplyToOptions = By.cssSelector("fieldset[name ='conditionAppliesTo'] label");
    By loc_lnkAddCollectionOrSpecificProduct = By.cssSelector("fieldset[name ='conditionAppliesTo'] .gs-fake-link");
    
    By loc_dlgSelectSegment = By.cssSelector(".select-segment-modal");
    By loc_dlgSelectCollection = By.cssSelector(".select-collection-modal");
    By loc_dlgSelectService = By.cssSelector(".product-no-variation-modal");
    By loc_txtSearchInDialog = By.cssSelector(".search-input");
    
    By loc_tblServiceNames = By.cssSelector(".product-name");
    
    By loc_btnSave = By.cssSelector(".gs-button__green");
    
    By loc_lblPageTitle = By.cssSelector(".gs-page-title");
	By loc_chkApplyDiscountAsReward = By.xpath("(//form//div[contains(@class,'col-xl-12')]//label[contains(@class, 'custom-check-box')])[1]");
	By loc_txtRewardDescription = By.cssSelector(".show-placeholder > div");
	By loc_pnlPlatforms = By.cssSelector("fieldset[name = 'conditionPlatform'] label");
}
