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

import static api.dashboard.login.Login.storeName;
import static api.storefront.signup.SignUp.*;
import static java.lang.Thread.sleep;

public class BH_8887 extends BaseTest {

    String sfDomain;

    @BeforeSuite
    void createCustomerAndSegment() throws SQLException, InterruptedException {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        new SignUp().signUpByPhoneNumber();

        new LoginSF().LoginByPhoneNumber();

        sleep(3000);

        new Customers().addCustomerTag(customerName)
                .createSegment();

        sfDomain = "https://%s.unisell.vn/".formatted(storeName);
    }

    @BeforeGroups(groups = "Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int stockQuantity = 5;
        new CreateProduct()
                .getTaxList()
                .getBranchList()
                .createWithoutVariationProduct(isIMEIProduct, stockQuantity)
                .addWholeSalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int stockQuantity = 5;
        new CreateProduct().getTaxList()
                .getBranchList()
                .createWithoutVariationProduct(isIMEIProduct, stockQuantity)
                .addWholeSalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int stockQuantity = 2;
        int increaseStock = 1;
        new CreateProduct().getTaxList()
                .getBranchList()
                .createVariationProduct(isIMEIProduct, stockQuantity, increaseStock)
                .addWholeSalePriceProduct()
                .createCollection();
    }

    @BeforeGroups(groups = "IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int stockQuantity = 2;
        int increaseStock = 1;
        new CreateProduct().getTaxList()
                .getBranchList()
                .createVariationProduct(isIMEIProduct, stockQuantity, increaseStock)
                .addWholeSalePriceProduct()
                .createCollection();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case1_1_G1_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkFlashSalePriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case1_2_G1_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);


        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleCampaignShouldBeShown()
                .checkWholesaleCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case1_3_G1_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);


        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkProductPriceWhenFlashSaleIsScheduleWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case2_1_G1_WholesaleCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleCampaignShouldBeShown()
                .checkWholesaleCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case2_2_G1_WholesaleCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin)
                .endEarlyWholesaleCampaign();

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case2_3_G1_WholesaleCampaignIsSchedule() throws IOException {
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(endMin - 1, endMin);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }


    @Test(groups = "IMEI product - Without variation")
    void BH_8887_Case1_1_G2_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkFlashSalePriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_Case1_2_G2_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleCampaignShouldBeShown()
                .checkWholesaleCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_Case1_3_G2_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);


        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkProductPriceWhenFlashSaleIsScheduleWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_Case2_1_G2_WholesaleCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleCampaignShouldBeShown()
                .checkWholesaleCampaignPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_Case2_2_G2_WholesaleCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin)
                .endEarlyWholesaleCampaign();

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Without variation")
    void BH_8887_Case2_3_G2_WholesaleCampaignIsSchedule() throws IOException {
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(endMin - 1, endMin);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceWithoutVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_Case1_1_G3_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkFlashSalePriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_Case1_2_G3_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleCampaignShouldBeShown()
                .checkWholesaleCampaignPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_Case1_3_G3_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkProductPriceWhenFlashSaleIsScheduleVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_Case2_1_G3_WholesaleCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);


        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_Case2_2_G3_WholesaleCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin)
                .endEarlyWholesaleCampaign();

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "Normal product - Variation")
    void BH_8887_Case2_3_G3_WholesaleCampaignIsSchedule() throws IOException, InterruptedException {
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(endMin - 1, endMin);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }


    @Test(groups = "IMEI product - Variation")
    void BH_8887_Case1_1_G4_FlashSaleIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkFlashSalePriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_Case1_2_G4_FlashSaleIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(startMin, endMin)
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleCampaignShouldBeShown()
                .checkWholesaleCampaignPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_Case1_3_G4_FlashSaleIsSchedule() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .createFlashSale(endMin - 1, endMin)
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkFlashSaleShouldBeShown()
                .checkProductPriceWhenFlashSaleIsScheduleVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_Case2_1_G4_WholesaleCampaignIsInProgress() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin);

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);


        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_Case2_2_G4_WholesaleCampaignIsExpired() throws IOException, InterruptedException {
        int startMin = 1;
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(startMin, endMin)
                .endEarlyWholesaleCampaign();

        sleep(startMin * 60 * 1000);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }

    @Test(groups = "IMEI product - Variation")
    void BH_8887_Case2_3_G4_WholesaleCampaignIsSchedule() throws IOException, InterruptedException {
        int endMin = 20;

        new CreatePromotion()
                .endEarlyFlashSale()
                .endEarlyWholesaleCampaign()
                .createProductWholeSaleCampaign(endMin - 1, endMin);

        new LoginPage(driver)
                .navigate(sfDomain)
                .performLogin(phoneNumber, password);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWholesaleProductShouldBeShown()
                .checkWholesaleProductPriceVariationProduct()
                .completeVerify();
    }


}
