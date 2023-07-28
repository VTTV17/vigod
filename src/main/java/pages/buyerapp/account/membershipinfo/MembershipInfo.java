package pages.buyerapp.account.membershipinfo;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class MembershipInfo {
	final static Logger logger = LogManager.getLogger(MembershipInfo.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public MembershipInfo(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonMobile(driver);
	}

	By YOUAREINTITLE = By.xpath("//*[ends-with(@resource-id,'activity_membership_information_tv_membership_level')]");
	By DESCRIPTION = By.xpath("//*[ends-with(@resource-id,'activity_membership_information_vp_membership_information')]//*[ends-with(@class,'TextView')]");
    
	public String getMembershipIntroduction() {
        String value = commonAction.getText(YOUAREINTITLE);
        logger.info("Retrieved Membership Introduction: " + value);
        return value;
    }
	
	public String getMembershipDescription() {
		String value = commonAction.getText(DESCRIPTION);
		logger.info("Retrieved Membership Description: " + value);
		return value;
	}

}
