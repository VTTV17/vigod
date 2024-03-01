package web.Dashboard.products.all_products.management;

import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.CreateProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import web.Dashboard.products.all_products.crud.ProductPage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;

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
        productPage = new ProductPage(driver, staffLoginInformation);
        return this;
    }

    void navigateToProductListPage() {
        if (!driver.getCurrentUrl().contains("/product/list"))
            driver.get("%s/product/list".formatted(DOMAIN));
    }

    void openBulkActionsDropdown() {
        if (commonAction.getListElement(loc_tblProductManagement_productRow).isEmpty())
            driver.navigate().refresh();
        if (!commonAction.isCheckedJS(loc_chkSelectAll)) {
            commonAction.clickJS(loc_chkSelectAll);
        }
        commonAction.clickJS(loc_lnkSelectAction);
    }

    void exportAllProducts() {
        navigateToProductListPage();
        commonAction.clickJS(loc_btnExport);
        commonAction.clickJS(loc_ddlExportActions, 0);
        commonAction.clickJS(loc_dlgExportProductListingFile_btnExport);
        logger.info("Export all products.");
    }

    void exportWholesaleProducts() {
        navigateToProductListPage();
        commonAction.clickJS(loc_btnExport);
        commonAction.clickJS(loc_ddlExportActions, 1);
        logger.info("Export wholesale products.");
    }

    void navigateToDownloadHistoryPage() {
        if (!driver.getCurrentUrl().contains("/product/export-history")) {
            driver.get(DOMAIN + "/product/export-history");
        }
    }

    void importProduct() {
        // open list import actions
        commonAction.clickJS(loc_btnImport);

        // open import product popup
        commonAction.openPopupJS(loc_ddlImportActions, 0, loc_dlgImport);

        // upload file
        Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"), "/src/main/resources/uploadfile/import_product/import_product.xlsx".replace("/", File.separator)));
        commonAction.uploads(loc_dlgImport_btnDragAndDrop, filePath.toString());

        // complete import product
        commonAction.closePopup(loc_dlgImport_btnImport);
    }

    void applyAll(long price, int typeIndex) {
        commonAction.sendKeys(loc_dlgUpdatePrice_txtApplyAll, String.valueOf(price));
        commonAction.openDropdownJS(loc_dlgUpdatePrice_ddvSelectedPriceType, loc_dlgUpdatePrice_ddlPriceType);
        commonAction.clickJS(loc_dlgUpdatePrice_ddlPriceType, typeIndex);
        commonAction.click(loc_dlgUpdatePrice_btnApplyAll);
    }

    void bulkActionsUpdatePrice(long listingPrice, long sellingPrice, long costPrice) {
        // bulk actions
        openBulkActionsDropdown();

        // open update price popup
        commonAction.openPopupJS(loc_ddlListActions, 8, loc_dlgUpdatePrice);

        // input listing price
        applyAll(listingPrice, 0);

        // input selling price
        applyAll(sellingPrice, 1);

        // input cost price
        if (permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
            applyAll(costPrice, 2);
        } else {
            // view cost price
            assertCustomize.assertTrue(commonAction.getValue(loc_dlgUpdatePrice_txtCostPrice, 0).equals("0"), "Product cost price still shows when staff does not have 'View product cost price' permission.");
        }

        // complete update price
        commonAction.click(loc_dlgUpdatePrice_btnUpdate);
    }

    // check permission
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

        // get list product ids
        List<Integer> productIds = new APIAllProducts(staffLoginInformation).getListProduct().getProductIds();

        // check view product list
        checkViewProductList(productIds, createdProductId, notCreatedProductId);

        // check create product
        checkCreateProduct();

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
            productPage.checkProductManagementPermission(permissions, productIds.get(0));
        }
    }

    void checkViewProductList(List<Integer> dbProductList, int createdProductId, int notCreatedProductId) {
        // navigate to product list
        navigateToProductListPage();

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
        navigateToProductListPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isActivateProduct()) {
            commonAction.clickJS(loc_ddlListActions, 3);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 3), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Activate product.");
    }

    void checkDeactivateProduct() {
        // navigate to product list
        navigateToProductListPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isDeactivateProduct()) {
            commonAction.clickJS(loc_ddlListActions, 2);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 2), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Deactivate product.");
    }

    void checkCreateProduct() throws Exception {
        // navigate to product list
        navigateToProductListPage();

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
            productPage.createWithoutVariationProduct(false, 1);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateProduct),
                    "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        }
        logger.info("Check permission: Product >> Product management >> Create product.");
    }


    void checkEditPrice() {
        // navigate to product list
        navigateToProductListPage();

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
        navigateToProductListPage();

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
        navigateToProductListPage();

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
        navigateToProductListPage();

        if (permissions.getProduct().getProductManagement().isPrintBarcode()) {
            commonAction.openPopupJS(loc_btnPrintBarcode, loc_dlgPrintBarcode);
            commonAction.closePopup(loc_dlgPrintBarcode_btnCancel);
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
        navigateToProductListPage();

        // open list actions
        commonAction.clickJS(loc_btnImport);

        if (permissions.getProduct().getProductManagement().isUpdateWholesalePrice()) {
            // open import wholesale pricing popup
            commonAction.openPopupJS(loc_ddlImportActions, 1, loc_dlgImport);

            // close popup
            commonAction.closePopup(loc_dlgImport_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlImportActions, 1), "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update wholesale price.");
    }

    void checkClearStock() {
        logger.info("Check permission: Product >> Inventory >> Clear stock.");

        // navigate to product list
        navigateToProductListPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getInventory().isClearStock()) {
            commonAction.clickJS(loc_ddlListActions, 0);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 0), "Restricted popup is not shown.");
        }
    }

    void checkDeleteProduct() {
        // navigate to product list
        navigateToProductListPage();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isDeleteProduct()) {
            commonAction.clickJS(loc_ddlListActions, 1);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
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
        navigateToProductListPage();

        // open bulk actions dropdown
        openBulkActionsDropdown();
        if (permissions.getProduct().getLotDate().isEnableProductLot()) {
            // manage selected product by lot date
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions, 10, loc_dlgConfirmManageProductByLotDate),
                    "Can not open confirm manage product by lot-date popup.");

            // close confirm popup
            if (!commonAction.getListElement(loc_dlgConfirmManageProductByLotDate).isEmpty()) {
                commonAction.closePopup(loc_dlgConfirmManageProductByLotDate_btnYes);
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
            assertCustomize.assertTrue(!commonAction.getListElement(productPage.loc_cntNoCollection).isEmpty(), "Can not found any product collection.");
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
