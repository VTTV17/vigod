package web.Dashboard.orders.return_orders.return_order_management;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.orders.return_order.APIAllReturnOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.orders.return_orders.create_return_order.CreateReturnOrderPage;
import web.Dashboard.orders.return_orders.return_order_detail.ReturnOrderDetailPage;

import java.util.List;

import static utilities.links.Links.DOMAIN;

public class ReturnOrdersManagementPage extends ReturnOrdersManagementElement {

    final static Logger logger = LogManager.getLogger(ReturnOrdersManagementPage.class);

    WebDriver driver;
    UICommonAction commonAction;

    public ReturnOrdersManagementPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    public boolean isDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.getElements(loc_dlgSelectOrderToReturn).isEmpty();
    }

    public void closeDialog() {
        commonAction.click(loc_btnCloseDialog);
        logger.info("Closed Dialog.");
    }

    public void clickCreateReturnOrder() {
        commonAction.click(loc_btnCreateReturnOrder);
        logger.info("Clicked on 'Export Order' button.");
    }

    public ReturnOrdersManagementPage clickExport() {
        commonAction.click(loc_btnExport);
        logger.info("Clicked on 'Export' button.");
        return this;
    }

    public void clickExportReturnOrder() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportReturnOrder).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportReturnOrder));
            return;
        }
        commonAction.click(loc_btnExportReturnOrder);
        logger.info("Clicked on 'Export Return Order' button.");
    }

    public void clickExportHistory() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportHistory).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportHistory));
            return;
        }
        commonAction.click(loc_btnExportHistory);
        logger.info("Clicked on 'Export History' button.");
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateReturnedOrder(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickExport().clickCreateReturnOrder();
            boolean flag = isDialogDisplayed();
            closeDialog();
            Assert.assertTrue(flag);
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToExportReturnedOrder(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickExport().clickExportReturnOrder();
            new ConfirmationDialog(driver).clickCancelBtn();
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToExportHistory(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickExport().clickExportHistory();
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
            commonAction.navigateBack();
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    void navigateToReturnOrderManagementPage() {
        driver.get("%s/order/return-order/list".formatted(DOMAIN));
        driver.navigate().refresh();

        logger.info("Navigate to return order management page by URL.");
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

    public ReturnOrdersManagementPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public ReturnOrdersManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
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

        // check view return order list
        checkViewReturnOrderList();

        // check others permission
        new ReturnOrderDetailPage(driver, permissions).getLoginInformation(sellerLoginInformation, staffLoginInformation)
                .checkReturnOrdersPermission();
    }

    void checkViewReturnOrderList() {
        List<Integer> listOfReturnOrderIdWithStaffToken = apiAllReturnOrdersWithStaffToken.getAllReturnOrdersInformation().getIds();
        if (permissions.getOrders().getReturnOrder().isViewOrderReturnList()) {
            List<Integer> listOfReturnOrderIdWithSellerToken = apiAllReturnOrdersWithSellerToken.getListReturnOrderIdAfterFilterByAssignedBranch(staffLoginInfo.getAssignedBranchesIds());
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(listOfReturnOrderIdWithStaffToken, listOfReturnOrderIdWithSellerToken),
                    "List return order must be %s, but found %s.".formatted(listOfReturnOrderIdWithSellerToken, listOfReturnOrderIdWithStaffToken));

            // check create return order
            checkCreateReturnOrder();
        } else {
            assertCustomize.assertTrue(listOfReturnOrderIdWithStaffToken.isEmpty(),
                    "List return order must be empty, but found %s.".formatted(listOfReturnOrderIdWithStaffToken));
        }

        if (!permissions.getOrders().getReturnOrder().toString().contains("true")) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/order/return-order/list".formatted(DOMAIN)),
                    "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/order/return-order/list".formatted(DOMAIN), "/order/return-order/list"),
                    "Return order management page must be shown, but found %s.".formatted(driver.getCurrentUrl()));
        }

        logger.info("Check permission: Orders >> Return order >> Check view return order list.");
    }

    void checkCreateReturnOrder() {
        // navigate to return order management page
        navigateToReturnOrderManagementPage();

        // click create return order button
        commonAction.clickJS(loc_btnCreateReturnOrder);

        // check create return order permission
        if (permissions.getOrders().getReturnOrder().isCreateReturnOrder()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgSelectOrderToReturn).isEmpty(),
                    "Can not open Select order to return popup.");

            if (!commonAction.getListElement(loc_dlgSelectOrderToReturn).isEmpty()) {
                // open list search type
                commonAction.clickJS(loc_dlgSelectOrderToReturn_ddvSelectedSearchType);

                // select "Order number" search type
                commonAction.clickJS(loc_dlgSelectOrderToReturn_ddvOrderNumberSearchType);

                // get orderId to create new return order
                long orderId = new APIAllOrders(staffLoginInformation).getOrderIdForReturnOrder(staffLoginInfo.getAssignedBranchesIds());

                if (orderId != 0) {
                    // search order
                    commonAction.sendKeys(loc_dlgSelectOrderToReturn_txtSearch, String.valueOf(orderId));
                    logger.info("OrderID: %s.".formatted(orderId));

                    // select order
                    commonAction.click(By.xpath(str_tblOrder_orderId.formatted(orderId)));

                    // create new return order
                    new CreateReturnOrderPage(driver, permissions).createReturnOrder();
                } else logger.warn("Can not find any order that can make a new return order.");
            }
        }

        logger.info("Check permission: Orders >> Return order >> Create return order.");
    }
}
