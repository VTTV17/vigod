package pages.storefront.userprofile.userprofileinfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.UICommonAction;
import java.time.Duration;

public class UserProfileInfo {
	
	final static Logger logger = LogManager.getLogger(UserProfileInfo.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    SoftAssert soft = new SoftAssert();
    UserProfileElement userProfileUI ;
    public UserProfileInfo (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        userProfileUI = new UserProfileElement(driver);
        PageFactory.initElements(driver, this);
    }

<<<<<<< Updated upstream:src/main/java/pages/storefront/userprofile/UserProfileInfo.java
	@FindBy(css = ".icon-my-account")
    WebElement MYACCOUNT_SECTION;

	@FindBy(css = ".icon-my-address")
	WebElement MYADDRESS_SECTION;

    @FindBy(css = ".box_name .user-left-info__user-name p")
    WebElement DISPLAY_NAME;
    @FindBy(css = ".box_name .user-left-info__user-level p")
    WebElement MEMBER_SHIP_LEVEL;

	@FindBy(css = ".icon-membership")
	WebElement MEMBERSHIP_SECTION;
    
=======
>>>>>>> Stashed changes:src/main/java/pages/storefront/userprofile/userprofileinfo/UserProfileInfo.java
    public UserProfileInfo clickMyAccountSection() {
    	commonAction.clickElement(userProfileUI.MYACCOUNT_SECTION);
    	logger.info("Clicked on My Account section.");
        return this;
    }
    public UserProfileInfo clickMyAddressSection() {
    	commonAction.clickElement(userProfileUI.MYADDRESS_SECTION);
    	logger.info("Clicked on My Address section.");
    	return this;
    }
<<<<<<< Updated upstream:src/main/java/pages/storefront/userprofile/UserProfileInfo.java
    
    public UserProfileInfo clickMembershipInfoSection() {
    	commonAction.clickElement(MEMBERSHIP_SECTION);
    	logger.info("Clicked on Membership Infomation section.");
    	return this;
    }
    
=======
    public UserProfileInfo verifyDisplayName(String expectedName){
        Assert.assertEquals(commonAction.getText(userProfileUI.DISPLAY_NAME),expectedName);
        return this;
    }
    public UserProfileInfo verifyMembershipLevel(String expectedMembership){
        Assert.assertEquals(commonAction.getText(userProfileUI.MEMBERSHIP_LEVEL),expectedMembership);
        return this;
    }
    public UserProfileInfo verifyBarcode(String expectedBarcode){
        Assert.assertEquals(commonAction.getText(userProfileUI.BARCODE_NUMBER),expectedBarcode);
        return this;
    }
    public UserProfileInfo verifyAvatarDisplay(){
        Assert.assertTrue(commonAction.isElementDisplay(userProfileUI.AVATAR));
        return this;
    }
>>>>>>> Stashed changes:src/main/java/pages/storefront/userprofile/userprofileinfo/UserProfileInfo.java
}
