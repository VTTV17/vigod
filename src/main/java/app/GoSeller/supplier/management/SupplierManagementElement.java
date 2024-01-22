package app.GoSeller.supplier.management;

import org.openqa.selenium.By;

public class SupplierManagementElement {
    By ADD_ICON = By.xpath("//*[contains(@resource-id, 'ivActionBarIconRight')]");
    By SEARCH_BOX = By.xpath("//*[contains(@resource-id, 'edtSupplierSearch')]");
    By SUPPLIER_CODE = By.xpath("//*[contains(@resource-id, 'tvSupplierCode')]");
    By SUPPLIER_NAME = By.xpath("//*[contains(@resource-id, 'tvSupplierName')]");
    By SUPPLIER_EMAIL = By.xpath("//*[contains(@resource-id, 'tvEmail')]");
    By SUPPLIER_PHONE = By.xpath("//*[contains(@resource-id, 'tvPhoneNumber')]");
    By DELETE_BTN = By.xpath("//*[contains(@resource-id, 'rlDelete')]");

    By CONFIRM_POPUP_OK_BTN = By.xpath("//*[contains(@resource-id, 'tvRightButton')]");
}
