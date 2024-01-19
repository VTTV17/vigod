package pages.dashboard.analytics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class OrderAnalytics {

	final static Logger logger = LogManager.getLogger(OrderAnalytics.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public OrderAnalytics(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_lnkRefresh = By.cssSelector(".time-frame-wrapper [href='#']");
    By loc_spnRefreshSpinner = By.xpath("//span[contains(@class,'spinner-border') and not(@hidden)]");
    
    public OrderAnalytics clickRefresh() {
    	commonAction.click(loc_lnkRefresh);
    	logger.info("Clicked on 'Refresh' link text.");
    	commonAction.waitVisibilityOfElementLocated(loc_spnRefreshSpinner);
    	return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToUseOrderAnalytics(String permission) {
		if (permission.contentEquals("A")) {
			clickRefresh();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/       
    
}
