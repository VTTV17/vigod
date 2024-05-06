package web.Dashboard.settings.bankaccountinformation;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.settings.requiredpassworddialog.RequiredPasswordDialog;

public class BankAccountInformation {

	final static Logger logger = LogManager.getLogger(BankAccountInformation.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	BankAccountInformationElement elements;

	public BankAccountInformation(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new BankAccountInformationElement();
	}

	public BankAccountInformation navigate() {
		clickStoreInformationTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
		return this;
	}

	BankAccountInformation navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public BankAccountInformation navigateToDetailScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=3");
//    	for (int i=0; i<30; i++) {
//    		if (!commonAction.getElements(elements.loc_txtFullName).isEmpty()) break;
//    		commonAction.sleepInMiliSecond(500);
//    	}
    	commonAction.sleepInMiliSecond(1000, "Wait a little after navigation");
		return this;
	}		
	
	public BankAccountInformation clickStoreInformationTab() {
		commonAction.click(elements.loc_tabBankAccountInfo);
		logger.info("Clicked on Bank Account Information tab.");
		return this;
	}

	public BankAccountInformation selectCountry(String country) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(elements.loc_ddlCountry), country);
		logger.info("Selected Country: " + selectedOption);
		return this;
	}

	public BankAccountInformation inputFullName(String fullName) {
		commonAction.inputText(elements.loc_txtFullName, fullName);
		logger.info("Input Full Name: " + fullName);
		return this;
	}

	public String getFullName() {
		String text = commonAction.getAttribute(elements.loc_txtFullName, "value");
		logger.info("Retrieved Full Name: " + text);
		return text;
	}
	
	public BankAccountInformation inputTaxCode(String taxCode) {
		commonAction.inputText(elements.loc_txtTaxCode, taxCode);
		logger.info("Input Tax: " + taxCode);
		return this;
	}

	public BankAccountInformation inputAccountHolder(String accountHolder) {
		commonAction.inputText(elements.loc_txtAccountHolder, accountHolder);
		logger.info("Input Account Holder: " + accountHolder);
		return this;
	}

	public BankAccountInformation inputBankAccountNumber(String accountNumber) {
		commonAction.inputText(elements.loc_txtBankAccountNumber, accountNumber);
		logger.info("Input Bank Account Number: " + accountNumber);
		return this;
	}

	public BankAccountInformation selectBankName(String bank) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(elements.loc_txtBankName), bank);
		logger.info("Selected Bank: " + selectedOption);
		return this;
	}

	public BankAccountInformation selectCityProvince(String cityProvince) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(elements.loc_ddlCityProvince), cityProvince);
		logger.info("Selected City/Province: " + selectedOption);
		return this;
	}

	public BankAccountInformation inputBankName(String bank) {
		commonAction.inputText(elements.loc_txtBankName, bank);
		logger.info("Input Bank Name: " + bank);
		return this;
	}
	
	public BankAccountInformation inputBranch(String branch) {
		commonAction.inputText(elements.loc_txtBranchName, branch);
		logger.info("Input Branch: " + branch);
		return this;
	}	

	public BankAccountInformation clickSaveBtn() {
		commonAction.click(elements.loc_btnSave);
		logger.info("Clicked Save button.");
		return this;
	}	
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToSetBankAccountInfo(String permission) {
    	navigate();
		if (permission.contentEquals("A")) {
			selectCountry("Vietnam");
			inputFullName("Nguyen Van A");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/   	

    public void checkPermissionToViewBankInfo(AllPermissions staffPermission) {
    	navigateToDetailScreenByURL(); 
    	
    	if (!staffPermission.getSetting().getBankAccount().isViewBankInformation() && !staffPermission.getSetting().getBankAccount().isUpdateBankInformation()) {
    		logger.info("Staff does not have Bank Info Setting permission. Skipping checking view bank info permission");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	String fullName = getFullName();
    	
    	if (staffPermission.getSetting().getBankAccount().isViewBankInformation()) {
    		Assert.assertFalse(fullName.isEmpty(), "Full Name is empty");
    	} else {
    		Assert.assertTrue(fullName.isEmpty(), "Full Name is empty");
    	}
    	
    	logger.info("Finished checking permission to view bank info");
    }    
    
    public void checkPermissionToEditBankInfo(AllPermissions staffPermission, String adminPassword) {
    	navigateToDetailScreenByURL(); 
    	
    	if (!staffPermission.getSetting().getBankAccount().isViewBankInformation() && !staffPermission.getSetting().getBankAccount().isUpdateBankInformation()) {
    		logger.info("Staff does not have Bank Info Setting permission. Skipping checking edit bank info permission");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	inputFullName("Nguyen Thi B");
    	inputTaxCode("12345670");
    	inputAccountHolder("Nguyen Thi B");
    	inputBankAccountNumber("111101111");
    	selectBankName("ACB - NH TMCP A Chau");
    	selectCityProvince("Ben Tre");
    	inputBranch("Long Thanh");
    	clickSaveBtn();
    	
    	if (staffPermission.getSetting().getBankAccount().isUpdateBankInformation()) {
    		new RequiredPasswordDialog(driver).inputPassword(adminPassword).clickOKBtn();
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    	}
    	
    	logger.info("Finished checking permission to edit bank info");
    }    
    
    public void checkBankAccountPermission(AllPermissions staffPermission, String adminPassword) {
    	checkPermissionToViewBankInfo(staffPermission);
    	checkPermissionToEditBankInfo(staffPermission, adminPassword);
    } 
    
}
