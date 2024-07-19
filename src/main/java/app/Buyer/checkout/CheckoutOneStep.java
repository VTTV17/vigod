package app.Buyer.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class CheckoutOneStep extends CheckoutOneStepElement{
    final static Logger logger = LogManager.getLogger(CheckoutStep1.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;


    public CheckoutOneStep(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }

}
