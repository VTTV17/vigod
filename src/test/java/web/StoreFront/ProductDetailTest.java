package web.StoreFront;

import api.Seller.customers.APIAllCustomers;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.all_products.WholesaleProduct;
import api.Seller.promotion.FlashSale;
import api.Seller.promotion.ProductDiscountCampaign;
import api.Seller.setting.BranchManagement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.model.api.promotion.productDiscountCampaign.ProductDiscountCampaignConditions;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.login.LoginPage;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.account.AccountTest.*;

public class ProductDetailTest extends BaseTest {
    LoginInformation loginInformation;
    List<Integer> branchID;
    int customerId;

    ProductInfo productInfo;
    int productId;
    FlashSale flashSale;
    ProductDiscountCampaignConditions conditions;
    ProductDiscountCampaign discountCampaign;

    @BeforeClass
    void setup() {
        System.out.println("step2");
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        branchID = new BranchManagement(loginInformation).getInfo().getBranchID();
        customerId = new APIAllCustomers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        flashSale = new FlashSale(loginInformation);
        discountCampaign = new ProductDiscountCampaign(loginInformation);
        driver = new InitWebdriver().getDriver(browser, headless);
        conditions = new ProductDiscountCampaignConditions();
        conditions.setCustomerId(customerId);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84", loginInformation);
    }

    @BeforeGroups(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void preCondition_G1() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, 5).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void preCondition_G2() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, 5).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void preCondition_G3() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, 1).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void preCondition_G4() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, 1).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }


    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void G1_01_FlashSaleIsInProgress() throws Exception {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void G1_02_FlashSaleIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void G1_03_FlashSaleIsSchedule() throws Exception {
        flashSale.createFlashSale(productInfo, 29, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void G1_04_DiscountCampaignIsInProgress() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void G1_05_DiscountCampaignIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
    void G1_06_DiscountCampaignIsSchedule() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void G1_07_HideStockAndInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createWithoutVariationProduct(false, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void G1_08_ShowStockAndInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createWithoutVariationProduct(false, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stockQuantity > 0
    void G1_09_SettingDisplayAndProductInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(false, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void G1_10_SettingDisplayAndProductOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(false).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void G1_11_SettingHiddenAndProductInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(false, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void G1_12_SettingHiddenAndProductOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(false, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G1_13_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G1_14_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Without variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G1_15_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void G2_01_FlashSaleIsInProgress() throws Exception {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void G2_02_FlashSaleIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void G2_03_FlashSaleIsSchedule() throws Exception {
        flashSale.createFlashSale(productInfo, 29, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void G2_04_DiscountCampaignIsInProgress() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void G2_05_DiscountCampaignIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
    void G2_06_DiscountCampaignIsSchedule() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void G2_07_HideStockAndInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(true).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void G2_08_ShowStockAndInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stockQuantity > 0
    void G2_09_SettingDisplayAndProductInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void G2_10_SettingDisplayAndProductOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(true).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void G2_11_SettingHiddenAndProductInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void G2_12_SettingHiddenAndProductOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(true).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G2_13_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G2_14_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Without variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G2_15_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void G3_01_FlashSaleIsInProgress() throws Exception {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void G3_02_FlashSaleIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void G3_03_FlashSaleIsSchedule() throws Exception {
        flashSale.createFlashSale(productInfo, 29, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void G3_04_DiscountCampaignIsInProgress() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void G3_05_DiscountCampaignIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
    void G3_06_DiscountCampaignIsSchedule() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variation stock quantity > 0
    void G3_07_HideStockAndInStock_AllVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void G3_08_HideStockAndInStock_SomeVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(false, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void G3_09_ShowStockAndInStock_AllVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void G3_10_ShowStockAndInStock_SomeVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(false, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity > 0
    void G3_11_SettingDisplayAndProductInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void G3_12_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void G3_13_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(false, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void G3_14_SettingHiddenAndAllVariationsInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void G3_15_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(false, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void G3_16_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(false, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G3_17_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G3_18_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] Normal product - Variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G3_19_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void G4_01_FlashSaleIsInProgress() throws Exception {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void G4_02_FlashSaleIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void G4_03_FlashSaleIsSchedule() throws Exception {
        flashSale.createFlashSale(productInfo, 29, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void G4_04_DiscountCampaignIsInProgress() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void G4_05_DiscountCampaignIsExpired() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
    void G4_06_DiscountCampaignIsSchedule() throws Exception {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variation stock quantity > 0
    void G4_07_HideStockAndInStock_AllVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void G4_08_HideStockAndInStock_SomeVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void G4_09_ShowStockAndInStock_AllVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void G4_10_ShowStockAndInStock_SomeVariations() throws Exception {
        productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity > 0
    void G4_11_SettingDisplayAndProductInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void G4_12_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void G4_13_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void G4_14_SettingHiddenAndAllVariationsInStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void G4_15_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void G4_16_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(true, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G4_17_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G4_18_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[STOREFRONT - PRODUCT DETAIL] IMEI product - Variation")
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void G4_19_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    void waitFlashSaleStart() {
        try {
            sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
