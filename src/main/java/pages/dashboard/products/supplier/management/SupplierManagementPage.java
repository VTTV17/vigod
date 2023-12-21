package pages.dashboard.products.supplier.management;

import api.dashboard.products.SupplierAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.links.Links.DOMAIN;

public class SupplierManagementPage extends SupplierManagementElement {
    UICommonAction commonAction;
    WebDriver driver;
    WebDriverWait wait;
    String SUPPLIER_MANAGEMENT_PATH = "/supplier/list";
    String language;
    LoginInformation loginInformation;
    AssertCustomize assertCustomize;
    final static Logger logger = LogManager.getLogger(SupplierManagementPage.class);

    public SupplierManagementPage(WebDriver driver, LoginInformation loginInformation) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        this.loginInformation = loginInformation;
        assertCustomize = new AssertCustomize(driver);
    }

    public SupplierManagementPage inputSearchTerm(String searchTerm) {
        commonAction.sendKeys(loc_txtSearchSupplier, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        new HomePage(driver).waitTillSpinnerDisappear();
        return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToManageSupplier(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commonAction.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commonAction.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }


    /*-------------------------------------*/
    public SupplierManagementPage setLanguage(String language) {
        this.language = language;
        // set dashboard language
        // get current language
        String currentLanguage = commonAction.getLangKey().equals("vi") ? "VIE" : "ENG";
        logger.info("Current language: %s".formatted(currentLanguage));

        // set dashboard language
        if (!currentLanguage.equals(language)) {
            // open language dropdown list
            commonAction.clickJS(loc_lblSelectedLanguage);

            // select language
            commonAction.click(By.xpath(languageLocator.formatted(language)));

            // log
            logger.info("New language: %s.".formatted(language));
        }

        return this;
    }

    public void navigateToSupplierManagementPage() {
        driver.get(DOMAIN + SUPPLIER_MANAGEMENT_PATH);

        commonAction.sleepInMiliSecond(3000);
    }

    public void navigateToAddSupplierPage() {
        navigateToSupplierManagementPage();

        commonAction.clickJS(loc_btnAddSupplier);

        commonAction.sleepInMiliSecond(3000);
    }

    public void searchSupplierByCode(String supplierCode) {
        // navigate to supplier management page
        navigateToSupplierManagementPage();

        // search supplier by supplier code
        commonAction.sendKeys(loc_txtSearchSupplier, supplierCode);

        logger.info("Search supplier by code, keywords: %s".formatted(supplierCode));

        // wait result
        commonAction.sleepInMiliSecond(1000);
    }

    public List<String> getListSupplierCode() {
        return commonAction.getListElement(loc_lblSupplierCode).stream().map(WebElement::getText).toList();
    }

    public List<String> getListSupplierName() {
        return commonAction.getListElement(loc_lblSupplierName).stream().map(WebElement::getText).toList();
    }

    public List<String> getListEmail() {
        return commonAction.getListElement(loc_lblEmail).stream().map(WebElement::getText).toList();
    }

    public List<String> getListPhoneNumber() {
        return commonAction.getListElement(loc_lblPhoneNumber).stream().map(WebElement::getText).toList();
    }

    public void searchSupplierByName(String supplierName) {
        // navigate to supplier management page
        navigateToSupplierManagementPage();

        // search supplier by supplier name
        commonAction.sendKeys(loc_txtSearchSupplier, supplierName);

        logger.info("Search supplier by name, keywords: %s".formatted(supplierName));

        // wait result
        commonAction.sleepInMiliSecond(1000);
    }

    public void findAndNavigateToSupplierDetailPage(String supplierCode) {
        // search supplier
        searchSupplierByCode(supplierCode);

        // navigate to supplier page
        logger.info("Navigate to supplier detail with supplier code: %s".formatted(commonAction.getText(loc_lblSupplierCode, 0)));
        commonAction.click(loc_lblSupplierCode, 0);

        // wait page loaded
        commonAction.sleepInMiliSecond(3000);
    }

    public void checkSupplierInformationAfterCRU(String supplierCode, String supplierName, String email, String phoneNumber) {
        searchSupplierByCode(supplierCode);
        // get supplier information at supplier management page
        String supCode = getListSupplierCode().get(0);
        String supName = getListSupplierName().get(0);
        String supEmail = getListEmail().get(0);
        String supPhone = getListPhoneNumber().get(0);

        // check supplier code
        assertCustomize.assertTrue(supCode.equals(supplierCode), "[Failed][Supplier Information] Supplier code should be %s, but found %s.".formatted(supplierCode, supCode));
        logger.info("Check Supplier Information - Supplier code.");

        // check supplier name
        assertCustomize.assertTrue(supName.equals(supplierName), "[Failed][Supplier Information] Supplier name should be %s, but found %s.".formatted(supplierName, supName));
        logger.info("Check Supplier Information - Supplier name.");

        // check email
        assertCustomize.assertTrue(supEmail.equals(email), "[Failed][Supplier Information] Email should be %s, but found %s.".formatted(email, supEmail));
        logger.info("Check Supplier Information - Email.");

        // check phone number
        assertCustomize.assertTrue(supPhone.equals(phoneNumber), "[Failed][Supplier Information] Phone number should be %s, but found %s.".formatted(phoneNumber, supPhone));
        logger.info("Check Supplier Information - Phone number.");
    }

    public void checkUISupplierManagementPage() throws Exception {
        // search supplier with invalid keywords to check no result text
        searchSupplierByCode(String.valueOf( Instant.now().toEpochMilli()));

        // check UI
        checkUIProductManagementPage(language);

        if (AssertCustomize.getCountFalse() > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
    }

    public void checkNavigateToAddSupplierPage() {
        // click Add supplier button
        navigateToAddSupplierPage();

        // check URL
        assertCustomize.assertTrue(driver.getCurrentUrl().contains("/supplier/create"), "[Failed][Supplier Management] Can not navigate to Add supplier page, current url: %s.".formatted(driver.getCurrentUrl()));
        logger.info("Check Supplier Management - Navigate to Add supplier page.");

        if (AssertCustomize.getCountFalse() > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
    }

    public void checkSearchWithInvalidSupplierCode() {
        // search supplier by invalid code
        String keywords = String.valueOf(Instant.now().toEpochMilli());
        searchSupplierByCode(keywords);

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list supplier match search result
        List<String> listAvailableSupplier = getListSupplierCode();
        assertCustomize.assertTrue(listAvailableSupplier.isEmpty(), "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
        logger.info("Check Supplier Management - Search by invalid supplier code.");

        if (AssertCustomize.getCountFalse() > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
    }

    public void checkSearchWithValidSupplierCode() {
        List<String> listAvailableSupplier = getListSupplierCode();
        if (listAvailableSupplier.isEmpty()) {
            // check available supplier or not, if no supplier, post API to create new supplier
            new SupplierAPI(loginInformation).createSupplier();

            // refresh page
            driver.navigate().refresh();

            // wait page loaded
            commonAction.sleepInMiliSecond(3000);
        }

        // search supplier by valid code
        String keywords = String.valueOf(getListSupplierCode().get(0));
        searchSupplierByCode(keywords);

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list supplier match search result
        listAvailableSupplier = getListSupplierCode();
        assertCustomize.assertTrue(listAvailableSupplier.stream().allMatch(supCode -> supCode.contains(keywords)), "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
        logger.info("Check Supplier Management - Search by valid supplier code.");

        if (AssertCustomize.getCountFalse() > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
    }

    public void checkSearchWithInvalidSupplierName() {
        // search supplier by invalid name
        String keywords = String.valueOf(Instant.now().toEpochMilli());
        searchSupplierByName(keywords);

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list supplier match search result
        List<String> listAvailableSupplier = getListSupplierName();
        assertCustomize.assertTrue(listAvailableSupplier.isEmpty(), "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
        logger.info("Check Supplier Management - Search by invalid supplier name.");

        if (AssertCustomize.getCountFalse() > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
    }

    public void checkSearchWithValidSupplierName() {
        List<String> listAvailableSupplier = getListSupplierCode();
        if (listAvailableSupplier.isEmpty()) {
            // check available supplier or not, if no supplier, post API to create new supplier
            new SupplierAPI(loginInformation).createSupplier();

            // refresh page
            driver.navigate().refresh();

            // wait page loaded
            commonAction.sleepInMiliSecond(3000);
        }

        // search supplier by valid name
        String keywords = String.valueOf(getListSupplierName().get(0));
        searchSupplierByName(keywords);

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list supplier match search result
        listAvailableSupplier = getListSupplierName();
        assertCustomize.assertTrue(listAvailableSupplier.stream().allMatch(supName -> supName.contains(keywords)), "[Failed][Supplier Management] Search result: %s, keywords: %s".formatted(listAvailableSupplier, keywords));
        logger.info("Check Supplier Management - Search by valid supplier name.");

        if (AssertCustomize.getCountFalse() > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
    }

    void checkHeader(String language) throws Exception {
        // check page title
        String dbTitle = commonAction.getText(loc_lblPageTitle).split("\n")[0];
        String ppTitle = getPropertiesValueByDBLang("products.supplier.management.header.pageTitle", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Supplier Management] Page title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Supplier Management - Page title.".formatted(language));

        // check Add supplier button
        String dbAddSupplierBtn = commonAction.getText(loc_lblAddSupplier);
        String ppAddSupplierBtn = getPropertiesValueByDBLang("products.supplier.management.header.addSupplierBtn", language);
        assertCustomize.assertEquals(dbAddSupplierBtn, ppAddSupplierBtn, "[Failed][Supplier Management] Add supplier button should be %s, but found %s.".formatted(ppAddSupplierBtn, dbAddSupplierBtn));
        logger.info("[UI][%s] Check Supplier Management - Add supplier button.".formatted(language));

        // check search box placeholder
        String dbSearchBoxPlaceholder = commonAction.getAttribute(loc_plhSearchSupplier, "placeholder");
        String ppSearchBoxPlaceholder = getPropertiesValueByDBLang("products.supplier.management.header.searchBoxPlaceholder", language);
        assertCustomize.assertEquals(dbSearchBoxPlaceholder, ppSearchBoxPlaceholder, "[Failed][Supplier Management] Search box placeholder should be %s, but found %s.".formatted(ppSearchBoxPlaceholder, dbSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Supplier Management - Search box placeholder.".formatted(language));
    }

    void checkSupplierTableList(String language) throws Exception {
        // check table column
        List<String> dbTableColumn = commonAction.getListElement(loc_tblSupplier).stream().map(WebElement::getText).toList();
        List<String> ppTableColumn = List.of(getPropertiesValueByDBLang("products.supplier.management.table.column.0", language),
                getPropertiesValueByDBLang("products.supplier.management.table.column.1", language),
                getPropertiesValueByDBLang("products.supplier.management.table.column.2", language),
                getPropertiesValueByDBLang("products.supplier.management.table.column.3", language));
        assertCustomize.assertEquals(dbTableColumn, ppTableColumn, "[Failed][Supplier Management] Supplier table list column title should be %s, but found %s.".formatted(ppTableColumn, dbTableColumn));
        logger.info("[UI][%s] Check Supplier Management - Supplier table list column title.".formatted(language));
    }

    void checkNoSearchResult(String language) throws Exception {
        // check no result
        String dbNoResult = commonAction.getText(loc_lblNoResult);
        String ppNoResult = getPropertiesValueByDBLang("products.supplier.management.noSearchResult", language);
        assertCustomize.assertEquals(dbNoResult, ppNoResult, "[Failed][Supplier Management] No search result should be %s, but found %s.".formatted(ppNoResult, dbNoResult));
        logger.info("[UI][%s] Check Supplier Management - No search result.".formatted(language));
    }

    public void checkUIProductManagementPage(String language) throws Exception {
        checkHeader(language);
        checkSupplierTableList(language);
        checkNoSearchResult(language);
    }

}
