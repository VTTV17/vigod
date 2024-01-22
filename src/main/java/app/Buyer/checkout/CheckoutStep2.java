package app.Buyer.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import app.Buyer.buyergeneral.BuyerGeneral;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class CheckoutStep2 {
    final static Logger logger = LogManager.getLogger(CheckoutStep2.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;


    public CheckoutStep2(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By SHIPPING_METHOD = By.xpath("//*[ends-with(@resource-id,'tvShippingPlan')]");
    public CheckoutStep3 tapOnContinueBtn(){
        new BuyerGeneral(driver).tapOnContinueBtn_Checkout();
        return new CheckoutStep3(driver);
    }
}
