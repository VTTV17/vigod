package pages.dashboard.marketing.loyaltypoint;

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
    
    public LoyaltyPoint clickSave() {
    	commonAction.clickElement(SAVE_BTN);
    	logger.info("Clicked on 'Save' button.");
        return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToConfigureLoyaltyPoint(String permission) {
		if (permission.contentEquals("A")) {
			clickSave();
			new HomePage(driver).getToastMessage();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/   
    
}
