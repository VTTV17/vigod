package web.Dashboard.orders.orderlist.order_detail;

import org.openqa.selenium.By;

public class OrderDetailElement {
    By loc_btnPrintReceipt = By.cssSelector("#dropdownPrintButton");
    By loc_btnConfirmOrder = By.id("btn-readyToShip");
    public static By loc_btnEditOrder = By.cssSelector("#btn-edit");
    By loc_btnDeliveredOrder = By.xpath("//*[@id = 'btn-cancelOrder']/preceding-sibling::button[1]");
    By loc_btnCancelOrder = By.cssSelector("#btn-cancelOrder");
    By loc_btnShipmentOK = By.cssSelector(".ready-to-ship-confirm__btn-wrapper .gs-button__green");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
    By loc_lblDeliveryInformation = By.cssSelector(".delivery-information-list");
    By loc_lblAddReceipt = By.xpath("//*[contains(text(),'ADD')]");
    By loc_lblGetReceipt = By.xpath("//*[contains(text(),'GET')]");
    By loc_dlgConfirmCancelOrder = By.cssSelector(".modal-cancel-order-confirm");
    By loc_dlgConfirmCancelOrder_txtReason = By.cssSelector("[name= 'reason']");
    By loc_dlgConfirmCancelOrder_btnConfirm = By.cssSelector(".modal-cancel-order-confirm .gs-button__green");
    By loc_dlgConfirmDeliveredOrder = By.cssSelector(".modal-delivered-order");
    By loc_dlgConfirmDeliveredOrder_btnConfirm = By.cssSelector(".modal-delivered-order .gs-button__green");


}
