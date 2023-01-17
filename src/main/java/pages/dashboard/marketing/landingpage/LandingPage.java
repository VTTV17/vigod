package pages.dashboard.marketing.landingpage;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class LandingPage {
	final static Logger logger = LogManager.getLogger(LandingPage.class);
	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;
	LandingPageElement landingPageUI;

	public LandingPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		landingPageUI = new LandingPageElement(driver);
		PageFactory.initElements(driver, this);
	}

	public CreateLandingPage clickCreateLandingPage() {
		commonAction.clickElement(landingPageUI.CREATE_PAGE_LANDING_BTN);
		logger.info("Clicked on 'Create New Landing Page' button");
		return new CreateLandingPage(driver);
	}

	public boolean isPermissionModalDisplay() {
		commonAction.sleepInMiliSecond(500);
		return commonAction.isElementDisplay(landingPageUI.PERMISSION_MODAL);
	}

	public LandingPage closeModal() {
		commonAction.clickElement(landingPageUI.CLOSE_MODAL_BTN);
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateLandingPage(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateLandingPage().clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToAddAnalyticsToLandingPage(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickCreateLandingPage().inputGoogleAnalyticsId("123456").clickCancelBtn();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToAddSEOToLandingPage(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickCreateLandingPage().inputSEOTitle("Test Permission").clickCancelBtn();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToAddCustomerTagToLandingPage(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickCreateLandingPage().inputCustomerTag("Test Permission").clickCancelBtn();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToCustomDomain(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickCreateLandingPage().inputSubDomain("testdomain@gmail.com").clickCancelBtn();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    /*-------------------------------------*/  	
	
}
