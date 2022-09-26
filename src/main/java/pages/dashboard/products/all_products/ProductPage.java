package pages.dashboard.products.all_products;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.all_products.conversion_unit.ConversionUnitPage;
import pages.dashboard.products.all_products.wholesale_price.WholesalePricePage;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

import static java.lang.Thread.sleep;

public class ProductPage extends ProductVerify {
    String language;

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    Logger logger = LogManager.getLogger(LogManager.class);
    Actions actions = new Actions(driver);

    /**
     * Set language to determine the expected page title or something that we need
     */
    public ProductPage setLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * <p> After login: </p>
     * <p> Wait for home page loading </p>
     * <p> And click on the "Products" button on the Side menu </p>
     * <p> To navigate to the "All products" page </p>
     */
    public ProductPage navigate() throws InterruptedException {
        new HomePage(driver).verifyPageLoaded()
                .selectLanguage(language)
                .navigateToAllProductsPage();
        logger.info("Navigate to All Products Page");
        wait.until(ExpectedConditions.titleIs("Admin Staging - Products"));
        logger.info("Title of Setting page is %s".formatted(driver.getTitle()));
        return this;
    }

    /**
     * On the "All Products" page, click on the "Create Product" button to open the "Create Product" page
     *
     */
    public ProductPage clickOnTheCreateProductBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PRODUCT_BTN)).click();
        logger.info("Click on the Create Product button");
        return this;
    }

    /**
     * On the "Create Product"/"Product Detail" page, input product name
     */
    public ProductPage inputProductName(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_NAME)).clear();
        PRODUCT_NAME.sendKeys(productName);
        logger.info("Input product name: %s".formatted(productName));
        return this;
    }

    /**
     * On the "Create Product"/"Product Detail" page, input product description
     */
    public ProductPage inputProductDescription(String productDescription) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).clear();
        PRODUCT_DESCRIPTION.sendKeys(productDescription);
        logger.info("Input product descriptions: %s".formatted(productDescription));
        return this;
    }

    /**
     * On the "Create Product"/"Product Detail" page, input product name
     */
    public ProductPage uploadProductImage(String imageFileName) {
        PRODUCT_IMAGE.sendKeys(Paths.get(System.getProperty("user.dir") + "/src/main/resources/uploadfile/product_images/%s".formatted(imageFileName).replace("/", File.separator)).toString());
        logger.info("Upload product image");
        return this;
    }

    /**
     * <p> On the "Create Product"/"Product Detail" page, input listing/selling/cost price</p>
     * <p> WARN1: This function is only used for the normal/IMEI product without variation</p>
     */
    public ProductPage changePriceForNoVariationProduct(int listingPrice, int sellingPrice, int costPrice) {
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

    /**
     * <p> On the "Create Product"/"Product Detail" page, select VAT</p>
     * <p> WARN: In case, VAT does not match with any VAT in the VAT list, "Tax does not apply" should be selected</p>
     */
    public ProductPage selectProductVAT(String VAT) {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_VAT_DROPDOWN)).click();
        logger.info("Open VAT dropdown list");
        waitElementList(VAT_LIST);
        for (WebElement element : VAT_LIST) {
            if (element.getText().contains(VAT)) {
                logger.info("Select product VAT: %s".formatted(element.getText()));
                element.click();
                break;
            }
        }
        return this;
    }

    /**
     * Only allow 2 variations
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

    /**
     * On the "Create Product"/"Product Detail" page, create variations follow the variations list
     */
    public ProductPage addVariations(Map<String, List<String>> variation) {
        variation = getVariationsMap(variation);
        int id = -1;
        for (String variationName : variation.keySet()) {
            id++;
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();
            logger.info("Click on the Add Variation button");
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_NAME.get(id))).sendKeys(variationName);
            logger.info("Input variation %d name: %s".formatted(id, variationName));
            VARIATION_VALUE.get(id).click();
            for (String variationValue : variation.get(variationName)) {
                actions.sendKeys("%s\n".formatted(variationValue)).build().perform();
                logger.info("Input variation %d value: %s".formatted(id, variationValue));
            }
        }
        return this;
    }

    /**
     * <p> On the "Create Product"/"Product Detail" page, select the product collections</p>
     * <p> WARN: the collection should be ignored if does not match with any collection on the collection dropdown</p>
     */
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

    public ProductPage changeStockQuantityForNormalProductNoVariation(int stockQuantity) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_STOCK_QUANTITY)).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + stockQuantity).build().perform();
        logger.info("Stock quantity for all branch, number of stock: %d".formatted(stockQuantity));

        sleep(3000);

        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_STOCK_QUANTITY)).click();
        return this;
    }

    public ProductPage changeStockQuantityForIMEIProduct(int stockQuantity) {
        waitElementList(IMEI_STOCK);
        for (int i = 0; i < IMEI_STOCK.size(); i++) {
            IMEI_STOCK.get(i).click();
            waitElementList(LIST_BRANCH_NAME_IN_STOCK_INVENTORY);
            String branchName = LIST_BRANCH_NAME_IN_STOCK_INVENTORY.get(i).getText();
            for (int imei = 0; imei < stockQuantity; imei++) {
                wait.until(ExpectedConditions.elementToBeClickable(IMEI_INPUT)).sendKeys(branchName + imei + "\n");
            }
            wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN_IN_IMEI_STOCK_TABLE)).click();
        }
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

    public ProductPage setPlatForm(List<String> platformList) {
        // Deselect all platform
        for (int i = 0; i < PRODUCT_PLATFORM_LABEL.size(); i++) {
            if (PRODUCT_PLATFORM_CHECKBOX.get(i).isSelected()) {
                actions.moveToElement(PRODUCT_PLATFORM_LABEL.get(i)).click().build().perform();
            }
        }
        for (String platform : platformList) {
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

    public ProductPage changePriceForEachVariation(int listingPrice, int sellingPrice, int costPrice) {
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

    public ProductPage changePriceForAllVariations(int listingPrice, int sellingPrice, int costPrice) throws InterruptedException {
        selectAllVariationsCheckbox();
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SELECT_ACTIONS_IN_VARIATION_TABLE);
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(0))).click();
        changeVariationPriceInTable(listingPrice, sellingPrice, costPrice);
        return this;
    }

    private void changeStockQuantityInTableNormal(int stockQuantity) {
        wait.until(ExpectedConditions.elementToBeClickable(STOCK_VALUE_IN_STOCK_QUANTITY_TABLE)).sendKeys(Integer.toString(stockQuantity));
        logger.info("Change stock quantity for all branch: %d".formatted(stockQuantity));

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }


    /**
     * IMEI: IMEI_ + Variation name + branch name + index
     */
    private void changeStockQuantityInTableIMEI(int stockQuantity, String variationName) {
        int id = 1;
        waitElementList(INPUT_IMEI_VALUE);
        for (WebElement element : INPUT_IMEI_VALUE) {
            String branchName = getBranchNameInStockTable().get(id);
            for (int i = 0; i < stockQuantity; i++) {
                element.sendKeys("IMEI_" + variationName + branchName + i + "\n");
            }
            id++;
        }
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    private List<String> getListVariationName() {
        List<String> list = new ArrayList<>();
        waitElementList(VARIATION_TEXT);
        for (WebElement element : VARIATION_TEXT) {
            list.add(element.getText().split("\n")[0]);
        }
        return list;
    }

    private List<String> getListDepositName() {
        List<String> list = new ArrayList<>();
        waitElementList(DEPOSIT_TEXT);
        for (WebElement element : DEPOSIT_TEXT) {
            list.add(element.getText().split("\n")[0]);
        }
        return list;
    }

    private List<String> getBranchNameInStockTable() {
        List<String> list = new ArrayList<>();
        waitElementList(BRANCH_TEXT_IN_STOCK_TABLE);
        for (WebElement element : BRANCH_TEXT_IN_STOCK_TABLE) {
            list.add(element.getText());
        }
        return list;
    }

    private List<String> getListBranchNameInSKUTable() {
        List<String> list = new ArrayList<>();
        waitElementList(BRANCH_TEXT_IN_SKU_TABLE);
        for (WebElement element : BRANCH_TEXT_IN_SKU_TABLE) {
            list.add(element.getText());
        }
        return list;
    }

    public ProductPage changeStockQuantityForEachVariationNormal(int stockQuantity) {
        for (int i = 3; i < OPEN_VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityInTableNormal(stockQuantity);
        }
        return this;
    }

    public ProductPage changeStockQuantityForEachVariationsIMEI(int stockQuantity) {
        List<String> listVariationName = getListVariationName();
        int variationID = 0;
        for (int i = 3; i < OPEN_VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityInTableIMEI(stockQuantity, listVariationName.get(variationID));
            variationID++;
        }
        return this;
    }

    public ProductPage changeStockQuantityForAllVariationsNormal(int stockQuantity) {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityInTableNormal(stockQuantity);
        return this;
    }

    private void changeSKUInTable(List<String> listVariationName) {
        waitElementList(SKU_LIST_IN_SKU_TABLE);
        List<String> branchList = getListBranchNameInSKUTable();
        int branchId = 0;
        int variationId = 0;
        for (WebElement skuElement : SKU_LIST_IN_SKU_TABLE) {
            String branchName = branchList.get(branchId);
            wait.until(ExpectedConditions.elementToBeClickable(skuElement)).sendKeys("SKU_" + listVariationName.get(variationId) + branchName);
            if (branchId < branchList.size() - 1) {
                branchId++;
            } else {
                branchId = 0;
                variationId++;
            }

        }
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage changeSKUForEachVariation() {
        List<String> listVariationName = getListVariationName();
        for (int i = 4; i < OPEN_VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_VARIATION_TABLE.get(i))).click();
            logger.info("Open SKU table");
            changeSKUInTable(listVariationName);
        }
        return this;
    }

    public ProductPage changeSKUForAllVariations() throws InterruptedException {
        selectAllVariationsCheckbox();
        List<String> listVariationName = getListVariationName();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable(listVariationName);
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

    public ProductPage clickOnTheConfigureConversionUnitBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_CONVERSION_UNIT_CHECKBOX)).click();
        wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_CONVERSION_UNIT_BTN)).click();
        return this;
    }

    public ProductPage configureConversionUnitForNoVariationProduct(Map<String, Integer> conversionMap) throws InterruptedException {
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

    public ProductPage configureWholesalePriceForNoVariationProduct(Map<Integer, List<String>> wholesaleMap) {
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
        actions.sendKeys(Keys.TAB);
        for (String deposit : depositList) {
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

    public ProductPage changePriceForEachDeposit(Integer depositPrice) {
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

    public ProductPage changePriceForAllDeposits(int depositPrice) throws InterruptedException {
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
            changeStockQuantityInTableNormal(stockQuantity);
        }
        return this;
    }

    public ProductPage changeStockQuantityForAllDeposits(int stockQuantity) {
        selectAllDepositsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityInTableNormal(stockQuantity);
        return this;
    }

    public ProductPage changeSKUForEachDeposit() {
        List<String> listDepositName = getListDepositName();
        for (int i = 2; i < OPEN_DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(OPEN_DEPOSIT_TABLE.get(i))).click();
            logger.info("Open SKU table");
            changeSKUInTable(listDepositName);
        }
        return this;
    }

    public ProductPage changeSKUForAllDeposits() throws InterruptedException {
        selectAllDepositsCheckbox();
        List<String> listDepositName = getListDepositName();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable(listDepositName);
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

    public ProductPage clickOnTheSaveBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        return this;
    }

    public ProductPage closeNotificationPopup() {
        wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BTN)).click();
        return this;
    }

    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }

    // Integrate function

    private ProductPage createNormalProductWithoutVariation(String productName, String productDescription) throws InterruptedException {
        setLanguage(language).navigate()
                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
//                .uploadProductImage(imgFileName)
//                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
//                .selectProductVAT(VAT)
//                .selectCollections(collectionName)
//                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
//                .setDimension(weight, length, width, height)
//                .setPlatForm(platformList)
//                .clickOnTheSaveBtn()
//                .closeNotificationPopup()
//                .openProductDetailPage()
//                .checkProductName(productName)
//                .checkProductDescription(productDescription)
//                .checkPrice(listingPrice, sellingPrice, costPrice)
//                .checkVAT(VAT)
//                .checkCollection(List.of(collectionName))
//                .checkStock(stockQuantity)
//                .checkDimension(weight, length, width, height)
//                .checkSelectedPlatform(platformList)
                .completeVerify();
        return this;
    }
}
