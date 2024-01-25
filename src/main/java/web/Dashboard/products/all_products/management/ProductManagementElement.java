package web.Dashboard.products.all_products.management;

import lombok.Getter;
import org.openqa.selenium.By;

public class ProductManagementElement {
    @Getter
    By loc_btnCreateProduct = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[1]");
    By loc_btnExport = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[2]");
    By loc_ddlExportActions = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[2]/following-sibling::div/button");
    By loc_dlgExportProductListingFile_btnExport = By.cssSelector(".modal-footer .gs-button__green");
    By loc_btnImport = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[3]");
    By loc_ddlImportActions = By.xpath("(//*[contains(@class, 'gs-button__green gs-button--undefined')])[3]/following-sibling::div/button");
    By loc_btnPrintBarcode = By.cssSelector(".gs-button__green--outline");
    By loc_chkSelectAll = By.cssSelector("thead input");
    By loc_lnkSelectAction = By.cssSelector(".actions");
    By loc_icnDownloadExportFile = By.xpath("//*[contains(@class, 'd-desktop-flex')]//*[contains(text(), 'EXPORT_PRODUCT')]//following-sibling::div/div/img[@alt='download-file-blue']");
    By loc_dlgUpdatePrice_txtCostPrice = By.xpath("//*[contains(@name,'costPrice')]//parent::div//parent::div//preceding-sibling::input");

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
    By loc_dlgUpdatePrice_btnClose = By.cssSelector(".modal-footer .gs-button__gray--outline");
}
