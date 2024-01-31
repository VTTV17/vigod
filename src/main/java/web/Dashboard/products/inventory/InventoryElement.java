package web.Dashboard.products.inventory;

import org.openqa.selenium.By;

public class InventoryElement {
    By loc_btnInventoryHistory = By.cssSelector(".inventory-list-page .gs-page-title button");
    By loc_lnkRemainingStock = By.cssSelector("#remaining-item-number");
    By loc_dlgUpdateStock = By.cssSelector(".product-multiple-branch-stock_editor_modal");
    By loc_dlgUpdateStock_btnCancel = By.cssSelector(".product-multiple-branch-stock_editor_modal .gs-button__gray--outline");
}
