package pages.dashboard.promotion.discount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.promotion.discount.product_discount_code.ProductDiscountCodePage;
import pages.dashboard.promotion.discount.product_wholesale_campaign.ProductWholesaleCampaignPage;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.Objects;

import static java.lang.Thread.sleep;
import static utilities.page_loaded_text.PageLoadedText.*;

public class DiscountPage extends DiscountElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    public DiscountPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    Logger logger = LogManager.getLogger(DiscountPage.class);

    /**
     * navigate to discount page
     */
    public DiscountPage navigate() throws InterruptedException {
        // on home page:
        // 1: hide facebook bubble
        // 2: navigate to Promotion>>Discount page
        new HomePage(driver).hideFacebookBubble()
                .navigateToPromotion_DiscountPage();

        // log page title for debug
        logger.info("Current page is %s".formatted(driver.getTitle()));

        // wait page loaded
        commonAction.verifyPageLoaded(DB_DISCOUNT_PAGE_LOADED_TEXT_VIE, DB_DISCOUNT_PAGE_LOADED_TEXT_ENG);

        return this;
    }

    /**
     * navigate to create product wholesale campaign page
     */
    public ProductWholesaleCampaignPage openCreateProductWholesaleCampaignPage() {
        // on Discount page:
        // 1: Click on wholesale pricing button
        // 2: Then click product wholesale pricing button
        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICING_BTN)).click();
        logger.info("Click on the Wholesale pricing button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WHOLESALE_PRICING)).click();
        logger.info("Navigate to create a new product wholesale campaign");
        return new ProductWholesaleCampaignPage(driver);
    }

    public ProductDiscountCodePage openCreateProductDiscountCodePage() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PROMOTION_BTN)).click();
        logger.info("Click on the Create promotion button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DISCOUNT_CODE)).click();
        return new ProductDiscountCodePage(driver);
    }


}
