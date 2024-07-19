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

import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import api.Seller.promotion.CreatePromotion;
import api.Seller.promotion.PromotionList;
import api.Seller.setting.PermissionAPI;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.DiscountStatus;
import utilities.enums.DiscountType;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.promotion.discount.DiscountPage;

public class DiscountPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	PermissionAPI permissionAPI;
	APISegment segmentAPI;
	PromotionList promotionListAPI;
	CreatePromotion createPromotionAPI;
	

	int permissionGroupId;

	@BeforeClass
	void loadTestData() {
		ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		permissionAPI = new PermissionAPI(ownerCredentials);
		segmentAPI = new APISegment(ownerCredentials);
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
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);;

    	segmentAPI.getListSegmentIdInStore();
    }

	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setProduct_productManagement(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"00", "01", "10", "11"})));
		model.setCustomer_segment(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"1", "0"})));
		model.setProduct_collection(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"1", "0"})));
		model.setService_serviceCollection(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"1", "0"})));
		model.setPromotion_discountCode(permissionBinary);
		return model;
	}

	@Test(dataProvider = "discountPermission", dataProviderClass = PermissionDataProvider.class)
	public void CC_01_CheckDiscountPermission(String permissionBinary) {

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

		int productDiscountCodeIdToEnd = 0;
		List<Integer> createdProductList = new ArrayList<>();
		if (allPermissionDTO.getPromotion().getDiscountCode().isEndProductDiscountCode()) {
			createdProductList.add(createPromotionAPI.createProductDiscountCode());
			if (allPermissionDTO.getPromotion().getDiscountCode().isViewProductDiscountCodeDetail()) {
				productDiscountCodeIdToEnd = createPromotionAPI.createProductDiscountCode();
				createdProductList.add(productDiscountCodeIdToEnd);
			}
		}
		productDiscountCodeIdToEnd = productDiscountCodeIdToEnd == 0 ? promotionListAPI.getDiscountId(DiscountType.PRODUCT_DISCOUNT_CODE, DiscountStatus.IN_PROGRESS) : productDiscountCodeIdToEnd;
		
		int serviceDiscountCodeIdToEnd = 0;
		List<Integer> createdServiceList = new ArrayList<>();
		if (allPermissionDTO.getPromotion().getDiscountCode().isEndServiceDiscountCode()) {
			createdServiceList.add(createPromotionAPI.createServiceDiscountCode());
			if (allPermissionDTO.getPromotion().getDiscountCode().isViewServiceDiscountCodeDetail()) {
				serviceDiscountCodeIdToEnd = createPromotionAPI.createServiceDiscountCode();
				createdServiceList.add(serviceDiscountCodeIdToEnd);
			}
		}
		
		serviceDiscountCodeIdToEnd = serviceDiscountCodeIdToEnd == 0 ? promotionListAPI.getDiscountId(DiscountType.SERVICE_DISCOUNT_CODE, DiscountStatus.IN_PROGRESS) : serviceDiscountCodeIdToEnd;
		
		discountPage.checkDiscountPermission(allPermissionDTO, 11557142, 11565085, productDiscountCodeIdToEnd, serviceDiscountCodeIdToEnd, "Tien's Jacket", "Staff A's Dog Food", "Air plane ticket 1", "Staff A's Cleaning Services");
		
		List<Integer> createdDiscountCodeList = new ArrayList<>();
		createdDiscountCodeList.addAll(createdProductList);
		createdDiscountCodeList.addAll(createdServiceList);
		for (int id: createdDiscountCodeList) {
			createPromotionAPI.deleteDiscount(id);
		}
	}

}
