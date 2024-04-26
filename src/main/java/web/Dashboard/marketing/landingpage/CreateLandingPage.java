package web.Dashboard.marketing.landingpage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import web.Dashboard.home.HomePage;

import java.util.ArrayList;
import java.util.List;

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
	By loc_btnSave = By.cssSelector(".justify-content-end .gs-button__green");
	By loc_btnCancel = By.xpath("(//section[@class='landing-page-editor__pane--middle']/section//button[contains(@class,'gs-button__gray--outline')])[2]");
	By loc_btnClose = By.cssSelector("[data-sherpherd='tour-guide-alert-button-close']");
	By loc_txtTitleName = By.cssSelector("#title");
	By loc_btnSelectTemplate = By.cssSelector(".landing-page-editor-setting__template-selector button");
	/*
	0: previous
	1: page 1
	maximun 4 page
	 */
	By loc_dlgSelectTemplate_lstPageNumber = By.cssSelector(".page-item");
	String xpathUseBtnByName ="//div[@class='modal-body']//span[text()='%s']/following-sibling::div/button[contains(@class,'gs-button__green')]";
	By loc_txtSearchProduct = By.cssSelector(".search-input input");
	By loc_lst_lblProductNameSearchResult = By.cssSelector(".search-list .name");
	By loc_btnPublish = By.cssSelector(".landing-page-editor-live-preview__btn--publish");
	By loc_btnUnpublish = By.cssSelector(".landing-page-editor-live-preview__btn--draft");

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
	public CreateLandingPage inputTitleName(String titleName){
		commonAction.inputText(loc_txtTitleName,titleName);
		logger.info("Input title name: "+titleName);
		return this;
	}
	public CreateLandingPage createLandingPage(){
		String radom = new DataGenerator().generateString(10);
		String name ="Landing page "+ radom;
		inputTitleName(name);
		inputSubDomain(radom);
		commonAction.sleepInMiliSecond(2000);
		clickSaveBtn();
		return this;
	}
	public CreateLandingPage clickOnSelectTemplate(){
		commonAction.click(loc_btnSelectTemplate);
		logger.info("Click on Select template button.");
		return this;
	}
	/*
	0: page 1
	Maximun 4 page
	 */
	public CreateLandingPage selectPageTemplate(int pageNumber){
		commonAction.click(loc_dlgSelectTemplate_lstPageNumber,pageNumber);
		logger.info("Select page %s on Select Template dialog.");
		return this;
	}
	public CreateLandingPage selectToUseTemplate(String templateName){
		By loc = By.xpath(xpathUseBtnByName.formatted(templateName));
		commonAction.click(loc);
		logger.info("Select template: "+templateName);
		return this;
	}
	public CreateLandingPage selectCheckoutTemplate(){
		clickOnSelectTemplate();
		selectPageTemplate(3);
		selectToUseTemplate("Page Checkout 01");
		new HomePage(driver).waitTillLoadingDotsDisappear();
		return this;
	}
	public boolean isProductShowWhenSearch(String productName){
		commonAction.inputText(loc_txtSearchProduct,productName);
		commonAction.sleepInMiliSecond(100);
		new HomePage(driver).waitTillSpinnerDisappear1();
		List<WebElement> productNames = new ArrayList<>();
		for (int j=0;j<5;j++){
			productNames = commonAction.getElements(loc_lst_lblProductNameSearchResult);
			if(!productNames.isEmpty()) {
				commonAction.sleepInMiliSecond(500);
				break;
			}
		}
		if (productNames.isEmpty()) return false;
		for (int i=0; i<productNames.size();i++) {
			if(commonAction.getText(loc_lst_lblProductNameSearchResult,i).equalsIgnoreCase(productName))
				return true;
		}
		return false;
	}
	public CreateLandingPage clickOnPublishBtn(){
		commonAction.click(loc_btnPublish);
		logger.info("Click on Publish button.");
		return this;
	}
	public CreateLandingPage clickOnUnPublishBtn(){
		commonAction.click(loc_btnUnpublish);
		logger.info("Click on Unpublish button.");
		return this;
	}
}
