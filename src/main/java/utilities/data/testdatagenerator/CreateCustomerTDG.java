package utilities.data.testdatagenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.mifmif.common.regex.Generex;

import api.catalog.APICatalog;
import utilities.data.DataGenerator;
import utilities.enums.DisplayLanguage;
import utilities.model.dashboard.catalog.CityTree;
import utilities.model.dashboard.catalog.District;
import utilities.model.dashboard.catalog.Ward;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerGeoLocation;
import utilities.model.dashboard.customer.CustomerPhone;
import utilities.model.dashboard.customer.create.CreateCustomerModel;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.utils.ListUtils;

public class CreateCustomerTDG {
	
	public static List<String> personality = Arrays.asList("Affable", "Gregarious", "Observant", "Cranky", "Ambitious", "Humorous", "Discreet", "Aggressive", "Bossy", "Impatient", "Hostile");
	
	public static String randomizePersonality() {
		return ListUtils.getRandomListElement(personality);
	}
	static String randomizeGender() {
		return ListUtils.getRandomListElement(Arrays.asList("MALE", "FEMALE", null));
	}
	static String randomizeBirthday() {
		return ListUtils.getRandomListElement(Arrays.asList("2000-07-26T00:00:00+07:00", null));
	}
	static String randomizeVNAddress() {
		return "%s Quang Trung".formatted(new Generex("[1-9]\\d{2}").random());
	}
	static String randomizeForeignAddress() {
		return "%s High Street".formatted(new Generex("[1-9]\\d{2}").random());
	}
	static String randomizeAddress2() {
		return ListUtils.getRandomListElement(List.of("%s High Street".formatted(new Generex("[1-9]\\d{2}").random()), ""));
	}
	static List<String> randomizeTags() {
		int tagCount = DataGenerator.generatNumberInBound(0, 21);
		return randomizeTags(tagCount);
	}	
	public static List<String> randomizeTags(int tagCount) {
		List<String> tags = new ArrayList<>();
		IntStream.range(0, tagCount).forEach(e -> tags.add(randomizePersonality() + String.valueOf(System.nanoTime()).replaceAll("0{2}$", "").replaceAll("^\\d{3}", "")));
		return tags;
	}	
	
	public static CreateCustomerModel generateVNCustomer(String storeName) {
		String country = "Vietnam";
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneCode = DataGenerator.getPhoneCode(country);
		String phoneNumber = "0" + DataGenerator.generatePhoneFromRegex("(?:5(?:2[238]|59)|89[6-9]|99[013-9])\\d{6}|(?:3\\d|5[689]|7[06-9]|8[1-8]|9[0-8])\\d{7}");
		String customerName = "Auto Buyer " + phoneNumber;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(phoneNumber);

		List<CityTree> cities = APICatalog.getCityTree(countryCode);
		CityTree city = ListUtils.getRandomListElement(cities.subList(0, cities.size()-1)); //Deliberately remove the last element which is 'Other' from the list
		District district = ListUtils.getRandomListElement(city.getDistricts());
		Ward ward = ListUtils.getRandomListElement(district.getWards());
		
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
		data.setLocationCode(city.getCode());
		data.setDistrictCode(district.getCode());
		data.setWardCode(ward.getCode());
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
		
		String country = CountryTDG.randomCustomerCountry().getOut_country();
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneCode = DataGenerator.getPhoneCode(country);
		String customerPhone = DataGenerator.randomValidPhoneByCountry(country);
		String customerName = "Auto Buyer " + customerPhone;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(customerPhone);

		List<CityTree> cities = APICatalog.getCityTree(countryCode);
		CityTree city = ListUtils.getRandomListElement(cities);
		
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
		data.setLocationCode(city.getCode()); 
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
	
	public static CreateCustomerModel generateForeignCustomerUI(String storeName) {
		
		String country = CountryTDG.randomCustomerCountry().getOut_country();
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneCode = DataGenerator.getPhoneCode(country);
		String customerPhone = DataGenerator.randomValidPhoneByCountry(country);
		String customerName = "Auto Buyer " + customerPhone;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(customerPhone);

		List<CityTree> cities = APICatalog.getCityTree(countryCode);
		CityTree city = ListUtils.getRandomListElement(cities);
		
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
		data.setLocationCode(city.getCode()); 
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
	
	/**
	 * @param dashboardDisplayLang VIE/ENG
	 */
	public static UICreateCustomerData buildVNCustomerUIData(DisplayLanguage dashboardDisplayLang) {
		String country = "Vietnam";
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneNumber = "0" + DataGenerator.randomValidPhoneByCountry(country);
		String customerName = "Auto Buyer " + phoneNumber;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(phoneNumber);

		List<CityTree> cities = APICatalog.getCityTree(countryCode);
		CityTree city = ListUtils.getRandomListElement(cities.subList(0, cities.size()-1)); //Deliberately remove the last element which is 'Other' from the list
		District district = ListUtils.getRandomListElement(city.getDistricts());
		Ward ward = ListUtils.getRandomListElement(district.getWards());
		
		String cityName = dashboardDisplayLang.equals(DisplayLanguage.VIE) ? city.getInCountry() : city.getOutCountry(); 
		String districtName = dashboardDisplayLang.equals(DisplayLanguage.VIE) ? district.getInCountry() : district.getOutCountry(); 
		String wardName = dashboardDisplayLang.equals(DisplayLanguage.VIE) ? ward.getInCountry() : ward.getOutCountry();
		
		return UICreateCustomerData.builder()
				.name(customerName)
				.phone(phoneNumber)
				.email(customerEmail)
				.note(randomizePersonality() + " customer")
				.tags(randomizeTags())
				.country(country)
				.address(randomizeVNAddress())
				.province(cityName)
				.district(districtName)
				.ward(wardName)
				.isCreateUser(false)
				.gender(randomizeGender())
				.birthday(null)
				.build();
	}
	
	/**
	 * @param dashboardDisplayLang VIE/ENG
	 */
	public static UICreateCustomerData buildForeignCustomerUIData(DisplayLanguage dashboardDisplayLang) {
		
		String country = CountryTDG.randomCustomerCountry().getOut_country();
		String countryCode = DataGenerator.getCountryCode(country);
		String phoneNumber = DataGenerator.randomValidPhoneByCountry(country);
		String customerName = "Auto Buyer " + phoneNumber;
		String customerEmail = "auto-buyer%s@mailnesia.com".formatted(phoneNumber);
		
		List<CityTree> cities = APICatalog.getCityTree(countryCode);
		CityTree city = ListUtils.getRandomListElement(cities);
		
		String cityName = dashboardDisplayLang.equals(DisplayLanguage.VIE) ? city.getInCountry() : city.getOutCountry(); 
		
		return UICreateCustomerData.builder()
				.name(customerName)
				.phone(phoneNumber)
				.email(customerEmail)
				.note(randomizePersonality() + " customer")
				.tags(randomizeTags())
				.country(country)
				.address(randomizeForeignAddress())
				.address2(randomizeAddress2())
				.province(cityName)
				.city("LA")
				.zipCode("%s%s".formatted(countryCode, new Generex("[1-9]\\d{6}").random()))
				.isCreateUser(false)
				.gender(randomizeGender())
				.birthday(null)
				.build();
	}	
	
	
	
}