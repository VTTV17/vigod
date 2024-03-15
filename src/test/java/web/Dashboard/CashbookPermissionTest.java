package web.Dashboard;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import api.Seller.supplier.supplier.SupplierAPI;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.cashbook.CashbookAPI;
import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;

import web.Dashboard.cashbook.Cashbook;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;

public class CashbookPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	CashbookAPI cashbookAPI;
	Customers customerAPI;
	BranchManagement branchManagmentAPI;
	SupplierAPI supplierAPI;
	StaffManagement staffManagementAPI;
	
	int permissionGroupId;
	String assignedCustomerName;
	String unassignedCustomerName;
	int unassignedCustomerId;
	String branchName;
	int branchId;
	
	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		cashbookAPI = new CashbookAPI(ownerCredentials);
		customerAPI = new Customers(ownerCredentials);
		branchManagmentAPI = new BranchManagement(ownerCredentials);
		supplierAPI = new SupplierAPI(ownerCredentials);
		staffManagementAPI = new StaffManagement(ownerCredentials);
		
		preConditionSetup();
	}	

	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	

    void preConditionSetup() {
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
		int staffUserId = getStaffUserId(staffCredentials);
		assignedCustomerName = getCustomerName(customerAPI, staffUserId);
		unassignedCustomerName = getCustomerName(customerAPI, -1);
		unassignedCustomerId = getCustomerId(customerAPI, -1);
		//Assign a staff member to a customer if the customer doesn't have any responsible staff
		if (assignedCustomerName == null) {
			customerAPI.assignStaffToCustomer(staffUserId, unassignedCustomerId);
			assignedCustomerName = getCustomerName(customerAPI, staffUserId);
			unassignedCustomerName = getCustomerName(customerAPI, -1);
		}
		
		branchName = getActiveBranchName(branchManagmentAPI);
		branchId = getActiveBranchId(branchManagmentAPI);
		
		cashbookAPI.createReceipt(branchId, branchName, "SALE_OF_ASSETS", unassignedCustomerId, unassignedCustomerName);
		cashbookAPI.createPayment(branchId, branchName, "UTILITIES", unassignedCustomerId, unassignedCustomerName);
    }
    
	String getRandomListElement(List<String> list) {
		return list.get(new Random().nextInt(0, list.size()));
	}
	
	String randomSupplier(SupplierAPI supplierAPI) {
		return getRandomListElement(supplierAPI.getAllSupplierNames());
	}		
	
	String randomStaff(StaffManagement staffAPI) {
		return getRandomListElement(staffAPI.getAllStaffNames());
	}		
	
	int getStaffUserId(LoginInformation staffCredentials) {
		return new Login().getInfo(staffCredentials).getUserId();
	}
	int getStaffId(LoginInformation ownerCredentials, LoginInformation staffCredentials) {
		return new StaffManagement(ownerCredentials).getStaffId(getStaffUserId(staffCredentials));
	}
	
	String getCustomerName(Customers customerAPI, int staffUserId) {
		List<String> customerNames = customerAPI.getNamesOfCustomersAssignedToStaff(staffUserId);
		return customerNames.isEmpty() ? null : customerNames.get(0);
	}
	
	int getCustomerId(Customers customerAPI, int staffUserId) {
		List<Integer> customerIds = customerAPI.getIdsOfCustomersAssignedToStaff(staffUserId);
		return customerIds.isEmpty() ? null : customerIds.get(0);
	}
	
	String getActiveBranchName(BranchManagement branchManagementAPI) {
		List<String> activeBranchNames = branchManagementAPI.getInfo().getActiveBranches();
		return activeBranchNames.isEmpty() ? null : activeBranchNames.get(0);
	}
	
	int getActiveBranchId(BranchManagement branchManagementAPI) {
		BranchInfo branchInfo = branchManagementAPI.getInfo();
		
		List<String> allBranchNames = branchInfo.getBranchName();
		List<String> activeBranchNames = branchInfo.getActiveBranches();
		List<Integer> branchIds = branchInfo.getBranchID();

		int matchedIndex = -1;
		for (String branch : activeBranchNames) {
			matchedIndex = allBranchNames.indexOf(branch);
			if (matchedIndex >-1) return branchIds.get(matchedIndex);
		}
		return matchedIndex;
	}
	
	CreatePermission setPermissionModel(String cashbookPermissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		Random rd = new Random();
		if (rd.nextBoolean()) {
			model.setCustomer_customerManagement("11");
			model.setSetting_staffManagement("01");
//			model.setSupplier_supplier("01"); //Bug
		} else if (rd.nextBoolean()) {
//			model.setCustomer_customerManagement("10"); //Bug
			model.setSetting_staffManagement("10");
		} else {
			model.setCustomer_customerManagement("01");
		}
		model.setCashbook_none(cashbookPermissionBinary);
		return model;
	}
	
	@Test(dataProvider = "cashbookPermission", dataProviderClass = PermissionDataProvider.class)
	public void CB_01_CheckCashbookPermission(String permissionBinary) {
		
		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		Cashbook cashbookPage = new Cashbook(driver);
		HomePage homePage = new HomePage(driver);

		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
		
		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());
		if (allPermissionDTO.getCashbook().isDeleteReceiptTransaction()) cashbookAPI.createReceipt(branchId, branchName, "SALE_OF_ASSETS", unassignedCustomerId, unassignedCustomerName);
		if (allPermissionDTO.getCashbook().isDeletePaymentTransaction()) cashbookAPI.createPayment(branchId, branchName, "UTILITIES", unassignedCustomerId, unassignedCustomerName);
		cashbookPage.checkCashbookPermission(allPermissionDTO, unassignedCustomerName, assignedCustomerName, randomSupplier(supplierAPI), randomStaff(staffManagementAPI));
	}		
}
