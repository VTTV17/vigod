import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.VAT;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.IOException;

import static utilities.api_body.product.CreateProductBody.isHideStock;

// BH_8616:Check to display/hide if out of stock at product detail
public class BH_8616 extends BaseTest {
    CreateProduct createProduct;

    @BeforeSuite
    void initPreCondition() {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();

        new BranchManagement().getBranchInformation();

        new VAT().getTaxList();
    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: Hide remaining stock on online store
    // stock quantity > 0
    public void BH_8616_G1_Case1_1_HideStockAndInStock() throws IOException {
        boolean isIMEIProduct = false;
        isHideStock = true;
        int branchStock = 5;
        createProduct.createWithoutVariationProduct(isIMEIProduct,
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
    public void BH_8616_G1_Case2_1_ShowStockAndInStock() throws IOException {
        boolean isIMEIProduct = false;
        isHideStock = false;
        int branchStock = 5;
        createProduct.createWithoutVariationProduct(isIMEIProduct,
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
    public void BH_8616_G2_Case1_1_HideStockAndInStock() throws IOException {
        boolean isIMEIProduct = true;
        isHideStock = true;
        int branchStock = 5;
        createProduct.createWithoutVariationProduct(isIMEIProduct,
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
    public void BH_8616_G2_Case2_1_ShowStockAndInStock() throws IOException {
        boolean isIMEIProduct = true;
        isHideStock = false;
        int branchStock = 5;
        createProduct.createWithoutVariationProduct(isIMEIProduct,
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
    public void BH_8616_G3_Case1_1_HideStockAndInStock_AllVariations() throws IOException {
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G3_Case1_2_HideStockAndInStock_SomeVariations() throws IOException {
        boolean isIMEIProduct = false;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G3_Case2_1_ShowStockAndInStock_AllVariations() throws IOException {
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G3_Case2_2_ShowStockAndInStock_SomeVariations() throws IOException {
        boolean isIMEIProduct = false;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G4_Case1_1_HideStockAndInStock_AllVariations() throws IOException {
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 2;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G4_Case1_2_HideStockAndInStock_SomeVariations() throws IOException {
        boolean isIMEIProduct = true;
        isHideStock = true;
        int increaseNum = 1;
        int branchStock = 0;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G4_Case2_1_ShowStockAndInStock_AllVariations() throws IOException {
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 2;
        createProduct.createVariationProduct(isIMEIProduct,
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
    public void BH_8616_G4_Case2_2_ShowStockAndInStock_SomeVariations() throws IOException {
        boolean isIMEIProduct = true;
        isHideStock = false;
        int increaseNum = 1;
        int branchStock = 0;
        createProduct.createVariationProduct(isIMEIProduct,
                increaseNum,
                branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }
}