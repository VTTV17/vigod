package utilities.data.testdatagenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.mifmif.common.regex.Generex;

import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerGeoLocation;
import utilities.model.dashboard.customer.CustomerPhone;
import utilities.model.dashboard.customer.create.CreateCustomerModel;

public class CreateCustomerTDG {
	
	public static List<String> personality = Arrays.asList("Affable", "Gregarious", "Observant", "Cranky", "Ambitious", "Humorous", "Discreet", "Considerate", "Aggressive", "Temparamental", "Overcritical", "Bossy", "Confrontational", "Impatient", "Hostile");
	
	public static String randomizePersonality() {
		return DataGenerator.getRandomListElement(personality);
	}
	static String randomizeGender() {
		return DataGenerator.getRandomListElement(Arrays.asList("MALE", "FEMALE", null));
	}
	static String randomizeBirthday() {
		return DataGenerator.getRandomListElement(Arrays.asList("2000-07-26T00:00:00+07:00", null));
	}
	static String randomizeVNAddress() {
		return "%s Quang Trung".formatted(new Generex("[1-9]\\d{2}").random());
	}
	static String randomizeForeignAddress() {
		return "%s High Street".formatted(new Generex("[1-9]\\d{2}").random());
	}
	static String randomizeAddress2() {
		return DataGenerator.getRandomListElement(List.of("%s High Street".formatted(new Generex("[1-9]\\d{2}").random()), ""));
	}
	static List<String> randomizeTags() {
		int tagCount = DataGenerator.generatNumberInBound(0, 21);
		return randomizeTags(tagCount);
	}	
	public static List<String> randomizeTags(int tagCount) {
		List<String> tags = new ArrayList<>();
		IntStream.range(0, tagCount).forEach(e -> tags.add(randomizePersonality() + System.nanoTime()));
		return tags;
	}	

	
	public static CreateCustomerModel generateVNCustomer(String storeName) {
		String country = "Vietnam";
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneCode = DataGenerator.getPhoneCode(country);
		String phoneNumber = "0" + DataGenerator.generatePhoneFromRegex("(?:5(?:2[238]|59)|89[6-9]|99[013-9])\\d{6}|(?:3\\d|5[689]|7[06-9]|8[1-8]|9[0-8])\\d{7}");
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

		CreateCustomerModel data = new CreateCustomerModel();
		data.setName(customerName);
		data.setPhone(phoneNumber);
		data.setEmail(customerEmail);
		data.setNote(randomizePersonality() + " customer");
		data.setTags(randomizeTags());
		data.setAddress(randomizeVNAddress());
		data.setLocationCode("VN-SG");
		data.setDistrictCode("0213");
		data.setWardCode("21315");
		data.setIsCreateUser(false);
		data.setGender(randomizeGender());
		data.setBirthday(randomizeBirthday());
		data.setCountryCode(countryCode);
		data.setGeoLocation(new CustomerGeoLocation());
		data.setPhones(Arrays.asList(phone));
		data.setEmails(email);
		data.setStoreName(storeName);
		data.setLangKey("en");
		data.setBranchId("");
		
		return data;
	}
	public static CreateCustomerModel generateForeignCustomer(String storeName) {
		
		String country = "Albania";
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneCode = DataGenerator.getPhoneCode(country);
		String customerPhone = DataGenerator.generatePhoneFromRegex("6(?:[78][2-9]|9\\d)\\d{6}");
		String customerName = "Auto Buyer " + customerPhone;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(customerPhone);

		CustomerEmail email = new CustomerEmail();
		email.setEmail(customerEmail);
		email.setEmailName(customerName);
		email.setEmailType("MAIN");

		CustomerPhone phone = new CustomerPhone();
		phone.setPhoneCode(phoneCode);
		phone.setPhoneName(customerName);
		phone.setPhoneNumber(customerPhone);
		phone.setPhoneType("MAIN");

		CreateCustomerModel data = new CreateCustomerModel();
		data.setName(customerName);
		data.setPhone(customerPhone);
		data.setEmail(customerEmail);
		data.setNote(randomizePersonality() + " customer");
		data.setTags(randomizeTags());
		data.setAddress(randomizeForeignAddress());
		data.setLocationCode("AL-1"); 
		data.setAddress2(randomizeAddress2());
		data.setCity("LA");
		data.setZipCode("%s%s".formatted(countryCode, new Generex("[1-9]\\d{6}").random()));
		data.setIsCreateUser(false);
		data.setGender(randomizeGender());
		data.setBirthday(randomizeBirthday());
		data.setCountryCode(countryCode);
		data.setGeoLocation(new CustomerGeoLocation());
		data.setPhones(Arrays.asList(phone));
		data.setEmails(email);
		data.setStoreName(storeName);
		data.setLangKey("en");
		data.setBranchId("");
		
		return data;
	}
	
}