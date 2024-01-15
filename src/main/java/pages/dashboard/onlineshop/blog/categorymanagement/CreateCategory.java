package pages.dashboard.onlineshop.blog.categorymanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class CreateCategory {

	final static Logger logger = LogManager.getLogger(CreateCategory.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreateCategory(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtCategoryName = By.id("title");

	public CreateCategory inputCategoryName(String category) {
		commonAction.sendKeys(loc_txtCategoryName, category);
		logger.info("Input '" + category + "' into Category Name field.");
		return this;
	}

}
