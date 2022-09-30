package pages.storefront.userprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

import java.time.Duration;

public class UserProfileInfo {
	
	final static Logger logger = LogManager.getLogger(UserProfileInfo.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public UserProfileInfo (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

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
    
    public UserProfileInfo clickMyAccountSection() {
    	commonAction.clickElement(MYACCOUNT_SECTION);
    	logger.info("Clicked on My Account section.");
        return this;
    }
    
    public UserProfileInfo clickMyAddressSection() {
    	commonAction.clickElement(MYADDRESS_SECTION);
    	logger.info("Clicked on My Address section.");
    	return this;
    }
    
    public UserProfileInfo clickMembershipInfoSection() {
    	commonAction.clickElement(MEMBERSHIP_SECTION);
    	logger.info("Clicked on Membership Infomation section.");
    	return this;
    }
    
}
