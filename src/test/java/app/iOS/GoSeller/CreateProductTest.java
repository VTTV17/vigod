package app.iOS.GoSeller;

import mobile.seller.iOS.login.LoginScreen;
import mobile.seller.iOS.products.create_product.CreateProductScreen;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitIOSDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class CreateProductTest extends BaseTest {
    LoginInformation loginInformation;
    CreateProductScreen createProductScreen;

    @BeforeClass
    void setup() {
        // init WebDriver
        String udid = PropertiesUtil.getEnvironmentData("udidIOSThang");
        driver = new InitIOSDriver().getSellerDriver(udid);

        // init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // login to dashboard with login information
        new LoginScreen(driver).performLogin(loginInformation);

        // init product page POM
        createProductScreen = new CreateProductScreen(driver);
    }

    //G1: Normal product without variation
    @Test
    void CR_PRODUCT_G1_01_CreateProductWithoutDimension() {
        createProductScreen.getManageByIMEI(false)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_02_CreateProductWithDimension() {
        createProductScreen.getManageByIMEI(false)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_03_CreateProductWithInStock() {
        createProductScreen.getManageByIMEI(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_04_CreateProductWithOutOfStock() {
        createProductScreen.getManageByIMEI(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(0);
    }

    @Test
    void CR_PRODUCT_G1_01_CreateProductWithDiscountPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_06_CreateProductWithoutDiscountPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_07_CreateProductWithoutCostPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_08_CreateProductWithCostPrice() {
        createProductScreen.getManageByIMEI(false).getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_09_CreateProductWithNonePlatform() {
        createProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_10_CreateProductWithAnyPlatform() {
        createProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_11_CreateProductWithManageByLotDate() {
        createProductScreen.getManageByIMEI(false)
                .getManageByLotDate(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_12_CreateProductWithoutManageByLotDate() {
        createProductScreen.getManageByIMEI(false)
                .getManageByLotDate(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_13_CreateProductWithPriority() {
        createProductScreen.getManageByIMEI(false)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G1_14_CreateProductWithoutPriority() {
        createProductScreen.getManageByIMEI(false)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }


    //G2: IMEI product without variation
    @Test
    void CR_PRODUCT_G2_01_CreateProductWithoutDimension() {
        createProductScreen.getManageByIMEI(true)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_02_CreateProductWithDimension() {
        createProductScreen.getManageByIMEI(true)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_03_CreateProductWithInStock() {
        createProductScreen.getManageByIMEI(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_04_CreateProductWithOutOfStock() {
        createProductScreen.getManageByIMEI(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(0);
    }

    @Test
    void CR_PRODUCT_G2_01_CreateProductWithDiscountPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_06_CreateProductWithoutDiscountPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_07_CreateProductWithoutCostPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_08_CreateProductWithCostPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_09_CreateProductWithNonePlatform() {
        createProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_10_CreateProductWithAnyPlatform() {
        createProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_11_CreateProductWithPriority() {
        createProductScreen.getManageByIMEI(true)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    @Test
    void CR_PRODUCT_G2_12_CreateProductWithoutPriority() {
        createProductScreen.getManageByIMEI(true)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithoutVariation(1);
    }

    //G3: Normal product with variation
    @Test
    void CR_PRODUCT_G3_01_CreateProductWithoutDimension() {
        createProductScreen.getManageByIMEI(false)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_02_CreateProductWithDimension() {
        createProductScreen.getManageByIMEI(false)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_03_CreateProductWithInStock() {
        createProductScreen.getManageByIMEI(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_04_CreateProductWithOutOfStock() {
        createProductScreen.getManageByIMEI(false).getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(0);
    }

    @Test
    void CR_PRODUCT_G3_01_CreateProductWithDiscountPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_06_CreateProductWithoutDiscountPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_07_CreateProductWithoutCostPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_08_CreateProductWithCostPrice() {
        createProductScreen.getManageByIMEI(false)
                .getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_09_CreateProductWithNonePlatform() {
        createProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_10_CreateProductWithAnyPlatform() {
        createProductScreen.getManageByIMEI(false)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_11_CreateProductWithManageByLotDate() {
        createProductScreen.getManageByIMEI(false)
                .getManageByLotDate(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_12_CreateProductWithoutManageByLotDate() {
        createProductScreen.getManageByIMEI(false)
                .getManageByLotDate(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_13_CreateProductWithPriority() {
        createProductScreen.getManageByIMEI(false)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G3_14_CreateProductWithoutPriority() {
        createProductScreen.getManageByIMEI(false)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    //G4: IMEI product with variation
    @Test
    void CR_PRODUCT_G4_01_CreateProductWithoutDimension() {
        createProductScreen.getManageByIMEI(true)
                .getHasDimension(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_02_CreateProductWithDimension() {
        createProductScreen.getManageByIMEI(true)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_03_CreateProductWithInStock() {
        createProductScreen.getManageByIMEI(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_04_CreateProductWithOutOfStock() {
        createProductScreen.getManageByIMEI(true)
                .getHasDimension(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(0);
    }

    @Test
    void CR_PRODUCT_G4_05_CreateProductWithDiscountPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasDiscount(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_06_CreateProductWithoutDiscountPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasDiscount(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_07_CreateProductWithoutCostPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasCostPrice(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_08_CreateProductWithCostPrice() {
        createProductScreen.getManageByIMEI(true)
                .getHasCostPrice(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_09_CreateProductWithNonePlatform() {
        createProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(false, false, false, false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_10_CreateProductWithAnyPlatform() {
        createProductScreen.getManageByIMEI(true)
                .getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_11_CreateProductWithPriority() {
        createProductScreen.getManageByIMEI(true)
                .getHasPriority(true)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }

    @Test
    void CR_PRODUCT_G4_12_CreateProductWithoutPriority() {
        createProductScreen.getManageByIMEI(true)
                .getHasPriority(false)
                .navigateToCreateProductScreen()
                .createProductWithVariation(1, 1);
    }
}
