package utilities.permission;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;

public class CheckPermission {
    WebDriver driver;
    UICommonAction commonAction;

    public CheckPermission(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver, 5);
    }

    public boolean checkAccessRestricted(By locator) {
        commonAction.click(locator);
        try {
            commonAction.waitURLShouldBeContains("/restricted");
            return true;
        } catch (TimeoutException ex) {
            return !commonAction.getListElement(By.cssSelector(".access-restricted")).isEmpty();
        }
    }

    public boolean checkAccessRestricted(String url) {
        driver.get(url);
        try {
            commonAction.waitURLShouldBeContains("/restricted");
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, By destinationLocator) {
        commonAction.click(locator);
        try {
            commonAction.getElement(destinationLocator);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, String url) {
        commonAction.click(locator);
        try {
            commonAction.waitURLShouldBeContains(url);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(String url, String destinationURL) {
        driver.get(url);
        try {
            commonAction.waitURLShouldBeContains(destinationURL);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }
}
