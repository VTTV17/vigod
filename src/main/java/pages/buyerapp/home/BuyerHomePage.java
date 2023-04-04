package pages.buyerapp.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;

public class BuyerHomePage extends BuyerHomeElement {
    final static Logger logger = LogManager.getLogger(BuyerHomePage.class);

    WebDriver driver;
    WebDriverWait wait;

    public BuyerHomePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchProductByName() {

        try {
            wait.until(ExpectedConditions.elementToBeClickable(HEADER_SEARCH_ICON)).click();
        } catch (TimeoutException ex) {
            logger.info(ex);
            try {
                wait.until(ExpectedConditions.elementToBeClickable(HEADER_SEARCH_ICON)).click();
            } catch (TimeoutException ex1) {
                logger.info(ex1);
                wait.until(ExpectedConditions.elementToBeClickable(HEADER_SEARCH_ICON)).click();
            }
        }

    }
}
