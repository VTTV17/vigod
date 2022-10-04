package pages.dashboard.promotion.discount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;

import java.time.Duration;

public class DiscountPage extends DiscountElement {
    WebDriverWait wait;

    String pageLoadedTextENG = "Promotion";
    String pageLoadedTextVIE = "Ưu Đãi";

    public DiscountPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(DiscountPage.class);

    public DiscountPage navigate() throws InterruptedException {
        new HomePage(driver).hideFacebookBubble().navigateToPromotion_DiscountPage();
        logger.info("Current page is %s".formatted(driver.getTitle()));
        return this;
    }

    public DiscountPage openCreateProductWholesaleCampaignPage() {
        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICING_BTN)).click();
        logger.info("Click on the Wholesale pricing button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WHOLESALE_PRICING)).click();
        logger.info("Navigate to create a new product wholesale campaign");
        return this;
    }

    public DiscountPage verifyPageLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains(pageLoadedTextVIE) || driver.getPageSource().contains(pageLoadedTextENG);
        });
        return this;
    }

}
