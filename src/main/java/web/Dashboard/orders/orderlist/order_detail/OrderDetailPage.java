package web.Dashboard.orders.orderlist.order_detail;

import api.Seller.orders.delivery.APIPartialDeliveryOrders;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.products.location_receipt.APILocationReceipt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.orders.orderlist.edit_order.EditOrderPage;

import java.util.Objects;

import static api.Seller.orders.order_management.APIAllOrders.*;
import static api.Seller.orders.order_management.APIAllOrders.Channel.*;
import static utilities.links.Links.DOMAIN;

public class OrderDetailPage extends OrderDetailElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(OrderDetailPage.class);

    public OrderDetailPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    void navigateToOrderDetailPageByURL(Channel channel, Long orderId) {
        driver.get("%s/order/detail/%s/%s".formatted(DOMAIN, channel, orderId));
        driver.navigate().refresh();
        logger.info("[%s] Navigate to order detail page, orderId: %s.".formatted(channel, orderId));
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-13817
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    APIAllOrders apiAllOrdersWithSellerToken;

    public OrderDetailPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public OrderDetailPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        apiAllOrdersWithSellerToken = new APIAllOrders(sellerLoginInformation);
        return this;
    }

    void checkViewOrderDetailWithChannel(Channel channel) {
        // get permission
        boolean permission = switch (channel) {
            case GOSELL, BEECOW -> {
                logger.info("[%s] Check permission: Orders >> Order management >> View order detail.".formatted(channel));
                yield permissions.getOrders().getOrderManagement().isViewOrderDetail();
            }
            case SHOPEE -> {
                logger.info("[%s] Check permission: Shopee >> View order detail.".formatted(channel));
                yield permissions.getShopee().isViewOrderDetail();
            }
            case LAZADA -> {
                logger.info("[%s] Check permission: Lazada >> View order detail.".formatted(channel));
                yield permissions.getLazada().isViewOrderDetail();
            }
            case TIKTOK -> {
                logger.info("[%s] Check permission: Tiktok >> View order detail.".formatted(channel));
                yield permissions.getTiktok().isViewOrderDetail();
            }
        };
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForViewDetail(channel);
        if (orderId != 0) {
            // check view order detail permission
            if (permission) {
                // check can access to order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/order/detail/%s/%s".formatted(DOMAIN, channel, orderId),
                                String.valueOf(orderId)),
                        "[%s] Can not access to order detail page.".formatted(channel));

                // check View delivery package list
                if (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW)) {
                    checkViewDeliveryPackageList(channel);
                    checkViewLocationReceiptList(channel, orderId);
                    new EditOrderPage(driver, permissions).getLoginInformation(sellerLoginInformation, staffLoginInformation)
                            .checkEditOrder(channel);
                    checkDeliveredOrders(channel);
                }

                // check confirm order
                checkConfirmOrderWithChannel(channel);

                // check cancel order
                checkCancelOrder(channel);
            } else
                // if staff don’t have permission “View order detail” => Show restricted
                // when click to view order detail
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/order/detail/%s/%s".formatted(DOMAIN, channel, orderId)),
                        "[%s] Restricted page must be shown instead of %s.".formatted(channel, driver.getCurrentUrl()));
        }
    }

    public void checkViewOrderDetail() {
        // check permission view order detail
        getAllOrderChannels().forEach(this::checkViewOrderDetailWithChannel);
    }

    void checkViewDeliveryPackageList(Channel channel) {
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForDeliveredOrder(channel);
        if (orderId != 0) {
            // navigate to order detail page
            navigateToOrderDetailPageByURL(channel, orderId);

            if (permissions.getOrders().getDelivery().isViewDeliveryPackageList()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_lblDeliveryInformation).isEmpty(), "Delivery package section do not shown.");
            } else {
                assertCustomize.assertTrue(commonAction.getListElement(loc_lblDeliveryInformation).isEmpty(), "Delivery package section still shown.");
            }
        }
        logger.info("[%s] Check permission: Order >> Delivery >> View delivery package list.".formatted(channel));
    }

    void checkViewLocationReceiptList(Channel channel, long orderId) {
        // navigate to order detail page
        navigateToOrderDetailPageByURL(channel, orderId);

        // if order have delivery package, hide location receipt section in order detail page
        if (new APIPartialDeliveryOrders(staffLoginInformation).getNumOfOrderDeliveryPackage(orderId) == 0) {
            // staff don’t have permission “View get product location receipt list”
            // will not see any get product to location receipt in order detail
            int actualNumberOfGetReceiptInOrder = commonAction.getListElement(loc_lblGetReceipt).size();
            int expectedNumberOfGetReceiptInOrder = permissions.getProduct().getLocationReceipt().isViewGetProductLocationReceiptList()
                    ? new APILocationReceipt(sellerLoginInformation).getNumberOfGetReceiptInOrder(orderId)
                    : 0;
            assertCustomize.assertEquals(actualNumberOfGetReceiptInOrder,
                    expectedNumberOfGetReceiptInOrder,
                    "Number of GET receipt in order detail must be %s, but found %s."
                            .formatted(expectedNumberOfGetReceiptInOrder, actualNumberOfGetReceiptInOrder));

            // staff don’t have permission “View add product location receipt list”
            // will not see any add product to location receipt in order detail
            int actualNumberOfAddReceiptInOrder = commonAction.getListElement(loc_lblAddReceipt).size();
            int expectedNumberOfAddReceiptInOrder = permissions.getProduct().getLocationReceipt().isViewAddProductLocationReceiptList()
                    ? new APILocationReceipt(sellerLoginInformation).getNumberOfAddReceiptInOrder(orderId)
                    : 0;
            assertCustomize.assertEquals(actualNumberOfAddReceiptInOrder,
                    expectedNumberOfAddReceiptInOrder,
                    "Number of ADD receipt in order detail must be %s, but found %s."
                            .formatted(expectedNumberOfAddReceiptInOrder, actualNumberOfAddReceiptInOrder));
        }
        logger.info("[%s] Check permission: Product >> Location Receipt >> View add product location receipt list.".formatted(channel));
        logger.info("[%s] Check permission: Product >> Location Receipt >> View get product location receipt list.".formatted(channel));
    }

    void checkConfirmOrderWithChannel(Channel channel) {
        boolean permission = switch (channel) {
            case GOSELL, BEECOW -> {
                logger.info("[%s] Check permission: Orders >> Order management >> Confirm order.".formatted(channel));
                yield permissions.getOrders().getOrderManagement().isConfirmOrder();
            }
            case SHOPEE -> {
                logger.info("[%s] Check permission: Shopee >> Confirm order.".formatted(channel));
                yield permissions.getShopee().isConfirmOrder();
            }
            case LAZADA -> {
                logger.info("[%s] Check permission: Lazada >> Confirm order.".formatted(channel));
                yield permissions.getLazada().isConfirmOrder();
            }
            case TIKTOK -> {
                logger.info("[%s] Check permission: Tiktok >> Confirm order.".formatted(channel));
                yield permissions.getTiktok().isConfirmOrder();
            }
        };
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForConfirmOrder(channel);
        if (orderId != 0) {
            // navigate to order detail page
            navigateToOrderDetailPageByURL(channel, orderId);

            // check confirm order permission
            if (permission) {
                // check can access to order detail page
                if (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW)) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnConfirmOrder, "/order/arrange-shipment/"),
                            "[%s] Can not access to Arrange Shipment page.".formatted(channel));
                } else {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnConfirmOrder, loc_dlgToast),
                            "[%s] Can not confirm order.".formatted(channel));
                }
            } else
                // if staff don’t have permission “Confirm order” in Order management permission
                // => show restricted popup
                // when click on [Confirm order] button in GoSELL order detail page
                //if staff don’t have permission “Confirm order” in Shopee permission
                //  => show restricted popup
                // when click on [Confirm order] button in Lazada order detail page
                //if staff don’t have permission “Confirm order” in Shopee permission
                //  => show restricted popup
                // when click on [Confirm order] button in TikTok order detail page
                //if staff don’t have permission “Confirm order” in Shopee permission
                //   => show restricted popup
                // when click on [Confirm order] button in TikTok order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnConfirmOrder),
                        "[%s] Restricted popup is not shown.".formatted(channel));
        }
    }

    void checkCancelOrder(Channel channel) {
        boolean permission = switch (channel) {
            case GOSELL, BEECOW -> {
                logger.info("[%s] Check permission: Orders >> Order management >> Cancel order.".formatted(channel));
                yield permissions.getOrders().getOrderManagement().isCancelOrder();
            }
            case SHOPEE -> {
                logger.info("[%s] Check permission: Shopee >> Cancel order.".formatted(channel));
                yield permissions.getShopee().isCancelOrder();
            }
            case LAZADA -> {
                logger.info("[%s] Check permission: Lazada >> Cancel order.".formatted(channel));
                yield permissions.getLazada().isCancelOrder();
            }
            case TIKTOK -> {
                logger.info("[%s] Check permission: Tiktok >> Cancel order.".formatted(channel));
                yield permissions.getTiktok().isCancelOrder();
            }
        };
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForCancelOrder(channel);
        if (orderId != 0) {
            // navigate to order detail page
            navigateToOrderDetailPageByURL(channel, orderId);

            // check cancel order permission
            if (permission) {
                // check can open confirm cancel order popup
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnCancelOrder, loc_dlgConfirmCancelOrder),
                            "[%s] Can not open Confirm Cancel Order popup.".formatted(channel));
                    if (!commonAction.getListElement(loc_dlgConfirmCancelOrder).isEmpty()) {
                        commonAction.sendKeys(loc_dlgConfirmCancelOrder_txtReason, "Cancel reason %s.".formatted(orderId));
                        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmCancelOrder_btnConfirm, loc_dlgToast),
                                "[%s] Can not cancel order.".formatted(channel));
                    }
            } else
                // if staff don’t have permission “Cancel order”
                // => show restricted popup
                // when click on [Cancel order] button in GoSELL order detail page
                // if staff don’t have permission “Cancel order” in Shopee permission
                // => show restricted popup
                // when click on [Cancel order] button in Shopee order detail page
                // if staff don’t have permission “Cancel order” in Shopee permission
                // => show restricted popup
                // when click on [Cancel order] button in Lazada order detail page
                // if staff don’t have permission “Cancel order” in Shopee permission
                // => show restricted popup
                // when click on [Cancel order] button in TikTok order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCancelOrder),
                        "[%s] Restricted popup is not shown.".formatted(channel));
        }
    }

    void checkDeliveredOrders(Channel channel) {
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForDeliveredOrder(channel);
        if (orderId != 0) {
            // navigate to order detail page
            navigateToOrderDetailPageByURL(channel, orderId);

            // check delivered order permission
            if (permissions.getOrders().getOrderManagement().isDeliveredOrders()) {
                // check can open confirm delivered order popup
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnDeliveredOrder, loc_dlgConfirmDeliveredOrder),
                        "[%s] Can not open Confirm Delivered Order popup.".formatted(channel));
                if (!commonAction.getListElement(loc_dlgConfirmCancelOrder).isEmpty()) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmDeliveredOrder_btnConfirm, loc_dlgToast),
                            "[%s] Can not delivered order.".formatted(channel));
                }
            } else
                // if staff don’t have permission “Delivered order”
                // => show restricted popup
                // when click on [Delivered] button in GoSELL order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDeliveredOrder),
                        "[%s] Restricted popup is not shown.".formatted(channel));
        }
    }

    void checkPrintOrderSlip() {

    }

    void checkPrintOrderReceipt() {

    }

    void checkCreateOrderTag() {

    }

    void checkAddTagToOrder() {

    }

    void checkRemoveTagFromOrder() {

    }

    void checkViewTagList() {

    }

    void checkDeleteTag() {

    }

    void checkConfirmPayment() {

    }
}
