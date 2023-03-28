package pages.dashboard.products.supplier.ui.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;

import static utilities.links.Links.DOMAIN;

public class UISupplierManagementPage extends UISupplierManagementElement {
	UICommonAction commons;
	WebDriverWait wait;
	String SUPPLIER_MANAGEMENT_PATH = "/supplier/list";

	final static Logger logger = LogManager.getLogger(UISupplierManagementPage.class);

	public UISupplierManagementPage(WebDriver driver) {
		super(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}
	public UISupplierManagementPage inputSearchTerm(String searchTerm) {
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

	void navigateToSupplierManagementPage() {
		driver.get(DOMAIN + SUPPLIER_MANAGEMENT_PATH);

		commons.verifyPageLoaded("Quản lý nhà cung cấp", "Supplier Management");

		((JavascriptExecutor) driver).executeScript("arguments[0].click()", HEADER_ADD_SUPPLIER_BTN);
	}
	
}
