import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import api.dashboard.setting.VAT;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.File;
import java.util.Arrays;

import static api.dashboard.setting.BranchManagement.apiBranchID;

public class BH_9753 extends BaseTest{
    CreateProduct createProduct;
    BranchManagement branchManagement;

    @BeforeSuite
    void initPreCondition() {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();
        branchManagement = new BranchManagement();

        branchManagement.getBranchInformation();

        new StoreInformation().getStoreInformation();

        new VAT().getTaxList();

        tcsFileName = "check_product_detail_sf/BH_9753_Search and view branchs in product detail page.xlsx".replace("/", File.separator);
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createWithoutVariationProduct(isIMEIProduct, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createWithoutVariationProduct(isIMEIProduct, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createWithoutVariationProduct(isIMEIProduct, stock);

        branchManagement.showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createWithoutVariationProduct(isIMEIProduct, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createWithoutVariationProduct(isIMEIProduct, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createWithoutVariationProduct(isIMEIProduct, stock);

        branchManagement.showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createVariationProduct(isIMEIProduct, increaseNum, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createVariationProduct(isIMEIProduct, increaseNum, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createVariationProduct(isIMEIProduct, increaseNum, stock);

        branchManagement.showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createVariationProduct(isIMEIProduct, increaseNum, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createVariationProduct(isIMEIProduct, increaseNum, stock);

        branchManagement.hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
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
        int[] stock = new int[apiBranchID.size()];
        Arrays.fill(stock, branchStock);
        createProduct.createVariationProduct(isIMEIProduct, increaseNum, stock);

        branchManagement.showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }


}
