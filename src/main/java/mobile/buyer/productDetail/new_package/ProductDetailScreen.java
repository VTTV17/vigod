package mobile.buyer.productDetail.new_package;

import api.Seller.products.all_products.APIProductDetail;
import mobile.buyer.navigationbar.NavigationBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class ProductDetailScreen extends ProductDetailElement{
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonAndroid;
    Logger logger = LogManager.getLogger();
    public ProductDetailScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonAndroid = new UICommonAndroid(driver);
    }

    void checkProductName(String productName) {
        // Check product name
        String actProductName = commonAndroid.getText(loc_lblProductName);
        assertCustomize.assertEquals(actProductName, productName, "Product/Version name must be %s, but found %s".formatted(productName, actProductName));
    }

    void checkPromotion() {
     // Check badge

     // Check price

    }

    void checkBranchInformation() {
        // Check filter and search icon

        // Check branch name, stock
    }

    void checkDescription() {
        // Check description
    }

    void checkReview() {
        // Check review

    }

    void checkSimilarProducts() {

    }

    public void test() {
        ProductInfo productInfo = new APIProductDetail(new LoginInformation("stgauto@nbobd.com", "Abc@12345")).getInfo(1289438);
        new NavigationBar(driver).tapOnHomeIcon().waitHomepageLoaded()
                .searchAndNavigateToProductScreenByName(productInfo, "vi");

        String text = commonAndroid.getText(loc_lblBranchAndStock(5));
        System.out.println(text);
    }
}
