package web.Dashboard.orders.orderlist;

import api.Seller.login.Login;
import api.Seller.orders.delivery.APIPartialDeliveryOrders;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.orders.order_management.APIAllOrders.Channel;
import api.Seller.products.location_receipt.APILocationReceipt;
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
import web.Dashboard.orders.orderlist.order_detail.OrderDetailPage;

import java.util.List;
import java.util.Objects;

import static api.Seller.orders.order_management.APIAllOrders.Channel.*;
import static utilities.links.Links.DOMAIN;

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

    public OrderManagementPage clickExport() {
        commonAction.click(loc_btnExport);
        logger.info("Clicked on 'Export' button.");
        return this;
    }

    public OrderManagementPage clickExportOrder() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportOrder).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportOrder));
            return this;
        }
        commonAction.click(loc_btnExportOrder);
        logger.info("Clicked on 'Export Order' button.");
        return this;
    }

    public OrderManagementPage clickExportOrderByProduct() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportOrderByProduct).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportOrderByProduct));
            return this;
        }
        commonAction.click(loc_btnExportOrderByProduct);
        logger.info("Clicked on 'Export Order By Product' button.");
        new HomePage(driver).waitTillLoadingDotsDisappear();
        return this;
    }

    public OrderManagementPage clickExportHistory() {
        if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportHistory).findElement(By.xpath("./parent::*")))) {
            new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportHistory));
            return this;
        }
        commonAction.click(loc_btnExportHistory);
        new HomePage(driver).waitTillLoadingDotsDisappear();
        logger.info("Clicked on 'Export History' button.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
     * @return
     */
    public OrderManagementPage clickFirstOrder() {
        commonAction.click(loc_tmpRecords, 0);
        logger.info("Clicked on the first order in order list.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
     * @return
     */
    public OrderManagementPage clickConfirmOrder() {
        commonAction.click(loc_btnConfirmOrder);
        logger.info("Clicked on 'Confirm Order' button.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
     * @return
     */
    public OrderManagementPage clickShipmentOKBtn() {
        commonAction.click(loc_btnShipmentOK);
        logger.info("Clicked on 'OK' button to confirm shipment.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
     * @return
     */
    public OrderManagementPage clickDeliveredBtn() {
        commonAction.click(loc_btnDelivered);
        logger.info("Clicked on 'Delivered' button.");
        return this;
    }

    /**
     * Temporary function, will be deleted soon.
     * @return
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
                new ExportOrderByProductDialog(driver).clickCancel();
            } catch (Exception e) {
                commonAction.navigateBack();
            }

        } else if (permission.contentEquals("D")) {
            clickExport().clickExportOrderByProduct();
            boolean flag = new ExportOrderByProductDialog(driver).isSelectProductDialogDisplayed();
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

    public void checkOrderManagementPermission(AllPermissions permissions) throws Exception {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init order management API
        apiAllOrdersWithSellerToken = new APIAllOrders(sellerLoginInformation);
        apiAllOrdersWithStaffToken = new APIAllOrders(staffLoginInformation);

        checkViewOrderList();

        new OrderDetailPage(driver, permissions).getLoginInformation(sellerLoginInformation, staffLoginInformation)
                .checkViewOrderDetail();

    }

    void checkViewOrderListWithChannel(Channel channel) {
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

    void checkPrintOrderSlip() {
    }

    void checkPrintOrderReceipt() {

    }

    void checkExportOrder() {

    }

    void checkExportOrderByProduct() {

    }

    void checkRemoveTagFromOrder() {

    }

    void checkViewTagList() {

    }

    void checkDeleteTag() {

    }

    void checkDisplayOrderSetting() {

    }

    void checkDownloadExportedOrders() {

    }
}
