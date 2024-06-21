package web.Dashboard.marketing.affiliate.order;

import org.openqa.selenium.By;

public class PartnerOrdersElement {
    By loc_lstOrderId = By.cssSelector(".affiliate-order__table td:nth-child(2)");
    By loc_btnFilter = By.cssSelector(".btn-filter-action");
    String dynamic_loc_filter_btnApproveStatus = "//div[contains(@class,'gs-mega-filter-row')][3]//div[@data-test=\"%s\"]";
    String dynamic_loc_filter_btnPaymentStatus = "//div[contains(@class,'gs-mega-filter-row')][1]//div[@data-test=\"%s\"]";
    String dynamic_loc_filter_btnDeliveryStatus = "//div[contains(@class,'gs-mega-filter-row')][2]//div[@data-test=\"%s\"]";
    String dynamic_loc_filter_btnPartnerStatus = "//div[contains(@class,'gs-mega-filter-row')][4]//div[@data-test=\"%s\"]";
    By loc_filter_btnDone = By.cssSelector(".mega-filter-container .gs-button__green");
    By loc_lst_ckbSelectOrder = By.cssSelector(".affiliate-order__table tbody .uik-checkbox__label");
    By loc_lst_ckbSelectOrderValue = By.cssSelector(".affiliate-order__table tbody input");
    By loc_ckbSelectAll = By.xpath("//input[@name='check_all']/following-sibling::div");
    By loc_lnkSelectAction = By.cssSelector(".gs-dropdown-action");
    By loc_lst_btnApproveReject = By.cssSelector(".actions div");
//    By loc_lst_tabCommissionByProductAndRevenue = By.cssSelector(".tab-commission-drop-ship span");
    By loc_lblRejectingMessage = By.xpath("//div[contains(@class,'affiliate-order__table')]/preceding-sibling::div/span");
    By loc_dtpDateFilter = By.cssSelector(".date-ranger-picker");
    By loc_dtpDateFilter_btnReset = By.cssSelector(".cancelBtn");
    By loc_tab_dropshipReseller = By.xpath("//div[contains(@class,'affiliate-tab__tab__button')]");
    By loc_btnExport = By.cssSelector(".gss-content-header .gs-button__green");
    By loc_lst_btnExportOption = By.cssSelector(".uik-dropdown-item__wrapper");
    By loc_dlgExportOrder_ckbSelectAllReseller = By.cssSelector("[name='check-all-selected']");
    By loc_dlgExportOrder_lblSelectAllReseller = By.xpath("//input[@name='check-all-selected']/following-sibling::label");
    By loc_dlgExportOrder_lst_lblResellerName = By.xpath("//input[@name='store-reseller']//following-sibling::label");
    By loc_dlgExportOrder_btnExport = By.cssSelector(".modal-footer .gs-button__green");

}
