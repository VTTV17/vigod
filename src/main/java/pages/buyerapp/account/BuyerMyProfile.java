package pages.buyerapp.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;

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
    public BuyerMyProfile verifyText() throws Exception {
        Assert.assertEquals(common.getText(MY_PROFILE_HEADER_TITLE), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.pageTitle"));
        Assert.assertEquals(common.getText(MY_PROFILE_HEADER_SAVE_BTN), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.saveBtn"));
        Assert.assertEquals(common.getText(YOUR_NAME_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.yourNameLbl"));
        Assert.assertEquals(common.getText(EMAIL_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.emailLbl"));
        Assert.assertEquals(common.getText(IDENTITY_CARD_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.identityCardLbl"));
        Assert.assertEquals(common.getText(OTHER_EMAILS_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherEmailsLbl"));
        Assert.assertEquals(common.getText(PHONE_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.phoneLbl"));
        Assert.assertEquals(common.getText(OTHER_PHONE_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.otherPhoneNumbersLbl"));
        Assert.assertEquals(common.getText(COMPANY_NAME_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.companyNameLbl"));
        Assert.assertEquals(common.getText(PHONE_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.phoneLbl"));
        common.swipeByCoordinatesInPercent(0.75,0.75,0.25,0.25);
        Assert.assertEquals(common.getText(TAX_CODE_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.taxCodeLbl"));
        Assert.assertEquals(common.getText(PROFILE_ADDRESS_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.addressLbl"));
        Assert.assertEquals(common.getText(GENDER_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.genderLbl"));
        Assert.assertEquals(common.getText(MAN_OPTION), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.gender.man"));
        Assert.assertEquals(common.getText(WOMAN_OPTION), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.gender.woman"));
        Assert.assertEquals(common.getText(BIRTHDAY_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.birthdayLbl"));
        Assert.assertEquals(common.getText(CHANGE_PASSWORD_LBl), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.changePassword"));
        Assert.assertEquals(common.getText(DELETE_ACCOUNT_LBL), PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.deleteAccount"));
        return this;
    }
}
