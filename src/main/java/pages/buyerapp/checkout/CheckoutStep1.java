package pages.buyerapp.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.account.BuyerMyProfile;
import utilities.UICommonMobile;

import java.time.Duration;

public class CheckoutStep1 {
    final static Logger logger = LogManager.getLogger(CheckoutStep1.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;


    public CheckoutStep1(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By PAYMENT_VISA = By.xpath("//*[ends-with(@resource-id,'fragment_page_shopping_cart_payment_method_chkbox_credit')]");
    By PAYMENT_ATM = By.xpath("//*[ends-with(@resource-id,'fragment_page_shopping_cart_payment_method_chkbox_atm')]");
    By PAYMENT_COD = By.xpath("//*[ends-with(@resource-id,'fragment_page_shopping_cart_payment_method_chkbox_atm')]");
    By COUNTRY = By.xpath("//*[ends-with(@resource-id,'edtCountry')]");
    By NAME = By.xpath("//*[ends-with(@resource-id,'edtName')]");
    By PHONE_CODE = By.xpath("//*[ends-with(@resource-id,'tvPhoneCode')]");
    By PHONE_NUMBER = By.xpath("//*[ends-with(@resource-id,'edtPhoneNumber')]");
    By EMAIL = By.xpath("//*[ends-with(@resource-id,'edtEmail')]");
    By ADDRESS = By.xpath("//*[ends-with(@resource-id,'edtAddress1')]");
    By CITY_PROVINCE = By.xpath("//*[ends-with(@resource-id,'edtCityInsideVN')]");
    By DISTRICT = By.xpath("//*[ends-with(@resource-id,'edtDistrict')]");
    By WARD = By.xpath("//*[ends-with(@resource-id,'edtWard')]");
    By ADDRESS2 = By.xpath("//*[ends-with(@resource-id,'edtAddress2')]");
    By CITY = By.xpath("//*[ends-with(@resource-id,'edtCityOutsideVN')]");
    By STATE_REGION_PROVINCE = By.xpath("//*[ends-with(@resource-id,'edtState')]");
    By ZIP_CODE = By.xpath("//*[ends-with(@resource-id,'edtZipCode')]");
    By NEXT_BTN = By.xpath("//*[ends-with(@resource-id,'activity_shopping_cart_checkout_btn_next_or_confirm')]");

    /**
     *
     * @param paymentMethod: COD, ATM, VISA
     * @return CheckoutStep1
     */
    public CheckoutStep1 selectPaymentMethod(String paymentMethod){
        switch (paymentMethod){
            case "COD" -> common.clickElement(PAYMENT_COD);
            case "ATM" -> common.clickElement(PAYMENT_ATM);
            case "VISA" -> common.clickElement(PAYMENT_VISA);
            default -> {
                try {
                    throw new Exception("Payment method not found.");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this;
    }
    public CheckoutStep1 selectCountry(String country){
        common.sleepInMiliSecond(1000);
        common.clickElement(COUNTRY);
        new BuyerGeneral(driver).searchOnHeader(country);
        return this;
    }
    public String getCountry(){
        return common.getText(COUNTRY);
    }
    public CheckoutStep1 inputName(String name){
        common.inputText(NAME,name);
        logger.info("Input name: "+name);
        return this;
    }
    public CheckoutStep1 inputPhone(String phoneCode, String phoneNumber){
        String phoneCodeCurrent = common.getText(PHONE_CODE);
        String phoneCodeExpected = "(" +phoneCode+ ")";
        if(!phoneCodeCurrent.equals(phoneCodeExpected)){
            common.clickElement(PHONE_CODE);
            new BuyerGeneral(driver).searchOnHeader(phoneCodeExpected);
        }
        common.hideKeyboard("android");
        common.inputText(PHONE_NUMBER,phoneNumber);
        return this;
    }
    public String getEmail(){
        return common.getText(EMAIL);
    }
    public CheckoutStep1 inputEmail(String email){
        common.inputText(EMAIL,email);
        logger.info("Input email: "+email);
        return this;
    }
    public CheckoutStep1 selectCityProvince(String cityProvince){
        common.clickElement(CITY_PROVINCE);
        new BuyerGeneral(driver).searchOnHeader(cityProvince);
        return this;
    }
    public String getCityProvince(){
        return common.getText(CITY_PROVINCE);
    }
    public CheckoutStep1 selectDistrict(String district){
        common.clickElement(DISTRICT);
        new BuyerGeneral(driver).searchOnHeader(district);
        return this;
    }
    public String getDistrict(){
        return common.getText(DISTRICT);
    }
    public CheckoutStep1 selectWard(String ward){
        common.clickElement(WARD);
        new BuyerGeneral(driver).searchOnHeader(ward);
        return this;
    }
    public String getWard(){
        return common.getText(WARD);
    }
    public CheckoutStep1 inputAddress(String address){
        common.inputText(ADDRESS,address);
        logger.info("Input address: "+address);
        return this;
    }
    public String getAddress(){
        return common.getText(ADDRESS);
    }
    public CheckoutStep1 inputAddress2(String address2){
        common.inputText(ADDRESS2,address2);
        logger.info("Input address2: "+address2);
        return this;
    }
    public String getAddress2(){
        return common.getText(ADDRESS2);
    }
    public CheckoutStep1 inputCity(String city){
        common.inputText(CITY,city);
        logger.info("Input city: "+city);
        return this;
    }
    public String getCity(){
        return common.getText(CITY);
    }
    public CheckoutStep1 selectStateRegion(String stateRegion){
        common.clickElement(STATE_REGION_PROVINCE);
        new BuyerGeneral(driver).searchOnHeader(stateRegion);
        return this;
    }
    public String getStateRegion(){
        return common.getText(STATE_REGION_PROVINCE);
    }
    public CheckoutStep1 inputZipCode(String zipCode){
        common.inputText(ZIP_CODE,zipCode);
        logger.info("Input zipcode: "+zipCode);
        return this;
    }
    public String getZipCode(){
        return common.getText(ZIP_CODE);
    }
    public CheckoutStep1 verifyCountry(String country){
        Assert.assertEquals(getCountry(),country);
        logger.info("Verify country: "+country);
        return this;
    }
    public CheckoutStep1 verifyAddress(String address){
        Assert.assertEquals(getAddress(),address);
        logger.info("Verify address: "+address);
        return this;
    }
    public CheckoutStep1 verifyAddress2(String address2){
        Assert.assertEquals(getAddress2(),address2);
        logger.info("Verify address2: "+address2);
        return this;
    }
    public CheckoutStep1 verifyCityProvince(String cityProvince){
        Assert.assertEquals(getCityProvince(),cityProvince);
        logger.info("Verify cityProvince: "+cityProvince);
        return this;
    }
    public CheckoutStep1 verifyCity(String city){
        Assert.assertEquals(getCity(),city);
        logger.info("Verify city: "+city);
        return this;
    }
    public CheckoutStep1 verifyDistrict(String district){
        Assert.assertTrue(getDistrict().contains(district));
        logger.info("Verify district: "+district);
        return this;
    }
    public CheckoutStep1 verifyWard(String ward){
        Assert.assertEquals(getWard(),ward);
        logger.info("Verify ward: "+ward);
        return this;
    }
    public CheckoutStep1 verifyStateRegion(String stateRegion){
        Assert.assertEquals(getStateRegion(),stateRegion);
        logger.info("Verify stateRegion: "+stateRegion);
        return this;
    }
    public CheckoutStep1 verifyZipCode(String zipCode){
        Assert.assertEquals(getZipCode(),zipCode);
        logger.info("Verify zipCode: "+zipCode);
        return this;
    }
    public CheckoutStep1 inputAddressVN(String country, String address, String cityProvince, String district, String ward){
        if(!country.equals(getCountry()) && !country.equals("")){
            selectCountry(country);
        }
        inputAddress(address);
        selectCityProvince(cityProvince);
        selectDistrict(district);
        selectWard(ward);
        return this;
    }
    public CheckoutStep1 inputAddressNonVN(String country, String address, String address2, String city, String stateRegion, String zipCode){
        if(!country.equals(getCountry())){
            selectCountry(country);
        }
        inputAddress(address);
        inputAddress2(address2);
        inputCity(city);
        scrollDown();
        selectStateRegion(stateRegion);
        inputZipCode(zipCode);
        return this;
    }
    public CheckoutStep1 verifyAddressVN(String country, String address, String cityProvince, String district, String ward){
        common.sleepInMiliSecond(1000);
        verifyCountry(country);
        verifyAddress(address);
        verifyCityProvince(cityProvince);
        verifyDistrict(district);
        verifyWard(ward);
        return this;
    }
    public CheckoutStep1 verifyAddressNonVN(String country, String address, String address2, String city, String stateRegion, String zipCode){
        common.sleepInMiliSecond(1500);
        verifyCountry(country);
        scrollDown();
        verifyAddress(address);
        verifyAddress2(address2);
        verifyCity(city);
        verifyStateRegion(stateRegion);
        verifyZipCode(zipCode);
        return this;
    }
    public CheckoutStep2 tapOnContinueBtn(){
        new BuyerGeneral(driver).tapOnContinueBtn_Checkout();
        return new CheckoutStep2(driver);
    }
    public CheckoutStep1 scrollDown(){
        common.swipeByCoordinatesInPercent(0.75,0.75,0.25,0.25);
        logger.info("Scroll down");
        return new CheckoutStep1(driver);
    }
}
