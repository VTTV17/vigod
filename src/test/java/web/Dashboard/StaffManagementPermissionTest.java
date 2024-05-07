package web.Dashboard;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.staff_management.StaffPage;

/**
 *<p>Preconditions: There exists at least one staff member and a permission group</p>
 */

public class StaffManagementPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	
	LoginPage loginPage;
	StaffPage staffPage;
	HomePage homePage;
	
	int permissionGroupId;
	
	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		staffPage = new StaffPage(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	

	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
    }	
	
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_staffManagement(permissionBinary);
		return model;
	}

	@DataProvider
	public Object[][] staffManagementPermissionBinary() {
		return new Object[][] { 
			{"00000"},
			{"00001"},
			{"00010"},
			{"00011"},
			{"00100"},
			{"00101"},
			{"00110"},
			{"00111"},
			{"01000"},
			{"01001"},
			{"01010"},
			{"01011"},
			{"01100"},
			{"01101"},
			{"01110"},
			{"01111"},
			{"10000"},
			{"10001"},
			{"10010"},
			{"10011"},
			{"10100"},
			{"10101"},
			{"10110"},
			{"10111"},
			{"11000"},
			{"11001"},
			{"11010"},
			{"11011"},
			{"11100"},
			{"11101"},
			{"11110"},
			{"11111"}
		};
	}		
	
	@Test(dataProvider = "staffManagementPermissionBinary")
	public void CheckStaffManagementPermission(String permissionBinary) {
		
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getSetting().getStaffManagement());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(2000, "OMG");
		
		staffPage.checkStaffManagementPermission(allPermissionDTO, ownerCredentials.getPassword());
	}		
}
