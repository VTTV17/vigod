package pages.dashboard.promotion.flashsale.campaign;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;
import java.time.LocalDate;

import static java.lang.Thread.sleep;
import static utilities.character_limit.CharacterLimit.MAX_FLASH_SALE_CAMPAIGN_NAME;
import static utilities.page_loaded_text.PageLoadedText.DB_FLASH_SALE_CAMPAIGN_PAGE_LOADED_TEXT_ENG;
import static utilities.page_loaded_text.PageLoadedText.DB_FLASH_SALE_CAMPAIGN_PAGE_LOADED_TEXT_VIE;

public class CampaignPage extends CampaignElement {
    WebDriverWait wait;

    Logger logger = LogManager.getLogger(CampaignPage.class);

    public CampaignPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait page loaded successfully
     */
    public CampaignPage verifyPageLoaded() {
        new UICommonAction(driver).verifyPageLoaded(DB_FLASH_SALE_CAMPAIGN_PAGE_LOADED_TEXT_VIE, DB_FLASH_SALE_CAMPAIGN_PAGE_LOADED_TEXT_ENG);
        logger.info("Wait page loaded");
        return this;
    }


    /**
     * Input flash sale campaign name
     */
    private void inputCampaignName(String... campaignName) {
        // get campaign name
        String campName = campaignName.length == 0 ? RandomStringUtils.randomAlphanumeric(MAX_FLASH_SALE_CAMPAIGN_NAME) : campaignName[0];

        // input campaign name
        wait.until(ExpectedConditions.elementToBeClickable(CAMPAIGN_NAME)).sendKeys(campName);

        // log
        logger.info("Input flash sale campaign name: %s".formatted(campName));
    }

    /**
     * Convert date format YYYY-MM-DD to DD/MM/YYYY
     */
    private String convertDateFormat(String date) {
        // split date = YYYY, MM, DD
        String[] info = date.split("-");

        // return date with DD/MM/YYYY format
        return "%s/%s/%s".formatted(info[2], info[1], info[0]);
    }

    /**
     * Set flash sale date, if start time ~ 23:59 => setting campaign for the next day
     */
    private void setDate(String date, int incDay) {
        // convert current date to DD/MM/YYYY format
        // if start time ~ 23:59, setting flash sale for next day
        date = convertDateFormat(LocalDate.parse(date).plusDays(incDay).toString());

        // clear date text box
        wait.until(ExpectedConditions.elementToBeClickable(DATE_SET)).clear();

        // input date promotion
        DATE_SET.sendKeys(date);
        logger.info("Set flash sale date: %s".formatted(date));
    }

    /**
     * Convert time to string (HH:MM format)
     */
    private String convertTimeToString(int hour, int min) {
        // if hour < 10, ex: hour = "01" instead of "1"
        String time = hour < 10 ? ("0" + hour) : String.valueOf(hour);

        // if min < 10, ex: min = "01" instead of "1"
        time = min < 10 ? (time + ":" + "0" + min) : (time + ":" + min);
        return time;
    }

    /**
     * Set flash sale time
     */
    private void setTime(int startHour, int startMin) {
        // wait time dropdown can be interactable
        wait.until(ExpectedConditions.elementToBeClickable(TIME_SET)).click();


        String time = convertTimeToString(startHour, startMin) + " - %s".formatted(convertTimeToString(startHour, startMin + 1));
        for (WebElement element : TIME_DROPDOWN) {
            if (element.getText().equals(time)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
                break;
            }
        }
        logger.info("Set flash sale time: %s".formatted(time));
    }

    /**
     * Check has result or not with search keywords
     */
    private boolean hasResult() {
        boolean check = true;
        try {
            TABLE_BODY.isDisplayed();
        } catch (NoSuchElementException ex) {
            check = false;
        }
        return check;
    }

    /**
     * Search and select product
     */
    private void addFlashSaleProduct(String... productName) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_PRODUCT_BTN)).click();
        logger.info("Open select flash sale product popup");
        for (String name : productName) {
            wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).clear();
            SEARCH_BOX.sendKeys(name);
            sleep(1500);
            if (hasResult()) {
                wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL_PRODUCT_MATCH_CONDITION)).click();
                logger.info("Select all product match with keyword: %s".formatted(name));
            } else {
                logger.info("No product match with keyword: %s".formatted(name));
            }
        }

        wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
        logger.info("Close select flash sale product popup");
    }

    /**
     * Remove out of stock product
     */
    private void removeOutOfStockProduct() {
        new UICommonAction(driver).waitElementList(LIST_REMAINING_STOCK);
        for (int i = 0; i < LIST_REMAINING_STOCK.size(); i++) {
            String stock = wait.until(ExpectedConditions.elementToBeClickable(LIST_REMAINING_STOCK.get(i))).getText();
            if (stock.equals("0")) {
                wait.until(ExpectedConditions.elementToBeClickable(DELETE_ICON.get(i))).click();
            }
        }
        logger.info("Remove out of stock product");
    }

    /**
     * Setting flash sale price for each product
     */
    private void inputFlashSalePrice(int price, String currency) {
        new UICommonAction(driver).waitElementList(LIST_FLASH_SALE_PRICE);
        for (int i = 0; i < LIST_FLASH_SALE_PRICE.size(); i++) {
            int sellingPrice = Integer.parseInt(LIST_PRICE.get(i).getText().replace(",", "").replace(currency, ""));
            if (sellingPrice > price) {
                LIST_FLASH_SALE_PRICE.get(i).sendKeys(String.valueOf(price));
            } else {
                LIST_FLASH_SALE_PRICE.get(i).sendKeys(String.valueOf(sellingPrice - 1));
            }
        }
        logger.info("Input flash sale price");
    }

    /**
     * Setting max purchase limit
     */
    private void inputMaxPurchaseLimit(int quantity) {
        new UICommonAction(driver).waitElementList(LIST_MAX_PURCHASE_LIMIT);
        for (int i = 0; i < LIST_MAX_PURCHASE_LIMIT.size(); i++) {
            int remainingStock = Integer.parseInt(LIST_REMAINING_STOCK.get(i).getText().replace(",", ""));
            if (remainingStock > quantity) {
                wait.until(ExpectedConditions.elementToBeClickable(LIST_MAX_PURCHASE_LIMIT.get(i))).sendKeys(String.valueOf(quantity));
            } else {
                wait.until(ExpectedConditions.elementToBeClickable(LIST_MAX_PURCHASE_LIMIT.get(i))).sendKeys(String.valueOf(remainingStock));
            }
        }
        logger.info("Input max purchase limit");
    }

    /**
     * Click Save button to complete create flash sale campaign
     */
    private void clickOnTheSaveBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SAVE_BTN);
        logger.info("Create new flash sale campaign successfully");
    }

    /**
     * Integrate all function
     */
    public void setCampaign(String campaignName, String day, int incDay, int startHour, int startMin, int price, String currency, int quantity, String... productName) throws InterruptedException {
        inputCampaignName(campaignName);
        setDate(day, incDay);
        setTime(startHour, startMin);
        addFlashSaleProduct(productName);
        removeOutOfStockProduct();
        inputFlashSalePrice(price, currency);
        inputMaxPurchaseLimit(quantity);
        clickOnTheSaveBtn();
    }
}
