package pages.dashboard.analytics;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class OrderAnalytics {

	final static Logger logger = LogManager.getLogger(OrderAnalytics.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public OrderAnalytics(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = ".time-frame-wrapper [href='#']")
    WebElement REFRESH_LINKTEXT;
    
    @FindBy (xpath = "//span[contains(@class,'spinner-border') and not(@hidden)]")
    WebElement REFRESH_SPINNER;
    
    public OrderAnalytics clickRefresh() {
    	commonAction.clickElement(REFRESH_LINKTEXT);
    	logger.info("Clicked on 'Refresh' link text.");
    	commonAction.waitForElementVisible(REFRESH_SPINNER, 30);
    	return this;
    }
    
}
