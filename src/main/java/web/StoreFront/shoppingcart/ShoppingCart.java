package web.StoreFront.shoppingcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import web.StoreFront.GeneralSF;
import web.StoreFront.checkout.checkoutstep1.CheckOutStep1;
import utilities.commons.UICommonAction;

import java.time.Duration;

public class ShoppingCart extends ShoppingCartElement {
    final static Logger logger = LogManager.getLogger(ShoppingCart.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public ShoppingCart(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);

        PageFactory.initElements(driver, this);
    }

    public CheckOutStep1 clickOnContinue() {
        commonAction.clickElement(CONTINUE_BTN);
        logger.info("Click on Continue button");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return new CheckOutStep1(driver);
    }

}
