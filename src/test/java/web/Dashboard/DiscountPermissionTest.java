package web.Dashboard;

import java.io.IOException;
import java.util.Random;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.customers.Customers;
import api.Seller.customers.Customers.CustomerManagementInfo;
import api.Seller.customers.SegmentAPI;
import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.BaseTest;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.promotion.discount.DiscountPage;

public class DiscountPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;

	int permissionGroupId;

	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);

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
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);;

		new Login().getInfo(staffCredentials).getUserId();
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

		model.setPromotion_discountCode(permissionBinary);

		return model;
	}

	@Test(dataProvider = "discountPermission", dataProviderClass = PermissionDataProvider.class)
	public void CC_01_CheckCustomerPermission(String permissionBinary) {

		driver = new InitWebdriver().getDriver(browser, headless);
		LoginPage loginPage = new LoginPage(driver);
		HomePage homePage = new HomePage(driver);
		DiscountPage discountPage = new DiscountPage(driver);

		//Edit a permisison
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));

		//Check permission
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();

		AllPermissions allPermissionDTO = new AllPermissions(new Login().getInfo(staffCredentials).getStaffPermissionToken());

		discountPage.checkDiscountPermission(allPermissionDTO);
	}

}
