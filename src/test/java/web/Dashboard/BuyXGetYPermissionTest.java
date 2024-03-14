package web.Dashboard;

import java.io.IOException;
import java.util.Random;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.customers.SegmentAPI;
import api.Seller.login.Login;
import api.Seller.promotion.CreatePromotion;
import api.Seller.promotion.PromotionList;
import api.Seller.setting.PermissionAPI;
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
	SegmentAPI segmentAPI;
	PromotionList promotionListAPI;
	CreatePromotion createPromotionAPI;
	

	int permissionGroupId;

	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		segmentAPI = new SegmentAPI(ownerCredentials);
		promotionListAPI = new PromotionList(ownerCredentials);
		createPromotionAPI = new CreatePromotion(ownerCredentials);
		
		preConditionSetup();
	}

	@AfterClass
	void deletePermissionGroup() {
		permissionAPI.deleteGroupPermission(permissionGroupId);
	}

    @Override
	@AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }

    void preConditionSetup() {
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    }

	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");

		Random rd = new Random();
		if (rd.nextBoolean()) {
			model.setProduct_productManagement("00000000000000000011");
		} else if (rd.nextBoolean()) {
			model.setProduct_productManagement("00000000000000000000");
		} else if (rd.nextBoolean()) {
			model.setProduct_productManagement("00000000000000000001");
		} else {
			model.setProduct_productManagement("00000000000000000010");
		}
		
		if (rd.nextBoolean()) {
			model.setCustomer_segment("0000");
		} else {
			model.setCustomer_segment("0001");
		}

		if (rd.nextBoolean()) {
			model.setProduct_collection("000000");
		} else {
			model.setProduct_collection("000001");
		}
		
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
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));

		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();

		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());

		buyXGetYPage.checkBuyXGetYPermission(allPermissionDTO, 11608134, "Tien's Jacket", "Staff A's Dog Food");
		
	}

}
