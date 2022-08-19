import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.dashboard.products.all_products.wholesale_price.WholesalePricePage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ProductTest extends BaseTest {
    String fileName;
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
    int inventoryStock;
    List<String> platforms;

    String collectionName;
    Map<String, Integer> conversionMap;

    Map<Integer, List<String>> wholesaleMap;

    @BeforeClass
    public void initTestData() {
        fileName = "img.jpg";
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
        inventoryStock = 1000;
        conversionMap = new HashMap<>();
        conversionMap.put("10 products", 10);
        conversionMap.put("100 products", 100);
        wholesaleMap = new HashMap<>();
        wholesaleMap.put(0,List.of("wholesaleName1", "1", "1000","Segment 1", "Segment 2"));
        wholesaleMap.put(1,List.of("wholesaleName2", "2", "1000","Segment 2", "Segment 1"));
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
                .selectProductVAT(vatID)
//                .inputPriceNormalProduct(listingPrice, sellingPrice, costPrice)
                .uploadProductImage(fileName)
                .addVariations(variations)
                .selectCollections(collectionName)
//                .manageInventoryByIMEI()
//                .setInventoryByNormalProduct(inventoryStock)
                .setDimension(weight, length, width, height)
                .setPlatForm(platforms)
                .changeVariationPriceForAllVariations(listingPrice, sellingPrice, costPrice)
                .changeStockQuantityForAllVariations(inventoryStock)
                .changeSKUForAllVariations()
                .uploadImageForAllVariations(fileName)
                .clickOnTheConfigureConversionUnit()
//                .configureConversionUnitForNormalProduct(conversionMap)
                .selectAllVariations()
                .configureConversionUnitForAllVariations(conversionMap)
                .clickOnTheConfigureWholesalePriceBtn()
//                .configureWholesalePriceForNormalProduct(wholesaleMap);
                .configureWholesalePriceForVariationProduct(wholesaleMap);
    }
}
