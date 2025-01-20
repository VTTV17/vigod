package web.Dashboard.cashbook;

import org.openqa.selenium.By;

public class CashbookElement {

	By loc_icnDataLoading = By.cssSelector("[data-loading='true']");
	
	By loc_lblPageTitle = By.cssSelector("[role=heading]");
	By loc_lblTotalRecordCount = By.cssSelector(".gs-page-title span.uik-tag__wrapper span");
	By loc_lblCashbookSummary = By.cssSelector(".cashbook-summary .number");
	By loc_tltCashbookSummary = By.cssSelector(".tippy-tooltip-content");
	By loc_tblCashbookRecord = By.xpath("//div[contains(@class,'cashbook-list')]//table/tbody/tr");
	By loc_tblTableTitle = By.xpath("//div[contains(@class,'cashbook-list')]//table/thead/tr");
	By loc_txtSearchRecord = By.cssSelector(".uik-input__input");
	By loc_btnCreateReceipt = By.cssSelector(".cashbook-management .gss-content-header--undefined button:nth-of-type(2)");
	By loc_btnCreatePayment = By.cssSelector(".cashbook-management .gss-content-header--undefined button:nth-of-type(3)");
	By loc_dtpPrincipleTimeRange = By.cssSelector(".date-ranger-picker");
	By loc_btnResetDatePicker = By.cssSelector(".daterangepicker .btn-default");
	By loc_btnFilter = By.cssSelector(".btn-filter-action");
	By loc_frmFilterContainer = By.cssSelector(".mega-filter-container .dropdown-menu-right");
	By loc_frmFilterCondition = By.xpath("//div[contains(@class,'gs-mega-filter-row-select')]");
	By loc_btnFilterDone = By.cssSelector(".gs-button__green.gs-button--small");
	By loc_lblCreateReceiptModalDialog = By.cssSelector(".modal-title");
	By loc_ddlSenderGroup = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[1]");
	By loc_ddlSource = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[2]");
	By loc_ddlBranch = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[3]");
	By loc_ddlPaymentMethod = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[4]");
	By loc_ddlSenderName = By.cssSelector("[class*=gs-dropdown-search]");
	By loc_txtSearchSenderName = By.cssSelector(".search-box input");
	By loc_txtAmount = By.id("amount");
	By loc_txtNote = By.id("note");
	By loc_chkAccounting = By.name("accounting");

	By loc_tmpRandomReceipt = By.xpath("//*[@class='transaction-code' and contains(.,'RN')]");
	By loc_tmpRandomPayment = By.xpath("//*[@class='transaction-code' and contains(.,'PN')]");
	
	String searchResultXpath = "//div[contains(@class,'search-item') %s]";
	String conditionFilterDropdownXpath = "//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()=\"%s\"]";
	
	By loc_icnEmptyWallet = By.xpath("//img[contains(@src,'cashbook_empty.svg')]");
}
