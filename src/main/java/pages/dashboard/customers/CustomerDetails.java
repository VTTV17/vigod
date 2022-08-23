package pages.dashboard.customers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.UICommonAction;

import java.time.Duration;

public class CustomerDetails {
	
	final static Logger logger = LogManager.getLogger(CustomerDetails.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public CustomerDetails (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (id = "email")
    WebElement EMAIL;
    
    @FindBy (id = "phone")
    WebElement PHONE;
    
    public String getEmail() {
    	commonAction.sleepInMiliSecond(1000);
    	String value = commonAction.getElementAttribute(EMAIL, "value");
    	logger.info("Retrieved Email: " + value);
        return value;
    }
    
    public String getPhoneNumber() {
    	commonAction.sleepInMiliSecond(1000);
    	String value = commonAction.getText(PHONE);
    	logger.info("Retrieved Phone Number: " + value);
    	return value;
    }
    
//    public void verifyLoginWithDeletedStaffAccount(String content) {
//        wait.until(ExpectedConditions.visibilityOf(WARNING_POPUP));
//        Assert.assertTrue(WARNING_POPUP.getText().contains(content),
//                "[Login][Deleted Staff Account] No warning popup has been shown");
//    }

    public void completeVerify() {
        soft.assertAll();
    }    
    
}
