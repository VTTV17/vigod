package utilities.assert_customize;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.screenshot.Screenshot;

public class AssertCustomize {
    WebDriver driver;
    @Getter
    @Setter
    static int countFalse;

    public AssertCustomize() {
    }

    public AssertCustomize(WebDriver driver) {
        this.driver = driver;
    }

    Logger logger = LogManager.getLogger(AssertCustomize.class);

    public void assertEquals(Object actual, Object expected, String mess) {
        try {
            Assert.assertEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            if (driver != null) new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public void assertNotEquals(Object actual, Object expected, String mess) {
        try {
            Assert.assertNotEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            if (driver != null) new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public void assertTrue(boolean actual, String mess) {
        try {
            Assert.assertTrue(actual, mess);
        } catch (AssertionError ex) {
            if (driver != null) new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public void assertFalse(boolean actual, String mess) {
        try {
            Assert.assertFalse(actual, mess);
        } catch (AssertionError ex) {
            if (driver != null) new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public static void verifyTest() {
        // get current count false
        int tempCount = countFalse;

        // reset count false for next test
        countFalse = 0;

        // verify test
        if (tempCount > 0) Assert.fail("Count fail: %d.".formatted(tempCount));
    }
}
