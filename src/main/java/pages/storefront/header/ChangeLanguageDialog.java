package pages.storefront.header;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utilities.UICommonAction;

public class ChangeLanguageDialog {

	final static Logger logger = LogManager.getLogger(ChangeLanguageDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ChangeLanguageDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "[rv-on-click=\"methods.changeLang | args 'en'\"]")
	WebElement ENGLISH_LANGUAGE;
	
	@FindBy(css = "[rv-on-click=\"methods.changeLang | args 'vi'\"]")
	WebElement VIETNAMESE_LANGUAGE;

	@FindBy(css = "#modalChangeLanguage .btn-primary")
	WebElement SAVE_BTN;

	@FindBy(css = "#modalChangeLanguage .btn-secondary")
	WebElement CLOSE_BTN;

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
			commonAction.clickElement(ENGLISH_LANGUAGE);
		} else if (language.contentEquals("VIE")) {
			commonAction.clickElement(VIETNAMESE_LANGUAGE);
		} else {
			throw new Exception("Input value does not match any of the accepted values: English/Vietnamese");
		}
		logger.info("Selected language '%s'.".formatted(language));
		return this;
	}

	public ChangeLanguageDialog clickSaveBtn() {
		commonAction.clickElement(SAVE_BTN);
		logger.info("Clicked on 'Save' button");
		return this;
	}

	public ChangeLanguageDialog clickCloseBtn() {
		commonAction.clickElement(CLOSE_BTN);
		logger.info("Clicked on 'Close' button");
		return this;
	}

}
