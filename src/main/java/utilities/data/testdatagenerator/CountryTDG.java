package utilities.data.testdatagenerator;

import java.util.ArrayList;
import java.util.List;

import utilities.data.DataGenerator;
import utilities.model.dashboard.setupstore.CountryData;

public class CountryTDG {
	public static List<String> avoidedCountry() {
		List<String> avoidedCountries = new ArrayList<>();
		//these countries have phone numbers of less than 8 digits
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
		//they don't actually have valid phone numbers
		avoidedCountries.add("Bouvet Island");
		avoidedCountries.add("French Southern Territories");
		avoidedCountries.add("Heard Island and McDonald Islands");
		avoidedCountries.add("Pitcairn Island");
		avoidedCountries.add("United States Minor Outlying Islands");
		avoidedCountries.add("Antarctica");
		avoidedCountries.add("South Georgia");
		
		return avoidedCountries;
	}
	
	/**
	 * <p>Retrieves a random CountryData DTO to create stores excluding countries in the list of avoided countries
	 * <p>Countries having null timezone/language will also be excluded
	 * @return CountryData DTO
	 */
	public static CountryData randomStoreCountry() {
		CountryData countryEntity = null;
		for (int i=0; i<500; i++) {
			countryEntity = DataGenerator.getRandomListElement(DataGenerator.getCountryListExp());
			
			//Temporarily skip these country as they have valid phone numbers
			if(avoidedCountry().contains(countryEntity.getOut_country())) continue;
			
			//Temporarily skip this country as its timezone is defined as null in our database
			if (countryEntity.getTimezone()==null) continue;
			//Temporarily skip this country as its language is defined as null in our database
			if (countryEntity.getLanguage()==null) continue;
			break;
		}
		return countryEntity;
	}	
	
	/**
	 * <p>Retrieves a random CountryData DTO to create customers excluding countries in the list of avoided countries.
	 * @return CountryData DTO
	 */
	public static CountryData randomCustomerCountry() {
		CountryData countryEntity = null;
		for (int i=0; i<500; i++) {
			countryEntity = DataGenerator.getRandomListElement(DataGenerator.getCountryListExp());
			if(avoidedCountry().contains(countryEntity.getOut_country())) continue;
			break;
		}
		return countryEntity;
	}	
	
}