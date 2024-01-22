package web.Dashboard.onlineshop.blog.categorymanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class CategoryManagement {

	final static Logger logger = LogManager.getLogger(CategoryManagement.class);

	WebDriver driver;
	UICommonAction commonAction;


	public CategoryManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnCreateCategory = By.cssSelector(".gss-content-header--undefined .gs-button__green");

    public CategoryManagement clickCreateCategory() {
    	commonAction.click(loc_btnCreateCategory);
    	logger.info("Clicked on 'Create Article' button.");
    	return this;
    }   
}
