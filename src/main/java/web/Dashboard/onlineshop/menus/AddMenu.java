package web.Dashboard.onlineshop.menus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class AddMenu {

	final static Logger logger = LogManager.getLogger(AddMenu.class);

	WebDriver driver;
	UICommonAction commonAction;

	public AddMenu(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtMenuName = By.id("name");

	public AddMenu inputMenuTitle(String menuTitle) {
		commonAction.sendKeys(loc_txtMenuName, menuTitle);
		logger.info("Input '" + menuTitle + "' into Menu Title field.");
		return this;
	}

}
