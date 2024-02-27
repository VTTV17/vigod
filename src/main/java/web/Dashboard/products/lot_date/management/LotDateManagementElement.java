package web.Dashboard.products.lot_date.management;

import org.openqa.selenium.By;

public class LotDateManagementElement {
    public By loc_btnCreateLotDate = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    public By loc_dlgAddLotDate = By.cssSelector(".lot-editor-modal");
    public By loc_dlgAddLotDate_txtLotName = By.cssSelector("#lot-editor-name");
    public By loc_dlgAddLotDate_txtLotCode = By.cssSelector("#lot-editor-code");
    public By loc_dlgAddLotDate_dtpManufactureDate = By.xpath("(//*[contains(@class,'lot-editor-date-input')])[1]");
    public By loc_dlgAddLotDate_dtpExpiryDate = By.xpath("(//*[contains(@class,'lot-editor-date-input')])[2]");
    public By loc_dlgAddLotDate_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    public By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_icnDelete = By.cssSelector(".gs-component-tooltip i");
    By loc_dlgConfirmDeleteLot = By.cssSelector(".confirm-modal-delete-lot");
    By loc_dlgConfirmDeleteLot_btnYes = By.cssSelector(".confirm-modal-delete-lot .gs-button__green");
}
