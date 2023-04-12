import api.dashboard.products.CreateProduct;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static utilities.account.AccountTest.*;
import static utilities.links.Links.DOMAIN;

public class UpdateProductTest extends BaseTest {
    boolean showOutOfStock;

    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        new pages.storefront.login.LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG);
        driver.get(DOMAIN);
        tcsFileName = "check_product_detail_sf/Update product.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock);
    }

    @BeforeGroups(groups = "IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock);
    }

    @BeforeGroups(groups = "Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock);
    }

    @BeforeGroups(groups = "IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock);
    }
 
    // G1: Normal product - without variation
    @Test(groups = "Normal product - Without variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_01";
        showOutOfStock = true;
        int branchStock = 5;
        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_02";
        showOutOfStock = true;
        int branchStock = 5;
        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_03";
        showOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_04";
        showOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Without variation")
    void UP_PRODUCT_G1_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_G1_05";

        new ProductPage(driver).setLanguage(language).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Without variation")
    void UP_PRODUCT_G1_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G1_06";

        new ProductPage(driver).setLanguage(language).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G1_07_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G1_07";

        new ProductPage(driver).setLanguage(language).changeProductStatus("ACTIVE").uncheckWebPlatform();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Without variation")
    void UP_PRODUCT_G1_08_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G1_08";

        new ProductPage(driver).setLanguage(language).deleteProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }


    // G2: IMEI product - without variation
    @Test(groups = "IMEI product - Without variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_01";
        showOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_02";
        showOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_03";
        showOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_04";
        showOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }


    @Test(groups = "IMEI product - Without variation")
    void UP_PRODUCT_G2_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_G2_05";
        new ProductPage(driver).setLanguage(language).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Without variation")
    void UP_PRODUCT_G2_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G2_06";

        new ProductPage(driver).setLanguage(language).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G2_07_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G2_07";

        new ProductPage(driver).setLanguage(language).changeProductStatus("ACTIVE").uncheckWebPlatform();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }


    @Test(groups = "IMEI product - Without variation")
    void UP_PRODUCT_G2_08_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G2_08";

        new ProductPage(driver).setLanguage(language).deleteProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G3: Normal product - Variation
    @Test(groups = "Normal product - Variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_01";
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_02";
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_03";
        showOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_04";
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_05";
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;


        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_06";
        showOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G3_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G3_07";

        new ProductPage(driver).setLanguage(language).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G3_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_G3_08";

        new ProductPage(driver).setLanguage(language).editVariationTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G3_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G3_09";

        new ProductPage(driver).setLanguage(language).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G3_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G3_10";

        new ProductPage(driver).setLanguage(language).changeProductStatus("ACTIVE").changeVariationStatus();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G3_11_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G3_11";

        new ProductPage(driver).setLanguage(language).changeProductStatus("ACTIVE").uncheckWebPlatform();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "Normal product - Variation")
    void UP_PRODUCT_G3_12_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G3_12";

        new ProductPage(driver).setLanguage(language).deleteProduct();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G4: IMEI product - Variation
    @Test(groups = "IMEI product - Variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_01";
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_02";
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_03";
        showOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_04";
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_05";
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_06";
        showOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).setLanguage(language).navigateToUpdateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
    void UP_PRODUCT_G4_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G4_07";

        new ProductPage(driver).setLanguage(language).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
    void UP_PRODUCT_G4_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_G4_08";

        new ProductPage(driver).setLanguage(language).editVariationTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
    void UP_PRODUCT_G4_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G4_09";

        new ProductPage(driver).setLanguage(language).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
    void UP_PRODUCT_G4_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G4_10";

        new ProductPage(driver).setLanguage(language).changeProductStatus("ACTIVE").changeVariationStatus();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
    void UP_PRODUCT_G4_11_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G4_11";

        new ProductPage(driver).setLanguage(language).changeProductStatus("ACTIVE").uncheckWebPlatform();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test(groups = "IMEI product - Variation")
    void UP_PRODUCT_G4_12_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G4_12";

        new ProductPage(driver).setLanguage(language).deleteProduct();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }
}
