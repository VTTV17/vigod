package web.Dashboard.orders.orderlist.order_list;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.orders.order_management.APIAllOrders.Channel;
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
import utilities.utils.FileUtils;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.orders.orderlist.order_detail.OrderDetailPage;

import java.util.List;
import java.util.Objects;

import static api.Seller.orders.order_management.APIAllOrders.Channel.*;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.orders.orderlist.order_list.OrderManagementElement.BulkActions.*;

public class OrderManagementPage extends OrderManagementElement {

    final static Logger logger = LogManager.getLogger(OrderManagementPage.class);

    WebDriver driver;
    UICommonAction commonAction;

    public OrderManagementPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public OrderManagementPage navigate() {
        new HomePage(driver).navigateToPage("Orders");
        new HomePage(driver).hideFacebookBubble();
        return this;
    }

    public void clickCancel() {
        commonAction.clickJS(loc_btnCancel);
        logger.info("Clicked on 'Cancel' button.");
    }

    public boolean isSelectProductDialogDisplayed() {
        commonAction.sleepInMiliSecond(1000);
        return !commonAction.getElements(loc_btnExportOrderByProduct).isEmpty();
    }

    public OrderManagementPage clickExport() {
        commonAction.click(loc_btnExport);
        logger.info("Clicked on 'Export' button.");
        return this;
    }

    public void clickExportOrder() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportOrder).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportOrder));
            return;
        }
        commonAction.click(loc_btnExportOrder);
        logger.info("Clicked on 'Export Order' button.");
    }

    public void clickExportOrderByProduct() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportOrderByProduct).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportOrderByProduct));
            return;
        }
        commonAction.click(loc_btnExportOrderByProduct);
        logger.info("Clicked on 'Export Order By Product' button.");
        new HomePage(driver).waitTillLoadingDotsDisappear();
    }

    public void clickExportHistory() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportHistory).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportHistory));
            return;
        }
        commonAction.click(loc_btnExportHistory);
        new HomePage(driver).waitTillLoadingDotsDisappear();
        logger.info("Clicked on 'Export History' button.");
    }

    /**
     * Temporary function, will be deleted soon.
    
     */
    public OrderManagementPage clickFirstOrder() {
        commonAction.click(loc_tmpRecords, 0);
        logger.info("Clicked on the first order in order list.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
    
     */
    public OrderManagementPage clickConfirmOrder() {
        commonAction.click(loc_btnConfirmOrder);
        logger.info("Clicked on 'Confirm Order' button.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
    
     */
    public OrderManagementPage clickShipmentOKBtn() {
        commonAction.click(loc_btnShipmentOK);
        logger.info("Clicked on 'OK' button to confirm shipment.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
    
     */
    public OrderManagementPage clickDeliveredBtn() {
        commonAction.click(loc_btnDelivered);
        logger.info("Clicked on 'Delivered' button.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
    
     */
    public OrderManagementPage clickConfirmDeliveredOKBtn() {
        new ConfirmationDialog(driver).clickOKBtn();
        logger.info("Clicked on 'OK' button to confirm the order is delivered.");
        return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToExportOrder(String permission) {
        if (permission.contentEquals("A")) {
            clickExport().clickExportOrder();
            new ConfirmationDialog(driver).clickCancelBtn_V2();
        } else if (permission.contentEquals("D")) {
            clickExport().clickExportOrder();
            boolean flag = new ConfirmationDialog(driver).isConfirmationDialogDisplayed();
            new OrderManagementPage(driver).clickExport();
            Assert.assertFalse(flag);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToExportOrderByProduct(String permission) {
        if (permission.contentEquals("A")) {
            clickExport().clickExportOrderByProduct();
            try {
               clickCancel();
            } catch (Exception e) {
                commonAction.navigateBack();
            }

        } else if (permission.contentEquals("D")) {
            clickExport().clickExportOrderByProduct();
            boolean flag = isSelectProductDialogDisplayed();
            clickExport();
            Assert.assertFalse(flag);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public void verifyPermissionToExportHistory(String permission, String url) {
        if (permission.contentEquals("A")) {
            clickExport().clickExportHistory();
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    public OrderManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    void navigateToOrderManagementPageByURL(Channel channel) {
        driver.get("%s/order/list?page=1&size=50&channel=%s&view=COMPACT&fromDate=&toDate".formatted(DOMAIN, channel));
        driver.navigate().refresh();
        logger.info("[%s] Navigate to order management page.".formatted(channel));
    }

    void bulkActionsWithFirstOrder(BulkActions actions) {
        // select all orders
        if (!commonAction.isCheckedJS(loc_chkOrder, 0)) commonAction.clickJS(loc_chkOrder, 0);

        // select actions
        commonAction.clickJS(loc_ddlActions, getAllActions().indexOf(actions));

        // wait loading
        if (Objects.equals(actions, printShippingLabel)) {
            commonAction.sleepInMiliSecond(2000, "Wait print screen shows.");
        }
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-13817
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo staffLoginInfo;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    APIAllOrders apiAllOrdersWithSellerToken;
    APIAllOrders apiAllOrdersWithStaffToken;

    public void checkOrderManagementPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init order management API
        apiAllOrdersWithSellerToken = new APIAllOrders(sellerLoginInformation);
        apiAllOrdersWithStaffToken = new APIAllOrders(staffLoginInformation);

//        checkViewOrderList();

        new OrderDetailPage(driver, permissions).getLoginInformation(sellerLoginInformation, staffLoginInformation)
                .checkViewOrderDetail();

    }

    void checkViewOrderListWithChannel(Channel channel) {
        // navigate to order management page
        navigateToOrderManagementPageByURL(channel);

        // get list order ids
        List<Long> listOfOrderIdsWithStaffToken = apiAllOrdersWithStaffToken.getAllOrderInformation(channel).getIds();
        boolean permission = switch (channel) {
            case GOSELL, BEECOW -> {
                logger.info("[%s] Check permission: Orders >> Order management >> View order list.".formatted(channel));
                logger.info("[%s] Check permission: Orders >> Order management >> View created order list.".formatted(channel));
                yield permissions.getOrders().getOrderManagement().isViewOrderList() || permissions.getOrders().getOrderManagement().isViewCreatedOrderList();
            }
            case SHOPEE -> {
                logger.info("[%s] Check permission: Shopee >> View order list.".formatted(channel));
                yield permissions.getShopee().isViewOrderList();
            }
            case LAZADA -> {
                logger.info("[%s] Check permission: Lazada >> View order list.".formatted(channel));
                yield permissions.getLazada().isViewOrderList();
            }
            case TIKTOK -> {
                logger.info("[%s] Check permission: Tiktok >> View order list.".formatted(channel));
                yield permissions.getTiktok().isViewOrderList();
            }
        };

        // check permission
        if (permission) {
            List<Long> listOfOrderIdsWithSellerToken = ((Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW))
                    && !permissions.getOrders().getOrderManagement().isViewOrderList()
                    && permissions.getOrders().getOrderManagement().isViewCreatedOrderList())
                    ? apiAllOrdersWithSellerToken.getOrderIdsAfterFilterByAssignedBranchIdsAndCreatedBy(channel,
                    staffLoginInfo.getAssignedBranchesIds(),
                    staffLoginInfo.getUserName())
                    : apiAllOrdersWithSellerToken.getOrderIdsAfterFilterByAssignedBranchIds(channel,
                    staffLoginInfo.getAssignedBranchesIds());
            assertCustomize.assertEquals(listOfOrderIdsWithStaffToken, listOfOrderIdsWithSellerToken,
                    "[%s] List order must be: %s, but found: %s.".formatted(channel, listOfOrderIdsWithSellerToken.toString(), listOfOrderIdsWithStaffToken.toString()));

            if (!commonAction.getListElement(loc_chkOrder).isEmpty()) {
                checkPrintOrderReceipt(channel);
                checkPrintOrderSlip(channel);
                checkAddTagsToOrder(channel);
                checkRemoveTagFromOrder(channel);
                checkExportOrder(channel);
                checkExportOrderByProduct(channel);
                checkDisplayOrderSetting(channel);
                checkDownloadExportedOrders();
                checkPrintOrder();
            }
        } else {
            // staff can see list all order in assigned branch if they have permission “View order list”
            // staff can only see list order that created by themselves only if they have permission “View created order list”
            // staff don’t have permission “View order list” or “View created order list” => don’t see any GoSELL/GoMUA order in order list
            // staff don’t have permission “View order list” in Shopee permission => don’t see any GoSELL/GoMUA order in order list
            // staff don’t have permission “View order list” in TikTok permission  => don’t see any TikTok order in order list
            // staff don’t have permission “View order list” in Lazada permission  => don’t see any Lazada order in order list
            assertCustomize.assertTrue(listOfOrderIdsWithStaffToken.isEmpty(),
                    "[%s] List order must be empty, but found: %s.".formatted(channel, listOfOrderIdsWithStaffToken.toString()));
        }
    }

    void checkViewOrderList() {
        // check permission view order list
        getAllOrderChannels().forEach(this::checkViewOrderListWithChannel);
    }

    void checkPrintOrderSlip(Channel channel) {
        if (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW)) {
            // navigate to order management page
            navigateToOrderManagementPageByURL(channel);

            // bulk actions print shipping label
            bulkActionsWithFirstOrder(printShippingLabel);


            // check print order slip permission
            if (permissions.getOrders().getOrderManagement().isPrintOrderSlip()) {
                if (driver.getWindowHandles().size() > 1) {
                    // switch to print tab
                    commonAction.switchToWindow(1);

                    // check print tab
                    assertCustomize.assertTrue(driver.getCurrentUrl().contains("/print"), "Tab must be print, but found %s.".formatted(driver.getCurrentUrl()));

                    // switch to main tab
                    commonAction.switchToWindow(0);
                }
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
            }
        }
    }

    void checkPrintOrderReceipt(Channel channel) {
        if (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW)) {
            // navigate to order management page
            navigateToOrderManagementPageByURL(channel);

            // bulk actions print order receipt
            bulkActionsWithFirstOrder(printReceipt);

            // check print order receipt permission
            if (permissions.getOrders().getOrderManagement().isPrintOrderReceipt()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgSelectPrintSize).isEmpty(), "Can not open Select print size popup.");
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted is not shown.");
            }
        }
        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Print order receipt.".formatted(channel));
    }

    void checkExportOrder(Channel channel) {
        boolean permission = switch (channel) {
            case GOSELL, BEECOW -> {
                logger.info("[%s] Check permission: Orders >> Order management >> Export order.".formatted(channel));
                yield permissions.getOrders().getOrderManagement().isExportOrder();
            }
            case SHOPEE -> {
                logger.info("[%s] Check permission: Shopee >> Export order.".formatted(channel));
                yield permissions.getShopee().isExportOrder();
            }
            case LAZADA -> {
                logger.info("[%s] Check permission: Lazada >> Export order.".formatted(channel));
                yield permissions.getLazada().isExportOrder();
            }
            case TIKTOK -> {
                logger.info("[%s] Check permission: Tiktok >> Export order.".formatted(channel));
                yield permissions.getTiktok().isExportOrder();
            }
        };

        // navigate to order management page
        navigateToOrderManagementPageByURL(channel);

        // select export order actions
        commonAction.click(loc_btnExport);
        commonAction.click(loc_btnExportOrder);

        // check export order permission
        if (permission) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgConfirmExportOrder).isEmpty(),
                    "Can not open Confirm export order popup.");

            if (!commonAction.getListElement(loc_dlgConfirmExportOrder).isEmpty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmExportOrder_btnOK, loc_dlgToast),
                        "Can not export order.");
            }
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }

    }

    void checkExportOrderByProduct(Channel channel) {
        if (Objects.equals(channel, GOSELL) || Objects.equals(channel, BEECOW)) {
            // navigate to order management page
            navigateToOrderManagementPageByURL(channel);

            // select export order actions
            commonAction.click(loc_btnExport);
            commonAction.click(loc_btnExportOrderByProduct);

            // check export order by product permission
            if (permissions.getOrders().getOrderManagement().isExportOrderByProduct()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgExportOrderByProduct).isEmpty(),
                        "Can not open Export order by product popup.");
                if (!commonAction.getListElement(loc_dlgExportOrderByProduct).isEmpty()) {
                    if (commonAction.getListElement(loc_dlgExportOrderByProduct_chkProduct).isEmpty()) {
                        commonAction.click(loc_dlgExportOrderByProduct_btnCancel);
                    } else {
                        commonAction.clickJS(loc_dlgExportOrderByProduct_chkProduct, 0);
                        commonAction.click(loc_dlgExportOrderByProduct_btnExportByProduct);
                    }
                }
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
            }
        }
        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Export order by product.".formatted(channel));
    }

    void checkRemoveTagFromOrder(Channel channel) {
        // navigate to order management page
        navigateToOrderManagementPageByURL(channel);

        // bulk action remove tags from order
        bulkActionsWithFirstOrder(removeTags);

        // check remove tags from order permission
        if (permissions.getOrders().getOrderManagement().isRemoveTagFromOrder()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgRemoveTags).isEmpty(), "Can not open Remove tags popup.");

            if (!commonAction.getListElement(loc_dlgRemoveTags).isEmpty()) {
                if (commonAction.getListElement(loc_dlgRemoveTags_lstTags).isEmpty()) {
                    commonAction.click(loc_dlgRemoveTags_btnCancel);
                } else {
                    commonAction.click(loc_dlgRemoveTags_lstTags, 0);
                    commonAction.click(loc_dlgRemoveTags_btnRemoveTags);
                }
            }
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }
        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Remove tags from order.".formatted(channel));
    }

    void checkAddTagsToOrder(Channel channel) {
        // navigate to order management page
        navigateToOrderManagementPageByURL(channel);

        // bulk action remove tags from order
        bulkActionsWithFirstOrder(addTags);

        // check remove tags from order permission
        if (permissions.getOrders().getOrderManagement().isAddTagToOrder()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgAddTags).isEmpty(), "Can not open Add tags popup.");

            if (!commonAction.getListElement(loc_dlgAddTags).isEmpty()) {
                commonAction.click(loc_dlgAddTags_txtTag);
                if (commonAction.getListElement(loc_dlgAddTags_lstTag).isEmpty()) {
                    commonAction.click(loc_dlgAddTags_icnClose);
                } else {
                    commonAction.click(loc_dlgAddTags_lstTag, 0);
                    commonAction.click(loc_dlgAddTags_btnAdd);
                }
            }
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }
        // log
        logger.info("[%s] Check permission: Orders >> Order management >> Add tags to order.".formatted(channel));
    }

    void checkDisplayOrderSetting(Channel channel) {
        if (Objects.equals(channel, GOSELL)) {
            // navigate to order management page
            navigateToOrderManagementPageByURL(channel);

            // actions setting
            commonAction.clickJS(icnSetting);

            // check display order setting permission
            if (permissions.getOrders().getOrderManagement().isDisplayOrderSetting()) {
                if (driver.getCurrentUrl().contains("order-display-configuration")) {
                    commonAction.click(btnSaveOnEditOrderDisplaySetting);
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(btnSaveOnEditOrderDisplaySetting, loc_dlgToast),
                            "Can not edit order display setting.");
                }
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
            }
        }
        // log
        logger.info("Check permission: Orders >> Order management >> Display order setting.");
    }

    void navigateToExportOrderHistoryPage() {
        driver.get("%s/order/export-history".formatted(DOMAIN));
        driver.navigate().refresh();
        logger.info("Navigate to export order history page by URL.");
    }

    void checkDownloadExportedOrders() {
        // navigate to export order history page
        navigateToExportOrderHistoryPage();

        // check download export order history permission
        if (permissions.getOrders().getOrderManagement().isDownloadExportedOrders()) {
            // init file utils
            FileUtils fileUtils = new FileUtils();

            // delete old export order file
            fileUtils.deleteFileInDownloadFolder("EXPORT_ORDER");

            // download new exported order
            commonAction.clickJS(loc_icnDownloadExportFile, 0);
            commonAction.sleepInMiliSecond(1000, "Waiting for download.");
            assertCustomize.assertTrue(fileUtils.isDownloadSuccessful("EXPORT_ORDER"), "No exported order file is downloaded.");

        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnDownloadExportFile, 0), "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Orders >> Order management >> Download exported order.");
    }

    void checkPrintOrder() {
        if (apiAllOrdersWithSellerToken.isPrintOrder() && permissions.getOrders().getOrderManagement().isPrintOrders()) {
            assertCustomize.assertFalse(commonAction.getListElement(btnPrintOrder).isEmpty(), "Print order button is hidden.");
        } else {
            assertCustomize.assertTrue(commonAction.getListElement(btnPrintOrder).isEmpty(), "Print order still shows.");
        }
    }
}
