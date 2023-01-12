package pages.dashboard.marketing.buylink;

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

public class BuyLinkManagement {

	final static Logger logger = LogManager.getLogger(BuyLinkManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public BuyLinkManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

    @FindBy(css = ".buylink-intro .gs-button__green")
    WebElement EXPLORE_NOW_BTN;	
    
    @FindBy(css = ".buylink-header button div")
    WebElement CREATE_BUYLINK_BTN;	
	
    public BuyLinkManagement clickExploreNow() {
    	commonAction.clickElement(EXPLORE_NOW_BTN);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }    
    
    public BuyLinkManagement clickCreateBuyLink() {
    	commonAction.clickElement(CREATE_BUYLINK_BTN);
    	logger.info("Clicked on 'Create Buy Link' button.");
    	return this;
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateBuyLink(String permission) {
		if (permission.contentEquals("A")) {
			clickExploreNow().clickCreateBuyLink();
			boolean flag =  new CreateBuyLink(driver).isProductSelectionDialogDisplayed();
			commonAction.navigateBack();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/       

}
