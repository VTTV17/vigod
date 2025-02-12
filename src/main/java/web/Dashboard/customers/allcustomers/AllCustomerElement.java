package web.Dashboard.customers.allcustomers;

import org.openqa.selenium.By;

public class AllCustomerElement {
	By loc_tmpAnchor = By.cssSelector(".link-table-row");
	By loc_chkSelectCustomer = By.cssSelector(".uik-checkbox__label.green");
	By loc_lnkSelectAction = By.xpath("(//span[contains(@class,'gs-fake-link')])[2]");
	By loc_btnDelete = By.xpath("(//div[contains(@class,'actions')])[2]/div[1]");
	By loc_btnExport = By.xpath("(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[2]");
	By loc_btnExportCustomer = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
	By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");
	By loc_icnDownloadExportFile = By.xpath("//*[contains(@class, 'd-desktop-flex')]//*[contains(., 'CUSTOMER')]//following-sibling::*//img[@alt='download-file-blue']");
	By loc_btnImportCustomer = By.xpath("(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[3]");
	By loc_btnMergeCustomer = By.xpath("(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[4]");
	By loc_dlgMergeCustomer = By.cssSelector(".merge-dup-customer-modal");
	By loc_btnPrintBarcode = By.xpath("(//div[contains(@class,'customer-list')]//div[contains(@class,'buttons-row')]//button)[last()]");
	By loc_tmpParent = By.xpath("./parent::*");
	By loc_txtSearchCustomer = By.cssSelector(".customer-list__filter-container .gs-search-box__wrapper .uik-input__input");
	String loc_lblCustomerName = "//div[@class='text-truncate' and text()='%s']";
	String loc_lblCustomerPhone = "//div[@class='full-name' and text()='%s']/ancestor::*/following-sibling::td[2]";
	By loc_tblNames = By.xpath("//div[@class='text-truncate']");
	By loc_btnFilter = By.cssSelector(".btn-filter-action");
	By loc_ddlFilterBranch = By.xpath("(//div[contains(@class,'filter-title')])[1]/following-sibling::div");
	String loc_ddlFilterBranchValues = "//div[@class='uik-select__label' and text()='%s']";
	String loc_ddlFilterBranchNone = "//div[@class='uik-select__label' and (.='Không xác định' or .='None Branch')]";
	By loc_btnCreateCustomer = By.xpath("(//*[contains(@class, 'gs-page-container-max customer-list')]//button[contains(@class,'gs-button__green')])[1]");
	By loc_btnDoneFilter = By.cssSelector(".dropdown-menu-right .gs-button__green");
	By loc_dlgCreateCustomer = By.cssSelector(".create-customer-modal");	
	By loc_dlgImportCustomer = By.cssSelector(".customer-list-import-modal");	
	By loc_dlgPrintBarcode = By.cssSelector(".customer-list-barcode-printer");
	By loc_dlgDeleteCustomer = By.cssSelector(".modal-danger.modal-header");
	
    By loc_chkCustomer(String customerName) {
    	return By.xpath(loc_lblCustomerName.formatted(customerName)+"/parent::td/preceding-sibling::*//div[contains(@class,'uik-checkbox__label')]");
    }
	
}
