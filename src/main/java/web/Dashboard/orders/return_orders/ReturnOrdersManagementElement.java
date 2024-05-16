package web.Dashboard.orders.return_orders;

import org.openqa.selenium.By;

public class ReturnOrdersManagementElement {
    By loc_btnCreateReturnOrder = By.xpath("(//div[contains(@class,'gs-page-container-max return-order-list')]//button[contains(@class,'gs-button__green')])[1]");
    By loc_btnExport = By.xpath("(//div[contains(@class,'gs-page-container-max return-order-list')]//button[contains(@class,'gs-button__green')])[2]");
    By loc_btnExportReturnOrder = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
    By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");
}
