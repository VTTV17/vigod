package pages.dashboard.gochat;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Facebook {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(Facebook.class);

	public Facebook(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".btn-connect")
	WebElement CONNECT_FB_BTN;

	public Facebook clickConnectFacebook() {
		commons.clickElement(CONNECT_FB_BTN);
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
