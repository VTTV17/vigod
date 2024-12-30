package utilities.thirdparty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class Google {

	final static Logger logger = LogManager.getLogger(Google.class);

	WebDriver driver;

	UICommonAction commonAction;

	public Google(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	//Will move these locators to a separate file later
	By loc_txtUsername = By.id("identifierId");
	By loc_btnNext = By.id("identifierNext");
	By loc_txtPassword = By.cssSelector("[name='Passwd']");
	By loc_btnPasswordNext = By.id("passwordNext");

	public Google inputUsername(String username) {
		commonAction.inputText(loc_txtUsername, username);
		logger.info("Input '" + username + "' into Facebook Username field.");
		return this;
	}
	public Google clickNextBtn() {
		commonAction.click(loc_btnNext);
		logger.info("Clicked on Next button after inputing username.");
		return this;
	}
	public Google inputPassword(String password) {
		commonAction.inputText(loc_txtPassword, password);
		logger.info("Input '" + password + "' into Facebook Password field.");
		return this;
	}
	public Google clickPasswordNextBtn() {
		commonAction.click(loc_btnPasswordNext);
		logger.info("Clicked on Next button after inputing username.");
		return this;
	}	
	public Google performLogin(String username, String password) {
		inputUsername(username);
		clickNextBtn();
		inputPassword(password);
		clickPasswordNextBtn();
		return this;
	}

}
