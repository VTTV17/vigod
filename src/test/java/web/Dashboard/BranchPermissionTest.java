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
import web.Dashboard.settings.branch_management.BranchPage;

/**
 * <p>Ticket: https://mediastep.atlassian.net/browse/BH-25478</p>
 * <p>Preconditions: There exists at least 1 FREE branch</p>
 */

public class BranchPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	
	LoginPage loginPage;
	HomePage homePage;
	BranchPage branchPage;
	
	int permissionGroupId;
	
	@BeforeClass
	void loadTestData() {
//		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
//		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		ownerCredentials = new Login().setLoginInformation("+84", "automation0-shop74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff74053@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		
//    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
//    	permissionGroupId = 1910;
    	permissionGroupId = 5645;
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		branchPage = new BranchPage(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	

	@AfterClass
	void deletePermissionGroup() {
//		permissionAPI.deleteGroupPermission(permissionGroupId);
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	
	
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_branchManagement(permissionBinary);
		return model;
	}

	@DataProvider
	public Object[][] branchPermission() {
		return new Object[][] { 
//			{"0000000"},
//			{"0000001"},
//			{"0000010"},
//			{"0000011"},
//			{"0000100"},
//			{"0000101"},
//			{"0000110"},
//			{"0000111"},
//			{"0001000"}, //Bug2 Case 120
//			{"0001001"}, //Bug2 Case 56
//			{"0001010"}, //Bug2 Case 88
//			{"0001011"}, //Bug2 Case 24
//			{"0001100"}, //Bug2 Case 104
//			{"0001101"}, //Bug2 Case 40
//			{"0001110"}, //Bug2 Case 72
//			{"0001111"}, //Bug2 Case 8
//			{"0010000"}, //Bug2 Case 124
//			{"0010001"}, //Bug2 Case 60
//			{"0010010"}, //Bug2 Case 92
//			{"0010011"}, //Bug2 Case 28
//			{"0010100"}, //Bug2 Case 108
//			{"0010101"}, //Bug2 Case 44
//			{"0010110"}, //Bug2 Case 76
//			{"0010111"}, //Bug2 Case 12
//			{"0011000"}, //Bug2 Case 44
//			{"0011001"}, //Bug2 Case 52
//			{"0011010"}, //Bug2 Case 84
//			{"0011011"}, //Bug2 Case 20
//			{"0011100"}, //Bug2 Case 100
//			{"0011101"}, //Bug2 Case 36
//			{"0011110"}, //Bug2 Case 68
//			{"0011111"}, //Bug2 Case 4
//			{"0100000"}, //Bug1 Case 126 
//			{"0100001"}, //Bug1 Case 62
//			{"0100010"}, //Bug1 Case 94
//			{"0100011"}, //Bug1 Case 30
//			{"0100100"}, //Bug1 Case 110
//			{"0100101"}, //Bug1 Case 46
//			{"0100110"}, //Bug1 Case 78
//			{"0100111"}, //Bug1 Case 14
//			{"0101000"}, //Bug1 Case 118
//			{"0101001"}, //Bug1 Case 54
//			{"0101010"}, //Bug1 Case 86
//			{"0101011"}, //Bug1 Case 22
//			{"0101100"}, //Bug1 Case 102
//			{"0101101"}, //Bug1 Case 38
//			{"0101110"}, //Bug1 Case 70
//			{"0101111"}, //Bug1 Case 6
//			{"0110000"}, //Bug2 Case 122
//			{"0110001"}, //Bug2 Case 58
//			{"0110010"}, //Bug2 Case 90
//			{"0110011"}, //Bug2 Case 26
//			{"0110100"}, //Bug2 Case 106
//			{"0110101"}, //Bug2 Case 42
//			{"0110110"}, //Bug2 Case 74
//			{"0110111"}, //Bug2 Case 106
//			{"0111000"},
//			{"0111001"},
//			{"0111010"},
//			{"0111011"},
//			{"0111100"},
//			{"0111101"},
//			{"0111110"},
//			{"0111111"},
//			{"1000000"},
//			{"1000001"},
//			{"1000010"},
//			{"1000011"},
//			{"1000100"},
//			{"1000101"},
//			{"1000110"},
//			{"1000111"},
//			{"1001000"}, //Bug2 Case 119
//			{"1001001"}, //Bug2 Case 55
//			{"1001010"}, //Bug2 Case 87
//			{"1001011"}, //Bug2 Case 23
//			{"1001100"}, //Bug2 Case 103
//			{"1001101"}, //Bug2 Case 39
//			{"1001110"}, //Bug2 Case 71
//			{"1001111"}, //Bug2 Case 7
//			{"1010000"}, //Bug2 Case 123
//			{"1010001"}, //Bug2 Case 59
//			{"1010010"}, //Bug2 Case 91
//			{"1010011"}, //Bug2 Case 27
//			{"1010100"}, //Bug2 Case 107
//			{"1010101"}, //Bug2 Case 43
//			{"1010110"}, //Bug2 Case 75
//			{"1010111"}, //Bug2 Case 11
//			{"1011000"}, //Bug2 Case 115
//			{"1011001"}, //Bug2 Case 51
//			{"1011010"}, //Bug2 Case 83
//			{"1011011"}, //Bug2 Case 19
//			{"1011100"}, //Bug2 Case 99
//			{"1011101"}, //Bug2 Case 35
//			{"1011110"}, //Bug2 Case 67
//			{"1011111"}, //Bug2 Case 3
//			{"1100000"}, //Bug1 Case 125
//			{"1100001"}, //Bug1 Case 61
//			{"1100010"}, //Bug1 Case 93
//			{"1100011"}, //Bug1 Case 29
//			{"1100100"}, //Bug1 Case 109
//			{"1100101"}, //Bug1 Case 45
//			{"1100110"}, //Bug1 Case 77
//			{"1100111"}, //Bug1 Case 13
//			{"1101000"}, //Bug1 Case 117
//			{"1101001"}, //Bug1 Case 53
//			{"1101010"}, //Bug1 Case 85
//			{"1101011"}, //Bug1 Case 21
//			{"1101100"}, //Bug1 Case 101
//			{"1101101"}, //Bug1 Case 37
//			{"1101110"}, //Bug1 Case 69
//			{"1101111"}, //Bug1 Case 5
//			{"1110000"}, //Bug2 Case 121
//			{"1110001"}, //Bug2 Case 57
//			{"1110010"}, //Bug2 Case 89
//			{"1110011"}, //Bug2 Case 25
//			{"1110100"}, //Bug2 Case 105
//			{"1110101"}, //Bug2 Case 41
//			{"1110110"}, //Bug2 Case 73
//			{"1110111"}, //Bug2 Case 9
//			{"1111000"},
//			{"1111001"},
//			{"1111010"},
//			{"1111011"},
//			{"1111100"},
			{"1111101"},
			{"1111110"},
			{"1111111"},
		};
	}		
	
	@Test(dataProvider = "branchPermission")
	public void CheckBranchPermission(String permissionBinary) {
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getSetting().getBranchManagement());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(2000, "OMG");
		
		branchPage.checkBranchPermission(allPermissionDTO);
	}		
}
