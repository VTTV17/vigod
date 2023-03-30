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

// BH_4694, 4695, 4696, 4697, 4698, 4699, 4700, 4701, 4702, 4703, 4704, 7467, 7472, 3545,
public class CreateProductTest extends BaseTest {
    @BeforeSuite
    @Parameters({"browser", "headless", "account", "password"})
    void initPreCondition(@Optional("chrome") String browser,
                          @Optional("true") String headless,
                          @Optional(ADMIN_ACCOUNT_THANG) String account,
                          @Optional(ADMIN_PASSWORD_THANG) String password) {

        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(account, password);
        tcsFileName = "check_product_detail_sf/Create product.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "Dashboard language = VIE")
    void VIE() {
        language = "VIE";
    }

    @BeforeGroups(groups = "Dashboard language = ENG")
    void ENG() {
        language = "ENG";
    }

    /* check VIE language*/
    // G1: Normal product - without variation
    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_VIE_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G1_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_VIE_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G1_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_VIE_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G1_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_VIE_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G1_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G2: IMEI product - without variation
    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_VIE_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G2_01";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_VIE_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G2_02";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_VIE_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G2_03";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_VIE_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G2_04";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G3: Normal product - Variation
    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_VIE_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G3_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_VIE_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G3_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_VIE_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G3_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_VIE_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G3_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_VIE_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G3_05";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_VIE_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G3_06";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G4: IMEI product - Variation
    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_VIE_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G4_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_VIE_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G4_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_VIE_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G4_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_VIE_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G4_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_VIE_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G4_05";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = VIE")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_VIE_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_VIE_G4_06";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    /* check ENG language*/
    // G1: Normal product - without variation
    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_ENG_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G1_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_ENG_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G1_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_ENG_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G1_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_ENG_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G1_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G2: IMEI product - without variation
    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_ENG_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G2_01";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_ENG_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G2_02";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_ENG_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G2_03";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_ENG_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G2_04";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G3: Normal product - Variation
    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_ENG_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G3_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_ENG_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G3_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_ENG_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G3_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_ENG_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G3_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_ENG_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G3_05";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_ENG_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G3_06";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    // G4: IMEI product - Variation
    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_ENG_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G4_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_ENG_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G4_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_ENG_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G4_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_ENG_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G4_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_ENG_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G4_05";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Dashboard language = ENG")
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_ENG_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_ENG_G4_06";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @AfterSuite
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
