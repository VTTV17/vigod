package pages.dashboard.onlineshop.preferences;

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

public class Configuration {

	final static Logger logger = LogManager.getLogger(Configuration.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Configuration(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (xpath = "(//div[contains(@class,'live-chat-configuration')]//section)[1]//h3[contains(.,'Facebook')]/following-sibling::*")
    WebElement ENABLE_FACEBOOK_MESSENGER_TOGGLE;
    
    @FindBy (xpath = "(//div[contains(@class,'live-chat-configuration')]//section)[2]//h3[contains(.,'Zalo')]/following-sibling::*")
    WebElement ENABLE_ZALO_OA_MESSENGER_TOGGLE;
    
    @FindBy (xpath = "(//div[contains(@class,'live-chat-configuration')]//section)[3]//h3[contains(.,'login')]/following-sibling::*")
    WebElement ENABLE_FACEBOOK_LOGIN_FOR_ONLINE_STORE_TOGGLE;
    
    @FindBy (css = ".btn-next")
    WebElement COMPLETE_BTN;
    
    public Configuration clickEnableFacebookMessengerToggle() {
    	commonAction.clickElement(ENABLE_FACEBOOK_MESSENGER_TOGGLE);
    	logger.info("Clicked on 'Enable Facebook Messenger' toggle button.");
    	return this;
    }
    
    public Configuration clickEnableZaloOAMessengerToggle() {
    	commonAction.clickElement(ENABLE_ZALO_OA_MESSENGER_TOGGLE);
    	logger.info("Clicked on 'Zalo OA Messenger' toggle button.");
    	return this;
    }
    
    public Configuration clickEnableFacebookLoginForOnlineStoreToggle() {
    	commonAction.clickElement(ENABLE_FACEBOOK_LOGIN_FOR_ONLINE_STORE_TOGGLE);
    	logger.info("Clicked on 'Zalo OA Messenger' toggle button.");
    	return this;
    }
    
}
