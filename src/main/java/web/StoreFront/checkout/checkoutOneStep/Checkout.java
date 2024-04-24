package web.StoreFront.checkout.checkoutOneStep;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.dashboard.storefront.AddressInfo;
import utilities.utils.PropertiesUtil;
import web.StoreFront.GeneralSF;
import web.StoreFront.checkout.checkoutstep1.CheckOutStep1;
import web.StoreFront.checkout.checkoutstep1.CheckOutStep1Element;
import web.StoreFront.checkout.ordercomplete.OrderComplete;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Checkout extends CheckoutElement {
    final static Logger logger = LogManager.getLogger(Checkout.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    public Checkout(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    public Checkout clickOnEditIcon(){
        commonAction.sleepInMiliSecond(2000);
        commonAction.click(loc_icnEditShippingInfo);
        logger.info("Click on edit shipping information icon.");
        return this;
    }
    public Checkout clickUpdateBtnOnMyAddress(){
        commonAction.click(loc_dlgShippingAddress_btnUpdate);
        logger.info("Click on Update button on My Address tab.");
        return this;
    }
    public String getFullName() {
        commonAction.waitVisibilityOfElementLocated(loc_dlgUpdateAddress_txtFullName);
        String name = commonAction.getAttribute(loc_dlgUpdateAddress_txtFullName, "value");
        logger.info("Retrieved full name: %s.".formatted(name));
        return name;
    }
    public String getCountry(){
        return commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlCountry);
    }

    public Checkout verifyCountrySelectedValue(String expectedCountry) {
        String value = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlCountry);
        Assert.assertEquals(value, expectedCountry);
        logger.info("Verify country: %s display".formatted(value));
        return this;
    }
    public Checkout verifyAddress(String expectedAddress) {
        commonAction.waitVisibilityOfElementLocated(loc_dlgUpdateAddress_txtAddress);
        String address = commonAction.getAttribute(loc_dlgUpdateAddress_txtAddress, "value");
        Assert.assertEquals(address, expectedAddress);
        logger.info("Verify address: %s display".formatted(address));
        return this;
    }

    public Checkout verifyCityProvince(String expectedCityProvince) {
//        commonAction.sleepInMiliSecond(2000);
        String cityProvince = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlCityProvince);
        Assert.assertEquals(cityProvince, expectedCityProvince);
        logger.info("Verify city/province: %s".formatted(cityProvince));
        return this;
    }

    public Checkout verifyDistrict(String expectedDistrict) {
        String district = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlDistrict);
        Assert.assertEquals(district, expectedDistrict);
        logger.info("Verify district: %s display".formatted(district));
        return this;
    }

    public Checkout verifyWard(String expected) {
        String ward = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlWardTown);
        Assert.assertEquals(ward, expected);
        logger.info("Verify ward: %s display".formatted(ward));
        return this;
    }
    public Checkout verifyZipCode(String expected) {
        String zipCode = commonAction.getAttribute(loc_dlgUpdateAddress_txtZipCode, "value");
        Assert.assertEquals(zipCode, expected);
        logger.info("Verify zipcode: %s display".formatted(zipCode));
        return this;
    }

    public Checkout verifyAddress2(String expected) {
        String address2 = commonAction.getAttribute(loc_dlgUpdateAddress_txtAddress2, "value");
        Assert.assertEquals(address2, expected);
        logger.info("Verify address 2: %s display".formatted(address2));
        return this;
    }

    public Checkout verifyCity_CountryNonVietName(String expected) {
        String city = commonAction.getAttribute(loc_dlgUpdateAddress_ddlCityProvince, "value");
        Assert.assertEquals(city, expected);
        logger.info("Verify inputted city: %s display".formatted(city));
        return this;
    }

    public Checkout verifyStateRegionProvince(String expected) {
        String state = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlState);
        Assert.assertEquals(state, expected);
        logger.info("Verify State/Region/Provice: %s display".formatted(state));
        return this;
    }
    public Checkout inputAddress(String address) {
        commonAction.inputText(loc_dlgUpdateAddress_txtAddress, address);
        logger.info("Input address: %s".formatted(address));
        return this;
    }

    public String selectCountry(String country) {
        if(country.isEmpty()){
            commonAction.selectByIndex(loc_dlgUpdateAddress_ddlCountry, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(loc_dlgUpdateAddress_ddlCountry)).size()));
            country =  commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlCountry);
        }else commonAction.selectByVisibleText(loc_dlgUpdateAddress_ddlCountry, country);
        logger.info("Select country: %s".formatted(country));
        return country;
    }

    public String selectCityProvince(String city) {
        if(city.isEmpty()){
            commonAction.selectByIndex(loc_dlgUpdateAddress_ddlCityProvince, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(loc_dlgUpdateAddress_ddlCityProvince)).size()));
            city =  commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlCityProvince);
        }else {
            commonAction.selectByVisibleText(loc_dlgUpdateAddress_ddlCityProvince, city);
        }
        logger.info("Select city/province: %s".formatted(city));
        return city;
    }

    public String selectDistrict(String district) {
        commonAction.sleepInMiliSecond(1000);
        if(district.isEmpty()){
            commonAction.selectByIndex(loc_dlgUpdateAddress_ddlDistrict, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(loc_dlgUpdateAddress_ddlDistrict)).size()));
            district = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlDistrict);
        }else {
            commonAction.selectByVisibleText(loc_dlgUpdateAddress_ddlDistrict, district);
        }
        logger.info("Select district: %s".formatted(district));
        return district;
    }

    public String selectWard(String ward) {
        commonAction.sleepInMiliSecond(500);
        if(ward.isEmpty()){
            commonAction.selectByIndex(loc_dlgUpdateAddress_ddlWardTown, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(loc_dlgUpdateAddress_ddlWardTown)).size()));
            ward = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlWardTown);
        }else {
            commonAction.selectByVisibleText(loc_dlgUpdateAddress_ddlWardTown, ward);
        }
        logger.info("Select ward: %s".formatted(ward));
        return ward;
    }

    public Checkout inputAddress2(String address2) {
        commonAction.sendKeys(loc_dlgUpdateAddress_txtAddress2, address2);
        logger.info("Input address 2: %s".formatted(address2));
        return this;
    }

    public Checkout inputCity(String city) {
        commonAction.sendKeys(loc_dlgUpdateAddress_txtCity, city);
        logger.info("Input city: %s".formatted(city));
        return this;
    }

    public String selectState(String state) {
        commonAction.sleepInMiliSecond(500);
        if(state.isEmpty()){
            commonAction.selectByIndex(loc_dlgUpdateAddress_ddlState, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(loc_dlgUpdateAddress_ddlWardTown)).size()));
            state = commonAction.getDropDownSelectedValue(loc_dlgUpdateAddress_ddlState);
        }else commonAction.selectByVisibleText(loc_dlgUpdateAddress_ddlState, state);
        logger.info("Select state/region/province: %s".formatted(state));
        return state;
    }

    public Checkout inputZipCode(String zipCode) {
        commonAction.inputText(loc_dlgUpdateAddress_txtZipCode, zipCode);
        logger.info("Input zip code: %s".formatted(zipCode));
        return this;
    }

    public Checkout inputPhoneNumber(String phone) {
        commonAction.inputText(loc_dlgUpdateAddress_txtPhoneNumber, phone);
        logger.info("Input phone number: %s".formatted(phone));
        commonAction.sleepInMiliSecond(1000);
        return this;
    }
    public Checkout inputAddressInfo_VN(String country, String address, String city, String district, String ward) {
        if (country!= "") {
            selectCountry(country);
        }
        inputAddress(address);
        selectCityProvince(city);
        selectDistrict(district);
        selectWard(ward);

        return this;
    }
    public AddressInfo inputAddressInfo_VN() {
        AddressInfo addressInfo = new AddressInfo();
        String currentCountry = getCountry();
        if ( currentCountry!= "Vietnam"|| currentCountry != "Việt Nam") {
            try {
                String selectedCountry = PropertiesUtil.getPropertiesValueBySFLang("country.vietNam");
                selectCountry(selectedCountry);
                addressInfo.setCountry(selectedCountry);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String address = "address "+ new DataGenerator().generateString(10);
        inputAddress(address);
        addressInfo.setCityProvince(selectCityProvince(""));
        addressInfo.setDistrict(selectDistrict(""));
        addressInfo.setWard(selectWard(""));

        return addressInfo;
    }
    public Checkout inputAddressInfo_NonVN(String country, String address, String address2, String state, String city, String zipCode) {
        selectCountry(country);
        inputAddress(address);
        inputAddress2(address2);
        selectState(state);
        inputCity(city);
        inputZipCode(zipCode);
        return this;
    }
    public AddressInfo inputAddressInfo_NonVN() {
        AddressInfo addressInfo = new AddressInfo();
        String country = selectCountry("");
        String address = "address "+ new DataGenerator().generateString(10);
        inputAddress(address);
        String address2 = "address s "+ new DataGenerator().generateString(10);
        inputAddress2(address2);
        String state  = selectState("");
        String city = "city "+ new DataGenerator().generateString(5);
        inputCity(city);
        String zipCode = new DataGenerator().generateNumber(5);
        inputZipCode(zipCode);
        addressInfo.setCountry(country);
        addressInfo.setStreetAddress(address);
        addressInfo.setAddress2(address2);
        addressInfo.setStateRegionProvince(state);
        addressInfo.setCity(city);
        addressInfo.setZipCode(zipCode);
        return addressInfo;
    }

    public Checkout verifyAddressInfo_VN(String country, String address, String city, String district, String ward) {
        if (country != "") {
            verifyCountrySelectedValue(country);
        }
        verifyAddress(address);
        verifyCityProvince(city);
        verifyDistrict(district);
        verifyWard(ward);
        return this;
    }

    public Checkout verifyAddressInfo_NonVN(String country, String address, String address2, String state, String city, String zipCode) {
//        commonAction.sleepInMiliSecond(3000);
        verifyCountrySelectedValue(country);
        verifyAddress(address);
        verifyAddress2(address2);
        verifyStateRegionProvince(state);
        verifyCity_CountryNonVietName(city);
        verifyZipCode(zipCode);
        return this;
    }
    public Map<String,String> getOtherPhoneMap(){
        commonAction.click(loc_dlgUpdateAddress_txtPhoneNumber);
        Map<String,String> otherPhoneMap = new HashMap<>();
        String mainPhone = commonAction.getAttribute(loc_dlgUpdateAddress_txtPhoneNumber,"value").trim();
        List<WebElement> otherPhoneElement = commonAction.getElements(loc_dlgUpdateAddress_lstOtherPhone);
        for(int i=0;i<otherPhoneElement.size();i++){
            String phone = commonAction.getText(loc_dlgUpdateAddress_lstOtherPhone,i);
            String onlyPhoneNumber = phone.split("\\)")[1].trim();
            if(onlyPhoneNumber.equals(mainPhone)){
                continue;
            }
            String fullPhone= String.join("",phone.split("\\)|\\(|\s"));
            otherPhoneMap.put(fullPhone,commonAction.getText(loc_dlUpdateAddress_lstOtherPhoneName,i));
        }
        logger.info("Other phone map: "+otherPhoneMap);
        return otherPhoneMap;
    }
    public Map<String,String> getOtherEmailMap(){
        commonAction.click(loc_dlgUpdateAddress_txtEmail);
        Map<String,String> otherEmailMap = new HashMap<>();
        String mainEmail = commonAction.getAttribute(loc_dlgUpdateAddress_txtEmail,"value");
        List<WebElement> otherEmailElements = commonAction.getElements(loc_dlgUpdateAddress_lstOtherEmail);
        for(int i=0;i<otherEmailElements.size();i++){
            String email = commonAction.getText(loc_dlgUpdateAddress_lstOtherEmail,i);
            if(email.equals(mainEmail)){
                continue;
            }
            otherEmailMap.put(email,commonAction.getText(loc_dlgUpdateAddress_lstOtherEmailName,i));
        }
        logger.info("Other email map: "+otherEmailMap);
        return otherEmailMap;
    }
    public Checkout verifyOtherPhoneList(Map<String,String> actual, Map<String,String> expected){
        Assert.assertEquals(actual,expected);
        logger.info("Verify other phone list.");
        return this;
    }
    public Checkout verifyOtherEmailList(Map<String,String> actual, Map<String,String> expected){
        Assert.assertEquals(actual,expected);
        logger.info("Verify other email list.");
        return this;
    }
    public Checkout verifyDicountAmount(String expected){
        System.out.println("Discount: "+commonAction.getText(loc_lblDiscountAmount));
        Assert.assertEquals(commonAction.getText(loc_lblDiscountAmount).replaceAll("[^\\dđ]", ""),expected);
        logger.info("Verify discount amount.");
        return this;
    }
    public OrderComplete clickOnCompleteBtn(){
        commonAction.click(loc_btnComplete);
        logger.info("Click on Complete button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return new OrderComplete(driver);
    }
    public Checkout verifyProductName(String...productNamesExpected){
        for (int i=0;i<productNamesExpected.length;i++) {
            Assert.assertEquals(commonAction.getText(loc_lstProductName,i).toLowerCase().trim(),productNamesExpected[i].toLowerCase().trim());
        }
        logger.info("Verify product name list.");
        return this;
    }
    public Checkout clickOnConfirmButtonOnUpdateAddresModal(){
        commonAction.click(loc_dlgUpdateAddress_btnConfirm);
        logger.info("Click on confirm button on update address modal.");
        return this;
    }
    public Checkout clickOnConfirmbuttonOnShippingAddressModal(){
        commonAction.click(loc_dlgShippingAddress_btnConfirm);
        logger.info("Click on Confirm button on Shipping address modal");
        return this;
    }
    public Checkout updateAddressVN(String country,String address, String city, String district, String ward){
        goToEditMyAddress();
        inputAddressInfo_VN(country,address,city,district,ward);
        completeEditAddress();
        return this;
    }
    public Checkout goToEditMyAddress(){
        clickOnEditIcon();
        clickUpdateBtnOnMyAddress();
        return this;
    }
    public Checkout completeEditAddress(){
        clickOnConfirmButtonOnUpdateAddresModal();
        clickOnConfirmbuttonOnShippingAddressModal();
        return this;
    }
    public int getShippingFee(){
        int shippingFee = Integer.parseInt(commonAction.getText(loc_lblShippingFee).replaceAll("[^\\d]",""));
        logger.info("Shipping free: "+shippingFee);
        return shippingFee;
    }
}
