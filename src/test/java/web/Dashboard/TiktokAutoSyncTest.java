package web.Dashboard;

import api.Seller.sale_channel.tiktok.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sql.SQLGetInventoryMapping;
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

public class TiktokAutoSyncTest extends BaseTest{
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
        driver = new InitWebdriver().getDriver("chrome", "false");
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
    void checkDownloadAllTiktokProducts() {
        // Load TikTok accounts and initiate product download, retrieving start and end times
        AccountInformationPage pageAfterLoad = new AccountInformationPage(driver).loadConnectedTiktokAccounts(connectedShop);
        String[] actionTime = pageAfterLoad.openTiktokAccountInfoPage().initiateDownloadAllTikTokProducts();

        // Verify download event details in the database
        pageAfterLoad.verifyDownloadAllTiktokProductsEvent(tikTokProducts, actionTime, connection, isAutoSynced);
    }

    @Test
    void checkCreateProductToGoSELL() {
        // Load TikTok accounts and navigate to products page
        ProductsPage pageAfterLoad = new ProductsPage(driver).loadConnectedTiktokAccounts(connectedShop).openTikTokProductsPage();

        // Store original TikTok products and inventory mappings before product creation in GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = tikTokProducts.getFirst().getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Create products in GoSELL, capturing action times
        String[] actionTime = pageAfterLoad.createProductsToGoSell(tikTokProducts);

        // Retrieve updated TikTok products after creation in GoSELL
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful creation of TikTok products in GoSELL
        pageAfterLoad.verifyTikTokProductCreationInGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, actionTime, connection, isAutoSynced);
    }

    @Test
    void checkUpdateProductToGoSELL() {
        // Load TikTok accounts, navigate to products page, and store original data
        ProductsPage pageAfterLoad = new ProductsPage(driver).loadConnectedTiktokAccounts(connectedShop).openTikTokProductsPage();
        var orgTiktokProduct = tikTokProducts;
        int storeId = tikTokProducts.getFirst().getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Update products in GoSELL
        pageAfterLoad.updateProductsToGoSell(tikTokProducts);

        // Retrieve updated TikTok products post-update in GoSELL
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful update of TikTok products in GoSELL
        pageAfterLoad.verifyTikTokProductUpdatedToGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, connection);
    }

    @Test
    void checkDeleteTiktokProducts() {
        // Load TikTok accounts, navigate to products page, and store original data
        ProductsPage pageAfterLoad = new ProductsPage(driver).loadConnectedTiktokAccounts(connectedShop).openTikTokProductsPage();
        var orgTiktokProduct = tikTokProducts;
        int storeId = tikTokProducts.getFirst().getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Delete selected TikTok products and retrieve the updated list of products
        List<APIGetTikTokProducts.TikTokProduct> deletedTiktokProducts = pageAfterLoad.deleteTiktokProducts(tikTokProducts);
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful deletion of TikTok products in GoSELL
        pageAfterLoad.verifyDeleteProducts(orgTiktokProduct, tikTokProducts, orgInventoryMappings, deletedTiktokProducts, connection);
    }

    @Test
    void checkDownloadIndividualTiktokProducts() {
        // Load TikTok accounts, navigate to products page, and store original data
        ProductsPage pageAfterLoad = new ProductsPage(driver).loadConnectedTiktokAccounts(connectedShop).openTikTokProductsPage();
        var orgTiktokProduct = tikTokProducts;
        int storeId = tikTokProducts.getFirst().getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Download individual TikTok products, capturing action times
        String[] actionTime = ProductsPage.downloadProducts(tikTokProducts, credentials);
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful download of TikTok products in GoSELL
        pageAfterLoad.verifyDownloadProducts(orgTiktokProduct, tikTokProducts, orgInventoryMappings, actionTime, isAutoSynced, connection);
    }

    @Test
    void checkLinkProductToGoSELL() {
        // Store original TikTok products and inventory mappings before linking to GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = tikTokProducts.getFirst().getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Link TikTok products to GoSELL, capturing action times
        String[] actionTime = new APILinkTiktokProductToGoSELL(credentials).linkTiktokProductsToGoSELL(tikTokProducts);
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful linking of TikTok products to GoSELL
        APILinkTiktokProductToGoSELL.verifyLinkProductsToGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, actionTime, connection, isAutoSynced);
    }

    @Test
    void checkUnLinkProductFromGoSELL() {
        // Store original TikTok products and inventory mappings before unlinking from GoSELL
        var orgTiktokProduct = tikTokProducts;
        int storeId = tikTokProducts.getFirst().getBcStoreId();
        var orgInventoryMappings = new SQLGetInventoryMapping(connection).getTiktokInventoryMappings(storeId);

        // Unlink TikTok products from GoSELL
        new APIUnlinkTiktokProduct(credentials).unlinkTiktokProducts(tikTokProducts);
        tikTokProducts = new APIGetTikTokProducts(credentials).getTikTokProducts();

        // Verify successful unlinking of TikTok products from GoSELL
        APIUnlinkTiktokProduct.verifyLinkProductsToGoSELL(orgTiktokProduct, tikTokProducts, orgInventoryMappings, connection);
    }

    @AfterClass
    void tearDown() throws SQLException {
        // Close WebDriver and database connection after tests are complete
        if (driver != null) driver.quit();
        if (connection != null) connection.close();
    }
}
