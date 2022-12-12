import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.promotion.CreatePromotion;
import api.storefront.login.LoginSF;
import api.storefront.signup.SignUp;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.login.LoginPage;

import java.io.IOException;
import java.sql.SQLException;

import static api.dashboard.login.Login.storeURL;
import static api.dashboard.promotion.CreatePromotion.productDiscountCampaignBranchConditionType;
import static api.storefront.signup.SignUp.*;
import static java.lang.Thread.sleep;
import static utilities.links.Links.SF_DOMAIN;

public class BH_8887 extends BaseTest {

    String sfDomain;
    CreateProduct createProduct;

    @BeforeSuite
    void createCustomerAndSegment() throws SQLException, InterruptedException {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();

        createProduct.getTaxList().getBranchList();

        new SignUp().signUpByPhoneNumber();

        new LoginSF().LoginByPhoneNumber();

        sleep(3000);

        new Customers().addCustomerTag(customerName).createSegment();

        // Hardcode
        // PROD config
//        segmentID = 3502258;
//        phoneNumber = "prd@nbobd.com";
//        password = "Abc@12345";

//        //STG config
//        segmentID = 3502908;
//        phoneNumber = "stgbuyer@nbobd.com";
//        password = "Abc@12345";
//        productDiscountCampaignBranchConditionType = 1;

        sfDomain = "https://%s%s/".formatted(storeURL, SF_DOMAIN);
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
    public void setup() throws InterruptedException {
        CreatePromotion createPromotion = new CreatePromotion();
        int startMin = 1;
        int endMin = 60;
        createPromotion
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        System.out.println(createPromotion.getFlashSaleStatus());
        System.out.println(createPromotion.getProductDiscountCampaignStatus());
        sleep(61000);
        System.out.println(createPromotion.getFlashSaleStatus());
        System.out.println(createPromotion.getProductDiscountCampaignStatus());
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case1_1_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSalePriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case1_2_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case1_3_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkProductPriceWhenFlashSaleIsScheduleWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case2_1_DiscountCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case2_2_DiscountCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_G1_Case2_3_DiscountCampaignIsSchedule() throws IOException {
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }


    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case1_1_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSalePriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case1_2_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case1_3_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkProductPriceWhenFlashSaleIsScheduleWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case2_1_DiscountCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case2_2_DiscountCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_G2_Case2_3_DiscountCampaignIsSchedule() throws IOException {
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case1_1_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSalePriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case1_2_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case1_3_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkProductPriceWhenFlashSaleIsScheduleVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case2_1_DiscountCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case2_2_DiscountCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_G3_Case2_3_DiscountCampaignIsSchedule() throws IOException {
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }


    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case1_1_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSalePriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case1_2_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case1_3_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkProductPriceWhenFlashSaleIsScheduleVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case2_1_DiscountCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkDiscountCampaignPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case2_2_DiscountCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(startMin, endMin)
                .endEarlyDiscountCampaign();

        sleep(startMin * 60 * 1000);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_G4_Case2_3_DiscountCampaignIsSchedule() throws IOException {
        int endMin = 60;

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new CreatePromotion()
                .endEarlyFlashSale()
                .createProductDiscountCampaign(endMin - 1, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }
}
