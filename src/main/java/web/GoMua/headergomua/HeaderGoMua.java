package web.GoMua.headergomua;

import static utilities.links.Links.GOMUA_URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.GoMua.signup.SignupGomua;
import utilities.commons.UICommonAction;

public class HeaderGoMua {
	final static Logger logger = LogManager.getLogger(HeaderGoMua.class);

	WebDriver driver;
	UICommonAction commonAction;
	HeaderGoMuaElement headerUI;

	public HeaderGoMua(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		headerUI = new HeaderGoMuaElement();
	}

	public HeaderGoMua navigateToGoMua() {
		commonAction.navigateToURL(GOMUA_URL);
		return this;
	}

	public HeaderGoMua clickOnLogInBTN() {
		commonAction.click(headerUI.loc_btnLogin);
		logger.info("Click on Login button on Header");
		return this;
	}

	public SignupGomua clickSignUpBtn() {
		commonAction.click(headerUI.loc_btnSingup);
		logger.info("Click on 'Sign Up' button on Header");
		return new SignupGomua(driver);
	}	
	
	public HeaderGoMua verifyDisplayName(String expected) {
		Assert.assertEquals(commonAction.getText(headerUI.loc_lblDisplayName), expected);
		logger.info("Display name on header is updated");
		return this;
	}

	public HeaderGoMua clickOnDisplayName() {
		commonAction.click(headerUI.loc_lblDisplayName);
		logger.info("Click on Display name");
		return this;
	}

	public HeaderGoMua clickOnMyProfile() {
		commonAction.click(headerUI.loc_lblMyProfile);
		logger.info("Click on My profile");
		return this;
	}

	public HeaderGoMua goToMyProfile() {
		clickOnDisplayName();
		clickOnMyProfile();
		return this;
	}

	public HeaderGoMua clickOnChangeLanguage() {
		commonAction.click(headerUI.loc_lblChangeLanguage);
		logger.info("Click on Change location");
		return this;
	}

	public HeaderGoMua selectLanguage(String lang) {
		if (lang.equalsIgnoreCase("english")) {
			commonAction.click(headerUI.loc_lblEnglish);
		} else {
			commonAction.click(headerUI.loc_lblVietnamese);
		}
		logger.info("Click on %s button".formatted(lang));
		return this;
	}

	public HeaderGoMua clickOnSaveOnPOPUP() {
		commonAction.click(headerUI.loc_btnSavePopup);
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

	public ChangePasswordDialog clickChangePassword() {
		commonAction.click(headerUI.loc_lblChangePassword);
		logger.info("Clicked on 'Change Password' link text.");
		return new ChangePasswordDialog(driver);
	}		
	
	public HeaderGoMua clickCreateShop() {
		commonAction.click(headerUI.loc_lnkCreateShop);
		logger.info("Clicked on 'Create Shop' link text.");
		return this;
	}

	public SignupGomua clickCreateGomuaAccountBtn() {
		commonAction.click(headerUI.loc_btnCreateAccount);
		logger.info("Clicked on 'Create Gomua Account' button.");
		return new SignupGomua(driver);
	}
}
