package web.Dashboard.products.all_products.management;

import lombok.Getter;
import org.openqa.selenium.By;

public class ProductManagementElement {
    By loc_tblProductManagement_productRow = By.cssSelector(".gs-table-body-items");
    @Getter
    By loc_btnCreateProduct = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[1]");
    By loc_btnExport = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[2]");
    /**
     * 0: Export all products
     * 1: Export wholesale products
     * 2: Export history
     */
    By loc_ddlExportActions = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[2]/following-sibling::div/button");
    By loc_dlgExportProductListingFile_btnExport = By.cssSelector(".modal-footer .gs-button__green");
    By loc_btnImport = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[3]");
    By loc_ddlImportActions = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[3]/following-sibling::div/button");
    By loc_dlgImport = By.cssSelector(".item-list-import-modal");
    String str_dlgImport_chkBranch = "//span[contains(text(), '%s')]/parent::div/preceding-sibling::input";
    By loc_dlgImport_btnDragAndDrop = By.cssSelector(".item-list-import-modal__drop-zone input");
    By loc_dlgImport_btnImport = By.cssSelector(".item-list-import-modal .gs-button__green");
    By loc_dlgImport_btnCancel = By.cssSelector(".item-list-import-modal .gs-button__white");
    By loc_prgImportStatus = By.xpath("//*[contains(@class, 'uik-widget-table__wrapper')]/preceding-sibling::div[1]/span");
    By loc_btnPrintBarcode = By.cssSelector(".gs-button__green--outline");
    By loc_dlgPrintBarcode = By.cssSelector(".product-list-barcode-printer");
    By loc_dlgPrintBarcode_btnCancel = By.cssSelector(".product-list-barcode-printer .gs-button__gray--outline");
    By loc_chkSelectAll = By.cssSelector("thead input");
    By loc_lnkSelectAction = By.cssSelector(".actions");
    By loc_icnDownloadExportFile = By.xpath("//*[contains(@class, 'd-desktop-flex')]//*[contains(text(), 'EXPORT_PRODUCT')]//following-sibling::div/div/img[@alt='download-file-blue']");
    /**
     * 0: Clear stock
     * 1: Delete
     * 2: Deactivate
     * 3: Activate
     * 4: Update stock
     * 5: Update Tax
     * 6: Display out of stock
     * 7: Update selling platform
     * 8: Update price
     * 9: Set stock alert
     * 10: Manage stock by Lot-date
     */
    By loc_ddlListActions = By.cssSelector(".actions > div");
    By loc_dlgUpdatePrice = By.cssSelector("#multi-price .modal-content");
    By loc_dlgUpdatePrice_ddvSelectedPriceType = By.cssSelector(".modal-body .uik-select__arrowWrapper");

    /**
     * 0: Listing price
     * 1: Selling price
     * 2: Cost price
     */
    By loc_dlgUpdatePrice_ddlPriceType = By.cssSelector(".modal-body .uik-select__optionList > .uik-select__option");
    By loc_dlgUpdatePrice_txtApplyAll = By.xpath("//*[@id='apply-all']/parent::div/parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_btnApplyAll = By.cssSelector(".modal-body .gs-button__blue");
    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[contains(@name,'orgPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[contains(@name,'newPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_txtCostPrice = By.xpath("//*[contains(@name,'costPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_btnUpdate = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgUpdatePrice_btnClose = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgConfirm_icnClose = By.cssSelector(".modal.fade.show .close");
    By loc_dlgConfirmManageProductByLotDate = By.cssSelector(".bulk-update-lot-type-modal");
    By loc_dlgConfirmManageProductByLotDate_btnYes = By.cssSelector(".bulk-update-lot-type-modal");
}
