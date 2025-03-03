package web.Dashboard.products.all_products.management;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.inventory.APIInventoryHistory;
import api.Seller.sale_channel.tiktok.APIGetTikTokProducts;
import api.Seller.setting.BranchManagement;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import sql.SQLGetInventoryMapping;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.commons.WebUtils;
import utilities.data.DataGenerator;
import utilities.excel.ExcelUtils;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.Dashboard.sales_channels.tiktok.VerifyAutoSyncHelper;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.character_limit.CharacterLimit.MAX_STOCK_QUANTITY;
import static utilities.file.FileNameAndPath.downloadFolder;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.products.all_products.crud.ProductPageElement.*;
import static web.Dashboard.products.all_products.management.ProductManagementPage.BulkActions.*;
import static web.Dashboard.products.all_products.management.ProductManagementPage.DisplayOutOfStockActions.*;
import static web.Dashboard.products.all_products.management.ProductManagementPage.PriceType.*;

public class ProductManagementPage extends ProductManagementElement {
    Logger logger = LogManager.getLogger(ProductManagementPage.class);
    WebDriver driver;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    WebUtils webUtils;
    ProductPage productPage;

    public ProductManagementPage(WebDriver driver) {
        this.driver = driver;
        checkPermission = new CheckPermission(driver);
        webUtils = new WebUtils(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public ProductManagementPage getLoginInformation(LoginInformation sellerLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        return this;
    }

    public ProductManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        productPage = new ProductPage(driver);
        return this;
    }

    public ProductManagementPage navigateToProductManagementPage() {
        logger.info("Navigate to product management page.");
        if (driver.getCurrentUrl().contains("%s/product/list".formatted(DOMAIN))) {
            return this;
        }

        driver.get("%s/product/list".formatted(DOMAIN));
        driver.navigate().refresh();

        return this;
    }

    enum BulkActions {
        clearStock, delete, deactivate, active, updateStock, updateTax, displayOutOfStock, updateSellingPlatform, updatePrice, setStockAlert, manageStockByLotDate;

        static List<BulkActions> bulkActionsValues() {
            return new ArrayList<>(Arrays.asList(values()));
        }
    }

    void openBulkActionsDropdown() {
        if (webUtils.getListElement(loc_lblProductId).isEmpty())
            driver.navigate().refresh();
        if (!webUtils.isCheckedJS(loc_chkSelectAll)) {
            webUtils.clickJS(loc_chkSelectAll);
        }
        webUtils.clickJS(loc_lnkSelectAction);
    }

    private String exportAllProductsThenGetExportTime() {
        navigateToProductManagementPage();
        webUtils.clickJS(loc_btnExport);
        webUtils.clickJS(loc_ddlExportActions, 0);
        webUtils.clickJS(loc_dlgExportProductListingFile_btnExport);
        logger.info("Export all products.");
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-HH-mm"));
    }

    void exportWholesaleProducts() {
        navigateToProductManagementPage();
        webUtils.clickJS(loc_btnExport);
        if (!webUtils.getListElement(loc_ddlExportActions).isEmpty()) {
            webUtils.clickJS(loc_ddlExportActions, 1);
            logger.info("Export wholesale products.");
        }
    }

    void navigateToDownloadHistoryPage() {
        driver.get(DOMAIN + "/product/export-history");
        driver.navigate().refresh();
        logger.info("Navigate to download export product history page.");

    }

    void importProduct(String branchName, String importFilePath) {
        // open list import actions
        webUtils.clickJS(loc_btnImport);

        // open import product popup
        webUtils.clickJS(loc_ddlImportActions, 0);

        // select branch
        if (!webUtils.isCheckedJS(By.xpath(str_dlgImport_chkBranch.formatted(branchName)))) {
            webUtils.clickJS(By.xpath(str_dlgImport_chkBranch.formatted(branchName)));
        }

        // check import product is opened or not
        assertCustomize.assertFalse(webUtils.getListElement(loc_dlgImport).isEmpty(), "Can not open import popup");

        // upload file
        if (!webUtils.getListElement(loc_dlgImport).isEmpty()) {
            webUtils.uploads(loc_dlgImport_btnDragAndDrop, importFilePath);

            // complete import product
            Assert.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgImport_btnImport, loc_prgStatus),
                    "Can not import product.");
        }
    }

    void applyAll(long price, int typeIndex) {
        webUtils.sendKeys(loc_dlgUpdatePrice_txtApplyAll, String.valueOf(price));
        webUtils.click(loc_dlgUpdatePrice_ddvSelectedPriceType);
        webUtils.clickJS(loc_dlgUpdatePrice_ddlPriceType, typeIndex);
        webUtils.click(loc_dlgUpdatePrice_btnApplyAll);
    }

    enum PriceType {
        listing, selling, cost;

        static List<PriceType> getAllPriceTypes() {
            return Arrays.stream(values()).toList();
        }
    }

    void bulkActionsUpdatePrice(long listingPrice, long sellingPrice, long costPrice) {
        // bulk actions
        openBulkActionsDropdown();

        // open update price popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(updatePrice));

        // input listing price

        applyAll(listingPrice, getAllPriceTypes().indexOf(listing));

        // input selling price
        applyAll(sellingPrice, getAllPriceTypes().indexOf(selling));

        // input cost price
        if (permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
            applyAll(costPrice, getAllPriceTypes().indexOf(cost));
        } else {
            // view cost price
            assertCustomize.assertTrue(webUtils.getValue(loc_dlgUpdatePrice_txtCostPrice, 0).equals("0"), "Product cost price still shows when staff does not have 'View product cost price' permission.");
        }

        // complete update price
        webUtils.click(loc_dlgUpdatePrice_btnUpdate);
    }

    /* Check bulk actions */
    List<String> getAllProductIdIn1stPage() {
        // navigate to product management page
        navigateToProductManagementPage();

        // if page is not loaded, refresh page
        if (webUtils.getListElement(loc_lblProductId).isEmpty()) {
            driver.navigate().refresh();
        }

        // get number of products in 1st page
        int bound = webUtils.getListElement(loc_lblProductId).size();

        // return list productId
        return IntStream.range(0, bound).mapToObj(index -> webUtils.getText(loc_lblProductId, index)).toList();
    }

    void waitUpdated() {
        if (!webUtils.getListElement(loc_prgStatus).isEmpty()) {
            driver.navigate().refresh();
            logger.info("Wait bulk update.");
            waitUpdated();
        }
    }

    // bulk clear stock
    public void bulkClearStock() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // get before update stock in item-service
        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
        Map<String, List<Integer>> beforeUpdateStocksInItemService = productInformation.getCurrentProductStocksMap(productIds);

        // get before update stock in ES
        APIAllProducts allProducts = new APIAllProducts(sellerLoginInformation);
        Map<String, Integer> beforeUpdateStocksInES = allProducts.getCurrentStocks(productIds);

        // log
        logger.info("Wait get product stock before clear stock.");

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(clearStock));

        // confirm clear stock
        webUtils.click(loc_dlgClearStock_btnOK);

        /* Do not need to wait product updated because calculate function needs ~ 2 minutes, that time is enough for product to be updated.*/
        // Check product stock are updated on item-service
        List<Integer> expectedStockOnItemService = new ArrayList<>(productInformation.getExpectedListProductStockQuantityAfterClearStock(productIds, beforeUpdateStocksInItemService));
        Collections.sort(expectedStockOnItemService);
        List<Integer> actualStockOnItemService = new ArrayList<>(productInformation.getCurrentStockOfProducts(productIds));
        Collections.sort(actualStockOnItemService);
        assertCustomize.assertEquals(expectedStockOnItemService, actualStockOnItemService,
                "Product stock are not updated on item-service, , stock must be %s, but found %s.".formatted(actualStockOnItemService.toString(), expectedStockOnItemService.toString()));
        logger.info("Check product stock on item-service after clearing stock.");

        // check product stock are updated on ES
        List<Integer> expectedStockOnES = new ArrayList<>(allProducts.getExpectedListProductStockQuantityAfterClearStock(productIds, beforeUpdateStocksInES));
        Collections.sort(expectedStockOnES);
        List<Integer> actualStockOnES = new ArrayList<>(allProducts.getListProductStockQuantityAfterClearStock(productIds));
        Collections.sort(actualStockOnES);
        assertCustomize.assertEquals(expectedStockOnES, actualStockOnES,
                "Product stock are not updated on ES, stock must be %s, but found %s.".formatted(actualStockOnES.toString(), expectedStockOnES.toString()));
        logger.info("Check product stock on ES after clearing stock.");

        // log
        logger.info("Check product status after bulk actions: CLEAR STOCK.");

        // verify test
        AssertCustomize.verifyTest();
    }


    // bulk delete
    public List<Boolean> checkProductCanBeDeleted(List<String> productIds) {
        List<Integer> listProductIdThatInInCompleteTransfer = new APIInventoryHistory(sellerLoginInformation).listOfCanNotBeDeletedProductIds(productIds);
        return productIds.stream().map(productId -> !listProductIdThatInInCompleteTransfer.contains(Integer.parseInt(productId))).toList();
    }

    public void bulkDeleteProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(delete));

        // confirm active product
        webUtils.click(loc_dlgDeleteProduct_btnDelete);

        // check actions are completed or not
        if (!webUtils.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product must be deleted in ES
            List<Integer> currentProductIds = new APIAllProducts(sellerLoginInformation).getListProductId();
            List<Boolean> checkProductCanBeDeleted = checkProductCanBeDeleted(productIds);
            assertCustomize.assertTrue(IntStream.range(0, productIds.size())
                            .noneMatch(index -> (currentProductIds.contains(Integer.parseInt(productIds.get(index))) && checkProductCanBeDeleted.get(index))
                                                || (!currentProductIds.contains(Integer.parseInt(productIds.get(index))) && !checkProductCanBeDeleted.get(index))),
                    "Product is not deleted in ES.");

            // check product must be deleted in item-service
            List<Boolean> isDeleted = new APIProductDetail(sellerLoginInformation).isDeleted(productIds);
            assertCustomize.assertEquals(checkProductCanBeDeleted, isDeleted, "Product is not deleted in item-service.");

            // log
            logger.info("Check product list after bulk actions: DELETE.");
        } else logger.error("Can not bulk actions DELETE product.");

        // verify test
        AssertCustomize.verifyTest();
    }

    // bulk deactivate
    public void bulkDeactivateProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm deactivate popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(deactivate));

        // confirm active product
        webUtils.click(loc_dlgDeactivateProduct_btnYes);

        // check actions are completed or not
        if (!webUtils.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product status after updating
            assertCustomize.assertTrue(new APIProductDetail(sellerLoginInformation)
                            .getListProductStatus(productIds)
                            .stream()
                            .allMatch(status -> status.equals("INACTIVE")),
                    "All selected products must be DEACTIVATE, but some product is not updated.");

            // log
            logger.info("Check product status after bulk actions: DEACTIVATE.");
        } else logger.error("Can not bulk actions ACTIVE product.");

        // verify test
        AssertCustomize.verifyTest();
    }

    // bulk active
    public void bulkActivateProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(active));

        // confirm active product
        webUtils.click(loc_dlgActiveProduct_btnYes);

        // check actions are completed or not
        if (!webUtils.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product in product management
            assertCustomize.assertTrue(new APIProductDetail(sellerLoginInformation)
                            .getListProductStatus(productIds)
                            .stream()
                            .allMatch(status -> status.equals("ACTIVE")),
                    "All selected products must be ACTIVE, but some product is not updated.");

            // log
            logger.info("Check product status after bulk actions: ACTIVATE.");
        } else logger.error("Can not bulk actions ACTIVATE product.");

        // verify test
        AssertCustomize.verifyTest();
    }

    // bulk update stock
    public void bulkUpdateStock() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // get before update stock in item-service
        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
        Map<String, List<Integer>> beforeUpdateStocksInItemService = productInformation.getCurrentProductStocksMap(productIds);

        // get before update stock in ES
        APIAllProducts allProducts = new APIAllProducts(sellerLoginInformation);
        int branchId = new BranchManagement(sellerLoginInformation).getInfo().getBranchID().get(0);
        Map<String, Integer> beforeUpdateStocksInES = allProducts.getCurrentStocks(productIds, branchId);

        // log
        logger.info("Wait get product stock before update stock.");

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(updateStock));

        // select change actions
        webUtils.click(loc_dlgUpdateStock_actionsChange);

        // input stock value
        int stock = nextInt(MAX_STOCK_QUANTITY);
        webUtils.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));
        logger.info("Input stock value: %,d.".formatted(stock));

        // confirm update stock
        webUtils.click(loc_dlgUpdateStock_btnUpdate);

        /* Do not need to wait product updated because calculate function needs ~ 2 minutes, that time is enough for product to be updated.*/
        // check product stock are updated on item-service
        List<Integer> expectedStockOnItemService = new ArrayList<>(productInformation.getExpectedListProductStockQuantityAfterUpdateStock(productIds, branchId, beforeUpdateStocksInItemService, stock));
        Collections.sort(expectedStockOnItemService);
        List<Integer> actualStockOnItemService = new ArrayList<>(productInformation.getCurrentStockOfProducts(productIds));
        Collections.sort(actualStockOnItemService);
        assertCustomize.assertEquals(expectedStockOnItemService, actualStockOnItemService,
                "Product stock are not updated on item-service, , stock must be %s, but found %s.".formatted(actualStockOnItemService.toString(), expectedStockOnItemService.toString()));
        logger.info("Check product stock on item-service after updating stock.");

        // check product stock are updated on ES
        List<Integer> expectedStockOnES = new ArrayList<>(allProducts.getExpectedListProductStockQuantityAfterUpdateStock(productIds, beforeUpdateStocksInES, stock));
        Collections.sort(expectedStockOnES);
        List<Integer> actualStockOnES = new ArrayList<>(allProducts.getListProductStockQuantityAfterUpdateStock(productIds, branchId));
        Collections.sort(actualStockOnES);
        assertCustomize.assertEquals(expectedStockOnES, actualStockOnES,
                "Product stock are not updated on ES, stock must be %s, but found %s.".formatted(actualStockOnES.toString(), expectedStockOnES.toString()));
        logger.info("Check product stock on ES after updating stock.");

        // log
        logger.info("Check product status after bulk actions: UPDATE STOCK.");

        // verify test
        AssertCustomize.verifyTest();
    }

    // bulk update tax
    public void bulkUpdateTax() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm deactivate popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(updateTax));

        // get taxId
        int bound = webUtils.getListElement(loc_dlgUpdateTax_ddlTaxOptions).size();
        int taxIndex = nextInt(bound);
        int newTaxId = Integer.parseInt(webUtils.getValue(loc_dlgUpdateTax_ddlTaxOptions, taxIndex));
        webUtils.clickJS(loc_dlgUpdateTax_ddlTaxOptions, taxIndex);
        logger.info("Bulk actions update tax: %d.".formatted(newTaxId));

        // confirm active product
        webUtils.click(loc_dlgUpdateTax_btnOK);

        // check actions are completed or not
        if (!webUtils.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product status after updating
            APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
            List<Integer> taxList = productInformation.getListProductTaxId(productIds);
            assertCustomize.assertTrue(IntStream.range(0, taxList.size())
                            .allMatch(index -> taxList.get(index) == newTaxId),
                    "Tax of selected products must be %s.".formatted(taxList.toString()));

            // log
            logger.info("Check product taxId after bulk actions: UPDATE TAX.");
        } else logger.error("Can not bulk actions Update Tax product.");

        // verify test
        AssertCustomize.verifyTest();
    }

    enum DisplayOutOfStockActions {
        displayWhenOutOfStock, doNotDisplayWhenOutOfStock;

        static List<DisplayOutOfStockActions> displayOutOfStockActions() {
            return new ArrayList<>(Arrays.asList(values()));
        }
    }

    void bulkDisplayOutOfStock(DisplayOutOfStockActions displayOutOfStockActions) {
        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open display out of stock popup
        webUtils.click(loc_ddlListActions,
                bulkActionsValues().indexOf(displayOutOfStock) );

        // select option
        webUtils.clickJS(loc_dlgDisplayOutOfStockProduct_listOptions,
                displayOutOfStockActions().indexOf(displayOutOfStockActions));

        // confirm bulk display when out of stock product
        webUtils.click(loc_dlgDisplayOutOfStockProduct_btnYes);
    }

    public void bulkDisplayOutOfStockProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // bulk do not display when out of stock
        bulkDisplayOutOfStock(doNotDisplayWhenOutOfStock);

        // wait updated
        waitUpdated();

        // check product display after updating
        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
        List<Boolean> showOutOfStock = productInformation.getDisplayWhenOutOfStock(productIds);
        assertCustomize.assertTrue(IntStream.range(0, showOutOfStock.size())
                        .noneMatch(showOutOfStock::get),
                "Display when out of selected products must be %s.".formatted(showOutOfStock.toString()));

        // log
        logger.info("Check product taxId after bulk actions: DO NOT DISPLAY OUT OF STOCK PRODUCT.");

        // bulk display when out of stock
        bulkDisplayOutOfStock(displayWhenOutOfStock);

        // wait updated
        waitUpdated();

        // check product display after updating
        showOutOfStock = productInformation.getDisplayWhenOutOfStock(productIds);
        assertCustomize.assertTrue(IntStream.range(0, showOutOfStock.size())
                        .allMatch(showOutOfStock::get),
                "Display when out of selected products must be %s.".formatted(showOutOfStock.toString()));
        // log
        logger.info("Check product taxId after bulk actions: DISPLAY OUT OF STOCK PRODUCT.");

        // verify test
        AssertCustomize.verifyTest();
    }

    public void bulkUpdateSellingPlatform() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open display out of stock popup
        webUtils.click(loc_ddlListActions,
                bulkActionsValues().indexOf(updateSellingPlatform));

        // update selling platform
        boolean onApp = !webUtils.isDisabledJS(loc_dlgUpdateSellingPlatform_chkApp) && nextBoolean();
        if (webUtils.isCheckedJS(loc_dlgUpdateSellingPlatform_chkApp) != onApp)
            webUtils.clickJS(loc_dlgUpdateSellingPlatform_chkApp);
        logger.info("onApp: %s.".formatted(onApp));

        boolean onWeb = !webUtils.isDisabledJS(loc_dlgUpdateSellingPlatform_chkWeb) && nextBoolean();
        if (webUtils.isCheckedJS(loc_dlgUpdateSellingPlatform_chkWeb) != onWeb)
            webUtils.clickJS(loc_dlgUpdateSellingPlatform_chkWeb);
        logger.info("onWeb: %s.".formatted(onWeb));

        boolean inStore = !webUtils.isDisabledJS(loc_dlgUpdateSellingPlatform_chkInStore) && nextBoolean();
        if (webUtils.isCheckedJS(loc_dlgUpdateSellingPlatform_chkInStore) != inStore)
            webUtils.clickJS(loc_dlgUpdateSellingPlatform_chkInStore);
        logger.info("inStore: %s.".formatted(inStore));

        boolean inGoSocial = !webUtils.isDisabledJS(loc_dlgUpdateSellingPlatform_chkGoSocial) && nextBoolean();
        if (webUtils.isCheckedJS(loc_dlgUpdateSellingPlatform_chkGoSocial) != inGoSocial)
            webUtils.clickJS(loc_dlgUpdateSellingPlatform_chkGoSocial);
        logger.info("inGoSocial: %s.".formatted(inGoSocial));

        // confirm bulk update selling platforms
        webUtils.click(loc_dlgUpdateSellingPlatform_btnConfirm);

        // wait updated
        waitUpdated();

        // check product display after updating
        Map<String, List<Boolean>> sellingPlatforms = new APIProductDetail(sellerLoginInformation).getMapOfListSellingPlatform(productIds);
        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("onWeb")).stream()
                        .allMatch(webPlatform -> webPlatform == onWeb),
                "Web platform of selected products must be %s, but found %s.".formatted(onWeb, sellingPlatforms.get("onWeb")));

        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("onApp")).stream()
                        .allMatch(appPlatform -> appPlatform == onApp),
                "App platform of selected products must be %s, but found %s.".formatted(onApp, sellingPlatforms.get("onApp")));

        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("inStore")).stream()
                        .allMatch(inStorePlatform -> inStorePlatform == inStore),
                "In store platform of selected products must be %s, but found %s.".formatted(inStore, sellingPlatforms.get("inStore")));

        assertCustomize.assertTrue(new ArrayList<>(sellingPlatforms.get("inGoSocial")).stream()
                        .allMatch(inGoSocialPlatform -> inGoSocialPlatform == inGoSocial),
                "GoSocial platform of selected products must be %s, but found %s.".formatted(inGoSocial, sellingPlatforms.get("inGoSocial")));

        // log
        logger.info("Check product selling platform after bulk actions: UPDATE SELLING PLATFORM.");

        // verify test
        AssertCustomize.verifyTest();
    }

    public void bulkUpdatePrice() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // bulk actions
        openBulkActionsDropdown();

        // open update price popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(updatePrice));

        // get map of products price
        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
        Map<String, List<Long>> mapOfProductsPrice = productInformation.getMapOfCurrentProductsPrice(productIds);

        // input listing price
        long maxListingPrice = Collections.max(new ArrayList<>(mapOfProductsPrice.get("listingPrice")));
        long listingPrice = maxListingPrice + nextLong(Math.max(MAX_PRICE - maxListingPrice, 1));
        applyAll(listingPrice, getAllPriceTypes().indexOf(listing));
        logger.info("Input listing price: %,d.".formatted(listingPrice));

        // input selling price
        long maxSellingPrice = Collections.max(new ArrayList<>(mapOfProductsPrice.get("listingPrice")));
        long sellingPrice = maxSellingPrice + nextLong(Math.max(listingPrice - maxSellingPrice, 1));
        applyAll(sellingPrice, getAllPriceTypes().indexOf(selling));
        logger.info("Input selling price: %,d.".formatted(sellingPrice));

        // input cost price
        long minCostPrice = Collections.min(new ArrayList<>(mapOfProductsPrice.get("costPrice")));
        long costPrice = nextLong(Math.max(minCostPrice, 1));
        applyAll(costPrice, getAllPriceTypes().indexOf(cost));
        logger.info("Input cost price: %,d.".formatted(costPrice));

        // complete update price
        webUtils.click(loc_dlgUpdatePrice_btnUpdate);

        // wait updated
        waitUpdated();

        // check product display after updating
        Map<String, List<Long>> mapOfActualPrice = productInformation.getMapOfCurrentProductsPrice(productIds);
        Map<String, List<Long>> mapOfExpectedPrice = productInformation.getMapOfExpectedProductsPrice(productIds, listingPrice, sellingPrice, costPrice);

        // check product listing price
        List<Long> actualListingPrice = new ArrayList<>(mapOfActualPrice.get("listingPrice"));
        Collections.sort(actualListingPrice);
        List<Long> expectedListingPrice = new ArrayList<>(mapOfExpectedPrice.get("listingPrice"));
        Collections.sort(expectedListingPrice);
        assertCustomize.assertEquals(actualListingPrice, expectedListingPrice,
                "Product listing price after updating must be %s, but found %s.".formatted(expectedListingPrice, actualListingPrice));

        // check product selling price
        List<Long> actualSellingPrice = new ArrayList<>(mapOfActualPrice.get("sellingPrice"));
        Collections.sort(actualSellingPrice);
        List<Long> expectedSellingPrice = new ArrayList<>(mapOfExpectedPrice.get("sellingPrice"));
        Collections.sort(expectedSellingPrice);
        assertCustomize.assertEquals(actualSellingPrice, expectedSellingPrice,
                "Product selling price after updating must be %s, but found %s.".formatted(expectedSellingPrice, actualSellingPrice));

        // check product cost price
        List<Long> actualCostPrice = new ArrayList<>(mapOfActualPrice.get("costPrice"));
        Collections.sort(actualCostPrice);
        List<Long> expectedCostPrice = new ArrayList<>(mapOfExpectedPrice.get("costPrice"));
        Collections.sort(expectedCostPrice);
        assertCustomize.assertEquals(actualCostPrice, expectedCostPrice,
                "Product cost price after updating must be %s, but found %s.".formatted(expectedCostPrice, actualCostPrice));

        // log
        logger.info("Check product price after bulk actions: UPDATE PRICE.");

        // verify test
        AssertCustomize.verifyTest();
    }


    public void bulkSetStockAlert() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open set stock alert popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(setStockAlert));

        // set new stock alert number
        int stockAlertValue = nextInt(MAX_STOCK_QUANTITY);
        webUtils.sendKeys(loc_dlgSetStockAlert_txtStockAlertValueForAllProducts, String.valueOf(stockAlertValue));
        webUtils.click(loc_dlgSetStockAlert_btnApply);
        logger.info("Bulk actions set stock alert: %d.".formatted(stockAlertValue));

        // confirm update new stock alert value
        webUtils.click(loc_dlgSetStockAlert_btnUpdate);

        // check actions are completed or not
        if (!webUtils.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product stock alert value after updating
            APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
            List<Integer> stockAlert = productInformation.getListOfProductStockAlert(productIds);
            stockAlert.forEach(alert -> assertCustomize.assertEquals(alert, stockAlertValue, "Stock alert must be '%,d' but found '%,d', index: %d".formatted(stockAlertValue, alert, stockAlert.indexOf(alert))));

            // log
            logger.info("Check product stock alert value after bulk actions: SET STOCK ALERT.");
        } else logger.error("Can not bulk actions set stock alert.");

        // verify test
        AssertCustomize.verifyTest();
    }

    public void bulkManageStockByLotDate() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // get current product lot date
        APIProductDetail productInformation = new APIProductDetail(sellerLoginInformation);
        Map<String, List<Boolean>> beforeUpdateLotDate = productInformation.getMapOfCurrentManageByLotDate(productIds);


        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open display out of stock popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(manageStockByLotDate));

        // set expire
        boolean expiredQuality = nextBoolean();
        if (webUtils.isCheckedJS(loc_dlgManageProductByLotDate_chkExcludeExpireQuantity) != expiredQuality)
            webUtils.clickJS(loc_dlgManageProductByLotDate_chkExcludeExpireQuantity);
        logger.info("Exclude expired quantity from remaining stock: {}.", expiredQuality);

        // confirm bulk update selling platforms
        webUtils.click(loc_dlgManageProductByLotDate_btnYes);

        // wait updated
        waitUpdated();

        // check product display after updating
        Map<String, List<Boolean>> expectedMap = productInformation.getMapOfExpectedManageByLotDate(productIds, beforeUpdateLotDate, expiredQuality);
        Map<String, List<Boolean>> actualMap = productInformation.getMapOfCurrentManageByLotDate(productIds);
        assertCustomize.assertEquals(expectedMap, actualMap,
                "Map of managed by lot date of selected products must be %s, but found %s.".formatted(expectedMap, actualMap));

        // verify test
        AssertCustomize.verifyTest();
    }

    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-13814
    public void checkProductManagementPermission(AllPermissions permissions) throws Exception {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // get productId is created by staff
        int createdProductId = permissions.getProduct().getProductManagement().isCreateProduct()
                ? new APICreateProduct(staffLoginInformation).createWithoutVariationProduct(false, 1000).getProductID()
                : 0;

        // get productId is created by seller
        int notCreatedProductId = new APICreateProduct(sellerLoginInformation).createWithoutVariationProduct(false, 1000).getProductID();

        // check view product list
        checkViewProductList(createdProductId, notCreatedProductId);

        // check create product
        checkCreateProduct();

        // get list product ids
        List<Integer> productIds = new APIAllProducts(staffLoginInformation).getAllProductInformation().getProductIds();
        if (!productIds.isEmpty()) {
            // check clear stock
            checkClearStock();

            // check delete product
            checkDeleteProduct();

            // check activate product
            checkActivateProduct();

            // check deactivate product
            checkDeactivateProduct();

            // check export product
            checkExportProduct();

            // check download export product
            checkDownloadExportedProducts();

            // check import product
            checkImportProduct();

            // check print barcode
            checkPrintBarcode();

            // check edit price
            checkEditPrice();

            // check enable product lot
            checkEnableProductLot();

            // check view product detail
            productPage.getLoginInformation(staffLoginInformation)
                    .checkProductManagementPermission(permissions,
                            productIds.get(0));
        }
    }

    void checkViewProductList(int createdProductId, int notCreatedProductId) {
        // navigate to product list
        navigateToProductManagementPage();

        // get list product ids
        List<Integer> dbProductList = new APIAllProducts(staffLoginInformation).getAllProductInformation().getProductIds();

        // GET the product list from API.
        if (permissions.getProduct().getProductManagement().isViewProductList()) {
            List<Integer> checkData = (createdProductId != 0)
                    ? List.of(createdProductId, notCreatedProductId)
                    : List.of(notCreatedProductId);
            assertCustomize.assertTrue(new HashSet<>(dbProductList).containsAll(checkData),
                    "List product must be contains: %s, but found list product: %s.".formatted(checkData.toString(), dbProductList.toString()));
        } else if (permissions.getProduct().getProductManagement().isViewCreatedProductList()) {
            if (createdProductId != 0)
                assertCustomize.assertTrue(new HashSet<>(dbProductList).contains(createdProductId),
                        "List product must be contains: %s, but found list product: %s.".formatted(createdProductId, dbProductList.toString()));
            assertCustomize.assertFalse(new HashSet<>(dbProductList).contains(notCreatedProductId),
                    "List product must not contains: %s, but found list product: %s.".formatted(notCreatedProductId, dbProductList.toString()));
        } else {
            assertCustomize.assertTrue(dbProductList.isEmpty(),
                    "All products must be hidden, but found: %s.".formatted(dbProductList.toString()));
        }
        logger.info("Check permission: Product >> Product management >> View product list.");
        logger.info("Check permission: Product >> Product management >> View created product list.");
    }

    void checkActivateProduct() {
        // navigate to product list
        navigateToProductManagementPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isActivateProduct()) {
            webUtils.clickJS(loc_ddlListActions, 3);
            webUtils.click(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 3), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Activate product.");
    }

    void checkDeactivateProduct() {
        // navigate to product list
        navigateToProductManagementPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isDeactivateProduct()) {
            webUtils.clickJS(loc_ddlListActions, 2);
            webUtils.click(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 2), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Deactivate product.");
    }

    void checkCreateProduct() throws Exception {
        // navigate to product list
        navigateToProductManagementPage();

        // check create product permission
        if (permissions.getProduct().getProductManagement().isCreateProduct()) {
            // check create product page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnCreateProduct, "/create"), "Create product page must be shown instead of %s.".formatted(driver.getCurrentUrl()));

            // check view list collection
            checkViewCollectionList();

            // check create list collection
            checkCreateCollection();

            // check add/delete variation
            checkAddVariation();

            // create product
            driver.navigate().refresh();
            productPage.getLoginInformation(staffLoginInformation).createWithoutVariationProduct(false, 1);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateProduct),
                    "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        }
        logger.info("Check permission: Product >> Product management >> Create product.");
    }


    void checkEditPrice() {
        // navigate to product list
        navigateToProductManagementPage();

        // bulk action
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isEditPrice()) {
            // update price
            bulkActionsUpdatePrice(MAX_PRICE, MAX_PRICE, nextLong(1000));
            logger.info("Check permission: Product >> Product management >> View cost price.");
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 8), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Edit price.");
    }

    void checkExportProduct() {
        // navigate to product list
        navigateToProductManagementPage();

        if (!permissions.getProduct().getProductManagement().isExportProducts()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnExport), "Restricted popup is not shown.");
        } else {
            // export product list
            exportAllProductsThenGetExportTime();

            // export wholesale product
            exportWholesaleProducts();
            UICommonAction.sleepInMiliSecond(3000, "Waiting for download.");
            assertCustomize.assertTrue(new FileUtils().isDownloadSuccessful("wholesale-price-export"),
                    "No exported wholesale product file is downloaded.");
        }
        logger.info("Check permission: Product >> Product management >> Export product.");
    }

    void checkImportProduct() {
        // navigate to product list
        navigateToProductManagementPage();

        if (permissions.getProduct().getProductManagement().isImportProducts()) {
            // import product
            String branchName = new Login().getInfo(staffLoginInformation).getAssignedBranchesNames().get(0);
            importProduct(branchName, new DataGenerator().getPathOfFileInResourcesRoot("import_product.xlsx"));

            // check update wholesale price
            checkUpdateWholesalePrice();
        } else {
            webUtils.clickJS(loc_btnImport);
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlImportActions, 0), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Import product.");
    }

    void checkPrintBarcode() {
        // navigate to product list
        navigateToProductManagementPage();

        if (permissions.getProduct().getProductManagement().isPrintBarcode()) {
            webUtils.click(loc_btnPrintBarcode);
            webUtils.click(loc_dlgPrintBarcode_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPrintBarcode), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Print barcode.");
    }

    private String downloadExportedProducts(FileUtils fileUtils, String startExportTime) {
        // Return early if startExportTime is null or empty
        if (startExportTime == null || startExportTime.isEmpty()) {
            return null;
        }

        // Delete the old exported product file
        fileUtils.deleteFileInDownloadFolder("EXPORT_PRODUCT");

        // Download the new exported product
        return WebUtils.retryUntil(5, 30_000, "Cannot find exported file.", () -> {
            // Navigate to the history export page
            navigateToDownloadHistoryPage();

            // Get the latest export file name and extract export time
            String latestExportFile = webUtils.getText(loc_lstExportedFileName);
            String exportTimeInFileName = DataGenerator.getStringByRegex(latestExportFile, "20\\d{2}-(0[0-9]|1[0-9]|2[0-3])-(0[0-9]|[1-5][0-9])");

            // Check if the export time in the file is greater than the provided time
            return StringUtils.compare(startExportTime, exportTimeInFileName) > 0;
        }, () -> {
            // Download the export file
            webUtils.clickJS(loc_icnDownloadExportFile);
            WebUtils.sleep(1000);

            // Verify the download was successful
            assertCustomize.assertTrue(fileUtils.isDownloadSuccessful("EXPORT_PRODUCT"), "No exported product file is downloaded.");

            // Return the latest export file name
            return webUtils.getText(loc_lstExportedFileName);
        });
    }

    void checkDownloadExportedProducts() {
        // navigate to history export page
        navigateToDownloadHistoryPage();

        // check permission
        if (!webUtils.getListElement(loc_icnDownloadExportFile).isEmpty()) {
            if (permissions.getProduct().getProductManagement().isDownloadExportProduct()) {
                // init file utils
                FileUtils fileUtils = new FileUtils();

                // delete old wholesale price exported file
                fileUtils.deleteFileInDownloadFolder("wholesale-price-export");

                // download new exported product
                downloadExportedProducts(fileUtils, null);

            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnDownloadExportFile, 0), "Restricted popup is not shown.");
            }
        } else logger.info("Unavailable exported product file.");

        // log
        logger.info("Check permission: Product >> Product management >> Download exported product.");
    }

    void checkUpdateWholesalePrice() {
        // navigate to product list
        navigateToProductManagementPage();

        // open list actions
        webUtils.clickJS(loc_btnImport);

        if (permissions.getProduct().getProductManagement().isUpdateWholesalePrice()) {
            // open import wholesale pricing popup
            webUtils.click(loc_ddlImportActions, 1);

            // close popup
            webUtils.click(loc_dlgImport_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlImportActions, 1), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update wholesale price.");
    }

    void checkClearStock() {
        logger.info("Check permission: Product >> Inventory >> Clear stock.");

        // navigate to product list
        navigateToProductManagementPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getInventory().isClearStock()) {
            webUtils.clickJS(loc_ddlListActions, 0);
            webUtils.click(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 0), "Restricted popup is not shown.");
        }
    }

    void checkDeleteProduct() {
        // navigate to product list
        navigateToProductManagementPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isDeleteProduct()) {
            webUtils.clickJS(loc_ddlListActions, 1);
            webUtils.click(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 1), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Delete product.");
    }

    void checkAddVariation() {
        if (!permissions.getProduct().getProductManagement().isAddVariation()) {
            // add new variation group
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnAddVariation), "Restricted popup is not shown.");
        } else {
            // add new variation group
            webUtils.clickJS(loc_btnAddVariation);

            // add variation value
            webUtils.getElement(loc_txtVariationValue).sendKeys("abc");
            UICommonAction.sleepInMiliSecond(500, "Wait suggest list variation value.");
            webUtils.getElement(loc_txtVariationValue).sendKeys(Keys.ENTER);

            // check delete variation
            checkDeleteVariation();
        }
        logger.info("Check permission: Product >> Product management >> Add variation.");
    }

    void checkDeleteVariation() {
        if (!permissions.getProduct().getProductManagement().isDeleteVariation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeleteVariation), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Delete variation.");
    }

    void checkEnableProductLot() {
        // navigate to product list
        navigateToProductManagementPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();
        if (permissions.getProduct().getLotDate().isEnableProductLot()) {
            // manage selected product by lot date
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions, 10, loc_dlgManageProductByLotDate),
                    "Can not open confirm manage product by lot-date popup.");

            // close confirm popup
            if (!webUtils.getListElement(loc_dlgManageProductByLotDate).isEmpty()) {
                webUtils.click(loc_dlgManageProductByLotDate_btnYes);
            }

        } else {
            // if staff don’t have permission “Enable product lot”
            // => show popup restricted
            // when select action “Manage product by lot” in product list
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 10),
                    "Restricted popup is not shown.");
        }

        logger.info("Check permission: Product >> Lot-date >> Enable product lot.");
    }

    public void checkViewCollectionList() {
        // get current url
        String currentURL = driver.getCurrentUrl();

        // check collection permission
        if (permissions.getProduct().getCollection().isViewCollectionList()) {
            assertCustomize.assertTrue(!webUtils.getListElement(productPage.loc_cntNoCollection).isEmpty(), "Can not find any product collection.");
        }
        logger.info("Check permission: Product >> Collection >> View collection list.");

        // back to previous page
        driver.get(currentURL);
    }

    public void checkCreateCollection() {
        // get current url
        String currentURL = driver.getCurrentUrl();

        // check create collection permission
        if (!permissions.getProduct().getCollection().isViewCollectionList()) {
            // open confirm popup
            webUtils.click(productPage.loc_lnkCreateCollection);

            // check permission
            if (permissions.getProduct().getCollection().isCreateCollection()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(productPage.loc_dlgConfirm_btnNo, "/collection/create/product/PRODUCT"),
                        "Can not navigate to create product collection page.");
            } else {
                // Show restricted popup
                // when click on [Create product collection] button in Collection management page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productPage.loc_dlgConfirm_btnNo), "No restricted popup is shown.");
            }
        }

        logger.info("Check permission: Product >> Collection >> Create collection.");

        // back to previous page
        driver.get(currentURL);
    }

    public ProductManagementPage clickOnSearchType() {
        webUtils.click(loc_btnSearchType);
        logger.info("Click on Search Type");
        return this;
    }

    public enum SearchType {
        PRODUCT_NAME,
        SKU,
        BARCODE
    }

    @SneakyThrows
    public ProductManagementPage selectSearchType(SearchType searchType) {
        switch (searchType) {
            case PRODUCT_NAME -> webUtils.click(loc_lst_btnSearchType, 0);
            case SKU -> webUtils.click(loc_lst_btnSearchType, 1);
            case BARCODE -> webUtils.click(loc_lst_btnSearchType, 2);
            default -> throw new Exception("Search type not found.");
        }
        new HomePage(driver).waitTillSpinnerDisappear1();
        logger.info("Select search type: {}", searchType);
        return this;
    }

    public ProductManagementPage inputSearch(String keyword) {
        webUtils.sendKeys(loc_txtSearch, keyword);
        logger.info("Input {} into search field.", keyword);
//        commonAction.sleepInMiliSecond(2000);
        new HomePage(driver).waitTillSpinnerDisappear();
        return this;
    }

    public ProductManagementPage excuteSearch(SearchType searchType, String keywork) {
        clickOnSearchType();
        selectSearchType(searchType);
        inputSearch(keywork);
        return this;
    }

    public String[] updateStockAction(String branchName, boolean isChangeStock, int newGoSELLStock) {
        // Array to store the start and end times of the action
        String[] actionsTime = new String[2];

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(updateStock));

        //Select branch if any
        selectBranchOnUpdateStockModal(branchName);

        // select change actions
        if (isChangeStock) webUtils.click(loc_dlgUpdateStock_actionsChange);

        // input stock value
        webUtils.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(newGoSELLStock));
        logger.info("Input stock value: %,d.".formatted(newGoSELLStock));

        // Record the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("Bulk update stock process started at {}", actionsTime[0]);

        // confirm update stock
        webUtils.click(loc_dlgUpdateStock_btnUpdate);
        waitUpdated();

        // Record and log the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("Bulk update stock process completed at {}", actionsTime[1]);

        // Return the start and end times of the creation process
        return actionsTime;
    }

    public String[] updateStockAction(String branchName, boolean isChangeStock) {
        int stock = nextInt(MAX_STOCK_QUANTITY);
        return updateStockAction(branchName, isChangeStock, stock);
    }


    public ProductManagementPage selectBranchOnUpdateStockModal(String branchName) {
        if (webUtils.getText(loc_dlgUpdateStock_ddvSelectedBranch).equalsIgnoreCase(branchName)) return this;
        webUtils.click(loc_dlgUpdateStock_ddvSelectedBranch);
        new UICommonAction(driver).selectDropdownOptionByValue(getLoc_dlgUpdateStock_lstBranch, branchName);
        logger.info("Select branch: {} on Update stock modal.", branchName);
        return this;
    }

    public String[] clearStockAction() {
        // Array to store the start and end times of the action
        String[] actionsTime = new String[2];

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        webUtils.click(loc_ddlListActions, bulkActionsValues().indexOf(clearStock));

        // Record the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("Bulk clear stock process started at {}", actionsTime[0]);

        // confirm clear stock
        webUtils.click(loc_dlgClearStock_btnOK);

        //wait
        waitUpdated(loc_lblClearStockProgressing);

        // Record and log the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("Bulk clear stock process completed at {}", actionsTime[1]);

        // Return the start and end times of the creation process
        return actionsTime;
    }

    void waitUpdated(By loadingLocator) {
        if (!webUtils.getListElement(loadingLocator).isEmpty()) {
            driver.navigate().refresh();
            logger.info("Wait loading progress disappear.");
            waitUpdated(loadingLocator);
        }
    }

    public void importToUpdateStock(int storeId, String branchName, List<String> stocks) {
        String exportTime = exportAllProductsThenGetExportTime();
        String exportFileName = downloadExportedProducts(new FileUtils(), exportTime);
        navigateToProductManagementPage();
        String newExportFileName = handlesFilename(storeId, exportFileName);
        String filePath = downloadFolder + File.separator + newExportFileName;
        // update stock
        String stockColumnName = webUtils.getLocalStorageValue("langKey").equals("vi") ? "Kho hàng" : "Remaining Stock";
        new ExcelUtils(filePath).writeColumnByValue(0, stockColumnName, stocks);
        importProduct(branchName, downloadFolder + File.separator + newExportFileName);
    }

    private String handlesFilename(int storeId, String fileName) {
        String localTime = DataGenerator.getStringByRegex(fileName, "(\\d{2}-\\d{2}-\\d{4}-\\d{2}-\\d{2})");

        System.out.println(localTime);
        // Define the formatter for the input and output
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");

        // Parse the local time
        LocalDateTime localDateTime = LocalDateTime.parse(localTime, inputFormatter);

        // Convert local time to UTC
        ZonedDateTime localZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        // Format the UTC time to the desired format
        String utcTime = utcZonedDateTime.format(outputFormatter);

        return "EXPORT_PRODUCT" +"-"+ storeId + "-"  + utcTime + ".xlsx";
    }


    /**
     * Verifies the bulk update of stock for a third-party product.
     * <p>
     * This method validates the consistency of inventory data after performing a bulk stock update. Specifically, it:
     * <ul>
     *     <li>Ensures the product with the specified GoSELL item ID exists in the original TikTok product list.</li>
     *     <li>Retrieves the updated inventory mappings for the specified product.</li>
     *     <li>Verifies inventory events based on synchronization status and action times.</li>
     *     <li>Checks that the updated inventory mappings reflect the expected stock values.</li>
     * </ul>
     * </p>
     *
     * @param originalTiktokProducts    The list of TikTok products before the bulk update.
     * @param originalInventoryMappings The list of inventory mappings before the bulk update.
     * @param goSELLItemId              The GoSELL item ID to locate and verify in the TikTok product list.
     * @param actionsTime               Array of action times used to validate inventory events.
     * @param connection                Database connection for querying inventory data.
     * @param isAutoSynced              Indicates whether the product is synchronized automatically.
     * @param isChangedStock            Indicates whether the stock value changed during the bulk update.
     * @param newGoSELLStock            The new stock value for verification.
     * @throws RuntimeException If the specified product cannot be found in the TikTok product list.
     */
    public void verifyBulkUpdateStockWith3rdProduct(List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
                                                    List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
                                                    int goSELLItemId, String[] actionsTime, Connection connection,
                                                    boolean isAutoSynced, boolean isChangedStock, int newGoSELLStock) {
        // Verify the product with the specified GoSELL item ID exists in the TikTok product list
        APIGetTikTokProducts.TikTokProduct existingProduct = originalTiktokProducts.stream()
                .filter(tikTokProduct -> tikTokProduct.getBcItemId().equals(String.valueOf(goSELLItemId)))
                .findFirst()
                .orElse(null);

        if (existingProduct == null) {
            throw new RuntimeException("Cannot find product with ID: " + goSELLItemId);
        }
        logger.info("Product with ID {} successfully bulk updated stock.", goSELLItemId);

        // Retrieve the updated inventory mappings for the product
        var itemMappingsWithNewInventoryEvents = APIGetTikTokProducts.getItemMapping(List.of(existingProduct));

        // Use the store ID from the first TikTok product to validate inventory mappings
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Verify inventory events based on synchronization status and action times
        String eventAction = isChangedStock ? "GS_SET_PRODUCT_STOCK" : "GS_CHANGE_PRODUCT_STOCK";
        VerifyAutoSyncHelper.verifyInventoryEvent(isAutoSynced,
                itemMappingsWithNewInventoryEvents, actionsTime,
                storeId, connection, eventAction);

        // Validate the consistency of inventory mappings post-update
        VerifyAutoSyncHelper.verifyInventoryMapping(
                originalInventoryMappings, null,
                null, storeId, connection, newGoSELLStock);
    }

    /**
     * Verifies the bulk clearance of stock for a third-party product.
     * <p>
     * This method ensures that stock values for a third-party product are cleared (set to 0) during a bulk update.
     * It delegates to {@link #verifyBulkUpdateStockWith3rdProduct} with pre-defined parameters to handle stock clearance.
     * </p>
     *
     * @param originalTiktokProducts    The list of TikTok products before the stock clearance.
     * @param originalInventoryMappings The list of inventory mappings before the stock clearance.
     * @param goSELLItemId              The GoSELL item ID to locate and verify in the TikTok product list.
     * @param actionsTime               Array of action times used to validate inventory events.
     * @param connection                Database connection for querying inventory data.
     * @param isAutoSynced              Indicates whether the product is synchronized automatically.
     * @throws RuntimeException If the specified product cannot be found in the TikTok product list.
     */
    public void verifyBulkClearStockWith3rdProduct(List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
                                                   List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
                                                   int goSELLItemId, String[] actionsTime, Connection connection,
                                                   boolean isAutoSynced) {
        verifyBulkUpdateStockWith3rdProduct(originalTiktokProducts, originalInventoryMappings, goSELLItemId,
                actionsTime, connection, isAutoSynced, true, 0);
    }
}
