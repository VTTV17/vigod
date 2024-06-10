package web.Dashboard.customers.allcustomers;

import static utilities.links.Links.DOMAIN;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.allcustomers.analytics.CustomerAnalytics;
import web.Dashboard.customers.allcustomers.create_customer.CreateCustomerPopup;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.Dashboard.home.HomePage;

public class AllCustomers {

	final static Logger logger = LogManager.getLogger(AllCustomers.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	AllCustomerElement elements;

	public AllCustomers(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new AllCustomerElement();
	}

	public AllCustomers navigate() {
		homePage.navigateToPage("Customers");
		waitTillDataPresent();
		return this;
	}
	
	public AllCustomers navigateByURL() {
		String url = DOMAIN + "/customers/all-customers/list";
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}
	
	public AllCustomers navigateToCustomerDetailScreenByURL(int customerProfileId, int userId, String channel) {
		String url = DOMAIN + "/customers/all-customers/edit/%s/%s/%s".formatted(customerProfileId, userId, channel);
		driver.get(url);
		homePage.waitTillSpinnerDisappear1();
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		return this;
	}

	/**
	 * Merely wait 2 seconds till records are displayed
	 */
	void waitTillDataPresent() {
		for (int i=0; i<4; i++) {
			if (!commonAction.getElements(elements.loc_tmpAnchor).isEmpty()) break;
			commonAction.sleepInMiliSecond(500, "Records not displayed. Waiting a little more.");
		}
	}

	//Updates needed
	public List<String> readCustomerNames() {
		List<String> names = new ArrayList<String>();
		for (int i=0; i<commonAction.getElements(elements.loc_tblNames).size(); i++) {
			names.add(commonAction.getText(elements.loc_tblNames, i));
		}
		logger.info("Retrieved customer names from customer management table.");
		return names;
	}	
	
	public AllCustomers clickCreateCustomerButton() {
		commonAction.click(elements.loc_btnCreateCustomer);
		logger.info("Clicked on 'Create new customer' button.");
		return this;
	}

    
    public boolean isCreateCustomerDialogDisplayed(){
    	commonAction.sleepInMiliSecond(1000, "Wait for create customer dialog to appear");
    	boolean isDisplayed = !commonAction.getElements(elements.loc_dlgCreateCustomer).isEmpty();
    	logger.info("isCreateCustomerDialogDisplayed: " + isDisplayed);
    	return isDisplayed;
    }  	
	
	public AllCustomers clickExport() {
		commonAction.click(elements.loc_btnExport);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public AllCustomers clickExportCustomer() {
		if (commonAction.isElementVisiblyDisabled(new ByChained(elements.loc_btnExportCustomer, elements.loc_tmpParent))) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnExportCustomer));
			return this;
		}
		commonAction.click(elements.loc_btnExportCustomer);
		logger.info("Clicked on 'Export Customer' button.");
		return this;
	}	

	public AllCustomers clickExportHistory() {
		commonAction.click(elements.loc_btnExportHistory);
		logger.info("Clicked on 'Export History' button.");
		return this;
	}	
	
	public AllCustomers clickImportCustomer() {
		if (commonAction.isElementVisiblyDisabled(new ByChained(elements.loc_btnImportCustomer, elements.loc_tmpParent))) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_btnImportCustomer));
			return this;
		}
		commonAction.click(elements.loc_btnImportCustomer);
		logger.info("Clicked on 'Import Customer' button.");
		return this;
	}	

	public AllCustomers clickMergeButton() {
		commonAction.click(elements.loc_btnMergeCustomer);
		logger.info("Clicked on 'Merge' button.");
		return this;
	}	
	
    public boolean isDeleteCustomerConfirmationDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.getElements(elements.loc_dlgDeleteCustomer).isEmpty();
    }   	
	
    public boolean isMergeCustomerDialogDisplayed(){
    	commonAction.sleepInMiliSecond(1000, "Wait for merge customer dialog to appear");
    	boolean isDisplayed = !commonAction.getElements(elements.loc_dlgMergeCustomer).isEmpty();
    	logger.info("isMergeCustomerDialogDisplayed: " + isDisplayed);
    	return isDisplayed;
    }  	
	
    public AllCustomers clickPrintBarcode() {
    	if (commonAction.isElementVisiblyDisabled(new ByChained(elements.loc_btnPrintBarcode, elements.loc_tmpParent))) {
    		Assert.assertFalse(homePage.isElementClicked(elements.loc_btnPrintBarcode));
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
		homePage.waitTillSpinnerDisappear1();
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
		homePage.hideFacebookBubble();
		clickFilterIcon();
		clickBranchList();
		commonAction.click(By.xpath(elements.loc_ddlFilterBranchValues.formatted(branch)));
		logger.info("Selected branch: " + branch);
		clickFilterDoneBtn();
		homePage.waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickUser(String customerName) {
		homePage.hideFacebookBubble();
		commonAction.click(By.xpath(elements.loc_lblCustomerName.formatted(customerName)));
		logger.info("Clicked on user: " + customerName);
		homePage.waitTillSpinnerDisappear();
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
		commonAction.sleepInMiliSecond(2000, "wait shown result.");
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
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToImportCustomer(String permission) {
		clickImportCustomer();
		boolean flag = isImportCustomerDialogDisplayed();
		commonAction.refreshPage();
		homePage.waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToPrintBarCode(String permission) {
		clickPrintBarcode();
		boolean flag = isPrintBarcodeDialogDisplayed();
		commonAction.refreshPage();
		homePage.waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(flag);
    	} else {
    		Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
    	}
    }
    
    /*-------------------------------------*/ 
    
    boolean permissionToViewAllCustomerList(AllPermissions staffPermission) {
    	return staffPermission.getCustomer().getCustomerManagement().isViewAllCustomerList();
    }
    boolean permissionToViewAssignedCustomerList(AllPermissions staffPermission) {
    	return staffPermission.getCustomer().getCustomerManagement().isViewAssignedCustomerList();
    }
    boolean permissionToViewGeneralInfo(AllPermissions staffPermission) {
    	return staffPermission.getCustomer().getCustomerManagement().isViewCustomerGeneralInformation();
    }
    boolean permissionToViewBankInfo(AllPermissions staffPermission) {
    	return staffPermission.getCustomer().getCustomerManagement().isViewCustomerBankInformation();
    }
    boolean isAccessRestrictedPresent() {
    	return new CheckPermission(driver).isAccessRestrictedPresent();
    }
    
    void checkPermissionToViewCustomerList(AllPermissions staffPermission, String unassignedName, String assignedName) {
    	navigateByURL();
    	waitTillDataPresent();
    	
    	List<String> names = readCustomerNames();
    	
    	System.out.println(names.toString());
    	
    	if (permissionToViewAllCustomerList(staffPermission)) {
    		Assert.assertTrue(names.contains(unassignedName));
    		Assert.assertTrue(names.contains(assignedName));
    	} else {
    		if (permissionToViewAssignedCustomerList(staffPermission)) {
        		Assert.assertFalse(names.contains(unassignedName));
        		Assert.assertTrue(names.contains(assignedName));
    		} else {
    			Assert.assertEquals(names.size(), 0);
    		}
    	}
    	
    	logger.info("Finished checkPermissionToViewCustomerList");
    }      
    
    void checkPermissionToViewGeneralInfo(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	customerDetailPage.clickGeneralInfoTab();
    	
    	if (staffPermission.getCustomer().getCustomerManagement().isViewCustomerGeneralInformation()) {
    		customerDetailPage.inputNote("Test Permission");
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToViewGeneralInfo");
    }      

    void checkPermissionToViewBankInfo(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	customerDetailPage.clickBankInfoTab();
    	
    	if (staffPermission.getCustomer().getCustomerManagement().isViewCustomerBankInformation()) {
    		customerDetailPage.inputBankBranchName("Go Vap");
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToViewBankInfo");
    }      
    
    void checkPermissionToAddCustomer(AllPermissions staffPermission) {
    	navigateByURL();
    	clickCreateCustomerButton();
    	if (staffPermission.getCustomer().getCustomerManagement().isAddCustomer()) {
    		Assert.assertTrue(isCreateCustomerDialogDisplayed()); //Remember to actually fill in the form and click Add button
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToAddCustomer");
    }     
    
    void checkPermissionToDeleteCustomer(AllPermissions staffPermission) {
    	
    	if (!permissionToViewAllCustomerList(staffPermission) && !permissionToViewAssignedCustomerList(staffPermission)) {
    		logger.info("Permissions to view customer list is false. Skipping checkPermissionToDeleteCustomer");
    		return;
    	}
    	
    	navigateByURL();

    	commonAction.click(elements.loc_chkSelectCustomer, 1);
    	commonAction.click(elements.loc_lnkSelectAction);
    	commonAction.click(elements.loc_btnDelete);
    	
    	if (staffPermission.getCustomer().getCustomerManagement().isDeleteCustomer()) {
    		Assert.assertTrue(isDeleteCustomerConfirmationDialogDisplayed()); //Remember to actually delete
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	
    	logger.info("Finished checkPermissionToImportCustomer");
    }    
    
    void checkPermissionToImportCustomer(AllPermissions staffPermission) {
    	navigateByURL();
    	clickImportCustomer();
    	if (staffPermission.getCustomer().getCustomerManagement().isImportCustomer()) {
    		Assert.assertTrue(isImportCustomerDialogDisplayed()); //Remember to actually import file
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	
    	logger.info("Finished checkPermissionToImportCustomer");
    }    
    
    void checkPermissionToEditInfo(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	commonAction.sleepInMiliSecond(1000, "Wait a little for profile to load");
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	for (int i=0; i<2; i++) {
    		boolean isViewDetailPermissionGranted;
    		if (i==0){
    			isViewDetailPermissionGranted = permissionToViewGeneralInfo(staffPermission);
    			customerDetailPage.clickGeneralInfoTab();
    			if (!isViewDetailPermissionGranted) continue;
    			customerDetailPage.inputNote("Test permission");
    			customerDetailPage.clickGeneralInfoTab();
    		} else {
    			isViewDetailPermissionGranted = permissionToViewBankInfo(staffPermission);
    			customerDetailPage.clickBankInfoTab();
    			if (!isViewDetailPermissionGranted) continue;
    			customerDetailPage.inputBankBranchName("Go Vap");
    			customerDetailPage.clickBankInfoTab();
    		}
    		customerDetailPage.clickSaveBtn();
    		
    		if (staffPermission.getCustomer().getCustomerManagement().isEditCustomerInformation()) {
    			new HomePage(driver).getToastMessage();
    		} else {
    			Assert.assertTrue(isAccessRestrictedPresent());
    		}
    	}
    	logger.info("Finished checkPermissionToEditInfo");
    }    
    
    void checkPermissionToAssignPartner(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	if (!permissionToViewGeneralInfo(staffPermission)) {
    		logger.info("permissionToViewGeneralInfo is false. Skipping checkPermissionToAssignPartner");
    		return;
    	}
    	
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	customerDetailPage.selectRandomPartner();
    	
    	if (staffPermission.getCustomer().getCustomerManagement().isAssignPartner()) {
    		Assert.assertFalse(customerDetailPage.isAssignPartnerListExpanded());
    		//Need to click save button?
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	
    	logger.info("Finished checkPermissionToAssignPartner");
    }    
    
    void checkPermissionToAssignStaff(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	if (!permissionToViewGeneralInfo(staffPermission)) {
    		logger.info("permissionToViewGeneralInfo is false. Skipping checkPermissionToAssignStaff");
    		return;
    	}
    	
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	customerDetailPage.selectRandomStaff();
    	
    	if (staffPermission.getCustomer().getCustomerManagement().isAssignStaff()) {
    		//Update later
    		//Need to click save button?
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	
    	logger.info("Finished checkPermissionToAssignStaff");
    }    

    void checkPermissionToUpdateStatus(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	if (!permissionToViewGeneralInfo(staffPermission)) {
    		logger.info("permissionToViewGeneralInfo is false. Skipping checkPermissionToUpdateStatus");
    		return;
    	}
    	
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	customerDetailPage.selectRandomStatus();
    	
		if (staffPermission.getCustomer().getCustomerManagement().isUpdateStatus()) {
			Assert.assertNotEquals(customerDetailPage.getSelectedStatus().length(), 0);
			//Need to click save button?
		} else {
			Assert.assertTrue(isAccessRestrictedPresent());
		}
    	
    	logger.info("Finished checkPermissionToUpdateStatus");
    }    
    
    void checkPermissionToMergeCustomer(AllPermissions staffPermission) {
    	navigateByURL();
    	
    	clickMergeButton();
    	
		if (staffPermission.getCustomer().getCustomerManagement().isMergeCustomer()) {
			Assert.assertTrue(isMergeCustomerDialogDisplayed());
		} else {
			Assert.assertTrue(isAccessRestrictedPresent());
		}
    	
    	logger.info("Finished checkPermissionToMergeCustomer");
    }    
    
    void checkPermissionToSeeCustomerAnalytics(AllPermissions staffPermission) {
    	
    	CustomerAnalytics customerAnalyticsPage = new CustomerAnalytics(driver);
    	
    	customerAnalyticsPage.navigateByURL();

    	if (staffPermission.getCustomer().getCustomerManagement().isCustomerAnalytics()) {
    		customerAnalyticsPage.clickUpdate();
    		//Need to check further?
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	
    	logger.info("Finished checkPermissionToSeeCustomerAnalytics");
    }
    
    void checkPermissionToConfirmPayment(AllPermissions staffPermission, int customerId, int userId, String channel) {
    	
    	navigateToCustomerDetailScreenByURL(customerId, userId, channel);
    	
    	if (!permissionToViewGeneralInfo(staffPermission)) {
    		logger.info("permissionToViewGeneralInfo is false. Skipping checkPermissionToConfirmPayment");
    		return;
    	}
    	
    	CustomerDetails customerDetailPage = new CustomerDetails(driver);
    	customerDetailPage.clickConfirmPaymentBtn();
		if (staffPermission.getCustomer().getCustomerManagement().isConfirmPayment()) {
			Assert.assertTrue(customerDetailPage.isPaymentConfirmationDialogDisplayed());
		} else {
			Assert.assertTrue(isAccessRestrictedPresent());
		}
    	
    	logger.info("Finished checkPermissionToConfirmPayment");
    }
    
    void checkPermissionToExportCustomer(AllPermissions staffPermission) {
    	navigateByURL();
    	clickExport();
    	clickExportCustomer();
    	if (staffPermission.getCustomer().getCustomerManagement().isExportCustomer()) {
    		new ConfirmationDialog(driver).clickCancelBtn(); //Remember to actually export file
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToExportCustomer");
    }
    
    void checkPermissionToDownloadExportedCustomer(AllPermissions staffPermission) {
    	navigateByURL();
    	clickExport();
    	clickExportHistory();
    	if (staffPermission.getCustomer().getCustomerManagement().isDownloadExportedCustomer()) {
    		//Remember to export files first
    		commonAction.sleepInMiliSecond(2000, "Wait in checkPermissionToDownloadExportedCustomer");
    		Assert.assertNotEquals(commonAction.getElements(elements.loc_icnDownloadExportFile).size(), 0);
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToDownloadExportedCustomer");
    }
    
    void checkPermissionToPrintBarcode(AllPermissions staffPermission) {
    	navigateByURL();
    	clickPrintBarcode();
    	if (staffPermission.getCustomer().getCustomerManagement().isPrintBarcode()) {
    		Assert.assertTrue(isPrintBarcodeDialogDisplayed());
    		//Remember to check customer list in the dialog 
    	} else {
    		Assert.assertTrue(isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToPrintBarcode");
    }
    
    //https://mediastep.atlassian.net/browse/BH-13820
    public void checkCustomerPermission(AllPermissions staffPermission, String unassignedCustomer, String assignedCustomer, int customerId, int userId, String saleChannel) {
    	checkPermissionToViewCustomerList(staffPermission, unassignedCustomer, assignedCustomer);
    	checkPermissionToViewGeneralInfo(staffPermission, customerId, userId, saleChannel);
    	checkPermissionToViewBankInfo(staffPermission, customerId, userId, saleChannel);
    	//Tab action is currently disabled
    	checkPermissionToAddCustomer(staffPermission);
    	checkPermissionToDeleteCustomer(staffPermission);
    	checkPermissionToImportCustomer(staffPermission);
    	checkPermissionToEditInfo(staffPermission, customerId, userId, saleChannel);
    	checkPermissionToAssignPartner(staffPermission, customerId, userId, saleChannel);
    	checkPermissionToAssignStaff(staffPermission, customerId, userId, saleChannel);
    	checkPermissionToUpdateStatus(staffPermission, customerId, userId, saleChannel);
    	checkPermissionToMergeCustomer(staffPermission);
    	checkPermissionToSeeCustomerAnalytics(staffPermission);
    	checkPermissionToConfirmPayment(staffPermission, customerId, userId, saleChannel);
    	checkPermissionToExportCustomer(staffPermission);
    	checkPermissionToDownloadExportedCustomer(staffPermission);
    	checkPermissionToPrintBarcode(staffPermission);
    }      
    
}
