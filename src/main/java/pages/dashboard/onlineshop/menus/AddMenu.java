package pages.dashboard.onlineshop.menus;

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

public class AddMenu {

	final static Logger logger = LogManager.getLogger(AddMenu.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public AddMenu(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "name")
	WebElement TITLE;

	public AddMenu inputMenuTitle(String menuTitle) {
		commonAction.inputText(TITLE, menuTitle);
		logger.info("Input '" + menuTitle + "' into Menu Title field.");
		return this;
	}

}
