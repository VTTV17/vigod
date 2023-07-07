package android;

import api.dashboard.login.Login;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.ProductInformation;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.productDetail.BuyerProductDetailPage;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;

import java.net.MalformedURLException;

import static utilities.account.AccountTest.*;

public class ProductDetailTest {

    WebDriver driver;
    int productId;
    boolean isIMEIProduct;
    boolean isHideStock;
    boolean isDisplayIfOutOfStock;
    ProductInfo productInfo;
    String url = "http://127.0.0.1:4723/wd/hub";

    @BeforeClass
    void setup() {
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setDBLanguage("vi");
        PropertiesUtil.setSFLanguage("vi");
        new Login().loginToDashboardByMail(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
    }

    @BeforeMethod
    void initTest() throws MalformedURLException {
        isIMEIProduct = true;
        isHideStock = false;
        isDisplayIfOutOfStock = true;
//        productId = 1214387; //new APIAllProducts().getProductIDWithoutVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        productId = new APIAllProducts().getProductIDWithVariationAndInStock(isIMEIProduct, isHideStock, isDisplayIfOutOfStock);
        productInfo = new ProductInformation().getInfo(productId);
        driver = new InitAppiumDriver().getAppiumDriver("10HC8G04UP0003U",
                "ANDROID",
                "com.mediastep.shop0018",
                "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity",
                url);
    }

    @Test
    void test1() throws Exception {
        new UICommonMobile(driver).waitSplashScreenLoaded();
        new NavigationBar(driver).tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG);
        new NavigationBar(driver).tapOnHomeIcon().waitHomepageLoaded().searchProductByName(productInfo, "vi").navigateToProductDetailPage();
        new BuyerProductDetailPage(driver).accessToProductDetailPageByProductIDAndCheckProductInformation("vi", productInfo);

    }

    @AfterMethod
    void teardown() {
        if (driver != null) driver.quit();
    }
}
