package pages.dashboard.products.all_products;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.all_products.conversion_unit.ConversionUnitPage;
import pages.dashboard.products.all_products.wholesale_price.WholesaleProductPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static api.dashboard.marketing.LoyaltyProgram.apiMembershipStatus;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.apiActiveBranches;
import static api.dashboard.setting.BranchManagement.apiBranchName;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static pages.dashboard.products.all_products.wholesale_price.WholesaleProductPage.*;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;
import static utilities.page_loaded_text.PageLoadedText.DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_ENG;
import static utilities.page_loaded_text.PageLoadedText.DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_VIE;

public class ProductPage extends ProductPageElement {
    WebDriverWait wait;
    Actions act;
    UICommonAction commonAction;
    String CREATE_PRODUCT_PATH = "/product/create";
    public static boolean isCreateByUI;

    Logger logger = LogManager.getLogger(ProductPage.class);

    public ProductPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        act = new Actions(driver);
        commonAction = new UICommonAction(driver);
    }

    public static String uiProductName;
    public static String uiProductDescription;
    public static List<Integer> uiProductListingPrice;
    public static List<Integer> uiProductSellingPrice;
    public static Map<String, List<String>> uiVariationMap;
    public static List<String> uiVariationList;
    public static Map<String, List<Integer>> uiProductStockQuantity;
    public static List<String> uiBranchName;
    public static int countFail = 0;

    public static boolean uiIsDisplayOutOfStock = true;
    public static boolean uiIsHideStock = false;
    //    public static boolean uiIsEnableListing = false;
    public static boolean uiIsShowOnApp = true;
    public static boolean uiIsShowOnWeb = true;
    public static boolean uiIsShowInStore = true;
    public static boolean uiIsShowInGoSocial = true;
    public static boolean uiIsIMEIProduct;
    public static Integer uiProductID;
    public static boolean uiIsVariation;
    public static String uiCollectionName;
    public static String language;

    public ProductPage clickPrintBarcode() {
        if (commonAction.isElementVisiblyDisabled(PRINT_BARCODE_BTN.findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(PRINT_BARCODE_BTN);
            return this;
        }
        commonAction.clickElement(PRINT_BARCODE_BTN);
        logger.info("Clicked on 'Print Barcode' button.");
        return this;
    }

    public ProductPage clickAddVariation() {
        if (commonAction.isElementVisiblyDisabled(ADD_VARIATION_BTN.findElement(By.xpath("./ancestor::div[@class='uik-widget__wrapper gs-widget gs-widget ']/parent::*")))) {
            new HomePage(driver).isMenuClicked(ADD_VARIATION_BTN);
            return this;
        }
        commonAction.clickElement(ADD_VARIATION_BTN);
        logger.info("Clicked on 'Add Deposit' button.");
        return this;
    }

    public ProductPage clickAddDepositBtn() {
        if (commonAction.isElementVisiblyDisabled(ADD_DEPOSIT_BTN.findElement(By.xpath("./ancestor::div[@class='uik-widget__wrapper gs-widget gs-widget ']/parent::*")))) {
            new HomePage(driver).isMenuClicked(ADD_DEPOSIT_BTN);
            return this;
        }
        commonAction.clickElement(ADD_DEPOSIT_BTN);
        logger.info("Clicked on 'Add Deposit' button.");
        return this;
    }

    public ProductPage inputSEOTitle(String seoTitle) {
        if (commonAction.isElementVisiblyDisabled(SEO_TITLE.findElement(By.xpath("./ancestor::div[contains(@class,'gs-widget  seo-editor')]/descendant::*[1]")))) {
            Assert.assertFalse(new HomePage(driver).isMenuClicked(SEO_TITLE));
            return this;
        }
        commonAction.inputText(SEO_TITLE, seoTitle);
        logger.info("Input '" + seoTitle + "' into SEO Title field.");
        return this;
    }

    public String getSEOTitle() {
        String title = commonAction.getElementAttribute(SEO_TITLE, "value");
        logger.info("Retrieved SEO Title: %s".formatted(title));
        return title;
    }

    public boolean isPrintBarcodeDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.isElementNotDisplay(driver.findElements(PRINT_BARCODE_MODAL));
    }

    public boolean isDeleteVariationBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return !commonAction.isElementNotDisplay(DELETE_VARIATION_BTN);
    }

    public boolean isDeleteDepositBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return !commonAction.isElementNotDisplay(DELETE_DEPOSIT_BTN);
    }

    public void clickOnTheCreateProductBtn() {
        // click create product button
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PRODUCT_BTN)).click();
        // log
        logger.info("Click on the Create Product button");
        // wait create product page loaded
        new UICommonAction(driver).verifyPageLoaded(DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_VIE, DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_ENG);
    }

    public ProductPage navigateToCreateProductPage() throws Exception {
        // access to create product page by URL
        driver.get("%s%s".formatted(DOMAIN, CREATE_PRODUCT_PATH));

        // wait page loaded
        commonAction.verifyPageLoaded("Chọn kênh bán hàng", "Select sale channel");

        // hide Facebook bubble
        commonAction.hideElement(driver.findElement(By.cssSelector("#fb-root")));

        // get branch name list
        uiBranchName = new ArrayList<>();
        uiBranchName.addAll(apiBranchName);

        // check [UI] create product page
        checkUIProductInfo();

        return this;
    }

    void inputProductName(String productName) {
        // input product name
        PRODUCT_NAME.sendKeys("a");
        wait.until(ExpectedConditions.elementToBeClickable(UI_PRODUCT_NAME)).click();
        PRODUCT_NAME.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        PRODUCT_NAME.sendKeys(productName);
    }

    void inputProductDescription() {
        // input product description
        uiProductDescription = """
                What is Lorem Ipsum?
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                                
                Why do we use it?
                It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).""";
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).sendKeys(uiProductDescription);

    }

    void uploadProductImage(String... imageFile) {
        // upload product image
        for (String imgFile : imageFile) {
            PRODUCT_IMAGE.sendKeys(Paths.get("%s%s".formatted(System.getProperty("user.dir"),
                    "/src/main/resources/uploadfile/product_images/%s".formatted(imgFile).replace("/", File.separator))).toString());
        }
    }

    void selectVAT() {
        // open VAT dropdown
        wait.until(ExpectedConditions.elementToBeClickable(VAT_DROPDOWN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VAT_DROPDOWN);

        // wait list VAT visible
        commonAction.waitElementList(VAT_LIST, 1);

        // random VAT index
        int index = nextInt(VAT_LIST.size());

        // log
        logger.info("VAT: %s".formatted(VAT_LIST.get(index).getText()));

        // select VAT
        VAT_LIST.get(index).click();

    }

    void selectCollection() {
        // click on collection search box
        wait.until(ExpectedConditions.elementToBeClickable(COLLECTION_SEARCH_BOX)).click();
        commonAction.sleepInMiliSecond(500);

        // select collection if any
        if (LIST_MANUAL_COLLECTION.size() > 0) {
            // random collection index
            int index = nextInt(LIST_MANUAL_COLLECTION.size());

            // log
            uiCollectionName = LIST_MANUAL_COLLECTION.get(index).getText();
            logger.info("Collection: %s".formatted(uiCollectionName));

            // select collection
            LIST_MANUAL_COLLECTION.get(index).click();
        }

    }

    void inputWithoutVariationProductSKU() {
        String sku = "SKU" + Instant.now().toEpochMilli();
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_SKU_WITHOUT_VARIATION)).sendKeys(sku);
    }

    void setManageInventory(boolean isIMEIProduct) throws Exception {
        uiIsIMEIProduct = isIMEIProduct;
        // set manage inventory by product or IMEI/Serial number
        new Select(MANAGE_INVENTORY).selectByValue(isIMEIProduct ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

        // check [UI] after select manage inventory by IMEI/Serial Number
        if (isIMEIProduct) checkManageInventoryByIMEINotice();

        // log
        logger.info("Manage inventory by: %s".formatted(isIMEIProduct ? "IMEI/Serial Number" : "Product"));

    }

    void setSFDisplay() {
        // Display if out of stock
        if (SF_DISPLAY_SETTING.get(0).isSelected() != uiIsDisplayOutOfStock)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SF_DISPLAY_SETTING.get(0));

        // Hide remaining stock on online store
        if (SF_DISPLAY_SETTING.get(1).isSelected() != uiIsHideStock)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SF_DISPLAY_SETTING.get(1));

    }

    void setPriority(int priority) {
        // set product priority (1-100)
        act.moveToElement(PRIORITY_TEXT_BOX).doubleClick().sendKeys(String.valueOf(priority));
    }

    void setProductDimension() {
        // input product weight
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WEIGHT));
        act.moveToElement(PRODUCT_WEIGHT).doubleClick().sendKeys("100").build().perform();
        logger.info("Input weight: 100");

        // input product length
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LENGTH));
        act.moveToElement(PRODUCT_LENGTH).doubleClick().sendKeys("10").build().perform();
        logger.info("Input length: 10");

        // input product width
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WIDTH));
        act.moveToElement(PRODUCT_WIDTH).doubleClick().sendKeys("10").build().perform();
        logger.info("Input width: 10");

        // input product height
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_HEIGHT));
        act.moveToElement(PRODUCT_HEIGHT).doubleClick().sendKeys("10").build().perform();
        logger.info("Input height: 10");

    }

    void selectPlatform() {
        // App
        if (PLATFORM_APP.isSelected() != uiIsShowOnApp)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", PLATFORM_APP);

        // Web
        if (PLATFORM_WEB.isSelected() != uiIsShowOnWeb)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", PLATFORM_WEB);

        // In-store
        if (PLATFORM_IN_STORE.isSelected() != uiIsShowInStore)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", PLATFORM_IN_STORE);

        // GoSocial
        if (PLATFORM_GOSOCIAL.isSelected() != uiIsShowInGoSocial)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", PLATFORM_GOSOCIAL);

    }

    void inputSEO() {
        // time
        String epoch = String.valueOf(Instant.now().toEpochMilli());
        // SEO title
        String title = "Auto - SEO Title - " + epoch;
        wait.until(ExpectedConditions.elementToBeClickable(SEO_TITLE)).click();
        SEO_TITLE.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        SEO_TITLE.sendKeys(title);

        // SEO description
        String description = "Auto - SEO Description - " + epoch;
        wait.until(ExpectedConditions.elementToBeClickable(SEO_DESCRIPTION)).click();
        SEO_DESCRIPTION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        SEO_DESCRIPTION.sendKeys(description);

        // SEO keyword
        String keyword = "Auto - SEO Keyword - " + epoch;
        wait.until(ExpectedConditions.elementToBeClickable(SEO_KEYWORD)).click();
        SEO_KEYWORD.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        SEO_KEYWORD.sendKeys(keyword);

        // SEO URL
//        act.moveToElement(SEO_URL).click().build().perform();
//        act.sendKeys(Keys.CONTROL + "a" + Keys.DELETE).build().perform();
//        wait.until(ExpectedConditions.elementToBeClickable(SEO_URL)).sendKeys(epoch);
    }

    void productInfo(String productName, boolean isIMEIProduct) throws Exception {
        inputProductName(productName);
        inputProductDescription();
        uploadProductImage("img.jpg");
        selectVAT();
        selectCollection();
        setManageInventory(isIMEIProduct);
        setSFDisplay();
        setPriority(nextInt(100) + 1);
        setProductDimension();
        selectPlatform();
        inputSEO();
    }

    // Without variation product
    public void inputWithoutVariationPrice() {
        // get listing price
        uiProductListingPrice = new ArrayList<>();
        uiProductListingPrice.add((int) (Math.random() * MAX_PRICE));
        System.out.println(uiProductListingPrice);

        // get selling price
        uiProductSellingPrice = new ArrayList<>();
        uiProductSellingPrice.add((int) (Math.random() * uiProductListingPrice.get(0)));
        System.out.println(uiProductSellingPrice);


        // input listing price
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LISTING_PRICE_WITHOUT_VARIATION));
        PRODUCT_LISTING_PRICE_WITHOUT_VARIATION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        PRODUCT_LISTING_PRICE_WITHOUT_VARIATION.sendKeys(String.valueOf(uiProductListingPrice.get(0)));
//        act.moveToElement(PRODUCT_LISTING_PRICE_WITHOUT_VARIATION).doubleClick().sendKeys(String.valueOf(uiProductListingPrice.get(0))).build().perform();

        // input selling price
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_SELLING_PRICE_WITHOUT_VARIATION));
        PRODUCT_SELLING_PRICE_WITHOUT_VARIATION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        PRODUCT_SELLING_PRICE_WITHOUT_VARIATION.sendKeys(String.valueOf(uiProductSellingPrice.get(0)));
//        act.moveToElement(PRODUCT_SELLING_PRICE_WITHOUT_VARIATION).doubleClick().sendKeys(String.valueOf(uiProductSellingPrice.get(0))).build().perform();

    }

    void addIMEIForEachBranch(String variationValue, List<Integer> branchStock) throws Exception {
        // select all branches
        wait.until(ExpectedConditions.elementToBeClickable(ADD_IMEI_POPUP_BRANCH_DROPDOWN)).click();
        if (!ADD_IMEI_POPUP_BRANCH_DROPDOWN_SELECT_ALL.isSelected())
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", ADD_IMEI_POPUP_BRANCH_DROPDOWN_SELECT_ALL);
        else ADD_IMEI_POPUP_BRANCH_DROPDOWN.click();

        // check [UI] add IMEI popup
        checkAddIMEIPopup();

        // input IMEI/Serial number for each branch
        for (int brIndex = 0; brIndex < apiActiveBranches.size(); brIndex++) {
            int brStockIndex = uiBranchName.indexOf(apiActiveBranches.get(brIndex));
            for (int i = 0; i < branchStock.get(brStockIndex); i++) {
                act.moveToElement(ADD_IMEI_POPUP_IMEI_TEXT_BOX.get(brIndex)).click().sendKeys("%s%s_IMEI_%s\n".formatted(variationValue != null ? "%s_".formatted(variationValue) : "", apiActiveBranches.get(brIndex), i)).build().perform();
            }
        }

        // save IMEI/Serial number
        wait.until(ExpectedConditions.elementToBeClickable(ADD_IMEI_POPUP_SAVE_BTN)).click();
    }

    public void inputWithoutVariationStock(int... branchStockQuantity) throws Exception {
        /* get without variation stock information */
        // get variation list
        uiVariationList = new ArrayList<>();
        uiVariationList.add(null);

        // get product stock quantity
        uiProductStockQuantity = new HashMap<>();
        uiProductStockQuantity.put(null, IntStream.range(0, uiBranchName.size()).mapToObj(i -> (branchStockQuantity.length > i) ? (apiActiveBranches.contains(uiBranchName.get(i)) ? branchStockQuantity[i] : 0) : 0).toList());

        /* input stock for each branch */
        if (uiIsIMEIProduct) {
            // handle StaleElementReferenceException exception
            try {
                // open Add IMEI/Serial number popup
                commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(0).click();
            } catch (StaleElementReferenceException ex) {
                // log error
                logger.info(ex);

                // open Add IMEI/Serial number popup again
                commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(0).click();
            }

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch(null, uiProductStockQuantity.get(null));
        } else {
            // handle StaleElementReference exception
            for (int i = 0; i < apiActiveBranches.size(); i++) {
                // clear old stock value
                try {
                    // clear old stock value
                    commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(i).clear();
                } catch (StaleElementReferenceException ex) {
                    // log error
                    logger.info(ex);

                    // clear old stock value again
                    commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(i).clear();
                }

                // handle StaleElementReference exception
                try {
                    // input stock
                    commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(i).sendKeys(String.valueOf(uiProductStockQuantity.get(null).get(i)));
                } catch (StaleElementReferenceException | ElementNotInteractableException ex) {
                    // log error
                    logger.info(ex);

                    // input stock again
                    // handle NotInteractable exception
                    try {
                        commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(i).sendKeys(String.valueOf(uiProductStockQuantity.get(null).get(i)));
                    } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                        // log error
                        logger.info(e);

                        // input stock again
                        commonAction.refreshListElement(LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT).get(i).sendKeys(String.valueOf(uiProductStockQuantity.get(null).get(i)));
                    }
                }
            }
        }

    }

    public ProductPage createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) throws Exception {
        isCreateByUI = true;
        uiIsVariation = false;

        // product name
        uiProductName = isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - ");
        uiProductName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(uiProductName, isIMEIProduct);
        inputWithoutVariationPrice();
        inputWithoutVariationStock(branchStock);
        inputWithoutVariationProductSKU();
        completeCreateProduct();
        initDiscountInformation();

        return this;
    }

    // Variation product
    public void addVariations() throws Exception {
        // generate variation map
        uiVariationMap = new DataGenerator().randomVariationMap();
        logger.info("Variation map: %s".formatted(uiVariationMap));

        // get variation list from variation map
        List<List<String>> varList = new ArrayList<>(uiVariationMap.values());
        uiVariationList = varList.get(0);
        if (varList.size() > 1)
            IntStream.range(1, varList.size())
                    .forEachOrdered(i -> uiVariationList = new DataGenerator()
                            .mixVariationValue(uiVariationList, varList.get(i)));
        logger.info("Variation list: %s".formatted(uiVariationList));

        // input variation name and variation value
        for (int varID = 0; varID < uiVariationMap.keySet().size(); varID++) {
            String varName = uiVariationMap.keySet().stream().toList().get(varID);
            logger.info("variation name: %s".formatted(varName));
            // click add variation button
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();

            // check [UI] after click Add variation button
            checkVariationInformation();
            checkVariationValuePlaceholder();

            // input variation name
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_NAME.get(varID))).sendKeys(varName);

            // input variation value
            for (String varValue : uiVariationMap.get(varName)) {
                act.moveToElement(VARIATION_VALUE.get(varID)).click().sendKeys("%s\n".formatted(varValue)).build().perform();
                logger.info("variation value: %s".formatted(varValue));
            }
        }

        // check [UI] after add variation (Check variation table column)
        checkVariationTableColumn();
    }


    void inputVariationPrice() throws Exception {
        // get listing, selling price
        uiProductListingPrice = new ArrayList<>();
        uiProductSellingPrice = new ArrayList<>();
        for (int i = 0; i < uiVariationList.size(); i++) {
            uiProductListingPrice.add((int) (Math.random() * MAX_PRICE));
            uiProductSellingPrice.add((int) (Math.random() * uiProductListingPrice.get(i)));
        }

        // select all variation
        if (!VARIATION_TABLE_SELECT_ALL_CHECKBOX.isSelected())
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_TABLE_SELECT_ALL_CHECKBOX);

        // check [UI] after select all variations
        checkBulkActionsOnVariationTable();

        // open list action dropdown
        wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE_SELECT_ACTION)).click();

        // check [UI] check list actions
        checkListActionsOnVariationTable();

        // open Update price popup
        commonAction.refreshListElement(VARIATION_TABLE_LIST_ACTION).get(0).click();

        // wait Update price popup visible
        wait.until(ExpectedConditions.visibilityOf(POPUP));

        // check [UI] product price table
        checkUpdatePricePopup();

        // input product price
        for (int i = 0; i < uiVariationList.size(); i++) {
            act.moveToElement(commonAction.refreshListElement(UPDATE_PRICE_POPUP_LISTING_PRICE).get(i)).doubleClick().sendKeys(String.valueOf(uiProductListingPrice.get(i))).build().perform();
            act.moveToElement(commonAction.refreshListElement(UPDATE_PRICE_POPUP_SELLING_PRICE).get(i)).doubleClick().sendKeys(String.valueOf(uiProductSellingPrice.get(i))).build().perform();
        }

        // close Update price popup
        wait.until(ExpectedConditions.elementToBeClickable(POPUP_UPDATE_BTN)).click();
    }

    void inputVariationStock(int increaseNum, int... branchStockQuantity) throws Exception {
        // get product stock quantity
        uiProductStockQuantity = new HashMap<>();
        for (int i = 0; i < uiVariationList.size(); i++) {
            List<Integer> variationStock = new ArrayList<>();
            // set branch stock
            for (int branchIndex = 0; branchIndex < uiBranchName.size(); branchIndex++) {
                variationStock.add((branchStockQuantity.length > branchIndex) ? ((apiActiveBranches.contains(uiBranchName.get(branchIndex)) ? (branchStockQuantity[branchIndex] + (i * increaseNum)) : 0)) : 0);
            }
            uiProductStockQuantity.put(uiVariationList.get(i), variationStock);
        }

        // input product price
        for (int i = 0; i < uiVariationList.size(); i++) {
            // open Update stock popup
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_TABLE_STOCK_QUANTITY.get(i));

            // wait Update stock popup visible
            wait.until(ExpectedConditions.visibilityOf(POPUP));

            if (uiIsIMEIProduct)
                addIMEIForEachBranch(uiVariationList.get(i), uiProductStockQuantity.get(uiVariationList.get(i)));
            else {
                // check [UI] update stock popup
                if (i == 0) checkUpdateNormalVariationStockPopup();

                // switch to change stock tab
                wait.until(ExpectedConditions.elementToBeClickable(UPDATE_STOCK_POPUP_CHANGE_TAB)).click();

                // input stock quantity to visible stock input field
                wait.until(ExpectedConditions.elementToBeClickable(UPDATE_STOCK_POPUP_INPUT_STOCK)).sendKeys(String.valueOf(Collections.max(uiProductStockQuantity.get(uiVariationList.get(i))) + 1));

                commonAction.sleepInMiliSecond(1000);

                // input stock for each branch
                for (int brIndex = apiActiveBranches.size() - 1; brIndex >= 0; brIndex--) {
                    int brStockIndex = uiBranchName.indexOf(apiActiveBranches.get(brIndex));
                    act.doubleClick(commonAction.refreshListElement(UPDATE_STOCK_POPUP_NORMAL_VARIATION_STOCK).get(brIndex)).build().perform();
                    commonAction.sleepInMiliSecond(200);

                    act.sendKeys(String.valueOf(uiProductStockQuantity.get(uiVariationList.get(i)).get(brStockIndex))).build().perform();
                }
            }

            // close Update stock popup
            wait.until(ExpectedConditions.elementToBeClickable(POPUP_UPDATE_BTN)).click();
        }
    }

    void inputVariationSKU() throws Exception {
        // get current epoch time
        String epoch = String.valueOf(Instant.now().toEpochMilli());

        // input SKU
        for (int i = 0; i < uiVariationList.size(); i++) {
            // open Update SKU popup
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_TABLE_SKU.get(i));

            // wait Update SKU popup visible
            wait.until(ExpectedConditions.visibilityOf(POPUP));
            commonAction.sleepInMiliSecond(1000);

            // check [UI] SKU popup
            if (i == 0) checkUpdateSKUPopup();

            // input SKU for each branch
            for (int brIndex = 0; brIndex < apiActiveBranches.size(); brIndex++) {
                act.doubleClick(commonAction.refreshListElement(UPDATE_SKU_POPUP_SKU_TEXT_BOX).get(brIndex)).build().perform();
                commonAction.sleepInMiliSecond(200);

                act.sendKeys("SKU_%s_%s_%s".formatted(uiVariationList.get(i), apiActiveBranches.get(brIndex), epoch)).build().perform();
            }

            // close Update SKU popup
            wait.until(ExpectedConditions.elementToBeClickable(POPUP_UPDATE_BTN)).click();
        }
    }

    void uploadVariationImage(String... imageFile) throws Exception {
        // upload image for each variation
        for (int i = 0; i < uiVariationList.size(); i++) {
            // open Update SKU popup
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_TABLE_IMAGE.get(i));

            // wait Update SKU popup visible
            wait.until(ExpectedConditions.visibilityOf(POPUP));
            commonAction.sleepInMiliSecond(1000);

            // check [UI] update image popup
            if (i == 0) checkUpdateVariationImagePopup();

            // upload image
            for (String imgFile : imageFile) {
                UPDATE_IMAGE_POPUP_UPLOAD_BTN.sendKeys(Paths.get("%s%s".formatted(System.getProperty("user.dir"),
                        "/src/main/resources/uploadfile/product_images/%s".formatted(imgFile).replace("/", File.separator))).toString());
            }

            // close Update image popup
            wait.until(ExpectedConditions.elementToBeClickable(POPUP_UPDATE_BTN)).click();
        }
    }

    public ProductPage createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) throws Exception {
        isCreateByUI = true;
        uiIsVariation = true;

        // product name
        uiProductName = isIMEIProduct ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - ");
        uiProductName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(uiProductName, isIMEIProduct);
        addVariations();
        uploadVariationImage("img.jpg");
        inputVariationPrice();
        inputVariationStock(increaseNum, branchStock);
        inputVariationSKU();
        completeCreateProduct();
        initDiscountInformation();

        return this;
    }

    /* Complete create/update product */
    void saveChange() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.visibilityOf(POPUP));
        wait.until(ExpectedConditions.elementToBeClickable(NOTIFICATION_POPUP_CLOSE_BTN)).click();
    }

    void completeCreateProduct() {
        saveChange();

        // wait product list page is loaded
        wait.until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains("/product/list");
        });

        // search product by name
        wait.until(ExpectedConditions.visibilityOf(SEARCH_BOX)).sendKeys(uiProductName);

        // wait api return result
        commonAction.sleepInMiliSecond(1000);

        // wait api return list product
        uiProductID = Integer.valueOf(wait.until(ExpectedConditions.visibilityOf(PRODUCT_ID)).getText());

        // log
        logger.info("Product id: %s".formatted(uiProductID));
    }

    public void completeUpdateProduct() {
        saveChange();
    }

    void initDiscountInformation() {
        // init wholesale product status
        uiWholesaleProductStatus = new HashMap<>();
        uiBranchName.forEach(brName -> uiWholesaleProductStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> false).toList()));

        // init flash sale status
        apiFlashSaleStatus = new HashMap<>();
        uiBranchName.forEach(brName -> apiFlashSaleStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init discount campaign status
        apiDiscountCampaignStatus = new HashMap<>();
        uiBranchName.forEach(brName -> apiDiscountCampaignStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init flash sale price
        apiFlashSalePrice = new ArrayList<>();
        apiFlashSalePrice.addAll(uiProductSellingPrice);

        // init flash sale stock
        apiFlashSaleStock = new ArrayList<>();
        uiVariationList.forEach(varName -> apiFlashSaleStock.add(Collections.max(uiProductStockQuantity.get(varName))));

        // init product discount campaign price
        apiDiscountCampaignPrice = new ArrayList<>();
        apiDiscountCampaignPrice.addAll(uiProductSellingPrice);

        // init wholesale product price, rate and stock
        uiWholesaleProductPrice = new ArrayList<>();
        uiWholesaleProductPrice.addAll(uiProductSellingPrice);

        uiWholesaleProductRate = new ArrayList<>();
        IntStream.range(0, uiWholesaleProductPrice.size()).forEach(i -> uiWholesaleProductRate.add(Float.valueOf(new DecimalFormat("#.##").format((1 - (float) uiWholesaleProductPrice.get(i) / uiProductSellingPrice.get(i)) * 100))));

        uiWholesaleProductStock = new ArrayList<>();
        uiVariationList.forEach(varName -> uiWholesaleProductStock.add(Collections.max(uiProductStockQuantity.get(varName))));

        // discount code
        apiDiscountCodeStatus = new HashMap<>();
        uiBranchName.forEach(brName -> apiDiscountCodeStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // membership
        apiMembershipStatus = new HashMap<>();
        uiBranchName.forEach(brName -> apiMembershipStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));
    }

    public void configWholesaleProduct() throws Exception {
        if (uiIsVariation) new WholesaleProductPage(driver)
                .navigateToWholesaleProductPage()
                .getWholesaleProductInfo()
                .addWholesaleProductVariation();
        else new WholesaleProductPage(driver)
                .navigateToWholesaleProductPage()
                .getWholesaleProductInfo()
                .addWholesaleProductWithoutVariation();
    }

    public ProductPage configConversionUnit() throws Exception {
        if (uiIsVariation) new ConversionUnitPage(driver)
                .navigateToConversionUnitPage()
                .addConversionUnitVariation();
        else new ConversionUnitPage(driver)
                .navigateToConversionUnitPage()
                .addConversionUnitWithoutVariation();
        return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToPrintBarCode(String permission) {
        clickPrintBarcode();
        boolean flag = isPrintBarcodeDialogDisplayed();
        commonAction.navigateBack();
        new HomePage(driver).waitTillSpinnerDisappear1();
        if (permission.contentEquals("A")) {
            Assert.assertTrue(flag);
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(flag);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToCreateProduct(String permission, String url) {
        clickOnTheCreateProductBtn();
        new HomePage(driver).waitTillSpinnerDisappear1();
        String currentURL = commonAction.getCurrentURL();
        commonAction.navigateBack();
        new HomePage(driver).waitTillSpinnerDisappear1();
        if (permission.contentEquals("A")) {
            Assert.assertTrue(currentURL.contains(url));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToCreateVariationProduct(String permission) {
        clickOnTheCreateProductBtn();
        clickAddVariation();

        boolean flag = isDeleteVariationBtnDisplayed();
        commonAction.refreshPage();
        commonAction.navigateBack();

        if (permission.contentEquals("A")) {
            Assert.assertTrue(flag);
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(flag);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToCreateDepositProduct(String permission) {
        clickOnTheCreateProductBtn();
        clickAddDepositBtn();
        boolean flag = isDeleteDepositBtnDisplayed();
        commonAction.refreshPage();
        commonAction.navigateBack();

        if (permission.contentEquals("A")) {
            Assert.assertTrue(flag);
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(flag);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToCreateProductSEO(String permission) {
        clickOnTheCreateProductBtn();
        inputSEOTitle("Test SEO");
        String flag = getSEOTitle();
        commonAction.refreshPage();
        commonAction.navigateBack();

        if (permission.contentEquals("A")) {
            Assert.assertEquals(flag, "Test SEO");
        } else if (permission.contentEquals("D")) {
            Assert.assertEquals(flag, "");
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    /* check UI function */
    void checkUIHeaderCreateProductPage() throws Exception {
        // check Go back to product list link text
        String dbGoBackToProductList = wait.until(ExpectedConditions.visibilityOf(UI_HEADER_GO_BACK_TO_PRODUCT_LIST)).getText();
        String ppGoBackToProductList = getPropertiesValueByDBLang("products.allProducts.createProduct.header.goBackToProductList", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoBackToProductList, ppGoBackToProductList, "[Failed][Header] Go back to product list link text should be %s, but found %s.".formatted(ppGoBackToProductList, dbGoBackToProductList));
        logger.info("[UI][%s] Check Header - Go back to product list.".formatted(language));

        // check header page title
        String dbHeaderPageTitle = wait.until(ExpectedConditions.visibilityOf(UI_HEADER_PAGE_TITLE)).getText();
        String ppHeaderPageTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.header.headerPageTitle", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbHeaderPageTitle, ppHeaderPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppHeaderPageTitle, dbHeaderPageTitle));
        logger.info("[UI][%s] Check Header - Header page title.".formatted(language));

        // check header Save button
        String dbHeaderSaveBtn = wait.until(ExpectedConditions.visibilityOf(UI_HEADER_SAVE_BTN)).getText();
        String ppHeaderSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbHeaderPageTitle, ppHeaderPageTitle, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppHeaderSaveBtn, dbHeaderSaveBtn));
        logger.info("[UI][%s] Check Header - Save button .".formatted(language));

        // check header Cancel button
        String dbHeaderCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_HEADER_CANCEL_BTN)).getText();
        String ppHeaderCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbHeaderPageTitle, ppHeaderPageTitle, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppHeaderCancelBtn, dbHeaderCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));
    }

    void checkUIProductInformation() throws Exception {
        // check product information
        String dbProductInformation = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_INFORMATION)).getText();
        String ppProductInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductInformation, ppProductInformation, "[Failed][Body] Product information should be %s, but found %s.".formatted(ppProductInformation, dbProductInformation));
        logger.info("[UI][%s] Check Body - Product Information.".formatted(language));

        // check product name
        String dbProductName = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_NAME)).getText();
        String ppProductName = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.productName", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductName, ppProductName, "[Failed][Body] Product name should be %s, but found %s.".formatted(ppProductName, dbProductName));
        logger.info("[UI][%s] Check Body - Product name.".formatted(language));

        // check product name error
        inputProductName("");
        String dbProductNameError = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_NAME_BLANK_ERROR)).getText();
        String ppProductNameError = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.productNameError", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductNameError, ppProductNameError, "[Failed][Body] Product name error should be %s, but found %s.".formatted(ppProductNameError, dbProductNameError));
        logger.info("[UI][%s] Check Body - Product name error.".formatted(language));

        // check product description
        String dbProductDescription = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_DESCRIPTION)).getText();
        String ppProductDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.description", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductDescription, ppProductDescription, "[Failed][Body] Product description should be %s, but found %s.".formatted(ppProductDescription, dbProductDescription));
        logger.info("[UI][%s] Check Body - Product description.".formatted(language));
    }

    void checkUIImages() throws Exception {
        // check images title
        String dbImages = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_IMAGE)).getText();
        String ppImages = getPropertiesValueByDBLang("products.allProducts.createProduct.images.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbImages, ppImages, "[Failed][Body] Images should be %s, but found %s.".formatted(ppImages, dbImages));
        logger.info("[UI][%s] Check Body - Images.".formatted(language));

        // check images drag and drop placeholder
        String dbImagesDragAndDrop = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_IMAGE_DRAG_AND_DROP_CONTENT)).getText();
        String ppImagesDragAndDrop = getPropertiesValueByDBLang("products.allProducts.createProduct.images.dragAndDrop", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, Objects.equals(dbImagesDragAndDrop, ppImagesDragAndDrop), "[Failed][Body] Images drag and drop placeholder should be %s, but found %s.".formatted(ppImagesDragAndDrop, dbImagesDragAndDrop));
        logger.info("[UI][%s] Check Body - Images drag and drop placeholder.".formatted(language));
    }

    void checkUIPricing() throws Exception {
        // check pricing title
        String dbProductPriceTitle = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_PRICE)).getText();
        String ppProductPriceTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductPriceTitle, ppProductPriceTitle, "[Failed][Body] Product price title should be %s, but found %s.".formatted(ppProductPriceTitle, dbProductPriceTitle));
        logger.info("[UI][%s] Check Body - Product price title.".formatted(language));

        // check product price for without variation product
        if (UI_PRODUCT_LISTING_PRICE.isDisplayed()) {
            // check product listing price
            String dbProductListingPrice = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_LISTING_PRICE)).getText();
            String ppProductListingPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.listingPrice", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductListingPrice, ppProductListingPrice, "[Failed][Body] Product listing price should be %s, but found %s.".formatted(ppProductListingPrice, dbProductListingPrice));
            logger.info("[UI][%s] Check Body - Product listing price.".formatted(language));

            // check product selling price
            String dbProductSellingPrice = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_SELLING_PRICE)).getText();
            String ppProductSellingPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.sellingPrice", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductSellingPrice, ppProductSellingPrice, "[Failed][Body] Product selling price should be %s, but found %s.".formatted(ppProductSellingPrice, dbProductSellingPrice));
            logger.info("[UI][%s] Check Body - Product selling price.".formatted(language));

            // check product cost price
            String dbProductCostPrice = wait.until(ExpectedConditions.visibilityOf(UI_PRODUCT_COST_PRICE)).getText();
            String ppProductCostPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.costPrice", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductCostPrice, ppProductCostPrice, "[Failed][Body] Product cost price should be %s, but found %s.".formatted(ppProductCostPrice, dbProductCostPrice));
            logger.info("[UI][%s] Check Body - Product cost price.".formatted(language));
        }

        // check VAT title
        String dbVATTitle = wait.until(ExpectedConditions.visibilityOf(UI_VAT)).getText();
        String ppVATTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.vat", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVATTitle, ppVATTitle, "[Failed][Body] VAT title should be %s, but found %s.".formatted(ppVATTitle, dbVATTitle));
        logger.info("[UI][%s] Check Body - VAT title.".formatted(language));

        // check VAT default value
        // open VAT dropdown
        VAT_DROPDOWN.click();
        String dbTaxDoesNotApply = wait.until(ExpectedConditions.visibilityOf(UI_TAX_DOES_NOT_APPLY)).getText();
        String ppTaxDoesNotApply = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.taxDoesNotApply", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTaxDoesNotApply, ppTaxDoesNotApply, "[Failed][Body] Tax does not apply text should be %s, but found %s.".formatted(ppTaxDoesNotApply, dbTaxDoesNotApply));
        logger.info("[UI][%s] Check Body - Tax does not apply.".formatted(language));
        // close VAT dropdown
        VAT_DROPDOWN.click();

        // show as listing product on storefront checkbox
        String dbShowAsListingProduct = wait.until(ExpectedConditions.visibilityOf(UI_SHOW_AS_LISTING_PRODUCT_ON_STOREFRONT)).getText();
        String ppShowAsListingProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.showAsListingProductCheckbox", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbShowAsListingProduct, ppShowAsListingProduct, "[Failed][Body] Show as listing product on storefront label should be %s, but found %s.".formatted(ppShowAsListingProduct, dbShowAsListingProduct));
        logger.info("[UI][%s] Check Body - Show as listing product on storefront checkbox.".formatted(language));
    }

    void checkUIVariations() throws Exception {
        // check variations title
        String dbVariations = wait.until(ExpectedConditions.visibilityOf(UI_VARIATIONS)).getText();
        String ppVariations = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariations, ppVariations, "[Failed][Body] Variations title should be %s, but found %s.".formatted(ppVariations, dbVariations));
        logger.info("[UI][%s] Check Body - Variations title.".formatted(language));

        // check add variation button
        String dbAddVariationBtn = wait.until(ExpectedConditions.visibilityOf(UI_ADD_VARIATION_BTN)).getText();
        String ppAddVariationBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addVariationBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddVariationBtn, ppAddVariationBtn, "[Failed][Body] Add variation button should be %s, but found %s.".formatted(ppAddVariationBtn, dbAddVariationBtn));
        logger.info("[UI][%s] Check Body - Add variation button.".formatted(language));

        // check variation description
        String dbVariationDescription = wait.until(ExpectedConditions.visibilityOf(UI_VARIATION_DESCRIPTION)).getText();
        String ppVariationDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationDescription", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, dbVariationDescription.equals(ppVariationDescription), "[Failed][Body] Variation description should be %s, but found %s.".formatted(ppVariationDescription, dbVariationDescription));
        logger.info("[UI][%s] Check Body - Variation description.".formatted(language));
    }

    void checkVariationInformation() throws Exception {
        //check variation name
        String dbVariationName = wait.until(ExpectedConditions.visibilityOf(UI_VARIATION_NAME)).getText();
        String ppVariationName = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationName", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariationName, ppVariationName, "[Failed][Body] Variation name should be %s, but found %s.".formatted(ppVariationName, dbVariationName));
        logger.info("[UI][%s] Check Body - Variation name.".formatted(language));

        // check variation value
        String dbVariationValue = wait.until(ExpectedConditions.visibilityOf(UI_VARIATION_VALUE)).getText();
        String ppVariationValue = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationValue", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariationValue, ppVariationValue, "[Failed][Body] Variation value should be %s, but found %s.".formatted(ppVariationValue, dbVariationValue));
        logger.info("[UI][%s] Check Body - Variation value.".formatted(language));
    }

    void checkVariationValuePlaceholder() throws Exception {
        // check variation value placeholder
        String dbVariationValuePlaceholder = wait.until(ExpectedConditions.visibilityOf(UI_VARIATION_VALUE_PLACEHOLDER.get(0))).getText();
        String ppVariationValuePlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationValuePlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariationValuePlaceholder, ppVariationValuePlaceholder, "[Failed][Body] Variation value placeholder should be %s, but found %s.".formatted(ppVariationValuePlaceholder, dbVariationValuePlaceholder));
        logger.info("[UI][%s] Check Body - Variation value placeholder.".formatted(language));
    }

    void checkBulkActionsOnVariationTable() throws Exception {
        // check number of selected variation
        String[] dbNumberOfSelectedVariation = wait.until(ExpectedConditions.visibilityOf(UI_VARIATION_TABLE_NUMBER_OF_SELECTED_VARIATIONS)).getText().split("\n")[0].split("\\d+");
        String[] ppNumberOfSelectedVariation = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.numberOfSelectedVariations", language).split("\\d+");;
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNumberOfSelectedVariation, ppNumberOfSelectedVariation, "[Failed][Body][Variation] Number of selected variations should be %s, but found %s.".formatted(ppNumberOfSelectedVariation, dbNumberOfSelectedVariation));
        logger.info("[UI][%s] Check Body - Number of selected variations on variation table.".formatted(language));

        // check Select action button
        String dbSelectActionLinkText = wait.until(ExpectedConditions.visibilityOf(UI_VARIATION_TABLE_SELECT_ACTION_LINK_TEXT)).getText();
        String ppSelectActionLinkText = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.selectAction", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSelectActionLinkText, ppSelectActionLinkText, "[Failed][Body][Variation table] Select action link text should be %s, but found %s.".formatted(ppSelectActionLinkText, dbSelectActionLinkText));
        logger.info("[UI][%s] Check Body - Select action link text on variation table".formatted(language));
    }

    void checkListActionsOnVariationTable() throws Exception {
        // check list actions
        List<String> dbListActions = UI_VARIATION_TABLE_LIST_ACTIONS.stream().map(WebElement::getText).toList();
        List<String> ppListActions = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.2", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.3", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbListActions, ppListActions, "[Failed][Body][Variation table] List action should be %s, but found %s.".formatted(ppListActions, dbListActions));
        logger.info("[UI][%s] Check Body - List action on variation table.".formatted(language));
    }

    void checkVariationTableColumn() throws Exception {
        // check variation table column
        List<String> dbVariationTableImageColumn = UI_VARIATION_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppVariationTableImageColumn = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.2", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.3", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.4", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.5", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.6", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariationTableImageColumn, ppVariationTableImageColumn, "[Failed][Body] Variation table column should be %s, but found %s.".formatted(ppVariationTableImageColumn, dbVariationTableImageColumn));
        logger.info("[UI][%s] Check Body - Variation table column.".formatted(language));
    }

    void checkUpdatePricePopup() throws Exception {
        // check title
        String dbTitle = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_PRICE_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Update price popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update price popup - Title.".formatted(language));

        // check price list in dropdown
        // open price dropdown
        wait.until(ExpectedConditions.elementToBeClickable(UI_UPDATE_PRICE_POPUP_PRICE_DROPDOWN)).click();
        List<String> dbListPriceInDropdown = UI_UPDATE_PRICE_POPUP_LIST_PRICE_IN_DROPDOWN.stream().map(WebElement::getText).toList();
        List<String> ppListPriceInDropdown = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.2", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbListPriceInDropdown, ppListPriceInDropdown, "[Failed][Update price popup] List price in dropdown should be %s, but found %s.".formatted(ppListPriceInDropdown, dbListPriceInDropdown));
        logger.info("[UI][%s] Check Update price popup - List price in dropdown.".formatted(language));
        // close price dropdown
        UI_UPDATE_PRICE_POPUP_PRICE_DROPDOWN.click();

        // check apply all button
        String dbApplyAllBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_PRICE_POPUP_APPLY_ALL_BTN)).getText();
        String ppApplyAllBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.applyAllBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbApplyAllBtn, ppApplyAllBtn, "[Failed][Update price popup] Apply all button should be %s, but found %s.".formatted(ppApplyAllBtn, dbApplyAllBtn));
        logger.info("[UI][%s] Check Update price popup - Apply all button.".formatted(language));

        // check price list in table
        List<String> dbListPriceInTable = IntStream.iterate(3, i -> i >= 1, i -> i - 1).mapToObj(i -> UI_UPDATE_PRICE_POPUP_LIST_PRICE_COLUMN.get(UI_UPDATE_PRICE_POPUP_LIST_PRICE_COLUMN.size() - i).getText()).toList();
        List<String> ppListPriceInTable = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.2", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbListPriceInTable, ppListPriceInTable, "[Failed][Update price popup] List price in table should be %s, but found %s.".formatted(ppListPriceInTable, dbListPriceInTable));
        logger.info("[UI][%s] Check Update price popup - List price in table.".formatted(language));

        // check update button
        String dbUpdateBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_PRICE_POPUP_UPDATE_BTN)).getText();
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.updateBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbUpdateBtn, ppUpdateBtn, "[Failed][Update price popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update price popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_PRICE_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Update price popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update price popup - Cancel button.".formatted(language));
    }

    void checkUpdateNormalVariationStockPopup() throws Exception {
        // check title
        String dbTitle = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_STOCK_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Update normal variation stock popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update normal variation stock popup - Title.".formatted(language));

        // check selected branches
        String[] dbNumberOfSelectedBranches = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_STOCK_POPUP_BRANCH_DROPDOWN)).getText().split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.numberOfSelectedBranches", language).split("\\d+");
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Update normal variation stock popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Update normal variation stock popup - Number of selected branches.".formatted(language));

        // check list actions
        List<String> dbListActions = UI_UPDATE_STOCK_POPUP_LIST_ACTION.stream().map(WebElement::getText).toList();
        List<String> ppListActions = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.1", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbListActions, ppListActions, "[Failed][Update normal variation stock popup] List actions should be %s, but found %s.".formatted(ppListActions, dbListActions));
        logger.info("[UI][%s] Check Update normal variation stock popup - List actions.".formatted(language));

        // check input stock placeholder
        String dbInputStockPlaceholder = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_STOCK_POPUP_INPUT_STOCK_PLACEHOLDER)).getAttribute("placeholder");
        String ppInputStockPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.inputStockPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbInputStockPlaceholder, ppInputStockPlaceholder, "[Failed][Update normal variation stock popup] Input stock placeholder should be %s, but found %s.".formatted(ppInputStockPlaceholder, dbInputStockPlaceholder));
        logger.info("[UI][%s] Check Update normal variation stock popup - Input stock placeholder.".formatted(language));

        // check action type
        String dbActionType = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_STOCK_ACTION_TYPE)).getText().split(": ")[0];
        String ppActionType0 = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.0", language);
        String ppActionType1 = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.1", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, dbActionType.equals(ppActionType1) || dbActionType.equals(ppActionType0), "[Failed][Update normal variation popup] Action type should be %s or %s, but found %s.".formatted(ppActionType0, ppActionType1, dbActionType));
        logger.info("[UI][%s] Check Update normal variation popup - Action type.".formatted(language));

        // check update button
        String dbUpdateBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_STOCK_POPUP_UPDATE_BTN)).getText();
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.updateBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbUpdateBtn, ppUpdateBtn, "[Failed][Update normal variation popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update normal variation popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_STOCK_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Update normal variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update normal variation popup - Cancel button.".formatted(language));
    }

    void checkAddIMEIPopup() throws Exception {
        // check title
        String dbTitle = wait.until(ExpectedConditions.visibilityOf(UI_ADD_IMEI_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Add IMEI popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Add IMEI popup - Title.".formatted(language));

        // check branch title
        String dbBranchTitle = wait.until(ExpectedConditions.visibilityOf(UI_ADD_IMEI_POPUP_BRANCH)).getText().split("\n")[0];
        String ppBranchTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.branchLabel", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbBranchTitle, ppBranchTitle, "[Failed][Add IMEI popup] Branch title should be %s, but found %s.".formatted(ppBranchTitle, dbBranchTitle));
        logger.info("[UI][%s] Check Add IMEI popup - Branch title.".formatted(language));

        // check number of selected branches
        String[] dbNumberOfSelectedBranches = wait.until(ExpectedConditions.visibilityOf(UI_ADD_IMEI_POPUP_BRANCH_DROPDOWN)).getText().split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.numberOfSelectedBranches", language).split("\\d+");
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Add IMEI popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Add IMEI popup - Number of selected branches.".formatted(language));

        // check add IMEI placeholder
        for (WebElement element : UI_ADD_IMEI_POPUP_INPUT_IMEI_PLACEHOLDER) {
            String dbIMEIPlaceholder = element.getAttribute("placeholder");
            String ppIMEIPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.addIMEITextBoxPlaceholder", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbIMEIPlaceholder, ppIMEIPlaceholder, "[Failed][Add IMEI popup] Input IMEI placeholder should be %s, but found %s.".formatted(ppIMEIPlaceholder, dbIMEIPlaceholder));
            logger.info("[UI][%s] Check Add IMEI popup - Input IMEI placeholder.".formatted(language));
        }

        // check product name column
        String dbProductNameColumn = wait.until(ExpectedConditions.visibilityOf(UI_ADD_IMEI_POPUP_PRODUCT_NAME_COLUMN)).getText();
        String ppProductNameColumn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.productNameColumn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductNameColumn, ppProductNameColumn, "[Failed][Add IMEI popup] Product name column should be %s, but found %s.".formatted(ppProductNameColumn, dbProductNameColumn));
        logger.info("[UI][%s] Check Add IMEI popup - Product name column.".formatted(language));

        // check save button
        String dbSaveBtn = wait.until(ExpectedConditions.visibilityOf(UI_ADD_IMEI_POPUP_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Add IMEI popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Add IMEI popup - Save button.".formatted(language));

        // check cancel button
        String dbCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_ADD_IMEI_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Add IMEI popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Add IMEI popup - Cancel button.".formatted(language));
    }

    void checkUpdateSKUPopup() throws Exception {
        // check title
        String dbTitle = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_SKU_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Update SKU popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update SKU popup - Title.".formatted(language));

        // check number of selected branches
        String[] dbNumberOfSelectedBranches = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_SKU_POPUP_BRANCH_DROPDOWN)).getText().split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.numberOfSelectedBranches", language).split("\\d+");
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Update SKU popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Update SKU popup - Number of selected branches.".formatted(language));

        // check update button
        String dbUpdateBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_SKU_POPUP_UPDATE_BTN)).getText();
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.updateBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbUpdateBtn, ppUpdateBtn, "[Failed][Update SKU popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update SKU popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPDATE_SKU_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Update SKU popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update SKU popup - Cancel button.".formatted(language));
    }

    void checkUpdateVariationImagePopup() throws Exception {
        // check title
        String dbTitle = wait.until(ExpectedConditions.visibilityOf(UI_UPLOAD_IMAGE_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Upload image popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Upload image popup - Title.".formatted(language));

        // check upload image button placeholder
        String dbUploadImageBtnPlaceholder = wait.until(ExpectedConditions.visibilityOf(UI_UPLOAD_IMAGE_POPUP_UPLOAD_IMAGE_BTN_PLACEHOLDER)).getText();
        String ppUploadImageBtnPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.uploadBtnPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbUploadImageBtnPlaceholder, ppUploadImageBtnPlaceholder, "[Failed][Upload image popup] Upload image button placeholder should be %s, but found %s.".formatted(ppUploadImageBtnPlaceholder, dbUploadImageBtnPlaceholder));
        logger.info("[UI][%s] Check Upload image popup - Upload image button placeholder.".formatted(language));

        // check update button
        String dbUpdateBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPLOAD_IMAGE_POPUP_SELECT_BTN)).getText();
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.selectBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbUpdateBtn, ppUpdateBtn, "[Failed][Upload image popup] Select button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Upload image popup - Select button.".formatted(language));

        // check cancel button
        String dbCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_UPLOAD_IMAGE_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Upload image popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Upload image popup - Cancel button.".formatted(language));
    }

    void checkUIAddConversionUnit() throws Exception {
        // check conversion unit title
        String dbConversionUnit = wait.until(ExpectedConditions.visibilityOf(UI_UNIT)).getText();
        String ppConversionUnit = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnit, ppConversionUnit, "[Failed][Body] Conversion unit title should be %s, but found %s.".formatted(ppConversionUnit, dbConversionUnit));
        logger.info("[UI][%s] Check Body - Conversion unit title.".formatted(language));

        // check conversion unit search box placeholder
        String dbConversionUnitSearchBoxPlaceholder = wait.until(ExpectedConditions.visibilityOf(UI_CONVERSION_UNIT_SEARCH_BOX_PLACEHOLDER)).getAttribute("placeholder");
        String ppConversionUnitSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.searchBoxPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitSearchBoxPlaceholder, ppConversionUnitSearchBoxPlaceholder, "[Failed][Body] Conversion unit search box placeholder should be %s, but found %s.".formatted(ppConversionUnitSearchBoxPlaceholder, dbConversionUnitSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Conversion unit search box placeholder.".formatted(language));

        // check add conversion unit checkbox
        String dbAddConversionUnitCheckbox = wait.until(ExpectedConditions.visibilityOf(UI_ADD_CONVERSION_UNIT_LABEL)).getText();
        String ppAddConversionUnitCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.addConversionUnitCheckbox", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddConversionUnitCheckbox, ppAddConversionUnitCheckbox, "[Failed][Body] Add conversion unit checkbox label should be %s, but found %s.".formatted(ppAddConversionUnitCheckbox, dbAddConversionUnitCheckbox));
        logger.info("[UI][%s] Check Body - Add conversion unit checkbox.".formatted(language));

        // check conversion unit tooltips
        act.moveToElement(UI_CONVERSION_UNIT_TOOLTIPS).build().perform();
        String dbConversionUnitTooltips = wait.until(ExpectedConditions.visibilityOf(UI_CONVERSION_UNIT_TOOLTIPS)).getAttribute("data-original-title");
        String ppConversionUnitTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.tooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitTooltips, ppConversionUnitTooltips, "[Failed][Body] Conversion unit tooltips should be %s, but found %s.".formatted(ppConversionUnitTooltips, dbConversionUnitTooltips));
        logger.info("[UI][%s] Check Body - Conversion unit tooltips.".formatted(language));
    }

    void checkUIAddWholesaleProduct() throws Exception {
        // check add wholesale product checkbox
        String dbAddWholesaleProductCheckbox = wait.until(ExpectedConditions.visibilityOf(UI_ADD_WHOLESALE_PRICING_LABEL)).getText();
        String ppAddWholesaleProductCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.addWholesaleProductCheckbox", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddWholesaleProductCheckbox, ppAddWholesaleProductCheckbox, "[Failed][Body] Add wholesale product checkbox label should be %s, but found %s.".formatted(ppAddWholesaleProductCheckbox, dbAddWholesaleProductCheckbox));
        logger.info("[UI][%s] Check Body - Add wholesale product checkbox.".formatted(language));
    }

    void checkUIDeposit() throws Exception {
        // check deposit title
        String dbDeposit = wait.until(ExpectedConditions.visibilityOf(UI_DEPOSIT)).getText();
        String ppDeposit = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDeposit, ppDeposit, "[Failed][Body] Deposit title should be %s, but found %s.".formatted(ppDeposit, dbDeposit));
        logger.info("[UI][%s] Check Body - Deposit.".formatted(language));

        // check Add deposit button
        String dbAddDepositBtn = wait.until(ExpectedConditions.visibilityOf(UI_ADD_DEPOSIT_BTN)).getText();
        String ppAddDepositBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.addDepositBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddDepositBtn, ppAddDepositBtn, "[Failed][Body] Add deposit button should be %s, but found %s.".formatted(ppAddDepositBtn, dbAddDepositBtn));
        logger.info("[UI][%s] Check Body - Add deposit button.".formatted(language));

        // check deposit description
        String dbDepositDescription = wait.until(ExpectedConditions.visibilityOf(UI_DEPOSIT_DESCRIPTION)).getText();
        String ppDepositDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.depositDescription", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDepositDescription, ppDepositDescription, "[Failed][Body] Deposit description should be %s, but found %s.".formatted(ppDepositDescription, dbDepositDescription));
        logger.info("[UI][%s] Check Body - Deposit description.".formatted(language));
    }

    void checkUISEO() throws Exception {
        // check SEO setting
        String dbSEOSetting = wait.until(ExpectedConditions.visibilityOf(UI_SEO_SETTING)).getText();
        String ppSEOSetting = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEOSetting, ppSEOSetting, "[Failed][Body] SEO setting should be %s, but found %s.".formatted(ppSEOSetting, dbSEOSetting));
        logger.info("[UI][%s] Check Body - SEO setting.".formatted(language));

        // check SEO live preview
        String dbLivePreview = wait.until(ExpectedConditions.visibilityOf(UI_LIVE_PREVIEW)).getText();
        String ppLivePreview = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.livePreview", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbLivePreview, ppLivePreview, "[Failed][Body] Live preview should be %s, but found %s.".formatted(ppLivePreview, dbLivePreview));
        logger.info("[UI][%s] Check Body - Live preview.".formatted(language));

        // check SEO live preview tooltips
        act.moveToElement(UI_LIVE_PREVIEW_TOOLTIPS).build().perform();
        String dbLivePreviewTooltips = wait.until(ExpectedConditions.visibilityOf(UI_LIVE_PREVIEW_TOOLTIPS)).getAttribute("data-original-title");
        String ppLivePreviewTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.livePreviewTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbLivePreviewTooltips, ppLivePreviewTooltips, "[Failed][Body] Live preview tooltips should be %s, but found %s.".formatted(ppLivePreviewTooltips, dbLivePreviewTooltips));
        logger.info("[UI][%s] Check Body - Live preview tooltips.".formatted(language));

        // check SEO title
        String dbSEOTitle = wait.until(ExpectedConditions.visibilityOf(UI_SEO_TITLE)).getText();
        String ppSEOTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoTitle", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEOTitle, ppSEOTitle, "[Failed][Body] SEO Title should be %s, but found %s.".formatted(ppSEOTitle, dbSEOTitle));
        logger.info("[UI][%s] Check Body - SEO Title.".formatted(language));

        // check SEO title tooltips
        act.moveToElement(UI_SEO_TITLE_TOOLTIPS).build().perform();
        String dbSEOTitleTooltips = wait.until(ExpectedConditions.visibilityOf(UI_SEO_TITLE_TOOLTIPS)).getAttribute("data-original-title");
        String ppSEOTitleTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoTitleTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEOTitleTooltips, ppSEOTitleTooltips, "[Failed][Body] SEO Title tooltips should be %s, but found %s.".formatted(ppSEOTitleTooltips, dbSEOTitleTooltips));
        logger.info("[UI][%s] Check Body - SEO Title tooltips.".formatted(language));

        // check SEO description
        String dbSEODescription = wait.until(ExpectedConditions.visibilityOf(UI_SEO_DESCRIPTION)).getText();
        String ppSEODescription = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoDescription", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEODescription, ppSEODescription, "[Failed][Body] SEO description should be %s, but found %s.".formatted(ppSEODescription, dbSEODescription));
        logger.info("[UI][%s] Check Body - SEO description.".formatted(language));

        // check SEO description tooltips
        act.moveToElement(UI_SEO_DESCRIPTIONS_TOOLTIPS).build().perform();
        String dbSEODescriptionTooltips = wait.until(ExpectedConditions.visibilityOf(UI_SEO_DESCRIPTIONS_TOOLTIPS)).getAttribute("data-original-title");
        String ppSEODescriptionTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoDescriptionTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEODescriptionTooltips, ppSEODescriptionTooltips, "[Failed][Body] SEO descriptions tooltips should be %s, but found %s.".formatted(ppSEODescriptionTooltips, dbSEODescriptionTooltips));
        logger.info("[UI][%s] Check Body - SEO description tooltips.".formatted(language));

        // check SEO keywords
        String dbSEOKeywords = wait.until(ExpectedConditions.visibilityOf(UI_SEO_KEYWORDS)).getText();
        String ppSEOKeywords = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoKeywords", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEOKeywords, ppSEOKeywords, "[Failed][Body] SEO keywords should be %s, but found %s.".formatted(ppSEOKeywords, dbSEOKeywords));
        logger.info("[UI][%s] Check Body - SEO keywords.".formatted(language));

        // check SEO keywords tooltips
        act.moveToElement(UI_SEO_KEYWORDS_TOOLTIPS).build().perform();
        String dbSEOKeywordsTooltips = wait.until(ExpectedConditions.visibilityOf(UI_SEO_KEYWORDS_TOOLTIPS)).getAttribute("data-original-title");
        String ppSEOKeywordsTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoKeywordsTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEOKeywordsTooltips, ppSEOKeywordsTooltips, "[Failed][Body] SEO keywords tooltips should be %s, but found %s.".formatted(ppSEOKeywordsTooltips, dbSEOKeywordsTooltips));
        logger.info("[UI][%s] Check Body - SEO keywords tooltips.".formatted(language));

        // check SEO URL link
        String dbSEOUrlLink = wait.until(ExpectedConditions.visibilityOf(UI_SEO_URL_LINK)).getText();
        String ppSEOUrlLink = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoUrlLink", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSEOUrlLink, ppSEOUrlLink, "[Failed][Body] SEO URL link should be %s, but found %s.".formatted(ppSEOUrlLink, dbSEOUrlLink));
        logger.info("[UI][%s] Check Body - SEO URL link.".formatted(language));
    }

    void checkUISaleChanel() throws Exception {
        // check Sale chanel title
        String dbSaleChanel = wait.until(ExpectedConditions.visibilityOf(UI_SALE_CHANEL)).getText();
        String ppSaleChanel = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaleChanel, ppSaleChanel, "[Failed][Body] Sale chanel title should be %s, but found %s.".formatted(ppSaleChanel, dbSaleChanel));
        logger.info("[UI][%s] Check Body - Sale chanel title.".formatted(language));

        // check Online shop tooltips
        act.moveToElement(UI_SALE_CHANEL_ONLINE_SHOP).build().perform();
        commonAction.sleepInMiliSecond(500);
        String dbOnlineShopTooltips = wait.until(ExpectedConditions.visibilityOf(UI_SALE_CHANEL_ONLINE_SHOP_TOOLTIPS)).getText();
        String ppOnlineShopTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.onlineShopTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbOnlineShopTooltips, ppOnlineShopTooltips, "[Failed][Body] Online shop tooltips should be %s, but found %s.".formatted(ppOnlineShopTooltips, dbOnlineShopTooltips));
        logger.info("[UI][%s] Check Body - Online shop tooltips.".formatted(language));

        // check Gomua tooltips
        act.moveToElement(UI_SALE_CHANEL_GOMUA).build().perform();
        commonAction.sleepInMiliSecond(500);
        String dbGomuaTooltips = wait.until(ExpectedConditions.visibilityOf(UI_SALE_CHANEL_GOMUA_TOOLTIPS)).getText();
        String ppGomuaTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.gomuaTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGomuaTooltips, ppGomuaTooltips, "[Failed][Body] Gomua tooltips should be %s, but found %s.".formatted(ppGomuaTooltips, dbGomuaTooltips));
        logger.info("[UI][%s] Check Body - Gomua tooltips.".formatted(language));

        // check Shopee tooltips
        act.moveToElement(UI_SALE_CHANEL_SHOPEE).build().perform();
        commonAction.sleepInMiliSecond(500);
        String dbShopeeTooltips = wait.until(ExpectedConditions.visibilityOf(UI_SALE_CHANEL_SHOPEE_TOOLTIPS)).getText();
        String ppShopeeTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.shopeeTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbShopeeTooltips, ppShopeeTooltips, "[Failed][Body] Shopee tooltips should be %s, but found %s.".formatted(ppShopeeTooltips, dbShopeeTooltips));
        logger.info("[UI][%s] Check Body - Shopee tooltips.".formatted(language));
    }

    void checkUICollections() throws Exception {
        // check collections title
        String dbCollectionsTitle = wait.until(ExpectedConditions.visibilityOf(UI_COLLECTIONS)).getText();
        String ppCollectionsTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCollectionsTitle, ppCollectionsTitle, "[Failed][Body] Collections title should be %s, but found %s.".formatted(ppCollectionsTitle, dbCollectionsTitle));
        logger.info("[UI][%s] Check Body - Collections title.".formatted(language));

        // check collections search box placeholder
        String dbCollectionsSearchBoxPlaceholder = wait.until(ExpectedConditions.visibilityOf(UI_COLLECTIONS_SEARCH_BOX_PLACEHOLDER)).getAttribute("placeholder");
        String ppCollectionsSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.searchBoxPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCollectionsSearchBoxPlaceholder, ppCollectionsSearchBoxPlaceholder, "[Failed][Body] Collections search box placeholder should be %s, but found %s.".formatted(ppCollectionsSearchBoxPlaceholder, dbCollectionsSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Collections search box placeholder.".formatted(language));
    }

    void checkUIWarehousing() throws Exception {
        // check warehousing title
        String dbWarehousing = wait.until(ExpectedConditions.visibilityOf(UI_WAREHOUSING)).getText();
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing title should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing title.".formatted(language));

        // check SKU
        String dbSKU = wait.until(ExpectedConditions.visibilityOf(UI_SKU)).getText();
        String ppSKU = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.sku", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSKU, ppSKU, "[Failed][Body] SKU title should be %s, but found %s.".formatted(ppSKU, dbSKU));
        logger.info("[UI][%s] Check Body - SKU title.".formatted(language));

        // check Barcode
        String dbBarcode = wait.until(ExpectedConditions.visibilityOf(UI_BARCODE)).getText();
        String ppBarcode = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.barcode", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbBarcode, ppBarcode, "[Failed][Body] Barcode title should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
        logger.info("[UI][%s] Check Body - Barcode title.".formatted(language));

        // check manage inventory
        String dbManageInventory = wait.until(ExpectedConditions.visibilityOf(UI_MANAGE_INVENTORY)).getText();
        String ppManageInventory = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbManageInventory, ppManageInventory, "[Failed][Body] Manage inventory title should be %s, but found %s.".formatted(ppManageInventory, dbManageInventory));
        logger.info("[UI][%s] Check Body - Manage inventory.".formatted(language));

        // check manage inventory by product
        String dbManageInventoryByProduct = wait.until(ExpectedConditions.visibilityOf(UI_MANAGE_INVENTORY_BY_PRODUCT)).getText();
        String ppManageInventoryByProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byProduct", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbManageInventoryByProduct, ppManageInventoryByProduct, "[Failed][Body] Manage inventory by product should be %s, but found %s.".formatted(ppManageInventoryByProduct, dbManageInventoryByProduct));
        logger.info("[UI][%s] Check Body - Manage inventory by product.".formatted(language));

        // check manage inventory by IMEI/Serial number
        String dbManageInventoryByIMEI = wait.until(ExpectedConditions.visibilityOf(UI_MANAGE_INVENTORY_BY_IMEI)).getText();
        String ppManageInventoryByIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbManageInventoryByIMEI, ppManageInventoryByIMEI, "[Failed][Body] Manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppManageInventoryByIMEI, dbManageInventoryByIMEI));
        logger.info("[UI][%s] Check Body - Manage inventory by IMEI/Serial number.".formatted(language));

        // check stock quantity title
        if (UI_STOCK_QUANTITY.isDisplayed()) {
            String dbStockQuantity = wait.until(ExpectedConditions.visibilityOf(UI_STOCK_QUANTITY)).getText();
            String ppStockQuantity = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.title", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbStockQuantity, ppStockQuantity, "[Failed][Body] Stock quantity title should be %s, but found %s.".formatted(ppStockQuantity, dbStockQuantity));
            logger.info("[UI][%s] Check Body - Stock quantity title.".formatted(language));
        }

        // check apply stock for all branches button
        String dbApplyAllBtn = wait.until(ExpectedConditions.visibilityOf(UI_STOCK_QUANTITY_APPLY_ALL_BTN)).getText();
        String ppApplyAllBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.applyAllBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbApplyAllBtn, ppApplyAllBtn, "[Failed][Body] Apply all button should be %s, but found %s.".formatted(ppApplyAllBtn, dbApplyAllBtn));
        logger.info("[UI][%s] Check Body - Apply all button.".formatted(language));

        // in-case edit product, check sold count and remaining stock
        if (driver.getCurrentUrl().contains("edit")) {
            // check remaining stock
            String dbRemainingStock = wait.until(ExpectedConditions.visibilityOf(UI_REMAINING_STOCK)).getText();
            String ppRemainingStock = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.remainingStock", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbRemainingStock, ppRemainingStock, "[Failed][Body] Remaining stock should be %s, but found %s.".formatted(ppRemainingStock, dbRemainingStock));
            logger.info("[UI][%s] Check Body - Remaining stock.".formatted(language));

            // check sold count
            String dbSoldCount = wait.until(ExpectedConditions.visibilityOf(UI_SOLD_COUNT)).getText();
            String ppSoldCount = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.soldCount", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbSoldCount, ppSoldCount, "[Failed][Body] Sold count should be %s, but found %s.".formatted(ppSoldCount, dbSoldCount));
            logger.info("[UI][%s] Check Body - Sold count.".formatted(language));
        }

        // check display if out of stock checkbox
        String dbDisplayIfOutOfStockCheckbox = wait.until(ExpectedConditions.visibilityOf(UI_DISPLAY_IF_OUT_OF_STOCK)).getText();
        String ppDisplayIfOutOfStockCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.displayIfOutOfStock", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDisplayIfOutOfStockCheckbox, ppDisplayIfOutOfStockCheckbox, "[Failed][Body] Display if out of stock checkbox label should be %s, but found %s.".formatted(ppDisplayIfOutOfStockCheckbox, dbDisplayIfOutOfStockCheckbox));
        logger.info("[UI][%s] Check Body - Display if out of stock checkbox.".formatted(language));

        // check hide remaining stock on online store
        String dbHideRemainingOnOnlineStoreCheckbox = wait.until(ExpectedConditions.visibilityOf(UI_HIDE_REMAINING_STOCK_ON_ONLINE_STORE)).getText();
        String ppHideRemainingOnOnlineStoreCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.hideRemainingStock", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbHideRemainingOnOnlineStoreCheckbox, ppHideRemainingOnOnlineStoreCheckbox, "[Failed][Body] Hide remaining stock on online store checkbox label should be %s, but found %s.".formatted(ppHideRemainingOnOnlineStoreCheckbox, dbHideRemainingOnOnlineStoreCheckbox));
        logger.info("[UI][%s] Check Body - Hide remaining stock on online store checkbox.".formatted(language));
    }

    void checkManageInventoryByIMEINotice() throws Exception {
        // check manage inventory by IMEI/Serial number notice
        String dbIMEINotice = wait.until(ExpectedConditions.visibilityOf(UI_MANAGE_INVENTORY_BY_IMEI_NOTICE)).getText();
        String ppIMEINotice = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI.notice", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbIMEINotice, ppIMEINotice, "[Failed][Body] Manage Inventory by IMEI/Serial number notice should be %s, but found %s.".formatted(ppIMEINotice, dbIMEINotice));
        logger.info("[UI][%s] Check Body - Manage Inventory by IMEI/Serial number notice.".formatted(language));
    }

    void checkUIPackageInformation() throws Exception {
        // check package information title
        String dbPackageInformation = wait.until(ExpectedConditions.visibilityOf(UI_PACKAGE_INFORMATION)).getText();
        String ppPackageInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPackageInformation, ppPackageInformation, "[Failed][Body] Package information title should be %s, but found %s.".formatted(ppPackageInformation, dbPackageInformation));
        logger.info("[UI][%s] Check Body - Package information title.".formatted(language));

        // check package information tooltips
        act.moveToElement(UI_PACKAGE_INFORMATION_TOOLTIPS).build().perform();
        String dbPackageInformationTooltips = wait.until(ExpectedConditions.visibilityOf(UI_PACKAGE_INFORMATION_TOOLTIPS)).getAttribute("data-original-title");
        String ppPackageInformationTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.tooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPackageInformationTooltips, ppPackageInformationTooltips, "[Failed][Body] Package information tooltips should be %s, but found %s.".formatted(ppPackageInformationTooltips, dbPackageInformationTooltips));
        logger.info("[UI][%s] Check Body - Package information tooltips.".formatted(language));

        // check weight
        String dbWeight = wait.until(ExpectedConditions.visibilityOf(UI_WEIGHT)).getText();
        String ppWeight = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.weight", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWeight, ppWeight, "[Failed][Body] Product weight should be %s, but found %s.".formatted(ppWeight, dbWeight));
        logger.info("[UI][%s] Check Body - Product weight.".formatted(language));

        // check length
        String dbLength = wait.until(ExpectedConditions.visibilityOf(UI_LENGTH)).getText();
        String ppLength = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.length", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbLength, ppLength, "[Failed][Body] Product length should be %s, but found %s.".formatted(ppLength, dbLength));
        logger.info("[UI][%s] Check Body - Product length.".formatted(language));

        // check width
        String dbWidth = wait.until(ExpectedConditions.visibilityOf(UI_WIDTH)).getText();
        String ppWidth = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.width", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWidth, ppWidth, "[Failed][Body] Product width should be %s, but found %s.".formatted(ppWidth, dbWidth));
        logger.info("[UI][%s] Check Body - Product width.".formatted(language));

        // check height
        String dbHeight = wait.until(ExpectedConditions.visibilityOf(UI_HEIGHT)).getText();
        String ppHeight = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.height", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbHeight, ppHeight, "[Failed][Body] Product height should be %s, but found %s.".formatted(ppHeight, dbHeight));
        logger.info("[UI][%s] Check Body - Product height.".formatted(language));

        // check shipping notice
        String dbShippingFeeNote = wait.until(ExpectedConditions.visibilityOf(UI_SHIPPING_FEE_NOTE)).getText();
        String ppShippingFeeNote = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.shippingFeeNote", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbShippingFeeNote, ppShippingFeeNote, "[Failed][Body] Shipping fee note should be %s, but found %s.".formatted(ppShippingFeeNote, dbShippingFeeNote));
        logger.info("[UI][%s] Check Body - Shipping fee note.".formatted(language));
    }

    void checkUIPriority() throws Exception {
        // check priority title
        String dbPriorityTitle = wait.until(ExpectedConditions.visibilityOf(UI_PRIORITY)).getText();
        String ppPriorityTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.priority.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPriorityTitle, ppPriorityTitle, "[Failed][Body] Priority Title should be %s, but found %s.".formatted(ppPriorityTitle, dbPriorityTitle));
        logger.info("[UI][%s] Check Body - Priority Title.".formatted(language));

        // check priority tooltips
        act.moveToElement(UI_PRIORITY_TOOLTIPS).build().perform();
        String dbPriorityTooltips = wait.until(ExpectedConditions.visibilityOf(UI_PRIORITY_TOOLTIPS)).getAttribute("data-original-title");
        String ppPriorityTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.priority.tooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPriorityTooltips, ppPriorityTooltips, "[Failed][Body] Priority tooltips should be %s, but found %s.".formatted(ppPriorityTooltips, dbPriorityTooltips));
        logger.info("[UI][%s] Check Body - Priority tooltips.".formatted(language));

        // check priority text box placeholder
        String dbPriorityTextBoxPlaceholder = wait.until(ExpectedConditions.visibilityOf(UI_PRIORITY_PLACE_HOLDER)).getAttribute("placeholder");
        String ppPriorityTextBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.priority.placeholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPriorityTextBoxPlaceholder, ppPriorityTextBoxPlaceholder, "[Failed][Body] Priority text box placeholder should be %s, but found %s.".formatted(ppPriorityTextBoxPlaceholder, dbPriorityTextBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Priority text box placeholder.".formatted(language));
    }

    void checkUIPlatform() throws Exception {
        // check platform title
        String dbPlatformTitle = wait.until(ExpectedConditions.visibilityOf(UI_PLATFORM)).getText();
        String ppPlatformTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPlatformTitle, ppPlatformTitle, "[Failed][Body] Platform title should be %s, but found %s.".formatted(ppPlatformTitle, dbPlatformTitle));
        logger.info("[UI][%s] Check Body - Platform title.".formatted(language));

        // check platform tooltips
        act.moveToElement(UI_PLATFORM_TOOLTIPS).build().perform();
        String dbPlatformTooltips = wait.until(ExpectedConditions.visibilityOf(UI_PLATFORM_TOOLTIPS)).getAttribute("data-original-title");
        String ppPlatformTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.tooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPlatformTooltips, ppPlatformTooltips, "[Failed][Body] Platform tooltips should be %s, but found %s.".formatted(ppPlatformTooltips, dbPlatformTooltips));
        logger.info("[UI][%s] Check Body - Platform tooltips.".formatted(language));

        // check App platform
        String dbApp = wait.until(ExpectedConditions.visibilityOf(UI_APP)).getText();
        String ppApp = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.app", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbApp, ppApp, "[Failed][Body] Platform app should be %s, but found %s.".formatted(ppApp, dbApp));
        logger.info("[UI][%s] Check Body - Platform app.".formatted(language));

        // check Web platform
        String dbWeb = wait.until(ExpectedConditions.visibilityOf(UI_WEB)).getText();
        String ppWeb = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.web", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWeb, ppWeb, "[Failed][Body] Platform web should be %s, but found %s.".formatted(ppWeb, dbWeb));
        logger.info("[UI][%s] Check Body - Platform web.".formatted(language));

        // check POS platform
        String dbPOS = wait.until(ExpectedConditions.visibilityOf(UI_IN_STORE)).getText();
        String ppPOS = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.pos", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPOS, ppPOS, "[Failed][Body] Platform POS should be %s, but found %s.".formatted(ppPOS, dbPOS));
        logger.info("[UI][%s] Check Body - Platform POS.".formatted(language));

        // check GoSocial platform
        String dbGoSocial = wait.until(ExpectedConditions.visibilityOf(UI_GOSOCIAL)).getText();
        String ppGoSocial = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.gosocial", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoSocial, ppGoSocial, "[Failed][Body] Platform GoSocial should be %s, but found %s.".formatted(ppGoSocial, dbGoSocial));
        logger.info("[UI][%s] Check Body - Platform GoSocial.".formatted(language));
    }

    void checkUIProductInfo() throws Exception {
        checkUIHeaderCreateProductPage();
        checkUIProductInformation();
        checkUIImages();
        checkUIPricing();
        checkUIVariations();
        checkUIAddConversionUnit();
        checkUIAddWholesaleProduct();
        checkUIDeposit();
        checkUISEO();
        checkUISaleChanel();
        checkUICollections();
        checkUIWarehousing();
        checkUIPackageInformation();
        checkUIPriority();
        checkUIPlatform();
    }


}
