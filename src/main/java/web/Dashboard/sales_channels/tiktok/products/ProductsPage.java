package web.Dashboard.sales_channels.tiktok.products;

import api.Seller.sale_channel.tiktok.APIDownloadIndividualTiktokProducts;
import api.Seller.sale_channel.tiktok.APIGetTikTokProducts;
import api.Seller.sale_channel.tiktok.APIGetTiktokShops;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import sql.SQLGetInventoryMapping;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.sales_channels.tiktok.VerifyAutoSyncHelper;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utilities.links.Links.DOMAIN;

/**
 * Represents the TikTok Products Page in the Dashboard.
 * Provides methods for navigating the page and performing product actions.
 */
public class ProductsPage extends ProductsElement {
    private static final Logger logger = LogManager.getLogger(ProductsPage.class);
    private final WebDriver driver;
    private final UICommonAction commonAction;
    private static List<APIGetTiktokShops.TiktokShopAccount> connectedTiktokShops;

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    /**
     * Loads the connected TikTok shop accounts for further actions on the page.
     *
     * @param connectedTiktokShops List of connected TikTok shop accounts fetched via API.
     * @return the current instance of ProductsPage for method chaining.
     */
    public ProductsPage loadConnectedTiktokAccounts(List<APIGetTiktokShops.TiktokShopAccount> connectedTiktokShops) {
        ProductsPage.connectedTiktokShops = connectedTiktokShops;
        logger.info("Loaded {} connected TikTok shop accounts.", connectedTiktokShops.size());
        if (connectedTiktokShops.isEmpty()) {
            logger.warn("No connected TikTok shops available. Unable to open TikTok products page.");
            return null;
        }
        return this;
    }

    /**
     * Navigates to the TikTok products page.
     * If there are no connected TikTok shops, the navigation will not occur.
     *
     * @return The current instance of ProductsPage for method chaining.
     */
    public ProductsPage openTikTokProductsPage() {
        UICommonAction.performAction("Navigating to TikTok products page",
                () -> driver.get(DOMAIN + "/channel/tiktok/product"),
                () -> Assert.assertEquals(driver.getCurrentUrl(), DOMAIN + "/channel/tiktok/product", "Can not navigate to Tiktok products page."));
        return this;
    }

    /**
     * Initiates the creation of products in GoSell for the given list of unlinked TikTok product IDs.
     *
     * <p>
     * This method filters unlinked TikTok products from the provided list, logs the start time of the creation process,
     * iterates over each unlinked product to select it, performs the creation action, waits for synchronization to complete,
     * logs the end time, and returns both the start and end times in a formatted String array.
     * </p>
     *
     * @param tikTokProducts The list of TikTok products to create in GoSell.
     * @return A String array containing two elements: the start time and end time of the creation action,
     *         formatted as 'YYYY-MM-DD HH:MM:SS.SSS', or null if no unlinked products are found.
     */
    public String[] createProductsToGoSell(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        // Retrieve unlinked TikTok products
        var unlinkedProducts = APIGetTikTokProducts.getUnLinkedTiktokProduct(tikTokProducts);

        if (unlinkedProducts.isEmpty()) {
            logger.info("No unlinked TikTok products found. Skipping creation process.");
            return null;
        }

        // Array to store the start and end times of the action
        String[] actionsTime = new String[2];

        // Record and log the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("Creation process started at {}", actionsTime[0]);

        // Log the number of unlinked products
        logger.info("Creating products in GoSell for {} unlinked TikTok products.", unlinkedProducts.size());

        // Select each unlinked product and perform the creation action
        unlinkedProducts.forEach(tiktokProduct -> selectProduct(tiktokProduct.getThirdPartyItemId()));

        logger.info("Performing creation action for all selected TikTok products.");
        performAction(0); // 0: Create product

        // Wait for synchronization
        UICommonAction.sleepInMiliSecond(120_000, "Waiting for synchronization to complete.");

        // Record and log the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("Creation process completed at {}", actionsTime[1]);

        // Return the start and end times of the creation process
        return actionsTime;
    }


    /**
     * Initiates the update of products in GoSell for the given list of linked TikTok product IDs.
     *
     * <p>
     * This method retrieves the linked TikTok products, checks if there are any available for update,
     * and if so, selects each linked product and performs the update action.
     * If there are no linked products available, a message is logged and the method exits.
     * </p>
     *
     * @param tikTokProducts The list of TikTok products to update in GoSell.
     * @param credentials The seller's login credentials required for authentication.
     */
    public void updateProductsToGoSell(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts, LoginInformation credentials) {
        // Get linked products and error products
        var linkedProducts = APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts);
        var errorProducts = APIGetTikTokProducts.getErrorTiktokProduct(tikTokProducts);

        // Combine the linked and error products into a single list
        var allRelevantProducts = new ArrayList<>(linkedProducts);
        allRelevantProducts.addAll(errorProducts);

        if (allRelevantProducts.isEmpty()) {
            logger.info("No linked or error TikTok products available for update.");
            return;
        }

        logger.info("Updating products in GoSell for {} linked TikTok products.", allRelevantProducts.size());
        allRelevantProducts.forEach(tiktokProduct -> selectProduct(tiktokProduct.getThirdPartyItemId()));
        performAction(1); // 1: Update product

        UICommonAction.sleepInMiliSecond(60_000, "Waiting for synchronization to complete.");
    }

    /**
     * Marks the specified list of TikTok products for deletion in GoSell.
     *
     * <p>This method identifies both linked and unlinked TikTok products from the provided list,
     * marks them for deletion, and returns a list of those products that were successfully deleted.
     * If no products are found for deletion, the method returns null.</p>
     *
     * @param tikTokProducts The list of TikTok products that are available in GoSell.
     * @return A list of TikTok products that were successfully deleted, or null if none were found for deletion.
     */

    public List<APIGetTikTokProducts.TikTokProduct> deleteTiktokProducts(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        // Check for an empty list of TikTok products
        if (tikTokProducts.isEmpty()) {
            logger.info("No TikTok products available for deletion.");
            return null;
        }

        // Initialize list to store products marked for deletion
        List<APIGetTikTokProducts.TikTokProduct> productsToDelete = new ArrayList<>();

        // Collect the first linked product if it exists
        APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts).stream()
                .findFirst()
                .ifPresent(productsToDelete::add);

        // Collect the first unlinked product if it exists
        APIGetTikTokProducts.getUnLinkedTiktokProduct(tikTokProducts).stream()
                .findFirst()
                .ifPresent(productsToDelete::add);

        // Log the deletion process and perform the delete action if any products are marked
        logger.info("Deleting {} TikTok products in GoSell.", productsToDelete.size());
        productsToDelete.forEach(product -> selectProduct(product.getThirdPartyItemId()));
        performAction(2); // 2: Delete product action

        UICommonAction.sleepInMiliSecond(10_000, "Waiting for deletion to complete.");

        return productsToDelete;
    }


    /**
     * Downloads the products associated with the specified list of TikTok products.
     *
     * @param tikTokProducts The list of TikTok products to download.
     */
    public String[] downloadProducts(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts, LoginInformation credentials) {
        if (connectedTiktokShops.isEmpty()) return null;

        if (tikTokProducts.isEmpty()) {
            logger.info("No TikTok products available for download.");
            return null;
        }
        // Array to store the start and end times of the download action
        String[] actionsTime = new String[2];

        // Record the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // Loop through and download each individual TikTok product
        logger.info("Downloading products for {} TikTok products.", tikTokProducts.size());
        tikTokProducts.forEach(tikTokProduct -> {
            // Retrieve the third-party product and shop IDs from the TikTok product
            String productId = tikTokProduct.getThirdPartyItemId();
            String shopId = tikTokProduct.getThirdPartyShopId();

            // Log information about the product being downloaded
            logger.info("Downloading TikTok product - Shop ID: {}, Product ID: {}", shopId, productId);

            // Initiate the download process for the specified TikTok product using API
            new APIDownloadIndividualTiktokProducts(credentials)
                    .downloadProduct(shopId, productId);
        });

        // Wait for the download complete (10 seconds)
        UICommonAction.sleepInMiliSecond(10_000, "Waiting for download to complete.");

        // Record the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // Return the start and end times of the download action
        return actionsTime;
    }

    // Helper method to select a product by its ID
    private void selectProduct(String tiktokProductId) {
        logger.info("Selecting product with ID: {}", tiktokProductId);
        commonAction.clickJS(loc_chkProduct(tiktokProductId));
    }

    // Helper method to perform an action based on the action index
    private void performAction(int actionIndex) {
        String action = switch (actionIndex) {
            case 0 -> "Create product to GoSELL";
            case 1 -> "Update product to GoSELL";
            default -> "Delete product";
        };

        UICommonAction.performAction("Open list actions",
                () -> commonAction.click(loc_lnkSelectActions),
                () -> Assert.assertFalse(commonAction.getListElement(loc_lstActions(actionIndex)).isEmpty(), "Can not open actions list."));
        commonAction.click(loc_lnkSelectActions);
        commonAction.click(loc_lstActions(actionIndex));

        // Confirm the update action
        commonAction.click(loc_dlgConfirmation_btnOK);

        logger.info("Performing action: {}", action);
    }

    /**
     * Verifies the creation of TikTok products in GoSELL.
     * <p>
     * This method checks the unlinked TikTok products against the newly created products in GoSELL,
     * verifies that they have been created successfully, and ensures that the inventory events
     * and mappings are accurately reflected after the actions.
     *
     * @param originalTiktokProducts    The list of original TikTok products before the actions.
     * @param updatedTiktokProducts     The list of current TikTok products after the actions.
     * @param originalInventoryMappings The original inventory mappings before any changes.
     * @param actionTime                Array containing the start and end times of the actions performed.
     * @param connection                Database connection to retrieve inventory events and mappings.
     * @param isAutoSynced              Boolean flag indicating if auto-sync is enabled.
     */
    public void verifyTikTokProductCreationInGoSELL(
            List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
            List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
            List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
            String[] actionTime,
            Connection connection,
            boolean isAutoSynced) {

        // Retrieve list of TikTok products that were unlinked before actions.
        // These products will have mappings created after the actions are completed.
        List<APIGetTikTokProducts.TikTokProduct> unlinkedTiktokProducts = APIGetTikTokProducts.getUnLinkedTiktokProduct(originalTiktokProducts);

        // Verify each unlinked product has been successfully created in GoSELL.
        unlinkedTiktokProducts.forEach(unlinkedProduct -> verifyProductCreatedInGoSELL(unlinkedProduct, updatedTiktokProducts));

        // Retrieve list of product IDs that were created in GoSELL.
        List<String> unlinkedTiktokProductIds = unlinkedTiktokProducts.stream()
                .map(APIGetTikTokProducts.TikTokProduct::getThirdPartyItemId)
                .toList();

        // Retrieve list of TikTok products that were newly created in GoSELL.
        List<APIGetTikTokProducts.TikTokProduct> newLinkedTiktokProducts = updatedTiktokProducts.stream()
                .filter(tiktokProduct -> unlinkedTiktokProductIds.contains(tiktokProduct.getThirdPartyItemId()))
                .toList();

        // Get item mappings for newly linked TikTok products.
        List<APIGetTikTokProducts.ItemMapping> changedItemMappings = APIGetTikTokProducts.getItemMapping(newLinkedTiktokProducts);

        // Retrieve the store ID from the first changed item mapping.
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Verify the inventory event based on the sync status and action times.
        VerifyAutoSyncHelper.verifyInventoryEvent(isAutoSynced, changedItemMappings, actionTime, storeId, connection, "GS_TIKTOK_SYNC_ITEM_EVENT");

        // Verify that the original inventory mappings remain consistent with the newly created mappings.
        VerifyAutoSyncHelper.verifyInventoryMapping(originalInventoryMappings, null, changedItemMappings, storeId, connection);
    }

    /**
     * Verifies if the given unlinked TikTok product has been created in GoSELL.
     *
     * @param unlinkedProduct   The original unlinked TikTok product.
     * @param newTiktokProducts The list of newly fetched TikTok products.
     */
    private void verifyProductCreatedInGoSELL(APIGetTikTokProducts.TikTokProduct unlinkedProduct, List<APIGetTikTokProducts.TikTokProduct> newTiktokProducts) {
        // Attempt to find a matching product in the newly fetched products
        newTiktokProducts.stream()
                .filter(newProduct -> Objects.equals(newProduct.getThirdPartyItemId(), unlinkedProduct.getThirdPartyItemId()))
                .findFirst()
                .ifPresentOrElse(this::assertProductCreated,
                        () -> logger.error("No matching product found in GoSELL for TikTok product ID: {}", unlinkedProduct.getThirdPartyItemId()));
    }

    /**
     * Asserts that the product's GoSELL status is not "UNLINK" indicating it has been created.
     *
     * @param tikTokProduct The TikTok product to verify.
     */
    private void assertProductCreated(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        Assert.assertNotEquals(tikTokProduct.getGosellStatus(), "UNLINK",
                "Product is not created in GoSELL, TikTok product ID: %s".formatted(tikTokProduct.getThirdPartyItemId()));
        logger.info("Verified product creation in GoSELL for TikTok product ID: {}", tikTokProduct.getThirdPartyItemId());
    }

    /**
     * Verifies the update of TikTok products in GoSELL after synchronization actions.
     * <p>
     * This method first identifies TikTok products that were previously linked to ensure
     * their mappings remain consistent, unless there are changes in the variations (indicated
     * by `hasLinkErrorStatus`). The method then verifies that inventory mappings for
     * these products are correctly updated, considering both unchanged mappings and any removed
     * mappings.
     * </p>
     * <p>
     * The verification includes checking that:
     * <ul>
     *   <li>Unchanged inventory mappings are retained for products without variation changes.</li>
     *   <li>Mappings are removed or modified as expected for products with errors.</li>
     *   <li>New inventory mappings are triggered correctly for products requiring updates.</li>
     * </ul>
     * </p>
     *
     * @param originalTiktokProducts    The list of original TikTok products before the actions.
     * @param updatedTiktokProducts     The list of current TikTok products after the actions.
     * @param originalInventoryMappings The original inventory mappings before any changes.
     * @param connection                Database connection to retrieve inventory events and mappings.
     */
    public void verifyTikTokProductUpdatedToGoSELL(
            List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
            List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
            List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
            Connection connection) {

        // Retrieve the list of TikTok products that were initially linked (i.e., without link errors).
        List<APIGetTikTokProducts.TikTokProduct> linkedTiktokProducts = APIGetTikTokProducts.getLinkedTiktokProduct(originalTiktokProducts);

        // Gather inventory mappings for products with no variation changes.
        List<SQLGetInventoryMapping.InventoryMapping> unchangedInventoryMappings = SQLGetInventoryMapping.getInventoryMappingsByItems(linkedTiktokProducts, originalInventoryMappings);

        // Identify TikTok products with link errors (indicating changes to variations).
        List<APIGetTikTokProducts.TikTokProduct> errorProducts = APIGetTikTokProducts.getErrorTiktokProduct(originalTiktokProducts);

        // Retrieve mappings associated with products that have link errors (removed or modified mappings).
        List<SQLGetInventoryMapping.InventoryMapping> removedInventoryMappings = SQLGetInventoryMapping.getInventoryMappingsByItems(errorProducts, originalInventoryMappings);

        // Retrieve Tiktok product IDs from error products for quick lookup
        List<String> errorProductIds = errorProducts.stream()
                .map(APIGetTikTokProducts.TikTokProduct::getThirdPartyItemId)
                .toList();

        // Filter updated TikTok products to include only those with IDs in errorProductIds
        List<APIGetTikTokProducts.TikTokProduct> changedTiktokProducts = updatedTiktokProducts.stream()
                .filter(tikTokProduct -> errorProductIds.contains(tikTokProduct.getThirdPartyItemId()))
                .toList();

        // Obtain new item mappings for products with updated inventory events.
        List<APIGetTikTokProducts.ItemMapping> itemMappingsWithNewInventoryMapping = APIGetTikTokProducts.getItemMapping(changedTiktokProducts);

        // Retrieve store ID from the first item in the original product list.
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Verify inventory mappings based on unchanged, removed, and new mappings.
        VerifyAutoSyncHelper.verifyInventoryMapping(
                unchangedInventoryMappings,
                removedInventoryMappings,
                itemMappingsWithNewInventoryMapping,
                storeId,
                connection
        );
    }

    /**
     * Verifies the deletion of TikTok products and updates to inventory mappings in GoSELL.
     * <p>
     * This method performs several checks to ensure that:
     * 1. Specified TikTok products have been successfully deleted.
     * 2. Inventory mappings related to deleted products are properly updated in the system.
     * 3. Original inventory mappings remain unchanged for products that were not deleted.
     * </p>
     *
     * @param originalTiktokProducts    The list of TikTok products prior to deletion actions.
     * @param updatedTiktokProducts     The list of current TikTok products after deletion actions.
     * @param originalInventoryMappings The list of original inventory mappings before any deletion.
     * @param deletedTiktokProducts     The list of TikTok products that were intended for deletion.
     * @param connection                Database connection to validate inventory mappings.
     * @throws RuntimeException if any specified product could not be deleted.
     */
    public void verifyDeleteProducts(
            List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
            List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
            List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
            List<APIGetTikTokProducts.TikTokProduct> deletedTiktokProducts,
            Connection connection) {

        // Verify each TikTok product was deleted by checking against the updated list
        deletedTiktokProducts.forEach(deletedTikTokProduct -> {
            String deletedTiktokProductId = deletedTikTokProduct.getThirdPartyItemId();
            updatedTiktokProducts.stream()
                    .filter(tikTokProduct -> tikTokProduct.getThirdPartyItemId().equals(deletedTiktokProductId))
                    .findFirst()
                    .ifPresentOrElse(
                            ignored -> {
                                throw new RuntimeException("Cannot delete product with ID: " + deletedTiktokProductId);
                            },
                            () -> logger.info("Product with ID {} successfully removed from updated list.", deletedTiktokProductId));
        });

        // Identify linked TikTok products among deleted ones
        List<APIGetTikTokProducts.TikTokProduct> linkedDeletedProducts = APIGetTikTokProducts.getLinkedTiktokProduct(deletedTiktokProducts);

        // Map deleted item mappings to their corresponding inventory mappings for removal
        List<SQLGetInventoryMapping.InventoryMapping> removedInventoryMappings = SQLGetInventoryMapping.getInventoryMappingsByItems(linkedDeletedProducts, originalInventoryMappings);

        // Remove inventory mappings that correspond to deleted TikTok products from the original list
        originalInventoryMappings.removeAll(removedInventoryMappings);

        // Use the store ID from the first original TikTok product to verify the inventory mappings
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Verify the consistency of inventory mappings post-deletion
        VerifyAutoSyncHelper.verifyInventoryMapping(
                originalInventoryMappings, removedInventoryMappings, null, storeId, connection);
    }

    /**
     * Verifies the consistency of TikTok product downloads in GoSELL.
     * <p>
     * This method ensures that the linked products and inventory mappings remain consistent
     * after the download action. It checks that the product mappings have not been altered unless
     * there are changes in the product variations, in which case it ensures the inventory mappings
     * are updated or removed as required.
     * </p>
     *
     * @param originalTiktokProducts    The list of original TikTok products before any actions.
     * @param updatedTiktokProducts     The list of TikTok products after the download action.
     * @param originalInventoryMappings The inventory mappings before any changes.
     * @param actionTime                An array of action timestamps to verify inventory events.
     * @param isAutoSynced              Indicates whether the syncing was automatic.
     * @param connection                Database connection used to verify inventory mappings.
     */
    public void verifyDownloadProducts(
            List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
            List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
            List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
            String[] actionTime,
            boolean isAutoSynced,  // Added parameter for sync status
            Connection connection) {

        // Download >> get new info >> so sánh với before >> list product changed >> unchanged và changed >> từ changed >> removed and new mappings?

        // Extract store ID from the first original TikTok product to verify against updated mappings
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Get item mappings which have new inventory events after downloading the product
        List<APIGetTikTokProducts.ItemMapping> itemMappingsWithNewInventoryEvents = APIGetTikTokProducts.getItemMapping(
                APIGetTikTokProducts.getLinkedTiktokProduct(originalTiktokProducts));

        // Verify the inventory event based on the sync status and action times
        VerifyAutoSyncHelper.verifyInventoryEvent(isAutoSynced, itemMappingsWithNewInventoryEvents, actionTime, storeId, connection, "GS_TIKTOK_DOWNLOAD_PRODUCT");

        // Identify TikTok products with link errors where variations do not match original linked variations
        List<APIGetTikTokProducts.TikTokProduct> changedTiktokProducts = updatedTiktokProducts.stream()
                .filter(APIGetTikTokProducts.TikTokProduct::getHasLinkErrorStatus)
                .toList();

        // Retrieve item mappings corresponding to products with variation mismatches
        List<APIGetTikTokProducts.ItemMapping> itemMappingsWithRemovedMappings = APIGetTikTokProducts.getItemMapping(changedTiktokProducts);

        // Map changed item mappings to their respective inventory mappings that should be removed
        List<SQLGetInventoryMapping.InventoryMapping> removedInventoryMappings = itemMappingsWithRemovedMappings.stream()
                .map(itemMapping -> SQLGetInventoryMapping.getInventoryMappingsByItemMapping(originalInventoryMappings, itemMapping))
                .flatMap(Collection::stream)
                .toList();

        // Remove any inventory mappings related to deleted TikTok products from the original mappings list
        originalInventoryMappings.removeAll(removedInventoryMappings);

        // Verify consistency of inventory mappings post-deletion, ensuring correct updates or removals in database
        VerifyAutoSyncHelper.verifyInventoryMapping(
                originalInventoryMappings, removedInventoryMappings, null, storeId, connection);
    }
}