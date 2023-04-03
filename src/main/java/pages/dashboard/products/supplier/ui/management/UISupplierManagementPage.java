package pages.dashboard.products.supplier.ui.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.products.supplier.function.management.SupplierManagementPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;

public class UISupplierManagementPage extends UISupplierManagementElement {
	UICommonAction commons;
	WebDriverWait wait;
	public AssertCustomize assertCustomize;
	public int countFail;

	final static Logger logger = LogManager.getLogger(UISupplierManagementPage.class);

	public UISupplierManagementPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
		assertCustomize = new AssertCustomize(driver);
		this.countFail = assertCustomize.countFail;
	}

	void checkHeader(String language) throws Exception {
		// check page title
		String dbTitle = wait.until(visibilityOf(UI_PAGE_TITLE)).getText().split("\n")[0];
		String ppTitle = getPropertiesValueByDBLang("products.supplier.management.header.pageTitle", language);
		countFail = assertCustomize.assertEquals(countFail, dbTitle, ppTitle, "[Failed][Supplier Management] Page title should be %s, but found %s.".formatted(ppTitle, dbTitle));
		logger.info("[UI][%s] Check Supplier Management - Page title.".formatted(language));

		// check Add supplier button
		String dbAddSupplierBtn = wait.until(visibilityOf(UI_HEADER_ADD_SUPPLIER_BTN)).getText();
		String ppAddSupplierBtn = getPropertiesValueByDBLang("products.supplier.management.header.addSupplierBtn", language);
		countFail = assertCustomize.assertEquals(countFail, dbAddSupplierBtn, ppAddSupplierBtn, "[Failed][Supplier Management] Add supplier button should be %s, but found %s.".formatted(ppAddSupplierBtn, dbAddSupplierBtn));
		logger.info("[UI][%s] Check Supplier Management - Add supplier button.".formatted(language));

		// check search box placeholder
		String dbSearchBoxPlaceholder = wait.until(visibilityOf(UI_SEARCH_BOX_PLACEHOLDER)).getAttribute("placeholder");
		String ppSearchBoxPlaceholder = getPropertiesValueByDBLang("products.supplier.management.header.searchBoxPlaceholder", language);
		countFail = assertCustomize.assertEquals(countFail, dbSearchBoxPlaceholder, ppSearchBoxPlaceholder, "[Failed][Supplier Management] Search box placeholder should be %s, but found %s.".formatted(ppSearchBoxPlaceholder, dbSearchBoxPlaceholder));
		logger.info("[UI][%s] Check Supplier Management - Search box placeholder.".formatted(language));
	}

	void checkSupplierTableList(String language) throws Exception {
		// check table column
		List<String> dbTableColumn = UI_SUPPLIER_TABLE_COLUMN.stream().map(WebElement::getText).toList();
		List<String> ppTableColumn = List.of(getPropertiesValueByDBLang("products.supplier.management.table.column.0", language),
				getPropertiesValueByDBLang("products.supplier.management.table.column.1", language),
				getPropertiesValueByDBLang("products.supplier.management.table.column.2", language),
				getPropertiesValueByDBLang("products.supplier.management.table.column.3", language));
		countFail = assertCustomize.assertEquals(countFail, dbTableColumn, ppTableColumn, "[Failed][Supplier Management] Supplier table list column title should be %s, but found %s.".formatted(ppTableColumn, dbTableColumn));
		logger.info("[UI][%s] Check Supplier Management - Supplier table list column title.".formatted(language));
	}

	void checkNoSearchResult(String language) throws Exception {
		// check no result
		String dbNoResult = wait.until(visibilityOf(UI_NO_RESULT)).getText();
		String ppNoResult = getPropertiesValueByDBLang("products.supplier.management.noSearchResult", language);
		countFail = assertCustomize.assertEquals(countFail, dbNoResult, ppNoResult, "[Failed][Supplier Management] No search result should be %s, but found %s.".formatted(ppNoResult, dbNoResult));
		logger.info("[UI][%s] Check Supplier Management - No search result.".formatted(language));
	}

	public void checkUIProductManagementPage(String language) throws Exception {
		checkHeader(language);
		checkSupplierTableList(language);
		checkNoSearchResult(language);
	}

}
