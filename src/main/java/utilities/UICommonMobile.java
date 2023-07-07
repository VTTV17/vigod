package utilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.MouseButton;
import org.openqa.selenium.interactions.PointerInput.Origin;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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
	
	public void clickElement(By bySelector, int timeout) {
		try {
			getElement(bySelector,timeout).click();
		} catch(StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in clickElement\n" + ex);
			getElement(bySelector,timeout).click();
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
		String text;
		try {
			text = getElement(bySelector,5).getText();
		} catch(StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getText\n" + ex);
			text = getElement(bySelector,5).getText();
		}
		return text;
	}	
	
	public boolean isElementEnabled(By bySelector) {
		return getElement(bySelector, 5).isEnabled();
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

	/**
	 * This method performs a tap action on the specified position on the screen using the given x and y coordinates.
	 * @param x The x coordinate of the position to tap.
	 * @param y The y coordinate of the position to tap.
	 * @throws IllegalArgumentException if the platform is not recognized.
	 */
	public void tapByCoordinates(int x, int y) {
		// Create new PointerInput objects for start and end positions
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

		// Create a new sequence for the tap gesture and add actions to it
		Sequence tapPosition = new Sequence(finger,1);
		tapPosition.addAction(finger.createPointerMove(Duration.ZERO, Origin.viewport(), x, y))
				.addAction(finger.createPointerDown(MouseButton.LEFT.asArg()))
				.addAction(finger.createPointerUp(MouseButton.LEFT.asArg()));

		String platformNameFromCapacity = ((AppiumDriver)driver).getCapabilities().getCapability("platformName").toString();
		if (platformNameFromCapacity.equalsIgnoreCase("android")) {
			((AppiumDriver)driver).perform(List.of(tapPosition));
		} else if(platformNameFromCapacity.equalsIgnoreCase("ios")) {
			((IOSDriver)driver).perform(List.of(tapPosition));
		} else {
			throw new IllegalArgumentException("Unknown platform: " + platformNameFromCapacity);
		}
	}

	/**
	 * This method takes coordinates in percentage and performs a tap action on the mobile device.
	 * @param x The x coordinate in percentage.
	 * @param y The y coordinate in percentage.
	 * @throws IllegalArgumentException If the platform is not recognized.
	 */
	public void tapByCoordinatesInPercent(double x, double y) {
		// Get the size of the device screen
		Dimension size = driver.manage().window().getSize();

		tapByCoordinates((int) (size.width * x), (int) (size.height * y));
	}

	/**
	 * This method performs a swipe gesture on the device screen from the specified start coordinates to the specified end coordinates.
	 * The start and end coordinates are defined as percentages of the device screen size, where (0.0, 0.0) is the top-left corner and (1.0, 1.0) is the bottom-right corner.
	 * @param startX The X coordinate of the starting point of the swipe gesture in a percentage value.
	 * @param startY The Y coordinate of the starting point of the swipe gesture in a percentage value.
	 * @param endX The X coordinate of the ending point of the swipe gesture in a percentage value.
	 * @param endY The Y coordinate of the ending point of the swipe gesture in a percentage value.
	 * @throws IllegalArgumentException if the platform name obtained from the driver's capabilities is neither "android" nor "ios".
	 */
	public void swipeByCoordinatesInPercent(double startX, double startY, double endX, double endY) {
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
		swipeGesture.addAction(new Pause(finger, Duration.ofMillis(100)));
		swipeGesture.addAction(finger.createPointerMove(Duration.ofMillis(200), PointerInput.Origin.viewport(), endXCoordinate, endYCoordinate));
		swipeGesture.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		// Execute the swipe gesture on the device
		String platformNameFromCapacity = ((AppiumDriver)driver).getCapabilities().getCapability("platformName").toString();
		if (platformNameFromCapacity.equalsIgnoreCase("android")) {
			((AppiumDriver)driver).perform(List.of(swipeGesture));
		} else if(platformNameFromCapacity.equalsIgnoreCase("ios")) {
			((IOSDriver)driver).perform(List.of(swipeGesture));
		} else {
			throw new IllegalArgumentException("Unknown platform: " + platformNameFromCapacity);
		}
	}

	public String getText(WebElement element) {
		return element.getText();
	}
	public void clickElement(WebElement element) {
		element.click();
	}

    public void waitSplashScreenLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until((ExpectedCondition<Boolean>) driver -> {
            AndroidDriver andDriver = (AndroidDriver) driver;
            assert andDriver != null;
            return Objects.requireNonNull(andDriver.currentActivity()).contains("MainActivity");
        });
    }

    public void moveToTopScreen(By topLocator) {
        // scroll to top screen
        boolean isTopScreen = false;
        while (!isTopScreen) {
            isTopScreen = driver.findElements(topLocator).size() > 0;
            swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.75);
        }
    }

    public WebElement moveAndGetElement(By topLocator, By locator) {
        // scroll to top
        moveToTopScreen(topLocator);

        String currentPageSource = driver.getPageSource();

        // scroll to and find element until it present
        boolean isFound;
        do {
            isFound = driver.findElements(locator).size() > 0;
            if (!isFound) swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);
			sleepInMiliSecond(1000);

            String nextPageSource = driver.getPageSource();
            System.out.println("MoveAndGetElement, locator: " + locator + ", end: " + currentPageSource.equals(nextPageSource));

            if (nextPageSource.equals(currentPageSource)) break;
        } while (!isFound);
        return isFound ? driver.findElement(locator) : null;
    }

    public List<String> getListElementText(By topLocator, By locator) {
        // move and find start point
        moveAndGetElement(topLocator, locator);

        String currentPageSource = driver.getPageSource();

        // get list text element
        List<WebElement> listElement;
        List<String> listElementText = new ArrayList<>();
        do {
            // get list elementId
            listElement = driver.findElements(locator);

            // if list.size() > 0
            // add element text if not contains
            if (listElement.size() > 0) for (int index = 0; index < listElement.size(); index++) {
				String elementText = driver.findElements(locator).get(index).getText();
				if (!listElementText.contains(elementText)) listElementText.add(elementText);
			}

            //swipe screen to get next element list
            swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);

			sleepInMiliSecond(1000);

            String nextPageSource = driver.getPageSource();
            System.out.println("getListElementText, locator: " + locator + ", end: " + currentPageSource.equals(nextPageSource));
            if (currentPageSource.equals(nextPageSource)) break;

            // get new element list
            listElement = driver.findElements(locator);

//            if (Instant.now().toEpochMilli() - epoch >= duration * 1000L) break;
        } while (((listElement.size() == 0) & (listElementText.size() == 0)) || ((listElement.size() > 0) & !new HashSet<>(listElementText).containsAll(IntStream.range(0, listElement.size()).mapToObj(index -> driver.findElements(locator).get(index).getText()).toList())));
        return listElementText;
    }

    public int moveAndGetElement(By topLocator, By locator, String text) {
        // move and find start point
        moveAndGetElement(topLocator, locator);

        String currentPageSource = driver.getPageSource();

        // find element
        List<WebElement> listElement;
        List<String> checkedElementText = new ArrayList<>();
        do {
            // get list elementId
            listElement = driver.findElements(locator);

            // find element has attribute value match with 'text'
            if (listElement.size() > 0) for (int index = 0; index < listElement.size(); index++) {
                String elementText = driver.findElements(locator).get(index).getText();
                if (elementText.equals(text)) return index;
                else checkedElementText.add(elementText);
            }

            // swipe to get new list elements
            swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);
			sleepInMiliSecond(1000);

            String nextPageSource = driver.getPageSource();

            System.out.println("moveAndGetElementByText, locator: " + locator + ", end: " + currentPageSource.equals(nextPageSource));

            if (currentPageSource.equals(nextPageSource)) break;

            // get new list elements
            listElement = driver.findElements(locator);

        } while (((listElement.size() == 0) & (checkedElementText.size() == 0)) || !new HashSet<>(checkedElementText).containsAll(IntStream.range(0, listElement.size()).mapToObj(index -> driver.findElements(locator).get(index).getText()).toList()));
        return -1;
    }

}
