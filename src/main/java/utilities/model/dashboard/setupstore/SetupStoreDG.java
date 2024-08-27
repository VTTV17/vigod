package utilities.model.dashboard.setupstore;

import java.util.Arrays;
import java.util.Random;

import lombok.Data;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.CountryTDG;
import utilities.links.Links;

@Data
public class SetupStoreDG {

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
		phone = country.contentEquals("Vietnam") ? "0" + DataGenerator.randomValidPhoneByCountry(country) : DataGenerator.randomValidPhoneByCountry(country);
		email = "auto0-shop%s@mailnesia.com".formatted(phone);	
		name = "Automation Shop %s".formatted(getPhone());

		if (accountType==null) accountType = DataGenerator.getRandomListElement(Arrays.asList(new String[] {"EMAIL", "MOBILE"}));
		username = accountType.contentEquals("EMAIL") ? email : phone;
		contact = accountType.contentEquals("EMAIL") ? phone : email;
		password = "fortesting!1";
		isContactProvided = new Random().nextBoolean();

		if (domain==null) domain = DataGenerator.getRandomListElement(Arrays.asList(new String[] {Links.DOMAIN + Links.SIGNUP_PATH, Links.DOMAIN_BIZ + Links.SIGNUP_PATH}));
		referralCode = "REFFERAL" + countryCode;
	}
	
	public void randomStoreData() {
		generateData(CountryTDG.randomStoreCountry());
	}
	
	public void randomStoreData(String country) {
		generateData(DataGenerator.getCountryListExp().stream().filter(e -> e.getOut_country().contentEquals(country)).findFirst().orElse(null));
	}

}