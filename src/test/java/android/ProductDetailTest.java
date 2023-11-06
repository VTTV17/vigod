package android;

import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.CreateProduct;
import api.dashboard.products.ProductInformation;
import api.dashboard.products.WholesaleProduct;
import api.dashboard.promotion.FlashSale;
import api.dashboard.promotion.ProductDiscountCampaign;
import api.dashboard.setting.BranchManagement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import pages.buyerapp.navigationbar.NavigationBar;
import pages.buyerapp.productDetail.BuyerProductDetailPage;
import utilities.UICommonMobile;
import utilities.driver.InitAppiumDriver;
import utilities.model.api.promotion.productDiscountCampaign.ProductDiscountCampaignConditions;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.account.AccountTest.*;

public class ProductDetailTest extends BaseTest {
    int productId;
    boolean isIMEIProduct;
    boolean isHideStock = false;
    boolean isDisplayIfOutOfStock = true;
    ProductInfo productInfo;
    String udid = "RFCW81B9BRX";
    String appPackage = "com.mediastep.shop0018";
    String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
    String URL = "http://127.0.0.1:4723/wd/hub";
    LoginInformation loginInformation;
    List<Integer> branchID;
    BuyerProductDetailPage productDetailPage;
    ProductDiscountCampaignConditions conditions;
    FlashSale flashSale;
    ProductDiscountCampaign discountCampaign;
    int customerId;
    int startMin = 1;
    int endMin = 30;

    @BeforeClass
    void setup() throws MalformedURLException {
//        tcsFileName = "android/Check product detail.xlsx".replace("/", File.separator);
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        flashSale = new FlashSale(loginInformation);
        discountCampaign = new ProductDiscountCampaign(loginInformation);
        branchID = new BranchManagement(loginInformation).getInfo().getBranchID();

        driver = new InitAppiumDriver().getAppiumDriver(udid, "ANDROID", appPackage, appActivity, URL);

        productDetailPage = new BuyerProductDetailPage(driver);

        new UICommonMobile(driver).waitSplashScreenLoaded();
        new NavigationBar(driver).tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG);
        customerId = new Customers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        conditions = new ProductDiscountCampaignConditions();
        conditions.setCustomerId(customerId);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void preCondition_G1() {
        isIMEIProduct = false;
        int branchStock = 5;
        // get product ID
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void preCondition_G2() {
        isIMEIProduct = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void preCondition_G3() {
        isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }

    @BeforeGroups(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void preCondition_G4() {
        isIMEIProduct = true;
        int branchStock = 2;
        int increaseNum = 1;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct,
                isHideStock,
                isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, branchStock)
                    .getProductID();
        // get product information
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        // add wholesale product config
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(productInfo);
    }


    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_4_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_4";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_5_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_5";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Without variation")
    void Android_Buyer_G1_Case1_6_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G1_Case1_6";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void Android_Buyer_G1_Case2_1_HideStockAndInStock() throws Exception {
        testCaseId = "Android_Buyer_G1_Case2_1";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void Android_Buyer_G1_Case2_2_ShowStockAndInStock() throws Exception {
        testCaseId = "Android_Buyer_G1_Case2_2";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage.openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stockQuantity > 0
    void Android_Buyer_G1_Case3_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "Android_Buyer_G1_Case3_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void Android_Buyer_G1_Case3_2_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G1_Case3_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        System.out.println(productId);
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        System.out.println(productInfo.isShowOutOfStock());

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void Android_Buyer_G1_Case3_3_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "Android_Buyer_G1_Case3_3";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void Android_Buyer_G1_Case3_4_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G1_Case3_4";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G1_Case4_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G1_Case4_1";
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G1_Case4_2_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G1_Case4_2";
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G1_Case4_3_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G1_Case4_3";
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_1";


        flashSale.createFlashSale(productInfo, startMin, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_4_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_4";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_5_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_5";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Without variation")
    void Android_Buyer_G2_Case1_6_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G2_Case1_6";


        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void Android_Buyer_G2_Case2_1_HideStockAndInStock() throws Exception {
        testCaseId = "Android_Buyer_G2_Case2_1";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // stock quantity > 0
    void Android_Buyer_G2_Case2_2_ShowStockAndInStock() throws Exception {
        testCaseId = "Android_Buyer_G2_Case2_2";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stockQuantity > 0
    void Android_Buyer_G2_Case3_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "Android_Buyer_G2_Case3_1";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // stock quantity = 0
    void Android_Buyer_G2_Case3_2_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G2_Case3_2";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = true;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity > 0
    void Android_Buyer_G2_Case3_3_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "Android_Buyer_G2_Case3_3";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = false;
        int branchStock = 5;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // stock quantity = 0
    void Android_Buyer_G2_Case3_4_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G2_Case3_4";
        boolean isIMEIProduct = true;
        isDisplayIfOutOfStock = false;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createWithoutVariationProduct(isIMEIProduct, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G2_Case4_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G2_Case4_1";
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G2_Case4_2_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G2_Case4_2";
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G2_Case4_3_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G2_Case4_3";
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_4_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_4";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_5_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_5";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] Normal product - Variation")
    void Android_Buyer_G3_Case1_6_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G3_Case1_6";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variation stock quantity > 0
    void Android_Buyer_G3_Case2_1_HideStockAndInStock_AllVariations() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_1";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void Android_Buyer_G3_Case2_2_HideStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_2";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void Android_Buyer_G3_Case2_3_ShowStockAndInStock_AllVariations() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_3";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void Android_Buyer_G3_Case2_4_ShowStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "Android_Buyer_G3_Case2_4";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity > 0
    void Android_Buyer_G3_Case3_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "Android_Buyer_G3_Case3_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void Android_Buyer_G3_Case3_2_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G3_Case3_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void Android_Buyer_G3_Case3_3_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G3_Case3_3";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void Android_Buyer_G3_Case3_4_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "Android_Buyer_G3_Case3_4";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void Android_Buyer_G3_Case3_5_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G3_Case3_5";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void Android_Buyer_G3_Case3_6_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G3_Case3_6";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G3_Case4_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G3_Case4_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G3_Case4_2_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G3_Case4_2";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G3_Case4_3_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G3_Case4_3";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }


    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_1_FlashSaleIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_1";

        flashSale.createFlashSale(productInfo, startMin, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);
        waitFlashSaleStart();

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_2_FlashSaleIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_2";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_3_FlashSaleIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_3";

        flashSale.createFlashSale(productInfo, endMin - 1, endMin);
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_4_DiscountCampaignIsInProgress() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_4";

        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 0);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_5_DiscountCampaignIsExpired() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_5";

        flashSale.endEarlyFlashSale();
        discountCampaign.endEarlyDiscountCampaign();

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test(groups = "[ANDROID - PRODUCT DETAIL] IMEI product - Variation")
    void Android_Buyer_G4_Case1_6_DiscountCampaignIsSchedule() throws Exception {
        testCaseId = "Android_Buyer_G4_Case1_6";


        flashSale.endEarlyFlashSale();
        discountCampaign.createProductDiscountCampaign(conditions, productInfo, 1);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variation stock quantity > 0
    void Android_Buyer_G4_Case2_1_HideStockAndInStock_AllVariations() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_1";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void Android_Buyer_G4_Case2_2_HideStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_2";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // all variations stock quantity > 0
    void Android_Buyer_G4_Case2_3_ShowStockAndInStock_AllVariations() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_3";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: Hide remaining stock on online store
        // some variations stock quantity > 0
    void Android_Buyer_G4_Case2_4_ShowStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "Android_Buyer_G4_Case2_4";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setHideStock(isHideStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity > 0
    void Android_Buyer_G4_Case3_1_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "Android_Buyer_G4_Case3_1";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // one of variation stock quantity = 0
    void Android_Buyer_G4_Case3_2_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G4_Case3_2";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: check Display if out of stock checkbox
        // all variations stock quantity = 0
    void Android_Buyer_G4_Case3_3_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G4_Case3_3";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity > 0
    void Android_Buyer_G4_Case3_4_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "Android_Buyer_G4_Case3_4";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // one of variation stock quantity = 0
    void Android_Buyer_G4_Case3_5_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G4_Case3_5";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // setting: uncheck Display if out of stock checkbox
        // all variations stock quantity = 0
    void Android_Buyer_G4_Case3_6_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "Android_Buyer_G4_Case3_6";
        boolean isIMEIProduct = false;
        isDisplayIfOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndOutOfStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).setShowOutOfStock(isDisplayIfOutOfStock).createVariationProduct(isIMEIProduct, increaseNum, branchStock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // store only active 1 branch
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G4_Case4_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G4_Case4_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G4_Case4_2_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G4_Case4_2";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void Android_Buyer_G4_Case4_3_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "Android_Buyer_G4_Case4_3";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productId = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productId == 0)
            productId = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productId);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);

        productDetailPage
                .openProductDetailScreenAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @AfterMethod
    void teardown() {
        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);
    }

    void waitFlashSaleStart() {
        try {
            sleep((long) startMin * 60 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
