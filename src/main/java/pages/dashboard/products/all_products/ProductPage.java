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
import utilities.UICommonAction;
import utilities.data.DataGenerator;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static api.dashboard.marketing.LoyaltyProgram.membershipStatus;
import static api.dashboard.promotion.CreatePromotion.*;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static pages.dashboard.products.all_products.wholesale_price.WholesaleProductPage.*;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.character_limit.CharacterLimit.MAX_PRODUCT_DESCRIPTION;
import static utilities.links.Links.DOMAIN;
import static utilities.page_loaded_text.PageLoadedText.DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_ENG;
import static utilities.page_loaded_text.PageLoadedText.DB_PRODUCT_DETAIL_PAGE_LOADED_TEXT_VIE;

public class ProductPage extends ProductPageElement {
    WebDriverWait wait;
    Actions act;
    UICommonAction commonAction;
    String CREATE_PRODUCT_PATH = "/product/create";

    Logger logger = LogManager.getLogger(ProductPage.class);

    public ProductPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        act = new Actions(driver);
        commonAction = new UICommonAction(driver);
    }

    public static String uiProductName;
    public static List<Integer> uiProductListingPrice;
    public static List<Integer> uiProductSellingPrice;
    public static Map<String, List<String>> uiVariationMap;
    public static List<String> uiVariationList;
    public static Map<String, List<Integer>> uiProductStockQuantity;
    public static List<String> uiBranchName;

    public static boolean uiIsDisplayOutOfStock = true;
    public static boolean uiIsHideStock = false;
    //    public static boolean uiIsEnableListing = false;
    public static boolean uiIsShowOnApp = true;
    public static boolean uiIsShowOnWeb = true;
    public static boolean uiIsShowInStore = true;
    public static boolean uiIsShowInGoSocial = true;
    public static boolean uiIsIMEIProduct;
    public static Integer uiProductID;

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

    public ProductPage navigateToCreateProductPage() {
        // access to create product page by URL
        driver.get("%s%s".formatted(DOMAIN, CREATE_PRODUCT_PATH));

        // wait page loaded
        commonAction.verifyPageLoaded("Chọn kênh bán hàng", "Select sale channel");

        // hide Facebook bubble
        commonAction.hideElement(driver.findElement(By.cssSelector("#fb-root")));

        // wait all branches name element is visible
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(LIST_BRANCH_NAME));

        // get list elements
        List<WebElement> listBranchName = driver.findElements(LIST_BRANCH_NAME);

        // get branch name list
        uiBranchName = new ArrayList<>();
        for (int i = 0; i < listBranchName.size(); i++) {
            try {
                // get branch name
                uiBranchName.add(listBranchName.get(i).getText());
            } catch (StaleElementReferenceException ex) {
                logger.info(ex);
                // wait all branches name element is visible again
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(LIST_BRANCH_NAME));

                // get list elements
                listBranchName = driver.findElements(LIST_BRANCH_NAME);

                // get branch name again
                uiBranchName.add(listBranchName.get(i).getText());
            }
        }

        return this;
    }

    void inputProductName(String productName) {
        // input product name
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_NAME)).sendKeys(productName);

    }

    void inputProductDescription() {
        // input product description
        String productDescription = randomAlphabetic(nextInt(MAX_PRODUCT_DESCRIPTION) + 1);
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DESCRIPTION)).sendKeys(productDescription);

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
            logger.info("Collection: %s".formatted(LIST_MANUAL_COLLECTION.get(index).getText()));

            // select collection
            LIST_MANUAL_COLLECTION.get(index).click();
        }

    }

    void inputWithoutVariationProductSKU() {
        String sku = "SKU" + Instant.now().toEpochMilli();
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_SKU_WITHOUT_VARIATION)).sendKeys(sku);
    }

    void setManageInventory(boolean isIMEIProduct) {
        uiIsIMEIProduct = isIMEIProduct;
        // set manage inventory by product or IMEI/Serial number
        new Select(MANAGE_INVENTORY).selectByValue(isIMEIProduct ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

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
        if (PLATFORM_GO_SOCIAL.isSelected() != uiIsShowInGoSocial)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", PLATFORM_GO_SOCIAL);

    }

    void inputSEO() {
        // time
        String time = String.valueOf(Instant.now().toEpochMilli());
        // SEO title
        String title = "Auto - SEO Title - " + time;
        wait.until(ExpectedConditions.elementToBeClickable(SEO_TITLE)).sendKeys(title);

        // SEO description
        String description = "Auto - SEO Description - " + time;
        wait.until(ExpectedConditions.elementToBeClickable(SEO_DESCRIPTION)).sendKeys(description);

        // SEO keyword
        String keyword = "Auto - SEO Keyword - " + time;
        wait.until(ExpectedConditions.elementToBeClickable(SEO_KEYWORD)).sendKeys(keyword);

        // SEO URL
        wait.until(ExpectedConditions.elementToBeClickable(SEO_URL)).sendKeys(time);
    }

    public void productInfo(String productName, boolean isIMEIProduct) {
        inputProductName(productName);
        inputProductDescription();
        uploadProductImage("img.jpg");
        selectVAT();
        selectCollection();
        setManageInventory(isIMEIProduct);
        setSFDisplay();
        setProductDimension();
        selectPlatform();
        inputSEO();
    }

    // Without variation product
    public void inputWithoutVariationPrice() {
        // get listing price
        uiProductListingPrice = new ArrayList<>();
        uiProductListingPrice.add((int) (Math.random() * MAX_PRICE));

        // get selling price
        uiProductSellingPrice = new ArrayList<>();
        uiProductSellingPrice.add((int) (Math.random() * uiProductListingPrice.get(0)));


        // input listing price
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LISTING_PRICE_WITHOUT_VARIATION));
        act.moveToElement(PRODUCT_LISTING_PRICE_WITHOUT_VARIATION).doubleClick().sendKeys(String.valueOf(uiProductListingPrice.get(0))).build().perform();

        // input selling price
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_SELLING_PRICE_WITHOUT_VARIATION));
        act.moveToElement(PRODUCT_SELLING_PRICE_WITHOUT_VARIATION).doubleClick().sendKeys(String.valueOf(uiProductSellingPrice.get(0))).build().perform();

    }

    void addIMEIForEachBranch(String variationValue, List<Integer> branchStock) {
        // select all branches
        wait.until(ExpectedConditions.elementToBeClickable(ADD_IMEI_POPUP_BRANCH_DROPDOWN)).click();
        if (!ADD_IMEI_POPUP_BRANCH_DROPDOWN_SELECT_ALL.isSelected())
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", ADD_IMEI_POPUP_BRANCH_DROPDOWN_SELECT_ALL);
        else ADD_IMEI_POPUP_BRANCH_DROPDOWN.click();

        // input IMEI/Serial number for each branch
        for (int brIndex = 0; brIndex < uiBranchName.size(); brIndex++) {
            for (int i = 0; i < branchStock.get(brIndex); i++) {
                act.moveToElement(ADD_IMEI_POPUP_IMEI_TEXT_BOX.get(brIndex)).click().sendKeys("%s%s_IMEI_%s\n".formatted(variationValue != null ? "%s_".formatted(variationValue) : "", uiBranchName.get(brIndex), i)).build().perform();
            }
        }

        // save IMEI/Serial number
        wait.until(ExpectedConditions.elementToBeClickable(ADD_IMEI_POPUP_SAVE_BTN)).click();
    }

    public void inputWithoutVariationStock(int... branchStockQuantity) {
        /* get without variation stock information */
        // get variation list
        uiVariationList = new ArrayList<>();
        uiVariationList.add(null);

        // get product stock quantity
        uiProductStockQuantity = new HashMap<>();
        uiProductStockQuantity.put(null, IntStream.range(0, uiBranchName.size()).mapToObj(i -> branchStockQuantity.length > i ? branchStockQuantity[i] : 0).toList());

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
            for (int i = 0; i < uiBranchName.size(); i++) {
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

    public void createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) {
        // product name
        uiProductName = isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - ");
        uiProductName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(uiProductName, isIMEIProduct);
        inputWithoutVariationPrice();
        inputWithoutVariationStock(branchStock);
        inputWithoutVariationProductSKU();
        completeCreateProduct();
        initDiscountInformation();
    }

    // Variation product
    public void addVariations() {
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
            // click add variation button
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();

            // input variation name
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_NAME.get(varID))).sendKeys(varName);

            // input variation value
            for (String varValue : uiVariationMap.get(varName)) {
                act.moveToElement(VARIATION_VALUE.get(varID)).click().sendKeys("%s\n".formatted(varValue)).build().perform();
            }
        }
    }


    void inputVariationPrice() {
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

        // open list action dropdown
        wait.until(ExpectedConditions.elementToBeClickable(VARIATION_TABLE_SELECT_ACTION)).click();

        // open Update price popup
        commonAction.refreshListElement(VARIATION_TABLE_LIST_ACTION).get(0).click();

        // wait Update price popup visible
        wait.until(ExpectedConditions.visibilityOf(POPUP));

        // input product price
        for (int i = 0; i < uiVariationList.size(); i++) {
            act.moveToElement(commonAction.refreshListElement(UPDATE_PRICE_POPUP_LISTING_PRICE).get(i)).doubleClick().sendKeys(String.valueOf(uiProductListingPrice.get(i))).build().perform();
            act.moveToElement(commonAction.refreshListElement(UPDATE_PRICE_POPUP_SELLING_PRICE).get(i)).doubleClick().sendKeys(String.valueOf(uiProductSellingPrice.get(i))).build().perform();
        }

        // close Update price popup
        wait.until(ExpectedConditions.elementToBeClickable(POPUP_UPDATE_BTN)).click();
    }

    void inputVariationStock(int increaseNum, int... branchStockQuantity) {
        // get product stock quantity
        uiProductStockQuantity = new HashMap<>();
        for (int i = 0; i < uiVariationList.size(); i++) {
            List<Integer> variationStock = new ArrayList<>();
            // set branch stock
            for (int branchIndex = 0; branchIndex < uiBranchName.size(); branchIndex++) {
                variationStock.add((branchStockQuantity.length > branchIndex) ? (branchStockQuantity[branchIndex] + (i * increaseNum)) : 0);
            }
            uiProductStockQuantity.put(uiVariationList.get(i), variationStock);
        }

        // input product price
        for (int i = 0; i < uiVariationList.size(); i++) {
            // open Update stock popup
            act.moveToElement(commonAction.refreshListElement(VARIATION_TABLE_STOCK_QUANTITY).get(i)).click().build().perform();

            // wait Update stock popup visible
            wait.until(ExpectedConditions.visibilityOf(POPUP));

            if (uiIsIMEIProduct)
                addIMEIForEachBranch(uiVariationList.get(i), uiProductStockQuantity.get(uiVariationList.get(i)));
            else {
                // switch to change stock tab
                wait.until(ExpectedConditions.elementToBeClickable(UPDATE_STOCK_POPUP_CHANGE_TAB)).click();

                // input stock quantity to visible stock input field
                wait.until(ExpectedConditions.elementToBeClickable(UPDATE_STOCK_POPUP_INPUT_STOCK)).sendKeys(String.valueOf(Collections.max(uiProductStockQuantity.get(uiVariationList.get(i))) + 1));

                // input stock for each branch
                for (int brIndex = 0; brIndex < uiBranchName.size(); brIndex++) {
                    act.moveToElement(commonAction.refreshListElement(UPDATE_STOCK_POPUP_NORMAL_VARIATION_STOCK).get(brIndex))
                            .doubleClick()
                            .sendKeys(String.valueOf(uiProductStockQuantity.get(uiVariationList.get(i)).get(brIndex)))
                            .build()
                            .perform();
                }
            }

            // close Update stock popup
            wait.until(ExpectedConditions.elementToBeClickable(POPUP_UPDATE_BTN)).click();
        }
    }

    public void createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) {
        // product name
        uiProductName = isIMEIProduct ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - ");
        uiProductName += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(uiProductName, isIMEIProduct);
        addVariations();
        inputVariationPrice();
        inputVariationStock(increaseNum, branchStock);
        completeCreateProduct();
        initDiscountInformation();
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

        // wait api return list product
        uiProductID = Integer.valueOf(wait.until(ExpectedConditions.visibilityOf(PRODUCT_ID)).getText());
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
        flashSaleStatus = new HashMap<>();
        uiBranchName.forEach(brName -> flashSaleStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init discount campaign status
        discountCampaignStatus = new HashMap<>();
        uiBranchName.forEach(brName -> discountCampaignStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // init flash sale price
        flashSalePrice = new ArrayList<>();
        flashSalePrice.addAll(uiProductSellingPrice);

        // init flash sale stock
        flashSaleStock = new ArrayList<>();
        uiVariationList.forEach(varName -> flashSaleStock.add(Collections.max(uiProductStockQuantity.get(varName))));

        // init product discount campaign price
        discountCampaignPrice = new ArrayList<>();
        discountCampaignPrice.addAll(uiProductSellingPrice);

        // init wholesale product price, rate and stock
        uiWholesaleProductPrice = new ArrayList<>();
        uiWholesaleProductPrice.addAll(uiProductSellingPrice);

        uiWholesaleProductRate = new ArrayList<>();
        IntStream.range(0, uiWholesaleProductPrice.size()).forEach(i -> uiWholesaleProductRate.add(Float.valueOf(new DecimalFormat("#.##").format((1 - (float) uiWholesaleProductPrice.get(i) / uiProductSellingPrice.get(i)) * 100))));

        uiWholesaleProductStock = new ArrayList<>();
        uiVariationList.forEach(varName -> uiWholesaleProductStock.add(Collections.max(uiProductStockQuantity.get(varName))));

        // discount code
        discountCodeStatus = new HashMap<>();
        uiBranchName.forEach(brName -> discountCodeStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));

        // membership
        membershipStatus = new HashMap<>();
        uiBranchName.forEach(brName -> membershipStatus
                .put(brName, IntStream.range(0, uiVariationList.size())
                        .mapToObj(i -> "EXPIRED").toList()));
    }

    public void configWholesaleProduct() {

    }

}
