package web.Dashboard.analytics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class ReservationAnalytics {

	final static Logger logger = LogManager.getLogger(ReservationAnalytics.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public ReservationAnalytics(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    
    By loc_ddlFilterDate = By.cssSelector(".analytics-reservations button");
    
    public ReservationAnalytics selectDateFilterValue(String date) {
    	commonAction.click(loc_ddlFilterDate);
    	commonAction.click(By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(date)));
    	return this;
    }
    
    /*Verify permission for certain feature*/
    public void verifyPermissionToUseAnalytics(String permission) {
		String displayLanguage = new HomePage(driver).getDashboardLanguage();
		String data = null;
    	try {
    		data = PropertiesUtil.getPropertiesValueByDBLang("analytics.reservations.filter.yesterday", displayLanguage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (permission.contentEquals("A")) {
			selectDateFilterValue(data);
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/  
    
}
