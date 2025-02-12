package web.Dashboard;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.customers.APICreateCustomer;
import api.Seller.customers.APIEditCustomer;
import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.model.dashboard.customer.create.CreateCustomerModel;
import utilities.model.dashboard.customer.segment.CreateSegment;
import utilities.model.dashboard.customer.segment.SegmentCondition;
import utilities.model.dashboard.customer.segment.SegmentDetail;
import utilities.model.dashboard.customer.segment.SegmentList;
import utilities.model.dashboard.customer.update.EditCustomerModel;
import utilities.model.sellerApp.login.LoginInformation;

public class CreateCustomerAPI {

	LoginInformation credentials;
	String storeName;
	int storeId;
	
	@BeforeMethod
	public void setup() {
		credentials = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();
		storeName = new Login().getInfo(credentials).getStoreName();
		storeId = new Login().getInfo(credentials).getStoreID();
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

}
