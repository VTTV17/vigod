package mobile.seller.android.products.child_screen.filter;

import mobile.seller.android.products.child_screen.filter.branch.BranchScreen;
import mobile.seller.android.products.child_screen.filter.collections.CollectionsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;

public class FilterScreen extends FilterElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();

    public FilterScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonMobile = new UICommonAndroid(driver);
    }

    public void filterByStatus(String status) {
        // Reset all filters
        commonMobile.click(rsId_btnReset);

        // Select status
        switch (status) {
            case "ACTIVE" -> commonMobile.click(rsId_sctStatus, loc_btnFilterByStatus, 1);
            case "INACTIVE" -> commonMobile.click(rsId_sctStatus, loc_btnFilterByStatus, 2);
            case "ERROR" -> commonMobile.click(rsId_sctStatus, loc_btnFilterByStatus, 3);
            default -> commonMobile.click(rsId_sctStatus, loc_btnFilterByStatus, 0);
        }

        // Apply filter
        commonMobile.click(rsId_btnApply);

        // Log
        logger.info("Filter list product by status: {}", status);
    }

    public void filterByChannel(String channel) {
        // Reset all filters
        commonMobile.click(rsId_btnReset);

        // Select channel
        switch (channel) {
            case "LAZADA" -> commonMobile.click(rsId_sctChannel, loc_btnFilterByChannel, 1);
            case "SHOPEE" -> commonMobile.click(rsId_sctChannel, loc_btnFilterByChannel, 2);
            default -> commonMobile.click(rsId_sctChannel, loc_btnFilterByChannel, 0);
        }

        // Apply filter
        commonMobile.click(rsId_btnApply);

        // Log
        logger.info("Filter list product by channel, channelName: {}", channel);
    }

    public void filterByPlatform(String platform) {
        // Reset all filters
        commonMobile.click(rsId_btnReset);

        // Select platform
        switch (platform) {
            case "WEB" -> commonMobile.click(rsId_sctPlatform,loc_btnFilterByPlatform, 1);
            case "APP" -> commonMobile.click(rsId_sctPlatform,loc_btnFilterByPlatform, 2);
            case "IN_STORE" -> commonMobile.click(rsId_sctPlatform,loc_btnFilterByPlatform, 3);
            case "NONE" -> commonMobile.click(rsId_sctPlatform,loc_btnFilterByPlatform, 4);
            default -> commonMobile.click(rsId_sctPlatform,loc_btnFilterByPlatform, 0);
        }

        // Apply filter
        commonMobile.click(rsId_btnApply);

        // Log
        logger.info("Filter list product platform, platformName: {}", platform);
    }

    public void filterByBranch(String branchName) {
        // Reset all filters
        commonMobile.click(rsId_btnReset);

        // Navigate to branch screen
        commonMobile.click(rsId_btnSeeAllBranches);

        // Select branch
        new BranchScreen(driver).selectBranch(branchName);

        // Apply filter
        commonMobile.click(rsId_btnApply);

        // Log
        logger.info("Filter list product by branch, branchName: {}", branchName);
    }

    public void filterByCollections(String collectionName) {
        // Reset all filters
        commonMobile.click(rsId_btnReset);

        // Navigate to collections screen
        commonMobile.click(rsId_btnSeeAllCollections);

        // Select collection
        new CollectionsScreen(driver).selectCollection(collectionName);

        // Apply filter
        commonMobile.click(rsId_btnApply);

        // Log
        logger.info("Filter list product by collection, collectionName: {}", collectionName);
    }
}
