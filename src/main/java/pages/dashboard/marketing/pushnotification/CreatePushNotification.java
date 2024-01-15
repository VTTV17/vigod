package pages.dashboard.marketing.pushnotification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class CreatePushNotification {

	final static Logger logger = LogManager.getLogger(CreatePushNotification.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreatePushNotification(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtCampaignName = By.id("campaignName");

	public CreatePushNotification inputCampaignName(String campaignName) {
		commonAction.sendKeys(loc_txtCampaignName, campaignName);
		logger.info("Input '" + campaignName + "' into Campaign Name field.");
		return this;
	}

}
