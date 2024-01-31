package web.Dashboard.products.inventory.history;

import org.openqa.selenium.By;

public class InventoryHistoryElement {
    By loc_txtSearchRecord = By.cssSelector(".uik-input__input");
    By loc_tmpRecords = By.xpath("//div[contains(@class,'inventory_history')]//table/tbody/tr");
    By loc_btnExport = By.cssSelector(".inventory_history  .gs-button__green");
    /**
     * 0: Export Inventory
     * 1: Export History
     */
    By loc_ddvExportActions = By.cssSelector(".inventory_history .uik-menuDrop__list > button");
    By loc_icnDownloadExportedFile = By.xpath("//*[contains(@class, 'd-desktop-flex')]//*[contains(text(), 'EXPORT_INVENTORY_HISTORY')]//following-sibling::div/div/img[@alt='download-file-blue']");
}
