package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.Mailnesia;
import pages.dashboard.products.all_products.conversion_unit.ConversionUnitPage;
import utilities.database.InitConnection;

import java.io.File;
import java.sql.SQLException;
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
		int count = 0;
		int maxTries = 3;
		while(true) {
			try {
				wait.until(ExpectedConditions.elementToBeClickable(element)).click();
				break;
			} catch (StaleElementReferenceException ex) {
				logger.debug("StaleElementReferenceException caught in clickElement \n" + ex);
				if (++count == maxTries) throw ex;
			} catch (ElementNotInteractableException ex) {
				logger.debug("ElementNotInteractableException caught in clickElement \n" + ex);
				if (++count == maxTries) throw ex;
			}
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
			}else return !elements.get(0).isDisplayed();
		}
	public String getElementAttribute(WebElement element,String attributeName){
		String value;
		try {
			value = element.getAttribute(attributeName);
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getElementAttribute");
			value = element.getAttribute(attributeName);
		}
		return value;
	}
	public String getCurrentURL(){
		return driver.getCurrentUrl();
	}

	public String selectByVisibleText(WebElement element, String visibleText) {
		sleepInMiliSecond(1000); //Delay 500ms so that API request has some more time to render data onto front end.
		wait.until(ExpectedConditions.elementToBeClickable(element));
		Select select = new Select(element);
		select.selectByVisibleText(visibleText);
		
		// Reduces time taken to get selected option by using javascript.
		String js = "var e=arguments[0], i=e.selectedIndex; return i < 0 ? null : e.options[i];";
		WebElement selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, element);
		if (selectedOption == null)
			throw new NoSuchElementException("No options are selected");
		return selectedOption.getText();
	}

	public String selectByIndex(WebElement element, int index) {
		wait.until(ExpectedConditions.elementToBeClickable(element));
		Select select = new Select(element);
		select.selectByIndex(index);
		
		// Reduces time taken to get selected option by using javascript.
		String js = "var e=arguments[0], i=e.selectedIndex; return i < 0 ? null : e.options[i];";
		WebElement selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, element);
		if (selectedOption == null)
			throw new NoSuchElementException("No options are selected");
		return selectedOption.getText();
	}

	// Useful to hide the facebook message bubble at dashboard login page
	public void hideElement(WebElement element) {
		String js = "arguments[0].style.display='none';";
		((JavascriptExecutor) driver).executeScript(js, element);
	}	
	public void navigateToURL(String url){
		driver.get(url);
	}
	public WebElement getElementByXpath(String xpath) {
		return driver.findElement(By.xpath(xpath));
	}

	public void sleepInMiliSecond(long miliSecond) {
		try {
			Thread.sleep(miliSecond);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	public Boolean isElementDisplay(WebElement element){
		try {
			if(element.isDisplayed()){
				return true;
			}else return false;
		}catch (Exception e){
			logger.debug("Element not display: "+e.getMessage());
			return false;
		}
	}
	public void navigateBack(){
		driver.navigate().back();
	}

	public void verifyPageLoaded(String pageLoadedTextVIE, String pageLoadedTextENG) {
		new WebDriverWait(driver, Duration.ofSeconds(30)).until((ExpectedCondition<Boolean>) driver -> {
			assert driver != null;
			return driver.getPageSource().contains(pageLoadedTextVIE) || driver.getPageSource().contains(pageLoadedTextENG);
		});
	}

	public void waitElementList(List<WebElement> elementList) {
		new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
			assert driver != null;
			return elementList.size() > 0;
		});
	}

	public String getDropDownSelectedValue(WebElement element){
		wait.until(ExpectedConditions.elementToBeClickable(element));
		Select select = new Select(element);
		return  getText(select.getFirstSelectedOption());
	}
	public void scrollBottomPage() {
		JavascriptExecutor executor= (JavascriptExecutor)driver;
		executor.executeScript( "window.scrollBy(0,document.body.scrollHeight)");
	}

	public void refreshPage(){
		driver.navigate().refresh();
		logger.debug("Refreshed page.");
	}
}
