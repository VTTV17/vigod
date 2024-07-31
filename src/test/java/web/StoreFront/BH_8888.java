package web.StoreFront;

import api.Seller.customers.APIAllCustomers;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.all_products.WholesaleProduct;
import api.Seller.promotion.FlashSale;
import api.Seller.promotion.ProductDiscountCampaign;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.login.LoginPage;
import utilities.driver.InitWebdriver;
import utilities.model.api.promotion.productDiscountCampaign.ProductDiscountCampaignConditions;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import static java.lang.Thread.sleep;
import static utilities.account.AccountTest.*;

public class BH_8888 extends BaseTest {
    ProductInfo productInfo;
    int productID;
    boolean isHideStock = false;
    boolean isDisplayIfOutOfStock = true;
    LoginInformation loginInformation;
    int customerId;
    int startMin = 1;
    int endMin = 5;
    FlashSale flashSale;
    ProductDiscountCampaign discountCampaign;
    ProductDiscountCampaignConditions conditions;

    @BeforeClass
    void setup() {
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        flashSale = new FlashSale(loginInformation);
        discountCampaign = new ProductDiscountCampaign(loginInformation);
        driver = new InitWebdriver().getDriver(browser, headless);
        customerId = new APIAllCustomers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        conditions = new ProductDiscountCampaignConditions();
        conditions.setCustomerId(customerId);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84", loginInformation);
        tcsFileName = "BH_8888_View wholesale product at product detail.xlsx";
    }

    @BeforeGroups(groups = "[BH_8888] Normal product - Without variation")
    void preCondition_G1() {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new APICreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[BH_8888] IMEI product - Without variation")
    void preCondition_G2() {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new APICreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[BH_8888] Normal product - Variation")
    void preCondition_G3() {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new APICreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[BH_8888] IMEI product - Variation")
    void preCondition_G4() {
        boolean isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0)
            productID = new APICreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock, branchStock)
                    .getProductID();
        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productID);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @Test(groups = "[BH_8888] Normal product - Without variation")
    void BH_8888_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G1_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        waitFlashSaleStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Without variation")
    void BH_8888_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G1_Case1_2";

        flashSale.endEarlyFlashSale();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Without variation")
    void BH_8888_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G1_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Without variation")
    void BH_8888_G1_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G1_Case2_1";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Without variation")
    void BH_8888_G1_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G1_Case2_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Without variation")
    void BH_8888_G1_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G1_Case2_3";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[BH_8888] IMEI product - Without variation")
    void BH_8888_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G2_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        waitFlashSaleStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Without variation")
    void BH_8888_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G2_Case1_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Without variation")
    void BH_8888_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G2_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Without variation")
    void BH_8888_G2_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G2_Case2_1";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Without variation")
    void BH_8888_G2_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G2_Case2_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Without variation")
    void BH_8888_G2_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G2_Case2_3";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Variation")
    void BH_8888_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G3_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        discountCampaign.endEarlyDiscountCampaign();
        waitFlashSaleStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Variation")
    void BH_8888_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G3_Case1_2";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Variation")
    void BH_8888_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G3_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Variation")
    void BH_8888_G3_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G3_Case2_1";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Variation")
    void BH_8888_G3_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G3_Case2_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] Normal product - Variation")
    void BH_8888_G3_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G3_Case2_3";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    //
    @Test(groups = "[BH_8888] IMEI product - Variation")
    void BH_8888_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "BH_8888_G4_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        discountCampaign.endEarlyDiscountCampaign();
        waitFlashSaleStart();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Variation")
    void BH_8888_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "BH_8888_G4_Case1_2";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Variation")
    void BH_8888_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "BH_8888_G4_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Variation")
    void BH_8888_G4_Case2_1_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "BH_8888_G4_Case2_1";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Variation")
    void BH_8888_G4_Case2_2_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "BH_8888_G4_Case2_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[BH_8888] IMEI product - Variation")
    void BH_8888_G4_Case2_3_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "BH_8888_G4_Case2_3";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, endMin);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    void waitFlashSaleStart() {
        try {
            sleep((long) startMin * 60 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
