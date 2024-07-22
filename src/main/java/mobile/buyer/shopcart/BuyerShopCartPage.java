package mobile.buyer.shopcart;

import app.Buyer.checkout.CheckoutStep1;
import mobile.buyer.buyergeneral.BuyerGeneral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class BuyerShopCartPage extends BuyerShopCartElement {
    final static Logger logger = LogManager.getLogger(BuyerShopCartPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;


    public BuyerShopCartPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }
    public CheckoutStep1 tapOnContinueBtn(){
        commonAction.clickElement(CONTINUE_BTN);
        logger.info("Tap on Continue button.");
        commonAction.sleepInMiliSecond(2000);
        return new CheckoutStep1(driver);
    }
    public BuyerShopCartPage waitLoadingDisapear(){
        logger.info("Wait loading...");
        new BuyerGeneral(driver).waitLoadingDisappear();
        return this;
    }
}
