import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.IOException;

import static api.dashboard.login.Login.storeURL;
import static utilities.api_body.product.CreateProductBody.isDisplayOutOfStock;
import static utilities.links.Links.SF_DOMAIN;

// BH_9536:Check to display/hide if out of stock at product detail
public class BH_9536 extends BaseTest {

    String sfDomain;
    CreateProduct createProduct;

    @BeforeSuite
    void initPreCondition() {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        createProduct = new CreateProduct();

        createProduct.getTaxList().getActiveBranchList();

        sfDomain = "https://%s%s/".formatted(storeURL, SF_DOMAIN);

    }

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void BH_9536_G1_Case1_1_SettingDisplayAndProductInStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
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
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void BH_9536_G1_Case2_1_SettingDisplayAndProductOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
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
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void BH_9536_G1_Case3_1_SettingHiddenAndProductInStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
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
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void BH_9536_G1_Case3_2_SettingHiddenAndProductOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
        int branchStock = 0;
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
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void BH_9536_G2_Case1_1_SettingDisplayAndProductInStock() throws IOException {
        boolean isIMEIProduct = true;
        isDisplayOutOfStock = true;
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
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void BH_9536_G2_Case2_1_SettingDisplayAndProductOutOfStock() throws IOException {
        boolean isIMEIProduct = true;
        isDisplayOutOfStock = true;
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
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void BH_9536_G2_Case3_1_SettingHiddenAndProductInStock() throws IOException {
        boolean isIMEIProduct = true;
        isDisplayOutOfStock = false;
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
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void BH_9536_G2_Case3_2_SettingHiddenAndProductOutOfStock() throws IOException {
        boolean isIMEIProduct = true;
        isDisplayOutOfStock = false;
        int branchStock = 0;
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
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void BH_9536_G3_Case1_1_SettingDisplayAndProductInStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
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
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void BH_9536_G3_Case2_1_SettingDisplayAndOneOfVariationOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
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
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void BH_9536_G3_Case2_2_SettingDisplayAndAllVariationsOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
        int increaseNum = 0;
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
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void BH_9536_G3_Case3_1_SettingHiddenAndAllVariationsInStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
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
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void BH_9536_G3_Case3_2_SettingHiddenAndOneOfVariationOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
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
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void BH_9536_G3_Case3_3_SettingHiddenAndAllVariationsOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
        int increaseNum = 0;
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
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void BH_9536_G4_Case1_1_SettingDisplayAndProductInStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
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
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void BH_9536_G4_Case2_1_SettingDisplayAndOneOfVariationOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
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
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void BH_9536_G4_Case2_2_SettingDisplayAndAllVariationsOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = true;
        int increaseNum = 0;
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
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void BH_9536_G4_Case3_1_SettingHiddenAndAllVariationsInStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
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
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void BH_9536_G4_Case3_2_SettingHiddenAndOneOfVariationOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
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
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void BH_9536_G4_Case3_3_SettingHiddenAndAllVariationsOutOfStock() throws IOException {
        boolean isIMEIProduct = false;
        isDisplayOutOfStock = false;
        int increaseNum = 0;
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
