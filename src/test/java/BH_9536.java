import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

// BH_9536:Check to display/hide if out of stock at product detail
public class BH_9536 extends BaseTest {
    boolean showOutOfStock;

    @BeforeClass
    void setup() {
        new Login().loginToDashboardByMail(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        driver = new InitWebdriver().getDriver(browser, headless);
        tcsFileName = "check_product_detail_sf/BH_9536_Check to display if out of stock at product detail.xlsx".replace("/", File.separator);
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void BH_9536_G1_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G1_Case1_1";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int branchStock = 5;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G1_Case2_1_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G1_Case2_1";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int branchStock = 5;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void BH_9536_G1_Case3_1_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G1_Case3_1";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int branchStock = 5;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G1_Case3_2_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G1_Case3_2";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G2: IMEI product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void BH_9536_G2_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G2_Case1_1";
        boolean isIMEIProduct = true;
        showOutOfStock = true;
        int branchStock = 5;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G2_Case2_1_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G2_Case2_1";
        boolean isIMEIProduct = true;
        showOutOfStock = true;
        int branchStock = 5;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void BH_9536_G2_Case3_1_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G2_Case3_1";
        boolean isIMEIProduct = true;
        showOutOfStock = false;
        int branchStock = 5;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G2_Case3_2_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G2_Case3_2";
        boolean isIMEIProduct = true;
        showOutOfStock = false;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G3: Normal product - Variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void BH_9536_G3_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G3_Case1_1";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G3_Case2_1_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case2_1";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G3_Case2_2_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case2_2";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void BH_9536_G3_Case3_1_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "BH_9536_G3_Case3_1";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G3_Case3_2_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case3_2";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G3_Case3_3_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case3_3";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    // G4: IMEI product - Variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void BH_9536_G4_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G4_Case1_1";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G4_Case2_1_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case2_1";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G4_Case2_2_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case2_2";
        boolean isIMEIProduct = false;
        showOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void BH_9536_G4_Case3_1_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "BH_9536_G4_Case3_1";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G4_Case3_2_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case3_2";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G4_Case3_3_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case3_3";
        boolean isIMEIProduct = false;
        showOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;
        new CreateProduct().setShowOutOfStock(showOutOfStock).createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }
}
