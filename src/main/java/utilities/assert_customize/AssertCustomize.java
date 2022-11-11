package utilities.assert_customize;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.screenshot.Screenshot;

import java.io.IOException;

public class AssertCustomize {
    WebDriver driver;

    public AssertCustomize(WebDriver driver) {
        this.driver = driver;

    }

    public Integer assertEquals(int countFalse, String actual, String expected, String mess) throws IOException {
        try {
            Assert.assertEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
        }
        return countFalse;
    }

    public Integer assertNotEquals(int countFalse, String actual, String expected, String mess) throws IOException {
        try {
            Assert.assertNotEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
        }
        return countFalse;
    }

    public Integer assertTrue(int countFalse, boolean actual, String mess) throws IOException {
        try {
            Assert.assertTrue(actual, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
        }
        return countFalse;
    }

    public Integer assertFalse(int countFalse, boolean actual, String mess) throws IOException {
        try {
            Assert.assertFalse(actual, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
        }
        return countFalse;
    }
}
