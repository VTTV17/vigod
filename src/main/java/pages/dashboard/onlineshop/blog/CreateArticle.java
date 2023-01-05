package pages.dashboard.onlineshop.blog;

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

public class CreateArticle {

	final static Logger logger = LogManager.getLogger(CreateArticle.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreateArticle(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "title")
	WebElement TITLE;

	public CreateArticle inputTitleName(String articleTitle) {
		commonAction.inputText(TITLE, articleTitle);
		logger.info("Input '" + articleTitle + "' into Title Name field.");
		return this;
	}

}
