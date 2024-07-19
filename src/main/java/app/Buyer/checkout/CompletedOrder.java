package app.Buyer.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import app.Buyer.home.BuyerHomePage;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class CompletedOrder {
    final static Logger logger = LogManager.getLogger(CompletedOrder.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;


    public CompletedOrder(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By CONTINUE_SHOPPING = By.xpath("//*[ends-with(@resource-id,'tvContinueShopping')]");
    public BuyerHomePage tapOnContinueShopping(){
        common.clickElement(CONTINUE_SHOPPING);
        logger.info("Tap on Continue Shopping button");
        return new BuyerHomePage(driver);
    }
}
