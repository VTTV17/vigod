package pages.dashboard.gochat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Zalo {
	WebDriver driver;
	UICommonAction commons;

	final static Logger logger = LogManager.getLogger(Zalo.class);

	public Zalo(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_btnConnectZalo = By.cssSelector(".btn-connect");

	public Zalo clickConnectZalo() {
		commons.click(loc_btnConnectZalo);
		logger.info("Clicked on 'Connect Zalo' button.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToConnectToZalo(String permission) {
		if (permission.contentEquals("A")) {
			new Zalo(driver).clickConnectZalo();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    
    /*-------------------------------------*/	
	
}
