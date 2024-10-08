package web.Dashboard;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APIAllCustomers.CustomerManagementInfo;
import api.Seller.customers.APICreateCustomer;
import api.Seller.customers.APICustomerDetail;
import api.Seller.customers.APIEditCustomer;
import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.model.dashboard.customer.CustomerDebtRecord;
import utilities.model.dashboard.customer.CustomerOrder;
import utilities.model.dashboard.customer.CustomerOrderSummary;
import utilities.model.dashboard.customer.create.CreateCustomerModel;
import utilities.model.dashboard.customer.segment.CreateSegment;
import utilities.model.dashboard.customer.segment.SegmentCondition;
import utilities.model.dashboard.customer.segment.SegmentDetail;
import utilities.model.dashboard.customer.segment.SegmentList;
import utilities.model.dashboard.customer.update.EditCustomerModel;
import utilities.model.dashboard.marketing.loyaltyProgram.LoyaltyProgramInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class CustomerTest {

	LoginInformation credentials;
	String storeName;
	int storeId;
	
	@BeforeMethod
	public void setup() {
		credentials = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();
//		credentials = new Login().setLoginInformation("tienvan-staging-foreign@mailnesia.com", "fortesting!1").getLoginInformation();
//		credentials = new Login().setLoginInformation("auto0-shop172215557@mailnesia.com", "fortesting!1").getLoginInformation();
		storeName = new Login().getInfo(credentials).getStoreName();
		storeId = new Login().getInfo(credentials).getStoreID();
	}	

	@Test(invocationCount = 20)
	public void createCustomerVN() {

		CreateCustomerModel data = CreateCustomerTDG.generateVNCustomer(storeName);

		APICreateCustomer createCustomerAPI = new APICreateCustomer(credentials);

		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();


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

		Assert.assertTrue(new APIAllCustomers(credentials).getAllCustomerNames().contains(data.getName()));

		System.out.println();
	}

	@Test(invocationCount = 20)
	public void createCustomerForeign() throws InterruptedException {

		CreateCustomerModel data = CreateCustomerTDG.generateForeignCustomer(storeName);

		APICreateCustomer createCustomerAPI = new APICreateCustomer(credentials);

		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();

		
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
		
		Thread.sleep(500); //Sometimes it takes longer for the newly created customer to be included in the All Customer list
		Assert.assertTrue(new APIAllCustomers(credentials).getAllCustomerNames().contains(data.getName()));

	}
	
	@Test
	public void deleteProfles() {

		APIAllCustomers allCustomerAPI = new APIAllCustomers(credentials);

		allCustomerAPI.deleteProfiles(allCustomerAPI.getAllCustomerIds());
	}	

	@Test
	public void createSegment() {

		CreateCustomerModel data = CreateCustomerTDG.generateForeignCustomer(storeName);
		if (data.getTags().isEmpty()) data.setTags(CreateCustomerTDG.randomizeTags(1));

		APICreateCustomer createCustomerAPI = new APICreateCustomer(credentials);
		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();

		SegmentCondition condition1 = new SegmentCondition();
		condition1.setName("Customer Data_Customer tag_is equal to");
		condition1.setValue(data.getTags().get(0));
		
		CreateSegment segmentdata = new CreateSegment();
		segmentdata.setName("Segment " + data.getTags().get(0));
		segmentdata.setMatchCondition("ALL");
		segmentdata.setConditions(List.of(condition1));

		APISegment createSegmentAPI = new APISegment(credentials);
		JsonPath createSegmentResponse = createSegmentAPI.createSegment(segmentdata).jsonPath();
		Integer createdSegmentId = createSegmentResponse.get("id");

		//Create segment response
		Assert.assertEquals(segmentdata.getName(), createSegmentResponse.get("name"));
		Assert.assertEquals(segmentdata.getMatchCondition(), createSegmentResponse.get("matchCondition"));

		//All segment list
		SegmentList createdSegment = createSegmentAPI.getSegmentList().stream().filter(it -> it.getId().equals(createdSegmentId)).findFirst().orElse(null);
		Assert.assertEquals(segmentdata.getName(), createdSegment.getName());
		Assert.assertEquals(Integer.valueOf(1), createdSegment.getUserCount());

	}

	@Test
	public void deleteSegments() {

		APISegment createSegmentAPI = new APISegment(credentials);

		createSegmentAPI.getSegmentList().parallelStream().forEach(it -> createSegmentAPI.deleteSegment(it.getId()));
	}

	@Test
	public void editProfles() {

		List<String> tags = CreateCustomerTDG.randomizeTags(5);

		APIEditCustomer editCustomerAPI = new APIEditCustomer(credentials);

		Response result = editCustomerAPI.addMoreTagForCustomer(4997181, tags);
		
		List<String> actualTags = result.jsonPath().get("tags.collect{ it -> it.value }");
		
		Assert.assertTrue(actualTags.containsAll(tags));
	}
	
	@Test
	public void removeTagsFromCustomer() {

		APIEditCustomer editCustomerAPI = new APIEditCustomer(credentials);

		EditCustomerModel editPayload = editCustomerAPI.getPayLoadFormat(4997181);
		editPayload.setTags(List.of());
		
		Response result = editCustomerAPI.updateCustomerInfo(editPayload);
		
		Assert.assertEquals(result.jsonPath().getList("tags").size(), 0);
		
	}

	@Test
	public void customerRemovedFromSegment() {

		//Build customer data
		CreateCustomerModel data = CreateCustomerTDG.generateForeignCustomer(storeName);
		if (data.getTags().isEmpty()) data.setTags(CreateCustomerTDG.randomizeTags(1));
		//Create customer
		APICreateCustomer createCustomerAPI = new APICreateCustomer(credentials);
		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();

		//Build segment condition
		SegmentCondition condition1 = new SegmentCondition();
		condition1.setName("Customer Data_Customer tag_is equal to");
		condition1.setValue(data.getTags().get(0));
		//Build segment data
		CreateSegment segmentdata = new CreateSegment();
		segmentdata.setName("Segment " + data.getTags().get(0));
		segmentdata.setMatchCondition("ALL");
		segmentdata.setConditions(List.of(condition1));
		//Create segment
		APISegment createSegmentAPI = new APISegment(credentials);
		JsonPath createSegmentResponse = createSegmentAPI.createSegment(segmentdata).jsonPath();
		Integer createdSegmentId = createSegmentResponse.get("id");

		//Verify response
		SegmentList createdSegment = createSegmentAPI.getSegmentList().stream().filter(it -> it.getId().equals(createdSegmentId)).findFirst().orElse(null);
		Assert.assertEquals(Integer.valueOf(1), createdSegment.getUserCount());
		
		//Remove tags from customer
		APIEditCustomer editCustomerAPI = new APIEditCustomer(credentials);
		EditCustomerModel editPayload = editCustomerAPI.getPayLoadFormat(createResponse.get("id"));
		editPayload.setTags(List.of());
		editCustomerAPI.updateCustomerInfo(editPayload);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Verify response
		Assert.assertEquals(Integer.valueOf(0), createSegmentAPI.getSegmentList().stream().filter(it -> it.getId().equals(createdSegmentId)).findFirst().orElse(null).getUserCount());
	
		//Rollback
		createSegmentAPI.deleteSegment(createdSegmentId);
	}	
	
	
	@Test
	public void editSegment() {
		
		//Build customer data
		CreateCustomerModel data = CreateCustomerTDG.generateForeignCustomer(storeName);
		if (data.getTags().isEmpty()) data.setTags(CreateCustomerTDG.randomizeTags(1));
		//Create customer
		APICreateCustomer createCustomerAPI = new APICreateCustomer(credentials);
		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();
		
		//Build segment condition
		SegmentCondition condition = new SegmentCondition();
		condition.setName("Customer Data_Customer tag_is equal to");
		condition.setValue(data.getTags().get(0));
		//Build segment data
		CreateSegment segmentdata = new CreateSegment();
		segmentdata.setName("Segment " + data.getTags().get(0));
		segmentdata.setMatchCondition("ANY");
		segmentdata.setConditions(List.of(condition));
		//Create segment
		APISegment segmentAPI = new APISegment(credentials);
		JsonPath createSegmentResponse = segmentAPI.createSegment(segmentdata).jsonPath();
		Integer createdSegmentId = createSegmentResponse.get("id");
		
		//Verify number of users belonging to the segment
		SegmentList createdSegment = segmentAPI.getSegmentList().stream().filter(it -> it.getId().equals(createdSegmentId)).findFirst().orElse(null);
		Assert.assertEquals(Integer.valueOf(1), createdSegment.getUserCount());
		
		//Get segment detail before editing it
		SegmentDetail segmentDetail = segmentAPI.getSegmentDetail(String.valueOf(createdSegmentId));
		//Edit condition
		condition.setValue("ahahaha");
		segmentDetail.setConditions(List.of(condition));
		
		segmentAPI.editSegment(String.valueOf(createdSegmentId), segmentDetail);
		
		//Verify number of users belonging to the segment
		Assert.assertEquals(Integer.valueOf(0), segmentAPI.getSegmentList().stream().filter(it -> it.getId().equals(createdSegmentId)).findFirst().orElse(null).getUserCount());
	}

	
	@Test
	public void createCustomerVNExp() {

		credentials = new Login().setLoginInformation("tienvan-staging-vn@mailnesia.com", "fortesting!1").getLoginInformation();
		
		CreateCustomerModel data = CreateCustomerTDG.generateVNCustomer(storeName);

		APICreateCustomer createCustomerAPI = new APICreateCustomer(credentials);

		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();

	}	
	
	@Test
	public void getCustomerOrderRelatedInfo() throws JsonProcessingException {
		
		credentials = new Login().setLoginInformation("tienvan-staging-vn@mailnesia.com", "fortesting!1").getLoginInformation();
		int customerId = 4516272;
		String userId = "53825261";
		
		ObjectMapper mapper = new ObjectMapper();
		
		APICustomerDetail customerDetailAPI = new APICustomerDetail(credentials);
		
		CustomerOrderSummary orderSummary = customerDetailAPI.getOrderSummary(customerId);
		System.out.println("Order summary: " + mapper.writeValueAsString(orderSummary));
		
		List<CustomerOrder> orders = customerDetailAPI.getOrders(customerId, userId);
		System.out.println("Orders: " + mapper.writeValueAsString(orders));
		
		List<CustomerDebtRecord> debtRecords = customerDetailAPI.getDebtRecords(customerId);
		System.out.println("Debt records: " + mapper.writeValueAsString(debtRecords));
		
		LoyaltyProgramInfo membership = customerDetailAPI.getMembership(customerId);
		System.out.println("Membership: " + mapper.writeValueAsString(membership));
		
		List<Object> points = customerDetailAPI.getPoint(userId).jsonPath().getList(".");
		System.out.println("Points: " + mapper.writeValueAsString(points));
		
	}	
	
	public void getCustomerOrder() throws JsonProcessingException {
		
		credentials = new Login().setLoginInformation("tienvan-staging-vn@mailnesia.com", "fortesting!1").getLoginInformation();
		
		APIAllCustomers allCustomerAPI = new APIAllCustomers(credentials);
		
		CustomerManagementInfo list = allCustomerAPI.getCustomerManagementInfo();
		
		for (int i=0; i<list.getUserId().size(); i++) {
			
			if (list.getUserId().get(i)==null) continue;
			
			System.out.println(i);
			
			String userId = list.getUserId().get(i);
			int customerId = list.getCustomerId().get(i);
			
			ObjectMapper mapper = new ObjectMapper();
			
			APICustomerDetail customerDetailAPI = new APICustomerDetail(credentials);
			
			CustomerOrderSummary orderSummary = customerDetailAPI.getOrderSummary(customerId);
			System.out.println("Order summary: " + mapper.writeValueAsString(orderSummary));
			
			List<CustomerOrder> orders = customerDetailAPI.getOrders(customerId, userId);
			System.out.println("Orders: " + mapper.writeValueAsString(orders));
			
			List<CustomerDebtRecord> debtRecords = customerDetailAPI.getDebtRecords(customerId);
			System.out.println("Debt records: " + mapper.writeValueAsString(debtRecords));
			
			LoyaltyProgramInfo membership = customerDetailAPI.getMembership(customerId);
			System.out.println("Membership: " + mapper.writeValueAsString(membership));
			
			List<Object> points = customerDetailAPI.getPoint(userId).jsonPath().getList(".");
			System.out.println("Points: " + mapper.writeValueAsString(points));
		}
	}	
	
	
}
