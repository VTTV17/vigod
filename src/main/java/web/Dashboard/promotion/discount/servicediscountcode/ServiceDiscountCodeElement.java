package web.Dashboard.promotion.discount.servicediscountcode;

import org.openqa.selenium.By;

public class ServiceDiscountCodeElement {

    By loc_lblPageTitle = By.cssSelector(".gs-page-title");
	By loc_chkApplyDiscountAsReward = By.xpath("(//form//div[contains(@class,'col-xl-12')]//label[contains(@class, 'custom-check-box')])[1]");
	By loc_txtRewardDescription = By.cssSelector(".show-placeholder > div");
	By loc_pnlPlatforms = By.cssSelector("fieldset[name = 'conditionPlatform'] label");
}
