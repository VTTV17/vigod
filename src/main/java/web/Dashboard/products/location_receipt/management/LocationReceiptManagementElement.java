package web.Dashboard.products.location_receipt.management;

import org.openqa.selenium.By;

public class LocationReceiptManagementElement {
    By loc_btnImportInBulk = By.cssSelector(".location-receipt-management .gs-button__green--outline");
    By loc_dlgImportProductLotOrLocationInBulk = By.cssSelector(".location-receipt-list-import-modal");
    String str_dlgImportProductLotOrLocationInBulk_chkBranch = "//span[contains(text(), '%s')]/parent::div/preceding-sibling::input";
    By loc_dlgImportProductLotOrLocationInBulk_icnRemove = By.cssSelector(".import-csv__btn-delete > i");

    By loc_dlgImportProductLotOrLocationInBulk_btnDragAndDrop = By.cssSelector(".import-csv__drop-zone > input");

    By loc_dlgImportProductLotOrLocationInBulk_btnImport = By.cssSelector(".location-receipt-list-import-modal .gs-button__green");
    By loc_prgImportProgressBar = By.cssSelector(".text-progress-assign");
}
