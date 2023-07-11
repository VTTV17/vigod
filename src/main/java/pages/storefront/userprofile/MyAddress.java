package pages.storefront.userprofile;

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
import pages.storefront.header.HeaderSF;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
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
		WebElement selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, myAddressUI.COUNTRY);
		if (selectedOption == null) throw new NoSuchElementException("No options are selected");
		String value = selectedOption.getText();
    	logger.info("Retrieved Country: " + value);
        return value;
    }
    public String getAddress(){
        String address = commonAction.getElementAttribute(myAddressUI.ADDRESS_INPUT,"value");
        logger.info("Get address: %s".formatted(address));
        return address;
    }
    public String getCity(){
        String city = commonAction.getDropDownSelectedValue(myAddressUI.CITY_DROPDOWN);
        logger.info("Get City: %s".formatted(city));
        return city;
    }
    public String getDistrict(){
        String district = commonAction.getDropDownSelectedValue(myAddressUI.DISTRICT_DROPDOWN);
        logger.info("Get district: %s".formatted(district));
        return district;
    }
    public String getWard(){
        String ward = commonAction.getDropDownSelectedValue(myAddressUI.WARD_DROPDOWN);
        logger.info("Get district: %s".formatted(ward));
        return ward;
    }
    public String getAddress2(){
        String address2 = commonAction.getElementAttribute(myAddressUI.ADDRESS2_INPUT,"value");
        logger.info("Get address 2: %s".formatted(address2));
        return address2;
    }
    public String getState(){
        String state = commonAction.getDropDownSelectedValue(myAddressUI.STATE_DROPDOWN);
        logger.info("Get state: %s".formatted(state));
        return state;
    }
    public String getInputtedCity(){
        String city = commonAction.getElementAttribute(myAddressUI.CITY_INPUT,"value");
        logger.info("Get inputted city: %s".formatted(city));
        return city;
    }
    public String getZipCode(){
        String zipCode = commonAction.getElementAttribute(myAddressUI.ZIPCODE_INPUT,"value");
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
    public MyAddress selectCountry(String country){
        commonAction.selectByVisibleText(myAddressUI.COUNTRY,country);
        logger.info("Input country: %s".formatted(country));
        return this;
    }
    public MyAddress inputAddress(String address){
        commonAction.inputText(myAddressUI.ADDRESS_INPUT,address);
        logger.info("Input address: %s".formatted(address));
        return this;
    }
    public MyAddress inputAddress2(String address2){
        commonAction.inputText(myAddressUI.ADDRESS2_INPUT,address2);
        logger.info("Input address 2: %s".formatted(address2));
        return this;
    }
    public MyAddress inputCity(String city){
        commonAction.inputText(myAddressUI.CITY_INPUT,city);
        logger.info("Input address: %s".formatted(city));
        return this;
    }
    public MyAddress selectCityProvince(String city){
        commonAction.selectByVisibleText(myAddressUI.CITY_DROPDOWN,city);
        logger.info("Select city: %s".formatted(city));
        return this;
    }
    public MyAddress selectDistrict(String district){
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(myAddressUI.DISTRICT_DROPDOWN,district);
        logger.info("Select district: %s".formatted(district));
        return this;
    }
    public MyAddress selectWard(String ward){
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(myAddressUI.WARD_DROPDOWN,ward);
        logger.info("Select ward: %s".formatted(ward));
        return this;
    }
    public MyAddress selectState(String state){
        commonAction.sleepInMiliSecond(500);
        commonAction.selectByVisibleText(myAddressUI.STATE_DROPDOWN,state);
        logger.info("Select ward: %s".formatted(state));
        return this;
    }
    public MyAddress inputZipCode(String zipCode){
        commonAction.inputText(myAddressUI.ZIPCODE_INPUT,zipCode);
        logger.info("Input address: %s".formatted(zipCode));
        return this;
    }
    public MyAddress clickOnSave(){
        commonAction.clickElement(myAddressUI.SAVE_BTN);
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
    public  MyAddress inputAddressInfo_NonVN(String country, String address, String address2, String city, String state, String zipCode){
        selectCountry(country);
        inputAddress(address);
        inputAddress2(address2);
        selectState(state);
        inputCity(city);
        inputZipCode(zipCode);
        return this;
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
        verifyCountry(country);
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
        Assert.assertEquals(commonAction.getText(myAddressUI.MY_ADDRESS_TITLE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.pageTitle"));
        Assert.assertEquals(commonAction.getText(myAddressUI.MY_ADDRESS_TITLE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.pageTitle"));
        selectCountry("Vietnam");
        Assert.assertEquals(commonAction.getText(myAddressUI.COUNTRY_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.country"));
        Assert.assertEquals(commonAction.getText(myAddressUI.ADDRESS_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.address"));
        Assert.assertEquals(commonAction.getText(myAddressUI.CITY_VN_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cityVN"));
        Assert.assertEquals(commonAction.getText(myAddressUI.DISTRICT_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.district"));
        Assert.assertEquals(commonAction.getText(myAddressUI.WARD_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.ward"));
        selectCountry("Tonga");
        Assert.assertEquals(commonAction.getText(myAddressUI.ADDRESS_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.streetAddress"));
        Assert.assertEquals(commonAction.getText(myAddressUI.ADDRESS2_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.address2"));
        Assert.assertEquals(commonAction.getText(myAddressUI.STATE_CODE_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.stateRegionProvince"));
        Assert.assertEquals(commonAction.getText(myAddressUI.CITY_NONVN_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cityNonVN"));
        Assert.assertEquals(commonAction.getText(myAddressUI.ZIP_CODE_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.zipCode"));
        Assert.assertEquals(commonAction.getElementAttribute(myAddressUI.ADDRESS_INPUT,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.addressHint"));
        Assert.assertEquals(commonAction.getElementAttribute(myAddressUI.ADDRESS2_INPUT,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.address2Hint"));
        Assert.assertEquals(commonAction.getElementAttribute(myAddressUI.CITY_INPUT,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cityNonVNHint"));
        Assert.assertEquals(commonAction.getElementAttribute(myAddressUI.ZIPCODE_INPUT,"placeholder"),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.zipCodeHint"));
        Assert.assertEquals(commonAction.getText(myAddressUI.SAVE_BTN), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.saveBtn"));
        Assert.assertEquals(commonAction.getText(myAddressUI.CANCEL_BTN), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAddress.cancelBtn"));
        return this;
    }
}
