package app.Buyer.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonMobile;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;

public class BuyerChangeLanguage extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(BuyerChangeLanguage.class);

    WebDriver driver;

    public BuyerChangeLanguage(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }
    String LANGUAGE_BTN_DYNAMIC = "//android.widget.TextView[@text='%s']";
    By CHANGE_BTN = By.xpath("//android.widget.Button[contains(@resource-id,'button1')]");
    By SELECTED_LANGUAGE = By.xpath("//android.widget.ImageView[contains(@resource-id,'item_language_checkbox')]//preceding-sibling::android.widget.TextView");
    By HEADER_BACK_ICON = By.xpath("//*[ends-with(@resource-id,'fragment_general_more_settings_change_language_toolbar')]/android.widget.ImageButton");
    
    By languageByLangNameLocator(String langName) {
    	return By.xpath("//*[ends-with(@resource-id, 'id/item_language_name') and @text=\"%s\"]".formatted(langName));
    }
    
    public void changeLanguage(String lang){
        String currentLanguage = getText(SELECTED_LANGUAGE);
        String selectLang = "";
        switch (lang){
            case "ENG"-> selectLang = "English";
            case "VIE" -> selectLang = "Vietnamese";
            default -> {
                try {
                    throw new Exception("Language not match!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(!selectLang.equalsIgnoreCase(currentLanguage)){
            sleepInMiliSecond(2000);
            String newXpach = LANGUAGE_BTN_DYNAMIC.formatted(selectLang);
            clickElement(getElementByXpath(newXpach));
            sleepInMiliSecond(200);
            clickElement(CHANGE_BTN);
            sleepInMiliSecond(2000);
        }else {
            clickElement(HEADER_BACK_ICON);
        }
    }
    public String getCurrentLanguage(){
        String currentLanguage = getText(SELECTED_LANGUAGE);
        switch (currentLanguage){
            case "English"-> {
                return "ENG";
            }
            case "Tiếng Việt" -> {
                return "VIE";
            }
            default -> {
                try {
                    throw new Exception("Language not match!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    /**
     * Selects display language on Buyer app
     * @param language
     */
    public BuyerChangeLanguage selectLanguageName(AdditionalLanguages language) {
    	
    	var languageName = language.getLangName();
    	
    	if (getText(SELECTED_LANGUAGE).contentEquals(languageName)) {
    		logger.info("Currently selected language is {}. No need to change it", languageName);
    		
    		//Go back to Account tab
    		clickElement(HEADER_BACK_ICON);
    		
    		return this;
    	}
    	
    	clickElement(languageByLangNameLocator(languageName));
    	
    	//Confirm language change
    	clickElement(CHANGE_BTN);
    	
    	logger.info("Selected language: {}", languageName);
    	
    	return this;
    }
    
}
