package pages.dashboard.promotion.discount.product_wholesale_campaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static java.lang.Thread.sleep;

public class ProductWholesaleCampaignPage extends ProductWholesaleCampaignElement {
    WebDriverWait wait;

    public ProductWholesaleCampaignPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(ProductWholesaleCampaignPage.class);

    public ProductWholesaleCampaignPage inputCampaignName(String campaignName) {
        wait.until(ExpectedConditions.elementToBeClickable(CAMPAIGN_NAME)).sendKeys(campaignName);
        logger.info("Input campaign name: %s".formatted(campaignName));
        return this;
    }

    public ProductWholesaleCampaignPage setPromotionDate(boolean isNoExpiry, int startIn) throws InterruptedException {
        if (isNoExpiry) {
            wait.until(ExpectedConditions.elementToBeClickable(NO_EXPIRY_DATE_CHECKBOX)).click();
            logger.info("No expiry date - checked");

            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            sleep(1000);
            AVAILABLE_DATE.get(startIn).click();
            logger.info("Active date: %s".formatted(LocalDate.now().plusDays(startIn)));
        } else {
            logger.info("No expiry date - no checked");
            wait.until(ExpectedConditions.elementToBeClickable(ACTIVE_DATE)).click();
            sleep(1000);
            AVAILABLE_DATE.get(startIn).click();
            AVAILABLE_DATE.get(startIn).click();
            APPLY_BTN.click();
            logger.info("Active date: %s".formatted(LocalDate.now().plusDays(startIn)));

        }
        return this;
    }

    public ProductWholesaleCampaignPage setDiscountTypeAndValue(int discountTypeID, int value) throws InterruptedException {
        waitElementList(TYPE_OF_DISCOUNT_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", TYPE_OF_DISCOUNT_LABEL.get(discountTypeID));

        DISCOUNT_VALUE.click();
        DISCOUNT_VALUE.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        if (discountTypeID == 0) {
            logger.info("Discount type: Percentage");

            if (value > 100) {
                DISCOUNT_VALUE.sendKeys(String.valueOf(100));
                logger.info("Percentage: %s".formatted(100));
            } else {
                DISCOUNT_VALUE.sendKeys(String.valueOf(value));
                logger.info("Percentage: %s".formatted(value));
            }
        } else {
            logger.info("Discount type: Fixed amount");

            DISCOUNT_VALUE.sendKeys(String.valueOf(value));
            logger.info("Fixed amount: %s".formatted(value));
        }
        PAGE_TITLE.click();
        sleep(1000);

        return this;
    }

    public ProductWholesaleCampaignPage setCustomerSegment(int segmentTypeID, String... segmentName) throws InterruptedException {
        waitElementList(CUSTOMER_SEGMENT_LABEL);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", CUSTOMER_SEGMENT_LABEL.get(segmentTypeID));

        if (segmentTypeID == 0) {
            logger.info("Customer segment: All customers");
        } else {
            logger.info("Customer segment: %s".formatted((Object) segmentName));

            wait.until(ExpectedConditions.elementToBeClickable(ADD_SEGMENT_BTN)).click();
            logger.info("Open add segment popup");

            for (String segment : segmentName) {
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
        waitElementList(APPLIES_TO_LABEL);
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
        waitElementList(APPLICABLE_BRANCH_LABEL);
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

    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
}
