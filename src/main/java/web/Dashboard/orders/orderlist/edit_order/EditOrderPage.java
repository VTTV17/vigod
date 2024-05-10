package web.Dashboard.orders.orderlist.edit_order;

import api.Seller.orders.order_management.APIAllOrders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.orders.orderlist.order_detail.OrderDetailPage;

import static api.Seller.orders.order_management.APIAllOrders.Channel;
import static web.Dashboard.orders.orderlist.order_detail.OrderDetailElement.loc_btnEditOrder;


public class EditOrderPage extends EditOrderElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(EditOrderPage.class);

    public EditOrderPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-13817
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    APIAllOrders apiAllOrdersWithSellerToken;
    public EditOrderPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public EditOrderPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        apiAllOrdersWithSellerToken = new APIAllOrders(sellerLoginInformation);
        return this;
    }

    public void checkEditOrder(Channel channel) {
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForEditOrder(channel);
        if (orderId != 0) {
            // check edit order permission
            if (permissions.getOrders().getOrderManagement().isEditOrder()) {
                // check can access to edit order page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnEditOrder,
                                "/order/edit/%s".formatted(orderId)),
                        "[%s] Can not access to edit order page.".formatted(channel));
            } else {
                // if staff don’t have permission “Edit order”
                // => show restricted popup
                // when click on [Edit order] button in order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnEditOrder),
                        "[%s] Restricted popup is not shown.");
            }
        }
        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Edit order.".formatted(channel));
    }

    void checkApplyDiscount() {

    }

    void checkViewOrderCostList() {

    }
}
