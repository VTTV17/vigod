package pages.dashboard.gochat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Facebook {
	WebDriver driver;
	UICommonAction commons;

	final static Logger logger = LogManager.getLogger(Facebook.class);

	public Facebook(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_btnConnectFacebook = By.cssSelector(".btn-connect");

	public Facebook clickConnectFacebook() {
		commons.click(loc_btnConnectFacebook);
		logger.info("Clicked on 'Connect Facebook' button.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToConnectToFacebook(String permission) {
		if (permission.contentEquals("A")) {
			new Facebook(driver).clickConnectFacebook();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    
    /*-------------------------------------*/	
	
}
