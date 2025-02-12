package web.Dashboard;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APICreateCustomer;
import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.enums.Domain;
import utilities.model.dashboard.customer.create.CreateCustomerModel;
import utilities.model.sellerApp.login.LoginInformation;

public class CreateCustomerAPITest extends BaseTest {

	String sellerCountry, sellerUsername, sellerPassword;
	
	LoginInformation sellerCredentials;
	APICreateCustomer createCustomerAPI;
	APIAllCustomers allCustomerAPI;
	
	String storeName;
	int storeId;

	@BeforeClass
	void loadData() {
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			sellerCountry = AccountTest.ADMIN_COUNTRY_TIEN;
			sellerUsername = AccountTest.ADMIN_USERNAME_TIEN;
			sellerPassword = AccountTest.ADMIN_PASSWORD_TIEN;
		} else {
			sellerCountry = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			sellerUsername = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			sellerPassword = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
		}
		sellerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(sellerCountry), sellerUsername, sellerPassword).getLoginInformation();
		
		createCustomerAPI = new APICreateCustomer(sellerCredentials);
		allCustomerAPI = new APIAllCustomers(sellerCredentials);
		
		var storeBasicInfo = new Login().getInfo(sellerCredentials);
		storeName = storeBasicInfo.getStoreName();
		storeId = storeBasicInfo.getStoreID();
	}	
	
	@Test(invocationCount = 5)
	public void createCustomerVN() {

		//Prepare data
		CreateCustomerModel data = CreateCustomerTDG.generateVNCustomer(storeName);

		//Create a customer
		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();

		//Verify a customer is created successfully
		Assert.assertEquals(data.getPhones().get(0).getPhoneCode(), createResponse.get("phoneCode"));
		Assert.assertEquals(data.getPhones().get(0).getPhoneCode(), createResponse.get("phones[0].phoneCode"));
		Assert.assertEquals(data.getName(), createResponse.get("phones[0].phoneName"));
		Assert.assertEquals(data.getPhones().get(0).getPhoneNumber(), createResponse.get("phones[0].phoneNumber"));
		Assert.assertEquals(data.getPhones().get(0).getPhoneType(), createResponse.get("phones[0].phoneType"));

		Assert.assertEquals(data.getEmails().getEmail(), createResponse.getList("emails.collect{ it -> it.email }").get(0));
		Assert.assertEquals(data.getEmails().getEmailName(), createResponse.getList("emails.collect{ it -> it.emailName }").get(0));
		Assert.assertEquals(data.getEmails().getEmailType(), createResponse.getList("emails.collect{ it -> it.emailType }").get(0));

		Assert.assertEquals(data.getName(), createResponse.get("fullName"));

		Assert.assertEquals(storeId, (int)createResponse.get("storeId"));

		Assert.assertEquals("GOSELL", createResponse.get("saleChannel"));

		Assert.assertEquals(data.getNote(), createResponse.get("note"));
		Assert.assertEquals(data.getTags(), createResponse.get("tags.collect{ it -> it.value }"));

		Assert.assertEquals(!data.getIsCreateUser(), Boolean.valueOf(createResponse.getBoolean("guest")));

		Assert.assertEquals(data.getAddress(), createResponse.get("customerAddress.address"));
		Assert.assertEquals(data.getCountryCode(), createResponse.get("customerAddress.countryCode"));
		Assert.assertEquals(data.getLocationCode(), createResponse.get("customerAddress.locationCode"));
		Assert.assertEquals(data.getDistrictCode(), createResponse.get("customerAddress.districtCode"));
		Assert.assertEquals(data.getWardCode(), createResponse.get("customerAddress.wardCode"));

		Assert.assertEquals(data.getGender(), createResponse.get("gender"));

		Assert.assertTrue(allCustomerAPI.getAllCustomerNames().contains(data.getName()));
		
		//Delete the customer profile
		allCustomerAPI.deleteProfiles(List.of(createResponse.getInt("id")));
	}

	@Test(invocationCount = 10)
	public void createCustomerForeign() throws InterruptedException {

		//Prepare data
		CreateCustomerModel data = CreateCustomerTDG.generateForeignCustomer(storeName);

		//Create a customer
		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();
		
		
		//Verify a customer is created successfully
		Assert.assertEquals(data.getPhones().get(0).getPhoneCode(), createResponse.get("phoneCode"));
		Assert.assertEquals(data.getPhones().get(0).getPhoneCode(), createResponse.get("phones[0].phoneCode"));
		Assert.assertEquals(data.getName(), createResponse.get("phones[0].phoneName"));
		Assert.assertEquals(data.getPhones().get(0).getPhoneNumber(), createResponse.get("phones[0].phoneNumber"));
		Assert.assertEquals(data.getPhones().get(0).getPhoneType(), createResponse.get("phones[0].phoneType"));

		Assert.assertEquals(data.getEmails().getEmail(), createResponse.getList("emails.collect{ it -> it.email }").get(0));
		Assert.assertEquals(data.getEmails().getEmailName(), createResponse.getList("emails.collect{ it -> it.emailName }").get(0));
		Assert.assertEquals(data.getEmails().getEmailType(), createResponse.getList("emails.collect{ it -> it.emailType }").get(0));

		Assert.assertEquals(data.getName(), createResponse.get("fullName"));

		Assert.assertEquals(storeId, (int)createResponse.get("storeId"));

		Assert.assertEquals("GOSELL", createResponse.get("saleChannel"));

		Assert.assertEquals(data.getNote(), createResponse.get("note"));
		Assert.assertEquals(data.getTags(), createResponse.get("tags.collect{ it -> it.value }"));

		Assert.assertEquals(!data.getIsCreateUser(), Boolean.valueOf(createResponse.getBoolean("guest")));

		Assert.assertEquals(data.getAddress(), createResponse.get("customerAddress.address"));
		Assert.assertEquals(data.getAddress2(), createResponse.get("customerAddress.address2"));
		Assert.assertEquals(data.getCountryCode(), createResponse.get("customerAddress.countryCode"));
		Assert.assertEquals(data.getLocationCode(), createResponse.get("customerAddress.locationCode"));
		Assert.assertEquals("", createResponse.get("customerAddress.districtCode"));
		Assert.assertEquals("", createResponse.get("customerAddress.wardCode"));

		Assert.assertEquals(data.getGender(), createResponse.get("gender"));
		
		boolean isIncluded = false;
		for (int i=0; i<6; i++) {
			if (allCustomerAPI.getAllCustomerNames().contains(data.getName())) {
				isIncluded = true;
				break;
			}
			Thread.sleep(500); //Sometimes it takes longer for the newly created customer to be included in the All Customer list
		}
		Assert.assertTrue(isIncluded, "Created customer is displayed on All Customer screen");
		
		//Delete the customer profile
		allCustomerAPI.deleteProfiles(List.of(createResponse.getInt("id")));
	}
}
