package pages.dashboard.marketing.loyaltypoint;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class LoyaltyPoint {
	
	final static Logger logger = LogManager.getLogger(LoyaltyPoint.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public LoyaltyPoint (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = ".loyalty-point-setting .gs-button__green")
    WebElement SAVE_BTN;
    
    By ACTIVATE_NOW_BTN = By.cssSelector(".loyalty-point-intro__left-col__activate");
    
    public LoyaltyPoint clickSave() {
    	commonAction.clickElement(SAVE_BTN);
    	logger.info("Clicked on 'Save' button.");
        return this;
    }
    
    public LoyaltyPoint clickActivateNow() {
    	commonAction.sleepInMiliSecond(1000);
    	if (commonAction.isElementNotDisplay(driver.findElements(ACTIVATE_NOW_BTN))) {
    		return this;
    	}
		if (commonAction.isElementVisiblyDisabled(driver.findElement(ACTIVATE_NOW_BTN))) {
			new HomePage(driver).isMenuClicked(driver.findElement(ACTIVATE_NOW_BTN));
			return this;
		}
    	commonAction.clickElement(driver.findElement(ACTIVATE_NOW_BTN));
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
