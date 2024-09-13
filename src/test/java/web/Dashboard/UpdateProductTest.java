package web.Dashboard;

import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.StoreFront.detail_product.ProductDetailPage;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class UpdateProductTest extends BaseTest {
    LoginInformation loginInformation;
    ProductPage productPage;
    ProductDetailPage productDetailPage;
    int productId;
    ProductInfoV2 productInfo;
    APIAllProducts apiAllProducts;
    APICreateProduct apiCreateProduct;
    APIProductDetailV2 apiProductDetail;

    @BeforeClass
    void setup() {
        // init WebDriver
        driver = new InitWebdriver().getDriver(browser, headless);

        // init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // login to dashboard with login information
        new LoginPage(driver).loginDashboardByJs(loginInformation);

        // init POM
        productPage = new ProductPage(driver)
                .getLoginInformation(loginInformation)
                .setLanguage(language);
        productDetailPage = new ProductDetailPage(driver);

        // init API
        apiCreateProduct = new APICreateProduct(loginInformation);
        apiAllProducts = new APIAllProducts(loginInformation);
        apiProductDetail = new APIProductDetailV2(loginInformation);
    }

    @BeforeGroups(groups = "[WEB][UPDATE] Normal product - Without variation")
    void preCondition_G1() {
        // get product ID
        productId = apiCreateProduct.createWithoutVariationProduct(false)
                .getProductID();
    }

    @BeforeGroups(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void preCondition_G2() {
        // get product ID
        productId = apiCreateProduct.createWithoutVariationProduct(true)
                .getProductID();
    }

    @BeforeGroups(groups = "[WEB][UPDATE] Normal product - Variation")
    void preCondition_G3() {
        productId = apiCreateProduct.createVariationProduct(false, 1)
                .getProductID();
    }

    @BeforeGroups(groups = "[WEB][UPDATE] IMEI product - Variation")
    void preCondition_G4() {
        productId = apiCreateProduct.createVariationProduct(true, 0)
                .getProductID();
    }

    //G1: Normal product without variation
    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_01_UpdateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_02_UpdateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_03_UpdateProductWithInStock() throws Exception {
        productPage.navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_04_UpdateProductWithOutOfStock() throws Exception {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(0);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_05_UpdateProductWithDiscountPrice() throws Exception {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_06_UpdateProductWithoutDiscountPrice() throws Exception {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_07_UpdateProductWithoutCostPrice() {
        productPage.setHasCostPrice(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_08_UpdateProductWithCostPrice() {
        productPage.setHasCostPrice(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_09_UpdateProductWithNonePlatform() throws Exception {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_10_UpdateProductWithAnyPlatform() throws Exception {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_11_UpdateProductWithAttribution() throws Exception {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_12_UpdateProductWithoutAttribution() throws Exception {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_13_UpdateProductWithSEO() throws Exception {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_14_UpdateProductWithoutSEO() throws Exception {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_15_UpdateProductWithoutManageByLotDate() {
        productPage.setManageByLotDate(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_16_UpdateProductWithManageByLotDate() {
        productPage.setManageByLotDate(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_17_EditTranslation() throws Exception {
        productPage.editTranslation(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_18_ChangeProductStatus() throws Exception {
        productPage.changeProductStatus("INACTIVE", productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_19_AddWholesaleProduct() {
        productInfo = apiProductDetail.getInfo(productId);
        productPage.configWholesaleProduct(productInfo);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_20_AddConversionUnit() {
        productInfo = apiProductDetail.getInfo(productId);
        productPage.configConversionUnit(productInfo);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_21_DeleteProduct() throws Exception {
        productPage.deleteProduct(productId);

        new UICommonAction(driver).sleepInMiliSecond(1000, "Wait product deleted...");

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    //G2: IMEI product without variation
    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_01_UpdateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_02_UpdateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_03_UpdateProductWithInStock() throws Exception {
        productPage.navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_04_UpdateProductWithOutOfStock() throws Exception {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(0);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_05_UpdateProductWithDiscountPrice() throws Exception {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_06_UpdateProductWithoutDiscountPrice() throws Exception {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_07_UpdateProductWithoutCostPrice() {
        productPage.setHasCostPrice(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_08_UpdateProductWithCostPrice() {
        productPage.setHasCostPrice(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_09_UpdateProductWithNonePlatform() throws Exception {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_10_UpdateProductWithAnyPlatform() throws Exception {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_11_UpdateProductWithAttribution() throws Exception {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_12_UpdateProductWithoutAttribution() throws Exception {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_13_UpdateProductWithSEO() throws Exception {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_14_UpdateProductWithoutSEO() throws Exception {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateWithoutVariationProduct(5);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_15_EditTranslation() throws Exception {
        productPage.editTranslation(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_16_ChangeProductStatus() throws Exception {
        productPage.changeProductStatus("INACTIVE", productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_17_AddWholesaleProduct() {
        productInfo = apiProductDetail.getInfo(productId);
        productPage.configWholesaleProduct(productInfo);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_18_DeleteProduct() throws Exception {
        productPage.deleteProduct(productId);

        new UICommonAction(driver).sleepInMiliSecond(1000, "Wait product deleted...");

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    //G3: Normal product with variation
    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_01_UpdateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_02_UpdateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_03_UpdateProductWithInStock() throws Exception {
        productPage.navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_04_UpdateProductWithOutOfStock() throws Exception {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(0);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);

    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_05_UpdateProductWithDiscountPrice() throws Exception {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_06_UpdateProductWithoutDiscountPrice() throws Exception {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_07_UpdateProductWithoutCostPrice() {
        productPage.setHasCostPrice(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_08_UpdateProductWithCostPrice() {
        productPage.setHasCostPrice(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_09_UpdateProductWithNonePlatform() throws Exception {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_10_UpdateProductWithAnyPlatform() throws Exception {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_11_UpdateProductWithAttribution() throws Exception {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1)
                .addVariationAttribution();

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_12_UpdateProductWithoutAttribution() throws Exception {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_13_UpdateProductWithSEO() throws Exception {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_14_UpdateProductWithoutSEO() throws Exception {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_15_UpdateProductWithoutManageByLotDate() {
        productPage.setManageByLotDate(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_16_UpdateProductWithManageByLotDate() {
        productPage.setManageByLotDate(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_17_EditTranslationForMainProduct() throws Exception {
        productPage.editTranslation(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_18_EditTranslationForEachVariation() throws Exception {
        productPage.editVariationTranslation(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_19_ChangeProductStatus() throws Exception {
        productPage.changeProductStatus("INACTIVE", productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_20_ChangeVariationStatus() throws Exception {
        productPage.changeProductStatus("ACTIVE", productId).changeVariationStatus(productId);
        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_21_AddWholesaleProduct() {
        productInfo = apiProductDetail.getInfo(productId);
        productPage.configWholesaleProduct(productInfo);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_22_AddConversionUnit() {
        productInfo = apiProductDetail.getInfo(productId);
        productPage.configWholesaleProduct(productInfo);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_23_DeleteProduct() throws Exception {
        productPage.deleteProduct(productId);

        new UICommonAction(driver).sleepInMiliSecond(1000, "Wait product deleted...");

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    //G4: IMEI product with variation
    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_01_UpdateProductWithoutDimension() {
        productPage.setHasDimension(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_02_UpdateProductWitDimension() {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_03_UpdateProductWithInStock() throws Exception {
        productPage.navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_04_UpdateProductWithOutOfStock() throws Exception {
        productPage.setHasDimension(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(0);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);

    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_05_UpdateProductWithDiscountPrice() throws Exception {
        productPage.setNoDiscount(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_06_UpdateProductWithoutDiscountPrice() throws Exception {
        productPage.setNoDiscount(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_07_UpdateProductWithoutCostPrice() {
        productPage.setHasCostPrice(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_08_UpdateProductWithCostPrice() {
        productPage.setHasCostPrice(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_09_UpdateProductWithNonePlatform() throws Exception {
        productPage.setSellingPlatform(false, false, false, false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_10_UpdateProductWithAnyPlatform() throws Exception {
        productPage.setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_11_UpdateProductWithAttribution() throws Exception {
        productPage.setHasAttribution(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1)
                .addVariationAttribution();

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_12_UpdateProductWithoutAttribution() throws Exception {
        productPage.setHasAttribution(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_13_UpdateProductWithSEO() throws Exception {
        productPage.setHasSEO(true)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_14_UpdateProductWithoutSEO() throws Exception {
        productPage.setHasSEO(false)
                .navigateToUpdateProductPage(productId)
                .updateVariationProduct(1);

        // get product detail after updating
        productInfo = apiProductDetail.getInfo(productId);

        // check product information in product detail page
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_15_EditTranslationForMainProduct() throws Exception {
        productPage.editTranslation(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_16_EditTranslationForEachVariation() throws Exception {
        productPage.editVariationTranslation(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_17_ChangeProductStatus() throws Exception {
        productPage.changeProductStatus("INACTIVE", productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_18_ChangeVariationStatus() throws Exception {
        productPage.changeProductStatus("ACTIVE", productId).changeVariationStatus(productId);

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_19_AddWholesaleProduct() {
        productInfo = apiProductDetail.getInfo(productId);
        productPage.configWholesaleProduct(productInfo);
        AssertCustomize.verifyTest();
    }

    @Test(groups = "[WEB][UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_20_DeleteProduct() throws Exception {
        productPage.deleteProduct(productId);

        new UICommonAction(driver).sleepInMiliSecond(1000, "Wait product deleted...");

        productInfo = apiProductDetail.getInfo(productId);
        productDetailPage.accessToProductDetailPageByProductIDAndCheckProductInformation(loginInformation, language, productInfo, 0);
    }
}
