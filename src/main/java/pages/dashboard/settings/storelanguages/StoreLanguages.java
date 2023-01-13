package pages.dashboard.settings.storelanguages;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class StoreLanguages {

	final static Logger logger = LogManager.getLogger(StoreLanguages.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	public StoreLanguages(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "li:nth-child(8) > a.nav-link")
	WebElement STORE_LANGUAGE_TAB;
	
	@FindBy(css = ".languages-setting .gs-button__green")
	WebElement ADD_LANGUAGE_BTN;

	@FindBy(id = "name")
	WebElement TAX_NAME;
	
	@FindBy(css = ".VATmodal .gs-button__white")
	WebElement CANCEL_BTN;

	@FindBy(css = ".modal-dialog.modal-change")
	List<WebElement> ADDLANGUAGE_DIALOG;	

	public StoreLanguages navigate() {
		commonAction.clickElement(STORE_LANGUAGE_TAB);
		logger.info("Clicked on Store Language tab.");
		return this;
	}

	public StoreLanguages clickAddLanguage() {
		if (commonAction.isElementVisiblyDisabled(ADD_LANGUAGE_BTN)) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(ADD_LANGUAGE_BTN));
			return this;
		}
		commonAction.clickElement(ADD_LANGUAGE_BTN);
		logger.info("Clicked on 'Add Language' button.");
		return this;
	}

	public StoreLanguages inputTaxName(String taxName) {
		commonAction.inputText(TAX_NAME, taxName);
		logger.info("Input '" + taxName + "' into Tax Name field.");
		return this;
	}
	
	public StoreLanguages clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button in 'Add tax information' dialog.");
		return this;
	}

	public boolean isAddLanguageDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return !commonAction.isElementNotDisplay(ADDLANGUAGE_DIALOG);
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
