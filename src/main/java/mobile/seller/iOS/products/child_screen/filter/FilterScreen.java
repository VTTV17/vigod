package mobile.seller.iOS.products.child_screen.filter;

import mobile.seller.iOS.products.child_screen.filter.branch.BranchScreen;
import mobile.seller.iOS.products.child_screen.filter.collections.CollectionsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class FilterScreen extends FilterElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();

    public FilterScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void filterByStatus(String status) {
        // Reset all filters
        commonIOS.click(loc_btnReset);

        // Select status
        switch (status) {
            case "ACTIVE" -> commonIOS.click(loc_btnActiveStatus);
            case "INACTIVE" -> commonIOS.click(loc_btnInActiveStatus);
            case "ERROR" -> commonIOS.click(loc_btnErrorStatus);
        }

        // Apply filter
        commonIOS.click(loc_btnApply);

        // Log
        logger.info("Filter list product by status: {}", status);
    }

    public void filterByChannel(String channel) {
        // Reset all filters
        commonIOS.click(loc_btnReset);

        // Select channel
        switch (channel) {
            case "LAZADA" -> commonIOS.click(loc_btnLazadaChannel);
            case "SHOPEE" -> commonIOS.click(loc_btnShopeeChannel);
        }

        // Apply filter
        commonIOS.click(loc_btnApply);

        // Log
        logger.info("Filter list product by channel, channelName: {}", channel);
    }

    public void filterByPlatform(String platform) {
        // Reset all filters
        commonIOS.click(loc_btnReset);

        // Select platform
        switch (platform) {
            case "WEB" -> commonIOS.click(loc_btnWebPlatform);
            case "APP" -> commonIOS.click(loc_btnAppPlatform);
            case "IN_STORE" -> commonIOS.click(loc_btnInStorePlatform);
            case "NONE" -> commonIOS.click(loc_btnNonePlatform);
        }

        // Apply filter
        commonIOS.click(loc_btnApply);

        // Log
        logger.info("Filter list product platform, platformName: {}", platform);
    }

    public void filterByBranch(String branchName) {
        // Reset all filters
        commonIOS.click(loc_btnReset);

        // Navigate to branch screen
        commonIOS.click(loc_btnSeeAllBranches);

        // Select branch
        new BranchScreen(driver).selectBranch(branchName);

        // Apply filter
        commonIOS.click(loc_btnApply);

        // Log
        logger.info("Filter list product by branch, branchName: {}", branchName);
    }

    public void filterByCollections(String collectionName) {
        // Reset all filters
        commonIOS.click(loc_btnReset);

        // Navigate to collections screen
        commonIOS.click(loc_btnSeeAllCollections);

        // Select collection
        new CollectionsScreen(driver).selectCollection(collectionName);

        // Apply filter
        commonIOS.click(loc_btnApply);

        // Log
        logger.info("Filter list product by collection, collectionName: {}", collectionName);
    }
}
