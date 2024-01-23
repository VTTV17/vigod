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
        	commonAction.getElement(By.xpath("//*[@class='access-restricted modal-header' or @class='no-permission-wrapper']"));
            return true;
        } catch (TimeoutException ex) {
        	return false;
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

    /**
     * Use: check input has value or not.
     * @param url: use url to navigate to page
     * @param locator: input element
     * @return true if show data / false if show no data
     */
    public boolean checkValueShow(String url, By locator){
        commonAction.navigateToURL(url);
        String value = commonAction.getValue(locator);
        return !value.isEmpty();
    }

    /**
     * Use: check input has value or not.
     * @param locatorClick: use click to navigate to page
     * @param locatorInput: check value of this element
     * @return true if show data / false if show no data
     */
    public boolean checkValueShow(By locatorClick, By locatorInput){
        commonAction.click(locatorClick);
        String value = commonAction.getValue(locatorInput);
        return !value.isEmpty();
    }
    public boolean checkValueShow(By locatorClick,int index, By locatorInput){
        commonAction.click(locatorClick,index);
        String value = commonAction.getValue(locatorInput);
        return !value.isEmpty();
    }
}
