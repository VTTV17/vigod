package web.Dashboard.orders.delivery.delivery_detail;

import api.Seller.login.Login;
import api.Seller.orders.delivery.APIDeliveryManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.time.Instant;

import static api.Seller.orders.delivery.APIDeliveryManagement.DeliveryPackageInformation;
import static utilities.links.Links.DOMAIN;

public class DeliveryDetailPage extends DeliveryDetailElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(DeliveryDetailPage.class);

    public DeliveryDetailPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-24815
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginDashboardInfo staffLoginInfo;
    APIDeliveryManagement apiDeliveryManagementWithSellerToken;
    APIDeliveryManagement apiDeliveryManagementWithStaffToken;

    public DeliveryDetailPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    void navigateToDeliveryPackageDetailPageByURL(long orderId, int deliveryId) {
        driver.get("%s/order/delivery/detail/%s/%s".formatted(DOMAIN, orderId, deliveryId));
        driver.navigate().refresh();
        logger.info("Navigate to delivery package detail page by URL, orderId: %s, deliveryId: %s.".formatted(orderId, deliveryId));
    }

    public DeliveryDetailPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiDeliveryManagementWithSellerToken = new APIDeliveryManagement(sellerLoginInformation);
        apiDeliveryManagementWithStaffToken = new APIDeliveryManagement(staffLoginInformation);
        return this;
    }

    public void checkDeliveryPermission() {
        DeliveryPackageInformation info = apiDeliveryManagementWithSellerToken.getDeliveryForViewDetail(staffLoginInfo.getAssignedBranchesIds());
        if (info.getDeliveryId() != 0) {
            if (permissions.getOrders().getDelivery().isViewDeliveryPackageDetail()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/order/delivery/detail/%s/%s".formatted(DOMAIN, info.getOrderId(), info.getDeliveryId()),
                                String.valueOf(info.getDeliveryId())),
                        "Can not access to delivery package detail page by URL, orderId: %s, deliveryId: %s.".formatted(info.getOrderId(), info.getDeliveryId()));

                // check update status
                checkUpdatePackageStatusWith3rdParty();
                checkUpdatePackageStatusWithSelfDeliveryMethod();

                // check print package slip
                checkPrintPackageSlip(info);
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/order/delivery/detail/%s/%s".formatted(DOMAIN, info.getOrderId(), info.getDeliveryId())),
                        "Restricted page is not shown.");
            }
        } else logger.warn("Can not find delivery for check view detail.");

        logger.info("Check permission: Orders >> Delivery >> View delivery detail.");
    }

    void checkUpdatePackageStatusWithSelfDeliveryMethod() {
        DeliveryPackageInformation info = apiDeliveryManagementWithSellerToken.getDeliveryForUpdateStatus(staffLoginInfo.getAssignedBranchesIds());
        if (info.getDeliveryId() != 0) {
            // navigate to delivery package detail page
            navigateToDeliveryPackageDetailPageByURL(info.getOrderId(), info.getDeliveryId());

            // click update status button
            commonAction.click(loc_btnUpdateStatus);

            if (permissions.getOrders().getDelivery().isUpdatePackageStatus()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgUpdateStatus).isEmpty(), "Can not open update delivery status popup.");

                if (!commonAction.getListElement(loc_dlgUpdateStatus).isEmpty()) {
                    // open package status dropdown
                    commonAction.click(loc_dlgUpdateStatus_ddlStatus);

                    // select status
                    commonAction.clickJS(loc_dlgUpdateStatus_ddvStatus, 1);

                    // save changes
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgUpdateStatus_btnConfirm,loc_dlgToastSuccess),
                            "Can not update delivery package status.");
                }
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
            }
        } else logger.warn("Can not find delivery for check update package status.");

        logger.info("Check permission: Orders >> Delivery >> Update package status with self-delivery/others method.");
    }

    void checkUpdatePackageStatusWith3rdParty() {
        DeliveryPackageInformation info = apiDeliveryManagementWithSellerToken.getDeliveryForCancel(staffLoginInfo.getAssignedBranchesIds());
        if (info.getDeliveryId() != 0) {
            // navigate to delivery package detail page
            navigateToDeliveryPackageDetailPageByURL(info.getOrderId(), info.getDeliveryId());

            // click cancel delivery button
            commonAction.click(loc_btnCancelDelivery);

            if (permissions.getOrders().getDelivery().isUpdatePackageStatus()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgCancelDelivery).isEmpty(), "Can not open cancel delivery popup.");

                if (!commonAction.getListElement(loc_dlgCancelDelivery).isEmpty()) {
                    // select status
                    commonAction.sendKeys(loc_dlgCancelDelivery_txtNote, "Cancel reason %s.".formatted(Instant.now()));

                    // save changes
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgCancelDelivery_btnYes,loc_dlgToastSuccess),
                            "Can not update delivery package status.");
                }
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
            }
        } else logger.warn("Can not find delivery for check cancel delivery.");

        logger.info("Check permission: Orders >> Delivery >> Update package status with 3rd party.");
    }

    void checkPrintPackageSlip(DeliveryPackageInformation info) {
        // navigate to delivery package detail page
        navigateToDeliveryPackageDetailPageByURL(info.getOrderId(), info.getDeliveryId());

        // click print package slip button
        commonAction.click(loc_btnPrintPackageSlip);

        // check print package slip permission
        if (permissions.getOrders().getOrderManagement().isPrintOrderSlip()) {
            if (commonAction.getAllWindowHandles().size() > 1) {
                // switch to print tab
                commonAction.switchToWindow(1);

                // check print package slip tab is shown
                assertCustomize.assertTrue(driver.getCurrentUrl().contains("blob:"),
                        "Can not print package slip.");

                // close print tab
                driver.close();
            }

            // back to delivery package detail tab
            commonAction.switchToWindow(0);
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                    "Restricted popup is not shown.");
        }

        logger.info("Check permission: Orders >> Delivery >> Print package slip.");
    }
}
