package pages.dashboard.customers.allcustomers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class CustomerDetails extends HomePage {
	
	final static Logger logger = LogManager.getLogger(CustomerDetails.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public CustomerDetails (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (id = "email")
    WebElement EMAIL;
    
    @FindBy (id = "phone")
    WebElement PHONE;
    @FindBy(id = "country")
    WebElement COUNTRY_DROPDOWN;
    @FindBy (id = "address")
    WebElement ADDRESS_INPUT;
    @FindBy (id = "province")
    WebElement CITY_PROVINCE_DROPDOWN;
    @FindBy (id = "district")
    WebElement DISTRICT_DROPDOWN;
    @FindBy (id = "ward")
    WebElement WARD_DROPDOWN;
    @FindBy (id = "address2")
    WebElement ADDRESS2_INPUT;
    @FindBy (id = "city")
    WebElement CITY_INPUT;
    @FindBy (id = "zipCode")
    WebElement ZIPCODE_INPUT;
    
    public String getEmail() {
    	String value = commonAction.getElementAttribute(wait.until(ExpectedConditions.visibilityOf(EMAIL)), "value");
    	logger.info("Retrieved Email: " + value);
        return value;
    }
    
    public String getPhoneNumber() {
    	String value = commonAction.getText(wait.until(ExpectedConditions.visibilityOf(PHONE)));
    	logger.info("Retrieved Phone Number: " + value);
    	return value;
    }
    
//    public void verifyLoginWithDeletedStaffAccount(String content) {
//        wait.until(ExpectedConditions.visibilityOf(WARNING_POPUP));
//        Assert.assertTrue(WARNING_POPUP.getText().contains(content),
//                "[Login][Deleted Staff Account] No warning popup has been shown");
//    }

    public void completeVerify() {
        soft.assertAll();
    }
    public String getCountry(){
        String country = commonAction.getDropDownSelectedValue(COUNTRY_DROPDOWN);
        logger.info("Get country: "+country);
        return country;
    }
    public String getAddress(){
        String address = commonAction.getElementAttribute(ADDRESS_INPUT,"value");
        logger.info("Get address: "+address);
        return address;
    }
    public String getCityProvince(){
        String city = commonAction.getDropDownSelectedValue(CITY_PROVINCE_DROPDOWN);
        logger.info("Get city/province: "+city);
        return city;
    }
    public String getDistrict(){
        String district = commonAction.getDropDownSelectedValue(DISTRICT_DROPDOWN);
        logger.info("Get district: "+district);
        return district;
    }
    public String getWard(){
        String ward = commonAction.getDropDownSelectedValue(WARD_DROPDOWN);
        logger.info("Get ward: "+ward);
        return ward;
    }
    public String getAddress2(){
        String address2 = commonAction.getElementAttribute(ADDRESS2_INPUT,"value");
        logger.info("Get address 2: "+address2);
        return address2;
    }
    public String getInputtedCity(){
        String city = commonAction.getElementAttribute(CITY_INPUT,"value");
        logger.info("Get inputted city: "+city);
        return city;
    }
    public String getZipCode(){
        String zipCode = commonAction.getElementAttribute(ZIPCODE_INPUT,"value");
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
}
