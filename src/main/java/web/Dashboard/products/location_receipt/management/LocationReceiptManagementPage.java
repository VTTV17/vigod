package web.Dashboard.products.location_receipt.management;

import api.Seller.login.Login;
import api.Seller.products.location_receipt.APILocationReceipt;
import api.Seller.products.location_receipt.APILocationReceipt.AllLocationReceiptInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.location_receipt.crud.LocationReceiptPage;

import java.util.ArrayList;
import java.util.List;

public class LocationReceiptManagementPage extends LocationReceiptManagementElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    Logger logger = LogManager.getLogger(LocationReceiptManagementPage.class);

    public LocationReceiptManagementPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
    }

    /*-------------------------------------*/
    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-24810
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;

    public LocationReceiptManagementPage getLoginInformation(LoginInformation sellerLoginInformation,
                                                             LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        this.sellerLoginInformation = sellerLoginInformation;
        return this;
    }

    public void checkLocationReceiptPermission(AllPermissions permissions) {
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
    }

    void checkViewProductLocationReceiptList() {
        // get staff login info
        LoginDashboardInfo staffLoginInfo = new Login().getInfo(staffLoginInformation);

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

    void checkImportProductToLocation() {

    }
}
