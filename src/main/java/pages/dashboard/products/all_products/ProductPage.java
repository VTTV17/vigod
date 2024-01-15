package pages.dashboard.products.all_products;

import api.dashboard.products.ProductCollection;
import api.dashboard.products.ProductInformation;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import api.dashboard.setting.VAT;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.all_products.conversion_unit.ConversionUnitPage;
import pages.dashboard.products.all_products.variation_detail.VariationDetailPage;
import pages.dashboard.products.all_products.wholesale_price.WholesaleProductPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;

public class ProductPage extends ProductPageElement {
    WebDriver driver;
    @Getter
    UICommonAction commonAction;
    String createProductPath = "/product/create";
    @Getter
    String updateProductPath = "/product/edit/%s";
    @Getter
    private static AssertCustomize assertCustomize;
    String epoch = String.valueOf(Instant.now().toEpochMilli());
    private boolean noDiscount = nextBoolean();
    private boolean hasDimension = nextBoolean();
    Logger logger = LogManager.getLogger(ProductPage.class);
    BranchInfo brInfo;
    private static StoreInfo storeInfo;
    private static ProductInfo productInfo;
    LoginInformation loginInformation;

    public ProductPage(WebDriver driver, LoginInformation loginInformation) {
        this.driver = driver;

        // init common function
        commonAction = new UICommonAction(driver);

        // get login information (username, password)
        this.loginInformation = loginInformation;

        // get branch information
        brInfo = new BranchManagement(loginInformation).getInfo();

        // get store information
        storeInfo = new StoreInformation(loginInformation).getInfo();

        // always reset count false when init ProductPage model.
        AssertCustomize.setResetCountFalse(true);

        // init assert customize function
        assertCustomize = new AssertCustomize(driver);

        // turn off count false flag to keep number of failure
        // combine with another test on product detail page
        AssertCustomize.setResetCountFalse(false);
    }

    String name;
    String description;
    @Getter
    private static List<Long> productListingPrice;
    @Getter
    private static List<Long> productSellingPrice;
    Map<String, List<String>> variationMap;
    @Getter
    private static List<String> variationList;
    @Getter
    private static Map<String, List<Integer>> productStockQuantity;
    private boolean showOutOfStock = true;
    boolean hideStock = false;
    boolean enableListing = false;
    boolean showOnApp = true;
    boolean showOnWeb = true;
    boolean uiIsShowInStore = true;
    boolean showInGoSocial = true;
    @Getter
    private static boolean manageByIMEI;
    @Getter
    private static int productID;
    @Getter
    private static boolean hasModel;
    @Getter
    private static String language;

    /* Tien */
    public void clickPrintBarcode() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnPrintBarcode).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnPrintBarcode));
            return;
        }
        commonAction.click(loc_btnPrintBarcode);
        logger.info("Clicked on 'Print Barcode' button.");
    }

    public void clickAddVariation() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(addVariationBtn).findElement(By.xpath("./ancestor::div[@class='uik-widget__wrapper gs-widget gs-widget ']/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(addVariationBtn));
            return;
        }
        commonAction.click(addVariationBtn);
        logger.info("Clicked on 'Add Variation' button.");
    }

    public void clickAddDepositBtn() {
        if (commonAction.isElementVisiblyDisabled(ADD_DEPOSIT_BTN.findElement(By.xpath("./ancestor::div[@class='uik-widget__wrapper gs-widget gs-widget ']/parent::*")))) {
            new HomePage(driver).isMenuClicked(ADD_DEPOSIT_BTN);
            return;
        }
        commonAction.clickElement(ADD_DEPOSIT_BTN);
        logger.info("Clicked on 'Add Deposit' button.");
    }

    public void inputSEOTitle(String seoTitle) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(this.seoTitle).findElement(By.xpath("./ancestor::div[contains(@class,'gs-widget  seo-editor')]/descendant::*[1]")))) {
            Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(this.seoTitle)));
            return;
        }
        commonAction.sendKeys(this.seoTitle, seoTitle);
        logger.info("Input '" + seoTitle + "' into SEO Title field.");
    }

    public String getSEOTitle() {
        String title = commonAction.getElementAttribute(commonAction.getElement(seoTitle), "value");
        logger.info("Retrieved SEO Title: %s".formatted(title));
        return title;
    }

    public boolean isPrintBarcodeDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.isElementNotDisplay(driver.findElements(PRINT_BARCODE_MODAL));
    }

    public boolean isDeleteVariationBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return !commonAction.isElementNotDisplay(commonAction.getElements(loc_btnDeleteVariation));
    }

    public boolean isDeleteDepositBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return !commonAction.isElementNotDisplay(DELETE_DEPOSIT_BTN);
    }

    public void clickOnTheCreateProductBtn() {
        // click create product button
        commonAction.click(createProductBtn);

        // log
        logger.info("Click on the Create Product button");
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

    public ProductPage setLanguage(String language) {
        ProductPage.language = language;

        driver.get(DOMAIN);

        // get current language
        String currentLanguage = commonAction.getLangKey().equals("vi") ? "VIE" : "ENG";
        logger.info("Current language: %s".formatted(currentLanguage));

        // set dashboard language
        if (!currentLanguage.equals(language)) {
            // open language dropdown list
            commonAction.clickJS(selectedLanguage);

            // select language
            commonAction.clickJS(By.xpath(languageLocator.formatted(language)));

            // log
            logger.info("New language: %s.".formatted(language));
        }
        return this;
    }


    /* Thang */

    public ProductPage setNoDiscount(boolean noDiscount) {
        this.noDiscount = noDiscount;
        return this;
    }

    public ProductPage setHasDimension(boolean hasDimension) {
        this.hasDimension = hasDimension;
        return this;
    }

    public ProductPage setShowOutOfStock(boolean showOutOfStock) {
        this.showOutOfStock = showOutOfStock;
        return this;
    }

    public ProductPage navigateToCreateProductPage() throws Exception {
        // access to create product page by URL
        driver.get("%s%s".formatted(DOMAIN, createProductPath));

        // hide Facebook bubble
        commonAction.removeFbBubble();

        // check [UI] create product page
        checkUICreateProductInfo();

        return this;
    }

    public ProductPage navigateToUpdateProductPage(int productID) throws Exception {
        // get product id
        ProductPage.productID = productID;

        // log
        logger.info("Product id: %s".formatted(productID));

        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath.formatted(productID)));

        // refresh page
        driver.navigate().refresh();

        // clear old conversion unit config
        if (commonAction.isCheckedJS(addConversionUnitCheckbox)) {
            commonAction.clickJS(addConversionUnitCheckbox);
        }
        logger.info("Remove old conversion unit config.");

        // delete old wholesale product config if any
        if (commonAction.isCheckedJS(addWholesalePricingCheckbox)) {
            // uncheck add wholesale pricing checkbox to delete old wholesale config
            commonAction.openPopupJS(addWholesalePricingCheckbox, confirmPopup);

            // confirm delete old wholesale config
            commonAction.closePopup(okBtnOnConfirmPopup);
        }
        logger.info("Remove old wholesale pricing config.");

        // save changes
        commonAction.openPopupJS(saveBtn, loc_dlgNotification);

        // if update product failed, try again
        if (!commonAction.getListElement(failPopup).isEmpty()) {
            // close update product failed popup
            commonAction.closePopup(closeBtnOnNotificationPopup);

            // save changes
            commonAction.openPopupJS(saveBtn, loc_dlgNotification);
        }

        // if that still failed, end test.
        Assert.assertTrue(commonAction.getListElement(failPopup).isEmpty(), "[Failed][Update product] Can not remove old conversion unit/wholesale pricing config.");

        // close notification popup
        commonAction.closePopup(closeBtnOnNotificationPopup);
        logger.info("Close notification popup.");

        // hide Facebook bubble
        commonAction.removeFbBubble();

        // check [UI] update product page
        checkUIUpdateProductInfo();

        return this;
    }

    void inputProductName(String name) {
        // input product name
        commonAction.sendKeys(productName, name);
        logger.info("Input product name: %s.".formatted(name));
    }

    void inputProductDescription() {
        // input product description
        description = "[%s] product descriptions".formatted(storeInfo.getDefaultLanguage());
        commonAction.sendKeys(productDescription, description);
        logger.info("Input product description: %s.".formatted(description));
    }

    void uploadProductImage(String... imageFile) {
        // upload product image
        for (String imgFile : imageFile) {
            Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"), "/src/main/resources/uploadfile/product_images/%s".formatted(imgFile).replace("/", File.separator)));
            commonAction.uploads(productImage, filePath.toString());
            logger.info("[Upload image popup] Upload images, file path: %s.".formatted(filePath));
        }
    }

    void selectVAT() {
        // open VAT dropdown
        commonAction.click(vatDropDown);
        logger.info("Open VAT dropdown.");

        // get VAT name
        List<String> vatList = new VAT(loginInformation).getInfo().getTaxName();
        String vatName = vatList.get(nextInt(vatList.size()));

        // select VAT
        commonAction.click(vatName.equals("tax.value.include") ? noTax : By.xpath(othersTaxLocator.formatted(vatName)));

        // log
        logger.info("Select VAT: %s.".formatted(vatName));
    }

    void selectCollection() {
        // click on collection search box
        List<String> collectionsNameList = new ProductCollection(loginInformation).getListOfManualProductCollectionsName();
        if (collectionsNameList.isEmpty()) logger.info("Store has not created any collections yet.");
        else {
            // remove all collections
            List<WebElement> removeIcons = commonAction.getListElement(removeCollectionBtn);
            if (!removeIcons.isEmpty()) {
                IntStream.iterate(removeIcons.size() - 1, index -> index >= 0, index -> index - 1)
                        .forEach(index -> commonAction.clickJS(removeCollectionBtn, index));
                logger.info("Remove assigned collections.");
            }
            // open collection dropdown
            commonAction.click(collectionSearchBox);
            logger.info("Open collections dropdown.");

            // get collection name
            String collectionName = collectionsNameList.get(nextInt(collectionsNameList.size()));

            // select collection
            commonAction.click(By.xpath(collectionLocator.formatted(collectionName)));

            // log
            logger.info("Select collection: %s".formatted(collectionName));
        }
    }

    void inputWithoutVariationProductSKU() {
        String sku = "SKU" + Instant.now().toEpochMilli();
        commonAction.sendKeys(productSKUWithoutVariation, sku);
        logger.info("Input SKU: %s.".formatted(sku));
    }

    void updateWithoutVariationProductSKU() throws Exception {
        // open update SKU popup
        commonAction.click(productSKUWithoutVariation);
        logger.info("Open update SKU popup.");

        // wait Update SKU popup visible
        commonAction.getElement(loc_dlgUpdateSKU);
        logger.info("Wait update SKU popup visible.");

        // check [UI] SKU popup
        checkUpdateSKUPopup();

        // input SKU for each branch
        for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
            String sku = "SKU_%s_%s".formatted(brInfo.getActiveBranches().get(brIndex), epoch);
            commonAction.sendKeys(textBoxOnUpdateSKUPopup, brIndex, sku);
            logger.info("Input SKU: %s.".formatted(sku));
        }

        // click around
        commonAction.click(titleOfUpdateSKUPopup);

        // close Update SKU popup
        commonAction.click(updateBtnOnPopup);
    }

    void setManageInventory(boolean isIMEIProduct) throws Exception {
        manageByIMEI = isIMEIProduct;
        // set manage inventory by product or IMEI/Serial number
        if (!driver.getCurrentUrl().contains("/edit/"))
            new Select(commonAction.getElement(manageByInventory)).selectByValue(isIMEIProduct ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

        // check [UI] after select manage inventory by IMEI/Serial Number
        if (isIMEIProduct) checkManageInventoryByIMEINotice();

        // log
        logger.info("Manage inventory by: %s".formatted(isIMEIProduct ? "IMEI/Serial Number" : "Product"));
    }

    void setSFDisplay() {
        // Display if out of stock
        if (commonAction.isCheckedJS(displayIfOutOfStockCheckbox) != showOutOfStock)
            commonAction.clickJS(displayIfOutOfStockCheckbox);

        // Hide remaining stock on online store
        if (commonAction.isCheckedJS(hideRemainingStockOnOnlineStoreCheckbox) != hideStock)
            commonAction.clickJS(hideRemainingStockOnOnlineStoreCheckbox);

        // Show as listing product on storefront
        if (commonAction.isCheckedJS(showAsListingProductOnStoreFrontCheckbox) != enableListing)
            commonAction.clickJS(showAsListingProductOnStoreFrontCheckbox);
    }

    void setPriority(int priority) {
        // set product priority (1-100)
        commonAction.sendKeys(priorityTextBox, String.valueOf(priority));
        logger.info("Input priority: %s.".formatted(priority));
    }

    void setProductDimension() {
        String dimension = (hasDimension) ? "10" : "0";
        // input product weight
        commonAction.sendKeys(productWeight, dimension);
        logger.info("Input weight: %s.".formatted(dimension));

        // input product length
        commonAction.sendKeys(productLength, dimension);
        logger.info("Input length: %s.".formatted(dimension));

        // input product width
        commonAction.sendKeys(productWidth, dimension);
        logger.info("Input width: %s.".formatted(dimension));

        // input product height
        commonAction.sendKeys(productHeight, dimension);
        logger.info("Input height: %s.".formatted(dimension));

    }

    void selectPlatform() {
        // App
        if (commonAction.getElement(platformApp).isSelected() != showOnApp)
            commonAction.clickJS(platformApp);

        // Web
        if (commonAction.getElement(platformWeb).isSelected() != showOnWeb)
            commonAction.clickJS(platformWeb);

        // In-store
        if (commonAction.getElement(platformInStore).isSelected() != uiIsShowInStore)
            commonAction.clickJS(platformInStore);

        // GoSocial
        if (commonAction.getElement(platformGoSocial).isSelected() != showInGoSocial)
            commonAction.clickJS(platformGoSocial);

    }

    void inputSEO() {
        // SEO title
        String title = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(seoTitle, title);

        // SEO description
        String description = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(seoDescription, description);

        // SEO keyword
        String keyword = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(seoKeywords, keyword);

        // SEO URL
        String url = "%s%s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(seoURL, url);
    }

    void productInfo(String name, boolean isIMEIProduct) throws Exception {
        inputProductName(name);
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
        productListingPrice = new ArrayList<>();
        productListingPrice.add(nextLong(MAX_PRICE));

        // get selling price
        productSellingPrice = new ArrayList<>();
        if (noDiscount) productSellingPrice.addAll(productListingPrice);
        else productSellingPrice.add(nextLong(productListingPrice.get(0)));

        // input listing price
        commonAction.sendKeys(productListingPriceWithoutVariation, String.valueOf(productListingPrice.get(0)));
        logger.info("Listing price: %s".formatted(String.format("%,d", productListingPrice.get(0))));

        // input selling price
        commonAction.sendKeys(productSellingPriceWithoutVariation, String.valueOf(productSellingPrice.get(0)));
        logger.info("Selling price: %s".formatted(String.format("%,d", productSellingPrice.get(0))));

        // input cost price
        long costPrice = nextLong(productSellingPrice.get(0));
        commonAction.sendKeys(productCostPriceWithoutVariation, String.valueOf(costPrice));
        logger.info("Cost price: %s.".formatted(String.format("%,d", costPrice)));

    }

    void addIMEIForEachBranch(String variationValue, List<Integer> branchStock, int varIndex) throws Exception {
        // select all branches
        commonAction.openDropdownJS(branchDropdownOnAddIMEIPopup, selectAllBranchesCheckboxOnAddIMEIPopup);
        logger.info("[Add IMEI popup] Open all branches dropdown.");

        if (!commonAction.isCheckedJS(selectAllBranchesCheckboxOnAddIMEIPopup))
            commonAction.clickJS(selectAllBranchesCheckboxOnAddIMEIPopup);
        else commonAction.closeDropdown(branchDropdownOnAddIMEIPopup, selectAllBranchesCheckboxOnAddIMEIPopup);
        logger.info("[Add IMEI popup] Select all branches.");

        // check [UI] add IMEI popup
        if (varIndex == 0) checkAddIMEIPopup();

        // input IMEI/Serial number for each branch
        for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
            String brName = brInfo.getActiveBranches().get(brIndex);
            int brStockIndex = brInfo.getBranchName().indexOf(brInfo.getActiveBranches().get(brIndex));
            for (int i = 0; i < branchStock.get(brStockIndex); i++) {
                String imei = "%s%s_IMEI_%s_%s\n".formatted(variationValue != null ? "%s_".formatted(variationValue) : "", brInfo.getActiveBranches().get(brIndex), epoch, i);
                commonAction.sendKeys(textBoxOnAddIMEIPopup, brIndex, imei);
                logger.info("Input IMEI: %s.".formatted(imei));
            }
            logger.info("%s[%s] Add IMEI, stock: %s.".formatted(variationValue == null ? "" : "[%s]".formatted(variationValue), brName, branchStock.get(brStockIndex)));
        }

        // save IMEI/Serial number
        commonAction.click(saveBtnOnAddIMEIPopup);
        logger.info("Close Add IMEI popup.");
    }

    public void inputWithoutVariationStock(int... branchStockQuantity) throws Exception {
        /* get without variation stock information */
        // get variation list
        variationList = new ArrayList<>();
        variationList.add(null);

        // get product stock quantity
        productStockQuantity = new HashMap<>();
        productStockQuantity.put(null, IntStream.range(0, brInfo.getBranchName().size()).mapToObj(i -> (branchStockQuantity.length > i) ? (brInfo.getActiveBranches().contains(brInfo.getBranchName().get(i)) ? branchStockQuantity[i] : 0) : 0).toList());

        /* input stock for each branch */
        if (manageByIMEI) {
            // open add IMEI popup
            commonAction.openPopupJS(branchStockWithoutVariation, 0, loc_dlgAddIMEI);
            logger.info("Open Add IMEI popup.");

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch(null, productStockQuantity.get(null), 0);
            logger.info("Complete add stock for IMEI product.");

        } else {
            // update stock for normal product
            IntStream.range(0, brInfo.getActiveBranches().size()).forEach(brIndex -> commonAction.sendKeys(branchStockWithoutVariation, brIndex, String.valueOf(productStockQuantity.get(null).get(brIndex))));
            logger.info("Complete update stock for Normal product.");
        }

    }

    void addNormalStockForEachBranch(List<Integer> branchStock, int varIndex) throws Exception {
        // select all branches
        commonAction.openDropdownJS(branchDropdownOnUpdateStockPopup, selectAllBranchesCheckboxOnUpdateStockPopup);
        logger.info("[Update stock popup] Open all branches dropdown.");

        // select all branches
        if (!commonAction.isCheckedJS(selectAllBranchesCheckboxOnUpdateStockPopup))
            commonAction.clickJS(selectAllBranchesCheckboxOnUpdateStockPopup);
        else commonAction.closeDropdown(branchDropdownOnUpdateStockPopup, selectAllBranchesCheckboxOnUpdateStockPopup);
        logger.info("[Update stock popup] Select all branches.");

        // check [UI] update stock popup
        if (varIndex == 0) checkUpdateStockPopup();

        // switch to change stock tab
        commonAction.click(changeTabOnUpdateStockPopup);

        // input stock quantity to visible stock input field
        int stock = Collections.max(branchStock) + 1;
        commonAction.sendKeys(quantityTextBoxOnUpdateStockPopup, String.valueOf(stock));

        // input stock for each branch
        for (int brIndex = brInfo.getActiveBranches().size() - 1; brIndex >= 0; brIndex--) {
            String brName = brInfo.getActiveBranches().get(brIndex);
            int brStockIndex = brInfo.getBranchName().indexOf(brName);
            commonAction.sendKeys(stockQuantityTextBoxOnUpdateStockPopup, brIndex, String.valueOf(branchStock.get(brStockIndex)));
            logger.info("%s[%s] Input stock: %s.".formatted(variationList.get(varIndex) == null ? "" : "[%s]".formatted(variationList.get(varIndex)), brName, branchStock.get(brStockIndex)));
        }
        // close Update stock popup
        commonAction.click(updateBtnOnPopup);
        logger.info("Close Update stock popup.");
    }

    void updateWithoutVariationStock(int... branchStockQuantity) throws Exception {
        /* get without variation stock information */
        // get variation list
        variationList = new ArrayList<>();
        variationList.add(null);

        // get product stock quantity
        productStockQuantity = new HashMap<>();
        productStockQuantity.put(null, IntStream.range(0, brInfo.getBranchName().size()).mapToObj(brIndex -> (branchStockQuantity.length > brIndex) ? (brInfo.getActiveBranches().contains(brInfo.getBranchName().get(brIndex)) ? branchStockQuantity[brIndex] : 0) : 0).toList());

        /* input stock for each branch */
        if (manageByIMEI) {
            // open Add IMEI popup
            commonAction.openDropdownJS(branchStockWithoutVariation, 0, loc_dlgAddIMEI);
            logger.info("Open Add IMEI popup.");

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch(null, productStockQuantity.get(null), 0);
            logger.info("Complete add stock for IMEI product.");
        } else {
            // open Update stock popup
            commonAction.openDropdownJS(branchStockWithoutVariation, 0, loc_dlgUpdateStock);
            logger.info("Open Update stock popup.");

            // add stock for each branch
            addNormalStockForEachBranch(productStockQuantity.get(null), 0);
            logger.info("Complete update stock for Normal product.");
        }

    }

    // Variation product
    public void addVariations() throws Exception {
        // generate variation map
        variationMap = new DataGenerator().randomVariationMap();
        logger.info("Variation map: %s".formatted(variationMap));

        // get variation list from variation map
        List<List<String>> varList = new ArrayList<>(variationMap.values());
        variationList = new ArrayList<>();
        varList.get(0).forEach(var -> variationList.add("%s_%s".formatted(storeInfo.getDefaultLanguage(), var)));
        if (varList.size() > 1)
            IntStream.range(1, varList.size())
                    .forEachOrdered(varIndex -> variationList = new DataGenerator()
                            .mixVariationValue(variationList, varList.get(varIndex), storeInfo.getDefaultLanguage()));
        logger.info("Variation list: %s".formatted(variationList));

        // delete old variation
        List<WebElement> deleteVariationIcons = commonAction.getListElement(loc_btnDeleteVariation);
        IntStream.iterate(deleteVariationIcons.size() - 1, index -> index >= 0, index -> index - 1).forEach(index -> commonAction.clickJS(loc_btnDeleteVariation, index));
        logger.info("Remove old variation.");

        System.out.println(variationMap);

        // check [UI] Add variation button
        checkAddVariationBtn();

        // click add variation button
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(varIndex -> commonAction.clickJS(addVariationBtn));
        logger.info("Add new variation group.");

        // input variation name and variation value
        for (int varIndex = 0; varIndex < variationMap.keySet().size(); varIndex++) {
            String varName = "%s_%s".formatted(storeInfo.getDefaultLanguage(), variationMap.keySet().stream().toList().get(varIndex));
            logger.info("Input variation name: %s".formatted(varName));

            // check [UI] after click Add variation button
            if (varIndex == 0) {
                checkVariationInformation();
                checkVariationValuePlaceholder();
            }

            // input variation name
            commonAction.sendKeys(variationName, varIndex, varName);
            WebElement valueElement = commonAction.getElement(variationValue, varIndex);

            // input variation value
            List<String> varValueList = variationMap.get(varName.split("_", 2)[1]);
            for (String varValue : varValueList) {
                // get variation value
                String var = "%s_%s".formatted(storeInfo.getDefaultLanguage(), varValue);

                // input variation value
                valueElement.sendKeys(var);

                // wait suggestion
                commonAction.sleepInMiliSecond(500);

                // complete input variation value
                valueElement.sendKeys(Keys.chord(Keys.ENTER));

                // log
                logger.info("Input variation value: %s".formatted(var));
            }
        }

        commonAction.click(variationsLabel);

        // check [UI] after add variation (Check variation table column)
        checkVariationTable();
    }


    void inputVariationPrice() throws Exception {
        // get listing, selling price
        productListingPrice = new ArrayList<>();
        productSellingPrice = new ArrayList<>();
        IntStream.range(0, variationList.size()).forEachOrdered(i -> {
            productListingPrice.add(nextLong(MAX_PRICE));
            if (noDiscount) productSellingPrice.add(productListingPrice.get(i));
            else productSellingPrice.add(nextLong(productListingPrice.get(i)));
        });

        // select all variation
        if (!commonAction.isCheckedJS(selectAllCheckboxOnVariationTable))
            commonAction.clickJS(selectAllCheckboxOnVariationTable);

        // check [UI] after select all variations
        checkBulkActionsOnVariationTable();

        // open list action dropdown
        commonAction.clickJS(selectActionLinkTextOnVariationTable);

        // check [UI] check list actions
        checkListActionsOnVariationTable();

        // open Update price popup
        commonAction.openPopupJS(actionOnVariationTable, 0, popup);

        // check [UI] product price table
        checkUpdatePricePopup();

        // input product price
        IntStream.range(0, variationList.size()).forEachOrdered(varIndex -> {
            // get current variation
            String variation = variationList.get(varIndex);

            // input listing price
            long listingPrice = productListingPrice.get(varIndex);
            commonAction.sendKeys(listingPriceOnUpdatePricePopup, varIndex, String.valueOf(String.format("%,d", listingPrice)));
            logger.info("[%s] Listing price: %s.".formatted(variation, String.format("%,d", listingPrice)));

            // input selling price
            long sellingPrice = productSellingPrice.get(varIndex);
            commonAction.sendKeys(sellingPriceOnUpdatePricePopup, varIndex, String.valueOf(sellingPrice));
            logger.info("[%s] Selling price: %s.".formatted(variation, String.format("%,d", sellingPrice)));

            // input costPrice
            long costPrice = nextLong(sellingPrice);
            commonAction.sendKeys(costPriceOnUpdatePricePopup, varIndex, String.valueOf(costPrice));
            logger.info("[%s] Cost price: %s.".formatted(variation, String.format("%,d", costPrice)));
        });


        // click around
        commonAction.click(titleOfUpdatePricePopup);

        // close Update price popup
        commonAction.closePopup(updateBtnOnPopup);
    }

    void inputVariationStock(int increaseNum, int... branchStockQuantity) throws Exception {
        // get product stock quantity
        productStockQuantity = new HashMap<>();
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            List<Integer> variationStock = new ArrayList<>();
            // set branch stock
            for (int branchIndex = 0; branchIndex < brInfo.getBranchName().size(); branchIndex++) {
                variationStock.add((branchStockQuantity.length > branchIndex) ? ((brInfo.getActiveBranches().contains(brInfo.getBranchName().get(branchIndex)) ? (branchStockQuantity[branchIndex] + (varIndex * increaseNum)) : 0)) : 0);
            }
            productStockQuantity.put(variationList.get(varIndex), variationStock);
        }

        // input product stock quantity
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update stock popup
            commonAction.clickJS(stockQuantityOnVariationTable, varIndex);

            if (manageByIMEI) {
                addIMEIForEachBranch(variationList.get(varIndex), productStockQuantity.get(variationList.get(varIndex)), varIndex);
            } else addNormalStockForEachBranch(productStockQuantity.get(variationList.get(varIndex)), varIndex);
        }
    }

    void inputVariationSKU() throws Exception {
        // input SKU
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update SKU popup
            commonAction.openPopupJS(skuOnVariationTable, varIndex, loc_dlgUpdateSKU);

            // check [UI] SKU popup
            if (varIndex == 0) checkUpdateSKUPopup();

            // input SKU for each branch
            for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
                String sku = "SKU_%s_%s_%s".formatted(variationList.get(varIndex), brInfo.getActiveBranches().get(brIndex), epoch);
                commonAction.sendKeys(textBoxOnUpdateSKUPopup, brIndex, sku);
                logger.info("[Update SKU popup] Input SKU: %s.".formatted(sku));
            }

            // click around
            commonAction.click(titleOfUpdateSKUPopup);

            // close Update SKU popup
            commonAction.closePopup(updateBtnOnPopup);
        }
    }

    void uploadVariationImage(String... imageFile) throws Exception {
        // upload image for each variation
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update SKU popup
            commonAction.openPopupJS(uploadImageBtnOnVariationTable, varIndex, popup);
            logger.info("Open upload variation image popup.");

            // check [UI] update image popup
            if (varIndex == 0) checkUpdateVariationImagePopup();

            // upload image
            for (String imgFile : imageFile) {
                Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"),
                        "/src/main/resources/uploadfile/product_images/%s".formatted(imgFile).replace("/", File.separator)));
                commonAction.uploads(uploadBtnOnUpdateImagePopup, filePath.toString());
                logger.info("[Upload image popup] Upload images, file path: %s.".formatted(filePath));
            }

            // close Update image popup
            commonAction.closePopup(updateBtnOnPopup);
        }
    }

    /* Active/Deactivate product */
    public ProductPage changeProductStatus(String status, int productID) {
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        if (!status.equals(productInfo.getBhStatus())) {
            // log
            logger.info("Product id: %s".formatted(productID));

            // navigate to product detail page by URL
            driver.get("%s%s".formatted(DOMAIN, updateProductPath.formatted(productID)));

            // wait page loaded
            commonAction.getElement(uiSEOSetting);

            // change status
            commonAction.clickJS(deactivateBtn);

            logger.info("change product status from %s to %s.".formatted(productInfo.getBhStatus(), status));
        }
        return this;
    }

    public void deleteProduct(int productID) throws Exception {
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        if (!productInfo.isDeleted()) {
            // log
            logger.info("Product id: %s".formatted(productID));

            // navigate to product detail page by URL
            driver.get("%s%s".formatted(DOMAIN, updateProductPath.formatted(productID)));

            // wait page loaded
            commonAction.getElement(uiSEOSetting);

            // open Confirm delete popup
            commonAction.openPopupJS(deleteBtn, popup);

            // check UI
            checkUIConfirmDeleteProductPopup();

            // close confirm delete product popup
            commonAction.closePopup(okBtnOnConfirmDeletePopup);
        }
    }


    /* Complete create/update product */
    void completeCreateProduct() {
        // save changes
        commonAction.openPopupJS(saveBtn, loc_dlgNotification);

        // close notification popup
        commonAction.closePopup(closeBtnOnNotificationPopup);

        // wait product list page is loaded
        commonAction.waitURLShouldBeContains("/product/list");

        // search product by name
        commonAction.sendKeys(searchBox, name);

        // wait api return result
        commonAction.sleepInMiliSecond(1000);

        // wait api return list product
        productID = Integer.parseInt(commonAction.getText(productId));

        // log
        logger.info("Product id: %s".formatted(productID));
    }

    void completeUpdateProduct() {
        // save changes
        commonAction.openPopupJS(saveBtn, loc_dlgNotification);

        // if update product failed, try again
        if (!commonAction.getListElement(failPopup).isEmpty()) {
            // close update product failed popup
            commonAction.closePopup(closeBtnOnNotificationPopup);

            // save changes again
            commonAction.openPopupJS(saveBtn, loc_dlgNotification);
        }

        // if that still failed, end test.
        Assert.assertTrue(commonAction.getListElement(failPopup).isEmpty(), "[Failed][Update product] Can not update product.");

        // close notification popup
        commonAction.closePopup(closeBtnOnNotificationPopup);
    }

    public void configWholesaleProduct() throws Exception {
        if (hasModel) new WholesaleProductPage(driver, loginInformation)
                .navigateToWholesaleProductPage()
                .getWholesaleProductInfo()
                .addWholesaleProductVariation();
        else new WholesaleProductPage(driver, loginInformation)
                .navigateToWholesaleProductPage()
                .getWholesaleProductInfo()
                .addWholesaleProductWithoutVariation();
    }

    public ProductPage configConversionUnit() throws Exception {
        if (hasModel) new ConversionUnitPage(driver, loginInformation)
                .navigateToConversionUnitPage()
                .addConversionUnitVariation();
        else new ConversionUnitPage(driver, loginInformation)
                .navigateToConversionUnitPage()
                .addConversionUnitWithoutVariation();
        return this;
    }

    /* Create product */
    public ProductPage createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) throws Exception {
        hasModel = false;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, isIMEIProduct);
        inputWithoutVariationPrice();
        inputWithoutVariationStock(branchStock);
        inputWithoutVariationProductSKU();
        completeCreateProduct();

        return this;
    }

    public ProductPage createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) throws Exception {
        hasModel = true;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, isIMEIProduct);
        addVariations();
        uploadVariationImage("img.jpg");
        inputVariationPrice();
        inputVariationStock(increaseNum, branchStock);
        inputVariationSKU();
        completeCreateProduct();

        return this;
    }

    /* Update Product */
    public ProductPage updateWithoutVariationProduct(int... newBranchStock) throws Exception {
        hasModel = false;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), productInfo.isManageInventoryByIMEI() ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, productInfo.isManageInventoryByIMEI());
        inputWithoutVariationPrice();
        updateWithoutVariationStock(newBranchStock);
        updateWithoutVariationProductSKU();
        completeUpdateProduct();

        return this;
    }

    public ProductPage updateVariationProduct(int newIncreaseNum, int... newBranchStock) throws Exception {
        hasModel = true;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), productInfo.isManageInventoryByIMEI() ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, productInfo.isManageInventoryByIMEI());
        addVariations();
        uploadVariationImage("img.jpg");
        inputVariationPrice();
        inputVariationStock(newIncreaseNum, newBranchStock);
        inputVariationSKU();
        completeUpdateProduct();

        return this;
    }

    public void changeVariationStatus(int productID) {
        // update variation product name and description
        // get current product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // update variation status
        for (String barcode : productInfo.getVariationModelList())
            new VariationDetailPage(driver, barcode, productInfo, loginInformation).changeVariationStatus(List.of("ACTIVE", "INACTIVE").get(nextInt(2)));
    }

    public void editVariationTranslation(int productID) throws Exception {
        // update variation product name and description
        // get current product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        for (String barcode : productInfo.getVariationModelList())
            new VariationDetailPage(driver, barcode, productInfo, loginInformation).updateVariationProductNameAndDescription(productInfo.getVariationStatus().get(productInfo.getVariationModelList().indexOf(barcode)));
    }

    /* Edit translation */
    void addTranslation(String language, String languageName, ProductInfo productInfo, int langIndex) throws Exception {
        // open edit translation popup
        if (storeInfo.getStoreLanguageList().size() > 1) {
            // open edit translation popup
            commonAction.openPopupJS(editTranslationText, loc_dlgEditTranslation);
            logger.info("Open translation popup.");

            // convert languageCode to languageName
            if (language.equals("en") && (ProductPage.language.equals("vi") || ProductPage.language.equals("VIE")))
                languageName = "Tiếng Anh";

            // select language for translation
            if (!commonAction.getText(loc_dlgEditTranslation_selectedLanguage).equals(languageName)) {
                // open language dropdown
                commonAction.openPopupJS(loc_dlgEditTranslation_selectedLanguage, By.cssSelector(".product-translate .uik-select__optionList"));

                // select language
                commonAction.click(By.xpath(str_dlgEditTranslation_languageInDropdown.formatted(languageName)));
            }
            logger.info("Add translation for '%s' language.".formatted(languageName));

            // check UI
            if (langIndex == 0) checkEditTranslationPopup();

            // input translate product name
            name = "[%s]%s%s".formatted(language, productInfo.isManageInventoryByIMEI() ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
            commonAction.sendKeys(productNameOnEditTranslationPopup, name);
            logger.info("Input translation for product name: %s.".formatted(name));


            // input translate product description
            description = "[%s] product description".formatted(language);
            commonAction.sendKeys(productDescriptionOnEditTranslationPopup, description);
            logger.info("Input translation for product description: %s.".formatted(description));

            // input variation if any
            if (productInfo.isHasModel()) {
                List<String> variationName = IntStream.range(0, productInfo.getVariationNameMap().get(storeInfo.getDefaultLanguage()).split("\\|").length).mapToObj(i -> "%s_var%s".formatted(language, i + 1)).toList();
                List<String> variationValue = new ArrayList<>();
                List<String> variationList = productInfo.getVariationListMap().get(storeInfo.getDefaultLanguage());
                variationList.stream().map(varValue -> varValue.replace(storeInfo.getDefaultLanguage(), language).split("\\|")).forEach(varValueList -> Arrays.stream(varValueList).filter(varValue -> !variationValue.contains(varValue)).forEach(var -> variationValue.add(var.contains("%s_".formatted(language)) ? var : "%s_%s".formatted(language, var))));
                Collections.sort(variationList);
                // input variation name
                IntStream.range(0, variationName.size()).forEachOrdered(varIndex -> commonAction.sendKeys(variationNameOnEditTranslationPopup, varIndex, variationName.get(varIndex)));
                // input variation value
                IntStream.range(0, variationValue.size()).forEachOrdered(varIndex -> commonAction.sendKeys(variationValueOnEditTranslationPopup, varIndex, variationValue.get(varIndex)));
            }

            // input SEO
            // input title
            String title = "[%s] Auto - SEO Title - %s".formatted(language, epoch);
            commonAction.sendKeys(seoTitleOnEditTranslationPopup, title);
            logger.info("Input translation for SEO title: %s.".formatted(title));

            // input description
            String description = "[%s] Auto - SEO Description - %s".formatted(language, epoch);
            commonAction.sendKeys(seoDescriptionOnEditTranslationPopup, description);
            logger.info("Input translation for SEO description: %s.".formatted(description));

            // input keywords
            String keywords = "[%s] Auto - SEO Keyword - %s".formatted(language, epoch);
            commonAction.sendKeys(seoKeywordsOnEditTranslationPopup, keywords);
            logger.info("Input translation for SEO keywords: %s.".formatted(keywords));

            // input url
            String url = "%s%s".formatted(language, epoch);
            commonAction.sendKeys(seoURLOnEditTranslationPopup, url);
            logger.info("Input translation for SEO url: %s.".formatted(url));

            // save changes
            commonAction.openPopupJS(saveBtnOnEditTranslationPopup, loc_dlgToast);
            logger.info("Save translation");

            // close edit translation popup
            commonAction.closePopup(loc_dlgEditTranslation_icnClose);
            logger.info("Close edit translation popup.");
        }
    }

    public void editTranslation(int productID) throws Exception {
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath.formatted(productID)));
        logger.info("Navigate to product detail page, productId: %s.".formatted(productID));

        // get online store language
        List<String> langCodeList = new ArrayList<>(storeInfo.getStoreLanguageList());
        List<String> langNameList = new ArrayList<>(storeInfo.getStoreLanguageName());
        langCodeList.remove(storeInfo.getDefaultLanguage());
        logger.info("List languages are not translated: %s.".formatted(langCodeList.toString()));

        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // add translation
        for (int langIndex = 0; langIndex < langCodeList.size(); langIndex++) {
            String langCode = langCodeList.get(langIndex);
            String langName = langNameList.get(storeInfo.getStoreLanguageList().indexOf(langCode));
            addTranslation(langCode, langName, productInfo, langIndex);
        }

        // save edit translation
        commonAction.openPopupJS(saveBtn, loc_dlgNotification);
        logger.info("Complete translation.");

        // close Notification popup
        commonAction.closePopup(closeBtnOnNotificationPopup);
        logger.info("Close Notification popup.");
    }

    public void uncheckWebPlatform() {
        showOnWeb = false;

        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath.formatted(productInfo.getProductID())));

        // wait page loaded
        commonAction.getElement(uiSEOSetting);

        logger.info("Navigate to product page and edit translation.");

        selectPlatform();

        completeUpdateProduct();
    }

    /* check UI function */
    void checkUICRHeaderProductPage() throws Exception {
        // check Go back to product list link text
        String dbGoBackToProductList = commonAction.getText(goBackToProductListText);
        String ppGoBackToProductList = getPropertiesValueByDBLang("products.allProducts.createProduct.header.goBackToProductList", language);
        assertCustomize.assertEquals(dbGoBackToProductList, ppGoBackToProductList, "[Failed][Header] Go back to product list link text should be %s, but found %s.".formatted(ppGoBackToProductList, dbGoBackToProductList));
        logger.info("[UI][%s] Check Header - Go back to product list.".formatted(language));

        // check header page title
        String dbHeaderPageTitle = commonAction.getText(pageTitleText);
        String ppHeaderPageTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.header.headerPageTitle", language);
        assertCustomize.assertEquals(dbHeaderPageTitle, ppHeaderPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppHeaderPageTitle, dbHeaderPageTitle));
        logger.info("[UI][%s] Check Header - Header page title.".formatted(language));

        // check header Save button
        String dbHeaderSaveBtn = commonAction.getText(saveText);
        String ppHeaderSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.saveBtn", language);
        assertCustomize.assertEquals(dbHeaderSaveBtn, ppHeaderSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppHeaderSaveBtn, dbHeaderSaveBtn));
        logger.info("[UI][%s] Check Header - Save button .".formatted(language));

        // check header Cancel button
        String dbHeaderCancelBtn = commonAction.getText(cancelText);
        String ppHeaderCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.cancelBtn", language);
        assertCustomize.assertEquals(dbHeaderCancelBtn, ppHeaderCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppHeaderCancelBtn, dbHeaderCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));
    }

    void checkUIUPHeaderProductPage() throws Exception {
        // check Go back to product list link text
        String dbGoBackToProductList = commonAction.getText(goBackToProductListText);
        String ppGoBackToProductList = getPropertiesValueByDBLang("products.allProducts.createProduct.header.goBackToProductList", language);
        assertCustomize.assertEquals(dbGoBackToProductList, ppGoBackToProductList, "[Failed][Header] Go back to product list link text should be %s, but found %s.".formatted(ppGoBackToProductList, dbGoBackToProductList));
        logger.info("[UI][%s] Check Header - Go back to product list.".formatted(language));

        // check header Edit translation button
        if (storeInfo.getStoreLanguageList().size() > 1) {
            String dbEditTranslationBtn = commonAction.getText(editTranslationText);
            String ppEditTranslationBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.header.editTranslation", language);
            assertCustomize.assertEquals(dbEditTranslationBtn, ppEditTranslationBtn, "[Failed][Header] Edit translation button should be %s, but found %s.".formatted(ppEditTranslationBtn, dbEditTranslationBtn));
            logger.info("[UI][%s] Check Header - Edit translation button.".formatted(language));
        }

        // check header Save button
        String dbHeaderSaveBtn = commonAction.getText(saveText);
        String ppHeaderSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.saveBtn", language);
        assertCustomize.assertEquals(dbHeaderSaveBtn, ppHeaderSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppHeaderSaveBtn, dbHeaderSaveBtn));
        logger.info("[UI][%s] Check Header - Save button .".formatted(language));

        // check header Cancel button
        String dbHeaderCancelBtn = commonAction.getText(cancelText);
        String ppHeaderCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.cancelBtn", language);
        assertCustomize.assertEquals(dbHeaderCancelBtn, ppHeaderCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppHeaderCancelBtn, dbHeaderCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));

        // check header Deactivate button
        String dbDeactivateBtn = commonAction.getText(deactivateText);
        String ppDeactivateBtn = getPropertiesValueByDBLang(productInfo.getBhStatus().equals("ACTIVE") ? "products.allProducts.updateProduct.header.deactivateBtn" : "products.allProducts.updateProduct.header.activeBtn", language);
        assertCustomize.assertEquals(dbDeactivateBtn, ppDeactivateBtn, "[Failed][Header] Deactivate button should be %s, but found %s.".formatted(ppDeactivateBtn, dbDeactivateBtn));
        logger.info("[UI][%s] Check Header - Deactivate button.".formatted(language));

        // check header Delete button
        String dbDeleteBtn = commonAction.getText(deleteText);
        String ppDeleteBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.header.deleteBtn", language);
        assertCustomize.assertEquals(dbDeleteBtn, ppDeleteBtn, "[Failed][Header] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Header - Delete button.".formatted(language));
    }

    void checkUIInformation() throws Exception {
        // check product information
        String dbProductInformation = commonAction.getText(productInformationLabel);
        String ppProductInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.title", language);
        assertCustomize.assertEquals(dbProductInformation, ppProductInformation, "[Failed][Body] Product information should be %s, but found %s.".formatted(ppProductInformation, dbProductInformation));
        logger.info("[UI][%s] Check Body - Product Information.".formatted(language));

        // check product name
        String dbProductName = commonAction.getText(productNameLabel);
        String ppProductName = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.productName", language);
        assertCustomize.assertEquals(dbProductName, ppProductName, "[Failed][Body] Product name should be %s, but found %s.".formatted(ppProductName, dbProductName));
        logger.info("[UI][%s] Check Body - Product name.".formatted(language));

        // check product name error
        commonAction.click(productName);
        commonAction.getElement(productName).sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        commonAction.click(productDescription);
        String dbProductNameError = commonAction.getText(productBlankErrorMessage);
        String ppProductNameError = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.productNameError", language);
        assertCustomize.assertEquals(dbProductNameError, ppProductNameError, "[Failed][Body] Product name error should be %s, but found %s.".formatted(ppProductNameError, dbProductNameError));
        logger.info("[UI][%s] Check Body - Product name error.".formatted(language));

        // check product description
        String dbProductDescription = commonAction.getText(productDescriptionLabel);
        String ppProductDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.description", language);
        assertCustomize.assertEquals(dbProductDescription, ppProductDescription, "[Failed][Body] Product description should be %s, but found %s.".formatted(ppProductDescription, dbProductDescription));
        logger.info("[UI][%s] Check Body - Product description.".formatted(language));
    }

    void checkUIImages() throws Exception {
        // remove old product image
        List<WebElement> removeImageIcons = commonAction.getListElement(removeProductImageBtn);
        if (!removeImageIcons.isEmpty())
            IntStream.iterate(removeImageIcons.size() - 1, iconIndex -> iconIndex >= 0, iconIndex -> iconIndex - 1).forEach(iconIndex -> commonAction.clickJS(removeProductImageBtn, iconIndex));

        // check images title
        String dbImages = commonAction.getText(imagesLabel);
        String ppImages = getPropertiesValueByDBLang("products.allProducts.createProduct.images.title", language);
        assertCustomize.assertEquals(dbImages, ppImages, "[Failed][Body] Images should be %s, but found %s.".formatted(ppImages, dbImages));
        logger.info("[UI][%s] Check Body - Images.".formatted(language));

        // check images drag and drop placeholder
        String dbImagesDragAndDrop = commonAction.getText(dragAndDropText);
        String ppImagesDragAndDrop = getPropertiesValueByDBLang("products.allProducts.createProduct.images.dragAndDrop", language);
        assertCustomize.assertTrue(Objects.equals(dbImagesDragAndDrop, ppImagesDragAndDrop), "[Failed][Body] Images drag and drop placeholder should be %s, but found %s.".formatted(ppImagesDragAndDrop, dbImagesDragAndDrop));
        logger.info("[UI][%s] Check Body - Images drag and drop placeholder.".formatted(language));
    }

    void checkUIPricing() throws Exception {
        // check pricing title
        String dbProductPriceTitle = commonAction.getText(pricingLabel);
        String ppProductPriceTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.title", language);
        assertCustomize.assertEquals(dbProductPriceTitle, ppProductPriceTitle, "[Failed][Body] Product price title should be %s, but found %s.".formatted(ppProductPriceTitle, dbProductPriceTitle));
        logger.info("[UI][%s] Check Body - Product price title.".formatted(language));

        // check product price for without variation product
        if (!commonAction.getListElement(listingPriceLabel).isEmpty()) {
            // check product listing price
            String dbProductListingPrice = commonAction.getText(listingPriceLabel);
            String ppProductListingPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.listingPrice", language);
            assertCustomize.assertEquals(dbProductListingPrice, ppProductListingPrice, "[Failed][Body] Product listing price should be %s, but found %s.".formatted(ppProductListingPrice, dbProductListingPrice));
            logger.info("[UI][%s] Check Body - Product listing price.".formatted(language));

            // check product selling price
            String dbProductSellingPrice = commonAction.getText(sellingPriceLabel);
            String ppProductSellingPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.sellingPrice", language);
            assertCustomize.assertEquals(dbProductSellingPrice, ppProductSellingPrice, "[Failed][Body] Product selling price should be %s, but found %s.".formatted(ppProductSellingPrice, dbProductSellingPrice));
            logger.info("[UI][%s] Check Body - Product selling price.".formatted(language));

            // check product cost price
            String dbProductCostPrice = commonAction.getText(costPriceLabel);
            String ppProductCostPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.costPrice", language);
            assertCustomize.assertEquals(dbProductCostPrice, ppProductCostPrice, "[Failed][Body] Product cost price should be %s, but found %s.".formatted(ppProductCostPrice, dbProductCostPrice));
            logger.info("[UI][%s] Check Body - Product cost price.".formatted(language));
        }

        // check VAT title
        String dbVATTitle = commonAction.getText(vatLabel);
        String ppVATTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.vat", language);
        assertCustomize.assertEquals(dbVATTitle, ppVATTitle, "[Failed][Body] VAT title should be %s, but found %s.".formatted(ppVATTitle, dbVATTitle));
        logger.info("[UI][%s] Check Body - VAT title.".formatted(language));

        // show as listing product on storefront checkbox
        String dbShowAsListingProduct = commonAction.getText(showAsListingProductOnStoreFrontText);
        String ppShowAsListingProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.showAsListingProductCheckbox", language);
        assertCustomize.assertEquals(dbShowAsListingProduct, ppShowAsListingProduct, "[Failed][Body] Show as listing product on storefront label should be %s, but found %s.".formatted(ppShowAsListingProduct, dbShowAsListingProduct));
        logger.info("[UI][%s] Check Body - Show as listing product on storefront checkbox.".formatted(language));
    }

    void checkUIVariations() throws Exception {
        // check variations title
        String dbVariations = commonAction.getText(variationsLabel);
        String ppVariations = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.title", language);
        assertCustomize.assertEquals(dbVariations, ppVariations, "[Failed][Body] Variations title should be %s, but found %s.".formatted(ppVariations, dbVariations));
        logger.info("[UI][%s] Check Body - Variations title.".formatted(language));

        // check variation description
        String dbVariationDescription = commonAction.getText(variationDescriptionText);
        String ppVariationDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationDescription", language);
        assertCustomize.assertTrue(dbVariationDescription.equals(ppVariationDescription), "[Failed][Body] Variation description should be %s, but found %s.".formatted(ppVariationDescription, dbVariationDescription));
        logger.info("[UI][%s] Check Body - Variation description.".formatted(language));
    }

    void checkAddVariationBtn() throws Exception {
        // check add variation button
        String dbAddVariationBtn = commonAction.getText(addVariationText);
        String ppAddVariationBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addVariationBtn", language);
        assertCustomize.assertEquals(dbAddVariationBtn, ppAddVariationBtn, "[Failed][Body] Add variation button should be %s, but found %s.".formatted(ppAddVariationBtn, dbAddVariationBtn));
        logger.info("[UI][%s] Check Body - Add variation button.".formatted(language));
    }

    void checkVariationInformation() throws Exception {
        //check variation name
        String dbVariationName = commonAction.getText(variationNameLabel);
        String ppVariationName = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationName", language);
        assertCustomize.assertEquals(dbVariationName, ppVariationName, "[Failed][Body] Variation name should be %s, but found %s.".formatted(ppVariationName, dbVariationName));
        logger.info("[UI][%s] Check Body - Variation name.".formatted(language));

        // check variation value
        String dbVariationValue = commonAction.getText(variationValueLabel);
        String ppVariationValue = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationValue", language);
        assertCustomize.assertEquals(dbVariationValue, ppVariationValue, "[Failed][Body] Variation value should be %s, but found %s.".formatted(ppVariationValue, dbVariationValue));
        logger.info("[UI][%s] Check Body - Variation value.".formatted(language));
    }

    void checkVariationValuePlaceholder() throws Exception {
        // check variation value placeholder
        String dbVariationValuePlaceholder = commonAction.getText(variationValuePlaceholder, 0);
        String ppVariationValuePlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationValuePlaceholder", language);
        assertCustomize.assertEquals(dbVariationValuePlaceholder, ppVariationValuePlaceholder, "[Failed][Body] Variation value placeholder should be %s, but found %s.".formatted(ppVariationValuePlaceholder, dbVariationValuePlaceholder));
        logger.info("[UI][%s] Check Body - Variation value placeholder.".formatted(language));
    }

    void checkBulkActionsOnVariationTable() throws Exception {
        // check number of selected variation
        String[] dbNumberOfSelectedVariation = commonAction.getText(numberOfSelectedVariationsText).split("\n")[0].split("\\d+");
        String[] ppNumberOfSelectedVariation = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.numberOfSelectedVariations", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedVariation, ppNumberOfSelectedVariation, "[Failed][Body][Variation] Number of selected variations should be %s, but found %s.".formatted(ppNumberOfSelectedVariation, dbNumberOfSelectedVariation));
        logger.info("[UI][%s] Check Body - Number of selected variations on variation table.".formatted(language));

        // check Select action button
        String dbSelectActionLinkText = commonAction.getText(selectActionText);
        String ppSelectActionLinkText = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.selectAction", language);
        assertCustomize.assertEquals(dbSelectActionLinkText, ppSelectActionLinkText, "[Failed][Body][Variation table] Select action link text should be %s, but found %s.".formatted(ppSelectActionLinkText, dbSelectActionLinkText));
        logger.info("[UI][%s] Check Body - Select action link text on variation table".formatted(language));
    }

    void checkListActionsOnVariationTable() throws Exception {
        // check list actions
        List<String> dbListActions = commonAction.getListElement(listActions).stream().map(WebElement::getText).toList();
        List<String> ppListActions = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.2", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.3", language));
        assertCustomize.assertEquals(dbListActions, ppListActions, "[Failed][Body][Variation table] List action should be %s, but found %s.".formatted(ppListActions, dbListActions));
        logger.info("[UI][%s] Check Body - List action on variation table.".formatted(language));
    }

    void checkVariationTable() throws Exception {
        // check variation table column
        List<String> dbVariationTableImageColumn = commonAction.getListElement(variationTableColumnLabel).stream().map(WebElement::getText).toList();
        List<String> ppVariationTableImageColumn = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.2", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.3", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.4", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.5", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.column.6", language));
        assertCustomize.assertEquals(dbVariationTableImageColumn, ppVariationTableImageColumn, "[Failed][Body] Variation table column should be %s, but found %s.".formatted(ppVariationTableImageColumn, dbVariationTableImageColumn));
        logger.info("[UI][%s] Check Body - Variation table column.".formatted(language));

        // check Edit SKU link text
        if (!commonAction.getListElement(editSKUTextOnVariationTable).isEmpty()) {
            String dbEditSKULinkText = commonAction.getText(editSKUTextOnVariationTable, 0);
            String ppEditSKULinkText = getPropertiesValueByDBLang("products.allProducts.updateProduct.variations.variationTable.editSKU", language);
            assertCustomize.assertEquals(dbEditSKULinkText, ppEditSKULinkText, "[Failed][Variation table] Edit SKU should be %s, but found %s.".formatted(ppEditSKULinkText, dbEditSKULinkText));
            logger.info("[UI][%s] Check Variation table - Edit SKU.".formatted(language));
        }
    }

    void checkEditTranslationPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(titleOfEditTranslationPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Edit translation popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Edit translation popup - Title.".formatted(language));

        // check information
        String dbInformation = commonAction.getText(informationLabelOnEditTranslationPopup);
        String ppInformation = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.information", language);
        assertCustomize.assertEquals(dbInformation, ppInformation, "[Failed][Edit translation popup] Information should be %s, but found %s.".formatted(ppInformation, dbInformation));
        logger.info("[UI][%s] Check Edit translation popup - Information.".formatted(language));

        // check product name
        String dbProductName = commonAction.getText(productNameLabelOnEditTranslationPopup);
        String ppProductName = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.productName", language);
        assertCustomize.assertEquals(dbProductName, ppProductName, "[Failed][Edit translation popup] Product name should be %s, but found %s.".formatted(ppProductName, dbProductName));
        logger.info("[UI][%s] Check Edit translation popup - Product name.".formatted(language));

        // check product description
        String dbProductDescription = commonAction.getText(productDescriptionLabelOnEditTranslationPopup);
        String ppProductDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.productDescription", language);
        assertCustomize.assertEquals(dbProductDescription, ppProductDescription, "[Failed][Edit translation popup] Product description should be %s, but found %s.".formatted(ppProductDescription, dbProductDescription));
        logger.info("[UI][%s] Check Edit translation popup - Product description.".formatted(language));

        // check variation if any
        if (productInfo.isHasModel()) {
            String dbVariation = commonAction.getText(variationLabelOnEditTranslationPopup);
            String ppVariation = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.variation", language);
            assertCustomize.assertEquals(dbVariation, ppVariation, "[Failed][Edit translation popup] Variation should be %s, but found %s.".formatted(ppVariation, dbVariation));
            logger.info("[UI][%s] Check Edit translation popup - Variation.".formatted(language));
        }

        // check SEO setting
        String dbSEOSetting = commonAction.getText(seoSettingLabelOnEditTranslationPopup);
        String ppSEOSetting = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoSetting", language);
        assertCustomize.assertEquals(dbSEOSetting, ppSEOSetting, "[Failed][Edit translation popup] SEO setting should be %s, but found %s.".formatted(ppSEOSetting, dbSEOSetting));
        logger.info("[UI][%s] Check Edit translation popup - SEO Setting.".formatted(language));

        // check Live preview
        String dbLivePreview = commonAction.getText(livePreviewLabelOnEditTranslationPopup);
        String ppLivePreview = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.livePreview", language);
        assertCustomize.assertEquals(dbLivePreview, ppLivePreview, "[Failed][Edit translation popup] Live preview should be %s, but found %s.".formatted(ppLivePreview, dbLivePreview));
        logger.info("[UI][%s] Check Edit translation popup - Live Preview.".formatted(language));

        // check Live preview tooltips
        commonAction.hoverActions(livePreviewTooltipsOnEditTranslationPopup);
        String dbLivePreviewTooltips = commonAction.getAttribute(livePreviewTooltipsOnEditTranslationPopup, "data-original-title");
        String ppLivePreviewTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.livePreviewTooltips", language);
        assertCustomize.assertEquals(dbLivePreviewTooltips, ppLivePreviewTooltips, "[Failed][Edit translation popup] Live preview tooltips should be %s, but found %s.".formatted(ppLivePreviewTooltips, dbLivePreviewTooltips));
        logger.info("[UI][%s] Check Edit translation popup - Live Preview Tooltips.".formatted(language));

        // check SEO title
        String dbSEOTitle = commonAction.getText(seoTitleLabelOnEditTranslationPopup);
        String ppSEOTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoTitle", language);
        assertCustomize.assertEquals(dbSEOTitle, ppSEOTitle, "[Failed][Edit translation popup] SEO title should be %s, but found %s.".formatted(ppSEOTitle, dbSEOTitle));
        logger.info("[UI][%s] Check Edit translation popup - SEO Title.".formatted(language));

        // check SEO title tooltips
        commonAction.hoverActions(seoTitleTooltipsOnEditTranslationPopup);
        String dbSEOTitleTooltips = commonAction.getAttribute(seoTitleTooltipsOnEditTranslationPopup, "data-original-title");
        String ppSEOTitleTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoTitleTooltips", language);
        assertCustomize.assertEquals(dbSEOTitleTooltips, ppSEOTitleTooltips, "[Failed][Edit translation popup] SEO title tooltips should be %s, but found %s.".formatted(ppSEOTitleTooltips, dbSEOTitleTooltips));
        logger.info("[UI][%s] Check Edit translation popup - SEO Title Tooltips.".formatted(language));

        // check SEO description
        String dbSEODescription = commonAction.getText(seoDescriptionLabelOnEditTranslationPopup);
        String ppSEODescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoDescription", language);
        assertCustomize.assertEquals(dbSEODescription, ppSEODescription, "[Failed][Edit translation popup] SEO description should be %s, but found %s.".formatted(ppSEODescription, dbSEODescription));
        logger.info("[UI][%s] Check Edit translation popup - SEO Description.".formatted(language));

        // check SEO description tooltips
        commonAction.hoverActions(seoDescriptionTooltipsOnEditTranslationPopup);
        String dbSEODescriptionTooltips = commonAction.getAttribute(seoDescriptionTooltipsOnEditTranslationPopup, "data-original-title");
        String ppSEODescriptionTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoDescriptionTooltips", language);
        assertCustomize.assertEquals(dbSEODescriptionTooltips, ppSEODescriptionTooltips, "[Failed][Edit translation popup] SEO description tooltips should be %s, but found %s.".formatted(ppSEODescriptionTooltips, dbSEODescriptionTooltips));
        logger.info("[UI][%s] Check Edit translation popup - SEO Description Tooltips.".formatted(language));

        // check SEO keywords
        String dbSEOKeywords = commonAction.getText(seoKeywordsLabelOnEditTranslationPopup);
        String ppSEOKeywords = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoKeywords", language);
        assertCustomize.assertEquals(dbSEOKeywords, ppSEOKeywords, "[Failed][Edit translation popup] SEO keywords should be %s, but found %s.".formatted(ppSEOKeywords, dbSEOKeywords));
        logger.info("[UI][%s] Check Edit translation popup - SEO Keywords.".formatted(language));

        // check SEO keywords tooltips
        commonAction.hoverActions(seoKeywordsTooltipsOnEditTranslationPopup);
        String dbSEOKeywordsTooltips = commonAction.getAttribute(seoKeywordsTooltipsOnEditTranslationPopup, "data-original-title");
        String ppSEOKeywordsTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoKeywordsTooltips", language);
        assertCustomize.assertEquals(dbSEOKeywordsTooltips, ppSEOKeywordsTooltips, "[Failed][Edit translation popup] SEO keywords tooltips should be %s, but found %s.".formatted(ppSEOKeywordsTooltips, dbSEOKeywordsTooltips));
        logger.info("[UI][%s] Check Edit translation popup - SEO Keywords tooltips.".formatted(language));

        // check SEO Url
        String dbSEOUrl = commonAction.getText(urlLabelOnEditTranslationPopup);
        String ppSEOUrl = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoURLLink", language);
        assertCustomize.assertEquals(dbSEOUrl, ppSEOUrl, "[Failed][Edit translation popup] SEO Url should be %s, but found %s.".formatted(ppSEOUrl, dbSEOUrl));
        logger.info("[UI][%s] Check Edit translation popup - SEO Url.".formatted(language));

        // Save button
        String dbSaveBtn = commonAction.getText(saveTextOnEditTranslationPopup);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.SaveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Edit translation popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Edit translation popup - Save button.".formatted(language));

        // Cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnEditTranslationPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.CancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Edit translation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Edit translation popup - Cancel button.".formatted(language));
    }

    void checkUpdatePricePopup() throws Exception {
        // check title
        commonAction.sleepInMiliSecond(1000);
        String dbTitle = commonAction.getText(titleOfUpdatePricePopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Update price popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update price popup - Title.".formatted(language));

        // check price list in dropdown
        // open price dropdown
        commonAction.clickJS(priceDropdownOnUpdatePricePopup);
        commonAction.getElement(priceTypeDropdownOnUpdatePricePopup);
        List<String> dbListPriceInDropdown = commonAction.getListElement(listOfPriceTypeOnUpdatePricePopup).stream().map(WebElement::getText).toList();
        List<String> ppListPriceInDropdown = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.2", language));
        assertCustomize.assertEquals(dbListPriceInDropdown, ppListPriceInDropdown, "[Failed][Update price popup] List price in dropdown should be %s, but found %s.".formatted(ppListPriceInDropdown, dbListPriceInDropdown));
        logger.info("[UI][%s] Check Update price popup - List price in dropdown.".formatted(language));

        // close price dropdown
        commonAction.click(priceDropdownOnUpdatePricePopup);

        // check apply all button
        String dbApplyAllBtn = commonAction.getText(applyTextOnUpdatePricePopup);
        String ppApplyAllBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.applyAllBtn", language);
        assertCustomize.assertEquals(dbApplyAllBtn, ppApplyAllBtn, "[Failed][Update price popup] Apply all button should be %s, but found %s.".formatted(ppApplyAllBtn, dbApplyAllBtn));
        logger.info("[UI][%s] Check Update price popup - Apply all button.".formatted(language));

        // check price list in table
        List<WebElement> listOfPrice = commonAction.getListElement(listOfPriceOnPriceTable);
        List<String> dbListPriceInTable = IntStream.iterate(3, i -> i >= 1, i -> i - 1).mapToObj(i -> listOfPrice.get(listOfPrice.size() - i).getText()).toList();
        List<String> ppListPriceInTable = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.2", language));
        assertCustomize.assertEquals(dbListPriceInTable, ppListPriceInTable, "[Failed][Update price popup] List price in table should be %s, but found %s.".formatted(ppListPriceInTable, dbListPriceInTable));
        logger.info("[UI][%s] Check Update price popup - List price in table.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(updateTextOnUpdatePricePopup);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.updateBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Update price popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update price popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnUpdatePricePopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Update price popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update price popup - Cancel button.".formatted(language));
    }

    void checkUpdateStockPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(titleOfUpdateStockPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Update normal variation stock popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update normal variation stock popup - Title.".formatted(language));

        // check selected branches
        String[] dbNumberOfSelectedBranches = commonAction.getText(branchDropdownOnUpdateStockPopup).split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.numberOfSelectedBranches", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Update normal variation stock popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Update normal variation stock popup - Number of selected branches.".formatted(language));

        // check list actions
        List<String> dbListActions = commonAction.getListElement(listActionsOnUpdateStockPopup).stream().map(WebElement::getText).toList();
        List<String> ppListActions = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.1", language));
        assertCustomize.assertEquals(dbListActions, ppListActions, "[Failed][Update normal variation stock popup] List actions should be %s, but found %s.".formatted(ppListActions, dbListActions));
        logger.info("[UI][%s] Check Update normal variation stock popup - List actions.".formatted(language));

        // check input stock placeholder
        String dbInputStockPlaceholder = commonAction.getAttribute(stockQuantityPlaceholderOnUpdateStockPopup, "placeholder");
        String ppInputStockPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.inputStockPlaceholder", language);
        assertCustomize.assertEquals(dbInputStockPlaceholder, ppInputStockPlaceholder, "[Failed][Update normal variation stock popup] Input stock placeholder should be %s, but found %s.".formatted(ppInputStockPlaceholder, dbInputStockPlaceholder));
        logger.info("[UI][%s] Check Update normal variation stock popup - Input stock placeholder.".formatted(language));

        // check action type
        String dbActionType = commonAction.getText(actionTypeOnUpdateStockPopup).split(": ")[0];
        String ppActionType0 = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.0", language);
        String ppActionType1 = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.1", language);
        assertCustomize.assertTrue(dbActionType.equals(ppActionType1) || dbActionType.equals(ppActionType0), "[Failed][Update normal variation popup] Action type should be %s or %s, but found %s.".formatted(ppActionType0, ppActionType1, dbActionType));
        logger.info("[UI][%s] Check Update normal variation popup - Action type.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(updateTextOnUpdateStockPopup);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.updateBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Update normal variation popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update normal variation popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnUpdateStockPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Update normal variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update normal variation popup - Cancel button.".formatted(language));
    }

    void checkAddIMEIPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(titleOfAddIMEIPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Add IMEI popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Add IMEI popup - Title.".formatted(language));

        // check branch title
        String dbBranchTitle = commonAction.getText(branchTextOnAddIMEIPopup);
        String ppBranchTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.branchLabel", language);
        assertCustomize.assertEquals(dbBranchTitle, ppBranchTitle, "[Failed][Add IMEI popup] Branch title should be %s, but found %s.".formatted(ppBranchTitle, dbBranchTitle));
        logger.info("[UI][%s] Check Add IMEI popup - Branch title.".formatted(language));

        // check number of selected branches
        String[] dbNumberOfSelectedBranches = commonAction.getText(branchDropdownOnAddIMEIPopup).split("\\d+");

        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.numberOfSelectedBranches", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Add IMEI popup] Number of selected branches should be %s, but found %s.".formatted(Arrays.toString(ppNumberOfSelectedBranches), Arrays.toString(dbNumberOfSelectedBranches)));
        logger.info("[UI][%s] Check Add IMEI popup - Number of selected branches.".formatted(language));

        // check add IMEI placeholder
        String dbIMEIPlaceholder = commonAction.getAttribute(inputIMEIPlaceholderOnAddIMEIPopup, 0, "placeholder");
        String ppIMEIPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.addIMEITextBoxPlaceholder", language);
        assertCustomize.assertEquals(dbIMEIPlaceholder, ppIMEIPlaceholder, "[Failed][Add IMEI popup] Input IMEI placeholder should be %s, but found %s.".formatted(ppIMEIPlaceholder, dbIMEIPlaceholder));
        logger.info("[UI][%s] Check Add IMEI popup - Input IMEI placeholder.".formatted(language));

        // check product name column
        String dbProductNameColumn = commonAction.getText(productNameLabelOnAddIMEITable);
        String ppProductNameColumn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.productNameColumn", language);
        assertCustomize.assertEquals(dbProductNameColumn, ppProductNameColumn, "[Failed][Add IMEI popup] Product name column should be %s, but found %s.".formatted(ppProductNameColumn, dbProductNameColumn));
        logger.info("[UI][%s] Check Add IMEI popup - Product name column.".formatted(language));

        // check save button
        String dbSaveBtn = commonAction.getText(saveTextOnAddIMEIPopup);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.saveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Add IMEI popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Add IMEI popup - Save button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnAddIMEIPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Add IMEI popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Add IMEI popup - Cancel button.".formatted(language));
    }

    void checkUpdateSKUPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(titleOfUpdateSKUPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Update SKU popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update SKU popup - Title.".formatted(language));

        // check number of selected branches
        String[] dbNumberOfSelectedBranches = commonAction.getText(branchDropdownOnUpdateSKUPopup).split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.numberOfSelectedBranches", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Update SKU popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Update SKU popup - Number of selected branches.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(updateTextOnUpdateSKUPopup);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.updateBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Update SKU popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update SKU popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnUpdateSKUPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Update SKU popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update SKU popup - Cancel button.".formatted(language));
    }

    void checkUpdateVariationImagePopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(titleOfUploadImagePopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Upload image popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Upload image popup - Title.".formatted(language));

        // check upload image button placeholder
        String dbUploadImageBtnPlaceholder = commonAction.getText(uploadImagePlaceholderOnUploadImagePopup);
        String ppUploadImageBtnPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.uploadBtnPlaceholder", language);
        assertCustomize.assertEquals(dbUploadImageBtnPlaceholder, ppUploadImageBtnPlaceholder, "[Failed][Upload image popup] Upload image button placeholder should be %s, but found %s.".formatted(ppUploadImageBtnPlaceholder, dbUploadImageBtnPlaceholder));
        logger.info("[UI][%s] Check Upload image popup - Upload image button placeholder.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(selectTextOnUploadImagePopup);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.selectBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Upload image popup] Select button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Upload image popup - Select button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnUploadImagePopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Upload image popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Upload image popup - Cancel button.".formatted(language));
    }

    void checkUIAddConversionUnit() throws Exception {
        // check conversion unit title
        String dbConversionUnit = commonAction.getText(unitLabel);
        String ppConversionUnit = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.title", language);
        assertCustomize.assertEquals(dbConversionUnit, ppConversionUnit, "[Failed][Body] Conversion unit title should be %s, but found %s.".formatted(ppConversionUnit, dbConversionUnit));
        logger.info("[UI][%s] Check Body - Conversion unit title.".formatted(language));

        // check conversion unit search box placeholder
        String dbConversionUnitSearchBoxPlaceholder = commonAction.getAttribute(searchUnitPlaceholder, "placeholder");
        String ppConversionUnitSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.searchBoxPlaceholder", language);
        assertCustomize.assertEquals(dbConversionUnitSearchBoxPlaceholder, ppConversionUnitSearchBoxPlaceholder, "[Failed][Body] Conversion unit search box placeholder should be %s, but found %s.".formatted(ppConversionUnitSearchBoxPlaceholder, dbConversionUnitSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Conversion unit search box placeholder.".formatted(language));

        // check add conversion unit checkbox
        String dbAddConversionUnitCheckbox = commonAction.getText(addConversionUnitLabel);
        String ppAddConversionUnitCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.addConversionUnitCheckbox", language);
        assertCustomize.assertEquals(dbAddConversionUnitCheckbox, ppAddConversionUnitCheckbox, "[Failed][Body] Add conversion unit checkbox label should be %s, but found %s.".formatted(ppAddConversionUnitCheckbox, dbAddConversionUnitCheckbox));
        logger.info("[UI][%s] Check Body - Add conversion unit checkbox.".formatted(language));

        // check conversion unit tooltips
        commonAction.hoverActions(conversionUnitTooltips);
        String dbConversionUnitTooltips = commonAction.getAttribute(conversionUnitTooltips, "data-original-title");
        String ppConversionUnitTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.tooltips", language);
        assertCustomize.assertEquals(dbConversionUnitTooltips, ppConversionUnitTooltips, "[Failed][Body] Conversion unit tooltips should be %s, but found %s.".formatted(ppConversionUnitTooltips, dbConversionUnitTooltips));
        logger.info("[UI][%s] Check Body - Conversion unit tooltips.".formatted(language));
    }

    void checkUIAddWholesaleProduct() throws Exception {
        // check add wholesale product checkbox
        String dbAddWholesaleProductCheckbox = commonAction.getText(addWholesalePricingLabel);
        String ppAddWholesaleProductCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.addWholesaleProductCheckbox", language);
        assertCustomize.assertEquals(dbAddWholesaleProductCheckbox, ppAddWholesaleProductCheckbox, "[Failed][Body] Add wholesale product checkbox label should be %s, but found %s.".formatted(ppAddWholesaleProductCheckbox, dbAddWholesaleProductCheckbox));
        logger.info("[UI][%s] Check Body - Add wholesale product checkbox.".formatted(language));
    }

    void checkUIDeposit() throws Exception {
        // check deposit title
        String dbDeposit = commonAction.getText(depositLabel);
        String ppDeposit = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.title", language);
        assertCustomize.assertEquals(dbDeposit, ppDeposit, "[Failed][Body] Deposit title should be %s, but found %s.".formatted(ppDeposit, dbDeposit));
        logger.info("[UI][%s] Check Body - Deposit.".formatted(language));

        // check Add deposit button
        String dbAddDepositBtn = commonAction.getText(addDepositText);
        String ppAddDepositBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.addDepositBtn", language);
        assertCustomize.assertEquals(dbAddDepositBtn, ppAddDepositBtn, "[Failed][Body] Add deposit button should be %s, but found %s.".formatted(ppAddDepositBtn, dbAddDepositBtn));
        logger.info("[UI][%s] Check Body - Add deposit button.".formatted(language));

        // check deposit description
        String dbDepositDescription = commonAction.getText(depositDescription);
        String ppDepositDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.depositDescription", language);
        assertCustomize.assertEquals(dbDepositDescription, ppDepositDescription, "[Failed][Body] Deposit description should be %s, but found %s.".formatted(ppDepositDescription, dbDepositDescription));
        logger.info("[UI][%s] Check Body - Deposit description.".formatted(language));
    }

    void checkUISEO() throws Exception {
        // check SEO setting
        String dbSEOSetting = commonAction.getText(uiSEOSetting);
        String ppSEOSetting = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.title", language);
        assertCustomize.assertEquals(dbSEOSetting, ppSEOSetting, "[Failed][Body] SEO setting should be %s, but found %s.".formatted(ppSEOSetting, dbSEOSetting));
        logger.info("[UI][%s] Check Body - SEO setting.".formatted(language));

        // check SEO live preview
        String dbLivePreview = commonAction.getText(livePreviewLabel);
        String ppLivePreview = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.livePreview", language);
        assertCustomize.assertEquals(dbLivePreview, ppLivePreview, "[Failed][Body] Live preview should be %s, but found %s.".formatted(ppLivePreview, dbLivePreview));
        logger.info("[UI][%s] Check Body - Live preview.".formatted(language));

        // check SEO live preview tooltips
        commonAction.hoverActions(livePreviewTooltips);
        String dbLivePreviewTooltips = commonAction.getAttribute(livePreviewTooltips, "data-original-title");
        String ppLivePreviewTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.livePreviewTooltips", language);
        assertCustomize.assertEquals(dbLivePreviewTooltips, ppLivePreviewTooltips, "[Failed][Body] Live preview tooltips should be %s, but found %s.".formatted(ppLivePreviewTooltips, dbLivePreviewTooltips));
        logger.info("[UI][%s] Check Body - Live preview tooltips.".formatted(language));

        // check SEO title
        String dbSEOTitle = commonAction.getText(seoTitleLabel);
        String ppSEOTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoTitle", language);
        assertCustomize.assertEquals(dbSEOTitle, ppSEOTitle, "[Failed][Body] SEO Title should be %s, but found %s.".formatted(ppSEOTitle, dbSEOTitle));
        logger.info("[UI][%s] Check Body - SEO Title.".formatted(language));

        // check SEO title tooltips
        commonAction.hoverActions(seoTitleTooltips);
        String dbSEOTitleTooltips = commonAction.getAttribute(seoTitleTooltips, "data-original-title");
        String ppSEOTitleTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoTitleTooltips", language);
        assertCustomize.assertEquals(dbSEOTitleTooltips, ppSEOTitleTooltips, "[Failed][Body] SEO Title tooltips should be %s, but found %s.".formatted(ppSEOTitleTooltips, dbSEOTitleTooltips));
        logger.info("[UI][%s] Check Body - SEO Title tooltips.".formatted(language));

        // check SEO description
        String dbSEODescription = commonAction.getText(seoDescriptionLabel);
        String ppSEODescription = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoDescription", language);
        assertCustomize.assertEquals(dbSEODescription, ppSEODescription, "[Failed][Body] SEO description should be %s, but found %s.".formatted(ppSEODescription, dbSEODescription));
        logger.info("[UI][%s] Check Body - SEO description.".formatted(language));

        // check SEO description tooltips
        commonAction.hoverActions(seoDescriptionTooltips);
        String dbSEODescriptionTooltips = commonAction.getAttribute(seoDescriptionTooltips, "data-original-title");
        String ppSEODescriptionTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoDescriptionTooltips", language);
        assertCustomize.assertEquals(dbSEODescriptionTooltips, ppSEODescriptionTooltips, "[Failed][Body] SEO descriptions tooltips should be %s, but found %s.".formatted(ppSEODescriptionTooltips, dbSEODescriptionTooltips));
        logger.info("[UI][%s] Check Body - SEO description tooltips.".formatted(language));

        // check SEO keywords
        String dbSEOKeywords = commonAction.getText(seoKeywordsLabel);
        String ppSEOKeywords = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoKeywords", language);
        assertCustomize.assertEquals(dbSEOKeywords, ppSEOKeywords, "[Failed][Body] SEO keywords should be %s, but found %s.".formatted(ppSEOKeywords, dbSEOKeywords));
        logger.info("[UI][%s] Check Body - SEO keywords.".formatted(language));

        // check SEO keywords tooltips
        commonAction.hoverActions(seoKeywordsTooltips);
        String dbSEOKeywordsTooltips = commonAction.getAttribute(seoKeywordsTooltips, "data-original-title");
        String ppSEOKeywordsTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoKeywordsTooltips", language);
        assertCustomize.assertEquals(dbSEOKeywordsTooltips, ppSEOKeywordsTooltips, "[Failed][Body] SEO keywords tooltips should be %s, but found %s.".formatted(ppSEOKeywordsTooltips, dbSEOKeywordsTooltips));
        logger.info("[UI][%s] Check Body - SEO keywords tooltips.".formatted(language));

        // check SEO URL link
        String dbSEOUrlLink = commonAction.getText(seoURLLabel);
        String ppSEOUrlLink = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoUrlLink", language);
        assertCustomize.assertEquals(dbSEOUrlLink, ppSEOUrlLink, "[Failed][Body] SEO URL link should be %s, but found %s.".formatted(ppSEOUrlLink, dbSEOUrlLink));
        logger.info("[UI][%s] Check Body - SEO URL link.".formatted(language));
    }

    private List<String> getAllSaleChannelTooltips() {
        try {
            List<WebElement> tooltips = commonAction.getListElement(this.allSaleChannelTooltips);
            return IntStream.range(0, tooltips.size()).mapToObj(tooltipIndex -> commonAction.getText(this.allSaleChannelTooltips, tooltipIndex)).toList();
        } catch (IndexOutOfBoundsException | StaleElementReferenceException ex) {
            logger.info(ex);
            return getAllSaleChannelTooltips();
        }
    }

    void checkUISaleChanel() throws Exception {
        // check Sale chanel title
        String dbSaleChanel = commonAction.getText(saleChannelLabel);
        String ppSaleChanel = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.title", language);
        assertCustomize.assertEquals(dbSaleChanel, ppSaleChanel, "[Failed][Body] Sale chanel title should be %s, but found %s.".formatted(ppSaleChanel, dbSaleChanel));
        logger.info("[UI][%s] Check Body - Sale chanel title.".formatted(language));

        // check Online shop tooltips
        commonAction.viewTooltips(onlineShopIcon, allSaleChannelTooltips);
        String ppOnlineShopTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.onlineShopTooltips", language);
        assertCustomize.assertTrue(getAllSaleChannelTooltips().contains(ppOnlineShopTooltips), "[Failed][Body] Online shop tooltips should be %s, but found %s.".formatted(ppOnlineShopTooltips, allSaleChannelTooltips));
        logger.info("[UI][%s] Check Body - Online shop tooltips.".formatted(language));

        // check Gomua tooltips
        commonAction.viewTooltips(gomuaIcon, allSaleChannelTooltips);
        String ppGomuaTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.gomuaTooltips", language);
        assertCustomize.assertTrue(getAllSaleChannelTooltips().contains(ppGomuaTooltips), "[Failed][Body] Gomua tooltips should be %s, but found %s.".formatted(ppGomuaTooltips, allSaleChannelTooltips.toString()));
        logger.info("[UI][%s] Check Body - Gomua tooltips.".formatted(language));

        // check Shopee tooltips
        commonAction.viewTooltips(shopeeIcon, allSaleChannelTooltips);
        List<String> ppShopeeTooltips = List.of(getPropertiesValueByDBLang("products.allProducts.updateProduct.saleChanel.shopeeTooltips.IMEI", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.deactivatedShopeeTooltips", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.activatedShopeeTooltips", language));
        List<String> joinShopeeList = getAllSaleChannelTooltips().stream().filter(ppShopeeTooltips::contains).toList();
        assertCustomize.assertFalse(joinShopeeList.isEmpty(), "[Failed][Body] Shopee tooltips should be %s, but found %s.".formatted(ppShopeeTooltips.toString(), allSaleChannelTooltips.toString()));
        logger.info("[UI][%s] Check Body - Shopee tooltips.".formatted(language));

        // check Tiktok tooltips
        commonAction.viewTooltips(tiktokIcon, allSaleChannelTooltips);
        List<String> ppTiktokTooltips = List.of(getPropertiesValueByDBLang("products.allProducts.updateProduct.saleChanel.tiktokTooltips.IMEI", language), getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.activatedTiktokTooltips", language), getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.deactivatedTiktokTooltips", language));
        List<String> joinTiktokList = getAllSaleChannelTooltips().stream().filter(ppTiktokTooltips::contains).toList();
        assertCustomize.assertFalse(joinTiktokList.isEmpty(), "[Failed][Body] Tiktok tooltips should be %s, but found %s.".formatted(ppTiktokTooltips.toString(), allSaleChannelTooltips.toString()));
        logger.info("[UI][%s] Check Body - Tiktok tooltips.".formatted(language));
    }

    void checkUICollections() throws Exception {
        // check collections title
        String dbCollectionsTitle = commonAction.getText(collectionsLabel);
        String ppCollectionsTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.title", language);
        assertCustomize.assertEquals(dbCollectionsTitle, ppCollectionsTitle, "[Failed][Body] Collections title should be %s, but found %s.".formatted(ppCollectionsTitle, dbCollectionsTitle));
        logger.info("[UI][%s] Check Body - Collections title.".formatted(language));

        // check collections search box placeholder
        String dbCollectionsSearchBoxPlaceholder = commonAction.getAttribute(searchCollectionPlaceholder, "placeholder");
        String ppCollectionsSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.searchBoxPlaceholder", language);
        assertCustomize.assertEquals(dbCollectionsSearchBoxPlaceholder, ppCollectionsSearchBoxPlaceholder, "[Failed][Body] Collections search box placeholder should be %s, but found %s.".formatted(ppCollectionsSearchBoxPlaceholder, dbCollectionsSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Collections search box placeholder.".formatted(language));

        if (new ProductCollection(loginInformation).getListOfManualProductCollectionsName().isEmpty()) {
            // check when no collection created
            String dbNoCreatedCollection = commonAction.getText(noCollectionText);
            String ppNoCreatedCollection = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.noCreatedCollection", language);
            assertCustomize.assertEquals(dbNoCreatedCollection, ppNoCreatedCollection, "[Failed][Body] No created collection should be %s, but found %s.".formatted(ppNoCreatedCollection, dbNoCreatedCollection));
            logger.info("[UI][%s] Check Body - No created collection.".formatted(language));
        }
    }

    void checkUICRWarehousing() throws Exception {
        // check warehousing title
        String dbWarehousing = commonAction.getText(warehousingLabel);
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.title", language);
        assertCustomize.assertEquals(dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing title should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing title.".formatted(language));

        // check SKU
        String dbSKU = commonAction.getText(withoutVariationSKULabel);
        String ppSKU = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.sku", language);
        assertCustomize.assertEquals(dbSKU, ppSKU, "[Failed][Body] SKU title should be %s, but found %s.".formatted(ppSKU, dbSKU));
        logger.info("[UI][%s] Check Body - SKU title.".formatted(language));

        // check Barcode
        String dbBarcode = commonAction.getText(withoutVariationBarcodeLabel);
        String ppBarcode = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.barcode", language);
        assertCustomize.assertEquals(dbBarcode, ppBarcode, "[Failed][Body] Barcode title should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
        logger.info("[UI][%s] Check Body - Barcode title.".formatted(language));

        // check manage inventory
        String dbManageInventory = commonAction.getText(manageInventoryLabel);
        String ppManageInventory = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.title", language);
        assertCustomize.assertEquals(dbManageInventory, ppManageInventory, "[Failed][Body] Manage inventory title should be %s, but found %s.".formatted(ppManageInventory, dbManageInventory));
        logger.info("[UI][%s] Check Body - Manage inventory.".formatted(language));

        // check manage inventory by product
        String dbManageInventoryByProduct = commonAction.getText(manageByProductText);
        String ppManageInventoryByProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byProduct", language);
        assertCustomize.assertEquals(dbManageInventoryByProduct, ppManageInventoryByProduct, "[Failed][Body] Manage inventory by product should be %s, but found %s.".formatted(ppManageInventoryByProduct, dbManageInventoryByProduct));
        logger.info("[UI][%s] Check Body - Manage inventory by product.".formatted(language));

        // check manage inventory by IMEI/Serial number
        String dbManageInventoryByIMEI = commonAction.getText(manageByIMEIText);
        String ppManageInventoryByIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI", language);
        assertCustomize.assertEquals(dbManageInventoryByIMEI, ppManageInventoryByIMEI, "[Failed][Body] Manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppManageInventoryByIMEI, dbManageInventoryByIMEI));
        logger.info("[UI][%s] Check Body - Manage inventory by IMEI/Serial number.".formatted(language));

        // check stock quantity title
        String dbStockQuantity = commonAction.getText(withoutVariationStockQuantityLabel);
        String ppStockQuantity = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.title", language);
        assertCustomize.assertEquals(dbStockQuantity, ppStockQuantity, "[Failed][Body] Stock quantity title should be %s, but found %s.".formatted(ppStockQuantity, dbStockQuantity));
        logger.info("[UI][%s] Check Body - Stock quantity title.".formatted(language));

        // check apply stock for all branches button
        String dbApplyAllBtn = commonAction.getText(stockQuantityApplyAllText);
        String ppApplyAllBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.applyAllBtn", language);
        assertCustomize.assertEquals(dbApplyAllBtn, ppApplyAllBtn, "[Failed][Body] Apply all button should be %s, but found %s.".formatted(ppApplyAllBtn, dbApplyAllBtn));
        logger.info("[UI][%s] Check Body - Apply all button.".formatted(language));

        // check display if out of stock checkbox
        String dbDisplayIfOutOfStockCheckbox = commonAction.getText(displayIfOutOfStockText);
        String ppDisplayIfOutOfStockCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.displayIfOutOfStock", language);
        assertCustomize.assertEquals(dbDisplayIfOutOfStockCheckbox, ppDisplayIfOutOfStockCheckbox, "[Failed][Body] Display if out of stock checkbox label should be %s, but found %s.".formatted(ppDisplayIfOutOfStockCheckbox, dbDisplayIfOutOfStockCheckbox));
        logger.info("[UI][%s] Check Body - Display if out of stock checkbox.".formatted(language));

        // check hide remaining stock on online store
        String dbHideRemainingOnOnlineStoreCheckbox = commonAction.getText(hideRemainingStockOnOnlineStoreText);
        String ppHideRemainingOnOnlineStoreCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.hideRemainingStock", language);
        assertCustomize.assertEquals(dbHideRemainingOnOnlineStoreCheckbox, ppHideRemainingOnOnlineStoreCheckbox, "[Failed][Body] Hide remaining stock on online store checkbox label should be %s, but found %s.".formatted(ppHideRemainingOnOnlineStoreCheckbox, dbHideRemainingOnOnlineStoreCheckbox));
        logger.info("[UI][%s] Check Body - Hide remaining stock on online store checkbox.".formatted(language));
    }

    void checkUIUPWarehousing() throws Exception {
        // check warehousing title
        String dbWarehousing = commonAction.getText(warehousingLabel);
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.title", language);
        assertCustomize.assertEquals(dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing title should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing title.".formatted(language));

        // check SKU
        if (!commonAction.getListElement(withoutVariationSKULabel).isEmpty()) {
            String dbSKU = commonAction.getText(withoutVariationSKULabel);
            String ppSKU = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.sku", language);
            assertCustomize.assertEquals(dbSKU, ppSKU, "[Failed][Body] SKU title should be %s, but found %s.".formatted(ppSKU, dbSKU));
            logger.info("[UI][%s] Check Body - SKU title.".formatted(language));

            // check Barcode
            String dbBarcode = commonAction.getText(withoutVariationBarcodeLabel);
            String ppBarcode = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.barcode", language);
            assertCustomize.assertEquals(dbBarcode, ppBarcode, "[Failed][Body] Barcode title should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
            logger.info("[UI][%s] Check Body - Barcode title.".formatted(language));
        }

        // check manage inventory
        String dbManageInventory = commonAction.getText(manageInventoryLabel);
        String ppManageInventory = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.title", language);
        assertCustomize.assertEquals(dbManageInventory, ppManageInventory, "[Failed][Body] Manage inventory title should be %s, but found %s.".formatted(ppManageInventory, dbManageInventory));
        logger.info("[UI][%s] Check Body - Manage inventory.".formatted(language));

        // check manage inventory by product
        String dbManageInventoryByProduct = commonAction.getText(manageByProductText);
        String ppManageInventoryByProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byProduct", language);
        assertCustomize.assertEquals(dbManageInventoryByProduct, ppManageInventoryByProduct, "[Failed][Body] Manage inventory by product should be %s, but found %s.".formatted(ppManageInventoryByProduct, dbManageInventoryByProduct));
        logger.info("[UI][%s] Check Body - Manage inventory by product.".formatted(language));

        // check manage inventory by IMEI/Serial number
        String dbManageInventoryByIMEI = commonAction.getText(manageByIMEIText);
        String ppManageInventoryByIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI", language);
        assertCustomize.assertEquals(dbManageInventoryByIMEI, ppManageInventoryByIMEI, "[Failed][Body] Manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppManageInventoryByIMEI, dbManageInventoryByIMEI));
        logger.info("[UI][%s] Check Body - Manage inventory by IMEI/Serial number.".formatted(language));

        // check remaining stock
        String dbRemainingStock = commonAction.getText(remainingStockLabel);
        String ppRemainingStock = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.remainingStock", language);
        assertCustomize.assertEquals(dbRemainingStock, ppRemainingStock, "[Failed][Body] Remaining stock should be %s, but found %s.".formatted(ppRemainingStock, dbRemainingStock));
        logger.info("[UI][%s] Check Body - Remaining stock.".formatted(language));

        // check view remaining stock popup
        checkViewRemainingStockPopup();

        // check sold count
        String dbSoldCount = commonAction.getText(soldCountLabel);
        String ppSoldCount = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.soldCount", language);
        assertCustomize.assertEquals(dbSoldCount, ppSoldCount, "[Failed][Body] Sold count should be %s, but found %s.".formatted(ppSoldCount, dbSoldCount));
        logger.info("[UI][%s] Check Body - Sold count.".formatted(language));

        // check view sold count popup
        checkViewSoldCountPopup();

        // check display if out of stock checkbox
        String dbDisplayIfOutOfStockCheckbox = commonAction.getText(displayIfOutOfStockText);
        String ppDisplayIfOutOfStockCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.displayIfOutOfStock", language);
        assertCustomize.assertEquals(dbDisplayIfOutOfStockCheckbox, ppDisplayIfOutOfStockCheckbox, "[Failed][Body] Display if out of stock checkbox label should be %s, but found %s.".formatted(ppDisplayIfOutOfStockCheckbox, dbDisplayIfOutOfStockCheckbox));
        logger.info("[UI][%s] Check Body - Display if out of stock checkbox.".formatted(language));

        // check hide remaining stock on online store
        String dbHideRemainingOnOnlineStoreCheckbox = commonAction.getText(hideRemainingStockOnOnlineStoreText);
        String ppHideRemainingOnOnlineStoreCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.hideRemainingStock", language);
        assertCustomize.assertEquals(dbHideRemainingOnOnlineStoreCheckbox, ppHideRemainingOnOnlineStoreCheckbox, "[Failed][Body] Hide remaining stock on online store checkbox label should be %s, but found %s.".formatted(ppHideRemainingOnOnlineStoreCheckbox, dbHideRemainingOnOnlineStoreCheckbox));
        logger.info("[UI][%s] Check Body - Hide remaining stock on online store checkbox.".formatted(language));
    }

    void checkViewRemainingStockPopup() throws Exception {
        // open view remaining stock popup
        commonAction.openPopupJS(remainingStockValue, popup);

        // check title
        String dbTitle = commonAction.getText(titleOfRemainingStockPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][View remaining stock popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check View remaining stock popup - Title.".formatted(language));

        // check text in dropdown when select all branches
        String dbSelectAllBranches = commonAction.getText(branchDropdownOnRemainingStockPopup);
        String ppSelectAllBranches = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.branchDropdown.text.allBranch", language);
        assertCustomize.assertEquals(dbSelectAllBranches, ppSelectAllBranches, "[Failed][ View remaining stock popup] Dropdown text when select all branches should be %s, but found %s.".formatted(ppSelectAllBranches, dbSelectAllBranches));
        logger.info("[UI][%s] Check View remaining stock popup - Check dropdown text when select all branches.".formatted(language));

        // open branch dropdown
        commonAction.click(branchDropdownOnRemainingStockPopup);

        // click on All branch check to unselect all branches
        commonAction.clickJS(allBranchesCheckboxOnRemainingStockPopup);

        // check text in dropdown when no select any branch
        String dbNoSelectAnyBranch = commonAction.getText(branchDropdownOnRemainingStockPopup);
        String ppNoSelectAnyBranch = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.branchDropdown.text.noBranch", language);
        assertCustomize.assertEquals(dbNoSelectAnyBranch, ppNoSelectAnyBranch, "[Failed][ View remaining stock popup] Dropdown text when no select any branch should be %s, but found %s.".formatted(ppNoSelectAnyBranch, dbNoSelectAnyBranch));
        logger.info("[UI][%s] Check View remaining stock popup -  Check dropdown text when no select any branch.".formatted(language));

        // check All branches checkbox label
        String dbAllBranchesCheckbox = commonAction.getText(allBranchesCheckboxOnRemainingStockPopup);
        String ppAllBranchesCheckbox = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.branchDropDown.allBranchCheckbox", language);
        assertCustomize.assertEquals(dbAllBranchesCheckbox, ppAllBranchesCheckbox, "[Failed][ View remaining stock popup] All branches checkbox label should be %s, but found %s.".formatted(ppAllBranchesCheckbox, dbAllBranchesCheckbox));
        logger.info("[UI][%s] Check View remaining stock popup - All branches checkbox.".formatted(language));

        // close branch dropdown
        commonAction.click(branchDropdownOnRemainingStockPopup);

        // check error when no select any branch
        String dbNoSelectBranchError = commonAction.getText(noBranchErrorMessageOnRemainingStockPopup);
        String ppNoSelectBranchError = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.noBranchError", language);
        assertCustomize.assertEquals(dbNoSelectBranchError, ppNoSelectBranchError, "[Failed][ View remaining stock popup] No select branch error should be %s, but found %s.".formatted(ppNoSelectBranchError, dbNoSelectBranchError));
        logger.info("[UI][%s] Check View remaining stock popup - No select branch error.".formatted(language));

        // close view remaining stock popup
        commonAction.closePopup(closeBtnOnRemainingPopup);
        logger.info("Close remaining popup.");
    }

    void checkViewSoldCountPopup() throws Exception {
        // open view sold count popup
        commonAction.openPopupJS(soldCountValue, popup);

        // check title
        String dbTitle = commonAction.getText(titleOfViewSoldCountPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][View sold count popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check View sold count popup - Title.".formatted(language));

        // check text in dropdown when select all branches
        String dbSelectAllBranches = commonAction.getText(branchDropdownOnViewSoldCountPopup);

        String ppSelectAllBranches = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.branchDropdown.text.allBranch", language);
        assertCustomize.assertEquals(dbSelectAllBranches, ppSelectAllBranches, "[Failed][ View sold count popup] Dropdown text when select all branches should be %s, but found %s.".formatted(ppSelectAllBranches, dbSelectAllBranches));
        logger.info("[UI][%s] Check View sold count popup - Check dropdown text when select all branches.".formatted(language));

        // open branch dropdown
        commonAction.openDropdownJS(branchDropdownOnViewSoldCountPopup, allBranchesCheckboxOnViewSoldCountPopup);

        // click on All branch check to unselect all branches
        commonAction.clickJS(allBranchesCheckboxOnViewSoldCountPopup);

        // check text in dropdown when no select any branch
        String dbNoSelectAnyBranch = commonAction.getText(branchDropdownOnViewSoldCountPopup);
        String ppNoSelectAnyBranch = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.branchDropdown.text.noBranch", language);
        assertCustomize.assertEquals(dbNoSelectAnyBranch, ppNoSelectAnyBranch, "[Failed][ View sold count popup] Dropdown text when no select any branch should be %s, but found %s.".formatted(ppNoSelectAnyBranch, dbNoSelectAnyBranch));
        logger.info("[UI][%s] Check View sold count popup -  Check dropdown text when no select any branch.".formatted(language));

        // check All branches checkbox label
        String dbAllBranchesCheckbox = commonAction.getText(allBranchesCheckboxOnViewSoldCountPopup);
        String ppAllBranchesCheckbox = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.branchDropDown.allBranchCheckbox", language);
        assertCustomize.assertEquals(dbAllBranchesCheckbox, ppAllBranchesCheckbox, "[Failed][ View sold count popup] All branches checkbox label should be %s, but found %s.".formatted(ppAllBranchesCheckbox, dbAllBranchesCheckbox));
        logger.info("[UI][%s] Check View sold count popup - All branches checkbox.".formatted(language));

        // close branch dropdown
        commonAction.closeDropdown(branchDropdownOnViewSoldCountPopup, allBranchesCheckboxOnViewSoldCountPopup);

        // check error when no select any branch
        String dbNoSelectBranchError = commonAction.getText(noBranchErrorMessageOnViewSoldCountPopup);
        String ppNoSelectBranchError = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.noBranchError", language);
        assertCustomize.assertEquals(dbNoSelectBranchError, ppNoSelectBranchError, "[Failed][ View sold count popup] No select branch error should be %s, but found %s.".formatted(ppNoSelectBranchError, dbNoSelectBranchError));
        logger.info("[UI][%s] Check View sold count popup - No select branch error.".formatted(language));

        // close view sold count popup
        commonAction.closePopup(closeBtnOnViewSoldCountPopup);
    }

    void checkManageInventoryByIMEINotice() throws Exception {
        // check manage inventory by IMEI/Serial number notice
        String dbIMEINotice = commonAction.getText(manageByIMEINoticeText);
        String ppIMEINotice = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI.notice", language);
        assertCustomize.assertEquals(dbIMEINotice, ppIMEINotice, "[Failed][Body] Manage Inventory by IMEI/Serial number notice should be %s, but found %s.".formatted(ppIMEINotice, dbIMEINotice));
        logger.info("[UI][%s] Check Body - Manage Inventory by IMEI/Serial number notice.".formatted(language));
    }

    void checkUIPackageInformation() throws Exception {
        // check package information title
        String dbPackageInformation = commonAction.getText(packageInformationLabel);
        String ppPackageInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.title", language);
        assertCustomize.assertEquals(dbPackageInformation, ppPackageInformation, "[Failed][Body] Package information title should be %s, but found %s.".formatted(ppPackageInformation, dbPackageInformation));
        logger.info("[UI][%s] Check Body - Package information title.".formatted(language));

        // check package information tooltips
        commonAction.hoverActions(packageInformationTooltips);
        String dbPackageInformationTooltips = commonAction.getAttribute(packageInformationTooltips, "data-original-title");
        String ppPackageInformationTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.tooltips", language);
        assertCustomize.assertEquals(dbPackageInformationTooltips, ppPackageInformationTooltips, "[Failed][Body] Package information tooltips should be %s, but found %s.".formatted(ppPackageInformationTooltips, dbPackageInformationTooltips));
        logger.info("[UI][%s] Check Body - Package information tooltips.".formatted(language));

        // check weight
        String dbWeight = commonAction.getText(weightLabel);
        String ppWeight = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.weight", language);
        assertCustomize.assertEquals(dbWeight, ppWeight, "[Failed][Body] Product weight should be %s, but found %s.".formatted(ppWeight, dbWeight));
        logger.info("[UI][%s] Check Body - Product weight.".formatted(language));

        // check length
        String dbLength = commonAction.getText(lengthLabel);
        String ppLength = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.length", language);
        assertCustomize.assertEquals(dbLength, ppLength, "[Failed][Body] Product length should be %s, but found %s.".formatted(ppLength, dbLength));
        logger.info("[UI][%s] Check Body - Product length.".formatted(language));

        // check width
        String dbWidth = commonAction.getText(widthLabel);
        String ppWidth = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.width", language);
        assertCustomize.assertEquals(dbWidth, ppWidth, "[Failed][Body] Product width should be %s, but found %s.".formatted(ppWidth, dbWidth));
        logger.info("[UI][%s] Check Body - Product width.".formatted(language));

        // check height
        String dbHeight = commonAction.getText(heightLabel);
        String ppHeight = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.height", language);
        assertCustomize.assertEquals(dbHeight, ppHeight, "[Failed][Body] Product height should be %s, but found %s.".formatted(ppHeight, dbHeight));
        logger.info("[UI][%s] Check Body - Product height.".formatted(language));

        // check shipping notice
        String dbShippingFeeNote = commonAction.getText(packageNote);
        String ppShippingFeeNote = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.shippingFeeNote", language);
        assertCustomize.assertEquals(dbShippingFeeNote, ppShippingFeeNote, "[Failed][Body] Shipping fee note should be %s, but found %s.".formatted(ppShippingFeeNote, dbShippingFeeNote));
        logger.info("[UI][%s] Check Body - Shipping fee note.".formatted(language));
    }

    void checkUIPriority() throws Exception {
        // check priority title
        String dbPriorityTitle = commonAction.getText(priorityLabel);
        String ppPriorityTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.priority.title", language);
        assertCustomize.assertEquals(dbPriorityTitle, ppPriorityTitle, "[Failed][Body] Priority Title should be %s, but found %s.".formatted(ppPriorityTitle, dbPriorityTitle));
        logger.info("[UI][%s] Check Body - Priority Title.".formatted(language));

        // check priority tooltips
        commonAction.hoverActions(priorityTooltips);
        String dbPriorityTooltips = commonAction.getAttribute(priorityTooltips, "data-original-title");
        String ppPriorityTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.priority.tooltips", language);
        assertCustomize.assertEquals(dbPriorityTooltips, ppPriorityTooltips, "[Failed][Body] Priority tooltips should be %s, but found %s.".formatted(ppPriorityTooltips, dbPriorityTooltips));
        logger.info("[UI][%s] Check Body - Priority tooltips.".formatted(language));

        // check priority text box placeholder
        String dbPriorityTextBoxPlaceholder = commonAction.getAttribute(priorityPlaceholder, "placeholder");
        String ppPriorityTextBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.priority.placeholder", language);
        assertCustomize.assertEquals(dbPriorityTextBoxPlaceholder, ppPriorityTextBoxPlaceholder, "[Failed][Body] Priority text box placeholder should be %s, but found %s.".formatted(ppPriorityTextBoxPlaceholder, dbPriorityTextBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Priority text box placeholder.".formatted(language));
    }

    void checkUIPlatform() throws Exception {
        // check platform title
        String dbPlatformTitle = commonAction.getText(platformLabel);
        String ppPlatformTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.title", language);
        assertCustomize.assertEquals(dbPlatformTitle, ppPlatformTitle, "[Failed][Body] Platform title should be %s, but found %s.".formatted(ppPlatformTitle, dbPlatformTitle));
        logger.info("[UI][%s] Check Body - Platform title.".formatted(language));

        // check platform tooltips
        commonAction.hoverActions(platformTooltips);
        String dbPlatformTooltips = commonAction.getAttribute(platformTooltips, "data-original-title");
        String ppPlatformTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.tooltips", language);
        assertCustomize.assertEquals(dbPlatformTooltips, ppPlatformTooltips, "[Failed][Body] Platform tooltips should be %s, but found %s.".formatted(ppPlatformTooltips, dbPlatformTooltips));
        logger.info("[UI][%s] Check Body - Platform tooltips.".formatted(language));

        // check App platform
        String dbApp = commonAction.getText(appLabel);
        String ppApp = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.app", language);
        assertCustomize.assertEquals(dbApp, ppApp, "[Failed][Body] Platform app should be %s, but found %s.".formatted(ppApp, dbApp));
        logger.info("[UI][%s] Check Body - Platform app.".formatted(language));

        // check Web platform
        String dbWeb = commonAction.getText(webLabel);
        String ppWeb = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.web", language);
        assertCustomize.assertEquals(dbWeb, ppWeb, "[Failed][Body] Platform web should be %s, but found %s.".formatted(ppWeb, dbWeb));
        logger.info("[UI][%s] Check Body - Platform web.".formatted(language));

        // check POS platform
        String dbPOS = commonAction.getText(inStoreLabel);
        String ppPOS = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.pos", language);
        assertCustomize.assertEquals(dbPOS, ppPOS, "[Failed][Body] Platform POS should be %s, but found %s.".formatted(ppPOS, dbPOS));
        logger.info("[UI][%s] Check Body - Platform POS.".formatted(language));

        // check GoSocial platform
        String dbGoSocial = commonAction.getText(goSocialLabel);
        String ppGoSocial = getPropertiesValueByDBLang("products.allProducts.createProduct.platform.gosocial", language);
        assertCustomize.assertEquals(dbGoSocial, ppGoSocial, "[Failed][Body] Platform GoSocial should be %s, but found %s.".formatted(ppGoSocial, dbGoSocial));
        logger.info("[UI][%s] Check Body - Platform GoSocial.".formatted(language));
    }

    void checkUIConfirmDeleteProductPopup() throws Exception {
        String dbTitle = commonAction.getText(titleOfConfirmDeleteProductPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.confirmDeleteProductPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Confirm delete product popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Confirm delete product popup - Title.".formatted(language));

        String dbWarningMessage = commonAction.getText(warningMessageOnConfirmDeleteProductPopup);
        String ppWarningMessage = getPropertiesValueByDBLang("products.allProducts.updateProduct.confirmDeleteProductPopup.warningMessage", language);
        assertCustomize.assertEquals(dbWarningMessage, ppWarningMessage, "[Failed][Confirm delete product popup] Warning message should be %s, but found %s.".formatted(ppWarningMessage, dbWarningMessage));
        logger.info("[UI][%s] Check Confirm delete product popup - Warning message.".formatted(language));

        String dbOKBtn = commonAction.getText(okTextOnConfirmDeleteProductPopup);
        String ppOKBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.confirmDeleteProductPopup.okBtn", language);
        assertCustomize.assertEquals(dbOKBtn, ppOKBtn, "[Failed][Confirm delete product popup] OK button should be %s, but found %s.".formatted(ppOKBtn, dbOKBtn));
        logger.info("[UI][%s] Check Confirm delete product popup - OK button.".formatted(language));

        String dbCancelBtn = commonAction.getText(cancelTextOnDeleteProductPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.confirmDeleteProductPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Confirm delete product popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Confirm delete product popup - Cancel button.".formatted(language));
    }

    void checkUICreateProductInfo() throws Exception {
        checkUICRHeaderProductPage();
        checkUIInformation();
        checkUIImages();
        checkUIPricing();
        checkUIVariations();
        checkUIAddConversionUnit();
        checkUIAddWholesaleProduct();
        checkUIDeposit();
        checkUISEO();
        checkUISaleChanel();
        checkUICollections();
        checkUICRWarehousing();
        checkUIPackageInformation();
        checkUIPriority();
        checkUIPlatform();
    }

    void checkUIUpdateProductInfo() throws Exception {
        checkUIUPHeaderProductPage();
        checkUIInformation();
        checkUIImages();
        checkUIPricing();
        checkUIVariations();
        checkUIAddConversionUnit();
        checkUIAddWholesaleProduct();
        checkUIDeposit();
        checkUISEO();
        checkUISaleChanel();
        checkUICollections();
        checkUIUPWarehousing();
        checkUIPackageInformation();
        checkUIPriority();
        checkUIPlatform();
    }

    public void navigateToProductAndDeleteAllVariation(int productId) {
        commonAction.navigateToURL(DOMAIN + updateProductPath.formatted(productId));
        new HomePage(driver).waitTillSpinnerDisappear();
        for (WebElement el : commonAction.getElements(loc_btnDeleteVariation)) {
            commonAction.clickElement(el);
        }
        completeUpdateProduct();
    }

    public void navigateToProductDetailById(int productId) {
        commonAction.navigateToURL(DOMAIN + updateProductPath.formatted(productId));
        new HomePage(driver).waitTillSpinnerDisappear1();
        commonAction.getElement(uiSEOSetting);
    }

    public Map<String, Integer> getStock() {

        By branchLocator = By.xpath("//*[@class='branch-list-stock__wrapper__row row']");

        /*
         * Loop through branches
         * then store its name and stock into a hashmap.
         * Retry the process when StaleElementReferenceException occurs
         */
        Map<String, Integer> stockByBranch;
        stockByBranch = new HashMap<>();
        for (int i = 0; i < commonAction.getElements(branchLocator).size(); i++) {
            String branchName;
            String stock;

            try {
                branchName = commonAction.getElements(branchLocator).get(i).findElement(By.xpath(".//div[1]")).getText();
                stock = commonAction.getElements(branchLocator).get(i).findElement(By.xpath(".//input")).getAttribute("value");
                stockByBranch.put(branchName, Integer.valueOf(stock));
            } catch (StaleElementReferenceException ex) {
                logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
                branchName = commonAction.getElements(branchLocator).get(i).findElement(By.xpath(".//div[1]")).getText();
                stock = commonAction.getElements(branchLocator).get(i).findElement(By.xpath(".//input")).getAttribute("value");
                stockByBranch.put(branchName, Integer.valueOf(stock));
            }
        }
        return stockByBranch;
    }

    public Map<String, Integer> getStockOfProductHavingVariations(String barcodeModel) {

        By variationLocator = By.xpath("(//input[contains(@id, 'barcode') and @value='%s']//ancestor::tr//span[@class='gs-fake-link '])[1]".formatted(barcodeModel));

        By branchLocator = By.xpath("//div[contains(@class,'h-fit-content')]//table[contains(@class,'stock_editor_modal__branch-table')]/thead//th");
        By quantityLocator = By.xpath("//div[contains(@class,'h-fit-content')]//table[contains(@class,'stock_editor_modal__branch-table')]/tbody//td");


        commonAction.click(variationLocator);
        commonAction.getElement(quantityLocator);

        /*
         * Loop through branches
         * then store its name and stock into a hashmap.
         * Retry the process when StaleElementReferenceException occurs
         */
        Map<String, Integer> stockByBranch = new HashMap<>();
        for (int i = 0; i < commonAction.getElements(branchLocator).size(); i++) {
            String branchName;
            String stock;

            try {
                branchName = commonAction.getElements(branchLocator).get(i).getText();
                stock = commonAction.getElements(quantityLocator).get(i).getText();
                stockByBranch.put(branchName, Integer.valueOf(stock));
            } catch (StaleElementReferenceException ex) {
                logger.debug("StaleElementReferenceException caught in getStockOfProductHavingVariations(). Retrying...");
                branchName = commonAction.getElements(branchLocator).get(i).getText();
                stock = commonAction.getElements(quantityLocator).get(i).getText();
                stockByBranch.put(branchName, Integer.valueOf(stock));
            }
        }

        commonAction.click(cancelBtnOnPopup);

        return stockByBranch;
    }

    public Map<String, List<String>> getIMEI() {

        By branchLocator = By.xpath("//table/thead//th");
        By quantityLocator = By.xpath("//table/tbody//td");
        By imeiLocator = By.xpath(".//div[@class='code']");


        commonAction.click(loc_lblUpdateStock);
        commonAction.getElement(quantityLocator);
        commonAction.sleepInMiliSecond(2000); // It takes some time for the IMEI to be rendered

        /*
         * Loop through branches
         * then store its name and IMEI values into a hashmap.
         * Retry the process when StaleElementReferenceException occurs
         */
        Map<String, List<String>> stockByBranch = new HashMap<>();
        for (int i = 1; i < commonAction.getElements(branchLocator).size(); i++) {
            String branchName;
            String[] imeiArray;

            try {
                branchName = commonAction.getElements(branchLocator).get(i).getText();
                imeiArray = commonAction.getElements(quantityLocator).get(i).findElement(imeiLocator).getText().split("\n");
                stockByBranch.put(branchName, Arrays.asList(imeiArray));
            } catch (StaleElementReferenceException ex) {
                logger.debug("StaleElementReferenceException caught in getIMEI(). Retrying...");
                branchName = commonAction.getElements(branchLocator).get(i).getText();
                imeiArray = commonAction.getElements(quantityLocator).get(i).findElement(imeiLocator).getText().split("\n");
                stockByBranch.put(branchName, Arrays.asList(imeiArray));
            }
        }

        new ConfirmationDialog(driver).clickCancelBtn();

        return stockByBranch;
    }

    public Map<String, List<String>> getIMEIOfProductHavingVariations(String barcodeModel) {
        By variationLocator = By.xpath("(//input[contains(@id, 'barcode') and @value='%s']//ancestor::tr//span[@class='gs-fake-link '])[1]".formatted(barcodeModel));

        By branchLocator = By.xpath("(//div[@class='table']//table/thead//th[@class='label'])[last()]//following-sibling::*");
        By quantityLocator = By.xpath("//div[@class='table']//table/tbody//form");
        By imeiLocator = By.xpath(".//div[@class='code']");

        commonAction.click(variationLocator);
        commonAction.getElement(quantityLocator);
        // Sometimes the dialog has appeared but the IMEI values is not displayed
        commonAction.sleepInMiliSecond(1000);
        for (int i = 0; i < 5; i++) {
            int sixe = commonAction.getElements(new ByChained(imeiLocator, By.xpath("./div"))).size();
            logger.debug("========= Size: " + sixe);
            if (sixe > 0) break;
            commonAction.sleepInMiliSecond(1000);
        }

        /*
         * Loop through branches
         * then store its name and IMEI values into a hashmap.
         * Retry the process when StaleElementReferenceException occurs
         */
        Map<String, List<String>> stockByBranch = new HashMap<>();
        for (int i = 0; i < commonAction.getElements(branchLocator).size(); i++) {

            String branchName;
            String[] imeiArray;

            try {
                branchName = commonAction.getElements(branchLocator).get(i).getText();
                imeiArray = commonAction.getElements(quantityLocator).get(i).findElement(imeiLocator).getText().split("\n");
                stockByBranch.put(branchName, Arrays.asList(imeiArray));
            } catch (StaleElementReferenceException ex) {
                logger.debug("StaleElementReferenceException caught in getIMEI(). Retrying...");
                branchName = commonAction.getElements(branchLocator).get(i).getText();
                imeiArray = commonAction.getElements(quantityLocator).get(i).findElement(imeiLocator).getText().split("\n");
                stockByBranch.put(branchName, Arrays.asList(imeiArray));
            }
        }

        new ConfirmationDialog(driver).clickCancelBtn();

        return stockByBranch;
    }

    public void clickImport() {
        commonAction.clickElement(IMPORT_BTN);
        logger.info("Click on import button.");
    }

    public void clickImportProduct() {
        commonAction.clickElement(IMPORT_PRODUCT_BTN);
        logger.info("Click on import product button.");
    }

    public void clickImportBtbOnModal() {
        commonAction.clickElement(IMPORT_BTN_MODAL);
        logger.info("Click on Import button on Import Product List modal.");
    }

    public void importProduct(String fileName) {
        clickImport();
        clickImportProduct();
        commonAction.sleepInMiliSecond(2000);
        commonAction.uploadMultipleFile(FILE_INPUT, "import_product", fileName);
        commonAction.sleepInMiliSecond(1000);
        clickImportBtbOnModal();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        waitImportingTextDisapear();
    }

    public void waitImportingTextDisapear() {
        commonAction.waitForElementInvisible(IMPORTING_LBL, 15);
        logger.info("Importing text have disappeared");
    }
}
