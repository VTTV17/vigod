package pages.buyerapp;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class NotificationPermission {

	final static Logger logger = LogManager.getLogger(NotificationPermission.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public NotificationPermission (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }
    
    By ALLOW_BTN = By.xpath("//*[ends-with(@resource-id,'permission_allow_button')]");
    By DENY_BTN = By.xpath("//*[ends-with(@resource-id,'permission_deny_button')]");
    
    public NotificationPermission clickAllowBtn() {
    	commonAction.clickElement(ALLOW_BTN);
    	logger.info("Clicked on 'Allow' button.");
    	return this;
    }
    
    public NotificationPermission clickDenyBtn() {
    	commonAction.clickElement(DENY_BTN);
    	logger.info("Clicked on 'Deny' button.");
    	return this;
    }
    
}
