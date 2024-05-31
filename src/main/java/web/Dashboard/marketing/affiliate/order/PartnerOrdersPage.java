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
import utilities.utils.FileUtils;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.exporthistory.ExportHistoryPage;
import web.Dashboard.home.HomePage;
import web.Dashboard.marketing.affiliate.general.AffiliateGeneral;

import java.util.List;

public class PartnerOrdersPage extends PartnerOrdersElement{
    final static Logger logger = LogManager.getLogger(PartnerOrdersPage.class);
    UICommonAction common;
    WebDriver driver;
    AllPermissions allPermissions;
    AssertCustomize assertCustomize;
    AffiliateGeneral affiliateGeneral;
    public PartnerOrdersPage(WebDriver driver){
        this.driver = driver;
        common = new UICommonAction(driver);
        assertCustomize  = new AssertCustomize(driver);
        affiliateGeneral = new AffiliateGeneral(driver);
    }
    public PartnerOrdersPage clickOnResellerTab(){
        String currentTab = common.getText(loc_tab_dropshipReseller, 0);
        String resellerTabName="";
        try {
            resellerTabName = PropertiesUtil.getPropertiesValueByDBLang("affiliate.information.resellerTab");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!currentTab.equals(resellerTabName)){
            common.click(loc_tab_dropshipReseller,1);
            logger.info("Click on Reseller tab.");
            return this;
        }
        logger.info("Current tab is Reseller");
        return this;
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
    public boolean hasViewOrderList(){
        return allPermissions.getAffiliate().getDropshipOrders().isViewOrdersList();
    }
    public boolean hasApproveCommission(){
        return allPermissions.getAffiliate().getDropshipOrders().isApproveCommission();
    }
    public boolean hasRejectCommission(){
        return allPermissions.getAffiliate().getDropshipOrders().isRejectCommission();
    }
    public boolean hasViewResellerOrderList(){
        return allPermissions.getAffiliate().getResellerOrders().isViewOrdersList();
    }
    public boolean hasExportOrderReseller(){
        return allPermissions.getAffiliate().getResellerOrders().isExportOrderReseller();
    }
    public boolean hasDownloadExportData(){
        return allPermissions.getAffiliate().getResellerOrders().isDownloadExportData();
    }
    public boolean hasApproveCommissionReseller(){
        return allPermissions.getAffiliate().getResellerOrders().isApproveCommission();
    }
    public boolean hasRejectCommissionReseller(){
        return allPermissions.getAffiliate().getResellerOrders().isRejectCommission();
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
    public PartnerOrdersPage clickOnExport(){
        common.click(loc_btnExport);
        logger.info("Click on Export button.");
        return this;
    }
    public PartnerOrdersPage clickOnExportOrder(){
        common.click(loc_lst_btnExportOption,0);
        common.sleepInMiliSecond(500);
        logger.info("Click on Export Order");
        return this;
    }
    public ExportHistoryPage clickOnExportHistory(){
        common.click(loc_lst_btnExportOption,1);
        logger.info("Click on Export history.");
        return new ExportHistoryPage(driver);
    }
    public PartnerOrdersPage clickOnSelectAllReseller(){
        common.click(loc_dlgExportOrder_lblSelectAllReseller);
        logger.info("Click on Select all reseller when export reseller's order");
        return this;
    }
    public PartnerOrdersPage clickExportOnSellectResellerPopup(){
        common.click(loc_dlgExportOrder_btnExport);
        logger.info("Click on Export button onf Select Reseller popup.");
        return this;
    }
    public void verifyViewOrderListPermission(boolean isDropshipTab){
        navigateByUrl();
        if(!isDropshipTab) clickOnResellerTab();
        resetDateFilter();
        List<WebElement> orderList = common.getElements(loc_lstOrderId,3);
        boolean hasViewOrderListPers = isDropshipTab? hasViewOrderList(): hasViewResellerOrderList();
        if(hasViewOrderListPers){
            assertCustomize.assertTrue(orderList.size()>0,"[Failed] Order list on Commission by product tab should be shown.");
        }else assertCustomize.assertTrue(orderList.isEmpty(),
                "[Failed] Order list on Commission by product tab should be empty, but it show %s orders".formatted(orderList.size()));
        if(isDropshipTab) {
            affiliateGeneral.selectTabCommissionByRevenue();
            orderList = common.getElements(loc_lstOrderId, 3);
            if (hasViewOrderListPers) {
                assertCustomize.assertTrue(orderList.size() > 0, "[Failed] Order list on Commission by revenue tab should be shown.");
            } else assertCustomize.assertTrue(orderList.isEmpty(),
                    "[Failed] Order list on Commission by revenue tab should be empty, but it show %s orders".formatted(orderList.size()));
        }
        logger.info("Verified View order list permission.");
    }
    /**
     *
     * @param isCommisionByProduct true if check permision on Commission By Product tab, false if check permission on Commission By Revenue tab
     */
    public void verifyApproveOrderPermission(boolean isDropshipTab, boolean isCommisionByProduct){
        boolean hasViewOrderListPers = isDropshipTab? hasViewOrderList(): hasViewResellerOrderList();
        boolean hasApprovePermissionPers = isDropshipTab? hasApproveCommission(): hasApproveCommissionReseller();

        if(hasViewOrderListPers){
            navigateByUrl();
            if(!isDropshipTab) clickOnResellerTab();
            resetDateFilter();
            if(isCommisionByProduct) affiliateGeneral.selectTabCommissionByRevenue();
            filterByApproveStatus(ApproveStatus.PENDING);
            common.getElements(loc_lstOrderId,5);
            if(hasApprovePermissionPers){
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
    public void verifyRejectOrderPermission(boolean isDropshipTab, boolean isCommisionByProduct){
        boolean hasViewOrderListPers = isDropshipTab? hasViewOrderList(): hasViewResellerOrderList();
        boolean hasRejectCommissionPers = isDropshipTab? hasRejectCommission(): hasRejectCommissionReseller();
        if(hasViewOrderListPers){
            navigateByUrl();
            if(!isDropshipTab) clickOnResellerTab();
            resetDateFilter();
            if(!isCommisionByProduct) affiliateGeneral.selectTabCommissionByRevenue();
            filterByApproveStatus(ApproveStatus.PENDING);
            common.getElements(loc_lstOrderId,5);
            if(hasRejectCommissionPers){
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
            logger.info("Verified reject order permission with isDropshipTab = '%s', isCommisionByProduct = '%s'".formatted(isDropshipTab,isCommisionByProduct));
        }else logger.info("Don't have View order list permission, so no need check Reject order permission.");
    }
    public void verifyExportOrderReseller(){
        navigateByUrl();
        clickOnResellerTab();
        clickOnExport();
        clickOnExportOrder();
        clickOnSelectAllReseller();
        if(hasExportOrderReseller()){
            clickExportOnSellectResellerPopup();
            String toastMessage = new HomePage(driver).getToastMessage();
            try {
                assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("affiliate.order.export.successMessage"),
                        "[Failed] Export success message should be shown, but '%s' is shown".formatted(toastMessage));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_dlgExportOrder_btnExport),
                "Restricted popup should be shown when click on Export button on Select Reseller popup.");
        logger.info("Verified Export reseller's order  permission.");
    }
    public void verifyDownloadExportData(){
        navigateByUrl();
        clickOnResellerTab();
        clickOnExport();
        ExportHistoryPage exportHistoryPage = clickOnExportHistory();
        List<WebElement> exportDropshipPartnerFiles = common.getElements(exportHistoryPage.loc_lst_iconDownloadDropshipPartner,3);
        if (exportDropshipPartnerFiles.size()>0) {
            if (hasDownloadExportData()) {
                //Delete old file.
                new FileUtils().deleteFileInDownloadFolder("RESELLER_ORDER");
                //Download new file
                exportHistoryPage.clickOnDownloadResellerOrder();
                common.sleepInMiliSecond(3000, "Waiting for download.");
                assertCustomize.assertTrue(new FileUtils().isDownloadSuccessful("RESELLER_ORDER"), "[Failed] Not found file in download folder.");
            } else {
                logger.info("Click on download icon");
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(exportHistoryPage.loc_lst_iconDownloadDropshipPartner, 0),
                        "[Failed] Restricted page should be shown when click on download icon.");
            }
            logger.info("Verified Download exported reseller order file.");
        }else logger.info("No data to download export dropship partner filed");
    }
    public PartnerOrdersPage checkDropshipOrderPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        logger.info("Dropship order permission: "+allPermissions.getAffiliate().getDropshipOrders());
        verifyViewOrderListPermission(true);
        verifyApproveOrderPermission(true,true);
        verifyApproveOrderPermission(true,false);
        verifyRejectOrderPermission(true,true);
        verifyRejectOrderPermission(true,false);
        AssertCustomize.verifyTest();
        return this;
    }
    public PartnerOrdersPage checkResellerOrderPermission(AllPermissions allPermissions){
        this.allPermissions = allPermissions;
        logger.info("Reseller order permission: "+allPermissions.getAffiliate().getResellerOrders());
        verifyViewOrderListPermission(false);
        verifyExportOrderReseller();
        verifyDownloadExportData();
        verifyApproveOrderPermission(false,false);
        verifyRejectOrderPermission(true,true);
        AssertCustomize.verifyTest();
        return this;
    }
}
