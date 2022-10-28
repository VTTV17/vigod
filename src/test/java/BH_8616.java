import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.IOException;

// BH_8616:Check to display/hide if out of stock at product detail
public class BH_8616 extends BaseTest {

    @BeforeMethod
    public void setup() throws InterruptedException {
        super.setup();
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage()
                .navigate();
    }

    @Test
    public void BH_8616_Case1_1_SettingON_InStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean hideRemainingStock = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .changePriceForWithoutVariationProduct()
                .selectProductVAT()
                .selectCollections()
                .changeStockQuantityWithoutVariationNormalProduct()
                .setDimension()
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case1_2_SettingON_InStock_VariationProduct() throws InterruptedException, IOException {
        boolean hideRemainingStock = true;
        int startQuantity = 1;
        int increaseStockForNextVariation = 1;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideVariationProduct()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case1_3_SettingON_OutOfStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean hideRemainingStock = true;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }


    @Test
    public void BH_8616_Case1_4_SettingON_OneOfVariationOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        int increaseStockForNextVariation = 1;
        boolean hideRemainingStock = true;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideVariationProduct()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case1_5_SettingON_AllVariationsOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        boolean hideRemainingStock = true;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideVariationProduct()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_1_SettingOFF_InStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean hideRemainingStock = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName()
                .inputProductDescription()
                .uploadProductImage(imgFileName)
                .changePriceForWithoutVariationProduct()
                .selectProductVAT()
                .selectCollections()
                .changeStockQuantityWithoutVariationNormalProduct()
                .setDimension()
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_2_SettingOFF_InStock_VariationProduct() throws InterruptedException, IOException {
        int startQuantity = 1;
        boolean hideRemainingStock = false;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideVariationProduct()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_3_SettingOFF_OutOfStock_WithoutVariationProduct() throws InterruptedException, IOException {
        boolean hideRemainingStock = false;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct()
                .checkWithoutVariationProductInformation()
                .completeVerify();
    }


    @Test
    public void BH_8616_Case2_4_SettingOFF_OneOfVariationOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        int increaseStockForNextVariation = 1;
        boolean hideRemainingStock = false;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideVariationProduct()
                .checkVariationProductInformation()
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_5_SettingOFF_AllVariationsOutOfStock() throws InterruptedException, IOException {
        int startQuantity = 0;
        boolean hideRemainingStock = false;
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
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm()
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .verifyPageLoaded()
                .checkRemainingStockIsDisplayOrHideVariationProduct()
                .checkVariationProductInformation()
                .completeVerify();
    }
}