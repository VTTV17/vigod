package pages.dashboard.onlineshop.blog.categorymanagement;

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

public class CreateCategory {

	final static Logger logger = LogManager.getLogger(CreateCategory.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreateCategory(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "title")
	WebElement CATEGORY;

	public CreateCategory inputCategoryName(String category) {
		commonAction.inputText(CATEGORY, category);
		logger.info("Input '" + category + "' into Category Name field.");
		return this;
	}

}
