package pages.dashboard.products.supplier.function.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.List;

import static utilities.links.Links.DOMAIN;

public class SupplierManagementPage extends SupplierManagementElement {
	UICommonAction commons;
	WebDriverWait wait;
	Actions act;
	String SUPPLIER_MANAGEMENT_PATH = "/supplier/list";

	final static Logger logger = LogManager.getLogger(SupplierManagementPage.class);

	public SupplierManagementPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		act = new Actions(driver);
	}
	public SupplierManagementPage inputSearchTerm(String searchTerm) {
		commons.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToManageSupplier(String permission, String url) {
		if (permission.contentEquals("A")) {
			Assert.assertTrue(commons.getCurrentURL().contains(url));
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commons.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }


    /*-------------------------------------*/

	public void navigateToSupplierManagementPage() {
		driver.get(DOMAIN + SUPPLIER_MANAGEMENT_PATH);

		commons.verifyPageLoaded("Quản lý nhà cung cấp", "Supplier Management");
	}
	public void navigateToAddSupplierPage() {
		navigateToSupplierManagementPage();

		((JavascriptExecutor) driver).executeScript("arguments[0].click()", HEADER_ADD_SUPPLIER_BTN);

		commons.sleepInMiliSecond(3000);
	}

	public void searchSupplierByCode(String supplierCode) {
		// navigate to supplier management page
		navigateToSupplierManagementPage();

		// search supplier by supplier code
		act.moveToElement(SEARCH_BOX).build().perform();
		SEARCH_BOX.click();
		SEARCH_BOX.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		SEARCH_BOX.sendKeys(supplierCode);

		logger.info("Search supplier by code, keywords: %s".formatted(supplierCode));

		// wait result
		commons.sleepInMiliSecond(1000);
	}

	public List<String> getListSupplierCode() {
		return SUPPLIER_CODE.stream().map(WebElement::getText).toList();
	}

	public void searchSupplierByName(String supplierName) {
		// navigate to supplier management page
		navigateToSupplierManagementPage();

		// search supplier by supplier name
		act.moveToElement(SEARCH_BOX).build().perform();
		SEARCH_BOX.click();
		SEARCH_BOX.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
		SEARCH_BOX.sendKeys(supplierName);

		logger.info("Search supplier by name, keywords: %s".formatted(supplierName));

		// wait result
		commons.sleepInMiliSecond(1000);
	}

	public void findAndNavigateToSupplierDetailPage(String supplierCode) {
		// search supplier
		searchSupplierByCode(supplierCode);

		// navigate to supplier page
		logger.info("Navigate to supplier detail with supplier code: %s".formatted(SUPPLIER_CODE.get(0).getText()));
		wait.until(ExpectedConditions.elementToBeClickable(SUPPLIER_CODE.get(0))).click();

		// wait page loaded
		commons.sleepInMiliSecond(3000);
	}
	
}
