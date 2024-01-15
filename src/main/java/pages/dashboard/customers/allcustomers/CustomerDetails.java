package pages.dashboard.customers.allcustomers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class CustomerDetails extends HomePage {
	
	final static Logger logger = LogManager.getLogger(CustomerDetails.class);

    WebDriver driver;
    UICommonAction commonAction;
    
    public CustomerDetails (WebDriver driver) {
        super(driver);
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_txtEmail = By.id("email");
    By loc_txtPhone = By.id("phone");
    By loc_txtPhoneCode = By.cssSelector(".row.phone-email .phone-code");
    By loc_txtBirthday = By.cssSelector(".birthday-date input");
    By loc_ddlCountry = By.id("country");
    By loc_txtAddress = By.id("address");
    By loc_ddlProvince = By.id("province");
    By loc_ddlDistrict = By.id("district");
    By loc_ddlWard = By.id("ward");
    By loc_txtAddress2 = By.id("address2");
    By loc_txtCity = By.id("city");
    By loc_txtZipcode = By.id("zipCode");
    By loc_btnCancel = By.cssSelector(".btn-cancel");
    
    public String getEmail() {
    	String value = commonAction.getValue(loc_txtEmail);
    	logger.info("Retrieved Email: " + value);
        return value;
    }
    
    public String getPhoneNumber() {
        String countryCode = commonAction.getText(loc_txtPhoneCode);
        String phoneNumber = commonAction.getValue(loc_txtPhone);
        String value = countryCode + ":" + phoneNumber;
    	logger.info("Retrieved Phone Number: " + value);
    	return value;
    }
    
    public String getBirthday(){
    	String birthday = commonAction.getValue(loc_txtBirthday);
    	logger.info("Retrieved birthday: " + birthday);
    	return birthday;
    }	    
    
    public String getCountry(){
    	logger.info("Getting country...");
        return commonAction.getDropDownSelectedValue(commonAction.getElement(loc_ddlCountry));
    }
    public String getAddress(){
        String address = commonAction.getValue(loc_txtAddress);
        logger.info("Get address: "+address);
        return address;
    }
    public String getCityProvince(){
        String city = commonAction.getDropDownSelectedValue(commonAction.getElement(loc_ddlProvince));
        logger.info("Get city/province: "+city);
        return city;
    }
    public String getDistrict(){
        String district = commonAction.getDropDownSelectedValue(commonAction.getElement(loc_ddlDistrict));
        logger.info("Get district: "+district);
        return district;
    }
    public String getWard(){
        String ward = commonAction.getDropDownSelectedValue(commonAction.getElement(loc_ddlWard));
        logger.info("Get ward: "+ward);
        return ward;
    }
    public String getAddress2(){
        String address2 = commonAction.getValue(loc_txtAddress2);
        logger.info("Get address 2: "+address2);
        return address2;
    }
    public String getInputtedCity(){
        String city = commonAction.getValue(loc_txtCity);
        logger.info("Get inputted city: "+city);
        return city;
    }
    public String getZipCode(){
        String zipCode = commonAction.getValue(loc_txtZipcode);
        logger.info("Get zip code: "+zipCode);
        return zipCode;
    }
    public CustomerDetails verifyCountry(String expected){
        Assert.assertEquals(getCountry(),expected);
        logger.info("Verify country show correctly");
        return this;
    }
    public CustomerDetails verifyAddress(String expected){
        Assert.assertEquals(getAddress(),expected);
        logger.info("Verify address show correctly");
        return this;
    }
    public CustomerDetails verifyCityProvinceState(String expected){
        Assert.assertEquals(getCityProvince(),expected);
        logger.info("Verify city/province/state show correctly");
        return this;
    }
    public CustomerDetails verifyDistrict(String expected){
        Assert.assertEquals(getDistrict(),expected);
        logger.info("Verify district show correctly");
        return this;
    }
    public CustomerDetails verifyWard(String expected){
        Assert.assertEquals(getWard(),expected);
        logger.info("Verify ward show correctly");
        return this;
    }
    public CustomerDetails verifyAddress2(String expected){
        Assert.assertEquals(getAddress2(),expected);
        logger.info("Verify address 2 show correctly");
        return this;
    }
    public CustomerDetails verifyInputtedCity(String expected){
        Assert.assertEquals(getInputtedCity(),expected);
        logger.info("Verify inputted city show correctly");
        return this;
    }
    public CustomerDetails verifyZipCode(String expected){
        Assert.assertEquals(getZipCode(),expected);
        logger.info("Verify zip code show correctly");
        return this;
    }
    public CustomerDetails verifyAddressInfo_VN(String country, String address, String city, String district, String ward){
        commonAction.sleepInMiliSecond(2000);
        if(country!="") {
            verifyCountry(country);
        }
        verifyAddress(address);
        verifyCityProvinceState(city);
        verifyDistrict(district);
        verifyWard(ward);
        return this;
    }
    public CustomerDetails verifyAddressInfo_NonVN(String country, String address, String address2, String state, String city, String zipCode){
        commonAction.sleepInMiliSecond(2000);
        verifyCountry(country);
        verifyAddress(address);
        verifyAddress2(address2);
        verifyCityProvinceState(state);
        verifyInputtedCity(city);
        verifyZipCode(zipCode);
        return this;
    }
    
    public void clickCancelBtn(){
    	commonAction.click(loc_btnCancel);
        logger.info("Clicked on Cancel button");
    }    
}
