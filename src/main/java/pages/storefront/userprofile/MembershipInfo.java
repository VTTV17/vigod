package pages.storefront.userprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.List;

public class MembershipInfo {

	final static Logger logger = LogManager.getLogger(MembershipInfo.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public MembershipInfo(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".membership-content .level-title")
	WebElement MEMBERSHIP_TITLE;
	
	@FindBy(css = ".membership-content .nav-tabs .nav-item")
	List<WebElement> MEMBERSHIP_TABS;

	@FindBy(css = ".membership-content .tab-pane.active")
	WebElement MEMBERSHIP_TAB_CONTENT;
	
	public String getMembershipTitle() {
		logger.info("Getting membership info...");
		return commonAction.getText(MEMBERSHIP_TITLE).trim();
	}
	
	public MembershipInfo clickMembershipTitle(String membership) {
		String retrievedMembership;
		boolean clicked = false;
		for (WebElement el : MEMBERSHIP_TABS) {
			retrievedMembership = commonAction.getText(el).trim();
			if (retrievedMembership.contentEquals(membership)) {
				clicked = true;
				commonAction.clickElement(el);
				logger.info("Clicked on Membership Tab: " + membership);
				break;
			}
		}
		Assert.assertTrue(clicked, "Membership Tab '%s' ".formatted(membership) + "is not found");
		return this;
	}	
	
	public String getMembershipContentInHTMLFormat() {
		String value = commonAction.getElementAttribute(MEMBERSHIP_TAB_CONTENT, "innerHTML");
		logger.info("Retrieved Membership Content in HTML Format: " + value);
		return value;
	}
	
	public String getMembershipContentInRawText() {
		String value = commonAction.getText(MEMBERSHIP_TAB_CONTENT);
		logger.info("Retrieved Membership Content in raw text: " + value);
		return value;
	}

    public void verifyMembershipTitle(boolean isMembership, String membershipTitle) throws Exception {
        String expectedTitle = isMembership ? PropertiesUtil.getPropertiesValueBySFLang("userProfile.membership.membership").formatted(membershipTitle) : PropertiesUtil.getPropertiesValueBySFLang("userProfile.membership.notMembership");
        Assert.assertEquals(expectedTitle, getMembershipTitle());
        logger.info("verifyMembershipTitle completed");
    }    
    
}
