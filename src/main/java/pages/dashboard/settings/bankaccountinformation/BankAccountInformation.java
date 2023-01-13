package pages.dashboard.settings.bankaccountinformation;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class BankAccountInformation {

	final static Logger logger = LogManager.getLogger(BankAccountInformation.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	public BankAccountInformation(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "li:nth-child(4) > a.nav-link")
	WebElement BANK_ACCOUNT_INFO_TAB;

	@FindBy(id = "countryCode")
	WebElement COUNTRY;
	
	@FindBy(id = "nameInside")
	WebElement FULL_NAME;
	
	@FindBy(id = "idCard")
	WebElement TAXCODE;
	
	@FindBy(id = "nameHolderInside")
	WebElement ACCOUNT_HOLDER;
	
	@FindBy(id = "accountNumberInside")
	WebElement BANK_ACCOUNT_NUMBER;
	
	@FindBy(id = "bankId")
	WebElement BANK_NAME;
	
	@FindBy(id = "region")
	WebElement CITY_PROVINCE;
	
	@FindBy(id = "branchName")
	WebElement BRANCH_NAME;

	public BankAccountInformation navigate() {
		clickStoreInformationTab();
		return this;
	}

	public BankAccountInformation clickStoreInformationTab() {
		commonAction.clickElement(BANK_ACCOUNT_INFO_TAB);
		logger.info("Clicked on Bank Account Information tab.");
		return this;
	}

	public BankAccountInformation selectCountry(String country) {
		String selectedOption = commonAction.selectByVisibleText(COUNTRY, country);
		logger.info("Selected Country: " + selectedOption);
		return this;
	}

	public BankAccountInformation inputFullName(String fullName) {
		commonAction.inputText(FULL_NAME, fullName);
		logger.info("Input '" + fullName + "' into Full Name field.");
		return this;
	}

	public BankAccountInformation inputTaxCode(String taxCode) {
		commonAction.inputText(TAXCODE, taxCode);
		logger.info("Input '" + taxCode + "' into Tax Code field.");
		return this;
	}

	public BankAccountInformation inputAccountHolder(String accountHolder) {
		commonAction.inputText(ACCOUNT_HOLDER, accountHolder);
		logger.info("Input '" + accountHolder + "' into Account Holder field.");
		return this;
	}

	public BankAccountInformation inputBankAccountNumber(String accountNumber) {
		commonAction.inputText(BANK_ACCOUNT_NUMBER, accountNumber);
		logger.info("Input '" + accountNumber + "' into Bank Account Number field.");
		return this;
	}

	public BankAccountInformation selectBankName(String bank) {
		String selectedOption = commonAction.selectByVisibleText(BANK_NAME, bank);
		logger.info("Selected Bank: " + selectedOption);
		return this;
	}

	public BankAccountInformation selectCityProvince(String cityProvince) {
		String selectedOption = commonAction.selectByVisibleText(CITY_PROVINCE, cityProvince);
		logger.info("Selected City/Province: " + selectedOption);
		return this;
	}

	public BankAccountInformation inputBankName(String bank) {
		commonAction.inputText(BANK_NAME, bank);
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
