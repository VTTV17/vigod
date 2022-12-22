package pages.dashboard.marketing.landingpage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1Element;
import utilities.UICommonAction;

import java.time.Duration;

public class LandingPage {
    final static Logger logger = LogManager.getLogger(CheckOutStep1.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    LandingPageElement landingPageUI;

    public LandingPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        landingPageUI = new LandingPageElement(driver);
        PageFactory.initElements(driver, this);
    }
    public boolean isPermissionModalDisplay(){
        return commonAction.isElementDisplay(landingPageUI.PERMISSION_MODAL);
    }
    public LandingPage closeModal(){
        commonAction.clickElement(landingPageUI.CLOSE_MODAL_BTN);
        return this;
    }
}
