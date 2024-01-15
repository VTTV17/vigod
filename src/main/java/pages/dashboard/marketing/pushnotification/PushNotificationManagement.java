package pages.dashboard.marketing.pushnotification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class PushNotificationManagement {

	final static Logger logger = LogManager.getLogger(PushNotificationManagement.class);

	WebDriver driver;
	UICommonAction commonAction;

	public PushNotificationManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

    By loc_btnExploreNow = By.cssSelector(".notification-intro .gs-button__green");
    By loc_btnCreateCampaign = By.cssSelector(".notification-header button.gs-button__green"); //Temporary
    
    public PushNotificationManagement clickExploreNow() {
    	commonAction.click(loc_btnExploreNow);
    	logger.info("Clicked on 'Explore Now' button.");
    	
    	//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
    	for (int i=0; i<30; i++) {
    		if (commonAction.getElements(loc_btnCreateCampaign).size() >0) break;
    		commonAction.sleepInMiliSecond(500);
    	}
    	return this;
    }        
    
    public PushNotificationManagement clickCreateCampaign() {
    	commonAction.click(loc_btnCreateCampaign);
    	logger.info("Clicked on 'Create Campaign' button.");
    	return this;
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreatePushNotification(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickExploreNow();
			commonAction.sleepInMiliSecond(1000); //Sometimes it navigates to Create Campaign screen. Temporary
			clickCreateCampaign();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/       
    
}
