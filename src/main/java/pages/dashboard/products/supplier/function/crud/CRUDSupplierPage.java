package pages.dashboard.products.supplier.function.crud;

import api.dashboard.products.PurchaseOrders;
import api.dashboard.products.SupplierAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.supplier.function.management.SupplierManagementPage;
import pages.dashboard.products.supplier.ui.crud.UICRUDSupplierPage;
import utilities.UICommonAction;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.openqa.selenium.Keys.CONTROL;
import static org.openqa.selenium.Keys.DELETE;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

/**
 * CRUD:
 * <p> CR: Create</p>
 * <p> U: Update</p>
 * <p> D: Delete</p>
 */
public class CRUDSupplierPage extends CRUDSupplierElement {

    Logger logger = LogManager.getLogger(CRUDSupplierPage.class);

    WebDriverWait wait;
    public String language;
    UICommonAction commonAction;
    Actions act;
    SupplierManagementPage supplierManagementPage;
    UICRUDSupplierPage uiCRUDSupplierPage;
    SupplierAPI sup = new SupplierAPI();
    String supplierName;
    String supplierCode;
    String phoneNumber;
    String email;

    public CRUDSupplierPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        act = new Actions(driver);
        supplierManagementPage = new SupplierManagementPage(driver);
        uiCRUDSupplierPage = new UICRUDSupplierPage(driver);
    }

    public CRUDSupplierPage setLanguage(String language) {
        this.language = language;
        // set dashboard language
        commonAction.sleepInMiliSecond(1000);

        System.out.println(((JavascriptExecutor) driver).executeScript("return localStorage.getItem('langKey')"));
        System.out.println(language);
        String currentLanguage = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('langKey')").equals("vi") ? "VIE" : "ENG";



        if (!currentLanguage.equals(language)) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", HEADER_SELECTED_LANGUAGE);
            HEADER_LANGUAGE_LIST.stream().filter(webElement -> webElement.getText().contains(language))
                    .findFirst().ifPresent(webElement -> ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click()", webElement));
        }

        return this;
    }

    void inputSupplierName(String name) {
        // input supplier name
        wait.until(elementToBeClickable(SUPPLIER_NAME)).click();
        SUPPLIER_NAME.sendKeys(CONTROL + "a", DELETE);
        SUPPLIER_NAME.sendKeys(name);
        logger.info("Input supplier name: %s".formatted(name));
    }

    void inputSupplierCode(String code) {
        // input supplier code
        wait.until(elementToBeClickable(SUPPLIER_CODE)).click();
        SUPPLIER_CODE.sendKeys(CONTROL + "a", DELETE);
        SUPPLIER_CODE.sendKeys(code);
        logger.info("Input supplier code: %s".formatted(code));

    }

    void inputPhoneNumber(String phone) {
        // input phone number
        wait.until(elementToBeClickable(PHONE_NUMBER)).click();
        PHONE_NUMBER.sendKeys(CONTROL + "a", DELETE);
        PHONE_NUMBER.sendKeys(phone);
        logger.info("Input phone number: %s".formatted(phone));
    }

    void inputEmail(String email) {
        // input email
        wait.until(elementToBeClickable(EMAIL)).click();
        EMAIL.sendKeys(CONTROL + "a", DELETE);
        EMAIL.sendKeys(email);
        logger.info("Input email: %s".formatted(email));
    }

    void selectCountry(boolean isVNSupplier) {
        // open country dropdown
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", COUNTRY_DROPDOWN);
        commonAction.sleepInMiliSecond(1000);

        // check supplier has locator on VN or not
        if (isVNSupplier) {
            // select country = Vietnam
            new Select(COUNTRY_DROPDOWN).selectByValue("VN");
            logger.info("Select country: Vietnam");
        } else {
            // select another country
            List<String> countryCode = COUNTRY_LIST.stream().map(webElement -> webElement.getAttribute("value")).toList();
            int index = nextInt(countryCode.size());
            while (countryCode.get(index).equals("VN")) index = nextInt(countryCode.size());
            new Select(COUNTRY_DROPDOWN).selectByIndex(index);
            logger.info("Select country: %s".formatted(COUNTRY_LIST.get(index).getText()));
        }

        commonAction.sleepInMiliSecond(2000);
    }

    void inputVNAddress(String address) {
        // input address
        act.moveToElement(VN_ADDRESS).click().build().perform();
        VN_ADDRESS.sendKeys(CONTROL + "a", DELETE);
        VN_ADDRESS.sendKeys(address);
        logger.info("Input address: %s".formatted(address));
    }

    /* VN address */
    void selectVNCity() {
        // open city dropdown
        wait.until(elementToBeClickable(VN_CITY_DROPDOWN)).click();

        // get index of selected city
        int index = nextInt(VN_CITY_LIST.size());
        new Select(VN_CITY_DROPDOWN).selectByIndex(index);
        logger.info("Select city: %s".formatted(VN_CITY_LIST.get(index).getText()));

        commonAction.sleepInMiliSecond(2000);
    }

    void selectVNDistrict() {
        // open district dropdown
        wait.until(elementToBeClickable(VN_DISTRICT_DROPDOWN)).click();

        // get index of selected district
        int index = nextInt(VN_DISTRICT_LIST.size());
        new Select(VN_DISTRICT_DROPDOWN).selectByIndex(index);
        logger.info("Select district: %s".formatted(VN_DISTRICT_LIST.get(index).getText()));

        commonAction.sleepInMiliSecond(2000);
    }

    void selectVNWard() {
        // open ward dropdown
        wait.until(elementToBeClickable(VN_WARD_DROPDOWN)).click();

        // get index of selected ward
        int index = nextInt(VN_WARD_LIST.size());
        new Select(VN_WARD_DROPDOWN).selectByIndex(index);
        logger.info("Select ward: %s".formatted(VN_WARD_LIST.get(index).getText()));
    }

    /* non-VN address */
    void inputNonVNStreetAddress(String address) {
        // input address
        act.moveToElement(NON_VN_STREET_ADDRESS).click().build().perform();
        NON_VN_STREET_ADDRESS.sendKeys(CONTROL + "a", DELETE);
        NON_VN_STREET_ADDRESS.sendKeys(address);
        logger.info("Input street address: %s".formatted(address));
    }

    void inputNonVNAddress2(String address2) {
        // input address 2 for non-VN country
        act.moveToElement(NON_VN_ADDRESS2).click().build().perform();
        NON_VN_ADDRESS2.sendKeys(CONTROL + "a", DELETE);
        NON_VN_ADDRESS2.sendKeys(address2);
        logger.info("Input address2: %s".formatted(address2));
    }

    void inputNonVNCity(String city) {
        // input city for non-VN country
        act.moveToElement(NON_VN_CITY).click().build().perform();
        NON_VN_CITY.sendKeys(CONTROL + "a", DELETE);
        NON_VN_CITY.sendKeys(city);
        logger.info("Input city: %s".formatted(city));
    }

    void selectNonVNProvince() {
        // open province dropdown
        wait.until(elementToBeClickable(NON_VN_PROVINCE_DROPDOWN)).click();

        commonAction.sleepInMiliSecond(1000);

        // get index of selected province
        int index = nextInt(NON_VN_PROVINCE_LIST.size());
        new Select(NON_VN_PROVINCE_DROPDOWN).selectByIndex(index);
        logger.info("Select province: %s".formatted(NON_VN_PROVINCE_LIST.get(index).getText()));
    }

    void inputNonVNZipcode(String zipcode) {
        // input zipcode for non-VN country
        act.moveToElement(NON_VN_ZIP_CODE).click().build().perform();
        commonAction.sleepInMiliSecond(1000);
        NON_VN_ZIP_CODE.sendKeys(CONTROL + "a", DELETE);
        NON_VN_ZIP_CODE.sendKeys(zipcode);
        logger.info("Input zipcode: %s".formatted(zipcode));
    }

    void selectResponsibleStaff() {
        // open responsible staff dropdown
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", RESPONSIBLE_STAFF_DROPDOWN);
        commonAction.sleepInMiliSecond(1000);

        // get index of selected staff
        int index = nextInt(RESPONSIBLE_STAFF_LIST.size());
        new Select(RESPONSIBLE_STAFF_DROPDOWN).selectByIndex(index);
        logger.info("Select responsible staff: %s".formatted(RESPONSIBLE_STAFF_LIST.get(index).getText()));
    }

    void inputDescription(String description) {
        act.moveToElement(DESCRIPTION).click().build().perform();
        DESCRIPTION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        DESCRIPTION.sendKeys(description);
        logger.info("Input description: %s".formatted(description));
    }

    void completeCRUSupplier() {
        act.moveToElement(HEADER_SAVE_BTN).click().build().perform();
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
        uiCRUDSupplierPage.checkErrorWhenLeaveRequiredFieldBlank(language);

        verifyTest();
    }

    public void checkErrorWhenInputDuplicateSupplierCode() throws Exception {
        // navigate to create supplier page
        supplierManagementPage.navigateToAddSupplierPage();

        // input supplier name
        inputSupplierName("abc");

        // get available supplier code
        SupplierAPI sup = new SupplierAPI();
        List<String> supplierCodeList = sup.getListSupplierCode("");
        String supplierCode = (supplierCodeList.size() == 0) ? sup.createSupplierAndGetSupplierCode() : sup.getListSupplierCode("").get(0);

        // input available supplier code
        inputSupplierCode(supplierCode);

        // click Save button
        completeCRUSupplier();

        // check error when input available supplier code
        uiCRUDSupplierPage.checkErrorWhenInputDuplicateSupplierCode(language);

        verifyTest();
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
        uiCRUDSupplierPage.checkErrorWhenInputInvalidFormatSupplierCode(language);

        verifyTest();
    }

    public void createNewSupplier(boolean isVNSupplier) throws Exception {
        // navigate to create supplier page
        supplierManagementPage.navigateToAddSupplierPage();

        // select country
        selectCountry(isVNSupplier);

        // check UI
        uiCRUDSupplierPage.checkUIAddSupplierPage(language, isVNSupplier);

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

        // wait supplier management page loaded
        commonAction.sleepInMiliSecond(3000);

        // check information after create supplier
        supplierManagementPage.checkSupplierInformationAfterCRU(supplierCode, supplierName, email, phoneNumber, uiCRUDSupplierPage);

        verifyTest();
    }

    void navigateSupplierDetailPage() {
        // navigate to supplier detail page
        supplierCode = (sup.getListSupplierCode("").size() == 0) ? sup.createSupplierAndGetSupplierCode() : sup.getListSupplierCode("").get(0);
        supplierManagementPage.findAndNavigateToSupplierDetailPage(supplierCode);
    }

    public void updateSupplier(boolean isVNSupplier) throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // select country
        selectCountry(isVNSupplier);

        // check UI
        uiCRUDSupplierPage.checkUIUpdateSupplierPage(language, isVNSupplier);

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

        // wait supplier management page loaded
        commonAction.sleepInMiliSecond(3000);

        // check information after update supplier
        supplierManagementPage.checkSupplierInformationAfterCRU(supplierCode, supplierName, email, phoneNumber, uiCRUDSupplierPage);

        verifyTest();
    }

    public void checkSupplierInformation() throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // get supplier ID
        int supplierID = Pattern.compile("(\\d+)").matcher(driver.getCurrentUrl()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);

        // get supplier information map
        Map<String, String> supInfo = sup.getSupplierInformationMap(supplierID);

        // check supplier name
        String dbSupplierName = wait.until(visibilityOf(SUPPLIER_NAME)).getAttribute("value");
        String infoSupplierName = supInfo.get("name");
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbSupplierName, infoSupplierName, "[Failed][Supplier Information] Supplier name should be %s, but found %s.".formatted(infoSupplierName, dbSupplierName));
        logger.info("Check Supplier Information - Supplier name.");

        // check supplier code
        String dbSupplierCode = wait.until(visibilityOf(SUPPLIER_CODE)).getAttribute("value");
        String infoSupplierCode = supInfo.get("code");
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbSupplierCode, infoSupplierCode, "[Failed][Supplier Information] Supplier code should be %s, but found %s.".formatted(infoSupplierCode, dbSupplierCode));
        logger.info("Check Supplier Information - Supplier code.");

        // check phone number
        String dbPhoneNumber = wait.until(visibilityOf(PHONE_NUMBER)).getAttribute("value");
        String infoPhoneNumber = supInfo.get("phoneNumber");
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbPhoneNumber, infoPhoneNumber, "[Failed][Supplier Information] Phone number should be %s, but found %s.".formatted(infoPhoneNumber, dbPhoneNumber));
        logger.info("Check Supplier Information - Phone number.");

        // check email
        String dbEmail = wait.until(visibilityOf(EMAIL)).getAttribute("value");
        String infoEmail = supInfo.get("email");
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbEmail, infoEmail, "[Failed][Supplier Information] Email should be %s, but found %s.".formatted(infoEmail, dbEmail));
        logger.info("Check Supplier Information - Email.");

        // check country
        String dbCountry = wait.until(visibilityOf(COUNTRY_DROPDOWN)).getAttribute("value");
        String infoCountry = supInfo.get("countryCode");
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbCountry, infoCountry, "[Failed][Supplier Information] Country should be %s, but found %s.".formatted(infoCountry, dbCountry));
        logger.info("Check Supplier Information - Country.");

        // check VN supplier
        if (supInfo.get("countryCode").equals("VN")) {
            // check address
            String dbAddress = wait.until(visibilityOf(VN_ADDRESS)).getAttribute("value");
            String infoAddress = supInfo.get("address");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbAddress, infoAddress, "[Failed][Supplier Information] Address should be %s, but found %s.".formatted(infoAddress, dbAddress));
            logger.info("Check Supplier Information - Address.");

            // check city/province
            String dbCity = wait.until(visibilityOf(VN_CITY_DROPDOWN)).getAttribute("value");
            String infoCity = supInfo.get("province");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbCity, infoCity, "[Failed][Supplier Information] City/Province should be %s, but found %s.".formatted(infoCity, dbCity));
            logger.info("Check Supplier Information - City/Province.");

            // check district
            String dbDistrict = wait.until(visibilityOf(VN_DISTRICT_DROPDOWN)).getAttribute("value");
            String infoDistrict = supInfo.get("district");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbDistrict, infoDistrict, "[Failed][Supplier Information] District should be %s, but found %s.".formatted(infoDistrict, dbDistrict));
            logger.info("Check Supplier Information - District.");

            // check ward
            String dbWard = wait.until(visibilityOf(VN_WARD_DROPDOWN)).getAttribute("value");
            String infoWard = supInfo.get("ward");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbWard, infoWard, "[Failed][Supplier Information] Ward should be %s, but found %s.".formatted(infoWard, dbWard));
            logger.info("Check Supplier Information - Ward.");
        }
        // check Non-VN supplier
        else {
            // check street address
            String dbStreetAddress = wait.until(visibilityOf(NON_VN_STREET_ADDRESS)).getAttribute("value");
            String infoStreetAddress = supInfo.get("address");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbStreetAddress, infoStreetAddress, "[Failed][Supplier Information] Street address should be %s, but found %s.".formatted(infoStreetAddress, dbStreetAddress));
            logger.info("Check Supplier Information - Street address.");

            // check address2
            String dbAddress2 = wait.until(visibilityOf(NON_VN_ADDRESS2)).getAttribute("value");
            String infoAddress2 = supInfo.get("address2");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbAddress2, infoAddress2, "[Failed][Supplier Information] Address2 should be %s, but found %s.".formatted(infoAddress2, dbAddress2));
            logger.info("Check Supplier Information - Address2.");

            // check city
            String dbCity = wait.until(visibilityOf(NON_VN_CITY)).getAttribute("value");
            String infoCity = supInfo.get("cityName");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbCity, infoCity, "[Failed][Supplier Information] City should be %s, but found %s.".formatted(infoCity, dbCity));
            logger.info("Check Supplier Information - City.");

            // check state/region/province
            String dbProvince = wait.until(visibilityOf(NON_VN_PROVINCE_DROPDOWN)).getAttribute("value");
            String infoProvince = supInfo.get("province");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbProvince, infoProvince, "[Failed][Supplier Information] State/Region/Province should be %s, but found %s.".formatted(infoProvince, dbProvince));
            logger.info("Check Supplier Information - State/Region/Province.");

            // check zip code
            String dbZipCode = wait.until(visibilityOf(NON_VN_ZIP_CODE)).getAttribute("value");
            String infoZipCode = supInfo.get("zipCode");
            uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbZipCode, infoZipCode, "[Failed][Supplier Information] Zip code should be %s, but found %s.".formatted(infoZipCode, dbZipCode));
            logger.info("Check Supplier Information - Zip code.");
        }

        // check responsible staff
        String dbResponsibleStaff = wait.until(visibilityOf(RESPONSIBLE_STAFF_DROPDOWN)).getAttribute("value");
        String infoResponsibleStaff = supInfo.get("responsibleStaff") != null ? supInfo.get("responsibleStaff") : "";
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbResponsibleStaff, infoResponsibleStaff, "[Failed][Supplier Information] Responsible staff should be %s, but found %s.".formatted(infoResponsibleStaff, dbResponsibleStaff));
        logger.info("Check Supplier Information - Responsible staff.");

        // check description
        String dbDescription = wait.until(visibilityOf(DESCRIPTION)).getAttribute("value");
        String infoDescription = supInfo.get("description") != null ? supInfo.get("description") : "";
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertEquals(uiCRUDSupplierPage.countFail, dbDescription, infoDescription, "[Failed][Supplier Information] Description should be %s, but found %s.".formatted(infoDescription, dbDescription));
        logger.info("Check Supplier Information - Description.");

        verifyTest();
    }

    public void checkOrderHistory() throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // get supplier ID
        int supplierID = Pattern.compile("(\\d+)").matcher(driver.getCurrentUrl()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);

        // if no purchase orders, post API to create data test
        List<String> listAvailablePurchaseId = sup.getListOrderId("", supplierID);
        String purchaseId = (listAvailablePurchaseId.size() == 0) ? new PurchaseOrders().createPurchaseOrderAndGetOrderId() : listAvailablePurchaseId.get(0);

        // input valid purchaseId and search
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).click();
        SEARCH_BOX.sendKeys(CONTROL + "a", DELETE);
        SEARCH_BOX.sendKeys("%s\n".formatted(purchaseId));

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list result
        List<String> listResult = LIST_RESULT.stream().map(WebElement::getText).toList();

        // verify list result shows correctly
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, listResult.stream().allMatch(result -> result.contains(purchaseId)), "[Failed][Order history] List result shown incorrectly, keywords: %s, but found list result: %s.".formatted(purchaseId, listResult));
        logger.info("Check Order history - Has search result.");

        // input invalid purchaseId and search
        // input valid purchaseId and search
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).click();
        SEARCH_BOX.sendKeys(CONTROL + "a", DELETE);
        SEARCH_BOX.sendKeys("%s\n".formatted(Instant.now().toEpochMilli()));

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list result
        listResult = LIST_RESULT.stream().map(WebElement::getText).toList();

        // verify list result shows correctly
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, listResult.size() == 0, "[Failed][Order history] Purchase orders is shown although search with invalid keywords.");
        logger.info("Check Order history - No search result.");

        // check UI no search result
        uiCRUDSupplierPage.checkNoOrderHistory(language);

        verifyTest();
    }

    public void deleteSupplier() throws Exception {
        // navigate to supplier detail page
        navigateSupplierDetailPage();

        // get supplier name
        String supplierName = SUPPLIER_NAME.getAttribute("value");

        // open Confirm delete supplier popup
        wait.until(ExpectedConditions.elementToBeClickable(HEADER_DELETE_BTN)).click();

        // wait confirm popup visible
        wait.until(ExpectedConditions.visibilityOf(CONFIRM_DELETE_SUPPLIER_POPUP));

        // check UI
        uiCRUDSupplierPage.checkConfirmDeleteSupplierPopup(language);

        // confirm delete supplier
        wait.until(ExpectedConditions.elementToBeClickable(CONFIRM_DELETE_SUPPLIER_POPUP_DELETE_BTN)).click();

        // wait confirm popup invisible
        wait.until(ExpectedConditions.invisibilityOf(CONFIRM_DELETE_SUPPLIER_POPUP));

        // wait page loaded
        commonAction.sleepInMiliSecond(3000);

        // search supplier code
        supplierManagementPage.searchSupplierByCode(supplierCode);

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list result
        List<String> listResult = supplierManagementPage.getListSupplierCode();

        // check no result when search deleted supplier by code
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, listResult.size() == 0, "[Failed][Order history] Supplier is deleted but search supplier with keywords: %s, return list: %s".formatted(supplierCode, listResult));
        logger.info("Check Supplier management - Search deleted supplier by supplier code.");

        // search supplier by name
        supplierManagementPage.searchSupplierByName(supplierName);

        // wait result
        commonAction.sleepInMiliSecond(1000);

        // get list result
        listResult = supplierManagementPage.getListSupplierCode();

        // check no result when search deleted supplier by code
        uiCRUDSupplierPage.countFail = uiCRUDSupplierPage.assertCustomize.assertTrue(uiCRUDSupplierPage.countFail, listResult.size() == 0, "[Failed][Order history] Supplier is deleted but search supplier with keywords: %s, return list: %s".formatted(supplierName, listResult));
        logger.info("Check Supplier management - Search deleted supplier by supplier name.");

        verifyTest();
    }

    void verifyTest() {
        if (uiCRUDSupplierPage.countFail > 0)
            Assert.fail("[Failed] Fail %d cases".formatted(uiCRUDSupplierPage.countFail));
    }
}
