package pages.buyerapp.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.buyerapp.account.address.BuyerAddress;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.data.DataGenerator;

import java.time.Duration;

public class BuyerMyProfile extends BuyerMyProfileElement{
    final static Logger logger = LogManager.getLogger(BuyerMyProfile.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public BuyerMyProfile(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
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
//        Assert.assertEquals(common.getText(OTHER_EMAIL_POPUP_TITLE),PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmail.addEmailPopupTitle"));
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
}
