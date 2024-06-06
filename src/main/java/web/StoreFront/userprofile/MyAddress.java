package web.StoreFront.userprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.data.DataGenerator;
import utilities.model.dashboard.storefront.AddressInfo;
import web.StoreFront.header.HeaderSF;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import java.time.Duration;

public class MyAddress extends HeaderSF {
	
	final static Logger logger = LogManager.getLogger(MyAddress.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    MyAddressElement myAddressUI;
    SoftAssert soft = new SoftAssert();
    
    public MyAddress (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        myAddressUI = new MyAddressElement(driver);
        PageFactory.initElements(driver, this);
    }

    public String getCountry() {
		String js = "var e=arguments[0], i=e.selectedIndex; return i < 0 ? null : e.options[i];";
		WebElement selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, commonAction.getElement(myAddressUI.loc_ddlCountry));
		if (selectedOption == null) throw new NoSuchElementException("No options are selected");
		String value = selectedOption.getText();
    	logger.info("Retrieved Country: " + value);
        return value;
    }
    public String getAddress(){
        String address = commonAction.getAttribute(myAddressUI.loc_txtAddress,"value");
        logger.info("Get address: %s".formatted(address));
        return address;
    }
    public String getCity(){
        String city = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlCity);
        logger.info("Get City: %s".formatted(city));
        return city;
    }
    public String getDistrict(){
        String district = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlDistrict);
        logger.info("Get district: %s".formatted(district));
        return district;
    }
    public String getWard(){
        String ward = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlWard);
        logger.info("Get district: %s".formatted(ward));
        return ward;
    }
    public String getAddress2(){
        String address2 = commonAction.getAttribute(myAddressUI.loc_txtAddress2,"value");
        logger.info("Get address 2: %s".formatted(address2));
        return address2;
    }
    public String getState(){
        String state = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlState);
        logger.info("Get state: %s".formatted(state));
        return state;
    }
    public String getInputtedCity(){
        String city = commonAction.getAttribute(myAddressUI.loc_txtCity,"value");
        logger.info("Get inputted city: %s".formatted(city));
        return city;
    }
    public String getZipCode(){
        String zipCode = commonAction.getAttribute(myAddressUI.loc_txtZipCode,"value");
        logger.info("Get zip code: %s".formatted(zipCode));
        return zipCode;
    }
    public MyAddress verifyCountry(String expected){
        Assert.assertEquals(getCountry(),expected);
        logger.info("Verify country show correctly");
        return this;
    }
    public MyAddress verifyAddress(String expected){
        Assert.assertEquals(getAddress(),expected);
        logger.info("Verify address show correctly");
        return this;
    }
    public MyAddress verifyCity(String expected){
        Assert.assertEquals(getCity(),expected);
        logger.info("Verify city show correctly");
        return this;
    }
    public MyAddress verifyDistrict(String expected){
        Assert.assertEquals(getDistrict(),expected);
        logger.info("Verify district show correctly");
        return this;
    }
    public MyAddress verifyWard(String expected){
        Assert.assertEquals(getWard(),expected);
        logger.info("Verify ward show correctly");
        return this;
    }
    public MyAddress verifyAddress2(String expected){
        Assert.assertEquals(getAddress2(),expected);
        logger.info("Verify address 2 show correctly");
        return this;
    }
    public MyAddress verifyState(String expected){
        Assert.assertEquals(getState(),expected);
        logger.info("Verify state/region/province show correctly");
        return this;
    }
    public MyAddress verifyInputtedCity(String expected){
        Assert.assertEquals(getInputtedCity(),expected);
        logger.info("Verify inputted city show correctly");
        return this;
    }
    public MyAddress verifyZipCode(String expected){
        Assert.assertEquals(getZipCode(),expected);
        logger.info("Verify zipcode show correctly");
        return this;
    }
    public String selectCountry(String country) {
        commonAction.waitTillSelectDropdownHasData(myAddressUI.loc_ddlCountry);
        if(country.isEmpty()){
            commonAction.selectByIndex(myAddressUI.loc_ddlCountry, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(myAddressUI.loc_ddlCountry)).size()));
            country =  commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlCountry);
        }else commonAction.selectByVisibleText(myAddressUI.loc_ddlCountry, country);
        logger.info("Select country: %s".formatted(country));
        return country;
    }
    public MyAddress inputAddress(String address){
        commonAction.inputText(myAddressUI.loc_txtAddress,address);
        logger.info("Input address: %s".formatted(address));
        return this;
    }
    public MyAddress inputAddress2(String address2){
        commonAction.inputText(myAddressUI.loc_txtAddress2,address2);
        logger.info("Input address 2: %s".formatted(address2));
        return this;
    }
    public MyAddress inputCity(String city){
        commonAction.inputText(myAddressUI.loc_txtCity,city);
        logger.info("Input address: %s".formatted(city));
        return this;
    }
    public String selectCityProvince(String city) {
        commonAction.waitTillSelectDropdownHasData(myAddressUI.loc_ddlCity);
        if(city.isEmpty()){
            commonAction.selectByIndex(myAddressUI.loc_ddlCity, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(myAddressUI.loc_ddlCity)).size()));
            city =  commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlCity);
        }else {
            commonAction.selectByVisibleText(myAddressUI.loc_ddlCity, city);
        }
        logger.info("Select city/province: %s".formatted(city));
        return city;
    }
    public String selectDistrict(String district) {
        commonAction.waitTillSelectDropdownHasData(myAddressUI.loc_ddlDistrict);
        if(district.isEmpty()){
            commonAction.selectByIndex(myAddressUI.loc_ddlDistrict, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(myAddressUI.loc_ddlDistrict)).size()));
            district = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlDistrict);
        }else {
            commonAction.selectByVisibleText(myAddressUI.loc_ddlDistrict, district);
        }
        logger.info("Select district: %s".formatted(district));
        return district;
    }
    public String selectWard(String ward) {
        commonAction.waitTillSelectDropdownHasData(myAddressUI.loc_ddlWard);
        if(ward.isEmpty()){
            commonAction.selectByIndex(myAddressUI.loc_ddlWard, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(myAddressUI.loc_ddlWard)).size()));
            ward = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlWard);
        }else {
            commonAction.selectByVisibleText(myAddressUI.loc_ddlWard, ward);
        }
        logger.info("Select ward: %s".formatted(ward));
        return ward;
    }
    public String selectState(String state) {
        commonAction.waitTillSelectDropdownHasData(myAddressUI.loc_ddlState);
        if(state.isEmpty()){
            commonAction.selectByIndex(myAddressUI.loc_ddlState, new DataGenerator().generatNumberInBound(1,commonAction.getAllOptionInDropDown(commonAction.getElement(myAddressUI.loc_ddlState)).size()));
            state = commonAction.getDropDownSelectedValue(myAddressUI.loc_ddlState);
        }else commonAction.selectByVisibleText(myAddressUI.loc_ddlState, state);
        logger.info("Select state/region/province: %s".formatted(state));
        return state;
    }
    public MyAddress inputZipCode(String zipCode){
        commonAction.inputText(myAddressUI.loc_txtZipCode,zipCode);
        logger.info("Input address: %s".formatted(zipCode));
        return this;
    }
    public MyAddress clickOnSave(){
        commonAction.click(myAddressUI.loc_btnSave);
        logger.info("Click on Save button.");
        waitTillLoaderDisappear();
        commonAction.sleepInMiliSecond(3000);
        return this;
    }
    public MyAddress inputAddressInfo_VN(String country, String address, String city, String district, String ward){
        if(country!="") {
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
        if ( currentCountry!= "Vietnam") {
            try {
                selectCountry("Vietnam");
                addressInfo.setCountry("Vietnam");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String address = "address "+ new DataGenerator().generateString(10);
        inputAddress(address);
        addressInfo.setAddress(address);
        addressInfo.setCityProvince(selectCityProvince(""));
        addressInfo.setDistrict(selectDistrict(""));
        addressInfo.setWard(selectWard(""));

        return addressInfo;
    }
    public  MyAddress inputAddressInfo_NonVN(String country, String address, String address2, String city, String state, String zipCode){
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
    public MyAddress verifyAddressInfo_VN(String country, String address, String city, String district, String ward){
        commonAction.sleepInMiliSecond(2000);
        if(country!="") {
            verifyCountry(country);
        }
        verifyAddress(address);
        verifyCity(city);
        verifyDistrict(district);
        verifyWard(ward);
        return this;
    }
    public  MyAddress verifyAddressInfo_NonVN(String country, String address, String address2, String city, String state, String zipCode){
        commonAction.sleepInMiliSecond(2000);
        if(country!=""){
            verifyCountry(country);
        }
        verifyAddress(address);
        verifyAddress2(address2);
        verifyState(state);
        verifyInputtedCity(city);
        verifyZipCode(zipCode);
        return this;
    }
    public MyAddress verifyAddressDisplayMaximumCharacter(int maximumChar){
        String address = getAddress();
        int addressLength = address.length();
        Assert.assertEquals(addressLength,maximumChar);
        logger.info("Validate address field: maximum "+maximumChar);
        return this;
    }
    public MyAddress verifyAddressEmpty(){
        Assert.assertEquals(getAddress(),"");
        logger.info("Verify address empty");
        return this;
    }
    public MyAddress verifyText() throws Exception {
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblMyAddress), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.pageTitle"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblMyAddress), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.pageTitle"));
        selectCountry("Vietnam");
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblCountry), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.country"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblAddress), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.address"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblCityVN), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cityVN"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblDistrict), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.district"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblWard), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.ward"));
        selectCountry("Tonga");
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblAddress), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.streetAddress"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblAddress2), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.address2"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblState), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.stateRegionProvince"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblCityNonVN), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cityNonVN"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_lblZipCode), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.zipCode"));
        Assert.assertEquals(commonAction.getAttribute(myAddressUI.loc_txtAddress,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.addressHint"));
        Assert.assertEquals(commonAction.getAttribute(myAddressUI.loc_txtAddress2,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.address2Hint"));
        Assert.assertEquals(commonAction.getAttribute(myAddressUI.loc_txtCity,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cityNonVNHint"));
        Assert.assertEquals(commonAction.getAttribute(myAddressUI.loc_txtZipCode,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.zipCodeHint"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_btnSave), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.saveBtn"));
        Assert.assertEquals(commonAction.getText(myAddressUI.loc_btnCancel), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cancelBtn"));
        return this;
    }
}
