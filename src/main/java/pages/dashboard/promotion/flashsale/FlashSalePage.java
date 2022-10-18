package pages.dashboard.promotion.flashsale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.promotion.flashsale.time.TimeManagementPage;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class FlashSalePage extends FlashSaleElement {
    WebDriverWait wait;

    public static String flashSaleURL;

    public FlashSalePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(FlashSalePage.class);

    /**
     * Open Flash Sale page
     */
    public FlashSalePage openFlashSaleCampaignManagementPage() throws InterruptedException {
        // On home page:
        // 1. Hide facebook bubble
        // 2. Navigate to Promotion/Flash Sale
        new HomePage(driver)
                .hideFacebookBubble()
                .navigateToPromotion_FlashSalePage();

        // log
        logger.info("Current page is %s".formatted(driver.getTitle()));

        return this;
    }

    public TimeManagementPage navigateToFlashSaleTimeManagementPage() throws InterruptedException {
        // wait flash sale intro page loaded, if any
        sleep(1000);

        // in case, flash sale intro page is shown, click on Explore Now button to skip
        if (driver.getCurrentUrl().contains("intro")) {
            // click Explore Now
            wait.until(ExpectedConditions.visibilityOf(EXPLORE_NOW_BTN)).click();
            logger.info("Skip Flash sale intro");
        }

        // get flashSaleURL
        flashSaleURL = driver.getCurrentUrl();

        // navigate to manage flash sale time page
        wait.until(ExpectedConditions.elementToBeClickable(MANAGE_FLASH_SALE_TIME_BTN)).click();
        logger.info("Navigate to manage flash sale time page");

        return new TimeManagementPage(driver);
    }
}
