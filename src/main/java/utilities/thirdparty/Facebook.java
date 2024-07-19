package utilities.thirdparty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.commons.UICommonAction;

public class Facebook {

	final static Logger logger = LogManager.getLogger(Facebook.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public Facebook(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	//Will move these locators to a separate file later
	By loc_txtUsername = By.id("email");
	By loc_txtPassword = By.id("pass");
	By loc_btnLogin = By.cssSelector("input[name='login']");
	By loc_btnReconnect = By.cssSelector("#platformDialogForm > div > div > div > div > div > div._6-wr > div > div._afns > div > div._afox > div._afoy > div:nth-child(2) > div > div");

	public Facebook inputUsername(String username) {
		commonAction.inputText(loc_txtUsername, username);
		logger.info("Input '" + username + "' into Facebook Username field.");
		return this;
	}
	public Facebook inputPassword(String password) {
		commonAction.inputText(loc_txtPassword, password);
		logger.info("Input '" + password + "' into Facebook Password field.");
		return this;
	}
	public Facebook clickLoginBtn() {
		commonAction.click(loc_btnLogin);
		logger.info("Clicked on Facebook Login button.");
		return this;
	}
	public Facebook clickReconnectBtn() {
		commonAction.click(loc_btnReconnect);
		logger.info("Clicked on 'Reconnect' button.");
		return this;
	}
	public Facebook performLogin(String username, String password) {
		inputUsername(username);
		inputPassword(password);
		clickLoginBtn();
		clickReconnectBtn();
		return this;
	}

}
