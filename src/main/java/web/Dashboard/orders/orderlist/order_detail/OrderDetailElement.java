package web.Dashboard.orders.orderlist.order_detail;

import org.openqa.selenium.By;

public class OrderDetailElement {
    By loc_btnPrintReceipt = By.cssSelector("#dropdownPrintButton");
    By loc_btnConfirmOrder = By.id("btn-readyToShip");
    public static By loc_btnEditOrder = By.cssSelector("#btn-edit");
    By loc_btnDeliveredOrder = By.cssSelector("[class='gs-button  gs-button__white gs-button--undefined ']:not(#btn-cancelOrder)");
    By loc_btnCancelOrder = By.cssSelector("#btn-cancelOrder");
    By loc_btnShipmentOK = By.cssSelector(".ready-to-ship-confirm__btn-wrapper .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_dlgToastError = By.cssSelector(".Toastify__toast--error");
    By loc_lblDeliveryInformation = By.cssSelector(".delivery-information-list");
    By loc_lblAddReceipt = By.xpath("//*[contains(text(),'ADD')]");
    By loc_lblGetReceipt = By.xpath("//*[contains(text(),'GET')]");
    By loc_dlgConfirmCancelOrder = By.cssSelector(".modal-cancel-order-confirm");
    By loc_dlgConfirmCancelOrder_txtReason = By.cssSelector("[name= 'reason']");
    By loc_dlgConfirmCancelOrder_btnConfirm = By.cssSelector(".modal-cancel-order-confirm .gs-button__green");
    By loc_dlgConfirmDeliveredOrder = By.cssSelector(".modal-delivered-order");
    By loc_dlgConfirmDeliveredOrder_btnConfirm = By.cssSelector(".modal-delivered-order .gs-button__green");
    By loc_btnPrintShippingLabel = By.cssSelector(".shipping-label");
    By loc_btnPrintOrderReceipt = By.cssSelector(".order-receipt");
    By loc_dlgPrintOrderReceipt = By.cssSelector(".gs-select-print-size-modal");
    By loc_txtTag = By.cssSelector("#container-selected-tag > input");
    By loc_lnkCreateNewTag = By.cssSelector("#popup-select-tag  span");
    By loc_ddlTagOptions = By.cssSelector(".option-tag");
    By loc_icnRemoveSelectedTag = By.cssSelector(".item-select-tag > img");
    By loc_lnkManageAllTags = By.cssSelector(".text-manage-all");
    By loc_dlgTagManagementOverview = By.cssSelector(".manage-all-tags-modal");
    By loc_dlgTagManagementOverview_txtTag = By.cssSelector(".manage-all-tags-modal input");
    By loc_dlgTagManagementOverview_btnCreateNewTag = By.cssSelector(".manage-all-tags-modal .gs-button__green--outline");
    By loc_dlgTagManagementOverview_icnDeleteTag = By.cssSelector(".manage-all-tags-modal img");
    By loc_dlgTagManagementOverview_btnClose = By.cssSelector(".manage-all-tags-modal .gs-button__white");
    By loc_btnConfirmPayment = By.cssSelector(".paymentSummary button");
    By loc_dlgConfirmPayment = By.cssSelector(".confirmation-payment-modal");
    By loc_dlgConfirmPayment_btnAdd = By.cssSelector(".confirmation-payment-modal .gs-button__green");
}
