package pages.dashboard.products.all_products;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.all_products.conversion_unit.ConversionUnitPage;
import pages.dashboard.products.all_products.wholesale_price.WholesalePricePage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ProductPage extends ProductElement {
    WebDriverWait wait;
    String language;

    public ProductPage(WebDriver driver) {
        super(driver);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(LogManager.class);
    Actions actions = new Actions(driver);

    public ProductPage setLanguage(String language) {
        this.language = language;
        return this;
    }

    public ProductPage navigate() throws InterruptedException, IOException {
        new HomePage(driver).verifyPageLoaded().selectLanguage(language).navigateToAllProductsPage();
        logger.info("Navigate to All Products Page");
        wait.until(ExpectedConditions.titleIs("Admin Staging - Products"));
        logger.info("Title of Setting page is %s".formatted(driver.getTitle()));
        return this;
    }

    public ProductPage clickOnTheCreateProductBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PRODUCT_BTN)).click();
        logger.info("Click on the Create Product button");
        return this;
    }

    public ProductPage inputProductName(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_NAME)).clear();
        PRODUCT_NAME.sendKeys(productName);
        logger.info("Input product name: %s".formatted(productName));
        return this;
    }

    public ProductPage inputProductDescription(String productDescription) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).clear();
        PRODUCT_DESCRIPTION.sendKeys(productDescription);
        logger.info("Input product descriptions: %s".formatted(productDescription));
        return this;
    }

    public ProductPage uploadProductImage(String imageFileName) {
        PRODUCT_IMAGE.sendKeys(Paths.get(System.getProperty("user.dir") + "/src/main/resources/uploadfile/product_images/%s".formatted(imageFileName).replace("/", File.separator)).toString());
        logger.info("Upload product image");
        return this;
    }

    public ProductPage inputPriceNormalProduct(int listingPrice, int sellingPrice, int costPrice) {
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(0))).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + listingPrice).build().perform();
        logger.info("Input listing price: %d".formatted(listingPrice));

        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(1))).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + sellingPrice).build().perform();
        logger.info("Input selling price: %d".formatted(sellingPrice));

        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(2))).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + costPrice).build().perform();
        logger.info("Input cost price: %d".formatted(costPrice));
        return this;
    }

    public ProductPage selectProductVAT(int vatID) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_VAT_DROPDOWN)).click();
        logger.info("Open VAT dropdown list");
        wait.until(ExpectedConditions.elementToBeClickable(VAT_LIST.get(vatID)));
        logger.info("Select product VAT: %s".formatted(VAT_LIST.get(vatID).getText()));
        VAT_LIST.get(vatID).click();
        return this;
    }

    /**
     * <p>Input variations maps. If maps.size() > 2, only keep 2 variations</p>
     */
    public Map<String, List<String>> getVariationsMap(Map<String, List<String>> variations) {
        Map<String, List<String>> newMaps = new HashMap<>();
        int count = 0;
        for (String key : variations.keySet()) {
            newMaps.put(key, variations.get(key));
            count++;
            if (count == 2) {
                break;
            }
        }
        return newMaps;
    }

    public ProductPage addVariations(Map<String, List<String>> variation) {

        variation = getVariationsMap(variation);
        int id = -1;
        for (String variationName : variation.keySet()) {
            id++;
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();
            logger.info("Click on the Add Variation button");
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_NAME.get(id))).sendKeys(variationName);
            logger.info("Input variation %d name: %s".formatted(id, variationName));
            for (String variationValue : variation.get(variationName)) {
                VARIATION_VALUE.get(id).click();
                actions.sendKeys("%s\n".formatted(variationValue)).build().perform();
                logger.info("Input variation %d value: %s".formatted(id, variationValue));
            }
        }
        return this;
    }

    public ProductPage selectCollections(String... collectionNames) throws InterruptedException {
        for (String collectionName : collectionNames) {
            wait.until(ExpectedConditions.elementToBeClickable(COLLECTION_SEARCH_BOX)).clear();
            COLLECTION_SEARCH_BOX.click();
            COLLECTION_SEARCH_BOX.sendKeys(collectionName);
            sleep(500);
            if (COLLECTION_LIST.size() > 0) {
                logger.info("Collection \"%s\" is selected".formatted(COLLECTION_LIST.get(0).getText()));
                COLLECTION_LIST.get(0).click();
            } else {
                logger.info("No collection found with keyword: %s".formatted(collectionName));
            }
        }
        return this;
    }

    public ProductPage waitAndHideFacebookBubble() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.visibilityOf(FACEBOOK_BUBBLE));
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'none';", FACEBOOK_BUBBLE);
        return this;
    }

    public ProductPage manageInventoryByIMEI() {
        wait.until(ExpectedConditions.elementToBeClickable(MANAGE_INVENTORY_BY_IMEI)).click();
        logger.info("Setting manage inventory by IMEI");
        return this;
    }

    public ProductPage setInventoryByNormalProduct(int stockQuantity) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_STOCK_QUANTITY)).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + stockQuantity).build().perform();
        logger.info("Stock quantity for all branch, number of stock: %d".formatted(stockQuantity));

        sleep(3000);

        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_STOCK_QUANTITY)).click();
        return this;
    }

    public ProductPage setDimension(int weight, int length, int width, int height) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WEIGHT));
        actions.moveToElement(PRODUCT_WEIGHT).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + weight).build().perform();
        logger.info("Input weight: %d".formatted(weight));

        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LENGTH));
        actions.moveToElement(PRODUCT_LENGTH).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + length).build().perform();
        logger.info("Input length: %d".formatted(length));

        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WIDTH));
        actions.moveToElement(PRODUCT_WIDTH).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + width).build().perform();
        logger.info("Input width: %d".formatted(width));

        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_HEIGHT));
        actions.moveToElement(PRODUCT_HEIGHT).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + height).build().perform();
        logger.info("Input height: %d".formatted(height));

        return this;
    }

    public ProductPage setPlatForm(List<String> platFormList) {
        // Deselect all platform
        for (int i = 0; i < PRODUCT_PLATFORM_LABEL.size(); i++) {
            if (PRODUCT_PLATFORM_CHECKBOX.get(i).isSelected()) {
                actions.moveToElement(PRODUCT_PLATFORM_LABEL.get(i)).click().build().perform();
            }
        }
        for (String platform : platFormList) {
            for (WebElement element : PRODUCT_PLATFORM_LABEL) {
                if (element.getText().contains(platform)) {
                    element.click();
                    break;
                }
            }
        }
        return this;
    }

    private void changeVariationPriceInTable(int listingPrice, int sellingPrice, int costPrice) {
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(listingPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply all listing price for selected variations, listing price: %d".formatted(listingPrice));

        wait.until(ExpectedConditions.elementToBeClickable(PRICE_DROPDOWN_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_TYPE_IN_VARIATION_TABLE.get(1))).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(sellingPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply all listing price for selected variations, selling price: %d".formatted(sellingPrice));

        wait.until(ExpectedConditions.elementToBeClickable(PRICE_DROPDOWN_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_TYPE_IN_VARIATION_TABLE.get(2))).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(costPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply all listing price for selected variations, cost price: %d".formatted(costPrice));

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage changeVariationPriceForEachVariation(int listingPrice, int sellingPrice, int costPrice) {
        for (int i = 0; i < OPEN_VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_VARIATION_TABLE.get(i))).click();
            logger.info("Open variation price table");
            changeVariationPriceInTable(listingPrice, sellingPrice, costPrice);
        }
        return this;
    }

    private void selectAllVariationsCheckbox() {
        if (!SELECT_ALL_VARIATIONS_CHECKBOX.isSelected()) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL_VARIATIONS_LABEL)).click();
        }
    }

    public ProductPage changeVariationPriceForAllVariations(int listingPrice, int sellingPrice, int costPrice) throws InterruptedException {
        selectAllVariationsCheckbox();
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(0))).click();
        changeVariationPriceInTable(listingPrice, sellingPrice, costPrice);
        return this;
    }

    private void changeStockQuantityInTable(int stockQuantity) {
        wait.until(ExpectedConditions.elementToBeClickable(STOCK_VALUE_IN_STOCK_QUANTITY_TABLE)).sendKeys(Integer.toString(stockQuantity));
        logger.info("Change stock quantity for all branch: %d".formatted(stockQuantity));

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage changeStockQuantityForEachVariation(int stockQuantity) {
        for (int i = 3; i < OPEN_VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityInTable(stockQuantity);
        }
        return this;
    }

    public ProductPage changeStockQuantityForAllVariations(int stockQuantity) {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityInTable(stockQuantity);
        return this;
    }

    private void changeSKUInTable() {
        for (WebElement skuElement : SKU_LIST_IN_SKU_TABLE) {
            String skuValue = RandomStringUtils.random(10, true, true).toUpperCase(Locale.ROOT);
            wait.until(ExpectedConditions.elementToBeClickable(skuElement)).sendKeys(skuValue);
        }

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage changeSKUForEachVariation() throws InterruptedException {
        for (int i = 4; i < OPEN_VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_VARIATION_TABLE.get(i))).click();
            logger.info("Open SKU table");
            sleep(500);

            changeSKUInTable();
        }
        return this;
    }

    public ProductPage changeSKUForAllVariations() throws InterruptedException {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable();
        return this;
    }

    private void addImage(String imageFileName) {
        ADD_IMAGE.sendKeys(Paths.get(System.getProperty("user.dir") + "/src/main/resources/uploadfile/product_images/%s".formatted(imageFileName).replace("/", File.separator)).toString());
        logger.info("Upload variation image");

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage uploadImageForEachVariation(String imageFileName) {
        for (WebElement element : IMAGE_LIST_IN_VARIATION_TABLE) {
            element.click();
            addImage(imageFileName);
        }
        return this;
    }

    public ProductPage uploadImageForAllVariations(String imageFileName) {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(3))).click();
        addImage(imageFileName);
        return this;
    }

    public ProductPage clickOnTheConfigureConversionUnit() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_CONVERSION_UNIT_CHECKBOX)).click();
        wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_CONVERSION_UNIT_BTN)).click();
        return this;
    }

    public ProductPage configureConversionUnitForNormalProduct(Map<String, Integer> conversionMap) throws InterruptedException {
        new ConversionUnitPage(driver).verifyPageLoaded()
                .selectConversionUnit(conversionMap);
        return this;
    }

    public ProductPage configureConversionUnitForVariationProduct(Map<String, Integer> conversionMap) throws InterruptedException {
        new ConversionUnitPage(driver).verifyPageLoaded()
                .selectVariations()
                .configureConversionUnitForAllVariations(conversionMap);
        return this;
    }

    public ProductPage clickOnTheConfigureWholesalePriceBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICE_CHECK_BOX)).click();
        wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_WHOLESALE_PRICE_BTN)).click();
        return this;
    }

    public ProductPage configureWholesalePriceForNormalProduct(Map<Integer, List<String>> wholesaleMap) {
        new WholesalePricePage(driver).verifyPageLoaded()
                .addWholesalePriceForNormalProduct(wholesaleMap)
                .configureWholesalePrice(wholesaleMap)
                .configureSegment(wholesaleMap);
        return this;
    }

    public ProductPage configureWholesalePriceForVariationProduct(Map<Integer, List<String>> wholesaleMap) throws InterruptedException {
        new WholesalePricePage(driver).verifyPageLoaded()
                .addWholesalePriceForAllVariations(wholesaleMap)
                .configureWholesalePrice(wholesaleMap)
                .configureSegment(wholesaleMap);
        return this;
    }

    public ProductPage clickOnTheAddDepositBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_DEPOSIT_BTN)).click();
        return this;
    }

    public ProductPage addDeposit(List<String> depositList) {
        for (String deposit : depositList) {
            wait.until(ExpectedConditions.elementToBeClickable(DEPOSIT_VALUE)).click();
            actions.sendKeys(deposit + "\n").build().perform();
        }
        return this;
    }

    private void changeDepositPrice(Integer depositPrice) {
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(depositPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply deposit price for selected deposit, price: %d".formatted(depositPrice));

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage changeDepositPriceForEachDeposit(Integer depositPrice) {
        for (int i = 0; i < OPEN_DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_DEPOSIT_TABLE.get(i))).click();
            logger.info("Open deposit price table");
            changeDepositPrice(depositPrice);
        }
        return this;
    }

    private void selectAllDepositsCheckbox() {
        if (!SELECT_ALL_DEPOSIT_CHECKBOX.isSelected()) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL_DEPOSIT_LABEL)).click();
        }
    }

    public ProductPage changeDepositPriceForAllDeposits(int depositPrice) throws InterruptedException {
        selectAllDepositsCheckbox();
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(0))).click();
        changeDepositPrice(depositPrice);
        return this;
    }

    public ProductPage changeStockQuantityForEachDeposit(int stockQuantity) {
        for (int i = 1; i < OPEN_DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_DEPOSIT_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityInTable(stockQuantity);
        }
        return this;
    }

    public ProductPage changeStockQuantityForAllDeposits(int stockQuantity) {
        selectAllDepositsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityInTable(stockQuantity);
        return this;
    }

    public ProductPage changeSKUForEachDeposit() throws InterruptedException {
        for (int i = 2; i < OPEN_DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_DEPOSIT_TABLE.get(i))).click();
            logger.info("Open SKU table");
            sleep(500);

            changeSKUInTable();
        }
        return this;
    }

    public ProductPage changeSKUForAllDeposits() throws InterruptedException {
        selectAllDepositsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable();
        return this;
    }

    public ProductPage uploadImageForEachDeposit(String imageFileName) {
        for (WebElement element : IMAGE_LIST_IN_DEPOSIT_TABLE) {
            element.click();
            addImage(imageFileName);
        }
        return this;
    }

    public ProductPage uploadImageForAllDeposits(String imageFileName) {
        selectAllDepositsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(3))).click();
        addImage(imageFileName);
        return this;
    }

}
