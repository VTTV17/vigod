import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.LoginPage;
import pages.dashboard.products.all_products.ProductPage;

import java.io.IOException;
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
    int vatID;
    Map<String, List<String>> variations;
    int listingPrice;
    int sellingPrice;
    int costPrice;
    int weight;
    int length;
    int width;
    int height;
    int stockQuantity;
    List<String> platforms;

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
        vatID = 2;
        variations = new HashMap<>();
        variations.put("Size", List.of("S", "M", "L"));
        variations.put("Color", List.of("Red", "White", "Blue"));
        collectionName = "Manual Collections 1";
        weight = 100;
        length = 100;
        width = 100;
        height = 100;
        platforms = List.of("In-Store", "App", "Web", "GoSOCIAL");
        stockQuantity = 1000;
        conversionMap = new HashMap<>();
        conversionMap.put("10 products", 10);
        conversionMap.put("100 products", 100);
        wholesaleMap = new HashMap<>();
        wholesaleMap.put(0, List.of("wholesaleName1", "1", "1000", "Segment 1", "Segment 2"));
        wholesaleMap.put(1, List.of("wholesaleName2", "2", "1000", "Segment 2", "Segment 1"));
        depositList = List.of("D1", "D2", "D3", "D4", "D5");
        depositPrice = 10000;
    }

    @Test
    public void Tcs01_CreateProduct() throws IOException, InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
                .waitAndHideFacebookBubble()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
//                .selectProductVAT(vatID)
//                .manageInventoryByIMEI()
//                .setInventoryByNormalProduct(stockQuantity)
//                .inputPriceNormalProduct(listingPrice, sellingPrice, costPrice)
                .uploadProductImage(imgFileName)
                .addVariations(variations)
                .selectCollections(collectionName)
                .setDimension(weight, length, width, height)
                .setPlatForm(platforms)
                .changeVariationPriceForAllVariations(listingPrice, sellingPrice, costPrice)
                .changeStockQuantityForAllVariations(stockQuantity)
                .changeSKUForAllVariations()
                .uploadImageForAllVariations(imgFileName)
                .clickOnTheConfigureConversionUnit()
                .configureConversionUnitForVariationProduct(conversionMap)
                .clickOnTheConfigureWholesalePriceBtn()
                .configureWholesalePriceForVariationProduct(wholesaleMap)
                .clickOnTheAddDepositBtn()
                .addDeposit(depositList)
                .changeDepositPriceForAllDeposits(depositPrice)
                .changeStockQuantityForAllDeposits(stockQuantity)
                .changeSKUForAllDeposits()
                .uploadImageForAllDeposits(imgFileName);
    }
}
