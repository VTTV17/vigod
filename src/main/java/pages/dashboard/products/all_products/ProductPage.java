package pages.dashboard.products.all_products;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static utilities.links.Links.STORE_CURRENCY;

public class ProductPage extends ProductVerify {
    String language;
    String pageLoadedTextVIE = "Thiết lập từ khóa SEO";
    String pageLoadedTextENG = "SEO Settings";


    public ProductPage(WebDriver driver) {
        super(driver);
    }

    Logger logger = LogManager.getLogger(ProductPage.class);
    Actions actions = new Actions(driver);

    // Character limit
    final int MAX_PRODUCT_NAME = 100;
    //    final int MAX_PRODUCT_DESCRIPTION = 100000;
    final int MAX_PRODUCT_DESCRIPTION = 1000;
    final int MAX_STOCK_QUANTITY = 1000000;
    final int MAX_STOCK_QUANTITY_IMEI = 10;
    final Long MAX_PRICE = 99999999999L;

    final int MAX_VARIATION_NAME = 14;
    final int MAX_VARIATION_VALUE = 20;
    final int MAX_VARIATION_NUM = 2;
    final int MAX_NUMBER_OF_VARIATION_PER_VARIATION = 20;
    final int MAX_TOTAL_VARIATION_QUANTITY = 50;


    // Product Information
    public String productName;
    public String productDescription;

    public int withoutVariationListingPrice;
    public List<Integer> VariationListingPrice;

    public int withoutVariationSellingPrice;
    public List<Integer> VariationSellingPrice;

    public int withoutVariationCostPrice;
    public List<Integer> VariationCostPrice;

    public String VAT;

    public Map<String, List<String>> variation;
    public boolean isIMEI;

    public List<String> variationsValueList = new ArrayList<>();

    public List<String> depositValueList = new ArrayList<>();

    public int withoutVariationStockQuantity;
    public List<Integer> variationStockQuantity = new ArrayList<>();

    public int increaseStockForNextVariation;

    public List<String> branchList = new ArrayList<>();

    public boolean isDisplayIfOutOfStock;

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
                .hideFacebookBubble()
                .selectLanguage(language)
                .navigateToProducts_AllProductsPage();

        logger.info("Navigate to All Products Page");
        logger.info("Title of Setting page is %s".formatted(driver.getTitle()));
        return this;
    }

    /**
     * On the "All Products" page, click on the "Create Product" button to open the "Create Product" page
     */
    public ProductPage clickOnTheCreateProductBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PRODUCT_BTN)).click();

        logger.info("Click on the Create Product button");

        // wait create product page loaded successfully
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains(pageLoadedTextVIE) || driver.getPageSource().contains(pageLoadedTextENG);
        });
        return this;
    }

    /**
     * On the "Create Product"/"Product Detail" page, input product name
     */
    public ProductPage inputProductName(String... productName) {
        // get product name
        this.productName = productName.length == 0 ? RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(MAX_PRODUCT_NAME) + 1) : productName[0];

        // input product name
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_NAME)).clear();
        PRODUCT_NAME.sendKeys(this.productName);

        // log
        logger.info("Input product name: %s".formatted(this.productName));

        // get branch list
        for (WebElement element : BRANCH_NAME_LIST) {
            branchList.add(element.getText());
        }
        return this;
    }

    /**
     * On the "Create Product"/"Product Detail" page, input product description
     */
    public ProductPage inputProductDescription(String... productDescription) {
        // get product description
        this.productDescription = productDescription.length == 0 ? RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(MAX_PRODUCT_DESCRIPTION)) : productDescription[0];

        // input product description
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).clear();
        PRODUCT_DESCRIPTION.sendKeys(this.productDescription);

        logger.info("Input product descriptions: %s".formatted(this.productDescription));
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
    public ProductPage changePriceForNoVariationProduct(int... price) {
        // get listing, selling and cost price
        this.withoutVariationListingPrice = price.length == 0 ? (int) (Math.random() * MAX_PRICE) : price[0];
        this.withoutVariationSellingPrice = price.length < 2 ? (int) (Math.random() * this.withoutVariationListingPrice) : price[1];
        this.withoutVariationCostPrice = price.length < 3 ? (int) (Math.random() * this.withoutVariationSellingPrice) : price[2];

        // input listing price
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(0))).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + withoutVariationListingPrice).build().perform();
        logger.info("Input listing price: %d".formatted(withoutVariationListingPrice));

        // input selling price
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(1))).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + withoutVariationSellingPrice).build().perform();
        logger.info("Input selling price: %d".formatted(withoutVariationSellingPrice));

        // input cost price
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_PRICE.get(2))).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + withoutVariationCostPrice).build().perform();
        logger.info("Input cost price: %d".formatted(withoutVariationCostPrice));
        return this;
    }

    /**
     * <p> On the "Create Product"/"Product Detail" page, select VAT</p>
     * <p> WARN: In case, VAT does not match with any VAT in the VAT list, "Tax does not apply" should be selected</p>
     */
    public ProductPage selectProductVAT(String... VAT) {
        if (VAT.length != 0) {
            // open VAT dropdown
            wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_VAT_DROPDOWN)).click();
            logger.info("Open VAT dropdown list");

            // wait and select VAT
            waitElementList(VAT_LIST);
            for (WebElement element : VAT_LIST) {
                // select VAT match with input VAT
                if (element.getText().contains(VAT[0])) {
                    this.VAT = element.getText();
                    logger.info("VAT: %s".formatted(this.VAT));
                    element.click();
                    break;
                }
            }

            // if no VAT match, "Tax does not apply" is selected
            if (VAT_LIST.size() > 0) {
                VAT_LIST.get(0).click();
                logger.info("VAT: Tax does not apply");
            }
        } else {
            logger.info("VAT: Tax does not apply");
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
            if (count == MAX_VARIATION_NUM) {
                break;
            }
        }
        return newMaps;
    }

    /**
     * generate Variation value
     */
    private List<String> randomStringList(int size) {
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randomList.add(RandomStringUtils.randomAlphanumeric(MAX_VARIATION_VALUE));
        }
        return randomList;
    }

    /**
     * generate variation maps <variation name : list variation value>
     */
    public Map<String, List<String>> randomVariationMap() {
        Map<String, List<String>> map = new HashMap<>();
        int variationNum = RandomUtils.nextInt(MAX_VARIATION_NUM) + 1;
        List<Integer> numberOfVariationValue = new ArrayList<>();
        numberOfVariationValue.add(RandomUtils.nextInt(MAX_NUMBER_OF_VARIATION_PER_VARIATION) + 1);
        for (int i = 1; i < variationNum; i++) {
            int prevMulti = 1;
            for (int id = 0; id < i; id++) {
                prevMulti = prevMulti * numberOfVariationValue.get(id);
            }
            numberOfVariationValue.add(RandomUtils.nextInt(Math.min((MAX_TOTAL_VARIATION_QUANTITY / prevMulti), MAX_NUMBER_OF_VARIATION_PER_VARIATION)) + 1);
        }
        for (Integer num : numberOfVariationValue) {
            map.put(RandomStringUtils.randomAlphanumeric(MAX_VARIATION_NAME), randomStringList(num));
        }
        return map;
    }

    /**
     * On the "Create Product"/"Product Detail" page, create variations follow the variations list
     */
    @SafeVarargs
    public final ProductPage addVariations(Map<String, List<String>>... variation) {
        this.variation = variation.length == 0 ? randomVariationMap() : variation[0];
        this.variation = getVariationsMap(this.variation);
        int id = -1;
        for (String variationName : this.variation.keySet()) {
            id++;
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();
            logger.info("Click on the Add Variation button");
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_NAME.get(id))).sendKeys(variationName);
            logger.info("Input variation %d name: %s".formatted(id, variationName));
            VARIATION_VALUE.get(id).click();
            for (String variationValue : this.variation.get(variationName)) {
                actions.sendKeys("%s\n".formatted(variationValue)).build().perform();
                logger.info("Input variation %d value: %s".formatted(id, variationValue));
            }
        }

        for (WebElement element : VARIATION_TEXT) {
            variationsValueList.add(element.getText().replace(STORE_CURRENCY, "").replace("\n", ""));
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

    public ProductPage manageInventory(boolean... isIMEI) {
        this.isIMEI = isIMEI.length == 0 ? RandomUtils.nextBoolean() : isIMEI[0];
        if (this.isIMEI) {
            wait.until(ExpectedConditions.elementToBeClickable(MANAGE_INVENTORY_BY_IMEI)).click();
            logger.info("MANAGE INVENTORY: Manage inventory by IMEI/Serial number");
        } else {
            logger.info("MANAGE INVENTORY: Manage inventory by product");
        }
        return this;
    }

    public ProductPage changeStockQuantityForNormalProductNoVariation(int... stockQuantity) throws InterruptedException {
        this.withoutVariationStockQuantity = stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY) + 1 : stockQuantity[0];
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_STOCK_QUANTITY)).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + this.withoutVariationStockQuantity).build().perform();
        logger.info("Stock quantity for all branch, number of stock: %d".formatted(this.withoutVariationStockQuantity));
        sleep(3000);

        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_STOCK_QUANTITY)).click();
        return this;
    }

    public ProductPage changeStockQuantityForIMEIProduct(int... stockQuantity) {
        this.withoutVariationStockQuantity = stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY_IMEI) : stockQuantity[0];
        waitElementList(IMEI_STOCK);
        for (int i = 0; i < IMEI_STOCK.size(); i++) {
            IMEI_STOCK.get(i).click();
            waitElementList(LIST_BRANCH_NAME_IN_STOCK_INVENTORY);
            String branchName = LIST_BRANCH_NAME_IN_STOCK_INVENTORY.get(i).getText();
            for (int imei = 0; imei < this.withoutVariationStockQuantity; imei++) {
                wait.until(ExpectedConditions.elementToBeClickable(IMEI_INPUT)).sendKeys(branchName + imei + "\n");
            }
            wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN_IN_IMEI_STOCK_TABLE)).click();
        }
        return this;
    }

    // BH_9536:Check to display/hide if out of stock at product detail

    /**
     * Setting display if out of stock or not
     */
    public ProductPage checkOnTheDisplayIfOutOfStockCheckbox(boolean... isDisplayIfOutOfStock) {
        this.isDisplayIfOutOfStock = isDisplayIfOutOfStock.length == 0 ? RandomUtils.nextBoolean() : isDisplayIfOutOfStock[0];
        waitElementList(CONFIGURE_DISPLAY_IN_SF_CHECKBOX);
        boolean currentCheckboxStatus = CONFIGURE_DISPLAY_IN_SF_CHECKBOX.get(0).isSelected();
        if (currentCheckboxStatus != this.isDisplayIfOutOfStock) {
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_DISPLAY_IN_SF_LABEL.get(0))).click();
        }
        logger.info("Display if out of stock checkbox is checked: %s".formatted(this.isDisplayIfOutOfStock));
        return this;
    }

    // BH_8616:Check to hide/show available stock at product detail

    /**
     * Setting hide remaining stock on online store
     */
    public ProductPage checkOnTheHideRemainingStockOnOnlineStoreCheckbox(boolean isHide) {
        waitElementList(CONFIGURE_DISPLAY_IN_SF_CHECKBOX);
        boolean currentCheckboxStatus = CONFIGURE_DISPLAY_IN_SF_CHECKBOX.get(1).isSelected();
        if (currentCheckboxStatus != isHide) {
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_DISPLAY_IN_SF_LABEL.get(1))).click();
        }
        logger.info("Hide remaining stock on online store is checked: %s".formatted(isHide));
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
        for (int i = 0; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
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
        waitElementList(INPUT_IMEI_VALUE);
        for (int id = 0; id < branchList.size(); id++) {
            String branchName = branchList.get(id);
            for (int i = 0; i < stockQuantity; i++) {
                INPUT_IMEI_VALUE.get(id).sendKeys("IMEI_%s_%s_%s\n".formatted(variationName, branchName, i));
            }
        }
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    /**
     * <p> startQuantity: the first variation quantity</p>
     * <p> increaseStockForNextVariation: the next variation quantity = the previous variation quantity + increaseStockForNextVariation</p>
     * <p> used when the variation stock different</p>
     */
    public ProductPage changeStockQuantityForEachVariationNormal(int... stockQuantity) {
        this.variationStockQuantity.add(stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY) : stockQuantity[0]);
        this.increaseStockForNextVariation = stockQuantity.length > 1 ? stockQuantity[1] : 0;

        for (int i = 0; i < variationsValueList.size() - 1; i++) {
            this.variationStockQuantity.add(this.variationStockQuantity.get(i) + increaseStockForNextVariation);
        }

        int variationID = 0;
        for (int i = 3; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityInTableNormal(this.variationStockQuantity.get(variationID));
            variationID++;
        }
        return this;
    }

    public ProductPage changeStockQuantityForEachVariationsIMEI(int... stockQuantity) {
        this.variationStockQuantity.add(stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY_IMEI) : stockQuantity[0]);
        this.increaseStockForNextVariation = stockQuantity.length > 1 ? stockQuantity[1] : 0;

        int variationID = 0;
        for (int i = 3; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityInTableIMEI(this.variationStockQuantity.get(variationID), variationsValueList.get(variationID));
            if (variationID < variationsValueList.size() - 1) {
                variationStockQuantity.add(variationStockQuantity.get(variationID) + increaseStockForNextVariation);
            }
            variationID++;
        }
        return this;
    }

    public ProductPage changeStockQuantityForAllVariationsNormal(int... stockQuantity) {
        this.variationStockQuantity.add(stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY) : stockQuantity[0]);
        for (int i = 0; i < variationsValueList.size() - 1; i++) {
            variationStockQuantity.add(variationStockQuantity.get(i));
        }
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityInTableNormal(this.variationStockQuantity.get(0));
        System.out.println(variationStockQuantity);
        System.out.println(variationsValueList.size() == variationStockQuantity.size());
        return this;
    }

    private void changeSKUInTable(List<String> listVariationName) {
        waitElementList(SKU_LIST_IN_SKU_TABLE);
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
        for (int i = 4; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open SKU table");
            changeSKUInTable(variationsValueList);
        }
        return this;
    }

    public ProductPage changeSKUForAllVariations() throws InterruptedException {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable(variationsValueList);
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

        for (WebElement element : DEPOSIT_TEXT) {
            depositValueList.add(element.getText().replace(STORE_CURRENCY, "").replace("\n", ""));
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
        for (int i = 0; i < DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(DEPOSIT_TABLE.get(i))).click();
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
        for (int i = 1; i < DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(DEPOSIT_TABLE.get(i))).click();
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
        for (int i = 2; i < DEPOSIT_TABLE.size(); i = i + 3) {
            wait.until(ExpectedConditions.elementToBeClickable(DEPOSIT_TABLE.get(i))).click();
            logger.info("Open SKU table");
            changeSKUInTable(depositValueList);
        }
        return this;
    }

    public ProductPage changeSKUForAllDeposits() throws InterruptedException {
        selectAllDepositsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable(depositValueList);
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
        logger.info("Click on the Save button");
        return this;
    }

    public ProductPage closeNotificationPopup() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(CLOSE_BTN)).click();
//        wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BTN)).click();
        logger.info("Wait Product created successfully! popup show and close it.");
        return this;
    }

    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
}
