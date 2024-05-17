package web.Dashboard.orders.return_orders.return_order_detail;

import api.Seller.login.Login;
import api.Seller.orders.return_order.APIAllReturnOrder;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

public class ReturnOrderDetailPage extends  ReturnOrderDetailElement{
    WebDriver driver;
    UICommonAction commonAction;

    public ReturnOrderDetailPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
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
    }

    void checkViewReturnOrderDetail() {

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
