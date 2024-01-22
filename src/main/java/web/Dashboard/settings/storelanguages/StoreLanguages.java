package web.Dashboard.settings.storelanguages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class StoreLanguages {

	final static Logger logger = LogManager.getLogger(StoreLanguages.class);

	WebDriver driver;
	UICommonAction commonAction;

	public StoreLanguages(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_tabStoreLanguage = By.cssSelector("li:nth-child(9) > a.nav-link");
	By loc_btnAddLanguage = By.cssSelector(".languages-setting .gs-button__green");
	By loc_dlgAddLanguage = By.cssSelector(".modal-dialog.modal-change");

	public StoreLanguages navigate() {
		commonAction.click(loc_tabStoreLanguage);
		logger.info("Clicked on Store Language tab.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
		return this;
	}

	public StoreLanguages clickAddLanguage() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnAddLanguage))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnAddLanguage)));
			return this;
		}
		commonAction.click(loc_btnAddLanguage);
		logger.info("Clicked on 'Add Language' button.");
		return this;
	}

	public boolean isAddLanguageDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return !commonAction.isElementNotDisplay(loc_dlgAddLanguage);
	}		
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToAddLanguages(String permission) {
    	navigate();
    	clickAddLanguage();
    	boolean flag = new ConfirmationDialog(driver).isConfirmationDialogDisplayed();
		if (permission.contentEquals("A")) {
			new ConfirmationDialog(driver).clickCancelBtn();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(flag);
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/ 	

}
