package web.Dashboard.supplier.purchaseorders.management;

import api.Seller.login.Login;
import api.Seller.setting.StaffManagement;
import api.Seller.supplier.purchase_orders.APIPurchaseOrders;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.supplier.purchaseorders.crud.PurchaseOrderPage;

import java.util.List;

import static utilities.links.Links.DOMAIN;

public class PurchaseOrderManagementPage extends PurchaseOrderManagementElement {
    WebDriver driver;
    UICommonAction commons;
    AssertCustomize assertCustomize;

    final static Logger logger = LogManager.getLogger(PurchaseOrderManagementPage.class);

    public PurchaseOrderManagementPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public PurchaseOrderManagementPage inputSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearch, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        new HomePage(driver).waitTillSpinnerDisappear();
        return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToManagePurchaseOrders(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commons.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-13850
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    PurchaseOrderPage purchaseOrderPage;
    LoginDashboardInfo staffLoginInfo;

    public PurchaseOrderManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    public void checkPurchaseOrderPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init crud purchase order POM
        purchaseOrderPage = new PurchaseOrderPage(driver);

        // check view purchase order list
        checkViewPurchaseOrderList();

        // check crud permission
        purchaseOrderPage.getLoginInformation(sellerLoginInformation, staffLoginInformation)
                .checkPurchaseOrderPermission(permissions);
    }

    void checkViewPurchaseOrderList() {
        List<Integer> listPurchaseIdWithStaffToken = new APIPurchaseOrders(staffLoginInformation).getAllPurchaseOrdersInformation().getIds();
        if (permissions.getSuppliers().getPurchaseOrder().isViewPurchaseOrderList()) {
            // staff can see list all purchase order
            // that has Original branch in assigned branch list
            // if they have permission “View purchase order list”
            List<Integer> listPurchaseIdWithSellerToken = new APIPurchaseOrders(sellerLoginInformation)
                    .getListPurchaseOrderMatchWithCondition(staffLoginInfo.getAssignedBranchesNames());

            // check purchase order list
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(listPurchaseIdWithSellerToken, listPurchaseIdWithStaffToken),
                    "List purchase order must be %s, but found %s.".formatted(listPurchaseIdWithSellerToken.toString(), listPurchaseIdWithStaffToken.toString()));
        } else if (permissions.getSuppliers().getPurchaseOrder().isViewListCreatedPurchaseOrder()) {
            // get staff name
            String staffName = new StaffManagement(sellerLoginInformation).getStaffName(staffLoginInfo.getUserId());

            // staff can see list purchase order
            // that has Original branch in assigned branch list and created by staff only
            // if they have permission “View created purchase order list”
            List<Integer> listPurchaseIdWithSellerToken = new APIPurchaseOrders(sellerLoginInformation)
                    .getListPurchaseOrderMatchWithCondition(staffLoginInfo.getAssignedBranchesNames(), staffName);

            // check created purchase order list
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(listPurchaseIdWithSellerToken, listPurchaseIdWithStaffToken),
                    "List purchase order must be %s, but found %s.".formatted(listPurchaseIdWithSellerToken.toString(), listPurchaseIdWithStaffToken.toString()));
        } else {
            // show empty list in Purchase Order
            // if staff don’t have permission “View purchaser order list”
            // and “View created purchase order list”
            assertCustomize.assertTrue(listPurchaseIdWithStaffToken.isEmpty(),
                    "All purchase orders must be hidden, but found: %s.".formatted(listPurchaseIdWithStaffToken.toString()));
        }

        // check can access to purchase order management page by URL
        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/product/purchase-order/list".formatted(DOMAIN),
                        "/product/purchase-order/list"),
                "Can not access to purchase order management page by URL.");
        logger.info("Check permission: Supplier >> Purchase order >> View purchase order list.");
        logger.info("Check permission: Supplier >> Purchase order >> View created purchase order list.");

    }
}