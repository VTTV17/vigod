package pages.sellerapp;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class HomePage {

	final static Logger logger = LogManager.getLogger(HomePage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();

    public HomePage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    By ACCOUNT_TAB = By.id("com.mediastep.GoSellForSeller.STG:id/bottom_navigation_tab_account");
    
    public boolean isAccountTabDisplayed() {
    	boolean isDisplayed = commonAction.getElement(ACCOUNT_TAB).isDisplayed();
    	logger.info("Is Account Tab displayed: " + isDisplayed);
    	return isDisplayed;
    }

}
