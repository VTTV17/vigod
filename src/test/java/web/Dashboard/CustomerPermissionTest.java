package web.Dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APIAllCustomers.CustomerManagementInfo;
import api.Seller.customers.APIEditCustomer;
import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import api.Seller.mediaservices.MediaServices.ExportHistoryInfo;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.PermissionAPI;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.utils.ListUtils;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.segments.Segments;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;

public class CustomerPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	APIEditCustomer editCustomerAPI;
	APISegment segmentAPI;
	BranchManagement branchManagmentAPI;
	CustomerManagementInfo customerManagementInfoAPI;
	
	int permissionGroupId;
	
	String assignedCustomerName;
	int assignedCustomerId;
	String assignedUserId;
	String assignedCustomerChannel;
	
	String unassignedCustomerName;
	int unassignedCustomerId;
	String unassignedUserId;
	String unassignedCustomerChannel;
	
	int segmentDesignatedForEditing;
	
	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		editCustomerAPI = new APIEditCustomer(ownerCredentials);
		segmentAPI = new APISegment(ownerCredentials);
		customerManagementInfoAPI = new APIAllCustomers(ownerCredentials).getCustomerManagementInfo();
		
		preConditionSetup();
	}	

	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
		segmentAPI.deleteSegment(segmentDesignatedForEditing);
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	

    void preConditionSetup() {
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);;
    	
		int staffUserId = new Login().getInfo(staffCredentials).getUserId();
		
		List<Object> customerHavingDebt = getCustomerHavingDebtAndAssignedToStaff(staffUserId);
		assignedCustomerName = (String)customerHavingDebt.get(0);
		assignedCustomerId = (Integer)customerHavingDebt.get(1);
		assignedUserId = (String)customerHavingDebt.get(2);
		assignedCustomerChannel = (String)customerHavingDebt.get(3);
		
		List<Object> customerNoStaff = getCustomerNotAssignedStaff();
		unassignedCustomerName = (String)customerNoStaff.get(0);
		unassignedCustomerId = (Integer)customerNoStaff.get(1);
		unassignedUserId = (String)customerNoStaff.get(2);
		unassignedCustomerChannel = (String)customerNoStaff.get(3);
		
		segmentDesignatedForEditing = createCustomerSegment();
    }
    
	int createCustomerSegment() {
		segmentAPI.createSegment();
		return segmentAPI.getSegmentID();
	}    
    
	boolean isFileExportedByStaffAndDownloadable(ExportHistoryInfo exportHistoryData, int staffId) {
		for (int i=0; i<exportHistoryData.getId().size(); i++) {
			Integer exportedFileStaffId = exportHistoryData.getExportByStaffId().get(i);
			if (exportedFileStaffId ==null || exportedFileStaffId != staffId) continue;
			if (!exportHistoryData.getDataType().get(i).equals("CUSTOMER")) continue;
			if (exportHistoryData.getIsExpired().get(i)) continue;
			return true;
		}
		return false;
	}

	/**
	 * Returns a list containing customerName, customerId, userId, saleChannel and staffUserId respectively
	 */
	List<Object> getCustomerHavingDebtAndAssignedToStaff(int staffUserId) {
		List<Object> customerInfo = new ArrayList<>();
		for (int i=0; i<customerManagementInfoAPI.getCustomerId().size(); i++) {
			if (customerManagementInfoAPI.getDebtAmount().get(i) ==0) continue; 
			customerInfo.add(customerManagementInfoAPI.getCustomerName().get(i));
			customerInfo.add(customerManagementInfoAPI.getCustomerId().get(i));
			customerInfo.add(customerManagementInfoAPI.getUserId().get(i));
			customerInfo.add(customerManagementInfoAPI.getSaleChannel().get(i));
			editCustomerAPI.assignStaffToCustomer(staffUserId, customerManagementInfoAPI.getCustomerId().get(i));
			customerInfo.add(staffUserId);
			return customerInfo;
		}
		return null;
	}	
	
	/**
	 * Returns a list containing customerName, customerId, userId and saleChannel respectively
	 */
	List<Object> getCustomerNotAssignedStaff() {
		List<Object> customerInfo = new ArrayList<>();
		for (int i=0; i<customerManagementInfoAPI.getCustomerId().size(); i++) {
			if (customerManagementInfoAPI.getResponsibleStaffUserId().get(i) == null || customerManagementInfoAPI.getResponsibleStaffUserId().get(i) <0) {
				if (customerManagementInfoAPI.getTotalOrder().get(i) ==0) continue;
				customerInfo.add(customerManagementInfoAPI.getCustomerName().get(i));
				customerInfo.add(customerManagementInfoAPI.getCustomerId().get(i));
				customerInfo.add(customerManagementInfoAPI.getUserId().get(i));
				customerInfo.add(customerManagementInfoAPI.getSaleChannel().get(i));
				return customerInfo;
			}
		}
		return null;
	}	
	
	CreatePermission setCustomerPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_staffManagement("1");
		model.setAffiliate_dropshipPartner("1");
		model.setMarketing_loyaltyPoint("1");
		model.setProduct_productManagement(ListUtils.getRandomListElement(Arrays.asList(new String[] {"00", "01", "10", "11"})));
		model.setCustomer_customerManagement(permissionBinary);
		return model;
	}
	
	CreatePermission setSegmentCustomerPermissionModel(String permissionBinary) {
		CreatePermission model = setCustomerPermissionModel(ListUtils.getRandomListElement(Arrays.asList(new String[] {"00", "01", "10", "11"})));
		model.setCustomer_segment(permissionBinary);
		return model;
	}
	
	@Test(dataProvider = "customerPermission", dataProviderClass = PermissionDataProvider.class)
	public void CC_01_CheckCustomerPermission(String permissionBinary) {
		
		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage = new HomePage(driver);
		AllCustomers customerPage = new AllCustomers(driver);
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setCustomerPermissionModel(permissionBinary));		
		
		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
		
		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());
		customerPage.checkCustomerPermission(allPermissionDTO, unassignedCustomerName, assignedCustomerName, assignedCustomerId, Integer.valueOf(assignedUserId), assignedCustomerChannel);
	}		
	
	@Test(dataProvider = "customerSegmentPermission", dataProviderClass = PermissionDataProvider.class)
	public void CC_02_CheckCustomerSegmentPermission(String permissionBinary) {
		
		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage = new HomePage(driver);
		Segments segmentPage = new Segments(driver);
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setSegmentCustomerPermissionModel(permissionBinary));		
		
		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
		
		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());
		
		int segmentDesignatedForDeletion = allPermissionDTO.getCustomer().getSegment().isViewSegmentList() && allPermissionDTO.getCustomer().getSegment().isDeleteSegment() ? createCustomerSegment() : segmentDesignatedForEditing;
		
		segmentPage.checkCustomerSegmentPermission(allPermissionDTO, segmentDesignatedForEditing, segmentDesignatedForDeletion, "Tien's Jacket", "Staff A's Dog Food");
	}		
}
