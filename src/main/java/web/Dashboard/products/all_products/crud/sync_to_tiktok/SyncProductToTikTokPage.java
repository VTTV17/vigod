package web.Dashboard.products.all_products.crud.sync_to_tiktok;

import api.Seller.sale_channel.tiktok.APIGetTikTokProducts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import sql.SQLGetInventoryMapping;
import utilities.commons.UICommonAction;
import web.Dashboard.sales_channels.tiktok.VerifyAutoSyncHelper;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.commons.UICommonAction.performAction;
import static utilities.links.Links.DOMAIN;

/**
 * Handles product synchronization from GoSELL to TikTok.
 */
public class SyncProductToTikTokPage {
    private final WebDriver driver;
    private final UICommonAction commonAction;
    private final Logger log = LogManager.getLogger();

    public SyncProductToTikTokPage(WebDriver driver) {
        this.driver = driver;
        this.commonAction = new UICommonAction(driver);
    }

    // Locators for various elements on the page
    By loc_btnPublishProduct = By.cssSelector(".gs-button__green");
    By loc_txtProductName = By.cssSelector("#productName");

    // Dynamic locator for category dropdown, adjusts based on level
    By loc_ddvCategory(int level) {
        if (level == 0) return By.cssSelector(".select-category");
        return By.xpath("(//*[contains(@class, 'row-category')])[%d]/p".formatted(level));
    }

    By loc_rtfDescription = By.cssSelector(".fr-element.fr-view");
    By loc_imgSizeChart = By.xpath("(//input[@type=\"file\"])[2]");
    By loc_ddlVariationGroup = By.xpath("//select[contains(@name, 'variation')]");
    By loc_txtBulkPrice = By.xpath("//div[div/div/input[contains(@id, 'priceBulk')]]/input");
    By loc_txtBulkStock = By.xpath("//div[div/div/input[contains(@id, 'stockBulk')]]/input");
    By loc_btnApply = By.cssSelector(".button__apply");

    By loc_txtWeight = By.cssSelector("[name=\"weightNumber\"]");
    By loc_txtWidth = By.xpath("//div[div/div/input[@name='width']]/input");
    By loc_txtHeight = By.xpath("//div[div/div/input[@name='height']]/input");
    By loc_txtLength = By.xpath("//div[div/div/input[@name='length']]/input");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_lblCategoryError = By.cssSelector(".error__category--support");

    /**
     * Navigates to the TikTok product sync page for a specific product ID.
     *
     * @param productId The product ID to sync.
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage navigateToSyncProductPage(int productId) {
        performAction("Navigate to sync TikTok product page.",
                () -> driver.get(DOMAIN + "/channel/tiktok/product/create/%d".formatted(productId)),
                () -> Assert.assertTrue(driver.getCurrentUrl().contains("/channel/tiktok/product/create/")));
        return this;
    }

    /**
     * Updates the TikTok product name with a specified name.
     *
     * @param productName The product name to input.
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage updateTiktokProductName(String productName) {
        performAction("Input product name: " + productName,
                () -> commonAction.sendKeys(loc_txtProductName, productName));
        return this;
    }

    /**
     * Updates the TikTok product name with a default name including a timestamp.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage updateTiktokProductName() {
        String productName = "[Auto][Name] Sync product from GoSELL to TikTok %s".formatted(OffsetDateTime.now());
        return updateTiktokProductName(productName);
    }

    /**
     * Selects categories by navigating through category levels.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage selectCategory() {
        int level = 0;
        int numOfNextLevelCategories = 1;

        do {
            int currentLevel = level;
            int finalNumOfNextLevelCategories = numOfNextLevelCategories;

            numOfNextLevelCategories = performAction(() -> {
                int categoryIndex = (currentLevel == 1 && finalNumOfNextLevelCategories > 5)
                        ? 5
                        : nextInt(finalNumOfNextLevelCategories);

                LogManager.getLogger().info("Category name: {}",
                        commonAction.getText(loc_ddvCategory(currentLevel), categoryIndex));

                commonAction.click(loc_ddvCategory(currentLevel), categoryIndex);

                return commonAction.getListElement(loc_ddvCategory(currentLevel + 1)).size();
            });

            level++;

        } while (numOfNextLevelCategories > 0);

        if (commonAction.getListElement(loc_lblCategoryError).isEmpty()) {
            return this;
        }

        String errMess = commonAction.getText(loc_lblCategoryError);

        log.info("Category is not support, error message: {}", errMess);

        return selectCategory();
    }

    /**
     * Updates the product description with a specified text.
     *
     * @param productDescription The product description to input.
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage updateProductDescription(String productDescription) {
        performAction("Input product description: " + productDescription,
                () -> commonAction.sendKeys(loc_rtfDescription, productDescription));
        return this;
    }

    /**
     * Updates the product description with a default text including a timestamp.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage updateProductDescription() {
        String productDescription = "[Auto][Description] Sync product from GoSELL to TikTok %s".formatted(OffsetDateTime.now());
        return updateProductDescription(productDescription);
    }

    /**
     * Uploads the size chart image if the upload input is present.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage uploadSizeChart() {
        if (commonAction.getListElement(loc_imgSizeChart).isEmpty()) return this;
        log.info("Uploading size chart image.");
        commonAction.uploads(loc_imgSizeChart, System.getProperty("user.dir") + "/src/main/resources/files/images/size_chart.png");
        return this;
    }

    /**
     * Selects variations from the dropdowns.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage selectVariation() {
        int numOfVariationGroups = performAction("Count all variations group", () -> commonAction.getListElement(loc_ddlVariationGroup).size());
        IntStream.range(0, numOfVariationGroups).forEach(groupIndex -> new Select(commonAction.getElement(loc_ddlVariationGroup, groupIndex)).selectByIndex(groupIndex + 1));
        return this;
    }

    /**
     * Updates the bulk price for the product.
     *
     * @param newPrice The new price to input.
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage bulkPrice(long newPrice) {
        log.info("Inputting new bulk price: {}", String.format("%,d", newPrice));
        commonAction.sendKeys(loc_txtBulkPrice, String.valueOf(newPrice));
        commonAction.click(loc_btnApply);
        return this;
    }

    /**
     * Sets a random bulk price within the specified maximum limit.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage bulkPrice() {
        long maxPrice = 180_000_000L;
        long newPrice = nextLong(maxPrice) + 1;
        return bulkPrice(newPrice);
    }

    /**
     * Updates the bulk stock for the product if the input is enabled.
     *
     * @param newStock The new stock value to input.
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage bulkStock(long newStock) {
        if (commonAction.isDisabledJS(loc_txtBulkStock)) return this;
        log.info("Inputting new bulk stock: {}", String.format("%,d", newStock));
        commonAction.sendKeys(loc_txtBulkStock, String.valueOf(newStock));
        commonAction.click(loc_btnApply);
        return this;
    }

    /**
     * Sets a random bulk stock within the specified maximum limit.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage bulkStock() {
        long maxStock = 999_999L;
        long newStock = nextLong(maxStock) + 1;
        return bulkStock(newStock);
    }


    /**
     * Updates the product's dimensions (weight, length, width, height).
     *
     * @param weight The product's weight.
     * @param length The product's length.
     * @param width  The product's width.
     * @param height The product's height.
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage updateProductDimension(int weight, int length, int width, int height) {
        log.info("Inputting product dimensions - Weight: {}, Length: {}, Width: {}, Height: {}", weight, length, width, height);
        commonAction.sendKeys(loc_txtWeight, String.valueOf(weight));
        commonAction.sendKeys(loc_txtLength, String.valueOf(length));
        commonAction.sendKeys(loc_txtWidth, String.valueOf(width));
        commonAction.sendKeys(loc_txtHeight, String.valueOf(height));
        return this;
    }

    /**
     * Updates the product dimensions (weight, length, width, height) with random values within predefined limits.
     *
     * @return SyncProductToTikTokPage
     */
    public SyncProductToTikTokPage updateProductDimension() {
        return updateProductDimension(nextInt(500) + 1,  // Weight: 1 to 500
                nextInt(10) + 1,   // Length: 1 to 10
                nextInt(10) + 1,   // Width: 1 to 10
                nextInt(10) + 1);  // Height: 1 to 10
    }

    /**
     * Publishes the product to TikTok and verifies the success via a toast message.
     * Records the start and end times of the publishing process in UTC format.
     *
     * @return an array containing the start and end times of the publishing process:
     * - actionsTime[0] - Start time (UTC)
     * - actionsTime[1] - End time (UTC)
     */
    public String[] publishProductToTikTok() {
        // Array to store the start and end times of the action
        String[] actionsTime = new String[2];

        // Record the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        log.info("Publish process started at {}", actionsTime[0]);

        performAction("Publish product to TikTok",
                () -> commonAction.click(loc_btnPublishProduct),
                () -> Assert.assertFalse(commonAction.getListElement(loc_dlgToastSuccess, 10000).isEmpty(), "Can not publish product to TikTok."));

        // Wait for synchronization
        UICommonAction.sleepInMiliSecond(30_000, "Waiting for synchronization to complete.");

        // Record and log the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        log.info("Publish process completed at {}", actionsTime[1]);

        // Return the start and end times of the creation process
        return actionsTime;
    }


    public void verifyPublishProductToTikTok(List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
                                             List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
                                             List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
                                             int goSELLItemId,
                                             String[] actionsTime,
                                             Connection connection, boolean isAutoSynced) {

        // Verify each TikTok product was deleted by checking against the updated list
        // Check if the product exists in the list, and handle accordingly.
        APIGetTikTokProducts.TikTokProduct existingProduct = updatedTiktokProducts.stream()
                .filter(tikTokProduct -> tikTokProduct.getBcItemId().equals(String.valueOf(goSELLItemId)))
                .findFirst()
                .orElse(null);

        if (existingProduct == null) {
            throw new RuntimeException("Cannot publish product with ID: " + goSELLItemId);
        }
        log.info("Product with ID {} successfully published to TikTok.", goSELLItemId);

        // Get new mappings
        var itemMappingsWithNewInventoryMappings = APIGetTikTokProducts.getItemMapping(List.of(existingProduct));

        // Use the store ID from the first original TikTok product to verify the inventory mappings
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Verify the inventory event based on the sync status and action times
        VerifyAutoSyncHelper.verifyInventoryEvent(isAutoSynced,
                itemMappingsWithNewInventoryMappings, actionsTime,
                storeId, connection, "GS_TIKTOK_SYNC_ITEM_EVENT");

        // Verify the consistency of inventory mappings post-deletion
        VerifyAutoSyncHelper.verifyInventoryMapping(
                originalInventoryMappings, null,
                itemMappingsWithNewInventoryMappings, storeId, connection);
    }
}
