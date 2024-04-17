package app.GoSeller.supplier.update;

import api.Seller.supplier.supplier.APISupplier.SupplierInformation;
import app.GoSeller.supplier.management.SupplierManagementScreen;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class UpdateSupplierScreen extends UpdateSupplierElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonMobile commonMobile;
    @Getter
    private SupplierInformation supInfo;
    private final static Logger logger = LogManager.getLogger();

    public UpdateSupplierScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonMobile = new UICommonMobile(driver);
    }

    void inputSupplierName(String name) {
        // input supplier name
        commonMobile.moveAndGetElement(SUPPLIER_NAME).clear();
        commonMobile.inputText(SUPPLIER_NAME, name);

        // set info
        supInfo.setName(name);

        // log
        logger.info("[Update supplier] Input supplier name: %s".formatted(name));
    }

    void inputSupplierCode(String code) {
        // input supplier code
        commonMobile.moveAndGetElement(SUPPLIER_CODE).clear();
        commonMobile.inputText(SUPPLIER_CODE, code);

        // set info
        supInfo.setCode(code);

        // log
        logger.info("[Update supplier] Input supplier code: %s".formatted(code));

    }

    void inputPhoneNumber(String phone) {
        // input phone number
        commonMobile.moveAndGetElement(SUPPLIER_PHONE).clear();
        commonMobile.inputText(SUPPLIER_PHONE, phone);

        // set info
        supInfo.setPhoneNumber(phone);

        // log
        logger.info("[Update supplier] Input phone number: %s".formatted(phone));
    }

    void inputEmail(String email) {
        // input email
        commonMobile.moveAndGetElement(SUPPLIER_EMAIL).clear();
        commonMobile.inputText(SUPPLIER_EMAIL, email);

        // set info
        supInfo.setEmail(email);

        // log
        logger.info("[Update supplier] Input email: %s".formatted(email));
    }

    void selectCountry(boolean isVNSupplier) {
        // get country
        String countryKeywords = "Vietnam";
        if (!isVNSupplier) do countryKeywords = new DataGenerator().randomCountry();
        while (countryKeywords.equals("Vietnam"));

        // re-initialize supplier information
        if (supInfo.getName() != null) supInfo = new SupplierInformation();

        // set info
        supInfo.setVNSupplier(isVNSupplier);
        supInfo.setCountryCode(countryKeywords);
        supInfo.setPhoneCode(new DataGenerator().getPhoneCode(countryKeywords));

        // search and select country
        if (!commonMobile.getText(SELECTED_COUNTRY).equals(countryKeywords)) {
            // open country dropdown
            commonMobile.moveAndGetElement(SELECTED_COUNTRY).click();

            // search country
            commonMobile.click(COUNTRY_SEARCH_ICON);
            commonMobile.getElement(COUNTRY_SEARCH_BOX).sendKeys(countryKeywords);
            logger.info("[Update supplier] Search country with keyword: %s".formatted(countryKeywords));

            // select country
            commonMobile.click(commonMobile.getElements(COUNTRY_LIST, 10).get(0));
        }

        // log
        logger.info("[Update supplier] Select country: %s".formatted(supInfo.getCountryCode()));
    }

    void inputVNAddress(String address) {
        // input address
        commonMobile.moveAndGetElement(VN_ADDRESS).clear();
        commonMobile.inputText(VN_ADDRESS, address);

        // set info
        supInfo.setAddress(address);

        // log
        logger.info("[Update supplier] Input address: %s".formatted(address));
    }

    /* VN address */
    void selectVNCity() {
        // open city dropdown
        commonMobile.moveAndGetElement(SELECTED_VN_CITY).click();

        // get index of selected city
        List<WebElement> cityList = commonMobile.getElements(VN_CITY_LIST, 10);
        int index;
        do {
            index = nextInt(cityList.size());
        } while (index == 0);


        // set info
        supInfo.setCityName(cityList.get(index).getText());

        // select city
        commonMobile.click(cityList.get(index));
        try {
            driver.findElement(VN_CITY_DROPDOWN_CLOSE_ICON).click();
        } catch (NoSuchElementException ignore) {
        }

        // log
        logger.info("[Update supplier] Select city: %s".formatted(supInfo.getCityName()));
    }

    void selectVNDistrict() {
        // open district dropdown
        commonMobile.moveAndGetElement(SELECTED_VN_DISTRICT).click();

        // get index of selected district
        List<WebElement> districtList = commonMobile.getElements(VN_DISTRICT_LIST, 10);
        int index = nextInt(districtList.size());

        // set info
        supInfo.setDistrict(districtList.get(index).getText());

        // select district
        commonMobile.click(districtList.get(index));
        try {
            driver.findElement(VN_DISTRICT_DROPDOWN_CLOSE_ICON).click();
        } catch (NoSuchElementException ignore) {
        }

        // log
        logger.info("[Update supplier] Select district: %s".formatted(supInfo.getDistrict()));
    }

    void selectVNWard() {
        // open ward dropdown
        commonMobile.moveAndGetElement(SELECTED_VN_WARD).click();

        // get index of selected ward
        List<WebElement> wardList = commonMobile.getElements(VN_WARD_LIST, 10);
        int index = nextInt(wardList.size());

        // set info
        supInfo.setWard(wardList.get(index).getText());

        // select ward
        commonMobile.click(wardList.get(index));
        try {
            driver.findElement(VN_WARD_DROPDOWN_CLOSE_ICON).click();
        } catch (NoSuchElementException ignore) {
        }

        // log
        logger.info("[Update supplier] Select ward: %s".formatted(supInfo.getWard()));
    }

    /* Outside VN address */
    void inputOutsideVNStreetAddress(String address) {
        // input address
        commonMobile.moveAndGetElement(OUTSIDE_VN_STREET_ADDRESS).clear();
        commonMobile.inputText(OUTSIDE_VN_STREET_ADDRESS, address);

        // set info
        supInfo.setStreetAddress(address);

        // log
        logger.info("[Update supplier] Input street address: %s".formatted(address));
    }

    void inputOutsideVNAddress2(String address2) {
        // input address 2 for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_ADDRESS2).clear();
        commonMobile.inputText(OUTSIDE_VN_ADDRESS2, address2);

        // set info
        supInfo.setAddress2(address2);

        // log
        logger.info("[Update supplier] Input address2: %s".formatted(address2));
    }

    void selectOutsideVNState() {
        // open province dropdown
        commonMobile.moveAndGetElement(SELECTED_OUTSIDE_VN_STATE).click();

        // get index of selected province
        List<WebElement> stateList = commonMobile.getElements(OUTSIDE_VN_STATE_LIST, 10);
        int index = nextInt(stateList.size());

        // set info
        supInfo.setProvince(stateList.get(index).getText());

        // select state
        commonMobile.click(stateList.get(index));
        try {
            driver.findElement(OUTSIDE_VN_STATE_DROPDOWN_CLOSE_ICON).click();
        } catch (NoSuchElementException ignore) {
        }

        // log
        logger.info("[Update supplier] Select state: %s".formatted(supInfo.getProvince()));
    }

    void inputOutsideVNCity(String city) {
        // input city for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_CITY).clear();
        commonMobile.inputText(OUTSIDE_VN_CITY, city);

        // set info
        supInfo.setCityName(city);

        // log
        logger.info("[Update supplier] Input city: %s".formatted(city));
    }

    void inputOutsideVNZipcode(String zipcode) {
        // input zipcode for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_ZIPCODE).clear();
        commonMobile.inputText(OUTSIDE_VN_ZIPCODE, zipcode);

        // set info
        supInfo.setZipcode(zipcode);

        // log
        logger.info("[Update supplier] Input zipcode: %s".formatted(zipcode));
    }

    void selectResponsibleStaff() {
        // open responsible staff dropdown
        commonMobile.moveAndGetElement(SELECTED_RESPONSIBLE_STAFF).click();

        // get index of selected staff
        List<WebElement> staffList = commonMobile.getElements(RESPONSIBLE_STAFF_LIST, 5);
        int index;
        do {
            index = nextInt(staffList.size());
        } while (index == 0);

        // set info
        supInfo.setResponsibleStaff(staffList.get(index).getText());

        // select staff
        commonMobile.click(staffList.get(index));

        // log
        logger.info("[Update supplier] Select responsible staff: %s".formatted(supInfo.getResponsibleStaff()));
    }

    void inputDescription(String description) {
        // input description
        commonMobile.moveAndGetElement(DESCRIPTION).clear();
        commonMobile.inputText(DESCRIPTION, description);

        // set info
        supInfo.setDescription(description);

        // log
        logger.info("[Update supplier] Input description: %s".formatted(description));
    }

    void completeUpdateSupplier() {
        // click save button
        commonMobile.moveAndGetElement(SAVE_BTN).click();

        // log
        logger.info("[Update supplier] Complete update supplier");
    }

    public void updateNewSupplier(boolean isVNSupplier) {
        // select country
        selectCountry(isVNSupplier);

        // generate data
        String epoch = String.valueOf(Instant.now().toEpochMilli());

        // input supplier name
        String supplierName = Pattern.compile("([\\w\\W]{0,100})").matcher("Auto - Supplier %s - %s".formatted(isVNSupplier ? "VN" : "Non VN", epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputSupplierName(supplierName);

        // input supplier code
        String supplierCode = Pattern.compile("(\\w{0,12})").matcher("%s".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputSupplierCode(supplierCode);

        // input supplier phone number
        String phoneNumber = Pattern.compile("(\\d{8,13})").matcher(epoch).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputPhoneNumber(phoneNumber);

        // input supplier email
        String email = Pattern.compile("([\\w\\W]{0,100})").matcher("%s@qa.team".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        inputEmail(email);

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
            inputOutsideVNStreetAddress("Street address %s".formatted(epoch));

            // input address2
            inputOutsideVNAddress2("Address2 %s".formatted(epoch));

            // select province
            selectOutsideVNState();

            // input city
            inputOutsideVNCity("City %s".formatted(epoch));

            // input zipcode
            inputOutsideVNZipcode("Zipcode %s".formatted(epoch));
        }

        // select responsible staff
        selectResponsibleStaff();

        // input description
        inputDescription("Descriptions %s".formatted(epoch));

        // click Save button to complete create supplier
        completeUpdateSupplier();
    }

    public void checkAllSupplierInformation(SupplierInformation supInfo) {
        // init supplier information
        this.supInfo = supInfo;

        // navigate to create supplier page
        new SupplierManagementScreen(driver).openSupplierDetailScreen(supInfo.getCode());

        // check supplier name
        String supName = commonMobile.moveAndGetElement(SUPPLIER_NAME).getText();
        assertCustomize.assertEquals(supName, supInfo.getName(), ("[Failed][Supplier detail screen] Supplier name " +
                "should be %s, but found %s.").formatted(supInfo.getName(), supName));
        logger.info("[Supplier detail] Check supplier name.");

        // check supplier code
        String supCode = commonMobile.moveAndGetElement(SUPPLIER_CODE).getText();
        assertCustomize.assertEquals(supCode, supInfo.getCode(), ("[Failed][Supplier detail screen] Supplier code should be %s, but found %s.").formatted(supInfo.getCode(), supCode));
        logger.info("[Supplier detail] Check supplier code.");

        // check supplier phone code
        String supPhoneCode = commonMobile.moveAndGetElement(SUPPLIER_PHONE_CODE).getText().replaceAll("[()]", "");
        assertCustomize.assertEquals(supPhoneCode, supInfo.getPhoneCode(), "[Failed][Supplier detail screen] Supplier phone code should be %s, but found %s.".formatted(supInfo.getPhoneCode(), supPhoneCode));
        logger.info("[Supplier detail] Check supplier phone code.");

        // check supplier phone number
        String supPhoneNumber = commonMobile.moveAndGetElement(SUPPLIER_PHONE).getText();
        assertCustomize.assertEquals(supPhoneNumber, supInfo.getPhoneNumber(), "[Failed][Supplier detail screen] Supplier phone number should be %s, but found %s.".formatted(supInfo.getPhoneNumber(), supPhoneNumber));
        logger.info("[Supplier detail] Check supplier phone number.");

        // check supplier mail
        String supEmail = commonMobile.moveAndGetElement(SUPPLIER_EMAIL).getText();
        assertCustomize.assertEquals(supEmail, supInfo.getEmail(), "[Failed][Supplier detail screen] Supplier email should be %s, but found %s.".formatted(supInfo.getEmail(), supEmail));
        logger.info("[Supplier detail] Check Supplier email.");

        // check country
        String country = commonMobile.moveAndGetElement(SELECTED_COUNTRY).getText();
        assertCustomize.assertEquals(country, supInfo.getCountryCode(), "[Failed][Supplier detail screen] Country should be %s, but found %s.".formatted(supInfo.getCountryCode(), country));
        logger.info("[Supplier detail] Check Country.");

        // check address information
        if (supInfo.isVNSupplier()) {
            // check VN address
            String vnAddress = commonMobile.moveAndGetElement(VN_ADDRESS).getText();
            assertCustomize.assertEquals(vnAddress, supInfo.getAddress(), "[Failed][Supplier detail screen] VN address should be %s, but found %s.".formatted(supInfo.getAddress(), vnAddress));
            logger.info("[Supplier detail] Check VN address.");

            // check VN city
            String vnCity = commonMobile.moveAndGetElement(SELECTED_VN_CITY).getText();
            assertCustomize.assertEquals(vnCity, supInfo.getCityName(), "[Failed][Supplier detail screen] VN city should be %s, but found %s.".formatted(supInfo.getCityName(), vnCity));
            logger.info("[Supplier detail] Check VN city.");

            // check VN district
            String vnDistrict = commonMobile.moveAndGetElement(SELECTED_VN_DISTRICT).getText();
            assertCustomize.assertEquals(vnDistrict, supInfo.getDistrict(), "[Failed][Supplier detail screen] VN district should be %s, but found %s.".formatted(supInfo.getDistrict(), vnDistrict));
            logger.info("[Supplier detail] Check VN district.");

            // check VN ward
            String vnWard = commonMobile.moveAndGetElement(SELECTED_VN_WARD).getText();
            assertCustomize.assertEquals(vnWard, supInfo.getWard(), "[Failed][Supplier detail screen] VN ward should be %s, but found %s.".formatted(supInfo.getWard(), vnWard));
            logger.info("[Supplier detail] Check VN ward.");
        } else {
            // check outside VN street address
            String streetAddress = commonMobile.moveAndGetElement(OUTSIDE_VN_STREET_ADDRESS).getText();
            assertCustomize.assertEquals(streetAddress, supInfo.getStreetAddress(), "[Failed][Supplier detail screen] Outside VN street address should be %s, but found %s.".formatted(supInfo.getStreetAddress(), streetAddress));
            logger.info("[Supplier detail] Check outside VN street address.");

            // check outside VN address2
            String address2 = commonMobile.moveAndGetElement(OUTSIDE_VN_ADDRESS2).getText();
            assertCustomize.assertEquals(address2, supInfo.getAddress2(), "[Failed][Supplier detail screen] Outside VN address2 should be %s, but found %s.".formatted(supInfo.getAddress2(), address2));
            logger.info("[Supplier detail] Check outside VN address2.");

            // check outside VN state
            String state = commonMobile.moveAndGetElement(SELECTED_OUTSIDE_VN_STATE).getText();
            assertCustomize.assertEquals(state, supInfo.getProvince(), "[Failed][Supplier detail screen] Outside VN state should be %s, but found %s.".formatted(supInfo.getProvince(), state));
            logger.info("[Supplier detail] Check outside VN state.");

            // check outside VN city
            String city = commonMobile.moveAndGetElement(OUTSIDE_VN_CITY).getText();
            assertCustomize.assertEquals(city, supInfo.getCityName(), "[Failed][Supplier detail screen] Outside VN city should be %s, but found %s.".formatted(supInfo.getCityName(), city));
            logger.info("[Supplier detail] Check outside VN city.");

            // check outside VN zipcode
            String zipcode = commonMobile.moveAndGetElement(OUTSIDE_VN_ZIPCODE).getText();
            assertCustomize.assertEquals(zipcode, supInfo.getZipcode(), "[Failed][Supplier detail screen] Outside VN zipcode should be %s, but found %s.".formatted(supInfo.getZipcode(), zipcode));
            logger.info("[Supplier detail] Check outside VN zipcode.");
        }

        // check responsible staff
        String responsibleStaff = commonMobile.moveAndGetElement(SELECTED_RESPONSIBLE_STAFF).getText();
        assertCustomize.assertEquals(responsibleStaff, supInfo.getResponsibleStaff(), "[Failed][Supplier detail screen] Responsible staff should be %s, but found %s.".formatted(supInfo.getResponsibleStaff(), responsibleStaff));
        logger.info("[Supplier detail] Check responsible staff.");

        // check description
        String description = commonMobile.moveAndGetElement(DESCRIPTION).getText();
        assertCustomize.assertEquals(description, supInfo.getDescription(), "[Failed][Supplier detail screen] should be %s, but found %s.".formatted(supInfo.getDescription(), description));
        logger.info("[Supplier detail] Check description.");

        // verify test
        AssertCustomize.verifyTest();
    }

    public void deleteSupplier() {
        // click Delete button
        commonMobile.getElement(DELETE_BTN, 10).click();

        // confirm delete supplier
        commonMobile.getElement(CONFIRM_POPUP_OK_BTN, 10).click();
    }
}
