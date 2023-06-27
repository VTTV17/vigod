package pages.buyerapp.account.address;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.account.BuyerMyProfile;
import pages.buyerapp.checkout.CheckoutStep1;
import utilities.UICommonMobile;

public class BuyerAddress {
	final static Logger logger = LogManager.getLogger(BuyerAddress.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public BuyerAddress(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonMobile(driver);
	}

	By COUNTRY_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_country')]");
	By ADDRESS = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_address')]");
	By CITY_PROVINCE = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_city')]");
	By DISTRICT = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_district')]");
	By WARD = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_ward')]");
	By ADDRESS2 = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_address2')]");
	By STATE_REGION_PROVINCE = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_state')]");
	By CITY = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_city_outside_vietnam')]");
	By ZIP_CODE = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_zipcode')]");
	By BACK_ICON = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_toolbar')]/android.widget.ImageButton");
    public String getCountry() {
        String value = commonAction.getText(COUNTRY_DROPDOWN);
        logger.info("Retrieved Country: " + value);
        return value;
    }
	public BuyerAddress selectCountry(String country){
		commonAction.sleepInMiliSecond(1000);
		commonAction.clickElement(COUNTRY_DROPDOWN);
		new BuyerGeneral(driver).searchOnHeader(country);
		return this;
	}
	public BuyerAddress selectCityProvince(String cityProvince){
		commonAction.clickElement(CITY_PROVINCE);
		new BuyerGeneral(driver).searchOnHeader(cityProvince);
		return this;
	}
	public String getCityProvince(){
		return commonAction.getText(CITY_PROVINCE);
	}
	public BuyerAddress selectDistrict(String district){
		commonAction.clickElement(DISTRICT);
		new BuyerGeneral(driver).searchOnHeader(district);
		return this;
	}
	public String getDistrict(){
		return commonAction.getText(DISTRICT);
	}
	public BuyerAddress selectWard(String ward){
		commonAction.clickElement(WARD);
		new BuyerGeneral(driver).searchOnHeader(ward);
		return this;
	}
	public String getWard(){
		return commonAction.getText(WARD);
	}
	public BuyerAddress inputAddress(String address){
		commonAction.inputText(ADDRESS,address);
		logger.info("Input address: "+address);
		return this;
	}
	public String getAddress(){
		return commonAction.getText(ADDRESS);
	}
	public BuyerAddress inputAddress2(String address2){
		commonAction.inputText(ADDRESS2,address2);
		logger.info("Input address2: "+address2);
		return this;
	}
	public String getAddress2(){
		return commonAction.getText(ADDRESS2);
	}
	public BuyerAddress inputCity(String city){
		commonAction.inputText(CITY,city);
		logger.info("Input city: "+city);
		return this;
	}
	public String getCity(){
		return commonAction.getText(CITY);
	}
	public BuyerAddress selectStateRegion(String stateRegion){
		commonAction.clickElement(STATE_REGION_PROVINCE);
		new BuyerGeneral(driver).searchOnHeader(stateRegion);
		return this;
	}
	public String getStateRegion(){
		return commonAction.getText(STATE_REGION_PROVINCE);
	}
	public BuyerAddress inputZipCode(String zipCode){
		commonAction.inputText(ZIP_CODE,zipCode);
		logger.info("Input zipcode: "+zipCode);
		return this;
	}
	public String getZipCode(){
		return commonAction.getText(ZIP_CODE);
	}
	public BuyerAddress verifyCountry(String country){
		Assert.assertEquals(getCountry(),country);
		logger.info("Verify country: "+country);
		return this;
	}
	public BuyerAddress verifyAddress(String address){
		Assert.assertEquals(getAddress(),address);
		logger.info("Verify address: "+address);
		return this;
	}
	public BuyerAddress verifyAddress2(String address2){
		Assert.assertEquals(getAddress2(),address2);
		logger.info("Verify address2: "+address2);
		return this;
	}
	public BuyerAddress verifyCityProvince(String cityProvince){
		Assert.assertEquals(getCityProvince(),cityProvince);
		logger.info("Verify cityProvince: "+cityProvince);
		return this;
	}
	public BuyerAddress verifyCity(String city){
		Assert.assertEquals(getCity(),city);
		logger.info("Verify city: "+city);
		return this;
	}
	public BuyerAddress verifyDistrict(String district){
		Assert.assertEquals(getDistrict(),district);
		logger.info("Verify district: "+district);
		return this;
	}
	public BuyerAddress verifyWard(String ward){
		Assert.assertEquals(getWard(),ward);
		logger.info("Verify ward: "+ward);
		return this;
	}
	public BuyerAddress verifyStateRegion(String stateRegion){
		Assert.assertEquals(getStateRegion(),stateRegion);
		logger.info("Verify stateRegion: "+stateRegion);
		return this;
	}
	public BuyerAddress verifyZipCode(String zipCode){
		Assert.assertEquals(getZipCode(),zipCode);
		logger.info("Verify zipCode: "+zipCode);
		return this;
	}
	public BuyerAddress inputAddressVN(String country, String address, String cityProvince, String district, String ward){
		if(!country.equals(getCountry())){
			selectCountry(country);
		}
		inputAddress(address);
		selectCityProvince(cityProvince);
		selectDistrict(district);
		selectWard(ward);
		return this;
	}
	public BuyerAddress inputAddressNonVN(String country, String address, String address2, String city, String stateRegion, String zipCode){
		if(!country.equals(getCountry())){
			selectCountry(country);
		}
		inputAddress(address);
		inputAddress2(address2);
		inputCity(city);
		selectStateRegion(stateRegion);
		inputZipCode(zipCode);
		return this;
	}
	public BuyerAddress verifyAddressVN(String country, String address, String cityProvince, String district, String ward){
		verifyCountry(country);
		verifyAddress(address);
		verifyCityProvince(cityProvince);
		verifyDistrict(district);
		verifyWard(ward);
		return this;
	}
	public BuyerAddress verifyAddressNonVN(String country, String address, String address2, String city, String stateRegion, String zipCode){
		verifyCountry(country);
		verifyAddress(address);
		verifyAddress2(address2);
		verifyCity(city);
		verifyStateRegion(stateRegion);
		verifyZipCode(zipCode);
		return this;
	}
	public BuyerMyProfile tapOnBackIcon(){
		commonAction.clickElement(BACK_ICON);
		logger.info("Tap on Back icon.");
		return new BuyerMyProfile(driver);
	}

}
