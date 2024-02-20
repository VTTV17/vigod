package web.Dashboard.customers.allcustomers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.allcustomers.create_customer.CreateCustomerPopup;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.Dashboard.home.HomePage;

public class AllCustomers extends HomePage {

	final static Logger logger = LogManager.getLogger(AllCustomers.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllCustomerElement elements;

	public AllCustomers(WebDriver driver) {
		super(driver);
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		elements = new AllCustomerElement();
	}

	public AllCustomers navigate() {
		navigateToPage("Customers");
		commonAction.sleepInMiliSecond(2000);
		return this;
	}

	public AllCustomers clickExport() {
		commonAction.click(elements.loc_btnExport);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public AllCustomers clickExportCustomer() {
		if (commonAction.isElementVisiblyDisabled(new ByChained(elements.loc_btnExportCustomer, elements.loc_tmpParent))) {
			Assert.assertFalse(isElementClicked(elements.loc_btnExportCustomer));
			return this;
		}
		commonAction.click(elements.loc_btnExportCustomer);
		logger.info("Clicked on 'Export Customer' button.");
		return this;
	}	
	
	public AllCustomers clickImportCustomer() {
		if (commonAction.isElementVisiblyDisabled(new ByChained(elements.loc_btnImportCustomer, elements.loc_tmpParent))) {
			Assert.assertFalse(isElementClicked(elements.loc_btnImportCustomer));
			return this;
		}
		commonAction.click(elements.loc_btnImportCustomer);
		logger.info("Clicked on 'Import Customer' button.");
		return this;
	}	

    public AllCustomers clickPrintBarcode() {
    	if (commonAction.isElementVisiblyDisabled(new ByChained(elements.loc_btnPrintBarcode, elements.loc_tmpParent))) {
    		Assert.assertFalse(isElementClicked(elements.loc_btnPrintBarcode));
    		return this;
    	}
    	commonAction.click(elements.loc_btnPrintBarcode);
    	logger.info("Clicked on 'Print Barcode' button.");
    	return this;
    }	
	
    public boolean isImportCustomerDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.getElements(elements.loc_dlgImportCustomer).isEmpty();
    }   	

    public boolean isPrintBarcodeDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.getElements(elements.loc_dlgPrintBarcode).isEmpty();
    }       
    
	public AllCustomers inputSearchTerm(String searchTerm) {
		commonAction.sendKeys(elements.loc_txtSearchCustomer, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickFilterIcon() {
		commonAction.click(elements.loc_btnFilter);
		logger.info("Clicked on Filter icon.");
		return this;
	}

	public AllCustomers clickFilterDoneBtn() {
		commonAction.click(elements.loc_btnDoneFilter);
		logger.info("Clicked on Filter Done button.");
		return this;
	}

	public AllCustomers clickBranchList() {
		commonAction.click(elements.loc_ddlFilterBranch);
		logger.info("Clicked on Branch list.");
		return this;
	}

	public AllCustomers selectBranch(String branch) {
		hideFacebookBubble();
		clickFilterIcon();
		clickBranchList();
		commonAction.click(By.xpath(elements.loc_ddlFilterBranchValues.formatted(branch)));
		logger.info("Selected branch: " + branch);
		clickFilterDoneBtn();
		waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickUser(String customerName) {
		hideFacebookBubble();
		commonAction.click(By.xpath(elements.loc_lblCustomerName.formatted(customerName)));
		logger.info("Clicked on user: " + customerName);
		waitTillSpinnerDisappear();
		return this;
	}

	public String getPhoneNumber(String customerName) {
		String value = commonAction.getText(By.xpath(elements.loc_lblCustomerPhone.formatted(customerName)));
		logger.info("Retrieved phone number: " + value);
		return value;
	}

	public CreateCustomerPopup clickCreateNewCustomerBtn() {
		commonAction.click(elements.loc_btnCreateCustomer);
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
			Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToImportCustomer(String permission) {
		clickImportCustomer();
		boolean flag = isImportCustomerDialogDisplayed();
		commonAction.refreshPage();
		waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToPrintBarCode(String permission) {
		clickPrintBarcode();
		boolean flag = isPrintBarcodeDialogDisplayed();
		commonAction.refreshPage();
		waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(flag);
    	} else {
    		Assert.assertEquals(verifySalePitchPopupDisplay(), 0);
    	}
    }
    
    /*-------------------------------------*/ 
	
}
