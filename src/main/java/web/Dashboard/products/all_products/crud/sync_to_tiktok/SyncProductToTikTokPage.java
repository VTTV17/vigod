package web.Dashboard.products.all_products.crud.sync_to_tiktok;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import utilities.commons.UICommonAction;

import java.time.OffsetDateTime;
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

        return this;
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
     * Publishes the product to TikTok and verifies success via a toast message.
     */
    public void publishProductToTikTok() {
        performAction("Publish product to TikTok",
                () -> commonAction.click(loc_btnPublishProduct),
                () -> Assert.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not publish product to TikTok."));
    }
}
