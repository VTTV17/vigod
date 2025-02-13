package web.Dashboard.analytics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class ReservationAnalytics {

	final static Logger logger = LogManager.getLogger(ReservationAnalytics.class);
	
    WebDriver driver;
    UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	String url = Links.DOMAIN + "/analytics/reservation";
    public ReservationAnalytics(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
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
    /*---------------Staff Permission----------------------*/
    public ReservationAnalytics checkReservationAnalyticsPermission(AllPermissions allPermissions){
		this.allPermissions = allPermissions;
		commonAction.sleepInMiliSecond(2000,"Wait login loaded");
		if(allPermissions.getAnalytics().getReservationAnalytics().isViewReservationAnalytics()){
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(url,url),
					"[Failed] Reservation page should be show when navigate to url: "+url);
		}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
				"[Failed] Restricted page should be shown when navigate to url: "+url);
		AssertCustomize.verifyTest();
		return this;
	}
}
