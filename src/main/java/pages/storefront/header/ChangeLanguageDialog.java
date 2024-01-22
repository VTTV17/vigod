package pages.storefront.header;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class ChangeLanguageDialog {

	final static Logger logger = LogManager.getLogger(ChangeLanguageDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ChangeLanguageDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_lblEnglish = By.cssSelector("[rv-on-click=\"methods.changeLang | args 'en'\"]");
	By loc_lblVietnamese = By.cssSelector("[rv-on-click=\"methods.changeLang | args 'vi'\"]");
	By loc_btnSave = By.cssSelector("#modalChangeLanguage .btn-primary");
	By loc_btnClose = By.cssSelector("#modalChangeLanguage .btn-secondary");

	/**
	 * <p>
	 * Change language of SF
	 * <p>
	 * Example: selectLanguage("English")
	 * 
	 * @param language the desired language. It is either Vietnamese or English
	 * 
	 */	
	public ChangeLanguageDialog selectLanguage(String language) throws Exception {
		if (language.contentEquals("ENG")) {
			commonAction.click(loc_lblEnglish);
		} else if (language.contentEquals("VIE")) {
			commonAction.click(loc_lblVietnamese);
		} else {
			throw new Exception("Input value does not match any of the accepted values: English/Vietnamese");
		}
		logger.info("Selected language '%s'.".formatted(language));
		return this;
	}

	public ChangeLanguageDialog clickSaveBtn() {
		commonAction.click(loc_btnSave);
		logger.info("Clicked on 'Save' button");
		return this;
	}

	public ChangeLanguageDialog clickCloseBtn() {
		commonAction.click(loc_btnClose);
		logger.info("Clicked on 'Close' button");
		return this;
	}

}
