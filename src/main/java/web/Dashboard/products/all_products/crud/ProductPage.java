package web.Dashboard.products.all_products.crud;

import api.Seller.products.APIAllProducts;
import api.Seller.products.ProductCollection;
import api.Seller.products.ProductInformation;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import api.Seller.setting.VAT;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.all_products.crud.conversion_unit.ConversionUnitPage;
import web.Dashboard.products.all_products.crud.variation_detail.VariationDetailPage;
import web.Dashboard.products.all_products.crud.wholesale_price.WholesaleProductPage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.StringUtils.trim;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;
import static utilities.utils.PropertiesUtil.getPropertiesValueByDBLang;

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
        logger.info("Input '" + seoTitle + "' into SEO Title field.");
    }

    public String getSEOTitle() {
        String title = commonAction.getElementAttribute(commonAction.getElement(loc_txtSEOTitle), "value");
        logger.info("Retrieved SEO Title: %s".formatted(title));
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
            commonAction.clickJS(loc_ddvSelectedLanguage);

            // select language
            commonAction.clickJS(By.xpath(loc_ddvLanguageValue.formatted(language)));

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
        if (commonAction.isCheckedJS(loc_chkAddConversionUnit)) {
            commonAction.clickJS(loc_chkAddConversionUnit);
        }
        logger.info("Remove old conversion unit config.");

        // delete old wholesale product config if any
        if (commonAction.isCheckedJS(loc_chkAddWholesalePricing)) {
            // uncheck add wholesale pricing checkbox to delete old wholesale config
            commonAction.openPopupJS(loc_chkAddWholesalePricing, loc_dlgConfirm);

            // confirm delete old wholesale config
            commonAction.closePopup(loc_dlgConfirm_btnOK);
        }
        logger.info("Remove old wholesale pricing config.");

        // save changes
        commonAction.openPopupJS(loc_btnSave, loc_dlgNotification);

        // if update product failed, try again
        if (!commonAction.getListElement(loc_dlgUpdateFailed).isEmpty()) {
            // close update product failed popup
            commonAction.closePopup(loc_dlgNotification_btnClose);

            // save changes
            commonAction.openPopupJS(loc_btnSave, loc_dlgNotification);
        }

        // if that still failed, end test.
        Assert.assertTrue(commonAction.getListElement(loc_dlgUpdateFailed).isEmpty(), "[Failed][Update product] Can not remove old conversion unit/wholesale pricing config.");

        // close notification popup
        commonAction.closePopup(loc_dlgNotification_btnClose);
        logger.info("Close notification popup.");

        // hide Facebook bubble
        commonAction.removeFbBubble();

        // check [UI] update product page
        checkUIUpdateProductInfo();

        return this;
    }

    void inputProductName(String name) {
        // input product name
        commonAction.sendKeys(loc_txtProductName, name);
        logger.info("Input product name: %s.".formatted(name));
    }

    void inputProductDescription() {
        // input product description
        description = "[%s] product descriptions".formatted(storeInfo.getDefaultLanguage());
        commonAction.sendKeys(loc_txaProductDescription, description);
        logger.info("Input product description: %s.".formatted(description));
    }

    void uploadProductImage(String... imageFile) {
        // upload product image
        for (String imgFile : imageFile) {
            Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"), "/src/main/resources/uploadfile/product_images/%s".formatted(imgFile).replace("/", File.separator)));
            commonAction.uploads(imgUploads, filePath.toString());
            logger.info("[Upload image popup] Upload images, file path: %s.".formatted(filePath));
        }
    }

    void selectVAT() {
        // open VAT dropdown
        commonAction.click(loc_ddvSelectedVAT);
        logger.info("Open VAT dropdown.");

        // get VAT name
        List<String> vatList = new VAT(loginInformation).getInfo().getTaxName();
        if (vatList.size() > 1) {
            if ((productInfo != null)) vatList.remove(productInfo.getTaxName());
            String vatName = vatList.get(nextInt(vatList.size()));

            // select VAT
            commonAction.click(vatName.equals("tax.value.include") ? loc_ddvNoVAT : By.xpath(loc_ddvOthersVAT.formatted(vatName)));

            // log
            logger.info("Select VAT: %s.".formatted(vatName));
        }
    }

    void selectCollection() {
        // click on collection search box
        List<String> collectionsNameList = new ProductCollection(loginInformation).getListOfManualProductCollectionsName();
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
            String collectionName = collectionsNameList.get(nextInt(collectionsNameList.size()));

            // select collection
            commonAction.click(By.xpath(loc_ddvCollectionValue.formatted(collectionName)));

            // log
            logger.info("Select collection: %s".formatted(collectionName));
        }
    }

    void inputWithoutVariationProductSKU() {
        String sku = "SKU" + Instant.now().toEpochMilli();
        commonAction.sendKeys(loc_txtWithoutVariationSKU, sku);
        logger.info("Input SKU: %s.".formatted(sku));
    }

    void updateWithoutVariationProductSKU() throws Exception {
        // open update SKU popup
        commonAction.click(loc_txtWithoutVariationSKU);
        logger.info("Open update SKU popup.");

        // wait Update SKU popup visible
        commonAction.getElement(loc_dlgUpdateSKU);
        logger.info("Wait update SKU popup visible.");

        // check [UI] SKU popup
        checkUpdateSKUPopup();

        // input SKU for each branch
        for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
            String sku = "SKU_%s_%s".formatted(brInfo.getActiveBranches().get(brIndex), epoch);
            commonAction.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
            logger.info("Input SKU: %s.".formatted(sku));
        }

        // click around
        commonAction.click(loc_ttlUpdateSKU);

        // close Update SKU popup
        commonAction.click(loc_dlgCommons_btnUpdate);
    }

    void setManageInventory(boolean isIMEIProduct) throws Exception {
        manageByIMEI = isIMEIProduct;
        // set manage inventory by product or IMEI/Serial number
        if (!driver.getCurrentUrl().contains("/edit/"))
            new Select(commonAction.getElement(loc_ddlManageInventory)).selectByValue(isIMEIProduct ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

        // check [UI] after select manage inventory by IMEI/Serial Number
        if (isIMEIProduct) checkManageInventoryByIMEINotice();

        // log
        logger.info("Manage inventory by: %s".formatted(isIMEIProduct ? "IMEI/Serial Number" : "Product"));
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
        logger.info("Input priority: %s.".formatted(priority));
    }

    void setProductDimension() {
        String dimension = (hasDimension) ? "10" : "0";
        // input product weight
        commonAction.sendKeys(loc_txtWeight, dimension);
        logger.info("Input weight: %s.".formatted(dimension));

        // input product length
        commonAction.sendKeys(loc_txtLength, dimension);
        logger.info("Input length: %s.".formatted(dimension));

        // input product width
        commonAction.sendKeys(loc_txtWidth, dimension);
        logger.info("Input width: %s.".formatted(dimension));

        // input product height
        commonAction.sendKeys(loc_txtHeight, dimension);
        logger.info("Input height: %s.".formatted(dimension));

    }

    void selectPlatform() {
        // App
        if (commonAction.getElement(loc_chkApp).isSelected() != showOnApp)
            commonAction.clickJS(loc_chkApp);

        // Web
        if (commonAction.getElement(loc_chkWeb).isSelected() != showOnWeb)
            commonAction.clickJS(loc_chkWeb);

        // In-store
        if (commonAction.getElement(loc_chkInStore).isSelected() != uiIsShowInStore)
            commonAction.clickJS(loc_chkInStore);

        // GoSocial
        if (commonAction.getElement(loc_chkGoSocial).isSelected() != showInGoSocial)
            commonAction.clickJS(loc_chkGoSocial);

    }

    void inputSEO() {
        // SEO title
        String title = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(loc_txtSEOTitle, title);

        // SEO description
        String description = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(loc_txtSEODescription, description);

        // SEO keyword
        String keyword = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(loc_txtSEOKeywords, keyword);

        // SEO URL
        String url = "%s%s".formatted(storeInfo.getDefaultLanguage(), epoch);
        commonAction.sendKeys(loc_txtSEOUrl, url);
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
        commonAction.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(productListingPrice.get(0)));
        logger.info("Listing price: %s".formatted(String.format("%,d", productListingPrice.get(0))));

        // input selling price
        commonAction.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(productSellingPrice.get(0)));
        logger.info("Selling price: %s".formatted(String.format("%,d", productSellingPrice.get(0))));

        // input cost price
        long costPrice = nextLong(productSellingPrice.get(0));
        commonAction.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(costPrice));
        logger.info("Cost price: %s.".formatted(String.format("%,d", costPrice)));

    }

    void addIMEIForEachBranch(String variationValue, List<Integer> branchStock, int varIndex) throws Exception {
        // select all branches
        commonAction.openDropdownJS(loc_dlgAddIMEISelectedBranch, loc_dlgAddIMEI_chkSelectAllBranches);
        logger.info("[Add IMEI popup] Open all branches dropdown.");

        if (!commonAction.isCheckedJS(loc_dlgAddIMEI_chkSelectAllBranches))
            commonAction.clickJS(loc_dlgAddIMEI_chkSelectAllBranches);
        else commonAction.closeDropdown(loc_dlgAddIMEISelectedBranch, loc_dlgAddIMEI_chkSelectAllBranches);
        logger.info("[Add IMEI popup] Select all branches.");

        // check [UI] add IMEI popup
        if (varIndex == 0) checkAddIMEIPopup();

        // input IMEI/Serial number for each branch
        for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
            String brName = brInfo.getActiveBranches().get(brIndex);
            int brStockIndex = brInfo.getBranchName().indexOf(brInfo.getActiveBranches().get(brIndex));
            for (int i = 0; i < branchStock.get(brStockIndex); i++) {
                String imei = "%s%s_IMEI_%s_%s\n".formatted(variationValue != null ? "%s_".formatted(variationValue) : "", brInfo.getActiveBranches().get(brIndex), epoch, i);
                commonAction.sendKeys(loc_dlgAddIMEI_txtAddIMEI, brIndex, imei);
                logger.info("Input IMEI: %s.".formatted(imei));
            }
            logger.info("%s[%s] Add IMEI, stock: %s.".formatted(variationValue == null ? "" : "[%s]".formatted(variationValue), brName, branchStock.get(brStockIndex)));
        }

        // save IMEI/Serial number
        commonAction.click(loc_dlgAddIMEI_btnSave);
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
            commonAction.openPopupJS(loc_txtWithoutVariationBranchStock, 0, loc_dlgAddIMEI);
            logger.info("Open Add IMEI popup.");

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch(null, productStockQuantity.get(null), 0);
            logger.info("Complete add stock for IMEI product.");

        } else {
            // update stock for normal product
            IntStream.range(0, brInfo.getActiveBranches().size()).forEach(brIndex -> {
                commonAction.sendKeys(loc_txtWithoutVariationBranchStock, brIndex, String.valueOf(productStockQuantity.get(null).get(brIndex)));
                logger.info("[%s] Input stock: %s.".formatted(brInfo.getActiveBranches().get(brIndex), productStockQuantity.get(null).get(brIndex)));
            });
            logger.info("Complete update stock for Normal product.");
        }

    }

    void addNormalStockForEachBranch(List<Integer> branchStock, int varIndex) throws Exception {
        // select all branches
        commonAction.openDropdownJS(loc_dlgUpdateStock_ddvSelectedBranch, loc_dlgUpdateStock_chkSelectAllBranches);
        logger.info("[Update stock popup] Open all branches dropdown.");

        // select all branches
        if (!commonAction.isCheckedJS(loc_dlgUpdateStock_chkSelectAllBranches))
            commonAction.clickJS(loc_dlgUpdateStock_chkSelectAllBranches);
        else commonAction.closeDropdown(loc_dlgUpdateStock_ddvSelectedBranch, loc_dlgUpdateStock_chkSelectAllBranches);
        logger.info("[Update stock popup] Select all branches.");

        // check [UI] update stock popup
        if (varIndex == 0) checkUpdateStockPopup();

        // switch to change stock tab
        commonAction.click(loc_dlgUpdateStock_tabChange);

        // input stock quantity to visible stock input field
        int stock = Collections.max(branchStock) + 1;
        commonAction.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));

        // input stock for each branch
        for (int brIndex = brInfo.getActiveBranches().size() - 1; brIndex >= 0; brIndex--) {
            String brName = brInfo.getActiveBranches().get(brIndex);
            int brStockIndex = brInfo.getBranchName().indexOf(brName);
            commonAction.sendKeys(loc_dlgUpdateStock_txtBranchStock, brIndex, String.valueOf(branchStock.get(brStockIndex)));
            logger.info("%s[%s] Input stock: %s.".formatted(variationList.get(varIndex) == null ? "" : "[%s]".formatted(variationList.get(varIndex)), brName, branchStock.get(brStockIndex)));
        }
        // close Update stock popup
        commonAction.click(loc_dlgCommons_btnUpdate);
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
            commonAction.openDropdownJS(loc_txtWithoutVariationBranchStock, 0, loc_dlgAddIMEI);
            logger.info("Open Add IMEI popup.");

            // add IMEI/Serial number for each branch
            addIMEIForEachBranch(null, productStockQuantity.get(null), 0);
            logger.info("Complete add stock for IMEI product.");
        } else {
            // open Update stock popup
            commonAction.openDropdownJS(loc_txtWithoutVariationBranchStock, 0, loc_dlgUpdateStock);
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
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(varIndex -> commonAction.clickJS(loc_btnAddVariation));
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
            commonAction.sendKeys(loc_txtVariationName, varIndex, varName);
            WebElement valueElement = commonAction.getElement(loc_txtVariationValue, varIndex);

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

        commonAction.click(loc_lblVariations);

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
        if (!commonAction.isCheckedJS(loc_tblVariation_chkSelectAll))
            commonAction.clickJS(loc_tblVariation_chkSelectAll);

        // check [UI] after select all variations
        checkBulkActionsOnVariationTable();

        // open list action dropdown
        commonAction.clickJS(loc_tblVariation_lnkSelectAction);

        // check [UI] check list actions
        checkListActionsOnVariationTable();

        // open Update price popup
        commonAction.openPopupJS(loc_tblVariation_ddvActions, 0, loc_dlgCommons);

        // check [UI] product price table
        checkUpdatePricePopup();

        // input product price
        IntStream.range(0, variationList.size()).forEachOrdered(varIndex -> {
            // get current variation
            String variation = variationList.get(varIndex);

            // input listing price
            long listingPrice = productListingPrice.get(varIndex);
            commonAction.sendKeys(loc_dlgUpdatePrice_txtListingPrice, varIndex, String.valueOf(String.format("%,d", listingPrice)));
            logger.info("[%s] Listing price: %s.".formatted(variation, String.format("%,d", listingPrice)));

            // input selling price
            long sellingPrice = productSellingPrice.get(varIndex);
            commonAction.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, varIndex, String.valueOf(sellingPrice));
            logger.info("[%s] Selling price: %s.".formatted(variation, String.format("%,d", sellingPrice)));

            // input costPrice
            long costPrice = nextLong(sellingPrice);
            commonAction.sendKeys(loc_dlgUpdatePrice_txtCostPrice, varIndex, String.valueOf(costPrice));
            logger.info("[%s] Cost price: %s.".formatted(variation, String.format("%,d", costPrice)));
        });


        // click around
        commonAction.click(loc_ttlUpdatePrice);

        // close Update price popup
        commonAction.closePopup(loc_dlgCommons_btnUpdate);
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
            commonAction.clickJS(loc_tblVariation_txtStock, varIndex);

            if (manageByIMEI) {
                addIMEIForEachBranch(variationList.get(varIndex), productStockQuantity.get(variationList.get(varIndex)), varIndex);
            } else addNormalStockForEachBranch(productStockQuantity.get(variationList.get(varIndex)), varIndex);
        }
    }

    void inputVariationSKU() throws Exception {
        // input SKU
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update SKU popup
            commonAction.openPopupJS(loc_tblVariation_txtSKU, varIndex, loc_dlgUpdateSKU);

            // check [UI] SKU popup
            if (varIndex == 0) checkUpdateSKUPopup();

            // input SKU for each branch
            for (int brIndex = 0; brIndex < brInfo.getActiveBranches().size(); brIndex++) {
                String sku = "SKU_%s_%s_%s".formatted(variationList.get(varIndex), brInfo.getActiveBranches().get(brIndex), epoch);
                commonAction.sendKeys(loc_dlgUpdateSKU_txtInputSKU, brIndex, sku);
                logger.info("[Update SKU popup] Input SKU: %s.".formatted(sku));
            }

            // click around
            commonAction.click(loc_ttlUpdateSKU);

            // close Update SKU popup
            commonAction.closePopup(loc_dlgCommons_btnUpdate);
        }
    }

    void uploadVariationImage(String... imageFile) throws Exception {
        // upload image for each variation
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            // open Update SKU popup
            commonAction.openPopupJS(loc_tblVariation_imgUploads, varIndex, loc_dlgCommons);
            logger.info("Open upload variation image popup.");

            // check [UI] update image popup
            if (varIndex == 0) checkUpdateVariationImagePopup();

            // upload image
            for (String imgFile : imageFile) {
                Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"),
                        "/src/main/resources/uploadfile/product_images/%s".formatted(imgFile).replace("/", File.separator)));
                commonAction.uploads(loc_dlgUploadsImage_btnUploads, filePath.toString());
                logger.info("[Upload image popup] Upload images, file path: %s.".formatted(filePath));
            }

            // close Update image popup
            commonAction.closePopup(loc_dlgCommons_btnUpdate);
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
            commonAction.getElement(loc_lblSEOSetting);

            // change status
            commonAction.clickJS(loc_btnDeactivate);

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
            commonAction.getElement(loc_lblSEOSetting);

            // open Confirm delete popup
            commonAction.openPopupJS(loc_btnDelete, loc_dlgCommons);

            // check UI
            checkUIConfirmDeleteProductPopup();

            // close confirm delete product popup
            commonAction.closePopup(loc_dlgConfirmDelete_btnOK);
        }
    }


    /* Complete create/update product */
    void completeCreateProduct() {
        // save changes
        commonAction.openPopupJS(loc_btnSave, loc_dlgNotification);

        // close notification popup
        commonAction.closePopup(loc_dlgNotification_btnClose);

        // wait api return list product
        productID = new APIAllProducts(loginInformation).searchProductIdByName(name);

        // log
        logger.info("Product id: %s".formatted(productID));
    }

    void completeUpdateProduct() {
        // save changes
        commonAction.openPopupJS(loc_btnSave, loc_dlgNotification);

        // if update product failed, try again
        if (!commonAction.getListElement(loc_dlgUpdateFailed).isEmpty()) {
            // close update product failed popup
            commonAction.closePopup(loc_dlgNotification_btnClose);

            // save changes again
            commonAction.openPopupJS(loc_btnSave, loc_dlgNotification);
        }

        // if that still failed, end test.
        Assert.assertTrue(commonAction.getListElement(loc_dlgUpdateFailed).isEmpty(), "[Failed][Update product] Can not update product.");

        // close notification popup
        commonAction.closePopup(loc_dlgNotification_btnClose);
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
            commonAction.openPopupJS(loc_lblEditTranslation, loc_dlgEditTranslation);
            logger.info("Open translation popup.");

            // convert languageCode to languageName
            if (language.equals("en") && (ProductPage.language.equals("vi") || ProductPage.language.equals("VIE")))
                languageName = "Tiếng Anh";

            // select language for translation
            if (!commonAction.getText(loc_dlgEditTranslation_ddvSelectedLanguage).equals(languageName)) {
                // open language dropdown
                commonAction.openPopupJS(loc_dlgEditTranslation_ddvSelectedLanguage, By.cssSelector(".product-translate .uik-select__optionList"));

                // select language
                commonAction.click(By.xpath(dlgEditTranslation_ddvOtherLanguage.formatted(languageName)));
            }
            logger.info("Add translation for '%s' language.".formatted(languageName));

            // check UI
            if (langIndex == 0) checkEditTranslationPopup();

            // input translate product name
            name = "[%s]%s%s".formatted(language, productInfo.isManageInventoryByIMEI() ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
            commonAction.sendKeys(loc_dlgEditTranslation_txtProductName, name);
            logger.info("Input translation for product name: %s.".formatted(name));


            // input translate product description
            description = "[%s] product description".formatted(language);
            commonAction.sendKeys(loc_dlgEditTranslation_txtProductDescription, description);
            logger.info("Input translation for product description: %s.".formatted(description));

            // input variation if any
            if (productInfo.isHasModel()) {
                List<String> variationName = IntStream.range(0, productInfo.getVariationNameMap().get(storeInfo.getDefaultLanguage()).split("\\|").length).mapToObj(i -> "%s_var%s".formatted(language, i + 1)).toList();
                List<String> variationValue = new ArrayList<>();
                List<String> variationList = productInfo.getVariationListMap().get(storeInfo.getDefaultLanguage());
                variationList.stream().map(varValue -> varValue.replace(storeInfo.getDefaultLanguage(), language).split("\\|")).forEach(varValueList -> Arrays.stream(varValueList).filter(varValue -> !variationValue.contains(varValue)).forEach(var -> variationValue.add(var.contains("%s_".formatted(language)) ? var : "%s_%s".formatted(language, var))));
                Collections.sort(variationList);
                // input variation name
                IntStream.range(0, variationName.size()).forEachOrdered(varIndex -> commonAction.sendKeys(loc_dlgEditTranslation_txtVariationName, varIndex, variationName.get(varIndex)));
                // input variation value
                IntStream.range(0, variationValue.size()).forEachOrdered(varIndex -> commonAction.sendKeys(loc_dlgEditTranslation_txtVariationValue, varIndex, variationValue.get(varIndex)));
            }

            // input SEO
            // input title
            String title = "[%s] Auto - SEO Title - %s".formatted(language, epoch);
            commonAction.sendKeys(loc_dlgEditTranslation_txtSEOTitle, title);
            logger.info("Input translation for SEO title: %s.".formatted(title));

            // input description
            String description = "[%s] Auto - SEO Description - %s".formatted(language, epoch);
            commonAction.sendKeys(loc_dlgEditTranslation_txtSEODescription, description);
            logger.info("Input translation for SEO description: %s.".formatted(description));

            // input keywords
            String keywords = "[%s] Auto - SEO Keyword - %s".formatted(language, epoch);
            commonAction.sendKeys(loc_dlgEditTranslation_txtSEOKeywords, keywords);
            logger.info("Input translation for SEO keywords: %s.".formatted(keywords));

            // input url
            String url = "%s%s".formatted(language, epoch);
            commonAction.sendKeys(loc_dlgEditTranslation_txtSEOUrl, url);
            logger.info("Input translation for SEO url: %s.".formatted(url));

            // save changes
            commonAction.openPopupJS(loc_dlgEditTranslation_btnSave, loc_dlgToast);
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
        commonAction.openPopupJS(loc_btnSave, loc_dlgNotification);
        logger.info("Complete translation.");

        // close Notification popup
        commonAction.closePopup(loc_dlgNotification_btnClose);
        logger.info("Close Notification popup.");
    }

    public void uncheckWebPlatform() {
        showOnWeb = false;

        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, updateProductPath.formatted(productInfo.getProductID())));

        // wait page loaded
        commonAction.getElement(loc_lblSEOSetting);

        logger.info("Navigate to product page and edit translation.");

        selectPlatform();

        completeUpdateProduct();
    }

    /* check UI function */
    void checkUICRHeaderProductPage() throws Exception {
        // check Go back to product list link text
        String dbGoBackToProductList = commonAction.getText(loc_lnkGoBackToProductList);
        String ppGoBackToProductList = getPropertiesValueByDBLang("products.allProducts.createProduct.header.goBackToProductList", language);
        assertCustomize.assertEquals(dbGoBackToProductList, ppGoBackToProductList, "[Failed][Header] Go back to product list link text should be %s, but found %s.".formatted(ppGoBackToProductList, dbGoBackToProductList));
        logger.info("[UI][%s] Check Header - Go back to product list.".formatted(language));

        // check header page title
        String dbHeaderPageTitle = commonAction.getText(loc_ttlPage);
        String ppHeaderPageTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.header.headerPageTitle", language);
        assertCustomize.assertEquals(dbHeaderPageTitle, ppHeaderPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppHeaderPageTitle, dbHeaderPageTitle));
        logger.info("[UI][%s] Check Header - Header page title.".formatted(language));

        // check header Save button
        String dbHeaderSaveBtn = commonAction.getText(loc_lblSave);
        String ppHeaderSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.saveBtn", language);
        assertCustomize.assertEquals(dbHeaderSaveBtn, ppHeaderSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppHeaderSaveBtn, dbHeaderSaveBtn));
        logger.info("[UI][%s] Check Header - Save button .".formatted(language));

        // check header Cancel button
        String dbHeaderCancelBtn = commonAction.getText(loc_lblCancel);
        String ppHeaderCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.cancelBtn", language);
        assertCustomize.assertEquals(dbHeaderCancelBtn, ppHeaderCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppHeaderCancelBtn, dbHeaderCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));
    }

    void checkUIUPHeaderProductPage() throws Exception {
        // check Go back to product list link text
        String dbGoBackToProductList = commonAction.getText(loc_lnkGoBackToProductList);
        String ppGoBackToProductList = getPropertiesValueByDBLang("products.allProducts.createProduct.header.goBackToProductList", language);
        assertCustomize.assertEquals(dbGoBackToProductList, ppGoBackToProductList, "[Failed][Header] Go back to product list link text should be %s, but found %s.".formatted(ppGoBackToProductList, dbGoBackToProductList));
        logger.info("[UI][%s] Check Header - Go back to product list.".formatted(language));

        // check header Edit translation button
        if (storeInfo.getStoreLanguageList().size() > 1) {
            String dbEditTranslationBtn = commonAction.getText(loc_lblEditTranslation);
            String ppEditTranslationBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.header.editTranslation", language);
            assertCustomize.assertEquals(dbEditTranslationBtn, ppEditTranslationBtn, "[Failed][Header] Edit translation button should be %s, but found %s.".formatted(ppEditTranslationBtn, dbEditTranslationBtn));
            logger.info("[UI][%s] Check Header - Edit translation button.".formatted(language));
        }

        // check header Save button
        String dbHeaderSaveBtn = commonAction.getText(loc_lblSave);
        String ppHeaderSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.saveBtn", language);
        assertCustomize.assertEquals(dbHeaderSaveBtn, ppHeaderSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppHeaderSaveBtn, dbHeaderSaveBtn));
        logger.info("[UI][%s] Check Header - Save button .".formatted(language));

        // check header Cancel button
        String dbHeaderCancelBtn = commonAction.getText(loc_lblCancel);
        String ppHeaderCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.header.cancelBtn", language);
        assertCustomize.assertEquals(dbHeaderCancelBtn, ppHeaderCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppHeaderCancelBtn, dbHeaderCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));

        // check header Deactivate button
        String dbDeactivateBtn = commonAction.getText(loc_lblDeactivate);
        String ppDeactivateBtn = getPropertiesValueByDBLang(productInfo.getBhStatus().equals("ACTIVE") ? "products.allProducts.updateProduct.header.deactivateBtn" : "products.allProducts.updateProduct.header.activeBtn", language);
        assertCustomize.assertEquals(dbDeactivateBtn, ppDeactivateBtn, "[Failed][Header] Deactivate button should be %s, but found %s.".formatted(ppDeactivateBtn, dbDeactivateBtn));
        logger.info("[UI][%s] Check Header - Deactivate button.".formatted(language));

        // check header Delete button
        String dbDeleteBtn = commonAction.getText(loc_lblDelete);
        String ppDeleteBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.header.deleteBtn", language);
        assertCustomize.assertEquals(dbDeleteBtn, ppDeleteBtn, "[Failed][Header] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Header - Delete button.".formatted(language));
    }

    void checkUIInformation() throws Exception {
        // check product information
        String dbProductInformation = commonAction.getText(loc_lblProductInformation);
        String ppProductInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.title", language);
        assertCustomize.assertEquals(dbProductInformation, ppProductInformation, "[Failed][Body] Product information should be %s, but found %s.".formatted(ppProductInformation, dbProductInformation));
        logger.info("[UI][%s] Check Body - Product Information.".formatted(language));

        // check product name
        String dbProductName = commonAction.getText(loc_lblProductName);
        String ppProductName = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.productName", language);
        assertCustomize.assertEquals(dbProductName, ppProductName, "[Failed][Body] Product name should be %s, but found %s.".formatted(ppProductName, dbProductName));
        logger.info("[UI][%s] Check Body - Product name.".formatted(language));

        // check product name error
        commonAction.clear(loc_txtProductName);
        commonAction.sendKeys(loc_txtProductName, Keys.TAB);
        String dbProductNameError = commonAction.getText(loc_lblErrorWhenLeaveProductNameBlank);
        String ppProductNameError = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.productNameError", language);
        assertCustomize.assertEquals(dbProductNameError, ppProductNameError, "[Failed][Body] Product name error should be %s, but found %s.".formatted(ppProductNameError, dbProductNameError));
        logger.info("[UI][%s] Check Body - Product name error.".formatted(language));

        // check product description
        String dbProductDescription = commonAction.getText(loc_lblProductDescription);
        String ppProductDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.productInfo.description", language);
        assertCustomize.assertEquals(dbProductDescription, ppProductDescription, "[Failed][Body] Product description should be %s, but found %s.".formatted(ppProductDescription, dbProductDescription));
        logger.info("[UI][%s] Check Body - Product description.".formatted(language));
    }

    void checkUIImages() throws Exception {
        // remove old product image
        List<WebElement> removeImageIcons = commonAction.getListElement(loc_icnRemoveImages);
        if (!removeImageIcons.isEmpty())
            IntStream.iterate(removeImageIcons.size() - 1, iconIndex -> iconIndex >= 0, iconIndex -> iconIndex - 1).forEach(iconIndex -> commonAction.clickJS(loc_icnRemoveImages, iconIndex));

        // check images title
        String dbImages = commonAction.getText(loc_lblImages);
        String ppImages = getPropertiesValueByDBLang("products.allProducts.createProduct.images.title", language);
        assertCustomize.assertEquals(dbImages, ppImages, "[Failed][Body] Images should be %s, but found %s.".formatted(ppImages, dbImages));
        logger.info("[UI][%s] Check Body - Images.".formatted(language));

        // check images drag and drop placeholder
        String dbImagesDragAndDrop = commonAction.getText(loc_lblDragAndDrop);
        String ppImagesDragAndDrop = getPropertiesValueByDBLang("products.allProducts.createProduct.images.dragAndDrop", language);
        assertCustomize.assertTrue(Objects.equals(dbImagesDragAndDrop, ppImagesDragAndDrop), "[Failed][Body] Images drag and drop placeholder should be %s, but found %s.".formatted(ppImagesDragAndDrop, dbImagesDragAndDrop));
        logger.info("[UI][%s] Check Body - Images drag and drop placeholder.".formatted(language));
    }

    void checkUIPricing() throws Exception {
        // check pricing title
        String dbProductPriceTitle = commonAction.getText(loc_lblPricing);
        String ppProductPriceTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.title", language);
        assertCustomize.assertEquals(dbProductPriceTitle, ppProductPriceTitle, "[Failed][Body] Product price title should be %s, but found %s.".formatted(ppProductPriceTitle, dbProductPriceTitle));
        logger.info("[UI][%s] Check Body - Product price title.".formatted(language));

        // check product price for without variation product
        if (!commonAction.getListElement(loc_lblListingPrice).isEmpty()) {
            // check product listing price
            String dbProductListingPrice = commonAction.getText(loc_lblListingPrice);
            String ppProductListingPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.listingPrice", language);
            assertCustomize.assertEquals(dbProductListingPrice, ppProductListingPrice, "[Failed][Body] Product listing price should be %s, but found %s.".formatted(ppProductListingPrice, dbProductListingPrice));
            logger.info("[UI][%s] Check Body - Product listing price.".formatted(language));

            // check product selling price
            String dbProductSellingPrice = commonAction.getText(loc_lblSellingPrice);
            String ppProductSellingPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.sellingPrice", language);
            assertCustomize.assertEquals(dbProductSellingPrice, ppProductSellingPrice, "[Failed][Body] Product selling price should be %s, but found %s.".formatted(ppProductSellingPrice, dbProductSellingPrice));
            logger.info("[UI][%s] Check Body - Product selling price.".formatted(language));

            // check product cost price
            String dbProductCostPrice = commonAction.getText(loc_lblCostPrice);
            String ppProductCostPrice = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.costPrice", language);
            assertCustomize.assertEquals(dbProductCostPrice, ppProductCostPrice, "[Failed][Body] Product cost price should be %s, but found %s.".formatted(ppProductCostPrice, dbProductCostPrice));
            logger.info("[UI][%s] Check Body - Product cost price.".formatted(language));
        }

        // check VAT title
        String dbVATTitle = commonAction.getText(loc_lblVAT);
        String ppVATTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.vat", language);
        assertCustomize.assertEquals(dbVATTitle, ppVATTitle, "[Failed][Body] VAT title should be %s, but found %s.".formatted(ppVATTitle, dbVATTitle));
        logger.info("[UI][%s] Check Body - VAT title.".formatted(language));

        // show as listing product on storefront checkbox
        String dbShowAsListingProduct = commonAction.getText(loc_lblShowAsListingProduct);
        String ppShowAsListingProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.pricing.showAsListingProductCheckbox", language);
        assertCustomize.assertEquals(dbShowAsListingProduct, ppShowAsListingProduct, "[Failed][Body] Show as listing product on storefront label should be %s, but found %s.".formatted(ppShowAsListingProduct, dbShowAsListingProduct));
        logger.info("[UI][%s] Check Body - Show as listing product on storefront checkbox.".formatted(language));
    }

    void checkUIVariations() throws Exception {
        // check variations title
        String dbVariations = commonAction.getText(loc_lblVariations);
        String ppVariations = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.title", language);
        assertCustomize.assertEquals(dbVariations, ppVariations, "[Failed][Body] Variations title should be %s, but found %s.".formatted(ppVariations, dbVariations));
        logger.info("[UI][%s] Check Body - Variations title.".formatted(language));

        // check variation description
        String dbVariationDescription = commonAction.getText(loc_cntVariation);
        String ppVariationDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationDescription", language);
        assertCustomize.assertTrue(dbVariationDescription.equals(ppVariationDescription), "[Failed][Body] Variation description should be %s, but found %s.".formatted(ppVariationDescription, dbVariationDescription));
        logger.info("[UI][%s] Check Body - Variation description.".formatted(language));
    }

    void checkAddVariationBtn() throws Exception {
        // check add variation button
        String dbAddVariationBtn = commonAction.getText(loc_lblAddVariation);
        String ppAddVariationBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addVariationBtn", language);
        assertCustomize.assertEquals(dbAddVariationBtn, ppAddVariationBtn, "[Failed][Body] Add variation button should be %s, but found %s.".formatted(ppAddVariationBtn, dbAddVariationBtn));
        logger.info("[UI][%s] Check Body - Add variation button.".formatted(language));
    }

    void checkVariationInformation() throws Exception {
        //check variation name
        String dbVariationName = commonAction.getText(loc_lblVariationName);
        String ppVariationName = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationName", language);
        assertCustomize.assertEquals(dbVariationName, ppVariationName, "[Failed][Body] Variation name should be %s, but found %s.".formatted(ppVariationName, dbVariationName));
        logger.info("[UI][%s] Check Body - Variation name.".formatted(language));

        // check variation value
        String dbVariationValue = commonAction.getText(loc_lblVariationValue);
        String ppVariationValue = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationValue", language);
        assertCustomize.assertEquals(dbVariationValue, ppVariationValue, "[Failed][Body] Variation value should be %s, but found %s.".formatted(ppVariationValue, dbVariationValue));
        logger.info("[UI][%s] Check Body - Variation value.".formatted(language));
    }

    void checkVariationValuePlaceholder() throws Exception {
        // check variation value placeholder
        String dbVariationValuePlaceholder = commonAction.getText(loc_plhVariationValue, 0);
        String ppVariationValuePlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationValuePlaceholder", language);
        assertCustomize.assertEquals(dbVariationValuePlaceholder, ppVariationValuePlaceholder, "[Failed][Body] Variation value placeholder should be %s, but found %s.".formatted(ppVariationValuePlaceholder, dbVariationValuePlaceholder));
        logger.info("[UI][%s] Check Body - Variation value placeholder.".formatted(language));
    }

    void checkBulkActionsOnVariationTable() throws Exception {
        // check number of selected variation
        String[] dbNumberOfSelectedVariation = commonAction.getText(loc_lblNumberOfSelectedVariations).split("\n")[0].split("\\d+");
        String[] ppNumberOfSelectedVariation = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.numberOfSelectedVariations", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedVariation, ppNumberOfSelectedVariation, "[Failed][Body][Variation] Number of selected variations should be %s, but found %s.".formatted(ppNumberOfSelectedVariation, dbNumberOfSelectedVariation));
        logger.info("[UI][%s] Check Body - Number of selected variations on variation table.".formatted(language));

        // check Select action button
        String dbSelectActionLinkText = commonAction.getText(loc_lnkSelectAction);
        String ppSelectActionLinkText = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.selectAction", language);
        assertCustomize.assertEquals(dbSelectActionLinkText, ppSelectActionLinkText, "[Failed][Body][Variation table] Select action link text should be %s, but found %s.".formatted(ppSelectActionLinkText, dbSelectActionLinkText));
        logger.info("[UI][%s] Check Body - Select action link text on variation table".formatted(language));
    }

    void checkListActionsOnVariationTable() throws Exception {
        // check list actions
        List<String> dbListActions = commonAction.getListElement(loc_lblActionsList).stream().map(WebElement::getText).toList();
        List<String> ppListActions = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.2", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.variationTable.listAction.3", language));
        assertCustomize.assertEquals(dbListActions, ppListActions, "[Failed][Body][Variation table] List action should be %s, but found %s.".formatted(ppListActions, dbListActions));
        logger.info("[UI][%s] Check Body - List action on variation table.".formatted(language));
    }

    void checkVariationTable() throws Exception {
        // check variation table column
        List<String> dbVariationTableImageColumn = commonAction.getListElement(loc_tblVariation_lblColumn).stream().map(WebElement::getText).toList();
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
        if (!commonAction.getListElement(loc_tblVariation_lblEditSKU).isEmpty()) {
            String dbEditSKULinkText = commonAction.getText(loc_tblVariation_lblEditSKU, 0);
            String ppEditSKULinkText = getPropertiesValueByDBLang("products.allProducts.updateProduct.variations.variationTable.editSKU", language);
            assertCustomize.assertEquals(dbEditSKULinkText, ppEditSKULinkText, "[Failed][Variation table] Edit SKU should be %s, but found %s.".formatted(ppEditSKULinkText, dbEditSKULinkText));
            logger.info("[UI][%s] Check Variation table - Edit SKU.".formatted(language));
        }
    }

    void checkEditTranslationPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_ttlEditTranslation);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Edit translation popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Edit translation popup - Title.".formatted(language));

        // check information
        String dbInformation = commonAction.getText(loc_dlgEditTranslation_lblInformation);
        String ppInformation = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.information", language);
        assertCustomize.assertEquals(dbInformation, ppInformation, "[Failed][Edit translation popup] Information should be %s, but found %s.".formatted(ppInformation, dbInformation));
        logger.info("[UI][%s] Check Edit translation popup - Information.".formatted(language));

        // check product name
        String dbProductName = commonAction.getText(loc_dlgEditTranslation_lblProductName);
        String ppProductName = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.productName", language);
        assertCustomize.assertEquals(dbProductName, ppProductName, "[Failed][Edit translation popup] Product name should be %s, but found %s.".formatted(ppProductName, dbProductName));
        logger.info("[UI][%s] Check Edit translation popup - Product name.".formatted(language));

        // check product description
        String dbProductDescription = commonAction.getText(loc_dlgEditTranslation_lblProductDescription);
        String ppProductDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.productDescription", language);
        assertCustomize.assertEquals(dbProductDescription, ppProductDescription, "[Failed][Edit translation popup] Product description should be %s, but found %s.".formatted(ppProductDescription, dbProductDescription));
        logger.info("[UI][%s] Check Edit translation popup - Product description.".formatted(language));

        // check variation if any
        if (productInfo.isHasModel()) {
            String dbVariation = commonAction.getText(loc_dlgEditTranslation_lblVariation);
            String ppVariation = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.variation", language);
            assertCustomize.assertEquals(dbVariation, ppVariation, "[Failed][Edit translation popup] Variation should be %s, but found %s.".formatted(ppVariation, dbVariation));
            logger.info("[UI][%s] Check Edit translation popup - Variation.".formatted(language));
        }

        // check SEO setting
        String dbSEOSetting = commonAction.getText(loc_dlgEditTranslation_lblSEOSetting);
        String ppSEOSetting = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoSetting", language);
        assertCustomize.assertEquals(dbSEOSetting, ppSEOSetting, "[Failed][Edit translation popup] SEO setting should be %s, but found %s.".formatted(ppSEOSetting, dbSEOSetting));
        logger.info("[UI][%s] Check Edit translation popup - SEO Setting.".formatted(language));

        // check Live preview
        String dbLivePreview = commonAction.getText(loc_dlgEditTranslation_lblSEOLivePreview);
        String ppLivePreview = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.livePreview", language);
        assertCustomize.assertEquals(dbLivePreview, ppLivePreview, "[Failed][Edit translation popup] Live preview should be %s, but found %s.".formatted(ppLivePreview, dbLivePreview));
        logger.info("[UI][%s] Check Edit translation popup - Live Preview.".formatted(language));

        // check Live preview tooltips
        commonAction.hoverActions(loc_dlgEditTranslation_tltSEOLivePreview);
        String dbLivePreviewTooltips = commonAction.getAttribute(loc_dlgEditTranslation_tltSEOLivePreview, "data-original-title");
        String ppLivePreviewTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.livePreviewTooltips", language);
        assertCustomize.assertEquals(dbLivePreviewTooltips, ppLivePreviewTooltips, "[Failed][Edit translation popup] Live preview tooltips should be %s, but found %s.".formatted(ppLivePreviewTooltips, dbLivePreviewTooltips));
        logger.info("[UI][%s] Check Edit translation popup - Live Preview Tooltips.".formatted(language));

        // check SEO title
        String dbSEOTitle = commonAction.getText(loc_dlgEditTranslation_lblSEOTitle);
        String ppSEOTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoTitle", language);
        assertCustomize.assertEquals(dbSEOTitle, ppSEOTitle, "[Failed][Edit translation popup] SEO title should be %s, but found %s.".formatted(ppSEOTitle, dbSEOTitle));
        logger.info("[UI][%s] Check Edit translation popup - SEO Title.".formatted(language));

        // check SEO title tooltips
        commonAction.hoverActions(loc_dlgEditTranslation_tltSEOTitle);
        String dbSEOTitleTooltips = commonAction.getAttribute(loc_dlgEditTranslation_tltSEOTitle, "data-original-title");
        String ppSEOTitleTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoTitleTooltips", language);
        assertCustomize.assertEquals(dbSEOTitleTooltips, ppSEOTitleTooltips, "[Failed][Edit translation popup] SEO title tooltips should be %s, but found %s.".formatted(ppSEOTitleTooltips, dbSEOTitleTooltips));
        logger.info("[UI][%s] Check Edit translation popup - SEO Title Tooltips.".formatted(language));

        // check SEO description
        String dbSEODescription = commonAction.getText(loc_dlgEditTranslation_lblSEODescription);
        String ppSEODescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoDescription", language);
        assertCustomize.assertEquals(dbSEODescription, ppSEODescription, "[Failed][Edit translation popup] SEO description should be %s, but found %s.".formatted(ppSEODescription, dbSEODescription));
        logger.info("[UI][%s] Check Edit translation popup - SEO Description.".formatted(language));

        // check SEO description tooltips
        commonAction.hoverActions(loc_dlgEditTranslation_tltSEODescription);
        String dbSEODescriptionTooltips = commonAction.getAttribute(loc_dlgEditTranslation_tltSEODescription, "data-original-title");
        String ppSEODescriptionTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoDescriptionTooltips", language);
        assertCustomize.assertEquals(dbSEODescriptionTooltips, ppSEODescriptionTooltips, "[Failed][Edit translation popup] SEO description tooltips should be %s, but found %s.".formatted(ppSEODescriptionTooltips, dbSEODescriptionTooltips));
        logger.info("[UI][%s] Check Edit translation popup - SEO Description Tooltips.".formatted(language));

        // check SEO keywords
        String dbSEOKeywords = commonAction.getText(loc_dlgEditTranslation_lblSEOKeywords);
        String ppSEOKeywords = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoKeywords", language);
        assertCustomize.assertEquals(dbSEOKeywords, ppSEOKeywords, "[Failed][Edit translation popup] SEO keywords should be %s, but found %s.".formatted(ppSEOKeywords, dbSEOKeywords));
        logger.info("[UI][%s] Check Edit translation popup - SEO Keywords.".formatted(language));

        // check SEO keywords tooltips
        commonAction.hoverActions(loc_dlgEditTranslation_tltSEOKeywords);
        String dbSEOKeywordsTooltips = commonAction.getAttribute(loc_dlgEditTranslation_tltSEOKeywords, "data-original-title");
        String ppSEOKeywordsTooltips = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoKeywordsTooltips", language);
        assertCustomize.assertEquals(dbSEOKeywordsTooltips, ppSEOKeywordsTooltips, "[Failed][Edit translation popup] SEO keywords tooltips should be %s, but found %s.".formatted(ppSEOKeywordsTooltips, dbSEOKeywordsTooltips));
        logger.info("[UI][%s] Check Edit translation popup - SEO Keywords tooltips.".formatted(language));

        // check SEO Url
        String dbSEOUrl = commonAction.getText(loc_dlgEditTranslation_lblSEOUrl);
        String ppSEOUrl = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.seoURLLink", language);
        assertCustomize.assertEquals(dbSEOUrl, ppSEOUrl, "[Failed][Edit translation popup] SEO Url should be %s, but found %s.".formatted(ppSEOUrl, dbSEOUrl));
        logger.info("[UI][%s] Check Edit translation popup - SEO Url.".formatted(language));

        // Save button
        String dbSaveBtn = commonAction.getText(loc_dlgEditTranslation_lblSave);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.SaveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Edit translation popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Edit translation popup - Save button.".formatted(language));

        // Cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgEditTranslation_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.editTranslationPopup.CancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Edit translation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Edit translation popup - Cancel button.".formatted(language));
    }

    void checkUpdatePricePopup() throws Exception {
        // check title
        commonAction.sleepInMiliSecond(1000);
        String dbTitle = commonAction.getText(loc_ttlUpdatePrice);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Update price popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update price popup - Title.".formatted(language));

        // check price list in dropdown
        // open price dropdown
        commonAction.clickJS(loc_dlgUpdatePrice_ddvSelectedPriceType);
        commonAction.getElement(loc_dlgUpdatePrice_ddlPriceType);
        List<String> dbListPriceInDropdown = commonAction.getListElement(loc_dlgUpdatePrice_lblPriceType).stream().map(WebElement::getText).toList();
        List<String> ppListPriceInDropdown = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInDropdown.2", language));
        assertCustomize.assertEquals(dbListPriceInDropdown, ppListPriceInDropdown, "[Failed][Update price popup] List price in dropdown should be %s, but found %s.".formatted(ppListPriceInDropdown, dbListPriceInDropdown));
        logger.info("[UI][%s] Check Update price popup - List price in dropdown.".formatted(language));

        // close price dropdown
        commonAction.click(loc_dlgUpdatePrice_ddvSelectedPriceType);

        // check apply all button
        String dbApplyAllBtn = commonAction.getText(loc_dlgUpdatePrice_lblApplyAll);
        String ppApplyAllBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.applyAllBtn", language);
        assertCustomize.assertEquals(dbApplyAllBtn, ppApplyAllBtn, "[Failed][Update price popup] Apply all button should be %s, but found %s.".formatted(ppApplyAllBtn, dbApplyAllBtn));
        logger.info("[UI][%s] Check Update price popup - Apply all button.".formatted(language));

        // check price list in table
        List<WebElement> listOfPrice = commonAction.getListElement(loc_dlgUpdatePrice_tblVariation_lblPriceType);
        List<String> dbListPriceInTable = IntStream.iterate(3, i -> i >= 1, i -> i - 1).mapToObj(i -> listOfPrice.get(listOfPrice.size() - i).getText()).toList();
        List<String> ppListPriceInTable = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.1", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.priceInTable.2", language));
        assertCustomize.assertEquals(dbListPriceInTable, ppListPriceInTable, "[Failed][Update price popup] List price in table should be %s, but found %s.".formatted(ppListPriceInTable, dbListPriceInTable));
        logger.info("[UI][%s] Check Update price popup - List price in table.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(loc_dlgUpdatePrice_lblUpdate);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.updateBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Update price popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update price popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgUpdatePrice_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updatePricePopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Update price popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update price popup - Cancel button.".formatted(language));
    }

    void checkUpdateStockPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_ttlUpdateStock);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Update normal variation stock popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update normal variation stock popup - Title.".formatted(language));

        // check selected branches
        String[] dbNumberOfSelectedBranches = commonAction.getText(loc_dlgUpdateStock_ddvSelectedBranch).split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.numberOfSelectedBranches", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Update normal variation stock popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Update normal variation stock popup - Number of selected branches.".formatted(language));

        // check list actions
        List<String> dbListActions = commonAction.getListElement(loc_dlgUpdateStock_lblActions).stream().map(WebElement::getText).toList();
        List<String> ppListActions = List.of(getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.0", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.1", language));
        assertCustomize.assertEquals(dbListActions, ppListActions, "[Failed][Update normal variation stock popup] List actions should be %s, but found %s.".formatted(ppListActions, dbListActions));
        logger.info("[UI][%s] Check Update normal variation stock popup - List actions.".formatted(language));

        // check input stock placeholder
        String dbInputStockPlaceholder = commonAction.getAttribute(loc_dlgUpdateStock_plhStockValue, "placeholder");
        String ppInputStockPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.inputStockPlaceholder", language);
        assertCustomize.assertEquals(dbInputStockPlaceholder, ppInputStockPlaceholder, "[Failed][Update normal variation stock popup] Input stock placeholder should be %s, but found %s.".formatted(ppInputStockPlaceholder, dbInputStockPlaceholder));
        logger.info("[UI][%s] Check Update normal variation stock popup - Input stock placeholder.".formatted(language));

        // check action type
        String dbActionType = commonAction.getText(loc_dlgUpdateStock_lblCurrentAction).split(": ")[0];
        String ppActionType0 = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.0", language);
        String ppActionType1 = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.listActions.1", language);
        assertCustomize.assertTrue(dbActionType.equals(ppActionType1) || dbActionType.equals(ppActionType0), "[Failed][Update normal variation popup] Action type should be %s or %s, but found %s.".formatted(ppActionType0, ppActionType1, dbActionType));
        logger.info("[UI][%s] Check Update normal variation popup - Action type.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(loc_dlgUpdateStock_lblUpdate);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.updateBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Update normal variation popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update normal variation popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgUpdateStock_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateStockPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Update normal variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update normal variation popup - Cancel button.".formatted(language));
    }

    void checkAddIMEIPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_ttlAddIMEI);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Add IMEI popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Add IMEI popup - Title.".formatted(language));

        // check branch title
        String dbBranchTitle = commonAction.getText(loc_dlgAddIMEI_lblBranch);
        String ppBranchTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.branchLabel", language);
        assertCustomize.assertEquals(dbBranchTitle, ppBranchTitle, "[Failed][Add IMEI popup] Branch title should be %s, but found %s.".formatted(ppBranchTitle, dbBranchTitle));
        logger.info("[UI][%s] Check Add IMEI popup - Branch title.".formatted(language));

        // check number of selected branches
        String[] dbNumberOfSelectedBranches = commonAction.getText(loc_dlgAddIMEISelectedBranch).split("\\d+");

        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.numberOfSelectedBranches", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Add IMEI popup] Number of selected branches should be %s, but found %s.".formatted(Arrays.toString(ppNumberOfSelectedBranches), Arrays.toString(dbNumberOfSelectedBranches)));
        logger.info("[UI][%s] Check Add IMEI popup - Number of selected branches.".formatted(language));

        // check add IMEI placeholder
        String dbIMEIPlaceholder = commonAction.getAttribute(loc_dlgAddIMEI_plhAddIMEI, 0, "placeholder");
        String ppIMEIPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.addIMEITextBoxPlaceholder", language);
        assertCustomize.assertEquals(dbIMEIPlaceholder, ppIMEIPlaceholder, "[Failed][Add IMEI popup] Input IMEI placeholder should be %s, but found %s.".formatted(ppIMEIPlaceholder, dbIMEIPlaceholder));
        logger.info("[UI][%s] Check Add IMEI popup - Input IMEI placeholder.".formatted(language));

        // check product name column
        String dbProductNameColumn = commonAction.getText(loc_dlgAddIMEI_lblProductName);
        String ppProductNameColumn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.productNameColumn", language);
        assertCustomize.assertEquals(dbProductNameColumn, ppProductNameColumn, "[Failed][Add IMEI popup] Product name column should be %s, but found %s.".formatted(ppProductNameColumn, dbProductNameColumn));
        logger.info("[UI][%s] Check Add IMEI popup - Product name column.".formatted(language));

        // check save button
        String dbSaveBtn = commonAction.getText(loc_dlgAddIMEI_lblSave);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.saveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Add IMEI popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Add IMEI popup - Save button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgAddIMEI_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.addIMEIPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Add IMEI popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Add IMEI popup - Cancel button.".formatted(language));
    }

    void checkUpdateSKUPopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_ttlUpdateSKU);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Update SKU popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Update SKU popup - Title.".formatted(language));

        // check number of selected branches
        String[] dbNumberOfSelectedBranches = commonAction.getText(loc_dlgUpdateSKU_lblBranch).split("\\d+");
        String[] ppNumberOfSelectedBranches = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.numberOfSelectedBranches", language).split("\\d+");
        assertCustomize.assertEquals(dbNumberOfSelectedBranches, ppNumberOfSelectedBranches, "[Failed][Update SKU popup] Number of selected branches should be %s, but found %s.".formatted(ppNumberOfSelectedBranches, dbNumberOfSelectedBranches));
        logger.info("[UI][%s] Check Update SKU popup - Number of selected branches.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(loc_dlgUpdateSKU_lblUpdate);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.updateBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Update SKU popup] Update button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Update SKU popup - Update button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgUpdateSKU_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateSKUPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Update SKU popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Update SKU popup - Cancel button.".formatted(language));
    }

    void checkUpdateVariationImagePopup() throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_ttlUploadImages);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Upload image popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Upload image popup - Title.".formatted(language));

        // check upload image button placeholder
        String dbUploadImageBtnPlaceholder = commonAction.getText(loc_dlgUploadImages_plhUpload);
        String ppUploadImageBtnPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.uploadBtnPlaceholder", language);
        assertCustomize.assertEquals(dbUploadImageBtnPlaceholder, ppUploadImageBtnPlaceholder, "[Failed][Upload image popup] Upload image button placeholder should be %s, but found %s.".formatted(ppUploadImageBtnPlaceholder, dbUploadImageBtnPlaceholder));
        logger.info("[UI][%s] Check Upload image popup - Upload image button placeholder.".formatted(language));

        // check update button
        String dbUpdateBtn = commonAction.getText(loc_dlgUploadImages_lblSelect);
        String ppUpdateBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.selectBtn", language);
        assertCustomize.assertEquals(dbUpdateBtn, ppUpdateBtn, "[Failed][Upload image popup] Select button should be %s, but found %s.".formatted(ppUpdateBtn, dbUpdateBtn));
        logger.info("[UI][%s] Check Upload image popup - Select button.".formatted(language));

        // check cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgUploadImages_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.variations.updateImagePopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Upload image popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Upload image popup - Cancel button.".formatted(language));
    }

    void checkUIAddConversionUnit() throws Exception {
        // check conversion unit title
        String dbConversionUnit = commonAction.getText(loc_lblUnit);
        String ppConversionUnit = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.title", language);
        assertCustomize.assertEquals(dbConversionUnit, ppConversionUnit, "[Failed][Body] Conversion unit title should be %s, but found %s.".formatted(ppConversionUnit, dbConversionUnit));
        logger.info("[UI][%s] Check Body - Conversion unit title.".formatted(language));

        // check conversion unit search box placeholder
        String dbConversionUnitSearchBoxPlaceholder = commonAction.getAttribute(loc_plhSearchUnit, "placeholder");
        String ppConversionUnitSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.searchBoxPlaceholder", language);
        assertCustomize.assertEquals(dbConversionUnitSearchBoxPlaceholder, ppConversionUnitSearchBoxPlaceholder, "[Failed][Body] Conversion unit search box placeholder should be %s, but found %s.".formatted(ppConversionUnitSearchBoxPlaceholder, dbConversionUnitSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Conversion unit search box placeholder.".formatted(language));

        // check add conversion unit checkbox
        String dbAddConversionUnitCheckbox = commonAction.getText(loc_lblAddConversionUnit);
        String ppAddConversionUnitCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.addConversionUnitCheckbox", language);
        assertCustomize.assertEquals(dbAddConversionUnitCheckbox, ppAddConversionUnitCheckbox, "[Failed][Body] Add conversion unit checkbox label should be %s, but found %s.".formatted(ppAddConversionUnitCheckbox, dbAddConversionUnitCheckbox));
        logger.info("[UI][%s] Check Body - Add conversion unit checkbox.".formatted(language));

        // check conversion unit tooltips
        commonAction.hoverActions(loc_tltConversionUnit);
        String dbConversionUnitTooltips = commonAction.getAttribute(loc_tltConversionUnit, "data-original-title");
        String ppConversionUnitTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.tooltips", language);
        assertCustomize.assertEquals(dbConversionUnitTooltips, ppConversionUnitTooltips, "[Failed][Body] Conversion unit tooltips should be %s, but found %s.".formatted(ppConversionUnitTooltips, dbConversionUnitTooltips));
        logger.info("[UI][%s] Check Body - Conversion unit tooltips.".formatted(language));
    }

    void checkUIAddWholesaleProduct() throws Exception {
        // check add wholesale product checkbox
        String dbAddWholesaleProductCheckbox = commonAction.getText(loc_lblAddWholesalePricing);
        String ppAddWholesaleProductCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.addWholesaleProductCheckbox", language);
        assertCustomize.assertEquals(dbAddWholesaleProductCheckbox, ppAddWholesaleProductCheckbox, "[Failed][Body] Add wholesale product checkbox label should be %s, but found %s.".formatted(ppAddWholesaleProductCheckbox, dbAddWholesaleProductCheckbox));
        logger.info("[UI][%s] Check Body - Add wholesale product checkbox.".formatted(language));
    }

    void checkUIDeposit() throws Exception {
        // check deposit title
        String dbDeposit = commonAction.getText(loc_lblDeposit);
        String ppDeposit = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.title", language);
        assertCustomize.assertEquals(dbDeposit, ppDeposit, "[Failed][Body] Deposit title should be %s, but found %s.".formatted(ppDeposit, dbDeposit));
        logger.info("[UI][%s] Check Body - Deposit.".formatted(language));

        // check Add deposit button
        String dbAddDepositBtn = commonAction.getText(loc_lblAddDeposit);
        String ppAddDepositBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.addDepositBtn", language);
        assertCustomize.assertEquals(dbAddDepositBtn, ppAddDepositBtn, "[Failed][Body] Add deposit button should be %s, but found %s.".formatted(ppAddDepositBtn, dbAddDepositBtn));
        logger.info("[UI][%s] Check Body - Add deposit button.".formatted(language));

        // check deposit description
        String dbDepositDescription = commonAction.getText(loc_cntDeposit);
        String ppDepositDescription = getPropertiesValueByDBLang("products.allProducts.createProduct.deposit.depositDescription", language);
        assertCustomize.assertEquals(dbDepositDescription, ppDepositDescription, "[Failed][Body] Deposit description should be %s, but found %s.".formatted(ppDepositDescription, dbDepositDescription));
        logger.info("[UI][%s] Check Body - Deposit description.".formatted(language));
    }

    void checkUISEO() throws Exception {
        // check SEO setting
        String dbSEOSetting = commonAction.getText(loc_lblSEOSetting);
        String ppSEOSetting = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.title", language);
        assertCustomize.assertEquals(dbSEOSetting, ppSEOSetting, "[Failed][Body] SEO setting should be %s, but found %s.".formatted(ppSEOSetting, dbSEOSetting));
        logger.info("[UI][%s] Check Body - SEO setting.".formatted(language));

        // check SEO live preview
        String dbLivePreview = commonAction.getText(loc_lblLivePreview);
        String ppLivePreview = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.livePreview", language);
        assertCustomize.assertEquals(dbLivePreview, ppLivePreview, "[Failed][Body] Live preview should be %s, but found %s.".formatted(ppLivePreview, dbLivePreview));
        logger.info("[UI][%s] Check Body - Live preview.".formatted(language));

        // check SEO live preview tooltips
        commonAction.hoverActions(loc_tltLivePreview);
        String dbLivePreviewTooltips = commonAction.getAttribute(loc_tltLivePreview, "data-original-title");
        String ppLivePreviewTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.livePreviewTooltips", language);
        assertCustomize.assertEquals(dbLivePreviewTooltips, ppLivePreviewTooltips, "[Failed][Body] Live preview tooltips should be %s, but found %s.".formatted(ppLivePreviewTooltips, dbLivePreviewTooltips));
        logger.info("[UI][%s] Check Body - Live preview tooltips.".formatted(language));

        // check SEO title
        String dbSEOTitle = commonAction.getText(loc_lblSEOTitle);
        String ppSEOTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoTitle", language);
        assertCustomize.assertEquals(dbSEOTitle, ppSEOTitle, "[Failed][Body] SEO Title should be %s, but found %s.".formatted(ppSEOTitle, dbSEOTitle));
        logger.info("[UI][%s] Check Body - SEO Title.".formatted(language));

        // check SEO title tooltips
        commonAction.hoverActions(loc_tltSEOTitle);
        String dbSEOTitleTooltips = commonAction.getAttribute(loc_tltSEOTitle, "data-original-title");
        String ppSEOTitleTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoTitleTooltips", language);
        assertCustomize.assertEquals(dbSEOTitleTooltips, ppSEOTitleTooltips, "[Failed][Body] SEO Title tooltips should be %s, but found %s.".formatted(ppSEOTitleTooltips, dbSEOTitleTooltips));
        logger.info("[UI][%s] Check Body - SEO Title tooltips.".formatted(language));

        // check SEO description
        String dbSEODescription = commonAction.getText(loc_lblSEODescription);
        String ppSEODescription = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoDescription", language);
        assertCustomize.assertEquals(trim(dbSEODescription), ppSEODescription, "[Failed][Body] SEO description should be %s, but found %s.".formatted(ppSEODescription, dbSEODescription));
        logger.info("[UI][%s] Check Body - SEO description.".formatted(language));

        // check SEO description tooltips
        commonAction.hoverActions(loc_tltSEODescription);
        String dbSEODescriptionTooltips = commonAction.getAttribute(loc_tltSEODescription, "data-original-title");
        String ppSEODescriptionTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoDescriptionTooltips", language);
        assertCustomize.assertEquals(dbSEODescriptionTooltips, ppSEODescriptionTooltips, "[Failed][Body] SEO descriptions tooltips should be %s, but found %s.".formatted(ppSEODescriptionTooltips, dbSEODescriptionTooltips));
        logger.info("[UI][%s] Check Body - SEO description tooltips.".formatted(language));

        // check SEO keywords
        String dbSEOKeywords = commonAction.getText(loc_lblSEOKeywords);
        String ppSEOKeywords = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoKeywords", language);
        assertCustomize.assertEquals(dbSEOKeywords, ppSEOKeywords, "[Failed][Body] SEO keywords should be %s, but found %s.".formatted(ppSEOKeywords, dbSEOKeywords));
        logger.info("[UI][%s] Check Body - SEO keywords.".formatted(language));

        // check SEO keywords tooltips
        commonAction.hoverActions(loc_tltSEOKeywords);
        String dbSEOKeywordsTooltips = commonAction.getAttribute(loc_tltSEOKeywords, "data-original-title");
        String ppSEOKeywordsTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoKeywordsTooltips", language);
        assertCustomize.assertEquals(dbSEOKeywordsTooltips, ppSEOKeywordsTooltips, "[Failed][Body] SEO keywords tooltips should be %s, but found %s.".formatted(ppSEOKeywordsTooltips, dbSEOKeywordsTooltips));
        logger.info("[UI][%s] Check Body - SEO keywords tooltips.".formatted(language));

        // check SEO URL link
        String dbSEOUrlLink = commonAction.getText(loc_lblSEOUrl);
        String ppSEOUrlLink = getPropertiesValueByDBLang("products.allProducts.createProduct.seoSettings.seoUrlLink", language);
        assertCustomize.assertEquals(dbSEOUrlLink, ppSEOUrlLink, "[Failed][Body] SEO URL link should be %s, but found %s.".formatted(ppSEOUrlLink, dbSEOUrlLink));
        logger.info("[UI][%s] Check Body - SEO URL link.".formatted(language));
    }

    private List<String> getAllSaleChannelTooltips() {
        try {
            List<WebElement> tooltips = commonAction.getListElement(this.loc_tltSaleChannel);
            return IntStream.range(0, tooltips.size()).mapToObj(tooltipIndex -> commonAction.getText(this.loc_tltSaleChannel, tooltipIndex)).toList();
        } catch (IndexOutOfBoundsException | StaleElementReferenceException ex) {
            logger.info(ex);
            return getAllSaleChannelTooltips();
        }
    }

    void checkUISaleChanel() throws Exception {
        // check Sale chanel title
        String dbSaleChanel = commonAction.getText(loc_lblSaleChannel);
        String ppSaleChanel = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.title", language);
        assertCustomize.assertEquals(dbSaleChanel, ppSaleChanel, "[Failed][Body] Sale chanel title should be %s, but found %s.".formatted(ppSaleChanel, dbSaleChanel));
        logger.info("[UI][%s] Check Body - Sale chanel title.".formatted(language));

        // check Online shop tooltips
        commonAction.viewTooltips(loc_icnOnlineShop, loc_tltSaleChannel);
        String ppOnlineShopTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.onlineShopTooltips", language);
        assertCustomize.assertTrue(getAllSaleChannelTooltips().contains(ppOnlineShopTooltips), "[Failed][Body] Online shop tooltips should be %s, but found %s.".formatted(ppOnlineShopTooltips, loc_tltSaleChannel));
        logger.info("[UI][%s] Check Body - Online shop tooltips.".formatted(language));

        // check Gomua tooltips
        commonAction.viewTooltips(loc_icnGoMua, loc_tltSaleChannel);
        String ppGomuaTooltips = getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.gomuaTooltips", language);
        assertCustomize.assertTrue(getAllSaleChannelTooltips().contains(ppGomuaTooltips), "[Failed][Body] Gomua tooltips should be %s, but found %s.".formatted(ppGomuaTooltips, loc_tltSaleChannel.toString()));
        logger.info("[UI][%s] Check Body - Gomua tooltips.".formatted(language));

        // check Shopee tooltips
        commonAction.viewTooltips(loc_icnShopee, loc_tltSaleChannel);
        List<String> ppShopeeTooltips = List.of(getPropertiesValueByDBLang("products.allProducts.updateProduct.saleChanel.shopeeTooltips.IMEI", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.deactivatedShopeeTooltips", language),
                getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.activatedShopeeTooltips", language));
        List<String> joinShopeeList = getAllSaleChannelTooltips().stream().filter(ppShopeeTooltips::contains).toList();
        assertCustomize.assertFalse(joinShopeeList.isEmpty(), "[Failed][Body] Shopee tooltips should be %s, but found %s.".formatted(ppShopeeTooltips.toString(), loc_tltSaleChannel.toString()));
        logger.info("[UI][%s] Check Body - Shopee tooltips.".formatted(language));

        // check Tiktok tooltips
        commonAction.viewTooltips(loc_icnTiktok, loc_tltSaleChannel);
        List<String> ppTiktokTooltips = List.of(getPropertiesValueByDBLang("products.allProducts.updateProduct.saleChanel.tiktokTooltips.IMEI", language), getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.activatedTiktokTooltips", language), getPropertiesValueByDBLang("products.allProducts.createProduct.saleChanel.deactivatedTiktokTooltips", language));
        List<String> joinTiktokList = getAllSaleChannelTooltips().stream().filter(ppTiktokTooltips::contains).toList();
        assertCustomize.assertFalse(joinTiktokList.isEmpty(), "[Failed][Body] Tiktok tooltips should be %s, but found %s.".formatted(ppTiktokTooltips.toString(), loc_tltSaleChannel.toString()));
        logger.info("[UI][%s] Check Body - Tiktok tooltips.".formatted(language));
    }

    void checkUICollections() throws Exception {
        // check collections title
        String dbCollectionsTitle = commonAction.getText(loc_lblCollection);
        String ppCollectionsTitle = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.title", language);
        assertCustomize.assertEquals(dbCollectionsTitle, ppCollectionsTitle, "[Failed][Body] Collections title should be %s, but found %s.".formatted(ppCollectionsTitle, dbCollectionsTitle));
        logger.info("[UI][%s] Check Body - Collections title.".formatted(language));

        // check collections search box placeholder
        String dbCollectionsSearchBoxPlaceholder = commonAction.getAttribute(loc_plhSearchCollection, "placeholder");
        String ppCollectionsSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.searchBoxPlaceholder", language);
        assertCustomize.assertEquals(dbCollectionsSearchBoxPlaceholder, ppCollectionsSearchBoxPlaceholder, "[Failed][Body] Collections search box placeholder should be %s, but found %s.".formatted(ppCollectionsSearchBoxPlaceholder, dbCollectionsSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Body - Collections search box placeholder.".formatted(language));

        if (new ProductCollection(loginInformation).getListOfManualProductCollectionsName().isEmpty()) {
            // check when no collection created
            String dbNoCreatedCollection = commonAction.getText(loc_cntNoCollection);
            String ppNoCreatedCollection = getPropertiesValueByDBLang("products.allProducts.createProduct.collections.noCreatedCollection", language);
            assertCustomize.assertEquals(dbNoCreatedCollection, ppNoCreatedCollection, "[Failed][Body] No created collection should be %s, but found %s.".formatted(ppNoCreatedCollection, dbNoCreatedCollection));
            logger.info("[UI][%s] Check Body - No created collection.".formatted(language));
        }
    }

    void checkUICRWarehousing() throws Exception {
        // check warehousing title
        String dbWarehousing = commonAction.getText(loc_lblWarehousing);
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.title", language);
        assertCustomize.assertEquals(dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing title should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing title.".formatted(language));

        // check SKU
        String dbSKU = commonAction.getText(loc_lblWithoutVariationSKU);
        String ppSKU = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.sku", language);
        assertCustomize.assertEquals(dbSKU, ppSKU, "[Failed][Body] SKU title should be %s, but found %s.".formatted(ppSKU, dbSKU));
        logger.info("[UI][%s] Check Body - SKU title.".formatted(language));

        // check Barcode
        String dbBarcode = commonAction.getText(loc_lblWithoutVariationBarcode);
        String ppBarcode = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.barcode", language);
        assertCustomize.assertEquals(dbBarcode, ppBarcode, "[Failed][Body] Barcode title should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
        logger.info("[UI][%s] Check Body - Barcode title.".formatted(language));

        // check manage inventory
        String dbManageInventory = commonAction.getText(loc_lblManageInventory);
        String ppManageInventory = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.title", language);
        assertCustomize.assertEquals(dbManageInventory, ppManageInventory, "[Failed][Body] Manage inventory title should be %s, but found %s.".formatted(ppManageInventory, dbManageInventory));
        logger.info("[UI][%s] Check Body - Manage inventory.".formatted(language));

        // check manage inventory by product
        String dbManageInventoryByProduct = commonAction.getText(loc_lblManageByProduct);
        String ppManageInventoryByProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byProduct", language);
        assertCustomize.assertEquals(dbManageInventoryByProduct, ppManageInventoryByProduct, "[Failed][Body] Manage inventory by product should be %s, but found %s.".formatted(ppManageInventoryByProduct, dbManageInventoryByProduct));
        logger.info("[UI][%s] Check Body - Manage inventory by product.".formatted(language));

        // check manage inventory by IMEI/Serial number
        String dbManageInventoryByIMEI = commonAction.getText(loc_lblManageByIMEI);
        String ppManageInventoryByIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI", language);
        assertCustomize.assertEquals(dbManageInventoryByIMEI, ppManageInventoryByIMEI, "[Failed][Body] Manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppManageInventoryByIMEI, dbManageInventoryByIMEI));
        logger.info("[UI][%s] Check Body - Manage inventory by IMEI/Serial number.".formatted(language));

        // check stock quantity title
        String dbStockQuantity = commonAction.getText(loc_lblWithoutVariationStockQuantity);
        String ppStockQuantity = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.title", language);
        assertCustomize.assertEquals(dbStockQuantity, ppStockQuantity, "[Failed][Body] Stock quantity title should be %s, but found %s.".formatted(ppStockQuantity, dbStockQuantity));
        logger.info("[UI][%s] Check Body - Stock quantity title.".formatted(language));

        // check apply stock for all branches button
        String dbApplyAllBtn = commonAction.getText(loc_lblApplyAll);
        String ppApplyAllBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.stockQuantity.applyAllBtn", language);
        assertCustomize.assertEquals(dbApplyAllBtn, ppApplyAllBtn, "[Failed][Body] Apply all button should be %s, but found %s.".formatted(ppApplyAllBtn, dbApplyAllBtn));
        logger.info("[UI][%s] Check Body - Apply all button.".formatted(language));

        // check display if out of stock checkbox
        String dbDisplayIfOutOfStockCheckbox = commonAction.getText(loc_lblDisplayIfOutOfStock);
        String ppDisplayIfOutOfStockCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.displayIfOutOfStock", language);
        assertCustomize.assertEquals(dbDisplayIfOutOfStockCheckbox, ppDisplayIfOutOfStockCheckbox, "[Failed][Body] Display if out of stock checkbox label should be %s, but found %s.".formatted(ppDisplayIfOutOfStockCheckbox, dbDisplayIfOutOfStockCheckbox));
        logger.info("[UI][%s] Check Body - Display if out of stock checkbox.".formatted(language));

        // check hide remaining stock on online store
        String dbHideRemainingOnOnlineStoreCheckbox = commonAction.getText(loc_lblHideRemainingStock);
        String ppHideRemainingOnOnlineStoreCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.hideRemainingStock", language);
        assertCustomize.assertEquals(dbHideRemainingOnOnlineStoreCheckbox, ppHideRemainingOnOnlineStoreCheckbox, "[Failed][Body] Hide remaining stock on online store checkbox label should be %s, but found %s.".formatted(ppHideRemainingOnOnlineStoreCheckbox, dbHideRemainingOnOnlineStoreCheckbox));
        logger.info("[UI][%s] Check Body - Hide remaining stock on online store checkbox.".formatted(language));
    }

    void checkUIUPWarehousing() throws Exception {
        // check warehousing title
        String dbWarehousing = commonAction.getText(loc_lblWarehousing);
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.title", language);
        assertCustomize.assertEquals(dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing title should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing title.".formatted(language));

        // check SKU
        if (!commonAction.getListElement(loc_lblWithoutVariationSKU).isEmpty()) {
            String dbSKU = commonAction.getText(loc_lblWithoutVariationSKU);
            String ppSKU = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.sku", language);
            assertCustomize.assertEquals(dbSKU, ppSKU, "[Failed][Body] SKU title should be %s, but found %s.".formatted(ppSKU, dbSKU));
            logger.info("[UI][%s] Check Body - SKU title.".formatted(language));

            // check Barcode
            String dbBarcode = commonAction.getText(loc_lblWithoutVariationBarcode);
            String ppBarcode = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.barcode", language);
            assertCustomize.assertEquals(dbBarcode, ppBarcode, "[Failed][Body] Barcode title should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
            logger.info("[UI][%s] Check Body - Barcode title.".formatted(language));
        }

        // check manage inventory
        String dbManageInventory = commonAction.getText(loc_lblManageInventory);
        String ppManageInventory = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.title", language);
        assertCustomize.assertEquals(dbManageInventory, ppManageInventory, "[Failed][Body] Manage inventory title should be %s, but found %s.".formatted(ppManageInventory, dbManageInventory));
        logger.info("[UI][%s] Check Body - Manage inventory.".formatted(language));

        // check manage inventory by product
        String dbManageInventoryByProduct = commonAction.getText(loc_lblManageByProduct);
        String ppManageInventoryByProduct = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byProduct", language);
        assertCustomize.assertEquals(dbManageInventoryByProduct, ppManageInventoryByProduct, "[Failed][Body] Manage inventory by product should be %s, but found %s.".formatted(ppManageInventoryByProduct, dbManageInventoryByProduct));
        logger.info("[UI][%s] Check Body - Manage inventory by product.".formatted(language));

        // check manage inventory by IMEI/Serial number
        String dbManageInventoryByIMEI = commonAction.getText(loc_lblManageByIMEI);
        String ppManageInventoryByIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI", language);
        assertCustomize.assertEquals(dbManageInventoryByIMEI, ppManageInventoryByIMEI, "[Failed][Body] Manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppManageInventoryByIMEI, dbManageInventoryByIMEI));
        logger.info("[UI][%s] Check Body - Manage inventory by IMEI/Serial number.".formatted(language));

        // check remaining stock
        String dbRemainingStock = commonAction.getText(loc_lblRemainingStock);
        String ppRemainingStock = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.remainingStock", language);
        assertCustomize.assertEquals(dbRemainingStock, ppRemainingStock, "[Failed][Body] Remaining stock should be %s, but found %s.".formatted(ppRemainingStock, dbRemainingStock));
        logger.info("[UI][%s] Check Body - Remaining stock.".formatted(language));

        // check view remaining stock popup
        checkViewRemainingStockPopup();

        // check sold count
        String dbSoldCount = commonAction.getText(loc_lblSoldCount);
        String ppSoldCount = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.soldCount", language);
        assertCustomize.assertEquals(dbSoldCount, ppSoldCount, "[Failed][Body] Sold count should be %s, but found %s.".formatted(ppSoldCount, dbSoldCount));
        logger.info("[UI][%s] Check Body - Sold count.".formatted(language));

        // check view sold count popup
        checkViewSoldCountPopup();

        // check display if out of stock checkbox
        String dbDisplayIfOutOfStockCheckbox = commonAction.getText(loc_lblDisplayIfOutOfStock);
        String ppDisplayIfOutOfStockCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.displayIfOutOfStock", language);
        assertCustomize.assertEquals(dbDisplayIfOutOfStockCheckbox, ppDisplayIfOutOfStockCheckbox, "[Failed][Body] Display if out of stock checkbox label should be %s, but found %s.".formatted(ppDisplayIfOutOfStockCheckbox, dbDisplayIfOutOfStockCheckbox));
        logger.info("[UI][%s] Check Body - Display if out of stock checkbox.".formatted(language));

        // check hide remaining stock on online store
        String dbHideRemainingOnOnlineStoreCheckbox = commonAction.getText(loc_lblHideRemainingStock);
        String ppHideRemainingOnOnlineStoreCheckbox = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.hideRemainingStock", language);
        assertCustomize.assertEquals(dbHideRemainingOnOnlineStoreCheckbox, ppHideRemainingOnOnlineStoreCheckbox, "[Failed][Body] Hide remaining stock on online store checkbox label should be %s, but found %s.".formatted(ppHideRemainingOnOnlineStoreCheckbox, dbHideRemainingOnOnlineStoreCheckbox));
        logger.info("[UI][%s] Check Body - Hide remaining stock on online store checkbox.".formatted(language));
    }

    void checkViewRemainingStockPopup() throws Exception {
        // open view remaining stock popup
        commonAction.openPopupJS(loc_lnkRemainingStock, loc_dlgCommons);

        // check title
        String dbTitle = commonAction.getText(loc_ttlRemainingStock);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][View remaining stock popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check View remaining stock popup - Title.".formatted(language));

        // check text in dropdown when select all branches
        String dbSelectAllBranches = commonAction.getText(loc_dlgRemainingStock_ddlBranch);
        String ppSelectAllBranches = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.branchDropdown.text.allBranch", language);
        assertCustomize.assertEquals(dbSelectAllBranches, ppSelectAllBranches, "[Failed][ View remaining stock popup] Dropdown text when select all branches should be %s, but found %s.".formatted(ppSelectAllBranches, dbSelectAllBranches));
        logger.info("[UI][%s] Check View remaining stock popup - Check dropdown text when select all branches.".formatted(language));

        // open branch dropdown
        commonAction.click(loc_dlgRemainingStock_ddlBranch);

        // click on All branch check to unselect all branches
        commonAction.clickJS(loc_dlgRemainingStock_chkAllBranches);

        // check text in dropdown when no select any branch
        String dbNoSelectAnyBranch = commonAction.getText(loc_dlgRemainingStock_ddlBranch);
        String ppNoSelectAnyBranch = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.branchDropdown.text.noBranch", language);
        assertCustomize.assertEquals(dbNoSelectAnyBranch, ppNoSelectAnyBranch, "[Failed][ View remaining stock popup] Dropdown text when no select any branch should be %s, but found %s.".formatted(ppNoSelectAnyBranch, dbNoSelectAnyBranch));
        logger.info("[UI][%s] Check View remaining stock popup -  Check dropdown text when no select any branch.".formatted(language));

        // check All branches checkbox label
        String dbAllBranchesCheckbox = commonAction.getText(loc_dlgRemainingStock_chkAllBranches);
        String ppAllBranchesCheckbox = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.branchDropDown.allBranchCheckbox", language);
        assertCustomize.assertEquals(dbAllBranchesCheckbox, ppAllBranchesCheckbox, "[Failed][ View remaining stock popup] All branches checkbox label should be %s, but found %s.".formatted(ppAllBranchesCheckbox, dbAllBranchesCheckbox));
        logger.info("[UI][%s] Check View remaining stock popup - All branches checkbox.".formatted(language));

        // close branch dropdown
        commonAction.click(loc_dlgRemainingStock_ddlBranch);

        // check error when no select any branch
        String dbNoSelectBranchError = commonAction.getText(loc_dlgRemainingStock_lblNoBranchSelected);
        String ppNoSelectBranchError = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewRemainingStockPopup.noBranchError", language);
        assertCustomize.assertEquals(dbNoSelectBranchError, ppNoSelectBranchError, "[Failed][ View remaining stock popup] No select branch error should be %s, but found %s.".formatted(ppNoSelectBranchError, dbNoSelectBranchError));
        logger.info("[UI][%s] Check View remaining stock popup - No select branch error.".formatted(language));

        // close view remaining stock popup
        commonAction.closePopup(loc_dlgRemainingStock_btnClose);
        logger.info("Close remaining popup.");
    }

    void checkViewSoldCountPopup() throws Exception {
        // open view sold count popup
        commonAction.openPopupJS(loc_lnkSoldCount, loc_dlgCommons);

        // check title
        String dbTitle = commonAction.getText(loc_ttlViewSoldCount);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][View sold count popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check View sold count popup - Title.".formatted(language));

        // check text in dropdown when select all branches
        String dbSelectAllBranches = commonAction.getText(loc_dlgViewSoldCount_ddlBranch);

        String ppSelectAllBranches = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.branchDropdown.text.allBranch", language);
        assertCustomize.assertEquals(dbSelectAllBranches, ppSelectAllBranches, "[Failed][ View sold count popup] Dropdown text when select all branches should be %s, but found %s.".formatted(ppSelectAllBranches, dbSelectAllBranches));
        logger.info("[UI][%s] Check View sold count popup - Check dropdown text when select all branches.".formatted(language));

        // open branch dropdown
        commonAction.openDropdownJS(loc_dlgViewSoldCount_ddlBranch, loc_dlgViewSoldCount_chkSelectAllBranches);

        // click on All branch check to unselect all branches
        commonAction.clickJS(loc_dlgViewSoldCount_chkSelectAllBranches);

        // check text in dropdown when no select any branch
        String dbNoSelectAnyBranch = commonAction.getText(loc_dlgViewSoldCount_ddlBranch);
        String ppNoSelectAnyBranch = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.branchDropdown.text.noBranch", language);
        assertCustomize.assertEquals(dbNoSelectAnyBranch, ppNoSelectAnyBranch, "[Failed][ View sold count popup] Dropdown text when no select any branch should be %s, but found %s.".formatted(ppNoSelectAnyBranch, dbNoSelectAnyBranch));
        logger.info("[UI][%s] Check View sold count popup -  Check dropdown text when no select any branch.".formatted(language));

        // check All branches checkbox label
        String dbAllBranchesCheckbox = commonAction.getText(loc_dlgViewSoldCount_chkSelectAllBranches);
        String ppAllBranchesCheckbox = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.branchDropDown.allBranchCheckbox", language);
        assertCustomize.assertEquals(dbAllBranchesCheckbox, ppAllBranchesCheckbox, "[Failed][ View sold count popup] All branches checkbox label should be %s, but found %s.".formatted(ppAllBranchesCheckbox, dbAllBranchesCheckbox));
        logger.info("[UI][%s] Check View sold count popup - All branches checkbox.".formatted(language));

        // close branch dropdown
        commonAction.closeDropdown(loc_dlgViewSoldCount_ddlBranch, loc_dlgViewSoldCount_chkSelectAllBranches);

        // check error when no select any branch
        String dbNoSelectBranchError = commonAction.getText(loc_dlgViewSoldCount_lblNoBranchSelected);
        String ppNoSelectBranchError = getPropertiesValueByDBLang("products.allProducts.updateProduct.warehousing.stockQuantity.viewSoldCountPopup.noBranchError", language);
        assertCustomize.assertEquals(dbNoSelectBranchError, ppNoSelectBranchError, "[Failed][ View sold count popup] No select branch error should be %s, but found %s.".formatted(ppNoSelectBranchError, dbNoSelectBranchError));
        logger.info("[UI][%s] Check View sold count popup - No select branch error.".formatted(language));

        // close view sold count popup
        commonAction.closePopup(loc_dlgViewSoldCount_btnClose);
    }

    void checkManageInventoryByIMEINotice() throws Exception {
        // check manage inventory by IMEI/Serial number notice
        String dbIMEINotice = commonAction.getText(loc_cntManageByIMEI);
        String ppIMEINotice = getPropertiesValueByDBLang("products.allProducts.createProduct.warehousing.manageInventory.byIMEI.notice", language);
        assertCustomize.assertEquals(dbIMEINotice, ppIMEINotice, "[Failed][Body] Manage Inventory by IMEI/Serial number notice should be %s, but found %s.".formatted(ppIMEINotice, dbIMEINotice));
        logger.info("[UI][%s] Check Body - Manage Inventory by IMEI/Serial number notice.".formatted(language));
    }

    void checkUIPackageInformation() throws Exception {
        // check package information title
        String dbPackageInformation = commonAction.getText(loc_lblPackageInformation);
        String ppPackageInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.packageInformation.title", language);
        assertCustomize.assertEquals(dbPackageInformation, ppPackageInformation, "[Failed][Body] Package information title should be %s, but found %s.".formatted(ppPackageInformation, dbPackageInformation));
        logger.info("[UI][%s] Check Body - Package information title.".formatted(language));

        // check package information tooltips
        commonAction.hoverActions(loc_tltPackageInformation);
        String dbPackageInformationTooltips = commonAction.getAttribute(loc_tltPackageInformation, "data-original-title");
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
        commonAction.getElement(loc_lblSEOSetting);
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

    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-13814
    AllPermissions permissions;
    CheckPermission checkPermission;

    public void checkProductManagementPermission(AllPermissions permissions, int createdProductId, int notCreatedProductId, List<Integer> manualCollectionIds) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // check view product detail
        checkViewProductDetail(createdProductId, manualCollectionIds);
    }

    void checkViewProductDetail(int productId, List<Integer> manualCollectionIds) {
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        // check view product detail permission
        if (permissions.getProduct().getProductManagement().isViewProductDetail()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/product/edit/%s".formatted(DOMAIN, productId), "/product/edit/"), "[Failed] Product detail page must be shown instead of %s.".formatted(driver.getCurrentUrl()));

            // check edit product and related permission
            checkEditProduct(manualCollectionIds);

            // check view cost price
            checkViewCostPrice();
        } else
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/product/edit/%s".formatted(DOMAIN, productId)), "[Failed] Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        logger.info("Check permission: View product detail.");
    }

    public void checkViewCollectionList(List<Integer> manualCollectionIds, AllPermissions permissions) {
        // check collection permission
        if (permissions.getProduct().getCollection().isViewCollectionList() && !manualCollectionIds.isEmpty()) {
            assertCustomize.assertTrue(!commonAction.getListElement(loc_cntNoCollection).isEmpty(), "[Failed] Can not found any product collection.");
        }
        logger.info("Check permission: View collection list.");
    }

    void checkEditProduct(List<Integer> manualCollectionIds) {
        if (permissions.getProduct().getProductManagement().isEditProduct()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave, loc_dlgNotification), "[Failed] Can not update product.");

            // close Notification
            commonAction.closePopup(loc_dlgNotification_btnClose);

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
            checkViewCollectionList(manualCollectionIds, permissions);

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
        logger.info("Check permission: Edit product.");
    }

    void checkUpdateStock() {
        if (!permissions.getProduct().getInventory().isUpdateStock()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productInfo.isHasModel() ? loc_tblVariation_txtStock : loc_txtWithoutVariationBranchStock, 0), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Update stock.");
    }

    void checkEditPrice() {
        if (!permissions.getProduct().getProductManagement().isEditPrice()) {
            if (productInfo.isHasModel()) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_txtListingPrice_0), "Restricted popup does not shown.");
            } else {
                commonAction.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(MAX_PRICE));
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted popup does not shown.");
            }
        }
        logger.info("Check permission: Edit price.");
    }

    void checkEnableProductLot() {
        if (!permissions.getProduct().getLotDate().isEnableProductLot()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_chkManageStockByLotDate), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Enable product.");
    }

    void checkDeleteProduct() {
        if (permissions.getProduct().getProductManagement().isDeleteProduct()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnDelete, loc_dlgConfirm), "Confirm delete product popup does not shown.");
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDelete), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Delete product.");
    }

    void checkAddVariation() {
        if (!permissions.getProduct().getProductManagement().isAddVariation()) {
            // add new variation group
            if (!commonAction.getListElement(loc_btnAddVariation).isEmpty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnAddVariation), "Restricted popup does not shown.");
            }

            // add variation value
            if (!commonAction.getListElement(loc_txtVariationValue).isEmpty()) {
                commonAction.getElement(loc_txtVariationValue, 0).sendKeys(epoch);
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lblVariations), "Restricted popup does not shown.");
            }
        }
        logger.info("Check permission: Add variation.");
    }

    void checkDeleteVariation() {
        if (!permissions.getProduct().getProductManagement().isDeleteVariation()) {
            if (!commonAction.getListElement(loc_btnDeleteVariation).isEmpty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeleteVariation), "Restricted popup does not shown.");
            }
        }
        logger.info("Check permission: Delete variation.");
    }

    void checkActiveProduct() {
        if (!permissions.getProduct().getProductManagement().isActivateProduct() && productInfo.getBhStatus().equals("INACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup does not shown.");
            if (productInfo.isHasModel()) {
                new VariationDetailPage(driver, productInfo.getVariationModelList().get(0), productInfo, loginInformation).checkActiveVariation(permissions, checkPermission);
            }
        }
        logger.info("Check permission: Activate product.");
    }

    void checkDeactivateProduct() {
        if (!permissions.getProduct().getProductManagement().isActivateProduct() && productInfo.getBhStatus().equals("ACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup does not shown.");
            if (productInfo.isHasModel()) {
                new VariationDetailPage(driver, productInfo.getVariationModelList().get(0), productInfo, loginInformation).checkDeactivateVariation(permissions, checkPermission);
            }
        }

        logger.info("Check permission: Deactivate product.");
    }

    void checkUpdateWholesalePrice() {
        if (!permissions.getProduct().getProductManagement().isUpdateWholesalePrice()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_chkAddWholesalePricing), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Update wholesale price.");
    }

    void checkEditTax() {
        if (!permissions.getProduct().getProductManagement().isEditTax() && permissions.getSetting().getTAX().isViewTAXList()) {
            selectVAT();
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Edit Tax.");
    }

    void checkViewCostPrice() {
        if (permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
            assertCustomize.assertTrue((productInfo.isHasModel() ? commonAction.getValue(loc_txtCostPrice_0) : commonAction.getValue(loc_txtWithoutVariationCostPrice)).equals("0"), "Product cost price still shows when staff does not have 'View product cost price' permission.");
        }
        logger.info("Check permission: View cost price.");
    }

    void checkUpdateSEOData() {
        if (!permissions.getProduct().getProductManagement().isEditSEOData()) {
            inputSEO();
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Update SEO data.");
    }

    void checkUpdateTranslation() {
        if (!permissions.getProduct().getProductManagement().isEditTranslation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lblEditTranslation), "Restricted popup does not shown.");
            if (productInfo.isHasModel()) {
                new VariationDetailPage(driver, productInfo.getVariationModelList().get(0), productInfo, loginInformation).checkEditTranslation(permissions, checkPermission);
            }
        }
        logger.info("Check permission: Update translation.");
    }
}
