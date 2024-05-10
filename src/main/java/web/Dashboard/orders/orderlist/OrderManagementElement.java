package web.Dashboard.orders.orderlist;

import org.openqa.selenium.By;

public class OrderManagementElement {
    By loc_tmpRecords = By.cssSelector(".transaction-row");
    By loc_btnExport = By.cssSelector(".order-list-management .button-v2");
    By loc_btnExportOrder = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
    By loc_btnExportOrderByProduct = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");
    By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[last()]");
    By loc_btnConfirmOrder = By.id("btn-readyToShip");
    By loc_btnShipmentOK = By.cssSelector(".ready-to-ship-confirm__btn-wrapper .gs-button__green");
    By loc_btnDelivered = By.cssSelector(".gs-button__green");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
}
