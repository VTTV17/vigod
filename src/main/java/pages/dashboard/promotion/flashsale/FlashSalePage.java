package pages.dashboard.promotion.flashsale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.promotion.flashsale.time.TimeManagementPage;

import java.time.Duration;

public class FlashSalePage extends TimeManagementPage {
    WebDriverWait wait;

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
}
