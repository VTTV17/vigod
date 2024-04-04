package web.Dashboard.products.location_receipt.management;

import api.Seller.login.Login;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.location.APILocation;
import api.Seller.products.location_receipt.APILocationReceipt;
import api.Seller.products.location_receipt.APILocationReceipt.AllLocationReceiptInfo;
import api.Seller.products.lot_date.APILotDate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.excel.Excel;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.location_receipt.crud.LocationReceiptPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static api.Seller.products.location.APILocation.ProductLocationInfo;
import static utilities.links.Links.DOMAIN;

public class LocationReceiptManagementPage extends LocationReceiptManagementElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LocationReceiptManagementPage.class);

    public LocationReceiptManagementPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    /*-------------------------------------*/
    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-24810
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo staffLoginInfo;

    public LocationReceiptManagementPage getLoginInformation(LoginInformation sellerLoginInformation,
                                                             LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        this.sellerLoginInformation = sellerLoginInformation;

        // get staff login info
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    void openImportProductLotOrLocationPopup() {
        // check can open import popup
        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnImportInBulk, loc_dlgImportProductLotOrLocationInBulk),
                "Can not open import product lot/location in bulk popup.");

        // log
        logger.info("Open import lot/location popup.");
    }

    void selectBranchToImport(String branchName) {
        // select branch

        By branchLocator = By.xpath(str_dlgImportProductLotOrLocationInBulk_chkBranch.formatted(branchName));
        if (!commonAction.isCheckedJS(branchLocator))
            commonAction.clickJS(branchLocator);
        logger.info("Select branch '%s'.".formatted(branchName));

    }

    void uploadImportFile() {
        // remove old file
        if (!commonAction.getListElement(loc_dlgImportProductLotOrLocationInBulk_icnRemove).isEmpty()) {
            commonAction.clickJS(loc_dlgImportProductLotOrLocationInBulk_icnRemove);
            // log
            logger.info("Remove old file.");
        }

        // upload file
        String importFilePath = new DataGenerator().getFilePath("import_location_receipt_template.xlsx");
        commonAction.uploads(loc_dlgImportProductLotOrLocationInBulk_btnDragAndDrop, importFilePath);
        logger.info("Import lot/location, file path: %s.".formatted(importFilePath));
    }

    public void checkLocationReceiptPermission(AllPermissions permissions) throws IOException {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // check view product location receipt list
        checkViewProductLocationReceiptList();

        // check permission at crud location receipt page
        new LocationReceiptPage(driver).getLoginInformation(sellerLoginInformation, staffLoginInformation)
                .checkLocationReceiptPermission(permissions);

        // check import product to location
        checkImportProductToLocation();
    }

    void checkViewProductLocationReceiptList() {
        // get full location receipt with seller role
        AllLocationReceiptInfo info = new APILocationReceipt(sellerLoginInformation).getAllLocationReceiptInfo();

        // init location receipt API with staff role
        APILocationReceipt locationReceipt = new APILocationReceipt(staffLoginInformation);

        // check location receipt in unassigned branch must be hidden
        assertCustomize.assertFalse(locationReceipt.hasLocationReceiptInUnassignedBranches(), "Location receipts in unassigned branches are still showing");

        // check view add list permission
        List<Integer> addListFromAPI = locationReceipt.getListAddProductToLocation(staffLoginInfo.getAssignedBranchesNames());
        // if staff don’t have permission “View get product location receipt list”
        // => don’t show any get receipt at Location receipt page
        List<Integer> checkAddList = permissions.getProduct().getLocationReceipt().isViewAddProductLocationReceiptList()
                ? locationReceipt.getListAddProductToLocation(staffLoginInfo.getAssignedBranchesNames(), info)
                : new ArrayList<>();
        assertCustomize.assertTrue(CollectionUtils.isEqualCollection(addListFromAPI, checkAddList),
                "List add location receipts must be %s, but found %s.".formatted(checkAddList.toString(), addListFromAPI.toString()));
        // log
        logger.info("Check permission: Product >> Location receipt >> View add product location receipt list.");

        // check view get list permission
        List<Integer> getListFromAPI = locationReceipt.getListGetProductFromLocation(staffLoginInfo.getAssignedBranchesNames());
        // if staff don’t have permission “View get product location receipt list”
        // => don’t show any Get receipt at Location receipt page
        List<Integer> checkGetList = permissions.getProduct().getLocationReceipt().isViewGetProductLocationReceiptList()
                ? locationReceipt.getListGetProductFromLocation(staffLoginInfo.getAssignedBranchesNames(), info)
                : new ArrayList<>();
        assertCustomize.assertTrue(CollectionUtils.isEqualCollection(getListFromAPI, checkGetList),
                "List get location receipts must be %s, but found %s.".formatted(checkGetList.toString(), getListFromAPI.toString()));
        // log
        logger.info("Check permission: Product >> Location receipt >> View get product location receipt list.");
    }

    void navigateToLocationReceiptManagementPage() {
        driver.get("%s/location-receipt/list".formatted(DOMAIN));
        driver.navigate().refresh();
        logger.info("Navigate to location receipt management page by URL.");
    }

    void generateImportLocationFile(String productId, String productName, String lotId, String locationCode) throws IOException {
        Excel excel = new Excel();
        String fileName = "import_location_receipt_template.xlsx";
        excel.writeCellValue(fileName, 0, 1, excel.getCellIndexByCellValue(fileName, 0, 0, "Product ID"), productId);
        excel.writeCellValue(fileName, 0, 1, excel.getCellIndexByCellValue(fileName, 0, 0, "Product Name"), productName);
        excel.writeCellValue(fileName, 0, 1, excel.getCellIndexByCellValue(fileName, 0, 0, "Quantity"), "1");
        excel.writeCellValue(fileName, 0, 1, excel.getCellIndexByCellValue(fileName, 0, 0, "Lot ID"), lotId);
        excel.writeCellValue(fileName, 0, 1, excel.getCellIndexByCellValue(fileName, 0, 0, "Location Code"), locationCode);
    }

    String getProductId(String itemId, String modelId) {
        return modelId == null ? itemId : "%s-%s".formatted(itemId, modelId);
    }

    void checkImportProductToLocation() throws IOException {
        // navigate to product location receipt management page
        navigateToLocationReceiptManagementPage();

        // get import information
        // get location code
        ProductLocationInfo locationInfo = new APILocation(sellerLoginInformation).getProductLocationThatHaveNoChildLocation(staffLoginInfo.getAssignedBranchesIds());
        String locationCode = (locationInfo.getLocationCode() != null)
                ? locationInfo.getLocationCode() :
                "";
        String branchName = locationInfo.getBranchName();

        // get branchId
        int branchId = locationInfo.getBranchId();

        // if no location => skip.
        if (branchId != 0) {
            // get product info
            APISuggestionProduct suggestionProductProductsWithSellerToken = new APISuggestionProduct(sellerLoginInformation);
            APISuggestionProduct.SuggestionProductsInfo hasLotProductInfo = suggestionProductProductsWithSellerToken.getSuggestProductForImportProductLocationReceipt(branchId, true);
            APISuggestionProduct.SuggestionProductsInfo noLotProductInfo = suggestionProductProductsWithSellerToken.getSuggestProductForImportProductLocationReceipt(branchId, false);

            // get lot
            APILotDate.LotDateManagementInfo lotDateManagementInfo = new APILotDate(sellerLoginInformation).getAllLotDateInformation();
            String lotId = !lotDateManagementInfo.getLotDateIds().isEmpty()
                    ? String.valueOf(lotDateManagementInfo.getLotDateIds().get(0))
                    : "";
            // check permission
            // only lot
            if (!lotId.isEmpty()) {
                checkImport(branchName, getProductId(hasLotProductInfo.getItemId(), hasLotProductInfo.getModelId()), hasLotProductInfo.getItemName(), lotId, "");
            }
            logger.info("Check file only have lot information.");

            // only location
            if (!locationCode.isEmpty())
                checkImport(branchName, getProductId(noLotProductInfo.getItemId(), noLotProductInfo.getModelId()), noLotProductInfo.getItemName(), "", locationCode);
            logger.info("Check file only have location information.");

            // both lot and location
            if (!(lotId.isEmpty() && locationCode.isEmpty()))
                checkImport(branchName, getProductId(hasLotProductInfo.getItemId(), hasLotProductInfo.getModelId()), hasLotProductInfo.getItemName(), lotId, locationCode);
            logger.info("Check file have both lot and location information.");
        }

        // log
        logger.info("Check permission: Product >> Lot-date >> Import lot.");
        logger.info("Check permission: Product >> Location receipt >> Import product to location.");
    }

    void checkImport(String branchName, String productId, String productName, String lotId, String locationCode) throws IOException {
        // input data into file import
        generateImportLocationFile(productId, productName, String.valueOf(lotId), locationCode);

        // open import product lot/location popup
        openImportProductLotOrLocationPopup();

        // select branch
        selectBranchToImport(branchName);

        // upload file
        uploadImportFile();

        boolean hasLot = !lotId.isEmpty();
        boolean hasLocation = !locationCode.isEmpty();
        boolean hasImportLotPermission = permissions.getProduct().getLotDate().isImportLot();
        boolean hasImportLocationPermission = permissions.getProduct().getLocationReceipt().isImportProductToLocation();
        if (hasLot && !hasLocation && hasImportLotPermission || !hasLot && hasLocation && hasImportLocationPermission || hasLot && hasImportLotPermission && hasImportLocationPermission) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgImportProductLotOrLocationInBulk_btnImport, loc_prgImportProgressBar),
                    "Can not import lot/location.");
        } else {
            // if staff don’t have permission “Import product to location”
            // => show restricted popup
            // when click [Confirm] button on popup import (at location receipt page)
            // to import a file contain Location information
            // OR
            // if staff don’t have permission “Import lot”
            // => show restricted popup
            // when click [Confirm] button on popup import (at location receipt page)
            // to import a file contain Lot information
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgImportProductLotOrLocationInBulk_btnImport),
                    "Restricted popup is not shown.");
        }
    }
}
