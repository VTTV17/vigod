import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;

import static pages.dashboard.products.all_products.ProductPage.uiIsDisplayOutOfStock;

public class CreateProductTest extends BaseTest {

    // G1: Normal product - without variation
    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stockQuantity > 0
    public void CR_PRODUCT_G1_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G1_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_G1_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G1_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G1_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

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
    public void CR_PRODUCT_G2_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_01";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G2_02_SettingDisplayAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_02";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = true;
        int branchStock = 5;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity > 0
    public void CR_PRODUCT_G2_03_SettingHiddenAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_03";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = false;
        int branchStock = 5;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // stock quantity = 0
    public void CR_PRODUCT_G2_04_SettingHiddenAndProductOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G2_04";
        boolean isIMEIProduct = true;
        uiIsDisplayOutOfStock = false;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createWithoutVariationProduct(isIMEIProduct, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    // G3: Normal product - Variation
    @Test()
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G3_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new LoginPage(driver).navigate()
                .performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G3_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G3_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G3_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G3_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_05";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G3_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G3_06";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

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
    public void CR_PRODUCT_G4_01_SettingDisplayAndProductInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_01";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 2;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G4_02_SettingDisplayAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_02";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 1;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: check Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G4_03_SettingDisplayAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_03";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = true;
        int increaseNum = 0;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity > 0
    public void CR_PRODUCT_G4_04_SettingHiddenAndAllVariationsInStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_04";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 2;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // one of variation stock quantity = 0
    public void CR_PRODUCT_G4_05_SettingHiddenAndOneOfVariationOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_05";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 1;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    // Pre-condition:
    // setting: uncheck Display if out of stock checkbox
    // all variations stock quantity = 0
    public void CR_PRODUCT_G4_06_SettingHiddenAndAllVariationsOutOfStock() throws Exception {
        testCaseId = "CR_PRODUCT_G4_06";
        boolean isIMEIProduct = false;
        uiIsDisplayOutOfStock = false;
        int increaseNum = 0;
        int branchStock = 0;

        new LoginPage(driver).navigate().performLogin(sellerAccount, sellerPassword)
                .getDashboardInformation();
        new HomePage(driver).waitTillSpinnerDisappear();

        new ProductPage(driver).navigateToCreateProductPage()
                .createVariationProduct(isIMEIProduct, increaseNum, branchStock);

        new ProductDetailPage(driver)
                .accessToProductDetailPageByProductID()
                .checkVariationProductInformation()
                .completeVerify();
    }

}
