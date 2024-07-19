package app.Buyer.checkout;

import app.Buyer.buyergeneral.BuyerGeneral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.model.dashboard.storefront.AddressInfo;

import java.time.Duration;
import java.util.List;

public class DeliveryAddress extends DeliveryAddressElement {
    final static Logger logger = LogManager.getLogger(DeliveryAddress.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public DeliveryAddress(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }

    public DeliveryAddress tapToSelectAddress() {
        common.click(loc_selectAddress);
        logger.info("Tap on Select address");
        return this;
    }

    public AddressInfo selectRandomAddress() {
        tapToSelectAddress();
        tapResetAddressIfAny();
        AddressInfo addressInfo = new AddressInfo();
        List<WebElement> cityProvinceList = common.getElements(loc_lstcityDistrictWard, 3);
        int cityIndex = new DataGenerator().generatNumberInBound(0, cityProvinceList.size() - 1);
        addressInfo.setCityProvince(common.getText(cityProvinceList.get(cityIndex)));
        common.clickElement(cityProvinceList, cityIndex);

        List<WebElement> districList = common.getElements(loc_lstcityDistrictWard, 3);
        int districIndex = new DataGenerator().generatNumberInBound(0, districList.size() - 1);
        addressInfo.setDistrict(common.getText(districList.get(districIndex)));
        common.clickElement(districList, districIndex);

        List<WebElement> wardList = common.getElements(loc_lstcityDistrictWard, 3);
        int wardIndex = new DataGenerator().generatNumberInBound(0, wardList.size() - 1);
        addressInfo.setWard(common.getText(wardList.get(wardIndex)));
        common.clickElement(wardList,wardIndex);

        return addressInfo;
    }
    public AddressInfo selectRandomState() {
        tapToSelectAddress();
        tapResetAddressIfAny();
        AddressInfo addressInfo = new AddressInfo();
        List<WebElement> stateList = common.getElements(loc_lstcityDistrictWard, 3);
        int stateIndex = new DataGenerator().generatNumberInBound(0, stateList.size() - 1);
        addressInfo.setStateRegionProvince(common.getText(stateList.get(stateIndex)));
        common.clickElement(stateList, stateIndex);
        return addressInfo;
    }

    public void inputAddress(String address) {
        common.inputText(loc_txtAddress, address);
    }

    public void inputAddress2(String address2) {
        common.inputText(loc_txtAddress2, address2);
        logger.info("Input address2: " + address2);
    }
    public void inputCity(String city){
        common.inputText(loc_txtCity,city);
        logger.info("Input city: "+city);
    }
    public void inputZipCode(String zipCode){
        common.inputText(loc_txtZipCode,zipCode);
        logger.info("Input zip code: "+zipCode);
    }
    public AddressInfo inputAndSelectRandomAddressVN() {
        selectCountryVN();
        AddressInfo addressInfo = selectRandomAddress();
        String addressRandom = "Address " + new DataGenerator().generateString(5);
        inputAddress(addressRandom);
        addressInfo.setAddress(addressRandom);
        addressInfo.setCountry(Constant.VIETNAM);
        return addressInfo;
    }

    public AddressInfo inputAndSelectRandomAddressNonVN() {
        String country = selectCountryNonVN();
        AddressInfo addressInfo = selectRandomState();
        String random = new DataGenerator().generateString(5);
        String addressRandom1 = "Address1 " + random;
        String addressRandom2 = "Address2 " + random;
        String city = "City "+random;
        String zipCode = new DataGenerator().randomNumberGeneratedFromEpochTime(10);
        inputAddress(addressRandom1);
        inputAddress2(addressRandom2);
        inputCity(city);
        inputZipCode(zipCode);
        addressInfo.setStreetAddress(addressRandom1);
        addressInfo.setAddress2(addressRandom2);
        addressInfo.setCountry(country);
        addressInfo.setCity(city);
        addressInfo.setZipCode(zipCode);
        return addressInfo;
    }
    public String getCountry() {
        String selectedCountry = common.getText(loc_tvCountry);
        logger.info("Selected country: " + selectedCountry);
        return selectedCountry;
    }

    public String getPhone() {
        String phone = common.getText(loc_txtPhone);
        logger.info("Get phone: " + phone);
        return phone;
    }

    public String getEmail() {
        String email = common.getText(loc_txtEmail);
        logger.info("Get email: " + email);
        return email;
    }

    public String inputRandomPhoneNumber() {
        String phone = "09" + new DataGenerator().generateNumber(8);
        common.inputText(loc_txtPhone, phone);
        logger.info("Input phone number: "+phone);
        return phone;
    }

    public String inputRandomEmail() {
        String email = "automail" + new DataGenerator().randomNumberGeneratedFromEpochTime(8) + "@mailnesia.com";
        common.inputText(loc_txtEmail, email);
        logger.info("Input email: "+email);
        return email;
    }

    public void selectCountryVN() {
        String selectedCountry = getCountry();
        if (!selectedCountry.equals(Constant.VIETNAM)) {
            common.click(loc_tvCountry);
            common.click(common.moveAndGetElementByText("(+84) "+Constant.VIETNAM));
        } else logger.info("Selected country = VN, so no need select country again.");
    }

    public void checkedOnUpdateMyProfile() {
        common.checkTheCheckBoxOrRadio(loc_chkUpdateInMyProfile);
        logger.info("Checked on Also update address in my profile checkbox.");
    }

    public String selectCountryNonVN() {
        common.click(loc_tvCountry);
        common.swipeByCoordinatesInPercent(0.75,0.75,0.75,0.25);
        List<WebElement> countryList = common.getElements(loc_lstCountry);
        int index = new DataGenerator().generatNumberInBound(0, countryList.size() - 1);
        String country = common.getText(countryList.get(index));
//        if (country.contains("(+84)")) index = new DataGenerator().generatNumberInBound(0, countryList.size() - 1);
//        country = common.getText(countryList.get(index));
        country = country.substring(country.indexOf(")") + 1).trim();
        common.click(loc_lstCountry, index);
        return country;
    }

    public String getInputtedAddress() {
        String address = common.getText(loc_txtAddress);
        logger.info("Get Inputted address: " + address);
        return address;
    }

    public String getSelectedAddress() {
        String selectedAddress = common.getText(loc_selectAddress);
        logger.info("Get Selected address: " + selectedAddress);
        return selectedAddress;
    }

    public String getInputtedCity() {
        String inputtedCity = common.getText(loc_txtCity);
        logger.info("Get Inputted city: " + inputtedCity);
        return inputtedCity;
    }

    public String getInputtedAddress2() {
        String inputtedAddress2 = common.getText(loc_txtAddress2);
        logger.info("Get Inputted address2: " + inputtedAddress2);
        return inputtedAddress2;
    }

    public String getInputtedZipCode() {
        String zipCode = common.getText(loc_txtZipCode);
        logger.info("Get Inputted zip code: " + zipCode);
        return zipCode;
    }
    public DeliveryAddress tapResetAddressIfAny(){
        if(common.getElements(loc_btnReset,1).size()>0) {
            common.click(loc_btnReset);
            logger.info("Click on Reset button.");
        }else logger.info("Reset button not show, so no need click Reset");
        return this;
    }
    public String getFullAddressOnEditAddressPage(boolean isVietnam) {
        String address;
        if (isVietnam) {
            address = getInputtedAddress() + ", " + getSelectedAddress() + ", " + getCountry();
        } else address = getInputtedAddress() + ", " + getInputtedAddress2() + ", " +getInputtedCity() + ", "
                + getSelectedAddress() + ", "+getInputtedZipCode() + ", " + getCountry();
        logger.info("Full address on Edit page: " + address);
        return address;
    }
    public String getFullAddressOnMyAddress(){
        String fullAddress = common.getText(loc_myAddress_tvFullAddress);
        logger.info("Full address on My address tab: "+fullAddress);
        return fullAddress;
    }
    public AddressInfo updateDeliveryAddress(boolean isVietnam, boolean isUpdateInProfile) {
        AddressInfo addressInfo ;
        if (!getPhone().matches("\\d+")) inputRandomPhoneNumber();
        if (!getEmail().matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) inputRandomEmail();
        if (isVietnam) {
            addressInfo = inputAndSelectRandomAddressVN();
        } else {
            addressInfo = inputAndSelectRandomAddressNonVN();
        }
        if (isUpdateInProfile) checkedOnUpdateMyProfile();
        new BuyerGeneral(driver).tapRightBtnOnHeader();
        return addressInfo;
    }
    public DeliveryAddress inputPhoneOrEmailIfNeed(){
        if (!getPhone().matches("\\d+")) inputRandomPhoneNumber();
        if (!getEmail().matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) inputRandomEmail();
        common.sleepInMiliSecond(2000);
        new BuyerGeneral(driver).tapRightBtnOnHeader();
        return this;
    }
    public DeliveryAddress verifyAddress(AddressInfo expectedAddressInfo) {
        boolean isVietnam = expectedAddressInfo.getCountry().equals(Constant.VIETNAM);
        String fullAddressActual = getFullAddressOnEditAddressPage(isVietnam);
        String fullAddressExpected = getFullAddressFromAddressInfo(expectedAddressInfo);
        Assert.assertEquals(fullAddressActual,fullAddressExpected);
        logger.info("Verify full address on Edit address screen.");
        return this;
    }
    public DeliveryAddress goToEditMyAddress(){
        common.click(loc_myAddress_lst_btnEdit);
        logger.info("Click on Edit my address.");
        return this;
    }
    public DeliveryAddress verifyFullAddressOnMyAddress(AddressInfo expectedAddressInfo){
        String fullAddressActual  = getFullAddressOnMyAddress();
        String fullAddressExpected = getFullAddressFromAddressInfo(expectedAddressInfo);
        Assert.assertEquals(fullAddressActual,fullAddressExpected);
        logger.info("Verify full address on My address tab");
        return this;
    }
    public String getFullAddressFromAddressInfo(AddressInfo addressInfo){
        boolean isVietnam = addressInfo.getCountry().equals(Constant.VIETNAM);
        String fullAddressExpected;
        if(isVietnam){
            fullAddressExpected = addressInfo.getAddress()+ ", " +addressInfo.getWard()+ ", " + addressInfo.getDistrict()
                    + ", " + addressInfo.getCityProvince()+ ", " + addressInfo.getCountry();
        }else fullAddressExpected = addressInfo.getStreetAddress()+ ", " +addressInfo.getAddress2()+ ", " +addressInfo.getCity()
                + ", " +addressInfo.getStateRegionProvince()+ ", " +addressInfo.getZipCode()+ ", " +addressInfo.getCountry();
        return fullAddressExpected;
    }
}
