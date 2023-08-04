import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.*;
import api.dashboard.promotion.CreatePromotion;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.login.LoginPage;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;

import static utilities.account.AccountTest.*;

public class BH_8887 extends BaseTest {
    int productID;
    ProductInfo productInfo;
    boolean isHideStock = false;
    boolean isDisplayIfOutOfStock = true;
    LoginInformation loginInformation;
    int customerId;

    @BeforeClass
    void setup() {
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        driver = new InitWebdriver().getDriver(browser, headless);
        customerId = new Customers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, loginInformation);
        tcsFileName = "check_product_detail_sf/BH_8887_View discount campaign at product detail.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "[BH_8887] Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        // get product ID
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock)
                .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[BH_8887] IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock)
                .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[BH_8887] Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[BH_8887] IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @Test(groups = "[BH_8887] Normal product - Without variation")
    void BH_8887_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G1_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Without variation")
    void BH_8887_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G1_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Without variation")
    void BH_8887_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G1_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Without variation")
    void BH_8887_G1_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G1_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Without variation")
    void BH_8887_G1_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G1_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Without variation")
    void BH_8887_G1_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G1_Case2_3";
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[BH_8887] IMEI product - Without variation")
    void BH_8887_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G2_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Without variation")
    void BH_8887_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G2_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Without variation")
    void BH_8887_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G2_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Without variation")
    void BH_8887_G2_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G2_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Without variation")
    void BH_8887_G2_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G2_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Without variation")
    void BH_8887_G2_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G2_Case2_3";
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Variation")
    void BH_8887_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G3_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Variation")
    void BH_8887_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G3_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Variation")
    void BH_8887_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G3_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Variation")
    void BH_8887_G3_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G3_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Variation")
    void BH_8887_G3_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G3_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] Normal product - Variation")
    void BH_8887_G3_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G3_Case2_3";
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[BH_8887] IMEI product - Variation")
    void BH_8887_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G4_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Variation")
    void BH_8887_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G4_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Variation")
    void BH_8887_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G4_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Variation")
    void BH_8887_G4_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G4_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Variation")
    void BH_8887_G4_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G4_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8887] IMEI product - Variation")
    void BH_8887_G4_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G4_Case2_3";
        int endMin = 60;

        new CreatePromotion(loginInformation)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }
}
