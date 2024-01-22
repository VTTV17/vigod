package web.StoreFront.userprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class MembershipInfo {

	final static Logger logger = LogManager.getLogger(MembershipInfo.class);

	WebDriver driver;
	UICommonAction commonAction;

	public MembershipInfo(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_lblMembershipTitle = By.cssSelector(".membership-content .level-title");
	By loc_tabMembershipTabs = By.cssSelector(".membership-content .nav-tabs .nav-item");
	By loc_lblMembershipTabContent = By.cssSelector(".membership-content .tab-pane.active");
	
	public String getMembershipTitle() {
		logger.info("Getting membership info...");
		return commonAction.getText(loc_lblMembershipTitle).trim();
	}
	
	public MembershipInfo clickMembershipTitle(String membership) {
		String retrievedMembership;
		boolean clicked = false;
		for (WebElement el : commonAction.getListElement(loc_tabMembershipTabs)) {
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
		String value = commonAction.getAttribute(loc_lblMembershipTabContent, "innerHTML");
		logger.info("Retrieved Membership Content in HTML Format: " + value);
		return value;
	}
	
	public String getMembershipContentInRawText() {
		String value = commonAction.getText(loc_lblMembershipTabContent);
		logger.info("Retrieved Membership Content in raw text: " + value);
		return value;
	}

    public void verifyMembershipTitle(boolean isMembership, String membershipTitle) throws Exception {
        String expectedTitle = isMembership ? PropertiesUtil.getPropertiesValueBySFLang("userProfile.membership.membership").formatted(membershipTitle) : PropertiesUtil.getPropertiesValueBySFLang("userProfile.membership.notMembership");
        Assert.assertEquals(expectedTitle, getMembershipTitle());
        logger.info("verifyMembershipTitle completed");
    }    
    
}
