package app.GoSeller.customer.cru_customer;

import app.Buyer.account.BuyerMyProfile;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.GoSeller.customer.customer_list.CustomerListScreen;
import app.GoSeller.general.SellerGeneral;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.utils.PropertiesUtil;

import java.time.Duration;
import java.util.List;

public class ViewCreateUpdateCustomer extends ViewCreateUpdateCustomerElement {
    final static Logger logger = LogManager.getLogger(ViewCreateUpdateCustomer.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;
    DataGenerator generator;

    public ViewCreateUpdateCustomer (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
        generator = new DataGenerator();
    }
    public ViewCreateUpdateCustomer inputFullName(String fullName){
        common.inputText(loc_txtFullName, fullName);
        return this;
    }
    public String inputFullName(){
        String fullName = generator.generateString(10);
        inputFullName(fullName);
        return fullName;
    }
    public String getFullName(){
        String fullName = common.getText(loc_txtFullName);
        logger.info("Retrived fullname: "+fullName);
        return fullName;
    }
    public ViewCreateUpdateCustomer inputPhoneNumber(String phoneNumber){
        common.inputText(loc_txtPhoneNumber, phoneNumber);
        return this;
    }
    public String inputPhoneNumber(){
        String phoneNumber = "09"+ generator.generateNumber(8);
        inputPhoneNumber(phoneNumber);
        return phoneNumber;
    }
    public String getPhoneNumber(){
        String phone = common.getText(loc_txtPhoneNumber);
        logger.info("Retrived phone: "+phone);
        return phone;
    }
    public ViewCreateUpdateCustomer inputEmail(String email){
        common.inputText(loc_txtEmail, email);
        return this;
    }
    public String inputEmail(){
        String email = generator.generateString(10)+"@mailnesia.com";
        inputEmail(email);
        return email;
    }
    public String getEmail(){
        String email = common.getText(loc_txtEmail);
        logger.info("Retrived email: "+email);
        return email;
    }
    public String getCountry() {
        String value = common.getText(loc_ddlCountry);
        logger.info("Retrieved Country: " + value);
        return value;
    }
    public String getCountryInAddressScreen() {
        String value = common.getText(loc_address_ddlCountry);
        logger.info("Retrieved Country: " + value);
        return value;
    }
    public String selectCountry(boolean isVietNam){
        String country = getCountry();
        boolean selectedVN = country.equals(Constant.VIETNAM);
        if ((isVietNam && !selectedVN) ||(!isVietNam && selectedVN)) {
            common.sleepInMiliSecond(1000);
            common.clickElement(loc_ddlCountry);
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
        common.click(loc_address_ddlCityProvince);
        String cityProvince = randomSelectItemInAddressList();
        if(cityProvince.equals("")) cityProvince = getCityProvince();
        logger.info("Select cityProvince: "+cityProvince);
        return cityProvince;
    }
    public String getCityProvince(){
        return common.getText(loc_address_ddlCityProvince);
    }
    public String selectDistrict(){
        common.click(loc_address_ddlDistrict);
        String district = randomSelectItemInAddressList();
        if(district.equals("")) district = getDistrict();
        logger.info("Select district: "+district);
        return district;
    }
    public String getDistrict(){
        return common.getText(loc_address_ddlDistrict);
    }
    public String selectWard(){
        common.click(loc_address_ddlWard);
        String ward = randomSelectItemInAddressList();
        if(ward.equals("")) ward = getWard();
        logger.info("Select ward: "+ward);
        return ward;
    }
    public String getWard(){
        return common.getText(loc_address_ddlWard);
    }
    public ViewCreateUpdateCustomer inputAddress(String address){
        common.inputText(loc_address_txtAddress,address);
        logger.info("Input address: "+address);
        return this;
    }
    public String inputAddress(){
        String address = generator.generateString(10);
        inputAddress(address);
        return address;
    }
    public String getAddress(){
        return common.getText(loc_address_txtAddress);
    }
    public ViewCreateUpdateCustomer inputAddress2(String address2){
        common.inputText(loc_address_txtAddress2, address2);
        logger.info("Input address2: "+address2);
        return this;
    }
    public String inputAddress2(){
        String address2 = generator.generateString(10);
        inputAddress2(address2);
        return address2;
    }
    public String getAddress2(){
        return common.getText(loc_address_txtAddress2);
    }
    public ViewCreateUpdateCustomer inputCity(String city){
        common.inputText(loc_address_txtCity,city);
        logger.info("Input city: "+city);
        return this;
    }
    public String inputCity(){
        String city = generator.generateString(10);
        inputCity(city);
        return city;
    }
    public String getCity(){
        return common.getText(loc_address_txtCity);
    }
    public String selectStateRegion(){
        common.clickElement(loc_address_ddlState);
        String state = randomSelectItemInAddressList();
        if(state.equals("")) state = getStateRegion();
        return state;
    }
    public String getStateRegion(){
        return common.getText(loc_address_ddlState);
    }
    public ViewCreateUpdateCustomer inputZipCode(String zipCode){
        common.inputText(loc_address_txtZipCode,zipCode);
        logger.info("Input zipcode: "+zipCode);
        return this;
    }
    public String inputZipCode(){
        String zipCode = generator.generateNumber(10);
        inputZipCode(zipCode);
        return zipCode;
    }
    public String randomSelectItemInAddressList(){
        List<WebElement> list = common.getElements(loc_lstCountry_cityProvice_district_ward,7);
        if(list.isEmpty()) {
            new BuyerGeneral(driver).tapCloseIconOnHeader();
            logger.info("List empty, so no need select"); //4.5 state = other, state list empty
            return "";
        }
        System.out.println("SIZE: "+list.size());
        int index = list.size()>1 ? new DataGenerator().generatNumberInBound(1, list.size() - 1) : 0;
        String selectIconEl ="("+el_lstCountry_cityProvice_district_ward+")[%s]".formatted(index)+"/following-sibling::*";
        System.out.println("selectIconEl: "+selectIconEl);
        boolean selectedBefore = common.getElements(By.xpath(selectIconEl),2).size()==1;
        if(selectedBefore)  {
            new BuyerGeneral(driver).tapCloseIconOnHeader();
            logger.info("Random value is selected before, so no need select.");
            return "";
        }
        String text = common.getText(list.get(index));
        common.clickElement(list,index);
        return text;
    }
    public String getZipCode(){
        return common.getText(loc_address_txtZipCode);
    }
    public ViewCreateUpdateCustomer verifyCountry(String country){
        Assert.assertEquals(getCountry(),country);
        logger.info("Verify country: "+country);
        return this;
    }
    public ViewCreateUpdateCustomer clickFullAddress(){
        common.click(loc_Address);
        logger.info("Tap on Full address field.");
        return this;
    }
    public ViewCreateUpdateCustomer clickSaveOnAddressScreen(){
        common.click(loc_address_btnSave);
        logger.info("Tap on Save button on Address screen.");
        return this;
    }
    public UICreateCustomerData createCustomer(boolean isVietNam) {
        UICreateCustomerData data = new UICreateCustomerData();
        data.setName(inputFullName());
        data.setPhone(inputPhoneNumber());
        data.setEmail(inputEmail());
        if(!isVietNam) data.setCountry(selectCountry(false));
        clickFullAddress();
        data.setAddress(inputAddress());
        if(!isVietNam){
            data.setAddress2(inputAddress2());
            data.setProvince(selectStateRegion());
            data.setCity(inputCity());
            data.setZipCode(inputZipCode());
        }else {
            data.setProvince(selectCityProvince());
            data.setDistrict(selectDistrict());
            data.setWard(selectWard());
        }
        clickSaveOnAddressScreen();
        new SellerGeneral(driver).tapHeaderRightIcon();
        return data;
    }
    public UICreateCustomerData getCustomerInfo(boolean isVietNam){
        UICreateCustomerData data = new UICreateCustomerData();
        data.setName(getFullName());
        data.setPhone(getPhoneNumber());
        scrollDown();
        data.setEmail(getEmail());
        clickFullAddress();
        data.setAddress(getAddress());
        if(!isVietNam){
            data.setCountry(getCountryInAddressScreen());
            data.setAddress2(getAddress2());
            data.setProvince(getStateRegion());
            data.setCity(getCity());
            data.setZipCode(getZipCode());
        }else {
            data.setProvince(getCityProvince());
            data.setDistrict(getDistrict());
            data.setWard(getWard());
        }
        return data;
    }
    public void verifyCustomerInfo(UICreateCustomerData customerInfoExpected, UICreateCustomerData customerInfoActual){
        Assert.assertEquals(customerInfoExpected, customerInfoActual, "Customer info.");
        System.out.println("customerInfoExpected: "+customerInfoExpected);
        System.out.println("customerInfoExpected: "+customerInfoActual);
    }
    @SneakyThrows
    public CustomerListScreen verifyCreateSuccessMessage(){
        new SellerGeneral(driver).verifyToastMessage(PropertiesUtil.getPropertiesValueByDBLang("customers.create.successMessage"));
        return new CustomerListScreen(driver);
    }
    public ViewCreateUpdateCustomer scrollDown(){
        common.swipeByCoordinatesInPercent(0.75,0.75,0.75,0.45);
        logger.info("Scroll down");
        return new ViewCreateUpdateCustomer(driver);
    }
}
