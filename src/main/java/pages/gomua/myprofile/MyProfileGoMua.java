package pages.gomua.myprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.Arrays;

public class MyProfileGoMua {
    final static Logger logger = LogManager.getLogger(MyProfileGoMua.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    MyProfileGoMuaElement myProfileUI;

    public MyProfileGoMua(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        myProfileUI = new MyProfileGoMuaElement(driver);
        PageFactory.initElements(driver, this);
    }
    public MyProfileGoMua clickOnEditProfile(){
        common.clickElement(myProfileUI.EDIT_PROFILE_LINK);
        logger.info("Click on Edit profile");
        return this;
    }
    public String getDisplayName() {
        String displayname = common.getText(myProfileUI.PROFILE_DISPLAY_NAME);
        logger.info("Get display name: %s".formatted(displayname));
        return displayname;
    }

    public String getPhoneNumber() {
        String phoneNumber = common.getText(myProfileUI.PROFILE_PHONE_NUMBER);
        logger.info("Get phone number: %s".formatted(phoneNumber));
        return phoneNumber;
    }

    public String getGender() {
        String gender = common.getText(myProfileUI.PROFILE_GENDER);
        logger.info("Get gender: %s".formatted(gender));
        return gender;
    }

    public MyProfileGoMua verifyDisplayName(String expected) {
        Assert.assertEquals(getDisplayName(), expected);
        logger.info("Display name is updated");
        return this;
    }

    public MyProfileGoMua verifyPhoneNumber(String expected) {
        String updateFormatPhoneExpected = String.join("", expected.split(":"));
        Assert.assertEquals(getPhoneNumber(), updateFormatPhoneExpected);
        logger.info("Phone number is updated");
        return this;
    }

    public MyProfileGoMua verifyGender(String expected) {
        Assert.assertEquals(getGender(), expected);
        logger.info("Gender is updated");
        return this;
    }

}
