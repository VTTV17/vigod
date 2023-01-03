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

public class BlogManagement {

	final static Logger logger = LogManager.getLogger(BlogManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public BlogManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}
	
    @FindBy(css = ".gss-content-header--undefined .gs-button__green")
    WebElement CREATE_ARTICLE_BTN;	
    
    @FindBy(css = ".gss-content-header--undefined .gs-button__green--outline")
    WebElement CATEGORY_MANAGEMENT_BTN;	
    
    public BlogManagement clickCreateArticle() {
    	commonAction.clickElement(CREATE_ARTICLE_BTN);
    	logger.info("Clicked on 'Create Article' button.");
    	return this;
    }    	
    
    public BlogManagement clickCategoryManagement() {
    	commonAction.clickElement(CATEGORY_MANAGEMENT_BTN);
    	logger.info("Clicked on 'Category Management' button.");
    	return this;
    }    	

}
