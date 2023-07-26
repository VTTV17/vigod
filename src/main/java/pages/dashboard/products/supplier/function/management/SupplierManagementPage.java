package pages.dashboard.products.supplier.function.management;

import api.dashboard.products.SupplierAPI;
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
import pages.dashboard.products.supplier.ui.crud.UICRUDSupplierPage;
import pages.dashboard.products.supplier.ui.management.UISupplierManagementPage;
import utilities.UICommonAction;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static utilities.links.Links.DOMAIN;

public class SupplierManagementPage extends SupplierManagementElement {
    UICommonAction commons;
    WebDriverWait wait;
    Actions act;
    String SUPPLIER_MANAGEMENT_PATH = "/supplier/list";
    String language;
	UISupplierManagementPage uiSupplierManagementPage;

    final static Logger logger = LogManager.getLogger(SupplierManagementPage.class);

    public SupplierManagementPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        act = new Actions(driver);
		uiSupplierManagementPage = new UISupplierManagementPage(driver);
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
    public SupplierManagementPage setLanguage(String language) {
        this.language = language;
        // set dashboard language
        commons.sleepInMiliSecond(1000);

        String currentLanguage = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('langKey')").equals("vi") ? "VIE" : "ENG";

        if (!currentLanguage.equals(language)) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", HEADER_SELECTED_LANGUAGE);
            HEADER_LANGUAGE_LIST.stream().filter(webElement -> webElement.getText().contains(language))
                    .findFirst().ifPresent(webElement -> ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click()", webElement));
        }

        return this;
    }

    public void navigateToSupplierManagementPage() {
        driver.get(DOMAIN + SUPPLIER_MANAGEMENT_PATH);

        commons.sleepInMiliSecond(3000);
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

    public List<String> getListSupplierName() {
        return SUPPLIER_NAME.stream().map(WebElement::getText).toList();
    }

    public List<String> getListEmail() {
        return EMAIL.stream().map(WebElement::getText).toList();
    }

    public List<String> getListPhoneNumber() {
        return PHONE_NUMBER.stream().map(WebElement::getText).toList();
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

    public void checkSupplierInformationAfterCRU(String supplierCode, String supplierName, String email, String phoneNumber, UICRUDSupplierPage uiCRUDSupplierPage) {
        searchSupplierByCode(supplierCode);
        // get supplier information at supplier management page
        String supCode = getListSupplierCode().get(0);
        String supName = getListSupplierName().get(0);
        String supEmail = getListEmail().get(0);
        String supPhone = getListPhoneNumber().get(0);

        // check supplier code
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, supCode.equals(supplierCode), "[Failed][Supplier Information] Supplier code should be %s, but found %s.".formatted(supplierCode, supCode));
        logger.info("Check Supplier Information - Supplier code.");

        // check supplier name
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, supName.equals(supplierName), "[Failed][Supplier Information] Supplier name should be %s, but found %s.".formatted(supplierName, supName));
        logger.info("Check Supplier Information - Supplier name.");

        // check email
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, supEmail.equals(email), "[Failed][Supplier Information] Email should be %s, but found %s.".formatted(email, supEmail));
        logger.info("Check Supplier Information - Email.");

        // check phone number
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, supPhone.equals(phoneNumber), "[Failed][Supplier Information] Phone number should be %s, but found %s.".formatted(phoneNumber, supPhone));
        logger.info("Check Supplier Information - Phone number.");
    }

    public void checkUISupplierManagementPage() throws Exception {
		// search supplier with invalid keywords to check no result text
        searchSupplierByCode(String.valueOf( Instant.now().toEpochMilli()));

		// check UI
		uiSupplierManagementPage.checkUIProductManagementPage(language);

		verifyTest();
    }

	public void checkNavigateToAddSupplierPage() {
		// click Add supplier button
		navigateToAddSupplierPage();

		// check URL
		uiSupplierManagementPage.countFail = uiSupplierManagementPage.assertCustomize.assertTrue(uiSupplierManagementPage.countFail, driver.getCurrentUrl().contains("/supplier/create"), "[Failed][Supplier Management] Can not navigate to Add supplier page, current url: %s.".formatted(driver.getCurrentUrl()));
		logger.info("Check Supplier Management - Navigate to Add supplier page.");

		verifyTest();
	}

	public void checkSearchWithInvalidSupplierCode() {
		// search supplier by invalid code
		String keywords = String.valueOf(Instant.now().toEpochMilli());
		searchSupplierByCode(keywords);

		// wait result
		commons.sleepInMiliSecond(1000);

		// get list supplier match search result
		List<String> listAvailableSupplier = getListSupplierCode();
		uiSupplierManagementPage.countFail = uiSupplierManagementPage.assertCustomize.assertTrue(uiSupplierManagementPage.countFail, listAvailableSupplier.size() == 0, "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
		logger.info("Check Supplier Management - Search by invalid supplier code.");

		verifyTest();
	}

	public void checkSearchWithValidSupplierCode() {
		List<String> listAvailableSupplier = getListSupplierCode();
		if (listAvailableSupplier.size() == 0) {
            // check available supplier or not, if no supplier, post API to create new supplier
            new SupplierAPI().createSupplier();

            // refresh page
            driver.navigate().refresh();

            // wait page loaded
            commons.sleepInMiliSecond(3000);
        }

		// search supplier by valid code
		String keywords = String.valueOf(getListSupplierCode().get(0));
		searchSupplierByCode(keywords);

		// wait result
		commons.sleepInMiliSecond(1000);

		// get list supplier match search result
		listAvailableSupplier = getListSupplierCode();
        uiSupplierManagementPage.countFail = uiSupplierManagementPage.assertCustomize.assertTrue(uiSupplierManagementPage.countFail, listAvailableSupplier.stream().allMatch(supCode -> supCode.contains(keywords)), "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
		logger.info("Check Supplier Management - Search by valid supplier code.");

		verifyTest();
	}

    public void checkSearchWithInvalidSupplierName() {
        // search supplier by invalid name
        String keywords = String.valueOf(Instant.now().toEpochMilli());
        searchSupplierByName(keywords);

        // wait result
        commons.sleepInMiliSecond(1000);

        // get list supplier match search result
        List<String> listAvailableSupplier = getListSupplierName();
        uiSupplierManagementPage.countFail = uiSupplierManagementPage.assertCustomize.assertTrue(uiSupplierManagementPage.countFail, listAvailableSupplier.size() == 0, "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
        logger.info("Check Supplier Management - Search by invalid supplier name.");

        verifyTest();
    }

    public void checkSearchWithValidSupplierName() {
        List<String> listAvailableSupplier = getListSupplierCode();
        if (listAvailableSupplier.size() == 0) {
            // check available supplier or not, if no supplier, post API to create new supplier
            new SupplierAPI().createSupplier();

            // refresh page
            driver.navigate().refresh();

            // wait page loaded
            commons.sleepInMiliSecond(3000);
        }

        // search supplier by valid name
        String keywords = String.valueOf(getListSupplierName().get(0));
        searchSupplierByName(keywords);

        // wait result
        commons.sleepInMiliSecond(1000);

        // get list supplier match search result
        listAvailableSupplier = getListSupplierName();
        uiSupplierManagementPage.countFail = uiSupplierManagementPage.assertCustomize.assertTrue(uiSupplierManagementPage.countFail, listAvailableSupplier.stream().allMatch(supName -> supName.contains(keywords)), "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
        logger.info("Check Supplier Management - Search by valid supplier name.");

        verifyTest();
    }

	void verifyTest() {
		if (uiSupplierManagementPage.countFail > 0)
			Assert.fail("[Failed] Fail %d cases".formatted(uiSupplierManagementPage.countFail));
	}

}
