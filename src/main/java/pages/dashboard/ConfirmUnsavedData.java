package pages.dashboard;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ConfirmUnsavedData {

	final static Logger logger = LogManager.getLogger(ConfirmUnsavedData.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public ConfirmUnsavedData(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = ".modal-footer button:nth-of-type(1)")
    WebElement CANCEL_BTN;
    
    @FindBy (css = ".modal-footer button:nth-of-type(2)")
    WebElement OK_BTN;
    
    public ConfirmUnsavedData clickCancelBtn() {
    	commonAction.clickElement(CANCEL_BTN);
    	logger.info("Clicked on 'Cancel' button to confirm unsaved data is lost.");
    	return this;
    }
    
    public ConfirmUnsavedData clickOKBtn() {
    	commonAction.clickElement(OK_BTN);
    	logger.info("Clicked on 'OK' button to confirm unsaved data is lost.");
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }
    
    
    
}
