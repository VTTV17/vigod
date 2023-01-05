package pages.dashboard.promotion.discount.servicediscountcode;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import pages.dashboard.onlineshop.Domains;
import pages.dashboard.promotion.discount.product_discount_code.ProductDiscountCodePage;
import utilities.UICommonAction;

public class ServiceDiscountCodePage {

	final static Logger logger = LogManager.getLogger(ServiceDiscountCodePage.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public ServiceDiscountCodePage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "div.col-xl-12 > div[class = ' '] > label:nth-child(1)")
	WebElement APPLY_DISCOUNT_CODE_AS_REWARD_CHECKBOX;

	@FindBy(css = ".show-placeholder > div")
	WebElement REWARD_DESCRIPTION;

	// 0: Web
	// 1: App
	// 2: In-store
	@FindBy(css = "fieldset[name = 'conditionPlatform'] label")
	List<WebElement> PLATFORM;

	@FindBy(css = ".btn-create.ml-auto")
	WebElement COMPLETE_BTN;

	public ServiceDiscountCodePage tickApplyDiscountCodeAsRewardCheckBox(boolean isTicked) {
		if (isTicked) {
			commonAction.checkTheCheckBoxOrRadio(APPLY_DISCOUNT_CODE_AS_REWARD_CHECKBOX);
			logger.info("Checked 'Apply Discount Code as a Reward' checkbox.");
		} else {
			commonAction.uncheckTheCheckboxOrRadio(APPLY_DISCOUNT_CODE_AS_REWARD_CHECKBOX);
			logger.info("Un-checked 'Apply Discount Code as a Reward' checkbox.");
		}
		return this;
	}

	public ServiceDiscountCodePage inputRewardDescription(String rewardDescription) {
		commonAction.inputText(REWARD_DESCRIPTION, rewardDescription);
		logger.info("Input '" + rewardDescription + "' into Reward Description field.");
		return this;
	}

	public void waitElementList(List<WebElement> elementList) {
		wait.until((ExpectedCondition<Boolean>) driver -> {
			assert driver != null;
			return elementList.size() > 0;
		});
	}

	public boolean isPlatformDisabled(String platform) {
		waitElementList(PLATFORM);
		WebElement element = null;
		switch (platform) {
		case "Web":
			element = PLATFORM.get(0);
			break;
		case "App":
			element = PLATFORM.get(1);
			break;
		case "In-store":
			element = PLATFORM.get(2);
			break;
		}

		if (commonAction.isElementVisiblyDisabled(element.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(element));
			return true;
		}
		return false;
	}

	public ServiceDiscountCodePage setPlatforms(List<String> platforms) {
		waitElementList(PLATFORM);
		for (int i = 0; i < PLATFORM.size(); i++) {
			if (platforms.contains(PLATFORM.get(i).getText())) {
				PLATFORM.get(i).click();
			}
		}
		return this;
	}

	
	
}
