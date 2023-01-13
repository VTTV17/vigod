package pages.dashboard.marketing.loyaltyprogram;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class LoyaltyProgram {

	final static Logger logger = LogManager.getLogger(LoyaltyProgram.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public LoyaltyProgram(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".loyalty .btn-save div")
	WebElement CREATE_MEMBERSHIP_BTN;
	
	@FindBy(css = ".confirm-modal .gs-button__green")
	WebElement DELETE_OK_BTN;
	
	@FindBy(css = ".confirm-modal .gs-button__gray--outline")
	WebElement DELETE_CANCEL_BTN;
	
	public LoyaltyProgram navigate() {
		new HomePage(driver).navigateToPage("Marketing", "Loyalty Program");
		return this;
	}

	public CreateLoyaltyProgram clickCreateMembershipBtn() {
		commonAction.clickElement(CREATE_MEMBERSHIP_BTN);
		logger.info("Clicked on 'Create Membership Level' button.");
		return new CreateLoyaltyProgram(driver);
	}
	
	public LoyaltyProgram deleteMembership(String membership) {
		String xpath = "//div[@class='gs-table-body-item name']/span[text()='%s']/parent::div/following-sibling::div//*[@class='icon-delete']".formatted(membership);
		commonAction.clickElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))));
		logger.info("Click on 'Delete' icon to delete membership '%s'.".formatted(membership));
		return this;
	}

	public LoyaltyProgram clickOKBtn() {
		commonAction.clickElement(DELETE_OK_BTN);
		logger.info("Clicked on 'OK' button to confirm membership deletion.");
		return this;
	}
	
	public LoyaltyProgram clickCancelBtn() {
		commonAction.clickElement(DELETE_CANCEL_BTN);
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
