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
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.promotion.buyxgety.BuyXGetYPage;

public class BuyXGetYPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	BuyXGetY buyXGetYAPI;

	int permissionGroupId;
	int productId;

	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		buyXGetYAPI = new BuyXGetY(ownerCredentials);
		
		permissionGroupId = 3671;
		productId = new APIAllProducts(ownerCredentials).getProductIDWithoutVariationAndInStock(false, false, true);
	}

	@AfterClass
	void deletePermissionGroup() {
//		permissionAPI.deleteGroupPermission(permissionGroupId);
	}

	@Override
	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		driver.quit();
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
	public void CC_01_CheckBuyXGetYPermission(String permissionBinary) {

		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage = new HomePage(driver);
		BuyXGetYPage buyXGetYPage = new BuyXGetYPage(driver);

		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description", setPermissionModel(permissionBinary));

		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();

		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());

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

		buyXGetYPage.checkBuyXGetYPermission(allPermissionDTO, 11608134, programIdToEnd, "Tien's Jacket", "Staff A's Dog Food");

		for (int id: createdProgramIds) {
			buyXGetYAPI.deleteBuyXGetYProgram(id);
		}
	}

}
