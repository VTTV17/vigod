package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UICommonAction {
	
	final static Logger logger = LogManager.getLogger(UICommonAction.class);
	
	WebDriver driver;
	WebDriverWait wait;

	public UICommonAction(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void clickElement(WebElement element) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in clickElement");
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		}
	}
	
	public void inputText(WebElement element, String text) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
			element.sendKeys(text);
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in inputText");
			wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
			element.sendKeys(text);
		}
	}
	
	public String getText(WebElement element) {
		String text;
		try {
			text = wait.until(ExpectedConditions.visibilityOf(element)).getText();
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in getText");
			text = wait.until(ExpectedConditions.visibilityOf(element)).getText();
		}
		logger.info("Text get: " + text);
		return text;
	}	

	public void uploadMultipleFile(WebElement element,String folder, String...fileNames){
		String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator +"uploadfile"+ File.separator + folder + File.separator;
		String fullName = "";
		for (String fileName: fileNames) {
			fullName = fullName + filePath + fileName + "\n";
		}
		logger.info("File path: "+fullName);
		element.sendKeys(fullName.trim());
	}
	public void checkTheCheckBoxOrRadio (WebElement element){
		if (!element.isSelected()){
			clickElement(element);
		}
	}
	public void checkTheCheckBoxOrRadio (WebElement elementValue,WebElement elementAction){
		if (!elementValue.isSelected()){
			clickElement(elementAction);
		}
	}
	public void uncheckTheCheckboxOrRadio (WebElement element){
		if (element.isSelected()){
			clickElement(element);
		}
	}
	public void uncheckTheCheckboxOrRadio (WebElement elementValue,WebElement elementAction){
		if (elementValue.isSelected()){
			clickElement(elementAction);
		}
	}

	public void openNewTab() {
		((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		logger.info("Opened a new blank tab.");
	}
	
	public void closeTab() {
		((JavascriptExecutor) driver).executeScript("window.close();");
		logger.info("Closed tab.");
	}
	
    public String getCurrentWindowHandle() {
		String currentWindows =  driver.getWindowHandle();
		logger.debug("The current windows handle is: '"+currentWindows+"'");
		return currentWindows;
	}
    
	public ArrayList<String> getAllWindowHandles() {
		ArrayList<String> availableWindows =  new ArrayList<String>(driver.getWindowHandles());
		logger.debug("All opening window(s): "+availableWindows);
		return availableWindows;
	}
	
	public void switchToWindow(int index) {
		driver.switchTo().window(getAllWindowHandles().get(index));
		logger.info("Switched to window/tab indexed: "+index);
	}	
	
	public void switchToWindow(String handle) {
		driver.switchTo().window(handle);
		logger.info("Switched to window/tab whose handle is: "+handle);
	}

	public void sendKeyToElementByJS(WebElement element, String value){
		JavascriptExecutor jsExecutor =(JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].setAttribute('value', '" + value + "')", element);
	}
	public void waitForElementInvisible(WebElement element){
		try {
			wait.until(ExpectedConditions.invisibilityOf(element));
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in waitForElementInvisible");
			wait.until(ExpectedConditions.invisibilityOf(element));
		}
	}
	public void waitForElementVisible(WebElement element){
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in waitForElementVisible");
			wait.until(ExpectedConditions.visibilityOf(element));
		}
	}
	public void waitForElementInvisible(WebElement element, int timeout){
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		try {
			wait.until(ExpectedConditions.invisibilityOf(element));
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in waitForElementInvisible");
			wait.until(ExpectedConditions.invisibilityOf(element));
		}
	}
	public void waitForElementVisible(WebElement element, int timeout){
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in waitForElementVisible");
			wait.until(ExpectedConditions.visibilityOf(element));
		}
	}

	public List<WebElement> getAllOptionInDropDown(WebElement element){
		Select select = new Select(element);
		return select.getOptions();
	}

	public void waitTillElementDisappear(WebElement element, int timeout) {
		try {
			waitForElementVisible(element, timeout);
		} catch (TimeoutException ex) {
			logger.debug("Timeout waiting for element to disappear: " + ex);
		}
		waitForElementInvisible(element, timeout);
	}
	public boolean isElementNotDisplay(List<WebElement> elements){
		if(elements.size()==0) {
			return true;
		}else if(elements.size()>0 && !elements.get(0).isDisplayed()) {
			return true;
		}else {
			return false;
		}
	}
	public String getElementAttribute(WebElement element,String attributeName){
		return element.getAttribute(attributeName);
	}
	public String getCurrentURL(){
		return driver.getCurrentUrl();
	}

	public String selectByVisibleText(WebElement element, String visibleText) {
		wait.until(ExpectedConditions.elementToBeClickable(element));
		Select select = new Select(element);
		select.selectByVisibleText(visibleText);
		WebElement option = select.getFirstSelectedOption();
		return option.getText();
	}	

	// Useful to hide the facebook message bubble at dashboard login page
	public void hideElement(WebElement element) {
		String js = "arguments[0].style.display='none';";
		((JavascriptExecutor) driver).executeScript(js, element);
	}	
	
}
