package mobile.seller.iOS.products.child_screen.filter.collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class CollectionsScreen extends CollectionsElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();

    public CollectionsScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void selectCollection(String collectionName) {
        // Select collection
        commonIOS.tap(collectionName.equals("ALL") ? loc_btnAllCollections : loc_btnCollection(collectionName));

        // Log
        logger.info("Select collection: {}", collectionName);
    }

}
