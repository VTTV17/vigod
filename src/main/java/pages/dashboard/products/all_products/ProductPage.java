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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.all_products.conversion_unit.ConversionUnitPage;
import pages.dashboard.products.all_products.wholesale_price.WholesalePricePage;
import utilities.UICommonAction;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static utilities.character_limit.CharacterLimit.*;
import static utilities.links.Links.STORE_CURRENCY;
import static utilities.page_loaded_text.PageLoadedText.DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_ENG;
import static utilities.page_loaded_text.PageLoadedText.DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_VIE;

public class ProductPage extends ProductVerify {
    String language;

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    Logger logger = LogManager.getLogger(ProductPage.class);
    Actions actions = new Actions(driver);


    // Product Information
    public static String productName;
    public static String productDescription;

    // Price
    public static int withoutVariationListingPrice;
    public static List<Integer> variationListingPrice;
    public static int withoutVariationSellingPrice;
    public static List<Integer> variationSellingPrice;
    public int withoutVariationCostPrice;
    public List<Integer> variationCostPrice;

    // VAT
    public String VAT;

    public boolean isIMEI;

    // variation
    public static Map<String, List<String>> variation;
    public static List<String> variationValueList;

    // deposit
    public List<String> depositList;
    public List<String> depositValueList = new ArrayList<>();

    public List<String> productCollections;
    public static int withoutVariationStockQuantity;
    public static List<Integer> variationStockQuantity;

    public static int increaseStockForNextVariation;

    public Map<String, Integer> conversionMap;

    public List<String> branchList;

    public static boolean isDisplayIfOutOfStock;

    public static boolean isHideRemainingStock;

    public int weight;
    public int length;
    public int width;
    public int height;

    public List<String> productPlatform;

    public static List<String>[] wholesaleMap;
    public static boolean isCreateByUI;

    /**
     * Set language to determine the expected page title or something that we need
     */
    public ProductPage setLanguage(String... language) {
        // if no language is provided, language is random in VIE and ENG
        this.language = language.length == 0 ? List.of("VIE", "ENG").get(RandomUtils.nextInt(2)) : language[0];
        return this;
    }

    /**
     * Navigate to All products page
     */
    public ProductPage navigate() throws InterruptedException {
        // wait home page loaded
        // hide facebook buble
        // select language
        // and navigate to All products page
        new HomePage(driver).verifyPageLoaded()
                .hideFacebookBubble()
                .selectLanguage(language)
                .navigateToProducts_AllProductsPage();

        // log
        logger.info("Navigate to All Products Page");
        logger.info("Title of Setting page is %s".formatted(driver.getTitle()));

        return this;
    }

    /**
     * On the "All Products" page, click on the "Create Product" button to open the "Create Product" page
     */
    public ProductPage clickOnTheCreateProductBtn() {
        // set control var
        isCreateByUI = true;

        // click create product button
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PRODUCT_BTN)).click();

        // log
        logger.info("Click on the Create Product button");

        // wait create product page loaded
        new UICommonAction(driver).verifyPageLoaded(DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_VIE, DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_ENG);

        return this;
    }

    /**
     * On the "Create Product"/"Product Detail" page, input product name
     */
    public ProductPage inputProductName(String... productName) {
        // get product name
        ProductPage.productName = productName.length == 0 ? RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(MAX_PRODUCT_NAME) + 1) : productName[0];

        // input product name
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_NAME)).clear();
        PRODUCT_NAME.sendKeys(ProductPage.productName);

        // log
        logger.info("Input product name: %s".formatted(ProductPage.productName));

        // init branch list
        branchList = new ArrayList<>();

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
        ProductPage.productDescription = productDescription.length == 0 ? RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(MAX_PRODUCT_DESCRIPTION)) : productDescription[0];

        // input product description
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).clear();
        PRODUCT_DESCRIPTION.sendKeys(ProductPage.productDescription);

        logger.info("Input product descriptions: %s".formatted(ProductPage.productDescription));
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
    public ProductPage changePriceForWithoutVariationProduct(int... price) {
        // get listing, selling and cost price
        withoutVariationListingPrice = price.length > 0 ? price[0] : (int) (Math.random() * MAX_PRICE);
        withoutVariationSellingPrice = price.length > 1 ? price[1] : (int) (Math.random() * withoutVariationListingPrice);
        this.withoutVariationCostPrice = price.length > 2 ? price[2] : (int) (Math.random() * withoutVariationSellingPrice);

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
        // check product has VAT or not
        if (VAT.length != 0) {

            // open VAT dropdown
            wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_VAT_DROPDOWN)).click();
            logger.info("Open VAT dropdown list");

            // wait and select VAT
            new UICommonAction(driver).waitElementList(VAT_LIST);
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

            // if product does not have VAT, default VAT has been selected
        } else {
            logger.info("VAT: Tax does not apply");
        }
        return this;
    }

    // Variation data pre-process

    /**
     * Only allow 2 variations
     */
    public Map<String, List<String>> getVariationsMap(Map<String, List<String>> variations) {
        Map<String, List<String>> newMaps = new HashMap<>();
        int count = 0;
        for (String key : variations.keySet()) {
            newMaps.put(key, variations.get(key));
            count++;
            if (count == MAX_VARIATION_QUANTITY) {
                break;
            }
        }
        return newMaps;
    }

    /**
     * generate Variation value
     */
    private List<String> generateListString(int size, int length) {
        List<String> randomList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randomList.add(RandomStringUtils.randomAlphanumeric(length));
        }
        return randomList;
    }

    /**
     * generate variation maps <variation name : list variation value>
     */
    public Map<String, List<String>> randomVariationMap() {
        Map<String, List<String>> map = new HashMap<>();
        int variationNum = RandomUtils.nextInt(MAX_VARIATION_QUANTITY) + 1;
        List<Integer> numberOfVariationValue = new ArrayList<>();
        numberOfVariationValue.add(variationNum);
        for (int i = 1; i < variationNum; i++) {
            int prevMulti = 1;
            for (int id = 0; id < i; id++) {
                prevMulti = prevMulti * numberOfVariationValue.get(id);
            }
            numberOfVariationValue.add(RandomUtils.nextInt(Math.min((MAX_VARIATION_QUANTITY_FOR_ALL_VARIATIONS / prevMulti), MAX_VARIATION_QUANTITY_FOR_EACH_VARIATION)) + 1);
        }
        for (Integer num : numberOfVariationValue) {
            map.put(RandomStringUtils.randomAlphanumeric(MAX_VARIATION_NAME), generateListString(num, MAX_VARIATION_VALUE));
        }
        return map;
    }

    /**
     * On the "Create Product"/"Product Detail" page, create variations follow the variations list
     */
    @SafeVarargs
    public final ProductPage addVariations(Map<String, List<String>>... variation) {
        // get variation maps <variation name: list variation value>
        ProductPage.variation = variation.length == 0 ? randomVariationMap() : variation[0];

        // remove not used variation
        ProductPage.variation = getVariationsMap(ProductPage.variation);
        int id = -1;

        // input variation name, variation value
        for (String variationName : ProductPage.variation.keySet()) {
            id++;
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();
            logger.info("Click on the Add Variation button");
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_NAME.get(id))).sendKeys(variationName);
            logger.info("Input variation %d name: %s".formatted(id, variationName));
            VARIATION_VALUE.get(id).click();
            for (String variationValue : ProductPage.variation.get(variationName)) {
                actions.sendKeys("%s\n".formatted(variationValue)).build().perform();
                logger.info("Input variation %d value: %s".formatted(id, variationValue));
            }
        }

        // init variation value list
        variationValueList = new ArrayList<>();

        // get variation list for another test
        for (WebElement element : VARIATION_TEXT) {
            variationValueList.add(element.getText().split(STORE_CURRENCY)[0].replace("\n", ""));
        }

        return this;
    }

    /**
     * <p> On the "Create Product"/"Product Detail" page, select the product collections</p>
     * <p> WARN: the collection should be ignored if does not match with any collection on the collection dropdown</p>
     */
    public ProductPage selectCollections(String... collectionNames) throws InterruptedException {

        // select product collections
        for (String collectionName : collectionNames) {
            // search collection by collection name
            wait.until(ExpectedConditions.elementToBeClickable(COLLECTION_SEARCH_BOX)).clear();
            COLLECTION_SEARCH_BOX.click();
            COLLECTION_SEARCH_BOX.sendKeys(collectionName);
            sleep(500);

            // init product collection
            productCollections = new ArrayList<>();

            // Have more result, select the first collection
            if (COLLECTION_LIST.size() > 0) {
                logger.info("Collection \"%s\" is selected".formatted(COLLECTION_LIST.get(0).getText()));

                // get product collect for another test
                this.productCollections.add(COLLECTION_LIST.get(0).getText());
                COLLECTION_LIST.get(0).click();

            } else {
                logger.info("No collection found with keyword: %s".formatted(collectionName));
            }
        }

        return this;
    }

    public ProductPage manageInventory(boolean... isIMEI) {
        // check product is IMEI or not
        this.isIMEI = isIMEI.length == 0 ? RandomUtils.nextBoolean() : isIMEI[0];

        if (this.isIMEI) {
            wait.until(ExpectedConditions.elementToBeClickable(MANAGE_INVENTORY_BY_IMEI)).click();
            logger.info("MANAGE INVENTORY: Manage inventory by IMEI/Serial number");
        } else {
            logger.info("MANAGE INVENTORY: Manage inventory by product");
        }

        return this;
    }

    public ProductPage changeStockQuantityWithoutVariationNormalProduct(int... stockQuantity) {
        // get without variation stock quantity
        withoutVariationStockQuantity = stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY) + 1 : stockQuantity[0];

        // input stock quantity
        wait.until(ExpectedConditions.elementToBeClickable(NORMAL_PRODUCT_STOCK_QUANTITY)).click();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + withoutVariationStockQuantity).build().perform();
        logger.info("Stock quantity for all branch, number of stock: %d".formatted(withoutVariationStockQuantity));

        // apply stock quantity for all branches
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_STOCK_QUANTITY)).click();
        return this;
    }

    public ProductPage changeStockQuantityForWithoutVariationIMEIProduct(int... stockQuantity) {
        // get without variation stock quantity
        withoutVariationStockQuantity = stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY_IMEI) : stockQuantity[0];

        // wait and generate imei for each branch
        new UICommonAction(driver).waitElementList(IMEI_STOCK);
        for (int i = 0; i < IMEI_STOCK.size(); i++) {
            IMEI_STOCK.get(i).click();
            new UICommonAction(driver).waitElementList(LIST_BRANCH_NAME_IN_STOCK_INVENTORY);
            String branchName = LIST_BRANCH_NAME_IN_STOCK_INVENTORY.get(i).getText();
            for (int imei = 0; imei < withoutVariationStockQuantity; imei++) {
                // input imei with format: IMEI_ + Variation name + branch name + index
                wait.until(ExpectedConditions.elementToBeClickable(IMEI_INPUT)).sendKeys("IMEI_%s_%s_%s\n".formatted("", branchName, imei));
                logger.info("Branch: %s, IMEI/Serial number: %s".formatted(branchName, "IMEI_%s_%s_%s".formatted("", branchName, imei)));
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
        ProductPage.isDisplayIfOutOfStock = isDisplayIfOutOfStock.length == 0 ? RandomUtils.nextBoolean() : isDisplayIfOutOfStock[0];
        new UICommonAction(driver).waitElementList(CONFIGURE_DISPLAY_IN_SF_CHECKBOX);
        boolean currentCheckboxStatus = CONFIGURE_DISPLAY_IN_SF_CHECKBOX.get(0).isSelected();
        if (currentCheckboxStatus != ProductPage.isDisplayIfOutOfStock) {
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_DISPLAY_IN_SF_LABEL.get(0))).click();
        }
        logger.info("Display if out of stock checkbox is checked: %s".formatted(ProductPage.isDisplayIfOutOfStock));

        return this;
    }

    // BH_8616:Check to hide/show available stock at product detail

    /**
     * Setting hide remaining stock on online store
     */
    public ProductPage checkOnTheHideRemainingStockOnOnlineStoreCheckbox(boolean... isHideRemainingStock) {
        // get "Hide remaining stock on online store" checkbox status
        ProductPage.isHideRemainingStock = isHideRemainingStock.length == 0 ? RandomUtils.nextBoolean() : isHideRemainingStock[0];

        // wait and change "Hide remaining stock on online store" checkbox status
        new UICommonAction(driver).waitElementList(CONFIGURE_DISPLAY_IN_SF_CHECKBOX);
        boolean currentCheckboxStatus = CONFIGURE_DISPLAY_IN_SF_CHECKBOX.get(1).isSelected();
        if (currentCheckboxStatus != ProductPage.isHideRemainingStock) {
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_DISPLAY_IN_SF_LABEL.get(1))).click();
        }
        logger.info("Hide remaining stock on online store is checked: %s".formatted(ProductPage.isHideRemainingStock));

        return this;
    }

    public ProductPage setDimension(int... dimension) {
        // get product dimension
        this.weight = dimension.length > 0 ? dimension[0] : RandomUtils.nextInt(MAX_WEIGHT);
        this.length = dimension.length > 1 ? dimension[1] : RandomUtils.nextInt(MAX_LENGTH);
        this.width = dimension.length > 2 ? dimension[2] : RandomUtils.nextInt(MAX_WIDTH);
        this.height = dimension.length > 3 ? dimension[3] : RandomUtils.nextInt(MAX_HEIGHT);

        // input product weight
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WEIGHT));
        actions.moveToElement(PRODUCT_WEIGHT).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + weight).build().perform();
        logger.info("Input weight: %d".formatted(weight));

        // input product length
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LENGTH));
        actions.moveToElement(PRODUCT_LENGTH).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + length).build().perform();
        logger.info("Input length: %d".formatted(length));

        // input product width
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WIDTH));
        actions.moveToElement(PRODUCT_WIDTH).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + width).build().perform();
        logger.info("Input width: %d".formatted(width));

        // input product height
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_HEIGHT));
        actions.moveToElement(PRODUCT_HEIGHT).click().build().perform();
        actions.sendKeys(Keys.CONTROL + "a" + Keys.DELETE + height).build().perform();
        logger.info("Input height: %d".formatted(height));

        return this;
    }

    @SafeVarargs
    public final ProductPage setPlatForm(List<String>... platformList) {

        // Deselect all platforms
        for (int i = 0; i < PRODUCT_PLATFORM_LABEL.size(); i++) {
            if (PRODUCT_PLATFORM_CHECKBOX.get(i).isSelected()) {
                actions.moveToElement(PRODUCT_PLATFORM_LABEL.get(i)).click().build().perform();
            }
        }

        // init product platform
        productPlatform = new ArrayList<>();

        // select product platform
        if (platformList.length > 0) {
            for (String platform : platformList[0]) {
                for (WebElement element : PRODUCT_PLATFORM_LABEL) {
                    if (element.getText().contains(platform)) {
                        this.productPlatform.add(element.getText());
                        element.click();
                        break;
                    }
                }
            }
        }

        // if no platform, Web has been selected as default setting
        if (productPlatform.size() == 0) {
            this.productPlatform.add(PRODUCT_PLATFORM_LABEL.get(1).getText());
            PRODUCT_PLATFORM_LABEL.get(1).click();
        }

        return this;
    }

    private void changeVariationPriceInTable(int listingPrice, int sellingPrice, int costPrice) {
        // input listing price
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(listingPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply all listing price for selected variations, listing price: %d".formatted(listingPrice));

        // input selling price
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_DROPDOWN_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_TYPE_IN_VARIATION_TABLE.get(1))).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(sellingPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply all listing price for selected variations, selling price: %d".formatted(sellingPrice));

        // input cost price
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_DROPDOWN_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_TYPE_IN_VARIATION_TABLE.get(2))).click();
        wait.until(ExpectedConditions.elementToBeClickable(PRICE_VALUE_IN_TABLE)).clear();
        PRICE_VALUE_IN_TABLE.sendKeys(Integer.toString(costPrice));
        wait.until(ExpectedConditions.elementToBeClickable(APPLY_ALL_IN_TABLE)).click();
        logger.info("Apply all listing price for selected variations, cost price: %d".formatted(costPrice));

        // completed setting price for variation
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    public ProductPage changePriceForEachVariation(int... price) {
        // init variation price
        variationListingPrice = new ArrayList<>();
        variationSellingPrice = new ArrayList<>();
        variationCostPrice = new ArrayList<>();

        // get listing, selling and cost price for each variation
        for (int variationID = 0; variationID < variationValueList.size(); variationID++) {
            variationListingPrice.add(price.length > 0 ? price[0] : (int) (Math.random() * MAX_PRICE));
            variationSellingPrice.add(price.length > 1 ? price[1] : (int) (Math.random() * variationListingPrice.get(variationID)));
            this.variationCostPrice.add(price.length > 2 ? price[2] : (int) (Math.random() * variationSellingPrice.get(variationID)));
        }

        // input product price for each variation
        int variationID = 0;
        for (int i = 0; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open variation price table");
            changeVariationPriceInTable(variationListingPrice.get(variationID), variationSellingPrice.get(variationID), this.variationCostPrice.get(variationID));
            variationID++;
        }
        return this;
    }

    /**
     * check on the select all variation checkbox
     */
    private void selectAllVariationsCheckbox() {
        if (!SELECT_ALL_VARIATIONS_CHECKBOX.isSelected()) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_ALL_VARIATIONS_LABEL)).click();
        }

        logger.info("Select all variations");
    }

    /**
     * update price for each variation
     */
    public ProductPage changePriceForAllVariations(int... price) throws InterruptedException {
        // init variation price
        variationListingPrice = new ArrayList<>();
        variationSellingPrice = new ArrayList<>();
        variationCostPrice = new ArrayList<>();

        // get listing, selling and cost price for each variation
        variationListingPrice.add(price.length > 0 ? price[0] : (int) (Math.random() * MAX_PRICE));
        variationSellingPrice.add(price.length > 1 ? price[1] : (int) (Math.random() * variationListingPrice.get(0)));
        this.variationCostPrice.add(price.length > 2 ? price[2] : (int) (Math.random() * variationSellingPrice.get(0)));

        for (int variationID = 1; variationID < variationValueList.size(); variationID++) {
            variationListingPrice.add(variationListingPrice.get(variationID - 1));
            variationSellingPrice.add(variationSellingPrice.get(variationID - 1));
            this.variationCostPrice.add(this.variationCostPrice.get(variationID - 1));
        }

        // input product price and apply for all variations
        selectAllVariationsCheckbox();
        sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SELECT_ACTIONS_IN_VARIATION_TABLE);
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(0))).click();
        logger.info("Open variation price table");
        changeVariationPriceInTable(variationListingPrice.get(0), variationSellingPrice.get(0), this.variationCostPrice.get(0));

        return this;
    }

    /**
     * Change stock quantity for normal product
     */
    private void changeStockQuantityNormalProduct(int stockQuantity) {

        //  change stock quantity
        wait.until(ExpectedConditions.elementToBeClickable(CHANGE_STOCK)).click();
        logger.info("Update stock quantity - CHANGE");

        // input stock quantity
        wait.until(ExpectedConditions.elementToBeClickable(STOCK_VALUE_IN_STOCK_QUANTITY_TABLE)).sendKeys(Integer.toString(stockQuantity));
        logger.info("Change stock quantity for all branch: %d".formatted(stockQuantity));

        // update stock
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    /**
     * Add imei/serial number
     */
    private void changeStockQuantityForIMEIProduct(int stockQuantity, String variationName) {
        // add imei with format: IMEI_ + Variation name + branch name + index
        new UICommonAction(driver).waitElementList(INPUT_IMEI_VALUE);
        for (int id = 0; id < branchList.size(); id++) {
            String branchName = branchList.get(id);
            for (int i = 0; i < stockQuantity; i++) {
                INPUT_IMEI_VALUE.get(id).sendKeys("IMEI_%s_%s_%s\n".formatted(variationName, branchName, i));
                logger.info("Variation/Deposit: %s, Branch: %s, IMEI/Serial number: %s".formatted(variationName, branchName, "IMEI_%s_%s_%s".formatted(variationName, branchName, i)));
            }
        }
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    /**
     * <p> Change stock for Normal product</p>
     * <p> stockQuantity[0]: the first variation quantity</p>
     * <p> stockQuantity[1]: increase quantity for next the variation</p>
     * <p> the next variation quantity = the previous variation quantity + stockQuantity[1]</p>
     * <p> used when the variation stock different</p>
     */
    public ProductPage changeStockQuantityForEachVariationNormalProduct(int... stockQuantity) {
        // init variation stock
        variationStockQuantity = new ArrayList<>();

        // get stock quantity and number of increase stock
        variationStockQuantity.add(stockQuantity.length > 0 ? stockQuantity[0] : RandomUtils.nextInt(MAX_STOCK_QUANTITY));
        increaseStockForNextVariation = stockQuantity.length > 1 ? stockQuantity[1] : 0;

        // get variation stock for another test
        for (int i = 1; i < variationValueList.size(); i++) {
            variationStockQuantity.add(variationStockQuantity.get(i - 1) + increaseStockForNextVariation);
        }

        // change quantity stock
        int variationID = 0;
        for (int i = 3; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityNormalProduct(variationStockQuantity.get(variationID));
            variationID++;
        }

        return this;
    }

    /**
     * <p> Change stock for IMEI product</p>
     * <p> stockQuantity[0]: the first variation quantity</p>
     * <p> stockQuantity[1]: increase quantity for next the variation</p>
     * <p> the next variation quantity = the previous variation quantity + stockQuantity[1]</p>
     * <p> used when the variation stock different</p>
     */
    public ProductPage changeStockQuantityForEachVariationsIMEIProduct(int... stockQuantity) {
        // init variation stock
        variationStockQuantity = new ArrayList<>();

        // get stock quantity and number of increase stock
        variationStockQuantity.add(stockQuantity.length > 0 ? stockQuantity[0] : RandomUtils.nextInt(MAX_STOCK_QUANTITY_IMEI));
        increaseStockForNextVariation = stockQuantity.length > 1 ? stockQuantity[1] : 0;

        // get variation stock for another test
        for (int i = 1; i < variationValueList.size(); i++) {
            variationStockQuantity.add(variationStockQuantity.get(i - 1) + increaseStockForNextVariation);
        }

        // change quantity stock
        int variationID = 0;
        for (int i = 3; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open stock quantity table");
            changeStockQuantityForIMEIProduct(variationStockQuantity.get(variationID), variationValueList.get(variationID));
            variationID++;
        }
        return this;
    }

    /**
     * Change stock for all variations (Normal product)
     */

    public ProductPage changeStockQuantityForAllVariationsNormalProduct(int... stockQuantity) {
        // init variation stock
        variationStockQuantity = new ArrayList<>();

        // get stock quantity
        variationStockQuantity.add(stockQuantity.length == 0 ? RandomUtils.nextInt(MAX_STOCK_QUANTITY) : stockQuantity[0]);
        for (int i = 1; i < variationValueList.size(); i++) {
            variationStockQuantity.add(variationStockQuantity.get(i));
        }

        // change stock quantity for all variations
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityNormalProduct(variationStockQuantity.get(0));

        return this;
    }

    /**
     * Add SKU
     */
    private void changeSKUInTable(List<String> listVariationName) {
        new UICommonAction(driver).waitElementList(SKU_LIST_IN_SKU_TABLE);
        int branchId = 0;
        int variationId = 0;

        // add SKU for each branch
        for (WebElement skuElement : SKU_LIST_IN_SKU_TABLE) {
            String branchName = branchList.get(branchId);
            // add SKU with format: SKU_Variation/Deposit_BranchName
            wait.until(ExpectedConditions.elementToBeClickable(skuElement)).sendKeys("SKU_%s%s".formatted(listVariationName.get(variationId), branchName));
            logger.info("Branch: %s, Variation: %s, SKU: %s");
            if (branchId < branchList.size() - 1) {
                branchId++;
            } else {
                branchId = 0;
                variationId++;
            }
        }

        // complete add SKU
        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
        logger.info("SKU has been added successfully");
    }

    /**
     * Add SKU for each variation
     */
    public ProductPage changeSKUForEachVariation() {
        for (int i = 4; i < VARIATION_TABLE.size(); i = i + 5) {
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE.get(i))).click();
            logger.info("Open SKU table");
            changeSKUInTable(variationValueList);
        }
        return this;
    }


    /**
     * Add SKU for all variations
     */
    public ProductPage changeSKUForAllVariations() throws InterruptedException {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(2))).click();
        sleep(500);
        changeSKUInTable(variationValueList);
        return this;
    }

    /**
     * Upload image for variation/deposit
     */
    private void addImage(String imageFileName) {
        ADD_IMAGE.sendKeys(Paths.get(System.getProperty("user.dir") + "/src/main/resources/uploadfile/product_images/%s".formatted(imageFileName).replace("/", File.separator)).toString());
        logger.info("Upload variation image");

        wait.until(ExpectedConditions.elementToBeClickable(UPDATE_BTN)).click();
    }

    /**
     * Upload image for each variation
     */
    public ProductPage uploadImageForEachVariation(String imageFileName) {
        for (WebElement element : IMAGE_LIST_IN_VARIATION_TABLE) {
            element.click();
            addImage(imageFileName);
        }
        return this;
    }

    /**
     * Upload image for all variations
     */
    public ProductPage uploadImageForAllVariations(String imageFileName) {
        selectAllVariationsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_VARIATION_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(3))).click();
        addImage(imageFileName);
        return this;
    }

    /**
     * Open configure conversion unit page
     */
    public ProductPage openConfigureConversionUnitPage() {
        // check on add conversion unit checkbox
        wait.until(ExpectedConditions.elementToBeClickable(ADD_CONVERSION_UNIT_CHECKBOX)).click();

        // click on configure button
        wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_CONVERSION_UNIT_BTN)).click();

        return this;
    }

    @SafeVarargs
    public final ProductPage configureConversionUnitForNoVariationProduct(Map<String, Integer>... conversionMap) throws InterruptedException {
        // init conversion map
        this.conversionMap = new HashMap<>();

        ConversionUnitPage conversionUnitPage = new ConversionUnitPage(driver);
        conversionUnitPage.verifyPageLoaded()
                .selectConversionUnit(conversionMap);
        this.conversionMap = conversionUnitPage.conversionMap;
        return this;
    }

    @SafeVarargs
    public final ProductPage configureConversionUnitForVariationProduct(Map<String, Integer>... conversionMap) throws InterruptedException {
        ConversionUnitPage conversionUnitPage = new ConversionUnitPage(driver);
        conversionUnitPage.verifyPageLoaded()
                .selectVariations()
                .configureConversionUnitForAllVariations(conversionMap);
        this.conversionMap = conversionUnitPage.conversionMap;
        return this;
    }

    public ProductPage openConfigureWholesalePricePage() throws InterruptedException {
        // check on add wholesale pricing checkbox
        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICE_CHECK_BOX)).click();

        // click on configure button
        wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_WHOLESALE_PRICE_BTN)).click();

        sleep(1000);
        if (!driver.getCurrentUrl().contains("wholesale-price")) {
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOf(TOAST_MESSAGE));
            wait.until(ExpectedConditions.invisibilityOf(TOAST_MESSAGE));

            // check on add wholesale pricing checkbox
            wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICE_CHECK_BOX)).click();

            // click on configure button
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_WHOLESALE_PRICE_BTN)).click();
        }

        return this;
    }

    @SafeVarargs
    public final ProductPage configureWholesalePriceForWithoutVariationProduct(List<String>... wholesaleMaps) throws InterruptedException {
        // call main function from WholesalePricePage.java
        new WholesalePricePage(driver).verifyPageLoaded()
                .configureWholesalePriceForWithoutVariationProduct(wholesaleMaps)
                .completeConfigWholesalePrice();

        // get wholesaleMap for another test
        ProductPage.wholesaleMap = WholesalePricePage.wholesaleMap;
        return this;
    }

    @SafeVarargs
    public final ProductPage configureWholesalePriceForVariationProduct(List<String>... wholesaleMap) throws InterruptedException {
        // call main function from WholesalePricePage.java
        new WholesalePricePage(driver).verifyPageLoaded()
                .configureWholesalePriceForVariationProduct(wholesaleMap)
                .completeConfigWholesalePrice();

        // get wholesaleMap for another test
        ProductPage.wholesaleMap = WholesalePricePage.wholesaleMap;
        return this;
    }

    public ProductPage clickOnTheAddDepositBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_DEPOSIT_BTN)).click();
        return this;
    }

    @SafeVarargs
    public final ProductPage addDeposit(List<String>... depositList) {
        this.depositList = depositList.length == 0 ? generateListString(MAX_DEPOSIT_QUANTITY, MAX_DEPOSIT_NAME) : depositList[0];
        actions.sendKeys(Keys.TAB);
        for (String deposit : this.depositList) {
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
            changeStockQuantityNormalProduct(stockQuantity);
        }
        return this;
    }

    public ProductPage changeStockQuantityForAllDeposits(int stockQuantity) {
        selectAllDepositsCheckbox();
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_ACTIONS_IN_DEPOSIT_TABLE)).click();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_ACTIONS.get(1))).click();
        changeStockQuantityNormalProduct(stockQuantity);
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
        logger.info("Wait Product created successfully! popup show and close it.");
        return this;
    }
}
