package pages.dashboard.onlineshop.menus;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class MenuManagement {

	final static Logger logger = LogManager.getLogger(MenuManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public MenuManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}
	
    @FindBy(css = ".gss-content-header--undefined .gs-button__green")
    WebElement ADD_MENU_BTN;	
    
    public MenuManagement clickAddMenu() {
    	commonAction.clickElement(ADD_MENU_BTN);
    	logger.info("Clicked on 'Add Menu' button.");
    	return this;
    }    	

    public void verifyPermissionToAddMenu(String permission) {
    	if (permission.contentEquals("A")) {
    		clickAddMenu();
    		new AddMenu(driver).inputMenuTitle("Test Permission");
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    
}
