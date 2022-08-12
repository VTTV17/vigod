import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.LoginPage;
import pages.dashboard.products.all_products.ProductPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @BeforeClass
    public void initTestData() {
        fileName = "img.jpg";
        sellerAccount = "stgaboned@nbobd.com";
        sellerPassword = "Abc@12345";
        env = "stg";
        language = "ENG";
        productName = "New products";
        productDescription = "New product descriptions";
        vatID = 2;
        variations = new HashMap<>();
        variations.put("size", List.of("S", "M", "L"));
    }

    @Test
    public void Tcs01_CreateProduct() throws IOException, InterruptedException {
        new LoginPage(driver).navigate()
                .inputEmailOrPhoneNumber(sellerAccount)
                .inputPassword(sellerPassword)
                .clickLoginBtn();

        new ProductPage(driver).setLanguage(language)
                .navigate()
                .clickOnTheCreateProductBtn()
                .inputProductName(productName)
                .inputProductDescription(productDescription)
                .selectProductVAT(vatID)
                .uploadProductImage(fileName)
                .clickOnTheAddVariationBtn()
                .inputVariationName(variations);
    }
}
