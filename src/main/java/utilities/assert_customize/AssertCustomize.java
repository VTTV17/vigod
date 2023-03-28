package utilities.assert_customize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.screenshot.Screenshot;

import java.io.IOException;

public class AssertCustomize {
    WebDriver driver;
    public int countFail = 0;

    public AssertCustomize(WebDriver driver) {
        this.driver = driver;
    }

    Logger logger = LogManager.getLogger(AssertCustomize.class);

    public Integer assertEquals(int countFalse, Object actual, Object expected, String mess) throws IOException {
        try {
            Assert.assertEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
        return countFalse;
    }

    public Integer assertNotEquals(int countFalse, Object actual, Object expected, String mess) throws IOException {
        try {
            Assert.assertNotEquals(actual, expected, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
        return countFalse;
    }

    public Integer assertTrue(int countFalse, boolean actual, String mess) {
        try {
            Assert.assertTrue(actual, mess);
        } catch (AssertionError ex) {
            try {
				new Screenshot().takeScreenshot(driver);
			} catch (IOException e) {
				e.printStackTrace();
			}
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
        return countFalse;
    }

    public Integer assertFalse(int countFalse, boolean actual, String mess) throws IOException {
        try {
            Assert.assertFalse(actual, mess);
        } catch (AssertionError ex) {
            new Screenshot().takeScreenshot(driver);
            countFalse += 1;
            logger.error(ex.toString().split("java.lang.AssertionError: ")[1].split(" expected ")[0]);
        }
        return countFalse;
    }
}
