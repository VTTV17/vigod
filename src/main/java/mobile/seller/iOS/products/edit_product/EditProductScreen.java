package mobile.seller.iOS.products.edit_product;

import api.Seller.products.all_products.APIProductDetail;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import lombok.SneakyThrows;
import mobile.seller.iOS.login.LoginScreen;
import mobile.seller.iOS.products.child_screen.crud_variations.CRUDVariationScreen;
import mobile.seller.iOS.products.child_screen.edit_multiple.EditMultipleScreen;
import mobile.seller.iOS.products.child_screen.inventory.InventoryScreen;
import mobile.seller.iOS.products.child_screen.product_description.ProductDescriptionScreen;
import mobile.seller.iOS.products.child_screen.product_variation.ProductVariationScreen;
import mobile.seller.iOS.products.child_screen.select_image.SelectImagePopup;
import mobile.seller.iOS.products.product_management.ProductManagementScreen;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
import utilities.data.DataGenerator;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mobile.seller.iOS.products.product_management.ProductManagementElement.loc_txtSearchBox;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class EditProductScreen extends EditProductElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    private static String defaultLanguage;
    private static BranchInfo branchInfo;
    private ProductInfo productInfo;
    ProductManagementScreen productManagementScreen;

    public EditProductScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);

        // Init product management POM
        productManagementScreen = new ProductManagementScreen(driver);

        // Get store default language
        defaultLanguage = new StoreInformation(LoginScreen.getLoginInformation())
                .getInfo()
                .getDefaultLanguage();

        // Get branch information
        branchInfo = new BranchManagement(LoginScreen.getLoginInformation()).getInfo();
    }

    private boolean hideRemainingStock = false;
    private boolean showOutOfStock = true;
    private boolean manageByIMEI;
    private boolean manageByLot = false;
    private boolean hasDiscount = true;
    private boolean hasCostPrice = true;
    private boolean hasDimension = false;
    private boolean showOnWeb = true;
    private boolean showOnApp = true;
    private boolean showInStore = true;
    private boolean showInGoSocial = true;
    private boolean hasPriority = false;

    public EditProductScreen getHideRemainingStock(boolean hideRemainingStock) {
        this.hideRemainingStock = hideRemainingStock;
        return this;
    }

    public EditProductScreen getShowOutOfStock(boolean showOutOfStock) {
        this.showOutOfStock = showOutOfStock;
        return this;
    }

    public EditProductScreen getManageByLotDate(boolean manageByLot) {
        this.manageByLot = manageByLot;
        return this;
    }

    public EditProductScreen getHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
        return this;
    }

    public EditProductScreen getHasCostPrice(boolean hasCostPrice) {
        this.hasCostPrice = hasCostPrice;
        return this;
    }

    public EditProductScreen getHasDimension(boolean hasDimension) {
        this.hasDimension = hasDimension;
        return this;
    }

    public EditProductScreen getProductSellingPlatform(boolean showOnWeb, boolean showOnApp, boolean showInStore, boolean showInGoSocial) {
        this.showOnWeb = showOnWeb;
        this.showOnApp = showOnApp;
        this.showInStore = showInStore;
        this.showInGoSocial = showInGoSocial;
        return this;
    }

    public EditProductScreen getHasPriority(boolean hasPriority) {
        this.hasPriority = hasPriority;
        return this;
    }

    private static long getCurrentEpoch() {
        return Instant.now().toEpochMilli();
    }

    private boolean hasLot;
    private APIProductDetail apiProductDetail;

    public EditProductScreen navigateToProductDetailScreen(int productId) {
        // Get product information
        apiProductDetail = new APIProductDetail(LoginScreen.getLoginInformation());
        this.productInfo = apiProductDetail.getInfo(productId);

        // Get lot manage status
        this.hasLot = productInfo.getLotAvailable();

        // Get product name
        String productName = productInfo.getMainProductNameMap().get(defaultLanguage);

        // get inventory manage type
        manageByIMEI = productInfo.getManageInventoryByIMEI();

        // Navigate to product detail screen
        productManagementScreen.navigateToProductManagementScreen()
                .navigateToProductDetailScreen(productName);

        // Log
        logger.info("Navigate to product detail screen");

        return this;
    }

    void removeOldVariations() {
        // Get product name
        String productName = productInfo.getMainProductNameMap().get(defaultLanguage);

        // If product has model, remove model and saves changes.
        if (productInfo.isHasModel() && !productInfo.getLotAvailable()) {

            // remove variation
            removeVariation();

            // Navigate to product detail screen
            productManagementScreen.navigateToProductDetailScreen(productName);

            // log
            logger.info("Remove old variation and navigate to product detail again");
        }
    }

    void selectProductImages() {
        // Remove product images
        int numberOfImages = commonIOS.getListElement(loc_icnDeleteImages).size();
        IntStream.range(0, numberOfImages)
                .forEach(index -> commonIOS.click(loc_icnDeleteImages));
        logger.info("Remove old product images");

        // Open select image popup
        commonIOS.click(loc_icnProductImage);

        // Select images
        new SelectImagePopup(driver).selectImages();

        // Log
        logger.info("Select product images.");
    }

    void inputProductName() {
        // Input product name
        String name = "[%s][%s] Product name %s".formatted(defaultLanguage, manageByIMEI ? "IMEI" : "NORMAL", getCurrentEpoch());
        commonIOS.sendKeys(loc_txtProductName, name);

        // Get new product name
        Map<String, String> mainNameMap = new HashMap<>(productInfo.getMainProductNameMap());
        mainNameMap.put(defaultLanguage, name);
        productInfo.setMainProductNameMap(mainNameMap);

        // Log
        logger.info("Input product name: {}", name);
    }

    void inputProductDescription() {
        // Open description popup
        commonIOS.click(loc_btnProductDescription);

        // Input product description
        String description = "[%s] Product description %s".formatted(defaultLanguage, getCurrentEpoch());
        new ProductDescriptionScreen(driver).inputDescription(description);

        // Get new product description
        Map<String, String> mainDescriptionMap = new HashMap<>(productInfo.getMainProductDescriptionMap());
        mainDescriptionMap.put(defaultLanguage, description);
        productInfo.setMainProductDescriptionMap(mainDescriptionMap);

        // Log
        logger.info("Input product description: {}", description);

    }

    void inputWithoutVariationPrice() {
        // Input listing price
        long listingPrice = nextLong(MAX_PRICE);
        commonIOS.sendKeys(loc_txtWithoutVariationListingPrice, String.valueOf(listingPrice));
        logger.info("Input without variation listing price: %,d".formatted(listingPrice));

        // Input selling price
        long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
        commonIOS.sendKeys(loc_txtWithoutVariationSellingPrice, String.valueOf(sellingPrice));
        logger.info("Input without variation selling price: %,d".formatted(sellingPrice));

        // Input cost price
        long costPrice = hasCostPrice ? nextLong(Math.max(sellingPrice, 1)) : 0;
        commonIOS.sendKeys(loc_txtWithoutVariationCostPrice, String.valueOf(costPrice));
        logger.info("Input without variation cost price: %,d".formatted(costPrice));

        // Get new product price
        productInfo.setProductListingPrice(List.of(listingPrice));
        productInfo.setProductSellingPrice(List.of(sellingPrice));
        productInfo.setProductCostPrice(List.of(costPrice));
    }

    void inputWithoutVariationSKU() {
        // Input without variation SKU
        String sku = "SKU%s".formatted(getCurrentEpoch());
        commonIOS.sendKeys(loc_txtWithoutVariationSKU, sku);

        // Log
        logger.info("Input without variation SKU: {}", sku);
    }

    void inputWithoutVariationBarcode() {
        // Input without variation barcode
        String barcode = "Barcode%s".formatted(getCurrentEpoch());
        commonIOS.sendKeys(loc_txtWithoutVariationBarcode, barcode);

        // Log
        logger.info("Input without variation barcode: {}", barcode);

        // Get new barcode
        productInfo.setBarcodeList(List.of(barcode));
    }

    @SneakyThrows
    void hideRemainingStockOnOnlineStore() {
        // Get current checkbox status
        boolean status = commonIOS.isChecked(commonIOS.getElement(loc_chkHideRemainingStock));

        // Hide remaining stock on online store config
        if (!Objects.equals(hideRemainingStock, status)) commonIOS.click(loc_chkHideRemainingStock);

        // Log
        logger.info("Hide remaining stock on online store config: {}", hideRemainingStock);

        // Get new hide remaining stock config
        productInfo.setHideStock(hideRemainingStock);
    }

    @SneakyThrows
    void displayIfOutOfStock() {
        // Get current checkbox status
        boolean status = commonIOS.isChecked(commonIOS.getElement(loc_chkDisplayIfOutOfStock));

        // Add display out of stock config
        if (!Objects.equals(showOutOfStock, status)) commonIOS.click(loc_chkDisplayIfOutOfStock);

        // Log
        logger.info("Display out of stock config: {}", showOutOfStock);

        // Get new show out of stock config
        productInfo.setShowOutOfStock(showOutOfStock);
    }

    void manageProductByLot() {
        if (!manageByIMEI) {
            // Get current manage by lot checkbox status
            boolean status = commonIOS.isChecked(commonIOS.getElement(loc_chkManageStockByLotDate));

            // Manage product by lot
            if (manageByLot && !status) commonIOS.click(loc_chkManageStockByLotDate);

            // Log
            logger.info("Manage product by lot date: {}", manageByLot || status);

            // Get new lot available
            productInfo.setLotAvailable(manageByLot || status);
        } else logger.info("Lot only support for the product has inventory managed by product");
    }

    void addWithoutVariationStock(int... branchStock) {
        // Check product is managed by lot or not
        if (!manageByLot || manageByIMEI) {
            // Navigate to inventory screen
            commonIOS.click(loc_btnInventory);

            // Add without variation stock
            new InventoryScreen(driver).updateStock(manageByIMEI, branchInfo, "", branchStock);
        } else logger.info("Product is managed by lot, requiring stock updates in the lot screen.");

        // Get new stock quantity
        List<Integer> stockQuantity = IntStream.range(0, branchInfo.getBranchID().size())
                .mapToObj(branchIndex -> productInfo.getLotAvailable() ? 0 : ((branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]))
                .toList();
        productInfo.setProductStockQuantityMap(Map.of(String.valueOf(productInfo.getProductId()), stockQuantity));
    }

    void modifyShippingInformation() {
        // Get current shipping config status
        boolean status = commonIOS.isChecked(commonIOS.getElement(loc_swShipping));

        // Update shipping status
        if (!Objects.equals(hasDimension, status)) commonIOS.click(loc_swShipping);

        // If product has dimension, add shipping configuration
        // Add product weight
        if (hasDimension) {
            commonIOS.sendKeys(loc_txtWeight, "10");
            logger.info("Add product weight: 10g");

            // Add product length
            commonIOS.sendKeys(loc_txtLength, "10");
            logger.info("Add product length: 10cm");

            // Add product width
            commonIOS.sendKeys(loc_txtWidth, "10");
            logger.info("Add product width: 10cm");

            // Add product height
            commonIOS.sendKeys(loc_txtHeight, "10");
            logger.info("Add product height: 10cm");
        } else logger.info("Product do not have shipping information.");
    }

    void modifyProductSellingPlatform() {
        /* WEB PLATFORM */
        // Get current show on web status
        boolean webStatus = commonIOS.isChecked(commonIOS.getElement(loc_swWeb));

        // Modify show on web config
        if (!Objects.equals(showOnWeb, webStatus)) commonIOS.click(loc_swWeb);

        // Log
        logger.info("On web configure: {}", showOnWeb);

        /* APP PLATFORM */
        // Get current show on app status
        boolean appStatus = commonIOS.isChecked(commonIOS.getElement(loc_swApp));

        // Modify show on app config
        if (!Objects.equals(showOnApp, appStatus)) commonIOS.click(loc_swApp);

        // Log
        logger.info("On app configure: {}", showOnApp);

        /* IN-STORE PLATFORM */
        // Get current show in-store status
        boolean inStoreStatus = commonIOS.isChecked(commonIOS.getElement(loc_swInStore));

        // Modify show in-store config
        if (!Objects.equals(showInStore, inStoreStatus)) commonIOS.click(loc_swInStore);

        // Log
        logger.info("In store configure: {}", showInStore);

        /* GO SOCIAL PLATFORM */
        // Get current show in goSocial status
        boolean goSocialStatus = commonIOS.isChecked(commonIOS.getElement(loc_swGoSocial));

        // Modify show in goSocial config
        if (!Objects.equals(showInGoSocial, goSocialStatus)) commonIOS.click(loc_swGoSocial);

        // Log
        logger.info("In goSOCIAL configure: {}", showInGoSocial);

        // Get new platform config
        productInfo.setOnApp(showOnApp);
        productInfo.setOnWeb(showOnWeb);
        productInfo.setInGoSocial(showInGoSocial);
        productInfo.setInStore(showInStore);
    }

    void modifyPriority() {
        // Get current priority config status
        boolean status = commonIOS.isChecked(commonIOS.getElement(loc_swPriority));

        // Update priority config
        if (!Objects.equals(hasPriority, status)) commonIOS.click(loc_swPriority);

        // If product has priority, add priority
        if (hasPriority) {
            // Input priority
            int priority = nextInt(100);
            commonIOS.sendKeys(loc_txtPriority, String.valueOf(priority));

            // Log
            logger.info("Product priority: {}", priority);
        } else logger.info("Product do not have priority configure");
    }

    void addVariations() {
        // If product is managed by Lot, that is not allow to remove variation
        if (this.hasLot) {
            logger.info("Product that is managed by Lot, do not allow add variation");
        } else {
            // Else navigate to Add/Edit variation screen to add new variation
            commonIOS.click(loc_swVariation);
            commonIOS.click(loc_btnAddVariation);

            // Add/Edit variation
            new CRUDVariationScreen(driver).addVariation(defaultLanguage);

            // Get variation map
            Map<String, List<String>> variationMap = CRUDVariationScreen.getVariationMap();
            String variationGroupName = variationMap.keySet().toString().replaceAll("[\\[\\]\\s]", "").replaceAll(",", "|");
            List<String> variationValueList = new DataGenerator().getVariationList(variationMap);

            // Get store information
            StoreInfo storeInfo = new StoreInformation(LoginScreen.getLoginInformation()).getInfo();

            // Init variation group map
            Map<String, String> groupMap = new HashMap<>();

            // Init variation value map
            Map<String, List<String>> valueMap = new HashMap<>();

            // Get variation group/value
            storeInfo.getStoreLanguageList()
                    .parallelStream()
                    .forEach(languageKey -> {
                        groupMap.put(languageKey, variationGroupName);
                        valueMap.put(languageKey, variationValueList);
                    });

            // Get new variation information
            productInfo.setVariationGroupNameMap(groupMap);
            productInfo.setVariationValuesMap(valueMap);
            productInfo.setHasModel(true);
        }
    }

    void removeVariation() {
        // If product is managed by Lot, that is not allow to remove variation
        if (this.hasLot) {
            logger.info("Product that is managed by Lot, do not allow remove variation");
        }
        // If product has variation, remove old variation
        else if (!commonIOS.getListElement(loc_lstVariations).isEmpty()) {
            // Navigate to Add/Edit variation
            commonIOS.click(loc_btnEditVariation);

            // Remove all variations and save changes
            new CRUDVariationScreen(driver).removeOldVariation()
                    .saveChanges();

            // Set cost price = 0
            commonIOS.sendKeys(loc_txtWithoutVariationCostPrice, "0");

            // Save changes
            commonIOS.click(loc_btnSave);

            // Wait product updated
            commonIOS.getElement(loc_txtSearchBox);
        }
    }

    void bulkUpdateVariations(int increaseNum, int... branchStock) {
        // Get total variations
        int totalVariations = this.hasLot
                ? productInfo.getVariationModelList().size()
                : CRUDVariationScreen.getVariationMap()
                    .values()
                    .stream()
                    .mapToInt(List::size)
                    .reduce(1, (a, b) -> a * b);

        // Navigate to edit multiple screen
        if (totalVariations > 1) {
            commonIOS.click(loc_btnEditMultiple);

            // Init edit multiple model
            EditMultipleScreen editMultipleScreen = new EditMultipleScreen(driver);

            // Bulk update price
            long listingPrice = nextLong(MAX_PRICE);
            long sellingPrice = hasDiscount ? nextLong(Math.max(listingPrice, 1)) : listingPrice;
            editMultipleScreen.bulkUpdatePrice(listingPrice, sellingPrice);

            // Get new product price
            productInfo.setProductListingPrice(IntStream.range(0, totalVariations).mapToLong(varIndex -> listingPrice).boxed().toList());
            productInfo.setProductSellingPrice(IntStream.range(0, totalVariations).mapToLong(varIndex -> sellingPrice).boxed().toList());

            // Bulk update stock
            editMultipleScreen.bulkUpdateStock(manageByIMEI, manageByLot, branchInfo, increaseNum, branchStock);

            // Get new stock quantity
            List<Integer> stockQuantity = IntStream.range(0, branchInfo.getBranchID().size())
                    .mapToObj(branchIndex -> (manageByIMEI || productInfo.getLotAvailable()) ? 0 : (((branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]) + (branchIndex * increaseNum)))
                    .toList();
            Map<String, List<Integer>> stockMap = IntStream.range(0, totalVariations).boxed().collect(Collectors.toMap(String::valueOf, variationIndex -> stockQuantity, (a, b) -> b));
            productInfo.setProductStockQuantityMap(stockMap);

        } else {
            // Can not bulk actions when total of variations is 1
            // So we must be updated variation information at that's detail screen
            updateVariationInformation(branchStock);
        }
    }

    void completeUpdateProduct() {
        // Save all product information
        commonIOS.click(loc_btnSave);

        // If product are managed by lot, accept when warning shows
        if (!commonIOS.getListElement(loc_dlgWarningManagedByLot_btnOK).isEmpty()) {
            commonIOS.click(loc_dlgWarningManagedByLot_btnOK);

            // Log
            logger.info("Confirm managed by lot");
        }

        // Wait product management screen loaded
        assertCustomize.assertFalse(commonIOS.getListElement(loc_txtSearchBox).isEmpty(), "Can not update product");

        // If product are updated, check information after updating
        if (!commonIOS.getListElement(loc_txtSearchBox).isEmpty()) {
            // Get current product information
            ProductInfo currentInfo = apiProductDetail.getInfo(productInfo.getProductId());

            // Check main product name
            assertCustomize.assertEquals(productInfo.getMainProductNameMap(), currentInfo.getMainProductNameMap(),
                    "Main product name must be %s, but found %s".formatted(productInfo.getMainProductNameMap(), currentInfo.getMainProductNameMap()));

            // Check main product description
            assertCustomize.assertEquals(productInfo.getMainProductDescriptionMap(), currentInfo.getMainProductDescriptionMap(),
                    "Main product description must be %s, but found %s".formatted(productInfo.getMainProductDescriptionMap(), currentInfo.getMainProductDescriptionMap()));

            // Check product listing price
            assertCustomize.assertEquals(productInfo.getProductListingPrice(), currentInfo.getProductListingPrice(),
                    "Product listing price must be %s, but found %s".formatted(productInfo.getProductListingPrice(), currentInfo.getProductListingPrice()));

            // Check product selling price
            assertCustomize.assertEquals(productInfo.getProductSellingPrice(), currentInfo.getProductSellingPrice(),
                    "Product selling price must be %s, but found %s".formatted(productInfo.getProductSellingPrice(), currentInfo.getProductSellingPrice()));

            // Check online store config
            assertCustomize.assertEquals(productInfo.getShowOutOfStock(), currentInfo.getShowOutOfStock(),
                    "Show when out of stock config must be %s, but found %s".formatted(productInfo.getShowOutOfStock(), currentInfo.getShowOutOfStock()));
            assertCustomize.assertEquals(productInfo.isHideStock(), currentInfo.isHideStock(),
                    "Hide remaining stock config must be %s, but found %s".formatted(productInfo.isHideStock(), currentInfo.isHideStock()));

            // Check inventory
            assertCustomize.assertEquals(productInfo.getLotAvailable(), currentInfo.getLotAvailable(),
                    "Manage by lot must be %s, but found %s".formatted(productInfo.getLotAvailable(), currentInfo.getLotAvailable()));

            // Check stock quantity
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(productInfo.getProductStockQuantityMap().values(), currentInfo.getProductStockQuantityMap().values()),
                    "Product stock quantity must be %s, but found %s".formatted(productInfo.getProductStockQuantityMap().values(), currentInfo.getProductStockQuantityMap().values()));

            // Check selling platform
            assertCustomize.assertEquals(productInfo.getOnWeb(), currentInfo.getOnWeb(),
                    "Web config must be %s, but found %s".formatted(productInfo.getOnWeb(), currentInfo.getOnWeb()));
            assertCustomize.assertEquals(productInfo.getOnApp(), currentInfo.getOnApp(),
                    "App config must be %s, but found %s".formatted(productInfo.getOnApp(), currentInfo.getOnApp()));
            assertCustomize.assertEquals(productInfo.getInStore(), currentInfo.getInStore(),
                    "In-store config must be %s, but found %s".formatted(productInfo.getInStore(), currentInfo.getInStore()));
            assertCustomize.assertEquals(productInfo.getInGoSocial(), currentInfo.getInGoSocial(),
                    "In GoSOCIAL config must be %s, but found %s".formatted(productInfo.getInGoSocial(), currentInfo.getInGoSocial()));

            // Check without variation cost price/barcode
            if (!productInfo.isHasModel()) {
                // Check product barcode
                assertCustomize.assertEquals(productInfo.getBarcodeList(), currentInfo.getBarcodeList(),
                        "Product barcode must be %s, but found %s".formatted(productInfo.getBarcodeList(), currentInfo.getBarcodeList()));

                // Check product cost price
                assertCustomize.assertEquals(productInfo.getProductCostPrice(), currentInfo.getProductCostPrice(),
                        "Product cost price must be %s, but found %s".formatted(productInfo.getProductCostPrice(), currentInfo.getProductCostPrice()));
            }

            // Check variation information
            if (getUpdateVariationInformation()) {
                // Check product barcode
                assertCustomize.assertEquals(productInfo.getBarcodeList(), currentInfo.getBarcodeList(),
                        "Product barcode must be %s, but found %s".formatted(productInfo.getBarcodeList(), currentInfo.getBarcodeList()));

                // Check product cost price
                assertCustomize.assertEquals(productInfo.getProductCostPrice(), currentInfo.getProductCostPrice(),
                        "Product cost price must be %s, but found %s".formatted(productInfo.getProductCostPrice(), currentInfo.getProductCostPrice()));

                // Check product cost price
                assertCustomize.assertEquals(productInfo.getProductCostPrice(), currentInfo.getProductCostPrice(),
                        "Product cost price must be %s, but found %s".formatted(productInfo.getProductCostPrice(), currentInfo.getProductCostPrice()));

                // Check product version name
                List<String> actualVersionNames = productInfo.getVersionNameMap().values().stream().map(map -> map.get(defaultLanguage)).toList();
                List<String> expectedVersionNames = currentInfo.getVersionNameMap().values().stream().map(map -> map.get(defaultLanguage)).toList();
                assertCustomize.assertTrue(CollectionUtils.isEqualCollection(actualVersionNames, expectedVersionNames),
                        "Variation version name must be %s, but found %s".formatted(expectedVersionNames, actualVersionNames));

                // Check product version description
                List<String> actualVersionDescriptions = productInfo.getVersionNameMap().values().stream().map(map -> map.get(defaultLanguage)).toList();
                List<String> expectedVersionDescriptions = currentInfo.getVersionNameMap().values().stream().map(map -> map.get(defaultLanguage)).toList();
                assertCustomize.assertTrue(CollectionUtils.isEqualCollection(actualVersionDescriptions, expectedVersionDescriptions),
                        "Variation version description must be %s, but found %s".formatted(expectedVersionDescriptions, actualVersionDescriptions));

                // Check product version status
                assertCustomize.assertEquals(productInfo.getVariationStatus(), currentInfo.getVariationStatus(),
                        "Variation status must be %s, but found %s".formatted(productInfo.getVariationStatus(), currentInfo.getVariationStatus()));
            }
        }

        // Assert
        AssertCustomize.verifyTest();
    }

    public void updateProductWithoutVariation(int... branchStock) {
        removeOldVariations();
        selectProductImages();
        inputProductName();
        inputProductDescription();
        inputWithoutVariationPrice();
//        inputWithoutVariationSKU();
        inputWithoutVariationBarcode();
        displayIfOutOfStock();
        hideRemainingStockOnOnlineStore();
        manageProductByLot();
        addWithoutVariationStock(branchStock);
        modifyShippingInformation();
        modifyProductSellingPlatform();
        modifyPriority();
        completeUpdateProduct();
    }

    public void updateProductWithVariation(int increaseNum, int... branchStock) {
        removeOldVariations();
        selectProductImages();
        inputProductName();
        inputProductDescription();
        displayIfOutOfStock();
        hideRemainingStockOnOnlineStore();
        modifyShippingInformation();
        modifyProductSellingPlatform();
        modifyPriority();
        addVariations();
        manageProductByLot();
        bulkUpdateVariations(increaseNum, branchStock);
        completeUpdateProduct();
    }

    public void updateEachVariationInformation(int... branchStock) {
        // Update variation information
        updateVariationInformation(branchStock);

        // Save changes
        completeUpdateProduct();
    }

    boolean updateVariationInformation = false;

    private boolean getUpdateVariationInformation() {
        boolean temp = updateVariationInformation;
        updateVariationInformation = false;
        return temp;
    }

    void updateVariationInformation(int... branchStock) {
        // Set update variation information flag
        updateVariationInformation = true;

        // Init variation POM
        ProductVariationScreen productVariationScreen = new ProductVariationScreen(driver);

        // Init variation information model
        List<ProductVariationScreen.VariationInfo> variationInfo = new ArrayList<>();

        // Update variation information
        IntStream.range(0, productInfo.getVariationValuesMap().get(defaultLanguage).size()).forEach(variationIndex -> {
            // Navigate to variation detail screen
            commonIOS.click(loc_lstVariations, variationIndex);

            // Update variation information
            productVariationScreen.getVariationInformation(defaultLanguage, branchInfo, hasDiscount, hasCostPrice, variationIndex, productInfo)
                    .updateVariationInformation(branchStock);

            variationInfo.add(ProductVariationScreen.getVariationInfo());
        });

        // Init variation information
        Map<String, Map<String, String>> versionNameMap = new HashMap<>();
        Map<String, Map<String, String>> versionDescriptionMap = new HashMap<>();
        Map<String, List<Integer>> stockQuantityMap = new HashMap<>();
        List<Long> listingPrices = new ArrayList<>();
        List<Long> sellingPrices = new ArrayList<>();
        List<Long> costPrices = new ArrayList<>();
        List<String> barcodes = new ArrayList<>();
        List<String> status = new ArrayList<>();

        // Get new variation information
        for (ProductVariationScreen.VariationInfo info : variationInfo) {
            versionNameMap.put(info.getVariation(), Map.of(defaultLanguage, info.getName()));
            versionDescriptionMap.put(info.getVariation(), Map.of(defaultLanguage, info.getDescription()));
            stockQuantityMap.put(info.getVariation(), info.getStockQuantity());
            listingPrices.add(info.getListingPrice());
            sellingPrices.add(info.getSellingPrice());
            costPrices.add(info.getCostPrice());
            barcodes.add(info.getBarcode());
            status.add(info.getStatus());
        }
        productInfo.setVersionNameMap(versionNameMap);
        productInfo.setVersionDescriptionMap(versionDescriptionMap);
        if (!hasLot) productInfo.setProductStockQuantityMap(stockQuantityMap);
        productInfo.setProductListingPrice(listingPrices);
        productInfo.setProductSellingPrice(sellingPrices);
        productInfo.setProductCostPrice(costPrices);
        productInfo.setBarcodeList(barcodes);
        productInfo.setVariationStatus(status);
    }
}
