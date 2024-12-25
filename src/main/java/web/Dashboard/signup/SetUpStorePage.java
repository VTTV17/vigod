package web.Dashboard.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.model.dashboard.setupstore.SetupStoreDG;

public class SetUpStorePage {

	final static Logger logger = LogManager.getLogger(SetUpStorePage.class);

	WebDriver driver;
	UICommonAction commonAction;

	public SetUpStorePage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_ddlCountry = By.cssSelector(".select-country .select-country-setup");
	By loc_ddlTimeZone = By.id("time-zone--selection");
	String loc_ddvTimeZone = "//div[@class='time-zone--selection-option' and .=\"%s\"]";
	By loc_ddlStoreLanguage = By.cssSelector(".select-country-setup.select.language");
	String loc_ddvStoreLanguage = "//div[contains(@class,'select-country__option') and .=\"%s\"]";
	By loc_ddlCurrency = By.id("currency--selection");
	String loc_ddvCurrency = "//div[@class='currency--selection-option' and .=\"%s\"]";
	By loc_btnContinue = By.cssSelector("form.setup-container button[type='submit']");

	public String getSelectedCountry() {
		String country = commonAction.getText(loc_ddlCountry);
		logger.info("Retrieved selected country: {}", country);    	
		return country;
	}    
	public SetUpStorePage selectTimeZone(String country, String region, String timezone) {
		String formatedTimezone = (region == null) ?  "%s%s".formatted(country, timezone) : "%s - %s%s".formatted(country, region, timezone);

		if(getSelectedTimeZone().contentEquals(formatedTimezone)) return this;

		commonAction.click(loc_ddlTimeZone);
		commonAction.click(By.xpath(loc_ddvTimeZone.formatted(formatedTimezone)));
		logger.info("Selected TimeZome: {}", formatedTimezone);    	
		return this;
	}
	public String getSelectedTimeZone() {
		String timezone = commonAction.getText(loc_ddlTimeZone).replaceAll("\\n", "");
		logger.info("Retrieved selected timezone: {}", timezone);    	
		return timezone;
	}     
	public SetUpStorePage selectLanguage(String language) {

		if(getSelectedLanguage().contentEquals(language)) return this;

		commonAction.click(loc_ddlStoreLanguage);
		commonAction.click(By.xpath(loc_ddvStoreLanguage.formatted(language)));
		logger.info("Selected Store Language: {}", language);    	
		return this;
	}
	public String getSelectedLanguage() {
		String language = commonAction.getText(loc_ddlStoreLanguage);
		logger.info("Retrieved selected Store Language: {}", language);    	
		return language;
	}   
	public SetUpStorePage selectCurrency(String currencyName, String currencyCode, String currencySymbol) {
		String formattedCurrency = "%s%s(%s)".formatted(currencyName, currencyCode, currencySymbol);

		if(getSelectedCurrency().contentEquals(formattedCurrency)) return this;

		commonAction.click(loc_ddlCurrency);
		commonAction.click(By.xpath(loc_ddvCurrency.formatted(formattedCurrency)));
		logger.info("Selected Store Currency: {}", formattedCurrency);    	
		return this;
	}
	public String getSelectedCurrency() {
		String currency = commonAction.getText(loc_ddlCurrency).replaceAll("\\n", "");
		logger.info("Retrieved selected Store Currency: {}", currency);    	
		return currency;
	}      
	public void clickContinueBtn() {
		commonAction.click(loc_btnContinue);
		logger.info("Clicked Continue button.");     
	}

	public void setupShopExp(String country, String region, String timezone, String language, String currencyName, String currencyCode, String currencySymbol) {
//		selectTimeZone(country, region, timezone);
		selectLanguage(language);
		selectCurrency(currencyName, currencyCode, currencySymbol);
		clickContinueBtn();
	}    
	
	public void setupShopExp(SetupStoreDG store) {
		setupShopExp( 
				store.getCountry(), store.getRegion(), store.getTimezone(), 
				store.getLanguage(), 
				store.getCurrencyName(), store.getCurrencyCode(), store.getCurrencySymbol());
	}    
}
