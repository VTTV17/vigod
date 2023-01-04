package pages.dashboard.saleschannels.shopee;

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

public class Settings {

	final static Logger logger = LogManager.getLogger(Settings.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Settings(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".shopeeSettings button.gs-button__green")
    WebElement SAVE_BTN;	

    
    public Settings clickSave() {
    	commonAction.clickElement(SAVE_BTN);
    	logger.info("Clicked on 'Save' button.");
    	return this;
    }      
}
