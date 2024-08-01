package web.Dashboard;

import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APICreateCustomer;
import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mifmif.common.regex.Generex;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerGeoLocation;
import utilities.model.dashboard.customer.CustomerPhone;
import utilities.model.dashboard.customer.create.CreateCustomer;
import utilities.model.dashboard.customer.segment.CreateSegment;
import utilities.model.dashboard.customer.segment.SegmentCondition;
import utilities.model.dashboard.customer.segment.SegmentList;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Arrays;
import java.util.List;

public class CustomerTest {
	

	@Test(invocationCount = 50, threadPoolSize = 10)
	public void createCustomerVN() throws JsonProcessingException {
		
		LoginInformation login = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();

        String storeName = new Login().getInfo(login).getStoreName();
        int storeId = new Login().getInfo(login).getStoreID();
		
        List<String> personality = Arrays.asList("Affable", "Gregarious", "Observant", "Cranky", "Ambitious", "Humorous", "Discreet", "Considerate", "Aggressive", "Temparamental", "Overcritical", "Bossy", "Confrontational", "Impatient", "Hostile");
        
		String country = "Vietnam";
		String countryCode = "VN";
		String phoneCode = DataGenerator.getPhoneCode(country);
		String phoneNumber = "0" + DataGenerator.generatePhoneFromRegex("(?:5(?:2[238]|59)|89[6-9]|99[013-9])\\d{6}|(?:3\\d|5[689]|7[06-9]|8[1-8]|9[0-8])\\d{7}");
		
		String customerName = "Auto Buyer " + phoneNumber;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(phoneNumber);
		
		CustomerEmail email = new CustomerEmail();
		email.setEmail(customerEmail);
		email.setEmailName(customerName);
		email.setEmailType("MAIN");
		
		CustomerPhone phone = new CustomerPhone();
		phone.setPhoneCode("+84");
		phone.setPhoneName(customerName);
		phone.setPhoneNumber(phoneNumber);
		phone.setPhoneType("MAIN");
		
		CreateCustomer data = new CreateCustomer();
		data.setName(customerName);
		data.setPhone(phoneNumber);
		data.setEmail(customerEmail);
		data.setNote(DataGenerator.getRandomListElement(personality) + " customer"); //empty when not provided
		data.setTags(Arrays.asList(DataGenerator.getRandomListElement(personality)));
		data.setAddress("%s Quang Trung".formatted(new Generex("[1-9]\\d{2}").random())); //empty when not provided
		data.setLocationCode("VN-SG"); //empty when not provided
		data.setDistrictCode("0213"); //empty when not provided
		data.setWardCode("21315"); //empty when not provided
		data.setIsCreateUser(false);
		data.setGender(DataGenerator.getRandomListElement(Arrays.asList("MALE", "FEMALE")));
		data.setBirthday("2000-07-26T00:00:00+07:00");
		data.setCountryCode(countryCode);
		data.setGeoLocation(new CustomerGeoLocation());
		data.setPhones(Arrays.asList(phone));
		data.setEmails(email);
		data.setStoreName(storeName);
		data.setLangKey("vi");
		data.setBranchId("");
		
		
    	APICreateCustomer createCustomerAPI = new APICreateCustomer(login);
    	
    	JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();
    	
    	
    	String actualPhoneCode = createResponse.get("phones[0].phoneCode");
    	String actualPhoneName = createResponse.get("phones[0].phoneName");
    	String actualPhoneNumber = createResponse.get("phones[0].phoneNumber");
    	String actualPhoneType = createResponse.get("phones[0].phoneType");
    	
    	
    	List<String> actualEmail = createResponse.get("emails.collect{ it -> it.email }");
    	List<String> actualEmailName = createResponse.get("emails.collect{ it -> it.emailName }");
    	List<String> actualEmailType = createResponse.get("emails.collect{ it -> it.emailType }");
    	
    	String actualFullName = createResponse.get("fullName");
    	
    	int actualStoreId = createResponse.get("storeId");
    	
    	String actualUselessPhoneCode = createResponse.get("phoneCode");
    	
    	String actualSaleChannel = createResponse.get("saleChannel");
    	
    	//Check if present
    	String actualNote = createResponse.get("note");
    	List<String> actualTags = createResponse.get("tags.collect{ it -> it.value }");
    	
    	boolean isGuest = createResponse.get("guest");
    	
    	String actualGender = createResponse.get("gender");
    	
    	String actualAddress = createResponse.get("customerAddress.address");
    	String actualAddressCountryCode = createResponse.get("customerAddress.countryCode");
    	String actualAddressLocationCode = createResponse.get("customerAddress.locationCode");
    	String actualAddressDistrictCode = createResponse.get("customerAddress.districtCode");
    	String actualAddressWardCode = createResponse.get("customerAddress.wardCode");
    	
    	Assert.assertEquals(data.getPhones().get(0).getPhoneCode(), actualPhoneCode);
    	Assert.assertEquals(data.getName(), actualPhoneName);
    	Assert.assertEquals(data.getPhones().get(0).getPhoneNumber(), actualPhoneNumber);
    	Assert.assertEquals(data.getPhones().get(0).getPhoneType(), actualPhoneType);
    	
    	Assert.assertEquals(data.getEmails().getEmail(), actualEmail.get(0));
    	Assert.assertEquals(data.getEmails().getEmailName(), actualEmailName.get(0));
    	Assert.assertEquals(data.getEmails().getEmailType(), actualEmailType.get(0));
    	
    	Assert.assertEquals(data.getName(), actualFullName);
    	
    	Assert.assertEquals(storeId, actualStoreId);
    	
    	Assert.assertEquals("GOSELL", actualSaleChannel);
    	
    	Assert.assertEquals(data.getNote(), actualNote);
    	
    	Assert.assertEquals(data.getTags(), actualTags);
    	
    	Assert.assertEquals(!data.getIsCreateUser(), Boolean.valueOf(isGuest));
    	
    	Assert.assertEquals(data.getAddress(), actualAddress);
    	Assert.assertEquals(data.getCountryCode(), actualAddressCountryCode);
    	Assert.assertEquals(data.getLocationCode(), actualAddressLocationCode);
    	Assert.assertEquals(data.getDistrictCode(), actualAddressDistrictCode);
    	Assert.assertEquals(data.getWardCode(), actualAddressWardCode);
    	
    	Assert.assertEquals(data.getGender(), actualGender);
    	
    	Assert.assertTrue(new APIAllCustomers(login).getAllCustomerNames().contains(data.getName()));
    	
    	System.out.println();
	}
	
	@Test(invocationCount = 50, threadPoolSize = 10)
	public void createCustomerForeign() throws JsonProcessingException {
		
		LoginInformation login = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();
		
		String storeName = new Login().getInfo(login).getStoreName();
		int storeId = new Login().getInfo(login).getStoreID();
		
		List<String> personality = Arrays.asList("Affable", "Gregarious", "Observant", "Cranky", "Ambitious", "Humorous", "Discreet", "Considerate", "Aggressive", "Temparamental", "Overcritical", "Bossy", "Confrontational", "Impatient", "Hostile");
		
		String country = "Albania";
		String countryCode = "AL";
		String phoneCode = DataGenerator.getPhoneCode(country);
		String phoneNumber = DataGenerator.generatePhoneFromRegex("6(?:[78][2-9]|9\\d)\\d{6}");
		
		String customerName = "Auto Buyer " + phoneNumber;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(phoneNumber);
		
		CustomerEmail email = new CustomerEmail();
		email.setEmail(customerEmail);
		email.setEmailName(customerName);
		email.setEmailType("MAIN");
		
		CustomerPhone phone = new CustomerPhone();
		phone.setPhoneCode(phoneCode);
		phone.setPhoneName(customerName);
		phone.setPhoneNumber(phoneNumber);
		phone.setPhoneType("MAIN");
		
		CreateCustomer data = new CreateCustomer();
		data.setName(customerName);
		data.setPhone(phoneNumber);
		data.setEmail(customerEmail);
		data.setNote(DataGenerator.getRandomListElement(personality) + " customer"); //empty when not provided
		data.setTags(Arrays.asList(DataGenerator.getRandomListElement(personality)));
		data.setAddress("%s High Street".formatted(new Generex("[1-9]\\d{2}").random())); //empty when not provided
		data.setLocationCode("AL-1"); //empty when not provided
		
		data.setAddress2(""); //empty when not provided
		data.setCity("LA");
		data.setZipCode("%s%s".formatted(countryCode, new Generex("[1-9]\\d{6}").random()));
		
		data.setIsCreateUser(false);
		data.setGender(DataGenerator.getRandomListElement(Arrays.asList("MALE", "FEMALE")));
		data.setBirthday("2000-07-26T00:00:00+07:00");
		data.setCountryCode(countryCode);
		data.setGeoLocation(new CustomerGeoLocation());
		data.setPhones(Arrays.asList(phone));
		data.setEmails(email);
		data.setStoreName(storeName);
		data.setLangKey("vi");
		data.setBranchId("");
		
		
		APICreateCustomer createCustomerAPI = new APICreateCustomer(login);
		
		JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();
		
		
		String actualPhoneCode = createResponse.get("phones[0].phoneCode");
		String actualPhoneName = createResponse.get("phones[0].phoneName");
		String actualPhoneNumber = createResponse.get("phones[0].phoneNumber");
		String actualPhoneType = createResponse.get("phones[0].phoneType");
		
		List<String> actualEmail = createResponse.get("emails.collect{ it -> it.email }");
		List<String> actualEmailName = createResponse.get("emails.collect{ it -> it.emailName }");
		List<String> actualEmailType = createResponse.get("emails.collect{ it -> it.emailType }");
		
		String actualFullName = createResponse.get("fullName");
		
		int actualStoreId = createResponse.get("storeId");
		
		String actualUselessPhoneCode = createResponse.get("phoneCode");
		
		String actualSaleChannel = createResponse.get("saleChannel");
		
		//Check if present
		String actualNote = createResponse.get("note");
		List<String> actualTags = createResponse.get("tags.collect{ it -> it.value }");
		
		boolean isGuest = createResponse.get("guest");
		
		
		String actualAddress = createResponse.get("customerAddress.address");
		String actualAddress2 = createResponse.get("customerAddress.address2");
		String actualAddressCountryCode = createResponse.get("customerAddress.countryCode");
		String actualAddressLocationCode = createResponse.get("customerAddress.locationCode");
		String actualAddressDistrictCode = createResponse.get("customerAddress.districtCode");
		String actualAddressWardCode = createResponse.get("customerAddress.wardCode");
		
		String actualGender = createResponse.get("gender");
		
		Assert.assertEquals(data.getPhones().get(0).getPhoneCode(), actualPhoneCode);
		Assert.assertEquals(data.getName(), actualPhoneName);
		Assert.assertEquals(data.getPhones().get(0).getPhoneNumber(), actualPhoneNumber);
		Assert.assertEquals(data.getPhones().get(0).getPhoneType(), actualPhoneType);
		
		Assert.assertEquals(data.getEmails().getEmail(), actualEmail.get(0));
		Assert.assertEquals(data.getEmails().getEmailName(), actualEmailName.get(0));
		Assert.assertEquals(data.getEmails().getEmailType(), actualEmailType.get(0));
		
		Assert.assertEquals(data.getName(), actualFullName);
		
		Assert.assertEquals(storeId, actualStoreId);
		
		Assert.assertEquals("GOSELL", actualSaleChannel);
		
		Assert.assertEquals(data.getNote(), actualNote);
		
		Assert.assertEquals(data.getTags(), actualTags);
		
		Assert.assertEquals(!data.getIsCreateUser(), Boolean.valueOf(isGuest));
		
		Assert.assertEquals(data.getAddress(), actualAddress);
		Assert.assertEquals(data.getAddress2(), actualAddress2);
		Assert.assertEquals(data.getCountryCode(), actualAddressCountryCode);
		Assert.assertEquals(data.getLocationCode(), actualAddressLocationCode);
		Assert.assertEquals("", actualAddressDistrictCode);
		Assert.assertEquals("", actualAddressWardCode);
		
		Assert.assertEquals(data.getGender(), actualGender);
		
		Assert.assertTrue(new APIAllCustomers(login).getAllCustomerNames().contains(data.getName()));
		
		System.out.println();
	}
	
	@Test(invocationCount = 50, threadPoolSize = 10)
	public void createSegment() throws JsonProcessingException {
		
		LoginInformation login = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();
		
		String storeName = new Login().getInfo(login).getStoreName();
		int storeId = new Login().getInfo(login).getStoreID();
		
		List<String> personality = Arrays.asList("Affable", "Gregarious", "Observant", "Cranky", "Ambitious", "Humorous", "Discreet", "Considerate", "Aggressive", "Temparamental", "Overcritical", "Bossy", "Confrontational", "Impatient", "Hostile");
		
		String adjectives = DataGenerator.getRandomListElement(personality);
		
		String country = "Vietnam";
		String countryCode = "VN";
		String phoneCode = DataGenerator.getPhoneCode(country);
		String phoneNumber = "0" + DataGenerator.generatePhoneFromRegex("(?:5(?:2[238]|59)|89[6-9]|99[013-9])\\d{6}|(?:3\\d|5[689]|7[06-9]|8[1-8]|9[0-8])\\d{7}");
		
		String customerName = "Auto Buyer " + phoneNumber;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(phoneNumber);
		
		CustomerEmail email = new CustomerEmail();
		email.setEmail(customerEmail);
		email.setEmailName(customerName);
		email.setEmailType("MAIN");
		
		CustomerPhone phone = new CustomerPhone();
		phone.setPhoneCode("+84");
		phone.setPhoneName(customerName);
		phone.setPhoneNumber(phoneNumber);
		phone.setPhoneType("MAIN");
		
		CreateCustomer data = new CreateCustomer();
		data.setName(customerName);
		data.setPhone(phoneNumber);
		data.setEmail(customerEmail);
		data.setNote(adjectives + " customer"); //empty when not provided
		data.setTags(Arrays.asList("%s%s".formatted(adjectives, phoneNumber)));
		data.setAddress("%s Quang Trung".formatted(new Generex("[1-9]\\d{2}").random())); //empty when not provided
		data.setLocationCode("VN-SG"); //empty when not provided
		data.setDistrictCode("0213"); //empty when not provided
		data.setWardCode("21315"); //empty when not provided
		data.setIsCreateUser(false);
		data.setGender(DataGenerator.getRandomListElement(Arrays.asList("MALE", "FEMALE")));
		data.setBirthday("2000-07-26T00:00:00+07:00");
		data.setCountryCode(countryCode);
		data.setGeoLocation(new CustomerGeoLocation());
		data.setPhones(Arrays.asList(phone));
		data.setEmails(email);
		data.setStoreName(storeName);
		data.setLangKey("vi");
		data.setBranchId("");
		
    	APICreateCustomer createCustomerAPI = new APICreateCustomer(login);
    	JsonPath createResponse = createCustomerAPI.createCustomer(data).jsonPath();
		
		SegmentCondition condition1 = new SegmentCondition();
		condition1.setName("Customer Data_Customer tag_is equal to");
		condition1.setValue(data.getTags().get(0));
		CreateSegment segmentdata = new CreateSegment();
		segmentdata.setName("Segment " + data.getTags().get(0));
		segmentdata.setMatchCondition("ALL");
		segmentdata.setConditions(Arrays.asList(condition1));
		
		APISegment createSegmentAPI = new APISegment(login);
		JsonPath createSegmentResponse = createSegmentAPI.createSegment(segmentdata).jsonPath();
		Integer createdSegmentId = createSegmentResponse.get("id");
		
		//Create segment response
		Assert.assertEquals(segmentdata.getName(), createSegmentResponse.get("name"));
		Assert.assertEquals(segmentdata.getMatchCondition(), createSegmentResponse.get("matchCondition"));
		
		//All segment list
		SegmentList createdSegment = createSegmentAPI.getSegmentList().stream().filter(it -> it.getId().equals(createdSegmentId)).findFirst().orElse(null);
		Assert.assertEquals(segmentdata.getName(), createdSegment.getName());
		Assert.assertEquals(Integer.valueOf(1), createdSegment.getUserCount());
		
		System.out.println();
	}
	
	
}
