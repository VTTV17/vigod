package web.Dashboard.sales_channels.tiktok.account_information;

import api.Seller.sale_channel.tiktok.APIGetTikTokProducts;
import api.Seller.sale_channel.tiktok.APIGetTiktokShops;
import api.Seller.sale_channel.tiktok.APITiktokItemDownloadInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.sales_channels.tiktok.VerifyAutoSyncHelper;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.links.Links.DOMAIN;

/**
 * This class represents the TikTok Account Information Page for the dashboard.
 * It includes methods to navigate to the account information page,
 * retrieve account details, and initiate TikTok product download.
 */
public class AccountInformationPage extends AccountInformationElement {

    // Logger to track the execution and log information
    private final static Logger logger = LogManager.getLogger();

    private final WebDriver driver;
    private final UICommonAction commonAction;
    private List<APIGetTiktokShops.TiktokShopAccount> connectedTiktokShops;

    /**
     * Constructor that initializes the WebDriver and UI common actions for this page.
     *
     * @param driver WebDriver instance used to interact with the page elements.
     */
    public AccountInformationPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        logger.info("AccountInformationPage initialized with WebDriver.");
    }

    /**
     * Loads the list of connected TikTok shop accounts and stores it for further use.
     * If the provided list is empty, a warning is logged, and the method returns null to skip further actions.
     *
     * @param connectedTiktokShops List of connected TikTok shop accounts fetched via API.
     * @return the current instance of AccountInformationPage if the list is not empty, otherwise returns null.
     */
    public AccountInformationPage loadConnectedTiktokAccounts(List<APIGetTiktokShops.TiktokShopAccount> connectedTiktokShops) {
        // Store the list of connected TikTok shop accounts in the instance variable
        this.connectedTiktokShops = connectedTiktokShops;

        // Log how many accounts were loaded
        logger.info("Loaded {} connected TikTok shop accounts.", connectedTiktokShops.size());

        // If there are no connected TikTok shop accounts, log a warning and return null to terminate further execution
        if (connectedTiktokShops.isEmpty()) {
            logger.warn("No connected TikTok shop accounts found. Skipping further actions.");
            return null; // Early exit if no connected TikTok shop accounts
        }

        // Return the current instance of the page to allow for method chaining
        return this;
    }

    /**
     * Navigates to the TikTok Account Information Page if connected TikTok shop accounts are present.
     * This method assumes that the accounts have already been loaded. If there are no accounts to navigate to,
     * this method should not be called as loadConnectedTiktokAccounts handles the early exit.
     *
     * @return the current instance of AccountInformationPage.
     */
    public AccountInformationPage openTiktokAccountInfoPage() {
        // Navigate to the specific URL where TikTok account information is displayed
        UICommonAction.performAction("Navigating to TikTok Account Information Page.",
                () -> driver.get(DOMAIN + "/channel/tiktok/account/information"),
                () -> Assert.assertEquals(driver.getCurrentUrl(),
                        DOMAIN + "/channel/tiktok/account/information",
                        "Can not navigate to Tiktok account information page."));


        // Return the current instance of the page to allow for method chaining
        return this;
    }

    /**
     * Initiates the download process for all connected TikTok products and records the
     * start and end times of the action.
     * <p>
     * This method checks if there are any connected TikTok shops. If none are connected, it logs a warning
     * and returns null. If shops are connected, it clicks the button to download all TikTok products,
     * waits for the loading indicator to disappear, and logs the action.
     * It returns an array containing the start time and end time of the download action in
     * UTC format.
     * </p>
     *
     * @param credentials The login information required to access the TikTok API for downloading products.
     * @return A String array containing two elements: the start time and end time of the
     *         download action, formatted as 'YYYY-MM-DD HH:MM:SS.SSS', or null if no connected
     *         TikTok shops are available.
     * @throws RuntimeException if the download process does not complete successfully within the expected time.
     */
    public String[] initiateDownloadAllTikTokProducts(LoginInformation credentials) {
        // Check if there are no connected TikTok shops
        if (connectedTiktokShops.isEmpty()) {
            // Log a warning message if no TikTok shops are connected
            logger.warn("No connected TikTok shops to download products from.");
            return null; // Return null if no shops are connected
        }

        // Array to store the start and end times of the download action
        String[] actionsTime = new String[2];

        // Record the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // Click on the "Download All TikTok Products" button
        commonAction.click(loc_btnDownloadTiktokProduct);

        // Log that the download button has been clicked
        logger.info("Clicked on 'Download All TikTok Products' button.");

        // Wait for download success
        new APITiktokItemDownloadInformation(credentials).waitForDownloadSuccess();

        // Record the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // Return the start and end times of the download action
        return actionsTime;
    }


    /**
     * Verifies that the download events for all linked TikTok products are properly recorded in the database.
     *
     * @param tikTokProducts The list of TikTok products to verify.
     * @param actionTime     An array containing the start and end time of the action in 'YYYY-MM-DD HH:MM:SS.mmm' format.
     * @param connection     A connection object to execute SQL queries for retrieving inventory events.
     * @param isAutoSynced   A boolean flag indicating whether auto-sync is enabled.
     * @throws IllegalArgumentException If the input parameters are invalid or the actionTime array does not contain at least two elements.
     */
    public void verifyDownloadAllTiktokProductsEvent(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts,
                                                     String[] actionTime, Connection connection, boolean isAutoSynced) {
        // Retrieve linked TikTok products
        List<APIGetTikTokProducts.TikTokProduct> linkedTiktokProducts = APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts);

        // Retrieve item mappings from linked TikTok products
        List<APIGetTikTokProducts.ItemMapping> itemMappings = APIGetTikTokProducts.getItemMapping(linkedTiktokProducts);

        // Verify inventory events based on the item mappings, action time, and connection
        VerifyAutoSyncHelper.verifyInventoryEvent(isAutoSynced, itemMappings, actionTime, tikTokProducts.get(0).getBcStoreId(), connection, "GS_TIKTOK_DOWNLOAD_PRODUCT");
    }
}