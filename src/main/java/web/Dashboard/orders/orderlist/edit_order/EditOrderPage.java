package web.Dashboard.orders.orderlist.edit_order;

import api.Seller.login.Login;
import api.Seller.marketing.LoyaltyPoint;
import api.Seller.orders.order_management.APIAllOrderCosts;
import api.Seller.orders.order_management.APIAllOrders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.orders.orderlist.order_detail.OrderDetailPage;

import java.util.List;
import java.util.stream.IntStream;

import static api.Seller.orders.order_management.APIAllOrders.Channel;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.orders.orderlist.edit_order.EditOrderElement.DiscountType.discountAmount;
import static web.Dashboard.orders.orderlist.edit_order.EditOrderElement.DiscountType.getAllDiscountType;
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
    LoginDashboardInfo staffLoginInfo;

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
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    void navigateToEditOrderPageByURL(Long orderId) {
        driver.get("%s/order/edit/%s".formatted(DOMAIN, orderId));
        driver.navigate().refresh();
        logger.info("Navigate to edit order page, orderId: %s.".formatted(orderId));
    }

    public void checkEditOrder(Channel channel) {
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForEditOrder(channel, staffLoginInfo.getAssignedBranchesIds());
        if (orderId != 0) {
            // navigate to order detail page
            new OrderDetailPage(driver).navigateToOrderDetailPageByURL(channel, orderId);

            // check edit order permission
            if (permissions.getOrders().getOrderManagement().isEditOrder()) {
                // check can access to edit order page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnEditOrder,
                                "/order/edit/%s".formatted(orderId)),
                        "[%s] Can not access to edit order page.".formatted(channel));
                checkApplyDiscount(channel, orderId);
                checkCreateOrderCost(channel, orderId);
                checkViewOrderCostList(channel, orderId);
                checkNotApplyEarningPoint(channel, orderId);
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

    void checkApplyDiscount(Channel channel, long orderId) {
        navigateToEditOrderPageByURL(orderId);
        commonAction.click(loc_btnPromotion);
        if (permissions.getOrders().getOrderManagement().isApplyDiscount()) {
            if (!commonAction.getListElement(loc_dlgDiscount).isEmpty()) {
                // apply new discount
                commonAction.click(loc_dlgDiscount_tabDiscountType, getAllDiscountType().indexOf(discountAmount));
                commonAction.sendKeys(loc_dlgDiscount_txtDiscountValue, String.valueOf(nextInt(10000)));
                commonAction.click(loc_dlgDiscount_btnApply);

                // save changes
                commonAction.click(loc_btnSave);
            } else logger.warn("[%s] Can not open Discount popup.".formatted(channel));
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "[%s] Restricted popup is not shown.".formatted(channel));
        }

        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Apply discount.".formatted(channel));
    }

    void checkCreateOrderCost(Channel channel, long orderId) {
        navigateToEditOrderPageByURL(orderId);
        if (permissions.getOrders().getOrderManagement().isViewOrderCostList()) {
            commonAction.click(loc_btnCost);
            if (!commonAction.getListElement(loc_dlgCost).isEmpty()) {
                // remove old cost
                if (!commonAction.getListElement(loc_dlgCost_imgDeleteCost).isEmpty()) {
                    IntStream.iterate(commonAction.getListElement(loc_dlgCost_imgDeleteCost).size() - 1, index -> index >= 0, index -> index - 1)
                            .forEach(index -> commonAction.clickJS(loc_dlgCost_imgDeleteCost, index));
                }
                commonAction.click(loc_dlgCost_imgAddCost);
                if (permissions.getOrders().getOrderManagement().isCreateOrderCost()) {
                    // add new cost
                    commonAction.sendKeys(loc_dlgCost_txtCostName, 0, "cost%s".formatted(System.currentTimeMillis()));
                    commonAction.sendKeys(loc_dlgCost_txtCostValue, 0, String.valueOf(nextInt(10000)));
                    commonAction.click(loc_dlgCost_btnSave);

                    // save changes
                    commonAction.click(loc_btnSave);
                } else {
                    assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "[%s] Restricted popup is not shown.".formatted(channel));
                }
            } else logger.warn("[%s] Block by 'View order cost list' permission.".formatted(channel));
        }

        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Create order cost.".formatted(channel));
    }

    void checkViewOrderCostList(Channel channel, long orderId) {
        navigateToEditOrderPageByURL(orderId);
        List<Integer> listOrderCostIdWithStaffToken = new APIAllOrderCosts(staffLoginInformation).getAllOrderCostsInformation().getIds();
        if (permissions.getOrders().getOrderManagement().isViewOrderCostList()) {
            // check UI
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnCost, loc_dlgCost), "[%s] Can not open cost popup.".formatted(channel));

            // check API
            List<Integer> listOrderCostIdWithSellerToken = new APIAllOrderCosts(sellerLoginInformation).getAllOrderCostsInformation().getIds();
            assertCustomize.assertEquals(listOrderCostIdWithStaffToken, listOrderCostIdWithSellerToken, "[%s] List order cost must be %s, but found %s."
                    .formatted(channel, listOrderCostIdWithSellerToken.toString(), listOrderCostIdWithStaffToken.toString()));
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCost), "[%s] Restricted popup is not shown.");
            assertCustomize.assertTrue(listOrderCostIdWithStaffToken.isEmpty(), "[%s] List order cost must be empty, but found %s.".formatted(channel, listOrderCostIdWithStaffToken));
        }

        // log
        logger.info("[%s] Check permission: Orders >> Order management >> View order cost list.".formatted(channel));
    }

    void checkNotApplyEarningPoint(Channel channel, long orderId) {
        navigateToEditOrderPageByURL(orderId);
        boolean isEnableLoyaltyPoint = new LoyaltyPoint(staffLoginInformation).isEnableLoyaltyPoint();
        if (isEnableLoyaltyPoint) {
            if (permissions.getMarketing().getLoyaltyPoint().isViewPointProgramInformation()) {
                assertCustomize.assertFalse(commonAction.isDisabledJS(loc_chkNotApplyEarningPoint), "[%s] Not apply earning point checkbox is disabled.".formatted(channel));
            } else {
                assertCustomize.assertTrue(commonAction.isDisabledJS(loc_chkNotApplyEarningPoint), "[%s] Not apply earning point checkbox is not disabled.".formatted(channel));
            }
        }
        logger.info("[%s] Check permission: Order >> POS >> Not apply earning point.".formatted(channel));
    }
}
