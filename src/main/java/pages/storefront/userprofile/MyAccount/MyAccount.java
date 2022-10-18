package pages.storefront.userprofile.MyAccount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.storefront.header.HeaderSF;
import utilities.UICommonAction;

import java.time.Duration;

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
        return value;
    }

    public String getPhoneNumber() {
        String countryCode = commonAction.getElementAttribute(myAccountUI.COUNTRY_CODE, "value");
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
        commonAction.inputText(myAccountUI.birthday, date);
        logger.info("Input birthday: %s".formatted(date));
        return this;
    }
    public MyAccount selectGender(boolean isSelectMale) {
        if (isSelectMale) {
            commonAction.checkTheCheckBoxOrRadio(myAccountUI.MALE_RADIO);
            logger.info("Select gender: %s".formatted("Male"));
        } else {
            commonAction.checkTheCheckBoxOrRadio(myAccountUI.FEMALE_RADIO);
            logger.info("Select gender: %s".formatted("Female"));
        }
        return this;
    }
    public MyAccount inputEmail(String email){
        commonAction.inputText(myAccountUI.EMAIL, email);
        logger.info("Input email value: %s".formatted(email));
        return this;
    }
    public String getGender() {
        if (myAccountUI.MALE_RADIO.isSelected()) {
            logger.info("Retrieved gender: Male");
            return "Male";
        }
        if (myAccountUI.FEMALE_RADIO.isSelected()) {
            logger.info("Retrieved gender: Female");
            return "Female";
        }
        logger.info("Retrieved gender: No selected");
        return "No Selected";
    }

    public String editGender() {
        if (getGender().equalsIgnoreCase("male")) {
            selectGender(false);
            logger.info("Edit gender to Female");
            return  "Female";
        } else {
            selectGender(true);
            logger.info("Edit gender to Male");
            return "Male";
        }
    }
    public MyAccount clickOnSaveButton(){
        commonAction.clickElement(myAccountUI.SAVE_BTN);
        logger.info("Click on Save button");
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
        Assert.assertFalse(myAccountUI.COUNTRY_CODE.isEnabled(),"Actual: Country code field is not disabled");
        logger.info("Verify phone number is disabled");
        return this;
    }
    public MyAccount verifyEmail(String expected){
        Assert.assertEquals(getEmail(),expected.toLowerCase());
        logger.info("Verify email after updated");
        return this;
    }
}
