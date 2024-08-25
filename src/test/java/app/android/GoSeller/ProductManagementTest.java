package app.android.GoSeller;

import mobile.seller.android.login.LoginScreen;
import mobile.seller.android.products.product_management.ProductManagementScreen;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitAndroidDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class ProductManagementTest extends BaseTest {
    LoginInformation loginInformation;
    ProductManagementScreen productManagementScreen;

    @BeforeClass
    void setup() throws MalformedURLException, URISyntaxException {
        // init WebDriver
        String uuid = Optional.ofNullable(System.getProperty("udidAndroid")).orElse(PropertiesUtil.getEnvironmentData("udidAndroidThang"));
        driver = new InitAndroidDriver().getSellerDriver(uuid);

        // init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // login to dashboard with login information
        new LoginScreen(driver).performLogin(loginInformation);

        // init product page POM
        productManagementScreen = new ProductManagementScreen(driver);
    }

    @Test
    void MN_PRODUCT_01_SortProductByRecentUpdated() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkSortByRecentUpdated();
    }

    @Test
    void MN_PRODUCT_02_SortProductByStockHighToLow() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkSortByStockHighToLow();
    }

    @Test
    void MN_PRODUCT_03_SortProductByStockLowToHigh() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkSortByStockLowToHigh();
    }

    @Test
    void MN_PRODUCT_04_SortProductByPriorityHighToLow() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkSortByPriorityHighToLow();
    }

    @Test
    void MN_PRODUCT_05_SortProductByPriorityLowToHigh() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkSortByPriorityLowToHigh();
    }

    @Test
    void MN_PRODUCT_06_FilterProductByActiveStatus() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByActiveStatus();
    }

    @Test
    void MN_PRODUCT_07_FilterProductByActiveStatus() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByInactiveStatus();
    }

    @Test
    void MN_PRODUCT_08_FilterProductByErrorStatus() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByErrorStatus();
    }

    @Test
    void MN_PRODUCT_09_FilterProductByLazadaChannel() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByLazadaChannel();
    }

    @Test
    void MN_PRODUCT_10_FilterProductByShopeeChannel() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByShopeeChannel();
    }

    @Test
    void MN_PRODUCT_11_FilterProductByWebPlatform() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByWebPlatform();
    }

    @Test
    void MN_PRODUCT_12_FilterProductByAppPlatform() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByAppPlatform();
    }

    @Test
    void MN_PRODUCT_13_FilterProductByInStorePlatform() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByPOSPlatform();
    }

    @Test
    void MN_PRODUCT_14_FilterProductByNonePlatform() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByNonePlatform();
    }

    @Test
    void MN_PRODUCT_15_FilterProductByBranch() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByBranch();
    }

    @Test
    void MN_PRODUCT_16_FilterProductByCollections() {
        productManagementScreen.navigateToProductManagementScreen()
                .checkFilterByCollections();
    }
}
