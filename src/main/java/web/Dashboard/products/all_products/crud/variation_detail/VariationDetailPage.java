package web.Dashboard.products.all_products.crud.variation_detail;

import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.all_products.crud.ProductPage;
import utilities.commons.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static utilities.utils.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.links.Links.DOMAIN;

public class VariationDetailPage extends VariationDetailElement {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    String modelId;
    String variation;
    String defaultLanguage;
    Logger logger = LogManager.getLogger(VariationDetailPage.class);
    StoreInfo storeInfo;
    LoginInformation loginInformation;
    ProductInfo productInfo;
    String uiLanguage;
    AssertCustomize assertCustomize;

    public VariationDetailPage(WebDriver driver, String modelId, ProductInfo productInfo, LoginInformation loginInformation) {
        this.modelId = modelId;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        this.loginInformation = loginInformation;
        this.productInfo = productInfo;
        storeInfo = new StoreInformation(loginInformation).getInfo();
        uiLanguage = ProductPage.getLanguage();
        this.driver = driver;
        variation = productInfo.getVariationListMap()
                .get(storeInfo.getDefaultLanguage())
                .get(productInfo.getBarcodeList().indexOf(modelId));
        assertCustomize = ProductPage.getAssertCustomize();
        defaultLanguage = storeInfo.getDefaultLanguage();
    }

    void navigateToVariationDetailPage() {
        driver.get("%s/product/%s/variation-detail/%s/edit".formatted(DOMAIN, modelId.split("-")[0], modelId.split("-")[1]));
        logger.info("Navigate to variation detail page, barcode: %s.".formatted(modelId));
    }

    void updateVariationProductName() {
        String name = "[Update][%s][%s] product version name".formatted(defaultLanguage, variation);
        commonAction.click(loc_txtProductVersionName);
        commonAction.sendKeys(loc_txtProductVersionName, name);
        logger.info("[%s] Update product version name: %s.".formatted(variation, name));
    }

    void updateVariationProductDescription() {
        boolean reuseDescription = nextBoolean();
        if (commonAction.isCheckedJS(loc_chkReuse) != reuseDescription)
            commonAction.clickJS(loc_chkReuse);

        if (!reuseDescription) {
            String description = "[Update][%s][%s] Product description".formatted(defaultLanguage, variation);
            commonAction.sendKeys(loc_rtfDescription, description);
            logger.info("[%s] Update product description: %s.".formatted(variation, description));
        }

    }

    void completeUpdateProductVersionNameAndDescription() {
        commonAction.click(loc_btnSave);
        logger.info("[%s] Update successfully.".formatted(variation));
    }

    void updateVariationTranslation(String languageCode, String languageName, int langIndex) throws Exception {
        commonAction.click(loc_btnEditTranslation);
        logger.info("[%s] Open edit translation popup.".formatted(variation));

        try {
            commonAction.getElement(loc_dlgEditTranslation);
            logger.info("[%s] Wait edit translation popup presented.".formatted(variation));
        } catch (TimeoutException ex) {
            commonAction.click(loc_btnEditTranslation);
            logger.info("[%s] Open edit translation popup again.".formatted(variation));
            commonAction.getElement(loc_dlgEditTranslation);
            logger.info("[%s] Wait edit translation popup visible again.".formatted(variation));
        }

        // convert languageCode to languageName
        if (languageCode.equals("en") && (uiLanguage.equals("vi") || uiLanguage.equals("VIE")))
            languageName = "Tiếng Anh";

        // select language for translation
        if (!commonAction.getText(loc_dlgEditTranslation_selectedLanguage).equals(languageName)) {
            // open language dropdown
            commonAction.click(loc_dlgEditTranslation_selectedLanguage);

            // select language
            commonAction.click(By.xpath(str_dlgEditTranslation_languageInDropdown.formatted(languageName)));
        }
        logger.info("[%s] Select language for translation: %s.".formatted(variation, languageName));

        // check [UI] Edit translation popup
        if (langIndex == 0) checkUIEditTranslationPopup(uiLanguage);

        // add translation for variation name
        String name = "[Update][%s][%s] Product version name".formatted(languageCode, variation);
        commonAction.sendKeys(loc_dlgEditTranslation_variationName, name);
        logger.info("[%s] Edit translation for product version name: %s.".formatted(variation, name));

        // add translation for variation description
        String description = "[Update][%s][%s] Product description".formatted(languageCode, variation);
        commonAction.sendKeys(loc_dlgEditTranslation_variationDescription, description);
        logger.info("[%s] Edit translation for product description: %s.".formatted(variation, description));

        commonAction.click(loc_dlgEditTranslation_btnSave);
        logger.info("[%s] Add translation successfully.".formatted(variation));

        // close edit translation popup
        commonAction.click(loc_dlgEditTranslation_btnClose);
        logger.info("[%s] Close Edit translation popup.".formatted(variation));
    }

    public void updateVariationProductNameAndDescription(String status) throws Exception {
        navigateToVariationDetailPage();
        checkUIVariationDetailPage(uiLanguage, status);
        updateVariationProductName();
        updateVariationProductDescription();
        completeUpdateProductVersionNameAndDescription();
        List<String> langCodeList = new ArrayList<>(storeInfo.getStoreLanguageList());
        List<String> langNameList = new ArrayList<>(storeInfo.getStoreLanguageName());
        langCodeList.remove(storeInfo.getDefaultLanguage());

        for (int langIndex = 0; langIndex < langCodeList.size(); langIndex++) {
            updateVariationTranslation(langCodeList.get(langIndex), langNameList.get(storeInfo.getStoreLanguageList().indexOf(langCodeList.get(langIndex))), langIndex);
        }
    }

    public void changeVariationStatus(String status) {
        navigateToVariationDetailPage();
        if (!productInfo.getVariationStatus().get(productInfo.getBarcodeList().indexOf(modelId)).equals(status))
            commonAction.clickJS(loc_btnDeactivate);
        logger.info("[%s] Update status successfully.".formatted(variation));
    }

    // check UI
    void checkUIVariationDetailPage(String language, String status) throws Exception {
        // check variation status
        String dbVariationStatus = commonAction.getText(loc_lblVariationStatus);
        String ppVariationStatus = status.equals("ACTIVE")
                ? getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.variationStatus.active", language)
                : getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.variationStatus.deactivate", language);
        assertCustomize.assertEquals(dbVariationStatus, ppVariationStatus, "[Failed][Header] Variation status should be %s, but found %s.".formatted(ppVariationStatus, dbVariationStatus));
        logger.info("[UI][%s] Check Header - Variation status.".formatted(language));

        // check header Edit translation button
        String dbEditTranslationBtn = commonAction.getText(loc_btnEditTranslation);
        String ppEditTranslationBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.editTranslationBtn", language);
        assertCustomize.assertEquals(dbEditTranslationBtn, ppEditTranslationBtn, "[Failed][Header] Edit translation button should be %s, but found %s.".formatted(ppEditTranslationBtn, dbEditTranslationBtn));
        logger.info("[UI][%s] Check Header - Edit translation button.".formatted(language));

        // check header Save button
        String dbSaveBtn = commonAction.getText(loc_lblSave);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.saveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check header Cancel button
        String dbCancelBtn = commonAction.getText(loc_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));

        // check header Active/Deactivate button
        String dbDeactivateBtn = commonAction.getText(loc_lblDeactivate);
        String ppDeactivateBtn = status.equals("ACTIVE")
                ? getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.deactivateBtn", language)
                : getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.activeBtn", language);
        assertCustomize.assertEquals(dbDeactivateBtn, ppDeactivateBtn, "[Failed][Header] Active/Deactivate button should be %s, but found %s.".formatted(ppDeactivateBtn, dbDeactivateBtn));
        logger.info("[UI][%s] Check Header - Active/Deactivate button.".formatted(language));

        // check product version
        String dbProductVersion = commonAction.getText(loc_lblProductVersion);
        String ppProductVersion = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.productVersion", language);
        assertCustomize.assertEquals(dbProductVersion, ppProductVersion, "[Failed][Body] Product version should be %s, but found %s.".formatted(ppProductVersion, dbProductVersion));
        logger.info("[UI][%s] Check Body - Product version.".formatted(language));

        // check product version name
        String dbProductVersionName = commonAction.getText(loc_lblProductVersionName);
        String ppProductVersionName = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.productVersionName", language);
        assertCustomize.assertEquals(dbProductVersionName, ppProductVersionName, "[Failed][Body] Product version name should be %s, but found %s.".formatted(ppProductVersionName, dbProductVersionName));
        logger.info("[UI][%s] Check Body - Product version name.".formatted(language));

        // check description
        String dbDescription = commonAction.getText(loc_lblDescription);
        String ppDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.description", language);
        assertCustomize.assertEquals(dbDescription, ppDescription, "[Failed][Body] Description should be %s, but found %s.".formatted(ppDescription, dbDescription));
        logger.info("[UI][%s] Check Body - Description.".formatted(language));

        // check reuse description
        String dbReuseDescription = commonAction.getText(loc_lblReuseDescription);
        String ppReuseDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.reuseDescription", language);
        assertCustomize.assertEquals(dbReuseDescription, ppReuseDescription, "[Failed][Body] Reuse description should be %s, but found %s.".formatted(ppReuseDescription, dbReuseDescription));
        logger.info("[UI][%s] Check Body - Reuse description.".formatted(language));

        // check images
        String dbImages = commonAction.getText(loc_lblImages);
        String ppImages = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.images", language);
        assertCustomize.assertEquals(dbImages, ppImages, "[Failed][Body] Images should be %s, but found %s.".formatted(ppImages, dbImages));
        logger.info("[UI][%s] Check Body - Images.".formatted(language));

        // check pricing
        String dbPricing = commonAction.getText(loc_lblPricing);
        String ppPricing = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.pricing", language);
        assertCustomize.assertEquals(dbPricing, ppPricing, "[Failed][Body] Pricing should be %s, but found %s.".formatted(ppPricing, dbPricing));
        logger.info("[UI][%s] Check Body - Pricing.".formatted(language));

        // check listing price
        String dbListingPrice = commonAction.getText(loc_lblListingPrice);
        String ppListingPrice = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.listingPrice", language);
        assertCustomize.assertEquals(dbListingPrice, ppListingPrice, "[Failed][Body] Listing price should be %s, but found %s.".formatted(ppListingPrice, dbListingPrice));
        logger.info("[UI][%s] Check Body - Listing price.".formatted(language));

        // check selling price
        String dbSellingPrice = commonAction.getText(loc_lblSellingPrice);
        String ppSellingPrice = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.sellingPrice", language);
        assertCustomize.assertEquals(dbSellingPrice, ppSellingPrice, "[Failed][Body] Selling price should be %s, but found %s.".formatted(ppSellingPrice, dbSellingPrice));
        logger.info("[UI][%s] Check Body - Selling price.".formatted(language));

        // check number of variations
        String dbNumOfVariations = commonAction.getText(loc_lblNumberOfVariations).split(" ")[1];
        String ppNumOfVariations = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.numOfVariations", language).split(" ")[1];
        assertCustomize.assertEquals(dbNumOfVariations, ppNumOfVariations, "[Failed][Body] Number of variations should be %s, but found %s.".formatted(ppNumOfVariations, dbNumOfVariations));
        logger.info("[UI][%s] Check Body - Number of variations.".formatted(language));

        // check branch
        String dbBranch = commonAction.getText(loc_lblBranch);
        String ppBranch = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.branch", language);
        assertCustomize.assertEquals(dbBranch, ppBranch, "[Failed][Body] Branch should be %s, but found %s.".formatted(ppBranch, dbBranch));
        logger.info("[UI][%s] Check Body - Branch.".formatted(language));

        // check variation
        String dbVariation = commonAction.getText(loc_lblVariation);
        String ppVariation = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.variation", language);
        assertCustomize.assertEquals(dbVariation, ppVariation, "[Failed][Body] Variation should be %s, but found %s.".formatted(ppVariation, dbVariation));
        logger.info("[UI][%s] Check Body - Variation.".formatted(language));

        // check warehousing
        String dbWarehousing = commonAction.getText(loc_lblWarehousing);
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.warehousing", language);
        assertCustomize.assertEquals(dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing.".formatted(language));

        // check update stock
        String dbUpdateStock = commonAction.getText(loc_lblUpdateStock);
        String ppUpdateStock = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.updateStock", language);
        assertCustomize.assertEquals(dbUpdateStock, ppUpdateStock, "[Failed][Body] Update stock should be %s, but found %s.".formatted(ppUpdateStock, dbUpdateStock));
        logger.info("[UI][%s] Check Body - Update stock.".formatted(language));

        // check sku
        String dbSKU = commonAction.getText(loc_lblSKU);
        String ppSKU = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.sku", language);
        assertCustomize.assertEquals(dbSKU, ppSKU, "[Failed][Body] SKU should be %s, but found %s.".formatted(ppSKU, dbSKU));
        logger.info("[UI][%s] Check Body - SKU.".formatted(language));

        // check barcode
        String dbBarcode = commonAction.getText(loc_lblBarcode);
        String ppBarcode = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.barcode", language);
        assertCustomize.assertEquals(dbBarcode, ppBarcode, "[Failed][Body] Barcode should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
        logger.info("[UI][%s] Check Body - Barcode.".formatted(language));

        // check remaining stock
        String dbRemainingStock = commonAction.getText(loc_lblRemainingStock);
        String ppRemainingStock = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.remainingStock", language);
        assertCustomize.assertEquals(dbRemainingStock, ppRemainingStock, "[Failed][Body] Remaining stock should be %s, but found %s.".formatted(ppRemainingStock, dbRemainingStock));
        logger.info("[UI][%s] Check Body - Remaining stock.".formatted(language));

        // check sold count
        String dbSoldCount = commonAction.getText(loc_lblSoldCount);
        String ppSoldCount = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.soldCount", language);
        assertCustomize.assertEquals(dbSoldCount, ppSoldCount, "[Failed][Body] Sold count should be %s, but found %s.".formatted(ppSoldCount, dbSoldCount));
        logger.info("[UI][%s] Check Body - Sold count.".formatted(language));
    }

    void checkUIEditTranslationPopup(String language) throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_dlgEditTranslationTitle);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Edit translation popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Edit translation popup - Title.".formatted(language));

        // check information
        String dbInformation = commonAction.getText(loc_dlgEditTranslation_lblInformation);
        String ppInformation = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.information", language);
        assertCustomize.assertEquals(dbInformation, ppInformation, "[Failed][Edit translation popup] Information should be %s, but found %s.".formatted(ppInformation, dbInformation));
        logger.info("[UI][%s] Check Edit translation popup - Information.".formatted(language));

        // check product name
        String dbName = commonAction.getText(loc_dlgEditTranslation_lblName);
        String ppName = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.name", language);
        assertCustomize.assertEquals(dbName, ppName, "[Failed][Edit translation popup] Name should be %s, but found %s.".formatted(ppName, dbName));
        logger.info("[UI][%s] Check Edit translation popup - Name.".formatted(language));


        // check description
        String dbDescription = commonAction.getText(loc_dlgEditTranslation_lblDescription);
        String ppDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.description", language);
        assertCustomize.assertEquals(dbDescription, ppDescription, "[Failed][Edit translation popup] Description should be %s, but found %s.".formatted(ppDescription, dbDescription));
        logger.info("[UI][%s] Check Edit translation popup - Description.".formatted(language));

        // check save button
        String dbSaveBtn = commonAction.getText(loc_dlgEditTranslation_lblSave);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.saveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Edit translation popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Edit translation popup - Save button.".formatted(language));

        String dbCancelBtn = commonAction.getText(loc_dlgEditTranslation_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Edit translation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Edit translation popup - Cancel button.".formatted(language));
    }

    // permission
    public void checkActiveVariation(AllPermissions permissions, CheckPermission checkPermission) {
        navigateToVariationDetailPage();
        if (!permissions.getProduct().getProductManagement().isActivateProduct() && productInfo.getVariationStatus().get(0).equals("INACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Activate product.");
    }

    public void checkDeactivateVariation(AllPermissions permissions, CheckPermission checkPermission) {
        navigateToVariationDetailPage();
        if (!permissions.getProduct().getProductManagement().isDeactivateProduct() && productInfo.getVariationStatus().get(0).equals("ACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Deactivate product.");
    }

    public void checkEditTranslation(AllPermissions permissions, CheckPermission checkPermission) {
        navigateToVariationDetailPage();
        if (!permissions.getProduct().getProductManagement().isEditTranslation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnEditTranslation), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update product translation.");
    }
}