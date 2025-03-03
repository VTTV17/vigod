package web.Dashboard.orders.return_orders.return_order_management;

import org.openqa.selenium.By;

public class ReturnOrdersManagementElement {
    By loc_btnCreateReturnOrder = By.cssSelector(".return-order-list .gs-content-header-right-el > .gs-button__green");
    By loc_btnExport = By.cssSelector(".return-order-list .gs-content-header-right-el > div> .gs-button__green");
    By loc_btnExportReturnOrder = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
    By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");
    By loc_dlgSelectOrderToReturn = By.cssSelector(".create-return-order-modal");
    By loc_dlgSelectOrderToReturn_txtSearch = By.cssSelector(".n-filter-container input");
    By loc_dlgSelectOrderToReturn_ddvSelectedSearchType = By.cssSelector(".create-return-order-modal .uik-select__valueWrapper");
    By loc_dlgSelectOrderToReturn_ddvOrderNumberSearchType = By.cssSelector(".create-return-order-modal  .uik-select__option:nth-child(1) .uik-select__label");
    String str_tblOrder_orderId = "//*[contains(@class, 'create-return-order-modal')] //*[text() = '%s']";
    By loc_btnCloseDialog = By.cssSelector("button.close");
}
