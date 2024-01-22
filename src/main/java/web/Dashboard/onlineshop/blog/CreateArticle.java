package web.Dashboard.onlineshop.blog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class CreateArticle {

	final static Logger logger = LogManager.getLogger(CreateArticle.class);

	WebDriver driver;
	UICommonAction commonAction;


	public CreateArticle(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtArticleName = By.id("title");

	public CreateArticle inputTitleName(String articleTitle) {
		commonAction.sendKeys(loc_txtArticleName, articleTitle);
		logger.info("Input '" + articleTitle + "' into Title Name field.");
		return this;
	}

}
