package web.Dashboard.settings.bankaccountinformation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class BankAccountInformation {

	final static Logger logger = LogManager.getLogger(BankAccountInformation.class);

	WebDriver driver;
	UICommonAction commonAction;

	public BankAccountInformation(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_tabBankAccountInfo = By.cssSelector("li:nth-child(4) > a.nav-link");
	By loc_ddlCountry = By.id("countryCode");
	By loc_txtFullName = By.id("nameInside");
	By loc_txtTaxCode = By.id("idCard");
	By loc_txtAccountHolder = By.id("nameHolderInside");
	By loc_txtBankAccountNumber = By.id("accountNumberInside");
	By loc_txtBankName = By.id("bankId");
	By loc_ddlCityProvince = By.id("region");
	By loc_txtBranchName = By.id("branchName");

	public BankAccountInformation navigate() {
		clickStoreInformationTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
		return this;
	}

	public BankAccountInformation clickStoreInformationTab() {
		commonAction.click(loc_tabBankAccountInfo);
		logger.info("Clicked on Bank Account Information tab.");
		return this;
	}

	public BankAccountInformation selectCountry(String country) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_ddlCountry), country);
		logger.info("Selected Country: " + selectedOption);
		return this;
	}

	public BankAccountInformation inputFullName(String fullName) {
		commonAction.inputText(loc_txtFullName, fullName);
		logger.info("Input '" + fullName + "' into Full Name field.");
		return this;
	}

	public BankAccountInformation inputTaxCode(String taxCode) {
		commonAction.inputText(loc_txtTaxCode, taxCode);
		logger.info("Input '" + taxCode + "' into Tax Code field.");
		return this;
	}

	public BankAccountInformation inputAccountHolder(String accountHolder) {
		commonAction.inputText(loc_txtAccountHolder, accountHolder);
		logger.info("Input '" + accountHolder + "' into Account Holder field.");
		return this;
	}

	public BankAccountInformation inputBankAccountNumber(String accountNumber) {
		commonAction.inputText(loc_txtBankAccountNumber, accountNumber);
		logger.info("Input '" + accountNumber + "' into Bank Account Number field.");
		return this;
	}

	public BankAccountInformation selectBankName(String bank) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_txtBankName), bank);
		logger.info("Selected Bank: " + selectedOption);
		return this;
	}

	public BankAccountInformation selectCityProvince(String cityProvince) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_ddlCityProvince), cityProvince);
		logger.info("Selected City/Province: " + selectedOption);
		return this;
	}

	public BankAccountInformation inputBankName(String bank) {
		commonAction.inputText(loc_txtBankName, bank);
		logger.info("Input '" + bank + "' into Bank Name field.");
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
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/   	
	
}
