package web.Dashboard;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.products.all_products.crud.ProductPage;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class CreateProductTest extends BaseTest {
    LoginInformation loginInformation;
    ProductPage productPage;

    @BeforeClass
    void setup() {
        // init WebDriver
        driver = new InitWebdriver().getDriver(browser, headless);

        // init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // init product page POM
        productPage = new ProductPage(driver).getLoginInformation(loginInformation);

        // login to dashboard with login information
        new LoginPage(driver).loginDashboardByJs(loginInformation);
    }

    //G1: Normal product without variation
    @Test
    void CR_PRODUCT_G1_01_CreateProductWithoutDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_02_CreateProductWitDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_03_CreateProductWithInStock() throws Exception {
        productPage.setLanguage(language)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_04_CreateProductWithOutOfStock() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G1_05_CreateProductWithDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_06_CreateProductWithoutDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_07_CreateProductWithoutCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_08_CreateProductWithCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_09_CreateProductWithNonePlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_10_CreateProductWithAnyPlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_11_CreateProductWithAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_12_CreateProductWithoutAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_13_CreateProductWithSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_14_CreateProductWithoutSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }


    @Test
    void CR_PRODUCT_G1_15_CreateProductWithManageByLotDate() throws Exception {
        productPage.setLanguage(language)
                .setManageByLotDate(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    @Test
    void CR_PRODUCT_G1_16_CreateProductWithoutManageByLotDate() throws Exception {
        productPage.setLanguage(language)
                .setManageByLotDate(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 5);
    }

    //G2: IMEI product without variation
    @Test
    void CR_PRODUCT_G2_01_CreateProductWithoutDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_02_CreateProductWitDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_03_CreateProductWithInStock() throws Exception {
        productPage.setLanguage(language)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_04_CreateProductWithOutOfStock() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G2_05_CreateProductWithDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_06_CreateProductWithoutDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_07_CreateProductWithoutCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_08_CreateProductWithCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_09_CreateProductWithNonePlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_10_CreateProductWithAnyPlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_11_CreateProductWithAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_12_CreateProductWithoutAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_13_CreateProductWithSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    @Test
    void CR_PRODUCT_G2_14_CreateProductWithoutSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(false)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(true, 5);
    }

    //G3: Normal product with variation
    @Test
    void CR_PRODUCT_G3_01_CreateProductWithoutDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_02_CreateProductWitDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_03_CreateProductWithInStock() throws Exception {
        productPage.setLanguage(language)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_04_CreateProductWithOutOfStock() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G3_05_CreateProductWithDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_06_CreateProductWithoutDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_07_CreateProductWithoutCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_08_CreateProductWithCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_09_CreateProductWithNonePlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_10_CreateProductWithAnyPlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_11_CreateProductWithAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_12_CreateProductWithoutAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_13_CreateProductWithSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_14_CreateProductWithoutSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }


    @Test
    void CR_PRODUCT_G3_15_CreateProductWithManageByLotDate() throws Exception {
        productPage.setLanguage(language)
                .setManageByLotDate(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G3_16_CreateProductWithoutManageByLotDate() throws Exception {
        productPage.setLanguage(language)
                .setManageByLotDate(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    //G4: IMEI product with variation
    @Test
    void CR_PRODUCT_G4_01_CreateProductWithoutDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_02_CreateProductWitDimension() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_03_CreateProductWithInStock() throws Exception {
        productPage.setLanguage(language)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_04_CreateProductWithOutOfStock() throws Exception {
        productPage.setLanguage(language)
                .setHasDimension(true)
                .navigateToCreateProductPage()
                .createWithoutVariationProduct(false, 0);
    }

    @Test
    void CR_PRODUCT_G4_05_CreateProductWithDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_06_CreateProductWithoutDiscountPrice() throws Exception {
        productPage.setLanguage(language)
                .setNoDiscount(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_07_CreateProductWithoutCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_08_CreateProductWithCostPrice() throws Exception {
        productPage.setLanguage(language)
                .setHasCostPrice(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_09_CreateProductWithNonePlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(false, false, false, false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_10_CreateProductWithAnyPlatform() throws Exception {
        productPage.setLanguage(language)
                .setSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_11_CreateProductWithAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(true)
                .navigateToCreateProductPage()
                .createVariationProduct(true, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_12_CreateProductWithoutAttribution() throws Exception {
        productPage.setLanguage(language)
                .setHasAttribution(false)
                .navigateToCreateProductPage()
                .createVariationProduct(true, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_13_CreateProductWithSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(true)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }

    @Test
    void CR_PRODUCT_G4_14_CreateProductWithoutSEO() throws Exception {
        productPage.setLanguage(language)
                .setHasSEO(false)
                .navigateToCreateProductPage()
                .createVariationProduct(false, 1, 1);
    }
}
