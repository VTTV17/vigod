package android;

import api.dashboard.login.Login;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.CreateProduct;
import api.dashboard.products.ProductInformation;
import api.dashboard.products.WholesaleProduct;
import api.dashboard.promotion.CreatePromotion;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.productDetail.BuyerProductDetailPage;
import utilities.UICommonMobile;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;

import java.io.File;
import java.net.MalformedURLException;

import static utilities.account.AccountTest.*;

public class ProductDetailTest extends BaseTest{
    int productId;
    boolean isIMEIProduct;
    boolean isHideStock = false;
    boolean isDisplayIfOutOfStock = true;
    ProductInfo productInfo;
     String udid = "10HC8G04UP0003U";
    String appPackage = "com.mediastep.shop0018";
    String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
    String URL = "http://127.0.0.1:4723/wd/hub";

    @BeforeClass
    void setup() throws MalformedURLException {
        tcsFileName = "android/Check promotion at product detail screen.xlsx".replace("/", File.separator);
        new Login().loginToDashboardByMail(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        driver = new InitAppiumDriver().getAppiumDriver(udid, "ANDROID", appPackage, appActivity, URL);

        new UICommonMobile(driver).waitSplashScreenLoaded();
        new NavigationBar(driver).tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void preCondition_G1() {
        isIMEIProduct = false;
        int branchStock = 5;
        // get product ID
        productId = new APIAllProducts().getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0) productId = new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock)
                .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productId);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void preCondition_G2() {
        isIMEIProduct = true;
        int branchStock = 5;
        productId = new APIAllProducts().getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0) productId = new CreateProduct().createWithoutVariationProduct(isIMEIProduct, branchStock)
                .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productId);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void preCondition_G3() {
        isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        productId = new APIAllProducts().getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productId);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void preCondition_G4() {
        isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        productId = new APIAllProducts().getProductIDWithVariationAndInStock(isIMEIProduct,
                isHideStock,
                isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation().getInfo(productId);

        // add wholesale product config
        new WholesaleProduct().addWholesalePriceProduct(productInfo);
    }


    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_2";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_3";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G1_Case2_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G1_Case2_2";

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G1_Case2_3";
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }


    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_2";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_3";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G2_Case2_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G2_Case2_2";

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G2_Case2_3";
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_2";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_3";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_2";

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_3";
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }


    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, startMin, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_2";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_3";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .createFlashSale(productInfo, endMin - 1, endMin)
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_1";
        int startMin = 1;
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, startMin, endMin)
                .waitPromotionStart();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_2";

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_3";
        int endMin = 120;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(productInfo, endMin - 1, endMin);

        new BuyerProductDetailPage(driver)
                .openProductDetailScreenAndCheckProductInformation(language, productInfo);
    }

    @AfterMethod
    void teardown() {
        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);
    }
}
