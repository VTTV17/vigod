package web.Dashboard.settings.permission.create;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

public class CreatePermissionPage {

	final static Logger logger = LogManager.getLogger(CreatePermissionPage.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	CreatePermissionPageElement elements;

	public CreatePermissionPage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new CreatePermissionPageElement();
	}
	
	public CreatePermissionPage inputGroupName(String name) {
		commonAction.inputText(elements.loc_txtName, name);
		logger.info("Input Permission Group name: " + name);
		return this;
	}	
	
	public CreatePermissionPage inputDescription(String description) {
		commonAction.inputText(elements.loc_txtDescription, description);
		logger.info("Input Description: " + description);
		return this;
	}	
	
	public CreatePermissionPage clickFullPermissionCheckbox() {
		commonAction.click(elements.loc_chkFullPermission);
		logger.info("Clicked 'Full Permission' checkbox.");
		return this;
	}	
	
	public CreatePermissionPage clickSaveBtn() {
		commonAction.click(elements.loc_btnSave);
		logger.info("Clicked 'Save' button.");
		return this;
	}	
    
}
