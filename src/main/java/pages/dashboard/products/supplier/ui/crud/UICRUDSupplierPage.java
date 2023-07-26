package pages.dashboard.products.supplier.ui.crud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;

public class UICRUDSupplierPage extends UICRUDSupplierElement {

    Logger logger = LogManager.getLogger(UICRUDSupplierPage.class);

    WebDriverWait wait;
    Actions act;

    public AssertCustomize assertCustomize;
    public int countFail = 0;

    public UICRUDSupplierPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        act = new Actions(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    void checkHeader(String language) throws Exception {
        // check Back to supplier management page
        String dbBackToSupplierManagement = wait.until(visibilityOf(UI_BACK_TO_SUPPLIER_MANAGEMENT_PAGE)).getText();
        String ppBackToSupplierManagement = getPropertiesValueByDBLang("products.supplier.addSupplier.header.backToSupplierManagementPage", language);
        countFail = assertCustomize.assertEquals(countFail, dbBackToSupplierManagement, ppBackToSupplierManagement, "[Failed][Header] Back to supplier management page should be %s, but found %s.".formatted(ppBackToSupplierManagement, dbBackToSupplierManagement));
        logger.info("[UI][%s] Check Header - Back to supplier management page.".formatted(language));

        // check page title
        String dbTitle = wait.until(visibilityOf(UI_PAGE_TITLE)).getText();
        String ppTitle = (driver.getCurrentUrl().contains("/edit")) ? getPropertiesValueByDBLang("products.supplier.updateSupplier.header.pageTitle", language) : getPropertiesValueByDBLang("products.supplier.addSupplier.header.pageTitle", language);
        countFail = assertCustomize.assertEquals(countFail, dbTitle, ppTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Header - Page title.".formatted(language));

        // check Save button
        String dbSaveBtn = wait.until(visibilityOf(UI_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.supplier.addSupplier.header.saveBtn", language);
        countFail = assertCustomize.assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = wait.until(ExpectedConditions.visibilityOf(UI_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.supplier.addSupplier.header.cancelBtn", language);
        countFail = assertCustomize.assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));

        // check delete button
        if (driver.getCurrentUrl().contains("/edit")) {
            String dbDeleteBtn = wait.until(ExpectedConditions.visibilityOf(UI_DELETE_BTN)).getText();
            String ppDeleteBtn = getPropertiesValueByDBLang("products.supplier.updateSupplier.header.deleteBtn", language);
            countFail = assertCustomize.assertEquals(countFail, dbDeleteBtn, ppDeleteBtn, "[Failed][Header] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
            logger.info("[UI][%s] Check Header - Delete button.".formatted(language));
        }
    }

    void checkSupplierInformation(String language) throws Exception {
        // check supplier name
        String dbSupplierName = wait.until(visibilityOf(UI_SUPPLIER_NAME)).getText();
        String ppSupplierName = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.supplierName", language);
        countFail = assertCustomize.assertEquals(countFail, dbSupplierName, ppSupplierName, "[Failed][Supplier information] Supplier name should be %s, but found %s.".formatted(ppSupplierName, dbSupplierName));
        logger.info("[UI][%s] Check Supplier information - Supplier name.".formatted(language));

        // check supplier code
        String dbSupplierCode = wait.until(visibilityOf(UI_SUPPLIER_CODE)).getText();
        String ppSupplierCode = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.supplierCode", language);
        countFail = assertCustomize.assertEquals(countFail, dbSupplierCode, ppSupplierCode, "[Failed][Supplier information] Supplier code should be %s, but found %s.".formatted(ppSupplierCode, dbSupplierCode));
        logger.info("[UI][%s] Check Supplier information - Supplier code.".formatted(language));

        // check supplier phone number
        String dbPhoneNumber = wait.until(visibilityOf(UI_PHONE_NUMBER)).getText();
        String ppPhoneNumber = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.phoneNumber", language);
        countFail = assertCustomize.assertEquals(countFail, dbPhoneNumber, ppPhoneNumber, "[Failed][Supplier information] Supplier phone number should be %s, but found %s.".formatted(ppPhoneNumber, dbPhoneNumber));
        logger.info("[UI][%s] Check Supplier information - Supplier phone number.".formatted(language));

        // check supplier number placeholder
        String dbPhoneNumberPlaceholder = wait.until(visibilityOf(UI_PHONE_NUMBER_PLACEHOLDER)).getAttribute("placeholder");
        String ppPhoneNumberPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.phoneNumberPlaceholder", language);
        countFail = assertCustomize.assertEquals(countFail, dbPhoneNumberPlaceholder, ppPhoneNumberPlaceholder, "[Failed][Supplier information] Supplier phone number placeholder should be %s, but found %s.".formatted(ppPhoneNumberPlaceholder, dbPhoneNumberPlaceholder));
        logger.info("[UI][%s] Check Supplier information - Supplier phone number placeholder.".formatted(language));


        // check supplier email
        String dbEmail = wait.until(visibilityOf(UI_EMAIL)).getText();
        String ppEmail = getPropertiesValueByDBLang("products.supplier.addSupplier.supplierInformation.email", language);
        countFail = assertCustomize.assertEquals(countFail, dbEmail, ppEmail, "[Failed][Supplier information] Supplier email should be %s, but found %s.".formatted(ppEmail, dbEmail));
        logger.info("[UI][%s] Check Supplier information - Supplier email.".formatted(language));
    }

    void checkAddressInformation(String language, boolean isVNSupplier) throws Exception {
        // check title
        String dbAddressInformation = wait.until(visibilityOf(UI_ADDRESS_INFORMATION)).getText();
        String ppAddressInformation = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.title", language);
        countFail = assertCustomize.assertEquals(countFail, dbAddressInformation, ppAddressInformation, "[Failed][Address Information] Title should be %s, but found %s.".formatted(ppAddressInformation, dbAddressInformation));
        logger.info("[UI][%s] Check Address Information - Title.".formatted(language));

        // check country
        String dbCountry = wait.until(visibilityOf(UI_COUNTRY)).getText();
        String ppCountry = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.country", language);
        countFail = assertCustomize.assertEquals(countFail, dbCountry, ppCountry, "[Failed][Address Information] Country should be %s, but found %s.".formatted(ppCountry, dbCountry));
        logger.info("[UI][%s] Check Address Information - Country.".formatted(language));

        if (isVNSupplier) {
            // check VN address
            String dbVNAddress = wait.until(visibilityOf(UI_VN_ADDRESS)).getText();
            String ppVNAddress = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.address", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNAddress, ppVNAddress, "[Failed][Address Information] VN address should be %s, but found %s.".formatted(ppVNAddress, dbVNAddress));
            logger.info("[UI][%s] Check Address Information - VN address.".formatted(language));

            // check VN address placeholder
            String dbVNAddressPlaceholder = wait.until(visibilityOf(UI_VN_ADDRESS_PLACEHOLDER)).getAttribute("placeholder");
            String ppVNAddressPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.addressPlaceholder", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNAddressPlaceholder, ppVNAddressPlaceholder, "[Failed][Address Information] VN address placeholder should be %s, but found %s.".formatted(ppVNAddressPlaceholder, dbVNAddressPlaceholder));
            logger.info("[UI][%s] Check Address Information - VN address placeholder.".formatted(language));

            // check VN city/province
            String dbVNCity = wait.until(visibilityOf(UI_VN_CITY)).getText();
            String ppVNCity = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.city/province", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNCity, ppVNCity, "[Failed][Address Information] VN city should be %s, but found %s.".formatted(ppVNCity, dbVNCity));
            logger.info("[UI][%s] Check Address Information - VN city.".formatted(language));

            // check VN city/province default option
            String dbVNCityDefaultOption = wait.until(visibilityOf(UI_VN_CITY_DEFAULT_OPTION)).getText();
            String ppVNCityDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.city/provinceDefaultOption", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNCityDefaultOption, ppVNCityDefaultOption, "[Failed][Address Information] VN city default option should be %s, but found %s.".formatted(ppVNCityDefaultOption, dbVNCityDefaultOption));
            logger.info("[UI][%s] Check Address Information - VN city default option.".formatted(language));

            // check VN district
            String dbVNDistrict = wait.until(visibilityOf(UI_VN_DISTRICT)).getText();
            String ppVNDistrict = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.district", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNDistrict, ppVNDistrict, "[Failed][Address Information] VN district should be %s, but found %s.".formatted(ppVNDistrict, dbVNDistrict));
            logger.info("[UI][%s] Check Address Information - VN district.".formatted(language));

            // check VN district default option
            String dbVNDistrictDefaultOption = wait.until(visibilityOf(UI_VN_DISTRICT_DEFAULT_OPTION)).getText();
            String ppVNDistrictDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.districtDefaultOption", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNDistrictDefaultOption, ppVNDistrictDefaultOption, "[Failed][Address Information] VN district default option should be %s, but found %s.".formatted(ppVNDistrictDefaultOption, dbVNDistrictDefaultOption));
            logger.info("[UI][%s] Check Address Information - VN district default option.".formatted(language));

            // check VN ward
            String dbVNWard = wait.until(visibilityOf(UI_VN_WARD)).getText();
            String ppVNWard = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.ward", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNWard, ppVNWard, "[Failed][Address Information] VN ward should be %s, but found %s.".formatted(ppVNWard, dbVNWard));
            logger.info("[UI][%s] Check Address Information - VN ward.".formatted(language));

            // check VN ward default option
            String dbVNWardDefaultOption = wait.until(visibilityOf(UI_WARD_DEFAULT_OPTION)).getText();
            String ppVNWardDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.vn.wardDefaultOption", language);
            countFail = assertCustomize.assertEquals(countFail, dbVNWardDefaultOption, ppVNWardDefaultOption, "[Failed][Address Information] VN ward default option should be %s, but found %s.".formatted(ppVNWardDefaultOption, dbVNWardDefaultOption));
            logger.info("[UI][%s] Check Address Information - VN ward default option.".formatted(language));
        } else {
            // check non-VN street address
            String dbNonVNStreetAddress = wait.until(visibilityOf(UI_NON_VN_STREET_ADDRESS)).getText();
            String ppNonVNStreetAddress = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.streetAddress", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNStreetAddress, ppNonVNStreetAddress, "[Failed][Address Information] Non-VN street address should be %s, but found %s.".formatted(ppNonVNStreetAddress, dbNonVNStreetAddress));
            logger.info("[UI][%s] Check Address Information - Non-VN street address.".formatted(language));

            // check non-VN street address placeholder
            String dbNonVNStreetAddressPlaceholder = wait.until(visibilityOf(UI_NON_VN_STREET_ADDRESS_PLACEHOLDER)).getAttribute("placeholder");
            String ppNonVNStreetAddressPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.streetAddressPlaceholder", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNStreetAddressPlaceholder, ppNonVNStreetAddressPlaceholder, "[Failed][Address Information] Non-VN street address placeholder should be %s, but found %s.".formatted(ppNonVNStreetAddressPlaceholder, dbNonVNStreetAddressPlaceholder));
            logger.info("[UI][%s] Check Address Information - Non-VN address placeholder.".formatted(language));

            // check non-VN address2
            String dbNonVNAddress2 = wait.until(visibilityOf(UI_NON_VN_ADDRESS2)).getText();
            String ppNonVNAddress2 = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.address2", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNAddress2, ppNonVNAddress2, "[Failed][Address Information] Non-VN address2 should be %s, but found %s.".formatted(ppNonVNAddress2, dbNonVNAddress2));
            logger.info("[UI][%s] Check Address Information - Non-VN address2.".formatted(language));

            // check non-VN address2 placeholder
            String dbNonVNAddress2Placeholder = wait.until(visibilityOf(UI_NON_VN_ADDRESS2_PLACEHOLDER)).getAttribute("placeholder");
            String ppNonVNAddress2Placeholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.address2Placeholder", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNAddress2Placeholder, ppNonVNAddress2Placeholder, "[Failed][Address Information] Non-VN address2 placeholder should be %s, but found %s.".formatted(ppNonVNAddress2Placeholder, dbNonVNAddress2Placeholder));
            logger.info("[UI][%s] Check Address Information - Non-VN address2 placeholder.".formatted(language));

            // check non-VN city
            String dbNonVNCity = wait.until(visibilityOf(UI_NON_VN_CITY)).getText();
            String ppNonVNCity = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.city", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNCity, ppNonVNCity, "[Failed][Address Information] Non-VN city should be %s, but found %s.".formatted(ppNonVNCity, dbNonVNCity));
            logger.info("[UI][%s] Check Address Information - Non-VN city.".formatted(language));

            // check non-VN city placeholder
            String dbNonVNCityPlaceholder = wait.until(visibilityOf(UI_NON_VN_CITY_PLACEHOLDER)).getAttribute("placeholder");
            String ppNonVNCityPlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.cityPlaceholder", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNCityPlaceholder, ppNonVNCityPlaceholder, "[Failed][Address Information] Non-VN city placeholder should be %s, but found %s.".formatted(ppNonVNCityPlaceholder, dbNonVNCityPlaceholder));
            logger.info("[UI][%s] Check Address Information - Non-VN city placeholder.".formatted(language));

            // check non-VN state/region/province
            String dbNonVNProvince = wait.until(visibilityOf(UI_NON_VN_STATE)).getText();
            String ppNonVNProvince = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.state/region/province", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNProvince, ppNonVNProvince, "[Failed][Address Information] Non-VN state/region/province should be %s, but found %s.".formatted(ppNonVNProvince, dbNonVNProvince));
            logger.info("[UI][%s] Check Address Information - Non-VN state/region/province.".formatted(language));

            // check non-VN state/region/province default option
            String dbNonVNProvinceDefaultOption = wait.until(visibilityOf(UI_NON_VN_STATE_DEFAULT_OPTION)).getText();
            String ppNonVNProvinceDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.state/region/provinceDefaultOption", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNProvinceDefaultOption, ppNonVNProvinceDefaultOption, "[Failed][Address Information] Non-VN state/region/province default option should be %s, but found %s.".formatted(ppNonVNProvinceDefaultOption, dbNonVNProvinceDefaultOption));
            logger.info("[UI][%s] Check Address Information - Non-VN state/region/province default option.".formatted(language));

            // check non-VN zipcode
            String dbNonVNZipcode = wait.until(visibilityOf(UI_NON_VN_ZIPCODE)).getText();
            String ppNonVNZipcode = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.zipcode", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNZipcode, ppNonVNZipcode, "[Failed][Address Information] Non-VN zipcode should be %s, but found %s.".formatted(ppNonVNZipcode, dbNonVNZipcode));
            logger.info("[UI][%s] Check Address Information - Non-VN zipcode.".formatted(language));

            // check non-VN zipcode placeholder
            String dbNonVNZipcodePlaceholder = wait.until(visibilityOf(UI_NON_VN_ZIPCODE_PLACEHOLDER)).getAttribute("placeholder");
            String ppNonVNZipcodePlaceholder = getPropertiesValueByDBLang("products.supplier.addSupplier.addressInformation.nonVN.zipcodePlaceholder", language);
            countFail = assertCustomize.assertEquals(countFail, dbNonVNZipcodePlaceholder, ppNonVNZipcodePlaceholder, "[Failed][Address Information] Non-VN zipcode placeholder should be %s, but found %s.".formatted(ppNonVNZipcodePlaceholder, dbNonVNZipcodePlaceholder));
            logger.info("[UI][%s] Check Address Information - Non-VN zipcode placeholder.".formatted(language));
        }
    }

    void checkOrderHistory(String language) throws Exception {
        // check title
        String dbTitle = wait.until(visibilityOf(UI_ORDER_HISTORY)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.title", language);
        countFail = assertCustomize.assertEquals(countFail, dbTitle, ppTitle, "[Failed][Order History] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Order History - Title.".formatted(language));

        // check search placeholder
        String dbSearchPlaceholder = wait.until(visibilityOf(UI_SEARCH_ORDER_HISTORY_PLACEHOLDER)).getAttribute("placeholder");
        String ppSearchPlaceholder = getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.searchPlaceholder", language);
        countFail = assertCustomize.assertEquals(countFail, dbSearchPlaceholder, ppSearchPlaceholder, "[Failed][Order History] Search placeholder should be %s, but found %s.".formatted(ppSearchPlaceholder, dbSearchPlaceholder));
        logger.info("[UI][%s] Check Order History - Search placeholder.".formatted(language));

        // check order history table column
        List<String> dbTableColumn = UI_ORDER_HISTORY_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppTableColumn = List.of(getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.0", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.1", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.2", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.3", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.4", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.5", language),
                getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.table.column.6", language));
        countFail = assertCustomize.assertEquals(countFail, dbTableColumn, ppTableColumn, "[Failed][Order History] Order history table should be %s, but found %s.".formatted(ppTableColumn, dbTableColumn));
        logger.info("[UI][%s] Check Order History - Order history table.".formatted(language));
    }

    public void checkNoOrderHistory(String language) throws Exception {
        // check no result
        String dbNoResult = wait.until(visibilityOf(UI_NO_ORDER_HISTORY)).getText();
        String ppNoResult = getPropertiesValueByDBLang("products.supplier.updateSupplier.orderHistory.noResult", language);
        countFail = assertCustomize.assertEquals(countFail, dbNoResult, ppNoResult, "[Failed][Order History] No result should be %s, but found %s.".formatted(ppNoResult, dbNoResult));
        logger.info("[UI][%s] Check Order History - No result.".formatted(language));
    }

    void checkSupplierSummary(String language) throws Exception {
        // check title
        String dbSupplierSummary = wait.until(visibilityOf(UI_SUPPLIER_SUMMARY)).getText();
        String ppSupplierSummary = getPropertiesValueByDBLang("products.supplier.updateSupplier.supplierSummary.title", language);
        countFail = assertCustomize.assertEquals(countFail, dbSupplierSummary, ppSupplierSummary, "[Failed][Supplier summary] Title should be %s, but found %s.".formatted(ppSupplierSummary, dbSupplierSummary));
        logger.info("[UI][%s] Check Supplier summary - Title.".formatted(language));

        // check status title
        String dbStatusTitle = wait.until(visibilityOf(UI_SUPPLIER_STATUS_TITLE)).getText();
        String ppStatusTitle = getPropertiesValueByDBLang("products.supplier.updateSupplier.supplierSummary.status.title", language);
        countFail = assertCustomize.assertEquals(countFail, dbStatusTitle, ppStatusTitle, "[Failed][Supplier summary] Supplier status title should be %s, but found %s.".formatted(ppStatusTitle, dbStatusTitle));
        logger.info("[UI][%s] Check Supplier summary - Supplier status title.".formatted(language));

        // check status label
        String dbStatusLabel = wait.until(visibilityOf(UI_SUPPLIER_STATUS_LABEL)).getText();
        String ppStatusLabel = getPropertiesValueByDBLang("products.supplier.updateSupplier.supplierSummary.status.label", language);
        countFail = assertCustomize.assertEquals(countFail, dbStatusLabel, ppStatusLabel, "[Failed][Supplier summary] Supplier status label should be %s, but found %s.".formatted(ppStatusLabel, dbStatusLabel));
        logger.info("[UI][%s] Check Supplier summary - Supplier status label.".formatted(language));
    }

    void checkOtherInformation(String language) throws Exception {
        // check title
        String dbOtherInformation = wait.until(visibilityOf(UI_OTHER_INFORMATION)).getText();
        String ppOtherInformation = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.title", language);
        countFail = assertCustomize.assertEquals(countFail, dbOtherInformation, ppOtherInformation, "[Failed][Other information] Title should be %s, but found %s.".formatted(ppOtherInformation, dbOtherInformation));
        logger.info("[UI][%s] Check Other information - Title.".formatted(language));

        // check responsible staff
        String dbResponsibleStaff = wait.until(visibilityOf(UI_RESPONSIBLE_STAFF)).getText();
        String ppResponsibleStaff = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.responsibleStaff", language);
        countFail = assertCustomize.assertEquals(countFail, dbResponsibleStaff, ppResponsibleStaff, "[Failed][Other information] Responsible staff should be %s, but found %s.".formatted(ppResponsibleStaff, dbResponsibleStaff));
        logger.info("[UI][%s] Check Other information - Responsible staff.".formatted(language));

        // check responsible staff default option
        String dbResponsibleStaffDefaultOption = wait.until(visibilityOf(UI_RESPONSIBLE_STAFF_DEFAULT_OPTION)).getText();
        String ppResponsibleStaffDefaultOption = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.responsibleStaffDefaultOption", language);
        countFail = assertCustomize.assertEquals(countFail, dbResponsibleStaffDefaultOption, ppResponsibleStaffDefaultOption, "[Failed][Other information] Responsible staff default option should be %s, but found %s.".formatted(ppResponsibleStaffDefaultOption, dbResponsibleStaffDefaultOption));
        logger.info("[UI][%s] Check Other information - Responsible staff default option.".formatted(language));

        // check description
        String dbDescription = wait.until(visibilityOf(UI_DESCRIPTION)).getText();
        String ppDescription = getPropertiesValueByDBLang("products.supplier.addSupplier.otherInformation.description", language);
        countFail = assertCustomize.assertEquals(countFail, dbDescription, ppDescription, "[Failed][Other information] Description should be %s, but found %s.".formatted(ppDescription, dbDescription));
        logger.info("[UI][%s] Check Other information - Description.".formatted(language));
    }

    public void checkConfirmDeleteSupplierPopup(String language) throws Exception {
        // check title
        String dbTitle = wait.until(visibilityOf(UI_CONFIRM_DELETE_SUPPLIER_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.title", language);
        countFail = assertCustomize.assertEquals(countFail, dbTitle, ppTitle, "[Failed][Delete supplier confirm popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Title.".formatted(language));

        // check content
        String dbContent = wait.until(visibilityOf(UI_CONFIRM_DELETE_SUPPLIER_POPUP_CONTENT)).getText();
        String ppContent = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.content", language);
        countFail = assertCustomize.assertEquals(countFail, dbContent, ppContent, "[Failed][Delete supplier confirm popup] Content should be %s, but found %s.".formatted(ppContent, dbContent));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Content.".formatted(language));

        // check Delete button
        String dbDeleteBtn = wait.until(visibilityOf(UI_CONFIRM_DELETE_SUPPLIER_POPUP_DELETE_BTN)).getText();
        String ppDeleteBtn = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.deleteBtn", language);
        countFail = assertCustomize.assertEquals(countFail, dbDeleteBtn, ppDeleteBtn, "[Failed][Delete supplier confirm popup] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Delete button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = wait.until(visibilityOf(UI_CONFIRM_DELETE_SUPPLIER_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.supplier.updateSupplier.confirmDeleteSupplierPopup.cancelBtn", language);
        countFail = assertCustomize.assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Delete supplier confirm popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Delete supplier confirm popup - Cancel button.".formatted(language));
    }
    public void checkErrorWhenLeaveRequiredFieldBlank(String language) throws Exception {
        String dbSupplierBlank = wait.until(visibilityOf(UI_SUPPLIER_NAME_ERROR)).getText();
        String ppSupplierBlank = getPropertiesValueByDBLang("products.supplier.addSupplier.error.supplierName.blank", language);
        countFail = assertCustomize.assertEquals(countFail, dbSupplierBlank, ppSupplierBlank, "[Failed][Validate] Error when leave supplier name blank should be %s, but found %s.".formatted(ppSupplierBlank, dbSupplierBlank));
        logger.info("[UI][%s] Check Validate - Leave supplier name blank.".formatted(language));
    }

    public void checkErrorWhenInputDuplicateSupplierCode(String language) throws Exception {
        String dbSupplierCodeDuplicate = wait.until(visibilityOf(UI_SUPPLIER_CODE_ERROR)).getText();
        String ppSupplierCodeDuplicate = getPropertiesValueByDBLang("products.supplier.addSupplier.error.supplierCode.duplicate", language);
        countFail = assertCustomize.assertEquals(countFail, dbSupplierCodeDuplicate, ppSupplierCodeDuplicate, "[Failed][Validate] Error when input duplicate supplier code should be %s, but found %s.".formatted(ppSupplierCodeDuplicate, dbSupplierCodeDuplicate));
        logger.info("[UI][%s] Check Validate - Input duplicate supplier code.".formatted(language));
    }

    public void checkErrorWhenInputInvalidFormatSupplierCode(String language) throws Exception {
        String dbInvalidSupplierCode = wait.until(visibilityOf(UI_SUPPLIER_CODE_ERROR)).getText();
        String ppInvalidSupplierCode = getPropertiesValueByDBLang("products.supplier.addSupplier.error.supplierCode.invalidFormat", language);
        countFail = assertCustomize.assertEquals(countFail, dbInvalidSupplierCode, ppInvalidSupplierCode, "[Failed][Validate] Error when input invalid format supplier code should be %s, but found %s.".formatted(ppInvalidSupplierCode, dbInvalidSupplierCode));
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
}
