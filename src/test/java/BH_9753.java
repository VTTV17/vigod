import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.driver.InitWebdriver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class BH_9753 extends BaseTest {
    List<Integer> branchID;

    @BeforeClass
    void setup() {
        new Login().loginToDashboardByMail(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        driver = new InitWebdriver().getDriver(browser, headless);
        tcsFileName = "check_product_detail_sf/BH_9753_Search and view branchs in product detail page.xlsx".replace("/", File.separator);
        branchID = new BranchManagement().getInfo().getBranchID();
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
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, stock);

        new BranchManagement().showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct, stock);

        new BranchManagement().showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, stock);

        new BranchManagement().showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .inactiveAllPaidBranches();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, stock);

        new BranchManagement().hideFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
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
        new CreateProduct().createVariationProduct(isIMEIProduct, increaseNum, stock);

        new BranchManagement().showFreeBranchOnShopOnline()
                .activeAndShowAllPaidBranchesOnShopOnline();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductIDAndCheckProductInformation(language);
    }
}
