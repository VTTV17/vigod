package web.Dashboard.products.all_products.crud.variation_detail;

import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.all_products.crud.ProductPage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;

public class VariationDetailPage extends VariationDetailElement {
    WebDriver driver;
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
        commonAction = new UICommonAction(driver);
        this.loginInformation = loginInformation;
        this.productInfo = productInfo;
        storeInfo = new StoreInformation(loginInformation).getInfo();
        uiLanguage = ProductPage.getLanguage();
        this.driver = driver;
        variation = productInfo.getVariationValuesMap()
                .get(storeInfo.getDefaultLanguage())
                .get(productInfo.getBarcodeList().indexOf(modelId));
        assertCustomize = new AssertCustomize(driver);
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

    void updateVariationTranslation(String languageCode, String languageName) {
        String variation = productInfo.getVariationValuesMap()
                .get(languageCode)
                .get(productInfo.getBarcodeList().indexOf(modelId));
        if (!commonAction.getListElement(loc_dlgEditTranslation).isEmpty()) {
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

            // add translation for variation name
            String name = "[Update][%s][%s] Product version name".formatted(languageCode, variation);
            commonAction.sendKeys(loc_dlgEditTranslation_variationName, name);
            logger.info("[%s] Edit translation for product version name: %s.".formatted(variation, name));

            // add translation for variation description
            String description = "[Update][%s][%s] Product description".formatted(languageCode, variation);
            commonAction.sendKeys(loc_dlgEditTranslation_variationDescription, description);
            logger.info("[%s] Edit translation for product description: %s.".formatted(variation, description));

            commonAction.openPopupJS(loc_dlgEditTranslation_btnSave, loc_dlgToastSuccess);
            logger.info("[%s] Add translation successfully.".formatted(variation));
        }
    }

    public void updateVariationProductNameAndDescription() {
        navigateToVariationDetailPage();
        updateVariationProductName();
        updateVariationProductDescription();
        completeUpdateProductVersionNameAndDescription();
        List<String> langCodeList = new ArrayList<>(storeInfo.getStoreLanguageList());
        List<String> langNameList = new ArrayList<>(storeInfo.getStoreLanguageName());
        langCodeList.remove(storeInfo.getDefaultLanguage());

        // open edit translation popup
        commonAction.click(loc_btnEditTranslation);
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgEditTranslation).isEmpty(),
                "Can not open edit translation popup.");

        langCodeList.forEach(languageCode -> updateVariationTranslation(languageCode, langNameList.get(storeInfo.getStoreLanguageList().indexOf(languageCode))));
    }

    public void changeVariationStatus(String status) {
        navigateToVariationDetailPage();
        if (!productInfo.getVariationStatus().get(productInfo.getBarcodeList().indexOf(modelId)).equals(status))
            commonAction.clickJS(loc_btnDeactivate);
        logger.info("[%s] Update status successfully.".formatted(variation));
    }


    public void updateAttribution() {
        navigateToVariationDetailPage();

        boolean isUseParentAttribution = nextBoolean();

        if (!Objects.equals(commonAction.isCheckedJS(loc_chkReUseParentAttribution), isUseParentAttribution)) {
            commonAction.clickJS(loc_chkReUseParentAttribution);
        }

        if (!isUseParentAttribution) {
            // remove old attribution
            if (!commonAction.getListElement(loc_icnDeleteAttribution).isEmpty()) {
                int bound = commonAction.getListElement(loc_icnDeleteAttribution).size();
                IntStream.iterate(bound - 1, index -> index >= 0, index -> index - 1).forEach(index -> commonAction.clickJS(loc_icnDeleteAttribution, index));
            }

            int numOfAttribute = nextInt(10);
            // add attribution
            IntStream.range(0, numOfAttribute)
                    .forEachOrdered(index -> commonAction.clickJS(loc_lnkAddAttribution));

            // input attribution
            long epoch = Instant.now().toEpochMilli();
            IntStream.range(0, numOfAttribute).forEach(attIndex -> {
                commonAction.sendKeys(loc_txtAttributionName, attIndex, "name_%s_%s".formatted(attIndex, epoch));
                commonAction.sendKeys(loc_txtAttributionValue, attIndex, "value_%s_%s".formatted(attIndex, epoch));
                if (!Objects.equals(commonAction.isCheckedJS(loc_chkDisplayAttribution, attIndex), nextBoolean())) {
                    commonAction.clickJS(loc_chkDisplayAttribution);
                }
            });

            // save changes
            commonAction.click(loc_btnSave);
        }
    }

    // permission
    public void checkActiveVariation(AllPermissions permissions, CheckPermission checkPermission) {
        navigateToVariationDetailPage();
        if (!permissions.getProduct().getProductManagement().isActivateProduct() && productInfo.getVariationStatus().get(0).equals("INACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Activate product.");
    }

    public void checkDeactivateVariation(AllPermissions permissions, CheckPermission checkPermission) {
        navigateToVariationDetailPage();
        if (!permissions.getProduct().getProductManagement().isDeactivateProduct() && productInfo.getVariationStatus().get(0).equals("ACTIVE")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeactivate), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Deactivate product.");
    }

    public void checkEditTranslation(AllPermissions permissions, CheckPermission checkPermission) {
        navigateToVariationDetailPage();
        if (!permissions.getProduct().getProductManagement().isEditTranslation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnEditTranslation), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update product translation.");
    }
}