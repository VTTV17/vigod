package utilities.model.dashboard.setupstore;

import java.util.Arrays;
import java.util.Random;

import lombok.Data;
import utilities.data.DataGenerator;

@Data
public class SetupStore {

	String domain;
	String referralCode;
	String accountType;
	String url;
	
	String email;
	String phone;
	String phoneCode;
	String name;
	
	String country;
	String countryCode;
	String region;
	String timezone;
	String language;
	
	String currencyName;
	String currencyCode;
	String currencySymbol;
	
	String username;
	String password;	
	String contact;
	boolean isContactProvided;

	void generateData(CountryData countryEntity) {
		
		region = countryEntity.getRegion();
		timezone = countryEntity.getTimezone();
		currencyName = countryEntity.getCurrency_name();
		currencyCode = countryEntity.getCurrency_code();
		currencySymbol = countryEntity.getCurrency_symbol();
		language = countryEntity.getLanguage();
		
		country = countryEntity.getOut_country();
		countryCode = countryEntity.getCode();
		phoneCode = countryEntity.getPhone_code();
		phone = DataGenerator.randomValidPhoneByCountry(countryCode);
		email = "auto0-shop%s@mailnesia.com".formatted(phone);	
		name = "Automation Shop %s".formatted(getPhone());

		if (accountType==null) accountType = DataGenerator.getRandomListElement(Arrays.asList(new String[] {"EMAIL", "MOBILE"}));
		username = accountType.contentEquals("EMAIL") ? email : phone;
		contact = accountType.contentEquals("EMAIL") ? phone : email;
		password = "fortesting!1";
		isContactProvided = new Random().nextBoolean();

		domain = "domain" + country;
		referralCode = "REFFERAL" + countryCode;
	}
	
	public void randomStoreData() {
		CountryData countryEntity = null;
		for (int i=0; i<500; i++) {
			countryEntity = DataGenerator.getRandomListElement(DataGenerator.getCountryListExp());
			//Temporarily skip these countries as they don't actually have valid phone numbers
			if (countryEntity.getOut_country().contentEquals("Bouvet Island")) continue;
			if (countryEntity.getOut_country().contentEquals("French Southern Territories")) continue;
			if (countryEntity.getOut_country().contentEquals("Heard Island and McDonald Islands")) continue;
			if (countryEntity.getOut_country().contentEquals("Pitcairn Island")) continue;
			if (countryEntity.getOut_country().contentEquals("United States Minor Outlying Islands")) continue;
			if (countryEntity.getOut_country().contentEquals("Antarctica")) continue;
			if (countryEntity.getOut_country().contentEquals("South Georgia")) continue;
			//Temporarily skip this country as its timezone is defined as null in our database
			if (countryEntity.getTimezone()==null) continue;
			//Temporarily skip this country as its language is defined as null in our database
			if (countryEntity.getLanguage()==null) continue;
			break;
		}
		generateData(countryEntity);
	}
	
	public void randomStoreData(String country) {
		generateData(DataGenerator.getCountryListExp().stream().filter(e -> e.getOut_country().contentEquals(country)).findFirst().orElse(null));
	}

}