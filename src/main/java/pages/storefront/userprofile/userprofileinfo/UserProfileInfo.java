package pages.storefront.userprofile.userprofileinfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.TestNG;
import org.testng.TestNGUtils;
import org.testng.asserts.SoftAssert;
import pages.storefront.GeneralSF;
import pages.storefront.header.HeaderSF;
import pages.storefront.userprofile.MembershipInfo;
import pages.storefront.userprofile.MyAddress;
import pages.storefront.userprofile.MyOrders;
import utilities.UICommonAction;
import java.time.Duration;

public class UserProfileInfo extends HeaderSF {

    final static Logger logger = LogManager.getLogger(UserProfileInfo.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    SoftAssert soft = new SoftAssert();
    UserProfileElement userProfileUI ;
    public UserProfileInfo (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        userProfileUI = new UserProfileElement(driver);
        PageFactory.initElements(driver, this);
    }

    public UserProfileInfo clickMyAccountSection() {
        commonAction.click(userProfileUI.loc_btnMyAccount);
        logger.info("Clicked on My Account section.");
        return this;
    }
    public MyAddress clickMyAddressSection() {
        commonAction.click(userProfileUI.loc_btnMyAddress);
        logger.info("Clicked on My Address section.");
        waitTillLoaderDisappear();
        commonAction.sleepInMiliSecond(2000);
        return new MyAddress(driver);
    }
    
    public MyOrders clickMyOrdersSection() {
    	commonAction.click(userProfileUI.loc_btnMyOrders);
    	logger.info("Clicked on My Orders section.");
    	waitTillLoaderDisappear();
    	return new MyOrders(driver);
    }
    
    public MembershipInfo clickMembershipInfoSection() {
    	commonAction.click(userProfileUI.loc_btnMembership);
    	logger.info("Clicked on Membership Infomation section.");
    	return new MembershipInfo(driver);
	}
    
    public UserProfileInfo verifyDisplayName(String expectedName){
        waitTillLoaderDisappear();
        Assert.assertEquals(commonAction.getText(userProfileUI.loc_lblDisplayName),expectedName);
        return this;
    }
    public UserProfileInfo verifyMembershipLevel(String expectedMembership){
        Assert.assertEquals(commonAction.getText(userProfileUI.loc_btnMembership),expectedMembership);
        return this;
    }
    public UserProfileInfo verifyBarcode(String expectedBarcode){
        Assert.assertEquals(commonAction.getText(userProfileUI.loc_lblBarcode),expectedBarcode);
        return this;
    }
    public UserProfileInfo verifyAvatarDisplay(){
        Assert.assertTrue(commonAction.isElementDisplay(userProfileUI.loc_imgAvatar));
        return this;
    }
}
