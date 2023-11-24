import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.CreateProduct;
import api.dashboard.products.ProductInformation;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.login.LoginPage;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;

// BH_9536:Check to display/hide if out of stock at product detail
public class BH_9536 extends BaseTest {
    boolean isDisplayIfOutOfStock;
    boolean isHideStock = false;
    int productID;
    ProductInfo productInfo;
    LoginInformation loginInformation;
    int customerId;

    @BeforeClass
    void setup() {
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        driver = new InitWebdriver().getDriver(browser, headless);
        customerId = new Customers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84", loginInformation);
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
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G1_Case2_1_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G1_Case2_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void BH_9536_G1_Case3_1_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G1_Case3_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G1_Case3_2_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G1_Case3_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G2: IMEI product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void BH_9536_G2_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G2_Case1_1";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G2_Case2_1_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G2_Case2_1";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void BH_9536_G2_Case3_1_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G2_Case3_1";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = false;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void BH_9536_G2_Case3_2_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "BH_9536_G2_Case3_2";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = false;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G3: Normal product - Variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void BH_9536_G3_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G3_Case1_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G3_Case2_1_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case2_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G3_Case2_2_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case2_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void BH_9536_G3_Case3_1_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "BH_9536_G3_Case3_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G3_Case3_2_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case3_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G3_Case3_3_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G3_Case3_3";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G4: IMEI product - Variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void BH_9536_G4_Case1_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "BH_9536_G4_Case1_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G4_Case2_1_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case2_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G4_Case2_2_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case2_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void BH_9536_G4_Case3_1_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "BH_9536_G4_Case3_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void BH_9536_G4_Case3_2_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case3_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void BH_9536_G4_Case3_3_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "BH_9536_G4_Case3_3";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }
}
