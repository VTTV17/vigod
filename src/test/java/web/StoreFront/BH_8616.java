package web.StoreFront;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.products.all_products.APIProductDetail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.login.LoginPage;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import static utilities.account.AccountTest.*;

// BH_8616:Check to display/hide if out of stock at product detail
public class BH_8616 extends BaseTest {
    boolean isHideStock;
    boolean isDisplayIfOutOfStock = true;
    int productID;
    ProductInfo productInfo;
    LoginInformation loginInformation;
    int customerId;

    @BeforeClass
    void setup() {
        tcsFileName = "BH_8616_Check hide remaining stock on online store.xlsx";
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        driver = new InitWebdriver().getDriver(browser, headless);
        customerId = new Customers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84", loginInformation);
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    void BH_8616_G1_Case1_1_HideStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G1_Case1_1";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void BH_8616_G1_Case2_1_ShowStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G1_Case2_1";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G2: IMEI product - without variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    void BH_8616_G2_Case1_1_HideStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G2_Case1_1";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void BH_8616_G2_Case2_1_ShowStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G2_Case2_1";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G3: Normal product - Variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // all variation stock quantity > 0
    void BH_8616_G3_Case1_1_HideStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case1_1";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void BH_8616_G3_Case1_2_HideStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case1_2";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void BH_8616_G3_Case2_1_ShowStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case2_1";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void BH_8616_G3_Case2_2_ShowStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case2_2";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    // G4: IMEI product - Variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // all variation stock quantity > 0
    void BH_8616_G4_Case1_1_HideStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case1_1";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void BH_8616_G4_Case1_2_HideStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case1_2";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void BH_8616_G4_Case2_1_ShowStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case2_1";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void BH_8616_G4_Case2_2_ShowStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case2_2";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }
}