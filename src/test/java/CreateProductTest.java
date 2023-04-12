import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;
import static utilities.links.Links.DOMAIN;

// BH_4694, 4695, 4696, 4697, 4698, 4699, 4700, 4701, 4702, 4703, 4704, 7467, 7472, 3545,
public class CreateProductTest extends BaseTest {
    boolean showOutOfStock;
    
    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        new pages.storefront.login.LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG);
        tcsFileName = "check_product_detail_sf/Create product.xlsx".replace("/", File.separator);
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_01";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int branchStock = 5;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_02";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int branchStock = 5;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_03";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int branchStock = 5;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_04";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G2: IMEI product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_01";
        boolean isIMEIProduct = true;
        showOutOfStock = true;
        int branchStock = 5;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_02";
        boolean isIMEIProduct = true;
        showOutOfStock = true;
        int branchStock = 5;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_03";
        boolean isIMEIProduct = true;
        showOutOfStock = false;
        int branchStock = 5;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_04";
        boolean isIMEIProduct = true;
        showOutOfStock = false;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createWithoutVariationProduct(isIMEIProduct, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G3: Normal product - Variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_01";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_02";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_03";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_04";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_05";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_06";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G4: IMEI product - Variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_01";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_02";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_03";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_04";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_05";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_06";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

       new ProductPage(driver).setLanguage(language).navigateToCreateProductPage()
                .setShowOutOfStock(showOutOfStock)
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                .configConversionUnit()
                .configWholesaleProduct();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }
}
