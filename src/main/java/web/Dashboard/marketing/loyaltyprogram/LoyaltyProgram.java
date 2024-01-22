package web.Dashboard.marketing.loyaltyprogram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class LoyaltyProgram {

	final static Logger logger = LogManager.getLogger(LoyaltyProgram.class);

	WebDriver driver;
	UICommonAction commonAction;

	public LoyaltyProgram(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnCreateMembership = By.cssSelector(".loyalty .btn-save div");
	
	public LoyaltyProgram navigate() {
		new HomePage(driver).navigateToPage("Marketing", "Loyalty Program");
		return this;
	}

	public CreateLoyaltyProgram clickCreateMembershipBtn() {
		commonAction.click(loc_btnCreateMembership);
		logger.info("Clicked on 'Create Membership Level' button.");
		return new CreateLoyaltyProgram(driver);
	}
	
	public LoyaltyProgram deleteMembership(String membership) {
		String xpath = "//div[@class='gs-table-body-item name']/span[text()='%s']/parent::div/following-sibling::div//*[@class='icon-delete']".formatted(membership);
		commonAction.click(By.xpath(xpath));
		logger.info("Click on 'Delete' icon to delete membership '%s'.".formatted(membership));
		return this;
	}

	public LoyaltyProgram clickOKBtn() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked on 'OK' button to confirm membership deletion.");
		return this;
	}
	
	public LoyaltyProgram clickCancelBtn() {
		new ConfirmationDialog(driver).clickGrayBtn();
		logger.info("Clicked on 'Cancel' button to abort membership deletion.");
		return this;
	}
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateLoyaltyProgram(String permission) {
		if (permission.contentEquals("A")) {
			clickCreateMembershipBtn().clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/    
	
}
