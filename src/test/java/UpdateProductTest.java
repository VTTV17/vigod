import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import org.testng.annotations.*;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static pages.dashboard.products.all_products.ProductPage.language;
import static pages.dashboard.products.all_products.ProductPage.uiIsDisplayOutOfStock;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class UpdateProductTest extends BaseTest {

    @BeforeSuite
    @Parameters({"browser", "headless", "account", "password"})
    void initPreCondition(@Optional("chrome") String browser,
                          @Optional("true") String headless,
                          @Optional(ADMIN_ACCOUNT_THANG) String account,
                          @Optional(ADMIN_PASSWORD_THANG) String password) {

        new Login().loginToDashboardByMail(account, password);
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(account, password);
        tcsFileName = "check_product_detail_sf/Update product.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "Normal product - Without variation - VIE")
    void preCondition_G1_VIE() {
        language = "VIE";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock);
    }

    @BeforeGroups(groups = "IMEI product - Without variation - VIE")
    void preCondition_G2_VIE() {
        language = "VIE";
        boolean isIMEIProduct = true;
        int branchStock = 2;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock);
    }

    @BeforeGroups(groups = "Normal product - Variation - VIE")
    void preCondition_G3_VIE() {
        language = "VIE";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock);
    }

    @BeforeGroups(groups = "IMEI product - Variation - VIE")
    void preCondition_G4_VIE() {
        language = "VIE";
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock);
    }

    @BeforeGroups(groups = "Normal product - Without variation - ENG")
    void preCondition_G1_ENG() {
        language = "ENG";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock);
    }

    @BeforeGroups(groups = "IMEI product - Without variation - ENG")
    void preCondition_G2_ENG() {
        language = "ENG";
        boolean isIMEIProduct = true;
        int branchStock = 2;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock);
    }

    @BeforeGroups(groups = "Normal product - Variation - ENG")
    void preCondition_G3_ENG() {
        language = "ENG";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock);
    }

    @BeforeGroups(groups = "IMEI product - Variation - ENG")
    void preCondition_G4_ENG() {
        language = "ENG";
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock);
    }

    /* check VIE language*/
    // G1: Normal product - without variation
    @Test(groups = "Normal product - Without variation - VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_VIE_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_01";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;
        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - VIE")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_VIE_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_02";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;
        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_VIE_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_03";
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_VIE_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_04";
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - VIE")
    void UP_PRODUCT_VIE_G1_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_05";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - VIE")
    void UP_PRODUCT_VIE_G1_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_06";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - VIE")
    void UP_PRODUCT_VIE_G1_07_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G1_07";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }


    // G2: IMEI product - without variation
    @Test(groups = "IMEI product - Without variation - VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_VIE_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_01";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - VIE")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_VIE_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_02";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_VIE_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_03";
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_VIE_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_04";
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }


    @Test(groups = "IMEI product - Without variation - VIE")
    void UP_PRODUCT_VIE_G2_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_05";
        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - VIE")
    void UP_PRODUCT_VIE_G2_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_06";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - VIE")
    void UP_PRODUCT_VIE_G2_07_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G2_07";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G3: Normal product - Variation
    @Test(groups = "Normal product - Variation - VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_VIE_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_01";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_VIE_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_02";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_VIE_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_03";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_VIE_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_04";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_VIE_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_05";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;


        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_VIE_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_06";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
    void UP_PRODUCT_VIE_G3_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_07";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
    void UP_PRODUCT_VIE_G3_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_08";

        new ProductPage(driver).editVariationTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
    void UP_PRODUCT_VIE_G3_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_09";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
    void UP_PRODUCT_VIE_G3_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_10";

        new ProductPage(driver).changeProductStatus("ACTIVE").changeVariationStatus();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - VIE")
    void UP_PRODUCT_VIE_G3_11_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G3_11";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G4: IMEI product - Variation
    @Test(groups = "IMEI product - Variation - VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_VIE_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_01";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_VIE_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_02";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_VIE_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_03";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_VIE_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_04";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_VIE_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_05";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_VIE_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_06";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
    void UP_PRODUCT_VIE_G4_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_07";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
    void UP_PRODUCT_VIE_G4_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_08";

        new ProductPage(driver).editVariationTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
    void UP_PRODUCT_VIE_G4_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_09";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
    void UP_PRODUCT_VIE_G4_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_10";

        new ProductPage(driver).changeProductStatus("ACTIVE").changeVariationStatus();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - VIE")
    void UP_PRODUCT_VIE_G4_11_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_VIE_G4_11";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    /* check ENG language*/
    // G1: Normal product - without variation
    @Test(groups = "Normal product - Without variation - ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_ENG_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_01";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_ENG_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_02";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_ENG_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_03";
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_ENG_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_04";
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
    void UP_PRODUCT_ENG_G1_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_05";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
    void UP_PRODUCT_ENG_G1_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_06";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
    void UP_PRODUCT_ENG_G1_07_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_07";

        new ProductPage(driver).changeProductStatus("ACTIVE")
                .uncheckWebPlatform();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation - ENG")
    void UP_PRODUCT_ENG_G1_08_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G1_08";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G2: IMEI product - without variation
    @Test(groups = "IMEI product - Without variation - ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_ENG_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_01";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_ENG_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_02";
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_ENG_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_03";
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_ENG_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_04";
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateWithoutVariationProduct(branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
    void UP_PRODUCT_ENG_G2_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_05";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
    void UP_PRODUCT_ENG_G2_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_06";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
    void UP_PRODUCT_ENG_G2_07_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_07";

        new ProductPage(driver).changeProductStatus("ACTIVE")
                .uncheckWebPlatform();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation - ENG")
    void UP_PRODUCT_ENG_G2_08_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G2_08";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G3: Normal product - Variation
    @Test(groups = "Normal product - Variation - ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_ENG_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_01";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_ENG_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_02";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_ENG_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_03";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_ENG_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_04";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_ENG_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_05";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_ENG_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_06";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
    void UP_PRODUCT_ENG_G3_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_07";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
    void UP_PRODUCT_ENG_G3_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_08";

        new ProductPage(driver).editVariationTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
    void UP_PRODUCT_ENG_G3_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_09";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
    void UP_PRODUCT_ENG_G3_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_10";

        new ProductPage(driver).changeProductStatus("ACTIVE").changeVariationStatus();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
    void UP_PRODUCT_ENG_G3_11_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_11";

        new ProductPage(driver).changeProductStatus("ACTIVE").uncheckWebPlatform();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation - ENG")
    void UP_PRODUCT_ENG_G3_12_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G3_12";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G4: IMEI product - Variation
    @Test(groups = "IMEI product - Variation - ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_ENG_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_01";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_ENG_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_02";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_ENG_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_03";
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_ENG_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_04";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_ENG_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_05";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_ENG_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_06";
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToUpdateProductPage()
                .updateVariationProduct(increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
    void UP_PRODUCT_ENG_G4_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_07";

        new ProductPage(driver).editTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
    void UP_PRODUCT_ENG_G4_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_08";

        new ProductPage(driver).editVariationTranslation();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
    void UP_PRODUCT_ENG_G4_10_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_09";

        new ProductPage(driver).changeProductStatus("INACTIVE");

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
    void UP_PRODUCT_ENG_G4_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_10";

        new ProductPage(driver).changeProductStatus("ACTIVE").changeVariationStatus();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
    void UP_PRODUCT_ENG_G4_11_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_11";

        new ProductPage(driver).changeProductStatus("ACTIVE").uncheckWebPlatform();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation - ENG")
    void UP_PRODUCT_ENG_G4_12_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_ENG_G4_12";

        new ProductPage(driver).deleteProduct();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation();
    }
}
