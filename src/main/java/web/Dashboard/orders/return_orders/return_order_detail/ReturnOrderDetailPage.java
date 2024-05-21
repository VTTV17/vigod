package web.Dashboard.orders.return_orders.return_order_detail;

import api.Seller.login.Login;
import api.Seller.orders.return_order.APIAllReturnOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.orders.return_orders.edit_return_order.EditReturnOrderPage;

import static utilities.links.Links.DOMAIN;

public class ReturnOrderDetailPage extends ReturnOrderDetailElement {
    WebDriver driver;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(ReturnOrderDetailPage.class);

    public ReturnOrderDetailPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    void navigateToReturnOrderDetailPage(int returnOrderId) {
        driver.get("%s/order/return-order/wizard/%s".formatted(DOMAIN, returnOrderId));
        driver.navigate().refresh();

        logger.info("Navigate to return order detail page by URL, returnOrderId: %s.".formatted(returnOrderId));
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-24812
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo staffLoginInfo;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    APIAllReturnOrder apiAllReturnOrdersWithSellerToken;
    APIAllReturnOrder apiAllReturnOrdersWithStaffToken;

    public ReturnOrderDetailPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public ReturnOrderDetailPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    public void checkReturnOrdersPermission() {
        // init api get all return order with seller token
        apiAllReturnOrdersWithSellerToken = new APIAllReturnOrder(sellerLoginInformation);

        // init api get all return order with staff token
        apiAllReturnOrdersWithStaffToken = new APIAllReturnOrder(staffLoginInformation);

        // check view return order detail
        checkViewReturnOrderDetail();
    }

    void checkViewReturnOrderDetail() {
        int returnOrderId = apiAllReturnOrdersWithSellerToken.getReturnOrderIdForViewDetail(staffLoginInfo.getAssignedBranchesIds());

        if (returnOrderId != 0) {
            // navigate to return order detail page
            navigateToReturnOrderDetailPage(returnOrderId);

            if (permissions.getOrders().getReturnOrder().isViewOrderReturnDetail()) {
                assertCustomize.assertTrue(driver.getCurrentUrl().contains("/order/return-order/wizard/"),
                        "Can not access to return order detail page.");

                checkCompleteReturnOrder();
                checkCancelReturnOrder();
                checkConfirmPayment();
                checkEditReturnOrder();
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted page is not shown.");
            }
        }
        logger.info("Check permission: Orders >> Return order >> Check view return order detail.");
    }

    void checkCompleteReturnOrder() {
        int returnOrderId = apiAllReturnOrdersWithSellerToken.getReturnOrderIdForCompleted(staffLoginInfo.getAssignedBranchesIds());

        if (returnOrderId != 0) {
            // navigate to edit return order page
            navigateToReturnOrderDetailPage(returnOrderId);

            // check completed return order permission
            if (permissions.getOrders().getReturnOrder().isCompleteReturnOrder()) {
                commonAction.click(loc_btnComplete);

                if (commonAction.getListElement(loc_dlgToastSuccess).isEmpty()) {
                    assertCustomize.assertFalse(commonAction.getListElement(loc_dlgConfirmComplete).isEmpty(),
                            "Can not open Confirm complete return order popup.");

                    if (!commonAction.getListElement(loc_dlgConfirmComplete).isEmpty()) {
                        if (permissions.getOrders().getOrderManagement().isViewOrderDetail()) {
                            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmComplete_btnOK, loc_dlgToastSuccess),
                                    "Can not complete return order.");
                        } else {
                            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmComplete_btnOK, loc_dlgToastError),
                                    "Error toast does not shown.");
                        }
                    }
                }
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnComplete),
                        "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Orders >> Return order >> Check completed return order.");
    }

    void checkCancelReturnOrder() {
        int returnOrderId = apiAllReturnOrdersWithSellerToken.getReturnOrderIdForCancel(staffLoginInfo.getAssignedBranchesIds());

        if (returnOrderId != 0) {
            // navigate to edit return order page
            navigateToReturnOrderDetailPage(returnOrderId);

            // open list actions
            commonAction.clickJS(loc_lnkSelectActions);

            // check cancel return order permission
            if (permissions.getOrders().getReturnOrder().isCancelReturnOrder()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddvCancelActions, loc_dlgToastSuccess),
                        "Can not cancel return order.");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddvCancelActions),
                        "Restricted popup is not shown.");
            }
        }

        logger.info("Check permission: Orders >> Return order >> Check cancel return order.");
    }

    void checkConfirmPayment() {
        int returnOrderId = apiAllReturnOrdersWithSellerToken.getReturnOrderIdForConfirmPayment(staffLoginInfo.getAssignedBranchesIds());

        if (returnOrderId != 0) {
            // navigate to edit return order page
            navigateToReturnOrderDetailPage(returnOrderId);

            // check confirm payment permission
            if (permissions.getOrders().getReturnOrder().isConfirmPayment()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(btnConfirmPayment, loc_dlgConfirmPayment),
                        "Can not open Confirm payment popup.");

                if (!commonAction.getListElement(loc_dlgConfirmPayment).isEmpty()) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmPayment_btnAdd, loc_dlgToastSuccess),
                            "Can not confirm payment.");
                }
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(btnConfirmPayment),
                        "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Orders >> Return order >> Check confirm payment.");
    }

    void checkEditReturnOrder() {
        int returnOrderId = apiAllReturnOrdersWithSellerToken.getReturnOrderIdForEdit(staffLoginInfo.getAssignedBranchesIds());

        if (returnOrderId != 0) {
            // navigate to edit return order page
            navigateToReturnOrderDetailPage(returnOrderId);

            // open list actions
            commonAction.click(loc_lnkSelectActions);

            // check cancel return order permission
            if (permissions.getOrders().getReturnOrder().isEditReturnOrder()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddvEditActions, "/order/return-order/edit/"),
                        "Can not access to edit return order page.");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddvEditActions),
                        "Restricted page is not shown.");
            }

            // check permission when access by URL
            new EditReturnOrderPage(driver, permissions).checkEditReturnOrder(returnOrderId);
        }
        logger.info("Check permission: Orders >> Return order >> Check edit return order.");
    }
}
