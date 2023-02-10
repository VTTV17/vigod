package pages.dashboard.analytics;

import java.time.Duration;
import java.util.List;

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
import utilities.PropertiesUtil;
import utilities.UICommonAction;

public class ReservationAnalytics {

	final static Logger logger = LogManager.getLogger(ReservationAnalytics.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public ReservationAnalytics(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
    
    @FindBy (css = ".analytics-reservations button")
    WebElement DEFAULT_FILTER_DATE_VALUE;
    
    @FindBy (css = "uik-select__label")
    List<WebElement> FILTER_DATES;
    
    public ReservationAnalytics selectDateFilterValue(String date) {
    	commonAction.clickElement(DEFAULT_FILTER_DATE_VALUE);
    	WebElement xp = driver.findElement(By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(date)));
    	commonAction.clickElement(xp);
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
