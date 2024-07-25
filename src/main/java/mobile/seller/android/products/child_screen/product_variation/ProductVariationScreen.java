package mobile.seller.android.products.child_screen.product_variation;

import lombok.Data;
import lombok.Getter;
import mobile.seller.android.products.child_screen.inventory.InventoryScreen;
import mobile.seller.android.products.child_screen.product_description.ProductDescriptionScreen;
import mobile.seller.android.products.child_screen.select_image_popup.SelectImagePopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;
import utilities.data.DataGenerator;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.environment.goSELLEREnvironment.goSELLERProductDetailActivity;

public class ProductVariationScreen extends ProductVariationElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();
    String defaultLanguage;
    boolean hasDiscount;
    boolean hasCostPrice;
    ProductInfo productInfo;
    int variationIndex;
    String variationValue;
    BranchInfo branchInfo;

    public ProductVariationScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonMobile = new UICommonAndroid(driver);
    }

    @Data
    public static class VariationInfo {
        private String variation;
        private String name;
        private String description;
        private String barcode;
        private long listingPrice;
        private long sellingPrice;
        private long costPrice;
        private List<Integer> stockQuantity;
        private String status = "ACTIVE";
    }

    private static long getCurrentEpoch() {
        return Instant.now().toEpochMilli();
    }

    @Getter
    public static VariationInfo variationInfo;

    public ProductVariationScreen getVariationInformation(String defaultLanguage, BranchInfo branchInfo, boolean hasDiscount, boolean hasCostPrice, int variationIndex, ProductInfo productInfo) {
        // Get default language
        this.defaultLanguage = defaultLanguage;

        // Get branch information
        this.branchInfo = branchInfo;

        // Get price condition
        this.hasDiscount = hasDiscount;

        // Get cost price condition
        this.hasCostPrice = hasCostPrice;

        // Get product information
        this.productInfo = productInfo;

        // Get variation index
        this.variationIndex = variationIndex;

        // Get variation value
        this.variationValue = productInfo.getVariationValuesMap().get(defaultLanguage).get(this.variationIndex);

        // Log
        logger.info("Update information of '{}' variation", variationValue);

        //Init variation information model
        variationInfo = new VariationInfo();

        // Get variation model code
        variationInfo.setVariation(variationValue);

        return this;
    }


    void selectVariationImages() {
        // Get list images
        List<String> imageFileNames = new DataGenerator().getAllFileNamesInFolder("images");

        // Sent list images to mobile device
        imageFileNames.forEach(fileName -> commonMobile.pushFileToMobileDevices(fileName));

        // Open select image popup
        commonMobile.click(loc_btnSelectImage);

        // Select images
        new SelectImagePopup(driver).selectImages(imageFileNames);

        // Log
        logger.info("Select variation images.");
    }

    void updateVariationName() {
        // Input variation name
        String name = "[%s][%s] Variation name %s".formatted(defaultLanguage, variationValue, getCurrentEpoch());
        commonMobile.sendKeys(loc_txtVariationName, name);

        // Get variation name
        variationInfo.setName(name);

        // Log
        logger.info("Input variation name: {}", name);
    }

    void updateVariationDescription() {
        // Get reuse description checkbox status
        boolean reuseParentDescription = nextBoolean();

        if (reuseParentDescription) {
            // Get variation description
            variationInfo.setDescription(productInfo.getMainProductDescriptionMap().get(defaultLanguage));

            // Log
            logger.info("Reuse parent description");
        } else {
            // Get current reuse description checkbox status
            boolean status = commonMobile.isChecked(loc_chkReuseProductDescription);

            // Uncheck reuse description checkbox
            if (status) commonMobile.click(loc_chkReuseProductDescription);

            // Open description popup
            commonMobile.click(loc_btnVariationDescription);

            // Input product description
            String description = "[%s][%s] Variation description %s".formatted(defaultLanguage, variationValue, getCurrentEpoch());
            new ProductDescriptionScreen(driver).inputDescription(description);

            // Get variation description
            variationInfo.setDescription(description);

            // Log
            logger.info("Input variation description: {}", description);
        }
    }

    void updateVariationPrice() {
        // Input listing price
        long listingPrice = nextLong(MAX_PRICE);
        commonMobile.sendKeys(loc_txtVariationListingPrice, String.valueOf(listingPrice));
        logger.info("Input variation listing price: %,d".formatted(listingPrice));

        // Input selling price
        long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
        commonMobile.sendKeys(loc_txtVariationSellingPrice, String.valueOf(sellingPrice));
        logger.info("Input variation selling price: %,d".formatted(sellingPrice));

        // Input cost price
        long costPrice = hasCostPrice ? nextLong(Math.max(sellingPrice, 1)) : 0;
        commonMobile.sendKeys(loc_txtVariationCostPrice, String.valueOf(costPrice));
        logger.info("Input variation cost price: %,d".formatted(costPrice));

        // Get variation price
        variationInfo.setListingPrice(listingPrice);
        variationInfo.setSellingPrice(sellingPrice);
        variationInfo.setCostPrice(costPrice);
    }

    void updateVariationSKU() {
        // Input variation SKU
        String sku = "SKU%s".formatted(getCurrentEpoch());
        commonMobile.sendKeys(loc_txtVariationSKU, sku);

        // Log
        logger.info("Input variation SKU: {}", sku);
    }

    void updateVariationBarcode() {
        // Input variation barcode
        String barcode = "Barcode%s".formatted(getCurrentEpoch());
        commonMobile.sendKeys(loc_txtVariationBarcode, barcode);

        // Log
        logger.info("Input variation barcode: {}", barcode);

        // Get variation barcode
        variationInfo.setBarcode(barcode);
    }

    void addVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!productInfo.getLotAvailable() || productInfo.getManageInventoryByIMEI()) {
            // Navigate to inventory screen
            commonMobile.click(loc_btnInventory);

            // Add variation stock
            new InventoryScreen(driver).addStock(productInfo.getManageInventoryByIMEI(), branchInfo, variationValue, branchStock);
        } else logger.info("Product is managed by lot, requiring add stocks in the lot screen.");

        // Get new stock quantity
        List<Integer> stockQuantity = IntStream.range(0, branchInfo.getBranchID().size())
                .mapToObj(branchIndex -> productInfo.getLotAvailable() ? 0 : (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex])
                .toList();
        variationInfo.setStockQuantity(stockQuantity);
    }

    void updateVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!productInfo.getLotAvailable() || productInfo.getManageInventoryByIMEI()) {
            // Navigate to inventory screen
            commonMobile.click(loc_btnInventory);

            // Add variation stock
            new InventoryScreen(driver).updateStock(productInfo.getManageInventoryByIMEI(), branchInfo, variationValue, branchStock);
        } else logger.info("Product is managed by lot, requiring stock updates in the lot screen.");

        // Get new stock quantity
        List<Integer> stockQuantity = IntStream.range(0, branchInfo.getBranchID().size())
                .mapToObj(branchIndex -> productInfo.getLotAvailable() ? 0 : ((branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]))
                .toList();
        variationInfo.setStockQuantity(stockQuantity);
    }

    void updateVariationStatus() {
        if (commonMobile.isShown(loc_btnDeactivate)) {
            // Get new variation status
            String newStatus = nextBoolean() ? "ACTIVE" : "DEACTIVE";

            // Get current variation status
            String currentStatus = productInfo.getVariationStatus().get(variationIndex);

            // Update variation status
            if (!currentStatus.equals(newStatus)) {
                commonMobile.click(loc_btnDeactivate);
            }

            // Get variation status
            variationInfo.setStatus(newStatus);

            // Log
            logger.info("New variation's status: {}", newStatus);
        }
    }

    void completeUpdateVariation() {
        // Save all product information
        commonMobile.click(loc_btnSave);

        // Wait product detail screen loaded
        commonMobile.waitUntilScreenLoaded(goSELLERProductDetailActivity);
    }

    public void addVariationInformation(int... branchStock) {
        selectVariationImages();
        updateVariationName();
        updateVariationDescription();
        updateVariationPrice();
//        updateVariationSKU();
        updateVariationBarcode();
        addVariationStock(branchStock);
        completeUpdateVariation();
    }

    public void updateVariationInformation(int... branchStock) {
        selectVariationImages();
        updateVariationName();
        updateVariationDescription();
        updateVariationPrice();
//        updateVariationSKU();
        updateVariationBarcode();
        updateVariationStock(branchStock);
        updateVariationStatus();
        completeUpdateVariation();
    }
}
