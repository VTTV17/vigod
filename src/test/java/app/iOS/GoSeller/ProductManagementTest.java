package app.iOS.GoSeller;

import mobile.seller.iOS.login.LoginScreen;
import mobile.seller.iOS.products.product_management.ProductManagementScreen;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitIOSDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;

import java.util.Optional;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class ProductManagementTest extends BaseTest {
    LoginInformation loginInformation;
    ProductManagementScreen productManagementScreen;

    @BeforeClass
    void setup() {
        // Init driver
        String uuid = Optional.ofNullable(System.getProperty("udidIOS")).orElse(PropertiesUtil.getEnvironmentData("udidIOSThang"));
        driver = new InitIOSDriver().getSellerDriver(uuid);

        // Init login information
        loginInformation = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // Login to dashboard with login information
        new LoginScreen(driver).performLogin(loginInformation);

        // Init product page POM
        productManagementScreen = new ProductManagementScreen(driver);
        
        // Navigate to product management screen
        productManagementScreen.navigateToProductManagementScreen();
    }

    @Test
    void MN_PRODUCT_01_SortProductByRecentUpdated() {
        productManagementScreen.checkSortByRecentUpdated();
    }

    @Test
    void MN_PRODUCT_02_SortProductByStockHighToLow() {
        productManagementScreen.checkSortByStockHighToLow();
    }

    @Test
    void MN_PRODUCT_03_SortProductByStockLowToHigh() {
        productManagementScreen.checkSortByStockLowToHigh();
    }

    @Test
    void MN_PRODUCT_04_SortProductByPriorityHighToLow() {
        productManagementScreen.checkSortByPriorityHighToLow();
    }

    @Test
    void MN_PRODUCT_05_SortProductByPriorityLowToHigh() {
        productManagementScreen.checkSortByPriorityLowToHigh();
    }

    @Test
    void MN_PRODUCT_06_FilterProductByActiveStatus() {
        productManagementScreen.checkFilterByActiveStatus();
    }

    @Test
    void MN_PRODUCT_07_FilterProductByActiveStatus() {
        productManagementScreen.checkFilterByInactiveStatus();
    }

    @Test
    void MN_PRODUCT_08_FilterProductByErrorStatus() {
        productManagementScreen.checkFilterByErrorStatus();
    }

    @Test
    void MN_PRODUCT_09_FilterProductByLazadaChannel() {
        productManagementScreen.checkFilterByLazadaChannel();
    }

    @Test
    void MN_PRODUCT_10_FilterProductByShopeeChannel() {
        productManagementScreen.checkFilterByShopeeChannel();
    }

    @Test
    void MN_PRODUCT_11_FilterProductByWebPlatform() {
        productManagementScreen.checkFilterByWebPlatform();
    }

    @Test
    void MN_PRODUCT_12_FilterProductByAppPlatform() {
        productManagementScreen.checkFilterByAppPlatform();
    }

    @Test
    void MN_PRODUCT_13_FilterProductByInStorePlatform() {
        productManagementScreen.checkFilterByPOSPlatform();
    }

    @Test
    void MN_PRODUCT_14_FilterProductByNonePlatform() {
        productManagementScreen.checkFilterByNonePlatform();
    }

    @Test
    void MN_PRODUCT_15_FilterProductByBranch() {
        productManagementScreen.checkFilterByBranch();
    }

    @Test
    void MN_PRODUCT_16_FilterProductByCollections() {
        productManagementScreen.checkFilterByCollections();
    }
}
