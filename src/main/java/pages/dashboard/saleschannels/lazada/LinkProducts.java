package pages.dashboard.saleschannels.lazada;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class LinkProducts {

	final static Logger logger = LogManager.getLogger(LinkProducts.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public LinkProducts(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

	@FindBy(xpath = "//div[@class='ml-auto']/preceding-sibling::*//input")
	WebElement SEARCH_BOX;    
    
	public LinkProducts inputSearchTerm(String searchTerm) {
		commonAction.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
    
}
