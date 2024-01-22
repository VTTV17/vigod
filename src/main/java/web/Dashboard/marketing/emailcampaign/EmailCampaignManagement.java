package web.Dashboard.marketing.emailcampaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class EmailCampaignManagement {

	final static Logger logger = LogManager.getLogger(EmailCampaignManagement.class);

	WebDriver driver;
	UICommonAction commonAction;

	public EmailCampaignManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

    By loc_btnCreateEmailCampaign = By.cssSelector(".marketing-email-campaign-list .gss-content-header--undefined button");	
    
    public EmailCampaignManagement clickCreateEmailCampaign() {
    	commonAction.click(loc_btnCreateEmailCampaign);
    	logger.info("Clicked on 'Create New Email Campaign' button.");
    	return this;
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateEmailCampaign(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateEmailCampaign();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/     
    
}
