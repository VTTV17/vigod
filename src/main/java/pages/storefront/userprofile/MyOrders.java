package pages.storefront.userprofile;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.storefront.header.HeaderSF;
import utilities.UICommonAction;

public class MyOrders extends HeaderSF {
	
	final static Logger logger = LogManager.getLogger(MyOrders.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    SoftAssert soft = new SoftAssert();
    
    public MyOrders (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
	
	@FindBy(css = ".order-item__review")
	List<WebElement> REVIEW_LINKTEXT;    
    
    public MyOrders clickWriteReview(){
        commonAction.clickElement(REVIEW_LINKTEXT.get(0));
        logger.info("Click on Write Review.");
        return this;
    }
    
    public boolean isWriteReviewDisplayed(){
    	commonAction.sleepInMiliSecond(1000);
    	boolean isDisplayed = (REVIEW_LINKTEXT.size() > 0) ? true : false;
    	logger.info("Is Write Review displayed: " + isDisplayed);
    	return isDisplayed;
    }
}
