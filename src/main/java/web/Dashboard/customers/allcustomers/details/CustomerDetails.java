package web.Dashboard.customers.allcustomers.details;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

public class CustomerDetails extends HomePage {
	
	final static Logger logger = LogManager.getLogger(CustomerDetails.class);

    WebDriver driver;
    UICommonAction commonAction;
    CustomerDetailElement elements;
    
    public CustomerDetails (WebDriver driver) {
        super(driver);
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        elements = new CustomerDetailElement();
    }

    public String getEmail() {
    	String value = commonAction.getValue(elements.loc_txtEmail);
    	logger.info("Retrieved Email: " + value);
        return value;
    }
    
    public String getPhoneNumber() {
        String countryCode = commonAction.getText(elements.loc_txtPhoneCode);
        String phoneNumber = commonAction.getValue(elements.loc_txtPhone);
        String value = countryCode + ":" + phoneNumber;
    	logger.info("Retrieved Phone Number: " + value);
    	return value;
    }
    
    public String getBirthday(){
    	String birthday = commonAction.getValue(elements.loc_txtBirthday);
    	logger.info("Retrieved birthday: " + birthday);
    	return birthday;
    }	    
    
    public String getCountry(){
    	logger.info("Getting country...");
        return commonAction.getDropDownSelectedValue(commonAction.getElement(elements.loc_ddlCountry));
    }
    public String getAddress(){
        String address = commonAction.getValue(elements.loc_txtAddress);
        logger.info("Get address: "+address);
        return address;
    }
    public String getCityProvince(){
        String city = commonAction.getDropDownSelectedValue(commonAction.getElement(elements.loc_ddlProvince));
        logger.info("Get city/province: "+city);
        return city;
    }
    public String getDistrict(){
        String district = commonAction.getDropDownSelectedValue(commonAction.getElement(elements.loc_ddlDistrict));
        logger.info("Get district: "+district);
        return district;
    }
    public String getWard(){
        String ward = commonAction.getDropDownSelectedValue(commonAction.getElement(elements.loc_ddlWard));
        logger.info("Get ward: "+ward);
        return ward;
    }
    public String getAddress2(){
        String address2 = commonAction.getValue(elements.loc_txtAddress2);
        logger.info("Get address 2: "+address2);
        return address2;
    }
    public String getInputtedCity(){
        String city = commonAction.getValue(elements.loc_txtCity);
        logger.info("Get inputted city: "+city);
        return city;
    }
    public String getZipCode(){
        String zipCode = commonAction.getValue(elements.loc_txtZipcode);
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

    public void clickGeneralInfoTab(){
    	commonAction.click(elements.loc_tabGeneral);
    	logger.info("Clicked on General Info tab");
    }     
    
    public void clickBankInfoTab(){
    	commonAction.click(elements.loc_tabBank);
    	logger.info("Clicked on bank tab");
    }     
    
    public void inputNote(String note){
    	commonAction.inputText(elements.loc_txtNote, note);
    	logger.info("Input Note: " + note);
    }    
    
    public void inputCompanyName(String company){
    	commonAction.inputText(elements.loc_txtCompany, company);
    	logger.info("Input Company: " + company);
    }    
    
    public void inputBankBranchName(String bankBranch){
    	commonAction.inputText(elements.loc_txtBankBranch, bankBranch);
    	logger.info("Input Bank Branch Name: " + bankBranch);
    }    
    
    public void clickCancelBtn(){
    	commonAction.click(elements.loc_btnCancel);
        logger.info("Clicked on Cancel button");
    }    
    
    public void clickSaveBtn(){
    	commonAction.click(elements.loc_btnSave);
    	logger.info("Clicked on Save button");
    }    
    
    //Updating needed
    public void selectRandomStatus(){
    	commonAction.clickJS(elements.loc_btnStatus);
    	commonAction.clickJS(elements.loc_ddlStatus);
    	logger.info("Clicked on a random status");
    }    
    //Updating needed
    public void selectRandomStaff(){
    	commonAction.click(elements.loc_ddlStaff);
    	commonAction.click(elements.loc_ddlStaffOption);
    	logger.info("Clicked on a random staff");
    }    
    //Updating needed
    public void selectRandomPartner(){
    	for (int i=0; i<5; i++) {
    		commonAction.clickJS(elements.loc_btnAssignPartner);
    		if (isAssignPartnerListExpanded()) break;
    	}
    	commonAction.click(elements.loc_ddlAssignPartner);
    	logger.info("Clicked on a random partner");
    }    
    //Updating needed
    public boolean isAssignPartnerListExpanded(){
    	commonAction.sleepInMiliSecond(500, "Wait a little isAssignPartnerListExpanded");
    	boolean expanded = commonAction.getAttribute(elements.loc_btnAssignPartner, "aria-expanded").equals("true");
    	logger.info("isAssignPartnerListExpanded: " + expanded);
    	return expanded;
    }    
    
    public String getSelectedStatus(){
    	String text = commonAction.getText(elements.loc_ddlStatus);
    	logger.info("Retrieved selected status: " + text);
    	return text;
    }    
    
    public void clickConfirmPaymentBtn(){
    	commonAction.clickActions(elements.loc_btnConfirmPayment);
    	logger.info("Clicked on Confirm Payment button");
    }    
    
    public boolean isPaymentConfirmationDialogDisplayed(){
    	commonAction.sleepInMiliSecond(1000, "Wait for payment confirmation dialog to appear");
    	boolean isDisplayed = !commonAction.getElements(elements.loc_dlgConfirmPayment).isEmpty();
    	logger.info("isPaymentConfirmationDialogDisplayed: " + isDisplayed);
    	return isDisplayed;
    }  
    
    
}
