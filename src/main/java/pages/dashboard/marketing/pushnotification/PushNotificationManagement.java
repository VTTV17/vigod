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

import pages.dashboard.marketing.buylink.BuyLinkManagement;
import utilities.UICommonAction;

public class PushNotificationManagement {

	final static Logger logger = LogManager.getLogger(PushNotificationManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public PushNotificationManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

    @FindBy(css = ".notification-intro .gs-button__green")
    WebElement EXPLORE_NOW_BTN;	
	
    @FindBy(css = ".notification-header button.gs-button__green")
    WebElement CREATE_CAMPAIGN_BTN;	

    
    public PushNotificationManagement clickExploreNow() {
    	commonAction.clickElement(EXPLORE_NOW_BTN);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }        
    
    public PushNotificationManagement clickCreateCampaign() {
    	commonAction.clickElement(CREATE_CAMPAIGN_BTN);
    	logger.info("Clicked on 'Create Campaign' button.");
    	return this;
    }    	

}
