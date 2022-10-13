import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.IOException;

// BH_9536:Check to display/hide if out of stock at product detail
public class BH_9536 extends BaseTest {

    @BeforeMethod
    public void setup() throws InterruptedException {
        super.setup();
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate();
    }

    @Test
    public void BH_9536_Case1_1_SettingON_InStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean displayIfOutOfStockCheckbox = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .changePriceForWithoutVariationProduct()
                .selectProductVAT()
                .selectCollections()
                .changeStockQuantityWithoutVariationNormalProduct()
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideWithoutVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case1_2_SettingON_InStock_VariationProduct() throws InterruptedException, IOException {
        int startQuantity = 1;
        boolean displayIfOutOfStockCheckbox = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .selectProductVAT()
                .selectCollections()
                .addVariations()
                .changePriceForEachVariation()
                .changeStockQuantityForEachVariationNormalProduct(startQuantity)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case2_1_SettingON_OutOfStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean displayIfOutOfStockCheckbox = true;
        int stockQuantity = 0;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .changePriceForWithoutVariationProduct()
                .selectProductVAT()
                .selectCollections()
                .changeStockQuantityWithoutVariationNormalProduct(stockQuantity)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideWithoutVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }


    @Test
    public void BH_9536_Case2_2_SettingON_OneOfVariationOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        int increaseStockForNextVariation = 1;
        boolean displayIfOutOfStockCheckbox = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .selectProductVAT()
                .selectCollections()
                .addVariations()
                .changePriceForEachVariation()
                .changeStockQuantityForEachVariationNormalProduct(startQuantity, increaseStockForNextVariation)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case2_3_SettingON_AllVariationsOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        boolean displayIfOutOfStockCheckbox = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .selectProductVAT()
                .selectCollections()
                .addVariations()
                .changePriceForEachVariation()
                .changeStockQuantityForEachVariationNormalProduct(startQuantity)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case3_1_SettingOFF_InStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean displayIfOutOfStockCheckbox = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .changePriceForWithoutVariationProduct()
                .selectProductVAT()
                .selectCollections()
                .changeStockQuantityWithoutVariationNormalProduct()
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideWithoutVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case3_2_SettingOFF_InStock_VariationProduct() throws InterruptedException, IOException {
        int startQuantity = 1;
        boolean displayIfOutOfStockCheckbox = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .selectProductVAT()
                .selectCollections()
                .addVariations()
                .changePriceForEachVariation()
                .changeStockQuantityForEachVariationNormalProduct(startQuantity)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case3_3_SettingOFF_OutOfStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean displayIfOutOfStockCheckbox = false;
        int stockQuantity = 0;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .changePriceForWithoutVariationProduct()
                .selectProductVAT()
                .selectCollections()
                .changeStockQuantityWithoutVariationNormalProduct(stockQuantity)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideWithoutVariationProduct()
                .check404PageShouldBeShownWhenProductOutOfStock()
                .completeVerify();
    }


    @Test
    public void BH_9536_Case3_4_SettingOFF_OneOfVariationOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        int increaseStockForNextVariation = 1;
        boolean displayIfOutOfStockCheckbox = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .selectProductVAT()
                .selectCollections()
                .addVariations()
                .changePriceForEachVariation()
                .changeStockQuantityForEachVariationNormalProduct(startQuantity, increaseStockForNextVariation)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideVariationProduct()
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_9536_Case3_5_SettingOFF_AllVariationsOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        boolean displayIfOutOfStockCheckbox = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .selectProductVAT()
                .selectCollections()
                .addVariations()
                .changePriceForEachVariation()
                .changeStockQuantityForEachVariationNormalProduct(startQuantity)
                .setDimension()
                .checkOnTheDisplayIfOutOfStockCheckbox(displayIfOutOfStockCheckbox)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver).checkProductIsDisplayOrHideVariationProduct()
                .check404PageShouldBeShownWhenProductOutOfStock()
                .completeVerify();
    }
}
