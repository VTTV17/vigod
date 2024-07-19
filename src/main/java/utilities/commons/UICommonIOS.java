package utilities.commons;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class UICommonIOS {
    private final static Logger logger = LogManager.getLogger();
    WebDriver driver;
    WebDriverWait wait;

    public UICommonIOS(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void allowPermission(String optionText) {
        HashMap<String, Object> args = new HashMap<>();
        args.put("action", "accept");
        args.put("buttonLabel", optionText);
        ((IOSDriver) driver).executeScript("mobile: alert", args);
    }

    void hidKeyboard() {
        // Check if keyboard shows, hid this
        By loc_btnDone = By.xpath("//XCUIElementTypeButton[@name=\"Done\"]");
        if (!driver.findElements(loc_btnDone).isEmpty()) {
            // Hid keyboard
            tap(loc_btnDone);
        }
    }

    public WebDriverWait customWait(int milSeconds) {
        return new WebDriverWait(driver, Duration.ofMillis(milSeconds));
    }

    public List<WebElement> getListElements(By locator, int... milSeconds) {
        int waitTime = (milSeconds.length == 0) ? 3000 : milSeconds[0];
        try {
            customWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException ignored) {
        }

        return driver.findElements(locator).isEmpty()
                ? List.of()
                : wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement getElement(String predicateString) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.iOSNsPredicateString(predicateString)));
    }

    public WebElement getElement(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (StaleElementReferenceException ex) {
            return driver.findElement(locator);
        }
    }

    public WebElement getElement(By locator, int index) {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)).get(index);
        } catch (StaleElementReferenceException ex) {
            return driver.findElements(locator).get(index);
        }
    }

    public void tapByCoordinates(int x, int y) {
        // Create new PointerInput objects for start and end positions
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        // Create a new sequence for the tap gesture and add actions to it
        Sequence tapPosition = new Sequence(finger, 1);
        tapPosition.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        ((IOSDriver) driver).perform(List.of(tapPosition));
    }

    public void tapByCoordinatesInPercent(double x, double y) {
        // Get the size of the device screen
        Dimension size = driver.manage().window().getSize();

        tapByCoordinates((int) (size.width * x), (int) (size.height * y));
    }

    void tapOnCenter(WebElement element) {
        // Get the center coordinates of the element
        int centerX = element.getLocation().getX() + element.getSize().getWidth() / 2;
        int centerY = element.getLocation().getY() + element.getSize().getHeight() / 2;

        // Perform tap action using PointerInput
        tapByCoordinates(centerX, centerY);
    }

    void tapOnRightTopCorner(WebElement element) {
        // Get the right top coordinates of the element
        int rightTopX = element.getLocation().getX() + element.getSize().getWidth();
        int rightTopY = element.getLocation().getY();

        // Perform tap action using PointerInput
        tapByCoordinates(rightTopX, rightTopY);
    }

    public void tap(By locator) {
        tapOnCenter(getElement(locator));
    }

    public void tap(By locator, int index) {
        tapOnCenter(getElement(locator, index));
    }

    public void tapOnRightTopCorner(By locator) {
        tapOnRightTopCorner(getElement(locator));
    }

    public void tapOnRightTopCorner(By locator, int index) {
        tapOnRightTopCorner(getElement(locator, index));
    }

    public void click(By locator) {
        getElement(locator).click();
    }

    public void click(By locator, int index) {
        getElement(locator, index).click();
    }

    public void sendKeys(By locator, CharSequence content) {
        getElement(locator).clear();
        getElement(locator).sendKeys(content);
        hidKeyboard();
    }

    public void sendKeys(By locator, int index, CharSequence content) {
        getElement(locator, index).clear();
        getElement(locator, index).sendKeys(content);
        hidKeyboard();
    }

    public String getText(By locator) {
        return getElement(locator).getText();
    }

    public String getText(By locator, int index) {
        return getElement(locator, index).getText();
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

    public boolean isChecked(WebElement element) {
        if (element.getAttribute("type").equals("XCUIElementTypeOther")) {
            element.click();
            return !element.findElements(By.xpath("//XCUIElementTypeImage[@name=\"icon_checked_white\"]")).isEmpty();
        }
        return element.getAttribute("value").equals("1");
    }

}
