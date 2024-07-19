package mobile.seller.iOS.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class AccountScreen extends AccountElement {
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

    public void logout() {
        // Open logout popup
        commonIOS.tap(loc_icnLogout);

        // Confirm logout
        commonIOS.tap(loc_dlgLogout_btnOK);

        // Log
        logger.info("Logout");
    }
}
