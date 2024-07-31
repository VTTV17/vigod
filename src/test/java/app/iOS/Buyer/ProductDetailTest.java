package app.iOS.Buyer;

import api.Seller.customers.APIAllCustomers;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.WholesaleProduct;
import api.Seller.promotion.FlashSale;
import api.Seller.promotion.ProductDiscountCampaign;
import api.Seller.setting.BranchManagement;
import mobile.buyer.iOS.login.LoginScreen;
import mobile.buyer.iOS.product_detail.ProductDetailScreen;
import org.apache.logging.log4j.LogManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import utilities.commons.UICommonIOS;
import utilities.driver.InitIOSDriver;
import utilities.model.api.promotion.productDiscountCampaign.ProductDiscountCampaignConditions;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.account.AccountTest.*;
import static utilities.environment.goBUYEREnvironment.goBUYERBundleId;

public class ProductDetailTest extends BaseTest {
    int productId;
    ProductInfo productInfo;

    LoginInformation loginInformation;
    List<Integer> branchID;
    UICommonIOS commonIOS;
    ProductDetailScreen productDetailScreen;
    ProductDiscountCampaignConditions conditions;
    FlashSale flashSale;
    ProductDiscountCampaign discountCampaign;
    int customerId;

    @BeforeClass
    void setup() {
        // Init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // Get API
        flashSale = new FlashSale(loginInformation);
        discountCampaign = new ProductDiscountCampaign(loginInformation);
        branchID = new BranchManagement(loginInformation).getInfo().getBranchID();
        customerId = new APIAllCustomers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        conditions = new ProductDiscountCampaignConditions();
        conditions.setCustomerId(customerId);

        // Init driver
        String udid = PropertiesUtil.getEnvironmentData("udidIOSThang");
        driver = new InitIOSDriver().getBuyerDriver(udid, goBUYERBundleId);

        // Init precondition
        commonIOS = new UICommonIOS(driver);
        productDetailScreen = new ProductDetailScreen(driver);
        new LoginScreen(driver).performLogin(new LoginInformation(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG));
    }

    @BeforeGroups(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void preCondition_G1() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, 5).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void preCondition_G2() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, 5).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void preCondition_G3() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, 1).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void preCondition_G4() {
        // create product for test
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, 1).getProductID();

        // get product information
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }


    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void IOS_Buyer_G1_01_FlashSaleIsInProgress() {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void IOS_Buyer_G1_02_FlashSaleIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void IOS_Buyer_G1_03_FlashSaleIsSchedule() {
        flashSale.createFlashSale(productInfo, 30 - 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void IOS_Buyer_G1_04_DiscountCampaignIsInProgress() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void IOS_Buyer_G1_05_DiscountCampaignIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Without variation")
    void IOS_Buyer_G1_06_DiscountCampaignIsSchedule() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void IOS_Buyer_G1_07_HideStockAndInStock() {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(false, true, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void IOS_Buyer_G1_08_ShowStockAndInStock() {
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createWithoutVariationProduct(false, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stockQuantity > 0
    void IOS_Buyer_G1_09_SettingDisplayAndProductInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(false, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void IOS_Buyer_G1_10_SettingDisplayAndProductOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(false).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void IOS_Buyer_G1_11_SettingHiddenAndProductInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(false, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(false, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void IOS_Buyer_G1_12_SettingHiddenAndProductOutOfStock() {
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(false, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(false, branchStock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G1_13_OneBranchActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G1_14_AllBranchesActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
            productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G1_15_AllBranchesActiveAndShowBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void IOS_Buyer_G2_01_FlashSaleIsInProgress() {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();
        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void IOS_Buyer_G2_02_FlashSaleIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void IOS_Buyer_G2_03_FlashSaleIsSchedule() {
        flashSale.createFlashSale(productInfo, 30 - 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void IOS_Buyer_G2_04_DiscountCampaignIsInProgress() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void IOS_Buyer_G2_05_DiscountCampaignIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Without variation")
    void IOS_Buyer_G2_06_DiscountCampaignIsSchedule() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void IOS_Buyer_G2_07_HideStockAndInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(true, true, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(true).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void IOS_Buyer_G2_08_ShowStockAndInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stockQuantity > 0
    void IOS_Buyer_G2_9_SettingDisplayAndProductInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void IOS_Buyer_G2_10_SettingDisplayAndProductOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createWithoutVariationProduct(true).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void IOS_Buyer_G2_11_SettingHiddenAndProductInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(true, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(true, 5).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void IOS_Buyer_G2_12_SettingHiddenAndProductOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(true, true, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createWithoutVariationProduct(true).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G2_13_OneBranchActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
            productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G2_14_AllBranchesActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
            productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G2_15_AllBranchesActiveAndShowBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 5);
        productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(true, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void IOS_Buyer_G3_01_FlashSaleIsInProgress() {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void IOS_Buyer_G3_02_FlashSaleIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void IOS_Buyer_G3_03_FlashSaleIsSchedule() {
        flashSale.createFlashSale(productInfo, 30 - 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void IOS_Buyer_G3_04_DiscountCampaignIsInProgress() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void IOS_Buyer_G3_05_DiscountCampaignIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] Normal product - Variation")
    void IOS_Buyer_G3_06_DiscountCampaignIsSchedule() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variation stock quantity > 0
    void IOS_Buyer_G3_07_HideStockAndInStock_AllVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, true, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void IOS_Buyer_G3_08_HideStockAndInStock_SomeVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, true, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(false, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void IOS_Buyer_G3_09_ShowStockAndInStock_AllVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void IOS_Buyer_G3_10_ShowStockAndInStock_SomeVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(false, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity > 0
    void IOS_Buyer_G3_11_SettingDisplayAndProductInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void IOS_Buyer_G3_12_SettingDisplayAndOneOfVariationOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void IOS_Buyer_G3_13_SettingDisplayAndAllVariationsOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(false, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(false, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void IOS_Buyer_G3_14_SettingHiddenAndAllVariationsInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(false, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void IOS_Buyer_G3_15_SettingHiddenAndOneOfVariationOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(false, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(false, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void IOS_Buyer_G3_16_SettingHiddenAndAllVariationsOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(false, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(false, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G3_17_OneBranchActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
            productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G3_18_AllBranchesActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
            productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G3_19_AllBranchesActiveAndShowBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(false, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void IOS_Buyer_G4_01_FlashSaleIsInProgress() {
        flashSale.createFlashSale(productInfo, 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();
        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void IOS_Buyer_G4_02_FlashSaleIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void IOS_Buyer_G4_03_FlashSaleIsSchedule() {
        flashSale.createFlashSale(productInfo, 30 - 1, 30);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void IOS_Buyer_G4_04_DiscountCampaignIsInProgress() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void IOS_Buyer_G4_05_DiscountCampaignIsExpired() {
        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[IOS - PRODUCT DETAIL] IMEI product - Variation")
    void IOS_Buyer_G4_06_DiscountCampaignIsSchedule() {
        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variation stock quantity > 0
    void IOS_Buyer_G4_07_HideStockAndInStock_AllVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, true, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void IOS_Buyer_G4_08_HideStockAndInStock_SomeVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, true, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(true).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void IOS_Buyer_G4_09_ShowStockAndInStock_AllVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void IOS_Buyer_G4_10_ShowStockAndInStock_SomeVariations() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setHideStock(false).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity > 0
    void IOS_Buyer_G4_11_SettingDisplayAndProductInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void IOS_Buyer_G4_12_SettingDisplayAndOneOfVariationOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void IOS_Buyer_G4_13_SettingDisplayAndAllVariationsOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(true, false, true);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(true).createVariationProduct(true, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void IOS_Buyer_G4_14_SettingHiddenAndAllVariationsInStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(true, 1, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void IOS_Buyer_G4_15_SettingHiddenAndOneOfVariationOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(true, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(true, 1).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void IOS_Buyer_G4_16_SettingHiddenAndAllVariationsOutOfStock() {
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(true, false, false);
        if (productId == 0)
            productId = new APICreateProduct(loginInformation).setShowOutOfStock(false).createVariationProduct(true, 0).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G4_17_OneBranchActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G4_18_AllBranchesActiveAndHideBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void IOS_Buyer_G4_19_AllBranchesActiveAndShowBranchOnStoreFront() {
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, 1);
        productId = new APICreateProduct(loginInformation).createVariationProduct(true, 1, stock).getProductID();
        productInfo = new APIProductDetail(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        commonIOS.relaunchApp(goBUYERBundleId);

        productDetailScreen
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @AfterMethod
    void teardown() {
        // Relaunch
        commonIOS.relaunchApp(goBUYERBundleId);
    }

    void waitFlashSaleStart() {
        try {
            sleep(60000);
            LogManager.getLogger().info("Wait discount start");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
