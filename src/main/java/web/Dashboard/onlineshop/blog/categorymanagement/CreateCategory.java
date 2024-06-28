package web.Dashboard.onlineshop.blog.categorymanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.links.Links;

public class CreateCategory {

	final static Logger logger = LogManager.getLogger(CreateCategory.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreateCategory(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	public By loc_txtCategoryName = By.id("title");
	public By loc_btnSave = By.cssSelector(".gs-button__green");
	public By loc_btnEditTranslation = By.xpath("//button[contains(@class,'gs-button__green')]/preceding-sibling::button");
	By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
	public CreateCategory inputCategoryName(String category) {
		commonAction.sendKeys(loc_txtCategoryName, category);
		logger.info("Input '" + category + "' into Category Name field.");
		return this;
	}
	public CategoryManagement clickOnSaveBtn(){
		commonAction.click(loc_btnSave);
		logger.info("Click on Save button");
		return new CategoryManagement(driver);
	}
	public CreateCategory navigateByUrl(int id){
		String url = Links.DOMAIN + "/channel/storefront/blog/article/category/edit/"+id;
		commonAction.navigateToURL(url);
		logger.info("Navigate to edit category url: "+url);
		return this;
	}
	public CategoryManagement createSimpleCategory(){
		String random = new DataGenerator().generateString(10);
		inputCategoryName("Category "+random);
		return clickOnSaveBtn();
	}
	public CreateCategory clickOnEditTranslation(){
		commonAction.click(loc_btnEditTranslation);
		logger.info("Click on Edit Translation button");
		return this;
	}
	public CreateCategory clickSaveOnEditTranslationModal(){
		commonAction.click(loc_dlgEditTranslation_btnSave);
		logger.info("Click on Save button on Edit Translation modal.");
		return this;
	}
}
