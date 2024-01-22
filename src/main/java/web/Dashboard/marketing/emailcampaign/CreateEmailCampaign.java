package web.Dashboard.marketing.emailcampaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class CreateEmailCampaign {

	final static Logger logger = LogManager.getLogger(CreateEmailCampaign.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreateEmailCampaign(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtCampaignName = By.id("campaignName");

	public CreateEmailCampaign inputCampaignName(String campaignName) {
		commonAction.sendKeys(loc_txtCampaignName, campaignName);
		logger.info("Input '" + campaignName + "' into Campaign Name field.");
		return this;
	}

}
