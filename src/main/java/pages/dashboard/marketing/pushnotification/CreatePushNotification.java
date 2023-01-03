package pages.dashboard.marketing.pushnotification;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class CreatePushNotification {

	final static Logger logger = LogManager.getLogger(CreatePushNotification.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreatePushNotification(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "campaignName")
	WebElement CAMPAIGN_NAME;

	public CreatePushNotification inputCampaignName(String campaignName) {
		commonAction.inputText(CAMPAIGN_NAME, campaignName);
		logger.info("Input '" + campaignName + "' into Campaign Name field.");
		return this;
	}

}
