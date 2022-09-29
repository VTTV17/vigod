import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.detail_product.ProductDetailPage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// BH_8616:Check to display/hide if out of stock at product detail
public class BH_8616 extends BaseTest {
    String productDescription;
    String VAT;
    Map<String, List<String>> variations;
    int listingPrice;
    int sellingPrice;
    int costPrice;
    int weight;
    int length;
    int width;
    int height;
    String currencySymbol;
    List<String> platformList;
    String collectionName;
    Map<String, Integer> conversionMap;

    Map<Integer, List<String>> wholesaleMap;
    List<String> depositList;

    int depositPrice;

    @BeforeClass
    public void initTestData() {
        productDescription = """
                What is Lorem Ipsum?
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Elettra sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                Why do we use it?
                It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).

                Where does it come from?
                Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClinton, a Latin professor at Hampered-Sydney College in Virginia, looked up one of the more obscure Latin words, consecrate, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubted source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of "de Minibus Bono-rum et Malo-rum" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, "Lorem ipsum dolor sit amet..", comes from a line in section 1.10.32.
                The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from "de Minibus Bono-rum et Malo-rum" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rack-ham.
                                
                Where can I get some?
                There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc.""";
        listingPrice = 10000;
        sellingPrice = 9000;
        costPrice = 8000;
        VAT = "VAT 2";
        variations = new HashMap<>();
        variations.put("Size", List.of("S", "M", "L"));
        variations.put("Color", List.of("Red", "White", "Blue"));
        collectionName = "Manual Collections 1";
        weight = 100;
        length = 100;
        width = 100;
        height = 100;
        platformList = List.of("In-Store", "App", "Web");
        conversionMap = new HashMap<>();
        conversionMap.put("10 products", 10);
        conversionMap.put("100 products", 100);
        wholesaleMap = new HashMap<>();
        wholesaleMap.put(0, List.of("wholesaleName1", "1", "1000", "Segment 1", "Segment 2"));
        wholesaleMap.put(1, List.of("wholesaleName2", "2", "1000", "Segment 2", "Segment 1"));
        depositList = List.of("D1", "D2", "D3", "D4", "D5");
        depositPrice = 10000;
        currencySymbol = "đ";
    }

    @BeforeMethod
    public void setup() throws InterruptedException {
        super.setup();
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
                .waitAndHideFacebookBubble();
    }

    @Test
    public void BH_8616_Case1_1_SettingON_InStock_WithoutVariationProduct() throws InterruptedException, IOException {
        String productName = "BH_8616_Case1_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        boolean hideRemainingStock = true;
        int stockQuantity = 1000000;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct(hideRemainingStock, stockQuantity)
                .checkAllInformationIsDisplayedProperlyWithoutVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, stockQuantity, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case1_2_SettingON_InStock_WithVariationProduct() throws InterruptedException, IOException {
        String productName = "BH_8616_Case1_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        boolean hideRemainingStock = true;
        int startQuantity = 1;
        int increaseStockForNextVariation = 1;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .addVariations(variations)
                .changeStockQuantityForEachVariationNormal(startQuantity, increaseStockForNextVariation)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideVariationProduct(hideRemainingStock, startQuantity, increaseStockForNextVariation)
                .checkAllInformationIsDisplayedProperlyVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, variations, startQuantity, increaseStockForNextVariation, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case1_3_SettingON_OutOfStock_WithoutVariationProduct() throws InterruptedException, IOException {
        String productName = "BH_8616_Case1_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        boolean hideRemainingStock = true;
        int stockQuantity = 0;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct(hideRemainingStock, stockQuantity)
                .checkAllInformationIsDisplayedProperlyWithoutVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, stockQuantity, productDescription)
                .completeVerify();
    }


    @Test
    public void BH_8616_Case1_4_SettingON_OneOfVariationOutOfStock() throws InterruptedException, IOException {
        String productName = "BH_8616_Case1_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        int startQuantity = 0;
        int increaseStockForNextVariation = 1;
        boolean hideRemainingStock = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .addVariations(variations)
                .changeStockQuantityForEachVariationNormal(startQuantity, increaseStockForNextVariation)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideVariationProduct(hideRemainingStock, startQuantity, increaseStockForNextVariation)
                .checkAllInformationIsDisplayedProperlyVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, variations, startQuantity, increaseStockForNextVariation, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case1_5_SettingON_AllVariationsOutOfStock() throws InterruptedException, IOException {
        String productName = "BH_8616_Case1_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        int startQuantity = 0;
        int increaseStockForNextVariation = 0;
        boolean hideRemainingStock = true;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .addVariations(variations)
                .changeStockQuantityForEachVariationNormal(startQuantity, increaseStockForNextVariation)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideVariationProduct(hideRemainingStock, startQuantity, increaseStockForNextVariation)
                .checkAllInformationIsDisplayedProperlyVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, variations, startQuantity, increaseStockForNextVariation, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_1_SettingOFF_InStock_WithoutVariationProduct() throws InterruptedException, IOException {
        String productName = "BH_8616_Case2_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        boolean hideRemainingStock = false;
        int stockQuantity = 1000000;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct(hideRemainingStock, stockQuantity)
                .checkAllInformationIsDisplayedProperlyWithoutVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, stockQuantity, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_2_SettingOFF_InStock_WithVariationProduct() throws InterruptedException, IOException {
        String productName = "BH_8616_Case2_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        int startQuantity = 1;
        int increaseStockForNextVariation = 1;
        boolean hideRemainingStock = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .addVariations(variations)
                .changeStockQuantityForEachVariationNormal(startQuantity, increaseStockForNextVariation)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideVariationProduct(hideRemainingStock, startQuantity, increaseStockForNextVariation)
                .checkAllInformationIsDisplayedProperlyVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, variations, startQuantity, increaseStockForNextVariation, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_3_SettingOFF_OutOfStock_WithoutVariationProduct() throws InterruptedException, IOException {
        String productName = "BH_8616_Case2_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        boolean hideRemainingStock = false;
        int stockQuantity = 0;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideWithoutVariationProduct(hideRemainingStock, stockQuantity)
                .checkAllInformationIsDisplayedProperlyWithoutVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, stockQuantity, productDescription)
                .completeVerify();
    }


    @Test
    public void BH_8616_Case2_4_SettingOFF_OneOfVariationOutOfStock() throws InterruptedException, IOException {
        String productName = "BH_8616_Case2_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        int startQuantity = 0;
        int increaseStockForNextVariation = 1;
        boolean hideRemainingStock = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .addVariations(variations)
                .changeStockQuantityForEachVariationNormal(startQuantity, increaseStockForNextVariation)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideVariationProduct(hideRemainingStock, startQuantity, increaseStockForNextVariation)
                .checkAllInformationIsDisplayedProperlyVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, variations, startQuantity, increaseStockForNextVariation, productDescription)
                .completeVerify();
    }

    @Test
    public void BH_8616_Case2_5_SettingOFF_AllVariationsOutOfStock() throws InterruptedException, IOException {
        String productName = "BH_8616_Case2_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        int startQuantity = 0;
        int increaseStockForNextVariation = 0;
        boolean hideRemainingStock = false;
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .addVariations(variations)
                .changeStockQuantityForEachVariationNormal(startQuantity, increaseStockForNextVariation)
                .setDimension(weight, length, width, height)
                .checkOnTheHideRemainingStockOnOnlineStoreCheckbox(hideRemainingStock)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .getURLAndNavigateToStoreFront();

        new ProductDetailPage(driver)
                .accessToProductDetailPageByURL()
                .checkRemainingStockIsDisplayOrHideVariationProduct(hideRemainingStock, startQuantity, increaseStockForNextVariation)
                .checkAllInformationIsDisplayedProperlyVariationProduct(productName, listingPrice, sellingPrice, currencySymbol, variations, startQuantity, increaseStockForNextVariation, productDescription)
                .completeVerify();
    }
}
