import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import api.dashboard.setting.VAT;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.File;

import static utilities.api_body.product.CreateProductBody.isHideStock;

// BH_8616:Check to display/hide if out of stock at product detail
public class BH_8616 extends BaseTest {

    @BeforeSuite
    void initPreCondition() {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        new BranchManagement().getBranchInformation();

        new VAT().getTaxList();

        new StoreInformation().getStoreInformation();

        tcsFileName = "check_product_detail_sf/BH_8616_Check hide remaining stock on online store.xlsx".replace("/", File.separator);
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    public void BH_8616_G1_Case1_1_HideStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G1_Case1_1";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int branchStock = 5;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
                        branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    public void BH_8616_G1_Case2_1_ShowStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G1_Case2_1";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int branchStock = 5;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    // G2: IMEI product - without variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    public void BH_8616_G2_Case1_1_HideStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G2_Case1_1";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int branchStock = 5;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
                        branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    public void BH_8616_G2_Case2_1_ShowStockAndInStock() throws Exception {
        testCaseId = "BH_8616_G2_Case2_1";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int branchStock = 5;
        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    // G3: Normal product - Variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // all variation stock quantity > 0
    public void BH_8616_G3_Case1_1_HideStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case1_1";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // some variations stock quantity > 0
    public void BH_8616_G3_Case1_2_HideStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case1_2";
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // all variations stock quantity > 0
    public void BH_8616_G3_Case2_1_ShowStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case2_1";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // some variations stock quantity > 0
    public void BH_8616_G3_Case2_2_ShowStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G3_Case2_2";
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }


    // G4: IMEI product - Variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // all variation stock quantity > 0
    public void BH_8616_G4_Case1_1_HideStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case1_1";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // some variations stock quantity > 0
    public void BH_8616_G4_Case1_2_HideStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case1_2";
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // all variations stock quantity > 0
    public void BH_8616_G4_Case2_1_ShowStockAndInStock_AllVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case2_1";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // some variations stock quantity > 0
    public void BH_8616_G4_Case2_2_ShowStockAndInStock_SomeVariations() throws Exception {
        testCaseId = "BH_8616_G4_Case2_2";
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        new CreateProduct().createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }
}