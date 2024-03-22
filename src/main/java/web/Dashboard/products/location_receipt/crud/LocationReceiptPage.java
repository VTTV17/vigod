package web.Dashboard.products.location_receipt.crud;

import api.Seller.login.Login;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.location.APILocation;
import api.Seller.products.location_receipt.APILocationReceipt;
import api.Seller.products.lot_date.APILotDate;
import api.Seller.products.lot_date.APILotDate.ProductLotInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static api.Seller.products.all_products.APISuggestionProduct.*;
import static api.Seller.products.location.APILocation.ProductLocationInfo;
import static utilities.links.Links.DOMAIN;

public class LocationReceiptPage extends LocationReceiptElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LocationReceiptPage.class);

    public LocationReceiptPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    LoginInformation staffLoginInformation;
    LoginInformation sellLoginInformation;
    LoginDashboardInfo staffLoginInfo;
    APISuggestionProduct suggestionProductsAPIWithSellerToken;
    APILocationReceipt allLocationReceiptWithSellerToken;

    public LocationReceiptPage getLoginInformation(LoginInformation sellLoginInformation, LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        this.sellLoginInformation = sellLoginInformation;
        suggestionProductsAPIWithSellerToken = new APISuggestionProduct(sellLoginInformation);
        allLocationReceiptWithSellerToken = new APILocationReceipt(sellLoginInformation);
        return this;
    }

    void navigateToCreateAddLocationReceiptPage() {
        driver.get("%s/location-receipt/add".formatted(DOMAIN));
        logger.info("Navigate to create add location receipt page by URL.");
    }

    void navigateToCreateGetLocationReceiptPage() {
        driver.get("%s/location-receipt/get".formatted(DOMAIN));
        logger.info("Navigate to create get location receipt page by URL.");
    }

    void navigateToAddLocationReceiptDetailPage(int receiptId) {
        driver.get("%s/location-receipt/add/%s".formatted(DOMAIN, receiptId));
        logger.info("Navigate to add location receipt detail page by URL, receiptId: %s.".formatted(receiptId));
    }

    void navigateToGetLocationReceiptDetailPage(int receiptId) {
        driver.get("%s/location-receipt/get/%s".formatted(DOMAIN, receiptId));
        logger.info("Navigate to get location receipt detail page by URL, receiptId: %s.".formatted(receiptId));
    }


    /*
    Using API to get data to create/edit location receipt
     */
    int addBranchId() {
        List<Integer> assignedBranchIds = staffLoginInfo.getAssignedBranchesIds();

        return assignedBranchIds.stream()
                .mapToInt(branchId -> branchId)
                .filter(branchId -> suggestionProductsAPIWithSellerToken.findProductInformationMatchesWithAddLocationReceipt(branchId)
                        .getItemId() != null)
                .findFirst()
                .orElse(0);
    }

    int getBranchId() {
        List<Integer> assignedBranchIds = staffLoginInfo.getAssignedBranchesIds();

        return assignedBranchIds.stream()
                .mapToInt(branchId -> branchId)
                .filter(branchId -> suggestionProductsAPIWithSellerToken.findProductInformationMatchesWithGetLocationReceipt(branchId)
                        .getItemId() != null)
                .findFirst()
                .orElse(0);
    }

    public SuggestionProductsInfo addProductInfo(int branchId) {
        // get product info
        return (branchId == 0) ? new SuggestionProductsInfo()
                : suggestionProductsAPIWithSellerToken.findProductInformationMatchesWithAddLocationReceipt(branchId);
    }

    SuggestionProductsInfo getProductInfo(int branchId) {
        // get product info
        return (branchId == 0) ? new SuggestionProductsInfo()
                : suggestionProductsAPIWithSellerToken.findProductInformationMatchesWithGetLocationReceipt(branchId);
    }

    ProductLotInfo addLotInfo(int branchId, SuggestionProductsInfo productsInfo) {
        // get lot
        return (branchId == 0) ? new ProductLotInfo()
                : new APILotDate(staffLoginInformation).getLotInStock(productsInfo.getItemId(),
                productsInfo.getModelId().isEmpty() ? "\"\"" : productsInfo.getModelId(),
                branchId,
                "ADD_PRODUCT_TO_LOCATION");
    }

    ProductLotInfo getLotInfo(int branchId, SuggestionProductsInfo productsInfo) {
        // get lot
        return (branchId == 0) ? new ProductLotInfo()
                : new APILotDate(staffLoginInformation).getLotInStock(productsInfo.getItemId(),
                productsInfo.getModelId().isEmpty() ? "\"\"" : productsInfo.getModelId(),
                branchId,
                "GET_PRODUCT_FROM_LOCATION");
    }

    ProductLocationInfo addLocationInfo(int branchId, SuggestionProductsInfo productsInfo) {
        // get location
        return (branchId == 0) ? new ProductLocationInfo()
                : new APILocation(staffLoginInformation).getLocation(productsInfo.getItemId(),
                productsInfo.getModelId().isEmpty() ? "\"\"" : productsInfo.getModelId(),
                branchId,
                "ADD_PRODUCT_TO_LOCATION");
    }

    ProductLocationInfo getLocationInfo(int branchId, SuggestionProductsInfo productsInfo) {
        // get location
        return (branchId == 0) ? new ProductLocationInfo()
                : new APILocation(staffLoginInformation).getLocationInStock(productsInfo.getItemId(),
                productsInfo.getModelId().isEmpty() ? "\"\"" : productsInfo.getModelId(),
                branchId,
                "GET_PRODUCT_FROM_LOCATION");
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    void selectBranch(int... branchIds) {
        // get branchId
        int branchId = (Arrays.stream(branchIds).sum() == 0)
                ? staffLoginInfo.getAssignedBranchesIds().get(0)
                : branchIds[0];

        // open branch dropdown
        commonAction.click(loc_ddvSelectedBranch);

        // get branch locator
        By loc_ddvBranchLocator = By.cssSelector(str_ddvBranch.formatted(branchId));

        // select branch
        commonAction.click(loc_ddvBranchLocator);

        // get selected branch name
        String branchName = commonAction.getText(loc_ddvBranchLocator);

        // log
        logger.info("Select branch: %s.".formatted(branchName));
    }

    void selectProduct(String barcode) {
        // remove old product
        if (!commonAction.getListElement(loc_icnRemove).isEmpty()) {
            int bound = commonAction.getListElement(loc_icnRemove).size();
            IntStream.iterate(bound - 1, index -> index >= 0, index -> index - 1)
                    .forEachOrdered(index -> commonAction.clickJS(loc_icnRemove, index));
        }

        // change search type = barcode
        new Select(commonAction.getElement(loc_ddvSearchType)).selectByValue("PRODUCT_BARCODE");

        // get product by barcode
        commonAction.sendKeys(loc_txtSearchProduct, barcode);

        // select product
        commonAction.click(By.xpath(str_ddvProduct.formatted(barcode)));

        // log
        logger.info("Select product with barcode: %s.".formatted(barcode));
    }

    void selectLot(String lotName) {
        if (!(commonAction.getListElement(loc_lnkSelectLot).isEmpty() || (lotName == null))) {
            // open select lot-date popup
            commonAction.openPopupJS(loc_lnkSelectLot, 0, loc_dlgSelectLotDate);

            // un-check hide expired lot
            if (!commonAction.isCheckedJS(loc_dlgSelectLotDate_chkHideExpiredLot)) {
                commonAction.clickJS(loc_dlgSelectLotDate_chkHideExpiredLot);
            }

            // search lot-date
            commonAction.sendKeys(loc_dlgSelectLotDate_txtSearchLot, lotName);

            // input lot quantity
            commonAction.getElement(loc_dlgSelectLotDate_txtQuantity, 0).sendKeys(String.valueOf(1));

            // completed select lot
            commonAction.closePopup(loc_dlgSelectLotDate_btnConfirm);

            // log
            logger.info("Select lot: %s.".formatted(lotName));
        }
    }

    void selectLocation(String locationName) {
        if (!(commonAction.getListElement(loc_lnkSelectLocation).isEmpty() || (locationName == null))) {
            // open select location popup
            commonAction.openPopupJS(loc_lnkSelectLocation, 0, loc_dlgSelectLocation);

            // input lot quantity
            commonAction.getElement(By.xpath(str_dlgSelectLocation_txtQuantity.formatted(locationName)), 0)
                    .sendKeys(String.valueOf(1));

            // completed select location
            commonAction.closePopup(loc_dlgSelectLocation_btnConfirm);
        }

        // log
        logger.info("Select location: %s.".formatted(locationName));
    }

    void saveAsDraft() {
        // click save as draft button
        commonAction.click(loc_btnSaveAsDraft);

        // check location is saved as a draft or not
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(),
                "Can not save location receipt as a draft.");

        // logger
        logger.info("Save location receipt as a draft.");
    }

    void completedLocationReceipt() {
        // click complete button
        commonAction.click(loc_btnComplete);

        // check location is saved as a completed or not
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(),
                "Can not save location receipt as a completed.");

        // logger
        logger.info("Save location receipt as a completed.");
    }

    public void saveAddReceiptAsADraft() {
        // select branch
        selectBranch();

        // save location receipt as a draft
        saveAsDraft();
    }

    public void saveAddReceiptAsACompleted() {
        // get branchId
        int branchId = addBranchId();

        // get product info
        SuggestionProductsInfo productsInfo = addProductInfo(branchId);

        if (productsInfo.getBarcode() != null) {
            // get lot info
            ProductLotInfo lotInfo = addLotInfo(branchId, productsInfo);

            // get location info
            ProductLocationInfo locationInfo = addLocationInfo(branchId, productsInfo);

            // select branch
            selectBranch(branchId);

            // select product
            selectProduct(productsInfo.getBarcode());

            // select lot
            selectLot(lotInfo.getLotName());

            // select location
            selectLocation(locationInfo.getLocationName());

            // save receipt as a draft
            completedLocationReceipt();
        } else logger.info("Can not find any product that matches add location receipt condition.");
    }

    public void saveGetReceiptAsADraft() {
        // select branch
        selectBranch();

        // save location receipt as a draft
        saveAsDraft();
    }

    public void saveGetReceiptAsACompleted() {
        // get branchId
        int branchId = getBranchId();

        // get product info
        SuggestionProductsInfo productsInfo = getProductInfo(branchId);

        if (productsInfo.getBarcode() != null) {

            // get lot info
            ProductLotInfo lotInfo = getLotInfo(branchId, productsInfo);

            // get location info
            ProductLocationInfo locationInfo = getLocationInfo(branchId, productsInfo);

            // select branch
            selectBranch(branchId);

            // select product
            selectProduct(productsInfo.getBarcode());

            // select lot
            selectLot(lotInfo.getLotName());

            // select location
            selectLocation(locationInfo.getLocationName());

            // save receipt as a draft
            completedLocationReceipt();
        } else logger.info("Can not find any product that matches get location receipt condition.");
    }

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-24810
    AllPermissions permissions;
    CheckPermission checkPermission;

    public void checkLocationReceiptPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // check view add product location receipt detail
        checkViewAddProductLocationReceiptDetail();

        // check view get product location receipt detail
        checkViewGetProductLocationReceiptDetail();

        // check create add product receipt
        checkCreateDraftAddProductReceipt();

        // check create completed add product receipt
        checkCreateCompletedAddProductReceipt();

        // check create get product receipt
        checkCreateDraftGetProductReceipt();

        // check create completed get product receipt
        checkCreateCompletedGetProductReceipt();
    }

    void checkViewAddProductLocationReceiptDetail() {
        List<Integer> getListAddLocationReceipt = allLocationReceiptWithSellerToken.getListAddProductToLocation(staffLoginInfo.getAssignedBranchesNames());
        if (!getListAddLocationReceipt.isEmpty()) {
            int addReceiptId = getListAddLocationReceipt.get(0);
            if (permissions.getProduct().getLocationReceipt().isViewAddProductLocationReceiptDetail()) {
                // check can access to location receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s%s".formatted(DOMAIN, "/location-receipt/add/%s".formatted(addReceiptId)),
                                String.valueOf(addReceiptId)),
                        "Can not access to add location receipt detail page, receiptId: %s.".formatted(addReceiptId));

                // check delete add receipt
                checkDeleteDraftAddProductReceipt();

                // check edit add receipt
                checkEditAddProductReceipt();

                // check complete add receipt
                checkCompleteAddProductReceipt();
            } else {
                // if staff don’t have permission “View add product location receipt detail”
                // => show restricted page
                // when access Add receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s%s".formatted(DOMAIN, "/location-receipt/add/%s".formatted(addReceiptId))),
                        "Restricted page is not shown.");
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> View add product location receipt detail.");
    }

    void checkViewGetProductLocationReceiptDetail() {
        List<Integer> getListGetLocationReceipt = allLocationReceiptWithSellerToken.getListGetProductFromLocation(staffLoginInfo.getAssignedBranchesNames());
        if (!getListGetLocationReceipt.isEmpty()) {
            int getReceiptId = getListGetLocationReceipt.get(0);
            if (permissions.getProduct().getLocationReceipt().isViewGetProductLocationReceiptDetail()) {
                // check can access to location receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s%s".formatted(DOMAIN, "/location-receipt/get/%s".formatted(getReceiptId)),
                                String.valueOf(getReceiptId)),
                        "Can not access to get location receipt detail page, receiptId: %s.".formatted(getReceiptId));

                // check delete get receipt
                checkDeleteDraftGetProductReceipt();

                // check edit get receipt
                checkEditGetProductReceipt();

                // check complete get receipt
                checkCompleteGetProductReceipt();
            } else {
                // if staff don’t have permission “View get product location detail”
                // => show restricted page
                // when access Get receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s%s".formatted(DOMAIN, "/location-receipt/get/%s".formatted(getReceiptId))),
                        "Restricted page is not shown.");
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> View get location receipt detail.");
    }

    void checkCreateDraftAddProductReceipt() {
        // navigate to add location receipt page
        navigateToCreateAddLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateDraftAddProductReceipt()) {
            // check can add draft location receipt
            saveAddReceiptAsADraft();
        } else {
            // if staff don’t have permission “Create draft add product receipt”
            // => show restricted popup
            // when click [Save] a draft add product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSaveAsDraft),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Create draft add product receipt.");
    }

    void checkCreateCompletedAddProductReceipt() {
        // navigate to add location receipt page
        navigateToCreateAddLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateCompletedAddProductReceipt()) {
            // check can add completed location receipt
            saveAddReceiptAsACompleted();

        } else {
            // if staff don’t have permission “Create completed add product receipt”
            // => show restricted popup
            // when click [Save] a completed add product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Create completed add product receipt.");
    }

    void checkCreateDraftGetProductReceipt() {
        // navigate to get location receipt page
        navigateToCreateGetLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateDraftGetProductReceipt()) {
            // check can create draft get location receipt
            saveGetReceiptAsADraft();
        } else {
            // if staff don’t have permission “Create draft get product receipt”
            // => show restricted popup
            // when click [Save] a draft get product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSaveAsDraft),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Create draft get product receipt.");
    }

    void checkCreateCompletedGetProductReceipt() {
        // navigate to get location receipt page
        navigateToCreateGetLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateCompletedGetProductReceipt()) {
            // check can create completed get location receipt
            saveGetReceiptAsACompleted();

        } else {
            // if staff don’t have permission “Create completed get product receipt”
            // => show restricted popup
            // when click [Save] a completed add product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Create complete get product receipt.");
    }

    void checkDeleteDraftAddProductReceipt() {
        // get draft add location receipt
        int receiptId = allLocationReceiptWithSellerToken.getDraftAddLocationReceiptId(staffLoginInfo.getAssignedBranchesNames());

        if (receiptId != 0) {
            // navigate to location receipt detail page
            navigateToAddLocationReceiptDetailPage(receiptId);

            // open confirm delete draft add receipt
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnDelete, loc_dlgConfirm),
                    "Can not open confirm delete location popup.");

            // check permission
            if (commonAction.getListElement(loc_dlgConfirm).isEmpty()) {
                if (permissions.getProduct().getLocationReceipt().isDeleteDraftAddProductReceipt()) {
                    // check can delete draft add location receipt
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirm_BtnYes, loc_dlgToastSuccess),
                            "Can not delete draft add location receipt.");
                } else {
                    // if staff don’t have permission “Delete draft get product receipt”
                    // => show restricted popup
                    // when click [Yes] on popup confirm delete draft get receipt
                    assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgConfirm_BtnYes),
                            "Restricted popup is not shown.");
                }
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Delete draft add product receipt.");
    }

    void checkDeleteDraftGetProductReceipt() {
        // get draft add location receipt
        int receiptId = allLocationReceiptWithSellerToken.getDraftGetLocationReceiptId(staffLoginInfo.getAssignedBranchesNames());

        if (receiptId != 0) {
            // navigate to location receipt detail page
            navigateToGetLocationReceiptDetailPage(receiptId);

            // open confirm delete draft get receipt
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnDelete, loc_dlgConfirm),
                    "Can not open confirm delete location popup.");

            // check permission
            if (commonAction.getListElement(loc_dlgConfirm).isEmpty()) {
                if (permissions.getProduct().getLocationReceipt().isDeleteDraftGetProductReceipt()) {
                    // check can delete draft get location receipt
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirm_BtnYes, loc_dlgToastSuccess),
                            "Can not delete draft add location receipt.");
                } else {
                    //
                    assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgConfirm_BtnYes),
                            "Restricted popup is not shown.");
                }
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Delete draft get product receipt.");
    }

    void checkEditAddProductReceipt() {
        // get draft add location receipt
        int receiptId = allLocationReceiptWithSellerToken.getDraftAddLocationReceiptId(staffLoginInfo.getAssignedBranchesNames());

        if (receiptId != 0) {
            // navigate to location receipt detail page
            navigateToAddLocationReceiptDetailPage(receiptId);

            // check permission
            if (permissions.getProduct().getLocationReceipt().isEditAddProductReceipt()) {
                // check can edit draft add location receipt
                saveGetReceiptAsADraft();

            } else {
                // if staff don’t have permission “Edit add product receipt”
                // => show restricted popup
                // when click [Save] in detail page of receipt add product
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSaveAsDraft),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Edit add product receipt.");
    }

    void checkEditGetProductReceipt() {
        // get draft get location receipt
        int receiptId = allLocationReceiptWithSellerToken.getDraftGetLocationReceiptId(staffLoginInfo.getAssignedBranchesNames());

        if (receiptId != 0) {
            // navigate to location receipt detail page
            navigateToGetLocationReceiptDetailPage(receiptId);

            // check permission
            if (permissions.getProduct().getLocationReceipt().isEditGetProductReceipt()) {
                // check can edit draft get location receipt
                saveGetReceiptAsADraft();

            } else {
                // if staff don’t have permission “Edit get product receipt”
                // => show restricted popup
                // when click [Save] in detail page of receipt get product
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSaveAsDraft),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Edit get product receipt.");
    }

    void checkCompleteAddProductReceipt() {
        // get draft add location receipt
        int receiptId = allLocationReceiptWithSellerToken.getDraftAddLocationReceiptId(staffLoginInfo.getAssignedBranchesNames());

        if (receiptId != 0) {
            // navigate to location receipt detail page
            navigateToAddLocationReceiptDetailPage(receiptId);

            // check permission
            if (permissions.getProduct().getLocationReceipt().isCompleteAddProductReceipt()) {
                // check can completed add location receipt
                saveAddReceiptAsACompleted();

            } else {
                // if staff don’t have permission “Complete add product receipt”
                // => show restricted popup
                // when click [Complete] in detail page of receipt add product
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Complete add product receipt.");
    }

    void checkCompleteGetProductReceipt() {
        // get draft get location receipt
        int receiptId = allLocationReceiptWithSellerToken.getDraftGetLocationReceiptId(staffLoginInfo.getAssignedBranchesNames());

        if (receiptId != 0) {
            // navigate to location receipt detail page
            navigateToGetLocationReceiptDetailPage(receiptId);

            // check permission
            if (permissions.getProduct().getLocationReceipt().isCompleteGetProductReceipt()) {
                // check can completed get location receipt
                saveGetReceiptAsACompleted();

            } else {
                // if staff don’t have permission “Complete get product receipt”
                // => show restricted popup
                // when click [Complete] in detail page of receipt get product
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Product >> Location receipt >> Complete get product receipt.");
    }
}
