package pages.dashboard.gochat;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonAction;

public class Zalo {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(Zalo.class);

	public Zalo(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".btn-connect")
	WebElement CONNECT_ZALO_BTN;

	public Zalo clickConnectZalo() {
		commons.clickElement(CONNECT_ZALO_BTN);
		logger.info("Clicked on 'Connect Zalo' button.");
		return this;
	}
}
