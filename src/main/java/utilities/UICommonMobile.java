package utilities;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UICommonMobile extends UICommonAction {

	final static Logger logger = LogManager.getLogger(UICommonMobile.class);

	public UICommonMobile(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void clickElement(By bySelector) {
		getElement(bySelector,5).click();
	}
	
	public void inputText(By bySelector, String text) {
		WebElement el = getElement(bySelector,5);
		el.clear();
		getElement(bySelector).sendKeys(text);
	}
	
	public String getText(By bySelector) {
		return getElement(bySelector,5).getText();
	}	

	public WebElement getElement(By by) {
		return driver.findElement(by);
	}
	
	public WebElement getElement(By by, int inSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(inSeconds));
		return wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	public void waitSplashScreenLoaded() {
		String currentActivity = ((AndroidDriver) driver).currentActivity();
		new WebDriverWait(driver, Duration.ofSeconds(60)).until((ExpectedCondition<Boolean>) driver -> {
			AndroidDriver andDriver = (AndroidDriver) driver;
			assert andDriver != null;
			return !andDriver.currentActivity().equals(currentActivity);
		});
	}

	public void sendKeys(By locator, String text) {
		wait.until(ExpectedConditions.elementToBeClickable(locator));
		WebElement element = driver.findElement(locator);
		element.clear();
		element.sendKeys(text);
	}

	public void click(By locator) {
		wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
	}

	public void waitElementVisible(By locator) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public void waitListElementVisible(By locator) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
}
