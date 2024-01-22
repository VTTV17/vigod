package web.StoreFront.userprofile.MyAccount;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import web.StoreFront.header.HeaderSF;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;

public class MyAccount extends HeaderSF {

    final static Logger logger = LogManager.getLogger(MyAccount.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();
    MyAccountElement myAccountUI;

    public MyAccount(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        myAccountUI = new MyAccountElement(driver);
        PageFactory.initElements(driver, this);
    }


    public String getDisplayName() {
        String value = commonAction.getAttribute(myAccountUI.loc_txtFullName, "value");
        logger.info("Retrieved Display Name: " + value);
        return value;
    }

    public String getEmail() {
        String value = commonAction.getAttribute(myAccountUI.loc_txtEmail, "value");
        logger.info("Retrieved Mail: " + value);
        return value.toLowerCase();
    }

	/**
	 * <p>
	 * To retrieve phone number of customers
	 * <p>
	 * @return the customer's phone number along with a country code separated by ":". Eg. +84:0841001002
	 */
    public String getPhoneNumber() {
        String countryCode = commonAction.getAttribute(myAccountUI.loc_lblCountryCodeValue, "value");
        String phoneNumber = commonAction.getAttribute(myAccountUI.loc_txtPhone, "value");
        String value = countryCode + ":" + phoneNumber;
        logger.info("Retrieved phone number prefixed with country code: " + value);
        return value;
    }

    public String getBirthday() {
        String value = commonAction.getAttribute(myAccountUI.loc_txtBirthday, "value");
        logger.info("Retrieved Birthday: " + value);
        return value;
    }

    public MyAccount inputFullName(String fullName) {
        commonAction.sendKeys(myAccountUI.loc_txtFullName, fullName);
        logger.info("Input fullname: %s".formatted(fullName));
        return this;
    }

    public MyAccount inputPhoneNumber(String phoneNumber) {
        commonAction.sendKeys(myAccountUI.loc_txtPhone, phoneNumber);
        logger.info("Input phone number: %s".formatted(phoneNumber));
        return this;
    }

    /**
     *
     * @param date: format dd/mm/yyyy
     */
    public MyAccount inputBirthday(String date){
        commonAction.sendKeys(myAccountUI.loc_txtBirthday, date+"\n");
        logger.info("Input birthday: %s".formatted(date));
        return this;
    }

    public MyAccount inputEmail(String email){
        commonAction.sendKeys(myAccountUI.loc_txtEmail, email);
        logger.info("Input email value: %s".formatted(email));
        return this;
    }    
    
	/**
	 * <p>
	 * To select gender Male/Female
	 * <p>
	 * @param isSelectMale :boolean value of true/false. True represents male and false represents female
	 * @return selected gender - either Male/Female or Nam/Nữ depending on the site's current display language
	 */
    public String selectGender(boolean isSelectMale) {
        if (isSelectMale) {
            commonAction.checkTheCheckBoxOrRadio(myAccountUI.loc_chbMale);
        } else {
            commonAction.checkTheCheckBoxOrRadio(myAccountUI.loc_chbFemale);
        }
        String gender = getGender();
        logger.info("Select gender: %s".formatted(gender));
        return gender;
    }
    
	/**
	 * <p>
	 * To retrieve customer's gender
	 * @return either Male/Female or Nam/Nữ depending on the site's display language or "" if the gender has not been selected
	 */    
    public String getGender() {
    	String gender = "";
        if (commonAction.getElement(myAccountUI.loc_chbMale).isSelected()) {
        	gender = commonAction.getText(myAccountUI.loc_lblMale);
        }
        if (commonAction.getElement(myAccountUI.loc_chbFemale).isSelected()) {
        	gender = commonAction.getText(myAccountUI.loc_lblFemale);
        }
        logger.info("Retrieved gender: %s".formatted(gender));
        return gender;
    }

    public String editGender() {
    	String gender;
        if (getGender().equalsIgnoreCase("male") || getGender().equalsIgnoreCase("nam")) {
            gender = selectGender(false);
        } else {
        	gender = selectGender(true);
        }
        logger.info("Gender is edited to '%s'".formatted(gender));
        return gender;
    }
    public MyAccount clickOnSaveButton(){
        commonAction.click(myAccountUI.loc_btnSave);
        logger.info("Click on Save button");
        wait.until(ExpectedConditions.visibilityOf(commonAction.getElement(myAccountUI.loc_lblToastMessage)));
        waitTillLoaderDisappear();
        commonAction.sleepInMiliSecond(1000);
        return this;
    }

    public boolean isBirthdayDisabled() {
        try {
            wait.until(ExpectedConditions.visibilityOf(commonAction.getElement(myAccountUI.loc_btnBirthday))).click();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    public MyAccount verifyBirthdayDisabled(){
        commonAction.sleepInMiliSecond(1000);
        Assert.assertTrue(isBirthdayDisabled(),"Actual: birthday is enabled");
        logger.info("Birthday field is disabled");
        return this;
    }
    public MyAccount verifyDisplayName( String expected){
        Assert.assertEquals(getDisplayName(),expected);
        logger.info("Verify display name correct after updated");
        return this;
    }
    public MyAccount verifyPhoneNumber(String expected){
        Assert.assertEquals(getPhoneNumber(),expected);
        logger.info("Verify phone correct after updated");
        return this;
    }
    public MyAccount verifyGender( String expected){
        Assert.assertEquals(getGender(),expected);
        logger.info("Verify gender correct after updated");
        return this;
    }
    public MyAccount verifyEmailDisabled(){
        Assert.assertFalse(commonAction.getElement(myAccountUI.loc_txtEmail).isEnabled());
        logger.info("Verify email is disabled");
        return this;
    }
    public MyAccount verifyBirday(String expected){
        Assert.assertEquals(getBirthday(),expected,"Birthday Actual: %s\nBirthday Expected: %s".formatted(getBirthday(),expected));
        logger.info("Verify birthday after updated");
        return this;
    }
    public MyAccount verifyPhoneDisabled(){
        Assert.assertFalse(commonAction.getElement(myAccountUI.loc_txtPhone).isEnabled(),"Actual: Phone number field is not disabled");
        Assert.assertFalse(commonAction.getElement(myAccountUI.loc_ddlCountryCode).isEnabled(),"Actual: Country code field is not disabled");
        logger.info("Verify phone number is disabled");
        return this;
    }
    public MyAccount verifyEmail(String expected){
        Assert.assertEquals(getEmail(),expected.toLowerCase());
        logger.info("Verify email after updated");
        return this;
    }

    public MyAccount addOtherPhones(String name, String countryPhoneCode, String...phones) throws Exception {
        if(phones.length==0){
            throw new  Exception("Phones are empty!");
        }
        commonAction.scrollToTopPage();
        for (String phone: phones) {
            commonAction.sleepInMiliSecond(1000);
            waitTillLoaderDisappear();
            commonAction.click(myAccountUI.loc_btnAddOtherPhone);
            commonAction.sendKeys(myAccountUI.loc_dlgOtherPhone_txtName, name);
            commonAction.sleepInMiliSecond(2000);
            commonAction.selectByVisibleText(commonAction.getElement(myAccountUI.loc_dlgOtherPhone_ddlPhoneCode), countryPhoneCode);
            commonAction.sendKeys(myAccountUI.loc_dlgOtherPhone_txtPhoneNumber, phone);
            commonAction.click(myAccountUI.loc_dlgOtherPhone_btnSave);
        }
        logger.info("Add other phones.");
        return this;
    }
//    public MyAccount addOtherPhones(String name, String countryPhoneCode, int quantity) {
//        commonAction.scrollToTopPage();
//        DataGenerator generate = new DataGenerator();
//        for (int i=0;i<quantity;i++) {
//            String phone = "01" + generate.generateNumber(8);
////            commonAction.sleepInMiliSecond(1000);
//            waitTillLoaderDisappear();
//            commonAction.click(myAccountUI.ADD_OTHER_PHONE_BTN);
//            commonAction.inputText(myAccountUI.PHONE_NAME_ADD_OTHER_PHONE, name);
////            commonAction.sleepInMiliSecond(2000);
//            commonAction.selectByVisibleText(myAccountUI.PHONE_CODE_ADD_OTHER_PHONE, countryPhoneCode);
//            commonAction.inputText(myAccountUI.PHONE_NUMBER_ADD_OTHER_PHONE, phone);
//            commonAction.clickElement(myAccountUI.SAVE_BTN_ADD_OTHER_PHONE);
//        }
//        logger.info("Add other phones.");
//        return this;
//    }
    public MyAccount addOtherEmails(String name, String...emails) throws Exception {
        if(emails.length==0){
            throw new  Exception("Email are empty!");
        }
        for (String email: emails) {
            commonAction.click(myAccountUI.loc_btnAddOtherEmail);
            commonAction.sendKeys(myAccountUI.loc_dlgOtherEmail_txtEmailName, name);
            commonAction.sendKeys(myAccountUI.loc_dlgOtherEmail_txtEmail, email);
            commonAction.click(myAccountUI.loc_dlgOtherEmail_btnSave);
        }
        logger.info("Add other emails.");
        return this;
    }
    public int getQuantityOfOtherPhone(){
        return commonAction.getElements(myAccountUI.loc_lst_lblOtherPhoneNumber).size();
    }
    public int getQuantityOfOtherEmail(){
        return commonAction.getElements(myAccountUI.loc_lst_lblOtherEmail).size();
    }
    public MyAccount verifyOtherPhoneListSize(int expected){
        Assert.assertEquals(getQuantityOfOtherPhone(),expected,"Verify Other phone list size");
        return this;
    }
    public MyAccount verifyOtherEmailListSize(int expected){
        Assert.assertEquals(getQuantityOfOtherEmail(),expected,"Verify Other email list size");
        return this;
    }
    public MyAccount inputCompanyName(String compayName){
        commonAction.sendKeys(myAccountUI.loc_txtCompanyName,compayName);
        return this;
    }
    public MyAccount inputTaxCode(String taxCode){
        commonAction.sendKeys(myAccountUI.loc_txtTaxCode,taxCode);
        return this;
    }
    public MyAccount verifyCompanyName(String expected){
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_txtCompanyName,"value"),expected);
        return this;
    }
    public MyAccount verifyTaxCode(String expected){
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_txtTaxCode,"value"),expected);
        return this;
    }
    public void verifyTextOfMyAccountPage() throws Exception {
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblMyAccount), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.pageTitle"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblFullName), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.fullNameLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblCompanyName), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.companyNameLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblTaxCode), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.taxCodeLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblPhoneNumber), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.phoneNumberLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblOtherPhoneNumber), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.otherPhoneNumberLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblEmail), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.emailLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblOtherEmail), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.otherEmailLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblGender), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.genderLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblMale), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.gender.maleLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblFemale), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.gender.femaleLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblBirthday), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.birthdayLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_btnCancel), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.cancelBtn"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_btnSave), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.saveBtn"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_txtFullName,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputFullNameHintTxt"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_txtCompanyName,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputCompanyNameHintTxt"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_txtTaxCode,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputTaxCodeHintTxt"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_txtPhone,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputPhoneNumberHintTxt"));
        commonAction.click(myAccountUI.loc_btnAddOtherPhone);
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherPhone_lblPhoneName), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.nameLbl"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_dlgOtherPhone_txtName,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.inputNameHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherPhone_lblPhoneNumber), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.phoneNumberLbl"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_dlgOtherPhone_txtPhoneNumber,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.inputPhoneNumberHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherPhone_btnCancel), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.cancelBtn"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherPhone_btnSave), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.saveBtn"));
        commonAction.click(myAccountUI.loc_dlgOtherPhone_btnCancel);
        commonAction.click(myAccountUI.loc_btnAddOtherEmail);
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherEmail_lblName), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.nameLbl"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_dlgOtherEmail_txtEmailName,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.inputNameHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherEmail_lblEmail), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.phoneNumberLbl"));
        Assert.assertEquals(commonAction.getAttribute(myAccountUI.loc_dlgOtherEmail_txtEmail,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.inputPhoneNumberHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherEmail_btnCancel), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.cancelBtn"));
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_dlgOtherEmail_btnSave), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.saveBtn"));
        logger.info("Check text successfully.");
    }
    public MyAccount checkErrorWhenInputOtherPhoneOutOfRange() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        String phoneInvalid = "01"+dataGenerator.generateNumber(5);
        addOtherPhones("Other Phone Invalid","+84",phoneInvalid);
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblAddOtherPhoneError),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.invalidPhoneError"));
        commonAction.click(myAccountUI.loc_dlgOtherPhone_btnCancel);
        phoneInvalid = "01"+dataGenerator.generateNumber(14);
        addOtherPhones("Other Phone Invalid","+84",phoneInvalid);
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblAddOtherPhoneError),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.invalidPhoneError"));
        commonAction.click(myAccountUI.loc_dlgOtherPhone_btnCancel);
        logger.info("Check error when input other phone out of range.");
        return this;
    }
    public MyAccount checkErrorWhenInputOtherPhoneWithExistingValue() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        String phone = "01"+dataGenerator.generateNumber(6);
        addOtherPhones("Other Phone","+84",phone,phone);
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblAddOtherPhoneError),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.existOtherPhoneError"));
        commonAction.click(myAccountUI.loc_dlgOtherPhone_btnCancel);
        logger.info("Check error when input other phone with existing value.");
        return this;
    }
    public MyAccount checkErrorWhenInputInvalidEmail() throws Exception {
        addOtherEmails("Other email invalid.","test");
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblAddOtherEmailError),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.invalidEmailError"));
        commonAction.click(myAccountUI.loc_dlgOtherEmail_btnCancel);
        logger.info("Check error when input invalid enail.");
        return this;
    }
    public MyAccount checkErrorWhenSaveOtherEmailWithBlankField() throws Exception {
        commonAction.click(myAccountUI.loc_btnAddOtherEmail);
        commonAction.click(myAccountUI.loc_dlgOtherEmail_btnSave);
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblAddOtherEmailError),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.emptyError"));
        commonAction.click(myAccountUI.loc_dlgOtherEmail_btnCancel);
        logger.info("Check error when save other email with blank field");
        return this;
    }
    public MyAccount checkErrorWhenSaveOtherPhoneWithBlankField() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();
        String phoneInvalid = "01"+dataGenerator.generateNumber(5);
        addOtherPhones("Other Phone Invalid","+84",phoneInvalid);
        commonAction.sendKeys(myAccountUI.loc_dlgOtherPhone_txtPhoneNumber,"");
        commonAction.click(myAccountUI.loc_dlgOtherPhone_btnSave); //click outside
        Assert.assertEquals(commonAction.getText(myAccountUI.loc_lblAddOtherPhoneError),PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.emptyError"));
        commonAction.click(myAccountUI.loc_dlgOtherPhone_btnCancel);
        logger.info("Check error when save other phone with blank field.");
        return this;
    }
    public Map<String,String> getOtherPhoneMap(){
        Map<String, String> otherPhoneMap = new HashMap<>();
        List<WebElement> otherPhoneElements = commonAction.getElements(myAccountUI.loc_lst_lblOtherPhoneNumber);
        for (int i=0;i<otherPhoneElements.size();i++){
            otherPhoneMap.put(commonAction.getText(otherPhoneElements.get(i)),commonAction.getText(myAccountUI.loc_lst_lblPhoneName,i));
        }
        logger.info("Get other phone map: "+otherPhoneMap);
        return otherPhoneMap;
    }
    public MyAccount verifyOtherPhoneNumber(Map<String,String> actual, Map<String,String>expected){
        Assert.assertEquals(actual,expected);
        logger.info("Verify other phone list.");
        return this;
    }
    public Map<String,String> getOtherEmailMap(){
        Map<String, String> otherPhoneMap = new HashMap<>();
        commonAction.sleepInMiliSecond(1000);
        List<WebElement>otherEmailElements = commonAction.getElements(myAccountUI.loc_lst_lblOtherEmail);
        for (int i=0;i<otherEmailElements.size();i++){
            otherPhoneMap.put(commonAction.getText(otherEmailElements.get(i)),commonAction.getText(myAccountUI.loc_lst_lblOtherEmailName,i));
        }
        logger.info("Get other email map: "+otherPhoneMap);
        return otherPhoneMap;
    }
    public MyAccount verifyOtherEmail(Map<String,String> actual, Map<String,String>expected){
        Assert.assertEquals(actual,expected);
        logger.info("Verify other email list.");
        return this;
    }
    public Map<String,String> editOtherPhoneNumber(){
        Map<String,String> otherPhoneMap = new HashMap<>();
        String[] phoneCodeList = {"+84", "+1", "+95", "+86", "+93", "+35818", "+355"};
        DataGenerator dataGenerator = new DataGenerator();
        commonAction.sleepInMiliSecond(2000);
        List<WebElement> otherPhoneElements = commonAction.getElements(myAccountUI.loc_lst_lblOtherPhoneNumber);
        for(int i=0; i<otherPhoneElements.size();i++){
            commonAction.click(myAccountUI.loc_lst_lblOtherPhoneNumber,i);
            commonAction.sleepInMiliSecond(500);
            String phoneNameEdit = "Updated"+i;
            commonAction.inputText(myAccountUI.loc_dlgOtherPhone_txtName, phoneNameEdit);
            String phoneCodeEdit = phoneCodeList[dataGenerator.generatNumberInBound(0,phoneCodeList.length)];
            String phoneEdit =  "01" + dataGenerator.generateNumber(8);
            commonAction.selectByVisibleText(commonAction.getElement(myAccountUI.loc_dlgOtherPhone_ddlPhoneCode),phoneCodeEdit);
            commonAction.inputText(myAccountUI.loc_dlgOtherPhone_txtPhoneNumber, phoneEdit);
            commonAction.sleepInMiliSecond(500);
            commonAction.click(myAccountUI.loc_dlgOtherPhone_btnSave); //click outside
            commonAction.click(myAccountUI.loc_dlgOtherPhone_btnSave); //click save
            otherPhoneMap.put(phoneCodeEdit+phoneEdit,phoneNameEdit);
        }
        logger.info("Other phone list after edit: "+otherPhoneMap);
        return otherPhoneMap;
    }
    public Map<String,String> editOtherEmail(){
        Map<String,String> otherEmailMap = new HashMap<>();
        DataGenerator dataGenerator = new DataGenerator();
        List<WebElement> otherEmailElements = commonAction.getElements(myAccountUI.loc_lst_lblOtherEmail);
        for(int i=0; i<otherEmailElements.size();i++){
            commonAction.click(myAccountUI.loc_lst_lblOtherEmail,i);
            commonAction.sleepInMiliSecond(500);
            String otherEmail = dataGenerator.generateString(5) + "@mailnesia.com";
            commonAction.sendKeys(myAccountUI.loc_dlgOtherEmail_txtEmailName, "Update email"+i);
            commonAction.sendKeys(myAccountUI.loc_dlgOtherEmail_txtEmail, otherEmail);
            commonAction.sleepInMiliSecond(500);
            commonAction.click(myAccountUI.loc_dlgOtherEmail_btnSave);
            otherEmailMap.put(otherEmail,"Update email"+i);
        }
        logger.info("Other phone list after edit: "+otherEmailMap);
        return otherEmailMap;
    }
    public MyAccount deleteAllOtherPhone(){
        int listSize = commonAction.getElements(myAccountUI.loc_lst_btnDeleteOtherPhone).size();
        for (int i=0;i<listSize;i++){
            commonAction.click(myAccountUI.loc_lst_btnDeleteOtherPhone,0);
        }
        logger.info("Click all delete other phone icon.");
        return this;
    }
    public MyAccount deleteAllOtherEmail(){
        int listSize = commonAction.getElements(myAccountUI.loc_lst_btnDeleteOtherEmail).size();
        for (int i=0;i<listSize;i++){
            commonAction.click(myAccountUI.loc_lst_btnDeleteOtherEmail,0);
        }
        logger.info("Click all delete other email icon.");
        return this;
    }
}
