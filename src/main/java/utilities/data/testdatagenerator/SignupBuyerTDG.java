package utilities.data.testdatagenerator;

import java.util.Arrays;

import utilities.data.DataGenerator;
import utilities.enums.AccountType;
import utilities.model.dashboard.setupstore.CountryData;
import utilities.model.dashboard.storefront.BuyerSignupData;
import utilities.utils.ListUtils;

/**
 * This class helps generate dummy data to create buyers
 */
public class SignupBuyerTDG {
	
	static String password = "fortesting!1";
	static String birthday = ListUtils.getRandomListElement(Arrays.asList("21/02/1990", ""));
	
	static String phoneByCountry(String country) {
		return country.contentEquals("Vietnam") ? "0" + DataGenerator.randomValidPhoneByCountry(country) : DataGenerator.randomValidPhoneByCountry(country);
	}
	static String nameByPhone(String phoneNumber) {
		return "Auto Buyer " + phoneNumber;
	}
	static String emailByPhone(String phoneNumber) {
		return "auto-buyer%s@mailnesia.com".formatted(phoneNumber);
	}
	
	public static BuyerSignupData buildSignupByEmailData() {
		
		CountryData randomCountry = CountryTDG.randomCustomerCountry();
		
		var country = randomCountry.getOut_country();
		var phoneNumber = phoneByCountry(country);
		
		return BuyerSignupData.builder()
				.type(AccountType.EMAIL)
				.country(country)
				.countryCode(randomCountry.getCode())
				.phoneCode(randomCountry.getPhone_code())
				.username(emailByPhone(phoneNumber))
				.password(password)
				.email(emailByPhone(phoneNumber))
				.phone(phoneNumber)
				.displayName(nameByPhone(phoneNumber))
				.birthday(birthday)
				.build();
	}
	
	public static BuyerSignupData buildSignupByPhoneData() {
		
		CountryData randomCountry = CountryTDG.randomCustomerCountry();
		
		var country = randomCountry.getOut_country();
		var phoneNumber = phoneByCountry(country);
		
		return BuyerSignupData.builder()
				.type(AccountType.MOBILE)
				.country(country)
				.countryCode(randomCountry.getCode())
				.phoneCode(randomCountry.getPhone_code())
				.username(phoneNumber)
				.password(password)
				.email(emailByPhone(phoneNumber))
				.phone(phoneNumber)
				.displayName(nameByPhone(phoneNumber))
				.birthday(birthday)
				.build();
	}
	
	
	
}