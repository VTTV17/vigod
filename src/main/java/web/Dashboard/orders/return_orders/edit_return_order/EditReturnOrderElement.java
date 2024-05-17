package web.Dashboard.orders.return_orders.edit_return_order;

import org.openqa.selenium.By;

public class EditReturnOrderElement {
    By loc_btnSave = By.cssSelector(".return-order-form-header .gs-button__green");
    By loc_txtQuantity = By.cssSelector(".quantity input");
    By loc_lblRemainingQuantity = By.cssSelector(".quantity div > span");
    By loc_imgSelectIMEI = By.cssSelector(".quantity a img");
    By loc_dlgSelectIMEI = By.cssSelector(".managed-inventory-POS-modal");
    By loc_dlgSelectIMEI_lblSelectedMax = By.cssSelector(".selected-max-POS");
    By loc_dlgSelectIMEI_icnRemoveSelectedIMEI = By.cssSelector(".code__multi-value__remove");
    By loc_dlgSelectIMEI_lstIMEI = By.cssSelector(".code p");
    By loc_dlgSelectIMEI_btnSave = By.cssSelector(".managed-inventory-POS-modal .gs-button__green");
    By loc_chkReceivedGoods = By.cssSelector("[name= 'receivedGoods']");
}
