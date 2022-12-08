package pages.storefront.checkout.checkoutstep1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2;
import pages.storefront.userprofile.userprofileinfo.UserProfileElement;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.UICommonAction;

import java.time.Duration;

public class CheckOutStep1 extends GeneralSF {
    final static Logger logger = LogManager.getLogger(CheckOutStep1.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    CheckOutStep1Element checkOutStep1UI;

    public CheckOutStep1(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        checkOutStep1UI = new CheckOutStep1Element(driver);
        PageFactory.initElements(driver, this);
    }

    public CheckOutStep1 verifyCountrySelectedValue(String expectedCountry) {
        String value = commonAction.getDropDownSelectedValue(checkOutStep1UI.COUNTRY_DROPDOWN);
        Assert.assertEquals(value, expectedCountry);
        logger.info("Verify country: %s display".formatted(value));
        return this;
    }

    public CheckOutStep1 verifyFullName(String expectedName) {
        String fullName = commonAction.getElementAttribute(checkOutStep1UI.FULL_NAME_INPUT, "value");
        Assert.assertEquals(fullName, expectedName);
        logger.info("Verify fullname: %s display".formatted(fullName));
        return this;
    }

    public CheckOutStep1 verifyPhoneNumber(String expectedPhone) {
        String phoneNumber = commonAction.getElementAttribute(checkOutStep1UI.PHONE_NUMBER_INPUT, "value");
        Assert.assertEquals(phoneNumber, expectedPhone);
        logger.info("Verify phone number: %s display".formatted(phoneNumber));
        return this;
    }

    public CheckOutStep1 verifyAddress(String expectedAddress) {
        String address = commonAction.getElementAttribute(checkOutStep1UI.ADDRESS_INPUT, "value");
        Assert.assertEquals(address, expectedAddress);
        logger.info("Verify address: %s display".formatted(address));
        return this;
    }

    public CheckOutStep1 verifyCityProvince(String expectedCityProvince) {
        String cityProvince = commonAction.getDropDownSelectedValue(checkOutStep1UI.CITY_PROVINCE_DROPDOWN);
        Assert.assertEquals(cityProvince, expectedCityProvince);
        logger.info("Verify city/province: %s".formatted(cityProvince));
        return this;
    }

    public CheckOutStep1 verifyDistrict(String expectedDistrict) {
        String district = commonAction.getDropDownSelectedValue(checkOutStep1UI.DISTRICT_DROPDOWN);
        Assert.assertEquals(district, expectedDistrict);
        logger.info("Verify district: %s display".formatted(district));
        return this;
    }

    public CheckOutStep1 verifyWard(String expected) {
        String ward = commonAction.getDropDownSelectedValue(checkOutStep1UI.WARD_DROPDOWN);
        Assert.assertEquals(ward, expected);
        logger.info("Verify ward: %s display".formatted(ward));
        return this;
    }

    /**
     * @param payment: COD, BANKTRANFER, PAYPAL, DEBT, MOMO
     */
    public CheckOutStep1 selectPaymentMethod(String payment) {
        commonAction.scrollBottomPage();
        switch (payment) {
            case "COD":
                commonAction.clickElement(checkOutStep1UI.COD);
                break;
            case "BANKTRANFER":
                commonAction.clickElement(checkOutStep1UI.BANK_TRANSFER);
                break;
            case "PAYPAL":
                commonAction.clickElement(checkOutStep1UI.PAYPAL);
                break;
            case "DEBT":
                commonAction.clickElement(checkOutStep1UI.DEBT);
                break;
            case "MOMO":
                commonAction.clickElement(checkOutStep1UI.MOMO);
                break;
        }
        return this;
    }

    public CheckOutStep1 verifyZipCode(String expected) {
        String zipCode = commonAction.getElementAttribute(checkOutStep1UI.ZIP_CODE_INPUT, "value");
        Assert.assertEquals(zipCode, expected);
        logger.info("Verify zipcode: %s display".formatted(zipCode));
        return this;
    }

    public CheckOutStep1 verifyAddress2(String expected) {
        String address2 = commonAction.getElementAttribute(checkOutStep1UI.ADDRESS_2_INPUT, "value");
        Assert.assertEquals(address2, expected);
        logger.info("Verify address 2: %s display".formatted(address2));
        return this;
    }

    public CheckOutStep1 verifyCity_CountryNonVietName(String expected) {
        String city = commonAction.getElementAttribute(checkOutStep1UI.CITY_INPUT, "value");
        Assert.assertEquals(city, expected);
        logger.info("Verify inputted city: %s display".formatted(city));
        return this;
    }

    public CheckOutStep1 verifyStateRegionProvince(String expected) {
        String state = commonAction.getDropDownSelectedValue(checkOutStep1UI.STATE_REGION_PROVICE_DROPDOWN);
        Assert.assertEquals(state, expected);
        logger.info("Verify State/Region/Provice: %s display".formatted(state));
        return this;
    }

    public CheckOutStep2 clickOnNextButton() {
        commonAction.clickElement(checkOutStep1UI.NEXT_BUTTON);
        logger.info("Click on Next button.");
        waitTillLoaderDisappear();
        return new CheckOutStep2(driver);
    }

    public CheckOutStep1 inputAddres(String address) {
        commonAction.inputText(checkOutStep1UI.ADDRESS_INPUT, address);
        logger.info("Input address: %s".formatted(address));
        return this;
    }

    public CheckOutStep1 selectCountry(String country) {
        commonAction.selectByVisibleText(checkOutStep1UI.COUNTRY_DROPDOWN, country);
        logger.info("Select country: %s".formatted(country));
        return this;
    }

    public CheckOutStep1 selectCityProvince(String city) {
        commonAction.selectByVisibleText(checkOutStep1UI.CITY_PROVINCE_DROPDOWN, city);
        logger.info("Select city/province: %s".formatted(city));
        return this;
    }

    public CheckOutStep1 selectDistrict(String district) {
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(checkOutStep1UI.DISTRICT_DROPDOWN, district);
        logger.info("Select district: %s".formatted(district));
        return this;
    }

    public CheckOutStep1 selectWard(String ward) {
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(checkOutStep1UI.WARD_DROPDOWN, ward);
        logger.info("Select ward: %s".formatted(ward));
        return this;
    }

    public CheckOutStep1 inputAddress2(String address2) {
        commonAction.inputText(checkOutStep1UI.ADDRESS_2_INPUT, address2);
        logger.info("Input address 2: %s".formatted(address2));
        return this;
    }

    public CheckOutStep1 inputCity(String city) {
        commonAction.inputText(checkOutStep1UI.CITY_INPUT, city);
        logger.info("Input city: %s".formatted(city));
        return this;
    }

    public CheckOutStep1 selectState(String state) {
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(checkOutStep1UI.STATE_REGION_PROVICE_DROPDOWN, state);
        logger.info("Select state/region/province: %s".formatted(state));
        return this;
    }

    public CheckOutStep1 inputZipCode(String zipCode) {
        commonAction.inputText(checkOutStep1UI.ZIP_CODE_INPUT, zipCode);
        logger.info("Input zip code: %s".formatted(zipCode));
        return this;
    }

    public CheckOutStep1 inputPhoneNumber(String phone) {
        commonAction.inputText(checkOutStep1UI.PHONE_NUMBER_INPUT, phone);
        logger.info("Input phone number: %s".formatted(phone));
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public CheckOutStep1 inputAddressInfo_VN(String country, String address, String city, String district, String ward) {
        if (country != "") {
            selectCountry(country);
        }
        inputAddres(address);
        selectCityProvince(city);
        selectDistrict(district);
        selectWard(ward);
        return this;
    }

    public CheckOutStep1 inputAddressInfo_NonVN(String country, String address, String address2, String state, String city, String zipCode) {
        selectCountry(country);
        inputAddres(address);
        inputAddress2(address2);
        selectState(state);
        inputCity(city);
        inputZipCode(zipCode);
        return this;
    }

    public CheckOutStep1 verifyAddressInfo_VN(String country, String address, String city, String district, String ward) {
        commonAction.sleepInMiliSecond(2000);
        if (country != "") {
            verifyCountrySelectedValue(country);
        }
        verifyAddress(address);
        verifyCityProvince(city);
        verifyDistrict(district);
        verifyWard(ward);
        return this;
    }

    public CheckOutStep1 verifyAddressInfo_NonVN(String country, String address, String address2, String state, String city, String zipCode) {
        commonAction.sleepInMiliSecond(2000);
        verifyCountrySelectedValue(country);
        verifyAddress(address);
        verifyAddress2(address2);
        verifyStateRegionProvince(state);
        verifyCity_CountryNonVietName(city);
        verifyZipCode(zipCode);
        return this;
    }
}
