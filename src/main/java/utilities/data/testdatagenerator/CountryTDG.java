package utilities.data.testdatagenerator;

import java.util.ArrayList;
import java.util.List;

import utilities.data.DataGenerator;
import utilities.model.dashboard.setupstore.CountryData;
import utilities.utils.ListUtils;

public class CountryTDG {
	/**
	 * <p>These countries are excluded from country pool for automation as their phone doesn't comply to GoSELL's phone criteria
	 * (Eg. phone number length is not in the range of (8, 15))
	 */
	public static final List<String> excludedCountries; 
	static {
		excludedCountries = new ArrayList<>();
		//these countries have phone numbers of less than 8 digits
		excludedCountries.add("Aland Islands");
		excludedCountries.add("Austria");
		excludedCountries.add("Andorra");
		excludedCountries.add("Aruba");
		excludedCountries.add("Belize");
		excludedCountries.add("Bonaire, Sint Eustatius and Saba");
		excludedCountries.add("British Indian Ocean Territory");
		excludedCountries.add("Brunei");
		excludedCountries.add("Cape Verde");
		excludedCountries.add("Comoros");
		excludedCountries.add("Cook Islands");
		excludedCountries.add("Eritrea");
		excludedCountries.add("Estonia");
		excludedCountries.add("Falkland Islands");
		excludedCountries.add("Faroe Islands");
		excludedCountries.add("Fiji Islands");
		excludedCountries.add("Gabon");
		excludedCountries.add("Gambia The");
		excludedCountries.add("Greenland");
		excludedCountries.add("Guyana");
		excludedCountries.add("Iceland");
		excludedCountries.add("Liberia");
		excludedCountries.add("Liechtenstein");
		excludedCountries.add("Maldives");
		excludedCountries.add("Marshall Islands");
		excludedCountries.add("Micronesia");
		excludedCountries.add("Nauru");
		excludedCountries.add("New Caledonia");
		excludedCountries.add("Niue");
		excludedCountries.add("Norfolk Island");
		excludedCountries.add("Palau");
		excludedCountries.add("Panama");
		excludedCountries.add("Saint Helena");
		excludedCountries.add("Saint Pierre and Miquelon");
		excludedCountries.add("Sao Tome and Principe");
		excludedCountries.add("Seychelles");
		excludedCountries.add("Solomon Islands");
		excludedCountries.add("Suriname");
		excludedCountries.add("Tokelau");
		excludedCountries.add("Tonga");
		excludedCountries.add("Tuvalu");
		excludedCountries.add("Vanuatu");
		excludedCountries.add("Wallis And Futuna Islands");
		//they don't actually have valid phone numbers
		excludedCountries.add("Bouvet Island");
		excludedCountries.add("French Southern Territories");
		excludedCountries.add("Heard Island and McDonald Islands");
		excludedCountries.add("Pitcairn Island");
		excludedCountries.add("United States Minor Outlying Islands");
		excludedCountries.add("Antarctica");
		excludedCountries.add("South Georgia");
		//wrong phoneCode in database
		excludedCountries.add("Montserrat");
		excludedCountries.add("Trinidad And Tobago");
	}
	
	/**
	 * <p>Retrieves a random CountryData DTO to create stores excluding countries in the list of excluded countries
	 * <p>Countries having null timezone/language will also be excluded
	 * @return CountryData DTO
	 */
	public static CountryData randomStoreCountry() {
	    CountryData countryEntity;
	    int attempts = 0;
	    do {
	        countryEntity = ListUtils.getRandomListElement(DataGenerator.getCountryListExp());
	        attempts++;
	    } while ((excludedCountries.contains(countryEntity.getOut_country()) ||
	              countryEntity.getTimezone() == null ||
	              countryEntity.getLanguage() == null) && attempts < 500);
	    return countryEntity;
	}	
	
	/**
	 * <p>Retrieves a random CountryData DTO to create customers excluding countries in the list of excluded countries.
	 * @return CountryData DTO
	 */
	public static CountryData randomCustomerCountry() {
	    CountryData countryEntity;
	    int attempts = 0;
	    do {
	        countryEntity = ListUtils.getRandomListElement(DataGenerator.getCountryListExp());
	        attempts++;
	    } while (excludedCountries.contains(countryEntity.getOut_country()) && attempts < 500);
	    return countryEntity;
	}	
	
}