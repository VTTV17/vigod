package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UICommonAction {

	final static Logger logger = LogManager.getLogger(UICommonAction.class);

	WebDriver driver;
	WebDriverWait wait;

	public UICommonAction(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(60));
	}


	public void clickElement(List<WebElement> element, int index) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element.get(index))).click();
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in clickElement \n" + ex);
			element = refreshElement(element.get(index));
			wait.until(ExpectedConditions.elementToBeClickable(element.get(index))).click();
		} catch (ElementNotInteractableException ex) {
			logger.debug("ElementNotInteractableException caught in clickElement \n" + ex);
			wait.until(ExpectedConditions.elementToBeClickable(element.get(index))).click();
		}
	}

	public void inputText(List<WebElement> element, int index, String text) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element.get(index))).clear();
			doubleClickElement(element.get(index));
			element.get(index).sendKeys(text);
		} catch (StaleElementReferenceException | TimeoutException ex) {
			if (ex instanceof StaleElementReferenceException) {
				logger.debug("StaleElementReferenceException caught in inputText");
			} else {
				logger.debug("TimeoutException caught in inputText");
			}
			element = refreshElement(element.get(index));
			wait.until(ExpectedConditions.elementToBeClickable(element.get(index))).clear();
			doubleClickElement(element.get(index));
			element.get(index).sendKeys(text);
		}
	}


	public String getText(List<WebElement> element, int index) {
		String text;
		try {
			text = wait.until(ExpectedConditions.visibilityOf(element.get(index))).getText();
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getText");
			element = refreshElement(element.get(index));
			text = wait.until(ExpectedConditions.visibilityOf(element.get(index))).getText();
		}
		logger.info("Text get: " + text);
		return text;
	}


	public void clickElement(WebElement element) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in clickElement \n" + ex);
			List<WebElement> listOfElements = refreshElement(element);
			if (listOfElements.size() == 1) element = listOfElements.get(0);
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		} catch (ElementNotInteractableException ex) {
			logger.debug("ElementNotInteractableException caught in clickElement \n" + ex);
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		}
	}
	
	public void inputText(WebElement element, String text) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
			doubleClickElement(element);
			element.sendKeys(text);
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in inputText");
			List<WebElement> listOfElements = refreshElement(element);
			if (listOfElements.size() ==1) element = listOfElements.get(0);
			wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
			doubleClickElement(element);
			element.sendKeys(text);
		}
	}
	
	public String getText(WebElement element) {
		String text;
		try {
			text = wait.until(ExpectedConditions.visibilityOf(element)).getText();
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getText");
			List<WebElement> listOfElements = refreshElement(element);
			if (listOfElements.size() ==1) element = listOfElements.get(0);
			text = wait.until(ExpectedConditions.visibilityOf(element)).getText();
		}
		logger.info("Text get: " + text);
		return text;
	}	
	
    public List<WebElement> refreshElement(WebElement element) {
        String elementInfo = element.toString();
        elementInfo = elementInfo.substring(elementInfo.indexOf("->"));
        String extractedLocator = elementInfo.substring(elementInfo.indexOf(": "));
        extractedLocator = extractedLocator.substring(2, extractedLocator.length() - 1);
        logger.debug("Refreshing element " + elementInfo);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        List<WebElement> newElement = null;
        if (elementInfo.contains("-> link text:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText(extractedLocator)));
        } else if (elementInfo.contains("-> name:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(extractedLocator)));
        } else if (elementInfo.contains("-> id:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(extractedLocator)));
        } else if (elementInfo.contains("-> xpath:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(extractedLocator)));
        } else if (elementInfo.contains("-> class name:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(extractedLocator)));
        } else if (elementInfo.contains("-> css selector:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(extractedLocator)));
        } else if (elementInfo.contains("-> partial link text:")) {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.partialLinkText(extractedLocator)));
        } else {
        	newElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName(extractedLocator)));
        }
        return newElement;
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
	
	public void switchToFrameByIndex(int frameIndex) {
		driver.switchTo().frame(frameIndex);
		logger.info("Switched to frame indexed: "+ frameIndex);
	}
	
	public void switchToFrameByNameOrId(String nameID) {
		driver.switchTo().frame(nameID);
		logger.info("Switched to frame whose name/id is: "+nameID);
	}
	
	public void switchToFrameByElement(WebElement element) {
		driver.switchTo().frame(element);
		logger.info("Switched to frame whose name/id is: "+element);
	}

	public void sendKeyToElementByJS(WebElement element, String value){
		JavascriptExecutor jsExecutor =(JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].setAttribute('value', '" + value + "')", element);
	}
	public void clickElementByJS(WebElement element){
		JavascriptExecutor jsExecutor =(JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].click();", element);
	}
	public void waitForElementInvisible(WebElement element){
		try {
			wait.until(ExpectedConditions.invisibilityOf(element));
			// Some elements sometimes disappear and appear again after milliseconds.
			sleepInMiliSecond(200);
			wait.until(ExpectedConditions.invisibilityOf(element));
			logger.info("Element invisible");
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in waitForElementInvisible");
			wait.until(ExpectedConditions.invisibilityOf(element));
			sleepInMiliSecond(200);
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
	
	/**
	 * Wait till the element disappears in the specific duration
	 * @param element
	 * @param timeout in seconds
	 */
	public void waitForElementInvisible(WebElement element, int timeout){
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		try {
			wait.until(ExpectedConditions.invisibilityOf(element));
			// Some elements sometimes disappear and appear again after milliseconds.
			sleepInMiliSecond(200);
			wait.until(ExpectedConditions.invisibilityOf(element));
		} catch (StaleElementReferenceException ex) {
			logger.debug("Catch StaleElementReferenceException caught in waitForElementInvisible");
			wait.until(ExpectedConditions.invisibilityOf(element));
			sleepInMiliSecond(200);
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

	/**
	 * Waits till the element <b>appears once then disappears</b> in the specific duration
	 * @param element
	 * @param timeout in seconds
	 */
	public void waitTillElementDisappear(WebElement element, int timeout) {
		try {
			waitForElementVisible(element, timeout);
		} catch (TimeoutException|StaleElementReferenceException ex) {
			if (ex instanceof TimeoutException) {
				logger.debug("Timeout waiting for element to disappear: " + ex);
			}
			else {
				logger.debug("StaleElementReferenceException caught in waitTillElementDisappear: " + ex);
			}
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
		String url = driver.getCurrentUrl();
		logger.info("Current URL: " + url);
		return url;
	}

	public void hoverOverElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();     
	}	
	
	public boolean isElementVisiblyDisabled(WebElement element) {
		return element.getAttribute("class").contains("gs-atm--disable");
	}	
	
	public int waitTillSelectDropdownHasData(WebElement element) {
		List<WebElement> options;
		int optionCount =0;
		for (int i =0; i <30; i++) {
			options = getAllOptionInDropDown(element);
			optionCount = options.size();
			logger.debug("Number of dropdown options: " + optionCount);
			if (optionCount >0) {
				if (options.get(optionCount-1).getAttribute("value").length() >0) return optionCount;
			}
			sleepInMiliSecond(100);
		}
		return optionCount;
	}	
	
	public String selectByVisibleText(WebElement element, String visibleText) {
		waitTillSelectDropdownHasData(element);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		Select select = new Select(element);
		select.selectByVisibleText(visibleText);

		// Reduces time taken to get selected option by using javascript.
		String js = "var e=arguments[0], i=e.selectedIndex; return i < 0 ? null : e.options[i];";
		WebElement selectedOption;
		try {
			selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, element);
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in selectByVisibleText");
			selectedOption = (WebElement) ((JavascriptExecutor) driver).executeScript(js, element);

		}

		if (selectedOption == null)
			throw new NoSuchElementException("No options are selected");
		return selectedOption.getText();
	}
	public String selectByIndex(WebElement element, int index) {
		waitTillSelectDropdownHasData(element);
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
	public List<WebElement> getListElementByXpath(String xpath) {
		return driver.findElements(By.xpath(xpath));
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
			return element.isDisplayed();
		}catch (Exception e){
			logger.debug("Element not display: "+e.getMessage());
			return false;
		}
	}
	public void navigateBack(){
		driver.navigate().back();
	}
	public void waitElementVisible(WebElement element) {
		new WebDriverWait(driver, Duration.ofSeconds(60)).until((ExpectedCondition<Boolean>) driver -> {
			assert driver != null;
			return ((JavascriptExecutor) driver).executeScript("return arguments[0].offsetParent", element) != null;
		});
	}

	public void waitElementList(List<WebElement> elementList, int... listSize) {
		new WebDriverWait(driver, Duration.ofSeconds(60)).until((ExpectedCondition<Boolean>) driver -> {
			assert driver != null;
			return elementList.size() > ((listSize.length > 0) ? (listSize[0] - 1) : 0);
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
	public void scrollToTopPage() {
		JavascriptExecutor executor= (JavascriptExecutor)driver;
		executor.executeScript( "window.scrollTo(0,0)");
	}
	public void scrollToElement(WebElement element) {
		JavascriptExecutor executor= (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].scrollIntoView(true);", element);
	}
	public void refreshPage(){
		driver.navigate().refresh();
		logger.debug("Refreshed page.");
	}

	public String getPageTitle(){
		String title = driver.getTitle();
		logger.debug("Retrieved page title: " + title);
		return title;
	}
	public void doubleClickElement(WebElement el){
		Actions actionObj = new Actions(driver);
		actionObj.doubleClick(el).build().perform();
	}

	public List<WebElement> refreshListElement(By locator) {
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
		return driver.findElements(locator);
	}
	public String getCopiedText(WebElement buttonToCopyEl) throws IOException, UnsupportedFlavorException {
		clickElement(buttonToCopyEl);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		String actualCopedText = (String) clipboard.getData(DataFlavor.stringFlavor);
		return actualCopedText;
	}
	public List<WebElement> getElements(By by) {
		return driver.findElements(by);
	}
	public WebElement getElement(By by) {
		return driver.findElement(by);
	}

}
