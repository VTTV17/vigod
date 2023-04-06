package utilities;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class UICommonMobile extends UICommonAction {

	final static Logger logger = LogManager.getLogger(UICommonMobile.class);

	public UICommonMobile(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void clickElement(By bySelector) {
		try {
			getElement(bySelector,5).click();
		} catch(StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in clickElement\n" + ex);
			getElement(bySelector,5).click();
		}
	}
	
	public void inputText(By bySelector, String text) {
		try {
			WebElement el = getElement(bySelector,5);
			el.clear();
			el.sendKeys(text);
		} catch(StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in inputText\n" + ex);
			WebElement el = getElement(bySelector,5);
			el.clear();
			el.sendKeys(text);
		}
	}
	
	public String getText(By bySelector) {
		String text = "";
		try {
			text = getElement(bySelector,5).getText();
		} catch(StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getText\n" + ex);
			text = getElement(bySelector,5).getText();
		}
		return text;
	}	

	public WebElement getElement(By by) {
		return driver.findElement(by);
	}
	
	public WebElement getElement(By by, int inSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(inSeconds));
		return wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}
	
	/**
	 * This function hides the keyboard either on an Android or IOS device, depending on the platform specified.
	 * @param platform The platform of the device (either "android" or "ios")
	 * @throws WebDriverException If the WebDriver encounters an error while attempting to hide the keyboard
	 * @throws IllegalArgumentException If the platform specified is neither "android" nor "ios"
	 */
	public void hideKeyboard(String platform) {
		if (platform.equalsIgnoreCase("android")) {
			((AndroidDriver)driver).hideKeyboard();
		} else if(platform.equalsIgnoreCase("ios")) {
			((IOSDriver)driver).hideKeyboard();
		} else {
			throw new IllegalArgumentException("Unknown platform: " + platform);
		}
	}
	
}
