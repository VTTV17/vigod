package web.Dashboard.promotion.discount.servicediscountcode;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class ServiceDiscountCodePage {

	final static Logger logger = LogManager.getLogger(ServiceDiscountCodePage.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ServiceDiscountCodePage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_chkApplyDiscountAsReward = By.xpath("(//form//div[contains(@class,'col-xl-12')]//label[contains(@class, 'custom-check-box')])[1]");
	By loc_txtRewardDescription = By.cssSelector(".show-placeholder > div");
	By loc_pnlPlatforms = By.cssSelector("fieldset[name = 'conditionPlatform'] label");
	
	public ServiceDiscountCodePage tickApplyDiscountCodeAsRewardCheckBox(boolean isTicked) {
		WebElement el = commonAction.getElement(loc_chkApplyDiscountAsReward);
		if (isTicked) {
			commonAction.checkTheCheckBoxOrRadio(el);
			logger.info("Checked 'Apply Discount Code as a Reward' checkbox.");
		} else {
			commonAction.uncheckTheCheckboxOrRadio(el);
			logger.info("Un-checked 'Apply Discount Code as a Reward' checkbox.");
		}
		return this;
	}

	public ServiceDiscountCodePage inputRewardDescription(String rewardDescription) {
		commonAction.sendKeys(loc_txtRewardDescription, rewardDescription);
		logger.info("Input '" + rewardDescription + "' into Reward Description field.");
		return this;
	}

	public boolean isPlatformDisabled(String platform) {
		List<WebElement> el = commonAction.getListElement(loc_pnlPlatforms);
		WebElement element = null;
		switch (platform) {
		case "web":
			element = el.get(0);
			break;
		case "App":
			element = el.get(1);
			break;
		case "In-store":
			element = el.get(2);
			break;
		}

		if (commonAction.isElementVisiblyDisabled(element.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(element));
			return true;
		}
		return false;
	}

	public ServiceDiscountCodePage setPlatforms(List<String> platforms) {
		for (WebElement e : commonAction.getListElement(loc_pnlPlatforms)) {
			if (platforms.contains(e.getText())) {
				commonAction.clickElement(e);
			}
		}
		return this;
	}

	
	
}
