package pages.storefront.header;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.storefront.GeneralSF;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.List;

public class HeaderSF extends GeneralSF {
	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commons;
	final static Logger logger = LogManager.getLogger(HeaderSF.class);

	public HeaderSF(WebDriver driver){
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver,this);
	}

	@FindBy(css = ".navbar-brand.nav-link")
	WebElement USER_INFO_ICON;

	@FindBy(css = "#dropdown-menu-profile > :nth-child(1) a")
	WebElement USER_PROFILE;

	@FindBy(id = "btn-login")
	WebElement LOGIN_ICON;

	@FindBy(id = "btn-signup")
	WebElement SIGNUP_ICON;

	@FindBy(css = "#dropdown-menu-profile #btn-change-pwd")
	WebElement CHANGE_PASSWORD_LINKTEXT;	
	
	@FindBy(css = "[data-target='#modalChangeLanguage']")
	WebElement CHANGE_LANGUAGE_LINKTEXT;	
	
	@FindBy(css = "#header-search-web-component")
	WebElement SEARCH_FIELD_TO_CLICK;

	@FindBy(xpath = "//input[@type='search']")
	WebElement SEARCH_FIELD_TO_INPUT;

	@FindBy (css = ".lds-ellipsis")
	WebElement SEARCH_LOADING;
	@FindBy(xpath = "//h3[contains(@class,'search-result-item-title')]")
	List<WebElement> SEARCH_SUGGESTION_RESULT_TITLE;

	@FindBy(xpath = "//strong[contains(@class,'search-result-item-price')]")
	List<WebElement> SEARCH_SUGGESTION_RESULT_PRICE;

	@FindBy (id = "btn-logout")
	WebElement LOGOUT_BTN;
	@FindBy(css = "[onclick=\"gosellUtils.changeLanguage('en')\"]")
	WebElement ENGLISH_LANGUAGE;
	@FindBy(css = "[onclick=\"gosellUtils.changeLanguage('vi')\"]")
	WebElement VIETNAMESE_LANGUAGE;
	String MENU_ITEM_BY_TEXT = "//div[contains(@class,'desktop')]//a[text()='%s']";
	public HeaderSF clickUserInfoIcon() {
		commons.clickElement(USER_INFO_ICON);
		logger.info("Clicked on User Info icon.");
		return this;
	}

	public UserProfileInfo clickUserProfile() {
		commons.clickElement(USER_PROFILE);
		logger.info("Clicked on User Profile linktext.");
		return new UserProfileInfo(driver);
	}

	public HeaderSF clickLoginIcon() {
		commons.clickElement(LOGIN_ICON);
		logger.info("Clicked on Login icon.");
		return this;
	}

	public HeaderSF clickSignupIcon() {
		commons.clickElement(SIGNUP_ICON);
		logger.info("Clicked on Signup icon.");
		return this;
	}

	public HeaderSF searchWithFullName(String fullName){
		commons.clickElement(SEARCH_FIELD_TO_CLICK);
		logger.info("Click on Search bar");
		commons.inputText(SEARCH_FIELD_TO_INPUT,fullName);
		logger.info("Input: %s into search field".formatted(fullName));
		return this;
	}
	public HeaderSF verifySearchSuggestion(String fullName, String price){
		commons.waitForElementVisible(SEARCH_LOADING);
		commons.waitForElementInvisible(SEARCH_LOADING);
		String searchSuggestionItem1_Title = commons.getText(SEARCH_SUGGESTION_RESULT_TITLE.get(0));
		Assert.assertEquals(searchSuggestionItem1_Title,fullName);
		logger.info("Verify name: %s display on search suggestion".formatted(fullName));
		String searchSuggestionItem1_Price = commons.getText(SEARCH_SUGGESTION_RESULT_PRICE.get(0));
		if(price!="") {
			searchSuggestionItem1_Price = String.join("", searchSuggestionItem1_Price.split(",")).trim();
			System.out.println("searchSuggestionItem1_Price:" + searchSuggestionItem1_Price);
			Assert.assertEquals(searchSuggestionItem1_Price.subSequence(0, searchSuggestionItem1_Price.length() - 1), price.trim());
		}else {
			Assert.assertEquals(searchSuggestionItem1_Price,price);
		}
		logger.info("Verify price: %s display on search suggestion".formatted(price));
		return this;
	}
	public HeaderSF clickSearchResult (){
		commons.clickElement(SEARCH_SUGGESTION_RESULT_TITLE.get(0));
		logger.info("Click on the first suggestion to go to detail page");
		return this;
	}

	public void clickLogout(){
		commons.clickElement(LOGOUT_BTN);
		logger.info("Clicked on Logout linktext");
	}
	public UserProfileInfo navigateToUserProfile(){
		clickUserInfoIcon();
		clickUserProfile();
		return new UserProfileInfo(driver);
	}

	public boolean checkForPresenceOfChangePasswordLink() {
		boolean isDisplayed = commons.isElementDisplay(CHANGE_PASSWORD_LINKTEXT);
		if (isDisplayed) {
			logger.info("'Change Password' link text is displayed.");
		} else {
			logger.info("'Change Password' link text is not displayed.");
		}
		return isDisplayed;
	}
	
	public ChangePasswordDialog clickChangePassword() {
		commons.clickElement(CHANGE_PASSWORD_LINKTEXT);
		logger.info("Clicked on 'Change Password' link text.");
		return new ChangePasswordDialog(driver);
	}
	
	public void clickChangeLanguage() {
		commons.clickElement(CHANGE_LANGUAGE_LINKTEXT);
		logger.info("Clicked on 'Change Language' link text.");
	}

	/**
	* <p>
	* Change language of SF
	* <p>
	* Example: changeLanguage("VIE")
	* @param language the desired language. It is either VIE or ENG
	* 
	*/	
	public HeaderSF changeLanguage(String language) {
		clickChangeLanguage();
		try {
			new ChangeLanguageDialog(driver)
			.selectLanguage(language)
			.clickSaveBtn();
		} catch (Exception e) {
			e.printStackTrace();
		}
		waitTillLoaderDisappear();
		return this;
	}	
	public HeaderSF selectLanguage(String lang) throws Exception {
		if (lang.contentEquals("ENG")) {
			commons.clickElementByJS(ENGLISH_LANGUAGE);
		} else if (lang.contentEquals("VIE")) {
			commons.clickElementByJS(VIETNAMESE_LANGUAGE);
		} else {
			throw new Exception("Input value does not match any of the accepted values: ENG/VIE");
		}
		logger.info("Selected language '%s'.".formatted(lang));
		waitTillLoaderDisappear();
		return this;
	}
	public HeaderSF clickOnMenuItemByText(String menuItemByText){
		String menuItemNewXpath = MENU_ITEM_BY_TEXT.formatted(menuItemByText);
		commons.clickElement(wait.until(ExpectedConditions.visibilityOf(commons.getElementByXpath(menuItemNewXpath))));
		logger.info("Click on menu: "+menuItemByText);
		return this;
	}

	public HeaderSF clickUserInfoIconJS() {
		wait.until(ExpectedConditions.visibilityOf(USER_INFO_ICON));
		((JavascriptExecutor) driver).executeScript("arguments[0].click()", USER_INFO_ICON);
		logger.info("Clicked on User Info icon.");
		return this;
	}

	public void clickLoginIconJS() {
		wait.until(ExpectedConditions.visibilityOf(LOGIN_ICON));
		((JavascriptExecutor) driver).executeScript("arguments[0].click()", LOGIN_ICON);
		logger.info("Clicked on Login icon.");
	}

}
