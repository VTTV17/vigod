package web.Dashboard.marketing.affiliate.order;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.enums.ApproveStatus;
import utilities.enums.DeliveryStatus;
import utilities.enums.PartnerStatus;
import utilities.enums.PaymentStatus;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.marketing.affiliate.partner.PartnerPage;

import java.util.List;

public class PartnerOrdersPage extends PartnerOrdersElement{
    final static Logger logger = LogManager.getLogger(PartnerOrdersPage.class);
    UICommonAction common;
    WebDriver driver;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    public PartnerOrdersPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize  = new AssertCustomize(driver);
    }
    public PartnerOrdersPage clickOnFilter(){
        common.click(loc_btnFilter);
        logger.info("Click on filter.");
        return this;
    }
    public PartnerOrdersPage clickOnDoneFilter(){
        common.click(loc_filter_btnDone);
        logger.info("Click on done on Filter dropdown.");
        return this;
    }
    public PartnerOrdersPage selectApproveStatusInFilter(ApproveStatus approveStatus){
        String xpath = dynamic_loc_filter_btnApproveStatus.formatted(approveStatus);
        By selectElement = By.xpath(xpath);
        if(!common.getAttribute(selectElement,"class").contains("selected")){
            common.click(selectElement);
            logger.info("Select approve status: "+approveStatus);
        }else logger.info(approveStatus+" is selected before");
        return this;
    }
    public PartnerOrdersPage selectPaymentStatusInFilter(PaymentStatus paymentStatus){
        String xpath = dynamic_loc_filter_btnPaymentStatus.formatted(paymentStatus);
        By selectElement = By.xpath(xpath);
        if(!common.getAttribute(selectElement,"class").contains("selected")){
            common.click(selectElement);
            logger.info("Select payment status: "+paymentStatus);
        }else logger.info(paymentStatus+" is selected before");
        return this;
    }
    public PartnerOrdersPage selectDeliveryStatusInFilter(DeliveryStatus deliveryStatus){
        String xpath = dynamic_loc_filter_btnDeliveryStatus.formatted(deliveryStatus);
        By selectElement = By.xpath(xpath);
        if(!common.getAttribute(selectElement,"class").contains("selected")){
            common.click(selectElement);
            logger.info("Select delivery status: "+deliveryStatus);
        }else logger.info(deliveryStatus+" is selected before");
        return this;
    }
    public PartnerOrdersPage selectPartnerStatusInFilter(PartnerStatus partnerStatus){
        String xpath = dynamic_loc_filter_btnPartnerStatus.formatted(partnerStatus);
        By selectElement = By.xpath(xpath);
        if(!common.getAttribute(selectElement,"class").contains("selected")){
            common.click(selectElement);
            logger.info("Select partner status: "+partnerStatus);
        }else logger.info(partnerStatus+" is selected before");
        return this;
    }
    public PartnerOrdersPage filterByApproveStatus(ApproveStatus approveStatus){
        clickOnFilter();
        selectApproveStatusInFilter(approveStatus);
        clickOnDoneFilter();
        new HomePage(driver).waitTillSpinnerDisappear1();
        common.sleepInMiliSecond(1000);
        return this;
    }
    public PartnerOrdersPage selectAnOrder(){
        common.click(loc_ckbSelectAll);
        List<WebElement> selectOrderCheckboxList = common.getElements(loc_lst_ckbSelectOrder);
        for(int i = 1;i< selectOrderCheckboxList.size();i++){
            common.uncheckTheCheckboxOrRadio(loc_lst_ckbSelectOrderValue,loc_lst_ckbSelectOrder,i);
        }
        logger.info("Select order 0");
        common.sleepInMiliSecond(1000, "wait set checked to checkbox");
        return this;
    }
    public PartnerOrdersPage clickOnSelectActionLink(){
        common.click(loc_lnkSelectAction);
        logger.info("Click on Select Action link.");
        return this;
    }
    public PartnerOrdersPage clickOnApprove(){
        common.click(loc_lst_btnApproveReject,0);
        logger.info("Click on Approve");
        return this;
    }
    public PartnerOrdersPage clickOnReject(){
        common.click(loc_lst_btnApproveReject,1);
        logger.info("Click on Reject");
        return this;
    }
    public PartnerOrdersPage selectTabCommissionByRevenue(){
        common.click(loc_lst_tabCommissionByProductAndRevenue,1);
        logger.info("Select Commission by Revenue tab.");
        return this;
    }
    public boolean hasViewOrderList(){
        return allPermissions.getAffiliate().getDropshipOrders().isViewOrdersList();
    }
    public boolean hasApproveCommission(){
        return allPermissions.getAffiliate().getDropshipOrders().isApproveCommission();
    }
    public boolean hasRejectCommission(){
        return allPermissions.getAffiliate().getDropshipOrders().isRejectCommission();
    }
    public PartnerOrdersPage navigateByUrl(){
        String url = Links.DOMAIN + "/affiliate/order";
        common.navigateToURL(url);
        logger.info("Navigate to url: "+url);
        common.sleepInMiliSecond(500);
        return this;
    }
    public PartnerOrdersPage approveAnOrder(){
        selectAnOrder();
        clickOnSelectActionLink();
        clickOnApprove();
        new ConfirmationDialog(driver).clickOKBtn();
        return this;
    }
    public PartnerOrdersPage rejectAnOrder(){
        selectAnOrder();
        clickOnSelectActionLink();
        clickOnReject();
        new ConfirmationDialog(driver).clickOKBtn();
        return this;
    }
    public PartnerOrdersPage clickOnDateFilter(){
        common.click(loc_dtpDateFilter);
        logger.info("Click on Date Filter");
        return this;
    }
    public PartnerOrdersPage clickOnResetBtn(){
        common.click(loc_dtpDateFilter_btnReset);
        logger.info("Click on reset button on Date picker.");
        return this;
    }
    public PartnerOrdersPage resetDateFilter(){
        clickOnDateFilter();
        clickOnResetBtn();
        new HomePage(driver).waitTillSpinnerDisappear1();
        return this;
    }
    public void verifyViewOrderListPermission(){
        navigateByUrl();
        resetDateFilter();
        List<WebElement> orderList = common.getElements(loc_lstOrderId,3);
        if(hasViewOrderList()){
            assertCustomize.assertTrue(orderList.size()>0,"[Failed] Order list on Commission by product tab should be shown.");
        }else assertCustomize.assertTrue(orderList.isEmpty(),
                "[Failed] Order list on Commission by product tab should be empty, but it show %s orders".formatted(orderList.size()));
        selectTabCommissionByRevenue();
        orderList = common.getElements(loc_lstOrderId,3);
        if(hasViewOrderList()){
            assertCustomize.assertTrue(orderList.size()>0,"[Failed] Order list on Commission by revenue tab should be shown.");
        }else assertCustomize.assertTrue(orderList.isEmpty(),
                "[Failed] Order list on Commission by revenue tab should be empty, but it show %s orders".formatted(orderList.size()));
        logger.info("Verified View order list permission.");
    }
    /**
     *
     * @param isCommisionByProduct true if check permision on Commission By Product tab, false if check permission on Commission By Revenue tab
     */
    public void verifyApproveOrderPermission(boolean isCommisionByProduct){
        if(hasViewOrderList()){
            navigateByUrl();
            resetDateFilter();
            if(isCommisionByProduct) selectTabCommissionByRevenue();
            filterByApproveStatus(ApproveStatus.PENDING);
            common.getElements(loc_lstOrderId,5);
            if(hasApproveCommission()){
                approveAnOrder();
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.order.update.success"),
                            "[Failed] Update success message should be shown, but '%s' is shown.".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else {
                selectAnOrder();
                clickOnSelectActionLink();
                clickOnApprove();
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(new ConfirmationDialog(driver).loc_btnOK),"" +
                        "[Failed] Restricted popup should be shown when confirm to approve order.");
            }
            logger.info("Verified approve order permission.");
        }else logger.info("Don't have View order list permission, so no need check Approve order permission.");
    }

    /**
     *
     * @param isCommisionByProduct true if check permision on Commission By Product tab, false if check permission on Commission By Revenue tab
     */
    public void verifyRejectOrderPermission(boolean isCommisionByProduct){
        if(hasViewOrderList()){
            navigateByUrl();
            resetDateFilter();
            if(!isCommisionByProduct) selectTabCommissionByRevenue();
            filterByApproveStatus(ApproveStatus.PENDING);
            common.getElements(loc_lstOrderId,5);
            if(hasRejectCommission()){
                rejectAnOrder();
                common.sleepInMiliSecond(1000);
                String rejectingMessage = common.getText(loc_lblRejectingMessage);
                try {
                    assertCustomize.assertEquals(rejectingMessage, PropertiesUtil.getPropertiesValueByDBLang("affiliate.order.reject.rejectingMessage"),
                            "[Failed] Rejecting message should be shown, but '%s' is shown.".formatted(rejectingMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else {
                selectAnOrder();
                clickOnSelectActionLink();
                clickOnReject();
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(new ConfirmationDialog(driver).loc_btnOK),"" +
                        "[Failed] Restricted popup should be shown when confirm to reject order.");
            }
            logger.info("Verified reject order permission with isCommisionByProduct = "+isCommisionByProduct);
        }else logger.info("Don't have View order list permission, so no need check Reject order permission.");
    }
    public PartnerOrdersPage completeVerifyStaffPermissionPartnerPage() {
//        logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
//        if (assertCustomize.getCountFalse() > 0) {
//            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
//        }
        AssertCustomize.verifyTest();
        return this;
    }
    public PartnerOrdersPage checkDropshipOrderPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        logger.info("Dropship order permission: "+allPermissions.getMarketing().getBuyLink());
        verifyViewOrderListPermission();
        verifyApproveOrderPermission(true);
        verifyApproveOrderPermission(false);
        verifyRejectOrderPermission(true);
        verifyRejectOrderPermission(false);
        completeVerifyStaffPermissionPartnerPage();
        return this;
    }
}
