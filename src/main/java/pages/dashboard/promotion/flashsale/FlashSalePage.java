package pages.dashboard.promotion.flashsale;

import static java.lang.Thread.sleep;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import pages.dashboard.promotion.flashsale.campaign.FlashSaleCampaignPage;
import pages.dashboard.promotion.flashsale.time.TimeManagementPage;
import utilities.UICommonAction;

public class FlashSalePage extends FlashSaleElement {
    WebDriverWait wait;
    UICommonAction commonAction;
    
    public static String flashSaleURL;

    public FlashSalePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    Logger logger = LogManager.getLogger(FlashSalePage.class);

    /**
     * Open Flash Sale page
     */
    public FlashSalePage openFlashSaleCampaignManagementPage() throws InterruptedException {
        // On home page:
        // 1. Hide facebook bubble
        // 2. Navigate to Promotion/Flash Sale
        new HomePage(driver)
                .hideFacebookBubble()
                .navigateToPromotion_FlashSalePage();

        // log
        logger.info("Current page is %s".formatted(driver.getTitle()));

        return this;
    }

    public TimeManagementPage navigateToFlashSaleTimeManagementPage() throws InterruptedException {
        // wait flash sale intro page loaded, if any
        sleep(1000);

        // in case, flash sale intro page is shown, click on Explore Now button to skip
        if (driver.getCurrentUrl().contains("intro")) {
            // click Explore Now
            wait.until(ExpectedConditions.visibilityOf(EXPLORE_NOW_BTN)).click();
            logger.info("Skip Flash sale intro");
        }

        // get flashSaleURL
        flashSaleURL = driver.getCurrentUrl();

        // navigate to manage flash sale time page
        wait.until(ExpectedConditions.elementToBeClickable(MANAGE_FLASH_SALE_TIME_BTN)).click();
        logger.info("Navigate to manage flash sale time page");

        return new TimeManagementPage(driver);
    }
    
    public FlashSalePage clickCreateCampaign() {
    	commonAction.clickElement(CREATE_CAMPAIGN_BTN);
    	logger.info("Clicked on 'Create Campaign' button.");
    	return this;
    } 
    
    public FlashSalePage clickExploreNow() {
    	commonAction.clickElement(EXPLORE_NOW_BTN);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }     
   
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateFlashSale(String permission) {
		if (permission.contentEquals("A")) {
			clickExploreNow().clickCreateCampaign();
			new FlashSaleCampaignPage(driver).inputCampaignName("Test Permission");
			commonAction.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/        
    
}
