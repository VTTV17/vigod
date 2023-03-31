package pages.buyerapp.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;

public class BuyerSearchDetailPage {
    final static Logger logger = LogManager.getLogger(BuyerSearchDetailPage.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public BuyerSearchDetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
}
