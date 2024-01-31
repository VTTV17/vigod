package web.Dashboard.products.transfer;

import org.openqa.selenium.By;

public class TransferElement {
    By loc_tmpRecord = By.xpath("//div[contains(@class,'transfer-management')]//table/tbody/tr");
    By loc_txtSearchRecord = By.cssSelector(".transfer-management .uik-input__input");
    By loc_btnCreateTransfer = By.cssSelector(".transfer-management .gs-button__green");
    By loc_txtNote = By.cssSelector(".transfer-form-editor #text-note");
    By loc_btnSave = By.cssSelector(".transfer-form-editor .gs-button__green");
    By loc_txtSearchProduct = By.cssSelector(".search-box .uik-input__input");
    By loc_btnTransferredQuantity = By.xpath("//*[contains(@class,'transfer-form-editor')]//tbody/tr[1]//input[@inputmode='numeric']");
    By loc_ddlBranches = By.cssSelector(".information [type='button'] .uik-select__valueWrapper");
    By loc_btnShipGoods = By.cssSelector(".transfer-toolbar .btn-save");

    String PRODUCT_NAME_IN_RESULT = "//div[contains(@class,'search-item')]/div/span[position()=1 %s]";
}
