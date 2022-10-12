package pages.gomua.headergomua;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.gomua.signup.SignupGomua;
import utilities.UICommonAction;

import java.time.Duration;

import static utilities.links.Links.GOMUA_URL;

public class HeaderGoMua {
	final static Logger logger = LogManager.getLogger(HeaderGoMua.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;
	HeaderGoMuaElement headerUI;

	public HeaderGoMua(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		headerUI = new HeaderGoMuaElement(driver);
		PageFactory.initElements(driver, this);
	}

	public HeaderGoMua navigateToGoMua() {
		commonAction.navigateToURL(GOMUA_URL);
		return this;
	}

	public HeaderGoMua clickOnLogInBTN() {
		commonAction.clickElement(headerUI.LOGIN_BTN);
		logger.info("Click on Login button on Header");
		return this;
	}

	public SignupGomua clickSignUpBtn() {
		commonAction.clickElement(headerUI.SIGNUP_BTN);
		logger.info("Click on 'Sign Up' button on Header");
		return new SignupGomua(driver);
	}	
	
	public HeaderGoMua verifyDisplayName(String expected) {
		Assert.assertEquals(commonAction.getText(headerUI.DISPLAY_NAME), expected);
		logger.info("Display name on header is updated");
		return this;
	}

	public HeaderGoMua clickOnDisplayName() {
		commonAction.clickElement(headerUI.DISPLAY_NAME);
		logger.info("Click on Display name");
		return this;
	}

	public HeaderGoMua clickOnMyProfile() {
		commonAction.clickElement(headerUI.MY_PROFILE);
		logger.info("Click on My profile");
		return this;
	}

	public HeaderGoMua goToMyProfile() {
		clickOnDisplayName();
		clickOnMyProfile();
		return this;
	}

	public HeaderGoMua clickOnChangeLanguage() {
		commonAction.clickElement(headerUI.CHANGE_LANGUAGE);
		logger.info("Click on Change location");
		return this;
	}

	public HeaderGoMua selectLanguage(String lang) {
		if (lang.equalsIgnoreCase("english")) {
			commonAction.clickElement(headerUI.ENGLISH);
		} else {
			commonAction.clickElement(headerUI.VIETNAMESE);
		}
		logger.info("Click on %s button".formatted(lang));
		return this;
	}

	public HeaderGoMua clickOnSaveOnPOPUP() {
		commonAction.clickElement(headerUI.POPUP_SAVE_BTN);
		logger.info("Click on Save button");
		return this;
	}

	public HeaderGoMua changeLanguage(String lang) {
		clickOnDisplayName();
		clickOnChangeLanguage();
		selectLanguage(lang);
		clickOnSaveOnPOPUP();
		return this;
	}

	public HeaderGoMua clickCreateShop() {
		commonAction.clickElement(headerUI.CREATE_SHOP_LINKTEXT);
		logger.info("Clicked on 'Create Shop' link text.");
		return this;
	}

	public SignupGomua clickCreateGomuaAccountBtn() {
		commonAction.clickElement(headerUI.CREATE_ACCOUNT_BTN);
		logger.info("Clicked on 'Create Gomua Account' button.");
		return new SignupGomua(driver);
	}
}
