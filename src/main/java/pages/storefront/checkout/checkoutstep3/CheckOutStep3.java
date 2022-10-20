package pages.storefront.checkout.checkoutstep3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2Element;
import pages.storefront.checkout.ordercomplete.OrderComplete;
import utilities.UICommonAction;

import java.time.Duration;

public class CheckOutStep3 extends GeneralSF {
    final static Logger logger = LogManager.getLogger(CheckOutStep3.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    CheckOutStep3Element checkOutStep3UI;
    public CheckOutStep3 (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        checkOutStep3UI = new CheckOutStep3Element(driver);
        PageFactory.initElements(driver, this);
    }
    public OrderComplete clickOnNextButton(){
        commonAction.clickElement(checkOutStep3UI.NEXT_BUTTON);
        logger.info("Click on Next button.");
        commonAction.sleepInMiliSecond(1000);
        waitTillLoaderDisappear();
        return new OrderComplete(driver);
    }
}
