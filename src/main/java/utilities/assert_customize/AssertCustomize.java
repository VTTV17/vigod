package utilities.assert_customize;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.screenshot.Screenshot;

import java.io.IOException;

public class AssertCustomize {
    WebDriver driver;
    @Getter
    @Setter
    static int countFalse;

    @Setter
    @Getter
    private static boolean keepCountFalse;

    public AssertCustomize(WebDriver driver) {
        this.driver = driver;
    }

    Logger logger = LogManager.getLogger(AssertCustomize.class);

    public void assertEquals(Object actual, Object expected, String mess) {
        try {
            Assert.assertEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            try {
                new Screenshot().takeScreenshot(driver);
            } catch (IOException ignore) {
            }
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public void assertNotEquals(Object actual, Object expected, String mess) {
        try {
            Assert.assertNotEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            try {
                new Screenshot().takeScreenshot(driver);
            } catch (IOException ignore) {
            }
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public void assertTrue(boolean actual, String mess) {
        try {
            Assert.assertTrue(actual, mess);
        } catch (AssertionError ex) {
            try {
                new Screenshot().takeScreenshot(driver);
            } catch (IOException ignore) {
            }
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }

    public void assertFalse(boolean actual, String mess) {
        try {
            Assert.assertFalse(actual, mess);
        } catch (AssertionError ex) {
            try {
                new Screenshot().takeScreenshot(driver);
            } catch (IOException ignore) {
            }
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
    }
}
