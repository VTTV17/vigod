package web.Dashboard.orders.return_orders.return_order_detail;

import org.openqa.selenium.By;

public class ReturnOrderDetailElement {
    By loc_lnkSelectActions = By.cssSelector(".gs-dropdown-action .gs-fake-link");
    By loc_ddvEditActions = By.cssSelector(".gs-dropdown-action .actions div:nth-child(1)");
    By loc_ddvCancelActions = By.cssSelector(".gs-dropdown-action .actions div:last-child");
    By loc_dlgConfirmCancel = By.cssSelector(".gs-cancel-note-modal");
    By loc_dlgConfirmCancel_btnOK = By.cssSelector(".gs-cancel-note-modal .gs-button__red");
    By loc_btnComplete = By.cssSelector(".gss-content-header .gs-button__green");
    By loc_dlgConfirmComplete = By.cssSelector(".complete-return-order-modal");
    By loc_dlgConfirmComplete_btnOK = By.cssSelector(".complete-return-order-modal .gs-button__green");
    By btnConfirmPayment = By.cssSelector(".payment-method .gs-button__green");
    By loc_dlgConfirmPayment = By.cssSelector(".confirmation-payment-modal");
    By loc_dlgConfirmPayment_btnAdd = By.cssSelector(".confirmation-payment-modal .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_dlgToastError = By.cssSelector(".Toastify__toast--error");
}
