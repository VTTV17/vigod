package web.Dashboard.products.inventory.history;

import org.openqa.selenium.By;

public class InventoryHistoryElement {
    By loc_txtSearchRecord = By.cssSelector(".uik-input__input");
    By loc_tmpRecords = By.xpath("//div[contains(@class,'inventory_history')]//table/tbody/tr");
}
