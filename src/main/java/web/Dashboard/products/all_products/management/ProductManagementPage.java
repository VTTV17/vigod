package web.Dashboard.products.all_products.management;

import api.Seller.products.APIAllProducts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
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
    LoginInformation loginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    ProductPage productPage;

    public ProductManagementPage(WebDriver driver, LoginInformation loginInformation) {
        this.driver = driver;
        this.loginInformation = loginInformation;
        checkPermission = new CheckPermission(driver);
        commonAction = new UICommonAction(driver);
        productPage = new ProductPage(driver, loginInformation);
        assertCustomize = new AssertCustomize(driver);
    }

    void navigateToProductList() {
        if (!driver.getCurrentUrl().contains("/product/list"))
            driver.get("%s/product/list".formatted(DOMAIN));
    }

    void openBulkActionsDropdown() {
        if (!commonAction.isCheckedJS(loc_chkSelectAll)) {
            commonAction.clickJS(loc_chkSelectAll);
        }
        commonAction.clickJS(loc_lnkSelectAction);
    }

    void exportAllProducts() {
        commonAction.clickJS(loc_btnExport);
        commonAction.clickJS(loc_ddlExportActions, 0);
        commonAction.clickJS(loc_dlgExportProductListingFile_btnExport);
    }

    void importProduct() {
        // open list import actions
        commonAction.clickJS(loc_btnImport);

        // open import product popup
        commonAction.openPopupJS(loc_ddlImportActions, 0, loc_dlgImport);

        // upload file
        Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"), "src/main/resources/uploadfile/import_product/import_product.xlsx".replace("/", File.separator)));
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
        commonAction.closePopup(loc_dlgUpdatePrice_btnUpdate);
    }

    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-13814
    public void checkProductManagementPermission(AllPermissions permissions, int createdProductId, int notCreatedProductId, List<Integer> manualCollectionIds) throws Exception {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // check view product list
        checkViewProductList(createdProductId, notCreatedProductId);

        if (permissions.getProduct().getProductManagement().isViewProductList() || permissions.getProduct().getProductManagement().isViewCreatedProductList()) {
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

            // check import product
            checkImportProduct();

            // check print barcode
            checkPrintBarcode();

            // check edit price
            checkEditPrice();
        }

        // check create product
        checkCreateProduct(manualCollectionIds);

        // check view product detail
        productPage.checkProductManagementPermission(permissions, createdProductId, manualCollectionIds);
    }

    /**
     * @param createdProductId    product is created by staff.
     * @param notCreatedProductId product is created by owner or other staff.
     */
    void checkViewProductList(int createdProductId, int notCreatedProductId) {
        // navigate to product list
        navigateToProductList();

        // GET the product list from API.
        List<Integer> dbProductList = new APIAllProducts(loginInformation).getListProduct().getProductIds();
        if (permissions.getProduct().getProductManagement().isViewProductList()) {
            List<Integer> checkData = List.of(createdProductId, notCreatedProductId);
            assertCustomize.assertTrue(new HashSet<>(dbProductList).containsAll(checkData), "[Failed] List product must be contains: %s, but found list product: %s.".formatted(checkData.toString(), dbProductList.toString()));
        } else if (permissions.getProduct().getProductManagement().isViewCreatedProductList()) {
            assertCustomize.assertTrue(new HashSet<>(dbProductList).contains(createdProductId), "[Failed] List product must be contains: %s, but found list product: %s.".formatted(createdProductId, dbProductList.toString()));
            assertCustomize.assertFalse(new HashSet<>(dbProductList).contains(notCreatedProductId), "[Failed] List product must not contains: %s, but found list product: %s.".formatted(notCreatedProductId, dbProductList.toString()));
        } else {
            assertCustomize.assertTrue(dbProductList.isEmpty(), "[Failed] All products must be hidden, but found: %s.".formatted(dbProductList.toString()));
        }
        logger.info("Check permission: Product >> Product management >> View product list.");
        logger.info("Check permission: Product >> Product management >> View created product list.");
    }

    void checkActivateProduct() {
        // navigate to product list
        navigateToProductList();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isActivateProduct()) {
            commonAction.clickJS(loc_ddlListActions, 3);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 3), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Activate product.");
    }

    void checkDeactivateProduct() {
        // navigate to product list
        navigateToProductList();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isActivateProduct()) {
            commonAction.clickJS(loc_ddlListActions, 2);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 2), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Deactivate product.");
    }

    void checkCreateProduct(List<Integer> manualCollectionIds) throws Exception {
        // navigate to product list
        navigateToProductList();

        // check create product permission
        if (permissions.getProduct().getProductManagement().isCreateProduct()) {
            // check create product page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnCreateProduct, "/create"), "[Failed] Create product page must be shown instead of %s.".formatted(driver.getCurrentUrl()));

            // check view list collection
            productPage.checkViewCollectionList(manualCollectionIds, permissions);

            // check add/delete variation
            checkAddVariation();

            // create product
            productPage.createWithoutVariationProduct(false, 1);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateProduct), "[Failed] Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        }
        logger.info("Check permission: Product >> Product management >> Create product.");
    }


    void checkEditPrice() {
        // navigate to product list
        navigateToProductList();

        // bulk action
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isEditPrice()) {
            // update price
            bulkActionsUpdatePrice(MAX_PRICE, MAX_PRICE, nextLong(1000));
            logger.info("Check permission: Product >> Product management >> View cost price.");
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 8), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Edit price.");
    }

    void checkExportProduct() {
        // navigate to product list
        navigateToProductList();

        if (!permissions.getProduct().getProductManagement().isExportProducts()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnExport), "Restricted popup does not shown.");
        } else {
            // export all products
            exportAllProducts();

            // check download export all products
            checkDownloadExportedProducts();
        }
        logger.info("Check permission: Product >> Product management >> Export product.");
    }

    void checkImportProduct() {
        // navigate to product list
        navigateToProductList();

        if (permissions.getProduct().getProductManagement().isImportProducts()) {
            // import product
            importProduct();

            // check update wholesale price
            checkUpdateWholesalePrice();
        } else {
            commonAction.clickJS(loc_btnImport);
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlImportActions, 0), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Import product.");
    }

    void checkPrintBarcode() {
        // navigate to product list
        navigateToProductList();

        if (permissions.getProduct().getProductManagement().isPrintBarcode()) {
            commonAction.openPopupJS(loc_btnPrintBarcode, loc_dlgPrintBarcode);
            commonAction.closePopup(loc_dlgPrintBarcode_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPrintBarcode), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Print barcode.");
    }

    void checkDownloadExportedProducts() {
        driver.get("%s/product/export-history".formatted(DOMAIN));
        if (!permissions.getProduct().getProductManagement().isDownloadExportProduct()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnDownloadExportFile), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Download exported product.");
    }

    void checkUpdateWholesalePrice() {
        // navigate to product list
        navigateToProductList();

        // open list actions
        commonAction.clickJS(loc_btnImport);

        if (permissions.getProduct().getProductManagement().isUpdateWholesalePrice()) {
            // open import wholesale pricing popup
            commonAction.openPopupJS(loc_ddlImportActions, 1, loc_dlgImport);

            // close popup
            commonAction.closePopup(loc_dlgImport_btnCancel);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlImportActions, 1), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Update wholesale price.");
    }

    void checkClearStock() {
        logger.info("Check permission: Product >> Inventory >> Clear stock.");

        // navigate to product list
        navigateToProductList();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getInventory().isClearStock()) {
            commonAction.clickJS(loc_ddlListActions, 0);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 0), "Restricted popup does not shown.");
        }
    }

    void checkDeleteProduct() {
        // navigate to product list
        navigateToProductList();

        // bulk actions
        openBulkActionsDropdown();
        if (permissions.getProduct().getProductManagement().isDeleteProduct()) {
            commonAction.clickJS(loc_ddlListActions, 1);
            commonAction.closePopup(loc_dlgConfirm_icnClose);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 1), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Delete product.");
    }

    void checkAddVariation() {
        if (!permissions.getProduct().getProductManagement().isAddVariation()) {
            // add new variation group
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productPage.getLoc_btnAddVariation()), "Restricted popup does not shown.");
        } else {
            // add new variation group
            commonAction.clickJS(productPage.getLoc_btnAddVariation());

            // add variation value
            commonAction.getElement(productPage.getLoc_txtVariationValue(), 0).sendKeys("abc");
            commonAction.clickJS(productPage.getLoc_lblVariations());

            // check delete variation
            checkDeleteVariation();
        }
        logger.info("Check permission: Product >> Product management >> Add variation.");
    }

    void checkDeleteVariation() {
        if (!permissions.getProduct().getProductManagement().isDeleteVariation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(productPage.getLoc_btnDeleteVariation()), "Restricted popup does not shown.");
        }
        logger.info("Check permission: Product >> Product management >> Delete variation.");
    }
}
