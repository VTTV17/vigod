package mobile.seller.android.products.product_management;

import api.Seller.products.all_products.APIAllProductsForCheckSortAndFilter;
import api.Seller.products.product_collections.APIProductCollection;
import api.Seller.products.product_collections.APIProductCollection.CollectionInfo;
import api.Seller.setting.BranchManagement;
import mobile.seller.android.login.LoginScreen;
import mobile.seller.android.products.child_screen.filter.FilterScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;

import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.environment.goSELLEREnvironment.*;

public class ProductManagementScreen extends ProductManagementElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();

    public ProductManagementScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonMobile = new UICommonAndroid(driver);
    }

    public ProductManagementScreen navigateToProductManagementScreen() {
        // Navigate to product management screen
        commonMobile.navigateToScreenUsingScreenActivity(goSELLERBundleId, goSELLERProductManagementActivity);

        // Log
        logger.info("Navigate to product management screen.");

        return this;
    }

    public void navigateToProductDetailScreen(String productName) {
        // Search product by name
        commonMobile.sendKeys(loc_txtSearchBox, productName);

        // Log
        logger.info("Search product by name: {}", productName);

        // Navigate to product detail screen
        By resultXpath = By.xpath(str_lblProductName.formatted(productName));
        if (commonMobile.isShown(resultXpath)) {
            // Click into first result
            commonMobile.click(resultXpath);

            // Wait screen loaded
            commonMobile.waitUntilScreenLoaded(goSELLERProductDetailActivity);

        } else throw new NoSuchElementException("No result with keyword: %s".formatted(productName));
    }

    void sortListProduct(String sortOption) {
        // Open list of sort options
        commonMobile.click(loc_btnSort);

        // Sort product
        switch (sortOption) {
            case "Stock high to low" -> commonMobile.click(loc_lstSortOptions( 1));
            case "Stock low to high" -> commonMobile.click(loc_lstSortOptions( 2));
            case "Priority high to low" -> commonMobile.click(loc_lstSortOptions( 3));
            case "Priority low to high" -> commonMobile.click(loc_lstSortOptions( 4));
            default ->  commonMobile.click(loc_lstSortOptions( 0));
        }

        // Log
        logger.info("Sort list product by {}", sortOption);
    }

    public void checkSortByRecentUpdated() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Get list product name after sort
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByRecentUpdated();

        // Verify list product are sorted correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkSortByStockHighToLow() {
        // Sort list product by stock high to low
        sortListProduct("Stock high to low");

        // Get list product name after sort
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByStockHighToLow();

        // Verify list product are sorted correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkSortByStockLowToHigh() {
        // Sort list product by stock low to high
        sortListProduct("Stock low to high");

        // Get list product name after sort
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByStockLowToHigh();

        // Verify list product are sorted correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkSortByPriorityHighToLow() {
        // Sort list product by priority high to low
        sortListProduct("Priority high to low");

        // Get list product name after sort
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByPriorityHighToLow();

        // Verify list product are sorted correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkSortByPriorityLowToHigh() {
        // Sort list product by priority low to high
        sortListProduct("Priority low to high");

        // Get list product name after sort
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductNameAfterSortByPriorityLowToHigh();

        // Verify list product are sorted correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after sorted on first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByActiveStatus() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by Activate status
        new FilterScreen(driver).filterByStatus("ACTIVE");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByStatus("ACTIVE");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByInactiveStatus() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by Inactive status
        new FilterScreen(driver).filterByStatus("INACTIVE");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByStatus("INACTIVE");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByErrorStatus() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by Error status
        new FilterScreen(driver).filterByStatus("ERROR");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByStatus("ERROR");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }


    public void checkFilterByLazadaChannel() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by Lazada channel
        new FilterScreen(driver).filterByChannel("LAZADA");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByChannel("LAZADA");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByShopeeChannel() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by Shopee channel
        new FilterScreen(driver).filterByChannel("SHOPEE");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByChannel("SHOPEE");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByWebPlatform() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by Web platform
        new FilterScreen(driver).filterByPlatform("WEB");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("WEB");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByAppPlatform() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by App platform
        new FilterScreen(driver).filterByPlatform("APP");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("APP");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();

    }

    public void checkFilterByPOSPlatform() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by in-store platform
        new FilterScreen(driver).filterByPlatform("IN_STORE");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("IN_STORE");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByNonePlatform() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Filter list product by in-store platform
        new FilterScreen(driver).filterByPlatform("NONE");

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByPlatform("NONE");

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByBranch() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Get branch info
        BranchInfo branchInfo = new BranchManagement(LoginScreen.getLoginInformation()).getInfo();
        String branchName = branchInfo.getBranchName().get(nextInt(branchInfo.getBranchName().size()));
        int branchId = branchInfo.getBranchID().get(branchInfo.getBranchName().indexOf(branchName));

        // Filter list product by in-store platform
        new FilterScreen(driver).filterByBranch(branchName);

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByBranch(branchId);

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }

    public void checkFilterByCollections() {
        // Sort list product by recent updated
        sortListProduct("Recent updated");

        // Navigate to Filter screen
        commonMobile.click(loc_btnFilter);

        // Get collection info
        CollectionInfo collectionInfo= new APIProductCollection(LoginScreen.getLoginInformation()).getManualCollection();
        String collectionName = collectionInfo.getCollectionNames().isEmpty() ? "ALL" : collectionInfo.getCollectionNames().get(0);
        String collectionId = collectionName.equals("ALL") ? "" : collectionInfo.getCollectionIds().get(0).toString();

        // Filter list product by in-store platform
        new FilterScreen(driver).filterByCollections(collectionName);

        // Get list product name after filter
        List<String> firstScreenProductNames = commonMobile.getListElementTextOnFirstScreen(loc_lblProductName);

        // Get list product nam by API
        List<String> expectedProductNames = new APIAllProductsForCheckSortAndFilter(LoginScreen.getLoginInformation()).getListProductAfterFilterByCollection(collectionId);

        // Verify list product are filtered correctly
        assertCustomize.assertEquals(firstScreenProductNames, expectedProductNames.subList(0, firstScreenProductNames.size()), "List product after filtered first screen must be %s, but found %s"
                .formatted(expectedProductNames.subList(0, firstScreenProductNames.size()), firstScreenProductNames));

        // Verify test
        AssertCustomize.verifyTest();
    }
}
