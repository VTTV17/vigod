package web.Dashboard.orders.delivery.delivery_management;

import api.Seller.login.Login;
import api.Seller.orders.delivery.APIDeliveryManagement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.orders.delivery.delivery_detail.DeliveryDetailPage;

import java.util.List;

import static utilities.links.Links.DOMAIN;

public class DeliveryManagementPage extends DeliveryManagementElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(DeliveryManagementPage.class);
    public DeliveryManagementPage(WebDriver driver) {
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
    public DeliveryManagementPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public DeliveryManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiDeliveryManagementWithSellerToken = new APIDeliveryManagement(sellerLoginInformation);
        apiDeliveryManagementWithStaffToken = new APIDeliveryManagement(staffLoginInformation);
        return this;
    }

    public void checkDeliveryPermission() {
        // check view delivery list
        checkViewDeliveryList();

        // check view delivery detail
        new DeliveryDetailPage(driver, permissions)
                .getLoginInformation(sellerLoginInformation, staffLoginInformation)
                .checkDeliveryPermission();
    }

    void checkViewDeliveryList() {
        List<Integer> listOfDeliveryIdWithStaffToken = apiDeliveryManagementWithStaffToken.getAllDeliveryInformation().getDeliveryIds();
        if (permissions.getOrders().getDelivery().isViewDeliveryPackageList()) {
            // check API
            List<Integer> listOfDeliveryIdWithSellerToken = apiDeliveryManagementWithSellerToken.getListDeliveryIdAfterFilterByBranches(staffLoginInfo.getAssignedBranchesIds());
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(listOfDeliveryIdWithStaffToken, listOfDeliveryIdWithStaffToken),
                    "Delivery package list must be %s, but found %s.".formatted(listOfDeliveryIdWithSellerToken, listOfDeliveryIdWithStaffToken));

            // check UI
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/order/delivery".formatted(DOMAIN), "order/delivery"),
                    "Can not access to delivery package management page by URL");
        } else {
            // check API
            assertCustomize.assertTrue(listOfDeliveryIdWithStaffToken.isEmpty(),
                    "Delivery package list must be empty, but found %s.".formatted(listOfDeliveryIdWithStaffToken));

            // check UI
            if (permissions.getOrders().getDelivery().toString().contains("true")) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/order/delivery".formatted(DOMAIN), "order/delivery"),
                        "Can not access to delivery package management page by URL");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/order/delivery".formatted(DOMAIN)),
                        "Restricted page is not shown.");
            }
        }

        // log
        logger.info("Check permission: Orders >> Delivery >> View delivery package list.");
    }
}