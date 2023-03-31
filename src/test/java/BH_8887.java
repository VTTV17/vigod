import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.promotion.CreatePromotion;
import org.testng.annotations.*;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static java.lang.Thread.sleep;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class BH_8887 extends BaseTest {
    @BeforeSuite
    @Parameters({"browser", "headless"})
    void initPreCondition(@Optional("chrome") String browser,
                          @Optional("true") String headless) {

        new Login().loginToDashboardByMail(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        driver = new InitWebdriver().getDriver(browser, headless);
        tcsFileName = "check_product_detail_sf/BH_8887_View discount campaign at product detail.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                        increaseNum,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                        increaseNum,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }
    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G1_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G1_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G1_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G1_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G1_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G1_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }


    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G2_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G2_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G2_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G2_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G2_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G2_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G3_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G3_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G3_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G3_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G3_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G3_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }


    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8887_G4_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8887_G4_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8887_G4_Case1_3";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8887_G4_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8887_G4_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8887_G4_Case2_3";
        int endMin = 60;

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation();
    }
}
