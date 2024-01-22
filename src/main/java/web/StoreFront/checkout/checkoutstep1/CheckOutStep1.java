package web.StoreFront.checkout.checkoutstep1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import web.StoreFront.GeneralSF;
import web.StoreFront.checkout.checkoutstep2.CheckOutStep2;
import utilities.commons.UICommonAction;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getFullName() {
    	commonAction.waitVisibilityOfElementLocated(checkOutStep1UI.loc_txtFullName);
        String name = commonAction.getAttribute(checkOutStep1UI.loc_txtFullName, "value");
        logger.info("Retrieved full name: %s.".formatted(name));
        return name;
    }      
    
    public CheckOutStep1 verifyCountrySelectedValue(String expectedCountry) {
        String value = commonAction.getDropDownSelectedValue(checkOutStep1UI.loc_ddlCountry);
        Assert.assertEquals(value, expectedCountry);
        logger.info("Verify country: %s display".formatted(value));
        return this;
    }

    public CheckOutStep1 verifyFullName(String expectedName) {
        String fullName = commonAction.getAttribute(checkOutStep1UI.loc_txtFullName, "value");
        Assert.assertEquals(fullName, expectedName);
        logger.info("Verify fullname: %s display".formatted(fullName));
        return this;
    }

    public CheckOutStep1 verifyPhoneNumber(String expectedPhone) {
        String phoneNumber = commonAction.getAttribute(checkOutStep1UI.loc_txtPhoneNumber, "value");
        Assert.assertEquals(phoneNumber, expectedPhone);
        logger.info("Verify phone number: %s display".formatted(phoneNumber));
        return this;
    }

    public CheckOutStep1 verifyAddress(String expectedAddress) {
        commonAction.waitVisibilityOfElementLocated(checkOutStep1UI.loc_txtAddress);
        String address = commonAction.getAttribute(checkOutStep1UI.loc_txtAddress, "value");
        Assert.assertEquals(address, expectedAddress);
        logger.info("Verify address: %s display".formatted(address));
        return this;
    }

    public CheckOutStep1 verifyCityProvince(String expectedCityProvince) {
        commonAction.sleepInMiliSecond(2000);
        String cityProvince = commonAction.getDropDownSelectedValue(checkOutStep1UI.loc_ddlCity);
        Assert.assertEquals(cityProvince, expectedCityProvince);
        logger.info("Verify city/province: %s".formatted(cityProvince));
        return this;
    }

    public CheckOutStep1 verifyDistrict(String expectedDistrict) {
        String district = commonAction.getDropDownSelectedValue(checkOutStep1UI.loc_ddlDistrict);
        Assert.assertEquals(district, expectedDistrict);
        logger.info("Verify district: %s display".formatted(district));
        return this;
    }

    public CheckOutStep1 verifyWard(String expected) {
        String ward = commonAction.getDropDownSelectedValue(checkOutStep1UI.loc_ddlWard);
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
                commonAction.click(checkOutStep1UI.loc_blkCOD);
                break;
            case "BANKTRANFER":
                commonAction.click(checkOutStep1UI.loc_blkBankTransfer);
                break;
            case "PAYPAL":
                commonAction.click(checkOutStep1UI.loc_blkPayPal);
                break;
            case "DEBT":
                commonAction.click(checkOutStep1UI.loc_blkDebt);
                break;
            case "MOMO":
                commonAction.click(checkOutStep1UI.loc_blkMoMo);
                break;
        }
        return this;
    }

    public CheckOutStep1 verifyZipCode(String expected) {
        String zipCode = commonAction.getAttribute(checkOutStep1UI.loc_txtZipCode, "value");
        Assert.assertEquals(zipCode, expected);
        logger.info("Verify zipcode: %s display".formatted(zipCode));
        return this;
    }

    public CheckOutStep1 verifyAddress2(String expected) {
        String address2 = commonAction.getAttribute(checkOutStep1UI.loc_txtAddress2, "value");
        Assert.assertEquals(address2, expected);
        logger.info("Verify address 2: %s display".formatted(address2));
        return this;
    }

    public CheckOutStep1 verifyCity_CountryNonVietName(String expected) {
        String city = commonAction.getAttribute(checkOutStep1UI.loc_txtCity, "value");
        Assert.assertEquals(city, expected);
        logger.info("Verify inputted city: %s display".formatted(city));
        return this;
    }

    public CheckOutStep1 verifyStateRegionProvince(String expected) {
        String state = commonAction.getDropDownSelectedValue(checkOutStep1UI.loc_ddlStateRegionProvince);
        Assert.assertEquals(state, expected);
        logger.info("Verify State/Region/Provice: %s display".formatted(state));
        return this;
    }

    public CheckOutStep2 clickOnNextButton() {
        commonAction.sleepInMiliSecond(1500);
        commonAction.click(checkOutStep1UI.loc_btnNext);
        logger.info("Click on Next button.");
        waitTillLoaderDisappear();
        return new CheckOutStep2(driver);
    }

    public CheckOutStep1 inputAddres(String address) {
        commonAction.inputText(checkOutStep1UI.loc_txtAddress, address);
        logger.info("Input address: %s".formatted(address));
        return this;
    }

    public CheckOutStep1 selectCountry(String country) {
        commonAction.selectByVisibleText(checkOutStep1UI.loc_ddlCountry, country);
        logger.info("Select country: %s".formatted(country));
        return this;
    }

    public CheckOutStep1 selectCityProvince(String city) {
        commonAction.selectByVisibleText(checkOutStep1UI.loc_ddlCity, city);
        logger.info("Select city/province: %s".formatted(city));
        return this;
    }

    public CheckOutStep1 selectDistrict(String district) {
        commonAction.sleepInMiliSecond(1000);
        commonAction.selectByVisibleText(checkOutStep1UI.loc_ddlDistrict, district);
        logger.info("Select district: %s".formatted(district));
        return this;
    }

    public CheckOutStep1 selectWard(String ward) {
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(checkOutStep1UI.loc_ddlWard, ward);
        logger.info("Select ward: %s".formatted(ward));
        return this;
    }

    public CheckOutStep1 inputAddress2(String address2) {
        commonAction.sendKeys(checkOutStep1UI.loc_txtAddress2, address2);
        logger.info("Input address 2: %s".formatted(address2));
        return this;
    }

    public CheckOutStep1 inputCity(String city) {
        commonAction.sendKeys(checkOutStep1UI.loc_txtCity, city);
        logger.info("Input city: %s".formatted(city));
        return this;
    }

    public CheckOutStep1 selectState(String state) {
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(checkOutStep1UI.loc_ddlStateRegionProvince, state);
        logger.info("Select state/region/province: %s".formatted(state));
        return this;
    }

    public CheckOutStep1 inputZipCode(String zipCode) {
        commonAction.inputText(checkOutStep1UI.loc_txtZipCode, zipCode);
        logger.info("Input zip code: %s".formatted(zipCode));
        return this;
    }

    public CheckOutStep1 inputPhoneNumber(String phone) {
        commonAction.inputText(checkOutStep1UI.loc_txtPhoneNumber, phone);
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
        commonAction.sleepInMiliSecond(3000);
        verifyCountrySelectedValue(country);
        verifyAddress(address);
        verifyAddress2(address2);
        verifyStateRegionProvince(state);
        verifyCity_CountryNonVietName(city);
        verifyZipCode(zipCode);
        return this;
    }
    public Map<String,String> getOtherPhoneMap(){
        commonAction.click(checkOutStep1UI.loc_txtPhoneNumber);
        Map<String,String> otherPhoneMap = new HashMap<>();
        String mainPhone = commonAction.getAttribute(checkOutStep1UI.loc_txtPhoneNumber,"value").trim();
        List<WebElement> otherPhoneElement = commonAction.getElements(checkOutStep1UI.loc_lst_lblOtherPhone);
        for(int i=0;i<otherPhoneElement.size();i++){
            String phone = commonAction.getText(checkOutStep1UI.loc_lst_lblOtherPhone,i);
            String onlyPhoneNumber = phone.split("\\)")[1].trim();
            if(onlyPhoneNumber.equals(mainPhone)){
                continue;
            }
            String fullPhone= String.join("",phone.split("\\)|\\(|\s"));
            otherPhoneMap.put(fullPhone,commonAction.getText(checkOutStep1UI.loc_lst_lblPhoneName,i));
        }
        logger.info("Other phone map: "+otherPhoneMap);
        return otherPhoneMap;
    }
    public Map<String,String> getOtherEmailMap(){
        commonAction.click(checkOutStep1UI.loc_txtEmail);
        Map<String,String> otherEmailMap = new HashMap<>();
        String mainEmail = commonAction.getAttribute(checkOutStep1UI.loc_txtEmail,"value");
        List<WebElement> otherEmailElements = commonAction.getElements(checkOutStep1UI.loc_lst_lblOtherEmail);
        for(int i=0;i<otherEmailElements.size();i++){
            String email = commonAction.getText(checkOutStep1UI.loc_lst_lblOtherEmail,i);
            if(email.equals(mainEmail)){
                continue;
            }
            otherEmailMap.put(email,commonAction.getText(checkOutStep1UI.loc_lst_lblEmailName,i));
        }
        logger.info("Other email map: "+otherEmailMap);
        return otherEmailMap;
    }
    public CheckOutStep1 verifyOtherPhoneList(Map<String,String> actual, Map<String,String> expected){
        Assert.assertEquals(actual,expected);
        logger.info("Verify other phone list.");
        return this;
    }
    public CheckOutStep1 verifyOtherEmailList(Map<String,String> actual, Map<String,String> expected){
        Assert.assertEquals(actual,expected);
        logger.info("Verify other email list.");
        return this;
    }
    public CheckOutStep1 clickOnArrowIcon(){
        commonAction.click(checkOutStep1UI.loc_iconArrowShowSummaryPrice);
        logger.info("Click on Arrow icon to show/hide total summary.");
        return this;
    }
    public CheckOutStep1 verifyDicountAmount(String expected){
        Assert.assertEquals(String.join("",commonAction.getText(checkOutStep1UI.loc_blkSummaryPrice_lblDiscountAmount).split(",|-\s")),expected);
        logger.info("Verify discount amount.");
        return this;
    }
}
