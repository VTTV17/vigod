package web.Dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.VAT;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.setting.Tax.TaxEntity;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.vat.VATInformation;


/**
 * <p>Ticket: https://mediastep.atlassian.net/browse/BH-25479</p>
 * <p>Preconditions: There exists a staff</p>
 */

public class VATPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	VAT vatAPI;
	
	TaxEntity originalTax;
	
	DataGenerator randomData;
	
	LoginPage loginPage;
	HomePage homePage;
	VATInformation vatPage;
	
	int permissionGroupId;
	
	int latestTaxId;
	String latestTaxName;
	
	@BeforeClass
	void precondition() {
		ownerCredentials = new Login().setLoginInformation("+84", "automation0-shop74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff74053@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		vatAPI = new VAT(ownerCredentials);
		
		randomData = new DataGenerator();
		
		originalTax = createSellingVAT();
		latestTaxId = originalTax.getId();
		latestTaxName = originalTax.getName();
		
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		vatPage = new VATInformation(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	

	@AfterClass
	void rollback() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
		getExistingVATIds().stream().filter(id-> id >= latestTaxId).forEach(id-> vatAPI.deleteTax(id));
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	
    
    List<Integer> getExistingVATIds() {
    	return Arrays.asList(vatAPI.getVATList()).stream().map(e -> e.getId()).collect(Collectors.toList());
    }    
    
    TaxEntity createSellingVAT() {
    	String name = "Auto Tax " + randomData.randomNumberGeneratedFromEpochTime(5);
    	return vatAPI.createSellingTax(name, "Test Permission", 50, false);
    }
    
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_tax(permissionBinary);
		return model;
	}

	
	@Test(dataProvider = "taxPermission", dataProviderClass = PermissionDataProvider.class)
	public void CheckBranchPermission(String permissionBinary) {
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getSetting().getTAX());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(2000, "OMG");
		
		String deletedTax = (allPermissionDTO.getSetting().getTAX().isDeleteTAX()) ? createSellingVAT().getName() : latestTaxName;
		
		vatPage.checkVATPermission(allPermissionDTO, deletedTax);
	}		
}
