package pages.dashboard.promotion.discount.product_wholesale_campaign;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Thread.sleep;
import static utilities.character_limit.CharacterLimit.*;

public class ProductWholesaleCampaignPage extends ProductWholesaleCampaignElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    public static String wholesaleCampaignName;
    public static boolean isNoExpiry;
    public static int startIn;
    public static int endIn;

    public static int discountType;

    public static int discountValue;

    public static int segmentType;
    public static List<String> segmentList;


    public ProductWholesaleCampaignPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    Logger logger = LogManager.getLogger(ProductWholesaleCampaignPage.class);

    /**
     * input wholesale campaign name
     */
    public ProductWholesaleCampaignPage inputCampaignName(String... campaignName) {
        // get wholesale campaign name for another test
        wholesaleCampaignName = campaignName.length == 0
                ? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_NAME) + 1)
                : campaignName[0];

        // input wholesale campaign name
        wait.until(ExpectedConditions.elementToBeClickable(CAMPAIGN_NAME)).sendKeys(wholesaleCampaignName);

        // log
        logger.info("Input campaign name: %s".formatted(wholesaleCampaignName));

        return this;
    }


    /**
     * get date coordinates
     */
    private void selectDate(int numberOfNextDay) {
        // get will be selected date
        String date = DateTimeFormatter.ofPattern("dd").format(LocalDate.now().plusDays(numberOfNextDay));
        String monthAndYear = DateTimeFormatter.ofPattern("MMM yyyy").format(LocalDate.now().plusDays(numberOfNextDay));

        // get current month in calendar
        String currentMonth = wait.until(ExpectedConditions.visibilityOf(CURRENT_MONTH)).getText();

        // go to selected month and year
        while (!currentMonth.equals(monthAndYear)) {
            // go to next month
            wait.until(ExpectedConditions.elementToBeClickable(NEXT_BTN)).click();

            // get current month
            currentMonth = wait.until(ExpectedConditions.visibilityOf(CURRENT_MONTH)).getText();
        }

        // wait list available date in current month visible
        commonAction.waitElementList(AVAILABLE_DATE);

        // find and select date
        for (WebElement element : AVAILABLE_DATE) {

            // if date < 10 => add "0"
            String dateInCalendar = element.getText().length() == 1 ? "0" + element.getText() : element.getText();

            // check and select date
            if (dateInCalendar.equals(date)) {
                element.click();
                logger.info("Select date");
                break;
            }
        }


    }

    /**
     * set campaign time
     */
    @SafeVarargs
    public final ProductWholesaleCampaignPage setPromotionDate(List<Serializable>... timeSetting) {
        // get time setting:
        List<Serializable> setting = timeSetting.length == 0
                ? List.of(RandomUtils.nextBoolean())
                : timeSetting[0];

        // isNoExpiry: Wholesale campaign has expiry date or not
        isNoExpiry = (setting.size() > 0) ? (boolean) setting.get(0) : RandomUtils.nextBoolean();

        // startIn: campaign will be started in startIn days
        startIn = (setting.size() > 1) ? (int) setting.get(1) : RandomUtils.nextInt(MAX_PROMOTION_DATE);

        // endIn: campaign will be ended in endIn day
        endIn = (setting.size() > 2) ? (int) setting.get(2) : (RandomUtils.nextInt(MAX_PROMOTION_DATE - startIn) + startIn);

        // No Expiry date checkbox has been checked
        if (isNoExpiry) {
            // check on No Expiry date checkbox
            wait.until(ExpectedConditions.elementToBeClickable(NO_EXPIRY_DATE_CHECKBOX)).click();
            logger.info("No expiry date - checked");

            // open calendar
            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            logger.info("Open calendar");

            // select start date
            selectDate(startIn);
            logger.info("Active date: %s".formatted(LocalDate.now().plusDays(startIn)));
        } else {
            // log
            logger.info("No expiry date - no checked");

            // open calendar
            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            logger.info("Open calendar");

            // select start date
            selectDate(startIn);

            // select end date
            selectDate(endIn);

            // complete select start - end date
            wait.until(ExpectedConditions.elementToBeClickable(APPLY_BTN)).click();
            logger.info("Active date: %s - %s".formatted(LocalDate.now().plusDays(startIn), LocalDate.now().plusDays(endIn)));
        }
        return this;
    }

    public ProductWholesaleCampaignPage setDiscountTypeAndValue(int... typeOfDiscount) {
        // get discount type
        // 0: Percentage
        // 1: Fixed amount
        discountType = typeOfDiscount.length > 0 ? typeOfDiscount[0] : RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_DISCOUNT_TYPE);

        // get discount value
        // max percentage value: 100
        // max fixed amount value: 1,000,000,000
        discountValue = typeOfDiscount.length > 1 ? typeOfDiscount[1]
                : (int) (discountType == 0 ? RandomUtils.nextInt(MAX_PERCENT_DISCOUNT) : Math.random() * MAX_FIXED_AMOUNT);

        // wait discount type element visible
        commonAction.waitElementList(TYPE_OF_DISCOUNT_LABEL);

        // select discount type
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", TYPE_OF_DISCOUNT_LABEL.get(discountType));

        // clear default discount
        wait.until(ExpectedConditions.elementToBeClickable(DISCOUNT_VALUE)).click();
        DISCOUNT_VALUE.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);

        // input new discount value
        if (discountType == 0) {
            // log
            logger.info("Discount type: Percentage");

            // validate discount value
            discountValue = Math.min(discountValue, MAX_PERCENT_DISCOUNT);

            // input discount value
            DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
            logger.info("Percentage: %s".formatted(discountValue));

        } else {
            // log
            logger.info("Discount type: Fixed amount");

            // validate discount value
            discountValue = Math.min(discountValue, MAX_FIXED_AMOUNT);

            // input discount value
            DISCOUNT_VALUE.sendKeys(String.valueOf(discountValue));
            logger.info("Fixed amount: %s".formatted(discountValue));
        }

        // click around
        PAGE_TITLE.click();

        return this;
    }

    @SafeVarargs
    public final ProductWholesaleCampaignPage setCustomerSegment(List<Object>... segmentCondition) throws InterruptedException {
        List<Object> segCondition = segmentCondition.length == 0 ? List.of(RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE)) : segmentCondition[0];
        segmentType = (segCondition.size() > 0) ? (int) segCondition.get(0) : RandomUtils.nextInt(MAX_PRODUCT_WHOLESALE_CAMPAIGN_SEGMENT_TYPE);

        segmentList = segCondition.size() > 1 ? (List<String>) segCondition.get(1) : new ArrayList<>();
        commonAction.waitElementList(CUSTOMER_SEGMENT_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", CUSTOMER_SEGMENT_LABEL.get(segmentType));

        if (segmentType == 0) {
            logger.info("Customer segment: All customers");
        } else {
            logger.info("Customer segment: %s".formatted(segmentList.toArray()));

            wait.until(ExpectedConditions.elementToBeClickable(ADD_SEGMENT_BTN)).click();
            logger.info("Open add segment popup");

            for (String segment : segmentList) {
                wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(segment);
                sleep(2000);
                wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                logger.info("Search and select segment with keyword: %s".formatted(segment));
            }

            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            logger.info("Close add segment popup");
        }
        return this;
    }

    public ProductWholesaleCampaignPage setAppliesProduct(int appliesProductTypeID, String... productCollectionsOrName) throws InterruptedException {
        commonAction.waitElementList(APPLIES_TO_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", APPLIES_TO_LABEL.get(appliesProductTypeID));

        switch (appliesProductTypeID) {
            case 1 -> {
                logger.info("Applies to: Specific product collections");

                wait.until(ExpectedConditions.elementToBeClickable(ADD_COLLECTION_OR_PRODUCT_BTN)).click();
                logger.info("Open add product collection popup");

                for (String collection : productCollectionsOrName) {
                    wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(collection);
                    sleep(2000);
                    wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                    logger.info("Search and select product collection with keyword: %s".formatted(collection));
                }

                wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
                logger.info("Close add product collection popup");


            }
            case 2 -> {
                logger.info("Applies to: Specific products");

                wait.until(ExpectedConditions.elementToBeClickable(ADD_COLLECTION_OR_PRODUCT_BTN)).click();
                logger.info("Open add product popup");

                for (String collection : productCollectionsOrName) {
                    wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(collection);
                    sleep(2000);
                    wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                    logger.info("Search and select product with keyword: %s".formatted(collection));
                }

                wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
                logger.info("Close add product popup");
            }
            default -> logger.info("Applies to: All products");
        }
        return this;
    }

    public ProductWholesaleCampaignPage setMinimumQuantityOfProducts(int quantity) {
        MINIMUM_REQUIREMENTS.click();
        MINIMUM_REQUIREMENTS.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        MINIMUM_REQUIREMENTS.sendKeys(String.valueOf(quantity));
        logger.info("Set minimum quantity of products: %s".formatted(quantity));
        PAGE_TITLE.click();
        return this;
    }

    public ProductWholesaleCampaignPage setBranch(int branchTypeID, String... branchList) throws InterruptedException {
        commonAction.waitElementList(APPLICABLE_BRANCH_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", APPLICABLE_BRANCH_LABEL.get(branchTypeID));
        if (branchTypeID == 0) {
            logger.info("Applicable branch: All branches");
        } else {
            logger.info("Applicable branch: %s".formatted((Object) branchList));

            wait.until(ExpectedConditions.elementToBeClickable(SELECT_BRANCH_BTN)).click();
            logger.info("Open add branch popup");

            for (String branch : branchList) {
                wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(branch);
                sleep(2000);
                wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL)).click();
                logger.info("Search and select product with keyword: %s".formatted(branch));
            }

            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            logger.info("Close add product popup");
        }
        return this;
    }

    public void clickOnTheSaveBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        logger.info("Create a new product wholesale campaign successfully");
    }
}
