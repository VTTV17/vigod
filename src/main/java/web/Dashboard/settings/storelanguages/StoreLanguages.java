package web.Dashboard.settings.storelanguages;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.settings.plans.PackagePayment;
import web.Dashboard.settings.plans.PlansPage;
import utilities.commons.UICommonAction;
import utilities.enums.PaymentMethod;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

public class StoreLanguages {

	final static Logger logger = LogManager.getLogger(StoreLanguages.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	StoreLanguageElement elements;

	public StoreLanguages(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new StoreLanguageElement();
	}

	public StoreLanguages navigate() {
		commonAction.click(elements.loc_tabStoreLanguage);
		logger.info("Clicked on Store Language tab.");
    	homePage.waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
		return this;
	}

	public StoreLanguages clickAddLanguage() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(elements.loc_btnAddLanguage))) {
			Assert.assertFalse(homePage.isMenuClicked(commonAction.getElement(elements.loc_btnAddLanguage)));
			return this;
		}
		commonAction.click(elements.loc_btnAddLanguage);
		logger.info("Clicked 'Add Language' button.");
		return this;
	}

	public boolean isAddLanguageDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return !commonAction.isElementNotDisplay(elements.loc_dlgAddLanguage);
	}		

	public StoreLanguages clickPublishLanguage(String language) {
		commonAction.click(By.xpath(elements.publishBtnByLanguage.formatted(language)));
		logger.info("Clicked 'Publish' link text for %s".formatted(language));
		return this;
	}	
	public StoreLanguages clickUnpublishLanguage(String language) {
		commonAction.click(By.xpath(elements.unpublishBtnByLanguage.formatted(language)));
		logger.info("Clicked 'Unpublish' link text for %s".formatted(language));
		return this;
	}	
	
	public StoreLanguages clickRemoveLanguageBtn(String language) {
		homePage.hideFacebookBubble();
		commonAction.click(By.xpath(elements.removeBtnByLanguage.formatted(language)));
		logger.info("Clicked 'Remove' icon of %s".formatted(language));
		return this;
	}	
	
	//Temporary function. Will think of a better way to handle this
	public StoreLanguages clickTranslationBtn() {
		homePage.hideFacebookBubble();
		commonAction.click(elements.loc_btnTranslation);
		logger.info("Clicked first 'Translation' button");
		waitTillLoadingIconDisappear();
		return this;
	}	
	
	public StoreLanguages clickSaveTranslationBtn() {
		commonAction.click(elements.loc_btnSaveTranslation);
		logger.info("Clicked 'Save' button to save adjusted translation");
		waitTillLoadingIconDisappear();
		return this;
	}	
	
	public StoreLanguages selectLanguageToAddFromDropdown(String language) {
		commonAction.click(elements.loc_ddlAddLanguages);
		commonAction.click(By.xpath(elements.ddvAddLanguageByName.formatted(language)));
		logger.info("Selected '%s' from Languages dropdown".formatted(language));
		return this;
	}	
	
	public StoreLanguages clickOkBtnInAddLanguagesDialog() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked OK button to complete adding language");
		waitTillLoadingIconDisappear();
		return this;
	}		

	public StoreLanguages clickRenewBtn() {
		commonAction.click(elements.loc_btnRenew);
		logger.info("Clicked 'Renew' button.");
		return this;
	}	
	
	public StoreLanguages clickChangeLanguageBtn() {
		commonAction.click(elements.loc_btnChangeLanguage);
		logger.info("Clicked 'Change Language' button.");
		return this;
	}	
	
	public void waitTillLoadingIconDisappear() {
		commonAction.sleepInMiliSecond(500);
		commonAction.waitInvisibilityOfElementLocated(elements.loc_icnLoading);
		logger.info("Loading icon has disappeared");
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
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/ 	

	boolean isPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffManagementPermisison = {
				staffPermission.getSetting().getStoreLanguage().isAddLanguage(),
				staffPermission.getSetting().getStoreLanguage().isChangeDefaultLanguage(),
				staffPermission.getSetting().getStoreLanguage().isPublishLanguage(),
				staffPermission.getSetting().getStoreLanguage().isPurchaseLanguagePackage(),
				staffPermission.getSetting().getStoreLanguage().isRemoveLanguage(),
				staffPermission.getSetting().getStoreLanguage().isRenewLanguagePackage(),
				staffPermission.getSetting().getStoreLanguage().isUnpublishLanguage(),
				staffPermission.getSetting().getStoreLanguage().isUpdateTranslation(),
		};
	    for(boolean individualPermission : allStaffManagementPermisison) if (individualPermission) return false;
	    return true;
	}	    
    
    StoreLanguages navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public StoreLanguages navigateToManagementScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=10");
		return this;
	}    
    
	void checkPermissionToPublishLanguage(AllPermissions staffPermission, boolean packagePurchased, String publishedLanguage) {
		navigateToManagementScreenByURL(); 

    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Language permission not granted. Skipping checkPermissionToPublishLanguage");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    		return;
    	}
    	if (!packagePurchased) {
    		logger.info("Multiple Languages package not purchased. Skipping checkPermissionToPublishLanguage");
    		return;
    	}
    	if (commonAction.getElements(elements.loc_tblLanguageRow).size() <2) {
    		logger.info("No additional languages found apart from default language. Skipping checkPermissionToPublishLanguage");
    		return;
    	}
    	
    	clickPublishLanguage(publishedLanguage);
    	if (staffPermission.getSetting().getStoreLanguage().isPublishLanguage()) {
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
    	}
    	
		logger.info("Finished checkPermissionToPublishLanguage");
	}    
	
	void checkPermissionToUnpublishLanguage(AllPermissions staffPermission, boolean packagePurchased, String unpublishedlanguage) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToUnpublishLanguage");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
    	if (!packagePurchased) {
    		logger.info("Multiple Languages package not purchased. Skipping checkPermissionToUnpublishLanguage");
    		return;
    	}
		if (commonAction.getElements(elements.loc_tblLanguageRow).size() <2) {
			logger.info("No additional languages found apart from default language. Skipping checkPermissionToUnpublishLanguage");
			return;
		}
		
		clickUnpublishLanguage(unpublishedlanguage);
		if (staffPermission.getSetting().getStoreLanguage().isUnpublishLanguage()) {
			new ConfirmationDialog(driver).clickOKBtn();
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToUnpublishLanguage");
	}    
	
	void checkPermissionToUpdateTranslation(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToUpdateTranslation");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		clickTranslationBtn().clickSaveTranslationBtn();
		if (staffPermission.getSetting().getStoreLanguage().isUpdateTranslation()) {
			//This implicitly means Save process is completed
			Assert.assertFalse(commonAction.getText(elements.loc_btnTranslation).isEmpty(), "Text of Translation button is empty");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToUpdateTranslation");
	}    
	
	void checkPermissionToAddLanguages(AllPermissions staffPermission, String addedLanguage) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToAddLanguages");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		clickAddLanguage();
		ConfirmationDialog confirmationDlg = new ConfirmationDialog(driver);
		
		if (staffPermission.getSetting().getStoreLanguage().isAddLanguage()) {
			if (confirmationDlg.isConfirmationDialogDisplayed()) {
				logger.info("Multiple Languages package not purchased or expired. Skipping checkPermissionToAddLanguages");
				return;
			} else {
				//Add languages
				selectLanguageToAddFromDropdown(addedLanguage);
				clickOkBtnInAddLanguagesDialog();
				clickPublishLanguage(addedLanguage); //This implicitly means the language is added successfully
			}
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToAddLanguages");
	}    
	
	void checkPermissionToRemoveLanguages(AllPermissions staffPermission, boolean packagePurchased, String removedLanguage) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToRemoveLanguages");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
    	if (!packagePurchased) {
    		logger.info("Multiple Languages package not purchased. Skipping checkPermissionToRemoveLanguages");
    		return;
    	}
		if (commonAction.getElements(elements.loc_tblLanguageRow).size() <2) {
			logger.info("No additional languages found apart from default language. Skipping checkPermissionToRemoveLanguages");
			return;
		}
		if (commonAction.getElements(elements.removeBtn).isEmpty()) {
			logger.info("Remove buttons not showed. Skipping checkPermissionToRemoveLanguages");
			return;
		}
		
		clickRemoveLanguageBtn(removedLanguage);
		if (staffPermission.getSetting().getStoreLanguage().isRemoveLanguage()) {
			new ConfirmationDialog(driver).clickOnRedBtn();
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToRemoveLanguages");
	}    
	
	void checkPermissionToPurchasePackage(AllPermissions staffPermission, boolean packagePurchased) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToPurchasePackage");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		if (packagePurchased) {
			logger.info("Multiple Languages package already purchased. Skipping checkPermissionToPurchasePackage");
			return;
		}
		if (!staffPermission.getSetting().getStoreLanguage().isAddLanguage()) {
			logger.info("Permission to add languages not granted. Skipping checkPermissionToPurchasePackage");
			return;
		}
		
		clickAddLanguage();
		new ConfirmationDialog(driver).clickOKBtn();
		PackagePayment planPage = new PackagePayment(driver);
		planPage.selectPaymentMethod(PaymentMethod.BANKTRANSFER);
		
		if (staffPermission.getSetting().getStoreLanguage().isPurchaseLanguagePackage()) {
			planPage.completePayment(PaymentMethod.BANKTRANSFER);
			Assert.assertTrue(!planPage.getOrderId().isEmpty(), "OrderId is not empty");
			//We won't actually purchase the package as we can only do that once
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToPurchasePackage");
	}    
	
	void checkPermissionToRenewPackage(AllPermissions staffPermission, boolean packagePurchased) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToRenewPackage");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		if (!packagePurchased) {
			logger.info("Multiple Languages package not purchased. Skipping checkPermissionToRenewPackage");
			return;
		}
		
		clickRenewBtn();
		
		if (staffPermission.getSetting().getStoreLanguage().isRenewLanguagePackage()) {
			//Not sure if this is a bug as it's not as expected in ticket
			PackagePayment planPage = new PackagePayment(driver);
			planPage.payThenComplete(PaymentMethod.BANKTRANSFER);
			Assert.assertTrue(!planPage.getOrderId().isEmpty(), "OrderId is not empty");
			//We won't actually renew as we can only do that once
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToRenewPackage");
	}    
	
	void checkPermissionToChangeDefaultLanguage(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Language permission not granted. Skipping checkPermissionToChangeDefaultLanguage");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		clickChangeLanguageBtn();
		if (staffPermission.getSetting().getStoreLanguage().isChangeDefaultLanguage()) {
			commonAction.click(elements.loc_ddlAddLanguages);
			commonAction.click(elements.ddvSelectDefaultLanguage);
			new ConfirmationDialog(driver).clickGreenBtn();
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		
		logger.info("Finished checkPermissionToChangeDefaultLanguage");
	}    
	
	public void checkStoreLanguagePermission(AllPermissions staffPermission, boolean isPackagePurchased, String publishedLanguage, String unpublishedLanguage, String addedLanguage, String removedLanguage) {
		checkPermissionToPublishLanguage(staffPermission, isPackagePurchased, publishedLanguage);
		checkPermissionToUnpublishLanguage(staffPermission, isPackagePurchased, unpublishedLanguage);
		checkPermissionToUpdateTranslation(staffPermission);
		checkPermissionToAddLanguages(staffPermission, addedLanguage);
		checkPermissionToRemoveLanguages(staffPermission, isPackagePurchased, removedLanguage);
		checkPermissionToPurchasePackage(staffPermission, isPackagePurchased);
		checkPermissionToRenewPackage(staffPermission, isPackagePurchased);
		checkPermissionToChangeDefaultLanguage(staffPermission);
	}	    
    
}
