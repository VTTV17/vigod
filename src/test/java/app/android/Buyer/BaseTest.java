package app.android.Buyer;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.excel.Excel;
import utilities.recording.ScreenRecording;
import utilities.screenshot.Screenshot;
import utilities.utils.PropertiesUtil;

import java.io.IOException;

public class BaseTest {
    public WebDriver driver;
    public String tcsFileName;
    public String testCaseId;
    public String browser;
    public String headless;
    public String language;

    @BeforeSuite
    @Parameters({"browser", "headless", "environment", "language"})
    void getConfig(@Optional("chrome") String browser,
                   @Optional("true") String headless,
                   @Optional("STAG") String environment,
                   @Optional("VIE") String language) {
        this.browser = browser;
        this.headless = headless;
        this.language = language;

        // set environment, language for Properties
        PropertiesUtil.setEnvironment(environment);
        PropertiesUtil.setDBLanguage(language);
        PropertiesUtil.setSFLanguage(language);
    }

    @BeforeMethod
    void startTest() {
        // Start recording
        ScreenRecording.startRecording(driver);
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        // Clear assert count false
        AssertCustomize.setCountFalse(0);

        // Stop recording
        ScreenRecording.stopRecording(driver, result);
    }

    @AfterSuite
    void tearDown() {
        // Clear driver
        if (driver != null) driver.quit();
    }
}
