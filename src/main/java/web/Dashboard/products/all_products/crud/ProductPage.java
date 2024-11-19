package web.Dashboard.products.all_products.crud;

import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import api.Seller.products.product_collections.APIProductCollection;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import api.Seller.setting.VAT;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.all_products.crud.conversion_unit.ConversionUnitPage;
import web.Dashboard.products.all_products.crud.shopeesync.ShopeeSyncPage;
import web.Dashboard.products.all_products.crud.sync_lazada.SyncLazadaPage;
import web.Dashboard.products.all_products.crud.variation_detail.VariationDetailPage;
import web.Dashboard.products.all_products.crud.wholesale_price.WholesaleProductPage;

import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;

public class ProductPage extends ProductPageElement {
    WebDriver driver;
    @Getter
    UICommonAction commonAction;
    String createProductPath = "/product/create";

    public static String updateProductPath(int productId) {
        return "/product/edit/" + productId;
    }

    AssertCustomize assertCustomize;
    private boolean noDiscount = nextBoolean();
    private boolean hasCostPrice = false;
    private boolean hasDimension = false;
    private boolean hasSEO = false;
    private boolean manageByLotDate = false;
    private boolean hasAttribution = false;
    Logger logger = LogManager.getLogger(ProductPage.class);
    BranchInfo brInfo;
    private static StoreInfo storeInfo;
    private static ProductInfoV2 productInfo;
    LoginInformation loginInformation;

    public ProductPage(WebDriver driver) {
        this.driver = driver;

        // init common function
        commonAction = new UICommonAction(driver);

        // init assert customize function
        assertCustomize = new AssertCustomize(driver);
    }

    public ProductPage getLoginInformation(LoginInformation loginInformation) {
        // get login information (username, password)
        this.loginInformation = loginInformation;

        // get branch information
        brInfo = new BranchManagement(loginInformation).getInfo();

        // get store information
        storeInfo = new StoreInformation(loginInformation).getInfo();
        return this;
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
    boolean showInStore = true;
    boolean showInGoSocial = true;
    @Getter
    private static boolean manageByIMEI;
    @Getter
    private static int productId;
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
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnAddVariation).findElement(By.xpath("./ancestor::div[@class='uik-widget__wrapper gs-widget gs-widget ']/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnAddVariation));
            return;
        }
        commonAction.click(loc_btnAddVariation);
        logger.info("Clicked on 'Add Variation' button.");
    }

    public void clickAddDepositBtn() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(btnAddDeposit).findElement(By.xpath("./ancestor::div[@class='uik-widget__wrapper gs-widget gs-widget ']/parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(btnAddDeposit));
            return;
        }
        commonAction.clickElement(commonAction.getElement(btnAddDeposit));
        logger.info("Clicked on 'Add Deposit' button.");
    }

    public void inputSEOTitle(String seoTitle) {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(this.loc_txtSEOTitle).findElement(By.xpath("./ancestor::div[contains(@class,'gs-widget  seo-editor')]/descendant::*[1]")))) {
            Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(this.loc_txtSEOTitle)));
            return;
        }
        commonAction.sendKeys(this.loc_txtSEOTitle, seoTitle);
        logger.info("Input '{}' into SEO Title field.", seoTitle);
    }

    public String getSEOTitle() {
        String title = commonAction.getElementAttribute(commonAction.getElement(loc_txtSEOTitle), "value");
        logger.info("Retrieved SEO Title: {}", title);
        return title;
    }

    public boolean isPrintBarcodeDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.isElementNotDisplay(driver.findElements(loc_dlgPrintProductBarcode));
    }

    public boolean isDeleteVariationBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return !commonAction.isElementNotDisplay(commonAction.getElements(loc_btnDeleteVariation));
    }

    public boolean isDeleteDepositBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return !commonAction.isElementNotDisplay(loc_icnDeleteDeposit);
    }

    public void clickOnTheCreateProductBtn() {
        // click create product button
        commonAction.click(loc_btnCreateProduct);

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
        driver.navigate().refresh();

        // open language dropdown list
        commonAction.click(loc_ddvSelectedLanguage);

        // language xpath
        By languageXpath = By.xpath(loc_ddvLanguageValue.formatted(language));

        // select language
        commonAction.clickJS(languageXpath);

        // log
        logger.info("New language: {}", language);
        return this;
    }


    /* Thang */

    public ProductPage setNoDiscount(boolean noDiscount) {
        this.noDiscount = noDiscount;
        return this;
    }

    public ProductPage setHasCostPrice(boolean hasCostPrice) {
        this.hasCostPrice = hasCostPrice;
        return this;
    }

    public ProductPage setHasDimension(boolean hasDimension) {
        this.hasDimension = hasDimension;
        return this;
    }

    public ProductPage setHasSEO(boolean hasSEO) {
        this.hasSEO = hasSEO;
        return this;
    }

    public ProductPage setManageByLotDate(boolean manageByLotDate) {
        this.manageByLotDate = manageByLotDate;
        return this;
    }

    public ProductPage setHasAttribution(boolean hasAttribution) {
        this.hasAttribution = hasAttribution;
        return this;
    }

    public ProductPage setShowOutOfStock(boolean showOutOfStock) {
        this.showOutOfStock = showOutOfStock;
        return this;
    }

    public ProductPage setSellingPlatform(boolean showOnApp, boolean showOnWeb, boolean showInStore, boolean showInGoSocial) {
        this.showOnApp = showOnApp;
        this.showOnWeb = showOnWeb;
        this.showInStore = showInStore;
        this.showInGoSocial = showInGoSocial;
        return this;
    }

    @SneakyThrows
    public ProductPage navigateToCreateProductPage() {
        // access to create product page by URL
        sleep(3000);
        driver.get("%s%s".formatted(DOMAIN, createProductPath));
        if (driver.getCurrentUrl().contains(createProductPath)) {
            driver.get("%s%s".formatted(DOMAIN, createProductPath));
        }

        // log
        logger.info("Navigate to create product page");

        // hide Facebook bubble
        commonAction.removeFbBubble();

        return this;
    }

    public ProductPage navigateToUpdateProductPage(int productID) {
        // get product id
        ProductPage.productId = productID;

        // log
        logger.info("Update product id: {}", productId);

        // get product information
        productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);

        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath(productID)));

        // refresh page
        driver.navigate().refresh();

        commonAction.getElement(loc_lblSEOSetting);
        long currentPointerHeight = (long) ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollTop;", commonAction.getElement(loc_bodyApp));
        assertCustomize.assertTrue(currentPointerHeight == 0L, "Product detail page is not focused on top of page.");

        // delete old wholesale product config if any
        if (commonAction.isCheckedJS(loc_chkAddWholesalePricing)) {
            // uncheck add wholesale pricing checkbox to delete old wholesale config
            commonAction.click(loc_chkAddWholesalePricing);

            // confirm delete old wholesale config
            commonAction.click(loc_dlgConfirm_btnOK);

            // save changes
            commonAction.click(loc_btnSave);

            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgSuccessNotification).isEmpty(),
                    "Can not remove old wholesale config.");

            driver.navigate().refresh();
        }

        // hide Facebook bubble
        commonAction.removeFbBubble();

        return this;
    }

    void inputProductName(String name) {
        // input product name
        commonAction.sendKeys(loc_txtProductName, name);
        logger.info("Input product name: {}", name);
    }

    void inputProductDescription() {
        // input product description
        description = "[%s] product descriptions".formatted(storeInfo.getDefaultLanguage());
        commonAction.sendKeys(loc_txaProductDescription, description);
        logger.info("Input product description: {}", description);
    }

    void uploadProductImage(String... imageFile) {
        // remove old product image
        List<WebElement> removeImageIcons = commonAction.getListElement(loc_icnRemoveImages);
        if (!removeImageIcons.isEmpty())
            IntStream.iterate(removeImageIcons.size() - 1, iconIndex -> iconIndex >= 0, iconIndex -> iconIndex - 1)
                    .forEach(iconIndex -> commonAction.clickJS(loc_icnRemoveImages, iconIndex));
        // upload product image
        for (String imgFile : imageFile) {
            String filePath = new DataGenerator().getPathOfFileInResourcesRoot(imgFile);
            commonAction.uploads(imgUploads, filePath);
            logger.info("[Upload product image popup] Upload images, file path: {}", filePath);
        }
    }

    void selectVAT() {
        // open VAT dropdown
        commonAction.clickJS(loc_ddvSelectedVAT);
        logger.info("Open VAT dropdown.");

        // get VAT name
        TaxInfo info = new VAT(loginInformation).getInfo();
        List<String> vatList = new ArrayList<>(info.getTaxName().stream().filter(vatName -> info.getTaxType().get(info.getTaxName().indexOf(vatName)).equals("SELL")).toList());
        if (vatList.size() > 1) {
            if ((productInfo != null)) vatList.remove(productInfo.getTaxName());
            String vatName = vatList.get(nextInt(vatList.size()));

            // select VAT
            commonAction.clickJS(vatName.equals("tax.value.include") ? loc_ddvNoVAT : By.xpath(loc_ddvOthersVAT.formatted(vatName)));

            // log
            logger.info("Select VAT: {}", vatName);
        }
    }

    void selectCollection() {
        // click on collection search box
        List<String> collectionsNameList = new APIProductCollection(loginInformation).getManualCollection().getCollectionNames();
        if (collectionsNameList.isEmpty()) logger.info("Store has not created any collections yet.");
        else {
            // remove all collections
            List<WebElement> removeIcons = commonAction.getListElement(loc_icnRemoveCollection);
            if (!removeIcons.isEmpty()) {
                IntStream.iterate(removeIcons.size() - 1, index -> index >= 0, index -> index - 1)
                        .forEach(index -> commonAction.clickJS(loc_icnRemoveCollection, index));
                logger.info("Remove assigned collections.");
            }
            // open collection dropdown
            commonAction.click(loc_txtCollectionSearchBox);
            logger.info("Open collections dropdown.");

            // get collection name
            String collectionName = collectionsNameList.get(0);

            // select collection
            commonAction.click(By.xpath(loc_ddvCollectionValue.formatted(collectionName)));

            // log
            logger.info("Select collection: {}", collectionName);
        }
    }

    void inputWithoutVariationProductSKU() {
        String sku = "SKU" + Instant.now().toEpochMilli();
        commonAction.sendKeys(loc_txtWithoutVariationSKU, sku);
        logger.info("Input SKU: {}", sku);
    }

    void updateWithoutVariationProductSKU() {
        // open update SKU popup
        commonAction.click(loc_txtWithoutVariationSKU);
        logger.info("Open update SKU popup.");

        // wait Update SKU popup visible
        commonAction.getElement(loc_dlgUpdateSKU);
        logger.info("Wait update SKU popup visible.");

        // input SKU for each branch
        for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
            String sku = "SKU_%s_%s".formatted(brInfo.getActiveBranches().get(brIndex), Instant.now().toEpochMilli());
            commonAction.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
            logger.info("Update SKU: {}", sku);
        }

        // click around
        commonAction.click(loc_ttlUpdateSKU);

        // close Update SKU popup
        commonAction.click(loc_dlgCommons_btnUpdate);
    }

    void setManageInventory(boolean isIMEIProduct) {
        manageByIMEI = isIMEIProduct;
        // set manage inventory by product or IMEI/Serial number
        if (!driver.getCurrentUrl().contains("/edit/"))
            commonAction.selectDropdownOptionByValue(loc_ddlManageInventory, isIMEIProduct ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

        // manage by lot date
        if (!isIMEIProduct && manageByLotDate) {
            if (!commonAction.isCheckedJS(loc_chkManageStockByLotDate)) {
                commonAction.clickJS(loc_chkManageStockByLotDate);
            }
        }

        // log
        logger.info("Manage inventory by: {}", isIMEIProduct ? "IMEI/Serial Number" : "Product");
    }

    void setSFDisplay() {
        // Display if out of stock
        if (commonAction.isCheckedJS(loc_chkDisplayIfOutOfStock) != showOutOfStock)
            commonAction.clickJS(loc_chkDisplayIfOutOfStock);

        // Hide remaining stock on online store
        if (commonAction.isCheckedJS(loc_chkHideRemainingStock) != hideStock)
            commonAction.clickJS(loc_chkHideRemainingStock);

        // Show as listing product on storefront
        if (commonAction.isCheckedJS(loc_chkShowAsListingProduct) != enableListing)
            commonAction.clickJS(loc_chkShowAsListingProduct);
    }

    void setPriority(int priority) {
        // set product priority (1-100)
        commonAction.sendKeys(loc_txtPriority, String.valueOf(priority));
        logger.info("Input priority: {}", priority);
    }

    void setProductDimension() {
        String dimension = (hasDimension) ? "10" : "0";
        // input product weight
        commonAction.sendKeys(loc_txtWeight, dimension);
        logger.info("Input weight: {}", dimension);

        // input product length
        commonAction.sendKeys(loc_txtLength, dimension);
        logger.info("Input length: {}", dimension);

        // input product width
        commonAction.sendKeys(loc_txtWidth, dimension);
        logger.info("Input width: {}", dimension);

        // input product height
        commonAction.sendKeys(loc_txtHeight, dimension);
        logger.info("Input height: {}", dimension);

    }

    void selectPlatform() {
        // App
        if (commonAction.getElement(loc_chkApp).isSelected() != showOnApp)
            commonAction.clickJS(loc_chkApp);

        // Web
        if (commonAction.getElement(loc_chkWeb).isSelected() != showOnWeb)
            commonAction.clickJS(loc_chkWeb);

        // In-store
        if (commonAction.getElement(loc_chkInStore).isSelected() != showInStore)
            commonAction.clickJS(loc_chkInStore);

        // GoSocial
        if (commonAction.getElement(loc_chkGoSocial).isSelected() != showInGoSocial)
            commonAction.clickJS(loc_chkGoSocial);

    }

    void addAttribution() {
        if (!commonAction.getListElement(loc_icnDeleteAttribution).isEmpty()) {
            int bound = commonAction.getListElement(loc_icnDeleteAttribution).size();
            IntStream.iterate(bound - 1, deleteIndex -> deleteIndex >= 0, deleteIndex -> deleteIndex - 1)
                    .forEach(deleteIndex -> commonAction.clickJS(loc_icnDeleteAttribution, deleteIndex));
        }

        if (hasAttribution) {
            int numOfAttribute = nextInt(10);
            // add attribution
            IntStream.range(0, numOfAttribute)
                    .forEachOrdered(ignored -> commonAction.clickJS(loc_btnAddAttribution));

            // input attribution
            long epoch = Instant.now().toEpochMilli();
            IntStream.range(0, numOfAttribute).forEach(attIndex -> {
                commonAction.sendKeys(loc_txtAttributionName, attIndex, "name_%s_%s".formatted(attIndex, epoch));
                commonAction.sendKeys(loc_txtAttributionValue, attIndex, "value_%s_%s".formatted(attIndex, epoch));
                if (!Objects.equals(commonAction.isCheckedJS(loc_chkDisplayAttribute, attIndex), nextBoolean())) {
                    commonAction.clickJS(loc_chkDisplayAttribute, attIndex);
                }
            });
        }
    }

    void inputSEO() {
        // SEO title
        String title = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        commonAction.sendKeys(loc_txtSEOTitle, title);
        logger.info("SEO title: {}.", title);

        // SEO description
        String description = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        commonAction.sendKeys(loc_txtSEODescription, description);
        logger.info("SEO description: {}.", description);

        // SEO keyword
        String keyword = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        commonAction.sendKeys(loc_txtSEOKeywords, keyword);
        logger.info("SEO keyword: {}", keyword);

        // SEO URL
        String url = "%s%s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        commonAction.sendKeys(loc_txtSEOUrl, url);
        logger.info("SEO url: {}.", url);
    }

    void productInfo(String name, boolean isIMEIProduct) {
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
        addAttribution();
        if (hasSEO) inputSEO();
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
        commonAction.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(productListingPrice.get(0)));
        logger.info("Listing price: {}", productListingPrice.get(0));

        // input selling price
        commonAction.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(productSellingPrice.get(0)));
        logger.info("Selling price: {}", productSellingPrice.get(0));

        // input cost price
        long costPrice = hasCostPrice ? nextLong(productSellingPrice.get(0)) : 0;
        commonAction.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(costPrice));
        logger.info("Cost price {}", costPrice);
    }

    void addIMEIForEachBranch(String variationValue, List<Integer> branchStock) {
        // select all branches
        commonAction.click(loc_dlgAddIMEISelectedBranch);
        logger.info("[Add IMEI popup] Open all branches dropdown.");

        if (!commonAction.isCheckedJS(loc_dlgAddIMEI_chkSelectAllBranches))
            commonAction.clickJS(loc_dlgAddIMEI_chkSelectAllBranches);
        else commonAction.click(loc_dlgAddIMEISelectedBranch);
        logger.info("[Add IMEI popup] Select all branches.");

        // remove old IMEI
        int bound = commonAction.getListElement(loc_dlgAddIMEI_icnDeleteIMEI).size();
        for (int index = bound - 1; index >= 0; index--) {
            commonAction.clickJS(loc_dlgAddIMEI_icnDeleteIMEI, index);
        }
        logger.info("Remove old IMEI.");

        // input IMEI/Serial number for each branch
        for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
            String brName = brInfo.getActiveBranches().get(brIndex);
            int brStockIndex = brInfo.getBranchName().indexOf(brInfo.getActiveBranches().get(brIndex));
            for (int i = 0; i < branchStock.get(brStockIndex); i++) {
                String imei = "%s%s_IMEI_%s_%s\n".formatted(variationValue != null ? "%s_".formatted(variationValue) : "", brInfo.getActiveBranches().get(brIndex), Instant.now().toEpochMilli(), i);
                commonAction.sendKeys(loc_dlgAddIMEI_txtAddIMEI, brIndex, imei);
                logger.info("Input IMEI: {}", imei.replace("\n", ""));
            }
            logger.info("{}[{}] Add IMEI, stock: {}", variationValue == null ? "" : "[%s]".formatted(variationValue), brName, branchStock.get(brStockIndex));
        }

        // save IMEI/Serial number
        commonAction.click(loc_dlgAddIMEI_btnSave);
        logger.info("Close Add IMEI popup.");
    }

    public void inputWithoutVariationStock(int... branchStockQuantity) {
        /* get without variation stock information */
        // get variation list
        variationList = new ArrayList<>();
        variationList.add(null);

        // get product stock quantity
        productStockQuantity = Map.of("", IntStream.range(0, brInfo.getBranchName().size()).mapToObj(i -> (branchStockQuantity.length > i) ? (brInfo.getActiveBranches().contains(brInfo.getBranchName().get(i)) ? branchStockQuantity[i] : 0) : 0).toList());

        /* input stock for each branch */
        if (manageByIMEI) {
            // open add IMEI popup
            commonAction.click(loc_txtWithoutVariationBranchStock);
            logger.info("[Create] Open Add IMEI popup without variation product.");

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch("", productStockQuantity.get(""));
            logger.info("[Create] Complete add stock for IMEI product.");

        } else {
            // update stock for normal product
            IntStream.range(0, brInfo.getActiveBranches().size()).forEach(brIndex -> {
                commonAction.sendKeys(loc_txtWithoutVariationBranchStock, brIndex, String.valueOf(productStockQuantity.get("").get(brIndex)));
                logger.info("[%s] Input stock: {}", brInfo.getActiveBranches().get(brIndex), productStockQuantity.get("").get(brIndex));
            });
            logger.info("[Create] Complete update stock for Normal product.");
        }

    }

    void addNormalStockForEachBranch(List<Integer> branchStock, int varIndex) {
        // select all branches
        commonAction.openDropdownJS(loc_dlgUpdateStock_ddvSelectedBranch, loc_dlgUpdateStock_chkSelectAllBranches);
        logger.info("[Update stock popup] Open all branches dropdown.");

        // select all branches
        if (!commonAction.isCheckedJS(loc_dlgUpdateStock_chkSelectAllBranches))
            commonAction.clickJS(loc_dlgUpdateStock_chkSelectAllBranches);
        else commonAction.closeDropdown(loc_dlgUpdateStock_ddvSelectedBranch, loc_dlgUpdateStock_chkSelectAllBranches);
        logger.info("[Update stock popup] Select all branches.");

        // switch to change stock tab
        commonAction.click(loc_dlgUpdateStock_tabChange);

        // input stock quantity to visible stock input field
        int stock = Collections.max(branchStock) + 1;
        commonAction.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));

        // input stock for each branch
        brInfo.getActiveBranches().forEach(brName -> {
            int brStockIndex = brInfo.getBranchName().indexOf(brName);
            if (!commonAction.getListElement(loc_dlgUpdateStock_txtBranchStock(brName), 1000).isEmpty()) {
                commonAction.sendKeys(loc_dlgUpdateStock_txtBranchStock(brName), String.valueOf(branchStock.get(brStockIndex)));
                logger.info("{}[{}] Update stock: {}", variationList.get(varIndex) == null ? "" : "[%s]".formatted(variationList.get(varIndex)), brName, branchStock.get(brStockIndex));
            } else {
                logger.info("{}[{}] Add stock: {}", variationList.get(varIndex) == null ? "" : "[%s]".formatted(variationList.get(varIndex)), brName, stock);
            }
        });
        // close Update stock popup
        commonAction.click(loc_dlgCommons_btnUpdate);
        logger.info("Close Update stock popup.");
    }

    void updateWithoutVariationStock(int... branchStockQuantity) {
        /* get without variation stock information */
        // get variation list
        variationList = new ArrayList<>();
        variationList.add(null);

        // get product stock quantity
        productStockQuantity = new HashMap<>();
        productStockQuantity.put("", IntStream.range(0, brInfo.getBranchName().size()).mapToObj(brIndex -> (branchStockQuantity.length > brIndex) ? (brInfo.getActiveBranches().contains(brInfo.getBranchName().get(brIndex)) ? branchStockQuantity[brIndex] : 0) : 0).toList());

        /* input stock for each branch */
        if (manageByIMEI) {
            // open Add IMEI popup
            commonAction.openDropdownJS(loc_txtWithoutVariationBranchStock, 0, loc_dlgAddIMEI);
            logger.info("[Update] Open Add IMEI popup without variation product.");

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch("", productStockQuantity.get(""));
            logger.info("[Update] Complete add stock for IMEI product.");
        } else {
            // open Update stock popup
            commonAction.openDropdownJS(loc_txtWithoutVariationBranchStock, 0, loc_dlgUpdateStock);
            logger.info("Open Update stock popup.");

            // add stock for each branch
            addNormalStockForEachBranch(productStockQuantity.get(""), 0);
            logger.info("[Update] Complete update stock for Normal product.");
        }

    }

    // Variation product
    public void addVariations() {
        // generate variation map
        variationMap = new DataGenerator().randomVariationMap(storeInfo.getDefaultLanguage());
        logger.info("Variation map: {}", variationMap);

        // get variation list from variation map
        variationList = new DataGenerator().getVariationList(variationMap);
        logger.info("Variation list: {}", variationList);

        // delete old variation
        List<WebElement> deleteVariationIcons = commonAction.getListElement(loc_btnDeleteVariation);
        IntStream.iterate(deleteVariationIcons.size() - 1, index -> index >= 0, index -> index - 1).forEach(index -> commonAction.clickJS(loc_btnDeleteVariation, index));
        logger.info("Remove old variation.");

        // click add variation button
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(ignored -> commonAction.clickJS(loc_btnAddVariation));
        logger.info("Add new variation group.");

        // input variation name and variation value
        for (int groupIndex = 0; groupIndex < variationMap.keySet().size(); groupIndex++) {
            String varName = variationMap.keySet().stream().toList().get(groupIndex);
            logger.info("Input variation name: {}", varName);

            // input variation name
            commonAction.sendKeys(loc_txtVariationName, groupIndex, varName);

            // input variation value
            for (String varValue : variationMap.get(varName)) {
                // input variation value
                commonAction.getElement(loc_txtVariationValue, groupIndex).sendKeys(varValue);

                // wait suggestion
                commonAction.sleepInMiliSecond(500);

                // complete input variation value
                commonAction.getElement(loc_txtVariationValue, groupIndex).sendKeys(Keys.chord(Keys.ENTER));

                // log
                logger.info("Input variation value: {}", varValue);
            }
        }

        commonAction.click(loc_lblVariations);
    }


    void inputVariationPrice() {
        // get listing, selling price
        productListingPrice = new ArrayList<>();
        productSellingPrice = new ArrayList<>();
        IntStream.range(0, variationList.size()).forEachOrdered(i -> {
            productListingPrice.add(nextLong(MAX_PRICE));
            if (noDiscount) productSellingPrice.add(productListingPrice.get(i));
            else productSellingPrice.add(nextLong(productListingPrice.get(i)));
        });

        // select all variation
        if (!commonAction.isCheckedJS(loc_tblVariation_chkSelectAll))
            commonAction.clickJS(loc_tblVariation_chkSelectAll);

        // open list action dropdown
        commonAction.clickJS(loc_tblVariation_lnkSelectAction);

        // open Update price popup
        commonAction.click(loc_tblVariation_ddvActions);

        // input product price
        IntStream.range(0, variationList.size()).forEachOrdered(varIndex -> {
            // get current variation
            String variation = variationList.get(varIndex);

            // input listing price
            long listingPrice = productListingPrice.get(varIndex);
            commonAction.sendKeys(loc_dlgUpdatePrice_txtListingPrice, varIndex, String.valueOf(listingPrice));
            logger.info("[{}] Listing price: {}.", variation, listingPrice);

            // input selling price
            long sellingPrice = productSellingPrice.get(varIndex);
            commonAction.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, varIndex, String.valueOf(sellingPrice));
            logger.info("[{}] Selling price: {}.", variation, sellingPrice);

            // input costPrice
            long costPrice = hasCostPrice ? nextLong(sellingPrice) : 0;
            commonAction.sendKeys(loc_dlgUpdatePrice_txtCostPrice, varIndex, String.valueOf(costPrice));
            logger.info("[{}] Cost price: {}.", variation, costPrice);
        });


        // click around
        commonAction.click(loc_ttlUpdatePrice);

        // close Update price popup
        commonAction.click(loc_dlgCommons_btnUpdate);
    }

    public void inputVariationStock(int increaseNum, int... branchStockQuantity) {
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
            commonAction.clickJS(loc_tblVariation_txtStock, varIndex);

            if (manageByIMEI) {
                addIMEIForEachBranch(variationList.get(varIndex), productStockQuantity.get(variationList.get(varIndex)));
            } else addNormalStockForEachBranch(productStockQuantity.get(variationList.get(varIndex)), varIndex);
        }
    }

    void inputVariationSKU() {
        // input SKU
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update SKU popup
            commonAction.click(loc_tblVariation_txtSKU, varIndex);

            // input SKU for each branch
            for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
                String sku = "SKU_%s_%s_%s".formatted(variationList.get(varIndex), brInfo.getActiveBranches().get(brIndex), Instant.now().toEpochMilli());
                commonAction.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
                logger.info("[Update SKU popup] Input SKU: {}", sku);
            }

            // click around
            commonAction.click(loc_ttlUpdateSKU);

            // close Update SKU popup
            commonAction.click(loc_dlgCommons_btnUpdate);
        }
    }

    void uploadVariationImage(String... imageFile) {
        // upload image for each variation
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update SKU popup
            commonAction.click(loc_tblVariation_imgUploads, varIndex);
            logger.info("Open upload variation image popup.");

            // upload image
            for (String imgFile : imageFile) {
                String filePath = new DataGenerator().getPathOfFileInResourcesRoot(imgFile);
                commonAction.uploads(loc_dlgUploadsImage_btnUploads, filePath);
                logger.info("[Upload variation image popup] Upload images, file path: {}", filePath);
            }

            // close Update image popup
            commonAction.click(loc_dlgCommons_btnUpdate);
        }
    }

    /* Active/Deactivate product */
    public ProductPage changeProductStatus(String status, int productID) {
        // get product information
        productInfo = new APIProductDetailV2(loginInformation).getInfo(productID);

        if (!status.equals(productInfo.getBhStatus())) {
            // log
            logger.info("Change product status, id: {}", productID);

            // navigate to product detail page by URL
            driver.get("%s%s".formatted(DOMAIN, updateProductPath(productID)));

            // wait page loaded
            commonAction.getElement(loc_lblSEOSetting);

            // change status
            commonAction.clickJS(loc_btnDeactivate);

            logger.info("change product status from %s to {}", productInfo.getBhStatus(), status);
        }
        return this;
    }

    public void deleteProduct(int productID) throws Exception {
        // get product information
        productInfo = new APIProductDetailV2(loginInformation).getInfo(productID);

        if (!productInfo.isDeleted()) {
            // log
            logger.info("Delete product id: {}", productID);

            // navigate to product detail page by URL
            driver.get("%s%s".formatted(DOMAIN, updateProductPath(productID)));

            // wait page loaded
            commonAction.getElement(loc_lblSEOSetting);

            // open Confirm delete popup
            commonAction.click(loc_btnDelete);

            // close confirm delete product popup
            commonAction.click(loc_dlgConfirmDelete_btnOK);
        }
    }

    /* Complete create/update product */
    void completeCreateProduct() {
        // save changes
        commonAction.click(loc_btnSave);

        // if create product successfully, close notification popup
        if (!commonAction.getListElement(loc_dlgSuccessNotification, 30000).isEmpty()) {
            // close notification popup
            commonAction.click(loc_dlgNotification_btnClose);
        } else Assert.fail("[Failed][Create product] Can not create product.");

        // log
        logger.info("Wait and get product id after creation.");

        // wait api return list product
        productId = new APIAllProducts(loginInformation).searchProductIdByName(name);

        // log
        logger.info("Complete create product, id: {}", productId);

        // verify test
        AssertCustomize.verifyTest();
    }

    void completeUpdateProduct() {
        // save changes
        commonAction.clickJS(loc_btnSave);
        //close waning popup if any
        if(!commonAction.getListElement(new ConfirmationDialog(driver).loc_btnYes,3000).isEmpty()){
            new ConfirmationDialog(driver).clickYesBtn();
        }
        // if update product successfully, close notification popup
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgSuccessNotification, 30000).isEmpty(), "Can not update product.");
        if (!commonAction.getListElement(loc_dlgSuccessNotification, 30000).isEmpty()) {
            // close notification popup
            commonAction.click(loc_dlgNotification_btnClose);

            // log
            logger.info("Complete update product.");
        }
    }

    public void configWholesaleProduct(ProductInfoV2 productInfo) {
        navigateToUpdateProductPage(productInfo.getId());

        if (productId != 0) {
            if (productInfo.isHasModel()) new WholesaleProductPage(driver, loginInformation, productInfo.getId())
                    .navigateToWholesaleProductPage()
                    .getWholesaleProductInfo()
                    .addWholesaleProductVariation();
            else new WholesaleProductPage(driver, loginInformation, productInfo.getId())
                    .navigateToWholesaleProductPage()
                    .getWholesaleProductInfo()
                    .addWholesaleProductWithoutVariation();

            // complete config wholesale product
            commonAction.click(loc_btnSave);
        } else {
            logger.info("Can not found product id.");
        }
    }

    public void configConversionUnit(ProductInfoV2 productInfo) {
        if (hasModel) new ConversionUnitPage(driver, loginInformation, productInfo)
                .navigateToConversionUnitPage()
                .addConversionUnitVariation();
        else new ConversionUnitPage(driver, loginInformation, productInfo)
                .navigateToConversionUnitPage()
                .addConversionUnitWithoutVariation();
    }

    /* Create product */
    public ProductPage createWithoutVariationProduct(boolean isIMEIProduct, int... branchStock) throws Exception {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] START... ");

        hasModel = false;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, isIMEIProduct);
        inputWithoutVariationPrice();
        if (!manageByLotDate) inputWithoutVariationStock(branchStock);
        inputWithoutVariationProductSKU();
        completeCreateProduct();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] DONE!!! ");

        return this;
    }

    public ProductPage createVariationProduct(boolean isIMEIProduct, int increaseNum, int... branchStock) throws Exception {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] START... ");

        hasModel = true;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), isIMEIProduct ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, isIMEIProduct);
        addVariations();
        uploadVariationImage("img.jpg");
        inputVariationPrice();
        if (!manageByLotDate) inputVariationStock(increaseNum, branchStock);
        inputVariationSKU();
        completeCreateProduct();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] DONE!!! ");

        return this;
    }

    /* Update Product */
    public void updateWithoutVariationProduct(int... newBranchStock) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [UpdateWithoutVariationProduct] START... ");

        hasModel = false;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"));
        inputWithoutVariationPrice();
        if (!manageByLotDate) updateWithoutVariationStock(newBranchStock);
        updateWithoutVariationProductSKU();
        completeUpdateProduct();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [UpdateWithoutVariationProduct] DONE!!! ");
    }

    public ProductPage updateVariationProduct(int newIncreaseNum, int... newBranchStock) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [UpdateVariationProduct] START... ");

        hasModel = true;

        // product name
        name = "[%s] %s".formatted(storeInfo.getDefaultLanguage(), productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? ("Auto - IMEI - Variation - ") : ("Auto - Normal - Variation - "));
        name += new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        productInfo(name, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER"));
        if (!productInfo.isLotAvailable() && !manageByLotDate) {
            addVariations();
            uploadVariationImage("img.jpg");
            inputVariationPrice();
            inputVariationStock(newIncreaseNum, newBranchStock);
            inputVariationSKU();
        }
        completeUpdateProduct();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [UpdateVariationProduct] DONE!!! ");

        return this;
    }

    public void changeVariationStatus(int productID) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [ChangeProductStatus] START... ");

        // update variation product name and description
        // get current product information
        ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productID);

        // update variation status
        for (int modelId : productInfo.getVariationModelList())
            new VariationDetailPage(driver, modelId, productInfo, loginInformation)
                    .changeVariationStatus(List.of("ACTIVE", "INACTIVE").get(nextInt(2)));

        // Logger
        LogManager.getLogger().info("===== STEP =====> [ChangeProductStatus] DONE!!! ");
    }

    public void editVariationTranslation(int productID) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [AddVariationTranslation] START... ");
        // update variation product name and description
        // get current product information
        ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productID);

        productInfo.getVariationModelList().forEach(barcode -> new VariationDetailPage(driver, barcode, productInfo, loginInformation).updateVariationProductNameAndDescription());

        // Logger
        LogManager.getLogger().info("===== STEP =====> [AddVariationTranslation] DONE!!! ");
    }

    /* Edit translation */
    void addTranslation(String language, String languageName, ProductInfoV2 productInfo) {
        // open edit translation popup
        if (storeInfo.getStoreLanguageList().size() > 1) {
            if (!commonAction.getListElement(loc_dlgEditTranslation).isEmpty()) {
                // convert languageCode to languageName
                if (language.equals("en") && (ProductPage.language.equals("vi") || ProductPage.language.equals("VIE")))
                    languageName = "Tiếng Anh";

                // select language for translation
                if (!commonAction.getText(loc_dlgEditTranslation_ddvSelectedLanguage).equals(languageName)) {
                    // open language dropdown
                    commonAction.openDropdownJS(loc_dlgEditTranslation_ddvSelectedLanguage, loc_dlgEditTranslation_ddlLanguages);

                    // select language
                    commonAction.click(By.xpath(dlgEditTranslation_ddvOtherLanguage.formatted(languageName)));
                }
                logger.info("Add translation for '{}' language.", languageName);

                // input translate product name
                name = "[%s] %s%s".formatted(language, productInfo.getInventoryManageType().equals("IMEI_SERIAL_NUMBER") ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
                commonAction.sendKeys(loc_dlgEditTranslation_txtProductName, name);
                logger.info("Input translation for product name: {}", name);


                // input translate product description
                description = "[%s] product description".formatted(language);
                commonAction.sendKeys(loc_dlgEditTranslation_txtProductDescription, description);
                logger.info("Input translation for product description: {}", description);

                // input variation if any
                if (productInfo.isHasModel()) {
                    List<String> variationName = IntStream.range(0, productInfo.getVariationGroupNameMap().get(storeInfo.getDefaultLanguage()).split("\\|").length).mapToObj(i -> "%s_var%s".formatted(language, i + 1)).toList();
                    List<String> variationValue = new ArrayList<>();
                    List<String> variationList = productInfo.getVariationValuesMap().get(storeInfo.getDefaultLanguage());
                    variationList.stream().map(varValue -> varValue.replace(storeInfo.getDefaultLanguage(), language).split("\\|")).forEach(varValueList -> Arrays.stream(varValueList).filter(varValue -> !variationValue.contains(varValue)).forEach(var -> variationValue.add(var.contains("%s_".formatted(language)) ? var : "%s_%s".formatted(language, var))));
                    Collections.sort(variationList);
                    // input variation name
                    IntStream.range(0, variationName.size()).forEachOrdered(varIndex -> commonAction.sendKeys(loc_dlgEditTranslation_txtVariationName, varIndex, variationName.get(varIndex)));
                    // input variation value
                    IntStream.range(0, variationValue.size()).forEachOrdered(varIndex -> commonAction.sendKeys(loc_dlgEditTranslation_txtVariationValue, varIndex, variationValue.get(varIndex)));
                }

                // input SEO
                // input title
                String title = "[%s] Auto - SEO Title - %s".formatted(language, Instant.now().toEpochMilli());
                commonAction.sendKeys(loc_dlgEditTranslation_txtSEOTitle, title);
                logger.info("Input translation for SEO title: {}", title);

                // input description
                String description = "[%s] Auto - SEO Description - %s".formatted(language, Instant.now().toEpochMilli());
                commonAction.sendKeys(loc_dlgEditTranslation_txtSEODescription, description);
                logger.info("Input translation for SEO description: {}", description);

                // input keywords
                String keywords = "[%s] Auto - SEO Keyword - %s".formatted(language, Instant.now().toEpochMilli());
                commonAction.sendKeys(loc_dlgEditTranslation_txtSEOKeywords, keywords);
                logger.info("Input translation for SEO keywords: {}", keywords);

                // input url
                String url = "%s%s".formatted(language, Instant.now().toEpochMilli());
                commonAction.sendKeys(loc_dlgEditTranslation_txtSEOUrl, url);
                logger.info("Input translation for SEO url: {}", url);

                // save changes
                commonAction.clickJS(loc_dlgEditTranslation_btnSave);
                logger.info("Save translation");
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(),
                        "Can not add new translation for '%s' language.".formatted(languageName));
            }
        }
    }

    public void editTranslation(int productID) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [AddProductTranslation] START... ");

        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath(productID)));
        logger.info("Navigate to product detail page, productId: {}", productID);

        // get online store language
        List<String> langCodeList = new ArrayList<>(storeInfo.getStoreLanguageList());
        List<String> langNameList = new ArrayList<>(storeInfo.getStoreLanguageName());
        langCodeList.remove(storeInfo.getDefaultLanguage());
        logger.info("List languages are not translated: {}", langCodeList.toString());

        // get product information
        productInfo = new APIProductDetailV2(loginInformation).getInfo(productID);

        // add translation
        // open edit translation popup
        commonAction.clickJS(loc_lblEditTranslation);
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgEditTranslation).isEmpty(),
                "Can not open edit translation popup.");

        for (String langCode : langCodeList) {
            addTranslation(langCode, langNameList.get(storeInfo.getStoreLanguageList().indexOf(langCode)), productInfo);
        }

        // save edit translation
        completeUpdateProduct();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [AddProductTranslation] DONE!!! ");
    }

    public void addVariationAttribution() {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [AddVariationAttribution] START... ");

        // get current product information
        ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);

        // update variation status
        for (int modelId : productInfo.getVariationModelList())
            new VariationDetailPage(driver, modelId, productInfo, loginInformation).updateAttribution();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [AddVariationAttribution] DONE!!! ");
    }

    public void navigateToProductAndDeleteAllVariation(int productId) {
        commonAction.navigateToURL(DOMAIN + updateProductPath(productId));
        new HomePage(driver).waitTillSpinnerDisappear();
        for (WebElement el : commonAction.getElements(loc_btnDeleteVariation)) {
            commonAction.clickElement(el);
        }
        completeUpdateProduct();
    }

    public void navigateToProductDetailById(int productId) {
        if (!driver.getCurrentUrl().contains(updateProductPath(productId))) {
            commonAction.navigateToURL(DOMAIN + updateProductPath(productId));
            logger.info("Navigate to product detail by URL, productId: {}", productId);
        }
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

        commonAction.click(dlgCommons_btnCancel);

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
            int size = commonAction.getElements(new ByChained(imeiLocator, By.xpath("./div"))).size();
            logger.debug("========= Size: {}", size);
            if (size > 0) break;
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

    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-13814
    AllPermissions permissions;
    CheckPermission checkPermission;

    public void checkProductManagementPermission(AllPermissions permissions, int productId) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // get productId
        ProductPage.productId = productId;

        // check view product detail
        checkViewProductDetail();
    }

    void checkViewProductDetail() {
        // check view product detail permission
        if (permissions.getProduct().getProductManagement().isViewProductDetail()) {
            // get product information
            productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);

            // navigate to product detail page
            navigateToProductDetailById(productId);

            // check edit product and related permission
            List<Integer> productCollectionIds = new APIProductCollection(loginInformation).getProductListCollectionIds(productId);
            checkEditProduct(productCollectionIds);

            // check view cost price
            checkViewCostPrice();
        } else
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/product/edit/%s".formatted(DOMAIN, productId)), "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        logger.info("Check permission: Product >> Product management >> View product detail.");
    }

    public void checkViewCollectionList(List<Integer> productCollectionIds, AllPermissions... staffPermission) {
        AllPermissions permission = staffPermission.length == 0 ? this.permissions : staffPermission[0];
        // get current url
        String currentURL = driver.getCurrentUrl();

        // check collection permission
        if (permission.getProduct().getCollection().isViewCollectionList() && !productCollectionIds.isEmpty()) {
            assertCustomize.assertTrue(!commonAction.getListElement(loc_cntNoCollection).isEmpty(), "Can not find any product collection.");
        }
        logger.info("Check permission: Product >> Collection >> View collection list.");

        // back to previous page
        driver.get(currentURL);
    }

    public void checkCreateCollection(List<Integer> productCollectionIds, AllPermissions... staffPermission) {
        AllPermissions permission = staffPermission.length == 0 ? this.permissions : staffPermission[0];
        // get current url
        String currentURL = driver.getCurrentUrl();

        // check create collection permission
        if (!permission.getProduct().getCollection().isViewCollectionList() && productCollectionIds.isEmpty()) {
            // open confirm popup
            commonAction.click(loc_lnkCreateCollection);

            // check permission
            if (permission.getProduct().getCollection().isCreateCollection()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirm_btnNo, "/collection/create/product/PRODUCT"),
                        "Can not navigate to create product collection page.");
            } else {
                // Show restricted popup
                // when click on [Create product collection] button in Collection management page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgConfirm_btnNo), "No restricted popup is shown.");
            }
        }

        logger.info("Check permission: Product >> Collection >> Create collection.");

        // back to previous page
        driver.get(currentURL);
    }

    void checkEditProduct(List<Integer> productCollectionIds) {
        if (permissions.getProduct().getProductManagement().isEditProduct()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave, loc_dlgSuccessNotification), "Can not update product.");

            // close Notification
            commonAction.click(loc_dlgNotification_btnClose);

            // check delete product
            checkDeleteProduct();

            // check add variation
            checkAddVariation();

            // check remove variation
            checkDeleteVariation();

            // check activate
            checkActiveProduct();

            // check deactivate
            checkDeactivateProduct();

            // check update wholesale price
            checkUpdateWholesalePrice();

            // check edit tax
            checkEditTax();

            // check update stock permission
            checkUpdateStock();

            // check view collection list
            checkViewCollectionList(productCollectionIds);

            // check create collection
            checkCreateCollection(productCollectionIds);

            // check edit price
            checkEditPrice();

            // check enable product lot
            checkEnableProductLot();

            // check edit SEO
            checkUpdateSEOData();

            // check update translation
            checkUpdateTranslation();
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        }
        logger.info("Check permission: Product >> Product management >> Edit product.");
    }

    void checkUpdateStock() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getInventory().isUpdateStock()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productInfo.isHasModel() ? loc_tblVariation_txtStock : loc_txtWithoutVariationBranchStock, 0), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update stock.");
    }

    void checkEditPrice() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isEditPrice()) {
            if (productInfo.isHasModel()) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_txtVariationListingPrice, 0), "Restricted popup is not shown.");
            } else {
                commonAction.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(MAX_PRICE));
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Product management >> Edit price.");
    }

    void checkEnableProductLot() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!(permissions.getProduct().getLotDate().isEnableProductLot()
              || commonAction.getListElement(loc_chkManageStockByLotDate).isEmpty()
              || commonAction.isCheckedJS(loc_chkManageStockByLotDate))) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_chkManageStockByLotDate), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Enable product.");
    }

    void checkDeleteProduct() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (permissions.getProduct().getProductManagement().isDeleteProduct()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnDelete, loc_dlgConfirm), "Confirm delete product popup is not shown.");
            commonAction.click(loc_dlgConfirm_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDelete), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Delete product.");
    }

    void checkAddVariation() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isAddVariation()) {
            // add new variation group
            if (!commonAction.getListElement(loc_btnAddVariation).isEmpty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnAddVariation), "Restricted popup is not shown.");
            }

            // add variation value
            if (!commonAction.getListElement(loc_txtVariationValue).isEmpty()) {
                commonAction.getElement(loc_txtVariationValue, 0).sendKeys(String.valueOf(Instant.now().toEpochMilli()));
                commonAction.getElement(loc_txtVariationValue, 0).sendKeys(Keys.ENTER);
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lblVariations), "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Product management >> Add variation.");
    }

    void checkDeleteVariation() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isDeleteVariation()) {
            if (!commonAction.getListElement(loc_btnDeleteVariation).isEmpty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeleteVariation), "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Product management >> Delete variation.");
    }

    void checkActiveProduct() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // get current productInfo
        ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isActivateProduct() && productInfo.getBhStatus().equals("INACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup is not shown.");
            if (productInfo.isHasModel()) {
                new VariationDetailPage(driver, productInfo.getVariationModelList().get(0), productInfo, loginInformation).checkActiveVariation(permissions, checkPermission);
            }
        }
        logger.info("Check permission: Product >> Product management >> Activate product.");
    }

    void checkDeactivateProduct() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // get current product info
        ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isDeactivateProduct() && productInfo.getBhStatus().equals("ACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup is not shown.");
            if (productInfo.isHasModel()) {
                new VariationDetailPage(driver, productInfo.getVariationModelList().get(0), productInfo, loginInformation).checkDeactivateVariation(permissions, checkPermission);
            }
        }

        logger.info("Check permission: Product >> Product management >> Deactivate product.");
    }

    void checkUpdateWholesalePrice() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isUpdateWholesalePrice()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_chkAddWholesalePricing), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update wholesale price.");
    }

    void checkEditTax() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isEditTax() && permissions.getSetting().getTAX().isViewTAXList()) {
            selectVAT();
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Edit Tax.");
    }

    void checkViewCostPrice() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
            assertCustomize.assertTrue((productInfo.isHasModel() ? commonAction.getValue(loc_txtVariationCostPrice, 0) : commonAction.getValue(loc_txtWithoutVariationCostPrice)).equals("0"), "Product cost price still shows when staff does not have 'View product cost price' permission.");
        }
        logger.info("Check permission: Product >> Product management >> View cost price.");
    }

    void checkUpdateSEOData() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isEditSEOData()) {
            inputSEO();
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update SEO data.");
    }

    void checkUpdateTranslation() {
        // navigate to product detail page
        navigateToProductDetailById(productId);

        // Get product information
        ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);

        // check permission
        if (!permissions.getProduct().getProductManagement().isEditTranslation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lblEditTranslation), "Restricted popup is not shown.");
            if (productInfo.isHasModel()) {
                new VariationDetailPage(driver, productInfo.getVariationModelList().get(0), productInfo, loginInformation).checkEditTranslation(permissions, checkPermission);
            }
        }
        logger.info("Check permission: Product >> Product management >> Update translation.");
    }
    public ProductPage navigateProductDetail(long productId){
        String url = DOMAIN + "/product/edit/"+ productId;
        commonAction.navigateToURL(url);
        return this;
    }
    public SyncLazadaPage clickLazadaIcon(){
        commonAction.click(loc_icnLazada);
        if(!commonAction.getElements(new ConfirmationDialog(driver).loc_btnOK_V2,1).isEmpty())
        {
            new ConfirmationDialog(driver).clickOKBtn_V2();
        }
        return new SyncLazadaPage(driver);
    }

    public ShopeeSyncPage selectShopeeToSync() {
        commonAction.click(loc_icnShopee);
        new ConfirmationDialog(driver).clickOKBtn();
        return new ShopeeSyncPage(driver);
    }
    public ProductPage clickSelectAllVariation(){
        // select all variation
        if (!commonAction.isCheckedJS(loc_tblVariation_chkSelectAll))
            commonAction.clickJS(loc_tblVariation_chkSelectAll);

        logger.info("Click select all variation");
        return this;
    }
    public ProductPage clickUpdateStockOnWarehousing(){
        commonAction.click(loc_lblUpdateStock);
        logger.info("Click on Update stock label on Warehousing section.");
        return this;
    }
    public ProductPage clickSaveBtn(){
        commonAction.click(loc_btnSave);
        logger.info("Click save button.");
        new HomePage(driver).waitTillSpinnerDisappear1();
        return this;
    }
    public ProductPage verifySuccessPopupShow(){
        Assert.assertFalse(commonAction.getListElement(loc_dlgSuccessNotification, 30000).isEmpty(), "Success popup not show.");
        return this;
    }
    public ProductPage performUpdateStockOnModal(boolean isChange){

        // switch to change stock tab
        if(isChange) commonAction.click(loc_dlgUpdateStock_tabChange);

        // input stock quantity to visible stock input field
        commonAction.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(DataGenerator.generatNumberInBound(10,20)));

        commonAction.click(loc_dlgCommons_btnUpdate);
        return this;
    }
    public ProductPage updateStock(boolean hasVariation, boolean isChangeStock){
        if(hasVariation) {
            clickSelectAllVariation();
            // open list action dropdown
            commonAction.clickJS(loc_tblVariation_lnkSelectAction);

            // open Update stock popup
            commonAction.click(loc_tblVariation_ddvActions,1);
        }
        else clickUpdateStockOnWarehousing();
        performUpdateStockOnModal(isChangeStock);
        clickSaveBtn();
        verifySuccessPopupShow();
        return this;
    }
}
