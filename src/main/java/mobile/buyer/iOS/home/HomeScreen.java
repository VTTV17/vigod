package mobile.buyer.iOS.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class HomeScreen extends HomeElement {
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

        // Accept permission
        boolean isAlertPresent = commonIOS.allowPermission("Allow");

        // Log
        if (isAlertPresent) logger.info("Accept notification permission");
    }

    public void navigateToAccountScreen() {
        // Navigate to account screen
        commonIOS.click(loc_btnFooterAccount);

        // Log
        logger.info("Navigate to account screen");
    }

    public void navigateToSearchScreen() {
        // Navigate to search screen
        commonIOS.click(loc_btnFooterSearch);

        // Log
        logger.info("Navigate to search screen");
    }
}
