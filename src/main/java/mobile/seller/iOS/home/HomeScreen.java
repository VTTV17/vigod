package mobile.seller.iOS.home;

import mobile.seller.iOS.account.AccountScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class HomeScreen extends HomeElement{
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    public HomeScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void logout() {
        if (!commonIOS.getListElements(loc_icnAccount).isEmpty()) {
            // Navigate to Account screen
            commonIOS.tap(loc_icnAccount);

            // Logout
            new AccountScreen(driver).logout();
        }
    }

    public void navigateToCreateProductScreen() {
        // Click create product icon
        commonIOS.tap(loc_icnCreateProduct);

        // Log
        logger.info("Navigate create product screen");
    }

    public void navigateToProductManagementScreen() {
        // Click product management icon
        commonIOS.tap(loc_icnProductManagement);

        // Log
        logger.info("Navigate product management screen");
    }
}
