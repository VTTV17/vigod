package mobile.buyer.iOS.search;

import mobile.buyer.iOS.home.HomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class SearchScreen extends SearchElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    public SearchScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void searchAndNavigateProductDetailScreen(String keywords) {
        // Navigate to search screen
        new HomeScreen(driver).navigateToSearchScreen();

        // Search
        commonIOS.click(loc_btnSearch);
        commonIOS.sendKeys(loc_txtSearchBox, keywords);

        // Log
        logger.info("Search product with keywords: {}", keywords);

        // Navigate to product detail
        commonIOS.click(loc_lstResult(keywords));
    }
}
