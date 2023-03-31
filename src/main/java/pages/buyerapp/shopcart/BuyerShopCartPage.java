package pages.buyerapp.shopcart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.buyerapp.account.BuyerAccountPage;
import utilities.UICommonAction;

import java.time.Duration;

public class BuyerShopCartPage {
    final static Logger logger = LogManager.getLogger(BuyerShopCartPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;


    public BuyerShopCartPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
}
