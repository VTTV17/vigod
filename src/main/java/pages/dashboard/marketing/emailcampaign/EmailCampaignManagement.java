package pages.dashboard.marketing.emailcampaign;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class EmailCampaignManagement {

	final static Logger logger = LogManager.getLogger(EmailCampaignManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public EmailCampaignManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

    @FindBy(css = ".marketing-email-campaign-list .gss-content-header--undefined button")
    WebElement CREATE_EMAIL_CAMPAIGN_BTN;	
    
    public EmailCampaignManagement clickCreateEmailCampaign() {
    	commonAction.clickElement(CREATE_EMAIL_CAMPAIGN_BTN);
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
