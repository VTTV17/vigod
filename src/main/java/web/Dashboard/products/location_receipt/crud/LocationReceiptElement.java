package web.Dashboard.products.location_receipt.crud;

import org.openqa.selenium.By;

public class LocationReceiptElement {
    By loc_btnDelete = By.cssSelector(".gss-content-header--action-btn--group .gs-button__red--outline");
    By loc_btnSaveAsDraft = By.cssSelector(".gss-content-header--action-btn--group .gs-button__green--outline");
    By loc_btnComplete = By.cssSelector(".gss-content-header--action-btn--group .btn-save");
    By loc_ddvSelectedBranch = By.cssSelector("#selectedBranch");
    By loc_ddvBranch = By.cssSelector("#selectedBranch option");
    String str_ddvBranch = "#selectedBranch > [value='%s']";
    By loc_ddvSearchType = By.cssSelector(".search select");
    By loc_txtSearchProduct = By.cssSelector(".search-input-cpn input");
    String str_ddvProduct = "//*[@class = 'info']/div/*[text() = '%s']";
    By loc_lnkSelectLot = By.cssSelector(".action-select-lot p");
    By loc_lnkSelectLocation = By.cssSelector("tr > td > .action-select");
    By loc_icnRemove = By.cssSelector("td > [alt='remove']");
    By loc_dlgSelectLotDate = By.cssSelector(".select-lot-modal");
    By loc_dlgSelectLotDate_chkHideExpiredLot = By.cssSelector("#hide-expired-lot");
    By loc_dlgSelectLotDate_txtSearchLot = By.cssSelector(".modal-body > input");
    By loc_dlgSelectLotDate_txtQuantity = By.cssSelector(".get-quantity");
    By loc_dlgSelectLotDate_btnConfirm = By.cssSelector(".select-lot-modal .gs-button__green");
    By loc_dlgSelectLocation = By.cssSelector(".select-location-modal");
    String str_dlgSelectLocation_txtQuantity = "//*[contains(@class, 'name')]/*[string() ='%s']/parent::td/following-sibling::*[contains(@class, 'get-quantity')]/input";
    String loc_dlgSelectLocation_strPage = "//*[@class = 'page-link']/self::node()[string() = '%s']";
    By loc_dlgSelectLocation_btnConfirm = By.cssSelector(".select-location-modal .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_dlgConfirm = By.cssSelector(".confirm-modal");
    By loc_dlgConfirm_BtnYes = By.cssSelector(".confirm-modal .gs-button__green");
}
