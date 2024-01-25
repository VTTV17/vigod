package web.Dashboard;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.products.APIAllProducts;
import api.Seller.products.CreateProduct;
import api.Seller.products.ProductInformation;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import web.BaseTest;
import web.Dashboard.login.LoginPage;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.StoreFront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;

import static utilities.account.AccountTest.*;

public class UpdateProductTest extends BaseTest {
    boolean isDisplayIfOutOfStock = true;
    boolean isHideStock = false;
    int productID;
    ProductInfo productInfo;
    LoginInformation loginInformation;
    int customerId;

    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        customerId = new Customers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        new web.StoreFront.login.LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84", loginInformation);
        tcsFileName = "check_product_detail_sf/Update product.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "[UPDATE] Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 2;

        // get product ID
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock)
                .getProductID();
    }

    @BeforeGroups(groups = "[UPDATE] IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 2;

        // get product ID
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock)
                .getProductID();
    }

    @BeforeGroups(groups = "[UPDATE] Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
    }

    @BeforeGroups(groups = "[UPDATE] IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
    }

    // G1: Normal product - without variation
    @Test(groups = "[UPDATE] Normal product - Without variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_01";
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_02";
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_03";
        isDisplayIfOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G1_04";
        isDisplayIfOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_G1_05";

        new ProductPage(driver, loginInformation).setLanguage(language).editTranslation(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G1_06";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("INACTIVE", productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_07_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G1_07";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("ACTIVE", productID).uncheckWebPlatform();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_08_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G1_08";

        new ProductPage(driver, loginInformation).setLanguage(language).deleteProduct(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    // G2: IMEI product - without variation
    @Test(groups = "[UPDATE] IMEI product - Without variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    void UP_PRODUCT_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_01";
        isDisplayIfOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_02";
        isDisplayIfOutOfStock = true;
        int branchStock = 5;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void UP_PRODUCT_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_03";
        isDisplayIfOutOfStock = false;
        int branchStock = 5;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void UP_PRODUCT_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G2_04";
        isDisplayIfOutOfStock = false;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateWithoutVariationProduct(branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_05_EditTranslation() throws Exception {
        testCaseId = "UP_PRODUCT_G2_05";
        new ProductPage(driver, loginInformation).setLanguage(language).editTranslation(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_06_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G2_06";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("INACTIVE", productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_07_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G2_07";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("ACTIVE", productID).uncheckWebPlatform();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_08_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G2_08";

        new ProductPage(driver, loginInformation).setLanguage(language).deleteProduct(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G3: [UPDATE] Normal product - Variation
    @Test(groups = "[UPDATE] Normal product - Variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_01";
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_02";
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_03";
        isDisplayIfOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_04";
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_05";
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;


        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G3_06";
        isDisplayIfOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G3_07";

        new ProductPage(driver, loginInformation).setLanguage(language).editTranslation(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_G3_08";

        new ProductPage(driver, loginInformation).setLanguage(language).editVariationTranslation(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G3_09";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("INACTIVE", productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G3_10";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("ACTIVE", productID).changeVariationStatus(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_11_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G3_11";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("ACTIVE", productID).uncheckWebPlatform();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_12_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G3_12";

        new ProductPage(driver, loginInformation).setLanguage(language).deleteProduct(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G4: [UPDATE] IMEI product - Variation
    @Test(groups = "[UPDATE] IMEI product - Variation")
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    void UP_PRODUCT_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_01";
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_02";
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_03";
        isDisplayIfOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void UP_PRODUCT_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_04";
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void UP_PRODUCT_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_05";
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void UP_PRODUCT_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "UP_PRODUCT_G4_06";
        isDisplayIfOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new ProductPage(driver, loginInformation).setLanguage(language).navigateToUpdateProductPage(productID)
                .setShowOutOfStock(isDisplayIfOutOfStock)
                .updateVariationProduct(increaseNum, branchStock)
//                .configConversionUnit()
                .configWholesaleProduct();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_07_EditTranslationForMainProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G4_07";

        new ProductPage(driver, loginInformation).setLanguage(language).editTranslation(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_08_EditTranslationForEachVariation() throws Exception {
        testCaseId = "UP_PRODUCT_G4_08";

        new ProductPage(driver, loginInformation).setLanguage(language).editVariationTranslation(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_09_ChangeProductStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G4_09";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("INACTIVE", productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_10_ChangeVariationStatus() throws Exception {
        testCaseId = "UP_PRODUCT_G4_10";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("ACTIVE", productID).changeVariationStatus(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_11_UncheckWebPlatform() throws Exception {
        testCaseId = "UP_PRODUCT_G4_11";

        new ProductPage(driver, loginInformation).setLanguage(language).changeProductStatus("ACTIVE", productID).uncheckWebPlatform();

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_12_DeleteProduct() throws Exception {
        testCaseId = "UP_PRODUCT_G4_12";

        new ProductPage(driver, loginInformation).setLanguage(language).deleteProduct(productID);

        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }
}
