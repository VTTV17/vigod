package web.Dashboard;

import api.Seller.sale_channel.tiktok.*;
import org.apache.logging.log4j.LogManager;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sql.SQLGetInventoryMapping;
import utilities.commons.UICommonAction;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.sales_channels.tiktok.account_information.AccountInformationPage;
import web.Dashboard.sales_channels.tiktok.products.ProductsPage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class TiktokAutoSyncTest extends BaseTest {
    Connection connection;
    LoginInformation credentials;
    boolean isAutoSynced;
    List<APIGetTiktokShops.TiktokShopAccount> connectedShop;
    List<APIGetTikTokProducts.TikTokProduct> tikTokProducts;

    private void initAPIPrecondition() {
        // Set login credentials
        credentials = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // Retrieve TikTok shop accounts and filter for connected accounts
        var shops = new APIGetTiktokShops(credentials).getListTiktokAccounts();
        connectedShop = APIGetTiktokShops.getListConnectedShop(shops);
        Assert.assertFalse(connectedShop.isEmpty(), "Test stopped as there are no connected TikTok accounts.");

        // Retrieve all TikTok products available
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Check auto-sync status
        isAutoSynced = new APIGetTiktokSetting(credentials).getTiktokSettings().isAutoSyncStock();
    }

    private void initSQLConnection() throws SQLException {
        // Set up SQL connection to staging database
        if (environment.equals("STAG")) {
            connection = new InitConnection().createConnection("172.16.113.55", "5432", "beecow", "readonly", "R7LHffcgeEh2tpQ0qU2y");
        } else {
            connection = new InitConnection().createConnection("db-ca.mediastep.com", "5432", "postgres", "qc_thangnguyen", "tc1t9rQYXyRS3XcK");
        }
    }

    private void initWebActions() {
        // Initialize WebDriver for browser actions, setting browser as Chrome
        driver = new InitWebdriver().getDriver(browser, headless);
    }

    @BeforeClass
    private void setup() throws SQLException {
        // Set up API, SQL connection, and WebDriver prerequisites for tests
        initAPIPrecondition();
        initSQLConnection();
        initWebActions();
        new LoginPage(driver).loginDashboardByJs(credentials);
    }

    @Test
    void TiktokSync01_checkDownloadAllTiktokProducts() {
        // Load TikTok accounts and initiate product download, retrieving start and end times
        AccountInformationPage pageAfterLoad = new AccountInformationPage(driver)
                .loadConnectedTiktokAccounts(connectedShop)
                .openTiktokAccountInfoPage();
        String[] actionTime = pageAfterLoad.initiateDownloadAllTikTokProducts(credentials);

        // Refresh the TikTok products list after attempting to download all products
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify download event details in the database
        pageAfterLoad.verifyDownloadAllTiktokProductsEvent(tikTokProducts, actionTime, connection, isAutoSynced);
    }

    @Test
    void TiktokSync02_checkCreateProductToGoSELL() {
        // Load TikTok accounts and navigate to products page
        ProductsPage pageAfterLoad = loadTiktokAccountThenNavigateToProductsPage();

        // Check if there are any TikTok products to work with
       checkProductsAvailable();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = orgTiktokProduct.get(0).getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Check if there are any unlinked products; if all products are linked, initiate unlinking
        ensureUnlinkedProducts();

        // Attempt to create products in GoSELL and capture action times
        String[] actionTime = pageAfterLoad.createProductsToGoSell(tikTokProducts);

        // Refresh the TikTok products list after attempting to create products in GoSELL
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify the successful creation and mapping of TikTok products in GoSELL
        pageAfterLoad.verifyTikTokProductCreationInGoSELL(
                orgTiktokProduct, tikTokProducts, orgInventoryMappings, actionTime, connection, isAutoSynced);
    }

    @Test
    void TiktokSync03_checkUpdateProductToGoSELL() {
        // Load TikTok accounts and navigate to products page
        ProductsPage pageAfterLoad = loadTiktokAccountThenNavigateToProductsPage();

        // Check if there are any TikTok products to work with
        checkProductsAvailable();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = orgTiktokProduct.get(0).getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Check if there are any linked products; if all products are unlinked, initiate linking
        ensureLinkedProducts();

        // Update products in GoSELL
        pageAfterLoad.updateProductsToGoSell(tikTokProducts, credentials);

        // Retrieve updated TikTok products post-update in GoSELL
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful update of TikTok products in GoSELL
        pageAfterLoad.verifyTikTokProductUpdatedToGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, connection);
    }


    @Test
    void TiktokSync04_checkDownloadIndividualTiktokProducts() {
        // Load TikTok accounts and navigate to products page
        ProductsPage pageAfterLoad = loadTiktokAccountThenNavigateToProductsPage();

        // Check if there are any TikTok products to work with
        checkProductsAvailable();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = orgTiktokProduct.get(0).getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Attempt to download individual products in GoSELL and capture action times
        String[] actionTime = pageAfterLoad.downloadProducts(tikTokProducts, credentials);

        // Verify successful download of TikTok products in GoSELL
        pageAfterLoad.verifyDownloadProducts(orgTiktokProduct, tikTokProducts, orgInventoryMappings, actionTime, isAutoSynced, connection);
    }

    @Test
    void TiktokSync05_checkLinkProductToGoSELL() {
        // Check if there are any TikTok products to work with
        checkProductsAvailable();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = orgTiktokProduct.get(0).getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Check if there are any unlinked products; if all products are linked, initiate unlinking
        ensureUnlinkedProducts();

        // Link TikTok products to GoSELL, capturing action times
        String[] actionTime = new APILinkTiktokProductToGoSELL(credentials).linkTiktokProductsToGoSELL(tikTokProducts);
        UICommonAction.sleepInMiliSecond(10_000);

        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful linking of TikTok products to GoSELL
        APILinkTiktokProductToGoSELL.verifyLinkProductsToGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, actionTime, connection, isAutoSynced);
    }

    @Test
    void TiktokSync06_checkUnLinkProductFromGoSELL() {
        // Check if there are any TikTok products to work with
        checkProductsAvailable();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = orgTiktokProduct.get(0).getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Check if there are any linked products; if all products are unlinked, initiate linking
        ensureLinkedProducts();

        // Unlink TikTok products from GoSELL
        new APIUnlinkTiktokProduct(credentials).unlinkTiktokProducts(tikTokProducts);

        UICommonAction.sleepInMiliSecond(10_000);

        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful unlinking of TikTok products from GoSELL
        APIUnlinkTiktokProduct.verifyLinkProductsToGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, connection);
    }

    @Test
    void TiktokSync07_checkDeleteTiktokProducts() {
        // Load TikTok accounts and navigate to products page
        ProductsPage pageAfterLoad = loadTiktokAccountThenNavigateToProductsPage();

        // Check if there are any TikTok products to work with
        checkProductsAvailable();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = orgTiktokProduct.get(0).getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Delete selected TikTok products and retrieve the updated list of products
        List<APIGetTikTokProducts.TikTokProduct> deletedTiktokProducts = pageAfterLoad.deleteTiktokProducts(tikTokProducts);
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful deletion of TikTok products in GoSELL
        pageAfterLoad.verifyDeleteProducts(orgTiktokProduct, tikTokProducts, orgInventoryMappings, deletedTiktokProducts, connection);
    }

    private ProductsPage loadTiktokAccountThenNavigateToProductsPage() {
        // Load TikTok accounts and navigate to products page
        return new ProductsPage(driver)
                .loadConnectedTiktokAccounts(connectedShop)
                .openTikTokProductsPage();
    }

    private void ensureUnlinkedProducts() {
        if (APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts).isEmpty()) {
            LogManager.getLogger().info("All products are linked. Unlinking products, then retrying...");
            new APIUnlinkTiktokProduct(credentials).unlinkTiktokProducts(tikTokProducts);
            driver.navigate().refresh();
        }
    }

    private void ensureLinkedProducts() {
        if (APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts).isEmpty()) {
            LogManager.getLogger().info("All products are unlinked. Linking products, then retrying...");
            new APILinkTiktokProductToGoSELL(credentials).linkTiktokProductsToGoSELL(tikTokProducts);
            driver.navigate().refresh();
        }
    }

    private void checkProductsAvailable() {
        if (tikTokProducts.isEmpty()) {
            throw new SkipException("No TikTok products available for actions. Please download products and retry.");
        }
    }

    @AfterClass
    void tearDown() throws SQLException {
        // Close WebDriver and database connection after tests are complete
        if (driver != null) driver.quit();
        if (connection != null) connection.close();
    }
}