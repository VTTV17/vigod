package utilities.restricted;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import utilities.commons.UICommonAction;

public class CheckRestricted {
    WebDriver driver;
    UICommonAction commonAction;
    public CheckRestricted(WebDriver driver) {
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
}
