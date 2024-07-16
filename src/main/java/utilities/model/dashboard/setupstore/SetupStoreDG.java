package utilities.model.dashboard.setupstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.Data;
import utilities.data.DataGenerator;
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

	List<String> avoidedCountry() {
		List<String> avoidedCountries = new ArrayList<>();
		avoidedCountries.add("Aland Islands");
		avoidedCountries.add("Andorra");
		avoidedCountries.add("Aruba");
		avoidedCountries.add("Belize");
		avoidedCountries.add("Bonaire, Sint Eustatius and Saba");
		avoidedCountries.add("British Indian Ocean Territory");
		avoidedCountries.add("Brunei");
		avoidedCountries.add("Cape Verde");
		avoidedCountries.add("Comoros");
		avoidedCountries.add("Cook Islands");
		avoidedCountries.add("Eritrea");
		avoidedCountries.add("Estonia");
		avoidedCountries.add("Falkland Islands");
		avoidedCountries.add("Faroe Islands");
		avoidedCountries.add("Fiji Islands");
		avoidedCountries.add("Gabon");
		avoidedCountries.add("Gambia The");
		avoidedCountries.add("Greenland");
		avoidedCountries.add("Guyana");
		avoidedCountries.add("Iceland");
		avoidedCountries.add("Liberia");
		avoidedCountries.add("Liechtenstein");
		avoidedCountries.add("Maldives");
		avoidedCountries.add("Marshall Islands");
		avoidedCountries.add("Micronesia");
		avoidedCountries.add("Nauru");
		avoidedCountries.add("New Caledonia");
		avoidedCountries.add("Niue");
		avoidedCountries.add("Norfolk Island");
		avoidedCountries.add("Palau");
		avoidedCountries.add("Panama");
		avoidedCountries.add("Saint Helena");
		avoidedCountries.add("Saint Pierre and Miquelon");
		avoidedCountries.add("Sao Tome and Principe");
		avoidedCountries.add("Seychelles");
		avoidedCountries.add("Solomon Islands");
		avoidedCountries.add("Suriname");
		avoidedCountries.add("Tokelau");
		avoidedCountries.add("Tonga");
		avoidedCountries.add("Tuvalu");
		avoidedCountries.add("Vanuatu");
		avoidedCountries.add("Wallis And Futuna Islands");
		return avoidedCountries;
	}
	
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
		phone = country.contentEquals("Vietnam") ? "0" + DataGenerator.randomValidPhoneByCountry(countryCode) : DataGenerator.randomValidPhoneByCountry(countryCode);
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
		CountryData countryEntity = null;
		for (int i=0; i<500; i++) {
			countryEntity = DataGenerator.getRandomListElement(DataGenerator.getCountryListExp());
			
			//Temporarily skip this country as they have phone numbers of less than 8 digits
			if(avoidedCountry().contains(countryEntity.getOut_country())) continue;
			
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