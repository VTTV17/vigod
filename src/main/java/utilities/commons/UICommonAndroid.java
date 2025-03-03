package utilities.commons;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.StartsActivity;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
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
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

public class UICommonAndroid {

    private final static Logger logger = LogManager.getLogger(UICommonAndroid.class);
    public final static String androidUIAutomatorResourcesIdString = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\"))";
    public final static String androidUIAutomatorResourcesIdInstanceString = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(%d))";
    public final static String androidUIAutomatorTextString = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"%s\"))";
    public final static String androidUIAutomatorPartTextString = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().textContains(\"%s\"))";

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
            driver.findElement(androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollBackward().scrollToBeginning(1000)"));
            logger.info("Scroll to top of screen");
        } catch (NoSuchElementException ignored) {
        }
    }

    void closeNotificationScreen() {
        // Close notification screen
        if (driver.getPageSource().contains("Appium Settings")) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(AndroidKey.BACK));

            // Log
            logger.info("Close notification screen");
        }
    }

    public List<WebElement> getListElement(By locator) {
        try {
            // Close notification screen
            closeNotificationScreen();

            // Wait element present
            customWait(3000).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignore) {
        }

        // Close notification screen
        closeNotificationScreen();

        return driver.findElements(locator).isEmpty()
                ? List.of()
                : wait.until(presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement getElement(By locator) {
        // Logger
        logLocatorDetails(locator);

        try {
            // Close notification screen
            closeNotificationScreen();

            // Get and return element
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (StaleElementReferenceException | TimeoutException ex) {
            // Close notification screen
            closeNotificationScreen();

            // Find again
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        }
    }

    public void click(By locator) {
        getElement(locator).click();
    }

    public void sendKeys(By locator, CharSequence content) {
        WebElement element = getElement(locator);
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

    public boolean isEnabled(By locator) {
        return getElement(locator).isEnabled();
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
        ((AppiumDriver) driver).perform(List.of(swipeGesture));
    }

    public void waitUntilScreenLoaded(String screenActivity) {
        customWait(30000).until((ExpectedCondition<Boolean>) driver -> {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            assert androidDriver != null;
            return Objects.requireNonNull(androidDriver.currentActivity()).equals(screenActivity);
        });
    }

    public boolean isShown(By locator) {
        return !getListElement(locator).isEmpty();
    }

    public void relaunchApp(String appPackage) {
        ((InteractsWithApps) driver).terminateApp(appPackage);
        ((InteractsWithApps) driver).activateApp(appPackage);
    }

    public void navigateToScreenUsingScreenActivity(String appPackage, String appActivity) {
        // Navigate to screen by activity
        if (!((AndroidDriver) driver).currentActivity().equals(appActivity)) {
            Activity activity = new Activity(appPackage, appActivity);
            activity.setStopApp(false);
            ((StartsActivity) driver).startActivity(activity);
        }
    }

    public boolean isChecked(By locator) {
        WebElement element = getElement(locator);
        if (element.getAttribute("class").equals("android.widget.ImageView")) {
            // Get element screenshot then compare screenshot with checked sample image
            try {
                return new Screenshot().takeScreenShot(element).compareImages();
            } catch (ArrayIndexOutOfBoundsException ex) {
                // In case, element is not shown full, swipe down and try again
                swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.6);
                return new Screenshot().takeScreenShot(element).compareImages();
            }
        }
        return element.getAttribute("checked").equals("true");
    }

    public boolean isDisplayed(By locator) {
        return getElement(locator).isDisplayed();
    }

    public void pushFileToMobileDevices(String fileName) {
        // Specify the file to be uploaded
        File file = new File(new DataGenerator().getPathOfFileInResourcesRoot(fileName));

        // Push the file to the device
        try {
            ((AndroidDriver) driver).pushFile("/sdcard/Download/%s".formatted(fileName), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Log
        logger.info("Push file to mobile device, file name: {}", fileName);
    }

    public List<String> getListElementTextOnFirstScreen(By locator) {
        // Scroll to top of screen
        scrollToTopOfScreen();

        // Get all elements in this screen
        List<WebElement> elements = getListElement(locator);

        return elements.isEmpty() ? List.of() : new ArrayList<>(elements.stream().map(WebElement::getText).toList());
    }

    private void logLocatorDetails(By by) {
        // Get full locator string
        String fullLocator = by.toString();

        // Log the detailed search type and locator
        if (fullLocator.contains("By.chained")) {
            logger.trace("FindElement - Search type: By Chained, Locator: {}", fullLocator);
        } else {
            logger.trace("FindElement - Search type: {}, Locator: {}", fullLocator.substring(0, fullLocator.indexOf(":")), fullLocator.substring(fullLocator.indexOf(": ") + 2));
        }
    }
}
