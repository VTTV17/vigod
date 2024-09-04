package app.iOS.GoSeller;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.recording.AppiumRecording;
import utilities.utils.PropertiesUtil;

@Listeners(utilities.listeners.ReportListener.class)
public class BaseTest {
    public WebDriver driver;
    public String language;

    @BeforeSuite
    @Parameters({"environment", "language"})
    public void getConfig(@Optional("PREPROD") String environment,
                          @Optional("VIE") String language) {
        this.language = language;
        // set environment, language for Properties
        PropertiesUtil.setEnvironment(environment);
        PropertiesUtil.setDBLanguage(language);
        PropertiesUtil.setSFLanguage(language);
    }
    @BeforeMethod
    void startTest() {
        // Start recording
        AppiumRecording.startRecording(driver);
    }

    @AfterMethod
    public void clear(ITestResult result) {
        // Clear assert count false
        AssertCustomize.setCountFalse(0);

        // Stop recording
        AppiumRecording.stopRecording(driver, result);
    }

    @AfterSuite
    void tearDown() {
        // Clear driver
        if (driver != null) driver.quit();
    }
}
