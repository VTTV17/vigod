package pages.dashboard.promotion.discount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.customers.segments.Segments;
import pages.dashboard.home.HomePage;
import pages.dashboard.promotion.discount.product_discount_code.ProductDiscountCodePage;
import pages.dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignPage;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static pages.dashboard.customers.allcustomers.create_customer.CreateCustomerPopup.customerTags;
import static pages.dashboard.customers.segments.CreateSegment.segmentName;
import static utilities.links.Links.DOMAIN;
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
     * navigate to create product discount campaign page
     */
    public ProductDiscountCampaignPage openCreateProductDiscountCampaignPage() {
        // on Discount page:
        // 1: Click on wholesale pricing button
        // 2: Then click product wholesale pricing button
        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICING_BTN)).click();
        logger.info("Click on the Wholesale pricing button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WHOLESALE_PRICING)).click();
        logger.info("Navigate to create a new product discount campaign");
        return new ProductDiscountCampaignPage(driver);
    }

    public ProductDiscountCodePage openCreateProductDiscountCodePage() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PROMOTION_BTN)).click();
        logger.info("Click on the Create promotion button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DISCOUNT_CODE)).click();
        return new ProductDiscountCodePage(driver);
    }

    /**
     * create new segment with new customer, used to specific segment condition
     */
    public List<String> generateSegmentForTest() {
        // open new dashboard in new tab
        ((JavascriptExecutor) driver).executeScript("window.open('%s');".formatted(DOMAIN));

        // get list tabs
        var tabs = new ArrayList<>(driver.getWindowHandles());

        // switch to have just opened windows
        driver.switchTo().window(tabs.get(tabs.size() - 1));

        // wait Home page loaded and hide facebook bubble
        new HomePage(driver).verifyPageLoaded()
                .hideFacebookBubble();

        // in all customers page
        // create new customer
        new AllCustomers(driver).navigate()
                .clickCreateNewCustomerBtn()
                .inputCustomerName()
                .inputCustomerPhone()
                .inputCustomerTags()
                .clickAddBtn();

        // Create customer segment with customer tag condition
        new Segments(driver).navigate()
                .clickCreateSegmentBtn()
                .inputSegmentName()
                .selectDataGroupCondition("Customers data")
                .selectDataCondition("Customer tag")
                .selectComparisonOperatorCondition("is equal to")
                .inputComparedValueCondition(customerTags[0])
                .clickSaveBtn();

        // close window after create segment
        ((JavascriptExecutor) driver).executeScript("window.close();");

        // back to create discount/campaign page
        driver.switchTo().window(tabs.get(0));

        // return segment have just created
        return List.of(segmentName);
    }

}
