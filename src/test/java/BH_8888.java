import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.promotion.CreatePromotion;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import api.dashboard.setting.VAT;
import api.storefront.login.LoginSF;
import api.storefront.signup.SignUp;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.login.LoginPage;

import java.io.File;
import java.sql.SQLException;

import static api.dashboard.setting.StoreInformation.storeURL;
import static api.storefront.signup.SignUp.*;
import static java.lang.Thread.sleep;
import static utilities.links.Links.SF_DOMAIN;

public class BH_8888 extends BaseTest {

    String sfDomain;
    CreateProduct createProduct;

    @BeforeSuite
    void initPreCondition() throws SQLException, InterruptedException {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();

        new BranchManagement().getBranchInformation();

        new StoreInformation().getStoreInformation();

        new VAT().getTaxList();

        new SignUp().signUpByPhoneNumber();

        new LoginSF().LoginByPhoneNumber();

        sleep(3000);

        new Customers().addCustomerTag(customerName).createSegment();

        sfDomain = "https://%s%s/".formatted(storeURL, SF_DOMAIN);

        tcsFileName = "check_product_detail_sf/BH_8888_View wholesale product at product detail.xlsx".replace("/", File.separator);
    }

    @BeforeGroups(groups = "Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        createProduct
                .createWithoutVariationProduct(isIMEIProduct,
                        branchStock,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        createProduct
                .createWithoutVariationProduct(isIMEIProduct,
                        branchStock,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        createProduct
                .createVariationProduct(isIMEIProduct,
                        increaseNum,
                        branchStock,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        createProduct
                .createVariationProduct(isIMEIProduct,
                        increaseNum,
                        branchStock,
                        branchStock)
                .addWholesalePriceProduct()
                .createCollection();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G1_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G1_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G1_Case1_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G1_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G1_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8888_G1_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G1_Case2_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }


    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G2_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G2_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G2_Case1_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G2_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G2_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8888_G2_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G2_Case2_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G3_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G3_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G3_Case1_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G3_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G3_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8888_G3_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G3_Case2_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }
    //
    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G4_Case1_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G4_Case1_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G4_Case1_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G4_Case2_1";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G4_Case2_2";
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8888_G4_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G4_Case2_3";
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLoginJS(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }
}
