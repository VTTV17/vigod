package web.Dashboard.supplier.supplier.crud;

import api.Seller.supplier.purchase_orders.APICreatePurchaseOrder;
import api.Seller.supplier.supplier.APISupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.supplier.supplier.management.SupplierManagementPage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;
import static utilities.utils.PropertiesUtil.getPropertiesValueByDBLang;

/**
 * CRUD:
 * <p> CR: Create</p>
 * <p> U: Update</p>
 * <p> D: Delete</p>
 */
public class SupplierPage extends SupplierElement {
    WebDriver driver;

    Logger logger = LogManager.getLogger(SupplierPage.class);

    WebDriverWait wait;
    public String language;
    UICommonAction commonAction;
    SupplierManagementPage supplierManagementPage;
    APISupplier supplierAPIWithSellerToken;
    private String supplierName;
    private String supplierCode;
    private String phoneNumber;
    private String email;
    private LoginInformation sellerLoginInformation;
    private final AssertCustomize assertCustomize;

    public SupplierPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        supplierManagementPage = new SupplierManagementPage(driver);
    }

    public SupplierPage getLoginInformation(LoginInformation sellerLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        supplierAPIWithSellerToken = new APISupplier(sellerLoginInformation);
        return this;
    }

    public SupplierPage setLanguage(String language) {
        this.language = language;

        driver.get(DOMAIN);

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

    void inputSupplierName(String name) {
        // input supplier name
        commonAction.sendKeys(loc_txtSupplierName, name);
        logger.info("Input supplier name: %s".formatted(name));
    }

    void inputSupplierCode(String code) {
        // input supplier code
        commonAction.sendKeys(loc_txtSupplierCode, code);
        logger.info("Input supplier code: %s".formatted(code));

    }

    void inputPhoneNumber(String phone) {
        // input phone number
        commonAction.sendKeys(loc_txtPhoneNumber, phone);
        logger.info("Input phone number: %s".formatted(phone));
    }

    void inputEmail(String email) {
        // input email
        commonAction.sendKeys(loc_txtEmail, email);
        logger.info("Input email: %s".formatted(email));
    }

    String getOutSideVnCountryName() {
        String countryName = new DataGenerator().randomCountry();
        return !countryName.equals("Vietnam") ? countryName : getOutSideVnCountryName();
    }

    void selectCountry(boolean isVNSupplier) {
        // get country name
        String countryName = isVNSupplier ? "Vietnam" : getOutSideVnCountryName();

        // get country code
        String countryCode = new DataGenerator().getCountryCode(countryName);

        // select country
        commonAction.selectDropdownOptionByValue(loc_ddvSelectedCountry, countryCode);

        // log
        logger.info("Select country: %s.".formatted(countryName));
    }

    void inputVNAddress(String address) {
        // input address
        commonAction.sendKeys(loc_txtVNAddress, address);
        logger.info("Input address: %s".formatted(address));
    }

    /* VN address */
    void selectVNCity() {
        // get index of selected city
        int index = nextInt(commonAction.getListElement(loc_ddlVNCity).size());

        // get city name
        String cityName = commonAction.getText(loc_ddlVNCity, index);

        // get city code
        String cityCode = commonAction.getValue(loc_ddlVNCity, index);

        // select city
        commonAction.selectDropdownOptionByValue(loc_lblSelectedVNCity, cityCode);

        // log
        logger.info("Select city: %s".formatted(cityName));
    }

    void selectVNDistrict() {
        // get index of selected district
        int index = nextInt(commonAction.getListElement(loc_ddlVNDistrict).size());

        // get district name
        String districtName = commonAction.getText(loc_ddlVNDistrict, index);

        // get district code
        String districtCode = commonAction.getValue(loc_ddlVNDistrict, index);

        // select district
        commonAction.selectDropdownOptionByValue(loc_lblSelectedVNDistrict, districtCode);

        // log
        logger.info("Select district: %s".formatted(districtName));
    }

    void selectVNWard() {
        // get index of selected ward
        int index = nextInt(commonAction.getListElement(loc_ddlVNWard).size());

        // get ward name
        String wardName = commonAction.getText(loc_ddlVNWard, index);

        // get ward name
        String wardCode = commonAction.getValue(loc_ddlVNWard, index);

        // select ward
        commonAction.selectDropdownOptionByValue(loc_lblSelectedVNWard, wardCode);

        // log
        logger.info("Select ward: %s".formatted(wardName));
    }

    /* non-VN address */
    void inputNonVNStreetAddress(String address) {
        // input address
        commonAction.sendKeys(loc_txtNonVNStreetAddress, address);
        logger.info("Input street address: %s".formatted(address));
    }

    void inputNonVNAddress2(String address2) {
        // input address 2 for non-VN country
        commonAction.sendKeys(loc_txtNonVNAddress2, address2);
        logger.info("Input address2: %s".formatted(address2));
    }

    void inputNonVNCity(String city) {
        // input city for non-VN country
        commonAction.sendKeys(loc_txtNonVNCity, city);
        logger.info("Input city: %s".formatted(city));
    }

    void selectNonVNProvince() {
        // get index of selected province
        int index = nextInt(commonAction.getListElement(loc_ddlNonVNProvince).size());

        // get province name
        String provinceName = commonAction.getText(loc_ddlNonVNProvince, index);

        // get province code
        String provinceCode = commonAction.getValue(loc_ddlNonVNProvince, index);

        // select province
        commonAction.selectDropdownOptionByValue(loc_lblSelectedNonVNProvince, provinceCode);
        commonAction.selectDropdownOptionByValue(loc_lblSelectedNonVNProvince, provinceCode);


        // log
        logger.info("Select province: %s".formatted(provinceName));
    }

    void inputNonVNZipcode(String zipcode) {
        // input zipcode for non-VN country
        commonAction.sendKeys(loc_txtNonVnZipcode, zipcode);
        logger.info("Input zipcode: %s".formatted(zipcode));
    }

    void selectResponsibleStaff() {
        // get index of selected staff
        int index = nextInt(commonAction.getListElement(loc_ddlResponsibleStaff).size());

        // get staff name
        String staffName = commonAction.getText(loc_ddlResponsibleStaff, index);

        // get staff name
        String staffId = commonAction.getValue(loc_ddlResponsibleStaff, index);

        // select staff
        commonAction.selectDropdownOptionByValue(loc_lblSelectedResponsibleStaff, staffId);

        // log
        logger.info("Select responsible staff: %s".formatted(staffName));
    }

    void inputDescription(String description) {
        commonAction.sendKeys(loc_txtDescription, description);
        logger.info("Input description: %s".formatted(description));
    }

    void completeCRUSupplier() {
        commonAction.click(loc_btnHeaderSave);
        logger.info("Complete create supplier");
    }

    public void checkErrorWhenLeaveRequiredFieldBlank() throws Exception {
        // navigate to create supplier page
        supplierManagementPage.navigateToAddSupplierPage();

        // leave supplier name blank
        inputSupplierName("");

        // click Save button
        completeCRUSupplier();

        // check error when leave supplier name field blank
        checkErrorWhenLeaveRequiredFieldBlank(language);

        AssertCustomize.verifyTest();
    }

    public void checkErrorWhenInputDuplicateSupplierCode() throws Exception {
        // navigate to create supplier page
        supplierManagementPage.navigateToAddSupplierPage();

        // input supplier name
        inputSupplierName("abc");

        // get available supplier code
        APISupplier sup = new APISupplier(sellerLoginInformation);
        List<String> supplierCodeList = sup.getListSupplierCode("");
        String supplierCode = (supplierCodeList.isEmpty()) ? sup.createSupplierAndGetSupplierCode() : sup.getListSupplierCode("").get(0);

        // input available supplier code
        inputSupplierCode(supplierCode);

        // click Save button
        completeCRUSupplier();

        // check error when input available supplier code
        checkErrorWhenInputDuplicateSupplierCode(language);

        AssertCustomize.verifyTest();
    }

    public void checkErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        // navigate to create supplier page
        supplierManagementPage.navigateToAddSupplierPage();

        // input supplier name
        inputSupplierName("abc");

        // input supplier code
        inputSupplierCode("a b");

        // click Save button
        completeCRUSupplier();

        // check error when input available supplier code
        checkErrorWhenInputInvalidFormatSupplierCode(language);

        AssertCustomize.verifyTest();
    }

    public void createNewSupplier(boolean isVNSupplier) throws Exception {
        // navigate to create supplier page
        supplierManagementPage.navigateToAddSupplierPage();

        // select country
        selectCountry(isVNSupplier);

        // check UI
//        checkUIAddSupplierPage(language, isVNSupplier);

        // generate data
        String epoch = String.valueOf(Instant.now().toEpochMilli());

        // input supplier name
        supplierName = Pattern.compile("([\\w\\W]{0,100})").matcher("[%s] Auto - Supplier %s - %s".formatted(language, isVNSupplier ? "VN" : "Non VN", epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputSupplierName(supplierName);

        // input supplier code
        supplierCode = Pattern.compile("(\\w{0,12})").matcher("%s".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputSupplierCode("%s".formatted(epoch));

        // input supplier phone number
        phoneNumber = Pattern.compile("(\\d{8,13})").matcher(epoch).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputPhoneNumber(epoch);

        // input supplier email
        email = Pattern.compile("([\\w\\W]{0,100})").matcher("%s@qa.team".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputEmail("%s@qa.team".formatted(epoch));

        // if country = Vietnam
        if (isVNSupplier) {
            // input address
            inputVNAddress("Address %s".formatted(epoch));

            // select city
            selectVNCity();

            // select district
            selectVNDistrict();

            // select ward
            selectVNWard();
        }
        // else country = non-Vietnam
        else {
            // input street address
            inputNonVNStreetAddress("Street address %s".formatted(epoch));

            // input address2
            inputNonVNAddress2("Address2 %s".formatted(epoch));

            // input city
            inputNonVNCity("City %s".formatted(epoch));

            // select province
            selectNonVNProvince();

            // input zipcode
            inputNonVNZipcode("Zipcode %s".formatted(epoch));
        }

        // select responsible staff
        selectResponsibleStaff();

        // input description
        inputDescription("Descriptions %s".formatted(epoch));

        // click Save button to complete create supplier
        completeCRUSupplier();

        // check supplier is created or not
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not create supplier.");

        // check information after create supplier
        supplierManagementPage.checkSupplierInformationAfterCRU(supplierCode, supplierName, email, phoneNumber);

        AssertCustomize.verifyTest();
    }

    void navigateSupplierDetailPage() {
        // navigate to supplier detail page
        supplierCode = (supplierAPIWithSellerToken.getListSupplierCode("").isEmpty())
                ? supplierAPIWithSellerToken.createSupplierAndGetSupplierCode() : supplierAPIWithSellerToken.getListSupplierCode("").get(0);
        supplierManagementPage.findAndNavigateToSupplierDetailPage(supplierCode);
    }

    void navigateSupplierDetailPage(int supplierId) {
        // navigate to supplier detail page
        driver.get("%s/supplier/edit/%s".formatted(DOMAIN, supplierId));

        // log
        logger.info("Navigate to supplier detail page by URL, supplierId: %s.".formatted(supplierId));
    }

    public void updateSupplier(boolean isVNSupplier) throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // select country
        selectCountry(isVNSupplier);

        // check UI
//        checkUIUpdateSupplierPage(language, isVNSupplier);

        // generate data
        String epoch = String.valueOf(Instant.now().toEpochMilli());

        // input supplier name
        supplierName = Pattern.compile("([\\w\\W]{0,100})").matcher("[%s] Auto - Supplier %s - %s".formatted(language, isVNSupplier ? "VN" : "Non VN", epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputSupplierName(supplierName);

        // input supplier code
        supplierCode = Pattern.compile("(\\w{0,12})").matcher("%s".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputSupplierCode("%s".formatted(epoch));

        // input supplier phone number
        phoneNumber = Pattern.compile("(\\d{8,13})").matcher(epoch).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputPhoneNumber(epoch);

        // input supplier email
        email = Pattern.compile("([\\w\\W]{0,100})").matcher("%s@qa.team".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputEmail("%s@qa.team".formatted(epoch));

        // if country = Vietnam
        if (isVNSupplier) {
            // input address
            inputVNAddress("Address %s".formatted(epoch));

            // select city
            selectVNCity();

            // select district
            selectVNDistrict();

            // select ward
            selectVNWard();
        }
        // else country = non-Vietnam
        else {
            // input street address
            inputNonVNStreetAddress("Street address %s".formatted(epoch));

            // input address2
            inputNonVNAddress2("Address2 %s".formatted(epoch));

            // input city
            inputNonVNCity("City %s".formatted(epoch));

            // select province
            selectNonVNProvince();

            // input zipcode
            inputNonVNZipcode("Zipcode %s".formatted(epoch));
        }

        // select responsible staff
        selectResponsibleStaff();

        // input description
        inputDescription("Descriptions %s".formatted(epoch));

        // click Save button to complete create supplier
        completeCRUSupplier();

        // check supplier is updated or not
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not update supplier.");

        // check information after update supplier
        supplierManagementPage.checkSupplierInformationAfterCRU(supplierCode, supplierName, email, phoneNumber);

        AssertCustomize.verifyTest();
    }

    public void checkSupplierInformation() {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // get supplier ID
        int supplierID = Pattern.compile("(\\d+)").matcher(driver.getCurrentUrl()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);

        // get supplier information map
        APISupplier.SupplierInformation supInfo = supplierAPIWithSellerToken.getSupplierInformation(supplierID);

        // check supplier name
        String dbSupplierName = commonAction.getValue(loc_txtSupplierName);
        String infoSupplierName = supInfo.getName();
        assertCustomize.assertEquals(dbSupplierName, infoSupplierName, "[Failed][Supplier Information] Supplier name should be %s, but found %s.".formatted(infoSupplierName, dbSupplierName));
        logger.info("Check Supplier Information - Supplier name.");

        // check supplier code
        String dbSupplierCode = commonAction.getValue(loc_txtSupplierCode);
        String infoSupplierCode = supInfo.getCode();
        assertCustomize.assertEquals(dbSupplierCode, infoSupplierCode, "[Failed][Supplier Information] Supplier code should be %s, but found %s.".formatted(infoSupplierCode, dbSupplierCode));
        logger.info("Check Supplier Information - Supplier code.");

        // check phone number
        String dbPhoneNumber = commonAction.getValue(loc_txtPhoneNumber);
        String infoPhoneNumber = supInfo.getPhoneNumber();
        assertCustomize.assertEquals(dbPhoneNumber, infoPhoneNumber, "[Failed][Supplier Information] Phone number should be %s, but found %s.".formatted(infoPhoneNumber, dbPhoneNumber));
        logger.info("Check Supplier Information - Phone number.");

        // check email
        String dbEmail = commonAction.getValue(loc_txtEmail);
        String infoEmail = supInfo.getEmail();
        assertCustomize.assertEquals(dbEmail, infoEmail, "[Failed][Supplier Information] Email should be %s, but found %s.".formatted(infoEmail, dbEmail));
        logger.info("Check Supplier Information - Email.");

        // check country
        String dbCountry = commonAction.getValue(loc_ddvSelectedCountry);
        String infoCountry = supInfo.getCountryCode();
        assertCustomize.assertEquals(dbCountry, infoCountry, "[Failed][Supplier Information] Country should be %s, but found %s.".formatted(infoCountry, dbCountry));
        logger.info("Check Supplier Information - Country.");

        // check VN supplier
        if (infoCountry.equals("VN")) {
            // check address
            String dbAddress = commonAction.getValue(loc_txtVNAddress);
            String infoAddress = supInfo.getAddress();
            assertCustomize.assertEquals(dbAddress, infoAddress, "[Failed][Supplier Information] Address should be %s, but found %s.".formatted(infoAddress, dbAddress));
            logger.info("Check Supplier Information - Address.");

            // check city/province
            String dbCity = commonAction.getValue(loc_lblSelectedVNCity);
            String infoCity = supInfo.getProvince();
            assertCustomize.assertEquals(dbCity, infoCity, "[Failed][Supplier Information] City/Province should be %s, but found %s.".formatted(infoCity, dbCity));
            logger.info("Check Supplier Information - City/Province.");

            // check district
            String dbDistrict = commonAction.getValue(loc_lblSelectedVNDistrict);
            String infoDistrict = supInfo.getDistrict();
            assertCustomize.assertEquals(dbDistrict, infoDistrict, "[Failed][Supplier Information] District should be %s, but found %s.".formatted(infoDistrict, dbDistrict));
            logger.info("Check Supplier Information - District.");

            // check ward
            String dbWard = commonAction.getValue(loc_lblSelectedVNWard);
            String infoWard = supInfo.getWard();
            assertCustomize.assertEquals(dbWard, infoWard, "[Failed][Supplier Information] Ward should be %s, but found %s.".formatted(infoWard, dbWard));
            logger.info("Check Supplier Information - Ward.");
        }
        // check Non-VN supplier
        else {
            // check street address
            String dbStreetAddress = commonAction.getValue(loc_txtNonVNStreetAddress);
            String infoStreetAddress = supInfo.getAddress();
            assertCustomize.assertEquals(dbStreetAddress, infoStreetAddress, "[Failed][Supplier Information] Street address should be %s, but found %s.".formatted(infoStreetAddress, dbStreetAddress));
            logger.info("Check Supplier Information - Street address.");

            // check address2
            String dbAddress2 = commonAction.getValue(loc_txtNonVNAddress2);
            String infoAddress2 = supInfo.getAddress2();
            assertCustomize.assertEquals(dbAddress2, infoAddress2, "[Failed][Supplier Information] Address2 should be %s, but found %s.".formatted(infoAddress2, dbAddress2));
            logger.info("Check Supplier Information - Address2.");

            // check city
            String dbCity = commonAction.getValue(loc_txtNonVNCity);
            String infoCity = supInfo.getCityName();
            assertCustomize.assertEquals(dbCity, infoCity, "[Failed][Supplier Information] City should be %s, but found %s.".formatted(infoCity, dbCity));
            logger.info("Check Supplier Information - City.");

            // check state/region/province
            String dbProvince = commonAction.getValue(loc_lblSelectedNonVNProvince);
            String infoProvince = supInfo.getProvince();
            assertCustomize.assertEquals(dbProvince, infoProvince, "[Failed][Supplier Information] State/Region/Province should be %s, but found %s.".formatted(infoProvince, dbProvince));
            logger.info("Check Supplier Information - State/Region/Province.");

            // check zip code
            String dbZipCode = commonAction.getValue(loc_txtNonVnZipcode);
            String infoZipCode = supInfo.getZipcode();
            assertCustomize.assertEquals(dbZipCode, infoZipCode, "[Failed][Supplier Information] Zip code should be %s, but found %s.".formatted(infoZipCode, dbZipCode));
            logger.info("Check Supplier Information - Zip code.");
        }

        // check responsible staff
        String dbResponsibleStaff = commonAction.getValue(loc_lblSelectedResponsibleStaff);
        String infoResponsibleStaff = supInfo.getResponsibleStaff() != null ? supInfo.getResponsibleStaff() : "";
        assertCustomize.assertEquals(dbResponsibleStaff, infoResponsibleStaff, "[Failed][Supplier Information] Responsible staff should be %s, but found %s.".formatted(infoResponsibleStaff, dbResponsibleStaff));
        logger.info("Check Supplier Information - Responsible staff.");

        // check description
        String dbDescription = commonAction.getValue(loc_txtDescription);
        String infoDescription = supInfo.getDescription() != null ? supInfo.getDescription() : "";
        assertCustomize.assertEquals(dbDescription, infoDescription, "[Failed][Supplier Information] Description should be %s, but found %s.".formatted(infoDescription, dbDescription));
        logger.info("Check Supplier Information - Description.");

        AssertCustomize.verifyTest();
    }

    public void checkOrderHistory() throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // get supplier ID
        int supplierID = Pattern.compile("(\\d+)").matcher(driver.getCurrentUrl()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);

        // if no purchase orders, post API to create data test
        List<String> listAvailablePurchaseId = supplierAPIWithSellerToken.getListOrderId("", supplierID);
        String purchaseId = (listAvailablePurchaseId.isEmpty()) ? new APICreatePurchaseOrder(sellerLoginInformation).createPurchaseOrderAndGetOrderId() : listAvailablePurchaseId.get(0);

        // input valid purchaseId and search
        commonAction.sendKeys(loc_txtSearchPurchaseOrder, "%s\n".formatted(purchaseId));

        // wait result
        commonAction.sleepInMiliSecond(1000, "Wait result updated.");

        // get list result
        List<String> listResult = commonAction.getListElement(loc_tblSearchResult_purchaseOrderId).stream().map(WebElement::getText).toList();

        // verify list result shows correctly
        assertCustomize.assertTrue(listResult.stream().allMatch(result -> result.contains(purchaseId)), "[Failed][Order history] List result shown incorrectly, keywords: %s, but found list result: %s.".formatted(purchaseId, listResult));
        logger.info("Check Order history - Has search result.");

        // input invalid purchaseId and search
        // input valid purchaseId and search
        commonAction.sendKeys(loc_txtSearchPurchaseOrder, "%s\n".formatted(Instant.now().toEpochMilli()));

        // wait result
        commonAction.sleepInMiliSecond(1000, "Wait result updated.");

        // get list result
        listResult = commonAction.getListElement(loc_tblSearchResult_purchaseOrderId).stream().map(WebElement::getText).toList();

        // verify list result shows correctly
        assertCustomize.assertTrue(listResult.isEmpty(), "[Failed][Order history] Purchase orders is shown although search with invalid keywords.");
        logger.info("Check Order history - No search result.");

        // check UI no search result
        checkNoOrderHistory(language);

        AssertCustomize.verifyTest();
    }

    public void deleteSupplier() throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // get supplier name
        String supplierName = commonAction.getValue(loc_txtSupplierName);

        // open Confirm delete supplier popup
        commonAction.click(loc_btnHeaderDelete);

        // wait confirm popup visible
        commonAction.getElement(loc_dlgConfirmDeleteSupplier);

        // check UI
        checkConfirmDeleteSupplierPopup(language);

        // confirm delete supplier
        commonAction.click(loc_dlgConfirmDeleteSupplier_btnDelete);

        // wait confirm popup invisible
        commonAction.waitInvisibilityOfElementLocated(loc_dlgConfirmDeleteSupplier);

        // search supplier code
        supplierManagementPage.searchSupplierByCode(supplierCode);

        // wait result
        commonAction.sleepInMiliSecond(1000, "Wait result updated.");

        // get list result
        List<String> listResult = supplierManagementPage.getListSupplierCode();

        // check no result when search deleted supplier by code
        assertCustomize.assertTrue(listResult.isEmpty(), "[Failed][Order history] Supplier is deleted but search supplier with keywords: %s, return list: %s".formatted(supplierCode, listResult));
        logger.info("Check Supplier management - Search deleted supplier by supplier code.");

        // search supplier by name
        supplierManagementPage.searchSupplierByName(supplierName);

        // wait result
        commonAction.sleepInMiliSecond(1000, "Wait result updated.");

        // get list result
        listResult = supplierManagementPage.getListSupplierCode();

        // check no result when search deleted supplier by code
        assertCustomize.assertTrue(listResult.isEmpty(), "[Failed][Order history] Supplier is deleted but search supplier with keywords: %s, return list: %s".formatted(supplierName, listResult));
        logger.info("Check Supplier management - Search deleted supplier by supplier name.");

        AssertCustomize.verifyTest();
    }

    void checkHeader(String language) throws Exception {
        // check Back to supplier management page
        String dbBackToSupplierManagement = commonAction.getText(loc_lblBackToSupplierManagementPage);
        String ppBackToSupplierManagement = getPropertiesValueByDBLang("products.supplier.addSupplier.header.backToSupplierManagementPage", language);
        assertCustomize.assertEquals(dbBackToSupplierManagement, ppBackToSupplierManagement, "[Failed][Header] Back to supplier management page should be %s, but found %s.".formatted(ppBackToSupplierManagement, dbBackToSupplierManagement));
        logger.info("[UI][%s] Check Header - Back to supplier management page.".formatted(language));

        // check page title
        String dbTitle = commonAction.getText(loc_lblPageTitle);
        String ppTitle = (driver.getCurrentUrl().contains("/edit")) ? getPropertiesValueByDBLang("products.supplier.updateSupplier.header.pageTitle", language) : getPropertiesValueByDBLang("products.supplier.addSupplier.header.pageTitle", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Header - Page title.".formatted(language));

        // check Save button
        String dbSaveBtn = commonAction.getText(loc_lblSave);
        String ppSaveBtn = getPropertiesValueByDBLang("products.supplier.addSupplier.header.saveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = commonAction.getText(loc_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.supplier.addSupplier.header.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));

        // check delete button
        if (driver.getCurrentUrl().contains("/edit")) {
            String dbDeleteBtn = commonAction.getText(loc_lblDelete);
            String ppDeleteBtn = getPropertiesValueByDBLang("products.supplier.updateSupplier.header.deleteBtn", language);
            assertCustomize.assertEquals(dbDeleteBtn, ppDeleteBtn, "[Failed][Header] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
            logger.info("[UI][%s] Check Header - Delete button.".formatted(language));
        }
    }

    void checkSupplierInformation(String language) throws Exception {
        // check supplier name
        String dbSupplierName = commonAction.getText(loc_lblSupplierName);
        String ppSupplierName = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.supplierName", language);
        assertCustomize.assertEquals(dbSupplierName, ppSupplierName, "[Failed][Supplier information] Supplier name should be %s, but found %s.".formatted(ppSupplierName, dbSupplierName));
        logger.info("[UI][%s] Check Supplier information - Supplier name.".formatted(language));

        // check supplier code
        String dbSupplierCode = commonAction.getText(loc_lblSupplierCode);
        String ppSupplierCode = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.supplierCode", language);
        assertCustomize.assertEquals(dbSupplierCode, ppSupplierCode, "[Failed][Supplier information] Supplier code should be %s, but found %s.".formatted(ppSupplierCode, dbSupplierCode));
        logger.info("[UI][%s] Check Supplier information - Supplier code.".formatted(language));

        // check supplier phone number
        String dbPhoneNumber = commonAction.getText(loc_lblPhoneNumber);
        String ppPhoneNumber = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.phoneNumber", language);
        assertCustomize.assertEquals(dbPhoneNumber, ppPhoneNumber, "[Failed][Supplier information] Supplier phone number should be %s, but found %s.".formatted(ppPhoneNumber, dbPhoneNumber));
        logger.info("[UI][%s] Check Supplier information - Supplier phone number.".formatted(language));

        // check supplier number placeholder
        String dbPhoneNumberPlaceholder = commonAction.getAttribute(loc_plhPhoneNumber, "placeholder");
        String ppPhoneNumberPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.phoneNumberPlaceholder", language);
        assertCustomize.assertEquals(dbPhoneNumberPlaceholder, ppPhoneNumberPlaceholder, "[Failed][Supplier information] Supplier phone number placeholder should be %s, but found %s.".formatted(ppPhoneNumberPlaceholder, dbPhoneNumberPlaceholder));
        logger.info("[UI][%s] Check Supplier information - Supplier phone number placeholder.".formatted(language));


        // check supplier email
        String dbEmail = commonAction.getText(loc_lblEmail);
        String ppEmail = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.email", language);
        assertCustomize.assertEquals(dbEmail, ppEmail, "[Failed][Supplier information] Supplier email should be %s, but found %s.".formatted(ppEmail, dbEmail));
        logger.info("[UI][%s] Check Supplier information - Supplier email.".formatted(language));
    }

    void checkAddressInformation(String language, boolean isVNSupplier) throws Exception {
        // check title
        String dbAddressInformation = commonAction.getText(loc_lblAddressInformation);
        String ppAddressInformation = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.title", language);
        assertCustomize.assertEquals(dbAddressInformation, ppAddressInformation, "[Failed][Address Information] Title should be %s, but found %s.".formatted(ppAddressInformation, dbAddressInformation));
        logger.info("[UI][%s] Check Address Information - Title.".formatted(language));

        // check country
        String dbCountry = commonAction.getText(loc_lblCountry);
        String ppCountry = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.country", language);
        assertCustomize.assertEquals(dbCountry, ppCountry, "[Failed][Address Information] Country should be %s, but found %s.".formatted(ppCountry, dbCountry));
        logger.info("[UI][%s] Check Address Information - Country.".formatted(language));

        if (isVNSupplier) {
            // check VN address
            String dbVNAddress = commonAction.getText(loc_lblVNAddress);
            String ppVNAddress = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.address", language);
            assertCustomize.assertEquals(dbVNAddress, ppVNAddress, "[Failed][Address Information] VN address should be %s, but found %s.".formatted(ppVNAddress, dbVNAddress));
            logger.info("[UI][%s] Check Address Information - VN address.".formatted(language));

            // check VN address placeholder
            String dbVNAddressPlaceholder = commonAction.getAttribute(loc_plhVNAddress, "placeholder");
            String ppVNAddressPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.addressPlaceholder", language);
            assertCustomize.assertEquals(dbVNAddressPlaceholder, ppVNAddressPlaceholder, "[Failed][Address Information] VN address placeholder should be %s, but found %s.".formatted(ppVNAddressPlaceholder, dbVNAddressPlaceholder));
            logger.info("[UI][%s] Check Address Information - VN address placeholder.".formatted(language));

            // check VN city/province
            String dbVNCity = commonAction.getText(loc_lblVNCity);
            String ppVNCity = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.city/province", language);
            assertCustomize.assertEquals(dbVNCity, ppVNCity, "[Failed][Address Information] VN city should be %s, but found %s.".formatted(ppVNCity, dbVNCity));
            logger.info("[UI][%s] Check Address Information - VN city.".formatted(language));

            // check VN city/province default option
            String dbVNCityDefaultOption = commonAction.getText(loc_lblDefaultVNCity);
            String ppVNCityDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.city/provinceDefaultOption", language);
            assertCustomize.assertEquals(dbVNCityDefaultOption, ppVNCityDefaultOption, "[Failed][Address Information] VN city default option should be %s, but found %s.".formatted(ppVNCityDefaultOption, dbVNCityDefaultOption));
            logger.info("[UI][%s] Check Address Information - VN city default option.".formatted(language));

            // check VN district
            String dbVNDistrict = commonAction.getText(loc_lblVNDistrict);
            String ppVNDistrict = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.district", language);
            assertCustomize.assertEquals(dbVNDistrict, ppVNDistrict, "[Failed][Address Information] VN district should be %s, but found %s.".formatted(ppVNDistrict, dbVNDistrict));
            logger.info("[UI][%s] Check Address Information - VN district.".formatted(language));

            // check VN district default option
            String dbVNDistrictDefaultOption = commonAction.getText(loc_lblDefaultVNDistrict);
            String ppVNDistrictDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.districtDefaultOption", language);
            assertCustomize.assertEquals(dbVNDistrictDefaultOption, ppVNDistrictDefaultOption, "[Failed][Address Information] VN district default option should be %s, but found %s.".formatted(ppVNDistrictDefaultOption, dbVNDistrictDefaultOption));
            logger.info("[UI][%s] Check Address Information - VN district default option.".formatted(language));

            // check VN ward
            String dbVNWard = commonAction.getText(loc_lblVNWard);
            String ppVNWard = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.ward", language);
            assertCustomize.assertEquals(dbVNWard, ppVNWard, "[Failed][Address Information] VN ward should be %s, but found %s.".formatted(ppVNWard, dbVNWard));
            logger.info("[UI][%s] Check Address Information - VN ward.".formatted(language));

            // check VN ward default option
            String dbVNWardDefaultOption = commonAction.getText(loc_lblDefaultVNWard);
            String ppVNWardDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.wardDefaultOption", language);
            assertCustomize.assertEquals(dbVNWardDefaultOption, ppVNWardDefaultOption, "[Failed][Address Information] VN ward default option should be %s, but found %s.".formatted(ppVNWardDefaultOption, dbVNWardDefaultOption));
            logger.info("[UI][%s] Check Address Information - VN ward default option.".formatted(language));
        } else {
            // check non-VN street address
            String dbNonVNStreetAddress = commonAction.getText(loc_lblNonVNAddress);
            String ppNonVNStreetAddress = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.streetAddress", language);
            assertCustomize.assertEquals(dbNonVNStreetAddress, ppNonVNStreetAddress, "[Failed][Address Information] Non-VN street address should be %s, but found %s.".formatted(ppNonVNStreetAddress, dbNonVNStreetAddress));
            logger.info("[UI][%s] Check Address Information - Non-VN street address.".formatted(language));

            // check non-VN street address placeholder
            String dbNonVNStreetAddressPlaceholder = commonAction.getAttribute(loc_plhNonVNAddress, "placeholder");
            String ppNonVNStreetAddressPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.streetAddressPlaceholder", language);
            assertCustomize.assertEquals(dbNonVNStreetAddressPlaceholder, ppNonVNStreetAddressPlaceholder, "[Failed][Address Information] Non-VN street address placeholder should be %s, but found %s.".formatted(ppNonVNStreetAddressPlaceholder, dbNonVNStreetAddressPlaceholder));
            logger.info("[UI][%s] Check Address Information - Non-VN address placeholder.".formatted(language));

            // check non-VN address2
            String dbNonVNAddress2 = commonAction.getText(loc_lblNonVNAddress2);
            String ppNonVNAddress2 = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.address2", language);
            assertCustomize.assertEquals(dbNonVNAddress2, ppNonVNAddress2, "[Failed][Address Information] Non-VN address2 should be %s, but found %s.".formatted(ppNonVNAddress2, dbNonVNAddress2));
            logger.info("[UI][%s] Check Address Information - Non-VN address2.".formatted(language));

            // check non-VN address2 placeholder
            String dbNonVNAddress2Placeholder = commonAction.getAttribute(loc_plhNonVNAddress2, "placeholder");
            String ppNonVNAddress2Placeholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.address2Placeholder", language);
            assertCustomize.assertEquals(dbNonVNAddress2Placeholder, ppNonVNAddress2Placeholder, "[Failed][Address Information] Non-VN address2 placeholder should be %s, but found %s.".formatted(ppNonVNAddress2Placeholder, dbNonVNAddress2Placeholder));
            logger.info("[UI][%s] Check Address Information - Non-VN address2 placeholder.".formatted(language));

            // check non-VN city
            String dbNonVNCity = commonAction.getText(loc_lblNonVNCity);
            String ppNonVNCity = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.city", language);
            assertCustomize.assertEquals(dbNonVNCity, ppNonVNCity, "[Failed][Address Information] Non-VN city should be %s, but found %s.".formatted(ppNonVNCity, dbNonVNCity));
            logger.info("[UI][%s] Check Address Information - Non-VN city.".formatted(language));

            // check non-VN city placeholder
            String dbNonVNCityPlaceholder = commonAction.getAttribute(loc_plhNonVNCity, "placeholder");
            String ppNonVNCityPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.cityPlaceholder", language);
            assertCustomize.assertEquals(dbNonVNCityPlaceholder, ppNonVNCityPlaceholder, "[Failed][Address Information] Non-VN city placeholder should be %s, but found %s.".formatted(ppNonVNCityPlaceholder, dbNonVNCityPlaceholder));
            logger.info("[UI][%s] Check Address Information - Non-VN city placeholder.".formatted(language));

            // check non-VN state/region/province
            String dbNonVNProvince = commonAction.getText(loc_lblNonVNState);
            String ppNonVNProvince = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.state/region/province", language);
            assertCustomize.assertEquals(dbNonVNProvince, ppNonVNProvince, "[Failed][Address Information] Non-VN state/region/province should be %s, but found %s.".formatted(ppNonVNProvince, dbNonVNProvince));
            logger.info("[UI][%s] Check Address Information - Non-VN state/region/province.".formatted(language));

            // check non-VN state/region/province default option
            String dbNonVNProvinceDefaultOption = commonAction.getText(loc_lblDefaultNonVNState);
            String ppNonVNProvinceDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.state/region/provinceDefaultOption", language);
            assertCustomize.assertEquals(dbNonVNProvinceDefaultOption, ppNonVNProvinceDefaultOption, "[Failed][Address Information] Non-VN state/region/province default option should be %s, but found %s.".formatted(ppNonVNProvinceDefaultOption, dbNonVNProvinceDefaultOption));
            logger.info("[UI][%s] Check Address Information - Non-VN state/region/province default option.".formatted(language));

            // check non-VN zipcode
            String dbNonVNZipcode = commonAction.getText(loc_lblNonVnZipcode);
            String ppNonVNZipcode = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.zipcode", language);
            assertCustomize.assertEquals(dbNonVNZipcode, ppNonVNZipcode, "[Failed][Address Information] Non-VN zipcode should be %s, but found %s.".formatted(ppNonVNZipcode, dbNonVNZipcode));
            logger.info("[UI][%s] Check Address Information - Non-VN zipcode.".formatted(language));

            // check non-VN zipcode placeholder
            String dbNonVNZipcodePlaceholder = commonAction.getAttribute(loc_plhNonVNZipcode, "placeholder");
            String ppNonVNZipcodePlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.zipcodePlaceholder", language);
            assertCustomize.assertEquals(dbNonVNZipcodePlaceholder, ppNonVNZipcodePlaceholder, "[Failed][Address Information] Non-VN zipcode placeholder should be %s, but found %s.".formatted(ppNonVNZipcodePlaceholder, dbNonVNZipcodePlaceholder));
            logger.info("[UI][%s] Check Address Information - Non-VN zipcode placeholder.".formatted(language));
        }
    }

    void checkOrderHistory(String language) throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_lblOrderHistory);
        String ppTitle = getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Order History] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Order History - Title.".formatted(language));

        // check search placeholder
        String dbSearchPlaceholder = commonAction.getAttribute(loc_plhSearchOrderHistory, "placeholder");
        String ppSearchPlaceholder = getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.searchPlaceholder", language);
        assertCustomize.assertEquals(dbSearchPlaceholder, ppSearchPlaceholder, "[Failed][Order History] Search placeholder should be %s, but found %s.".formatted(ppSearchPlaceholder, dbSearchPlaceholder));
        logger.info("[UI][%s] Check Order History - Search placeholder.".formatted(language));

        // check order history table column
        List<String> dbTableColumn = commonAction.getListElement(loc_tblOrderHistory).stream().map(WebElement::getText).toList();
        List<String> ppTableColumn = List.of(getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.0", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.1", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.2", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.3", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.4", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.5", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.6", language));
        assertCustomize.assertEquals(dbTableColumn, ppTableColumn, "[Failed][Order History] Order history table should be %s, but found %s.".formatted(ppTableColumn, dbTableColumn));
        logger.info("[UI][%s] Check Order History - Order history table.".formatted(language));
    }

    public void checkNoOrderHistory(String language) throws Exception {
        // check no result
        String dbNoResult = commonAction.getText(loc_lblNoOrderHistory);
        String ppNoResult = getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.noResult", language);
        assertCustomize.assertEquals(dbNoResult, ppNoResult, "[Failed][Order History] No result should be %s, but found %s.".formatted(ppNoResult, dbNoResult));
        logger.info("[UI][%s] Check Order History - No result.".formatted(language));
    }

    void checkSupplierSummary(String language) throws Exception {
        // check title
        String dbSupplierSummary = commonAction.getText(loc_lblSupplierSummary);
        String ppSupplierSummary = getPropertiesValueByDBLang("products.supplier.updateSupplier.supplierSummary.title", language);
        assertCustomize.assertEquals(dbSupplierSummary, ppSupplierSummary, "[Failed][Supplier summary] Title should be %s, but found %s.".formatted(ppSupplierSummary, dbSupplierSummary));
        logger.info("[UI][%s] Check Supplier summary - Title.".formatted(language));

        // check status title
        String dbStatusTitle = commonAction.getText(loc_lblSupplierStatus);
        String ppStatusTitle = getPropertiesValueByDBLang("products.supplier.updateSupplier.supplierSummary.status.title", language);
        assertCustomize.assertEquals(dbStatusTitle, ppStatusTitle, "[Failed][Supplier summary] Supplier status title should be %s, but found %s.".formatted(ppStatusTitle, dbStatusTitle));
        logger.info("[UI][%s] Check Supplier summary - Supplier status title.".formatted(language));

        // check status label
        String dbStatusLabel = commonAction.getText(loc_lblStatusOfSupplier);
        String ppStatusLabel = getPropertiesValueByDBLang("products.supplier.updateSupplier.supplierSummary.status.label", language);
        assertCustomize.assertEquals(dbStatusLabel, ppStatusLabel, "[Failed][Supplier summary] Supplier status label should be %s, but found %s.".formatted(ppStatusLabel, dbStatusLabel));
        logger.info("[UI][%s] Check Supplier summary - Supplier status label.".formatted(language));
    }

    void checkOtherInformation(String language) throws Exception {
        // check title
        String dbOtherInformation = commonAction.getText(loc_lblOtherInformation);
        String ppOtherInformation = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.title", language);
        assertCustomize.assertEquals(dbOtherInformation, ppOtherInformation, "[Failed][Other information] Title should be %s, but found %s.".formatted(ppOtherInformation, dbOtherInformation));
        logger.info("[UI][%s] Check Other information - Title.".formatted(language));

        // check responsible staff
        String dbResponsibleStaff = commonAction.getText(loc_lblResponsibleStaff);
        String ppResponsibleStaff = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.responsibleStaff", language);
        assertCustomize.assertEquals(dbResponsibleStaff, ppResponsibleStaff, "[Failed][Other information] Responsible staff should be %s, but found %s.".formatted(ppResponsibleStaff, dbResponsibleStaff));
        logger.info("[UI][%s] Check Other information - Responsible staff.".formatted(language));

        // check description
        String dbDescription = commonAction.getText(loc_lblDescription);
        String ppDescription = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.description", language);
        assertCustomize.assertEquals(dbDescription, ppDescription, "[Failed][Other information] Description should be %s, but found %s.".formatted(ppDescription, dbDescription));
        logger.info("[UI][%s] Check Other information - Description.".formatted(language));
    }

    public void checkConfirmDeleteSupplierPopup(String language) throws Exception {
        // check title
        String dbTitle = commonAction.getText(loc_dlgConfirmDeleteSupplierTitle);
        String ppTitle = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.title", language);
        assertCustomize.assertEquals(dbTitle, ppTitle, "[Failed][Delete supplier confirm popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Title.".formatted(language));

        // check content
        String dbContent = commonAction.getText(loc_dlgConfirmDeleteSupplierContent);
        String ppContent = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.content", language);
        assertCustomize.assertEquals(dbContent, ppContent, "[Failed][Delete supplier confirm popup] Content should be %s, but found %s.".formatted(ppContent, dbContent));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Content.".formatted(language));

        // check Delete button
        String dbDeleteBtn = commonAction.getText(loc_dlgConfirmDeleteSupplier_lblDelete);
        String ppDeleteBtn = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.deleteBtn", language);
        assertCustomize.assertEquals(dbDeleteBtn, ppDeleteBtn, "[Failed][Delete supplier confirm popup] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Delete button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = commonAction.getText(loc_dlgConfirmDeleteSupplier_lblCancel);
        String ppCancelBtn = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][Delete supplier confirm popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Cancel button.".formatted(language));
    }

    public void checkErrorWhenLeaveRequiredFieldBlank(String language) throws Exception {
        String dbSupplierBlank = commonAction.getText(loc_lblSupplierNameError);
        String ppSupplierBlank = getPropertiesValueByDBLang("products.supplier.addSupplier.error.supplierName.blank", language);
        assertCustomize.assertEquals(dbSupplierBlank, ppSupplierBlank, "[Failed][Validate] Error when leave supplier name blank should be %s, but found %s.".formatted(ppSupplierBlank, dbSupplierBlank));
        logger.info("[UI][%s] Check Validate - Leave supplier name blank.".formatted(language));
    }

    public void checkErrorWhenInputDuplicateSupplierCode(String language) throws Exception {
        String dbSupplierCodeDuplicate = commonAction.getText(lblSupplierCodeError);
        String ppSupplierCodeDuplicate = getPropertiesValueByDBLang("products.supplier.addSupplier.error.supplierCode.duplicate", language);
        assertCustomize.assertEquals(dbSupplierCodeDuplicate, ppSupplierCodeDuplicate, "[Failed][Validate] Error when input duplicate supplier code should be %s, but found %s.".formatted(ppSupplierCodeDuplicate, dbSupplierCodeDuplicate));
        logger.info("[UI][%s] Check Validate - Input duplicate supplier code.".formatted(language));
    }

    public void checkErrorWhenInputInvalidFormatSupplierCode(String language) throws Exception {
        String dbInvalidSupplierCode = commonAction.getText(lblSupplierCodeError);
        String ppInvalidSupplierCode = getPropertiesValueByDBLang("products.supplier.addSupplier.error.supplierCode.invalidFormat", language);
        assertCustomize.assertEquals(dbInvalidSupplierCode, ppInvalidSupplierCode, "[Failed][Validate] Error when input invalid format supplier code should be %s, but found %s.".formatted(ppInvalidSupplierCode, dbInvalidSupplierCode));
        logger.info("[UI][%s] Check Validate - Input invalid format supplier code .".formatted(language));
    }

    public void checkUIAddSupplierPage(String language, boolean isVNSupplier) throws Exception {
        checkHeader(language);
        checkSupplierInformation(language);
        checkAddressInformation(language, isVNSupplier);
        checkOtherInformation(language);
    }

    public void checkUIUpdateSupplierPage(String language, boolean isVNSupplier) throws Exception {
        checkHeader(language);
        checkSupplierInformation(language);
        checkAddressInformation(language, isVNSupplier);
        checkOrderHistory(language);
        checkSupplierSummary(language);
        checkOtherInformation(language);
    }


    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-13849
    AllPermissions permissions;
    CheckPermission checkPermission;

    public void addNewSupplier() {
        // get supplier name
        String name = Pattern.compile("([\\w\\W]{0,100})").matcher("Auto - Supplier %s - %s".formatted(nextBoolean() ? "VN" : "Non VN",
                Instant.now().toEpochMilli()))
                .results()
                .map(matchResult -> matchResult.group(1)).toList().get(0);
        // input supplier name
        inputSupplierName(name);

        // complete create supplier
        completeCRUSupplier();

        // check supplier is updated or not
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not update supplier.");
    }

    public void checkViewSupplierDetail(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // get supplier id
        int supplierId = supplierAPIWithSellerToken.getListSupplierID("").isEmpty()
                ? supplierAPIWithSellerToken.createSupplierAndGetSupplierID()
                : supplierAPIWithSellerToken.getListSupplierID("").get(0);

        // check permission
        if (permissions.getSuppliers().getSupplier().isViewSupplierDetail()) {
            // check can access to supplier detail by URL
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/supplier/edit/%s".formatted(DOMAIN, supplierId),
                            String.valueOf(supplierId)),
                    "Can not access to supplier detail page.");

//            // check edit supplier
//            checkEditSupplier(supplierId);
//
//            // check delete supplier
//            checkDeleteSupplier(supplierId);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/supplier/edit/%s".formatted(DOMAIN, supplierId)),
                    "Restricted page is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Supplier >> View supplier detail.");
    }

    void checkEditSupplier(int supplierId) {
        // navigate to supplier detail page
        navigateSupplierDetailPage(supplierId);

        // check permission
        if (permissions.getSuppliers().getSupplier().isEditSupplier()) {
            // check can update supplier
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnHeaderSave,
                            loc_dlgToastSuccess),
                    "Can not update supplier.");
        } else {
            // show restricted popup
            // if staff don’t have permission “Edit supplier”
            // and click on [Save] button in Supplier detail page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnHeaderSave),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Supplier >> Edit supplier.");
    }

    void checkDeleteSupplier(int supplierId) {
        // navigate to supplier detail page
        navigateSupplierDetailPage(supplierId);

        // check can delete supplier
        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnHeaderDelete,
                        loc_dlgConfirmDeleteSupplier),
                "Can not open confirm delete supplier popup.");

        if (!commonAction.getListElement(loc_dlgConfirmDeleteSupplier).isEmpty()) {
            // check permission
            if (permissions.getSuppliers().getSupplier().isDeleteSupplier()) {
                if (!commonAction.getListElement(loc_dlgConfirmDeleteSupplier).isEmpty()) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmDeleteSupplier_btnDelete,
                                    loc_dlgToastSuccess),
                            "Can not delete supplier.");
                }
            } else {
                // if staff don’t have permission “Delete supplier”
                // => show restricted popup
                // when staff click [Delete] button on popup confirm delete a supplier
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgConfirmDeleteSupplier_btnDelete),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Supplier >> Delete supplier.");
    }
}
