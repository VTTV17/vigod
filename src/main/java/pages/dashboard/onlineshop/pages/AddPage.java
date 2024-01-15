package pages.dashboard.onlineshop.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class AddPage {

	final static Logger logger = LogManager.getLogger(AddPage.class);

	WebDriver driver;
	UICommonAction commonAction;

	public AddPage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnPageTitle = By.id("title");

	public AddPage inputPageTitle(String pageTitle) {
		commonAction.sendKeys(loc_btnPageTitle, pageTitle);
		logger.info("Input '" + pageTitle + "' into Page Title field.");
		return this;
	}

}
