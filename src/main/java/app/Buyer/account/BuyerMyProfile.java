package app.Buyer.account;

import api.Seller.login.Login;
import app.Buyer.account.address.BuyerAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import app.Buyer.buyergeneral.BuyerGeneral;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyerMyProfile extends BuyerMyProfileElement{
    final static Logger logger = LogManager.getLogger(BuyerMyProfile.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;
    LoginDashboardInfo loginDashboardInfo;
    LoginInformation sellerLoginInfo;
    public BuyerMyProfile(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    public BuyerMyProfile getLoginInfo(LoginInformation sellerLoginInfo){
        this.sellerLoginInfo = sellerLoginInfo;
        return this;
    }
	public String getDisplayName() {
		String value = common.getText(YOUR_NAME_INPUT);
		logger.info("Retrieved Display Name: " + value);
		return value;
	}

	public String getEmail() {
		String value = common.getText(EMAIL_INPUT);
		logger.info("Retrieved Mail: " + value);
		return value;
	}    

	/**
	 * Retrieves phone number of customers
	 * @return the customer's phone number along with a country code separated by ":". Eg. +84:0841001002
	 */
    public String getPhoneNumber() {
        String country = common.getText(COUNTRY_NAME);
        String phoneNumber = common.getText(PHONE_NUMBER_INPUT);
        String value = new DataGenerator().getPhoneCode(country) + ":" + phoneNumber;
        logger.info("Retrieved phone number prefixed with country code: " + value);
        return value;
    }

    public String getBirthday() {
        String value = common.getText(BIRTHDAY_INPUT);
        logger.info("Retrieved Birthday: " + value);
        return value;
    }	
    
    public BuyerAddress clickAddress() {
    	common.clickElement(ADDRESS_TXT);
    	logger.info("Clicked on Address text box.");
    	return new BuyerAddress(driver);
    }    
    
    public BuyerMyProfile clickChangePassword() {
    	common.clickElement(CHANGE_PASSWORD_LBl);
    	logger.info("Clicked on 'Change Password'.");
    	return this;
    }
    
    public BuyerMyProfile clickNewPassword() {
    	common.clickElement(NEW_CHANGEPASSWORD);
    	logger.info("Clicked on New Password field.");
    	return this;
    }

    public BuyerMyProfile inputCurrentPassword(String password){
        common.inputText(CURRENT_CHANGEPASSWORD, password);
        logger.info("Input '" + password + "' into Current Password field to change password.");
        return this;
    }    
    
    public BuyerMyProfile inputNewPassword(String password){
    	common.inputText(NEW_CHANGEPASSWORD, password);
    	logger.info("Input '" + password + "' into New Password field to change password.");
    	return this;
    }
    
    public BuyerMyProfile clickChangePasswordDoneBtn() {
    	common.clickElement(CHANGEPASSWORD_DONE_BTN);
    	logger.info("Clicked on Done button to change password.");
    	return this;
    }     
    
    public String getCurrentPasswordError(){
    	String text = common.getText(CURRENT_CHANGEPASSWORD_ERROR);
    	logger.info("Retrieved error for Current Password field: " + text);
    	return text;
    }
    
    public String getNewPasswordError(){
    	String text = common.getText(NEW_CHANGEPASSWORD_ERROR);
    	logger.info("Retrieved error for New Password field: " + text);
    	return text;
    }
    
    public BuyerMyProfile verifyTextMyProfile() throws Exception {
        Assert.assertEquals(common.getText(MY_PROFILE_HEADER_TITLE), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.pageTitle"));
        Assert.assertEquals(common.getText(MY_PROFILE_HEADER_SAVE_BTN), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.saveBtn"));
        Assert.assertEquals(common.getText(YOUR_NAME_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.yourNameLbl"));
        Assert.assertEquals(common.getText(EMAIL_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.emailLbl"));
        Assert.assertEquals(common.getText(IDENTITY_CARD_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.identityCardLbl"));
        Assert.assertEquals(common.getText(OTHER_EMAILS_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmailsLbl"));
        verifyTextOtherEmailPage();
        Assert.assertEquals(common.getText(PHONE_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.phoneLbl"));
        common.swipeByCoordinatesInPercent(0.75,0.75,0.25,0.25);
        verifyTextOtherPhonePage();
        Assert.assertEquals(common.getText(COMPANY_NAME_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.companyNameLbl"));
        Assert.assertEquals(common.getText(TAX_CODE_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.taxCodeLbl"));
        Assert.assertEquals(common.getText(PROFILE_ADDRESS_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.addressLbl"));
        Assert.assertEquals(common.getText(GENDER_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.genderLbl"));
        Assert.assertEquals(common.getText(MAN_OPTION), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.gender.man"));
        Assert.assertEquals(common.getText(WOMAN_OPTION), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.gender.woman"));
        Assert.assertEquals(common.getText(BIRTHDAY_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.birthdayLbl"));
        Assert.assertEquals(common.getText(CHANGE_PASSWORD_LBl), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.changePassword"));
        Assert.assertEquals(common.getText(DELETE_ACCOUNT_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.deleteAccount"));
        verifyTextAddressPage();
        return this;
    }
    public void verifyTextOtherEmailPage() throws Exception {
        common.clickElement(YOU_HAVE_OTHER_MAIL_LBL);
        Assert.assertEquals(common.getText(OTHER_EMAIL_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.pageTitle"));
        common.clickElement(OTHER_EMAIL_ADD_ICON);
        Assert.assertEquals(common.getText(OTHER_EMAIL_POPUP_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.addEmailPopupTitle"));
        Assert.assertEquals(common.getText(OTHER_EMAIL_INPUT_NAME),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.enterNameHint"));
        Assert.assertEquals(common.getText(OTHER_EMAIL_INPUT_EMAIL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.enterEmailHint"));
        Assert.assertEquals(common.getText(OTHER_EMAIL_ADD_BTN),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.addBtn"));
        common.clickElement(OTHER_EMAIL_POPUP_CLOSE_ICON);
        common.clickElement(OTHER_EMAIL_BACK_ICON);
    }
    public void verifyTextOtherPhonePage() throws Exception {
        common.clickElement(YOUR_HAVE_OTHER_PHONE_LBL);
        Assert.assertEquals(common.getText(OTHER_PHONE_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.pageTitle"));
        common.clickElement(OTHER_PHONE_ADD_ICON);
        Assert.assertEquals(common.getText(OTHER_PHONE_POPUP_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.addPhonePopupTitle"));
        Assert.assertEquals(common.getText(OTHER_PHONE_INPUT_NAME),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.enterNameHint"));
        Assert.assertEquals(common.getText(OTHER_PHONE_INPUT_PHONE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.enterPhoneHint"));
        Assert.assertEquals(common.getText(OTHER_PHONE_ADD_BTN),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.addBtn"));
        common.clickElement(OTHER_PHONE_POPUP_CLOSE_ICON);
        common.clickElement(OTHER_PHONE_BACK_ICON);
    }
    public void verifyTextAddressPage() throws Exception {
        common.clickElement(ADDRESS_TXT);
        Assert.assertEquals(common.getText(ADDRESS_HEADER_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.pageTitle"));
        Assert.assertEquals(common.getText(ADDRESS_HEADER_SAVE_BTN),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.saveBtn"));
        Assert.assertEquals(common.getText(COUNTRY_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.countryLbl"));
        Assert.assertEquals(common.getText(ADDRESS_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.addressLbl"));
        Assert.assertEquals(common.getText(CITY_PROVINCE_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.cityProvinceLbl"));
        Assert.assertEquals(common.getText(DISTRICT_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.districtLbl"));
        Assert.assertEquals(common.getText(WARD_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.wardLbl"));
        common.clickElement(COUNTRY_DROPDOWN);
        Assert.assertEquals(common.getText(COUNTRY_HEADER_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.country.pageTitle"));
        common.clickElement(COUNTRY_LIST);
        Assert.assertEquals(common.getText(ADDRESS_2_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.address2Lbl"));
        Assert.assertEquals(common.getText(STATE_REGION_PROVINCE_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.stateRegionProviceLbl"));
        Assert.assertEquals(common.getText(CITY_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.cityLbl"));
        Assert.assertEquals(common.getText(ZIP_CODE_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.address.zipCodeLbl"));
    }
    public BuyerMyProfile scrollDown(){
        common.swipeByCoordinatesInPercent(0.75,0.75,0.75,0.25);
        logger.info("Scroll down");
        return new BuyerMyProfile(driver);
    }
    public BuyerMyProfile scrollUp(){
        common.swipeByCoordinatesInPercent(0.25,0.25,0.25,0.75);
        logger.info("Scroll up");
        return new BuyerMyProfile(driver);
    }
    public BuyerMyProfile inputYourName(String yourName){
        common.sleepInMiliSecond(500);
        common.inputText(YOUR_NAME_INPUT,yourName);
        common.sleepInMiliSecond(2000);
        logger.info("Input your name: "+yourName);
        return new BuyerMyProfile(driver);
    }
    public BuyerMyProfile inputEmail(String email){
        common.inputText(EMAIL_INPUT,email);
        logger.info("Input email: "+email);
        return new BuyerMyProfile(driver);
    }
    public BuyerMyProfile inputIdentityCard(String identityCard){
        common.inputText(IDENTITY_CARD_INPUT,identityCard);
        logger.info("Input identity card: "+identityCard);
        return new BuyerMyProfile(driver);
    }
    public BuyerMyProfile inputPhone(String contryCode, String phoneNumber){
        common.clickElement(COUNTRY_NAME);
        common.clickElement(COUNTRY_CODE_HEADER_SEARCH_ICON);
        common.inputText(COUNTRY_CODE_SEARCH_INPUT,contryCode);
        List<WebElement> countryCodeList = common.getElements(COUNTRY_CODE_LIST);
        boolean isClicked = false;
        for (int i = 0;i<countryCodeList.size();i++){
            if(common.getText(countryCodeList.get(i)).equals(contryCode)){
                common.clickElement(countryCodeList.get(i));
                isClicked = true;
                logger.info("Select phone code: "+contryCode);
                break;
            }
        }
        if(!isClicked){
            try {
                throw new Exception("Country code not found.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        common.hideKeyboard("android");
        common.inputText(PHONE_NUMBER_INPUT,phoneNumber);
        logger.info("Input phone number: "+phoneNumber);
        return this;
    }
    public BuyerMyProfile inputCompanyName(String company){
        common.inputText(COMPANY_NAME_INPUT,company);
        logger.info("Input company name: "+company);
        return this;
    }
    public BuyerMyProfile inputTaxCode(String taxCode){
        common.inputText(TAX_CODE_INPUT,taxCode);
        logger.info("Input tax code: "+taxCode);
        return this;
    }
    public BuyerMyProfile selectGender(boolean isSelectMale){
        if (isSelectMale) {
            common.clickElement(MAN_OPTION);
        } else {
            common.clickElement(WOMAN_OPTION);
        }
        return this;
    }
    public BuyerMyProfile verifyBirthdayDisabled(){
        Assert.assertTrue(!common.isElementEnabled(BIRTHDAY_INPUT),"Birthday not disable.");
        logger.info("Verify birthday field disabled.");
        return this;
    }
    public BuyerMyProfile inputBirthday(String birthday){
        common.inputText(BIRTHDAY_INPUT,birthday);
        logger.info("Input birthday: "+birthday);
        return this;
    }
    public BuyerMyProfile selectBirdayAsCurrentDate(){
        common.clickElement(BIRTHDAY_INPUT);
        common.clickElement(DATE_PICKER_OK_BTN);
        logger.info("Select birthday is current date.");
        return this;
    }
    public BuyerMyProfile verifyEmailDisabled(){
        Assert.assertTrue(!common.isElementEnabled(EMAIL_INPUT),"Email not disable.");
        logger.info("Verify email field disabled.");
        return this;
    }
    public BuyerAccountPage tapOnSaveBtn(){
        common.click(MY_PROFILE_HEADER_SAVE_BTN);
        logger.info("Tap on Save button.");
        common.sleepInMiliSecond(1000);
        return new BuyerAccountPage(driver);
    }
    public String getYourName(){
        return common.getText(YOUR_NAME_INPUT);
    }
    public String getIdentityCard(){
        return common.getText(IDENTITY_CARD_INPUT);
    }
    public String getPhone(){
        return common.getText(PHONE_NUMBER_INPUT);
    }
    public String getPhoneCode(){
        return common.getText(PHONE_CODE);
    }
    public String getCompanyName(){
        return common.getText(COMPANY_NAME_INPUT);
    }
    public String getTaxCode(){
        return common.getText(TAX_CODE_INPUT);
    }

    public BuyerMyProfile verifyYourName(String expected){
        common.sleepInMiliSecond(3000);
        Assert.assertEquals(getYourName(),expected);
        logger.info("Verify your name: "+expected);
        return this;
    }
    public BuyerMyProfile verifyEmail(String expected){
        Assert.assertEquals(getEmail(),expected);
        logger.info("Verify email: "+expected);
        return this;
    }
    public BuyerMyProfile verifyIdentityCard(String expected){
        Assert.assertEquals(getIdentityCard(),expected);
        logger.info("Verify identity card: "+expected);
        return this;
    }
    public BuyerMyProfile verifyPhoneCode(String expected){
        Assert.assertEquals(getPhoneCode(),expected);
        logger.info("Verify phone code: "+expected);
        return this;
    }
    public BuyerMyProfile verifyPhoneNumber(String expected){
        Assert.assertEquals(getPhone(),expected);
        logger.info("Verify phone number: "+expected);
        return this;
    }
    public BuyerMyProfile verifyCompanyName(String expected){
        Assert.assertEquals(getCompanyName(),expected);
        logger.info("Verify company name: "+expected);
        return this;
    }
    public BuyerMyProfile verifyTaxCode(String expected){
        Assert.assertEquals(getTaxCode(),expected);
        logger.info("Verify tax code: "+expected);
        return this;
    }
    public BuyerMyProfile verifyBirthday(String expected){
        Assert.assertEquals(getBirthday(),expected);
        logger.info("Verify birthday: "+expected);
        return this;
    }
    public BuyerMyProfile verifyPhoneDisabled(){
        Assert.assertTrue(!common.isElementEnabled(PHONE_NUMBER_INPUT),"Phone field not disable.");
        logger.info("Verify phone disabled.");
        return this;
    }
    public BuyerAccountPage tapOnBackIcon(){
        common.clickElement(HEADER_BACK_ICON);
        logger.info("Tap on back icon.");
        return new BuyerAccountPage(driver);
    }
    public BuyerMyProfile tapOtherPhones(){
        common.clickElement(YOUR_HAVE_OTHER_PHONE_LBL);
        logger.info("Tap on You have x other phones.");
        return this;
    }
    public BuyerMyProfile tapOnAddOtherPhoneIcon(){
        common.clickElement(OTHER_PHONE_ADD_ICON);
        logger.info("Tap on Add other phone icon.");
        return this;
    }
    public BuyerMyProfile inputName_OtherPhone(String name){
        common.inputText(OTHER_PHONE_INPUT_NAME,name);
        logger.info("Input name for Other phone: "+name);
        return this;
    }
    public BuyerMyProfile inputPhone_OtherPhone(String phone){
        common.inputText(OTHER_PHONE_INPUT_PHONE,phone);
        logger.info("Input phone for Other phone: "+phone);
        return this;
    }
    public BuyerMyProfile tapAddButton_OtherPhone(){
        common.clickElement(OTHER_PHONE_ADD_BTN);
        logger.info("Tap on Add button to add other phone.");
        return this;
    }
    public BuyerMyProfile tapBackIcon_OtherPhone(){
        common.clickElement(OTHER_PHONE_BACK_ICON);
        logger.info("Tap Back icon on Other phone page.");
        return this;
    }
    public BuyerMyProfile addOtherPhones(String name,String phoneCode, String...phones){
        if(phones.length==0){
            try {
                throw new  Exception("Phones are empty!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (int i=0;i<phones.length;i++){
            tapOnAddOtherPhoneIcon();
            inputName_OtherPhone(name);
            selectPhoneCodeOnOtherPhone(phoneCode);
            inputPhone_OtherPhone(phones[i]);
            tapAddButton_OtherPhone();
        }
        return this;
    }
    public BuyerMyProfile tapOtherEmails(){
        common.clickElement(YOU_HAVE_OTHER_MAIL_LBL);
        logger.info("Tap on You have x other emails.");
        return this;
    }
    public BuyerMyProfile tapOnAddOtherEmailIcon(){
        common.clickElement(OTHER_EMAIL_ADD_ICON);
        logger.info("Tap on Add other email icon.");
        return this;
    }
    public BuyerMyProfile inputName_OtherEmail(String name){
        common.inputText(OTHER_EMAIL_INPUT_NAME,name);
        logger.info("Input name for Other phone: "+name);
        return this;
    }
    public BuyerMyProfile inputEmail_OtherEmail(String email){
        common.inputText(OTHER_EMAIL_INPUT_EMAIL,email);
        logger.info("Input phone for Other email: "+email);
        return this;
    }
    public BuyerMyProfile tapAddButton_OtherEmail(){
        common.clickElement(OTHER_EMAIL_ADD_BTN);
        logger.info("Tap on Add button to add other email.");
        return this;
    }
    public BuyerMyProfile tapBackIcon_OtherEmail(){
        common.clickElement(OTHER_EMAIL_BACK_ICON);
        logger.info("Tap Back icon on Other phone page.");
        return this;
    }
    public BuyerMyProfile selectPhoneCodeOnOtherPhone(String phoneCode){
        String phoneCodeFormated = "("+phoneCode+")";
        if(!common.getText(OTHER_PHONE_PHONE_CODE).equals(phoneCode)){
            common.clickElement(OTHER_PHONE_PHONE_CODE);
            common.sleepInMiliSecond(1000);
            List<WebElement> phoneCodeList = common.getElements(OTHER_PHONE_PHONE_CODE_LIST);
            boolean isClicked = false;
            for (int i=0;i<phoneCodeList.size();i++){
                if(common.getText(phoneCodeList.get(i)).contains(phoneCodeFormated)){
                    common.clickElement(phoneCodeList.get(i));
                    isClicked = true;
                }
                if(isClicked){
                    break;
                }
            }
            if (!isClicked){
                try {
                    throw new Exception("Phone code %s not found".formatted(phoneCode));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this;
    }
    public BuyerMyProfile addOtherEmails(String name, String...emails){
        if(emails.length==0){
            try {
                throw new  Exception("Phones are empty!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (int i=0;i<emails.length;i++){
            tapOnAddOtherEmailIcon();
            inputName_OtherEmail(name);
            inputEmail_OtherEmail(emails[i]);
            tapAddButton_OtherEmail();
        }
        return this;
    }
    public BuyerMyProfile checkErrorWhenInputOtherPhoneOutOfRange() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        String phoneInvalid = "01"+dataGenerator.generateNumber(5);
        addOtherPhones("Other Phone Invalid","+84",phoneInvalid);
        Assert.assertEquals(common.getText(ADD_OTHER_PHONE_ERROR),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.addOtherPhone.invalidPhoneError"));
        common.clickElement(OTHER_PHONE_POPUP_CLOSE_ICON);
        phoneInvalid = "01"+dataGenerator.generateNumber(14);
        addOtherPhones("Other Phone Invalid","+84",phoneInvalid);
        Assert.assertEquals(common.getText(ADD_OTHER_PHONE_ERROR),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhone.addOtherPhone.invalidPhoneError"));
        common.clickElement(OTHER_PHONE_POPUP_CLOSE_ICON);
        logger.info("Check error when input other phone out of range.");
        common.clickElement(OTHER_PHONE_BACK_ICON);
        return this;
    }
    public BuyerMyProfile checkErrorWhenInputInvalidEmail() throws Exception {
        addOtherEmails("Other email invalid.","test");
        Assert.assertEquals(common.getText(ADD_OTHER_EMAIL_ERROR),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.addOtherEmail.invalidEmailError"));
        common.clickElement(OTHER_EMAIL_POPUP_CLOSE_ICON);
        logger.info("Check error when input invalid enail.");
        common.clickElement(OTHER_EMAIL_BACK_ICON);
        return this;
    }
    public BuyerMyProfile verifyOtherPhoneNumber(int expected) throws Exception {
        Assert.assertEquals(common.getText(YOUR_HAVE_OTHER_PHONE_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.youHaveOtherPhone").formatted(expected));
        logger.info("Verify other phone number.");
        return this;
    }
    public BuyerMyProfile verifyOtherEmailNumber(int expected) throws Exception {
        Assert.assertEquals(common.getText(YOU_HAVE_OTHER_MAIL_LBL),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.youHaveOtherEmailLbl").formatted(expected));
        logger.info("Verify other email number.");
        return this;
    }
    public Map<String,String> getOtherPhonesOrEmailMap(){
        common.sleepInMiliSecond(1000);
        List<WebElement> phoneNumerList = common.getElements(OTHER_PHONE_EMAIL_LIST);
        List<WebElement> phoneNameList = common.getElements(OTHER_PHONE_EMAIL_NAME_LIST);
        Map<String,String> phoneNumberNameMap= new HashMap<>();
        for(int i=0;i<phoneNumerList.size();i++){
            phoneNumberNameMap.put(common.getText(phoneNumerList.get(i)),common.getText(phoneNameList.get(i)));
        }
        logger.info("Get Other phone/email map: "+phoneNumberNameMap);
        return phoneNumberNameMap;
    }
    public BuyerMyProfile verifyOtherPhoneOrEmailMap(Map<String,String> expected){
        Assert.assertEquals(getOtherPhonesOrEmailMap(),expected);
        logger.info("Verify other phone/email and name phone list");
        return this;
    }
    public BuyerMyProfile verifyOtherPhoneAfterAdded(Map<String,String>expected) throws Exception {
        common.sleepInMiliSecond(1000);
        verifyOtherPhoneNumber(expected.size());
        tapOtherPhones();
        verifyOtherPhoneOrEmailMap(expected);
        tapBackIcon_OtherPhone();
        return this;
    }
    public BuyerMyProfile verifyOtherEmailAfterAdded(Map<String,String>expected) throws Exception {
        common.sleepInMiliSecond(1000);
        verifyOtherEmailNumber(expected.size());
        tapOtherEmails();
        verifyOtherPhoneOrEmailMap(expected);
        tapBackIcon_OtherEmail();
        return this;
    }
    public Map<String,String> editOtherPhones(){
        tapOtherPhones();
        common.sleepInMiliSecond(1000);
        Map<String,String> otherPhoneMap = new HashMap<>();
        String[] phoneCodeList = {"+93", "+355", "+376", "+35818", "+244"};
        DataGenerator dataGenerator = new DataGenerator();
        List<WebElement> otherPhoneNumberList = common.getElements(OTHER_PHONE_EMAIL_LIST);
        for(int i=0;i<otherPhoneNumberList.size();i++){
            common.clickElement(otherPhoneNumberList.get(i));
            String phoneNameEdit = "Updated"+i;
            inputName_OtherPhone(phoneNameEdit);
            String phoneCodeEdit = phoneCodeList[dataGenerator.generatNumberInBound(0, phoneCodeList.length)];
            selectPhoneCodeOnOtherPhone(phoneCodeEdit);
            String phoneEdit =  "01" + dataGenerator.generateNumber(8);
            inputPhone_OtherPhone(phoneEdit);
            tapAddButton_OtherPhone();
            otherPhoneMap.put("("+phoneCodeEdit+") "+phoneEdit,phoneNameEdit);
            otherPhoneNumberList = common.getElements(OTHER_PHONE_EMAIL_LIST);
            common.sleepInMiliSecond(500);
        }
        tapBackIcon_OtherPhone();
        logger.info("Edit other phones map: "+otherPhoneMap);
        return otherPhoneMap;
    }
    public Map<String,String> editOtherEmails(){
        tapOtherEmails();
        common.sleepInMiliSecond(1000);
        Map<String,String> otherEmails = new HashMap<>();
        DataGenerator generator = new DataGenerator();
        List<WebElement> otherEmailList = common.getElements(OTHER_PHONE_EMAIL_LIST);
        for(int i=0;i<otherEmailList.size();i++){
            common.clickElement(otherEmailList.get(i));
            String nameEdit = "Updated"+i;
            inputName_OtherEmail(nameEdit);
            String emailEdit = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
            inputEmail_OtherEmail(emailEdit);
            tapAddButton_OtherEmail();
            otherEmails.put(emailEdit,nameEdit);
            otherEmailList = common.getElements(OTHER_PHONE_EMAIL_LIST);
        }
        tapBackIcon_OtherEmail();
        logger.info("Edit other emails map: "+otherEmails);
        return otherEmails;
    }
    public BuyerMyProfile deleteOtherPhoneOtherEmail(){
        common.sleepInMiliSecond(500);
        List<WebElement> otherEmailList = common.getElements(OTHER_PHONE_EMAIL_LIST);
        common.swipeHorizontalInPercent(otherEmailList.get(0),0.7,0.3);
        common.clickElement(OTHER_PHONE_EMAIL_DELETE_ICON);
        return this;
    }
    public BuyerMyProfile deleteAllOtherPhoneEmail(){
        common.sleepInMiliSecond(1000);
        int sizeList = common.getElements(OTHER_PHONE_EMAIL_LIST).size();
        for (int i=0;i<sizeList;i++){
            deleteOtherPhoneOtherEmail();
        }
        return this;
    }
    public BuyerMyProfile addMultipleOtherEmail(int quantity){
        for (int i=0;i<quantity;i++){
            DataGenerator generator = new DataGenerator();
            String email = "email"+generator.generateNumber(10)+"@mailnesia.com";
            addOtherEmails("Other mail"+i,email);
        }
        return this;
    }
    public BuyerMyProfile addMultipleOtherPhone(int quantity){
        for (int i=0;i<quantity;i++){
            DataGenerator generator = new DataGenerator();
            String phone = "01"+generator.generateNumber(8);
            addOtherPhones("Other phone"+i,"+84",phone);
        }
        return this;
    }
    public BuyerMyProfile verifyAddOtherEmailNotShow(){
        Assert.assertTrue(common.isElementNotDisplay(common.getElements(OTHER_EMAIL_INPUT_EMAIL)));
        return this;
    }
    public BuyerMyProfile verifyAddOtherPhoneNotShow(){
        Assert.assertTrue(common.isElementNotDisplay(common.getElements(OTHER_PHONE_INPUT_PHONE)));
        return this;
    }
    public int getOtherEmailNumberFromText(){
        String text = common.getText(YOU_HAVE_OTHER_MAIL_LBL);
        int number = Integer.parseInt(text.replaceAll("\\D", ""));
        logger.info("Get number of other email: "+number);
        return number;
    }
    public int getOtherPhoneNumberFromText(){
        String text = common.getText(YOUR_HAVE_OTHER_PHONE_LBL);
        int number = Integer.parseInt(text.replaceAll("\\D", ""));
        logger.info("Get number of other phone: "+number);
        return number;
    }
    public BuyerMyProfile tapDeleteAccount(){
        common.clickElement(DELETE_ACCOUNT_LBL);
        logger.info("Tap on delete account");
        return this;
    }
    public BuyerMyProfile verifyTextDeleteAccountPopup() throws Exception {
        String storeName = new Login().getInfo(sellerLoginInfo).getStoreName();
        Assert.assertEquals(common.getText(DELETE_ACCOUNT_POPUP_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.deleteAccount.poupTitle"));
        Assert.assertEquals(common.getText(DELETE_ACCOUNT_POPUP_MESSAGE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.deleteAccount.message").formatted(storeName));
        Assert.assertEquals(common.getText(DELETE_ACCOUNT_POPUP_DELETE_BTN),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.deleteAccount.deleteBtn"));
        Assert.assertEquals(common.getText(DELETE_ACCOUNT_POPUP_CANCEL_BTN),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.deleteAccount.cancelBtn"));
        return this;
    }
    public void tapDeleteBTNOnDeletePopup(){
        common.clickElement(DELETE_ACCOUNT_POPUP_DELETE_BTN);
        logger.info("Tap on delete button on Delete popup.");
        new BuyerGeneral(driver).waitLoadingDisapear();
//        common.sleepInMiliSecond(1000);
    }
}
