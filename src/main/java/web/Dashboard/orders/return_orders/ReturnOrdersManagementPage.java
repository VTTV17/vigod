package web.Dashboard.orders.return_orders;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
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

public class ReturnOrdersManagementPage extends ReturnOrdersManagementElement {

    final static Logger logger = LogManager.getLogger(ReturnOrdersManagementPage.class);

    WebDriver driver;
    UICommonAction commonAction;

    public ReturnOrdersManagementPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
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
            boolean flag = new SelectOrderToReturnDialog(driver).isDialogDisplayed();
            new SelectOrderToReturnDialog(driver).closeDialog();
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

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-24812
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo staffLoginInfo;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    APIAllOrders apiAllOrdersWithSellerToken;
    APIAllOrders apiAllOrdersWithStaffToken;

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

    public void checkReturnOrdersPermission(AllPermissions permissions) {
    }

    void checkViewReturnOrderList() {

    }

    void checkViewReturnOrderDetail() {

    }

    void checkCreateReturnOrder() {

    }

    void checkEditReturnOrder() {

    }

    void checkRestockGoods() {

    }

    void checkCompleteReturnOrder() {

    }

    void checkCancelReturnOrder() {

    }

    void checkConfirmPayment() {

    }
}
