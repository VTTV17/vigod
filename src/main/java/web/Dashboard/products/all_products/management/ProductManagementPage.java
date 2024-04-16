package web.Dashboard.products.all_products.management;

import api.Seller.affiliate.dropship.PartnerTransferManagement;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.products.all_products.ProductInformation;
import api.Seller.products.transfer.TransferManagement;
import api.Seller.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import web.Dashboard.products.all_products.crud.ProductPage;

import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.character_limit.CharacterLimit.MAX_STOCK_QUANTITY;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.products.all_products.management.ProductManagementPage.BulkActions.*;
import static web.Dashboard.products.all_products.management.ProductManagementPage.PriceType.*;

public class ProductManagementPage extends ProductManagementElement {
    Logger logger = LogManager.getLogger(ProductManagementPage.class);
    WebDriver driver;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    ProductPage productPage;

    public ProductManagementPage(WebDriver driver) {
        this.driver = driver;
        checkPermission = new CheckPermission(driver);
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public ProductManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        productPage = new ProductPage(driver);
        return this;
    }

    void navigateToProductManagementPage() {
        driver.get("%s/product/list".formatted(DOMAIN));
        driver.navigate().refresh();
        logger.info("Navigate to product management page.");
    }

    enum BulkActions {
        clearStock, delete, deactivate, active, updateStock, updateTax, displayOutOfStock, updateSellingPlatform, updatePrice, setStockAlert, manageStockByLotDate;

        static List<BulkActions> bulkActionsValues() {
            return new ArrayList<>(Arrays.asList(values()));
        }
    }

    void openBulkActionsDropdown() {
        if (commonAction.getListElement(loc_lblProductId).isEmpty())
            driver.navigate().refresh();
        if (!commonAction.isCheckedJS(loc_chkSelectAll)) {
            commonAction.clickJS(loc_chkSelectAll);
        }
        commonAction.clickJS(loc_lnkSelectAction);
    }

    void exportAllProducts() {
        navigateToProductManagementPage();
        commonAction.clickJS(loc_btnExport);
        commonAction.clickJS(loc_ddlExportActions, 0);
        commonAction.clickJS(loc_dlgExportProductListingFile_btnExport);
        logger.info("Export all products.");
    }

    void exportWholesaleProducts() {
        navigateToProductManagementPage();
        commonAction.clickJS(loc_btnExport);
        if (!commonAction.getListElement(loc_ddlExportActions).isEmpty()) {
            commonAction.clickJS(loc_ddlExportActions, 1);
            logger.info("Export wholesale products.");
        }
    }

    void navigateToDownloadHistoryPage() {
        driver.get(DOMAIN + "/product/export-history");
        driver.navigate().refresh();
        logger.info("Navigate to download export product history page.");

    }

    void importProduct() {
        // open list import actions
        commonAction.clickJS(loc_btnImport);

        // open import product popup
        commonAction.clickJS(loc_ddlImportActions, 0);

        // select branch
        String branchName = new Login().getInfo(staffLoginInformation).getAssignedBranchesNames().get(0);
        commonAction.clickJS(By.xpath(str_dlgImport_chkBranch.formatted(branchName)));

        // check import product is opened or not
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgImport).isEmpty(), "Can not open import popup");

        // upload file
        if (!commonAction.getListElement(loc_dlgImport).isEmpty()) {
            commonAction.uploads(loc_dlgImport_btnDragAndDrop, new DataGenerator().getFilePath("import_product.xlsx"));

            // complete import product
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgImport_btnImport, loc_prgStatus),
                    "Can not import product.");
        }
    }

    void applyAll(long price, int typeIndex) {
        commonAction.sendKeys(loc_dlgUpdatePrice_txtApplyAll, String.valueOf(price));
        commonAction.openDropdownJS(loc_dlgUpdatePrice_ddvSelectedPriceType, loc_dlgUpdatePrice_ddlPriceType);
        commonAction.clickJS(loc_dlgUpdatePrice_ddlPriceType, typeIndex);
        commonAction.click(loc_dlgUpdatePrice_btnApplyAll);
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
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updatePrice), loc_dlgUpdatePrice);

        // input listing price

        applyAll(listingPrice, getAllPriceTypes().indexOf(listing));

        // input selling price
        applyAll(sellingPrice, getAllPriceTypes().indexOf(selling));

        // input cost price
        if (permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
            applyAll(costPrice, getAllPriceTypes().indexOf(cost));
        } else {
            // view cost price
            assertCustomize.assertTrue(commonAction.getValue(loc_dlgUpdatePrice_txtCostPrice, 0).equals("0"), "Product cost price still shows when staff does not have 'View product cost price' permission.");
        }

        // complete update price
        commonAction.click(loc_dlgUpdatePrice_btnUpdate);
    }

    /* Check bulk actions */
    List<String> getAllProductIdIn1stPage() {
        // navigate to product management page
        navigateToProductManagementPage();

        // if page is not loaded, refresh page
        if (commonAction.getListElement(loc_lblProductId).isEmpty()) {
            driver.navigate().refresh();
        }

        // get number of products in 1st page
        int bound = commonAction.getListElement(loc_lblProductId).size();

        // return list productId
        return IntStream.range(0, bound).mapToObj(index -> commonAction.getText(loc_lblProductId, index)).toList();
    }

    void waitUpdated() {
        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
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
        ProductInformation productInformation = new ProductInformation(sellerLoginInformation);
        Map<String, List<Integer>> beforeUpdateStocksInItemService = productInformation.getCurrentProductStocksMap(productIds);

        // get before update stock in ES
        APIAllProducts allProducts = new APIAllProducts(sellerLoginInformation);
        Map<String, Integer> beforeUpdateStocksInES = allProducts.getCurrentStocks(productIds);

        // log
        logger.info("Wait get product stock before clear stock.");

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(clearStock), loc_dlgClearStock);

        // confirm clear stock
        commonAction.click(loc_dlgClearStock_btnOK);

        /* Do not need to wait product updated because calculate function needs ~ 2 minutes, that time is enough for product to be updated.*/
        // check product stock are updated in item-service
        assertCustomize.assertEquals(productInformation.getExpectedListProductStockQuantityAfterClearStock(productIds, beforeUpdateStocksInItemService),
                productInformation.getCurrentStockOfProducts(productIds),
                "Product stock are not updated in item-service.");
        logger.info("Check product stock in item-service after clearing stock.");

        // check product stock are updated in ES
        assertCustomize.assertEquals(allProducts.getExpectedListProductStockQuantityAfterClearStock(productIds, beforeUpdateStocksInES),
                allProducts.getListProductStockQuantityAfterClearStock(productIds),
                "Product stock are not updated in ES.");
        logger.info("Check product stock in ES after clearing stock.");

        // log
        logger.info("Check product status after bulk actions: CLEAR STOCK.");
    }


    // bulk delete
    public List<Boolean> checkProductCanBeDeleted(List<String> productIds) {
        List<Integer> listProductIdThatInInCompleteTransfer = new ArrayList<>();
        listProductIdThatInInCompleteTransfer.addAll(new TransferManagement(sellerLoginInformation).getListProductIdInNotCompletedTransfer());
        listProductIdThatInInCompleteTransfer.addAll(new PartnerTransferManagement(sellerLoginInformation).getListProductIdInNotCompletedTransfer());
        return productIds.stream().map(productId -> !listProductIdThatInInCompleteTransfer.contains(Integer.parseInt(productId))).toList();
    }

    public void bulkDeleteProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(delete), loc_dlgDeleteProduct);

        // confirm active product
        commonAction.click(loc_dlgDeleteProduct_btnDelete);

        // check actions are completed or not
        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
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
            List<Boolean> isDeleted = new ProductInformation(sellerLoginInformation).isDeleted(productIds);
            assertCustomize.assertEquals(checkProductCanBeDeleted, isDeleted, "Product is not deleted in item-service.");

            // log
            logger.info("Check product list after bulk actions: DELETE.");
        } else logger.error("Can not bulk actions DELETE product.");
    }

    // bulk deactivate
    public void bulkDeactivateProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm deactivate popup
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(deactivate), loc_dlgDeactivateProduct);

        // confirm active product
        commonAction.click(loc_dlgDeactivateProduct_btnYes);

        // check actions are completed or not
        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product status after updating
            assertCustomize.assertTrue(new ProductInformation(sellerLoginInformation)
                            .getListProductStatus(productIds)
                            .stream()
                            .allMatch(status -> status.equals("INACTIVE")),
                    "All selected products must be DEACTIVATE, but some product is not updated.");

            // log
            logger.info("Check product status after bulk actions: DEACTIVATE.");
        } else logger.error("Can not bulk actions ACTIVE product.");
    }

    // bulk active
    public void bulkActiveProduct() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(active), loc_dlgActiveProduct);

        // confirm active product
        commonAction.click(loc_dlgActiveProduct_btnYes);

        // check actions are completed or not
        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product in product management
            assertCustomize.assertTrue(new ProductInformation(sellerLoginInformation)
                            .getListProductStatus(productIds)
                            .stream()
                            .allMatch(status -> status.equals("ACTIVE")),
                    "All selected products must be ACTIVE, but some product is not updated.");

            // log
            logger.info("Check product status after bulk actions: ACTIVE.");
        } else logger.error("Can not bulk actions ACTIVE product.");
    }

    // bulk update stock
    public void bulkUpdateStock() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // get before update stock in item-service
        ProductInformation productInformation = new ProductInformation(sellerLoginInformation);
        Map<String, List<Integer>> beforeUpdateStocksInItemService = productInformation.getCurrentProductStocksMap(productIds);

        // get before update stock in ES
        APIAllProducts allProducts = new APIAllProducts(sellerLoginInformation);
        int branchId = new BranchManagement(sellerLoginInformation).getInfo().getBranchID().get(0);
        Map<String, Integer> beforeUpdateStocksInES = allProducts.getCurrentStocks(productIds, branchId);

        // log
        logger.info("Wait get product stock before clear stock.");

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm active popup
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updateStock), loc_dlgUpdateStock);

        // select change actions
        commonAction.click(loc_dlgUpdateStock_actionsChange);

        // input stock value
        int stock = nextInt(MAX_STOCK_QUANTITY);
        commonAction.sendKeys(loc_dlgUpdateStock_txtStockValue, String.valueOf(stock));
        logger.info("Input stock value: %,d.".formatted(stock));

        // confirm update stock
        commonAction.click(loc_dlgUpdateStock_btnUpdate);

        /* Do not need to wait product updated because calculate function needs ~ 2 minutes, that time is enough for product to be updated.*/
        // check product stock are updated in item-service
        assertCustomize.assertEquals(productInformation.getExpectedListProductStockQuantityAfterUpdateStock(productIds, branchId, beforeUpdateStocksInItemService, stock),
                productInformation.getCurrentStockOfProducts(productIds),
                "Product stock are not updated in item-service.");
        logger.info("Check product stock in item-service after updating stock.");

        // check product stock are updated in ES
        assertCustomize.assertEquals(allProducts.getExpectedListProductStockQuantityAfterUpdateStock(productIds, beforeUpdateStocksInES, stock),
                allProducts.getListProductStockQuantityAfterUpdateStock(productIds, branchId),
                "Product stock are not updated in ES.");
        logger.info("Check product stock in ES after updating stock.");

        // log
        logger.info("Check product status after bulk actions: UPDATE STOCK.");
    }


    // bulk update tax
    public void bulkUpdateTax() {
        // get list product need to updated
        List<String> productIds = getAllProductIdIn1stPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();

        // open confirm deactivate popup
        commonAction.openPopupJS(loc_ddlListActions, bulkActionsValues().indexOf(updateTax), loc_dlgUpdateTax);

        // get taxId
        int bound = commonAction.getListElement(loc_dlgUpdateTax_ddlTaxOptions).size();
        int taxIndex = nextInt(bound);
        int newTaxId = Integer.parseInt(commonAction.getValue(loc_dlgUpdateTax_ddlTaxOptions, taxIndex));
        commonAction.clickJS(loc_dlgUpdateTax_ddlTaxOptions, taxIndex);

        // confirm active product
        commonAction.click(loc_dlgUpdateTax_btnOK);

        // check actions are completed or not
        if (!commonAction.getListElement(loc_prgStatus).isEmpty()) {
            // wait updated
            waitUpdated();

            // check product status after updating
            assertCustomize.assertTrue(new ProductInformation(sellerLoginInformation)
                            .getListProductTaxId(productIds)
                            .stream()
                            .allMatch(taxId -> taxId == newTaxId),
                    "Tax of all selected products must be %s.".formatted(newTaxId));

            // log
            logger.info("Check product taxId after bulk actions: UPDATE TAX.");
        } else logger.error("Can not bulk actions Update Tax product.");
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
                ? new CreateProduct(staffLoginInformation).createWithoutVariationProduct(false, 1000).getProductID()
                : 0;

        // get productId is created by seller
        int notCreatedProductId = new CreateProduct(sellerLoginInformation).createWithoutVariationProduct(false, 1000).getProductID();

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
            commonAction.clickJS(loc_ddlListActions, 3);
            commonAction.click(loc_dlgConfirm_icnClose);
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
            commonAction.clickJS(loc_ddlListActions, 2);
            commonAction.click(loc_dlgConfirm_icnClose);
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
            exportAllProducts();

            // export wholesale product
            exportWholesaleProducts();
            commonAction.sleepInMiliSecond(3000, "Waiting for download.");
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
            importProduct();

            // check update wholesale price
            checkUpdateWholesalePrice();
        } else {
            commonAction.clickJS(loc_btnImport);
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlImportActions, 0), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Import product.");
    }

    void checkPrintBarcode() {
        // navigate to product list
        navigateToProductManagementPage();

        if (permissions.getProduct().getProductManagement().isPrintBarcode()) {
            commonAction.openPopupJS(loc_btnPrintBarcode, loc_dlgPrintBarcode);
            commonAction.click(loc_dlgPrintBarcode_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPrintBarcode), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Print barcode.");
    }

    void checkDownloadExportedProducts() {
        // navigate to history export page
        navigateToDownloadHistoryPage();

        // check permission
        if (!commonAction.getListElement(loc_icnDownloadExportFile).isEmpty()) {
            if (permissions.getProduct().getProductManagement().isDownloadExportProduct()) {
                // init file utils
                FileUtils fileUtils = new FileUtils();

                // delete old wholesale price exported file
                fileUtils.deleteFileInDownloadFolder("wholesale-price-export");

                // delete old exported product
                fileUtils.deleteFileInDownloadFolder("EXPORT_PRODUCT");

                // download new exported product
                commonAction.clickJS(loc_icnDownloadExportFile, 0);
                commonAction.sleepInMiliSecond(1000, "Waiting for download.");
                assertCustomize.assertTrue(fileUtils.isDownloadSuccessful("EXPORT_PRODUCT"), "No exported product file is downloaded.");

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
        commonAction.clickJS(loc_btnImport);

        if (permissions.getProduct().getProductManagement().isUpdateWholesalePrice()) {
            // open import wholesale pricing popup
            commonAction.openPopupJS(loc_ddlImportActions, 1, loc_dlgImport);

            // close popup
            commonAction.click(loc_dlgImport_btnCancel);
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
            commonAction.clickJS(loc_ddlListActions, 0);
            commonAction.click(loc_dlgConfirm_icnClose);
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
            commonAction.clickJS(loc_ddlListActions, 1);
            commonAction.click(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 1), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Delete product.");
    }

    void checkAddVariation() {
        if (!permissions.getProduct().getProductManagement().isAddVariation()) {
            // add new variation group
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productPage.getLoc_btnAddVariation()), "Restricted popup is not shown.");
        } else {
            // add new variation group
            commonAction.clickJS(productPage.getLoc_btnAddVariation());

            // add variation value
            commonAction.getElement(productPage.getLoc_txtVariationValue(), 0).sendKeys("abc");
            commonAction.sleepInMiliSecond(500, "Wait suggest list variation value.");
            commonAction.getElement(productPage.getLoc_txtVariationValue(), 0).sendKeys(Keys.ENTER);

            // check delete variation
            checkDeleteVariation();
        }
        logger.info("Check permission: Product >> Product management >> Add variation.");
    }

    void checkDeleteVariation() {
        if (!permissions.getProduct().getProductManagement().isDeleteVariation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productPage.getLoc_btnDeleteVariation()), "Restricted popup is not shown.");
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
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions, 10, loc_dlgConfirmManageProductByLotDate),
                    "Can not open confirm manage product by lot-date popup.");

            // close confirm popup
            if (!commonAction.getListElement(loc_dlgConfirmManageProductByLotDate).isEmpty()) {
                commonAction.click(loc_dlgConfirmManageProductByLotDate_btnYes);
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
            assertCustomize.assertTrue(!commonAction.getListElement(productPage.loc_cntNoCollection).isEmpty(), "Can not find any product collection.");
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
            commonAction.click(productPage.loc_lnkCreateCollection);

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
}
