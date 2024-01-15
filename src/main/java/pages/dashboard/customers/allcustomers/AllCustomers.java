package pages.dashboard.customers.allcustomers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.customers.allcustomers.create_customer.CreateCustomerPopup;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class AllCustomers extends HomePage{

	final static Logger logger = LogManager.getLogger(AllCustomers.class);

	WebDriver driver;
	UICommonAction commonAction;

	public AllCustomers(WebDriver driver) {
		super(driver);
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnExport = By.xpath("(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[2]");
	By loc_btnExportCustomer = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
	By loc_btnImportCustomer = By.xpath("(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[3]");
	By loc_btnPrintBarcode = By.xpath("(//div[contains(@class,'customer-list')]//div[contains(@class,'buttons-row')]//button)[last()]");
	By loc_txtSearchCustomer = By.cssSelector(".customer-list__filter-container .gs-search-box__wrapper .uik-input__input");
	By loc_btnFilter = By.cssSelector(".btn-filter-action");
	By loc_ddlFilterBranch = By.xpath("(//div[contains(@class,'filter-title')])[1]/following-sibling::div");
	By loc_btnCreateCustomer = By.cssSelector(".gs-content-header-right-el > .gs-button__green");
	By loc_btnDoneFilter = By.cssSelector(".dropdown-menu-right .gs-button__green");
	By loc_dlgImportCustomer = By.cssSelector(".customer-list-import-modal");	
	By loc_dlgPrintBarcode = By.cssSelector(".customer-list-barcode-printer");
	
	public AllCustomers navigate() {
		new HomePage(driver).navigateToPage("Customers");
		commonAction.sleepInMiliSecond(2000);
		return this;
	}

	public AllCustomers clickExport() {
		commonAction.click(loc_btnExport);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public AllCustomers clickExportCustomer() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnExportCustomer).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnExportCustomer));
			return this;
		}
		commonAction.click(loc_btnExportCustomer);
		logger.info("Clicked on 'Export Customer' button.");
		return this;
	}	
	
	public AllCustomers clickImportCustomer() {
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnImportCustomer).findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnImportCustomer));
			return this;
		}
		commonAction.click(loc_btnImportCustomer);
		logger.info("Clicked on 'Import Customer' button.");
		return this;
	}	

    public AllCustomers clickPrintBarcode() {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnPrintBarcode).findElement(By.xpath("./parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnPrintBarcode));
    		return this;
    	}
    	commonAction.click(loc_btnPrintBarcode);
    	logger.info("Clicked on 'Print Barcode' button.");
    	return this;
    }	
	
    public boolean isImportCustomerDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.isElementNotDisplay(driver.findElements(loc_dlgImportCustomer));
    }   	

    public boolean isPrintBarcodeDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.isElementNotDisplay(driver.findElements(loc_dlgPrintBarcode));
    }       
    
	public AllCustomers inputSearchTerm(String searchTerm) {
		commonAction.sendKeys(loc_txtSearchCustomer, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickFilterIcon() {
		commonAction.click(loc_btnFilter);
		logger.info("Clicked on Filter icon.");
		return this;
	}

	public AllCustomers clickFilterDoneBtn() {
		commonAction.click(loc_btnDoneFilter);
		logger.info("Clicked on Filter Done button.");
		return this;
	}

	public AllCustomers clickBranchList() {
		commonAction.click(loc_ddlFilterBranch);
		logger.info("Clicked on Branch list.");
		return this;
	}

	public AllCustomers selectBranch(String branch) {
		new HomePage(driver).hideFacebookBubble();
		clickFilterIcon();
		clickBranchList();
		commonAction.click(new ByChained(loc_ddlFilterBranch, By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(branch))));
		logger.info("Selected branch: " + branch);
		clickFilterDoneBtn();
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickUser(String user) {
		new HomePage(driver).hideFacebookBubble();
		commonAction.click(By.xpath("//div[@class='text-truncate' and text()='%s']".formatted(user)));
		logger.info("Clicked on user: " + user);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public String getPhoneNumber(String user) {
		String value = commonAction.getText(By.xpath("//div[@class='full-name' and text()='%s']/ancestor::*/following-sibling::td[2]".formatted(user)));
		logger.info("Retrieved phone number: " + value);
		return value;
	}

	public CreateCustomerPopup clickCreateNewCustomerBtn() {
		commonAction.click(loc_btnCreateCustomer);
		return new CreateCustomerPopup(driver);
	}

	public CustomerDetails searchAndGoToCustomerDetailByName(String fullName){
		inputSearchTerm(fullName);
		clickUser(fullName);
		return new CustomerDetails(driver);
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToExportCustomer(String permission) {
		if (permission.contentEquals("A")) {
			clickExport().clickExportCustomer();
			new ConfirmationDialog(driver).clickCancelBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToImportCustomer(String permission) {
		clickImportCustomer();
		boolean flag = isImportCustomerDialogDisplayed();
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToPrintBarCode(String permission) {
		clickPrintBarcode();
		boolean flag = isPrintBarcodeDialogDisplayed();
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(flag);
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    
    /*-------------------------------------*/ 
	
}
