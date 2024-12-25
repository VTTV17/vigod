package utilities.model.dashboard.setupstore;

import java.util.Arrays;
import java.util.Random;

import lombok.Data;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.CountryTDG;
import utilities.enums.Domain;

@Data
public class SetupStoreDG {

	public SetupStoreDG(Domain domain) {
		this.domain = domain;
	}
	
	Domain domain;
	String referralCode;
	String accountType;
	String storeURL;
	
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

		referralCode = (new Random().nextBoolean()) ? "REFFERAL" + countryCode : "";
	}
	
	public void randomStoreData() {
		generateData(CountryTDG.randomStoreCountry());
	}
	
	public void randomStoreData(String country) {
		generateData(DataGenerator.getCountryListExp().stream().filter(e -> e.getOut_country().contentEquals(country)).findFirst().orElse(null));
	}

}