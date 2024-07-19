package app.Buyer.shopcart;

import app.Buyer.checkout.CheckoutOneStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import app.Buyer.buyergeneral.BuyerGeneral;
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
    public CheckoutOneStep tapOnContinueBtn(){
        commonAction.clickElement(CONTINUE_BTN);
        logger.info("Tap on Continue button.");
        waitLoadingDisapear();
        return new CheckoutOneStep(driver);
    }
    public BuyerShopCartPage waitLoadingDisapear(){
        logger.info("Start Wait loading...");
        new BuyerGeneral(driver).waitLoadingDisapear();
        return this;
    }
}
