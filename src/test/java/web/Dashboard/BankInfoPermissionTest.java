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
import web.Dashboard.settings.bankaccountinformation.BankAccountInformation;

/**
 *<p>Ticket: https://mediastep.atlassian.net/browse/BH-25475</p>
 */

public class BankInfoPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	
	LoginPage loginPage;
	BankAccountInformation bankinfoPage;
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
		bankinfoPage = new BankAccountInformation(driver);
		
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
		model.setSetting_bankAccount(permissionBinary);
		return model;
	}

	@DataProvider
	public Object[][] bankinfoPermission() {
		return new Object[][] { 
			{"00"},
			{"01"},
//			{"10"}, //Bug https://mediastep.atlassian.net/browse/BH-35715
			{"11"},
		};
	}		
	
	@Test(dataProvider = "bankinfoPermission")
	public void CheckBankInfoPermission(String permissionBinary) {
		
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getSetting().getBankAccount());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(3000, "Wait a little for Dashboard to get new permissionToken"); //Devs have been informed of this abnormal behavior
		
		bankinfoPage.checkBankAccountPermission(allPermissionDTO, ownerCredentials.getPassword());
	}		
}
