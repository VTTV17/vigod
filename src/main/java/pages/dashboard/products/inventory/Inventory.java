package pages.dashboard.products.inventory;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Inventory {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(Inventory.class);

	public Inventory(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".inventory-list-page .gs-page-title button")
	WebElement INVENTORY_HISTORY_BTN;

	public Inventory clickInventoryHistory() {
		commons.clickElement(INVENTORY_HISTORY_BTN);
		logger.info("Clicked on 'Inventory History' button.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
}
