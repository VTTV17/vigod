package utilities.commons;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.StartsActivity;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.data.DataGenerator;
import utilities.screenshot.Screenshot;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

public class UICommonAndroid {

    private final static Logger logger = LogManager.getLogger(UICommonAndroid.class);
    WebDriver driver;
    WebDriverWait wait;

    public UICommonAndroid(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public WebDriverWait customWait(int milSeconds) {
        return new WebDriverWait(driver, Duration.ofMillis(milSeconds));
    }

    public void scrollToTopOfScreen() {
        try {
            driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollBackward().scrollToBeginning(1000)"));
            logger.info("Scroll to top of screen");
        } catch (NoSuchElementException ignored) {
        }
    }

    public void scrollUp() {
        try {
            driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollBackward()"));
            logger.info("Scroll up");
        } catch (NoSuchElementException ignored) {
        }
    }

    public void scrollToEndOfScreen() {
        try {
            driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollForward().scrollToEnd(1000)"));
            logger.info("Scroll to end of screen");
        } catch (NoSuchElementException ignored) {
        }
    }

    public void scrollDown() {
        try {
            driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollForward()"));
            logger.info("Scroll down");
        } catch (NoSuchElementException ignored) {
        }
    }

    public List<WebElement> getListElement(By locator) {
        try {
            customWait(3000).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignore) {
        }
        return driver.findElements(locator).isEmpty()
                ? List.of()
                : wait.until(presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement getElement(String resourceId) {
        By locator = AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\"))".formatted(resourceId));
        try {
            // In case, element is present, must not scroll more.
            return driver.findElement(By.id(resourceId));
        } catch (NoSuchElementException ex) {
            // Can scroll into element
            return driver.findElement(locator);
        }
    }

    public WebElement getElement(By locator) {
        // if element is not presented, scroll more to get element.
        if (getListElement(locator).isEmpty()) scrollDown();
        return driver.findElement(locator);
    }

    public WebElement getElement(By locator, int index) {
        // get all elements in this screen
        List<WebElement> elements = new ArrayList<>(getListElement(locator));

        // init current number of elements
        int currentSize = elements.size();

        // find list elements
        while (currentSize <= index) {
            // init temp arr
            List<WebElement> tempArr = new ArrayList<>(elements);

            // scroll more to get new element
            scrollDown();

            // add new element to list
            tempArr.addAll(driver.findElements(locator));

            // remove duplicate element
            elements = tempArr.stream().distinct().toList();

            // check has new element or not
            if (elements.size() == currentSize) break;

            // get current number of elements
            currentSize = elements.size();
        }

        // return element
        return elements.get(index);
    }

    public WebElement getElement(String parentResourceId, By locator) {
        // move into parent element
        getElement(parentResourceId);

        // if element is not presented, scroll more to get element.
        if (driver.findElements(locator).isEmpty()) scrollDown();
        return driver.findElement(locator);
    }

    public WebElement getElement(String parentResourceId, By locator, int index) {
        // move into parent element
        getElement(parentResourceId);

        // get all elements in this screen
        List<WebElement> elements = new ArrayList<>(driver.findElements(locator));

        // init current number of elements
        int currentSize = elements.size();

        // find list elements
        while (currentSize <= index) {
            // init temp arr
            List<WebElement> tempArr = new ArrayList<>(elements);

            // scroll more to get new element
            scrollDown();

            // add new element to list
            tempArr.addAll(driver.findElements(locator));

            // remove duplicate element
            elements = tempArr.stream().distinct().toList();

            // check has new element or not
            if (elements.size() == currentSize) break;

            // get current number of elements
            currentSize = elements.size();
        }

        // return element
        return elements.get(index);
    }

    public void click(By locator) {
        getElement(locator).click();
    }

    public void click(String resourceId) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(getElement(resourceId))).click();
        } catch (StaleElementReferenceException ex) {
            getElement(resourceId).click();
        }
    }

    public void click(String parentResourceId, By locator) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(getElement(parentResourceId, locator))).click();
        } catch (StaleElementReferenceException ex) {
            getElement(parentResourceId, locator).click();
        }
    }

    public void click(By locator, int index) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(getElement(locator, index))).click();
        } catch (StaleElementReferenceException ex) {
            getElement(locator, index).click();
        }
    }

    public void click(String parentResourceId, By locator, int index) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(getElement(parentResourceId, locator, index))).click();
        } catch (StaleElementReferenceException ex) {
            getElement(parentResourceId, locator, index).click();
        }
    }

    public void sendKeys(By locator, CharSequence content) {
        WebElement element = getElement(locator);
        element.clear();
        element.sendKeys(content);
    }

    public void sendKeys(String resourceId, CharSequence content) {
        getElement(resourceId).clear();
        getElement(resourceId).sendKeys(content);
    }

    public void sendKeys(String parentResourceId, By locator, CharSequence content) {
        WebElement element = getElement(parentResourceId, locator);
        element.clear();
        element.sendKeys(content);
    }

    public void sendKeys(By locator, int index, CharSequence content) {
        WebElement element = getElement(locator, index);
        element.clear();
        element.sendKeys(content);
    }

    public void sendKeys(String parentResourceId, By locator, int index, CharSequence content) {
        WebElement element = getElement(parentResourceId, locator, index);
        element.clear();
        element.sendKeys(content);
    }

    public void sendKeysActions(WebElement element, CharSequence content) {
        element.clear();
        element.click();
        new Actions(driver).sendKeys(content).build().perform();
    }


    public String getText(By locator) {
        return getElement(locator).getText();
    }

    public String getText(By locator, int index) {
        return getElement(locator, index).getText();
    }

    public boolean isEnabled(By locator) {
        return getElement(locator).isEnabled();
    }

    public boolean isEnabled(String resourceId) {
        return getElement(resourceId).isEnabled();
    }

    public boolean isEnabled(By locator, int index) {
        return getElement(locator, index).isEnabled();
    }


    public void hidKeyboard() {
        ((AndroidDriver) driver).hideKeyboard();
        logger.debug("Hid keyboard");
    }

    public void tapByCoordinates(int x, int y) {
        // Create new PointerInput objects for start and end positions
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        // Create a new sequence for the tap gesture and add actions to it
        Sequence tapPosition = new Sequence(finger, 1);
        tapPosition.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((AndroidDriver) driver).perform(List.of(tapPosition));
    }

    public void tapByCoordinatesInPercent(double x, double y) {
        // Get the size of the device screen
        Dimension size = driver.manage().window().getSize();

        tapByCoordinates((int) (size.width * x), (int) (size.height * y));
    }

    public void swipeByCoordinatesInPercent(double startX, double startY, double endX, double endY) {
        swipeByCoordinatesInPercent(startX, startY, endX, endY, 200);
    }

    public void swipeByCoordinatesInPercent(double startX, double startY, double endX, double endY, int delay) {
        // Get the size of the device screen
        Dimension size = driver.manage().window().getSize();

        // Set start and end coordinates for the swipe
        int startXCoordinate = (int) (size.width * startX);
        int startYCoordinate = (int) (size.height * startY);
        int endXCoordinate = (int) (size.width * endX);
        int endYCoordinate = (int) (size.height * endY);

        // Create new PointerInput objects for start and end positions
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        // Create a new sequence for the swipe gesture and add actions to it
        Sequence swipeGesture = new Sequence(finger, 0);
        swipeGesture.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startXCoordinate, startYCoordinate));
        swipeGesture.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeGesture.addAction(new Pause(finger, Duration.ofMillis(10)));
        swipeGesture.addAction(finger.createPointerMove(Duration.ofMillis(delay), PointerInput.Origin.viewport(), endXCoordinate, endYCoordinate));
        swipeGesture.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Execute the swipe gesture on the device
        String platformNameFromCapacity = ((AppiumDriver) driver).getCapabilities().getCapability("platformName").toString();
        ((AppiumDriver) driver).perform(List.of(swipeGesture));
    }

    public void waitSplashScreenLoaded() {
        wait.until((ExpectedCondition<Boolean>) driver -> {
            AndroidDriver andDriver = (AndroidDriver) driver;
            assert andDriver != null;
            return Objects.requireNonNull(andDriver.currentActivity()).contains("MainActivity") || Objects.requireNonNull(andDriver.currentActivity()).contains("Login");
        });
    }

    public void waitUntilScreenLoaded(String screenActivity) {
        customWait(30000).until((ExpectedCondition<Boolean>) driver -> {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            assert androidDriver != null;
            return Objects.requireNonNull(androidDriver.currentActivity()).equals(screenActivity);
        });
    }

    public String getCurrentActivity() {
        return ((AndroidDriver) driver).currentActivity();
    }

    public boolean isShown(String resourceId) {
        return !getListElement(By.id(resourceId)).isEmpty();
    }

    public boolean isShown(By locator) {
        return !getListElement(locator).isEmpty();
    }

    public boolean isShown(String parentResourceId, By locator) {
        getElement(parentResourceId);
        return !getListElement(locator).isEmpty();
    }

    public WebElement getElementByText(String elText) {
        String xpath = "//*[@text = '%s']".formatted(elText);
        return getElement(By.xpath(xpath));
    }

    public WebElement moveAndGetOverlappedElementByText(String elText, By overlapLocator) {
        WebElement element = getElementByText(elText);
        Rectangle overlapRect = driver.findElement(overlapLocator).getRect();
        Rectangle elRect = element.getRect();
        if ((elRect.getY() + elRect.getHeight()) >= overlapRect.getY()) {
            swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);
        }
        return driver.findElement(By.xpath("//*[@text = '%s']".formatted(elText)));
    }

    public void waitPageLoaded() {
        String currentPageSource;
        String nextPageSource;
        do {
            currentPageSource = driver.getPageSource();

            scrollDown();

            nextPageSource = driver.getPageSource();
        } while (currentPageSource.equals(nextPageSource));
    }

    public double getElementLocationYPercent(By locator) {
        int y = getElement(locator).getLocation().getY();
        Dimension size = driver.manage().window().getSize();
        return (double) y / size.height;
    }

    public void swipeHorizontalInPercent(By locator, double startX, double endX) {
        double y = getElementLocationYPercent(locator);
        swipeByCoordinatesInPercent(startX, y, endX, y);
    }

    public void relaunchApp(String appPackage, String appActivity) {
        Activity activity = new Activity(appPackage, appActivity);
        activity.setStopApp(false);
        ((StartsActivity) driver).startActivity(activity);
    }

    public void navigateToScreenUsingScreenActivity(String appPackage, String activity) {
        // Navigate to screen by activity
        if (!((AndroidDriver) driver).currentActivity().equals(activity)) {
            relaunchApp(appPackage, activity);
        }
    }

    public boolean isChecked(WebElement element) {
        if (element.getAttribute("class").equals("android.widget.ImageView")) {
            // Get element screenshot then compare screenshot with checked sample image
            try {
                return new Screenshot().takeScreenShot(driver, element).compareImages();
            } catch (ArrayIndexOutOfBoundsException ex) {
                // In case, element is not shown full, swipe down and try again
                swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.6);
                return new Screenshot().takeScreenShot(driver, element).compareImages();
            }
        }
        return element.getAttribute("checked").equals("true");
    }

    public boolean isDisplayed(By locator) {
        return getElement(locator).isDisplayed();
    }

    public void waitPageLoaded(By locator) {
        // wait home page loaded
        boolean isLoaded;
        do {
            isLoaded = driver.getPageSource().contains(locator.toString().replaceAll("B.*?'|'.+", ""));
        } while (!isLoaded);
    }

    @SneakyThrows
    public void pushFileToMobileDevices(String fileName) {
        // Specify the file to be uploaded
        File file = new File(new DataGenerator().getFilePath(fileName));

        // Convert the file to a byte array
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Push the file to the device
        ((AndroidDriver) driver).pushFile("/sdcard/Download/%s".formatted(fileName), file);

        // Log
        logger.info("Push file to mobile device, file name: {}", fileName);
    }

    public void waitInvisible(String resourceId) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(resourceId)));
    }

    public void navigateToScreenUsingWebElement(String resourceId, String screenActivity) {
        click(resourceId);
        if (((AndroidDriver) driver).currentActivity().equals(screenActivity)) {
            navigateToScreenUsingWebElement(resourceId, screenActivity);
        }
    }

    public void navigateToScreenUsingWebElement(String parentResourceId, By locator, String screenActivity) {
        click(parentResourceId, locator);
        if (((AndroidDriver) driver).currentActivity().equals(screenActivity)) {
            navigateToScreenUsingWebElement(parentResourceId, locator, screenActivity);
        }
    }

    public void navigateToScreenUsingWebElement(By locator, int index, String screenActivity) {
        click(locator, index);
        if (((AndroidDriver) driver).currentActivity().equals(screenActivity)) {
            navigateToScreenUsingWebElement(locator, index, screenActivity);
        }
    }

    public void navigateToScreenUsingWebElement(String parentResourceId, By locator, int index, String screenActivity) {
        click(parentResourceId, locator, index);
        if (((AndroidDriver) driver).currentActivity().equals(screenActivity)) {
            navigateToScreenUsingWebElement(parentResourceId, locator, index, screenActivity);
        }
    }

    public List<String> getListElementTextOnFirstScreen(By locator) {
        // Scroll to top of screen
        scrollToTopOfScreen();

        // Get all elements in this screen
        List<WebElement> elements = getListElement(locator);

        return elements.isEmpty() ? List.of() : new ArrayList<>(elements.stream().map(WebElement::getText).toList());
    }

    public List<String> getListElementTextOnLastScreen(By locator) {
        // Scroll to end of screen
        scrollToEndOfScreen();

        // Get all elements in this screen
        List<WebElement> elements = new ArrayList<>(getListElement(locator));

        return new ArrayList<>(elements.stream().map(WebElement::getText).toList());
    }

}
