package pages.buyerapp.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import pages.dashboard.analytics.OrderAnalytics;
import utilities.UICommonAction;

import java.time.Duration;

public class BuyerAccountPage {
    final static Logger logger = LogManager.getLogger(BuyerAccountPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;


    public BuyerAccountPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
}
