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
    By loc_dlgRestricted = By.cssSelector(".access-restricted");
    By loc_dlgRestricted_btnOK = By.cssSelector(".modal.fade.show .gs-button__deep-blue");

    public boolean checkAccessRestricted(By locator) {
        commonAction.clickJS(locator);
        try {
            commonAction.waitURLShouldBeContains("/restricted");
            return true;
        } catch (TimeoutException ex) {
            if (!commonAction.getListElement(loc_dlgRestricted).isEmpty()) {
                commonAction.closePopup(loc_dlgRestricted_btnOK);
                return true;
            } else return false;
        }
    }

    public boolean checkAccessRestricted(By locator, int index) {
        commonAction.clickJS(locator, index);
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
        commonAction.clickJS(locator);
        try {
            commonAction.getElement(destinationLocator);
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public boolean checkAccessedSuccessfully(By locator, int index, By destinationLocator) {
        commonAction.clickJS(locator);
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

    public boolean checkAccessedSuccessfully(By locator, int index, String url) {
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
