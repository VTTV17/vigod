import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.LoginPage;
import pages.dashboard.products.all_products.ProductPage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductTest extends BaseTest {
    String imgFileName;
    String sellerAccount;
    String sellerPassword;
    String env;
    String language;
    String productName;
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
    int stockQuantity;
    List<String> platformList;

    String collectionName;
    Map<String, Integer> conversionMap;

    Map<Integer, List<String>> wholesaleMap;
    List<String> depositList;

    int depositPrice;

    @BeforeClass
    public void initTestData() {
        imgFileName = "img.jpg";
        sellerAccount = "stgauto@nbobd.com";
        sellerPassword = "Abc@12345";
        env = "stg";
        language = "ENG";
        productName = "New products";
        productDescription = "New product descriptions";
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
        stockQuantity = 1000000;
        conversionMap = new HashMap<>();
        conversionMap.put("10 products", 10);
        conversionMap.put("100 products", 100);
        wholesaleMap = new HashMap<>();
        wholesaleMap.put(0, List.of("wholesaleName1", "1", "1000", "Segment 1", "Segment 2"));
        wholesaleMap.put(1, List.of("wholesaleName2", "2", "1000", "Segment 2", "Segment 1"));
        depositList = List.of("D1", "D2", "D3", "D4", "D5");
        depositPrice = 10000;
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
    public void Tcs01_CreateNormalProduct() throws InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
//                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
                .setDimension(weight, length, width, height)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn();
    }

    @Test
    public void Tcs02_CreateNormalProductNoVariation_Unit_WholesalePrice_Deposit_EachVariation() throws InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
//                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .clickOnTheConfigureConversionUnitBtn()
                .configureConversionUnitForNoVariationProduct(conversionMap)
                .clickOnTheConfigureWholesalePriceBtn()
                .configureWholesalePriceForNoVariationProduct(wholesaleMap)
                .clickOnTheAddDepositBtn()
                .addDeposit(depositList)
                .uploadImageForEachVariation(imgFileName)
                .changePriceForEachDeposit(depositPrice)
                .changeStockQuantityForEachDeposit(stockQuantity)
                .changeSKUForEachDeposit()
                .selectCollections(collectionName)
                .setDimension(weight, length, width, height)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn();
    }

    @Test
    public void Tcs03_CreateNormalProductHasVariation_Unit_WholesalePrice_Deposit_EachVariation() throws InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
//                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .selectProductVAT(VAT)
                .addVariations(variations)
                .uploadImageForEachVariation(imgFileName)
                .changePriceForEachVariation(listingPrice, sellingPrice, costPrice)
                .changeStockQuantityForEachVariationNormal(stockQuantity)
                .changeSKUForEachVariation()
                .clickOnTheConfigureConversionUnitBtn()
                .configureConversionUnitForVariationProduct(conversionMap)
                .clickOnTheConfigureWholesalePriceBtn()
                .configureWholesalePriceForVariationProduct(wholesaleMap)
                .clickOnTheAddDepositBtn()
                .addDeposit(depositList)
                .uploadImageForEachVariation(imgFileName)
                .changePriceForEachDeposit(depositPrice)
                .changeStockQuantityForEachDeposit(stockQuantity)
                .changeSKUForEachDeposit()
                .selectCollections(collectionName)
                .setDimension(weight, length, width, height)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn();
    }

    @Test
    public void Tcs04_CreateNormalProductHasVariation_Unit_WholesalePrice_Deposit_AllVariations() throws InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
//                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .selectProductVAT(VAT)
                .addVariations(variations)
                .uploadImageForAllVariations(imgFileName)
                .changePriceForAllVariations(listingPrice, sellingPrice, costPrice)
                .changeStockQuantityForAllVariationsNormal(stockQuantity)
                .changeSKUForAllVariations()
                .clickOnTheConfigureConversionUnitBtn()
                .configureConversionUnitForVariationProduct(conversionMap)
                .clickOnTheConfigureWholesalePriceBtn()
                .configureWholesalePriceForVariationProduct(wholesaleMap)
                .clickOnTheAddDepositBtn()
                .addDeposit(depositList)
                .uploadImageForAllDeposits(imgFileName)
                .changePriceForAllDeposits(depositPrice)
                .changeStockQuantityForAllDeposits(stockQuantity)
                .changeSKUForAllDeposits()
                .selectCollections(collectionName)
                .setDimension(weight, length, width, height)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn();
    }

    @Test
    public void Tcs05_CreateIMEIProduct() throws InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
//                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .manageInventoryByIMEI()
                .changeStockQuantityForIMEIProduct(stockQuantity)
                .setDimension(weight, length, width, height)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn();
    }

    @Test
    public void Tcs06_CreateIMEIProductNoVariation_Unit_WholesalePrice_Deposit_EachVariation() throws InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
//                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .uploadProductImage(imgFileName)
                .manageInventoryByIMEI()
                .clickOnTheConfigureWholesalePriceBtn()
                .configureWholesalePriceForNoVariationProduct(wholesaleMap)
                .clickOnTheAddDepositBtn()
                .clickOnTheSaveBtn();
    }


    @Test(groups = "BH_4694")
    public void BH_4694_CreateNormalProductWithoutVariation_ManageInventoryByProduct() throws InterruptedException, IOException {
        String productName = "BH_4694_Case1_" + DateTimeFormatter.ofPattern("yyyy_MM_dd-hh_mm_ss").format(LocalDateTime.now());
        new ProductPage(driver).clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .uploadProductImage(imgFileName)
                .changePriceForNoVariationProduct(listingPrice, sellingPrice, costPrice)
                .selectProductVAT(VAT)
                .selectCollections(collectionName)
                .changeStockQuantityForNormalProductNoVariation(stockQuantity)
                .setDimension(weight, length, width, height)
                .setPlatForm(platformList)
                .clickOnTheSaveBtn()
                .closeNotificationPopup()
                .openProductDetailPage()
                .checkProductName(productName)
                .checkProductDescription(productDescription)
                .checkPrice(listingPrice, sellingPrice, costPrice)
                .checkVAT(VAT)
                .checkCollection(List.of(collectionName))
                .checkStock(stockQuantity)
                .checkDimension(weight, length, width, height)
                .checkSelectedPlatform(platformList)
                .completeVerify();
    }
}
