package mobile.buyer.iOS.home.account;

import mobile.buyer.iOS.home.HomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class AccountScreen extends AccountElement{
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    public AccountScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    void logout() {
        if (!commonIOS.getListElement(loc_btnLogout).isEmpty()) {
            // Click logout btn
            commonIOS.click(loc_btnLogout);

            // Accept logout
            commonIOS.click(loc_dlgConfirmLogout_btnYes);

            // Log
            logger.info("Logout successful");
        }
    }

    public void navigateToLoginScreen() {
        // Navigate to account screen
        new HomeScreen(driver).navigateToAccountScreen();

        // Logout if user are logged in
        logout();

        // Navigate to login screen
        commonIOS.click(loc_btnLogin);

        // Log
        logger.info("Navigate to login screen");
    }
}
