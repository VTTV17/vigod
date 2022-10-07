package pages.storefront.checkout.ordercomplete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xpath.operations.Or;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2;
import pages.storefront.checkout.checkoutstep3.CheckOutStep3Element;
import utilities.UICommonAction;

import java.time.Duration;

public class OrderComplete {
    final static Logger logger = LogManager.getLogger(CheckOutStep2.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    OrderCompleteElement orderCompleteUI;
    public OrderComplete (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        orderCompleteUI = new OrderCompleteElement(driver);
        PageFactory.initElements(driver, this);
    }
    public void clickOnBackToMarket(){
        commonAction.clickElement(orderCompleteUI.BACK_TO_MARKET_BTN);
        commonAction.sleepInMiliSecond(2000);
        logger.info("Click on Back to market button");
    }
}
