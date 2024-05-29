package utilities.commons;

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
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Thread.sleep;
import static org.apache.commons.lang.StringUtils.trim;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class UICommonAction {

    final static Logger logger = LogManager.getLogger(UICommonAction.class);

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    public UICommonAction(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        actions = new Actions(driver);
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

    public void inputText(By locator, String content) {
        waitVisibilityOfElementLocated(locator);
        getElement(locator).clear();
        doubleClick(locator);
        try {
            getElement(locator).sendKeys(content);
        } catch (StaleElementReferenceException | InvalidElementStateException ex) {
            getElement(locator).sendKeys(content);
        }
    }

    public void inputText(By locator, int index, String content) {
        waitVisibilityOfElementLocated(locator, index);
        getElements(locator).get(index).clear();
        doubleClick(locator, index);
        try {
            getElements(locator).get(index).sendKeys(content);
        } catch (StaleElementReferenceException | InvalidElementStateException ex) {
            getElements(locator).get(index).sendKeys(content);
        }
    }

    public String getText(List<WebElement> element, int index) {
        String text;
        try {
            text = wait.until(visibilityOf(element.get(index))).getText();
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in getText");
            element = refreshElement(element.get(index));
            text = wait.until(visibilityOf(element.get(index))).getText();
        }
        logger.info("Text get: " + text);
        return text;
    }


    public void clickElement(WebElement element) {
        for (int i = 0; i < 5; i++) { //There are times when the element is still stale after re-locating it
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                break;
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
    }

    public void inputText(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
            doubleClickElement(element);
            element.sendKeys(text);
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in inputText");
            List<WebElement> listOfElements = refreshElement(element);
            if (listOfElements.size() == 1) element = listOfElements.get(0);
            wait.until(ExpectedConditions.elementToBeClickable(element)).clear();
            doubleClickElement(element);
            element.sendKeys(text);
        }
    }

    public String getText(WebElement element) {
        String text;
        try {
            text = wait.until(visibilityOf(element)).getText();
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in getText");
            List<WebElement> listOfElements = refreshElement(element);
            if (listOfElements.size() == 1) element = listOfElements.get(0);
            text = wait.until(visibilityOf(element)).getText();
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
        List<WebElement> newElement;
        if (elementInfo.contains("-> link text:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.linkText(extractedLocator)));
        } else if (elementInfo.contains("-> name:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.name(extractedLocator)));
        } else if (elementInfo.contains("-> id:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.id(extractedLocator)));
        } else if (elementInfo.contains("-> xpath:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.xpath(extractedLocator)));
        } else if (elementInfo.contains("-> class name:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.className(extractedLocator)));
        } else if (elementInfo.contains("-> css selector:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.cssSelector(extractedLocator)));
        } else if (elementInfo.contains("-> partial link text:")) {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.partialLinkText(extractedLocator)));
        } else {
            newElement = wait.until(presenceOfAllElementsLocatedBy(By.tagName(extractedLocator)));
        }
        return newElement;
    }

    public void uploadMultipleFile(WebElement element, String folder, String... fileNames) {
        String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "uploadfile" + File.separator + folder + File.separator;
        String fullName = "";
        for (String fileName : fileNames) {
            fullName = fullName + filePath + fileName + "\n";
        }
        sleepInMiliSecond(2000);
        element.sendKeys(fullName.trim());
        logger.info("File path: " + fullName);
    }

    public void checkTheCheckBoxOrRadio(WebElement element) {
        if (!element.isSelected()) {
            clickElement(element);
        }
    }

    public void checkTheCheckBoxOrRadio(WebElement elementValue, WebElement elementAction) {
        if (!elementValue.isSelected()) {
            clickElement(elementAction);
        }
    }

    public void uncheckTheCheckboxOrRadio(WebElement element) {
        if (element.isSelected()) {
            clickElement(element);
        }
    }

    public void uncheckTheCheckboxOrRadio(WebElement elementValue, WebElement elementAction) {
        if (elementValue.isSelected()) {
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
        String currentWindows = driver.getWindowHandle();
        logger.debug("The current windows handle is: '" + currentWindows + "'");
        return currentWindows;
    }

    public ArrayList<String> getAllWindowHandles() {
        ArrayList<String> availableWindows = new ArrayList<>(driver.getWindowHandles());
        logger.debug("All opening window(s): " + availableWindows);
        return availableWindows;
    }

    public void switchToWindow(int index) {
        driver.switchTo().window(getAllWindowHandles().get(index));
        logger.info("Switched to window/tab indexed: " + index);
    }

    public void switchToWindow(String handle) {
        driver.switchTo().window(handle);
        logger.info("Switched to window/tab whose handle is: " + handle);
    }

    public void switchToFrameByIndex(int frameIndex) {
        driver.switchTo().frame(frameIndex);
        logger.info("Switched to frame indexed: " + frameIndex);
    }

    public void switchToFrameByNameOrId(String nameID) {
        driver.switchTo().frame(nameID);
        logger.info("Switched to frame whose name/id is: " + nameID);
    }

    public void switchToFrameByElement(WebElement element) {
        driver.switchTo().frame(element);
        logger.info("Switched to frame whose name/id is: " + element);
    }

    public void sendKeyToElementByJS(WebElement element, String value) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].setAttribute('value', '" + value + "')", element);
    }

    public void clickElementByJS(WebElement element) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    public void waitForElementInvisible(WebElement element) {
        try {
            wait.until(invisibilityOf(element));
            // Some elements sometimes disappear and appear again after milliseconds.
            sleepInMiliSecond(200);
            wait.until(invisibilityOf(element));
            logger.info("Element invisible");
        } catch (StaleElementReferenceException ex) {
            logger.debug("Catch StaleElementReferenceException caught in waitForElementInvisible");
            wait.until(invisibilityOf(element));
            sleepInMiliSecond(200);
            wait.until(invisibilityOf(element));
        }
    }

    public void waitForElementVisible(WebElement element) {
        try {
            wait.until(visibilityOf(element));
        } catch (StaleElementReferenceException ex) {
            logger.debug("Catch StaleElementReferenceException caught in waitForElementVisible");
            wait.until(visibilityOf(element));
        }
    }

    /**
     * Wait till the element disappears in the specific duration
     *
     * @param element
     * @param timeout in seconds
     */
    public void waitForElementInvisible(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        try {
            wait.until(invisibilityOf(element));
            // Some elements sometimes disappear and appear again after milliseconds.
            sleepInMiliSecond(200);
            wait.until(invisibilityOf(element));
        } catch (StaleElementReferenceException ex) {
            logger.debug("Catch StaleElementReferenceException caught in waitForElementInvisible");
            wait.until(invisibilityOf(element));
            sleepInMiliSecond(200);
            wait.until(invisibilityOf(element));
        }
    }

    public void waitForElementVisible(WebElement element, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        try {
            wait.until(visibilityOf(element));
        } catch (StaleElementReferenceException ex) {
            logger.debug("Catch StaleElementReferenceException caught in waitForElementVisible");
            wait.until(visibilityOf(element));
        }
    }

    public List<WebElement> getAllOptionInDropDown(WebElement element) {
        Select select = new Select(element);
        return select.getOptions();
    }

    public List<WebElement> getAllOptionInDropDown(By locator, int index) {
        Select select = new Select(getElements(locator).get(index));
        return select.getOptions();
    }
    public List<WebElement> getAllOptionInDropDown(By locator) {
        Select select = new Select(getElement(locator));
        return select.getOptions();
    }

    /**
     * Waits till the element <b>appears once then disappears</b> in the specific duration
     *
     * @param element
     * @param timeout in seconds
     */
    public void waitTillElementDisappear(WebElement element, int timeout) {
        try {
            waitForElementVisible(element, timeout);
        } catch (TimeoutException | StaleElementReferenceException ex) {
            if (ex instanceof TimeoutException) {
                logger.debug("Timeout waiting for element to disappear: " + ex);
            } else {
                logger.debug("StaleElementReferenceException caught in waitTillElementDisappear: " + ex);
            }
        }
        waitForElementInvisible(element, timeout);
    }

    public boolean isElementNotDisplay(List<WebElement> elements) {
        if (elements.isEmpty()) {
            return true;
        } else return !elements.get(0).isDisplayed();
    }

    public boolean isElementNotDisplay(By locator) {
        List<WebElement> elements = getElements(locator);
        if (elements.isEmpty()) {
            return true;
        } else return !getElements(locator).get(0).isDisplayed();
    }
    public boolean isElementNotDisplay(By locator, int timeout) {
        List<WebElement> elements = getElements(locator,timeout);
        if (elements.isEmpty()) {
            return true;
        } else return !getElements(locator,timeout).get(0).isDisplayed();
    }
    public String getElementAttribute(WebElement element, String attributeName) {
        String value;
        try {
            value = element.getAttribute(attributeName);
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in getElementAttribute");
            value = element.getAttribute(attributeName);
        }
        return value;
    }

    public String getCurrentURL() {
        String url = driver.getCurrentUrl();
        logger.info("Current URL: " + url);
        return url;
    }

    public void hoverOverElement(WebElement element) {
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();
    }

    // This function will soon be deleted
    public boolean isElementVisiblyDisabled(WebElement element) {
        return element.getAttribute("class").contains("gs-atm--disable");
    }

    public boolean isElementVisiblyDisabled(By locator) {
        return getAttribute(locator, "class").contains("gs-atm--disable");
    }

    //This function will soon be deleted
    public int waitTillSelectDropdownHasData(WebElement element) {
        List<WebElement> options;
        int optionCount = 0;
        for (int i = 0; i < 30; i++) {
            options = getAllOptionInDropDown(element);
            optionCount = options.size();
            logger.debug("Number of dropdown options: " + optionCount);
            if (optionCount > 0 && !options.get(optionCount - 1).getAttribute("value").isEmpty()) return optionCount;
            sleepInMiliSecond(100, "Waiting for dropdown to have options");
        }
        return optionCount;
    }
    public int waitTillSelectDropdownHasData(By by) {
        List<WebElement> options;
        int optionCount = 0;
        for (int i = 0; i < 30; i++) {
            options = getAllOptionInDropDown(by);
            optionCount = options.size();
            logger.debug("Number of dropdown options: " + optionCount);
            if (optionCount > 0 && !options.get(optionCount - 1).getAttribute("value").isEmpty()) return optionCount;
            sleepInMiliSecond(100, "Waiting for dropdown to have options");
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

    public void navigateToURL(String url) {
        driver.get(url);
    }

    public WebElement getElementByXpath(String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    public List<WebElement> getListElementByXpath(String xpath) {
        return driver.findElements(By.xpath(xpath));
    }

    public void sleepInMiliSecond(long miliSecond, String... note) {
        try {
            sleep(miliSecond);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Sleep: " + miliSecond + "%s".formatted((note.length != 0) ? (", %s".formatted(note[0])) : ""));
    }

    public Boolean isElementDisplay(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not display: " + e.getMessage());
            return false;
        }
    }

    public Boolean isElementDisplay(By by) {
        try {
            return getElement(by).isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not display: " + e.getMessage());
            return false;
        }
    }

    public void navigateBack() {
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

    public String getDropDownSelectedValue(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        Select select = new Select(element);
        return getText(select.getFirstSelectedOption());
    }

    public String getDropDownSelectedValue(By locator, int index) {
        WebElement element = getElements(locator).get(index);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        Select select = new Select(element);
        return getText(select.getFirstSelectedOption());
    }

    public String getDropDownSelectedValue(By locator) {
        WebElement element = getElement(locator);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        Select select = new Select(element);
        return getText(select.getFirstSelectedOption());
    }

    public void scrollBottomPage() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollBy(0,document.body.scrollHeight)");
    }

    public void scrollToTopPage() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.scrollTo(0,0)");
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void refreshPage() {
        driver.navigate().refresh();
        logger.debug("Refreshed page.");
    }

    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Retrieved page title: " + title);
        return title;
    }

    public void doubleClickElement(WebElement el) {
        Actions actionObj = new Actions(driver);
        actionObj.doubleClick(el).build().perform();
    }

    public void doubleClick(By locator) {
        Actions actionObj = new Actions(driver);
        actionObj.doubleClick(getElement(locator)).build().perform();
    }

    public void doubleClick(By locator, int index) {
        Actions actionObj = new Actions(driver);
        actionObj.doubleClick(getElements(locator).get(index)).build().perform();
    }

    public String getCopiedText(By buttonToCopyLocator) throws IOException, UnsupportedFlavorException {
        click(buttonToCopyLocator);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }

    public List<WebElement> getElements(By by) {
        return driver.findElements(by);
    }
    public List<WebElement> getElements(By by, int secondTimeout) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(secondTimeout));
        List<WebElement> elements = driver.findElements(by);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        return elements;
    }
    public List<String> getListText(By by, int secondTimeout){
        List<WebElement> elements = getElements(by,secondTimeout);
        List<String> texts = new ArrayList<>();
        for (WebElement element:elements){
            texts.add(getText(elements,elements.indexOf(element)));
        }
        return texts;
    }
    public List<String> getListText(By by){
        List<WebElement> elements = getElements(by);
        List<String> texts = new ArrayList<>();
        for (WebElement element:elements){
            texts.add(getText(elements,elements.indexOf(element)));
        }
        return texts;
    }
    public WebElement getElement(By locator) {
        try {
            return wait.until(presenceOfElementLocated(locator));
        } catch (StaleElementReferenceException ex) {
            return wait.until(presenceOfElementLocated(locator));
        }
    }

    public WebElement getElement(By locator1, By locator2) {
        try {
            return wait.until(presenceOfNestedElementLocatedBy(getElement(locator1), locator2));
        } catch (StaleElementReferenceException ex) {
            return wait.until(presenceOfNestedElementLocatedBy(getElement(locator1), locator2));
        }
    }

    public WebElement getElement(By locator, int index) {
        try {
            return wait.until(presenceOfAllElementsLocatedBy(locator)).get(index);
        } catch (StaleElementReferenceException ex) {
            return wait.until(presenceOfAllElementsLocatedBy(locator)).get(index);
        }
    }

    public WebElement getElement(By locator1, int index1, By locator2) {
        try {
            return wait.until(presenceOfNestedElementLocatedBy(getElement(locator1, index1), locator2));
        } catch (StaleElementReferenceException ex) {
            return wait.until(presenceOfNestedElementLocatedBy(getElement(locator1, index1), locator2));
        }
    }

    /*
        common for POM modals
     */
    public List<WebElement> getListElement(By locator) {
        try {
            getWait(3000).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignore) {
        }
        return driver.findElements(locator).isEmpty()
                ? driver.findElements(locator)
                : wait.until(presenceOfAllElementsLocatedBy(locator));
    }

    public List<WebElement> getListElement(By locator, int waitTime) {
        try {
            getWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignore) {
        }
        return driver.findElements(locator).isEmpty()
                ? driver.findElements(locator)
                : wait.until(presenceOfAllElementsLocatedBy(locator));
    }

    public void click(By locator) {
        try {
            elementToBeClickable(locator).click();
        } catch (StaleElementReferenceException | ElementNotInteractableException ex) {
            hoverActions(locator);
            clickActions(locator);
        }
    }

    public void click(By locator, int index) {
        try {
            elementToBeClickable(locator, index).click();
        } catch (StaleElementReferenceException | ElementClickInterceptedException ex) {
            hoverActions(locator, index);
            clickActions(locator, index);
        }
    }

    public void clickJS(By locator) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", getElement(locator));
        } catch (StaleElementReferenceException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", getElement(locator));
        }
    }

    public void clickJS(By locator, int index) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", getElement(locator, index));
        } catch (StaleElementReferenceException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", getElement(locator, index));
        }
    }

    public void clickActions(By locator) {
        hoverActions(locator);
        actions.click().build().perform();
    }

    public void clickActions(By locator, int index) {
        hoverActions(locator, index);
        actions.click().build().perform();
    }

    public void hoverActions(By locator) {
        try {
            actions.moveToElement(getElement(locator)).build().perform();
            sleep(500);
        } catch (StaleElementReferenceException | InterruptedException | ElementClickInterceptedException ex) {
            actions.moveToElement(getElement(locator)).build().perform();
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void openTooltips(By locator, By tooltips) {
        // hover element
        actions.moveToElement(getElement(locator)).build().perform();

        // check tooltips shows or not
        if (getListElement(tooltips).isEmpty()) {
            openTooltips(locator, tooltips);
        }
    }

    public void hoverActions(By locator, int index) {
        try {
            actions.moveToElement(getElement(locator, index)).build().perform();
        } catch (StaleElementReferenceException | ElementClickInterceptedException ex) {
            actions.moveToElement(getElement(locator, index)).build().perform();
        }
    }

    void clickOutOfTextBox(By locator) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", getElement(locator));
        } catch (StaleElementReferenceException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", getElement(locator));
        }
    }

    void clickOutOfTextBox(By locator, int index) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", getElement(locator, index));
        } catch (StaleElementReferenceException ex) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", getElement(locator, index));
        }
    }

    public void sendKeys(By locator, CharSequence content) {
        waitVisibilityOfElementLocated(locator);
        clear(locator);
        click(locator);
        try {
            getElement(locator).sendKeys(content);
        } catch (StaleElementReferenceException | InvalidElementStateException ex) {
            getElement(locator).sendKeys(content);
        }

        clickOutOfTextBox(locator);
    }

    public void sendKeys(By locator, int index, CharSequence content) {
        waitVisibilityOfElementLocated(locator, index);
        clear(locator, index);
        click(locator, index);
        try {
            getElement(locator, index).sendKeys(content);
        } catch (StaleElementReferenceException ex) {
            getElement(locator, index).sendKeys(content);
        }

        clickOutOfTextBox(locator, index);
    }

    public void uploads(By locator, CharSequence content) {
        try {
            getElement(locator).sendKeys(content);
        } catch (StaleElementReferenceException ex) {
            getElement(locator).sendKeys(content);
        }
    }

    public void uploads(By locator, int index, CharSequence content) {
        try {
            getElement(locator, index).sendKeys(content);
        } catch (StaleElementReferenceException ex) {
            getElement(locator, index).sendKeys(content);
        }
    }

    public String getText(By locator) {
        try {
            return trim(getAttribute(locator, "innerText"));
        } catch (StaleElementReferenceException ex) {
            return trim(getAttribute(locator, "innerText"));
        }
    }

    public String getText(By locator, int index) {
        try {
            return trim(getAttribute(locator, index, "innerText"));
        } catch (StaleElementReferenceException ex) {
            return trim(getAttribute(locator, index, "innerText"));
        }
    }

    public String getValue(By locator) {
        try {
            return getAttribute(locator, "value");
        } catch (StaleElementReferenceException ignore) {
            return getAttribute(locator, "value");
        }
    }

    public String getValue(By locator, int index) {
        try {
            return getAttribute(locator, index, "value");
        } catch (StaleElementReferenceException ignore) {
            return getAttribute(locator, index, "value");
        }
    }

    public String getAttribute(By locator, int index, String attribute) {
        try {
            return getElement(locator, index).getAttribute(attribute);
        } catch (StaleElementReferenceException ignore) {
            return getElement(locator, index).getAttribute(attribute);
        }
    }

    public String getAttribute(By locator, String attribute) {
        try {
            return getElement(locator).getAttribute(attribute);
        } catch (StaleElementReferenceException ignore) {
            return getElement(locator).getAttribute(attribute);
        }
    }

    public void clearText(WebElement el) {
        el.sendKeys(Keys.SPACE, Keys.BACK_SPACE);
    }

    static CharSequence[] clearChars = IntStream.range(0, 100).mapToObj(index -> List.of(Keys.DELETE, Keys.BACK_SPACE)).flatMap(Collection::stream).toArray(CharSequence[]::new);

    public void clear(By locator) {
        try {
            getElement(locator).sendKeys(clearChars);
        } catch (StaleElementReferenceException | ElementNotInteractableException ex) {
            waitVisibilityOfElementLocated(locator);
            getElement(locator).sendKeys(clearChars);
        }
        if (!getElement(locator).getText().isEmpty() || getValue(locator) != null && !getValue(locator).isEmpty()) {
            clear(locator);
        }
    }

    void clear(By locator, int index) {
        try {
            getElement(locator, index).sendKeys(clearChars);
        } catch (StaleElementReferenceException | ElementNotInteractableException ex) {
            waitVisibilityOfElementLocated(locator, index);
            getElement(locator, index).sendKeys(clearChars);
        }
        try {
            if (!getElement(locator, index).getText().isEmpty() || (getValue(locator, index) != null && !getValue(locator, index).isEmpty())) {
                clear(locator, index);
            }
        } catch (StaleElementReferenceException ex) {
            if (!getElement(locator, index).getText().isEmpty() || (getValue(locator, index) != null && !getValue(locator, index).isEmpty())) {
                clear(locator, index);
            }
        }
    }

    public boolean isCheckedJS(By locator) {
        try {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", getElement(locator));
        } catch (StaleElementReferenceException ex) {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", getElement(locator));
        }
    }

    public boolean isCheckedJS(By locator, int index) {
        try {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", getElement(locator, index));
        } catch (StaleElementReferenceException ex) {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", getElement(locator, index));
        }
    }

    public boolean isDisabledJS(By locator) {
        try {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator));
        } catch (StaleElementReferenceException ex) {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator));
        }
    }

    public boolean isDisabledJS(By locator, int index) {
        try {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator, index));
        } catch (StaleElementReferenceException ex) {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator, index));
        }
    }

    public boolean isDisableJS(By locator) {
        try {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator));
        } catch (StaleElementReferenceException ex) {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator));
        }
    }

    public boolean isDisableJS(By locator, int index) {
        try {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator, index));
        } catch (StaleElementReferenceException ex) {
            return (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].disabled", getElement(locator, index));
        }
    }

    public void removeAttribute(By locator, int index, String attribute) {
        if (!getListElement(locator).isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('%s')".formatted(attribute),
                    getElement(locator, index));
        }
    }

    public void removeElement(By locator) {
        if (!getListElement(locator).isEmpty())
            ((JavascriptExecutor) driver).executeScript("arguments[0].remove()", getElement(locator));
    }

    public void removeFbBubble() {
        removeElement(By.cssSelector("#fb-root"));
    }

    public String getLangKey() {
        try {
            return ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('langKey')").toString();
        } catch (NullPointerException ex) {
            driver.navigate().refresh();
            return getLangKey();
        }
    }

    public void waitVisibilityOfElementLocated(By locator) {
        try {
            wait.until(visibilityOfElementLocated(locator));
        } catch (StaleElementReferenceException ex) {
            wait.until(visibilityOfElementLocated(locator));
        }
    }

    public void waitVisibilityOfElementLocated(By locator, int index) {
        try {
            wait.until(visibilityOf(getElement(locator, index)));
        } catch (StaleElementReferenceException ex) {
            wait.until(visibilityOf(getElement(locator, index)));
        }
    }

    public void waitInvisibilityOfElementLocated(By locator) {
        try {
            wait.until(invisibilityOfElementLocated(locator));
        } catch (StaleElementReferenceException ex) {
            wait.until(invisibilityOfElementLocated(locator));
        }
    }

    public WebElement elementToBeClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (StaleElementReferenceException ex) {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        }
    }

    public WebElement elementToBeClickable(By locator, int index) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(getElement(locator, index)));
        } catch (StaleElementReferenceException ex) {
            return wait.until(ExpectedConditions.elementToBeClickable(getElement(locator, index)));
        }
    }

    public void waitURLShouldBeContains(String path, int... miliSeconds) {
        // wait page is loaded
        ((miliSeconds.length == 0) ? wait : getWait(miliSeconds[0])).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getCurrentUrl().contains(path);
        });
    }

    /* Click and wait popup closed.*/
    public void closePopup(By locator) {
        try {
            if (!getListElement(locator).isEmpty())
                clickJS(locator);
        } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ignore) {
        }

        try {
            getWait(3000).until(numberOfElementsToBeLessThan(locator, 1));
        } catch (TimeoutException ex) {
            closePopup(locator);
        }
    }

    public void closePopup(By locator, By popup) {
        try {
            if (!getListElement(locator).isEmpty())
                clickJS(locator);
        } catch (StaleElementReferenceException | NoSuchElementException | TimeoutException ignore) {
        }

        try {
            getWait(3000).until(numberOfElementsToBeLessThan(popup, 1));
        } catch (TimeoutException ex) {
            closePopup(locator);
        }
    }

    /* Click and wait popup opened.*/
    public void openPopupJS(By locator, By popup) {
        try {
            if (!getListElement(locator).isEmpty())
                clickJS(locator);
        } catch (StaleElementReferenceException | NoSuchElementException ignore) {
        }

        if (getListElement(popup).isEmpty()) {
            openPopupJS(locator, popup);
        }
    }

    public void openPopupJS(By locator, int index, By popup) {
        try {
            if (!getListElement(locator).isEmpty())
                clickJS(locator, index);
        } catch (StaleElementReferenceException | NoSuchElementException ignore) {
        }
        if (getListElement(popup).isEmpty()) {
            openPopupJS(locator, index, popup);
        }
    }

    public void openDropdownJS(By locator, By dropdown) {
        try {
            clickJS(locator);
        } catch (StaleElementReferenceException | NoSuchElementException ignore) {
        }

        if (getListElement(dropdown).isEmpty()) {
            openDropdownJS(locator, dropdown);
        }
    }

    public void openDropdownJS(By locator, int index, By dropdown) {
        try {
            clickJS(locator, index);
        } catch (StaleElementReferenceException | NoSuchElementException ignore) {
        }
        if (getListElement(dropdown).isEmpty()) {
            openDropdownJS(locator, index, dropdown);
        }
    }

    public void closeDropdown(By locator, By dropdown) {
        try {
            clickJS(locator);
        } catch (StaleElementReferenceException | NoSuchElementException ignore) {
        }
        if (!getListElement(dropdown).isEmpty()) {
            closeDropdown(locator, dropdown);
        }
    }

    public void checkTheCheckBoxOrRadio(By locator) {
        if (!getElement(locator).isSelected()) {
            click(locator);
        }
    }

    public void checkTheCheckBoxOrRadio(By locator, int index) {
        if (!getElements(locator).get(index).isSelected()) {
            click(locator);
        }
    }

    public void checkTheCheckBoxOrRadio(By locValue, By locAction) {
        if (!getElement(locValue).isSelected()) {
            click(locAction);
        }
    }

    public void uncheckTheCheckboxOrRadio(By locator) {
        if (getElement(locator).isSelected()) {
            click(locator);
        }
    }

    public void uncheckTheCheckboxOrRadio(By locValue, By locAction) {
        if (getElement(locValue).isSelected()) {
            click(locAction);
        }
    }
    public void uncheckTheCheckboxOrRadio(By locValue, By locAction, int index) {
        if (getElement(locValue,index).isSelected()) {
            click(locAction,index);
        }
    }
    public void selectByVisibleText(By locator, String visibleText) {
        WebElement element = getElement(locator);
        waitTillSelectDropdownHasData(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
    }

    public WebDriverWait getWait(int miliSeconds) {
        return new WebDriverWait(driver, Duration.ofMillis(miliSeconds));
    }

    public void selectByIndex(By locator, int index) {
        WebElement element = getElement(locator);
        waitTillSelectDropdownHasData(element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    String getSelectedValue(By ddvSelectedLocator) {
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value", getElement(ddvSelectedLocator));
    }

    public void selectDropdownOptionByValue(By ddvSelectedLocator, String value) {
        // select option
        try {
            new Select(getElement(ddvSelectedLocator)).selectByValue(value);
        } catch (NoSuchElementException | StaleElementReferenceException ex) {
            logger.info(ex);
            selectDropdownOptionByValue(ddvSelectedLocator, value);
        }

        // check option is selected or not
        try {
            if (!getSelectedValue(ddvSelectedLocator).equals(value)) {
                selectDropdownOptionByValue(ddvSelectedLocator, value);
            }
        } catch (StaleElementReferenceException ex) {
            logger.info(ex);
            if (!getSelectedValue(ddvSelectedLocator).equals(value)) {
                selectDropdownOptionByValue(ddvSelectedLocator, value);
            }
        }
    }
    public void selectDropdownOptionByValue(By ddvSelectedLocator,int index, String value) {
        try {
            new Select(getElement(ddvSelectedLocator,index)).selectByValue(value);
        } catch (NoSuchElementException | StaleElementReferenceException ex) {
            logger.info(ex);
            selectDropdownOptionByValue(ddvSelectedLocator,index, value);
        }
    }
}
