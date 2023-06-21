package pages.dashboard.marketing.landingpage;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class CreateLandingPage {

	final static Logger logger = LogManager.getLogger(CreateLandingPage.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreateLandingPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "domain-value-sub")
	WebElement SUB_DOMAIN;

	@FindBy(id = "customer-tag")
	WebElement CUSTOMER_TAG;

	@FindBy(id = "ggId")
	WebElement GOOGLE_ANALYTICS_ID;

	@FindBy(id = "fbId")
	WebElement FACEBOOK_PIXEL_ID;

	@FindBy(id = "seoTitle")
	WebElement SEO_TITLE;

	@FindBy(id = "seoDescription")
	WebElement SEO_DESCRIPTION;

	@FindBy(id = "seoKeywords")
	WebElement SEO_KEYWORDS;

	@FindBy(css = ".group-btn .btn-save")
	WebElement SAVE_BTN;

	@FindBy(xpath = "(//section[@class='landing-page-editor__pane--middle']/section//button[contains(@class,'gs-button__gray--outline')])[2]")
	WebElement CANCEL_BTN;

	@FindBy(css = "[data-sherpherd=\"tour-guide-alert-button-close\"]")
	WebElement CLOSE_BTN;

	public CreateLandingPage inputSubDomain(String domain) {
		commonAction.inputText(SUB_DOMAIN, domain);
		logger.info("Input '" + domain + "' into Sub-domain field.");
		return this;
	}

	public CreateLandingPage inputCustomerTag(String tag) {
		commonAction.inputText(CUSTOMER_TAG, tag);
		logger.info("Input '" + tag + "' into Customer Tag field.");
		return this;
	}

	public CreateLandingPage inputGoogleAnalyticsId(String id) {
		commonAction.inputText(GOOGLE_ANALYTICS_ID, id);
		logger.info("Input '" + id + "' into Google Analytics Id field.");
		return this;
	}

	public CreateLandingPage inputFacebookPixelId(String id) {
		commonAction.inputText(FACEBOOK_PIXEL_ID, id);
		logger.info("Input '" + id + "' into Facebook Pixel Id field.");
		return this;
	}

	public CreateLandingPage inputSEOTitle(String title) {
		commonAction.inputText(SEO_TITLE, title);
		logger.info("Input '" + title + "' into SEO Title field.");
		return this;
	}

	public CreateLandingPage inputSEODescription(String description) {
		commonAction.inputText(SEO_DESCRIPTION, description);
		logger.info("Input '" + description + "' into SEO Description field.");
		return this;
	}

	public CreateLandingPage inputSEOKeywords(String keywords) {
		commonAction.inputText(SEO_KEYWORDS, keywords);
		logger.info("Input '" + keywords + "' into SEO Keywords field.");
		return this;
	}

	public CreateLandingPage clickSaveBtn() {
		commonAction.clickElement(SAVE_BTN);
		logger.info("Clicked on 'Save' button");
		return this;
	}

	public CreateLandingPage clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}

	public CreateLandingPage clickCloseBtn() {
		commonAction.clickElement(CLOSE_BTN);
		logger.info("Clicked on 'Close' button");
		return this;
	}

}
