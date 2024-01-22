package web.Dashboard.marketing.loyaltypoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class LoyaltyPoint {
	
	final static Logger logger = LogManager.getLogger(LoyaltyPoint.class);

    WebDriver driver;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public LoyaltyPoint (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnSave = By.cssSelector(".loyalty-point-setting .gs-button__green");
    By loc_btnActivateNow = By.cssSelector(".loyalty-point-intro__left-col__activate");
    
    public LoyaltyPoint clickSave() {
    	commonAction.click(loc_btnSave);
    	logger.info("Clicked on 'Save' button.");
        return this;
    }
    
    public LoyaltyPoint clickActivateNow() {
    	commonAction.sleepInMiliSecond(1000);
    	if (commonAction.getElements(loc_btnActivateNow).size() == 0) return this;
    	
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnActivateNow))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnActivateNow));
			return this;
		}
    	commonAction.clickElement(driver.findElement(loc_btnActivateNow));
    	logger.info("Clicked on 'Activate Now' button.");
    	return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToConfigureLoyaltyPoint(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickActivateNow();
			clickSave();
			new HomePage(driver).getToastMessage();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/   
    
}
