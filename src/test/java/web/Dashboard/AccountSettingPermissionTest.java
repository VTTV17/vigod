package web.Dashboard;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;
import web.Dashboard.settings.storeinformation.StoreInformation;

public class AccountSettingPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;

	int permissionGroupId;

	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		
		permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
	}

	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
		driver.quit();
	}

	@Override
	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		driver.quit();
	}
	
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_account(permissionBinary);
		return model;
	}
	
	CreatePermission setStoreInfoPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_storeInformation(permissionBinary);
		return model;
	}

	@Test(dataProvider = "accountSettingPermission", dataProviderClass = PermissionDataProvider.class)
	public void CC_01_CheckAccountSettingPermission(String permissionBinary) {

		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage = new HomePage(driver);
		AccountPage accountPage = new AccountPage(driver);

		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description", setPermissionModel(permissionBinary));

		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();

		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());
		
		accountPage.checkAccountSettingPermission(allPermissionDTO, staffCredentials.getPassword());
	}
	
	@Test(dataProvider = "storeInfoSettingPermission", dataProviderClass = PermissionDataProvider.class)
	public void CC_02_CheckStoreInfoSettingPermission(String permissionBinary) {
		
		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage = new HomePage(driver);
		StoreInformation storeInforPage = new StoreInformation(driver);
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description", setStoreInfoPermissionModel(permissionBinary));
		
		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
		
		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());
		
		storeInforPage.checkStoreInfoSettingPermission(allPermissionDTO);
	}
}
