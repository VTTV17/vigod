package web.Dashboard.settings.vat;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.DOMAIN_BIZ;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.Domain;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

public class VATInformation {

	final static Logger logger = LogManager.getLogger(VATInformation.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	VATPageElement elements;
	
	Domain domain;

	public VATInformation(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new VATPageElement();
	}
	public VATInformation(WebDriver driver, Domain domain) {
		this(driver);
		this.domain = domain;
	}

	public VATInformation navigate() {
		commonAction.click(elements.loc_tabVAT);
		logger.info("Clicked on VAT tab.");
		homePage.waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
		return this;
	}

	VATInformation navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public VATInformation navigateToManagementScreenByURL() {
		if (domain.equals(Domain.VN)) {
			navigateByURL(DOMAIN + "/setting?tabId=9");
		} else {
			navigateByURL(DOMAIN_BIZ + "/setting?tabId=9");
		}
		
    	commonAction.sleepInMiliSecond(500, "Wait a little after navigation");
		return this;
	}	
	
	/**
	 * Waits until VATs appear
	 * @return true if there are VATs in the table
	 */
	public boolean waitForVATEntries() {
    	for (int i=0; i<5; i++) {
    		if (!commonAction.getElements(elements.loc_tblVATRows).isEmpty()) return true;
    		commonAction.sleepInMiliSecond(1000, "Wait until there are VATs in VAT table");
    	}
		return false;
	}	
	public boolean refreshUntilTableEmpty() {
    	for (int i=0; i<3; i++) {
    		if (!waitForVATEntries()) return true;
    		logger.debug("Table not empty, refreshing page...");
    		commonAction.refreshPage();
    	}
		return false;
	}		
	public boolean refreshUntilTableNotEmpty() {
		for (int i=0; i<3; i++) {
			if (waitForVATEntries()) return true;
			logger.debug("Table empty, refreshing page...");
			commonAction.refreshPage();
		}
		return false;
	}		
	
    /**
     * A temporary function that helps get rid of the annoying try catch block when reading text from property file
     * @param propertyKey
     */
    public String translateText(String propertyKey) {
    	String translatedText = null;
    	try {
    		translatedText = PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return translatedText;
    }	
	
	public VATInformation clickAddTaxInformation() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(elements.loc_btnAddVATInfo).findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnAddVATInfo));
			return this;
		}
		commonAction.click(elements.loc_btnAddVATInfo);
		logger.info("Clicked on 'Add Tax Information' button.");
		return this;
	}
	
	public boolean isAddTaxInfomationDialogDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return !commonAction.getElements(elements.loc_dlgAddVAT).isEmpty();
	}		
	
	public VATInformation inputTaxName(String taxName) {
		commonAction.inputText(elements.loc_txtVAT, taxName);
		logger.info("Input Tax name: " + taxName);
		return this;
	}
	
	public VATInformation inputTaxRate(String taxRate) {
		commonAction.inputText(elements.loc_txtVATRate, taxRate);
		logger.info("Input Tax rate: " + taxRate);
		return this;
	}
	
	public VATInformation inputDescription(String taxDescription) {
		commonAction.inputText(elements.loc_txtVATDescription, taxDescription);
		logger.info("Input Tax description: " + taxDescription);
		return this;
	}
	
	public VATInformation selectTaxType(int taxType) {
		switch (taxType) {
		case 0: {
			commonAction.click(elements.loc_rdoSellingVATType);
			logger.info("Selected Tax type: Sell");
			break;
		}
		case 1: {
			commonAction.click(elements.loc_rdoImportGoodsVATType);
			logger.info("Selected Tax type: Import Goods");
			break;
		}
		default:
			logger.info("Input Tax type is not allowed. Tax type 'Sell' is automatically selected");
			break;
		}
		return this;
	}
	
	public VATInformation clickAddBtn() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked on 'Add' button in 'Add tax information' dialog.");
		return this;
	}
	
	public VATInformation clickCancelBtn() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked on 'Cancel' button in 'Add tax information' dialog.");
		return this;
	}

	public VATInformation clickShowTaxInWebAppCheckbox() {
		commonAction.click(elements.loc_chkShowInWebApp);
		logger.info("Clicked 'Show TAX in web/app' checkbox");
		return this;
	}	
	
	public String getDefaultTax() {
		waitForVATEntries(); //Workaround to wait till the default tax is returned from API, otherwise it'll always show "No option", which makes our tests flaky on CI env
		
		String value = commonAction.getText(elements.loc_ddlDefaultVAT);
		logger.info("Retrieved default tax: " + value);
		return value;
	}	
	
	public boolean isDefaultTaxFieldDisabled() {
		boolean isEnabled = !commonAction.getElement(elements.loc_ddlDefaultVAT).isEnabled();
		logger.info("Is Default Tax field disabled: " + isEnabled);
		return isEnabled;
	}	
	
	public VATInformation selectDefaultTax(String taxName) {
		commonAction.click(elements.loc_ddlDefaultVAT);
		commonAction.click(By.xpath(elements.ddvDefaultVATLocator.formatted(taxName)));
		logger.info("Selected default tax: " + taxName);
		return this;
	}	
	
	public VATInformation clickApplyTaxAfterDiscountCheckbox() {
		commonAction.click(elements.loc_chkApplyAfterDiscount);
		logger.info("Clicked 'Apply TAX to product price after promotion' checkbox");
		return this;
	}	
	
	public VATInformation deleteTax(String taxName) {
		logger.info("Deleting tax: " + taxName);
		commonAction.click(By.xpath(elements.specificVATDeleteIconLocator.formatted("and text()='%s'".formatted(taxName))));
		new ConfirmationDialog(driver).clickOnRedBtn();
		return this;
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
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/ 
	boolean isPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffManagementPermisison = {
				staffPermission.getSetting().getTAX().isViewTAXList(),
				staffPermission.getSetting().getTAX().isCreateSellingTAX(),
				staffPermission.getSetting().getTAX().isCreateImportingTAX(),
				staffPermission.getSetting().getTAX().isUpdateTAXConfiguration(),
				staffPermission.getSetting().getTAX().isDeleteTAX()
		};
	    for(boolean individualPermission : allStaffManagementPermisison) if (individualPermission) return false;
	    return true;
	}	    
    
    public void checkPermissionToViewVATList(AllPermissions staffPermission) {
    	navigateToManagementScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("VAT permission not granted. Skipping checkPermissionToViewVATList");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	if (staffPermission.getSetting().getTAX().isViewTAXList()) {
    		Assert.assertTrue(refreshUntilTableNotEmpty(), "There are VAT entries in table");
    	} else {
    		Assert.assertTrue(refreshUntilTableEmpty(), "VAT table is empty");
    	}
    	logger.info("Finished checkPermissionToViewVATList");
    }    
    
    
    //Will merge the two functions below shortly
    public void checkPermissionToCreateSellingTax(AllPermissions staffPermission) {
    	navigateToManagementScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("VAT permission not granted. Skipping checkPermissionToCreateSellingTax");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	DataGenerator randomData = new DataGenerator();
    	
    	String taxName = "Auto Tax " + randomData.randomNumberGeneratedFromEpochTime(5);
    	String taxRate = String.valueOf(DataGenerator.generatNumberInBound(0, 101));
    	clickAddTaxInformation().inputTaxName(taxName).inputTaxRate(taxRate).selectTaxType(0).clickAddBtn();
    	
    	if (staffPermission.getSetting().getTAX().isCreateSellingTAX()) {
    		Assert.assertEquals(homePage.getToastMessage(), translateText("affiliate.partner.create.successMessage"));
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
    	}
    	logger.info("Finished checkPermissionToCreateSellingTax");
    }    
    public void checkPermissionToCreateImportingTax(AllPermissions staffPermission) {
    	navigateToManagementScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("VAT permission not granted. Skipping checkPermissionToCreateImportingTax");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	DataGenerator randomData = new DataGenerator();
    	
    	String taxName = "Auto Tax " + randomData.randomNumberGeneratedFromEpochTime(5);
    	String taxRate = String.valueOf(DataGenerator.generatNumberInBound(0, 101));
    	clickAddTaxInformation().inputTaxName(taxName).inputTaxRate(taxRate).selectTaxType(1).clickAddBtn();
    	
    	if (staffPermission.getSetting().getTAX().isCreateImportingTAX()) {
    		Assert.assertEquals(homePage.getToastMessage(), translateText("affiliate.partner.create.successMessage"));
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
    	}
    	logger.info("Finished checkPermissionToCreateImportingTax");
    }    
  
    public void checkPermissionToConfigureTax(AllPermissions staffPermission) {
    	navigateToManagementScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("VAT permission not granted. Skipping checkPermissionToConfigureTax");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	clickShowTaxInWebAppCheckbox();
    	if (staffPermission.getSetting().getTAX().isUpdateTAXConfiguration()) {
    		Assert.assertEquals(homePage.getToastMessage(), translateText("affiliate.partner.update.successMessage"));
    		clickShowTaxInWebAppCheckbox();
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
    	}
    	
    	if (staffPermission.getSetting().getTAX().isViewTAXList()) {
        	String originalDefaultTax = getDefaultTax();
        	selectDefaultTax(originalDefaultTax);
        	if (staffPermission.getSetting().getTAX().isUpdateTAXConfiguration()) {
        		Assert.assertEquals(homePage.getToastMessage(), translateText("affiliate.partner.update.successMessage"));
        	} else {
        		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
        	}
    	} else {
    		Assert.assertTrue(isDefaultTaxFieldDisabled(), "Is Default Tax field disabled");
    	}
    	
    	clickApplyTaxAfterDiscountCheckbox();
    	if (staffPermission.getSetting().getTAX().isUpdateTAXConfiguration()) {
    		//No changes to capture
    		clickApplyTaxAfterDiscountCheckbox();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
    	}
    	
    	logger.info("Finished checkPermissionToConfigureTax");
    }     
    
    public void checkPermissionToDeleteTax(AllPermissions staffPermission, String deletedTax) {
    	navigateToManagementScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("VAT permission not granted. Skipping checkPermissionToDeleteTax");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	if (!staffPermission.getSetting().getTAX().isViewTAXList()) {
    		logger.info("View VAT permission not granted. Skipping checkPermissionToDeleteTax");
    		return;
    	}

    	if (commonAction.getElements(elements.loc_tblVATRows).size()==1) {
    		logger.info("Only default tax shown. Skipping checkPermissionToDeleteTax");
    		return;
    	}
    	
    	deleteTax(deletedTax);
    	if (staffPermission.getSetting().getTAX().isDeleteTAX()) {
    		Assert.assertEquals(homePage.getToastMessage(), translateText("affiliate.commission.delete.successMessage"));
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
    	}
    	
    	logger.info("Finished checkPermissionToDeleteTax");
    }     
    
    public void checkVATPermission(AllPermissions staffPermission, String deletedTax) {
    	checkPermissionToViewVATList(staffPermission);
    	checkPermissionToCreateSellingTax(staffPermission);
    	checkPermissionToCreateImportingTax(staffPermission);
    	checkPermissionToConfigureTax(staffPermission);
    	checkPermissionToDeleteTax(staffPermission, deletedTax);
    }     
    
}
