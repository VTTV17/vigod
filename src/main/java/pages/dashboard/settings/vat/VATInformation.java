package pages.dashboard.settings.vat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class VATInformation {

	final static Logger logger = LogManager.getLogger(VATInformation.class);

	WebDriver driver;
	UICommonAction commonAction;

	public VATInformation(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_tabVAT = By.cssSelector("li:nth-child(8) > a.nav-link");
	By loc_btnAddVATInfo = By.cssSelector(".VAT .gs-button__green");
	By loc_txtVAT = By.id("name");
	By loc_btnCancel = By.cssSelector(".VATmodal .gs-button__white");
	By loc_dlgAddVAT = By.cssSelector(".modal-dialog.VATmodal");

	public VATInformation navigate() {
		commonAction.click(loc_tabVAT);
		logger.info("Clicked on VAT tab.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
		return this;
	}

	public VATInformation clickAddTaxInformation() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnAddVATInfo).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnAddVATInfo)));
			return this;
		}
		commonAction.click(loc_btnAddVATInfo);
		logger.info("Clicked on 'Add Tax Information' button.");
		return this;
	}

	public VATInformation inputTaxName(String taxName) {
		commonAction.inputText(loc_txtVAT, taxName);
		logger.info("Input '" + taxName + "' into Tax Name field.");
		return this;
	}
	
	public VATInformation clickCancelBtn() {
		commonAction.click(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button in 'Add tax information' dialog.");
		return this;
	}

	public boolean isAddTaxInfomationDialogDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return !commonAction.isElementNotDisplay(loc_dlgAddVAT);
	}	
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToConfigureVAT(String permission) {
    	navigate();
    	clickAddTaxInformation();
    	boolean flag = isAddTaxInfomationDialogDisplayed();
		if (permission.contentEquals("A")) {
			clickCancelBtn();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(flag);
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/ 
}
