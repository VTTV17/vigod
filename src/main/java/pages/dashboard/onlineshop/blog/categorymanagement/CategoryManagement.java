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

public class CategoryManagement {

	final static Logger logger = LogManager.getLogger(CategoryManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CategoryManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".gss-content-header--undefined .gs-button__green")
	WebElement CREATE_CATEGORY_BTN;

    public CategoryManagement clickCreateCategory() {
    	commonAction.clickElement(CREATE_CATEGORY_BTN);
    	logger.info("Clicked on 'Create Article' button.");
    	return this;
    }   
}
