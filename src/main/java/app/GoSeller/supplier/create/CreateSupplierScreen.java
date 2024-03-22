package app.GoSeller.supplier.create;

import app.GoSeller.supplier.management.SupplierManagementScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

import static api.Seller.supplier.supplier.APISupplier.SupplierInformation;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class CreateSupplierScreen extends CreateSupplierElement {
    WebDriver driver;
    UICommonMobile commonMobile;
    private final Logger logger = LogManager.getLogger(CreateSupplierScreen.class);
    private SupplierInformation supInfo = new SupplierInformation();

    public CreateSupplierScreen(WebDriver driver) {
        this.driver = driver;
        commonMobile = new UICommonMobile(driver);
    }

    void inputSupplierName(String name) {
        // input supplier name
        commonMobile.moveAndGetElement(SUPPLIER_NAME).clear();
        commonMobile.inputText(SUPPLIER_NAME, name);

        // set info
        supInfo.setName(name);

        // log
        logger.info("[Create supplier] Input supplier name: %s".formatted(name));
    }

    void inputSupplierCode(String code) {
        // input supplier code
        commonMobile.moveAndGetElement(SUPPLIER_CODE).clear();
        commonMobile.inputText(SUPPLIER_CODE, code);

        // set info
        supInfo.setCode(code);

        // log
        logger.info("[Create supplier] Input supplier code: %s".formatted(code));

    }

    void inputPhoneNumber(String phone) {
        // input phone number
        commonMobile.moveAndGetElement(SUPPLIER_PHONE).clear();
        commonMobile.inputText(SUPPLIER_PHONE, phone);

        // set info
        supInfo.setPhoneNumber(phone);

        // log
        logger.info("[Create supplier] Input phone number: %s".formatted(phone));
    }

    void inputEmail(String email) {
        // input email
        commonMobile.moveAndGetElement(SUPPLIER_EMAIL).clear();
        commonMobile.inputText(SUPPLIER_EMAIL, email);

        // set info
        supInfo.setEmail(email);

        // log
        logger.info("[Create supplier] Input email: %s".formatted(email));
    }

    void selectCountry(boolean isVNSupplier) {
        // get country
        String countryKeywords = "Vietnam";
        if (!isVNSupplier) do countryKeywords = new DataGenerator().randomCountry();
        while (countryKeywords.equals("Vietnam"));

        // re-initialize supplier information
        if (supInfo.getCountryCode() != null) supInfo = new SupplierInformation();

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
            logger.info("[Create supplier] Search country with keyword: %s".formatted(countryKeywords));

            // select country
            commonMobile.click(commonMobile.getElements(COUNTRY_LIST, 10).get(0));
        }

        // log
        logger.info("[Create supplier] Select country: %s".formatted(supInfo.getCountryCode()));
    }

    void inputVNAddress(String address) {
        // input address
        commonMobile.moveAndGetElement(VN_ADDRESS).clear();
        commonMobile.inputText(VN_ADDRESS, address);

        // set info
        supInfo.setAddress(address);

        // log
        logger.info("[Create supplier] Input address: %s".formatted(address));
    }

    /* VN address */
    void selectVNCity() {
        // open city dropdown
        commonMobile.getElement(SELECTED_VN_CITY).click();

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
        } catch (NoSuchElementException ignored) {}

        // log
        logger.info("[Create supplier] Select city: %s".formatted(supInfo.getCityName()));
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
        } catch (NoSuchElementException ignore) {}

        // log
        logger.info("[Create supplier] Select district: %s".formatted(supInfo.getDistrict()));
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
        } catch (NoSuchElementException ignore) {}

        // log
        logger.info("[Create supplier] Select ward: %s".formatted(supInfo.getWard()));
    }

    /* Outside VN address */
    void inputOutsideVNStreetAddress(String address) {
        // input address
        commonMobile.moveAndGetElement(OUTSIDE_VN_STREET_ADDRESS).clear();
        commonMobile.inputText(OUTSIDE_VN_STREET_ADDRESS, address);

        // set info
        supInfo.setStreetAddress(address);

        // log
        logger.info("[Create supplier] Input street address: %s".formatted(address));
    }

    void inputOutsideVNAddress2(String address2) {
        // input address 2 for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_ADDRESS2).clear();
        commonMobile.inputText(OUTSIDE_VN_ADDRESS2, address2);

        // set info
        supInfo.setAddress2(address2);

        // log
        logger.info("[Create supplier] Input address2: %s".formatted(address2));
    }

    void selectOutsideVNState() {
        // open province dropdown
        commonMobile.click(SELECTED_OUTSIDE_VN_STATE);

        // get index of selected province
        List<WebElement> stateList = commonMobile.getElements(OUTSIDE_VN_STATE_LIST, 10);
        int index = nextInt(stateList.size());

        // set info
        supInfo.setProvince(stateList.get(index).getText());

        // select state
        commonMobile.click(stateList.get(index));
        try {
            driver.findElement(OUTSIDE_VN_STATE_DROPDOWN_CLOSE_ICON).click();
        } catch (NoSuchElementException ignore) {}

        // log
        logger.info("[Create supplier] Select state: %s".formatted(supInfo.getProvince()));
    }

    void inputOutsideVNCity(String city) {
        // input city for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_CITY).clear();
        commonMobile.inputText(OUTSIDE_VN_CITY, city);

        // set info
        supInfo.setCityName(city);

        // log
        logger.info("[Create supplier] Input city: %s".formatted(city));
    }

    void inputOutsideVNZipcode(String zipcode) {
        // input zipcode for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_ZIPCODE).clear();
        commonMobile.inputText(OUTSIDE_VN_ZIPCODE, zipcode);

        // set info
        supInfo.setZipcode(zipcode);

        // log
        logger.info("[Create supplier] Input zipcode: %s".formatted(zipcode));
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
        logger.info("[Create supplier] Select responsible staff: %s".formatted(supInfo.getResponsibleStaff()));
    }

    void inputDescription(String description) {
        // input description
        commonMobile.moveAndGetElement(DESCRIPTION).clear();
        commonMobile.inputText(DESCRIPTION, description);

        // set info
        supInfo.setDescription(description);

        // log
        logger.info("[Create supplier] Input description: %s".formatted(description));
    }

    void completeCreateSupplier() {
        // click save button
        commonMobile.moveAndGetElement(SAVE_BTN).click();

        // log
        logger.info("[Create supplier] Complete create supplier");
    }

    public void createNewSupplier(boolean isVNSupplier) {
        // navigate to create supplier page
        new SupplierManagementScreen(driver).openCreateSupplierScreen();

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
        completeCreateSupplier();
    }

    public SupplierInformation getSupplierInfo() {
        // return supplier information
        return supInfo;
    }

}
