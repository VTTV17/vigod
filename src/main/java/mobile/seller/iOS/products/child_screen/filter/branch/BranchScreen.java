package mobile.seller.iOS.products.child_screen.filter.branch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class BranchScreen extends BranchElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    public BranchScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void selectBranch(String branchName) {
        // Select branch
        commonIOS.tap(branchName.equals("ALL") ? loc_btnAllBranches : loc_btnBranch(branchName));

        // Log
        logger.info("Select branch: {}", branchName);
    }
}
