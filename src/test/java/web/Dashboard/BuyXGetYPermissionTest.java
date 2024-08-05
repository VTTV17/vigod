package web.Dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.promotion.BuyXGetY;
import api.Seller.setting.PermissionAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.promotion.buyxgety.BuyXGetYPage;

/**
 *<p>Ticket: https://mediastep.atlassian.net/browse/BH-24964</p>
 *<p>Some data has been temporarily hard-coded. But improvements are being made</p>
 *<p>Preconditions: Update later</p>
 */

public class BuyXGetYPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	BuyXGetY buyXGetYAPI;
	
	LoginPage loginPage;
	HomePage homePage;
	BuyXGetYPage buyXGetYPage;
	
	int permissionGroupId;
	
	int productId;
	
	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		buyXGetYAPI = new BuyXGetY(ownerCredentials);
		
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
    	productId = new APIAllProducts(ownerCredentials).getProductIDWithoutVariationAndInStock(false, false, true);
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		buyXGetYPage = new BuyXGetYPage(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	

	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	
	
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setProduct_productManagement(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"00", "01", "10", "11"})));
		model.setCustomer_segment(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"1", "0"})));
		model.setProduct_collection(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"1", "0"})));
		model.setHome_none("11");
		model.setPromotion_buyXGetY(permissionBinary);
		return model;
	}
	
	@Test(dataProvider = "buyXGetYPermission", dataProviderClass = PermissionDataProvider.class)
	public void CheckBuyXGetYPermission(String permissionBinary) {
		
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));		
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getPromotion().getBxGy());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(2000, "OMG");
		
		int programIdToEnd = 0;
		List<Integer> createdProgramIds = new ArrayList<>();

		if (allPermissionDTO.getPromotion().getBxGy().isEndBuyXGetY()) {
			createdProgramIds.add(buyXGetYAPI.createBuyXGetYProgram(productId));
			if (allPermissionDTO.getPromotion().getBxGy().isViewBuyXGetYDetail()) {
				programIdToEnd = buyXGetYAPI.createBuyXGetYProgram(productId);
				createdProgramIds.add(programIdToEnd);
			}
		}
		programIdToEnd = programIdToEnd == 0 ? buyXGetYAPI.getProgramByStatus("IN_PROGRESS").get(0) : programIdToEnd;		
		
		buyXGetYPage.checkBuyXGetYPermission(allPermissionDTO, 12592014, programIdToEnd, "Tien's Jacket", "Staff A's Dog Food");
	}		
}
