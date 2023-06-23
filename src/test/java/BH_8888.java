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

import java.io.File;

import static utilities.account.AccountTest.*;

public class BH_8888 extends BaseTest {
    ProductInfo productInfo;
    int productID;
    boolean isHideStock = false;
    boolean isDisplayIfOutOfStock = true;

    @BeforeClass
    void setup() {
        new Login().loginToDashboardByMail(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG);
        tcsFileName = "check_product_detail_sf/BH_8888_View wholesale product at product detail.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        productID = new APIAllProducts().getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productID);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        productID = new APIAllProducts().getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productID);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts().getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productID);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts().getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productID);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G1_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G1_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G1_Case1_3";
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G1_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G1_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G1_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }


    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G2_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign()
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G2_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G2_Case1_3";
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G2_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G2_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G2_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G3_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign()
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G3_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G3_Case1_3";
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G3_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G3_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G3_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    //
    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G4_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign()
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G4_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G4_Case1_3";
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G4_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G4_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G4_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language, productInfo);
    }
}
