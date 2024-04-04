package web.Dashboard.products.transfer.crud;

import org.openqa.selenium.By;

public class TransferElement {
    By loc_tmpRecord = By.xpath("//div[contains(@class,'transfer-management')]//table/tbody/tr");
    By loc_txtSearchRecord = By.cssSelector(".transfer-management .uik-input__input");

    By loc_txtNote = By.cssSelector(".transfer-form-editor #text-note");
    By loc_btnSave = By.cssSelector(".transfer-form-editor .gs-button__green");
    By loc_txtSearchProduct = By.cssSelector(".search-box .uik-input__input");
    By loc_txtTransferredQuantity = By.xpath("//*[contains(@class,'transfer-form-editor')]//tbody/tr[1]//input[@inputmode='numeric']");
    By loc_ddlBranches = By.cssSelector(".information [type='button'] .uik-select__valueWrapper");
    By loc_btnShipGoods = By.cssSelector(".transfer-toolbar .btn-save");
    By loc_btnReceiveGoods = By.cssSelector(".transfer-toolbar .btn-save");

    String PRODUCT_NAME_IN_RESULT = "//div[contains(@class,'search-item')]/div/span[position()=1 %s]";
    String productBarcodeInResult = "//*[@class=' product-item']//*[text()='%s']";
    By results = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'product-item')]");

    By name = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=1]");
    By barcode = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=2]");
    By variation = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=3]");
    By inventory = By.xpath(".//div[contains(@class,'search-item')]/span/p[position()=1]");
    By unit = By.xpath(".//div[contains(@class,'search-item')]/span/p[position()=2]");
    String branchName = "//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']";
    By loc_lnkSelectIMEI = By.xpath("//*[contains(@name, 'transferredStock')]//ancestor::*[@class='number']/following-sibling::span");
    String str_imeiLocator = "(//div[@class='code in-purchase'])[2]/div[@class='content']/p[text()='%s']";
    String str_record = "[href='/product/transfer/wizard/%s']";
    By searchLoadingIcon = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'loading')]");
    String str_branch = "//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']";
    By loc_icnRemove = By.cssSelector(".delete");
    By loc_lnkSelectAction = By.cssSelector(".gs-dropdown-action");
    /**
     * 0: Edit
     * 1: Cancel
     */
    By loc_ddlListActions = By.cssSelector(".actions > div");

    By loc_dlgConfirmation = By.cssSelector(".transfer-wizard-cancel-modal");
    By loc_dlgConfirmation_btnOK = By.cssSelector(".gs-button__red");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");

}
