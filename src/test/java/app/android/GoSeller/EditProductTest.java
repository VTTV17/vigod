package app.android.GoSeller;

import api.Seller.products.all_products.CreateProduct;
import mobile.seller.android.login.LoginScreen;
import mobile.seller.android.products.edit_product.EditProductScreen;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import utilities.driver.InitAndroidDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class EditProductTest extends BaseTest {
    LoginInformation loginInformation;
    EditProductScreen editProductScreen;
    CreateProduct apiCreateProduct;
    int productId;

    @BeforeClass
    void setup() {
        // init WebDriver
        String uuid = PropertiesUtil.getEnvironmentData("udidAndroidThang");
        driver = new InitAndroidDriver().getSellerDriver(uuid);

        // init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // login to dashboard with login information
        new LoginScreen(driver).performLogin(loginInformation);

        // init product page POM
        editProductScreen = new EditProductScreen(driver);

        // init api create product
        apiCreateProduct = new CreateProduct(loginInformation);
    }

    @BeforeGroups(groups = "[UPDATE] Normal product - Without variation")
    void preCondition_G1() {
        // Get product ID
        productId = apiCreateProduct.createWithoutVariationProduct(false).getProductID();
    }

    @BeforeGroups(groups = "[UPDATE] IMEI product - Without variation")
    void preCondition_G2() {
        // Get product ID
        productId = apiCreateProduct.createWithoutVariationProduct(true).getProductID();
    }

    @BeforeGroups(groups = "[UPDATE] Normal product - Variation")
    void preCondition_G3() {
        // Get product ID
        productId = apiCreateProduct.createVariationProduct(false, 0).getProductID();
    }

    @BeforeGroups(groups = "[UPDATE] IMEI product - Variation")
    void preCondition_G4() {
        // Get product ID
        productId = apiCreateProduct.createVariationProduct(true, 0).getProductID();
    }

    //G1: Normal product without variation
    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_01_UpdateProductWithoutDimension() {
        editProductScreen.getHasDimension(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_02_UpdateProductWithDimension() {
        editProductScreen.getHasDimension(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_03_UpdateProductWithInStock() {
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_04_UpdateProductWithOutOfStock() {
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(0);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_05_UpdateProductWithDiscountPrice() {
        editProductScreen.getHasDiscount(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_06_UpdateProductWithoutDiscountPrice() {
        editProductScreen.getHasDiscount(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_07_UpdateProductWithoutCostPrice() {
        editProductScreen.getHasCostPrice(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_08_UpdateProductWithCostPrice() {
        editProductScreen.getHasCostPrice(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_09_UpdateProductWithNonePlatform() {
        editProductScreen.getProductSellingPlatform(false, false, false, false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_10_UpdateProductWithAnyPlatform() {
        editProductScreen.getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_11_UpdateProductWithoutManageByLotDate() {
        editProductScreen.getManageByLotDate(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_12_UpdateProductWithManageByLotDate() {
        editProductScreen.getManageByLotDate(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_13_UpdateProductWithPriority() {
        editProductScreen.getHasPriority(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] Normal product - Without variation")
    void UP_PRODUCT_G1_14_UpdateProductWithoutPriority() {
        editProductScreen.getHasPriority(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }


    //G2: IMEI product without variation
    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_01_UpdateProductWithoutDimension() {
        editProductScreen.getHasDimension(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_02_UpdateProductWithDimension() {
        editProductScreen.getHasDimension(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_03_UpdateProductWithInStock() {
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_04_UpdateProductWithOutOfStock() {
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(0);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_05_UpdateProductWithDiscountPrice() {
        editProductScreen.getHasDiscount(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_06_UpdateProductWithoutDiscountPrice() {
        editProductScreen.getHasDiscount(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_07_UpdateProductWithoutCostPrice() {
        editProductScreen.getHasCostPrice(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_08_UpdateProductWithCostPrice() {
        editProductScreen.getHasCostPrice(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_09_UpdateProductWithNonePlatform() {
        editProductScreen.getProductSellingPlatform(false, false, false, false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_10_UpdateProductWithAnyPlatform() {
        editProductScreen.getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_11_UpdateProductWithPriority() {
        editProductScreen.getHasPriority(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    @Test(groups = "[UPDATE] IMEI product - Without variation")
    void UP_PRODUCT_G2_12_UpdateProductWithoutPriority() {
        editProductScreen.getHasPriority(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithoutVariation(5);
    }

    //G3: Normal product with variation
    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_01_UpdateProductWithoutDimension() {
        editProductScreen.getHasDimension(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_02_UpdateProductWithDimension() {
        editProductScreen.getHasDimension(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_03_UpdateProductWithInStock() {
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_04_UpdateProductWithOutOfStock() {
        editProductScreen.getHasDimension(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(0);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_05_UpdateProductWithDiscountPrice() {
        editProductScreen.getHasDiscount(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_06_UpdateProductWithoutDiscountPrice() {
        editProductScreen.getHasDiscount(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_07_UpdateProductWithoutCostPrice() {
        editProductScreen.getHasCostPrice(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_08_UpdateProductWithCostPrice() {
        editProductScreen.getHasCostPrice(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_09_UpdateProductWithNonePlatform() {
        editProductScreen.getProductSellingPlatform(false, false, false, false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_10_UpdateProductWithAnyPlatform() {
        editProductScreen.getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_11_UpdateProductWithoutManageByLotDate() {
        editProductScreen.getManageByLotDate(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_12_UpdateProductWithManageByLotDate() {
        editProductScreen.getManageByLotDate(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_13_UpdateProductWithPriority() {
        editProductScreen.getHasPriority(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] Normal product - Variation")
    void UP_PRODUCT_G3_14_UpdateProductWithoutPriority() {
        editProductScreen.getHasPriority(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test
    void UP_PRODUCT_G3_15_UpdateProductInformationByEachVariation() {
        // Get product ID for new test
        productId = 1287599;//apiCreateProduct.createVariationProduct(false, 0).getProductID();

        // Update each variation
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateEachVariationInformation(5);
    }

    //G4: IMEI product with variation
    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_01_UpdateProductWithoutDimension() {
        editProductScreen.getHasDimension(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_02_UpdateProductWithDimension() {
        editProductScreen.getHasDimension(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_03_UpdateProductWithInStock() {
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_04_UpdateProductWithOutOfStock() {
        editProductScreen.getHasDimension(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(0);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_05_UpdateProductWithDiscountPrice() {
        editProductScreen.getHasDiscount(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_06_UpdateProductWithoutDiscountPrice() {
        editProductScreen.getHasDiscount(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_07_UpdateProductWithoutCostPrice() {
        editProductScreen.getHasCostPrice(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_08_UpdateProductWithCostPrice() {
        editProductScreen.getHasCostPrice(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_09_UpdateProductWithNonePlatform() {
        editProductScreen.getProductSellingPlatform(false, false, false, false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_10_UpdateProductWithAnyPlatform() {
        editProductScreen.getProductSellingPlatform(nextBoolean(), nextBoolean(), nextBoolean(), nextBoolean())
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_11_UpdateProductWithPriority() {
        editProductScreen.getHasPriority(true)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test(groups = "[UPDATE] IMEI product - Variation")
    void UP_PRODUCT_G4_12_UpdateProductWithoutPriority() {
        editProductScreen.getHasPriority(false)
                .navigateToProductDetailScreen(productId)
                .updateProductWithVariation(1, 1);
    }

    @Test
    void UP_PRODUCT_G4_13_UpdateProductInformationByEachVariation() {
        // Get product ID for new test
        productId = apiCreateProduct.createVariationProduct(true, 0).getProductID();

        // Update each variation
        editProductScreen.navigateToProductDetailScreen(productId)
                .updateEachVariationInformation(5);
    }
}
