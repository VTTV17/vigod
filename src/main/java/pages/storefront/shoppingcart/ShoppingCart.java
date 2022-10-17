package pages.storefront.shoppingcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import utilities.UICommonAction;

import javax.swing.text.GapContent;
import java.time.Duration;

public class ShoppingCart extends GeneralSF {
    final static Logger logger = LogManager.getLogger(ShoppingCart.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    ShoppingCartElement shoppingCartUI;
    public ShoppingCart (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        shoppingCartUI = new ShoppingCartElement(driver);
        PageFactory.initElements(driver, this);
    }
    public CheckOutStep1 clickOnContinue(){
        commonAction.clickElement(shoppingCartUI.CONTINUE_BTN);
        logger.info("Click on Continue button");
        commonAction.sleepInMiliSecond(2000);
        waitTillLoaderDisappear();
        commonAction.sleepInMiliSecond(2000);
        return new CheckOutStep1(driver);
    }
}
