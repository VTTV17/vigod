import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.VAT;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;

import java.util.Arrays;

import static api.dashboard.setting.BranchManagement.branchID;

public class BH_9753 extends BaseTest{
    CreateProduct createProduct;
    BranchManagement branchManagement;

    @BeforeSuite
    void initPreCondition() {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();
        branchManagement = new BranchManagement();

        branchManagement.getBranchInformation();

        new VAT().getTaxList();
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // store only active 1 branch
    // setting: Hide free branch on shop online
    // stock quantity > 0
    public void BH_9753_G1_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G1_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G1_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G2_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G2_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G2_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = true;
        int branchStock = 5;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G3_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G3_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G3_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G4_Case1_1_OneBranchActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G4_Case2_1_AllBranchesActiveAndHideBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
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
    public void BH_9753_G4_Case3_1_AllBranchesActiveAndShowBranchOnStoreFront() throws Exception {
        boolean isIMEIProduct = false;
        int branchStock = 2;
        int increaseNum = 1;
        int[] stock = new int[branchID.size()];
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
