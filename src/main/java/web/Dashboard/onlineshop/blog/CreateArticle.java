package web.Dashboard.onlineshop.blog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.links.Links;

public class CreateArticle {

	final static Logger logger = LogManager.getLogger(CreateArticle.class);

	WebDriver driver;
	UICommonAction commonAction;


	public CreateArticle(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtArticleName = By.id("title");
	By loc_txaDescription = By.cssSelector(".fr-view");
	By loc_btnSave = By.cssSelector(".gs-button__green");
	By loc_ddlCategory = By.xpath("//input[contains(@id,'input')]/parent::div");
	By loc_ddlCategory_lstOption = By.xpath("//div[contains(@id,'option')]");
	By loc_btnEditTranslation = By.xpath("//button[contains(@class,'gs-button__green')]/preceding-sibling::button");
	By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
	public CreateArticle inputTitleName(String articleTitle) {
		commonAction.sendKeys(loc_txtArticleName, articleTitle);
		logger.info("Input '" + articleTitle + "' into Title Name field.");
		return this;
	}
	public CreateArticle inputDescription(){
		commonAction.inputText(loc_txaDescription,"Article desciption "+new DataGenerator().generateString(10));
		logger.info("Input description");
		return this;
	}
	public void clickOnSave(){
		commonAction.click(loc_btnSave);
		logger.info("Click on Save button");
	}
	public void navigateByUrl(int id){
		String url = Links.DOMAIN + "/channel/storefront/blog/article/edit/"+id;
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
	}
	public void createSimpleArticle(){
		String radom = new DataGenerator().generateString(10);
		inputTitleName("Article "+radom);
		inputDescription();
		clickOnSave();
	}
	public void clickOnCategory(){
		commonAction.click(loc_ddlCategory);
		logger.info("Click on Category dropdown.");
	}
	public void clickOnEditTranslation(){
		commonAction.click(loc_btnEditTranslation);
		logger.info("Click on Edit Translation button");
		commonAction.sleepInMiliSecond(300,"Wait popup show.");
	}
	public void clickSaveOnEditTranslationModal(){
		commonAction.click(loc_dlgEditTranslation_btnSave);
		logger.info("Click on Save button on Edit Translation modal.");
	}
}
