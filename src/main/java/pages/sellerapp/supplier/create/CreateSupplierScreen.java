package pages.sellerapp.supplier.create;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.sellerapp.supplier.management.SupplierManagementScreen;
import utilities.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.model.sellerApp.supplier.SupplierInformation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class CreateSupplierScreen extends CreateSupplierElement {
    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonMobile;
    private final Logger logger = LogManager.getLogger(CreateSupplierScreen.class);

    public CreateSupplierScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonMobile = new UICommonMobile(driver);
    }

    SupplierInformation supInfo = new SupplierInformation();

    void inputSupplierName(String name) {
        // input supplier name
        commonMobile.moveAndGetElement(SUPPLIER_NAME).clear();
        commonMobile.inputText(SUPPLIER_NAME, name);

        // set info
        supInfo.setSupplierName(name);

        // log
        logger.info("Input supplier name: %s".formatted(name));
    }

    void inputSupplierCode(String code) {
        // input supplier code
        commonMobile.moveAndGetElement(SUPPLIER_CODE).clear();
        commonMobile.inputText(SUPPLIER_CODE, code);

        // set info
        supInfo.setSupplierCode(code);

        // log
        logger.info("Input supplier code: %s".formatted(code));

    }

    void inputPhoneNumber(String phone) {
        // input phone number
        commonMobile.moveAndGetElement(SUPPLIER_PHONE).clear();
        commonMobile.inputText(SUPPLIER_PHONE, phone);

        // set info
        supInfo.setSupplierPhone(phone);

        // log
        logger.info("Input phone number: %s".formatted(phone));
    }

    void inputEmail(String email) {
        // input email
        commonMobile.moveAndGetElement(SUPPLIER_EMAIL).clear();
        commonMobile.inputText(SUPPLIER_EMAIL, email);

        // set info
        supInfo.setSupplierEmail(email);

        // log
        logger.info("Input email: %s".formatted(email));
    }

    void selectCountry(boolean isVNSupplier) {
        // get country
        String countryKeywords = "Vietnam";
        if (!isVNSupplier) do countryKeywords = new DataGenerator().randomCountry();
        while (countryKeywords.equals("Vietnam"));

        // set info
        supInfo.setCountry(countryKeywords);

        // search and select country
        if (!commonMobile.getText(SELECTED_COUNTRY).equals(countryKeywords)) {
            // open country dropdown
            commonMobile.moveAndGetElement(SELECTED_COUNTRY).click();

            // search country
            wait.until(ExpectedConditions.elementToBeClickable(COUNTRY_SEARCH_ICON)).click();
            commonMobile.getElement(COUNTRY_SEARCH_BOX).sendKeys(countryKeywords);
            logger.info("Search country with keyword: %s".formatted(countryKeywords));

            // select country
            commonMobile.getElements(COUNTRY_LIST).get(0).click();
        }

        // log
        logger.info("Select country: %s".formatted(supInfo.getCountry()));
    }

    void inputVNAddress(String address) {
        // input address
        commonMobile.moveAndGetElement(VN_ADDRESS).clear();
        commonMobile.inputText(VN_ADDRESS, address);

        // log
        logger.info("Input address: %s".formatted(address));
    }

    /* VN address */
    void selectVNCity() {
        // open city dropdown
        String currentVNCity = commonMobile.moveAndGetElement(SELECTED_VN_CITY).getText();
        commonMobile.getElement(SELECTED_VN_CITY).click();

        // get index of selected city
        List<WebElement> cityList = commonMobile.getElements(VN_CITY_LIST, 10);
        int index = nextInt(cityList.size());

        // set info
        supInfo.setVnCity(cityList.get(index).getText());

        // select city
        if (currentVNCity.equals(supInfo.getVnCity())) commonMobile.getElement(VN_CITY_DROPDOWN_CLOSE_ICON).click();
        else  cityList.get(index).click();

        // log
        logger.info("Select city: %s".formatted(supInfo.getVnCity()));
    }

    void selectVNDistrict() {
        // open district dropdown
        String currentVNDistrict = commonMobile.moveAndGetElement(SELECTED_VN_DISTRICT).getText();
        commonMobile.getElement(SELECTED_VN_DISTRICT).click();

        // get index of selected district
        List<WebElement> districtList = commonMobile.getElements(VN_DISTRICT_LIST, 10);
        int index = nextInt(districtList.size());

        // set info
        supInfo.setVnDistrict(districtList.get(index).getText());

        // select district
        if (currentVNDistrict.equals(supInfo.getVnDistrict())) commonMobile.getElement(VN_DISTRICT_DROPDOWN_CLOSE_ICON).click();
        else districtList.get(index).click();

        // log
        logger.info("Select district: %s".formatted(supInfo.getVnDistrict()));
    }

    void selectVNWard() {
        // open ward dropdown
        String currentVNWard = commonMobile.moveAndGetElement(SELECTED_VN_WARD).getText();
        commonMobile.click(SELECTED_VN_WARD);

        // get index of selected ward
        List<WebElement> wardList = commonMobile.getElements(VN_WARD_LIST, 10);
        int index = nextInt(wardList.size());

        // set info
        if (currentVNWard.equals(supInfo.getVnWard())) commonMobile.click(VN_WARD_DROPDOWN_CLOSE_ICON);
        else supInfo.setVnWard(wardList.get(index).getText());

        // select ward
        wardList.get(index).click();

        // log
        logger.info("Select ward: %s".formatted(supInfo.getVnWard()));
    }

    /* Outside VN address */
    void inputOutsideVNStreetAddress(String address) {
        // input address
        commonMobile.moveAndGetElement(OUTSIDE_VN_STREET_ADDRESS).clear();
        commonMobile.inputText(OUTSIDE_VN_STREET_ADDRESS, address);

        // set info
        supInfo.setOutsideVnStreetAddress(address);

        // log
        logger.info("Input street address: %s".formatted(address));
    }

    void inputOutsideVNAddress2(String address2) {
        // input address 2 for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_ADDRESS2).clear();
        commonMobile.inputText(OUTSIDE_VN_ADDRESS2, address2);

        // set info
        supInfo.setOutsideVnAddress2(address2);

        // log
        logger.info("Input address2: %s".formatted(address2));
    }

    void selectOutsideVNState() {
        // open province dropdown
        wait.until(elementToBeClickable(SELECTED_OUTSIDE_VN_STATE)).click();

        // get index of selected province
        List<WebElement> stateList = commonMobile.getElements(OUTSIDE_VN_STATE_LIST, 10);
        int index = nextInt(stateList.size());
        
        // set info
        supInfo.setOutsideVnState(stateList.get(index).getText());
        
        // select state
        stateList.get(index).click();
        
        // log
        logger.info("Select province: %s".formatted(supInfo.getOutsideVnState()));
    }
    
    void inputOutsideVNCity(String city) {
        // input city for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_CITY).clear();
        commonMobile.inputText(OUTSIDE_VN_CITY, city);
        
        // set info
        supInfo.setOutsideVNCity(city);
        
        // log
        logger.info("Input city: %s".formatted(city));
    }

    void inputOutsideVNZipcode(String zipcode) {
        // input zipcode for outside VN country
        commonMobile.moveAndGetElement(OUTSIDE_VN_ZIPCODE).clear();
        commonMobile.inputText(OUTSIDE_VN_ZIPCODE, zipcode);
        
        // set info
        supInfo.setOutsideVnZipCode(zipcode);
        
        // log
        logger.info("Input zipcode: %s".formatted(zipcode));
    }

    void selectResponsibleStaff() {
        // open responsible staff dropdown
        commonMobile.moveAndGetElement(SELECTED_RESPONSIBLE_STAFF).click();

        // get index of selected staff
        List<WebElement> staffList = commonMobile.getElements(RESPONSIBLE_STAFF_LIST, 5);
        int index = nextInt(staffList.size());

        // set info
        supInfo.setResponsibleStaff(staffList.get(index).getText());

        // select staff
        staffList.get(index).click();

        // log
        logger.info("Select responsible staff: %s".formatted(supInfo.getResponsibleStaff()));
    }

//    void inputDescription(String description) {
//        act.moveToElement(DESCRIPTION).click().build().perform();
//        DESCRIPTION.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
//        DESCRIPTION.sendKeys(description);
//        logger.info("Input description: %s".formatted(description));
//    }
//
//    void completeCRUSupplier() {
//        act.moveToElement(HEADER_SAVE_BTN).click().build().perform();
//        logger.info("Complete create supplier");
//    }
//
//    public void createNewSupplier(boolean isVNSupplier) throws Exception {
//        String language = "VIE";
//        // navigate to create supplier page
//        new SupplierManagementScreen(driver).openCreateSupplierScreen();
//
//        // select country
//        selectCountry(isVNSupplier);
//
//        // check UI
////        uiCRUDSupplierPage.checkUIAddSupplierPage(language, isVNSupplier);
//
//        // generate data
//        String epoch = String.valueOf(Instant.now().toEpochMilli());
//
//        // input supplier name
//        String supplierName = Pattern.compile("([\\w\\W]{0,100})").matcher("[%s] Auto - Supplier %s - %s".formatted(language, isVNSupplier ? "VN" : "Non VN", epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
//        inputSupplierName(supplierName);
//
//        // input supplier code
//        String supplierCode = Pattern.compile("(\\w{0,12})").matcher("%s".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
//        inputSupplierCode("%s".formatted(epoch));
//
//        // input supplier phone number
//        String phoneNumber = Pattern.compile("(\\d{8,13})").matcher(epoch).results().map(matchResult -> matchResult.group(1)).toList().get(0);
//        inputPhoneNumber(epoch);
//
//        // input supplier email
//        String email = Pattern.compile("([\\w\\W]{0,100})").matcher("%s@qa.team".formatted(epoch)).results().map(matchResult -> matchResult.group(1)).toList().get(0);
//        inputEmail("%s@qa.team".formatted(epoch));
//
//        // if country = Vietnam
//        if (isVNSupplier) {
//            // input address
//            inputVNAddress("Address %s".formatted(epoch));
//
//            // select city
//            selectVNCity();
//
//            // select district
//            selectVNDistrict();
//
//            // select ward
//            selectVNWard();
//        }
//        // else country = non-Vietnam
//        else {
//            // input street address
//            inputOutsideVNStreetAddress("Street address %s".formatted(epoch));
//
//            // input address2
//            inputOutsideVNAddress2("Address2 %s".formatted(epoch));
//
//            // input city
//            inputOutsideVNCity("City %s".formatted(epoch));
//
//            // select province
//            selectOutsideVNState();
//
//            // input zipcode
//            inputOutsideVNZipcode("Zipcode %s".formatted(epoch));
//        }
//
//        // select responsible staff
//        selectResponsibleStaff();
//
//        // input description
//        inputDescription("Descriptions %s".formatted(epoch));
//
//        // click Save button to complete create supplier
//        completeCRUSupplier();

//        // wait supplier management page loaded
//        commonAction.sleepInMiliSecond(3000);
//
//        // check information after create supplier
//        supplierManagementPage.checkSupplierInformationAfterCRU(supplierCode, supplierName, email, phoneNumber, uiCRUDSupplierPage);
//
//        verifyTest();
//    }

}
