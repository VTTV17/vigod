package pages.dashboard.products.all_products.variation_detail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static api.dashboard.products.ProductInformation.*;
import static api.dashboard.setting.StoreInformation.apiDefaultLanguage;
import static api.dashboard.setting.StoreInformation.apiStoreLanguageList;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static pages.dashboard.products.all_products.ProductPage.countFail;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.links.Links.DOMAIN;

public class VariationDetailPage extends VariationDetailElement {
    WebDriverWait wait;
    UICommonAction commonAction;
    String barcode;
    Logger logger = LogManager.getLogger(VariationDetailPage.class);

    public VariationDetailPage(WebDriver driver, String barcode) {
        super(driver);
        this.barcode = barcode;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    void navigateToVariationDetailPage() {
        driver.get("%s/product/%s/variation-detail/%s/edit".formatted(DOMAIN, barcode.split("-")[0], barcode.split("-")[1]));
        new Actions(driver).keyDown(Keys.CONTROL).keyDown(Keys.SHIFT).sendKeys("r").keyUp(Keys.CONTROL).keyUp(Keys.SHIFT).build().perform();
        logger.info("Navigate to variation detail page, barcode: %s.".formatted(barcode));
        commonAction.sleepInMiliSecond(3000);
        commonAction.verifyPageLoaded("Mẫu Mã Sản Phẩm", "Product Version");
    }

    void updateVariationProductName() {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_VERSION_NAME));
        PRODUCT_VERSION_NAME.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        PRODUCT_VERSION_NAME.sendKeys("[Update][%s][%s] product version name".formatted(apiDefaultLanguage, variationListMap.get(apiDefaultLanguage).get(barcodeList.indexOf(barcode))));
        logger.info("Update product version name.");
    }

    void updateVariationProductDescription() {
        boolean reuseDescription = false;
        if (!(boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", REUSE_DESCRIPTION_CHECKBOX) == reuseDescription)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", REUSE_DESCRIPTION_CHECKBOX);

        if (!reuseDescription) {
            wait.until(ExpectedConditions.elementToBeClickable(DESCRIPTION));
            DESCRIPTION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
            DESCRIPTION.sendKeys("[Update][%s][%s] Product description".formatted(apiDefaultLanguage, variationListMap.get(apiDefaultLanguage).get(barcodeList.indexOf(barcode))));
        }
        logger.info("Update product description.");
    }

    void completeUpdateProductVersionNameAndDescription() {
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        logger.info("Complete update variation version name and description.");

        commonAction.sleepInMiliSecond(5000);
    }

    void updateVariationTranslation(String language) {
        wait.until(ExpectedConditions.elementToBeClickable(EDIT_TRANSLATION_BTN)).click();
        logger.info("Open edit translation popup.");

        wait.until(visibilityOf(POPUP));

        wait.until(ExpectedConditions.elementToBeClickable(EDIT_TRANSLATION_POPUP_PRODUCT_NAME));
        EDIT_TRANSLATION_POPUP_PRODUCT_NAME.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        EDIT_TRANSLATION_POPUP_PRODUCT_NAME.sendKeys("[Update][%s][%s] Product version name".formatted(language, variationListMap.get(language).get(barcodeList.indexOf(barcode))));
        logger.info("Edit translation for product version name.");

        wait.until(ExpectedConditions.elementToBeClickable(EDIT_TRANSLATION_POPUP_DESCRIPTION));
        EDIT_TRANSLATION_POPUP_DESCRIPTION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        EDIT_TRANSLATION_POPUP_DESCRIPTION.sendKeys("[Update][%s][%s] Product description".formatted(language, variationListMap.get(language).get(barcodeList.indexOf(barcode))));
        logger.info("Edit translation for product description.");

        commonAction.sleepInMiliSecond(1000);
        wait.until(ExpectedConditions.elementToBeClickable(EDIT_TRANSLATION_POPUP_SAVE_BTN)).click();
        logger.info("Complete edit translation for variation.");
    }

    public void updateVariationProductNameAndDescription(String language, String status) throws Exception {
        navigateToVariationDetailPage();
        checkUIVariationDetailPage(language, status);
        updateVariationProductName();
        updateVariationProductDescription();
        completeUpdateProductVersionNameAndDescription();
        List<String> langList = new ArrayList<>(apiStoreLanguageList);
        langList.remove(apiDefaultLanguage);
        langList.forEach(this::updateVariationTranslation);
    }

    public void changeVariationStatus(String status) {
        navigateToVariationDetailPage();
        if (!status.equals(variationStatus.get(barcodeList.indexOf(barcode))))
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", DEACTIVATE_BTN);
        logger.info("Update variation status.");
    }

    // check UI
    void checkUIVariationDetailPage(String language, String status) throws Exception {
        // check variation status
        String dbVariationStatus = wait.until(visibilityOf(UI_HEADER_VARIATION_STATUS)).getText();
        String ppVariationStatus = status.equals("ACTIVE") ? getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.variationStatus.active", language) : getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.variationStatus.deactivate", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariationStatus, ppVariationStatus, "[Failed][Header] Variation status should be %s, but found %s.".formatted(ppVariationStatus, dbVariationStatus));
        logger.info("[UI][%s] Check Header - Variation status.".formatted(language));

        // check header Edit translation button
        String dbEditTranslationBtn = wait.until(visibilityOf(UI_HEADER_EDIT_TRANSLATION_BTN)).getText();
        String ppEditTranslationBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.editTranslationBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbEditTranslationBtn, ppEditTranslationBtn, "[Failed][Header] Edit translation button should be %s, but found %s.".formatted(ppEditTranslationBtn, dbEditTranslationBtn));
        logger.info("[UI][%s] Check Header - Edit translation button.".formatted(language));

        // check header Save button
        String dbSaveBtn = wait.until(visibilityOf(UI_HEADER_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check header Cancel button
        String dbCancelBtn = wait.until(visibilityOf(UI_HEADER_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));

        // check header Active/Deactivate button
        String dbDeactivateBtn = wait.until(visibilityOf(UI_HEADER_DEACTIVATE_BTN)).getText();
        String ppDeactivateBtn = status.equals("ACTIVE") ? getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.deactivateBtn", language) : getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.header.activeBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDeactivateBtn, ppDeactivateBtn, "[Failed][Header] Active/Deactivate button should be %s, but found %s.".formatted(ppDeactivateBtn, dbDeactivateBtn));
        logger.info("[UI][%s] Check Header - Active/Deactivate button.".formatted(language));

        // check product version
        String dbProductVersion = wait.until(visibilityOf(UI_PRODUCT_VERSION)).getText();
        String ppProductVersion = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.productVersion", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductVersion, ppProductVersion, "[Failed][Body] Product version should be %s, but found %s.".formatted(ppProductVersion, dbProductVersion));
        logger.info("[UI][%s] Check Body - Product version.".formatted(language));

        // check product version name
        String dbProductVersionName = wait.until(visibilityOf(UI_PRODUCT_VERSION_NAME)).getText();
        String ppProductVersionName = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.productVersionName", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbProductVersionName, ppProductVersionName, "[Failed][Body] Product version name should be %s, but found %s.".formatted(ppProductVersionName, dbProductVersionName));
        logger.info("[UI][%s] Check Body - Product version name.".formatted(language));

        // check description
        String dbDescription = wait.until(visibilityOf(UI_DESCRIPTION)).getText();
        String ppDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.description", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDescription, ppDescription, "[Failed][Body] Description should be %s, but found %s.".formatted(ppDescription, dbDescription));
        logger.info("[UI][%s] Check Body - Description.".formatted(language));

        // check reuse description
        String dbReuseDescription = wait.until(visibilityOf(UI_REUSE_DESCRIPTION)).getText();
        String ppReuseDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.reuseDescription", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbReuseDescription, ppReuseDescription, "[Failed][Body] Reuse description should be %s, but found %s.".formatted(ppReuseDescription, dbReuseDescription));
        logger.info("[UI][%s] Check Body - Reuse description.".formatted(language));

        // check images
        String dbImages = wait.until(visibilityOf(UI_IMAGES)).getText();
        String ppImages = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.images", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbImages, ppImages, "[Failed][Body] Images should be %s, but found %s.".formatted(ppImages, dbImages));
        logger.info("[UI][%s] Check Body - Images.".formatted(language));

        // check pricing
        String dbPricing = wait.until(visibilityOf(UI_PRICING)).getText();
        String ppPricing = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.pricing", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPricing, ppPricing, "[Failed][Body] Pricing should be %s, but found %s.".formatted(ppPricing, dbPricing));
        logger.info("[UI][%s] Check Body - Pricing.".formatted(language));

        // check listing price
        String dbListingPrice = wait.until(visibilityOf(UI_LISTING_PRICE)).getText();
        String ppListingPrice = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.listingPrice", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbListingPrice, ppListingPrice, "[Failed][Body] Listing price should be %s, but found %s.".formatted(ppListingPrice, dbListingPrice));
        logger.info("[UI][%s] Check Body - Listing price.".formatted(language));

        // check selling price
        String dbSellingPrice = wait.until(visibilityOf(UI_SELLING_PRICE)).getText();
        String ppSellingPrice = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.sellingPrice", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSellingPrice, ppSellingPrice, "[Failed][Body] Selling price should be %s, but found %s.".formatted(ppSellingPrice, dbSellingPrice));
        logger.info("[UI][%s] Check Body - Selling price.".formatted(language));

        // check number of variations
        String dbNumOfVariations = wait.until(visibilityOf(UI_NUM_OF_VARIATIONS)).getText().split(" ")[1];
        String ppNumOfVariations = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.numOfVariations", language).split(" ")[1];
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNumOfVariations, ppNumOfVariations, "[Failed][Body] Number of variations should be %s, but found %s.".formatted(ppNumOfVariations, dbNumOfVariations));
        logger.info("[UI][%s] Check Body - Number of variations.".formatted(language));

        // check branch
        String dbBranch = wait.until(visibilityOf(UI_BRANCH)).getText();
        String ppBranch = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.branch", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbBranch, ppBranch, "[Failed][Body] Branch should be %s, but found %s.".formatted(ppBranch, dbBranch));
        logger.info("[UI][%s] Check Body - Branch.".formatted(language));

        // check variation
        String dbVariation = wait.until(visibilityOf(UI_VARIATION)).getText();
        String ppVariation = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.variation", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbVariation, ppVariation, "[Failed][Body] Variation should be %s, but found %s.".formatted(ppVariation, dbVariation));
        logger.info("[UI][%s] Check Body - Variation.".formatted(language));

        // check warehousing
        String dbWarehousing = wait.until(visibilityOf(UI_WAREHOUSING)).getText();
        String ppWarehousing = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.warehousing", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWarehousing, ppWarehousing, "[Failed][Body] Warehousing should be %s, but found %s.".formatted(ppWarehousing, dbWarehousing));
        logger.info("[UI][%s] Check Body - Warehousing.".formatted(language));

        // check update stock
        String dbUpdateStock = wait.until(visibilityOf(UI_UPDATE_STOCK)).getText();
        String ppUpdateStock = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.updateStock", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbUpdateStock, ppUpdateStock, "[Failed][Body] Update stock should be %s, but found %s.".formatted(ppUpdateStock, dbUpdateStock));
        logger.info("[UI][%s] Check Body - Update stock.".formatted(language));

        // check sku
        String dbSKU = wait.until(visibilityOf(UI_SKU)).getText();
        String ppSKU = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.sku", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSKU, ppSKU, "[Failed][Body] SKU should be %s, but found %s.".formatted(ppSKU, dbSKU));
        logger.info("[UI][%s] Check Body - SKU.".formatted(language));

        // check barcode
        String dbBarcode = wait.until(visibilityOf(UI_BARCODE)).getText();
        String ppBarcode = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.barcode", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbBarcode, ppBarcode, "[Failed][Body] Barcode should be %s, but found %s.".formatted(ppBarcode, dbBarcode));
        logger.info("[UI][%s] Check Body - Barcode.".formatted(language));

        // check remaining stock
        String dbRemainingStock = wait.until(visibilityOf(UI_REMAINING_STOCK)).getText();
        String ppRemainingStock = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.remainingStock", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbRemainingStock, ppRemainingStock, "[Failed][Body] Remaining stock should be %s, but found %s.".formatted(ppRemainingStock, dbRemainingStock));
        logger.info("[UI][%s] Check Body - Remaining stock.".formatted(language));

        // check sold count
        String dbSoldCount = wait.until(visibilityOf(UI_SOLD_COUNT)).getText();
        String ppSoldCount = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.soldCount", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSoldCount, ppSoldCount, "[Failed][Body] Sold count should be %s, but found %s.".formatted(ppSoldCount, dbSoldCount));
        logger.info("[UI][%s] Check Body - Sold count.".formatted(language));
    }

    void checkUIEditTranslationPopup(String language) throws Exception {
        // check title
        String dbTitle = wait.until(visibilityOf(UI_EDIT_TRANSLATION_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Edit translation popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Edit translation popup - Title.".formatted(language));
        
        // check information
        String dbInformation = wait.until(visibilityOf(UI_EDIT_TRANSLATION_POPUP_INFORMATION)).getText();
        String ppInformation = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.information", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbInformation, ppInformation, "[Failed][Edit translation popup] Information should be %s, but found %s.".formatted(ppInformation, dbInformation));
        logger.info("[UI][%s] Check Edit translation popup - Information.".formatted(language));

        // check product name
        String dbName = wait.until(visibilityOf(UI_EDIT_TRANSLATION_POPUP_NAME)).getText();
        String ppName = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.name", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbName, ppName, "[Failed][Edit translation popup] Name should be %s, but found %s.".formatted(ppName, dbName));
        logger.info("[UI][%s] Check Edit translation popup - Name.".formatted(language));


        // check description
        String dbDescription = wait.until(visibilityOf(UI_EDIT_TRANSLATION_POPUP_DESCRIPTION)).getText();
        String ppDescription = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.description", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDescription, ppDescription, "[Failed][Edit translation popup] Description should be %s, but found %s.".formatted(ppDescription, dbDescription));
        logger.info("[UI][%s] Check Edit translation popup - Description.".formatted(language));

        // check save button
        String dbSaveBtn = wait.until(visibilityOf(UI_EDIT_TRANSLATION_POPUP_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Edit translation popup] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Edit translation popup - Save button.".formatted(language));
        
        String dbCancelBtn = wait.until(visibilityOf(UI_EDIT_TRANSLATION_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.updateProduct.variationDetail.editTranslationPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Edit translation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Edit translation popup - Cancel button.".formatted(language));
    }
}