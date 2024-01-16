package pages.dashboard.onlineshop.menus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class MenuManagement {

	final static Logger logger = LogManager.getLogger(MenuManagement.class);

	WebDriver driver;
	UICommonAction commonAction;

	public MenuManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}
	
    By loc_btnAddMenu = By.cssSelector(".gss-content-header--undefined .gs-button__green");	
    
    public MenuManagement clickAddMenu() {
    	commonAction.click(loc_btnAddMenu);
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
