package pages.sellerapp;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonMobile;

public class HomePage {

	final static Logger logger = LogManager.getLogger(HomePage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    SoftAssert soft = new SoftAssert();

    public HomePage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    By ACCOUNT_TAB = By.id("com.mediastep.GoSellForSeller.STG:id/bottom_navigation_tab_account");
    By LOGOUT_BTN = By.id("com.mediastep.GoSellForSeller.STG:id/llLogout");
    By LOGOUT_OK_BTN = By.id("com.mediastep.GoSellForSeller.STG:id/tvRightButton");
    By LOGOUT_ABORT_BTN = By.id("com.mediastep.GoSellForSeller.STG:id/tvLeftButton");
    
    public boolean isAccountTabDisplayed() {
    	boolean isDisplayed = commonAction.getElement(ACCOUNT_TAB, 5).isDisplayed();
    	logger.info("Is Account Tab displayed: " + isDisplayed);
    	return isDisplayed;
    }
    
    public HomePage clickAccountTab() {
    	commonAction.getElement(ACCOUNT_TAB, 5).click();
    	logger.info("Click on Account tab");
    	return this;
    }
    
    public HomePage clickLogoutBtn() {
    	commonAction.getElement(LOGOUT_BTN, 5).click();
    	logger.info("Click on Logout button");
    	return this;
    }
    
    public HomePage clickLogoutOKBtn() {
    	commonAction.getElement(LOGOUT_OK_BTN, 5).click();
    	logger.info("Click on OK button to confirm logout");
    	return this;
    }
    
    public HomePage clickLogoutAbortBtn() {
    	commonAction.getElement(LOGOUT_ABORT_BTN, 5).click();
    	logger.info("Click on Cancel button to abort logout");
    	return this;
    }

}
