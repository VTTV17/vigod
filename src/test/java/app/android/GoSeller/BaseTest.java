package app.android.GoSeller;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.assert_customize.AssertCustomize;
import utilities.recording.AppiumRecording;
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
    @Parameters({"environment", "language"})
    public void getConfig(@Optional("STAG") String environment,
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
    public void writeResult(ITestResult result) throws IOException {
        // Clear assert count false
        AssertCustomize.setCountFalse(0);

        // Stop recording
        AppiumRecording.stopRecording(driver, result);
    }

    @AfterSuite
    void tearDownAndroid() {
        // Clear driver
//        if (driver != null) driver.quit();
    }
}
