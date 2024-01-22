package web.Dashboard.promotion.flashsale.campaign;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAction;
import web.Dashboard.promotion.flashsale.time.TimeManagementPage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static utilities.character_limit.CharacterLimit.MAX_FLASH_SALE_CAMPAIGN_NAME;
import static utilities.links.Links.STORE_CURRENCY;

public class FlashSaleCampaignPage extends FlashSaleCampaignElement {
    WebDriverWait wait;

    Logger logger = LogManager.getLogger(FlashSaleCampaignPage.class);
    UICommonAction commonAction;

    public String campaignDate;

    public static Map<String, List<String>> flashSaleProductNameAndVariation;
    public static List<Integer> flashSalePrice;
    public static List<Integer> flashSaleStock;
    public static List<Integer> flashSaleMaxPurchaseLimit;


    public FlashSaleCampaignPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    /**
     * Wait page loaded successfully
     */
    private void verifyPageLoaded() {
        commonAction.waitElementVisible(UI_PRODUCT_INFORMATION);
        logger.info("Wait page loaded");
    }


    /**
     * Input flash sale campaign name
     */
    public FlashSaleCampaignPage inputCampaignName(String... campaignName) {
        // wait page loaded
        verifyPageLoaded();

        // get campaign name
        String campName = campaignName.length == 0 ? RandomStringUtils.randomAlphanumeric(MAX_FLASH_SALE_CAMPAIGN_NAME) : campaignName[0];

        // input campaign name
        wait.until(ExpectedConditions.elementToBeClickable(CAMPAIGN_NAME)).sendKeys(campName);

        // log
        logger.info("Input flash sale campaign name: %s".formatted(campName));

        return this;
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
    public FlashSaleCampaignPage setFlashSaleCampaignDate(String... date) {
        // get flash sale date
        campaignDate = date.length == 0 ? LocalDate.now().plusDays(RandomUtils.nextInt(365)).toString() : date[0];

        // convert current date to DD/MM/YYYY format
        // if start time ~ 23:59, setting flash sale for next day
        campaignDate = convertDateFormat(LocalDate.parse(campaignDate).plusDays(TimeManagementPage.incDay).toString());

        // clear date text box
        wait.until(ExpectedConditions.elementToBeClickable(DATE_SET)).clear();

        // input date promotion
        DATE_SET.sendKeys(campaignDate);
        logger.info("Set flash sale date: %s".formatted(campaignDate));

        return this;
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
    public FlashSaleCampaignPage setFlashSaleCampaignTime() {
        // wait time dropdown can be interactable
        wait.until(ExpectedConditions.elementToBeClickable(TIME_SET)).click();

        // input time setting just created on manage time page
        String time = "%s - %s".formatted(convertTimeToString(TimeManagementPage.startHour, TimeManagementPage.startMin), convertTimeToString(TimeManagementPage.endHour, TimeManagementPage.endMin));

        // find and select time
        for (WebElement element : TIME_DROPDOWN) {
            if (element.getText().equals(time)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
                break;
            }
        }

        // log
        logger.info("Set flash sale time: %s".formatted(time));

        return this;
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

    private void searchAndSelectProduct(String productName) throws InterruptedException {
        // clear and input new search keyword
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).clear();
        SEARCH_BOX.sendKeys(productName);

        // wait api return search result
        sleep(1500);

        // check have result or not
        // select all products match with search condition

        if (hasResult()) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL_PRODUCT_MATCH_CONDITION)).click();
            logger.info("Select all product match with keyword: %s".formatted(productName));
        } else logger.info("No product match with keyword: %s".formatted(productName));
    }

    /**
     * Search and select product
     */
//    public FlashSaleCampaignPage addFlashSaleProduct(String... productName) throws InterruptedException {
//        // open product list
//        wait.until(ExpectedConditions.elementToBeClickable(ADD_PRODUCT_BTN)).click();
//        logger.info("Open select flash sale product popup");
//
//        // if list product is provided
//        // search product by product name
//        // else select just created product name
//        if (productName.length > 0) for (String name : productName) searchAndSelectProduct(name);
//        else searchAndSelectProduct(uiProductName);
//
//        // close popup
//        wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
//        logger.info("Close select flash sale product popup");
//
//        return this;
//    }

    /**
     * Remove out of stock product
     */
    private void removeOutOfStockProduct() {
        // wait remaining stock element visible
        new UICommonAction(driver).waitElementList(LIST_REMAINING_STOCK);

        // remove out of stock product
        for (int i = 0; i < LIST_REMAINING_STOCK.size(); i++) {
            String stock = wait.until(ExpectedConditions.elementToBeClickable(LIST_REMAINING_STOCK.get(i))).getText();

            // stock = 0 => out of stock => should be removed
            if (stock.equals("0")) wait.until(ExpectedConditions.elementToBeClickable(DELETE_ICON.get(i))).click();
        }

        // log
        logger.info("Remove out of stock product");
    }

    public FlashSaleCampaignPage getFlashSaleProductNameAndVariation() {
        // remove out of stock product
        removeOutOfStockProduct();

        // generate product information map = {[Variation product name, List of variation], [Without variation product, ""], ...}
        // we have more table with same structure
        // ex: flash sale product price map = {[Variation product name, List price of each variation], [Without variation product, price], ...]
        Map<String, List<String>> productInfo = new HashMap<>();

        // wait product name/ variation element visible
        commonAction.waitElementList(PRODUCT_NAME);
        commonAction.waitElementList(PRODUCT_VARIATION);

        // variation product: get product name and variation
        // without variation product: get product name, variation should be set is ""
        for (int i = 0; i < PRODUCT_NAME.size(); i++) {
            // get product name
            String productName = PRODUCT_NAME.get(i).getText();
            String variationValue;

            // get variation value
            try {
                // PRODUCT_VARIATION.get(i).getText() = "product name \n variation_value1 | variation_value2 ..."
                // get variation value with format "variation_value1 variation_value2 ...."
                variationValue = PRODUCT_VARIATION.get(i).getText().split("\n")[1].replace(" | ", " ");

            } catch (ArrayIndexOutOfBoundsException ex) {
                // if no variation value (without variation product), variation value = ""
                variationValue = "";
            }

            // merge variation value by product name
            // ex: Product A has 2 variations V1, V2, Product B has 3 variations V3, V4, V5
            // => variation value list of product A is List.of (V1, V2) and product B is List.of(V3, V4, V5)
            // to check other information on product detail screen instead of
            // having to open detail page of same product multiple times
            List<String> variationValueList = productInfo.get(productName);

            // if product does not have any variation
            // generate variation list
            if (variationValueList == null) {
                variationValueList = new ArrayList<>();
            }

            // add new variation to variation value list
            variationValueList.add(variationValue);

            // update new variation value list for this product
            productInfo.put(productName, variationValueList);
        }

        // get flash sale product name and variation for another test
        flashSaleProductNameAndVariation = productInfo;

        return this;
    }

    /**
     * Setting flash sale price for each product
     */
    public FlashSaleCampaignPage inputFlashSalePrice(int... price) {
        // init flash sale price list
        flashSalePrice = new ArrayList<>();

        boolean isProvidedPrice = price.length != 0;

        // wait flash sale price element visible
        commonAction.waitElementList(LIST_FLASH_SALE_PRICE);

        // input flash sale price
        for (int i = 0; i < LIST_FLASH_SALE_PRICE.size(); i++) {

            // get selling price
            int sellingPrice = Integer.parseInt(LIST_PRICE.get(i).getText().replace(",", "").replace(STORE_CURRENCY, ""));

            // get flash sale price
            int productPrice = isProvidedPrice ? price[0] : RandomUtils.nextInt(sellingPrice);

            // flash sale always < selling price
            LIST_FLASH_SALE_PRICE.get(i).sendKeys(String.valueOf(productPrice));

            // get flash sale product price for another test
            flashSalePrice.add(productPrice);
        }

        // log
        logger.info("Input flash sale price");

        return this;
    }

    /**
     * Setting flash sale stock
     */

    public FlashSaleCampaignPage inputFlashSaleStock(int... stock) {
        // init flash sale stock
        flashSaleStock = new ArrayList<>();

        // check flash sale stock quantity is provided or not
        boolean isProvidedStock = stock.length != 0;

        // wait flash sale stock element visible
        commonAction.waitElementList(LIST_FLASH_SALE_STOCK);

        // input flash sale stock
        for (int i = 0; i < LIST_FLASH_SALE_STOCK.size(); i++) {

            // get remaining stock
            int remainingStock = Integer.parseInt(LIST_REMAINING_STOCK.get(i).getText().replace(",", ""));

            // get and validate flash sale stock (flash sale stock is always less than or equal to remaining stock)
            int campaignStock = isProvidedStock ? Math.min(stock[0], remainingStock) : RandomUtils.nextInt(remainingStock) + 1;

            // clear old flash sale stock
            wait.until(ExpectedConditions.elementToBeClickable(LIST_FLASH_SALE_STOCK.get(i))).sendKeys(Keys.CONTROL + "a" + Keys.DELETE);

            // input new flash sale stock
            LIST_FLASH_SALE_STOCK.get(i).sendKeys(String.valueOf(campaignStock));

            // get flash sale stock for another test
            flashSaleStock.add(campaignStock);
        }

        // log
        logger.info("Input flash sale stock");

        return this;
    }

    /**
     * Setting max purchase limit
     */
    public FlashSaleCampaignPage inputMaxPurchaseLimit(int... maxPurchaseLimitQuantity) {
        // init flash sale max purchase limit
        flashSaleMaxPurchaseLimit = new ArrayList<>();

        // check max purchase limit is provided or not
        boolean isProvideLimitStock = maxPurchaseLimitQuantity.length != 0;

        // wait max purchase limit element list
        commonAction.waitElementList(LIST_MAX_PURCHASE_LIMIT);

        // inout max purchase limit
        for (int i = 0; i < LIST_MAX_PURCHASE_LIMIT.size(); i++) {

            // get and validate flash sale max purchase limit (max purchase limit is always less than or equal to flash sale stock)
            int maxPurchaseLimit = isProvideLimitStock ? Math.min(maxPurchaseLimitQuantity[0], flashSaleStock.get(i)) : RandomUtils.nextInt(flashSaleStock.get(i)) + 1;

            // input max purchase limit for each product
            wait.until(ExpectedConditions.elementToBeClickable(LIST_MAX_PURCHASE_LIMIT.get(i))).sendKeys(Integer.toString(maxPurchaseLimit));

            // get flash sale max purchase limit for another test
            flashSaleMaxPurchaseLimit.add(maxPurchaseLimit);
        }

        // click around
        CAMPAIGN_NAME.click();

        // log
        logger.info("Input max purchase limit");

        return this;
    }

    /**
     * Click Save button to complete create flash sale campaign
     */
    public void completeCreateFlashSaleCampaign() {
        // wait Save button visible
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN));

        // complete create new flash sale campaign
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SAVE_BTN);

        // log
        logger.info("Create new flash sale campaign successfully");

    }
}
