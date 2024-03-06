package web.Dashboard.products.location_receipt.crud;

import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APIAllProducts.SuggestionProductsInfo;
import api.Seller.products.location.APILocation;
import api.Seller.products.location_receipt.APILocationReceipt;
import api.Seller.products.lot_date.APILotDate;
import api.Seller.products.lot_date.APILotDate.ProductLotInfo;
import api.Seller.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
    APIAllProducts allProductsAPIWithSellerToken;
    APILocationReceipt allLocationReceiptWithSellerToken;

    public LocationReceiptPage getLoginInformation(LoginInformation sellLoginInformation, LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        this.sellLoginInformation = sellLoginInformation;
        allProductsAPIWithSellerToken = new APIAllProducts(sellLoginInformation);
        allLocationReceiptWithSellerToken = new APILocationReceipt(sellLoginInformation);
        return this;
    }

    void navigateToAddLocationReceiptPage() {
        driver.get("%s/location-receipt/add".formatted(DOMAIN));
        logger.info("Navigate to add location receipt by URL.");
    }

    void navigateToGetLocationReceiptPage() {
        driver.get("%s/location-receipt/get".formatted(DOMAIN));
        logger.info("Navigate to get location receipt by URL.");
    }

    /*
    Using API to get data to create/edit location receipt
     */
    int addBranchId() {
        List<Integer> assignedBranchIds = new BranchManagement(staffLoginInformation).getInfo().getBranchID();

        return assignedBranchIds.stream()
                .mapToInt(branchId -> branchId)
                .filter(branchId -> allProductsAPIWithSellerToken.findProductInformationMatchesWithAddLocationReceipt(branchId)
                        .getItemId() != null)
                .findFirst()
                .orElse(0);
    }

    int getBranchId() {
        List<Integer> assignedBranchIds = new BranchManagement(staffLoginInformation).getInfo().getBranchID();

        return assignedBranchIds.stream()
                .mapToInt(branchId -> branchId)
                .filter(branchId -> allProductsAPIWithSellerToken.findProductInformationMatchesWithGetLocationReceipt(branchId)
                        .getItemId() != null)
                .findFirst()
                .orElse(0);
    }

    SuggestionProductsInfo addProductInfo(int branchId) {
        // get product info
        return (branchId == 0) ? new SuggestionProductsInfo()
                : allProductsAPIWithSellerToken.findProductInformationMatchesWithAddLocationReceipt(branchId);
    }

    SuggestionProductsInfo getProductInfo(int branchId) {
        // get product info
        return (branchId == 0) ? new SuggestionProductsInfo()
                : allProductsAPIWithSellerToken.findProductInformationMatchesWithGetLocationReceipt(branchId);
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
        // open branch dropdown
        commonAction.click(loc_ddvSelectedBranch);

        // init select options
        if (Arrays.stream(branchIds).sum() == 0) {
            // select branch
            commonAction.click(loc_ddvSelectedBranch);
            commonAction.click(loc_ddvBranch, 1);

            // get selected branch
            String branchName = commonAction.getText(loc_ddvBranch, 1);

            // log
            logger.info("Select branch: %s.".formatted(branchName));
        } else {
            // select branch
            new Select(commonAction.getElement(loc_ddvSelectedBranch))
                    .selectByValue(String.valueOf(branchIds[0]));

            // get selected branch
            String branchName = commonAction.getText(By.cssSelector(str_ddvBranch.formatted(branchIds[0])));

            // log
            logger.info("Select branch: %s.".formatted(branchName));
        }
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

    public void createDraftAddLocationReceipt() {
        // select branch
        selectBranch();

        // save location receipt as a draft
        saveAsDraft();
    }

    public void createCompletedAddLocationReceipt() {
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

    public void createDraftGetLocationReceipt() {
        // select branch
        selectBranch();

        // save location receipt as a draft
        saveAsDraft();
    }

    public void createCompletedGetLocationReceipt() {
        // navigate to get location receipt page
        navigateToGetLocationReceiptPage();

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
    // https://mediastep.atlassian.net/browse/BH-24808
    AllPermissions permissions;
    CheckPermission checkPermission;

    void checkViewAddProductLocationReceiptDetail() {
        List<Integer> getListAddLocationReceipt = allLocationReceiptWithSellerToken.getListAddProductToLocation();
        if (!getListAddLocationReceipt.isEmpty()) {
            int addReceiptId = getListAddLocationReceipt.get(0);
            if (permissions.getProduct().getLocationReceipt().isViewAddProductLocationReceiptDetail()) {
                // check can access to location receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s%s".formatted(DOMAIN, "/location-receipt/add/%s".formatted(addReceiptId)),
                                String.valueOf(addReceiptId)),
                        "Can not access to location receipt detail page.");
            } else {
                // if staff don’t have permission “View add product location receipt detail”
                // => show restricted page
                // when access Add receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s%s".formatted(DOMAIN, "/location-receipt/add/%s".formatted(addReceiptId)),
                                String.valueOf(addReceiptId)),
                        "Restricted page is not shown.");
            }
        }
    }

    void checkViewGetProductLocationReceiptDetail() {
        List<Integer> getListGetLocationReceipt = allLocationReceiptWithSellerToken.getListGetProductFromLocation();
        if (!getListGetLocationReceipt.isEmpty()) {
            int getReceiptId = getListGetLocationReceipt.get(0);
            if (permissions.getProduct().getLocationReceipt().isViewGetProductLocationReceiptDetail()) {
                // check can access to location receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s%s".formatted(DOMAIN, "/location-receipt/add/%s".formatted(getReceiptId)),
                                String.valueOf(getReceiptId)),
                        "Can not access to location receipt detail page.");
            } else {
                // if staff don’t have permission “View get product location detail”
                // => show restricted page
                // when access Get receipt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s%s".formatted(DOMAIN, "/location-receipt/add/%s".formatted(getReceiptId))),
                        "Restricted page is not shown.");
            }
        }
    }

    void checkCreateDraftAddProductReceipt() {
        // navigate to add location receipt page
        navigateToAddLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateDraftAddProductReceipt()) {
            // check can add draft location receipt
            createDraftAddLocationReceipt();
        } else {
            // if staff don’t have permission “Create draft add product receipt”
            // => show restricted popup
            // when click [Save] a draft add product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSaveAsDraft),
                    "Restricted popup is not shown.");
        }
    }

    void checkCreateCompletedAddProductReceipt() {
        // navigate to add location receipt page
        navigateToAddLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateCompletedAddProductReceipt()) {
            // check can add completed location receipt
            createCompletedAddLocationReceipt();

        } else {
            // if staff don’t have permission “Create completed add product receipt”
            // => show restricted popup
            // when click [Save] a completed add product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                    "Restricted popup is not shown.");
        }
    }

    void checkCreateDraftGetProductReceipt() {
        // navigate to get location receipt page
        navigateToGetLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateDraftGetProductReceipt()) {
            // check can create draft get location receipt
            createDraftGetLocationReceipt();
        } else {
            // if staff don’t have permission “Create draft get product receipt”
            // => show restricted popup
            // when click [Save] a draft get product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSaveAsDraft),
                    "Restricted popup is not shown.");
        }
    }

    void checkCreateCompletedGetProductReceipt() {
        // navigate to get location receipt page
        navigateToGetLocationReceiptPage();

        // check permission
        if (permissions.getProduct().getLocationReceipt().isCreateCompletedGetProductReceipt()) {
            // check can create completed get location receipt
            createCompletedGetLocationReceipt();

        } else {
            // if staff don’t have permission “Create completed get product receipt”
            // => show restricted popup
            // when click [Save] a completed add product receipt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                    "Restricted popup is not shown.");
        }
    }

    void checkDeleteDraftAddProductReceipt() {

    }

    void checkDeleteDraftGetProductReceipt() {

    }

    void checkEditAddProductReceipt() {

    }

    void checkEditGetProductReceipt() {

    }

    void checkCompleteAddProductReceipt() {

    }

    void checkCompleteGetProductReceipt() {

    }
}
