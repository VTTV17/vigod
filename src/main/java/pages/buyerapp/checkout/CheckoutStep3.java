package pages.buyerapp.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.buyerapp.buyergeneral.BuyerGeneral;
import utilities.UICommonMobile;

import java.time.Duration;

public class CheckoutStep3 {
    final static Logger logger = LogManager.getLogger(CheckoutStep3.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;


    public CheckoutStep3(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    public CompletedOrder tapOnContinueBtn(){
        new BuyerGeneral(driver).tapOnContinueBtn_Checkout();
        return new CompletedOrder(driver);
    }
}
