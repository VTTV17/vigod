package utilities;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		String text = getElement(bySelector,5).getText();
		return text;
	}	

	public WebElement getElement(By by) {
		return driver.findElement(by);
	}
	
	public WebElement getElement(By by, int inSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(inSeconds));
		return wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}
	
}
