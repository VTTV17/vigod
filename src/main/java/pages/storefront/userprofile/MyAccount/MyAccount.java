package pages.storefront.userprofile.MyAccount;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.storefront.header.HeaderSF;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

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
        String value = commonAction.getElementAttribute(myAccountUI.DISPLAY_NAME, "value");
        logger.info("Retrieved Display Name: " + value);
        return value;
    }

    public String getEmail() {
        String value = commonAction.getElementAttribute(myAccountUI.EMAIL, "value");
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
        String countryCode = commonAction.getElementAttribute(myAccountUI.COUNTRY_CODE_INPUT, "value");
        String phoneNumber = commonAction.getElementAttribute(myAccountUI.PHONE, "value");
        String value = countryCode + ":" + phoneNumber;
        logger.info("Retrieved phone number prefixed with country code: " + value);
        return value;
    }

    public String getBirthday() {
        String value = commonAction.getElementAttribute(myAccountUI.birthday, "value");
        logger.info("Retrieved Birthday: " + value);
        return value;
    }

    public MyAccount inputFullName(String fullName) {
        commonAction.inputText(myAccountUI.DISPLAY_NAME, fullName);
        logger.info("Input fullname: %s".formatted(fullName));
        return this;
    }

    public MyAccount inputPhoneNumber(String phoneNumber) {
        commonAction.inputText(myAccountUI.PHONE, phoneNumber);
        logger.info("Input phone number: %s".formatted(phoneNumber));
        return this;
    }

    /**
     *
     * @param date: format dd/mm/yyyy
     */
    public MyAccount inputBirthday(String date){
        commonAction.inputText(myAccountUI.birthday, date+"\n");
        logger.info("Input birthday: %s".formatted(date));
        return this;
    }

    public MyAccount inputEmail(String email){
        commonAction.inputText(myAccountUI.EMAIL, email);
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
            commonAction.checkTheCheckBoxOrRadio(myAccountUI.MALE_RADIO);
        } else {
            commonAction.checkTheCheckBoxOrRadio(myAccountUI.FEMALE_RADIO);
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
        if (myAccountUI.MALE_RADIO.isSelected()) {
        	gender = commonAction.getText(myAccountUI.MALE_RADIO.findElement(By.xpath("./following-sibling::*")));
        }
        if (myAccountUI.FEMALE_RADIO.isSelected()) {
        	gender = commonAction.getText(myAccountUI.FEMALE_RADIO.findElement(By.xpath("./following-sibling::*")));  
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
        commonAction.clickElement(myAccountUI.SAVE_BTN);
        logger.info("Click on Save button");
        wait.until(ExpectedConditions.visibilityOf(myAccountUI.TOAST_MESSAGE));
        waitTillLoaderDisappear();
        return this;
    }

    public boolean isBirthdayDisabled() {
        try {
            wait.until(ExpectedConditions.visibilityOf(myAccountUI.PICKER)).click();
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
        Assert.assertFalse(myAccountUI.EMAIL.isEnabled());
        logger.info("Verify email is disabled");
        return this;
    }
    public MyAccount verifyBirday(String expected){
        Assert.assertEquals(getBirthday(),expected,"Birthday Actual: %s\nBirthday Expected: %s".formatted(getBirthday(),expected));
        logger.info("Verify birthday after updated");
        return this;
    }
    public MyAccount verifyPhoneDisabled(){
        Assert.assertFalse(myAccountUI.PHONE.isEnabled(),"Actual: Phone number field is not disabled");
        Assert.assertFalse(myAccountUI.COUNTRY_CODE_SELECT.isEnabled(),"Actual: Country code field is not disabled");
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
        for (String phone: phones) {
            commonAction.sleepInMiliSecond(1000);
            waitTillLoaderDisappear();
            commonAction.clickElement(myAccountUI.ADD_OTHER_PHONE_BTN);
            commonAction.inputText(myAccountUI.PHONE_NAME_ADD_OTHER_PHONE, name);
            commonAction.sleepInMiliSecond(2000);
            commonAction.selectByVisibleText(myAccountUI.PHONE_CODE_ADD_OTHER_PHONE, countryPhoneCode);
            commonAction.inputText(myAccountUI.PHONE_NUMBER_ADD_OTHER_PHONE, phone);
            commonAction.clickElement(myAccountUI.SAVE_BTN_ADD_OTHER_PHONE);
        }
        logger.info("Add other phones.");
        return this;
    }
    public MyAccount addOneOtherEmails(String name, String...emails) throws Exception {
        if(emails.length==0){
            throw new  Exception("Email are empty!");
        }
        for (String email: emails) {
            commonAction.clickElement(myAccountUI.ADD_OTHER_EMAIL_BTN);
            commonAction.inputText(myAccountUI.EMAIL_NAME_ADD_OTHER_EMAIL, name);
            commonAction.inputText(myAccountUI.EMAIL_ADD_OTHER_EMAIL, email);
            commonAction.clickElement(myAccountUI.SAVE_BTN_ADD_OTHER_EMAIL);
        }
        logger.info("Add other emails.");
        return this;
    }
    public int getQuantityOfOtherPhone(){
        return myAccountUI.OTHER_PHONE_LIST.size();
    }
    public int getQuantityOfOtherEmail(){
        return myAccountUI.OTHER_EMAIL_LIST.size();
    }
    public MyAccount verifyOtherPhoneListSize(int expected){
        Assert.assertEquals(getQuantityOfOtherPhone(),expected,"Verify Other phone list size");
        return this;
    }
    public MyAccount verifyOtherEmailListSize(int expected){
        Assert.assertEquals(getQuantityOfOtherEmail(),expected,"Verify Other email list size");
        return this;
    }
    public void verifyTextOfMyAccountPage() throws Exception {
        Assert.assertEquals(commonAction.getText(myAccountUI.MY_ACCOUNT_TITLE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.pageTitle"));
        Assert.assertEquals(commonAction.getText(myAccountUI.FULL_NAME_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.fullNameLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.COMPANY_NAME_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.companyNameLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.TAX_CODE_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.taxCodeLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.PHONE_NUMBER_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.phoneNumberLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.OTHER_PHONE_NUMBER_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.otherPhoneNumberLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.EMAIL_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.emailLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.OTHER_EMAIL_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.otherEmailLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.GENDER_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.genderLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.MALE_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.gender.maleLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.FEMALE_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.gender.femaleLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.BIRTHDAY_LBL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.birthdayLbl"));
        Assert.assertEquals(commonAction.getText(myAccountUI.CANCEL_BTN), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.cancelBtn"));
        Assert.assertEquals(commonAction.getText(myAccountUI.SAVE_BTN), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.saveBtn"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.DISPLAY_NAME,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputFullNameHintTxt"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.COMPANY_NAME_INPUT,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputCompanyNameHintTxt"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.TAX_CODE_INPUT,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputTaxCodeHintTxt"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.PHONE,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.inputPhoneNumberHintTxt"));
        commonAction.clickElement(myAccountUI.ADD_OTHER_PHONE_BTN);
        Assert.assertEquals(commonAction.getText(myAccountUI.PHONE_NAME_LBL_ADD_OTHER_PHONE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.nameLbl"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.PHONE_NAME_ADD_OTHER_PHONE,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.inputNameHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.PHONE_NUMBER_LBL_ADD_OTHER_PHONE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.phoneNumberLbl"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.PHONE_NUMBER_ADD_OTHER_PHONE,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.inputPhoneNumberHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.CANCEL_BTN_ADD_OTHER_PHONE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.cancelBtn"));
        Assert.assertEquals(commonAction.getText(myAccountUI.SAVE_BTN_ADD_OTHER_PHONE), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherPhone.saveBtn"));
        commonAction.clickElement(myAccountUI.CANCEL_BTN_ADD_OTHER_PHONE);
        commonAction.clickElement(myAccountUI.ADD_OTHER_EMAIL_BTN);
        Assert.assertEquals(commonAction.getText(myAccountUI.NAME_LBL_ADD_OTHER_EMAIL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.nameLbl"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.EMAIL_NAME_ADD_OTHER_EMAIL,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.inputNameHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.EMAIL_LBL_ADD_OTHER_EMAIL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.phoneNumberLbl"));
        Assert.assertEquals(commonAction.getElementAttribute(myAccountUI.EMAIL_ADD_OTHER_EMAIL,"placeholder"), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.inputPhoneNumberHintTxt"));
        Assert.assertEquals(commonAction.getText(myAccountUI.CANCEL_BTN_ADD_OTHER_EMAIL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.cancelBtn"));
        Assert.assertEquals(commonAction.getText(myAccountUI.SAVE_BTN_ADD_OTHER_EMAIL), PropertiesUtil.getPropertiesValueBySFLang("userProfile.myAccount.addOtherMail.saveBtn"));
    }
}
