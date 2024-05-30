package web.Dashboard.orders.delivery.delivery_detail;

import org.openqa.selenium.By;

public class DeliveryDetailElement {
    By loc_btnUpdateStatus = By.cssSelector(".delivery-detail-form-header .gs-button__gray--outline");
    By loc_dlgUpdateStatus = By.cssSelector(".update-status-delivery");
    By loc_dlgUpdateStatus_ddlStatus = By.cssSelector("#status");
    By loc_dlgUpdateStatus_btnConfirm = By.cssSelector(".update-status-delivery .gs-button__green");
    By loc_btnCancelDelivery = By.cssSelector(".delivery-detail-form-header .gs-button__gray--outline");
    By loc_dlgCancelDelivery = By.cssSelector(".cancel-delivery");
    By loc_dlgCancelDelivery_txtNote = By.cssSelector(".cancel-delivery [name='note']");
    By loc_dlgCancelDelivery_btnYes = By.cssSelector(".cancel-delivery .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_btnPrintPackageSlip = By.cssSelector(".gs-content-header-right-el > .gs-button__green--outline");
}
