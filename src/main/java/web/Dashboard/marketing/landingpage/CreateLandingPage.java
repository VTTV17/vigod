package web.Dashboard.marketing.landingpage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class CreateLandingPage {

	final static Logger logger = LogManager.getLogger(CreateLandingPage.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreateLandingPage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtSubDomain = By.id("domain-value-sub");
	By loc_txtCustomerTag = By.id("customer-tag");
	By loc_txtGoogleAnalyticsId = By.id("ggId");
	By loc_txtFacebookPixelId = By.id("fbId");
	By loc_txtSEOTitle = By.id("seoTitle");
	By loc_txtSEODescription = By.id("seoDescription");
	By loc_txtSEOKeyword = By.id("seoKeywords");
	By loc_btnSave = By.cssSelector(".group-btn .btn-save");
	By loc_btnCancel = By.xpath("(//section[@class='landing-page-editor__pane--middle']/section//button[contains(@class,'gs-button__gray--outline')])[2]");
	By loc_btnClose = By.cssSelector("[data-sherpherd='tour-guide-alert-button-close']");

	public CreateLandingPage inputSubDomain(String domain) {
		commonAction.sendKeys(loc_txtSubDomain, domain);
		logger.info("Input '" + domain + "' into Sub-domain field.");
		return this;
	}

	public CreateLandingPage inputCustomerTag(String tag) {
		commonAction.sendKeys(loc_txtCustomerTag, tag);
		logger.info("Input '" + tag + "' into Customer Tag field.");
		return this;
	}

	public CreateLandingPage inputGoogleAnalyticsId(String id) {
		commonAction.sendKeys(loc_txtGoogleAnalyticsId, id);
		logger.info("Input '" + id + "' into Google Analytics Id field.");
		return this;
	}

	public CreateLandingPage inputFacebookPixelId(String id) {
		commonAction.sendKeys(loc_txtFacebookPixelId, id);
		logger.info("Input '" + id + "' into Facebook Pixel Id field.");
		return this;
	}

	public CreateLandingPage inputSEOTitle(String title) {
		commonAction.sendKeys(loc_txtSEOTitle, title);
		logger.info("Input '" + title + "' into SEO Title field.");
		return this;
	}

	public CreateLandingPage inputSEODescription(String description) {
		commonAction.sendKeys(loc_txtSEODescription, description);
		logger.info("Input '" + description + "' into SEO Description field.");
		return this;
	}

	public CreateLandingPage inputSEOKeywords(String keywords) {
		commonAction.sendKeys(loc_txtSEOKeyword, keywords);
		logger.info("Input '" + keywords + "' into SEO Keywords field.");
		return this;
	}

	public CreateLandingPage clickSaveBtn() {
		commonAction.click(loc_btnSave);
		logger.info("Clicked on 'Save' button");
		return this;
	}

	public CreateLandingPage clickCancelBtn() {
		commonAction.click(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}

	public CreateLandingPage clickCloseBtn() {
		commonAction.click(loc_btnClose);
		logger.info("Clicked on 'Close' button");
		return this;
	}

}
