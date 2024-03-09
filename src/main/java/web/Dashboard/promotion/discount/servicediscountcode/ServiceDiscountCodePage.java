package web.Dashboard.promotion.discount.servicediscountcode;

import static utilities.links.Links.DOMAIN;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

public class ServiceDiscountCodePage {

	final static Logger logger = LogManager.getLogger(ServiceDiscountCodePage.class);

	WebDriver driver;
	UICommonAction commonAction;
	ServiceDiscountCodeElement elements;

	public ServiceDiscountCodePage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		elements = new ServiceDiscountCodeElement();
	}

	public ServiceDiscountCodePage tickApplyDiscountCodeAsRewardCheckBox(boolean isTicked) {
		WebElement el = commonAction.getElement(elements.loc_chkApplyDiscountAsReward);
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
		commonAction.sendKeys(elements.loc_txtRewardDescription, rewardDescription);
		logger.info("Input '" + rewardDescription + "' into Reward Description field.");
		return this;
	}

	public boolean isPlatformDisabled(String platform) {
		List<WebElement> el = commonAction.getListElement(elements.loc_pnlPlatforms);
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
		for (WebElement e : commonAction.getListElement(elements.loc_pnlPlatforms)) {
			if (platforms.contains(e.getText())) {
				commonAction.clickElement(e);
			}
		}
		return this;
	}
	
	public ServiceDiscountCodePage clickSaveBtn() {
		commonAction.click(elements.loc_btnSave);
		logger.info("Clicked on Save button");
		return this;
	}		
	
	public ServiceDiscountCodePage navigateToDiscountCodeDetailScreenByURL(int serviceDiscountCodeId) {
		String url = DOMAIN + "/discounts/detail/COUPON_SERVICE/" + serviceDiscountCodeId;
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	
	public ServiceDiscountCodePage navigateToCreateDiscountCodeScreenByURL() {
		String url = DOMAIN + "/discounts/create/COUPON_SERVICE/";
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	 	

	public ServiceDiscountCodePage navigateToEditDiscountCodeScreenByURL(int serviceDiscountCodeId) {
		String url = DOMAIN + "/discounts/edit/COUPON_SERVICE/" + serviceDiscountCodeId;
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	  	
	
	public ServiceDiscountCodePage selectApplyToOption(int option) {
		commonAction.click(elements.loc_rdoApplyToOptions, option);
		logger.info("Selected Apply To option: " + option);
		return this;
	}

	public ServiceDiscountCodePage clickAddCollectionLink() {
		commonAction.click(elements.loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Collection link");
		for (int i=0; i<5; i++) {
			if (!commonAction.getElements(elements.loc_dlgSelectCollection).isEmpty()) break;
			commonAction.sleepInMiliSecond(500, "Wait a little until the Add Collection dialog to appear");
		}
		return this;
	}	
	
	public ServiceDiscountCodePage clickAddServiceLink() {
		commonAction.click(elements.loc_lnkAddCollectionOrSpecificProduct);
		logger.info("Clicked on Add Service link");
		for (int i=0; i<5; i++) {
			if (!commonAction.getElements(elements.loc_dlgSelectService).isEmpty()) break;
			commonAction.sleepInMiliSecond(500, "Wait a little until the Select Services dialog to appear");
		}
		return this;
	}	

	public boolean isCollectionPresentInDialog() {
		commonAction.sleepInMiliSecond(1000, "Wait a little for collections to appear in the Add Collection dialog");
		return !commonAction.getElements(elements.loc_tblServiceNames).isEmpty();
	}		
	
	public boolean isServicePresentInDialog() {
		commonAction.sleepInMiliSecond(1000, "Wait a little for services to appear in the Add Services dialog");
		return !commonAction.getElements(elements.loc_tblServiceNames).isEmpty();
	}		
	
    public ServiceDiscountCodePage inputSearchTermInDialog(String searchTerm) {
    	commonAction.inputText(elements.loc_txtSearchInDialog, searchTerm);
        logger.info("Input search term: " + searchTerm);
        commonAction.sleepInMiliSecond(1000, "Wait a little inputSearchTermInDialog"); // Will find a better way to remove this sleep
        new HomePage(driver).waitTillSpinnerDisappear1();
        return this;
    }		
	
    public String getPageTitle() {
    	String title = commonAction.getText(elements.loc_lblPageTitle);
    	logger.info("Retrieved page title: " + title);
    	return title;
    }	
	
}
