package web.Dashboard;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.PermissionAPI.PermissionInformation;
import api.Seller.setting.StaffManagement;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.permission.management.PermissionManagementPage;

/**
 *<p>Ticket: https://mediastep.atlassian.net/browse/BH-25477</p>
 *<p>Preconditions: There exist at least 3 staff members and 1 permission group</p>
 */

public class PermissionPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	LoginInformation subordinate1Credentials;
	LoginInformation subordinate2Credentials;
	PermissionAPI permissionAPI;
	
	PermissionInformation permissionInfoModel;
	
	LoginPage loginPage;
	HomePage homePage;
	PermissionManagementPage permissionPage;
	
	int permissionGroupId;
	int subordinate1StaffId;
	int subordinate2StaffId;
	
	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		subordinate1Credentials = new Login().setLoginInformation("+84", "staff.b@mailnesia.com", "fortesting!1").getLoginInformation();
		subordinate2Credentials = new Login().setLoginInformation("+84", "staff.c@mailnesia.com", "fortesting!1").getLoginInformation();
		subordinate1StaffId = new StaffManagement(ownerCredentials).getStaffId(new Login().getInfo(subordinate1Credentials).getUserId());
		subordinate2StaffId = new StaffManagement(ownerCredentials).getStaffId(new Login().getInfo(subordinate2Credentials).getUserId());
		
		permissionAPI = new PermissionAPI(ownerCredentials);
		
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		permissionPage = new PermissionManagementPage(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	
	
	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.getAllPermissionGroupInformation().getPermissionIds().stream().filter(id-> id >= permissionGroupId).forEach(id-> permissionAPI.deleteGroupPermission(id));
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
    }	
	
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_permission(permissionBinary);
		return model;
	}
	
	@Test(dataProvider = "settingPermissionPermission", dataProviderClass = PermissionDataProvider.class)
	public void CheckPermissionPermission(String permissionBinary) {
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getSetting().getPermission());
		
		if (allPermissionDTO.getSetting().getPermission().isAddStaffToPermissionGroup()) {
			permissionAPI.removeGroupPermissionFromStaff(subordinate1StaffId, permissionGroupId);
			permissionAPI.removeGroupPermissionFromStaff(subordinate2StaffId, permissionGroupId);
			permissionAPI.grantGroupPermissionToStaff(subordinate2StaffId, permissionGroupId);
		}

		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(2000, "OMG");
		
		int deletedGroup = (allPermissionDTO.getSetting().getPermission().isViewPermissionGroupList() && allPermissionDTO.getSetting().getPermission().isDeletePermissionGroup()) ? 
				permissionAPI.createGroupPermissionAndGetID("Deleted Auto Group", "", setPermissionModel(permissionBinary)) : permissionGroupId;
		
		permissionPage.checkSetPermissionPermission(allPermissionDTO, permissionGroupId, subordinate1Credentials.getEmail(), subordinate2Credentials.getEmail(), deletedGroup);
		
	}		
}
