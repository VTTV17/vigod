package app.Buyer.account.address;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;

import app.Buyer.account.BuyerMyProfile;
import app.Buyer.buyergeneral.BuyerGeneral;
import utilities.commons.UICommonMobile;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.model.dashboard.storefront.AddressInfo;

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
	String el_lstCountry_cityProvice_district_ward = "//*[ends-with(@resource-id,'item_list_region_name')]";
	By loc_lstCountry_cityProvice_district_ward = By.xpath(el_lstCountry_cityProvice_district_ward);
	By ADDRESS = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_address')]");
	By CITY_PROVINCE = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_city')]");
	By DISTRICT = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_district')]");
	By WARD = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_ward')]");
	By ADDRESS2 = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_address2')]");
	By STATE_REGION_PROVINCE = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_state')]");
	By CITY = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_city_outside_vietnam')]");
	By ZIP_CODE = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_zipcode')]");
	By BACK_ICON = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_toolbar')]/android.widget.ImageButton");
	By HEADER_SAVE_BTN = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_btn_save')]");
    public String getCountry() {
		commonAction.sleepInMiliSecond(500);
        String value = commonAction.getText(COUNTRY_DROPDOWN);
        logger.info("Retrieved Country: " + value);
        return value;
    }
	public String selectCountry(boolean isVietNam){
		String country = getCountry();
		boolean selectedVN = country.equals(Constant.VIETNAM);
		if ((isVietNam && !selectedVN) ||(!isVietNam && selectedVN)) {
			commonAction.sleepInMiliSecond(1000);
			commonAction.clickElement(COUNTRY_DROPDOWN);
			if(isVietNam) {
				new BuyerGeneral(driver).searchOnHeader(Constant.VIETNAM);
				country = Constant.VIETNAM;
			}else {
				country = randomSelectItemInAddressList();
			}
		}
		if(country.equals("")) country = getCountry();
		logger.info("Select country: "+country);
		country = country.substring(country.indexOf(")") + 1).trim();
		return country;
	}
	public String selectCityProvince(){
		commonAction.click(CITY_PROVINCE);
		String cityProvince = randomSelectItemInAddressList();
		if(cityProvince.equals("")) cityProvince = getCityProvince();
		logger.info("Select cityProvince: "+cityProvince);
		return cityProvince;
	}
	public String getCityProvince(){
		return commonAction.getText(CITY_PROVINCE);
	}
	public String selectDistrict(){
		commonAction.click(DISTRICT);
		String district = randomSelectItemInAddressList();
		if(district.equals("")) district = getDistrict();
		logger.info("Select district: "+district);
		return district;
	}
	public String getDistrict(){
		return commonAction.getText(DISTRICT);
	}
	public String selectWard(){
		commonAction.click(WARD);
		String ward = randomSelectItemInAddressList();
		if(ward.equals("")) ward = getWard();
		logger.info("Select ward: "+ward);
		return ward;
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
	public String selectStateRegion(){
		commonAction.clickElement(STATE_REGION_PROVINCE);
		String state = randomSelectItemInAddressList();
		if(state.equals("")) state = getStateRegion();
		return state;
	}
	public String getStateRegion(){
		return commonAction.getText(STATE_REGION_PROVINCE);
	}
	public BuyerAddress inputZipCode(String zipCode){
		commonAction.inputText(ZIP_CODE,zipCode);
		logger.info("Input zipcode: "+zipCode);
		return this;
	}


	public String randomSelectItemInAddressList(){
		List<WebElement> list = commonAction.getElements(loc_lstCountry_cityProvice_district_ward,7);
		if(list.isEmpty()) {
			new BuyerGeneral(driver).tapCloseIconOnHeader();
			logger.info("List empty, so no need select"); //4.5 state = other, state list empty
			return "";
		}
		System.out.println("SIZE: "+list.size());
		int index = new DataGenerator().generatNumberInBound(0, list.size() - 1);
		String selectIconEl ="("+el_lstCountry_cityProvice_district_ward+")[%s]".formatted(index)+"/following-sibling::*";
		boolean selectedBefore = commonAction.getElements(By.xpath(selectIconEl),2).size()==1;
		if(selectedBefore)  {
			new BuyerGeneral(driver).tapCloseIconOnHeader();
			logger.info("Random value is selected before, so no need select.");
			return "";
		}
		String text = commonAction.getText(list.get(index));
		commonAction.clickElement(list,index);
		return text;
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
		Assert.assertTrue(getDistrict().contains(district));
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
	public AddressInfo updateRandomAddress(boolean isVietnam){
		AddressInfo addressInfo = new AddressInfo();
		String country = selectCountry(isVietnam);
		String addressRandom = "Address " + new DataGenerator().generateString(5);
		inputAddress(addressRandom);
		addressInfo.setCountry(country);
		if(country.equals(Constant.VIETNAM)){
			addressInfo.setAddress(addressRandom);
			addressInfo.setCityProvince(selectCityProvince());
			addressInfo.setDistrict(selectDistrict());
			addressInfo.setWard(selectWard());
		}else {
			addressInfo.setStreetAddress(addressRandom);
			String address2 = "Address2 "+ new DataGenerator().generateString(5);
			inputAddress2(address2);
			addressInfo.setAddress2(address2);
			addressInfo.setStateRegionProvince(selectStateRegion());
			String city = "city "+ new DataGenerator().generateString(5);
			inputCity(city);
			addressInfo.setCity(city);
			String zipCode = new DataGenerator().generateNumber(7);
			inputZipCode(zipCode);
			addressInfo.setZipCode(zipCode);
		}
		tapOnSaveBtn();
		new BuyerGeneral(driver).waitLoadingDisapear();
		return addressInfo;
	}
	public BuyerAddress verifyAddress(AddressInfo addressInfoExpected){
		commonAction.sleepInMiliSecond(2000);
		Assert.assertEquals(getAddressInfo(),addressInfoExpected);
		return this;
	}
	public BuyerMyProfile tapOnBackIcon(){
		commonAction.clickElement(BACK_ICON);
		logger.info("Tap on Back icon.");
		return new BuyerMyProfile(driver);
	}
	public BuyerMyProfile tapOnSaveBtn(){
		commonAction.clickElement(HEADER_SAVE_BTN);
		logger.info("Tap on Save button.");
		commonAction.sleepInMiliSecond(1000);
		return new BuyerMyProfile(driver);
	}
	public AddressInfo getAddressInfo(){
		AddressInfo addressInfo = new AddressInfo();
		addressInfo.setCountry(getCountry());
		boolean isVietNam = getCountry().equals(Constant.VIETNAM);
		if(isVietNam){
			addressInfo.setAddress(getAddress());
			addressInfo.setCityProvince(getCityProvince());
			addressInfo.setDistrict(getDistrict());
			addressInfo.setWard(getWard());
		}else {
			addressInfo.setStreetAddress(getAddress());
			addressInfo.setAddress2(getAddress2());
			addressInfo.setStateRegionProvince(getStateRegion());
			addressInfo.setCity(getCity());
			addressInfo.setZipCode(getZipCode());
		}
		return addressInfo;
	}
}
