import api.dashboard.customers.Customers;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.CreateProduct;
import api.dashboard.products.ProductInformation;
import api.dashboard.setting.BranchManagement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.login.LoginPage;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;

public class BH_9753 extends BaseTest {
    List<Integer> branchID;
    boolean isHideStock = false;
    boolean isDisplayIfOutOfStock = true;
    int productID;
    ProductInfo productInfo;
    LoginInformation loginInformation;
    int customerId;

    @BeforeClass
    void setup() {
        loginInformation = new LoginInformation();
        loginInformation.setEmail(ADMIN_ACCOUNT_THANG);
        loginInformation.setPassword(ADMIN_PASSWORD_THANG);
        driver = new InitWebdriver().getDriver(browser, headless);
        customerId = new Customers(loginInformation).getCustomerID(BUYER_ACCOUNT_THANG);
        new LoginPage(driver).performLoginJS(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, loginInformation);
        tcsFileName = "check_product_detail_sf/BH_9753_Search and view branchs in product detail page.xlsx".replace("/", File.separator);
        branchID = new BranchManagement(loginInformation).getInfo().getBranchID();
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // store only active 1 branch
    // setting: Hide free branch on shop online
    // stock quantity > 0
    void BH_9753_G1_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G1_Case1_1";
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G1_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G1_Case2_1";
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);

        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G1_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G1_Case3_1";
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G2: IMEI product - without variation
    @Test
    // Pre-condition:
    // store only active 1 branch
    // setting: Hide free branch on shop online
    // stock quantity > 0
    void BH_9753_G2_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G2_Case1_1";
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G2_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G2_Case2_1";
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G2_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G3_Case1_1";
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createWithoutVariationProduct(isIMEIProduct, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G3: Normal product - Variation
    @Test
    // Pre-condition:
    // store only active 1 branch
    // setting: Hide free branch on shop online
    // stock quantity > 0
    void BH_9753_G3_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G3_Case1_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G3_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G3_Case2_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G3_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G3_Case3_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    // G4: IMEI product - Variation
    @Test
    // Pre-condition:
    // store only active 1 branch
    // setting: Hide free branch on shop online
    // stock quantity > 0
    void BH_9753_G4_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G4_Case1_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G4_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G4_Case2_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }

    @Test
        // Pre-condition:
        // Active all branches
        // setting: Hide free branch on shop online
        // stock quantity > 0
    void BH_9753_G4_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        testCaseId = "BH_9753_G4_Case3_1";
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
        Arrays.fill(stock, branchStock);
        productID = new APIAllProducts(loginInformation).getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        if (productID == 0) productID = new CreateProduct(loginInformation).createVariationProduct(isIMEIProduct, increaseNum, stock).getProductID();
        productInfo = new ProductInformation(loginInformation).getInfo(productID);
        new BranchManagement(loginInformation).showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, customerId);
    }
}
